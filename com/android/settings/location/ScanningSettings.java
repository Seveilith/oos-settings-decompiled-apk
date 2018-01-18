package com.android.settings.location;

import android.content.ContentResolver;
import android.provider.Settings.Global;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;

public class ScanningSettings
  extends SettingsPreferenceFragment
{
  private static final String KEY_BLUETOOTH_SCAN_ALWAYS_AVAILABLE = "bluetooth_always_scanning";
  private static final String KEY_WIFI_SCAN_ALWAYS_AVAILABLE = "wifi_always_scanning";
  
  private PreferenceScreen createPreferenceHierarchy()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null) {
      localPreferenceScreen.removeAll();
    }
    addPreferencesFromResource(2131230776);
    localPreferenceScreen = getPreferenceScreen();
    initPreferences();
    return localPreferenceScreen;
  }
  
  private void initPreferences()
  {
    boolean bool2 = true;
    SwitchPreference localSwitchPreference = (SwitchPreference)findPreference("wifi_always_scanning");
    if (Settings.Global.getInt(getContentResolver(), "wifi_scan_always_enabled", 0) == 1)
    {
      bool1 = true;
      localSwitchPreference.setChecked(bool1);
      localSwitchPreference = (SwitchPreference)findPreference("bluetooth_always_scanning");
      if (Settings.Global.getInt(getContentResolver(), "ble_scan_always_enabled", 0) != 1) {
        break label70;
      }
    }
    label70:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      localSwitchPreference.setChecked(bool1);
      return;
      bool1 = false;
      break;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 131;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    int j = 0;
    int i = 0;
    Object localObject = paramPreference.getKey();
    if ("wifi_always_scanning".equals(localObject))
    {
      localObject = getContentResolver();
      if (((SwitchPreference)paramPreference).isChecked()) {
        i = 1;
      }
      Settings.Global.putInt((ContentResolver)localObject, "wifi_scan_always_enabled", i);
      return true;
    }
    if ("bluetooth_always_scanning".equals(localObject))
    {
      localObject = getContentResolver();
      i = j;
      if (((SwitchPreference)paramPreference).isChecked()) {
        i = 1;
      }
      Settings.Global.putInt((ContentResolver)localObject, "ble_scan_always_enabled", i);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onResume()
  {
    super.onResume();
    createPreferenceHierarchy();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\location\ScanningSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */