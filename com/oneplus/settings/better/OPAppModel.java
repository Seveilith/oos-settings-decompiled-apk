package com.oneplus.settings.better;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class OPAppModel
  implements Parcelable
{
  public static final Parcelable.Creator<OPAppModel> CREATOR = new Parcelable.Creator()
  {
    public OPAppModel createFromParcel(Parcel paramAnonymousParcel)
    {
      return new OPAppModel(paramAnonymousParcel);
    }
    
    public OPAppModel[] newArray(int paramAnonymousInt)
    {
      return new OPAppModel[paramAnonymousInt];
    }
  };
  private Drawable appIcon;
  private boolean isSelected;
  private String label;
  private int lockMode;
  private String pkgName;
  private String shortCutId;
  private int uid;
  
  public OPAppModel(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public OPAppModel(String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean)
  {
    this.pkgName = paramString1;
    this.label = paramString2;
    this.shortCutId = paramString3;
    this.uid = paramInt;
    this.isSelected = paramBoolean;
  }
  
  public static Parcelable.Creator<OPAppModel> getCreator()
  {
    return CREATOR;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Drawable getAppIcon()
  {
    return this.appIcon;
  }
  
  public String getLabel()
  {
    return this.label;
  }
  
  public int getLockMode()
  {
    return this.lockMode;
  }
  
  public String getPkgName()
  {
    return this.pkgName;
  }
  
  public String getShortCutId()
  {
    return this.shortCutId;
  }
  
  public int getUid()
  {
    return this.uid;
  }
  
  public boolean isSelected()
  {
    return this.isSelected;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    boolean bool = true;
    this.pkgName = paramParcel.readString();
    this.pkgName = paramParcel.readString();
    this.uid = paramParcel.readInt();
    this.lockMode = paramParcel.readInt();
    if (paramParcel.readInt() == 1) {}
    for (;;)
    {
      this.isSelected = bool;
      return;
      bool = false;
    }
  }
  
  public void setAppIcon(Drawable paramDrawable)
  {
    this.appIcon = paramDrawable;
  }
  
  public void setLabel(String paramString)
  {
    this.label = paramString;
  }
  
  public void setLockMode(int paramInt)
  {
    this.lockMode = paramInt;
  }
  
  public void setPkgName(String paramString)
  {
    this.pkgName = paramString;
  }
  
  public void setSelected(boolean paramBoolean)
  {
    this.isSelected = paramBoolean;
  }
  
  public void setShortCutId(String paramString)
  {
    this.shortCutId = paramString;
  }
  
  public void setUid(int paramInt)
  {
    this.uid = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.pkgName);
    paramParcel.writeString(this.label);
    paramParcel.writeInt(this.uid);
    paramParcel.writeInt(this.lockMode);
    if (this.isSelected) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\better\OPAppModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */