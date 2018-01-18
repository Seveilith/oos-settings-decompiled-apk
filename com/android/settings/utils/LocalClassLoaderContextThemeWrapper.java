package com.android.settings.utils;

import android.content.Context;
import android.view.ContextThemeWrapper;

public class LocalClassLoaderContextThemeWrapper
  extends ContextThemeWrapper
{
  private Class mLocalClass;
  
  public LocalClassLoaderContextThemeWrapper(Class paramClass, Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
    this.mLocalClass = paramClass;
  }
  
  public ClassLoader getClassLoader()
  {
    return this.mLocalClass.getClassLoader();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\utils\LocalClassLoaderContextThemeWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */