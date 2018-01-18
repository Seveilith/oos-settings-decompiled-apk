package com.android.settings;

import android.os.Bundle;
import android.os.UserManager;
import android.support.v7.preference.PreferenceScreen;

public class TestingSettings
  extends SettingsPreferenceFragment
{
  protected int getMetricsCategory()
  {
    return 89;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230866);
    if (!UserManager.get(getContext()).isAdminUser())
    {
      paramBundle = (PreferenceScreen)findPreference("radio_info_settings");
      getPreferenceScreen().removePreference(paramBundle);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\TestingSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */