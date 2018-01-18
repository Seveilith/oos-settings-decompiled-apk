package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class HotspotOffReceiver
  extends BroadcastReceiver
{
  private static final boolean DEBUG = Log.isLoggable("HotspotOffReceiver", 3);
  private static final String TAG = "HotspotOffReceiver";
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(paramIntent.getAction())) && (((WifiManager)paramContext.getSystemService("wifi")).getWifiApState() == 11))
    {
      if (DEBUG) {
        Log.d("HotspotOffReceiver", "TetherService.cancelRecheckAlarmIfNecessary called");
      }
      TetherService.cancelRecheckAlarmIfNecessary(paramContext, 0);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\HotspotOffReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */