package com.android.settings.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.DhcpInfo;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppManager;
import android.net.NetworkScorerAppManager.NetworkScorerAppData;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.util.OpFeatures;
import android.widget.Toast;
import com.android.settings.AppListSwitchPreference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ConfigureWifiSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener
{
  private static final String KEY_CELLULAR_FALLBACK = "wifi_cellular_data_fallback";
  private static final String KEY_CURRENT_GATEWAY = "current_gateway";
  private static final String KEY_CURRENT_IP_ADDRESS = "current_ip_address";
  private static final String KEY_CURRENT_NETMASK = "current_netmask";
  private static final String KEY_MAC_ADDRESS = "mac_address";
  private static final String KEY_NETWORK_AUTO_CHANGE = "network_auto_change";
  private static final String KEY_NOTIFY_OPEN_NETWORKS = "notify_open_networks";
  private static final String KEY_SAVED_NETWORKS = "saved_networks";
  private static final String KEY_SCAN_ALWAYS_AVAILABLE = "wifi_scan_always_available";
  private static final String KEY_SLEEP_POLICY = "sleep_policy";
  private static final String KEY_WAPI_CERT_INSTALL = "wapi_cert_install";
  private static final String KEY_WAPI_CERT_UNINSTALL = "wapi_cert_uninstall";
  private static final String KEY_WIFI_ASSISTANT = "wifi_assistant";
  private static final String KEY_WIFI_IPV6_SURPORT = "wifi_ipv6_supported";
  private static final String TAG = "ConfigureWifiSettings";
  public static final String WIFI_AUTO_CHANGE_TO_MOBILE_DATA = "wifi_auto_change_to_mobile_data";
  public static final String WIFI_SHOULD_SWITCH_NETWORK = "wifi_should_switch_network";
  private IntentFilter mFilter;
  private NetworkScoreManager mNetworkScoreManager;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ((paramAnonymousContext.equals("android.net.wifi.LINK_CONFIGURATION_CHANGED")) || (paramAnonymousContext.equals("android.net.wifi.STATE_CHANGE"))) {
        ConfigureWifiSettings.-wrap0(ConfigureWifiSettings.this);
      }
    }
  };
  private Preference mWapiCertInstall;
  private Preference mWapiCertUninstall;
  private AppListSwitchPreference mWifiAssistantPreference;
  private WifiManager mWifiManager;
  
  private boolean avoidBadWifiConfig()
  {
    return getActivity().getResources().getInteger(17694737) == 1;
  }
  
  private boolean avoidBadWifiCurrentSettings()
  {
    return "1".equals(Settings.Global.getString(getContentResolver(), "network_avoid_bad_wifi"));
  }
  
  private void initPreferences()
  {
    Object localObject1 = this.mWifiManager.getConfiguredNetworks();
    if ((localObject1 == null) || (((List)localObject1).size() == 0)) {
      removePreference("saved_networks");
    }
    localObject1 = (SwitchPreference)findPreference("notify_open_networks");
    boolean bool;
    label99:
    label134:
    label169:
    label198:
    Object localObject2;
    if (Settings.Global.getInt(getContentResolver(), "wifi_networks_available_notification_on", 0) == 1)
    {
      bool = true;
      ((SwitchPreference)localObject1).setChecked(bool);
      ((SwitchPreference)localObject1).setEnabled(this.mWifiManager.isWifiEnabled());
      localObject1 = (SwitchPreference)findPreference("wifi_scan_always_available");
      if (localObject1 != null)
      {
        if (Settings.Global.getInt(getContentResolver(), "wifi_scan_always_enabled", 0) != 1) {
          break label317;
        }
        bool = true;
        ((SwitchPreference)localObject1).setChecked(bool);
      }
      localObject1 = (SwitchPreference)findPreference("network_auto_change");
      if (localObject1 != null)
      {
        if (Settings.System.getInt(getContentResolver(), "wifi_should_switch_network", 0) != 1) {
          break label322;
        }
        bool = true;
        ((SwitchPreference)localObject1).setChecked(bool);
      }
      localObject1 = (SwitchPreference)findPreference("wifi_ipv6_supported");
      if (localObject1 != null)
      {
        if (Settings.Global.getInt(getContentResolver(), "wifi_ipv6_supported", 1) != 1) {
          break label327;
        }
        bool = true;
        ((SwitchPreference)localObject1).setChecked(bool);
      }
      removePreference("wifi_ipv6_supported");
      localObject1 = getActivity();
      if (!avoidBadWifiConfig()) {
        break label332;
      }
      removePreference("wifi_cellular_data_fallback");
      this.mWifiAssistantPreference = ((AppListSwitchPreference)findPreference("wifi_assistant"));
      localObject2 = NetworkScorerAppManager.getAllValidScorers((Context)localObject1);
      if ((UserManager.get((Context)localObject1).isAdminUser()) && (!((Collection)localObject2).isEmpty())) {
        break label359;
      }
      if (this.mWifiAssistantPreference != null) {
        getPreferenceScreen().removePreference(this.mWifiAssistantPreference);
      }
    }
    for (;;)
    {
      localObject2 = (ListPreference)findPreference("sleep_policy");
      if (localObject2 != null)
      {
        if (Utils.isWifiOnly((Context)localObject1)) {
          ((ListPreference)localObject2).setEntries(2131427396);
        }
        ((ListPreference)localObject2).setOnPreferenceChangeListener(this);
        localObject1 = String.valueOf(Settings.Global.getInt(getContentResolver(), "wifi_sleep_policy", 2));
        ((ListPreference)localObject2).setValue((String)localObject1);
        updateSleepPolicySummary((Preference)localObject2, (String)localObject1);
      }
      return;
      bool = false;
      break;
      label317:
      bool = false;
      break label99;
      label322:
      bool = false;
      break label134;
      label327:
      bool = false;
      break label169;
      label332:
      bool = avoidBadWifiCurrentSettings();
      localObject2 = (SwitchPreference)findPreference("wifi_cellular_data_fallback");
      if (localObject2 == null) {
        break label198;
      }
      ((SwitchPreference)localObject2).setChecked(bool);
      break label198;
      label359:
      this.mWifiAssistantPreference.setOnPreferenceChangeListener(this);
      initWifiAssistantPreference((Collection)localObject2);
    }
  }
  
  private void initWifiAssistantPreference(Collection<NetworkScorerAppManager.NetworkScorerAppData> paramCollection)
  {
    String[] arrayOfString = new String[paramCollection.size()];
    int i = 0;
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      arrayOfString[i] = ((NetworkScorerAppManager.NetworkScorerAppData)paramCollection.next()).mPackageName;
      i += 1;
    }
    this.mWifiAssistantPreference.setPackageNames(arrayOfString, this.mNetworkScoreManager.getActiveScorerPackage());
  }
  
  private void refreshWifiInfo()
  {
    Object localObject1 = null;
    Activity localActivity = getActivity();
    WifiInfo localWifiInfo = this.mWifiManager.getConnectionInfo();
    Object localObject2 = findPreference("mac_address");
    label35:
    Preference localPreference3;
    Preference localPreference2;
    if (localWifiInfo == null)
    {
      if (TextUtils.isEmpty((CharSequence)localObject1)) {
        break label237;
      }
      ((Preference)localObject2).setSummary((CharSequence)localObject1);
      ((Preference)localObject2).setSelectable(false);
      Preference localPreference1 = findPreference("current_ip_address");
      localObject2 = Utils.getWifiIpAddresses(localActivity);
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = localActivity.getString(2131691712);
      }
      localPreference1.setSummary((CharSequence)localObject1);
      localPreference1.setSelectable(false);
      localPreference3 = findPreference("current_gateway");
      localActivity = null;
      localPreference2 = findPreference("current_netmask");
      localPreference1 = null;
      if (!getResources().getBoolean(2131558423)) {
        break label254;
      }
      DhcpInfo localDhcpInfo = this.mWifiManager.getDhcpInfo();
      localObject2 = localActivity;
      localObject1 = localPreference1;
      if (localWifiInfo != null)
      {
        localObject2 = localActivity;
        localObject1 = localPreference1;
        if (localDhcpInfo != null)
        {
          localObject2 = Formatter.formatIpAddress(localDhcpInfo.gateway);
          localObject1 = Formatter.formatIpAddress(localDhcpInfo.netmask);
        }
      }
      if (localPreference3 != null)
      {
        if ((localObject2 != null) && (localDhcpInfo.gateway != 0)) {
          break label248;
        }
        localObject2 = getString(2131691712);
        label190:
        localPreference3.setSummary((CharSequence)localObject2);
      }
      if (localPreference2 != null)
      {
        if ((localObject1 != null) && (localDhcpInfo.netmask != 0)) {
          break label251;
        }
        localObject1 = getString(2131691712);
        label221:
        localPreference2.setSummary((CharSequence)localObject1);
      }
    }
    label237:
    label248:
    label251:
    label254:
    do
    {
      return;
      localObject1 = localWifiInfo.getMacAddress();
      break;
      localObject1 = localActivity.getString(2131691712);
      break label35;
      break label190;
      break label221;
      if (localPreference3 != null) {
        getPreferenceScreen().removePreference(localPreference3);
      }
    } while (localPreference2 == null);
    getPreferenceScreen().removePreference(localPreference2);
  }
  
  private void updateSleepPolicySummary(Preference paramPreference, String paramString)
  {
    if (paramString != null)
    {
      String[] arrayOfString1 = getResources().getStringArray(2131427397);
      int i;
      String[] arrayOfString2;
      if (Utils.isWifiOnly(getActivity()))
      {
        i = 2131427396;
        arrayOfString2 = getResources().getStringArray(i);
        i = 0;
      }
      for (;;)
      {
        if (i >= arrayOfString1.length) {
          break label89;
        }
        if ((paramString.equals(arrayOfString1[i])) && (i < arrayOfString2.length))
        {
          paramPreference.setSummary(arrayOfString2[i]);
          return;
          i = 2131427394;
          break;
        }
        i += 1;
      }
    }
    label89:
    this.mWapiCertInstall = findPreference("wapi_cert_install");
    this.mWapiCertUninstall = findPreference("wapi_cert_uninstall");
    paramPreference.setSummary("");
    Log.e("ConfigureWifiSettings", "Invalid sleep policy value: " + paramString);
  }
  
  protected int getMetricsCategory()
  {
    return 338;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    this.mFilter = new IntentFilter();
    this.mFilter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
    this.mFilter.addAction("android.net.wifi.STATE_CHANGE");
    this.mNetworkScoreManager = ((NetworkScoreManager)getSystemService("network_score"));
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230886);
  }
  
  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mReceiver);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    Activity localActivity = getActivity();
    String str = paramPreference.getKey();
    if ("wifi_assistant".equals(str))
    {
      paramPreference = NetworkScorerAppManager.getScorer(localActivity, (String)paramObject);
      if (paramPreference == null)
      {
        this.mNetworkScoreManager.setActiveScorer(null);
        return true;
      }
      paramObject = new Intent();
      if (paramPreference.mConfigurationActivityClassName != null) {
        ((Intent)paramObject).setClassName(paramPreference.mPackageName, paramPreference.mConfigurationActivityClassName);
      }
      for (;;)
      {
        startActivity((Intent)paramObject);
        return false;
        ((Intent)paramObject).setAction("android.net.scoring.CHANGE_ACTIVE");
        ((Intent)paramObject).putExtra("packageName", paramPreference.mPackageName);
      }
    }
    if ("sleep_policy".equals(str)) {}
    try
    {
      paramObject = (String)paramObject;
      Settings.Global.putInt(getContentResolver(), "wifi_sleep_policy", Integer.parseInt((String)paramObject));
      updateSleepPolicySummary(paramPreference, (String)paramObject);
      return true;
    }
    catch (NumberFormatException paramPreference)
    {
      Toast.makeText(localActivity, 2131691357, 0).show();
    }
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    int j = 0;
    int k = 0;
    int i = 0;
    Object localObject = paramPreference.getKey();
    if ("notify_open_networks".equals(localObject))
    {
      localObject = getContentResolver();
      if (((SwitchPreference)paramPreference).isChecked())
      {
        i = 1;
        Settings.Global.putInt((ContentResolver)localObject, "wifi_networks_available_notification_on", i);
      }
    }
    do
    {
      return true;
      i = 0;
      break;
      if ("wifi_cellular_data_fallback".equals(localObject))
      {
        localObject = getContentResolver();
        if (((SwitchPreference)paramPreference).isChecked()) {}
        for (paramPreference = "1";; paramPreference = null)
        {
          Settings.Global.putString((ContentResolver)localObject, "network_avoid_bad_wifi", paramPreference);
          return true;
        }
      }
      if ("wifi_scan_always_available".equals(localObject))
      {
        localObject = getContentResolver();
        if (((SwitchPreference)paramPreference).isChecked()) {
          i = 1;
        }
        Settings.Global.putInt((ContentResolver)localObject, "wifi_scan_always_enabled", i);
        return true;
      }
      if (!"network_auto_change".equals(localObject)) {
        break label260;
      }
      localObject = getContentResolver();
      if (!((SwitchPreference)paramPreference).isChecked()) {
        break label250;
      }
      i = 1;
      Settings.System.putInt((ContentResolver)localObject, "wifi_should_switch_network", i);
    } while (!OpFeatures.isSupport(new int[] { 1 }));
    localObject = getContentResolver();
    if (((SwitchPreference)paramPreference).isChecked()) {}
    for (i = 1;; i = 0)
    {
      Settings.System.putInt((ContentResolver)localObject, "wifi_auto_change_to_mobile_data", i);
      localObject = getContentResolver();
      i = j;
      if (((SwitchPreference)paramPreference).isChecked()) {
        i = 1;
      }
      Settings.Global.putInt((ContentResolver)localObject, "captive_portal_detection_enabled", i);
      return true;
      label250:
      i = 0;
      break;
    }
    label260:
    if ("wifi_ipv6_supported".equals(localObject))
    {
      localObject = getContentResolver();
      i = k;
      if (((SwitchPreference)paramPreference).isChecked()) {
        i = 1;
      }
      Settings.Global.putInt((ContentResolver)localObject, "wifi_ipv6_supported", i);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onResume()
  {
    super.onResume();
    initPreferences();
    getActivity().registerReceiver(this.mReceiver, this.mFilter);
    refreshWifiInfo();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\ConfigureWifiSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */