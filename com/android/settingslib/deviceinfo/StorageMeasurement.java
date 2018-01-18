package com.android.settingslib.deviceinfo;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.IPackageStatsObserver.Stub;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.UserInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseLongArray;
import com.android.internal.app.IMediaContainerService;
import com.android.internal.app.IMediaContainerService.Stub;
import com.android.internal.util.ArrayUtils;
import com.google.android.collect.Sets;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class StorageMeasurement
{
  public static final ComponentName DEFAULT_CONTAINER_COMPONENT = new ComponentName("com.android.defcontainer", "com.android.defcontainer.DefaultContainerService");
  private static final String DEFAULT_CONTAINER_PACKAGE = "com.android.defcontainer";
  private static final String DIRECTORY_VIDEO = "Video";
  private static final boolean LOCAL_LOGV = false;
  static final boolean LOGV = false;
  private static final String TAG = "StorageMeasurement";
  private static final Set<String> sMeasureMediaTypes = Sets.newHashSet(new String[] { Environment.DIRECTORY_DCIM, Environment.DIRECTORY_MOVIES, "Video", Environment.DIRECTORY_PICTURES, Environment.DIRECTORY_MUSIC, Environment.DIRECTORY_ALARMS, Environment.DIRECTORY_NOTIFICATIONS, Environment.DIRECTORY_RINGTONES, Environment.DIRECTORY_PODCASTS, Environment.DIRECTORY_DOWNLOADS, "Android" });
  private final Context mContext;
  private final MainHandler mMainHandler;
  private final MeasurementHandler mMeasurementHandler;
  private WeakReference<MeasurementReceiver> mReceiver;
  private final VolumeInfo mSharedVolume;
  private final VolumeInfo mVolume;
  
  public StorageMeasurement(Context paramContext, VolumeInfo paramVolumeInfo1, VolumeInfo paramVolumeInfo2)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mVolume = paramVolumeInfo1;
    this.mSharedVolume = paramVolumeInfo2;
    paramContext = new HandlerThread("MemoryMeasurement");
    paramContext.start();
    this.mMainHandler = new MainHandler(null);
    this.mMeasurementHandler = new MeasurementHandler(paramContext.getLooper());
  }
  
  private static void addValue(SparseLongArray paramSparseLongArray, int paramInt, long paramLong)
  {
    paramSparseLongArray.put(paramInt, paramSparseLongArray.get(paramInt) + paramLong);
  }
  
  private static void addValueIfKeyExists(SparseLongArray paramSparseLongArray, int paramInt, long paramLong)
  {
    int i = paramSparseLongArray.indexOfKey(paramInt);
    if (i >= 0) {
      paramSparseLongArray.put(paramInt, paramSparseLongArray.valueAt(i) + paramLong);
    }
  }
  
  private static long getDirectorySize(IMediaContainerService paramIMediaContainerService, File paramFile)
  {
    try
    {
      long l = paramIMediaContainerService.calculateDirectorySize(paramFile.toString());
      if (LOGV) {
        Log.v("StorageMeasurement", "getDirectorySize(" + paramFile + ") returned " + l);
      }
      return l;
    }
    catch (Exception paramIMediaContainerService)
    {
      Log.w("StorageMeasurement", "Could not read memory from default container service for " + paramFile, paramIMediaContainerService);
    }
    return 0L;
  }
  
  private void invalidate()
  {
    this.mMeasurementHandler.sendEmptyMessage(5);
  }
  
  private void measureExactStorage(IMediaContainerService paramIMediaContainerService)
  {
    Object localObject2 = (UserManager)this.mContext.getSystemService(UserManager.class);
    PackageManager localPackageManager = this.mContext.getPackageManager();
    Object localObject1 = ((UserManager)localObject2).getUsers();
    localObject2 = ((UserManager)localObject2).getEnabledProfiles(ActivityManager.getCurrentUser());
    Object localObject3 = new MeasurementDetails();
    Object localObject4 = this.mMeasurementHandler.obtainMessage(4, localObject3);
    Object localObject5;
    int i;
    Object localObject6;
    if ((this.mVolume != null) && (this.mVolume.isMountedReadable()))
    {
      if ((this.mSharedVolume != null) && (this.mSharedVolume.isMountedReadable())) {
        localObject5 = ((Iterable)localObject2).iterator();
      }
    }
    else
    {
      while (((Iterator)localObject5).hasNext())
      {
        i = ((UserInfo)((Iterator)localObject5).next()).id;
        localObject6 = this.mSharedVolume.getPathForUser(i);
        HashMap localHashMap = new HashMap(sMeasureMediaTypes.size());
        ((MeasurementDetails)localObject3).mediaSize.put(i, localHashMap);
        Iterator localIterator = sMeasureMediaTypes.iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          localHashMap.put(str, Long.valueOf(getDirectorySize(paramIMediaContainerService, new File((File)localObject6, str))));
          continue;
          ((Message)localObject4).sendToTarget();
          return;
        }
        addValue(((MeasurementDetails)localObject3).miscSize, i, measureMisc(paramIMediaContainerService, (File)localObject6));
      }
      if (this.mSharedVolume.getType() == 2)
      {
        localObject5 = ((Iterable)localObject1).iterator();
        while (((Iterator)localObject5).hasNext())
        {
          localObject6 = (UserInfo)((Iterator)localObject5).next();
          long l = getDirectorySize(paramIMediaContainerService, this.mSharedVolume.getPathForUser(((UserInfo)localObject6).id));
          addValue(((MeasurementDetails)localObject3).usersSize, ((UserInfo)localObject6).id, l);
        }
      }
    }
    paramIMediaContainerService = this.mVolume.getPath();
    if (paramIMediaContainerService != null)
    {
      ((MeasurementDetails)localObject3).totalSize = paramIMediaContainerService.getTotalSpace();
      ((MeasurementDetails)localObject3).availSize = paramIMediaContainerService.getFreeSpace();
    }
    if (this.mVolume.getType() == 1)
    {
      localObject5 = localPackageManager.getInstalledApplications(8704);
      paramIMediaContainerService = new ArrayList();
      localObject5 = ((Iterable)localObject5).iterator();
      while (((Iterator)localObject5).hasNext())
      {
        localObject6 = (ApplicationInfo)((Iterator)localObject5).next();
        if (Objects.equals(((ApplicationInfo)localObject6).volumeUuid, this.mVolume.getFsUuid())) {
          paramIMediaContainerService.add(localObject6);
        }
      }
      i = ((List)localObject1).size() * paramIMediaContainerService.size();
      if (i == 0)
      {
        ((Message)localObject4).sendToTarget();
        return;
      }
      localObject2 = new StatsObserver(true, (MeasurementDetails)localObject3, ActivityManager.getCurrentUser(), (List)localObject2, (Message)localObject4, i);
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject3 = (UserInfo)((Iterator)localObject1).next();
        localObject4 = paramIMediaContainerService.iterator();
        while (((Iterator)localObject4).hasNext()) {
          localPackageManager.getPackageSizeInfoAsUser(((ApplicationInfo)((Iterator)localObject4).next()).packageName, ((UserInfo)localObject3).id, (IPackageStatsObserver)localObject2);
        }
      }
    }
    ((Message)localObject4).sendToTarget();
    return;
  }
  
  private long measureMisc(IMediaContainerService paramIMediaContainerService, File paramFile)
  {
    paramFile = paramFile.listFiles();
    if (ArrayUtils.isEmpty(paramFile)) {
      return 0L;
    }
    long l2 = 0L;
    int i = 0;
    int j = paramFile.length;
    if (i < j)
    {
      File localFile = paramFile[i];
      String str = localFile.getName();
      long l1;
      if (sMeasureMediaTypes.contains(str)) {
        l1 = l2;
      }
      for (;;)
      {
        i += 1;
        l2 = l1;
        break;
        if (localFile.isFile())
        {
          l1 = l2 + localFile.length();
        }
        else
        {
          l1 = l2;
          if (localFile.isDirectory()) {
            l1 = l2 + getDirectorySize(paramIMediaContainerService, localFile);
          }
        }
      }
    }
    return l2;
  }
  
  public void forceMeasure()
  {
    invalidate();
    measure();
  }
  
  public void measure()
  {
    if (!this.mMeasurementHandler.hasMessages(1)) {
      this.mMeasurementHandler.sendEmptyMessage(1);
    }
  }
  
  public void onDestroy()
  {
    this.mReceiver = null;
    this.mMeasurementHandler.removeMessages(1);
    this.mMeasurementHandler.sendEmptyMessage(3);
  }
  
  public void setReceiver(MeasurementReceiver paramMeasurementReceiver)
  {
    if ((this.mReceiver == null) || (this.mReceiver.get() == null)) {
      this.mReceiver = new WeakReference(paramMeasurementReceiver);
    }
  }
  
  private class MainHandler
    extends Handler
  {
    private MainHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      Object localObject = null;
      StorageMeasurement.MeasurementDetails localMeasurementDetails = (StorageMeasurement.MeasurementDetails)paramMessage.obj;
      paramMessage = (Message)localObject;
      if (StorageMeasurement.-get2(StorageMeasurement.this) != null) {
        paramMessage = (StorageMeasurement.MeasurementReceiver)StorageMeasurement.-get2(StorageMeasurement.this).get();
      }
      if (paramMessage != null) {
        paramMessage.onDetailsChanged(localMeasurementDetails);
      }
    }
  }
  
  public static class MeasurementDetails
  {
    public SparseLongArray appsSize = new SparseLongArray();
    public long availSize;
    public long cacheSize;
    public SparseArray<HashMap<String, Long>> mediaSize = new SparseArray();
    public SparseLongArray miscSize = new SparseLongArray();
    public long totalSize;
    public SparseLongArray usersSize = new SparseLongArray();
    
    public String toString()
    {
      return "MeasurementDetails: [totalSize: " + this.totalSize + " availSize: " + this.availSize + " cacheSize: " + this.cacheSize + " mediaSize: " + this.mediaSize + " miscSize: " + this.miscSize + "usersSize: " + this.usersSize + "]";
    }
  }
  
  private class MeasurementHandler
    extends Handler
  {
    public static final int MSG_COMPLETED = 4;
    public static final int MSG_CONNECTED = 2;
    public static final int MSG_DISCONNECT = 3;
    public static final int MSG_INVALIDATE = 5;
    public static final int MSG_MEASURE = 1;
    private volatile boolean mBound = false;
    private StorageMeasurement.MeasurementDetails mCached;
    private final ServiceConnection mDefContainerConn = new ServiceConnection()
    {
      public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
      {
        paramAnonymousComponentName = IMediaContainerService.Stub.asInterface(paramAnonymousIBinder);
        StorageMeasurement.MeasurementHandler.-set1(StorageMeasurement.MeasurementHandler.this, paramAnonymousComponentName);
        StorageMeasurement.MeasurementHandler.-set0(StorageMeasurement.MeasurementHandler.this, true);
        StorageMeasurement.MeasurementHandler.this.sendMessage(StorageMeasurement.MeasurementHandler.this.obtainMessage(2, paramAnonymousComponentName));
      }
      
      public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
      {
        StorageMeasurement.MeasurementHandler.-set0(StorageMeasurement.MeasurementHandler.this, false);
        StorageMeasurement.MeasurementHandler.this.removeMessages(2);
      }
    };
    private IMediaContainerService mDefaultContainer;
    private Object mLock = new Object();
    
    public MeasurementHandler(Looper paramLooper)
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
        if (this.mCached != null)
        {
          StorageMeasurement.-get1(StorageMeasurement.this).obtainMessage(0, this.mCached).sendToTarget();
          return;
        }
      case 2: 
      case 3: 
      case 4: 
        for (;;)
        {
          synchronized (this.mLock)
          {
            if (this.mBound)
            {
              removeMessages(3);
              sendMessage(obtainMessage(2, this.mDefaultContainer));
              return;
            }
            Intent localIntent = new Intent().setComponent(StorageMeasurement.DEFAULT_CONTAINER_COMPONENT);
            StorageMeasurement.-get0(StorageMeasurement.this).bindServiceAsUser(localIntent, this.mDefContainerConn, 1, UserHandle.SYSTEM);
          }
          ??? = (IMediaContainerService)???.obj;
          StorageMeasurement.-wrap2(StorageMeasurement.this, ???);
          return;
          Object localObject2 = this.mLock;
          ??? = (Message)localObject2;
          try
          {
            if (this.mBound)
            {
              this.mBound = false;
              StorageMeasurement.-get0(StorageMeasurement.this).unbindService(this.mDefContainerConn);
              ??? = (Message)localObject2;
            }
          }
          finally {}
        }
        StorageMeasurement.-get1(StorageMeasurement.this).obtainMessage(0, this.mCached).sendToTarget();
        return;
      }
      this.mCached = null;
    }
  }
  
  public static abstract interface MeasurementReceiver
  {
    public abstract void onDetailsChanged(StorageMeasurement.MeasurementDetails paramMeasurementDetails);
  }
  
  private static class StatsObserver
    extends IPackageStatsObserver.Stub
  {
    private final int mCurrentUser;
    private final StorageMeasurement.MeasurementDetails mDetails;
    private final Message mFinished;
    private final boolean mIsPrivate;
    private int mRemaining;
    
    public StatsObserver(boolean paramBoolean, StorageMeasurement.MeasurementDetails paramMeasurementDetails, int paramInt1, List<UserInfo> paramList, Message paramMessage, int paramInt2)
    {
      this.mIsPrivate = paramBoolean;
      this.mDetails = paramMeasurementDetails;
      this.mCurrentUser = paramInt1;
      if (paramBoolean)
      {
        paramMeasurementDetails = paramList.iterator();
        while (paramMeasurementDetails.hasNext())
        {
          paramList = (UserInfo)paramMeasurementDetails.next();
          this.mDetails.appsSize.put(paramList.id, 0L);
        }
      }
      this.mFinished = paramMessage;
      this.mRemaining = paramInt2;
    }
    
    private void addStatsLocked(PackageStats paramPackageStats)
    {
      if (this.mIsPrivate)
      {
        long l6 = paramPackageStats.codeSize;
        long l5 = paramPackageStats.dataSize;
        long l4 = paramPackageStats.cacheSize;
        long l3 = l4;
        long l2 = l6;
        long l1 = l5;
        if (Environment.isExternalStorageEmulated())
        {
          l2 = l6 + (paramPackageStats.externalCodeSize + paramPackageStats.externalObbSize);
          l1 = l5 + (paramPackageStats.externalDataSize + paramPackageStats.externalMediaSize);
          l3 = l4 + paramPackageStats.externalCacheSize;
        }
        StorageMeasurement.-wrap0(this.mDetails.appsSize, paramPackageStats.userHandle, l2 + l1);
        StorageMeasurement.-wrap1(this.mDetails.usersSize, paramPackageStats.userHandle, l1);
        paramPackageStats = this.mDetails;
        paramPackageStats.cacheSize += l3;
        return;
      }
      StorageMeasurement.-wrap1(this.mDetails.appsSize, this.mCurrentUser, paramPackageStats.externalCodeSize + paramPackageStats.externalDataSize + paramPackageStats.externalMediaSize + paramPackageStats.externalObbSize);
      StorageMeasurement.MeasurementDetails localMeasurementDetails = this.mDetails;
      localMeasurementDetails.cacheSize += paramPackageStats.externalCacheSize;
    }
    
    public void onGetStatsCompleted(PackageStats paramPackageStats, boolean paramBoolean)
    {
      StorageMeasurement.MeasurementDetails localMeasurementDetails = this.mDetails;
      if (paramBoolean) {}
      try
      {
        addStatsLocked(paramPackageStats);
        int i = this.mRemaining - 1;
        this.mRemaining = i;
        if (i == 0) {
          this.mFinished.sendToTarget();
        }
        return;
      }
      finally {}
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\deviceinfo\StorageMeasurement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */