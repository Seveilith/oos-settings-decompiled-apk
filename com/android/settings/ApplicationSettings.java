package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;

public class ApplicationSettings
  extends SettingsPreferenceFragment
{
  private static final int APP_INSTALL_AUTO = 0;
  private static final String APP_INSTALL_AUTO_ID = "auto";
  private static final int APP_INSTALL_DEVICE = 1;
  private static final String APP_INSTALL_DEVICE_ID = "device";
  private static final int APP_INSTALL_SDCARD = 2;
  private static final String APP_INSTALL_SDCARD_ID = "sdcard";
  private static final String KEY_APP_INSTALL_LOCATION = "app_install_location";
  private static final String KEY_TOGGLE_ADVANCED_SETTINGS = "toggle_advanced_settings";
  private ListPreference mInstallLocation;
  private CheckBoxPreference mToggleAdvancedSettings;
  
  private String getAppInstallLocation()
  {
    int i = Settings.Global.getInt(getContentResolver(), "default_install_location", 0);
    if (i == 1) {
      return "device";
    }
    if (i == 2) {
      return "sdcard";
    }
    if (i == 0) {
      return "auto";
    }
    return "auto";
  }
  
  private boolean isAdvancedSettingsEnabled()
  {
    boolean bool = false;
    if (Settings.System.getInt(getContentResolver(), "advanced_settings", 0) > 0) {
      bool = true;
    }
    return bool;
  }
  
  private void setAdvancedSettingsEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(getContentResolver(), "advanced_settings", i);
      Intent localIntent = new Intent("android.intent.action.ADVANCED_SETTINGS");
      localIntent.putExtra("state", i);
      getActivity().sendBroadcast(localIntent);
      return;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 16;
  }
  
  protected void handleUpdateAppInstallLocation(String paramString)
  {
    if ("device".equals(paramString)) {
      Settings.Global.putInt(getContentResolver(), "default_install_location", 1);
    }
    for (;;)
    {
      this.mInstallLocation.setValue(paramString);
      return;
      if ("sdcard".equals(paramString)) {
        Settings.Global.putInt(getContentResolver(), "default_install_location", 2);
      } else if ("auto".equals(paramString)) {
        Settings.Global.putInt(getContentResolver(), "default_install_location", 0);
      } else {
        Settings.Global.putInt(getContentResolver(), "default_install_location", 0);
      }
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    int i = 0;
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230737);
    this.mToggleAdvancedSettings = ((CheckBoxPreference)findPreference("toggle_advanced_settings"));
    this.mToggleAdvancedSettings.setChecked(isAdvancedSettingsEnabled());
    getPreferenceScreen().removePreference(this.mToggleAdvancedSettings);
    this.mInstallLocation = ((ListPreference)findPreference("app_install_location"));
    if (Settings.Global.getInt(getContentResolver(), "set_install_location", 0) != 0) {
      i = 1;
    }
    if (i == 0)
    {
      getPreferenceScreen().removePreference(this.mInstallLocation);
      return;
    }
    this.mInstallLocation.setValue(getAppInstallLocation());
    this.mInstallLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        paramAnonymousPreference = (String)paramAnonymousObject;
        ApplicationSettings.this.handleUpdateAppInstallLocation(paramAnonymousPreference);
        return false;
      }
    });
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if (paramPreference == this.mToggleAdvancedSettings) {
      setAdvancedSettingsEnabled(this.mToggleAdvancedSettings.isChecked());
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ApplicationSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */