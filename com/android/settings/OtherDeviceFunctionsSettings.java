package com.android.settings;

import android.os.Bundle;
import android.support.v7.preference.Preference;

public class OtherDeviceFunctionsSettings
  extends SettingsPreferenceFragment
{
  protected int getMetricsCategory()
  {
    return 110;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230821);
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    return super.onPreferenceTreeClick(paramPreference);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\OtherDeviceFunctionsSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */