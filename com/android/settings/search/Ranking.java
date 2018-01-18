package com.android.settings.search;

import com.android.settings.ChooseLockGeneric.ChooseLockGenericFragment;
import com.android.settings.DateTimeSettings;
import com.android.settings.DevelopmentSettings;
import com.android.settings.DeviceInfoSettings;
import com.android.settings.DisplaySettings;
import com.android.settings.LegalSettings;
import com.android.settings.PrivacySettings;
import com.android.settings.ScreenPinningSettings;
import com.android.settings.SecuritySettings;
import com.android.settings.WirelessSettings;
import com.android.settings.accessibility.AccessibilitySettings;
import com.android.settings.accounts.AccountSettings;
import com.android.settings.applications.AdvancedAppSettings;
import com.android.settings.applications.SpecialAccessSettings;
import com.android.settings.bluetooth.BluetoothSettings;
import com.android.settings.datausage.DataUsageMeteredSettings;
import com.android.settings.datausage.DataUsageSummary;
import com.android.settings.deviceinfo.StorageSettings;
import com.android.settings.display.ScreenZoomSettings;
import com.android.settings.fuelgauge.BatterySaverSettings;
import com.android.settings.fuelgauge.PowerUsageSummary;
import com.android.settings.inputmethod.InputMethodAndLanguageSettings;
import com.android.settings.location.LocationSettings;
import com.android.settings.location.ScanningSettings;
import com.android.settings.notification.ConfigureNotificationSettings;
import com.android.settings.notification.SoundSettings;
import com.android.settings.notification.ZenModeAutomationSettings;
import com.android.settings.notification.ZenModePrioritySettings;
import com.android.settings.notification.ZenModeSettings;
import com.android.settings.notification.ZenModeVisualInterruptionSettings;
import com.android.settings.print.PrintSettingsFragment;
import com.android.settings.sim.SimSettings;
import com.android.settings.users.UserSettings;
import com.android.settings.wifi.AdvancedWifiSettings;
import com.android.settings.wifi.SavedAccessPointsWifiSettings;
import com.android.settings.wifi.WifiSettings;
import com.oneplus.settings.OPApplicationsSettings;
import com.oneplus.settings.OPButtonsSettings;
import com.oneplus.settings.OPCredentialsManagementSettings;
import com.oneplus.settings.OPFontStyleSettings;
import com.oneplus.settings.OPGestureSettings;
import com.oneplus.settings.OPStatusBarCustomizeSettings;
import com.oneplus.settings.notification.OPNotificationAndNotdisturb;
import com.oneplus.settings.others.OPOthersSettings;
import java.util.HashMap;

public final class Ranking
{
  public static final int BASE_RANK_DEFAULT = 2048;
  public static final int RANK_ACCESSIBILITY = 20;
  public static final int RANK_ACCOUNT = 16;
  public static final int RANK_APPS = 10;
  public static final int RANK_BT = 2;
  public static final int RANK_DATA_USAGE = 4;
  public static final int RANK_DATE_TIME = 19;
  public static final int RANK_DEVELOPEMENT = 22;
  public static final int RANK_DEVICE_INFO = 23;
  public static final int RANK_DISPLAY = 6;
  public static final int RANK_IME = 17;
  public static final int RANK_LOCATION = 14;
  public static final int RANK_NOTIFICATIONS = 8;
  public static final int RANK_OPAPPLICATIONSETTINGS = 33;
  public static final int RANK_OPBUTTONSSETTINGS = 26;
  public static final int RANK_OPCREDENTIALSMANAGEMENTSETTINGS = 25;
  public static final int RANK_OPFONTSTYLESETTINGS = 32;
  public static final int RANK_OPGESTURESETTINGS = 27;
  public static final int RANK_OPNOTIFICATIONANDNOTDISTURB = 24;
  public static final int RANK_OPOTHERSSETTINGS = 28;
  public static final int RANK_OPSTATUSBARCUSTOMIZESETTINGS = 29;
  public static final int RANK_OTHERS = 1024;
  public static final int RANK_POWER_USAGE = 12;
  public static final int RANK_PRINTING = 21;
  public static final int RANK_PRIVACY = 18;
  public static final int RANK_SECURITY = 15;
  public static final int RANK_SIM = 3;
  public static final int RANK_SOUND = 9;
  public static final int RANK_STORAGE = 11;
  public static final int RANK_UNDEFINED = -1;
  public static final int RANK_USERS = 13;
  public static final int RANK_WALLPAPER = 7;
  public static final int RANK_WIFI = 1;
  public static final int RANK_WIRELESS = 5;
  private static HashMap<String, Integer> sBaseRankMap;
  public static int sCurrentBaseRank = 2048;
  private static HashMap<String, Integer> sRankMap = new HashMap();
  
  static
  {
    sBaseRankMap = new HashMap();
    sRankMap.put(WifiSettings.class.getName(), Integer.valueOf(1));
    sRankMap.put(AdvancedWifiSettings.class.getName(), Integer.valueOf(1));
    sRankMap.put(SavedAccessPointsWifiSettings.class.getName(), Integer.valueOf(1));
    sRankMap.put(BluetoothSettings.class.getName(), Integer.valueOf(2));
    sRankMap.put(SimSettings.class.getName(), Integer.valueOf(3));
    sRankMap.put(DataUsageSummary.class.getName(), Integer.valueOf(4));
    sRankMap.put(DataUsageMeteredSettings.class.getName(), Integer.valueOf(4));
    sRankMap.put(WirelessSettings.class.getName(), Integer.valueOf(5));
    sRankMap.put(DisplaySettings.class.getName(), Integer.valueOf(6));
    sRankMap.put(ScreenZoomSettings.class.getName(), Integer.valueOf(1));
    sRankMap.put(SoundSettings.class.getName(), Integer.valueOf(9));
    sRankMap.put(ConfigureNotificationSettings.class.getName(), Integer.valueOf(8));
    sRankMap.put(ZenModeSettings.class.getName(), Integer.valueOf(8));
    sRankMap.put(ZenModePrioritySettings.class.getName(), Integer.valueOf(8));
    sRankMap.put(ZenModeAutomationSettings.class.getName(), Integer.valueOf(8));
    sRankMap.put(ZenModeVisualInterruptionSettings.class.getName(), Integer.valueOf(8));
    sRankMap.put(StorageSettings.class.getName(), Integer.valueOf(11));
    sRankMap.put(PowerUsageSummary.class.getName(), Integer.valueOf(12));
    sRankMap.put(BatterySaverSettings.class.getName(), Integer.valueOf(12));
    sRankMap.put(AdvancedAppSettings.class.getName(), Integer.valueOf(10));
    sRankMap.put(SpecialAccessSettings.class.getName(), Integer.valueOf(10));
    sRankMap.put(UserSettings.class.getName(), Integer.valueOf(13));
    sRankMap.put(LocationSettings.class.getName(), Integer.valueOf(14));
    sRankMap.put(ScanningSettings.class.getName(), Integer.valueOf(14));
    sRankMap.put(SecuritySettings.class.getName(), Integer.valueOf(15));
    sRankMap.put(ChooseLockGeneric.ChooseLockGenericFragment.class.getName(), Integer.valueOf(15));
    sRankMap.put(ScreenPinningSettings.class.getName(), Integer.valueOf(15));
    sRankMap.put(AccountSettings.class.getName(), Integer.valueOf(16));
    sRankMap.put(InputMethodAndLanguageSettings.class.getName(), Integer.valueOf(17));
    sRankMap.put(PrivacySettings.class.getName(), Integer.valueOf(18));
    sRankMap.put(DateTimeSettings.class.getName(), Integer.valueOf(19));
    sRankMap.put(AccessibilitySettings.class.getName(), Integer.valueOf(20));
    sRankMap.put(PrintSettingsFragment.class.getName(), Integer.valueOf(21));
    sRankMap.put(DevelopmentSettings.class.getName(), Integer.valueOf(22));
    sRankMap.put(DeviceInfoSettings.class.getName(), Integer.valueOf(23));
    sRankMap.put(LegalSettings.class.getName(), Integer.valueOf(23));
    sBaseRankMap.put("com.android.settings", Integer.valueOf(0));
    sRankMap.put(OPNotificationAndNotdisturb.class.getName(), Integer.valueOf(24));
    sRankMap.put(OPCredentialsManagementSettings.class.getName(), Integer.valueOf(25));
    sRankMap.put(OPButtonsSettings.class.getName(), Integer.valueOf(26));
    sRankMap.put(OPGestureSettings.class.getName(), Integer.valueOf(27));
    sRankMap.put(OPOthersSettings.class.getName(), Integer.valueOf(28));
    sRankMap.put(OPStatusBarCustomizeSettings.class.getName(), Integer.valueOf(29));
    sRankMap.put(OPFontStyleSettings.class.getName(), Integer.valueOf(32));
    sRankMap.put(OPApplicationsSettings.class.getName(), Integer.valueOf(33));
  }
  
  public static int getBaseRankForAuthority(String paramString)
  {
    synchronized (sBaseRankMap)
    {
      Integer localInteger = (Integer)sBaseRankMap.get(paramString);
      if (localInteger != null)
      {
        i = localInteger.intValue();
        return i;
      }
      sCurrentBaseRank += 1;
      sBaseRankMap.put(paramString, Integer.valueOf(sCurrentBaseRank));
      int i = sCurrentBaseRank;
      return i;
    }
  }
  
  public static int getRankForClassName(String paramString)
  {
    paramString = (Integer)sRankMap.get(paramString);
    if (paramString != null) {
      return paramString.intValue();
    }
    return 1024;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\search\Ranking.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */