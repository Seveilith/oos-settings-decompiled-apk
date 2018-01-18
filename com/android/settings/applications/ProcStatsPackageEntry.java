package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.settings.Utils;
import java.util.ArrayList;

public class ProcStatsPackageEntry
  implements Parcelable
{
  private static final float ALWAYS_THRESHOLD = 0.95F;
  public static final Parcelable.Creator<ProcStatsPackageEntry> CREATOR = new Parcelable.Creator()
  {
    public ProcStatsPackageEntry createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ProcStatsPackageEntry(paramAnonymousParcel);
    }
    
    public ProcStatsPackageEntry[] newArray(int paramAnonymousInt)
    {
      return new ProcStatsPackageEntry[paramAnonymousInt];
    }
  };
  private static boolean DEBUG = false;
  private static final float SOMETIMES_THRESHOLD = 0.25F;
  private static final String TAG = "ProcStatsEntry";
  long mAvgBgMem;
  long mAvgRunMem;
  long mBgDuration;
  double mBgWeight;
  final ArrayList<ProcStatsEntry> mEntries = new ArrayList();
  long mMaxBgMem;
  long mMaxRunMem;
  final String mPackage;
  long mRunDuration;
  double mRunWeight;
  public String mUiLabel;
  public ApplicationInfo mUiTargetApp;
  private long mWindowLength;
  
  public ProcStatsPackageEntry(Parcel paramParcel)
  {
    this.mPackage = paramParcel.readString();
    paramParcel.readTypedList(this.mEntries, ProcStatsEntry.CREATOR);
    this.mBgDuration = paramParcel.readLong();
    this.mAvgBgMem = paramParcel.readLong();
    this.mMaxBgMem = paramParcel.readLong();
    this.mBgWeight = paramParcel.readDouble();
    this.mRunDuration = paramParcel.readLong();
    this.mAvgRunMem = paramParcel.readLong();
    this.mMaxRunMem = paramParcel.readLong();
    this.mRunWeight = paramParcel.readDouble();
  }
  
  public ProcStatsPackageEntry(String paramString, long paramLong)
  {
    this.mPackage = paramString;
    this.mWindowLength = paramLong;
  }
  
  public static CharSequence getFrequency(float paramFloat, Context paramContext)
  {
    if (paramFloat > 0.95F) {
      return paramContext.getString(2131693450, new Object[] { Utils.formatPercentage((int)(100.0F * paramFloat)) });
    }
    if (paramFloat > 0.25F) {
      return paramContext.getString(2131693451, new Object[] { Utils.formatPercentage((int)(100.0F * paramFloat)) });
    }
    return paramContext.getString(2131693452, new Object[] { Utils.formatPercentage((int)(100.0F * paramFloat)) });
  }
  
  public void addEntry(ProcStatsEntry paramProcStatsEntry)
  {
    this.mEntries.add(paramProcStatsEntry);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public CharSequence getBackgroundFrequency(Context paramContext)
  {
    return getFrequency((float)this.mBgDuration / (float)this.mWindowLength, paramContext);
  }
  
  public CharSequence getRunningFrequency(Context paramContext)
  {
    return getFrequency((float)this.mRunDuration / (float)this.mWindowLength, paramContext);
  }
  
  public void retrieveUiData(Context paramContext, PackageManager paramPackageManager)
  {
    this.mUiTargetApp = null;
    this.mUiLabel = this.mPackage;
    try
    {
      if ("os".equals(this.mPackage))
      {
        this.mUiTargetApp = paramPackageManager.getApplicationInfo("android", 41472);
        this.mUiLabel = paramContext.getString(2131692530);
        return;
      }
      this.mUiTargetApp = paramPackageManager.getApplicationInfo(this.mPackage, 41472);
      this.mUiLabel = this.mUiTargetApp.loadLabel(paramPackageManager).toString();
      return;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
  }
  
  public void updateMetrics()
  {
    this.mMaxBgMem = 0L;
    this.mAvgBgMem = 0L;
    this.mBgDuration = 0L;
    this.mBgWeight = 0.0D;
    this.mMaxRunMem = 0L;
    this.mAvgRunMem = 0L;
    this.mRunDuration = 0L;
    this.mRunWeight = 0.0D;
    int j = this.mEntries.size();
    int i = 0;
    while (i < j)
    {
      ProcStatsEntry localProcStatsEntry = (ProcStatsEntry)this.mEntries.get(i);
      this.mBgDuration = Math.max(localProcStatsEntry.mBgDuration, this.mBgDuration);
      this.mAvgBgMem += localProcStatsEntry.mAvgBgMem;
      this.mBgWeight += localProcStatsEntry.mBgWeight;
      this.mRunDuration = Math.max(localProcStatsEntry.mRunDuration, this.mRunDuration);
      this.mAvgRunMem += localProcStatsEntry.mAvgRunMem;
      this.mRunWeight += localProcStatsEntry.mRunWeight;
      this.mMaxBgMem += localProcStatsEntry.mMaxBgMem;
      this.mMaxRunMem += localProcStatsEntry.mMaxRunMem;
      i += 1;
    }
    this.mAvgBgMem /= j;
    this.mAvgRunMem /= j;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackage);
    paramParcel.writeTypedList(this.mEntries);
    paramParcel.writeLong(this.mBgDuration);
    paramParcel.writeLong(this.mAvgBgMem);
    paramParcel.writeLong(this.mMaxBgMem);
    paramParcel.writeDouble(this.mBgWeight);
    paramParcel.writeLong(this.mRunDuration);
    paramParcel.writeLong(this.mAvgRunMem);
    paramParcel.writeLong(this.mMaxRunMem);
    paramParcel.writeDouble(this.mRunWeight);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ProcStatsPackageEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */