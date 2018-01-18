package com.android.settingslib;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.UserManager;
import com.android.settingslib.drawable.UserIconDrawable;
import com.android.settingslib.drawable.UserIcons;
import java.text.NumberFormat;

public class Utils
{
  private static String sPermissionControllerPackageName;
  private static String sServicesSystemSharedLibPackageName;
  private static String sSharedSystemSharedLibPackageName;
  private static Signature[] sSystemSignature;
  
  private static String formatPercentage(double paramDouble)
  {
    return NumberFormat.getPercentInstance().format(paramDouble);
  }
  
  public static String formatPercentage(int paramInt)
  {
    return formatPercentage(paramInt / 100.0D);
  }
  
  public static String formatPercentage(long paramLong1, long paramLong2)
  {
    return formatPercentage(paramLong1 / paramLong2);
  }
  
  public static int getBatteryLevel(Intent paramIntent)
  {
    return paramIntent.getIntExtra("level", 0) * 100 / paramIntent.getIntExtra("scale", 100);
  }
  
  public static String getBatteryStatus(Resources paramResources, Intent paramIntent)
  {
    return getBatteryStatus(paramResources, paramIntent, false);
  }
  
  public static String getBatteryStatus(Resources paramResources, Intent paramIntent, boolean paramBoolean)
  {
    int i = paramIntent.getIntExtra("plugged", 0);
    int j = paramIntent.getIntExtra("status", 1);
    if (j == 2)
    {
      if (i == 1) {
        if (paramBoolean) {
          i = R.string.battery_info_status_charging_ac_short;
        }
      }
      for (;;)
      {
        return paramResources.getString(i);
        i = R.string.battery_info_status_charging_ac;
        continue;
        if (i == 2)
        {
          if (paramBoolean) {
            i = R.string.battery_info_status_charging_usb_short;
          } else {
            i = R.string.battery_info_status_charging_usb;
          }
        }
        else if (i == 4)
        {
          if (paramBoolean) {
            i = R.string.battery_info_status_charging_wireless_short;
          } else {
            i = R.string.battery_info_status_charging_wireless;
          }
        }
        else {
          i = R.string.battery_info_status_charging;
        }
      }
    }
    if (j == 3) {
      return paramResources.getString(R.string.battery_info_status_discharging);
    }
    if (j == 4) {
      return paramResources.getString(R.string.battery_info_status_not_charging);
    }
    if (j == 5) {
      return paramResources.getString(R.string.battery_info_status_full);
    }
    return paramResources.getString(R.string.battery_info_status_unknown);
  }
  
  public static int getColorAccent(Context paramContext)
  {
    paramContext = paramContext.obtainStyledAttributes(new int[] { 16843829 });
    int i = paramContext.getColor(0, 0);
    paramContext.recycle();
    return i;
  }
  
  private static Signature getFirstSignature(PackageInfo paramPackageInfo)
  {
    if ((paramPackageInfo != null) && (paramPackageInfo.signatures != null) && (paramPackageInfo.signatures.length > 0)) {
      return paramPackageInfo.signatures[0];
    }
    return null;
  }
  
  private static Signature getSystemSignature(PackageManager paramPackageManager)
  {
    try
    {
      paramPackageManager = getFirstSignature(paramPackageManager.getPackageInfo("android", 64));
      return paramPackageManager;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager) {}
    return null;
  }
  
  public static int getTetheringLabel(ConnectivityManager paramConnectivityManager)
  {
    String[] arrayOfString1 = paramConnectivityManager.getTetherableUsbRegexs();
    String[] arrayOfString2 = paramConnectivityManager.getTetherableWifiRegexs();
    paramConnectivityManager = paramConnectivityManager.getTetherableBluetoothRegexs();
    int i;
    int j;
    if (arrayOfString1.length != 0)
    {
      i = 1;
      if (arrayOfString2.length == 0) {
        break label61;
      }
      j = 1;
      label33:
      if (paramConnectivityManager.length == 0) {
        break label66;
      }
    }
    label61:
    label66:
    for (int k = 1;; k = 0)
    {
      if ((j == 0) || (i == 0) || (k == 0)) {
        break label71;
      }
      return R.string.tether_settings_title_all;
      i = 0;
      break;
      j = 0;
      break label33;
    }
    label71:
    if ((j != 0) && (i != 0)) {
      return R.string.tether_settings_title_all;
    }
    if ((j != 0) && (k != 0)) {
      return R.string.tether_settings_title_all;
    }
    if (j != 0) {
      return R.string.tether_settings_title_wifi;
    }
    if ((i != 0) && (k != 0)) {
      return R.string.tether_settings_title_usb_bluetooth;
    }
    if (i != 0) {
      return R.string.tether_settings_title_usb;
    }
    return R.string.tether_settings_title_bluetooth;
  }
  
  public static UserIconDrawable getUserIcon(Context paramContext, UserManager paramUserManager, UserInfo paramUserInfo)
  {
    int i = UserIconDrawable.getSizeForList(paramContext);
    if (paramUserInfo.isManagedProfile())
    {
      paramContext = BitmapFactory.decodeResource(paramContext.getResources(), 17302314);
      return new UserIconDrawable(i).setIcon(paramContext).bake();
    }
    if (paramUserInfo.iconPath != null)
    {
      paramContext = paramUserManager.getUserIcon(paramUserInfo.id);
      if (paramContext != null) {
        return new UserIconDrawable(i).setIcon(paramContext).bake();
      }
    }
    return new UserIconDrawable(i).setIconDrawable(UserIcons.getDefaultUserIcon(paramUserInfo.id, false)).bake();
  }
  
  public static String getUserLabel(Context paramContext, UserInfo paramUserInfo)
  {
    if (paramUserInfo != null) {}
    for (String str = paramUserInfo.name; paramUserInfo.isManagedProfile(); str = null) {
      return paramContext.getString(R.string.managed_user_title);
    }
    if (paramUserInfo.isGuest()) {
      str = paramContext.getString(R.string.user_guest);
    }
    if ((str == null) && (paramUserInfo != null)) {
      str = Integer.toString(paramUserInfo.id);
    }
    for (;;)
    {
      return paramContext.getResources().getString(R.string.running_process_item_user_label, new Object[] { str });
      if (paramUserInfo == null) {
        str = paramContext.getString(R.string.unknown);
      }
    }
  }
  
  public static boolean isDeviceProvisioningPackage(Resources paramResources, String paramString)
  {
    paramResources = paramResources.getString(17039486);
    if (paramResources != null) {
      return paramResources.equals(paramString);
    }
    return false;
  }
  
  public static boolean isSystemPackage(Resources paramResources, PackageManager paramPackageManager, PackageInfo paramPackageInfo)
  {
    boolean bool2 = true;
    if (sSystemSignature == null) {
      sSystemSignature = new Signature[] { getSystemSignature(paramPackageManager) };
    }
    if (sPermissionControllerPackageName == null) {
      sPermissionControllerPackageName = paramPackageManager.getPermissionControllerPackageName();
    }
    if (sServicesSystemSharedLibPackageName == null) {
      sServicesSystemSharedLibPackageName = paramPackageManager.getServicesSystemSharedLibraryPackageName();
    }
    if (sSharedSystemSharedLibPackageName == null) {
      sSharedSystemSharedLibPackageName = paramPackageManager.getSharedSystemSharedLibraryPackageName();
    }
    boolean bool1;
    if (sSystemSignature[0] != null)
    {
      bool1 = bool2;
      if (sSystemSignature[0].equals(getFirstSignature(paramPackageInfo))) {}
    }
    else
    {
      bool1 = bool2;
      if (!paramPackageInfo.packageName.equals(sPermissionControllerPackageName))
      {
        bool1 = bool2;
        if (!paramPackageInfo.packageName.equals(sServicesSystemSharedLibPackageName))
        {
          bool1 = bool2;
          if (!paramPackageInfo.packageName.equals(sSharedSystemSharedLibPackageName))
          {
            bool1 = bool2;
            if (!paramPackageInfo.packageName.equals("com.android.printspooler")) {
              bool1 = isDeviceProvisioningPackage(paramResources, paramPackageInfo.packageName);
            }
          }
        }
      }
    }
    return bool1;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */