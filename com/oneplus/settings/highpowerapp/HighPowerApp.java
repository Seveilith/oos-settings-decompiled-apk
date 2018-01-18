package com.oneplus.settings.highpowerapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class HighPowerApp
  implements Parcelable
{
  public static final Parcelable.Creator<HighPowerApp> CREATOR = new Parcelable.Creator()
  {
    public HighPowerApp createFromParcel(Parcel paramAnonymousParcel)
    {
      return new HighPowerApp(paramAnonymousParcel);
    }
    
    public HighPowerApp[] newArray(int paramAnonymousInt)
    {
      return new HighPowerApp[paramAnonymousInt];
    }
  };
  public static final int HIGH_POWER_USAGE = 1;
  public static final int MIDDLE_POWER_USAGE = 0;
  public boolean isLocked;
  public boolean isStopped;
  public String pkgName;
  public int powerLevel;
  public long timeStamp;
  public int uid;
  
  public HighPowerApp(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public HighPowerApp(String paramString, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    this.pkgName = paramString;
    this.uid = paramInt1;
    this.powerLevel = paramInt2;
    this.isLocked = paramBoolean1;
    this.isStopped = paramBoolean2;
    this.timeStamp = paramLong;
  }
  
  public HighPowerApp(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    this.pkgName = paramString;
    this.powerLevel = paramInt;
    this.isLocked = paramBoolean1;
    this.isStopped = paramBoolean2;
    this.timeStamp = paramLong;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getState()
  {
    return this.powerLevel;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    boolean bool2 = true;
    this.pkgName = paramParcel.readString();
    this.powerLevel = paramParcel.readInt();
    if (paramParcel.readInt() == 1)
    {
      bool1 = true;
      this.isLocked = bool1;
      if (paramParcel.readInt() != 1) {
        break label70;
      }
    }
    label70:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.isStopped = bool1;
      this.timeStamp = paramParcel.readLong();
      this.uid = paramParcel.readInt();
      return;
      bool1 = false;
      break;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeString(this.pkgName);
    paramParcel.writeInt(this.powerLevel);
    if (this.isLocked)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.isStopped) {
        break label68;
      }
    }
    label68:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeLong(this.timeStamp);
      paramParcel.writeInt(this.uid);
      return;
      paramInt = 0;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\highpowerapp\HighPowerApp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */