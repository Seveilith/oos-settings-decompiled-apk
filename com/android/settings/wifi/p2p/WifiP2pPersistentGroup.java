package com.android.settings.wifi.p2p;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pGroup;
import android.support.v7.preference.Preference;

public class WifiP2pPersistentGroup
  extends Preference
{
  public WifiP2pGroup mGroup;
  
  public WifiP2pPersistentGroup(Context paramContext, WifiP2pGroup paramWifiP2pGroup)
  {
    super(paramContext);
    this.mGroup = paramWifiP2pGroup;
    setTitle(this.mGroup.getNetworkName());
  }
  
  String getGroupName()
  {
    return this.mGroup.getNetworkName();
  }
  
  int getNetworkId()
  {
    return this.mGroup.getNetworkId();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\p2p\WifiP2pPersistentGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */