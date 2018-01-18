package com.oneplus.settings.backgroundoptimize;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class AppControlMode
  implements Parcelable
{
  public static final Parcelable.Creator<AppControlMode> CREATOR = new Parcelable.Creator()
  {
    public AppControlMode createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AppControlMode(paramAnonymousParcel, null);
    }
    
    public AppControlMode[] newArray(int paramAnonymousInt)
    {
      return new AppControlMode[paramAnonymousInt];
    }
  };
  public int mode;
  public String packageName;
  public int value;
  
  private AppControlMode(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public AppControlMode(String paramString, int paramInt1, int paramInt2)
  {
    this.packageName = paramString;
    this.mode = paramInt1;
    this.value = paramInt2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.packageName = paramParcel.readString();
    this.mode = paramParcel.readInt();
    this.value = paramParcel.readInt();
  }
  
  public String toString()
  {
    return toString("");
  }
  
  public String toString(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append(" packageName=");
    localStringBuilder.append(this.packageName);
    localStringBuilder.append(" mode=");
    localStringBuilder.append(this.mode);
    localStringBuilder.append(" value=");
    localStringBuilder.append(this.value);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.packageName);
    paramParcel.writeInt(this.mode);
    paramParcel.writeInt(this.value);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\backgroundoptimize\AppControlMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */