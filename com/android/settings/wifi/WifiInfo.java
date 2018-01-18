package com.android.settings.wifi;

import android.os.Bundle;
import com.android.settings.SettingsPreferenceFragment;

public class WifiInfo
  extends SettingsPreferenceFragment
{
  protected int getMetricsCategory()
  {
    return 89;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230867);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */