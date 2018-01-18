package com.android.settings.wifi;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;
import java.util.List;

public class WifiConfigInfo
  extends Activity
{
  private TextView mConfigList;
  private WifiManager mWifiManager;
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    setContentView(2130969113);
    this.mConfigList = ((TextView)findViewById(2131362729));
  }
  
  protected void onResume()
  {
    super.onResume();
    if (this.mWifiManager.isWifiEnabled())
    {
      List localList = this.mWifiManager.getConfiguredNetworks();
      StringBuffer localStringBuffer = new StringBuffer();
      int i = localList.size() - 1;
      while (i >= 0)
      {
        localStringBuffer.append(localList.get(i));
        i -= 1;
      }
      this.mConfigList.setText(localStringBuffer);
      return;
    }
    this.mConfigList.setText(2131691529);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiConfigInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */