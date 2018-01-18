package com.oneplus.settings.defaultapp;

import android.content.Context;
import android.provider.Settings.System;

public class DataHelper
{
  public static final String DEFAULT_APP_INIT = "op_default_app_init";
  
  public static String getDefaultAppPackageName(Context paramContext, String paramString)
  {
    return Settings.System.getString(paramContext.getContentResolver(), paramString);
  }
  
  public static boolean isDefaultAppInited(Context paramContext)
  {
    boolean bool = false;
    if (Settings.System.getInt(paramContext.getContentResolver(), "op_default_app_init", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static void setDefaultAppInited(Context paramContext)
  {
    Settings.System.putInt(paramContext.getContentResolver(), "op_default_app_init", 1);
  }
  
  public static void setDefaultAppPackageName(Context paramContext, String paramString1, String paramString2)
  {
    Settings.System.putString(paramContext.getContentResolver(), paramString1, paramString2);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\defaultapp\DataHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */