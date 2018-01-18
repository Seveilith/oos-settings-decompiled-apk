package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseArray;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.InterestingConfigChanges;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RunningState
{
  static final long CONTENTS_UPDATE_DELAY = 2000L;
  static final boolean DEBUG_COMPARE = false;
  static final int MAX_SERVICES = 100;
  static final int MSG_REFRESH_UI = 3;
  static final int MSG_RESET_CONTENTS = 1;
  static final int MSG_UPDATE_CONTENTS = 2;
  static final int MSG_UPDATE_TIME = 4;
  static final String TAG = "RunningState";
  static final long TIME_UPDATE_DELAY = 1000L;
  static Object sGlobalLock = new Object();
  static RunningState sInstance;
  final ArrayList<ProcessItem> mAllProcessItems = new ArrayList();
  final ActivityManager mAm;
  final Context mApplicationContext;
  final Comparator<MergedItem> mBackgroundComparator = new Comparator()
  {
    public int compare(RunningState.MergedItem paramAnonymousMergedItem1, RunningState.MergedItem paramAnonymousMergedItem2)
    {
      int i = -1;
      if (paramAnonymousMergedItem1.mUserId != paramAnonymousMergedItem2.mUserId)
      {
        if (paramAnonymousMergedItem1.mUserId == RunningState.this.mMyUserId) {
          return -1;
        }
        if (paramAnonymousMergedItem2.mUserId == RunningState.this.mMyUserId) {
          return 1;
        }
        if (paramAnonymousMergedItem1.mUserId < paramAnonymousMergedItem2.mUserId) {
          return -1;
        }
        return 1;
      }
      if (paramAnonymousMergedItem1.mProcess == paramAnonymousMergedItem2.mProcess)
      {
        if (paramAnonymousMergedItem1.mLabel == paramAnonymousMergedItem2.mLabel) {
          return 0;
        }
        if (paramAnonymousMergedItem1.mLabel != null) {
          i = paramAnonymousMergedItem1.mLabel.compareTo(paramAnonymousMergedItem2.mLabel);
        }
        return i;
      }
      if (paramAnonymousMergedItem1.mProcess == null) {
        return -1;
      }
      if (paramAnonymousMergedItem2.mProcess == null) {
        return 1;
      }
      ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo1 = paramAnonymousMergedItem1.mProcess.mRunningProcessInfo;
      ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo2 = paramAnonymousMergedItem2.mProcess.mRunningProcessInfo;
      int j;
      if (localRunningAppProcessInfo1.importance >= 400)
      {
        i = 1;
        if (localRunningAppProcessInfo2.importance < 400) {
          break label185;
        }
        j = 1;
      }
      for (;;)
      {
        if (i != j)
        {
          if (i != 0)
          {
            return 1;
            i = 0;
            break;
            label185:
            j = 0;
            continue;
          }
          return -1;
        }
      }
      if ((localRunningAppProcessInfo1.flags & 0x4) != 0)
      {
        i = 1;
        if ((localRunningAppProcessInfo2.flags & 0x4) == 0) {
          break label235;
        }
        j = 1;
      }
      for (;;)
      {
        if (i != j)
        {
          if (i != 0)
          {
            return -1;
            i = 0;
            break;
            label235:
            j = 0;
            continue;
          }
          return 1;
        }
      }
      if (localRunningAppProcessInfo1.lru != localRunningAppProcessInfo2.lru)
      {
        if (localRunningAppProcessInfo1.lru < localRunningAppProcessInfo2.lru) {
          return -1;
        }
        return 1;
      }
      if (paramAnonymousMergedItem1.mProcess.mLabel == paramAnonymousMergedItem2.mProcess.mLabel) {
        return 0;
      }
      if (paramAnonymousMergedItem1.mProcess.mLabel == null) {
        return 1;
      }
      if (paramAnonymousMergedItem2.mProcess.mLabel == null) {
        return -1;
      }
      return paramAnonymousMergedItem1.mProcess.mLabel.compareTo(paramAnonymousMergedItem2.mProcess.mLabel);
    }
  };
  final BackgroundHandler mBackgroundHandler;
  ArrayList<MergedItem> mBackgroundItems = new ArrayList();
  long mBackgroundProcessMemory;
  final HandlerThread mBackgroundThread;
  long mForegroundProcessMemory;
  final Handler mHandler = new Handler()
  {
    int mNextUpdate = 0;
    
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      }
      for (;;)
      {
        return;
        if (???.arg1 != 0) {}
        for (int i = 2;; i = 1)
        {
          this.mNextUpdate = i;
          return;
        }
        synchronized (RunningState.this.mLock)
        {
          boolean bool = RunningState.this.mResumed;
          if (!bool) {
            return;
          }
          removeMessages(4);
          sendMessageDelayed(obtainMessage(4), 1000L);
          if (RunningState.this.mRefreshUiListener == null) {
            continue;
          }
          RunningState.this.mRefreshUiListener.onRefreshUi(this.mNextUpdate);
          this.mNextUpdate = 0;
          return;
        }
      }
    }
  };
  boolean mHaveData;
  final boolean mHideManagedProfiles;
  final InterestingConfigChanges mInterestingConfigChanges = new InterestingConfigChanges();
  final ArrayList<ProcessItem> mInterestingProcesses = new ArrayList();
  ArrayList<BaseItem> mItems = new ArrayList();
  final Object mLock = new Object();
  ArrayList<MergedItem> mMergedItems = new ArrayList();
  final int mMyUserId;
  int mNumBackgroundProcesses;
  int mNumForegroundProcesses;
  int mNumServiceProcesses;
  final SparseArray<MergedItem> mOtherUserBackgroundItems = new SparseArray();
  final SparseArray<MergedItem> mOtherUserMergedItems = new SparseArray();
  final PackageManager mPm;
  final ArrayList<ProcessItem> mProcessItems = new ArrayList();
  OnRefreshUiListener mRefreshUiListener;
  boolean mResumed;
  final SparseArray<ProcessItem> mRunningProcesses = new SparseArray();
  int mSequence = 0;
  final ServiceProcessComparator mServiceProcessComparator = new ServiceProcessComparator();
  long mServiceProcessMemory;
  final SparseArray<HashMap<String, ProcessItem>> mServiceProcessesByName = new SparseArray();
  final SparseArray<ProcessItem> mServiceProcessesByPid = new SparseArray();
  final SparseArray<AppProcessInfo> mTmpAppProcesses = new SparseArray();
  final UserManager mUm;
  private final UserManagerBroadcastReceiver mUmBroadcastReceiver = new UserManagerBroadcastReceiver(null);
  ArrayList<MergedItem> mUserBackgroundItems = new ArrayList();
  boolean mWatchingBackgroundItems;
  
  private RunningState(Context paramContext)
  {
    this.mApplicationContext = paramContext.getApplicationContext();
    this.mAm = ((ActivityManager)this.mApplicationContext.getSystemService("activity"));
    this.mPm = this.mApplicationContext.getPackageManager();
    this.mUm = ((UserManager)this.mApplicationContext.getSystemService("user"));
    this.mMyUserId = UserHandle.myUserId();
    paramContext = this.mUm.getUserInfo(this.mMyUserId);
    if ((paramContext != null) && (paramContext.canHaveProfile())) {}
    for (boolean bool = false;; bool = true)
    {
      this.mHideManagedProfiles = bool;
      this.mResumed = false;
      this.mBackgroundThread = new HandlerThread("RunningState:Background");
      this.mBackgroundThread.start();
      this.mBackgroundHandler = new BackgroundHandler(this.mBackgroundThread.getLooper());
      this.mUmBroadcastReceiver.register(this.mApplicationContext);
      return;
    }
  }
  
  private void addOtherUserItem(Context paramContext, ArrayList<MergedItem> paramArrayList, SparseArray<MergedItem> paramSparseArray, MergedItem paramMergedItem)
  {
    int j = 1;
    MergedItem localMergedItem = (MergedItem)paramSparseArray.get(paramMergedItem.mUserId);
    int i = j;
    if (localMergedItem != null) {
      if (localMergedItem.mCurSeq == this.mSequence) {
        break label71;
      }
    }
    Object localObject;
    label71:
    for (i = j;; i = 0)
    {
      localObject = localMergedItem;
      if (i == 0) {
        break label193;
      }
      localObject = this.mUm.getUserInfo(paramMergedItem.mUserId);
      if (localObject != null) {
        break;
      }
      return;
    }
    if ((this.mHideManagedProfiles) && (((UserInfo)localObject).isManagedProfile())) {
      return;
    }
    if (localMergedItem == null)
    {
      localMergedItem = new MergedItem(paramMergedItem.mUserId);
      paramSparseArray.put(paramMergedItem.mUserId, localMergedItem);
    }
    for (paramSparseArray = localMergedItem;; paramSparseArray = localMergedItem)
    {
      paramSparseArray.mCurSeq = this.mSequence;
      paramSparseArray.mUser = new UserState();
      paramSparseArray.mUser.mInfo = ((UserInfo)localObject);
      paramSparseArray.mUser.mIcon = Utils.getUserIcon(paramContext, this.mUm, (UserInfo)localObject);
      paramSparseArray.mUser.mLabel = Utils.getUserLabel(paramContext, (UserInfo)localObject);
      paramArrayList.add(paramSparseArray);
      localObject = paramSparseArray;
      label193:
      ((MergedItem)localObject).mChildren.add(paramMergedItem);
      return;
      localMergedItem.mChildren.clear();
    }
  }
  
  static RunningState getInstance(Context paramContext)
  {
    synchronized (sGlobalLock)
    {
      if (sInstance == null) {
        sInstance = new RunningState(paramContext);
      }
      paramContext = sInstance;
      return paramContext;
    }
  }
  
  private boolean isInterestingProcess(ActivityManager.RunningAppProcessInfo paramRunningAppProcessInfo)
  {
    if ((paramRunningAppProcessInfo.flags & 0x1) != 0) {
      return true;
    }
    return ((paramRunningAppProcessInfo.flags & 0x2) == 0) && (paramRunningAppProcessInfo.importance >= 100) && (paramRunningAppProcessInfo.importance < 170) && (paramRunningAppProcessInfo.importanceReasonCode == 0);
  }
  
  static CharSequence makeLabel(PackageManager paramPackageManager, String paramString, PackageItemInfo paramPackageItemInfo)
  {
    if ((paramPackageItemInfo != null) && ((paramPackageItemInfo.labelRes != 0) || (paramPackageItemInfo.nonLocalizedLabel != null)))
    {
      paramPackageManager = paramPackageItemInfo.loadLabel(paramPackageManager);
      if (paramPackageManager != null) {
        return paramPackageManager;
      }
    }
    paramPackageManager = paramString;
    int i = paramString.lastIndexOf('.');
    if (i >= 0) {
      paramPackageManager = paramString.substring(i + 1, paramString.length());
    }
    return paramPackageManager;
  }
  
  private void reset()
  {
    this.mServiceProcessesByName.clear();
    this.mServiceProcessesByPid.clear();
    this.mInterestingProcesses.clear();
    this.mRunningProcesses.clear();
    this.mProcessItems.clear();
    this.mAllProcessItems.clear();
  }
  
  private boolean update(Context arg1, ActivityManager arg2)
  {
    localObject3 = ???.getPackageManager();
    this.mSequence += 1;
    bool1 = false;
    localObject5 = ???.getRunningServices(100);
    if (localObject5 != null)
    {
      i = ((List)localObject5).size();
      j = 0;
      m = i;
      label46:
      if (j >= m) {
        break label163;
      }
      localObject1 = (ActivityManager.RunningServiceInfo)((List)localObject5).get(j);
      if ((((ActivityManager.RunningServiceInfo)localObject1).started) || (((ActivityManager.RunningServiceInfo)localObject1).clientLabel != 0)) {
        break label121;
      }
      ((List)localObject5).remove(j);
      k = j - 1;
      i = m - 1;
    }
    for (;;)
    {
      j = k + 1;
      m = i;
      break label46;
      i = 0;
      break;
      label121:
      i = m;
      k = j;
      if ((((ActivityManager.RunningServiceInfo)localObject1).flags & 0x8) != 0)
      {
        ((List)localObject5).remove(j);
        k = j - 1;
        i = m - 1;
      }
    }
    label163:
    localObject4 = ???.getRunningAppProcesses();
    if (localObject4 != null) {}
    for (i = ((List)localObject4).size();; i = 0)
    {
      this.mTmpAppProcesses.clear();
      j = 0;
      while (j < i)
      {
        ??? = (ActivityManager.RunningAppProcessInfo)((List)localObject4).get(j);
        this.mTmpAppProcesses.put(???.pid, new AppProcessInfo(???));
        j += 1;
      }
    }
    j = 0;
    while (j < m)
    {
      ??? = (ActivityManager.RunningServiceInfo)((List)localObject5).get(j);
      if ((???.restarting == 0L) && (???.pid > 0))
      {
        localObject1 = (AppProcessInfo)this.mTmpAppProcesses.get(???.pid);
        if (localObject1 != null)
        {
          ((AppProcessInfo)localObject1).hasServices = true;
          if (???.foreground) {
            ((AppProcessInfo)localObject1).hasForegroundServices = true;
          }
        }
      }
      j += 1;
    }
    j = 0;
    if (j < m)
    {
      localObject6 = (ActivityManager.RunningServiceInfo)((List)localObject5).get(j);
      if ((((ActivityManager.RunningServiceInfo)localObject6).restarting == 0L) && (((ActivityManager.RunningServiceInfo)localObject6).pid > 0))
      {
        ??? = (AppProcessInfo)this.mTmpAppProcesses.get(((ActivityManager.RunningServiceInfo)localObject6).pid);
        if ((??? != null) && (!???.hasForegroundServices)) {}
      }
      else
      {
        label401:
        localObject1 = (HashMap)this.mServiceProcessesByName.get(((ActivityManager.RunningServiceInfo)localObject6).uid);
        ??? = (ActivityManager)localObject1;
        if (localObject1 == null)
        {
          ??? = new HashMap();
          this.mServiceProcessesByName.put(((ActivityManager.RunningServiceInfo)localObject6).uid, ???);
        }
        localObject2 = (ProcessItem)???.get(((ActivityManager.RunningServiceInfo)localObject6).process);
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          bool1 = true;
          localObject1 = new ProcessItem(???, ((ActivityManager.RunningServiceInfo)localObject6).uid, ((ActivityManager.RunningServiceInfo)localObject6).process);
          ???.put(((ActivityManager.RunningServiceInfo)localObject6).process, localObject1);
        }
        bool2 = bool1;
        if (((ProcessItem)localObject1).mCurSeq != this.mSequence) {
          if (((ActivityManager.RunningServiceInfo)localObject6).restarting != 0L) {
            break label747;
          }
        }
      }
      label646:
      label747:
      for (k = ((ActivityManager.RunningServiceInfo)localObject6).pid;; k = 0)
      {
        if (k != ((ProcessItem)localObject1).mPid)
        {
          bool2 = true;
          bool1 = bool2;
          if (((ProcessItem)localObject1).mPid != k)
          {
            if (((ProcessItem)localObject1).mPid != 0) {
              this.mServiceProcessesByPid.remove(((ProcessItem)localObject1).mPid);
            }
            if (k != 0) {
              this.mServiceProcessesByPid.put(k, localObject1);
            }
            ((ProcessItem)localObject1).mPid = k;
            bool1 = bool2;
          }
        }
        ((ProcessItem)localObject1).mDependentProcesses.clear();
        ((ProcessItem)localObject1).mCurSeq = this.mSequence;
        bool2 = bool1;
        bool1 = bool2 | ((ProcessItem)localObject1).updateService(???, (ActivityManager.RunningServiceInfo)localObject6);
        j += 1;
        break;
        if (???.info.importance >= 300) {
          break label401;
        }
        n = 0;
        for (??? = (AppProcessInfo)this.mTmpAppProcesses.get(???.info.importanceReasonPid);; ??? = (AppProcessInfo)this.mTmpAppProcesses.get(???.info.importanceReasonPid))
        {
          k = n;
          if (??? != null)
          {
            if ((???.hasServices) || (isInterestingProcess(???.info))) {
              k = 1;
            }
          }
          else
          {
            if (k == 0) {
              break;
            }
            break label646;
          }
        }
      }
    }
    j = 0;
    if (j < i)
    {
      localObject2 = (ActivityManager.RunningAppProcessInfo)((List)localObject4).get(j);
      localObject1 = (ProcessItem)this.mServiceProcessesByPid.get(((ActivityManager.RunningAppProcessInfo)localObject2).pid);
      bool2 = bool1;
      ??? = (ActivityManager)localObject1;
      if (localObject1 == null)
      {
        localObject1 = (ProcessItem)this.mRunningProcesses.get(((ActivityManager.RunningAppProcessInfo)localObject2).pid);
        ??? = (ActivityManager)localObject1;
        if (localObject1 == null)
        {
          bool1 = true;
          ??? = new ProcessItem(???, ((ActivityManager.RunningAppProcessInfo)localObject2).uid, ((ActivityManager.RunningAppProcessInfo)localObject2).processName);
          ???.mPid = ((ActivityManager.RunningAppProcessInfo)localObject2).pid;
          this.mRunningProcesses.put(((ActivityManager.RunningAppProcessInfo)localObject2).pid, ???);
        }
        ???.mDependentProcesses.clear();
        bool2 = bool1;
      }
      if (isInterestingProcess((ActivityManager.RunningAppProcessInfo)localObject2))
      {
        if (!this.mInterestingProcesses.contains(???))
        {
          bool2 = true;
          this.mInterestingProcesses.add(???);
        }
        ???.mCurSeq = this.mSequence;
        ???.mInteresting = true;
        ???.ensureLabel((PackageManager)localObject3);
      }
      for (bool1 = bool2;; bool1 = bool2)
      {
        ???.mRunningSeq = this.mSequence;
        ???.mRunningProcessInfo = ((ActivityManager.RunningAppProcessInfo)localObject2);
        j += 1;
        break;
        ???.mInteresting = false;
      }
    }
    j = this.mRunningProcesses.size();
    i = 0;
    while (i < j)
    {
      localObject2 = (ProcessItem)this.mRunningProcesses.valueAt(i);
      if (((ProcessItem)localObject2).mRunningSeq == this.mSequence)
      {
        k = ((ProcessItem)localObject2).mRunningProcessInfo.importanceReasonPid;
        if (k != 0)
        {
          localObject1 = (ProcessItem)this.mServiceProcessesByPid.get(k);
          ??? = (ActivityManager)localObject1;
          if (localObject1 == null) {
            ??? = (ProcessItem)this.mRunningProcesses.get(k);
          }
          if (??? != null) {
            ???.mDependentProcesses.put(((ProcessItem)localObject2).mPid, localObject2);
          }
        }
        for (;;)
        {
          i += 1;
          break;
          ((ProcessItem)localObject2).mClient = null;
        }
      }
      bool1 = true;
      this.mRunningProcesses.remove(this.mRunningProcesses.keyAt(i));
      j -= 1;
    }
    j = this.mInterestingProcesses.size();
    i = 0;
    while (i < j)
    {
      ??? = (ProcessItem)this.mInterestingProcesses.get(i);
      if (???.mInteresting)
      {
        k = j;
        m = i;
        if (this.mRunningProcesses.get(???.mPid) != null) {}
      }
      else
      {
        bool1 = true;
        this.mInterestingProcesses.remove(i);
        m = i - 1;
        k = j - 1;
      }
      i = m + 1;
      j = k;
    }
    j = this.mServiceProcessesByPid.size();
    i = 0;
    while (i < j)
    {
      ??? = (ProcessItem)this.mServiceProcessesByPid.valueAt(i);
      bool2 = bool1;
      if (???.mCurSeq == this.mSequence) {
        bool2 = bool1 | ???.buildDependencyChain(???, (PackageManager)localObject3, this.mSequence);
      }
      i += 1;
      bool1 = bool2;
    }
    ??? = null;
    i = 0;
    while (i < this.mServiceProcessesByName.size())
    {
      localObject2 = (HashMap)this.mServiceProcessesByName.valueAt(i);
      localObject4 = ((HashMap)localObject2).values().iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (ProcessItem)((Iterator)localObject4).next();
        if (((ProcessItem)localObject5).mCurSeq == this.mSequence)
        {
          ((ProcessItem)localObject5).ensureLabel((PackageManager)localObject3);
          if (((ProcessItem)localObject5).mPid == 0) {
            ((ProcessItem)localObject5).mDependentProcesses.clear();
          }
          localObject1 = ((ProcessItem)localObject5).mServices.values().iterator();
          bool2 = bool1;
          for (;;)
          {
            bool1 = bool2;
            if (!((Iterator)localObject1).hasNext()) {
              break;
            }
            if (((ServiceItem)((Iterator)localObject1).next()).mCurSeq != this.mSequence)
            {
              bool2 = true;
              ((Iterator)localObject1).remove();
            }
          }
        }
        bool2 = true;
        ((Iterator)localObject4).remove();
        localObject1 = ???;
        if (((HashMap)localObject2).size() == 0)
        {
          localObject1 = ???;
          if (??? == null) {
            localObject1 = new ArrayList();
          }
          ((ArrayList)localObject1).add(Integer.valueOf(this.mServiceProcessesByName.keyAt(i)));
        }
        bool1 = bool2;
        ??? = (ActivityManager)localObject1;
        if (((ProcessItem)localObject5).mPid != 0)
        {
          this.mServiceProcessesByPid.remove(((ProcessItem)localObject5).mPid);
          bool1 = bool2;
          ??? = (ActivityManager)localObject1;
        }
      }
      i += 1;
    }
    if (??? != null)
    {
      i = 0;
      while (i < ???.size())
      {
        j = ((Integer)???.get(i)).intValue();
        this.mServiceProcessesByName.remove(j);
        i += 1;
      }
    }
    if (bool1)
    {
      localObject2 = new ArrayList();
      i = 0;
      while (i < this.mServiceProcessesByName.size())
      {
        ??? = ((HashMap)this.mServiceProcessesByName.valueAt(i)).values().iterator();
        while (???.hasNext())
        {
          localObject1 = (ProcessItem)???.next();
          ((ProcessItem)localObject1).mIsSystem = false;
          ((ProcessItem)localObject1).mIsStarted = true;
          ((ProcessItem)localObject1).mActiveSince = Long.MAX_VALUE;
          localObject3 = ((ProcessItem)localObject1).mServices.values().iterator();
          while (((Iterator)localObject3).hasNext())
          {
            localObject4 = (ServiceItem)((Iterator)localObject3).next();
            if ((((ServiceItem)localObject4).mServiceInfo != null) && ((((ServiceItem)localObject4).mServiceInfo.applicationInfo.flags & 0x1) != 0)) {
              ((ProcessItem)localObject1).mIsSystem = true;
            }
            if ((((ServiceItem)localObject4).mRunningService != null) && (((ServiceItem)localObject4).mRunningService.clientLabel != 0))
            {
              ((ProcessItem)localObject1).mIsStarted = false;
              if (((ProcessItem)localObject1).mActiveSince > ((ServiceItem)localObject4).mRunningService.activeSince) {
                ((ProcessItem)localObject1).mActiveSince = ((ServiceItem)localObject4).mRunningService.activeSince;
              }
            }
          }
          ((ArrayList)localObject2).add(localObject1);
        }
        i += 1;
      }
      Collections.sort((List)localObject2, this.mServiceProcessComparator);
      localObject3 = new ArrayList();
      localObject4 = new ArrayList();
      this.mProcessItems.clear();
      i = 0;
      if (i < ((ArrayList)localObject2).size())
      {
        localObject5 = (ProcessItem)((ArrayList)localObject2).get(i);
        ((ProcessItem)localObject5).mNeedDivider = false;
        j = this.mProcessItems.size();
        ((ProcessItem)localObject5).addDependentProcesses((ArrayList)localObject3, this.mProcessItems);
        ((ArrayList)localObject3).add(localObject5);
        if (((ProcessItem)localObject5).mPid > 0) {
          this.mProcessItems.add(localObject5);
        }
        ??? = null;
        bool2 = false;
        boolean bool3 = false;
        localObject1 = ((ProcessItem)localObject5).mServices.values().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject6 = (ServiceItem)((Iterator)localObject1).next();
          ((ServiceItem)localObject6).mNeedDivider = bool3;
          boolean bool4 = true;
          ((ArrayList)localObject3).add(localObject6);
          if (((ServiceItem)localObject6).mMergedItem != null)
          {
            bool3 = bool2;
            if (??? != null)
            {
              bool3 = bool2;
              if (??? != ((ServiceItem)localObject6).mMergedItem) {
                bool3 = false;
              }
            }
            ??? = ((ServiceItem)localObject6).mMergedItem;
            bool2 = bool3;
            bool3 = bool4;
          }
          else
          {
            bool2 = false;
            bool3 = bool4;
          }
        }
        if ((!bool2) || (??? == null)) {}
        while (???.mServices.size() != ((ProcessItem)localObject5).mServices.size())
        {
          localObject1 = new MergedItem(((ProcessItem)localObject5).mUserId);
          ??? = ((ProcessItem)localObject5).mServices.values().iterator();
          while (???.hasNext())
          {
            localObject6 = (ServiceItem)???.next();
            ((MergedItem)localObject1).mServices.add(localObject6);
            ((ServiceItem)localObject6).mMergedItem = ((MergedItem)localObject1);
          }
        }
        ???.update(???, false);
        if (???.mUserId != this.mMyUserId) {
          addOtherUserItem(???, (ArrayList)localObject4, this.mOtherUserMergedItems, ???);
        }
        for (;;)
        {
          i += 1;
          break;
          ((MergedItem)localObject1).mProcess = ((ProcessItem)localObject5);
          ((MergedItem)localObject1).mOtherProcesses.clear();
          for (;;)
          {
            ??? = (ActivityManager)localObject1;
            if (j >= this.mProcessItems.size() - 1) {
              break;
            }
            ((MergedItem)localObject1).mOtherProcesses.add((ProcessItem)this.mProcessItems.get(j));
            j += 1;
          }
          ((ArrayList)localObject4).add(???);
        }
      }
      j = this.mInterestingProcesses.size();
      i = 0;
      if (i < j)
      {
        ??? = (ProcessItem)this.mInterestingProcesses.get(i);
        if ((???.mClient == null) && (???.mServices.size() <= 0))
        {
          if (???.mMergedItem == null)
          {
            ???.mMergedItem = new MergedItem(???.mUserId);
            ???.mMergedItem.mProcess = ???;
          }
          ???.mMergedItem.update(???, false);
          if (???.mMergedItem.mUserId == this.mMyUserId) {
            break label2391;
          }
          addOtherUserItem(???, (ArrayList)localObject4, this.mOtherUserMergedItems, ???.mMergedItem);
        }
        for (;;)
        {
          this.mProcessItems.add(???);
          i += 1;
          break;
          label2391:
          ((ArrayList)localObject4).add(0, ???.mMergedItem);
        }
      }
      j = this.mOtherUserMergedItems.size();
      i = 0;
      while (i < j)
      {
        ??? = (MergedItem)this.mOtherUserMergedItems.valueAt(i);
        if (???.mCurSeq == this.mSequence) {
          ???.update(???, false);
        }
        i += 1;
      }
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        this.mItems = ((ArrayList)localObject3);
        this.mMergedItems = ((ArrayList)localObject4);
        this.mAllProcessItems.clear();
        this.mAllProcessItems.addAll(this.mProcessItems);
        n = 0;
        m = 0;
        k = 0;
        j = this.mRunningProcesses.size();
        i = 0;
        if (i >= j) {
          break;
        }
        ??? = (ProcessItem)this.mRunningProcesses.valueAt(i);
        if (???.mCurSeq == this.mSequence) {
          break label2663;
        }
        if (???.mRunningProcessInfo.importance >= 400)
        {
          n += 1;
          this.mAllProcessItems.add(???);
          i += 1;
        }
      }
      m += 1;
      this.mAllProcessItems.add(???);
      continue;
      Log.i("RunningState", "Unknown non-service process: " + ???.mProcessName + " #" + ???.mPid);
      continue;
      label2663:
      k += 1;
    }
    l6 = 0L;
    l2 = 0L;
    l1 = 0L;
    ??? = null;
    localObject4 = null;
    i1 = 0;
    i2 = 0;
    l3 = l6;
    bool2 = bool1;
    i = i1;
    l4 = l2;
    localObject1 = ???;
    l5 = l1;
    try
    {
      i3 = this.mAllProcessItems.size();
      l3 = l6;
      bool2 = bool1;
      i = i1;
      l4 = l2;
      localObject1 = ???;
      l5 = l1;
      localObject5 = new int[i3];
      j = 0;
      while (j < i3)
      {
        l3 = l6;
        bool2 = bool1;
        i = i1;
        l4 = l2;
        localObject1 = ???;
        l5 = l1;
        localObject5[j] = ((ProcessItem)this.mAllProcessItems.get(j)).mPid;
        j += 1;
      }
      l3 = l6;
      bool2 = bool1;
      i = i1;
      l4 = l2;
      localObject1 = ???;
      l5 = l1;
      localObject6 = ActivityManagerNative.getDefault().getProcessPss((int[])localObject5);
      j = 0;
      i1 = 0;
      ??? = null;
      i = i2;
      i2 = j;
      l3 = l6;
    }
    catch (RemoteException ???)
    {
      for (;;)
      {
        try
        {
          if (i1 >= localObject5.length) {
            continue;
          }
          l4 = l3;
          bool2 = bool1;
          j = i;
          localProcessItem = (ProcessItem)this.mAllProcessItems.get(i1);
          l4 = l3;
          bool2 = bool1;
          j = i;
          bool1 |= localProcessItem.updateSize(???, localObject6[i1], this.mSequence);
          l4 = l3;
          bool2 = bool1;
          j = i;
          if (localProcessItem.mCurSeq == this.mSequence)
          {
            l4 = l3;
            bool2 = bool1;
            j = i;
            l5 = l1 + localProcessItem.mSize;
            l1 = l5;
            continue;
          }
          l4 = l3;
          bool2 = bool1;
          j = i;
          if (localProcessItem.mRunningProcessInfo.importance < 400) {
            continue;
          }
          l4 = l3;
          bool2 = bool1;
          j = i;
          l6 = l3 + localProcessItem.mSize;
          if (??? != null)
          {
            l4 = l6;
            bool2 = bool1;
            j = i;
            localObject2 = new MergedItem(localProcessItem.mUserId);
            l4 = l6;
            bool2 = bool1;
            j = i;
            localProcessItem.mMergedItem = ((MergedItem)localObject2);
            l4 = l6;
            bool2 = bool1;
            j = i;
            localProcessItem.mMergedItem.mProcess = localProcessItem;
            l4 = l6;
            bool2 = bool1;
            j = i;
            if (((MergedItem)localObject2).mUserId != this.mMyUserId)
            {
              j = 1;
              i |= j;
              l4 = l6;
              bool2 = bool1;
              j = i;
              ???.add(localObject2);
              j = i;
              l3 = l6;
              bool2 = bool1;
              i = j;
              l4 = l2;
              localObject1 = ???;
              l5 = l1;
              ((MergedItem)localObject2).update(???, true);
              l3 = l6;
              bool2 = bool1;
              i = j;
              l4 = l2;
              localObject1 = ???;
              l5 = l1;
              ((MergedItem)localObject2).updateSize(???);
              i2 += 1;
              l3 = l6;
              i = j;
              continue;
            }
            j = 0;
            continue;
          }
          l4 = l6;
          bool2 = bool1;
          j = i;
          if (i2 < this.mBackgroundItems.size())
          {
            l4 = l6;
            bool2 = bool1;
            j = i;
            if (((MergedItem)this.mBackgroundItems.get(i2)).mProcess == localProcessItem) {
              continue;
            }
          }
          l4 = l6;
          bool2 = bool1;
          j = i;
          localObject3 = new ArrayList(n);
          int i3 = 0;
          j = i;
          if (i3 < i2)
          {
            l3 = l6;
            bool2 = bool1;
            i = j;
            l4 = l2;
            localObject1 = localObject3;
            l5 = l1;
            ??? = (MergedItem)this.mBackgroundItems.get(i3);
            l3 = l6;
            bool2 = bool1;
            i = j;
            l4 = l2;
            localObject1 = localObject3;
            l5 = l1;
            if (???.mUserId == this.mMyUserId) {
              break label4171;
            }
            i = 1;
            j |= i;
            l3 = l6;
            bool2 = bool1;
            i = j;
            l4 = l2;
            localObject1 = localObject3;
            l5 = l1;
            ((ArrayList)localObject3).add(???);
            i3 += 1;
            continue;
          }
          l3 = l6;
          bool2 = bool1;
          i = j;
          l4 = l2;
          localObject1 = localObject3;
          l5 = l1;
          localObject2 = new MergedItem(localProcessItem.mUserId);
          l3 = l6;
          bool2 = bool1;
          i = j;
          l4 = l2;
          localObject1 = localObject3;
          l5 = l1;
          localProcessItem.mMergedItem = ((MergedItem)localObject2);
          l3 = l6;
          bool2 = bool1;
          i = j;
          l4 = l2;
          localObject1 = localObject3;
          l5 = l1;
          localProcessItem.mMergedItem.mProcess = localProcessItem;
          l3 = l6;
          bool2 = bool1;
          i = j;
          l4 = l2;
          localObject1 = localObject3;
          l5 = l1;
          if (((MergedItem)localObject2).mUserId == this.mMyUserId) {
            continue;
          }
          i = 1;
          j |= i;
          l3 = l6;
          bool2 = bool1;
          i = j;
          l4 = l2;
          localObject1 = localObject3;
          l5 = l1;
          ((ArrayList)localObject3).add(localObject2);
          ??? = (ActivityManager)localObject3;
          continue;
          ??? = ???;
          l1 = l5;
          l2 = l4;
          bool1 = bool2;
        }
        catch (RemoteException localRemoteException)
        {
          ProcessItem localProcessItem;
          l3 = l4;
          bool1 = bool2;
          i = j;
          ActivityManager localActivityManager = ???;
          continue;
          i1 += 1;
        }
        i1 = i;
        ??? = (ActivityManager)localObject1;
        if (localObject1 == null)
        {
          i1 = i;
          ??? = (ActivityManager)localObject1;
          if (this.mBackgroundItems.size() > n)
          {
            localObject1 = new ArrayList(n);
            j = 0;
            i1 = i;
            ??? = (ActivityManager)localObject1;
            if (j < n)
            {
              ??? = (MergedItem)this.mBackgroundItems.get(j);
              if (???.mUserId != this.mMyUserId)
              {
                i1 = 1;
                i |= i1;
                ((ArrayList)localObject1).add(???);
                j += 1;
                continue;
                i = 0;
                continue;
                l4 = l6;
                bool2 = bool1;
                j = i;
                localObject2 = (MergedItem)this.mBackgroundItems.get(i2);
                j = i;
                continue;
                l4 = l3;
                bool2 = bool1;
                j = i;
                if (localProcessItem.mRunningProcessInfo.importance > 200) {
                  continue;
                }
                l4 = l3;
                bool2 = bool1;
                j = i;
                l5 = localProcessItem.mSize;
                l2 += l5;
                continue;
                localObject1 = ???;
                continue;
              }
              i1 = 0;
              continue;
            }
          }
        }
        localObject1 = localObject4;
        if (??? != null)
        {
          if (i1 == 0) {
            localObject1 = ???;
          }
        }
        else
        {
          i = 0;
          if (i >= this.mMergedItems.size()) {
            continue;
          }
          ((MergedItem)this.mMergedItems.get(i)).updateSize(???);
          i += 1;
          continue;
        }
        localObject2 = new ArrayList();
        j = ???.size();
        i = 0;
        if (i < j)
        {
          localObject1 = (MergedItem)???.get(i);
          if (((MergedItem)localObject1).mUserId != this.mMyUserId)
          {
            addOtherUserItem(???, (ArrayList)localObject2, this.mOtherUserBackgroundItems, (MergedItem)localObject1);
            i += 1;
          }
          else
          {
            ((ArrayList)localObject2).add(localObject1);
          }
        }
        else
        {
          j = this.mOtherUserBackgroundItems.size();
          i = 0;
          localObject1 = localObject2;
          if (i < j)
          {
            localObject1 = (MergedItem)this.mOtherUserBackgroundItems.valueAt(i);
            if (((MergedItem)localObject1).mCurSeq == this.mSequence)
            {
              ((MergedItem)localObject1).update(???, true);
              ((MergedItem)localObject1).updateSize(???);
            }
            i += 1;
            continue;
            synchronized (this.mLock)
            {
              this.mNumBackgroundProcesses = n;
              this.mNumForegroundProcesses = m;
              this.mNumServiceProcesses = k;
              this.mBackgroundProcessMemory = l3;
              this.mForegroundProcessMemory = l2;
              this.mServiceProcessMemory = l1;
              bool2 = bool1;
              if (??? != null)
              {
                this.mBackgroundItems = ???;
                this.mUserBackgroundItems = ((ArrayList)localObject1);
                bool2 = bool1;
                if (this.mWatchingBackgroundItems) {
                  bool2 = true;
                }
              }
              if (!this.mHaveData)
              {
                this.mHaveData = true;
                this.mLock.notifyAll();
              }
              return bool2;
            }
            continue;
            i = 0;
          }
        }
      }
    }
    l4 = l3;
    bool2 = bool1;
    j = i;
  }
  
  ArrayList<MergedItem> getCurrentBackgroundItems()
  {
    synchronized (this.mLock)
    {
      ArrayList localArrayList = this.mUserBackgroundItems;
      return localArrayList;
    }
  }
  
  ArrayList<MergedItem> getCurrentMergedItems()
  {
    synchronized (this.mLock)
    {
      ArrayList localArrayList = this.mMergedItems;
      return localArrayList;
    }
  }
  
  boolean hasData()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mHaveData;
      return bool;
    }
  }
  
  void pause()
  {
    synchronized (this.mLock)
    {
      this.mResumed = false;
      this.mRefreshUiListener = null;
      this.mHandler.removeMessages(4);
      return;
    }
  }
  
  void resume(OnRefreshUiListener paramOnRefreshUiListener)
  {
    synchronized (this.mLock)
    {
      this.mResumed = true;
      this.mRefreshUiListener = paramOnRefreshUiListener;
      boolean bool1 = this.mUmBroadcastReceiver.checkUsersChangedLocked();
      boolean bool2 = this.mInterestingConfigChanges.applyNewConfig(this.mApplicationContext.getResources());
      if ((bool1) || (bool2))
      {
        this.mHaveData = false;
        this.mBackgroundHandler.removeMessages(1);
        this.mBackgroundHandler.removeMessages(2);
        this.mBackgroundHandler.sendEmptyMessage(1);
      }
      if (!this.mBackgroundHandler.hasMessages(2)) {
        this.mBackgroundHandler.sendEmptyMessage(2);
      }
      this.mHandler.sendEmptyMessage(4);
      return;
    }
  }
  
  void setWatchingBackgroundItems(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      this.mWatchingBackgroundItems = paramBoolean;
      return;
    }
  }
  
  void updateNow()
  {
    synchronized (this.mLock)
    {
      this.mBackgroundHandler.removeMessages(2);
      this.mBackgroundHandler.sendEmptyMessage(2);
      return;
    }
  }
  
  void waitForData()
  {
    synchronized (this.mLock)
    {
      for (;;)
      {
        boolean bool = this.mHaveData;
        if (bool) {
          break;
        }
        try
        {
          this.mLock.wait(0L);
        }
        catch (InterruptedException localInterruptedException) {}
      }
      return;
    }
  }
  
  static class AppProcessInfo
  {
    boolean hasForegroundServices;
    boolean hasServices;
    final ActivityManager.RunningAppProcessInfo info;
    
    AppProcessInfo(ActivityManager.RunningAppProcessInfo paramRunningAppProcessInfo)
    {
      this.info = paramRunningAppProcessInfo;
    }
  }
  
  final class BackgroundHandler
    extends Handler
  {
    public BackgroundHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      default: 
        return;
      case 1: 
        RunningState.-wrap1(RunningState.this);
        return;
      }
      for (;;)
      {
        synchronized (RunningState.this.mLock)
        {
          boolean bool = RunningState.this.mResumed;
          if (!bool) {
            return;
          }
          ??? = RunningState.this.mHandler.obtainMessage(3);
          if (RunningState.-wrap0(RunningState.this, RunningState.this.mApplicationContext, RunningState.this.mAm))
          {
            i = 1;
            ???.arg1 = i;
            RunningState.this.mHandler.sendMessage(???);
            removeMessages(2);
            sendMessageDelayed(obtainMessage(2), 2000L);
            return;
          }
        }
        int i = 0;
      }
    }
  }
  
  static class BaseItem
  {
    long mActiveSince;
    boolean mBackground;
    int mCurSeq;
    String mCurSizeStr;
    String mDescription;
    CharSequence mDisplayLabel;
    final boolean mIsProcess;
    String mLabel;
    boolean mNeedDivider;
    PackageItemInfo mPackageInfo;
    long mSize;
    String mSizeStr;
    final int mUserId;
    
    public BaseItem(boolean paramBoolean, int paramInt)
    {
      this.mIsProcess = paramBoolean;
      this.mUserId = paramInt;
    }
    
    public Drawable loadIcon(Context paramContext, RunningState paramRunningState)
    {
      if (this.mPackageInfo != null)
      {
        paramContext = this.mPackageInfo.loadUnbadgedIcon(paramRunningState.mPm);
        return paramRunningState.mPm.getUserBadgedIcon(paramContext, new UserHandle(this.mUserId));
      }
      return null;
    }
  }
  
  static class MergedItem
    extends RunningState.BaseItem
  {
    final ArrayList<MergedItem> mChildren = new ArrayList();
    private int mLastNumProcesses = -1;
    private int mLastNumServices = -1;
    final ArrayList<RunningState.ProcessItem> mOtherProcesses = new ArrayList();
    RunningState.ProcessItem mProcess;
    final ArrayList<RunningState.ServiceItem> mServices = new ArrayList();
    RunningState.UserState mUser;
    
    MergedItem(int paramInt)
    {
      super(paramInt);
    }
    
    private void setDescription(Context paramContext, int paramInt1, int paramInt2)
    {
      int i;
      if ((this.mLastNumProcesses != paramInt1) || (this.mLastNumServices != paramInt2))
      {
        this.mLastNumProcesses = paramInt1;
        this.mLastNumServices = paramInt2;
        i = 2131692196;
        if (paramInt1 == 1) {
          break label83;
        }
        if (paramInt2 == 1) {
          break label76;
        }
        i = 2131692199;
      }
      for (;;)
      {
        this.mDescription = paramContext.getResources().getString(i, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
        return;
        label76:
        i = 2131692198;
        continue;
        label83:
        if (paramInt2 != 1) {
          i = 2131692197;
        }
      }
    }
    
    public Drawable loadIcon(Context paramContext, RunningState paramRunningState)
    {
      if (this.mUser == null) {
        return super.loadIcon(paramContext, paramRunningState);
      }
      if (this.mUser.mIcon != null)
      {
        paramContext = this.mUser.mIcon.getConstantState();
        if (paramContext == null) {
          return this.mUser.mIcon;
        }
        return paramContext.newDrawable();
      }
      return paramContext.getDrawable(17302477);
    }
    
    boolean update(Context paramContext, boolean paramBoolean)
    {
      this.mBackground = paramBoolean;
      if (this.mUser != null)
      {
        this.mPackageInfo = ((MergedItem)this.mChildren.get(0)).mProcess.mPackageInfo;
        if (this.mUser != null) {}
        int k;
        for (Object localObject = this.mUser.mLabel;; localObject = null)
        {
          this.mLabel = ((String)localObject);
          this.mDisplayLabel = this.mLabel;
          k = 0;
          i = 0;
          this.mActiveSince = -1L;
          int j = 0;
          while (j < this.mChildren.size())
          {
            localObject = (MergedItem)this.mChildren.get(j);
            k += ((MergedItem)localObject).mLastNumProcesses;
            i += ((MergedItem)localObject).mLastNumServices;
            if ((((MergedItem)localObject).mActiveSince >= 0L) && (this.mActiveSince < ((MergedItem)localObject).mActiveSince)) {
              this.mActiveSince = ((MergedItem)localObject).mActiveSince;
            }
            j += 1;
          }
        }
        if (!this.mBackground) {
          setDescription(paramContext, k, i);
        }
        return false;
      }
      this.mPackageInfo = this.mProcess.mPackageInfo;
      this.mDisplayLabel = this.mProcess.mDisplayLabel;
      this.mLabel = this.mProcess.mLabel;
      if (!this.mBackground) {
        if (this.mProcess.mPid <= 0) {
          break label327;
        }
      }
      label327:
      for (int i = 1;; i = 0)
      {
        setDescription(paramContext, i + this.mOtherProcesses.size(), this.mServices.size());
        this.mActiveSince = -1L;
        i = 0;
        while (i < this.mServices.size())
        {
          paramContext = (RunningState.ServiceItem)this.mServices.get(i);
          if ((paramContext.mActiveSince >= 0L) && (this.mActiveSince < paramContext.mActiveSince)) {
            this.mActiveSince = paramContext.mActiveSince;
          }
          i += 1;
        }
        break;
      }
    }
    
    boolean updateSize(Context paramContext)
    {
      if (this.mUser != null)
      {
        this.mSize = 0L;
        i = 0;
        while (i < this.mChildren.size())
        {
          MergedItem localMergedItem = (MergedItem)this.mChildren.get(i);
          localMergedItem.updateSize(paramContext);
          this.mSize += localMergedItem.mSize;
          i += 1;
        }
      }
      this.mSize = this.mProcess.mSize;
      int i = 0;
      while (i < this.mOtherProcesses.size())
      {
        this.mSize += ((RunningState.ProcessItem)this.mOtherProcesses.get(i)).mSize;
        i += 1;
      }
      paramContext = Formatter.formatShortFileSize(paramContext, this.mSize);
      if (!paramContext.equals(this.mSizeStr))
      {
        this.mSizeStr = paramContext;
        return false;
      }
      return false;
    }
  }
  
  static abstract interface OnRefreshUiListener
  {
    public static final int REFRESH_DATA = 1;
    public static final int REFRESH_STRUCTURE = 2;
    public static final int REFRESH_TIME = 0;
    
    public abstract void onRefreshUi(int paramInt);
  }
  
  static class ProcessItem
    extends RunningState.BaseItem
  {
    long mActiveSince;
    ProcessItem mClient;
    final SparseArray<ProcessItem> mDependentProcesses = new SparseArray();
    boolean mInteresting;
    boolean mIsStarted;
    boolean mIsSystem;
    int mLastNumDependentProcesses;
    RunningState.MergedItem mMergedItem;
    int mPid;
    final String mProcessName;
    ActivityManager.RunningAppProcessInfo mRunningProcessInfo;
    int mRunningSeq;
    final HashMap<ComponentName, RunningState.ServiceItem> mServices = new HashMap();
    final int mUid;
    
    public ProcessItem(Context paramContext, int paramInt, String paramString)
    {
      super(UserHandle.getUserId(paramInt));
      this.mDescription = paramContext.getResources().getString(2131692194, new Object[] { paramString });
      this.mUid = paramInt;
      this.mProcessName = paramString;
    }
    
    void addDependentProcesses(ArrayList<RunningState.BaseItem> paramArrayList, ArrayList<ProcessItem> paramArrayList1)
    {
      int j = this.mDependentProcesses.size();
      int i = 0;
      while (i < j)
      {
        ProcessItem localProcessItem = (ProcessItem)this.mDependentProcesses.valueAt(i);
        localProcessItem.addDependentProcesses(paramArrayList, paramArrayList1);
        paramArrayList.add(localProcessItem);
        if (localProcessItem.mPid > 0) {
          paramArrayList1.add(localProcessItem);
        }
        i += 1;
      }
    }
    
    boolean buildDependencyChain(Context paramContext, PackageManager paramPackageManager, int paramInt)
    {
      int j = this.mDependentProcesses.size();
      boolean bool = false;
      int i = 0;
      while (i < j)
      {
        ProcessItem localProcessItem = (ProcessItem)this.mDependentProcesses.valueAt(i);
        if (localProcessItem.mClient != this)
        {
          bool = true;
          localProcessItem.mClient = this;
        }
        localProcessItem.mCurSeq = paramInt;
        localProcessItem.ensureLabel(paramPackageManager);
        bool |= localProcessItem.buildDependencyChain(paramContext, paramPackageManager, paramInt);
        i += 1;
      }
      if (this.mLastNumDependentProcesses != this.mDependentProcesses.size())
      {
        bool = true;
        this.mLastNumDependentProcesses = this.mDependentProcesses.size();
      }
      return bool;
    }
    
    void ensureLabel(PackageManager paramPackageManager)
    {
      int i = 0;
      if (this.mLabel != null) {
        return;
      }
      try
      {
        ApplicationInfo localApplicationInfo1 = paramPackageManager.getApplicationInfo(this.mProcessName, 8192);
        if (localApplicationInfo1.uid == this.mUid)
        {
          this.mDisplayLabel = localApplicationInfo1.loadLabel(paramPackageManager);
          this.mLabel = this.mDisplayLabel.toString();
          this.mPackageInfo = localApplicationInfo1;
          return;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException1)
      {
        Object localObject1 = paramPackageManager.getPackagesForUid(this.mUid);
        if (localObject1.length == 1) {
          try
          {
            ApplicationInfo localApplicationInfo2 = paramPackageManager.getApplicationInfo(localObject1[0], 8192);
            this.mDisplayLabel = localApplicationInfo2.loadLabel(paramPackageManager);
            this.mLabel = this.mDisplayLabel.toString();
            this.mPackageInfo = localApplicationInfo2;
            return;
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException2) {}
        }
        int j = localObject1.length;
        while (i < j)
        {
          Object localObject2 = localObject1[i];
          try
          {
            PackageInfo localPackageInfo = paramPackageManager.getPackageInfo((String)localObject2, 0);
            if (localPackageInfo.sharedUserLabel != 0)
            {
              localObject2 = paramPackageManager.getText((String)localObject2, localPackageInfo.sharedUserLabel, localPackageInfo.applicationInfo);
              if (localObject2 != null)
              {
                this.mDisplayLabel = ((CharSequence)localObject2);
                this.mLabel = ((CharSequence)localObject2).toString();
                this.mPackageInfo = localPackageInfo.applicationInfo;
                return;
              }
            }
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException3)
          {
            i += 1;
          }
        }
        if (this.mServices.size() > 0)
        {
          this.mPackageInfo = ((RunningState.ServiceItem)this.mServices.values().iterator().next()).mServiceInfo.applicationInfo;
          this.mDisplayLabel = this.mPackageInfo.loadLabel(paramPackageManager);
          this.mLabel = this.mDisplayLabel.toString();
          return;
        }
        try
        {
          localObject1 = paramPackageManager.getApplicationInfo(localObject1[0], 8192);
          this.mDisplayLabel = ((ApplicationInfo)localObject1).loadLabel(paramPackageManager);
          this.mLabel = this.mDisplayLabel.toString();
          this.mPackageInfo = ((PackageItemInfo)localObject1);
          return;
        }
        catch (PackageManager.NameNotFoundException paramPackageManager) {}
      }
    }
    
    boolean updateService(Context paramContext, ActivityManager.RunningServiceInfo paramRunningServiceInfo)
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      boolean bool = false;
      RunningState.ServiceItem localServiceItem2 = (RunningState.ServiceItem)this.mServices.get(paramRunningServiceInfo.service);
      RunningState.ServiceItem localServiceItem1 = localServiceItem2;
      Object localObject;
      if (localServiceItem2 == null)
      {
        bool = true;
        localServiceItem2 = new RunningState.ServiceItem(this.mUserId);
        localServiceItem2.mRunningService = paramRunningServiceInfo;
        try
        {
          localServiceItem2.mServiceInfo = ActivityThread.getPackageManager().getServiceInfo(paramRunningServiceInfo.service, 8192, UserHandle.getUserId(paramRunningServiceInfo.uid));
          if (localServiceItem2.mServiceInfo == null)
          {
            Log.d("RunningService", "getServiceInfo returned null for: " + paramRunningServiceInfo.service);
            return false;
          }
        }
        catch (RemoteException localRemoteException)
        {
          localServiceItem2.mDisplayLabel = RunningState.makeLabel(localPackageManager, localServiceItem2.mRunningService.service.getClassName(), localServiceItem2.mServiceInfo);
          if (this.mDisplayLabel == null) {
            break label324;
          }
        }
        localObject = this.mDisplayLabel.toString();
        this.mLabel = ((String)localObject);
        localServiceItem2.mPackageInfo = localServiceItem2.mServiceInfo.applicationInfo;
        this.mServices.put(paramRunningServiceInfo.service, localServiceItem2);
        localObject = localServiceItem2;
      }
      ((RunningState.ServiceItem)localObject).mCurSeq = this.mCurSeq;
      ((RunningState.ServiceItem)localObject).mRunningService = paramRunningServiceInfo;
      if (paramRunningServiceInfo.restarting == 0L) {}
      for (long l = paramRunningServiceInfo.activeSince;; l = -1L)
      {
        if (((RunningState.ServiceItem)localObject).mActiveSince != l)
        {
          ((RunningState.ServiceItem)localObject).mActiveSince = l;
          bool = true;
        }
        if ((paramRunningServiceInfo.clientPackage == null) || (paramRunningServiceInfo.clientLabel == 0)) {
          break label347;
        }
        if (((RunningState.ServiceItem)localObject).mShownAsStarted)
        {
          ((RunningState.ServiceItem)localObject).mShownAsStarted = false;
          bool = true;
        }
        try
        {
          paramRunningServiceInfo = localPackageManager.getResourcesForApplication(paramRunningServiceInfo.clientPackage).getString(paramRunningServiceInfo.clientLabel);
          ((RunningState.ServiceItem)localObject).mDescription = paramContext.getResources().getString(2131692190, new Object[] { paramRunningServiceInfo });
          return bool;
        }
        catch (PackageManager.NameNotFoundException paramContext)
        {
          label324:
          ((RunningState.ServiceItem)localObject).mDescription = null;
          return bool;
        }
        localObject = null;
        break;
      }
      label347:
      if (!((RunningState.ServiceItem)localObject).mShownAsStarted)
      {
        ((RunningState.ServiceItem)localObject).mShownAsStarted = true;
        bool = true;
      }
      ((RunningState.ServiceItem)localObject).mDescription = paramContext.getResources().getString(2131692189);
      return bool;
    }
    
    boolean updateSize(Context paramContext, long paramLong, int paramInt)
    {
      this.mSize = (1024L * paramLong);
      if (this.mCurSeq == paramInt)
      {
        paramContext = Formatter.formatShortFileSize(paramContext, this.mSize);
        if (!paramContext.equals(this.mSizeStr))
        {
          this.mSizeStr = paramContext;
          return false;
        }
      }
      return false;
    }
  }
  
  static class ServiceItem
    extends RunningState.BaseItem
  {
    RunningState.MergedItem mMergedItem;
    ActivityManager.RunningServiceInfo mRunningService;
    ServiceInfo mServiceInfo;
    boolean mShownAsStarted;
    
    public ServiceItem(int paramInt)
    {
      super(paramInt);
    }
  }
  
  class ServiceProcessComparator
    implements Comparator<RunningState.ProcessItem>
  {
    ServiceProcessComparator() {}
    
    public int compare(RunningState.ProcessItem paramProcessItem1, RunningState.ProcessItem paramProcessItem2)
    {
      if (paramProcessItem1.mUserId != paramProcessItem2.mUserId)
      {
        if (paramProcessItem1.mUserId == RunningState.this.mMyUserId) {
          return -1;
        }
        if (paramProcessItem2.mUserId == RunningState.this.mMyUserId) {
          return 1;
        }
        if (paramProcessItem1.mUserId < paramProcessItem2.mUserId) {
          return -1;
        }
        return 1;
      }
      if (paramProcessItem1.mIsStarted != paramProcessItem2.mIsStarted)
      {
        if (paramProcessItem1.mIsStarted) {
          return -1;
        }
        return 1;
      }
      if (paramProcessItem1.mIsSystem != paramProcessItem2.mIsSystem)
      {
        if (paramProcessItem1.mIsSystem) {
          return 1;
        }
        return -1;
      }
      if (paramProcessItem1.mActiveSince != paramProcessItem2.mActiveSince)
      {
        if (paramProcessItem1.mActiveSince > paramProcessItem2.mActiveSince) {
          return -1;
        }
        return 1;
      }
      return 0;
    }
  }
  
  private final class UserManagerBroadcastReceiver
    extends BroadcastReceiver
  {
    private volatile boolean usersChanged;
    
    private UserManagerBroadcastReceiver() {}
    
    public boolean checkUsersChangedLocked()
    {
      boolean bool = this.usersChanged;
      this.usersChanged = false;
      return bool;
    }
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      synchronized (RunningState.this.mLock)
      {
        if (RunningState.this.mResumed)
        {
          RunningState.this.mHaveData = false;
          RunningState.this.mBackgroundHandler.removeMessages(1);
          RunningState.this.mBackgroundHandler.sendEmptyMessage(1);
          RunningState.this.mBackgroundHandler.removeMessages(2);
          RunningState.this.mBackgroundHandler.sendEmptyMessage(2);
          return;
        }
        this.usersChanged = true;
      }
    }
    
    void register(Context paramContext)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.USER_STOPPED");
      localIntentFilter.addAction("android.intent.action.USER_STARTED");
      localIntentFilter.addAction("android.intent.action.USER_INFO_CHANGED");
      paramContext.registerReceiverAsUser(this, UserHandle.ALL, localIntentFilter, null, null);
    }
  }
  
  static class UserState
  {
    Drawable mIcon;
    UserInfo mInfo;
    String mLabel;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\RunningState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */