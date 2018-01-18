package com.android.settings.wifi;

import android.content.Intent;
import android.support.v14.preference.PreferenceFragment;
import com.android.settings.ButtonBarHandler;
import com.android.settings.SettingsActivity;
import com.android.settings.wifi.p2p.WifiP2pSettings;

public class WifiPickerActivity
  extends SettingsActivity
  implements ButtonBarHandler
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    if (!localIntent.hasExtra(":settings:show_fragment"))
    {
      localIntent.putExtra(":settings:show_fragment", getWifiSettingsClass().getName());
      localIntent.putExtra(":settings:show_fragment_title_resid", 2131691337);
    }
    return localIntent;
  }
  
  Class<? extends PreferenceFragment> getWifiSettingsClass()
  {
    return WifiSettings.class;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return (WifiSettings.class.getName().equals(paramString)) || (WifiP2pSettings.class.getName().equals(paramString)) || (SavedAccessPointsWifiSettings.class.getName().equals(paramString)) || (AdvancedWifiSettings.class.getName().equals(paramString));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiPickerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */