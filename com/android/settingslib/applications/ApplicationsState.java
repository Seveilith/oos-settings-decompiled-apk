package com.android.settingslib.applications;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageStatsObserver.Stub;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.R.array;
import java.io.File;
import java.text.Collator;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationsState
{
  public static final Comparator<AppEntry> ALPHA_COMPARATOR;
  static final boolean DEBUG = false;
  static final boolean DEBUG_LOCKING = false;
  public static final Comparator<AppEntry> EXTERNAL_SIZE_COMPARATOR;
  public static final AppFilter FILTER_ALL_ENABLED;
  public static final AppFilter FILTER_DISABLED;
  public static final AppFilter FILTER_DOWNLOADED_AND_LAUNCHER;
  public static final AppFilter FILTER_EVERYTHING;
  public static final AppFilter FILTER_NOT_HIDE = new AppFilter()
  {
    private String[] mHidePackageNames;
    
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      if (ArrayUtils.contains(this.mHidePackageNames, paramAnonymousAppEntry.info.packageName))
      {
        if (!paramAnonymousAppEntry.info.enabled) {
          return false;
        }
        if (paramAnonymousAppEntry.info.enabledSetting == 4) {
          return false;
        }
      }
      return true;
    }
    
    public void init() {}
    
    public void init(Context paramAnonymousContext)
    {
      this.mHidePackageNames = paramAnonymousContext.getResources().getStringArray(R.array.config_hideWhenDisabled_packageNames);
    }
  };
  public static final AppFilter FILTER_PERSONAL;
  public static final AppFilter FILTER_THIRD_PARTY;
  public static final AppFilter FILTER_WITHOUT_DISABLED_UNTIL_USED;
  public static final AppFilter FILTER_WITH_DOMAIN_URLS;
  public static final AppFilter FILTER_WORK;
  public static final Comparator<AppEntry> INTERNAL_SIZE_COMPARATOR;
  public static final int MULTI_APP_USER_ID = 999;
  static final Pattern REMOVE_DIACRITICALS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
  public static final Comparator<AppEntry> SIZE_COMPARATOR;
  public static final int SIZE_INVALID = -2;
  public static final int SIZE_UNKNOWN = -1;
  static final String TAG = "ApplicationsState";
  static ApplicationsState sInstance;
  static final Object sLock = new Object();
  final ArrayList<Session> mActiveSessions = new ArrayList();
  final int mAdminRetrieveFlags;
  final ArrayList<AppEntry> mAppEntries = new ArrayList();
  List<ApplicationInfo> mApplications = new ArrayList();
  final BackgroundHandler mBackgroundHandler;
  final Context mContext;
  String mCurComputingSizePkg;
  int mCurComputingSizeUserId;
  long mCurId = 1L;
  final SparseArray<HashMap<String, AppEntry>> mEntriesMap = new SparseArray();
  boolean mHaveDisabledApps;
  final InterestingConfigChanges mInterestingConfigChanges = new InterestingConfigChanges();
  final IPackageManager mIpm;
  final MainHandler mMainHandler = new MainHandler(Looper.getMainLooper());
  PackageIntentReceiver mPackageIntentReceiver;
  final PackageManager mPm;
  final ArrayList<Session> mRebuildingSessions = new ArrayList();
  boolean mResumed;
  final int mRetrieveFlags;
  final ArrayList<Session> mSessions = new ArrayList();
  boolean mSessionsChanged;
  final HandlerThread mThread;
  final UserManager mUm;
  
  static
  {
    ALPHA_COMPARATOR = new Comparator()
    {
      private final Collator sCollator = Collator.getInstance();
      
      public int compare(ApplicationsState.AppEntry paramAnonymousAppEntry1, ApplicationsState.AppEntry paramAnonymousAppEntry2)
      {
        int i = this.sCollator.compare(paramAnonymousAppEntry1.label, paramAnonymousAppEntry2.label);
        if (i != 0) {
          return i;
        }
        if ((paramAnonymousAppEntry1.info != null) && (paramAnonymousAppEntry2.info != null))
        {
          i = this.sCollator.compare(paramAnonymousAppEntry1.info.packageName, paramAnonymousAppEntry2.info.packageName);
          if (i != 0) {
            return i;
          }
        }
        return paramAnonymousAppEntry1.info.uid - paramAnonymousAppEntry2.info.uid;
      }
    };
    SIZE_COMPARATOR = new Comparator()
    {
      public int compare(ApplicationsState.AppEntry paramAnonymousAppEntry1, ApplicationsState.AppEntry paramAnonymousAppEntry2)
      {
        if (paramAnonymousAppEntry1.size < paramAnonymousAppEntry2.size) {
          return 1;
        }
        if (paramAnonymousAppEntry1.size > paramAnonymousAppEntry2.size) {
          return -1;
        }
        return ApplicationsState.ALPHA_COMPARATOR.compare(paramAnonymousAppEntry1, paramAnonymousAppEntry2);
      }
    };
    INTERNAL_SIZE_COMPARATOR = new Comparator()
    {
      public int compare(ApplicationsState.AppEntry paramAnonymousAppEntry1, ApplicationsState.AppEntry paramAnonymousAppEntry2)
      {
        if (paramAnonymousAppEntry1.internalSize < paramAnonymousAppEntry2.internalSize) {
          return 1;
        }
        if (paramAnonymousAppEntry1.internalSize > paramAnonymousAppEntry2.internalSize) {
          return -1;
        }
        return ApplicationsState.ALPHA_COMPARATOR.compare(paramAnonymousAppEntry1, paramAnonymousAppEntry2);
      }
    };
    EXTERNAL_SIZE_COMPARATOR = new Comparator()
    {
      public int compare(ApplicationsState.AppEntry paramAnonymousAppEntry1, ApplicationsState.AppEntry paramAnonymousAppEntry2)
      {
        if (paramAnonymousAppEntry1.externalSize < paramAnonymousAppEntry2.externalSize) {
          return 1;
        }
        if (paramAnonymousAppEntry1.externalSize > paramAnonymousAppEntry2.externalSize) {
          return -1;
        }
        return ApplicationsState.ALPHA_COMPARATOR.compare(paramAnonymousAppEntry1, paramAnonymousAppEntry2);
      }
    };
    FILTER_PERSONAL = new AppFilter()
    {
      private int mCurrentUser;
      
      public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
      {
        return UserHandle.getUserId(paramAnonymousAppEntry.info.uid) == this.mCurrentUser;
      }
      
      public void init()
      {
        this.mCurrentUser = ActivityManager.getCurrentUser();
      }
    };
    FILTER_WITHOUT_DISABLED_UNTIL_USED = new AppFilter()
    {
      public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
      {
        return paramAnonymousAppEntry.info.enabledSetting != 4;
      }
      
      public void init() {}
    };
    FILTER_WORK = new AppFilter()
    {
      private int mCurrentUser;
      
      public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
      {
        return UserHandle.getUserId(paramAnonymousAppEntry.info.uid) != this.mCurrentUser;
      }
      
      public void init()
      {
        this.mCurrentUser = ActivityManager.getCurrentUser();
      }
    };
    FILTER_DOWNLOADED_AND_LAUNCHER = new AppFilter()
    {
      public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
      {
        if ((paramAnonymousAppEntry.info.flags & 0x80) != 0) {
          return true;
        }
        if ((paramAnonymousAppEntry.info.flags & 0x1) == 0) {
          return true;
        }
        if (paramAnonymousAppEntry.hasLauncherEntry) {
          return true;
        }
        return ((paramAnonymousAppEntry.info.flags & 0x1) != 0) && (paramAnonymousAppEntry.isHomeApp);
      }
      
      public void init() {}
    };
    FILTER_THIRD_PARTY = new AppFilter()
    {
      public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
      {
        if ((paramAnonymousAppEntry.info.flags & 0x80) != 0) {
          return true;
        }
        return (paramAnonymousAppEntry.info.flags & 0x1) == 0;
      }
      
      public void init() {}
    };
    FILTER_DISABLED = new AppFilter()
    {
      public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
      {
        return !paramAnonymousAppEntry.info.enabled;
      }
      
      public void init() {}
    };
    FILTER_ALL_ENABLED = new AppFilter()
    {
      public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
      {
        return paramAnonymousAppEntry.info.enabled;
      }
      
      public void init() {}
    };
    FILTER_EVERYTHING = new AppFilter()
    {
      public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
      {
        return true;
      }
      
      public void init() {}
    };
    FILTER_WITH_DOMAIN_URLS = new AppFilter()
    {
      public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
      {
        boolean bool = false;
        if ((paramAnonymousAppEntry.info.privateFlags & 0x10) != 0) {
          bool = true;
        }
        return bool;
      }
      
      public void init() {}
    };
  }
  
  private ApplicationsState(Application arg1)
  {
    this.mContext = ???;
    this.mPm = this.mContext.getPackageManager();
    this.mIpm = AppGlobals.getPackageManager();
    this.mUm = ((UserManager)???.getSystemService("user"));
    ??? = this.mUm.getProfileIdsWithDisabled(UserHandle.myUserId());
    int i = 0;
    int j = ???.length;
    while (i < j)
    {
      int k = ???[i];
      this.mEntriesMap.put(k, new HashMap());
      i += 1;
    }
    this.mThread = new HandlerThread("ApplicationsState.Loader", 10);
    this.mThread.start();
    this.mBackgroundHandler = new BackgroundHandler(this.mThread.getLooper());
    this.mAdminRetrieveFlags = 41472;
    this.mRetrieveFlags = 33280;
    try
    {
      synchronized (this.mEntriesMap)
      {
        this.mEntriesMap.wait(1L);
        return;
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;) {}
    }
  }
  
  private void addUser(int paramInt)
  {
    if (ArrayUtils.contains(this.mUm.getProfileIdsWithDisabled(UserHandle.myUserId()), paramInt)) {}
    synchronized (this.mEntriesMap)
    {
      this.mEntriesMap.put(paramInt, new HashMap());
      if (this.mResumed)
      {
        doPauseLocked();
        doResumeIfNeededLocked();
      }
      if (!this.mMainHandler.hasMessages(2)) {
        this.mMainHandler.sendEmptyMessage(2);
      }
      return;
    }
  }
  
  private void clearEntries()
  {
    int i = 0;
    while (i < this.mEntriesMap.size())
    {
      ((HashMap)this.mEntriesMap.valueAt(i)).clear();
      i += 1;
    }
    this.mAppEntries.clear();
  }
  
  private ApplicationInfo getAppInfoLocked(String paramString, int paramInt)
  {
    int i = 0;
    while (i < this.mApplications.size())
    {
      ApplicationInfo localApplicationInfo = (ApplicationInfo)this.mApplications.get(i);
      if ((paramString.equals(localApplicationInfo.packageName)) && (paramInt == UserHandle.getUserId(localApplicationInfo.uid))) {
        return localApplicationInfo;
      }
      i += 1;
    }
    return null;
  }
  
  private AppEntry getEntryLocked(ApplicationInfo paramApplicationInfo)
  {
    int i = UserHandle.getUserId(paramApplicationInfo.uid);
    AppEntry localAppEntry = (AppEntry)((HashMap)this.mEntriesMap.get(i)).get(paramApplicationInfo.packageName);
    Object localObject;
    if (localAppEntry == null)
    {
      localObject = this.mContext;
      long l = this.mCurId;
      this.mCurId = (1L + l);
      localObject = new AppEntry((Context)localObject, paramApplicationInfo, l);
      ((HashMap)this.mEntriesMap.get(i)).put(paramApplicationInfo.packageName, localObject);
      this.mAppEntries.add(localObject);
    }
    do
    {
      return (AppEntry)localObject;
      localObject = localAppEntry;
    } while (localAppEntry.info == paramApplicationInfo);
    localAppEntry.info = paramApplicationInfo;
    return localAppEntry;
  }
  
  public static ApplicationsState getInstance(Application paramApplication)
  {
    synchronized (sLock)
    {
      if (sInstance == null) {
        sInstance = new ApplicationsState(paramApplication);
      }
      paramApplication = sInstance;
      return paramApplication;
    }
  }
  
  private String getSizeStr(long paramLong)
  {
    Log.v("ApplicationsState", "Configuration:" + this.mContext.getResources().getConfiguration());
    if (paramLong >= 0L) {
      return Formatter.formatFileSize(this.mContext, paramLong);
    }
    return null;
  }
  
  private long getTotalExternalSize(PackageStats paramPackageStats)
  {
    if (paramPackageStats != null) {
      return paramPackageStats.externalCodeSize + paramPackageStats.externalDataSize + paramPackageStats.externalCacheSize + paramPackageStats.externalMediaSize + paramPackageStats.externalObbSize;
    }
    return -2L;
  }
  
  private long getTotalInternalSize(PackageStats paramPackageStats)
  {
    if (paramPackageStats != null) {
      return paramPackageStats.codeSize + paramPackageStats.dataSize;
    }
    return -2L;
  }
  
  public static String normalize(String paramString)
  {
    paramString = Normalizer.normalize(paramString, Normalizer.Form.NFD);
    return REMOVE_DIACRITICALS_PATTERN.matcher(paramString).replaceAll("").toLowerCase();
  }
  
  private void removeUser(int paramInt)
  {
    synchronized (this.mEntriesMap)
    {
      Object localObject1 = (HashMap)this.mEntriesMap.get(paramInt);
      if (localObject1 == null) {
        break label114;
      }
      localObject1 = ((HashMap)localObject1).values().iterator();
      if (((Iterator)localObject1).hasNext())
      {
        AppEntry localAppEntry = (AppEntry)((Iterator)localObject1).next();
        this.mAppEntries.remove(localAppEntry);
        this.mApplications.remove(localAppEntry.info);
      }
    }
    this.mEntriesMap.remove(paramInt);
    if (!this.mMainHandler.hasMessages(2)) {
      this.mMainHandler.sendEmptyMessage(2);
    }
    label114:
  }
  
  void addPackage(String paramString, int paramInt)
  {
    try
    {
      synchronized (this.mEntriesMap)
      {
        boolean bool = this.mResumed;
        if (!bool) {
          return;
        }
        int i = indexOfApplicationInfoLocked(paramString, paramInt);
        if (i >= 0) {
          return;
        }
        IPackageManager localIPackageManager = this.mIpm;
        if (this.mUm.isUserAdmin(paramInt)) {}
        for (i = this.mAdminRetrieveFlags;; i = this.mRetrieveFlags)
        {
          paramString = localIPackageManager.getApplicationInfo(paramString, i, paramInt);
          if (paramString != null) {
            break;
          }
          return;
        }
        if (!paramString.enabled)
        {
          paramInt = paramString.enabledSetting;
          if (paramInt != 3) {
            return;
          }
          this.mHaveDisabledApps = true;
        }
        this.mApplications.add(paramString);
        if (!this.mBackgroundHandler.hasMessages(2)) {
          this.mBackgroundHandler.sendEmptyMessage(2);
        }
        if (!this.mMainHandler.hasMessages(2)) {
          this.mMainHandler.sendEmptyMessage(2);
        }
        return;
      }
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  void doPauseIfNeededLocked()
  {
    if (!this.mResumed) {
      return;
    }
    int i = 0;
    while (i < this.mSessions.size())
    {
      if (((Session)this.mSessions.get(i)).mResumed) {
        return;
      }
      i += 1;
    }
    doPauseLocked();
  }
  
  void doPauseLocked()
  {
    this.mResumed = false;
    if (this.mPackageIntentReceiver != null)
    {
      this.mPackageIntentReceiver.unregisterReceiver();
      this.mPackageIntentReceiver = null;
    }
  }
  
  void doResumeIfNeededLocked()
  {
    if (this.mResumed) {
      return;
    }
    this.mResumed = true;
    if (this.mPackageIntentReceiver == null)
    {
      this.mPackageIntentReceiver = new PackageIntentReceiver(null);
      this.mPackageIntentReceiver.registerReceiver();
    }
    this.mApplications = new ArrayList();
    Object localObject1 = this.mUm.getProfiles(UserHandle.myUserId()).iterator();
    for (;;)
    {
      Object localObject2;
      if (((Iterator)localObject1).hasNext()) {
        localObject2 = (UserInfo)((Iterator)localObject1).next();
      }
      try
      {
        if (this.mEntriesMap.indexOfKey(((UserInfo)localObject2).id) < 0) {
          this.mEntriesMap.put(((UserInfo)localObject2).id, new HashMap());
        }
        IPackageManager localIPackageManager = this.mIpm;
        if (((UserInfo)localObject2).isAdmin()) {}
        for (int i = this.mAdminRetrieveFlags;; i = this.mRetrieveFlags)
        {
          localObject2 = localIPackageManager.getInstalledApplications(i, ((UserInfo)localObject2).id);
          this.mApplications.addAll(((ParceledListSlice)localObject2).getList());
          break;
        }
        label209:
        int j;
        if (this.mInterestingConfigChanges.applyNewConfig(this.mContext.getResources()))
        {
          clearEntries();
          this.mHaveDisabledApps = false;
          i = 0;
          if (i >= this.mApplications.size()) {
            break label396;
          }
          localObject1 = (ApplicationInfo)this.mApplications.get(i);
          if (((ApplicationInfo)localObject1).enabled) {
            break label313;
          }
          if (((ApplicationInfo)localObject1).enabledSetting == 3) {
            break label308;
          }
          this.mApplications.remove(i);
          j = i - 1;
        }
        for (;;)
        {
          i = j + 1;
          break label209;
          i = 0;
          while (i < this.mAppEntries.size())
          {
            ((AppEntry)this.mAppEntries.get(i)).sizeStale = true;
            i += 1;
          }
          break;
          label308:
          this.mHaveDisabledApps = true;
          label313:
          j = UserHandle.getUserId(((ApplicationInfo)localObject1).uid);
          if ((j == 999) && ((((ApplicationInfo)localObject1).flags & 0x1) > 0))
          {
            this.mApplications.remove(i);
            j = i - 1;
          }
          else
          {
            localObject2 = (AppEntry)((HashMap)this.mEntriesMap.get(j)).get(((ApplicationInfo)localObject1).packageName);
            j = i;
            if (localObject2 != null)
            {
              ((AppEntry)localObject2).info = ((ApplicationInfo)localObject1);
              j = i;
            }
          }
        }
        label396:
        if (this.mAppEntries.size() > this.mApplications.size()) {
          clearEntries();
        }
        this.mCurComputingSizePkg = null;
        if (!this.mBackgroundHandler.hasMessages(2)) {
          this.mBackgroundHandler.sendEmptyMessage(2);
        }
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
  }
  
  public void ensureIcon(AppEntry paramAppEntry)
  {
    if (paramAppEntry.icon != null) {
      return;
    }
    try
    {
      paramAppEntry.ensureIconLocked(this.mContext, this.mPm);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public Looper getBackgroundLooper()
  {
    return this.mThread.getLooper();
  }
  
  public AppEntry getEntry(String paramString, int paramInt)
  {
    synchronized (this.mEntriesMap)
    {
      AppEntry localAppEntry = (AppEntry)((HashMap)this.mEntriesMap.get(paramInt)).get(paramString);
      Object localObject2 = localAppEntry;
      Object localObject1;
      if (localAppEntry == null)
      {
        localObject2 = getAppInfoLocked(paramString, paramInt);
        localObject1 = localObject2;
        if (localObject2 != null) {}
      }
      try
      {
        localObject1 = this.mIpm.getApplicationInfo(paramString, 0, paramInt);
        localObject2 = localAppEntry;
        if (localObject1 != null) {
          localObject2 = getEntryLocked((ApplicationInfo)localObject1);
        }
        return (AppEntry)localObject2;
      }
      catch (RemoteException paramString)
      {
        Log.w("ApplicationsState", "getEntry couldn't reach PackageManager", paramString);
        return null;
      }
    }
  }
  
  public boolean haveDisabledApps()
  {
    return this.mHaveDisabledApps;
  }
  
  int indexOfApplicationInfoLocked(String paramString, int paramInt)
  {
    int i = this.mApplications.size() - 1;
    while (i >= 0)
    {
      ApplicationInfo localApplicationInfo = (ApplicationInfo)this.mApplications.get(i);
      if ((localApplicationInfo.packageName.equals(paramString)) && (UserHandle.getUserId(localApplicationInfo.uid) == paramInt)) {
        return i;
      }
      i -= 1;
    }
    return -1;
  }
  
  public void invalidatePackage(String paramString, int paramInt)
  {
    removePackage(paramString, paramInt);
    addPackage(paramString, paramInt);
  }
  
  public Session newSession(Callbacks arg1)
  {
    Session localSession = new Session(???);
    synchronized (this.mEntriesMap)
    {
      this.mSessions.add(localSession);
      return localSession;
    }
  }
  
  void rebuildActiveSessions()
  {
    synchronized (this.mEntriesMap)
    {
      boolean bool = this.mSessionsChanged;
      if (!bool) {
        return;
      }
      this.mActiveSessions.clear();
      int i = 0;
      while (i < this.mSessions.size())
      {
        Session localSession = (Session)this.mSessions.get(i);
        if (localSession.mResumed) {
          this.mActiveSessions.add(localSession);
        }
        i += 1;
      }
      return;
    }
  }
  
  public void removePackage(String paramString, int paramInt)
  {
    synchronized (this.mEntriesMap)
    {
      int i = indexOfApplicationInfoLocked(paramString, paramInt);
      if (i >= 0)
      {
        AppEntry localAppEntry = (AppEntry)((HashMap)this.mEntriesMap.get(paramInt)).get(paramString);
        if (localAppEntry != null)
        {
          ((HashMap)this.mEntriesMap.get(paramInt)).remove(paramString);
          this.mAppEntries.remove(localAppEntry);
        }
        paramString = (ApplicationInfo)this.mApplications.get(i);
        this.mApplications.remove(i);
        if (!paramString.enabled)
        {
          this.mHaveDisabledApps = false;
          paramInt = 0;
          if (paramInt < this.mApplications.size())
          {
            if (((ApplicationInfo)this.mApplications.get(paramInt)).enabled) {
              break label171;
            }
            this.mHaveDisabledApps = true;
          }
        }
        if (!this.mMainHandler.hasMessages(2)) {
          this.mMainHandler.sendEmptyMessage(2);
        }
      }
      return;
      label171:
      paramInt += 1;
    }
  }
  
  public void requestSize(String paramString, int paramInt)
  {
    synchronized (this.mEntriesMap)
    {
      if ((AppEntry)((HashMap)this.mEntriesMap.get(paramInt)).get(paramString) != null) {
        this.mPm.getPackageSizeInfoAsUser(paramString, paramInt, this.mBackgroundHandler.mStatsObserver);
      }
      return;
    }
  }
  
  long sumCacheSizes()
  {
    long l1 = 0L;
    synchronized (this.mEntriesMap)
    {
      int i = this.mAppEntries.size() - 1;
      while (i >= 0)
      {
        long l2 = ((AppEntry)this.mAppEntries.get(i)).cacheSize;
        l1 += l2;
        i -= 1;
      }
      return l1;
    }
  }
  
  public static class AppEntry
    extends ApplicationsState.SizeInfo
  {
    public final File apkFile;
    public long externalSize;
    public String externalSizeStr;
    public Object extraInfo;
    public boolean hasLauncherEntry;
    public Drawable icon;
    public final long id;
    public ApplicationInfo info;
    public long internalSize;
    public String internalSizeStr;
    public boolean isHomeApp;
    public String label;
    public boolean mounted;
    public String normalizedLabel;
    public long size;
    public long sizeLoadStart;
    public boolean sizeStale;
    public String sizeStr;
    
    AppEntry(Context paramContext, ApplicationInfo paramApplicationInfo, long paramLong)
    {
      this.apkFile = new File(paramApplicationInfo.sourceDir);
      this.id = paramLong;
      this.info = paramApplicationInfo;
      this.size = -1L;
      this.sizeStale = true;
      ensureLabel(paramContext);
    }
    
    private Drawable getBadgedIcon(PackageManager paramPackageManager)
    {
      return paramPackageManager.getUserBadgedIcon(paramPackageManager.loadUnbadgedItemIcon(this.info, this.info), new UserHandle(UserHandle.getUserId(this.info.uid)));
    }
    
    boolean ensureIconLocked(Context paramContext, PackageManager paramPackageManager)
    {
      if (this.icon == null)
      {
        if (this.apkFile.exists())
        {
          this.icon = getBadgedIcon(paramPackageManager);
          return true;
        }
        this.mounted = false;
        this.icon = paramContext.getDrawable(17303368);
      }
      while ((this.mounted) || (!this.apkFile.exists())) {
        return false;
      }
      this.mounted = true;
      this.icon = getBadgedIcon(paramPackageManager);
      return true;
    }
    
    public void ensureLabel(Context paramContext)
    {
      if ((this.label != null) && (this.mounted)) {
        return;
      }
      if (!this.apkFile.exists())
      {
        this.mounted = false;
        this.label = this.info.packageName;
        return;
      }
      this.mounted = true;
      paramContext = this.info.loadLabel(paramContext.getPackageManager());
      if (paramContext != null) {}
      for (paramContext = paramContext.toString();; paramContext = this.info.packageName)
      {
        this.label = paramContext;
        return;
      }
    }
    
    public String getNormalizedLabel()
    {
      if (this.normalizedLabel != null) {
        return this.normalizedLabel;
      }
      this.normalizedLabel = ApplicationsState.normalize(this.label);
      return this.normalizedLabel;
    }
    
    public String getVersion(Context paramContext)
    {
      try
      {
        paramContext = paramContext.getPackageManager().getPackageInfo(this.info.packageName, 0).versionName;
        return paramContext;
      }
      catch (PackageManager.NameNotFoundException paramContext) {}
      return "";
    }
  }
  
  public static abstract interface AppFilter
  {
    public abstract boolean filterApp(ApplicationsState.AppEntry paramAppEntry);
    
    public abstract void init();
    
    public void init(Context paramContext)
    {
      init();
    }
  }
  
  private class BackgroundHandler
    extends Handler
  {
    static final int MSG_LOAD_ENTRIES = 2;
    static final int MSG_LOAD_HOME_APP = 6;
    static final int MSG_LOAD_ICONS = 3;
    static final int MSG_LOAD_LAUNCHER = 5;
    static final int MSG_LOAD_SIZES = 4;
    static final int MSG_REBUILD_LIST = 1;
    boolean mRunning;
    final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub()
    {
      public void onGetStatsCompleted(PackageStats paramAnonymousPackageStats, boolean paramAnonymousBoolean)
      {
        int i = 0;
        synchronized (ApplicationsState.this.mEntriesMap)
        {
          Object localObject = (HashMap)ApplicationsState.this.mEntriesMap.get(paramAnonymousPackageStats.userHandle);
          if (localObject == null) {
            return;
          }
          localObject = (ApplicationsState.AppEntry)((HashMap)localObject).get(paramAnonymousPackageStats.packageName);
          if (localObject != null) {}
          try
          {
            ((ApplicationsState.AppEntry)localObject).sizeStale = false;
            ((ApplicationsState.AppEntry)localObject).sizeLoadStart = 0L;
            long l1 = paramAnonymousPackageStats.externalCodeSize + paramAnonymousPackageStats.externalObbSize;
            long l2 = paramAnonymousPackageStats.externalDataSize + paramAnonymousPackageStats.externalMediaSize;
            long l3 = l1 + l2 + ApplicationsState.-wrap3(ApplicationsState.this, paramAnonymousPackageStats);
            if ((((ApplicationsState.AppEntry)localObject).size != l3) || (((ApplicationsState.AppEntry)localObject).cacheSize != paramAnonymousPackageStats.cacheSize)) {}
            for (;;)
            {
              ((ApplicationsState.AppEntry)localObject).size = l3;
              ((ApplicationsState.AppEntry)localObject).cacheSize = paramAnonymousPackageStats.cacheSize;
              ((ApplicationsState.AppEntry)localObject).codeSize = paramAnonymousPackageStats.codeSize;
              ((ApplicationsState.AppEntry)localObject).dataSize = paramAnonymousPackageStats.dataSize;
              ((ApplicationsState.AppEntry)localObject).externalCodeSize = l1;
              ((ApplicationsState.AppEntry)localObject).externalDataSize = l2;
              ((ApplicationsState.AppEntry)localObject).externalCacheSize = paramAnonymousPackageStats.externalCacheSize;
              ((ApplicationsState.AppEntry)localObject).sizeStr = ApplicationsState.-wrap1(ApplicationsState.this, ((ApplicationsState.AppEntry)localObject).size);
              ((ApplicationsState.AppEntry)localObject).internalSize = ApplicationsState.-wrap3(ApplicationsState.this, paramAnonymousPackageStats);
              ((ApplicationsState.AppEntry)localObject).internalSizeStr = ApplicationsState.-wrap1(ApplicationsState.this, ((ApplicationsState.AppEntry)localObject).internalSize);
              ((ApplicationsState.AppEntry)localObject).externalSize = ApplicationsState.-wrap2(ApplicationsState.this, paramAnonymousPackageStats);
              ((ApplicationsState.AppEntry)localObject).externalSizeStr = ApplicationsState.-wrap1(ApplicationsState.this, ((ApplicationsState.AppEntry)localObject).externalSize);
              i = 1;
              long l4;
              long l5;
              do
              {
                if (i != 0)
                {
                  localObject = ApplicationsState.this.mMainHandler.obtainMessage(4, paramAnonymousPackageStats.packageName);
                  ApplicationsState.this.mMainHandler.sendMessage((Message)localObject);
                }
                if ((ApplicationsState.this.mCurComputingSizePkg != null) && (ApplicationsState.this.mCurComputingSizePkg.equals(paramAnonymousPackageStats.packageName)) && (ApplicationsState.this.mCurComputingSizeUserId == paramAnonymousPackageStats.userHandle))
                {
                  ApplicationsState.this.mCurComputingSizePkg = null;
                  ApplicationsState.BackgroundHandler.this.sendEmptyMessage(4);
                }
                return;
                if ((((ApplicationsState.AppEntry)localObject).codeSize != paramAnonymousPackageStats.codeSize) || (((ApplicationsState.AppEntry)localObject).dataSize != paramAnonymousPackageStats.dataSize) || (((ApplicationsState.AppEntry)localObject).externalCodeSize != l1) || (((ApplicationsState.AppEntry)localObject).externalDataSize != l2)) {
                  break;
                }
                l4 = ((ApplicationsState.AppEntry)localObject).externalCacheSize;
                l5 = paramAnonymousPackageStats.externalCacheSize;
              } while (l4 == l5);
            }
            paramAnonymousPackageStats = finally;
          }
          finally {}
        }
      }
    };
    
    BackgroundHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message arg1)
    {
      Object localObject1 = null;
      synchronized (ApplicationsState.this.mRebuildingSessions)
      {
        if (ApplicationsState.this.mRebuildingSessions.size() > 0) {
          localObject1 = new ArrayList(ApplicationsState.this.mRebuildingSessions);
        }
      }
      try
      {
        ApplicationsState.this.mRebuildingSessions.clear();
        if (localObject1 == null) {
          break label98;
        }
        i = 0;
        while (i < ((ArrayList)localObject1).size())
        {
          ((ApplicationsState.Session)((ArrayList)localObject1).get(i)).handleRebuildList();
          i += 1;
        }
        ??? = finally;
      }
      finally
      {
        label98:
        int k;
        for (;;)
        {
          int i;
          int j;
          int m;
          Object localObject10;
          Object localObject11;
          continue;
          j += 1;
        }
        k += 1;
      }
      throw ???;
      switch (???.what)
      {
      case 1: 
      default: 
        
      case 2: 
        j = 0;
        ??? = ApplicationsState.this.mEntriesMap;
        i = 0;
        try
        {
          while ((i < ApplicationsState.this.mApplications.size()) && (j < 6))
          {
            if (!this.mRunning)
            {
              this.mRunning = true;
              localObject1 = ApplicationsState.this.mMainHandler.obtainMessage(6, Integer.valueOf(1));
              ApplicationsState.this.mMainHandler.sendMessage((Message)localObject1);
            }
            localObject1 = (ApplicationInfo)ApplicationsState.this.mApplications.get(i);
            m = UserHandle.getUserId(((ApplicationInfo)localObject1).uid);
            k = j;
            if (((HashMap)ApplicationsState.this.mEntriesMap.get(m)).get(((ApplicationInfo)localObject1).packageName) == null)
            {
              k = j + 1;
              ApplicationsState.-wrap0(ApplicationsState.this, (ApplicationInfo)localObject1);
            }
            if ((m != 0) && (ApplicationsState.this.mEntriesMap.indexOfKey(0) >= 0))
            {
              ??? = (ApplicationsState.AppEntry)((HashMap)ApplicationsState.this.mEntriesMap.get(0)).get(((ApplicationInfo)localObject1).packageName);
              if ((??? != null) && ((((ApplicationsState.AppEntry)???).info.flags & 0x800000) == 0))
              {
                ((HashMap)ApplicationsState.this.mEntriesMap.get(0)).remove(((ApplicationInfo)localObject1).packageName);
                ApplicationsState.this.mAppEntries.remove(???);
              }
            }
            i += 1;
            j = k;
          }
          if (j >= 6)
          {
            sendEmptyMessage(2);
            return;
          }
        }
        finally {}
        if (!ApplicationsState.this.mMainHandler.hasMessages(8)) {
          ApplicationsState.this.mMainHandler.sendEmptyMessage(8);
        }
        sendEmptyMessage(6);
        return;
      case 6: 
        ArrayList localArrayList = new ArrayList();
        ApplicationsState.this.mPm.getHomeActivities(localArrayList);
        for (;;)
        {
          synchronized (ApplicationsState.this.mEntriesMap)
          {
            j = ApplicationsState.this.mEntriesMap.size();
            i = 0;
            if (i >= j) {
              break;
            }
            ??? = (HashMap)ApplicationsState.this.mEntriesMap.valueAt(i);
            localObject10 = localArrayList.iterator();
            if (((Iterator)localObject10).hasNext())
            {
              localObject11 = (ApplicationsState.AppEntry)((HashMap)???).get(((ResolveInfo)((Iterator)localObject10).next()).activityInfo.packageName);
              if (localObject11 == null) {
                continue;
              }
              ((ApplicationsState.AppEntry)localObject11).isHomeApp = true;
            }
          }
          i += 1;
        }
        sendEmptyMessage(5);
        return;
      case 5: 
        Intent localIntent = new Intent("android.intent.action.MAIN", null).addCategory("android.intent.category.LAUNCHER");
        i = 0;
        while (i < ApplicationsState.this.mEntriesMap.size())
        {
          k = ApplicationsState.this.mEntriesMap.keyAt(i);
          ??? = ApplicationsState.this.mPm.queryIntentActivitiesAsUser(localIntent, 786944, k);
          synchronized (ApplicationsState.this.mEntriesMap)
          {
            localObject10 = (HashMap)ApplicationsState.this.mEntriesMap.valueAt(i);
            m = ((List)???).size();
            j = 0;
            if (j < m)
            {
              localObject11 = ((ResolveInfo)((List)???).get(j)).activityInfo.packageName;
              ApplicationsState.AppEntry localAppEntry2 = (ApplicationsState.AppEntry)((HashMap)localObject10).get(localObject11);
              if (localAppEntry2 != null) {
                localAppEntry2.hasLauncherEntry = true;
              } else {
                Log.w("ApplicationsState", "Cannot find pkg: " + (String)localObject11 + " on user " + k);
              }
            }
          }
          i += 1;
        }
        if (!ApplicationsState.this.mMainHandler.hasMessages(7)) {
          ApplicationsState.this.mMainHandler.sendEmptyMessage(7);
        }
        sendEmptyMessage(3);
        return;
      case 3: 
        i = 0;
        ??? = ApplicationsState.this.mEntriesMap;
        k = 0;
        try
        {
          if ((k < ApplicationsState.this.mAppEntries.size()) && (i < 2))
          {
            localAppEntry1 = (ApplicationsState.AppEntry)ApplicationsState.this.mAppEntries.get(k);
            if ((localAppEntry1.icon != null) && (localAppEntry1.mounted)) {
              break;
            }
            j = i;
          }
        }
        finally
        {
          try
          {
            ApplicationsState.AppEntry localAppEntry1;
            if (localAppEntry1.ensureIconLocked(ApplicationsState.this.mContext, ApplicationsState.this.mPm))
            {
              if (!this.mRunning)
              {
                this.mRunning = true;
                ??? = ApplicationsState.this.mMainHandler.obtainMessage(6, Integer.valueOf(1));
                ApplicationsState.this.mMainHandler.sendMessage((Message)???);
              }
              j = i + 1;
            }
            i = j;
            break;
          }
          finally {}
          localObject5 = finally;
        }
        if ((i > 0) && (!ApplicationsState.this.mMainHandler.hasMessages(3))) {
          ApplicationsState.this.mMainHandler.sendEmptyMessage(3);
        }
        if (i >= 2)
        {
          sendEmptyMessage(3);
          return;
        }
        sendEmptyMessage(4);
        return;
      case 4: 
        synchronized (ApplicationsState.this.mEntriesMap)
        {
          Object localObject6 = ApplicationsState.this.mCurComputingSizePkg;
          if (localObject6 != null) {
            return;
          }
          long l = SystemClock.uptimeMillis();
          i = 0;
          while (i < ApplicationsState.this.mAppEntries.size())
          {
            localObject6 = (ApplicationsState.AppEntry)ApplicationsState.this.mAppEntries.get(i);
            if ((((ApplicationsState.AppEntry)localObject6).size == -1L) || (((ApplicationsState.AppEntry)localObject6).sizeStale))
            {
              if ((((ApplicationsState.AppEntry)localObject6).sizeLoadStart == 0L) || (((ApplicationsState.AppEntry)localObject6).sizeLoadStart < l - 20000L))
              {
                if (!this.mRunning)
                {
                  this.mRunning = true;
                  Message localMessage = ApplicationsState.this.mMainHandler.obtainMessage(6, Integer.valueOf(1));
                  ApplicationsState.this.mMainHandler.sendMessage(localMessage);
                }
                ((ApplicationsState.AppEntry)localObject6).sizeLoadStart = l;
                ApplicationsState.this.mCurComputingSizePkg = ((ApplicationsState.AppEntry)localObject6).info.packageName;
                ApplicationsState.this.mCurComputingSizeUserId = UserHandle.getUserId(((ApplicationsState.AppEntry)localObject6).info.uid);
                ApplicationsState.this.mPm.getPackageSizeInfoAsUser(ApplicationsState.this.mCurComputingSizePkg, ApplicationsState.this.mCurComputingSizeUserId, this.mStatsObserver);
              }
              return;
            }
            i += 1;
          }
          if (!ApplicationsState.this.mMainHandler.hasMessages(5))
          {
            ApplicationsState.this.mMainHandler.sendEmptyMessage(5);
            this.mRunning = false;
            localObject6 = ApplicationsState.this.mMainHandler.obtainMessage(6, Integer.valueOf(0));
            ApplicationsState.this.mMainHandler.sendMessage((Message)localObject6);
          }
          return;
        }
      }
    }
  }
  
  public static abstract interface Callbacks
  {
    public abstract void onAllSizesComputed();
    
    public abstract void onLauncherInfoChanged();
    
    public abstract void onLoadEntriesCompleted();
    
    public abstract void onPackageIconChanged();
    
    public abstract void onPackageListChanged();
    
    public abstract void onPackageSizeChanged(String paramString);
    
    public abstract void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> paramArrayList);
    
    public abstract void onRunningStateChanged(boolean paramBoolean);
  }
  
  public static class CompoundFilter
    implements ApplicationsState.AppFilter
  {
    private final ApplicationsState.AppFilter mFirstFilter;
    private final ApplicationsState.AppFilter mSecondFilter;
    
    public CompoundFilter(ApplicationsState.AppFilter paramAppFilter1, ApplicationsState.AppFilter paramAppFilter2)
    {
      this.mFirstFilter = paramAppFilter1;
      this.mSecondFilter = paramAppFilter2;
    }
    
    public boolean filterApp(ApplicationsState.AppEntry paramAppEntry)
    {
      if (this.mFirstFilter.filterApp(paramAppEntry)) {
        return this.mSecondFilter.filterApp(paramAppEntry);
      }
      return false;
    }
    
    public void init()
    {
      this.mFirstFilter.init();
      this.mSecondFilter.init();
    }
    
    public void init(Context paramContext)
    {
      this.mFirstFilter.init(paramContext);
      this.mSecondFilter.init(paramContext);
    }
  }
  
  class MainHandler
    extends Handler
  {
    static final int MSG_ALL_SIZES_COMPUTED = 5;
    static final int MSG_LAUNCHER_INFO_CHANGED = 7;
    static final int MSG_LOAD_ENTRIES_COMPLETE = 8;
    static final int MSG_PACKAGE_ICON_CHANGED = 3;
    static final int MSG_PACKAGE_LIST_CHANGED = 2;
    static final int MSG_PACKAGE_SIZE_CHANGED = 4;
    static final int MSG_REBUILD_COMPLETE = 1;
    static final int MSG_RUNNING_STATE_CHANGED = 6;
    
    public MainHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      ApplicationsState.this.rebuildActiveSessions();
      switch (paramMessage.what)
      {
      }
      for (;;)
      {
        return;
        paramMessage = (ApplicationsState.Session)paramMessage.obj;
        if (ApplicationsState.this.mActiveSessions.contains(paramMessage))
        {
          paramMessage.mCallbacks.onRebuildComplete(paramMessage.mLastAppList);
          return;
          int i = 0;
          while (i < ApplicationsState.this.mActiveSessions.size())
          {
            ((ApplicationsState.Session)ApplicationsState.this.mActiveSessions.get(i)).mCallbacks.onPackageListChanged();
            i += 1;
          }
          continue;
          i = 0;
          while (i < ApplicationsState.this.mActiveSessions.size())
          {
            ((ApplicationsState.Session)ApplicationsState.this.mActiveSessions.get(i)).mCallbacks.onPackageIconChanged();
            i += 1;
          }
          continue;
          i = 0;
          while (i < ApplicationsState.this.mActiveSessions.size())
          {
            ((ApplicationsState.Session)ApplicationsState.this.mActiveSessions.get(i)).mCallbacks.onPackageSizeChanged((String)paramMessage.obj);
            i += 1;
          }
          continue;
          i = 0;
          while (i < ApplicationsState.this.mActiveSessions.size())
          {
            ((ApplicationsState.Session)ApplicationsState.this.mActiveSessions.get(i)).mCallbacks.onAllSizesComputed();
            i += 1;
          }
          continue;
          i = 0;
          label282:
          ApplicationsState.Callbacks localCallbacks;
          if (i < ApplicationsState.this.mActiveSessions.size())
          {
            localCallbacks = ((ApplicationsState.Session)ApplicationsState.this.mActiveSessions.get(i)).mCallbacks;
            if (paramMessage.arg1 == 0) {
              break label339;
            }
          }
          label339:
          for (boolean bool = true;; bool = false)
          {
            localCallbacks.onRunningStateChanged(bool);
            i += 1;
            break label282;
            break;
          }
          i = 0;
          while (i < ApplicationsState.this.mActiveSessions.size())
          {
            ((ApplicationsState.Session)ApplicationsState.this.mActiveSessions.get(i)).mCallbacks.onLauncherInfoChanged();
            i += 1;
          }
          continue;
          i = 0;
          while (i < ApplicationsState.this.mActiveSessions.size())
          {
            ((ApplicationsState.Session)ApplicationsState.this.mActiveSessions.get(i)).mCallbacks.onLoadEntriesCompleted();
            i += 1;
          }
        }
      }
    }
  }
  
  private class PackageIntentReceiver
    extends BroadcastReceiver
  {
    private PackageIntentReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      int i = 0;
      paramContext = paramIntent.getAction();
      if ("android.intent.action.PACKAGE_ADDED".equals(paramContext))
      {
        paramContext = paramIntent.getData().getEncodedSchemeSpecificPart();
        i = 0;
        while (i < ApplicationsState.this.mEntriesMap.size())
        {
          ApplicationsState.this.addPackage(paramContext, ApplicationsState.this.mEntriesMap.keyAt(i));
          i += 1;
        }
      }
      if ("android.intent.action.PACKAGE_REMOVED".equals(paramContext))
      {
        paramContext = paramIntent.getData().getEncodedSchemeSpecificPart();
        i = 0;
        while (i < ApplicationsState.this.mEntriesMap.size())
        {
          ApplicationsState.this.removePackage(paramContext, ApplicationsState.this.mEntriesMap.keyAt(i));
          i += 1;
        }
      }
      if ("android.intent.action.PACKAGE_CHANGED".equals(paramContext))
      {
        paramContext = paramIntent.getData().getEncodedSchemeSpecificPart();
        i = 0;
        while (i < ApplicationsState.this.mEntriesMap.size())
        {
          ApplicationsState.this.invalidatePackage(paramContext, ApplicationsState.this.mEntriesMap.keyAt(i));
          i += 1;
        }
      }
      int k;
      if (("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(paramContext)) || ("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(paramContext)))
      {
        paramIntent = paramIntent.getStringArrayExtra("android.intent.extra.changed_package_list");
        if ((paramIntent == null) || (paramIntent.length == 0)) {
          return;
        }
        if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(paramContext)) {
          k = paramIntent.length;
        }
      }
      else
      {
        while (i < k)
        {
          paramContext = paramIntent[i];
          int j = 0;
          while (j < ApplicationsState.this.mEntriesMap.size())
          {
            ApplicationsState.this.invalidatePackage(paramContext, ApplicationsState.this.mEntriesMap.keyAt(j));
            j += 1;
          }
          i += 1;
          continue;
          if (!"android.intent.action.USER_ADDED".equals(paramContext)) {
            break label322;
          }
          ApplicationsState.-wrap4(ApplicationsState.this, paramIntent.getIntExtra("android.intent.extra.user_handle", 55536));
        }
      }
      label322:
      while (!"android.intent.action.USER_REMOVED".equals(paramContext)) {
        return;
      }
      ApplicationsState.-wrap5(ApplicationsState.this, paramIntent.getIntExtra("android.intent.extra.user_handle", 55536));
    }
    
    void registerReceiver()
    {
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
      localIntentFilter.addDataScheme("package");
      ApplicationsState.this.mContext.registerReceiver(this, localIntentFilter);
      localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
      localIntentFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
      ApplicationsState.this.mContext.registerReceiver(this, localIntentFilter);
      localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.USER_ADDED");
      localIntentFilter.addAction("android.intent.action.USER_REMOVED");
      ApplicationsState.this.mContext.registerReceiver(this, localIntentFilter);
    }
    
    void unregisterReceiver()
    {
      ApplicationsState.this.mContext.unregisterReceiver(this);
    }
  }
  
  public class Session
  {
    final ApplicationsState.Callbacks mCallbacks;
    ArrayList<ApplicationsState.AppEntry> mLastAppList;
    boolean mRebuildAsync;
    Comparator<ApplicationsState.AppEntry> mRebuildComparator;
    ApplicationsState.AppFilter mRebuildFilter;
    boolean mRebuildForeground;
    boolean mRebuildRequested;
    ArrayList<ApplicationsState.AppEntry> mRebuildResult;
    final Object mRebuildSync = new Object();
    boolean mResumed;
    
    Session(ApplicationsState.Callbacks paramCallbacks)
    {
      this.mCallbacks = paramCallbacks;
    }
    
    public ArrayList<ApplicationsState.AppEntry> getAllApps()
    {
      synchronized (ApplicationsState.this.mEntriesMap)
      {
        ArrayList localArrayList = new ArrayList(ApplicationsState.this.mAppEntries);
        return localArrayList;
      }
    }
    
    void handleRebuildList()
    {
      ApplicationsState.AppFilter localAppFilter;
      Object localObject5;
      synchronized (this.mRebuildSync)
      {
        boolean bool = this.mRebuildRequested;
        if (!bool) {
          return;
        }
        localAppFilter = this.mRebuildFilter;
        localObject5 = this.mRebuildComparator;
        this.mRebuildRequested = false;
        this.mRebuildFilter = null;
        this.mRebuildComparator = null;
        if (this.mRebuildForeground)
        {
          Process.setThreadPriority(-2);
          this.mRebuildForeground = false;
        }
        if (localAppFilter != null) {
          localAppFilter.init(ApplicationsState.this.mContext);
        }
      }
      synchronized (ApplicationsState.this.mEntriesMap)
      {
        ??? = new ArrayList(ApplicationsState.this.mAppEntries);
        ??? = new ArrayList();
        int i = 0;
        while (i < ((List)???).size())
        {
          ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)((List)???).get(i);
          SparseArray localSparseArray;
          if ((localAppEntry != null) && ((localAppFilter == null) || (localAppFilter.filterApp(localAppEntry))))
          {
            localSparseArray = ApplicationsState.this.mEntriesMap;
            if (localObject5 == null) {}
          }
          try
          {
            localAppEntry.ensureLabel(ApplicationsState.this.mContext);
            ((ArrayList)???).add(localAppEntry);
            i += 1;
          }
          finally {}
        }
        localObject1 = finally;
        throw ((Throwable)localObject1);
      }
      if (localObject5 != null) {
        Collections.sort((List)???, (Comparator)localObject5);
      }
      synchronized (this.mRebuildSync)
      {
        if (!this.mRebuildRequested)
        {
          this.mLastAppList = ((ArrayList)???);
          if (this.mRebuildAsync) {
            break label296;
          }
          this.mRebuildResult = ((ArrayList)???);
          this.mRebuildSync.notifyAll();
        }
        label296:
        while (ApplicationsState.this.mMainHandler.hasMessages(1, this))
        {
          Process.setThreadPriority(10);
          return;
        }
        localObject5 = ApplicationsState.this.mMainHandler.obtainMessage(1, this);
        ApplicationsState.this.mMainHandler.sendMessage((Message)localObject5);
      }
    }
    
    public void pause()
    {
      synchronized (ApplicationsState.this.mEntriesMap)
      {
        if (this.mResumed)
        {
          this.mResumed = false;
          ApplicationsState.this.mSessionsChanged = true;
          ApplicationsState.this.mBackgroundHandler.removeMessages(1, this);
          ApplicationsState.this.doPauseIfNeededLocked();
        }
        return;
      }
    }
    
    public ArrayList<ApplicationsState.AppEntry> rebuild(ApplicationsState.AppFilter paramAppFilter, Comparator<ApplicationsState.AppEntry> paramComparator)
    {
      return rebuild(paramAppFilter, paramComparator, true);
    }
    
    public ArrayList<ApplicationsState.AppEntry> rebuild(ApplicationsState.AppFilter paramAppFilter, Comparator<ApplicationsState.AppEntry> paramComparator, boolean paramBoolean)
    {
      synchronized (this.mRebuildSync)
      {
        synchronized (ApplicationsState.this.mRebuildingSessions)
        {
          ApplicationsState.this.mRebuildingSessions.add(this);
          this.mRebuildRequested = true;
          this.mRebuildAsync = true;
          this.mRebuildFilter = paramAppFilter;
          this.mRebuildComparator = paramComparator;
          this.mRebuildForeground = paramBoolean;
          this.mRebuildResult = null;
          if (!ApplicationsState.this.mBackgroundHandler.hasMessages(1))
          {
            paramAppFilter = ApplicationsState.this.mBackgroundHandler.obtainMessage(1);
            ApplicationsState.this.mBackgroundHandler.sendMessage(paramAppFilter);
          }
          return null;
        }
      }
    }
    
    public void release()
    {
      pause();
      synchronized (ApplicationsState.this.mEntriesMap)
      {
        ApplicationsState.this.mSessions.remove(this);
        return;
      }
    }
    
    public void resume()
    {
      synchronized (ApplicationsState.this.mEntriesMap)
      {
        if (!this.mResumed)
        {
          this.mResumed = true;
          ApplicationsState.this.mSessionsChanged = true;
          ApplicationsState.this.doResumeIfNeededLocked();
        }
        return;
      }
    }
  }
  
  public static class SizeInfo
  {
    public long cacheSize;
    public long codeSize;
    public long dataSize;
    public long externalCacheSize;
    public long externalCodeSize;
    public long externalDataSize;
  }
  
  public static class VolumeFilter
    implements ApplicationsState.AppFilter
  {
    private final String mVolumeUuid;
    
    public VolumeFilter(String paramString)
    {
      this.mVolumeUuid = paramString;
    }
    
    public boolean filterApp(ApplicationsState.AppEntry paramAppEntry)
    {
      return Objects.equals(paramAppEntry.info.volumeUuid, this.mVolumeUuid);
    }
    
    public void init() {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\applications\ApplicationsState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */