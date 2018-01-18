package com.google.analytics.tracking.android;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

class ParameterLoaderImpl
  implements ParameterLoader
{
  private final Context mContext;
  private String mOverrideResourcePackageName;
  
  public ParameterLoaderImpl(Context paramContext)
  {
    if (paramContext != null)
    {
      this.mContext = paramContext.getApplicationContext();
      return;
    }
    throw new NullPointerException("Context cannot be null");
  }
  
  private int getResourceIdForType(String paramString1, String paramString2)
  {
    if (this.mContext != null) {
      if (this.mOverrideResourcePackageName != null) {
        break label38;
      }
    }
    label38:
    for (String str = this.mContext.getPackageName();; str = this.mOverrideResourcePackageName)
    {
      return this.mContext.getResources().getIdentifier(paramString1, paramString2, str);
      return 0;
    }
  }
  
  public boolean getBoolean(String paramString)
  {
    int i = getResourceIdForType(paramString, "bool");
    if (i != 0) {
      return "true".equalsIgnoreCase(this.mContext.getString(i));
    }
    return false;
  }
  
  public Double getDoubleFromString(String paramString)
  {
    paramString = getString(paramString);
    if (!TextUtils.isEmpty(paramString)) {}
    try
    {
      double d = Double.parseDouble(paramString);
      return Double.valueOf(d);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.w("NumberFormatException parsing " + paramString);
    }
    return null;
    return null;
  }
  
  public int getInt(String paramString, int paramInt)
  {
    int i = getResourceIdForType(paramString, "integer");
    if (i != 0) {}
    try
    {
      int j = Integer.parseInt(this.mContext.getString(i));
      return j;
    }
    catch (NumberFormatException paramString)
    {
      Log.w("NumberFormatException parsing " + this.mContext.getString(i));
    }
    return paramInt;
    return paramInt;
  }
  
  public String getString(String paramString)
  {
    int i = getResourceIdForType(paramString, "string");
    if (i != 0) {
      return this.mContext.getString(i);
    }
    return null;
  }
  
  public boolean isBooleanKeyPresent(String paramString)
  {
    return getResourceIdForType(paramString, "bool") != 0;
  }
  
  public void setResourcePackageName(String paramString)
  {
    this.mOverrideResourcePackageName = paramString;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\ParameterLoaderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */