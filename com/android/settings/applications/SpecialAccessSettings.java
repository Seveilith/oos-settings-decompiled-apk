package com.android.settings.applications;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.utils.OPUtils;

public class SpecialAccessSettings
  extends SettingsPreferenceFragment
{
  private Preference mDataSaverPreference;
  
  protected int getMetricsCategory()
  {
    return 351;
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    addPreferencesFromResource(2131230862);
    this.mDataSaverPreference = ((PreferenceScreen)findPreference("data_saver"));
    if ((this.mDataSaverPreference != null) && (OPUtils.isGuestMode())) {
      getPreferenceScreen().removePreference(this.mDataSaverPreference);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\SpecialAccessSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */