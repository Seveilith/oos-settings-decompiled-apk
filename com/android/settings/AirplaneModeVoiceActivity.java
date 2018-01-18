package com.android.settings;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import com.android.settings.utils.VoiceSettingsActivity;

public class AirplaneModeVoiceActivity
  extends VoiceSettingsActivity
{
  private static final String TAG = "AirplaneModeVoiceActivity";
  
  protected boolean onVoiceSettingInteraction(Intent paramIntent)
  {
    if (paramIntent.hasExtra("airplane_mode_enabled")) {
      ((ConnectivityManager)getSystemService("connectivity")).setAirplaneMode(paramIntent.getBooleanExtra("airplane_mode_enabled", false));
    }
    for (;;)
    {
      return true;
      Log.v("AirplaneModeVoiceActivity", "Missing airplane mode extra");
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\AirplaneModeVoiceActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */