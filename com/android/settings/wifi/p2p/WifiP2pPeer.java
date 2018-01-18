package com.android.settings.wifi.p2p;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.widget.ImageView;

public class WifiP2pPeer
  extends Preference
{
  private static final int SIGNAL_LEVELS = 4;
  private static final int[] STATE_SECURED = { 2130771981 };
  public WifiP2pDevice device;
  private final int mRssi;
  private ImageView mSignal;
  
  public WifiP2pPeer(Context paramContext, WifiP2pDevice paramWifiP2pDevice)
  {
    super(paramContext);
    this.device = paramWifiP2pDevice;
    setWidgetLayoutResource(2130968936);
    this.mRssi = 60;
    if (TextUtils.isEmpty(this.device.deviceName)) {
      setTitle(this.device.deviceAddress);
    }
    for (;;)
    {
      setSummary(paramContext.getResources().getStringArray(2131427391)[this.device.status]);
      return;
      setTitle(this.device.deviceName);
    }
  }
  
  public int compareTo(Preference paramPreference)
  {
    int i = 1;
    if (!(paramPreference instanceof WifiP2pPeer)) {
      return 1;
    }
    paramPreference = (WifiP2pPeer)paramPreference;
    if (this.device.status != paramPreference.device.status)
    {
      if (this.device.status < paramPreference.device.status) {
        i = -1;
      }
      return i;
    }
    if (this.device.deviceName != null) {
      return this.device.deviceName.compareToIgnoreCase(paramPreference.device.deviceName);
    }
    return this.device.deviceAddress.compareToIgnoreCase(paramPreference.device.deviceAddress);
  }
  
  int getLevel()
  {
    if (this.mRssi == Integer.MAX_VALUE) {
      return -1;
    }
    return WifiManager.calculateSignalLevel(this.mRssi, 4);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mSignal = ((ImageView)paramPreferenceViewHolder.findViewById(2131362458));
    if (this.mRssi == Integer.MAX_VALUE) {
      this.mSignal.setImageDrawable(null);
    }
    for (;;)
    {
      this.mSignal.setImageLevel(getLevel());
      return;
      this.mSignal.setImageResource(2130838552);
      this.mSignal.setImageState(STATE_SECURED, true);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\p2p\WifiP2pPeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */