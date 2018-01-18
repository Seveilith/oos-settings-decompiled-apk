package com.android.settingslib.wifi;

import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import java.util.List;

public class WifiStatusTracker
{
  public boolean connected;
  public boolean enabled;
  public int level;
  private final WifiManager mWifiManager;
  public int rssi;
  public String ssid;
  
  public WifiStatusTracker(WifiManager paramWifiManager)
  {
    this.mWifiManager = paramWifiManager;
  }
  
  private String getSsid(WifiInfo paramWifiInfo)
  {
    Object localObject = paramWifiInfo.getSSID();
    if (localObject != null) {
      return (String)localObject;
    }
    localObject = this.mWifiManager.getConfiguredNetworks();
    int j = ((List)localObject).size();
    int i = 0;
    while (i < j)
    {
      if (((WifiConfiguration)((List)localObject).get(i)).networkId == paramWifiInfo.getNetworkId()) {
        return ((WifiConfiguration)((List)localObject).get(i)).SSID;
      }
      i += 1;
    }
    return null;
  }
  
  public void handleBroadcast(Intent paramIntent)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    Object localObject = paramIntent.getAction();
    if (((String)localObject).equals("android.net.wifi.WIFI_STATE_CHANGED"))
    {
      if (paramIntent.getIntExtra("wifi_state", 4) == 3) {
        bool1 = true;
      }
      this.enabled = bool1;
    }
    label160:
    do
    {
      do
      {
        return;
        if (!((String)localObject).equals("android.net.wifi.STATE_CHANGE")) {
          break;
        }
        localObject = (NetworkInfo)paramIntent.getParcelableExtra("networkInfo");
        bool1 = bool2;
        if (localObject != null) {
          bool1 = ((NetworkInfo)localObject).isConnected();
        }
        this.connected = bool1;
        if (this.connected)
        {
          if (paramIntent.getParcelableExtra("wifiInfo") != null) {}
          for (paramIntent = (WifiInfo)paramIntent.getParcelableExtra("wifiInfo");; paramIntent = this.mWifiManager.getConnectionInfo())
          {
            if (paramIntent == null) {
              break label160;
            }
            this.ssid = getSsid(paramIntent);
            if (!Build.DEBUG_ONEPLUS) {
              break;
            }
            Log.i("WifiStatusTracker", "" + paramIntent);
            return;
          }
          this.ssid = null;
          return;
        }
      } while (this.connected);
      this.ssid = null;
      return;
    } while (!((String)localObject).equals("android.net.wifi.RSSI_CHANGED"));
    this.rssi = paramIntent.getIntExtra("newRssi", 65336);
    this.level = WifiManager.calculateSignalLevel(this.rssi, 5);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\wifi\WifiStatusTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */