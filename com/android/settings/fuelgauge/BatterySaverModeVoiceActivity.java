package com.android.settings.fuelgauge;

import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import com.android.settings.utils.VoiceSettingsActivity;

public class BatterySaverModeVoiceActivity
  extends VoiceSettingsActivity
{
  private static final String TAG = "BatterySaverModeVoiceActivity";
  
  protected boolean onVoiceSettingInteraction(Intent paramIntent)
  {
    if (paramIntent.hasExtra("android.settings.extra.battery_saver_mode_enabled")) {
      if (((PowerManager)getSystemService("power")).setPowerSaveMode(paramIntent.getBooleanExtra("android.settings.extra.battery_saver_mode_enabled", false))) {
        notifySuccess(null);
      }
    }
    for (;;)
    {
      return true;
      Log.v("BatterySaverModeVoiceActivity", "Unable to set power mode");
      notifyFailure(null);
      continue;
      Log.v("BatterySaverModeVoiceActivity", "Missing battery saver mode extra");
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\BatterySaverModeVoiceActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */