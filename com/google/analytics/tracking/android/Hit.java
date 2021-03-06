package com.google.analytics.tracking.android;

import android.text.TextUtils;

class Hit
{
  private final long mHitId;
  private String mHitString;
  private final long mHitTime;
  private String mHitUrlScheme = "https:";
  
  Hit(String paramString, long paramLong1, long paramLong2)
  {
    this.mHitString = paramString;
    this.mHitId = paramLong1;
    this.mHitTime = paramLong2;
  }
  
  long getHitId()
  {
    return this.mHitId;
  }
  
  String getHitParams()
  {
    return this.mHitString;
  }
  
  long getHitTime()
  {
    return this.mHitTime;
  }
  
  String getHitUrlScheme()
  {
    return this.mHitUrlScheme;
  }
  
  void setHitString(String paramString)
  {
    this.mHitString = paramString;
  }
  
  void setHitUrl(String paramString)
  {
    if (paramString == null) {}
    while (TextUtils.isEmpty(paramString.trim())) {
      return;
    }
    if (!paramString.toLowerCase().startsWith("http:")) {
      return;
    }
    this.mHitUrlScheme = "http:";
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\Hit.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */