package com.oneplus.settings.gestures;

import android.graphics.drawable.Drawable;

public class OPGestureAppModel
{
  private String ShortCutId;
  private Drawable appIcon;
  private String pkgName;
  private String title;
  private int uid;
  
  public OPGestureAppModel(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    this.pkgName = paramString1;
    this.title = paramString2;
    this.ShortCutId = paramString3;
    this.uid = paramInt;
  }
  
  public Drawable getAppIcon()
  {
    return this.appIcon;
  }
  
  public String getPkgName()
  {
    return this.pkgName;
  }
  
  public String getShortCutId()
  {
    return this.ShortCutId;
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public int getUid()
  {
    return this.uid;
  }
  
  public void setAppIcon(Drawable paramDrawable)
  {
    this.appIcon = paramDrawable;
  }
  
  public void setPkgName(String paramString)
  {
    this.pkgName = paramString;
  }
  
  public void setShortCutId(String paramString)
  {
    this.ShortCutId = paramString;
  }
  
  public void setTitle(String paramString)
  {
    this.title = paramString;
  }
  
  public void setUid(int paramInt)
  {
    this.uid = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\gestures\OPGestureAppModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */