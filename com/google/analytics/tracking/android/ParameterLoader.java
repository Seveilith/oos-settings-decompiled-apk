package com.google.analytics.tracking.android;

abstract interface ParameterLoader
{
  public abstract boolean getBoolean(String paramString);
  
  public abstract Double getDoubleFromString(String paramString);
  
  public abstract int getInt(String paramString, int paramInt);
  
  public abstract String getString(String paramString);
  
  public abstract boolean isBooleanKeyPresent(String paramString);
  
  public abstract void setResourcePackageName(String paramString);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\ParameterLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */