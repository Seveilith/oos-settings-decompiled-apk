package com.android.settings.location;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;

public abstract class LocationSettingsBase
  extends SettingsPreferenceFragment
{
  private static final String CURRENT_MODE_KEY = "CURRENT_MODE";
  private static final String MODE_CHANGING_ACTION = "com.android.settings.location.MODE_CHANGING";
  private static final String NEW_MODE_KEY = "NEW_MODE";
  private static final String TAG = "LocationSettingsBase";
  private boolean mActive = false;
  private int mCurrentMode;
  private BroadcastReceiver mReceiver;
  
  private boolean isRestricted()
  {
    return ((UserManager)getActivity().getSystemService("user")).hasUserRestriction("no_share_location");
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (Log.isLoggable("LocationSettingsBase", 3)) {
          Log.d("LocationSettingsBase", "Received location mode change intent: " + paramAnonymousIntent);
        }
        LocationSettingsBase.this.refreshLocationMode();
      }
    };
  }
  
  public abstract void onModeChanged(int paramInt, boolean paramBoolean);
  
  public void onPause()
  {
    try
    {
      getActivity().unregisterReceiver(this.mReceiver);
      super.onPause();
      this.mActive = false;
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;) {}
    }
  }
  
  public void onResume()
  {
    super.onResume();
    this.mActive = true;
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.location.MODE_CHANGED");
    getActivity().registerReceiver(this.mReceiver, localIntentFilter);
  }
  
  public void refreshLocationMode()
  {
    if (this.mActive)
    {
      int i = Settings.Secure.getInt(getContentResolver(), "location_mode", 0);
      this.mCurrentMode = i;
      if (Log.isLoggable("LocationSettingsBase", 4)) {
        Log.i("LocationSettingsBase", "Location mode has been changed");
      }
      onModeChanged(i, isRestricted());
    }
  }
  
  public void setLocationMode(int paramInt)
  {
    if (isRestricted())
    {
      if (Log.isLoggable("LocationSettingsBase", 4)) {
        Log.i("LocationSettingsBase", "Restricted user, not setting location mode");
      }
      paramInt = Settings.Secure.getInt(getContentResolver(), "location_mode", 0);
      if (this.mActive) {
        onModeChanged(paramInt, true);
      }
      return;
    }
    Intent localIntent = new Intent("com.android.settings.location.MODE_CHANGING");
    localIntent.putExtra("CURRENT_MODE", this.mCurrentMode);
    localIntent.putExtra("NEW_MODE", paramInt);
    getActivity().sendBroadcast(localIntent, "android.permission.WRITE_SECURE_SETTINGS");
    Settings.Secure.putInt(getContentResolver(), "location_mode", paramInt);
    refreshLocationMode();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\location\LocationSettingsBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */