package com.android.settings.deviceinfo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import com.android.internal.util.ArrayUtils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import java.lang.ref.WeakReference;

public class Status
  extends SettingsPreferenceFragment
{
  private static final String[] CONNECTIVITY_INTENTS = { "android.bluetooth.adapter.action.STATE_CHANGED", "android.net.conn.CONNECTIVITY_CHANGE", "android.net.wifi.LINK_CONFIGURATION_CHANGED", "android.net.wifi.STATE_CHANGE" };
  private static final int EVENT_UPDATE_CONNECTIVITY = 600;
  private static final int EVENT_UPDATE_STATS = 500;
  private static final String KEY_BATTERY_LEVEL = "battery_level";
  private static final String KEY_BATTERY_STATUS = "battery_status";
  private static final String KEY_BT_ADDRESS = "bt_address";
  private static final String KEY_IMEI_INFO = "imei_info";
  private static final String KEY_IP_ADDRESS = "wifi_ip_address";
  private static final String KEY_SERIAL_NUMBER = "serial_number";
  private static final String KEY_SIM_STATUS = "sim_status";
  private static final String KEY_WIFI_MAC_ADDRESS = "wifi_mac_address";
  private static final String KEY_WIMAX_MAC_ADDRESS = "wimax_mac_address";
  private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.BATTERY_CHANGED".equals(paramAnonymousIntent.getAction()))
      {
        Status.-get1(Status.this).setSummary(Utils.getBatteryPercentage(paramAnonymousIntent));
        Status.-get2(Status.this).setSummary(Utils.getBatteryStatus(Status.this.getResources(), paramAnonymousIntent));
      }
    }
  };
  private Preference mBatteryLevel;
  private Preference mBatteryStatus;
  private Preference mBtAddress;
  private ConnectivityManager mCM;
  private IntentFilter mConnectivityIntentFilter;
  private final BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (ArrayUtils.contains(Status.-get0(), paramAnonymousContext)) {
        Status.-get3(Status.this).sendEmptyMessage(600);
      }
    }
  };
  private Handler mHandler;
  private Preference mIpAddress;
  private Resources mRes;
  private String mUnavailable;
  private String mUnknown;
  private Preference mUptime;
  private Preference mWifiMacAddress;
  private WifiManager mWifiManager;
  private Preference mWimaxMacAddress;
  
  private String convert(long paramLong)
  {
    int i = (int)(paramLong % 60L);
    int j = (int)(paramLong / 60L % 60L);
    int k = (int)(paramLong / 3600L);
    return k + ":" + pad(j) + ":" + pad(i);
  }
  
  private boolean hasBluetooth()
  {
    return BluetoothAdapter.getDefaultAdapter() != null;
  }
  
  private boolean hasWimax()
  {
    return this.mCM.getNetworkInfo(6) != null;
  }
  
  private String pad(int paramInt)
  {
    if (paramInt >= 10) {
      return String.valueOf(paramInt);
    }
    return "0" + String.valueOf(paramInt);
  }
  
  private void removePreferenceFromScreen(String paramString)
  {
    paramString = findPreference(paramString);
    if (paramString != null) {
      getPreferenceScreen().removePreference(paramString);
    }
  }
  
  private void setBtStatus()
  {
    String str = null;
    BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if ((localBluetoothAdapter != null) && (this.mBtAddress != null))
    {
      if (localBluetoothAdapter.isEnabled()) {
        str = localBluetoothAdapter.getAddress();
      }
      if (!TextUtils.isEmpty(str)) {
        this.mBtAddress.setSummary(str.toLowerCase());
      }
    }
    else
    {
      return;
    }
    this.mBtAddress.setSummary(this.mUnavailable);
  }
  
  private void setIpAddressStatus()
  {
    String str = Utils.getDefaultIpAddresses(this.mCM);
    if (str != null)
    {
      this.mIpAddress.setSummary(str);
      return;
    }
    this.mIpAddress.setSummary(this.mUnavailable);
  }
  
  private void setSummary(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      findPreference(paramString1).setSummary(SystemProperties.get(paramString2, paramString3));
      return;
    }
    catch (RuntimeException paramString1) {}
  }
  
  private void setSummaryText(String paramString1, String paramString2)
  {
    String str = paramString2;
    if (TextUtils.isEmpty(paramString2)) {
      str = this.mUnknown;
    }
    if (findPreference(paramString1) != null) {
      findPreference(paramString1).setSummary(str);
    }
  }
  
  private void setWifiStatus()
  {
    Object localObject = this.mWifiManager.getConnectionInfo();
    Preference localPreference;
    if (localObject == null)
    {
      localObject = null;
      localPreference = this.mWifiMacAddress;
      if (TextUtils.isEmpty((CharSequence)localObject)) {
        break label40;
      }
    }
    for (;;)
    {
      localPreference.setSummary((CharSequence)localObject);
      return;
      localObject = ((WifiInfo)localObject).getMacAddress();
      break;
      label40:
      localObject = this.mUnavailable;
    }
  }
  
  private void setWimaxStatus()
  {
    if (this.mWimaxMacAddress != null)
    {
      String str = SystemProperties.get("net.wimax.mac.address", this.mUnavailable);
      this.mWimaxMacAddress.setSummary(str);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 44;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
    super.onCreate(paramBundle);
    this.mHandler = new MyHandler(this);
    this.mCM = ((ConnectivityManager)getSystemService("connectivity"));
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    addPreferencesFromResource(2131230760);
    this.mBatteryLevel = findPreference("battery_level");
    this.mBatteryStatus = findPreference("battery_status");
    this.mBtAddress = findPreference("bt_address");
    this.mWifiMacAddress = findPreference("wifi_mac_address");
    this.mWimaxMacAddress = findPreference("wimax_mac_address");
    this.mIpAddress = findPreference("wifi_ip_address");
    this.mRes = getResources();
    this.mUnknown = this.mRes.getString(2131690786);
    this.mUnavailable = this.mRes.getString(2131691712);
    this.mUptime = findPreference("up_time");
    if (!hasBluetooth())
    {
      getPreferenceScreen().removePreference(this.mBtAddress);
      this.mBtAddress = null;
    }
    if (!hasWimax())
    {
      getPreferenceScreen().removePreference(this.mWimaxMacAddress);
      this.mWimaxMacAddress = null;
    }
    this.mConnectivityIntentFilter = new IntentFilter();
    paramBundle = CONNECTIVITY_INTENTS;
    int j = paramBundle.length;
    int i = 0;
    while (i < j)
    {
      String str = paramBundle[i];
      this.mConnectivityIntentFilter.addAction(str);
      i += 1;
    }
    updateConnectivity();
    paramBundle = Build.SERIAL;
    if ((paramBundle == null) || (paramBundle.equals(""))) {
      removePreferenceFromScreen("serial_number");
    }
    for (;;)
    {
      if ((!UserManager.get(getContext()).isAdminUser()) || (Utils.isWifiOnly(getContext())))
      {
        removePreferenceFromScreen("sim_status");
        removePreferenceFromScreen("imei_info");
      }
      if (SystemProperties.getBoolean("ro.alarm_boot", false)) {
        removePreferenceFromScreen("imei_info");
      }
      return;
      setSummaryText("serial_number", paramBundle);
    }
  }
  
  public void onPause()
  {
    super.onPause();
    getContext().unregisterReceiver(this.mBatteryInfoReceiver);
    getContext().unregisterReceiver(this.mConnectivityReceiver);
    this.mHandler.removeMessages(500);
  }
  
  public void onResume()
  {
    super.onResume();
    getContext().registerReceiver(this.mConnectivityReceiver, this.mConnectivityIntentFilter, "android.permission.CHANGE_NETWORK_STATE", null);
    getContext().registerReceiver(this.mBatteryInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    this.mHandler.sendEmptyMessage(500);
  }
  
  void updateConnectivity()
  {
    setWimaxStatus();
    setWifiStatus();
    setBtStatus();
    setIpAddressStatus();
  }
  
  void updateTimes()
  {
    long l1 = SystemClock.uptimeMillis() / 1000L;
    long l2 = SystemClock.elapsedRealtime() / 1000L;
    l1 = l2;
    if (l2 == 0L) {
      l1 = 1L;
    }
    this.mUptime.setSummary(convert(l1));
  }
  
  private static class MyHandler
    extends Handler
  {
    private WeakReference<Status> mStatus;
    
    public MyHandler(Status paramStatus)
    {
      this.mStatus = new WeakReference(paramStatus);
    }
    
    public void handleMessage(Message paramMessage)
    {
      Status localStatus = (Status)this.mStatus.get();
      if (localStatus == null) {
        return;
      }
      switch (paramMessage.what)
      {
      default: 
        return;
      case 500: 
        localStatus.updateTimes();
        sendEmptyMessageDelayed(500, 1000L);
        return;
      }
      localStatus.updateConnectivity();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\Status.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */