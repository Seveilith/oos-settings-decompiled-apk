package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings.Global;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import com.android.settings.HotspotPreference;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settingslib.TetherUtil;
import java.util.ArrayList;

public class WifiApEnabler
{
  private static final String ACTION_EXTRA = "choice";
  private static final String ACTION_HOTSPOT_POST_CONFIGURE = "Hotspot_PostConfigure";
  public static final int TETHERING_WIFI = 0;
  ConnectivityManager mCm;
  private final Context mContext;
  private final DataSaverBackend mDataSaverBackend;
  private boolean mEnabling = false;
  private final IntentFilter mIntentFilter;
  private final CharSequence mOriginalSummary;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      int i;
      if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(paramAnonymousContext))
      {
        i = paramAnonymousIntent.getIntExtra("wifi_state", 14);
        if (i == 14)
        {
          int j = paramAnonymousIntent.getIntExtra("wifi_ap_error_code", 0);
          WifiApEnabler.-wrap1(WifiApEnabler.this, i, j);
        }
      }
      do
      {
        return;
        WifiApEnabler.-wrap1(WifiApEnabler.this, i, 0);
        return;
        if ("android.net.conn.TETHER_STATE_CHANGED".equals(paramAnonymousContext))
        {
          paramAnonymousContext = paramAnonymousIntent.getStringArrayListExtra("availableArray");
          ArrayList localArrayList = paramAnonymousIntent.getStringArrayListExtra("activeArray");
          paramAnonymousIntent = paramAnonymousIntent.getStringArrayListExtra("erroredArray");
          WifiApEnabler.-wrap2(WifiApEnabler.this, paramAnonymousContext.toArray(), localArrayList.toArray(), paramAnonymousIntent.toArray());
          return;
        }
      } while (!"android.intent.action.AIRPLANE_MODE".equals(paramAnonymousContext));
      WifiApEnabler.-wrap0(WifiApEnabler.this);
    }
  };
  private final Preference mSwitch;
  private WifiManager mWifiManager;
  private String[] mWifiRegexs;
  
  public WifiApEnabler(Context paramContext, DataSaverBackend paramDataSaverBackend, Preference paramPreference)
  {
    this.mContext = paramContext;
    this.mDataSaverBackend = paramDataSaverBackend;
    this.mSwitch = paramPreference;
    this.mOriginalSummary = paramPreference.getSummary();
    paramPreference.setPersistent(false);
    this.mWifiManager = ((WifiManager)paramContext.getSystemService("wifi"));
    this.mCm = ((ConnectivityManager)this.mContext.getSystemService("connectivity"));
    this.mWifiRegexs = this.mCm.getTetherableWifiRegexs();
    this.mIntentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
    this.mIntentFilter.addAction("android.net.conn.TETHER_STATE_CHANGED");
    this.mIntentFilter.addAction("android.intent.action.AIRPLANE_MODE");
  }
  
  private void enableWifiSwitch()
  {
    boolean bool = false;
    int i;
    Preference localPreference;
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 0)
    {
      i = 1;
      if (i != 0) {
        break label55;
      }
      localPreference = this.mSwitch;
      if (!this.mDataSaverBackend.isDataSaverEnabled()) {
        break label50;
      }
    }
    for (;;)
    {
      localPreference.setEnabled(bool);
      return;
      i = 0;
      break;
      label50:
      bool = true;
    }
    label55:
    this.mSwitch.setSummary(this.mOriginalSummary);
    this.mSwitch.setEnabled(false);
  }
  
  private void handleWifiApStateChanged(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    if (this.mContext.getResources().getBoolean(2131558431))
    {
      localObject = (HotspotPreference)this.mSwitch;
      switch (paramInt1)
      {
      default: 
        ((HotspotPreference)localObject).setChecked(false);
        if (paramInt2 == 1) {
          ((HotspotPreference)localObject).setSummary(2131691341);
        }
        break;
      }
      for (;;)
      {
        enableWifiSwitch();
        return;
        ((HotspotPreference)localObject).setSummary(2131691498);
        ((HotspotPreference)localObject).setEnabled(false);
        return;
        postTurnOn(this.mContext, 0);
        ((HotspotPreference)localObject).setChecked(true);
        if (this.mDataSaverBackend.isDataSaverEnabled()) {}
        for (;;)
        {
          ((HotspotPreference)localObject).setEnabled(bool);
          return;
          bool = true;
        }
        ((HotspotPreference)localObject).setSummary(2131691499);
        ((HotspotPreference)localObject).setChecked(false);
        ((HotspotPreference)localObject).setEnabled(false);
        return;
        ((HotspotPreference)localObject).setChecked(false);
        ((HotspotPreference)localObject).setSummary(this.mOriginalSummary);
        enableWifiSwitch();
        return;
        ((HotspotPreference)localObject).setSummary(2131691340);
      }
    }
    Object localObject = (SwitchPreference)this.mSwitch;
    switch (paramInt1)
    {
    default: 
      ((SwitchPreference)localObject).setChecked(false);
      if (paramInt2 == 1) {
        ((SwitchPreference)localObject).setSummary(2131691341);
      }
      break;
    }
    for (;;)
    {
      enableWifiSwitch();
      return;
      ((SwitchPreference)localObject).setSummary(2131691498);
      ((SwitchPreference)localObject).setEnabled(false);
      return;
      ((SwitchPreference)localObject).setChecked(true);
      return;
      ((SwitchPreference)localObject).setSummary(2131691499);
      ((SwitchPreference)localObject).setChecked(false);
      ((SwitchPreference)localObject).setEnabled(false);
      return;
      ((SwitchPreference)localObject).setChecked(false);
      ((SwitchPreference)localObject).setSummary(this.mOriginalSummary);
      enableWifiSwitch();
      return;
      ((SwitchPreference)localObject).setSummary(2131691340);
    }
  }
  
  private boolean postTurnOn(Context paramContext, int paramInt)
  {
    if ((this.mEnabling) && (paramContext.getResources().getBoolean(2131558457)))
    {
      Intent localIntent = new Intent("Hotspot_PostConfigure");
      localIntent.putExtra("choice", paramInt);
      paramContext.startActivity(localIntent);
      this.mEnabling = false;
    }
    return true;
  }
  
  private void updateSwitchEnabledState()
  {
    SwitchPreference localSwitchPreference;
    if ((!this.mContext.getResources().getBoolean(2131558431)) && (this.mWifiManager.getWifiApState() == 13))
    {
      localSwitchPreference = (SwitchPreference)this.mSwitch;
      if (!this.mDataSaverBackend.isDataSaverEnabled()) {
        break label53;
      }
    }
    label53:
    for (boolean bool = false;; bool = true)
    {
      localSwitchPreference.setEnabled(bool);
      return;
    }
  }
  
  private void updateTetherState(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, Object[] paramArrayOfObject3)
  {
    int i = 0;
    int m = 0;
    int n = paramArrayOfObject2.length;
    int j = 0;
    int k;
    int i1;
    while (j < n)
    {
      paramArrayOfObject1 = (String)paramArrayOfObject2[j];
      String[] arrayOfString = this.mWifiRegexs;
      k = 0;
      i1 = arrayOfString.length;
      while (k < i1)
      {
        if (paramArrayOfObject1.matches(arrayOfString[k])) {
          i = 1;
        }
        k += 1;
      }
      j += 1;
    }
    n = paramArrayOfObject3.length;
    j = 0;
    while (j < n)
    {
      paramArrayOfObject1 = (String)paramArrayOfObject3[j];
      paramArrayOfObject2 = this.mWifiRegexs;
      k = 0;
      i1 = paramArrayOfObject2.length;
      while (k < i1)
      {
        if (paramArrayOfObject1.matches(paramArrayOfObject2[k])) {
          m = 1;
        }
        k += 1;
      }
      j += 1;
    }
    if (i != 0)
    {
      updateSwitchEnabledState();
      updateConfigSummary(this.mWifiManager.getWifiApConfiguration());
    }
    while (m == 0) {
      return;
    }
    updateSwitchEnabledState();
    this.mSwitch.setSummary(2131691340);
  }
  
  public void pause()
  {
    this.mContext.unregisterReceiver(this.mReceiver);
  }
  
  public void resume()
  {
    this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    enableWifiSwitch();
  }
  
  public void setChecked(boolean paramBoolean)
  {
    ((HotspotPreference)this.mSwitch).setChecked(paramBoolean);
  }
  
  public void setSoftapEnabled(boolean paramBoolean)
  {
    if (TetherUtil.setWifiTethering(paramBoolean, this.mContext)) {
      this.mSwitch.setEnabled(false);
    }
    for (;;)
    {
      this.mEnabling = paramBoolean;
      return;
      this.mSwitch.setSummary(2131691340);
    }
  }
  
  public void updateConfigSummary(WifiConfiguration paramWifiConfiguration)
  {
    String str1 = this.mContext.getString(17040374);
    Preference localPreference = this.mSwitch;
    String str2 = this.mContext.getString(2131691500);
    if (paramWifiConfiguration == null) {}
    for (paramWifiConfiguration = str1;; paramWifiConfiguration = paramWifiConfiguration.SSID)
    {
      localPreference.setSummary(String.format(str2, new Object[] { paramWifiConfiguration }));
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiApEnabler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */