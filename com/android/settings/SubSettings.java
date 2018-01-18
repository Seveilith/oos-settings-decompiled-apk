package com.android.settings;

import android.util.Log;

public class SubSettings
  extends SettingsActivity
{
  protected boolean isValidFragment(String paramString)
  {
    Log.d("SubSettings", "Launching fragment " + paramString);
    return true;
  }
  
  public boolean onNavigateUp()
  {
    finish();
    return true;
  }
  
  public static class BluetoothSubSettings
    extends SubSettings
  {}
  
  public static class SavedAccessPointsSubSettings
    extends SubSettings
  {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SubSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */