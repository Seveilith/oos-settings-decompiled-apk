package com.oneplus.settings.highpowerapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtils
{
  public static final String TAG = "PackageUtils";
  
  private PackageUtils()
  {
    throw new AssertionError();
  }
  
  public static boolean isSystemApplication(Context paramContext)
  {
    if (paramContext == null) {
      return false;
    }
    return isSystemApplication(paramContext, paramContext.getPackageName());
  }
  
  public static boolean isSystemApplication(Context paramContext, String paramString)
  {
    if (paramContext == null) {
      return false;
    }
    return isSystemApplication(paramContext.getPackageManager(), paramString);
  }
  
  public static boolean isSystemApplication(PackageManager paramPackageManager, String paramString)
  {
    boolean bool2 = false;
    if ((paramPackageManager == null) || (paramString == null)) {}
    while (paramString.length() == 0) {
      return false;
    }
    try
    {
      paramPackageManager = paramPackageManager.getApplicationInfo(paramString, 0);
      boolean bool1 = bool2;
      if (paramPackageManager != null)
      {
        int i = paramPackageManager.flags;
        bool1 = bool2;
        if ((i & 0x1) > 0) {
          bool1 = true;
        }
      }
      return bool1;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager)
    {
      paramPackageManager.printStackTrace();
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\highpowerapp\PackageUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */