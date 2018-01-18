package com.oneplus.settings.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.oneplus.settings.SettingsBaseApplication;

public class OPPrefUtil
{
  private static final String PREF_NAME = "OPSettingsPrefs";
  
  public static boolean getBoolean(String paramString, boolean paramBoolean)
  {
    return getSharedPreferences().getBoolean(paramString, paramBoolean);
  }
  
  public static int getInt(String paramString, int paramInt)
  {
    return getSharedPreferences().getInt(paramString, paramInt);
  }
  
  public static long getLong(String paramString, long paramLong)
  {
    return getSharedPreferences().getLong(paramString, paramLong);
  }
  
  private static SharedPreferences getSharedPreferences()
  {
    return SettingsBaseApplication.mApplication.getSharedPreferences("OPSettingsPrefs", 0);
  }
  
  private static SharedPreferences.Editor getSharedPreferencesEditor()
  {
    return SettingsBaseApplication.mApplication.getSharedPreferences("OPSettingsPrefs", 0).edit();
  }
  
  public static String getString(String paramString1, String paramString2)
  {
    return getSharedPreferences().getString(paramString1, paramString2);
  }
  
  public static void putBoolean(String paramString, boolean paramBoolean)
  {
    SharedPreferences.Editor localEditor = getSharedPreferencesEditor();
    localEditor.putBoolean(paramString, paramBoolean);
    localEditor.apply();
  }
  
  public static void putInt(String paramString, int paramInt)
  {
    SharedPreferences.Editor localEditor = getSharedPreferencesEditor();
    localEditor.putInt(paramString, paramInt);
    localEditor.apply();
  }
  
  public static void putLong(String paramString, long paramLong)
  {
    SharedPreferences.Editor localEditor = getSharedPreferencesEditor();
    localEditor.putLong(paramString, paramLong);
    localEditor.apply();
  }
  
  public static void putString(String paramString1, String paramString2)
  {
    SharedPreferences.Editor localEditor = getSharedPreferencesEditor();
    localEditor.putString(paramString1, paramString2);
    localEditor.apply();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\utils\OPPrefUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */