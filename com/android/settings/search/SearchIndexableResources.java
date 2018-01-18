package com.android.settings.search;

import android.provider.SearchIndexableResource;
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
import com.android.settings.deviceinfo.StorageSettings;
import com.android.settings.display.ScreenZoomSettings;
import com.android.settings.fuelgauge.BatterySaverSettings;
import com.android.settings.fuelgauge.PowerUsageSummary;
import com.android.settings.inputmethod.InputMethodAndLanguageSettings;
import com.android.settings.location.LocationSettings;
import com.android.settings.location.ScanningSettings;
import com.android.settings.notification.ConfigureNotificationSettings;
import com.android.settings.notification.SoundSettings;
import com.android.settings.notification.ZenModePrioritySettings;
import com.android.settings.print.PrintSettingsFragment;
import com.android.settings.sim.SimSettings;
import com.android.settings.users.UserSettings;
import com.android.settings.wifi.AdvancedWifiSettings;
import com.android.settings.wifi.WifiSettings;
import com.oneplus.settings.OPApplicationsSettings;
import com.oneplus.settings.OPButtonsSettings;
import com.oneplus.settings.OPCredentialsManagementSettings;
import com.oneplus.settings.OPFontStyleSettings;
import com.oneplus.settings.OPGestureSettings;
import com.oneplus.settings.OPStatusBarCustomizeSettings;
import com.oneplus.settings.notification.OPNotificationAndNotdisturb;
import com.oneplus.settings.others.OPOthersSettings;
import java.util.Collection;
import java.util.HashMap;

public final class SearchIndexableResources
{
  public static int NO_DATA_RES_ID = 0;
  private static HashMap<String, SearchIndexableResource> sResMap = new HashMap();
  
  static
  {
    sResMap.put(WifiSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(WifiSettings.class.getName()), NO_DATA_RES_ID, WifiSettings.class.getName(), 2130838075));
    sResMap.put(AdvancedWifiSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(AdvancedWifiSettings.class.getName()), NO_DATA_RES_ID, AdvancedWifiSettings.class.getName(), 2130838075));
    sResMap.put(BluetoothSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(BluetoothSettings.class.getName()), NO_DATA_RES_ID, BluetoothSettings.class.getName(), 2130838037));
    sResMap.put(SimSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(SimSettings.class.getName()), NO_DATA_RES_ID, SimSettings.class.getName(), 2130838081));
    sResMap.put(WirelessSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(WirelessSettings.class.getName()), NO_DATA_RES_ID, WirelessSettings.class.getName(), 2130838056));
    sResMap.put(ScreenZoomSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(ScreenZoomSettings.class.getName()), NO_DATA_RES_ID, ScreenZoomSettings.class.getName(), 2130838049));
    sResMap.put(DisplaySettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(DisplaySettings.class.getName()), NO_DATA_RES_ID, DisplaySettings.class.getName(), 2130838049));
    sResMap.put(ConfigureNotificationSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(ConfigureNotificationSettings.class.getName()), 2131230746, ConfigureNotificationSettings.class.getName(), 2130838061));
    sResMap.put(SoundSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(SoundSettings.class.getName()), NO_DATA_RES_ID, SoundSettings.class.getName(), 2130838068));
    sResMap.put(ZenModePrioritySettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(ZenModePrioritySettings.class.getName()), NO_DATA_RES_ID, ZenModePrioritySettings.class.getName(), 2130838061));
    sResMap.put(StorageSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(StorageSettings.class.getName()), NO_DATA_RES_ID, StorageSettings.class.getName(), 2130838069));
    sResMap.put(PowerUsageSummary.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(PowerUsageSummary.class.getName()), 2131230827, PowerUsageSummary.class.getName(), 2130838035));
    sResMap.put(BatterySaverSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(BatterySaverSettings.class.getName()), 2131230740, BatterySaverSettings.class.getName(), 2130838035));
    sResMap.put(AdvancedAppSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(AdvancedAppSettings.class.getName()), NO_DATA_RES_ID, AdvancedAppSettings.class.getName(), 2130838193));
    sResMap.put(SpecialAccessSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(SpecialAccessSettings.class.getName()), 2131230862, SpecialAccessSettings.class.getName(), 2130838033));
    sResMap.put(UserSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(UserSettings.class.getName()), NO_DATA_RES_ID, UserSettings.class.getName(), 2130838057));
    sResMap.put(LocationSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(LocationSettings.class.getName()), 2131230777, LocationSettings.class.getName(), 2130838053));
    sResMap.put(ScanningSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(ScanningSettings.class.getName()), 2131230776, ScanningSettings.class.getName(), 2130838053));
    sResMap.put(SecuritySettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(SecuritySettings.class.getName()), NO_DATA_RES_ID, SecuritySettings.class.getName(), 2130838066));
    sResMap.put(ScreenPinningSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(ScreenPinningSettings.class.getName()), NO_DATA_RES_ID, ScreenPinningSettings.class.getName(), 2130838066));
    sResMap.put(AccountSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(AccountSettings.class.getName()), NO_DATA_RES_ID, AccountSettings.class.getName(), 2130838032));
    sResMap.put(InputMethodAndLanguageSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(InputMethodAndLanguageSettings.class.getName()), NO_DATA_RES_ID, InputMethodAndLanguageSettings.class.getName(), 2130838052));
    sResMap.put(PrivacySettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(PrivacySettings.class.getName()), NO_DATA_RES_ID, PrivacySettings.class.getName(), 2130838034));
    sResMap.put(DateTimeSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(DateTimeSettings.class.getName()), 2131230755, DateTimeSettings.class.getName(), 2130838046));
    sResMap.put(AccessibilitySettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(AccessibilitySettings.class.getName()), NO_DATA_RES_ID, AccessibilitySettings.class.getName(), 2130838031));
    sResMap.put(PrintSettingsFragment.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(PrintSettingsFragment.class.getName()), NO_DATA_RES_ID, PrintSettingsFragment.class.getName(), 2130838063));
    sResMap.put(DevelopmentSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(DevelopmentSettings.class.getName()), NO_DATA_RES_ID, DevelopmentSettings.class.getName(), 2130838047));
    sResMap.put(DeviceInfoSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(DeviceInfoSettings.class.getName()), NO_DATA_RES_ID, DeviceInfoSettings.class.getName(), 2130838030));
    sResMap.put(LegalSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(LegalSettings.class.getName()), NO_DATA_RES_ID, LegalSettings.class.getName(), 2130838030));
    sResMap.put(OPNotificationAndNotdisturb.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(OPNotificationAndNotdisturb.class.getName()), NO_DATA_RES_ID, OPNotificationAndNotdisturb.class.getName(), 2130837971));
    sResMap.put(OPCredentialsManagementSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(OPCredentialsManagementSettings.class.getName()), NO_DATA_RES_ID, OPCredentialsManagementSettings.class.getName(), 2130838066));
    sResMap.put(OPButtonsSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(OPButtonsSettings.class.getName()), NO_DATA_RES_ID, OPButtonsSettings.class.getName(), 2130838040));
    sResMap.put(OPGestureSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(OPGestureSettings.class.getName()), NO_DATA_RES_ID, OPGestureSettings.class.getName(), 2130838050));
    sResMap.put(OPOthersSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(OPOthersSettings.class.getName()), NO_DATA_RES_ID, OPOthersSettings.class.getName(), 2130838193));
    sResMap.put(OPStatusBarCustomizeSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(OPStatusBarCustomizeSettings.class.getName()), NO_DATA_RES_ID, OPStatusBarCustomizeSettings.class.getName(), 2130838224));
    sResMap.put(OPFontStyleSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(OPFontStyleSettings.class.getName()), NO_DATA_RES_ID, OPFontStyleSettings.class.getName(), 2130838224));
    sResMap.put(OPApplicationsSettings.class.getName(), new SearchIndexableResource(Ranking.getRankForClassName(OPApplicationsSettings.class.getName()), NO_DATA_RES_ID, OPApplicationsSettings.class.getName(), 2130838033));
  }
  
  public static SearchIndexableResource getResourceByName(String paramString)
  {
    return (SearchIndexableResource)sResMap.get(paramString);
  }
  
  public static int size()
  {
    return sResMap.size();
  }
  
  public static Collection<SearchIndexableResource> values()
  {
    return sResMap.values();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\search\SearchIndexableResources.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */