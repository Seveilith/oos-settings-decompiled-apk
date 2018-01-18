package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.app.ProcessMap;
import com.android.internal.app.procstats.DumpUtils;
import com.android.internal.app.procstats.IProcessStats;
import com.android.internal.app.procstats.IProcessStats.Stub;
import com.android.internal.app.procstats.ProcessState;
import com.android.internal.app.procstats.ProcessStats;
import com.android.internal.app.procstats.ProcessStats.PackageState;
import com.android.internal.app.procstats.ProcessStats.ProcessDataCollection;
import com.android.internal.app.procstats.ProcessStats.TotalMemoryUseCollection;
import com.android.internal.app.procstats.ServiceState;
import com.android.internal.util.MemInfoReader;
import com.oneplus.settings.SettingsBaseApplication;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProcStatsData
{
  private static final boolean DEBUG = false;
  private static final String TAG = "ProcStatsManager";
  static final Comparator<ProcStatsEntry> sEntryCompare = new Comparator()
  {
    public int compare(ProcStatsEntry paramAnonymousProcStatsEntry1, ProcStatsEntry paramAnonymousProcStatsEntry2)
    {
      if (paramAnonymousProcStatsEntry1.mRunWeight < paramAnonymousProcStatsEntry2.mRunWeight) {
        return 1;
      }
      if (paramAnonymousProcStatsEntry1.mRunWeight > paramAnonymousProcStatsEntry2.mRunWeight) {
        return -1;
      }
      if (paramAnonymousProcStatsEntry1.mRunDuration < paramAnonymousProcStatsEntry2.mRunDuration) {
        return 1;
      }
      if (paramAnonymousProcStatsEntry1.mRunDuration > paramAnonymousProcStatsEntry2.mRunDuration) {
        return -1;
      }
      return 0;
    }
  };
  private static ProcessStats sStatsXfer;
  private Context mContext;
  private long mDuration;
  private MemInfo mMemInfo;
  private int[] mMemStates;
  private PackageManager mPm;
  private IProcessStats mProcessStats;
  private int[] mStates;
  private ProcessStats mStats;
  private boolean mUseUss;
  private long memTotalTime;
  private ArrayList<ProcStatsPackageEntry> pkgEntries;
  
  public ProcStatsData(Context paramContext, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.mPm = SettingsBaseApplication.mApplication.getPackageManager();
    this.mProcessStats = IProcessStats.Stub.asInterface(ServiceManager.getService("procstats"));
    this.mMemStates = ProcessStats.ALL_MEM_ADJ;
    this.mStates = ProcessStats.BACKGROUND_PROC_STATES;
    if (paramBoolean) {
      this.mStats = sStatsXfer;
    }
  }
  
  private ProcStatsPackageEntry createOsEntry(ProcessStats.ProcessDataCollection paramProcessDataCollection1, ProcessStats.ProcessDataCollection paramProcessDataCollection2, ProcessStats.TotalMemoryUseCollection paramTotalMemoryUseCollection, long paramLong)
  {
    ProcStatsPackageEntry localProcStatsPackageEntry = new ProcStatsPackageEntry("os", this.memTotalTime);
    if (paramTotalMemoryUseCollection.sysMemNativeWeight > 0.0D)
    {
      ProcStatsEntry localProcStatsEntry = new ProcStatsEntry("os", 0, this.mContext.getString(2131692531), this.memTotalTime, (paramTotalMemoryUseCollection.sysMemNativeWeight / this.memTotalTime), this.memTotalTime);
      localProcStatsEntry.evaluateTargetPackage(this.mPm, this.mStats, paramProcessDataCollection1, paramProcessDataCollection2, sEntryCompare, this.mUseUss);
      localProcStatsPackageEntry.addEntry(localProcStatsEntry);
    }
    if (paramTotalMemoryUseCollection.sysMemKernelWeight > 0.0D)
    {
      paramTotalMemoryUseCollection = new ProcStatsEntry("os", 0, this.mContext.getString(2131692532), this.memTotalTime, (paramTotalMemoryUseCollection.sysMemKernelWeight / this.memTotalTime), this.memTotalTime);
      paramTotalMemoryUseCollection.evaluateTargetPackage(this.mPm, this.mStats, paramProcessDataCollection1, paramProcessDataCollection2, sEntryCompare, this.mUseUss);
      localProcStatsPackageEntry.addEntry(paramTotalMemoryUseCollection);
    }
    if (paramLong > 0L)
    {
      paramTotalMemoryUseCollection = new ProcStatsEntry("os", 0, this.mContext.getString(2131692534), this.memTotalTime, paramLong / 1024L, this.memTotalTime);
      paramTotalMemoryUseCollection.evaluateTargetPackage(this.mPm, this.mStats, paramProcessDataCollection1, paramProcessDataCollection2, sEntryCompare, this.mUseUss);
      localProcStatsPackageEntry.addEntry(paramTotalMemoryUseCollection);
    }
    return localProcStatsPackageEntry;
  }
  
  private void createPkgMap(ArrayList<ProcStatsEntry> paramArrayList, ProcessStats.ProcessDataCollection paramProcessDataCollection1, ProcessStats.ProcessDataCollection paramProcessDataCollection2)
  {
    ArrayMap localArrayMap = new ArrayMap();
    int i = paramArrayList.size() - 1;
    while (i >= 0)
    {
      ProcStatsEntry localProcStatsEntry = (ProcStatsEntry)paramArrayList.get(i);
      localProcStatsEntry.evaluateTargetPackage(this.mPm, this.mStats, paramProcessDataCollection1, paramProcessDataCollection2, sEntryCompare, this.mUseUss);
      ProcStatsPackageEntry localProcStatsPackageEntry2 = (ProcStatsPackageEntry)localArrayMap.get(localProcStatsEntry.mBestTargetPackage);
      ProcStatsPackageEntry localProcStatsPackageEntry1 = localProcStatsPackageEntry2;
      if (localProcStatsPackageEntry2 == null)
      {
        localProcStatsPackageEntry1 = new ProcStatsPackageEntry(localProcStatsEntry.mBestTargetPackage, this.memTotalTime);
        localArrayMap.put(localProcStatsEntry.mBestTargetPackage, localProcStatsPackageEntry1);
        this.pkgEntries.add(localProcStatsPackageEntry1);
      }
      localProcStatsPackageEntry1.addEntry(localProcStatsEntry);
      i -= 1;
    }
  }
  
  private void distributeZRam(double paramDouble)
  {
    long l2 = (paramDouble / this.memTotalTime);
    long l1 = 0L;
    int i = this.pkgEntries.size() - 1;
    ProcStatsPackageEntry localProcStatsPackageEntry;
    int j;
    while (i >= 0)
    {
      localProcStatsPackageEntry = (ProcStatsPackageEntry)this.pkgEntries.get(i);
      j = localProcStatsPackageEntry.mEntries.size() - 1;
      while (j >= 0)
      {
        l1 += ((ProcStatsEntry)localProcStatsPackageEntry.mEntries.get(j)).mRunDuration;
        j -= 1;
      }
      i -= 1;
    }
    i = this.pkgEntries.size() - 1;
    while ((i >= 0) && (l1 > 0L))
    {
      localProcStatsPackageEntry = (ProcStatsPackageEntry)this.pkgEntries.get(i);
      long l4 = 0L;
      long l3 = 0L;
      j = localProcStatsPackageEntry.mEntries.size() - 1;
      ProcStatsEntry localProcStatsEntry;
      while (j >= 0)
      {
        localProcStatsEntry = (ProcStatsEntry)localProcStatsPackageEntry.mEntries.get(j);
        l5 = l4 + localProcStatsEntry.mRunDuration;
        l4 = l3;
        if (localProcStatsEntry.mRunDuration > l3) {
          l4 = localProcStatsEntry.mRunDuration;
        }
        j -= 1;
        l3 = l4;
        l4 = l5;
      }
      long l7 = l2 * l4 / l1;
      long l6 = l1;
      long l5 = l2;
      if (l7 > 0L)
      {
        l5 = l2 - l7;
        l6 = l1 - l4;
        localProcStatsEntry = new ProcStatsEntry(localProcStatsPackageEntry.mPackage, 0, this.mContext.getString(2131692533), l3, l7, this.memTotalTime);
        localProcStatsEntry.evaluateTargetPackage(this.mPm, this.mStats, null, null, sEntryCompare, this.mUseUss);
        localProcStatsPackageEntry.addEntry(localProcStatsEntry);
      }
      i -= 1;
      l1 = l6;
      l2 = l5;
    }
  }
  
  private ArrayList<ProcStatsEntry> getProcs(ProcessStats.ProcessDataCollection paramProcessDataCollection1, ProcessStats.ProcessDataCollection paramProcessDataCollection2)
  {
    ArrayList localArrayList = new ArrayList();
    ProcessMap localProcessMap = new ProcessMap();
    int i = 0;
    int n = this.mStats.mPackages.getMap().size();
    Object localObject1;
    int j;
    Object localObject2;
    int k;
    Object localObject3;
    int m;
    while (i < n)
    {
      localObject1 = (SparseArray)this.mStats.mPackages.getMap().valueAt(i);
      j = 0;
      while (j < ((SparseArray)localObject1).size())
      {
        localObject2 = (SparseArray)((SparseArray)localObject1).valueAt(j);
        k = 0;
        while (k < ((SparseArray)localObject2).size())
        {
          localObject3 = (ProcessStats.PackageState)((SparseArray)localObject2).valueAt(k);
          m = 0;
          if (m < ((ProcessStats.PackageState)localObject3).mProcesses.size())
          {
            Object localObject4 = (ProcessState)((ProcessStats.PackageState)localObject3).mProcesses.valueAt(m);
            ProcessState localProcessState = (ProcessState)this.mStats.mProcesses.get(((ProcessState)localObject4).getName(), ((ProcessState)localObject4).getUid());
            if (localProcessState == null) {
              Log.w("ProcStatsManager", "No process found for pkg " + ((ProcessStats.PackageState)localObject3).mPackageName + "/" + ((ProcessStats.PackageState)localObject3).mUid + " proc name " + ((ProcessState)localObject4).getName());
            }
            for (;;)
            {
              m += 1;
              break;
              localObject4 = (ProcStatsEntry)localProcessMap.get(localProcessState.getName(), localProcessState.getUid());
              if (localObject4 == null)
              {
                localObject4 = new ProcStatsEntry(localProcessState, ((ProcessStats.PackageState)localObject3).mPackageName, paramProcessDataCollection1, paramProcessDataCollection2, this.mUseUss);
                if (((ProcStatsEntry)localObject4).mRunWeight > 0.0D)
                {
                  localProcessMap.put(localProcessState.getName(), localProcessState.getUid(), localObject4);
                  localArrayList.add(localObject4);
                }
              }
              else
              {
                ((ProcStatsEntry)localObject4).addPackage(((ProcessStats.PackageState)localObject3).mPackageName);
              }
            }
          }
          k += 1;
        }
        j += 1;
      }
      i += 1;
    }
    i = 0;
    n = this.mStats.mPackages.getMap().size();
    while (i < n)
    {
      paramProcessDataCollection1 = (SparseArray)this.mStats.mPackages.getMap().valueAt(i);
      j = 0;
      while (j < paramProcessDataCollection1.size())
      {
        paramProcessDataCollection2 = (SparseArray)paramProcessDataCollection1.valueAt(j);
        k = 0;
        while (k < paramProcessDataCollection2.size())
        {
          localObject1 = (ProcessStats.PackageState)paramProcessDataCollection2.valueAt(k);
          m = 0;
          int i1 = ((ProcessStats.PackageState)localObject1).mServices.size();
          if (m < i1)
          {
            localObject2 = (ServiceState)((ProcessStats.PackageState)localObject1).mServices.valueAt(m);
            if (((ServiceState)localObject2).getProcessName() != null)
            {
              localObject3 = (ProcStatsEntry)localProcessMap.get(((ServiceState)localObject2).getProcessName(), paramProcessDataCollection1.keyAt(j));
              if (localObject3 == null) {
                break label530;
              }
              ((ProcStatsEntry)localObject3).addService((ServiceState)localObject2);
            }
            for (;;)
            {
              m += 1;
              break;
              label530:
              Log.w("ProcStatsManager", "No process " + ((ServiceState)localObject2).getProcessName() + "/" + paramProcessDataCollection1.keyAt(j) + " for service " + ((ServiceState)localObject2).getName());
            }
          }
          k += 1;
        }
        j += 1;
      }
      i += 1;
    }
    return localArrayList;
  }
  
  private void load()
  {
    try
    {
      Object localObject = this.mProcessStats.getStatsOverTime(this.mDuration);
      this.mStats = new ProcessStats(false);
      localObject = new ParcelFileDescriptor.AutoCloseInputStream((ParcelFileDescriptor)localObject);
      this.mStats.read((InputStream)localObject);
      try
      {
        ((InputStream)localObject).close();
        if (this.mStats.mReadError != null) {
          Log.w("ProcStatsManager", "Failure reading process stats: " + this.mStats.mReadError);
        }
        return;
      }
      catch (IOException localIOException)
      {
        for (;;) {}
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("ProcStatsManager", "RemoteException:", localRemoteException);
    }
  }
  
  public long getDuration()
  {
    return this.mDuration;
  }
  
  public long getElapsedTime()
  {
    return this.mStats.mTimePeriodEndRealtime - this.mStats.mTimePeriodStartRealtime;
  }
  
  public List<ProcStatsPackageEntry> getEntries()
  {
    return this.pkgEntries;
  }
  
  public MemInfo getMemInfo()
  {
    return this.mMemInfo;
  }
  
  public int getMemState()
  {
    int j = this.mStats.mMemFactor;
    if (j == -1) {
      return 0;
    }
    int i = j;
    if (j >= 4) {
      i = j - 4;
    }
    return i;
  }
  
  public void refreshStats(boolean paramBoolean)
  {
    if ((this.mStats == null) || (paramBoolean)) {
      load();
    }
    this.pkgEntries = new ArrayList();
    long l = SystemClock.uptimeMillis();
    this.memTotalTime = DumpUtils.dumpSingleTime(null, null, this.mStats.mMemFactorDurations, this.mStats.mMemFactor, this.mStats.mStartTime, l);
    Object localObject = new ProcessStats.TotalMemoryUseCollection(ProcessStats.ALL_SCREEN_ADJ, this.mMemStates);
    this.mStats.computeTotalMemoryUse((ProcessStats.TotalMemoryUseCollection)localObject, l);
    this.mMemInfo = new MemInfo(this.mContext, (ProcessStats.TotalMemoryUseCollection)localObject, this.memTotalTime, null);
    ProcessStats.ProcessDataCollection localProcessDataCollection1 = new ProcessStats.ProcessDataCollection(ProcessStats.ALL_SCREEN_ADJ, this.mMemStates, this.mStates);
    ProcessStats.ProcessDataCollection localProcessDataCollection2 = new ProcessStats.ProcessDataCollection(ProcessStats.ALL_SCREEN_ADJ, this.mMemStates, ProcessStats.NON_CACHED_PROC_STATES);
    createPkgMap(getProcs(localProcessDataCollection1, localProcessDataCollection2), localProcessDataCollection1, localProcessDataCollection2);
    if ((((ProcessStats.TotalMemoryUseCollection)localObject).sysMemZRamWeight <= 0.0D) || (((ProcessStats.TotalMemoryUseCollection)localObject).hasSwappedOutPss)) {}
    for (;;)
    {
      localObject = createOsEntry(localProcessDataCollection1, localProcessDataCollection2, (ProcessStats.TotalMemoryUseCollection)localObject, this.mMemInfo.baseCacheRam);
      this.pkgEntries.add(localObject);
      return;
      distributeZRam(((ProcessStats.TotalMemoryUseCollection)localObject).sysMemZRamWeight);
    }
  }
  
  public void setDuration(long paramLong)
  {
    if (paramLong != this.mDuration)
    {
      this.mDuration = paramLong;
      refreshStats(true);
    }
  }
  
  public void setMemStates(int[] paramArrayOfInt)
  {
    this.mMemStates = paramArrayOfInt;
    refreshStats(false);
  }
  
  public void setStats(int[] paramArrayOfInt)
  {
    this.mStates = paramArrayOfInt;
    refreshStats(false);
  }
  
  public void setTotalTime(int paramInt)
  {
    this.memTotalTime = paramInt;
  }
  
  public void xferStats()
  {
    sStatsXfer = this.mStats;
  }
  
  public static class MemInfo
  {
    long baseCacheRam;
    double freeWeight;
    double[] mMemStateWeights = new double[14];
    long memTotalTime;
    public double realFreeRam;
    public double realTotalRam;
    public double realUsedRam;
    double totalRam;
    double totalScale;
    double usedWeight;
    double weightToRam;
    
    private MemInfo(Context paramContext, ProcessStats.TotalMemoryUseCollection paramTotalMemoryUseCollection, long paramLong)
    {
      this.memTotalTime = paramLong;
      calculateWeightInfo(paramContext, paramTotalMemoryUseCollection, paramLong);
      double d1 = this.usedWeight * 1024.0D / paramLong;
      double d2 = this.freeWeight * 1024.0D / paramLong;
      this.totalRam = (d1 + d2);
      this.totalScale = (this.realTotalRam / this.totalRam);
      this.weightToRam = (this.totalScale / paramLong * 1024.0D);
      this.realUsedRam = (this.totalScale * d1);
      this.realFreeRam = (this.totalScale * d2);
      paramTotalMemoryUseCollection = new ActivityManager.MemoryInfo();
      ((ActivityManager)paramContext.getSystemService("activity")).getMemoryInfo(paramTotalMemoryUseCollection);
      if (paramTotalMemoryUseCollection.hiddenAppThreshold >= this.realFreeRam)
      {
        this.realUsedRam = d2;
        this.realFreeRam = 0.0D;
        this.baseCacheRam = (this.realFreeRam);
        return;
      }
      this.realUsedRam += paramTotalMemoryUseCollection.hiddenAppThreshold;
      this.realFreeRam -= paramTotalMemoryUseCollection.hiddenAppThreshold;
      this.baseCacheRam = paramTotalMemoryUseCollection.hiddenAppThreshold;
    }
    
    private void calculateWeightInfo(Context paramContext, ProcessStats.TotalMemoryUseCollection paramTotalMemoryUseCollection, long paramLong)
    {
      paramContext = new MemInfoReader();
      paramContext.readMemInfo();
      this.realTotalRam = paramContext.getTotalSize();
      this.freeWeight = (paramTotalMemoryUseCollection.sysMemFreeWeight + paramTotalMemoryUseCollection.sysMemCachedWeight);
      this.usedWeight = (paramTotalMemoryUseCollection.sysMemKernelWeight + paramTotalMemoryUseCollection.sysMemNativeWeight);
      if (!paramTotalMemoryUseCollection.hasSwappedOutPss) {
        this.usedWeight += paramTotalMemoryUseCollection.sysMemZRamWeight;
      }
      int i = 0;
      if (i < 14)
      {
        if (i == 7) {
          this.mMemStateWeights[i] = 0.0D;
        }
        for (;;)
        {
          i += 1;
          break;
          this.mMemStateWeights[i] = paramTotalMemoryUseCollection.processStateWeight[i];
          if (i >= 9) {
            this.freeWeight += paramTotalMemoryUseCollection.processStateWeight[i];
          } else {
            this.usedWeight += paramTotalMemoryUseCollection.processStateWeight[i];
          }
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ProcStatsData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */