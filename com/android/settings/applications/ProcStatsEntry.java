package com.android.settings.applications;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.app.ProcessMap;
import com.android.internal.app.procstats.ProcessState;
import com.android.internal.app.procstats.ProcessStats;
import com.android.internal.app.procstats.ProcessStats.PackageState;
import com.android.internal.app.procstats.ProcessStats.ProcessDataCollection;
import com.android.internal.app.procstats.ServiceState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ProcStatsEntry
  implements Parcelable
{
  public static final Parcelable.Creator<ProcStatsEntry> CREATOR = new Parcelable.Creator()
  {
    public ProcStatsEntry createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ProcStatsEntry(paramAnonymousParcel);
    }
    
    public ProcStatsEntry[] newArray(int paramAnonymousInt)
    {
      return new ProcStatsEntry[paramAnonymousInt];
    }
  };
  private static boolean DEBUG = false;
  private static final String TAG = "ProcStatsEntry";
  final long mAvgBgMem;
  final long mAvgRunMem;
  String mBestTargetPackage;
  final long mBgDuration;
  final double mBgWeight;
  public CharSequence mLabel;
  final long mMaxBgMem;
  final long mMaxRunMem;
  final String mName;
  final String mPackage;
  final ArrayList<String> mPackages = new ArrayList();
  final long mRunDuration;
  final double mRunWeight;
  ArrayMap<String, ArrayList<Service>> mServices = new ArrayMap(1);
  final int mUid;
  
  public ProcStatsEntry(Parcel paramParcel)
  {
    this.mPackage = paramParcel.readString();
    this.mUid = paramParcel.readInt();
    this.mName = paramParcel.readString();
    paramParcel.readStringList(this.mPackages);
    this.mBgDuration = paramParcel.readLong();
    this.mAvgBgMem = paramParcel.readLong();
    this.mMaxBgMem = paramParcel.readLong();
    this.mBgWeight = paramParcel.readDouble();
    this.mRunDuration = paramParcel.readLong();
    this.mAvgRunMem = paramParcel.readLong();
    this.mMaxRunMem = paramParcel.readLong();
    this.mRunWeight = paramParcel.readDouble();
    this.mBestTargetPackage = paramParcel.readString();
    int j = paramParcel.readInt();
    if (j > 0)
    {
      this.mServices.ensureCapacity(j);
      int i = 0;
      while (i < j)
      {
        String str = paramParcel.readString();
        ArrayList localArrayList = new ArrayList();
        paramParcel.readTypedList(localArrayList, Service.CREATOR);
        this.mServices.append(str, localArrayList);
        i += 1;
      }
    }
  }
  
  public ProcStatsEntry(ProcessState paramProcessState, String paramString, ProcessStats.ProcessDataCollection paramProcessDataCollection1, ProcessStats.ProcessDataCollection paramProcessDataCollection2, boolean paramBoolean)
  {
    paramProcessState.computeProcessData(paramProcessDataCollection1, 0L);
    paramProcessState.computeProcessData(paramProcessDataCollection2, 0L);
    this.mPackage = paramProcessState.getPackage();
    this.mUid = paramProcessState.getUid();
    this.mName = paramProcessState.getName();
    this.mPackages.add(paramString);
    this.mBgDuration = paramProcessDataCollection1.totalTime;
    if (paramBoolean)
    {
      l = paramProcessDataCollection1.avgUss;
      this.mAvgBgMem = l;
      if (!paramBoolean) {
        break label270;
      }
      l = paramProcessDataCollection1.maxUss;
      label109:
      this.mMaxBgMem = l;
      this.mBgWeight = (this.mAvgBgMem * this.mBgDuration);
      this.mRunDuration = paramProcessDataCollection2.totalTime;
      if (!paramBoolean) {
        break label279;
      }
      l = paramProcessDataCollection2.avgUss;
      label151:
      this.mAvgRunMem = l;
      if (!paramBoolean) {
        break label289;
      }
    }
    label270:
    label279:
    label289:
    for (long l = paramProcessDataCollection2.maxUss;; l = paramProcessDataCollection2.maxPss)
    {
      this.mMaxRunMem = l;
      this.mRunWeight = (this.mAvgRunMem * this.mRunDuration);
      if (DEBUG) {
        Log.d("ProcStatsEntry", "New proc entry " + paramProcessState.getName() + ": dur=" + this.mBgDuration + " avgpss=" + this.mAvgBgMem + " weight=" + this.mBgWeight);
      }
      return;
      l = paramProcessDataCollection1.avgPss;
      break;
      l = paramProcessDataCollection1.maxPss;
      break label109;
      l = paramProcessDataCollection2.avgPss;
      break label151;
    }
  }
  
  public ProcStatsEntry(String paramString1, int paramInt, String paramString2, long paramLong1, long paramLong2, long paramLong3)
  {
    this.mPackage = paramString1;
    this.mUid = paramInt;
    this.mName = paramString2;
    this.mRunDuration = paramLong1;
    this.mBgDuration = paramLong1;
    this.mMaxRunMem = paramLong2;
    this.mAvgRunMem = paramLong2;
    this.mMaxBgMem = paramLong2;
    this.mAvgBgMem = paramLong2;
    double d = paramLong3 * paramLong2;
    this.mRunWeight = d;
    this.mBgWeight = d;
    if (DEBUG) {
      Log.d("ProcStatsEntry", "New proc entry " + paramString2 + ": dur=" + this.mBgDuration + " avgpss=" + this.mAvgBgMem + " weight=" + this.mBgWeight);
    }
  }
  
  public void addPackage(String paramString)
  {
    this.mPackages.add(paramString);
  }
  
  public void addService(ServiceState paramServiceState)
  {
    ArrayList localArrayList2 = (ArrayList)this.mServices.get(paramServiceState.getPackage());
    ArrayList localArrayList1 = localArrayList2;
    if (localArrayList2 == null)
    {
      localArrayList1 = new ArrayList();
      this.mServices.put(paramServiceState.getPackage(), localArrayList1);
    }
    localArrayList1.add(new Service(paramServiceState));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void evaluateTargetPackage(PackageManager paramPackageManager, ProcessStats paramProcessStats, ProcessStats.ProcessDataCollection paramProcessDataCollection1, ProcessStats.ProcessDataCollection paramProcessDataCollection2, Comparator<ProcStatsEntry> paramComparator, boolean paramBoolean)
  {
    this.mBestTargetPackage = null;
    if (this.mPackages.size() == 1)
    {
      if (DEBUG) {
        Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": single pkg " + (String)this.mPackages.get(0));
      }
      this.mBestTargetPackage = ((String)this.mPackages.get(0));
      return;
    }
    int i = 0;
    while (i < this.mPackages.size())
    {
      if ("android".equals(this.mPackages.get(i)))
      {
        this.mBestTargetPackage = ((String)this.mPackages.get(i));
        return;
      }
      i += 1;
    }
    ArrayList localArrayList = new ArrayList();
    i = 0;
    int j;
    while (i < this.mPackages.size())
    {
      SparseArray localSparseArray = (SparseArray)paramProcessStats.mPackages.get((String)this.mPackages.get(i), this.mUid);
      j = 0;
      if (j < localSparseArray.size())
      {
        ProcessStats.PackageState localPackageState = (ProcessStats.PackageState)localSparseArray.valueAt(j);
        if (DEBUG) {
          Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ", pkg " + localPackageState + ":");
        }
        if (localPackageState == null) {
          Log.w("ProcStatsEntry", "No package state found for " + (String)this.mPackages.get(i) + "/" + this.mUid + " in process " + this.mName);
        }
        for (;;)
        {
          j += 1;
          break;
          ProcessState localProcessState = (ProcessState)localPackageState.mProcesses.get(this.mName);
          if (localProcessState == null) {
            Log.w("ProcStatsEntry", "No process " + this.mName + " found in package state " + (String)this.mPackages.get(i) + "/" + this.mUid);
          } else {
            localArrayList.add(new ProcStatsEntry(localProcessState, localPackageState.mPackageName, paramProcessDataCollection1, paramProcessDataCollection2, paramBoolean));
          }
        }
      }
      i += 1;
    }
    long l3;
    int k;
    long l1;
    if (localArrayList.size() > 1)
    {
      Collections.sort(localArrayList, paramComparator);
      if (((ProcStatsEntry)localArrayList.get(0)).mRunWeight > ((ProcStatsEntry)localArrayList.get(1)).mRunWeight * 3.0D)
      {
        if (DEBUG) {
          Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": best pkg " + ((ProcStatsEntry)localArrayList.get(0)).mPackage + " weight " + ((ProcStatsEntry)localArrayList.get(0)).mRunWeight + " better than " + ((ProcStatsEntry)localArrayList.get(1)).mPackage + " weight " + ((ProcStatsEntry)localArrayList.get(1)).mRunWeight);
        }
        this.mBestTargetPackage = ((ProcStatsEntry)localArrayList.get(0)).mPackage;
        return;
      }
      double d = ((ProcStatsEntry)localArrayList.get(0)).mRunWeight;
      l3 = -1L;
      j = 0;
      i = 0;
      if (i >= localArrayList.size()) {
        break label1649;
      }
      paramProcessDataCollection2 = (ProcStatsEntry)localArrayList.get(i);
      if (paramProcessDataCollection2.mRunWeight < d / 2.0D)
      {
        k = j;
        l1 = l3;
        if (DEBUG)
        {
          Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + paramProcessDataCollection2.mPackage + " weight " + paramProcessDataCollection2.mRunWeight + " too small");
          l1 = l3;
          k = j;
        }
      }
    }
    for (;;)
    {
      i += 1;
      j = k;
      l3 = l1;
      break;
      try
      {
        paramProcessStats = paramPackageManager.getApplicationInfo(paramProcessDataCollection2.mPackage, 0);
        if (paramProcessStats.icon != 0) {
          break label983;
        }
        k = j;
        l1 = l3;
        if (!DEBUG) {
          continue;
        }
        Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + paramProcessDataCollection2.mPackage + " has no icon");
        k = j;
        l1 = l3;
      }
      catch (PackageManager.NameNotFoundException paramProcessStats)
      {
        k = j;
        l1 = l3;
      }
      if (DEBUG)
      {
        Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + paramProcessDataCollection2.mPackage + " failed finding app info");
        k = j;
        l1 = l3;
        continue;
        label983:
        long l2;
        if ((paramProcessStats.flags & 0x8) != 0)
        {
          l2 = paramProcessDataCollection2.mRunDuration;
          if ((j == 0) || (l2 > l3))
          {
            if (DEBUG) {
              Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + paramProcessDataCollection2.mPackage + " new best pers run time " + l2);
            }
          }
          else
          {
            k = j;
            l1 = l3;
            if (!DEBUG) {
              continue;
            }
            Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + paramProcessDataCollection2.mPackage + " pers run time " + l2 + " not as good as last " + l3);
            k = j;
            l1 = l3;
          }
        }
        else
        {
          if (j != 0)
          {
            k = j;
            l1 = l3;
            if (!DEBUG) {
              continue;
            }
            Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + paramProcessDataCollection2.mPackage + " is not persistent");
            k = j;
            l1 = l3;
            continue;
          }
          paramProcessDataCollection1 = null;
          k = 0;
          int m = this.mServices.size();
          label1256:
          paramProcessStats = paramProcessDataCollection1;
          if (k < m)
          {
            paramProcessStats = (ArrayList)this.mServices.valueAt(k);
            if (!((Service)paramProcessStats.get(0)).mPackage.equals(paramProcessDataCollection2.mPackage)) {}
          }
          else
          {
            l1 = 0L;
            l2 = l1;
            if (paramProcessStats != null)
            {
              k = 0;
              m = paramProcessStats.size();
            }
          }
          for (;;)
          {
            l2 = l1;
            if (k < m)
            {
              paramProcessDataCollection1 = (Service)paramProcessStats.get(k);
              if (paramProcessDataCollection1.mDuration > 0L)
              {
                if (DEBUG) {
                  Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + paramProcessDataCollection2.mPackage + " service " + paramProcessDataCollection1.mName + " run time is " + paramProcessDataCollection1.mDuration);
                }
                l2 = paramProcessDataCollection1.mDuration;
              }
            }
            else
            {
              if (l2 <= l3) {
                break label1535;
              }
              if (DEBUG) {
                Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + paramProcessDataCollection2.mPackage + " new best run time " + l2);
              }
              this.mBestTargetPackage = paramProcessDataCollection2.mPackage;
              l1 = l2;
              k = j;
              break;
              k += 1;
              break label1256;
            }
            k += 1;
          }
          label1535:
          k = j;
          l1 = l3;
          if (!DEBUG) {
            continue;
          }
          Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + paramProcessDataCollection2.mPackage + " run time " + l2 + " not as good as last " + l3);
          k = j;
          l1 = l3;
          continue;
          if (localArrayList.size() == 1) {
            this.mBestTargetPackage = ((ProcStatsEntry)localArrayList.get(0)).mPackage;
          }
          label1649:
          return;
        }
        l1 = l2;
        k = 1;
      }
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackage);
    paramParcel.writeInt(this.mUid);
    paramParcel.writeString(this.mName);
    paramParcel.writeStringList(this.mPackages);
    paramParcel.writeLong(this.mBgDuration);
    paramParcel.writeLong(this.mAvgBgMem);
    paramParcel.writeLong(this.mMaxBgMem);
    paramParcel.writeDouble(this.mBgWeight);
    paramParcel.writeLong(this.mRunDuration);
    paramParcel.writeLong(this.mAvgRunMem);
    paramParcel.writeLong(this.mMaxRunMem);
    paramParcel.writeDouble(this.mRunWeight);
    paramParcel.writeString(this.mBestTargetPackage);
    int i = this.mServices.size();
    paramParcel.writeInt(i);
    paramInt = 0;
    while (paramInt < i)
    {
      paramParcel.writeString((String)this.mServices.keyAt(paramInt));
      paramParcel.writeTypedList((List)this.mServices.valueAt(paramInt));
      paramInt += 1;
    }
  }
  
  public static final class Service
    implements Parcelable
  {
    public static final Parcelable.Creator<Service> CREATOR = new Parcelable.Creator()
    {
      public ProcStatsEntry.Service createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ProcStatsEntry.Service(paramAnonymousParcel);
      }
      
      public ProcStatsEntry.Service[] newArray(int paramAnonymousInt)
      {
        return new ProcStatsEntry.Service[paramAnonymousInt];
      }
    };
    final long mDuration;
    final String mName;
    final String mPackage;
    final String mProcess;
    
    public Service(Parcel paramParcel)
    {
      this.mPackage = paramParcel.readString();
      this.mName = paramParcel.readString();
      this.mProcess = paramParcel.readString();
      this.mDuration = paramParcel.readLong();
    }
    
    public Service(ServiceState paramServiceState)
    {
      this.mPackage = paramServiceState.getPackage();
      this.mName = paramServiceState.getName();
      this.mProcess = paramServiceState.getProcessName();
      this.mDuration = paramServiceState.dumpTime(null, null, 0, -1, 0L, 0L);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.mPackage);
      paramParcel.writeString(this.mName);
      paramParcel.writeString(this.mProcess);
      paramParcel.writeLong(this.mDuration);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ProcStatsEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */