package com.oneplus.settings.apploader;

import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import com.oneplus.settings.OPOnlineConfigManager;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.highpowerapp.PackageUtils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OPApplicationLoader
{
  public static final Comparator<OPAppModel> ALPHA_COMPARATOR = new Comparator()
  {
    private final Collator sCollator = Collator.getInstance();
    
    public int compare(OPAppModel paramAnonymousOPAppModel1, OPAppModel paramAnonymousOPAppModel2)
    {
      int i = this.sCollator.compare(paramAnonymousOPAppModel1.getLabel(), paramAnonymousOPAppModel2.getLabel());
      if (i != 0) {
        return i;
      }
      i = this.sCollator.compare(paramAnonymousOPAppModel1.getPkgName(), paramAnonymousOPAppModel2.getPkgName());
      if (i != 0) {
        return i;
      }
      return paramAnonymousOPAppModel1.getUid() - paramAnonymousOPAppModel2.getUid();
    }
  };
  private static final String CALCULATOR_PACKAGE_NAME = "com.oneplus.calculator";
  private static final String CAMERA_PACKAGE_NAME = "com.oneplus.camera";
  private static final String CARD_PACKAGE_NAME = "com.oneplus.card";
  private static final String CONTACS_PACKAGE_NAME = "com.android.contacts";
  public static final int DATA_LOAD_COMPLETED = 0;
  private static final String DESKCLOCK_PACKAGE_NAME = "com.oneplus.deskclock";
  private static final String FILEMANAGER_PACKAGE_NAME = "com.oneplus.filemanager";
  private static final String GALLERY_PACKAGE_NAME = "com.oneplus.gallery";
  private static final String GOOGLE_QUICK_SEARCH_BOX_PACKAGE_NAME = "com.google.android.googlequicksearchbox";
  public static final int LOAD_ALL_APP = 0;
  public static final int LOAD_ALL_APP_SORT_BY_SELECTED = 3;
  public static final int LOAD_SELECTED_APP = 1;
  public static final int LOAD_UNSELECTED_APP = 2;
  private static final String MARKET_PACKAGE_NAME = "com.oneplus.market";
  private static final String MMS_PACKAGE_NAME = "com.android.mms";
  private static final String NOTE_PACKAGE_NAME = "com.oneplus.note";
  public static final int OP_GAME_MODE_APP = 68;
  public static final int OP_READ_MODE_APP = 67;
  public static final String PACKAGENAME_CALENDAR = "com.google.android.calendar";
  public static final String PACKAGENAME_OP_CALENDAR = "com.oneplus.calendar";
  private static final String PHONE_PACKAGE_NAME = "com.android.dialer";
  private static long PROGRESS_MIN_SHOW_TIME = 0L;
  private static final String SETTINGS_PACKAGE_NAME = "com.android.settings";
  private static final String SOUNDRECORDER_PACKAGE_NAME = "com.oneplus.soundrecorder";
  private static final String TAG = "AppLockerDataController";
  private static final String WEATHER_PACKAGE_NAME = "com.oneplus.weather";
  private static final String WEATHER_PACKAGE_NAME_NET = "net.oneplus.weather";
  private static long WILL_SHOW_PROGRESS_TIME = 300L;
  private List<OPAppModel> mAllAppList = new ArrayList();
  private List<OPAppModel> mAllAppSelectedList = new ArrayList();
  private List<OPAppModel> mAllAppSortBySelectedList = new ArrayList();
  private List<OPAppModel> mAllAppUnSelectedList = new ArrayList();
  private AppOpsManager mAppOpsManager;
  private int mAppType;
  private Context mContext;
  private Handler mHandler1 = new Handler(Looper.getMainLooper());
  private boolean mHasShowProgress;
  private boolean mLoading = false;
  private View mLoadingContainer;
  private boolean mNeedLoadWorkProfileApps = true;
  private PackageManager mPackageManager;
  private final List<UserHandle> mProfiles;
  private List<OPAppModel> mSelectedAppList = new ArrayList();
  private Map<Integer, String> mSelectedAppMap = new HashMap();
  private Runnable mShowPromptRunnable;
  private long mShowPromptTime;
  private ExecutorService mThreadPool = Executors.newCachedThreadPool();
  private List<OPAppModel> mUnSelectedAppList = new ArrayList();
  private Map<String, Integer> mUnSelectedAppMap = new HashMap();
  private final UserManager mUserManager;
  
  static
  {
    PROGRESS_MIN_SHOW_TIME = 500L;
  }
  
  public OPApplicationLoader(Context paramContext, AppOpsManager paramAppOpsManager, PackageManager paramPackageManager)
  {
    this.mContext = paramContext;
    this.mAppOpsManager = paramAppOpsManager;
    this.mPackageManager = paramPackageManager;
    this.mUserManager = UserManager.get(paramContext);
    this.mProfiles = this.mUserManager.getUserProfiles();
  }
  
  public OPApplicationLoader(Context paramContext, PackageManager paramPackageManager)
  {
    this.mContext = paramContext;
    this.mPackageManager = paramPackageManager;
    this.mUserManager = UserManager.get(paramContext);
    this.mProfiles = this.mUserManager.getUserProfiles();
  }
  
  private Drawable getBadgedIcon(PackageManager paramPackageManager, ResolveInfo paramResolveInfo)
  {
    paramResolveInfo = paramResolveInfo.activityInfo.applicationInfo;
    return paramPackageManager.getUserBadgedIcon(paramPackageManager.loadUnbadgedItemIcon(paramResolveInfo, paramResolveInfo), new UserHandle(UserHandle.getUserId(paramResolveInfo.uid)));
  }
  
  private boolean isThisUserAProfileOfCurrentUser(int paramInt)
  {
    int j = this.mProfiles.size();
    int i = 0;
    while (i < j)
    {
      if (((UserHandle)this.mProfiles.get(i)).getIdentifier() == paramInt) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private boolean multiAppPackageExcludeFilter(Context paramContext, String paramString)
  {
    return OPOnlineConfigManager.getMultiAppWhiteList().contains(paramString);
  }
  
  private boolean packageExcludeFilter(String paramString)
  {
    if (this.mAppType != 63)
    {
      boolean bool2;
      if ((!"com.oneplus.deskclock".equals(paramString)) && (!"com.oneplus.market".equals(paramString)) && (!"com.android.settings".equals(paramString)) && (!"com.google.android.googlequicksearchbox".equals(paramString)) && (!"com.android.dialer".equals(paramString)) && (!"com.android.contacts".equals(paramString)) && (!"com.oneplus.weather".equals(paramString)) && (!"net.oneplus.weather".equals(paramString)) && (!"com.google.android.calendar".equals(paramString)) && (!"com.oneplus.calendar".equals(paramString)) && (!"com.oneplus.gallery".equals(paramString)) && (!"com.oneplus.filemanager".equals(paramString)) && (!"com.oneplus.calculator".equals(paramString)))
      {
        bool2 = "com.oneplus.card".equals(paramString);
        bool1 = bool2;
        if (this.mAppType == 68) {
          if ((bool2) || ("com.android.mms".equals(paramString))) {
            break label202;
          }
        }
      }
      label202:
      for (boolean bool1 = "com.oneplus.note".equals(paramString);; bool1 = true)
      {
        bool2 = bool1;
        if (this.mAppType == 67)
        {
          if ((bool1) || ("com.oneplus.soundrecorder".equals(paramString))) {
            break label207;
          }
          bool2 = "com.oneplus.camera".equals(paramString);
        }
        return bool2;
        bool2 = true;
        break;
      }
      label207:
      return true;
    }
    if ((0 == 0) && (!"com.oneplus.deskclock".equals(paramString)) && (!"com.android.settings".equals(paramString))) {
      return "com.google.android.googlequicksearchbox".equals(paramString);
    }
    return true;
  }
  
  public List<OPAppModel> getAllAppList()
  {
    return this.mAllAppList;
  }
  
  public List<OPAppModel> getAllAppSortBySelectList()
  {
    return this.mAllAppSortBySelectedList;
  }
  
  public List<OPAppModel> getAppListByType(int paramInt)
  {
    if (paramInt == 0) {
      return this.mAllAppList;
    }
    if (paramInt == 1) {
      return this.mSelectedAppList;
    }
    if (paramInt == 2) {
      return this.mUnSelectedAppList;
    }
    return this.mAllAppSortBySelectedList;
  }
  
  public List<OPAppModel> getSelectedAppList()
  {
    return this.mSelectedAppList;
  }
  
  public List<OPAppModel> getUnSelectedAppList()
  {
    return this.mUnSelectedAppList;
  }
  
  public void initData(final int paramInt, final Handler paramHandler)
  {
    paramHandler = new Runnable()
    {
      public void run()
      {
        OPApplicationLoader.this.onPreExecute();
        OPApplicationLoader.-set1(OPApplicationLoader.this, true);
        OPApplicationLoader.this.loadAppListByType(paramInt);
        OPApplicationLoader.-set1(OPApplicationLoader.this, false);
        OPApplicationLoader.this.onPostExecute();
        paramHandler.sendEmptyMessage(paramInt);
      }
    };
    this.mThreadPool.execute(paramHandler);
  }
  
  public boolean isLoading()
  {
    return this.mLoading;
  }
  
  public boolean isNeedLoadWorkProfileApps()
  {
    return this.mNeedLoadWorkProfileApps;
  }
  
  public void loadAllAppList()
  {
    Object localObject4;
    Object localObject3;
    Object localObject5;
    try
    {
      this.mAllAppList.clear();
      localObject4 = new Intent("android.intent.action.MAIN", null);
      ((Intent)localObject4).addCategory("android.intent.category.LAUNCHER");
      if (this.mNeedLoadWorkProfileApps)
      {
        localObject3 = new ArrayList();
        localObject5 = this.mUserManager.getProfiles(UserHandle.myUserId()).iterator();
        for (;;)
        {
          Object localObject1 = localObject3;
          if (!((Iterator)localObject5).hasNext()) {
            break;
          }
          localObject1 = (UserInfo)((Iterator)localObject5).next();
          ((List)localObject3).addAll(this.mPackageManager.queryIntentActivitiesAsUser((Intent)localObject4, 0, ((UserInfo)localObject1).id));
        }
      }
      localObject2 = this.mPackageManager.queryIntentActivities((Intent)localObject4, 0);
    }
    catch (Exception localException)
    {
      Log.e("AppLockerDataController", "some unknown error happened.");
      localException.printStackTrace();
      return;
    }
    if (((List)localObject2).isEmpty()) {
      return;
    }
    Object localObject2 = ((Iterable)localObject2).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (ResolveInfo)((Iterator)localObject2).next();
      localObject4 = ((ResolveInfo)localObject3).activityInfo.name;
      localObject4 = ((ResolveInfo)localObject3).activityInfo.packageName;
      localObject5 = (String)((ResolveInfo)localObject3).loadLabel(this.mPackageManager);
      if (!"com.oneplus.camera".equals(localObject4))
      {
        localObject4 = new OPAppModel((String)localObject4, (String)localObject5, "", ((ResolveInfo)localObject3).activityInfo.applicationInfo.uid, false);
        ((OPAppModel)localObject4).setAppIcon(getBadgedIcon(this.mPackageManager, (ResolveInfo)localObject3));
        this.mAllAppList.add(localObject4);
      }
    }
    Collections.sort(this.mAllAppList, ALPHA_COMPARATOR);
  }
  
  public void loadAllAppListSortBySelected(boolean paramBoolean)
  {
    for (;;)
    {
      Object localObject2;
      try
      {
        this.mAllAppSortBySelectedList.clear();
        this.mAllAppSelectedList.clear();
        this.mAllAppUnSelectedList.clear();
        Object localObject1 = new Intent("android.intent.action.MAIN", null);
        ((Intent)localObject1).addCategory("android.intent.category.LAUNCHER");
        localObject1 = this.mPackageManager.queryIntentActivities((Intent)localObject1, 0);
        if (((List)localObject1).isEmpty()) {
          return;
        }
        localObject1 = ((Iterable)localObject1).iterator();
        if (!((Iterator)localObject1).hasNext()) {
          break;
        }
        ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject1).next();
        localObject2 = localResolveInfo.activityInfo.name;
        localObject2 = localResolveInfo.activityInfo.packageName;
        String str = (String)localResolveInfo.loadLabel(this.mPackageManager);
        if (((paramBoolean) && (PackageUtils.isSystemApplication(this.mContext, (String)localObject2))) || (!multiAppPackageExcludeFilter(this.mContext, (String)localObject2))) {
          continue;
        }
        int i = localResolveInfo.activityInfo.applicationInfo.uid;
        if (this.mSelectedAppMap.containsKey(Integer.valueOf(i)))
        {
          bool = this.mSelectedAppMap.containsValue(localObject2);
          localObject2 = new OPAppModel((String)localObject2, str, "", i, bool);
          ((OPAppModel)localObject2).setAppIcon(getBadgedIcon(this.mPackageManager, localResolveInfo));
          if (!bool) {
            break label280;
          }
          this.mAllAppSelectedList.add(localObject2);
          continue;
        }
        boolean bool = false;
      }
      catch (Exception localException)
      {
        Log.e("AppLockerDataController", "some unknown error happened.");
        localException.printStackTrace();
        return;
      }
      continue;
      label280:
      this.mAllAppUnSelectedList.add(localObject2);
    }
    Collections.sort(this.mAllAppSelectedList, ALPHA_COMPARATOR);
    Collections.sort(this.mAllAppUnSelectedList, ALPHA_COMPARATOR);
    this.mAllAppSortBySelectedList.addAll(this.mAllAppSelectedList);
    this.mAllAppSortBySelectedList.addAll(this.mAllAppUnSelectedList);
  }
  
  public void loadAppListByType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      loadAllAppList();
      return;
    case 1: 
      loadSelectedAppList();
      return;
    case 2: 
      loadUnSelectedAppList();
      return;
    }
    loadAllAppListSortBySelected(true);
  }
  
  public void loadSelectedAppList()
  {
    Object localObject4;
    Object localObject3;
    Object localObject5;
    try
    {
      this.mSelectedAppList.clear();
      localObject4 = new Intent("android.intent.action.MAIN", null);
      ((Intent)localObject4).addCategory("android.intent.category.LAUNCHER");
      if (this.mNeedLoadWorkProfileApps)
      {
        localObject3 = new ArrayList();
        localObject5 = this.mUserManager.getProfiles(UserHandle.myUserId()).iterator();
        for (;;)
        {
          Object localObject1 = localObject3;
          if (!((Iterator)localObject5).hasNext()) {
            break;
          }
          localObject1 = (UserInfo)((Iterator)localObject5).next();
          ((List)localObject3).addAll(this.mPackageManager.queryIntentActivitiesAsUser((Intent)localObject4, 0, ((UserInfo)localObject1).id));
        }
      }
      localObject2 = this.mPackageManager.queryIntentActivities((Intent)localObject4, 0);
    }
    catch (Exception localException)
    {
      Log.e("AppLockerDataController", "some unknown error happened.");
      localException.printStackTrace();
      return;
    }
    if (((List)localObject2).isEmpty()) {
      return;
    }
    Object localObject2 = ((Iterable)localObject2).iterator();
    label322:
    label325:
    for (;;)
    {
      int i;
      if (((Iterator)localObject2).hasNext())
      {
        localObject3 = (ResolveInfo)((Iterator)localObject2).next();
        localObject4 = ((ResolveInfo)localObject3).activityInfo.name;
        localObject4 = ((ResolveInfo)localObject3).activityInfo.packageName;
        localObject5 = (String)((ResolveInfo)localObject3).loadLabel(this.mPackageManager);
        if (packageExcludeFilter((String)localObject4)) {
          continue;
        }
        i = ((ResolveInfo)localObject3).activityInfo.applicationInfo.uid;
        if (!this.mSelectedAppMap.containsKey(Integer.valueOf(i))) {
          break label322;
        }
      }
      for (boolean bool = this.mSelectedAppMap.containsValue(localObject4);; bool = false)
      {
        if (!bool) {
          break label325;
        }
        localObject4 = new OPAppModel((String)localObject4, (String)localObject5, "", i, bool);
        ((OPAppModel)localObject4).setAppIcon(getBadgedIcon(this.mPackageManager, (ResolveInfo)localObject3));
        this.mSelectedAppList.add(localObject4);
        break;
        Collections.sort(this.mSelectedAppList, ALPHA_COMPARATOR);
        return;
      }
    }
  }
  
  public Map<Integer, String> loadSelectedGameOrReadAppMap(int paramInt)
  {
    Object localObject = this.mAppOpsManager.getPackagesForOps(new int[] { paramInt });
    if (this.mSelectedAppMap != null) {
      this.mSelectedAppMap.clear();
    }
    if (localObject != null)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        AppOpsManager.PackageOps localPackageOps = (AppOpsManager.PackageOps)((Iterator)localObject).next();
        int i = UserHandle.getUserId(localPackageOps.getUid());
        int j = localPackageOps.getUid();
        if (isThisUserAProfileOfCurrentUser(i))
        {
          Iterator localIterator = localPackageOps.getOps().iterator();
          while (localIterator.hasNext())
          {
            AppOpsManager.OpEntry localOpEntry = (AppOpsManager.OpEntry)localIterator.next();
            if ((localOpEntry.getOp() == paramInt) && (localOpEntry.getMode() == 0)) {
              this.mSelectedAppMap.put(Integer.valueOf(j), localPackageOps.getPackageName());
            }
          }
        }
      }
    }
    return this.mSelectedAppMap;
  }
  
  public void loadUnSelectedAppList()
  {
    Object localObject4;
    Object localObject3;
    Object localObject5;
    try
    {
      this.mUnSelectedAppList.clear();
      localObject4 = new Intent("android.intent.action.MAIN", null);
      ((Intent)localObject4).addCategory("android.intent.category.LAUNCHER");
      if (this.mNeedLoadWorkProfileApps)
      {
        localObject3 = new ArrayList();
        localObject5 = this.mUserManager.getProfiles(UserHandle.myUserId()).iterator();
        for (;;)
        {
          Object localObject1 = localObject3;
          if (!((Iterator)localObject5).hasNext()) {
            break;
          }
          localObject1 = (UserInfo)((Iterator)localObject5).next();
          ((List)localObject3).addAll(this.mPackageManager.queryIntentActivitiesAsUser((Intent)localObject4, 0, ((UserInfo)localObject1).id));
        }
      }
      localObject2 = this.mPackageManager.queryIntentActivities((Intent)localObject4, 0);
    }
    catch (Exception localException)
    {
      Log.e("AppLockerDataController", "some unknown error happened.");
      localException.printStackTrace();
      return;
    }
    if (((List)localObject2).isEmpty()) {
      return;
    }
    Object localObject2 = ((Iterable)localObject2).iterator();
    label322:
    label325:
    for (;;)
    {
      int i;
      if (((Iterator)localObject2).hasNext())
      {
        localObject3 = (ResolveInfo)((Iterator)localObject2).next();
        localObject4 = ((ResolveInfo)localObject3).activityInfo.name;
        localObject4 = ((ResolveInfo)localObject3).activityInfo.packageName;
        localObject5 = (String)((ResolveInfo)localObject3).loadLabel(this.mPackageManager);
        if (packageExcludeFilter((String)localObject4)) {
          continue;
        }
        i = ((ResolveInfo)localObject3).activityInfo.applicationInfo.uid;
        if (!this.mSelectedAppMap.containsKey(Integer.valueOf(i))) {
          break label322;
        }
      }
      for (boolean bool = this.mSelectedAppMap.containsValue(localObject4);; bool = false)
      {
        if (bool) {
          break label325;
        }
        localObject4 = new OPAppModel((String)localObject4, (String)localObject5, "", i, bool);
        ((OPAppModel)localObject4).setAppIcon(getBadgedIcon(this.mPackageManager, (ResolveInfo)localObject3));
        this.mUnSelectedAppList.add(localObject4);
        break;
        Collections.sort(this.mUnSelectedAppList, ALPHA_COMPARATOR);
        return;
      }
    }
  }
  
  protected final void onPostExecute()
  {
    if (this.mHasShowProgress)
    {
      long l1 = System.currentTimeMillis();
      long l2 = this.mShowPromptTime;
      l1 = PROGRESS_MIN_SHOW_TIME - (l1 - l2);
      if (l1 > 0L)
      {
        this.mHandler1.postDelayed(new Runnable()
        {
          public void run()
          {
            if (OPApplicationLoader.-get0(OPApplicationLoader.this) != null) {
              OPApplicationLoader.-get0(OPApplicationLoader.this).setVisibility(8);
            }
          }
        }, l1);
        return;
      }
      this.mHandler1.post(new Runnable()
      {
        public void run()
        {
          if (OPApplicationLoader.-get0(OPApplicationLoader.this) != null) {
            OPApplicationLoader.-get0(OPApplicationLoader.this).setVisibility(8);
          }
        }
      });
      return;
    }
    this.mHandler1.removeCallbacks(this.mShowPromptRunnable);
  }
  
  protected final void onPreExecute()
  {
    this.mHasShowProgress = false;
    this.mShowPromptRunnable = new Runnable()
    {
      public void run()
      {
        OPApplicationLoader.-set0(OPApplicationLoader.this, true);
        if (OPApplicationLoader.-get0(OPApplicationLoader.this) != null) {
          OPApplicationLoader.-get0(OPApplicationLoader.this).setVisibility(0);
        }
        OPApplicationLoader.-set2(OPApplicationLoader.this, System.currentTimeMillis());
      }
    };
    this.mHandler1.postDelayed(this.mShowPromptRunnable, WILL_SHOW_PROGRESS_TIME);
  }
  
  public void setAppType(int paramInt)
  {
    this.mAppType = paramInt;
  }
  
  public void setNeedLoadWorkProfileApps(boolean paramBoolean)
  {
    this.mNeedLoadWorkProfileApps = paramBoolean;
  }
  
  public void setmLoadingContainer(View paramView)
  {
    this.mLoadingContainer = paramView;
  }
  
  public class GestureAppComparetor
    implements Comparator<OPAppModel>
  {
    public GestureAppComparetor() {}
    
    public int compare(OPAppModel paramOPAppModel1, OPAppModel paramOPAppModel2)
    {
      if (paramOPAppModel1.getLabel().equals(paramOPAppModel2.getLabel())) {
        return 0;
      }
      return paramOPAppModel1.getLabel().compareTo(paramOPAppModel2.getLabel());
    }
  }
  
  public class SelectedAppComparetor
    implements Comparator<OPAppModel>
  {
    public SelectedAppComparetor() {}
    
    public int compare(OPAppModel paramOPAppModel1, OPAppModel paramOPAppModel2)
    {
      if ((!paramOPAppModel1.isSelected()) || (paramOPAppModel2.isSelected()))
      {
        if ((!paramOPAppModel1.isSelected()) && (paramOPAppModel2.isSelected())) {
          return 1;
        }
      }
      else {
        return -1;
      }
      return 0;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\apploader\OPApplicationLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */