package com.android.settings.location;

import android.support.v7.preference.PreferenceScreen;

public class LocationMode
  extends LocationSettingsBase
  implements RadioButtonPreference.OnClickListener
{
  private static final String KEY_BATTERY_SAVING = "battery_saving";
  private static final String KEY_HIGH_ACCURACY = "high_accuracy";
  private static final String KEY_SENSORS_ONLY = "sensors_only";
  private RadioButtonPreference mBatterySaving;
  private RadioButtonPreference mHighAccuracy;
  private RadioButtonPreference mSensorsOnly;
  
  private PreferenceScreen createPreferenceHierarchy()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null) {
      localPreferenceScreen.removeAll();
    }
    addPreferencesFromResource(2131230775);
    localPreferenceScreen = getPreferenceScreen();
    this.mHighAccuracy = ((RadioButtonPreference)localPreferenceScreen.findPreference("high_accuracy"));
    this.mBatterySaving = ((RadioButtonPreference)localPreferenceScreen.findPreference("battery_saving"));
    this.mSensorsOnly = ((RadioButtonPreference)localPreferenceScreen.findPreference("sensors_only"));
    this.mHighAccuracy.setOnClickListener(this);
    this.mBatterySaving.setOnClickListener(this);
    this.mSensorsOnly.setOnClickListener(this);
    refreshLocationMode();
    return localPreferenceScreen;
  }
  
  private void updateRadioButtons(RadioButtonPreference paramRadioButtonPreference)
  {
    if (paramRadioButtonPreference == null)
    {
      this.mHighAccuracy.setChecked(false);
      this.mBatterySaving.setChecked(false);
      this.mSensorsOnly.setChecked(false);
    }
    do
    {
      return;
      if (paramRadioButtonPreference == this.mHighAccuracy)
      {
        this.mHighAccuracy.setChecked(true);
        this.mBatterySaving.setChecked(false);
        this.mSensorsOnly.setChecked(false);
        return;
      }
      if (paramRadioButtonPreference == this.mBatterySaving)
      {
        this.mHighAccuracy.setChecked(false);
        this.mBatterySaving.setChecked(true);
        this.mSensorsOnly.setChecked(false);
        return;
      }
    } while (paramRadioButtonPreference != this.mSensorsOnly);
    this.mHighAccuracy.setChecked(false);
    this.mBatterySaving.setChecked(false);
    this.mSensorsOnly.setChecked(true);
  }
  
  public int getHelpResource()
  {
    return 2131693026;
  }
  
  protected int getMetricsCategory()
  {
    return 64;
  }
  
  public void onModeChanged(int paramInt, boolean paramBoolean)
  {
    switch (paramInt)
    {
    default: 
      if ((paramInt != 0) && (!paramBoolean)) {
        break;
      }
    }
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mHighAccuracy.setEnabled(paramBoolean);
      this.mBatterySaving.setEnabled(paramBoolean);
      this.mSensorsOnly.setEnabled(paramBoolean);
      return;
      updateRadioButtons(null);
      break;
      updateRadioButtons(this.mSensorsOnly);
      break;
      updateRadioButtons(this.mBatterySaving);
      break;
      updateRadioButtons(this.mHighAccuracy);
      break;
    }
  }
  
  public void onPause()
  {
    super.onPause();
  }
  
  public void onRadioButtonClicked(RadioButtonPreference paramRadioButtonPreference)
  {
    int i = 0;
    if (paramRadioButtonPreference == this.mHighAccuracy) {
      i = 3;
    }
    for (;;)
    {
      setLocationMode(i);
      return;
      if (paramRadioButtonPreference == this.mBatterySaving) {
        i = 2;
      } else if (paramRadioButtonPreference == this.mSensorsOnly) {
        i = 1;
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    createPreferenceHierarchy();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\location\LocationMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */