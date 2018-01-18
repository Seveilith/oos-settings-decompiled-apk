package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.OnStartTetheringCallback;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.datausage.DataSaverBackend.Listener;
import com.android.settings.wifi.WifiApDialog;
import com.android.settings.wifi.WifiApEnabler;
import com.android.settingslib.TetherUtil;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class TetherSettings
  extends RestrictedSettingsFragment
  implements DialogInterface.OnClickListener, Preference.OnPreferenceChangeListener, DataSaverBackend.Listener, Preference.OnPreferenceClickListener
{
  private static final String ACTION_EXTRA = "choice";
  private static final String ACTION_EXTRA_VALUE = "value";
  private static final String ACTION_HOTSPOT_CONFIGURE_RRSPONSE = "Hotspot_PreConfigure_Response";
  private static final String ACTION_HOTSPOT_POST_CONFIGURE = "Hotspot_PostConfigure";
  private static final String ACTION_HOTSPOT_PRE_CONFIGURE = "Hotspot_PreConfigure";
  private static final int CLOSE_WIFI_AP = 1;
  private static final String CONFIGURE_RESULT = "PreConfigure_result";
  private static final int CONFIG_SUBTEXT = 2131691505;
  private static final String CONNECTED_DEVICE_MANAGER = "connected_device_manager";
  private static final String DATA_SAVER_FOOTER = "disabled_on_data_saver";
  private static final int DIALOG_AP_SETTINGS = 1;
  private static final String ENABLE_BLUETOOTH_TETHERING = "enable_bluetooth_tethering";
  private static final String ENABLE_WIFI_AP = "enable_wifi_ap";
  private static final String ENABLE_WIFI_AP_EXT = "enable_wifi_ap_ext";
  private static final String KEY_FIRST_LAUNCH_HOTSPOT = "FirstLaunchHotspotTethering";
  private static final int PROVISION_REQUEST = 0;
  private static final String SHAREPREFERENCE_DEFAULT_WIFI = "def_wifiap_set";
  private static final String SHAREPREFERENCE_FIFE_NAME = "MY_PERFS";
  private static final String TAG = "TetheringSettings";
  private static final String TETHERING_HELP = "tethering_help";
  private static final String TETHER_CHOICE = "TETHER_TYPE";
  private static final String USB_TETHER_SETTINGS = "usb_tether_settings";
  private static final String WIFI_AP_SSID_AND_SECURITY = "wifi_ap_ssid_and_security";
  private PreferenceGroup TetherSettingsCategory;
  private boolean mBluetoothEnableForTether;
  private AtomicReference<BluetoothPan> mBluetoothPan = new AtomicReference();
  private String[] mBluetoothRegexs;
  private SwitchPreference mBluetoothTether;
  private Handler mCloseWiFIAPHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      try
      {
        TetherSettings.-get6(TetherSettings.this).setEnabled(false);
        if (TetherUtil.isProvisioningNeeded(TetherSettings.this.getActivity())) {
          TetherService.cancelRecheckAlarmIfNecessary(TetherSettings.this.getActivity(), 0);
        }
        TetherSettings.-get9(TetherSettings.this).setSoftapEnabled(false);
        TetherSettings.-get4(TetherSettings.this).setEnabled(false);
        TetherSettings.-get0(TetherSettings.this).removePreference(TetherSettings.-get4(TetherSettings.this));
        return;
      }
      catch (NullPointerException paramAnonymousMessage) {}
    }
  };
  private ConnectivityManager mCm;
  private BroadcastReceiver mConfigureReceiver;
  private Preference mConnectedDeviceManagerPreference;
  private ConnectivityManager mConnectivityManager;
  private Preference mCreateNetwork;
  private DataSaverBackend mDataSaverBackend;
  private boolean mDataSaverEnabled;
  private Preference mDataSaverFooter;
  private WifiApDialog mDialog;
  private Preference mEnableWifiAp;
  private Handler mHandler = new Handler();
  private IntentFilter mIntentFilter;
  private boolean mMassStorageActive;
  private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener()
  {
    public void onServiceConnected(int paramAnonymousInt, BluetoothProfile paramAnonymousBluetoothProfile)
    {
      TetherSettings.-get2(TetherSettings.this).set((BluetoothPan)paramAnonymousBluetoothProfile);
    }
    
    public void onServiceDisconnected(int paramAnonymousInt)
    {
      TetherSettings.-get2(TetherSettings.this).set(null);
    }
  };
  private String[] mProvisionApp;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int i = 0;
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ("codeaurora.net.conn.TETHER_CONNECT_STATE_CHANGED".equals(paramAnonymousContext)) {
        if ((TetherSettings.-get4(TetherSettings.this) != null) && (TetherSettings.-get5(TetherSettings.this) != null)) {
          new Handler().post(new Runnable()
          {
            public void run()
            {
              TetherSettings.-wrap3(TetherSettings.this, TetherSettings.-get5(TetherSettings.this).getTetherConnectedSta().size());
            }
          });
        }
      }
      do
      {
        return;
        if ("codeaurora.net.conn.TETHER_AUTO_SHUT_DOWN_SOFTAP".equals(paramAnonymousContext))
        {
          TetherSettings.-get3(TetherSettings.this).sendEmptyMessage(1);
          return;
        }
      } while (!"android.net.wifi.WIFI_AP_STATE_CHANGED".equals(paramAnonymousContext));
      if ((TetherSettings.-get11(TetherSettings.this) != null) && (TetherSettings.-get11(TetherSettings.this).getWifiApState() == 13))
      {
        if (Settings.Secure.getIntForUser(TetherSettings.this.getActivity().getContentResolver(), "hotspot_auto_shut_down", 0, 0) != 0) {
          i = 1;
        }
        if (i != 0) {
          TetherSettings.-get4(TetherSettings.this).setSummary(2131690213);
        }
        for (;;)
        {
          TetherSettings.-get0(TetherSettings.this).addPreference(TetherSettings.-get4(TetherSettings.this));
          TetherSettings.-get4(TetherSettings.this).setEnabled(true);
          return;
          TetherSettings.-get4(TetherSettings.this).setSummary(2131690451);
        }
      }
      TetherSettings.-get4(TetherSettings.this).setEnabled(false);
      TetherSettings.-get0(TetherSettings.this).removePreference(TetherSettings.-get4(TetherSettings.this));
    }
  };
  private boolean mRestartWifiApAfterConfigChange;
  private String[] mSecurityType;
  private OnStartTetheringCallback mStartTetheringCallback;
  private BroadcastReceiver mTetherChangeReceiver;
  private int mTetherChoice = -1;
  private PreferenceScreen mTetherHelp;
  private boolean mUnavailable;
  private boolean mUsbConnected;
  private String[] mUsbRegexs;
  private SwitchPreference mUsbTether;
  private WifiApEnabler mWifiApEnabler;
  private WifiConfiguration mWifiConfig = null;
  private WifiManager mWifiManager;
  private String[] mWifiRegexs;
  private Method setWifiStaSapConcurrencyEnabledMethod;
  
  public TetherSettings()
  {
    super("no_config_tethering");
  }
  
  private String addSpaceWhileZhCn()
  {
    if (SettingsBaseApplication.mApplication.getResources().getConfiguration().locale.getCountry().equals("CN")) {
      return " ";
    }
    return "";
  }
  
  private void checkDefaultValue(Context paramContext)
  {
    boolean bool1 = paramContext.getResources().getBoolean(2131558456);
    boolean bool2 = paramContext.getResources().getBoolean(2131558455);
    if ((bool1) || (bool2))
    {
      SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("MY_PERFS", 0);
      if ((!localSharedPreferences.getBoolean("def_wifiap_set", false)) && (setDefaultValue(paramContext, bool1, bool2)))
      {
        paramContext = localSharedPreferences.edit();
        paramContext.putBoolean("def_wifiap_set", true);
        paramContext.commit();
      }
    }
  }
  
  private void initWifiTethering()
  {
    Activity localActivity = getActivity();
    this.mWifiConfig = this.mWifiManager.getWifiApConfiguration();
    this.mSecurityType = getResources().getStringArray(2131427385);
    this.mRestartWifiApAfterConfigChange = false;
    if (this.mCreateNetwork == null) {
      return;
    }
    if (this.mWifiConfig == null)
    {
      String str = localActivity.getString(17040374);
      this.mCreateNetwork.setSummary(String.format(localActivity.getString(2131691505), new Object[] { str, this.mSecurityType[0] + addSpaceWhileZhCn() }));
      return;
    }
    int i = WifiApDialog.getSecurityTypeIndex(this.mWifiConfig);
    this.mCreateNetwork.setSummary(String.format(localActivity.getString(2131691505), new Object[] { this.mWifiConfig.SSID, this.mSecurityType[i] + addSpaceWhileZhCn() }));
  }
  
  private static boolean isIntentAvailable(Context paramContext)
  {
    String[] arrayOfString = paramContext.getResources().getStringArray(17235994);
    paramContext = paramContext.getPackageManager();
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.setClassName(arrayOfString[0], arrayOfString[1]);
    return paramContext.queryIntentActivities(localIntent, 65536).size() > 0;
  }
  
  private boolean isNeedShowHelp(Context paramContext)
  {
    paramContext = paramContext.getSharedPreferences("MY_PERFS", 0);
    SharedPreferences.Editor localEditor = paramContext.edit();
    boolean bool = paramContext.getBoolean("FirstLaunchHotspotTethering", true);
    if (bool)
    {
      localEditor.putBoolean("FirstLaunchHotspotTethering", false);
      localEditor.commit();
    }
    return bool;
  }
  
  public static boolean isProvisioningNeededButUnavailable(Context paramContext)
  {
    return (TetherUtil.isProvisioningNeeded(paramContext)) && (!isIntentAvailable(paramContext));
  }
  
  private boolean isSimCardReady(TelephonyManager paramTelephonyManager)
  {
    return paramTelephonyManager.getSimState() == 5;
  }
  
  private void registerConfigureReceiver(Context paramContext)
  {
    IntentFilter localIntentFilter = new IntentFilter("Hotspot_PreConfigure_Response");
    if (this.mConfigureReceiver == null) {
      this.mConfigureReceiver = new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          if (paramAnonymousIntent.getAction().equals("Hotspot_PreConfigure_Response"))
          {
            if (paramAnonymousIntent.getBooleanExtra("PreConfigure_result", true)) {
              TetherSettings.-wrap2(TetherSettings.this, TetherSettings.-get8(TetherSettings.this));
            }
          }
          else {
            return;
          }
          TetherSettings.-get9(TetherSettings.this).setChecked(false);
        }
      };
    }
    paramContext.registerReceiver(this.mConfigureReceiver, localIntentFilter);
  }
  
  private boolean setDefaultValue(Context paramContext, boolean paramBoolean1, boolean paramBoolean2)
  {
    WifiManager localWifiManager = (WifiManager)paramContext.getSystemService("wifi");
    if (localWifiManager == null) {
      return false;
    }
    WifiConfiguration localWifiConfiguration = localWifiManager.getWifiApConfiguration();
    if (localWifiConfiguration == null) {
      return false;
    }
    if (paramBoolean1)
    {
      String str2 = ((TelephonyManager)paramContext.getSystemService("phone")).getDeviceId();
      String str1 = "";
      paramContext = str1;
      if (str2 != null)
      {
        paramContext = str1;
        if (str2.length() > 3) {
          paramContext = str2.substring(str2.length() - 4);
        }
      }
      localWifiConfiguration.SSID = Build.MODEL;
      if ((!TextUtils.isEmpty(paramContext)) && (localWifiConfiguration.SSID != null) && (localWifiConfiguration.SSID.indexOf(paramContext) < 0)) {
        localWifiConfiguration.SSID = (localWifiConfiguration.SSID + " " + paramContext);
      }
    }
    if (paramBoolean2) {
      localWifiConfiguration.preSharedKey = "";
    }
    localWifiManager.setWifiApConfiguration(localWifiConfiguration);
    return true;
  }
  
  private void setWifiStaSapConcurrencyEnabled(Context paramContext)
  {
    int i = Settings.System.getIntForUser(paramContext.getContentResolver(), "oem_share_wifi", 0, -2);
    if ((this.mWifiManager != null) && (this.mWifiManager.getWifiApState() == 11)) {}
    try
    {
      this.setWifiStaSapConcurrencyEnabledMethod = this.mWifiManager.getClass().getDeclaredMethod("setWifiStaSapConcurrencyEnabled", new Class[] { Integer.TYPE });
      this.setWifiStaSapConcurrencyEnabledMethod.invoke(this.mWifiManager, new Object[] { Integer.valueOf(i) });
      return;
    }
    catch (Exception paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  private boolean showNoSimCardDialog(Context paramContext)
  {
    if (!isSimCardReady((TelephonyManager)paramContext.getSystemService("phone")))
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
      localBuilder.setTitle(paramContext.getResources().getString(2131693791));
      localBuilder.setMessage(paramContext.getResources().getString(2131693792));
      localBuilder.setPositiveButton(paramContext.getResources().getString(2131690994), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface.dismiss();
        }
      });
      localBuilder.show();
      return true;
    }
    return false;
  }
  
  private void startTethering(int paramInt)
  {
    if (paramInt == 2)
    {
      BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      if (localBluetoothAdapter.getState() == 10)
      {
        this.mBluetoothEnableForTether = true;
        localBluetoothAdapter.enable();
        this.mBluetoothTether.setSummary(2131690884);
        this.mBluetoothTether.setEnabled(false);
        return;
      }
    }
    this.mCm.startTethering(paramInt, true, this.mStartTetheringCallback, this.mHandler);
  }
  
  private void unRegisterConfigureReceiver()
  {
    if (this.mConfigureReceiver != null)
    {
      getActivity().unregisterReceiver(this.mConfigureReceiver);
      this.mConfigureReceiver = null;
    }
  }
  
  private void updateBluetoothState(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3)
  {
    int j = 0;
    int m = paramArrayOfString3.length;
    int i = 0;
    while (i < m)
    {
      paramArrayOfString1 = paramArrayOfString3[i];
      paramArrayOfString2 = this.mBluetoothRegexs;
      int k = 0;
      int n = paramArrayOfString2.length;
      while (k < n)
      {
        if (paramArrayOfString1.matches(paramArrayOfString2[k])) {
          j = 1;
        }
        k += 1;
      }
      i += 1;
    }
    paramArrayOfString1 = BluetoothAdapter.getDefaultAdapter();
    if (paramArrayOfString1 == null) {
      return;
    }
    i = paramArrayOfString1.getState();
    if (i == 13)
    {
      this.mBluetoothTether.setEnabled(false);
      this.mBluetoothTether.setSummary(2131690885);
      return;
    }
    if (i == 11)
    {
      this.mBluetoothTether.setEnabled(false);
      this.mBluetoothTether.setSummary(2131690884);
      return;
    }
    paramArrayOfString1 = (BluetoothPan)this.mBluetoothPan.get();
    if ((i == 12) && (paramArrayOfString1 != null) && (paramArrayOfString1.isTetheringOn()))
    {
      this.mBluetoothTether.setChecked(true);
      paramArrayOfString2 = this.mBluetoothTether;
      if (this.mDataSaverEnabled) {}
      for (bool = false;; bool = true)
      {
        paramArrayOfString2.setEnabled(bool);
        i = paramArrayOfString1.getConnectedDevices().size();
        if (i <= 1) {
          break;
        }
        paramArrayOfString1 = getString(2131691923, new Object[] { Integer.valueOf(i) });
        this.mBluetoothTether.setSummary(paramArrayOfString1);
        return;
      }
      if (i == 1)
      {
        this.mBluetoothTether.setSummary(2131691922);
        return;
      }
      if (j != 0)
      {
        this.mBluetoothTether.setSummary(2131691926);
        return;
      }
      this.mBluetoothTether.setSummary(2131691921);
      return;
    }
    paramArrayOfString1 = this.mBluetoothTether;
    if (this.mDataSaverEnabled) {}
    for (boolean bool = false;; bool = true)
    {
      paramArrayOfString1.setEnabled(bool);
      this.mBluetoothTether.setChecked(false);
      this.mBluetoothTether.setSummary(2131691925);
      return;
    }
  }
  
  private void updateConnectedDeviceCount(int paramInt)
  {
    int i = 1;
    if (paramInt == 0)
    {
      if (Settings.Secure.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "hotspot_auto_shut_down", 0, 0) != 0) {}
      for (paramInt = i; paramInt != 0; paramInt = 0)
      {
        this.mConnectedDeviceManagerPreference.setSummary(2131690213);
        return;
      }
      this.mConnectedDeviceManagerPreference.setSummary(2131690451);
      return;
    }
    String str2 = SettingsBaseApplication.mApplication.getResources().getString(2131690214);
    String str1 = str2;
    if (OPUtils.isEn(SettingsBaseApplication.mApplication))
    {
      str1 = str2;
      if (paramInt > 1) {
        str1 = SettingsBaseApplication.mApplication.getResources().getString(2131690215);
      }
    }
    this.mConnectedDeviceManagerPreference.setSummary(String.format(str1, new Object[] { Integer.valueOf(paramInt) }));
  }
  
  private void updateState()
  {
    updateState(this.mCm.getTetherableIfaces(), this.mCm.getTetheredIfaces(), this.mCm.getTetheringErroredIfaces());
  }
  
  private void updateState(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3)
  {
    updateUsbState(paramArrayOfString1, paramArrayOfString2, paramArrayOfString3);
    updateBluetoothState(paramArrayOfString1, paramArrayOfString2, paramArrayOfString3);
  }
  
  private void updateUsbState(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3)
  {
    int i;
    int j;
    if ((!this.mUsbConnected) || (this.mMassStorageActive))
    {
      i = 0;
      j = 0;
      i1 = paramArrayOfString1.length;
      k = 0;
    }
    Object localObject;
    int n;
    for (;;)
    {
      if (k >= i1) {
        break label126;
      }
      localObject = paramArrayOfString1[k];
      String[] arrayOfString = this.mUsbRegexs;
      m = 0;
      i2 = arrayOfString.length;
      for (;;)
      {
        if (m < i2)
        {
          n = j;
          if (((String)localObject).matches(arrayOfString[m]))
          {
            n = j;
            if (j == 0) {
              n = this.mCm.getLastTetherError((String)localObject);
            }
          }
          m += 1;
          j = n;
          continue;
          i = 1;
          break;
        }
      }
      k += 1;
    }
    label126:
    int k = 0;
    int i1 = paramArrayOfString2.length;
    int m = 0;
    while (m < i1)
    {
      paramArrayOfString1 = paramArrayOfString2[m];
      localObject = this.mUsbRegexs;
      n = 0;
      i2 = localObject.length;
      while (n < i2)
      {
        if (paramArrayOfString1.matches(localObject[n])) {
          k = 1;
        }
        n += 1;
      }
      m += 1;
    }
    i1 = 0;
    int i2 = paramArrayOfString3.length;
    m = 0;
    while (m < i2)
    {
      paramArrayOfString1 = paramArrayOfString3[m];
      paramArrayOfString2 = this.mUsbRegexs;
      n = 0;
      int i3 = paramArrayOfString2.length;
      while (n < i3)
      {
        if (paramArrayOfString1.matches(paramArrayOfString2[n])) {
          i1 = 1;
        }
        n += 1;
      }
      m += 1;
    }
    boolean bool;
    if (k != 0)
    {
      this.mUsbTether.setSummary(2131691915);
      paramArrayOfString1 = this.mUsbTether;
      if (this.mDataSaverEnabled) {}
      for (bool = false;; bool = true)
      {
        paramArrayOfString1.setEnabled(bool);
        this.mUsbTether.setChecked(true);
        return;
      }
    }
    if (i != 0)
    {
      if (j == 0)
      {
        this.mUsbTether.setSummary(2131691914);
        paramArrayOfString1 = this.mUsbTether;
        if (!this.mDataSaverEnabled) {
          break label389;
        }
      }
      label389:
      for (bool = false;; bool = true)
      {
        paramArrayOfString1.setEnabled(bool);
        this.mUsbTether.setChecked(false);
        return;
        this.mUsbTether.setSummary(2131691919);
        break;
      }
    }
    if (i1 != 0)
    {
      this.mUsbTether.setSummary(2131691919);
      this.mUsbTether.setEnabled(false);
      this.mUsbTether.setChecked(false);
      return;
    }
    if (this.mMassStorageActive)
    {
      this.mUsbTether.setSummary(2131691916);
      this.mUsbTether.setEnabled(false);
      this.mUsbTether.setChecked(false);
      return;
    }
    this.mUsbTether.setSummary(2131691917);
    this.mUsbTether.setEnabled(false);
    this.mUsbTether.setChecked(false);
  }
  
  public int getHelpResource()
  {
    return 2131693023;
  }
  
  protected int getMetricsCategory()
  {
    return 90;
  }
  
  public void onBlacklistStatusChanged(int paramInt, boolean paramBoolean) {}
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -1)
    {
      this.mWifiConfig = this.mDialog.getConfig();
      this.mDialog.confirmConfig();
      if (this.mWifiConfig != null)
      {
        if (this.mWifiManager.getWifiApState() != 13) {
          break label176;
        }
        Log.d("TetheringSettings", "Wifi AP config changed while enabled, stop and restart");
        this.mConnectedDeviceManagerPreference.setEnabled(false);
        this.TetherSettingsCategory.removePreference(this.mConnectedDeviceManagerPreference);
        this.mRestartWifiApAfterConfigChange = true;
        this.mCm.stopTethering(0);
        paramInt = WifiApDialog.getSecurityTypeIndex(this.mWifiConfig);
        if (this.mSecurityType == null) {
          this.mSecurityType = getResources().getStringArray(2131427385);
        }
        this.mCreateNetwork.setSummary(String.format(getActivity().getString(2131691505), new Object[] { this.mWifiConfig.SSID, this.mSecurityType[paramInt] + addSpaceWhileZhCn() }));
      }
    }
    label176:
    while (paramInt != -2) {
      for (;;)
      {
        return;
        this.mWifiManager.setWifiApConfiguration(this.mWifiConfig);
      }
    }
    this.mDialog.cancelConfig();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null) {
      this.mTetherChoice = paramBundle.getInt("TETHER_TYPE");
    }
    addPreferencesFromResource(2131230868);
    this.mDataSaverBackend = new DataSaverBackend(getContext());
    this.mDataSaverEnabled = this.mDataSaverBackend.isDataSaverEnabled();
    this.mDataSaverFooter = findPreference("disabled_on_data_saver");
    setIfOnlyAvailableForAdmins(true);
    if (isUiRestricted())
    {
      this.mUnavailable = true;
      setPreferenceScreen(new PreferenceScreen(getPrefContext(), null));
      return;
    }
    paramBundle = getActivity();
    Object localObject = BluetoothAdapter.getDefaultAdapter();
    if (localObject != null) {
      ((BluetoothAdapter)localObject).getProfileProxy(paramBundle.getApplicationContext(), this.mProfileServiceListener, 5);
    }
    this.mCreateNetwork = findPreference("wifi_ap_ssid_and_security");
    boolean bool1 = getResources().getBoolean(2131558431);
    boolean bool2 = getResources().getBoolean(2131558432);
    checkDefaultValue(getActivity());
    label282:
    int i;
    label387:
    int j;
    label397:
    int k;
    if (bool1)
    {
      this.mEnableWifiAp = ((HotspotPreference)findPreference("enable_wifi_ap_ext"));
      getPreferenceScreen().removePreference(findPreference("enable_wifi_ap"));
      getPreferenceScreen().removePreference(this.mCreateNetwork);
      this.mEnableWifiAp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
      {
        public boolean onPreferenceClick(Preference paramAnonymousPreference)
        {
          paramAnonymousPreference = new Intent();
          if (TetherSettings.-wrap0(TetherSettings.this, TetherSettings.this.getActivity())) {
            paramAnonymousPreference.setAction("Hotspot_PreConfigure");
          }
          for (;;)
          {
            paramAnonymousPreference.setPackage("com.qualcomm.qti.extsettings");
            TetherSettings.-get6(TetherSettings.this).setIntent(paramAnonymousPreference);
            return false;
            paramAnonymousPreference.setAction("com.qti.ap.settings");
          }
        }
      });
      if (bool2)
      {
        getPreferenceScreen().removePreference(this.mEnableWifiAp);
        getPreferenceScreen().removePreference(this.mCreateNetwork);
      }
      if (!getResources().getBoolean(2131558463)) {
        break label729;
      }
      this.mTetherHelp = ((PreferenceScreen)findPreference("tethering_help"));
      this.mUsbTether = ((SwitchPreference)findPreference("usb_tether_settings"));
      this.mBluetoothTether = ((SwitchPreference)findPreference("enable_bluetooth_tethering"));
      this.mDataSaverBackend.addListener(this);
      this.mCm = ((ConnectivityManager)getSystemService("connectivity"));
      this.mWifiManager = ((WifiManager)getSystemService("wifi"));
      this.mUsbRegexs = this.mCm.getTetherableUsbRegexs();
      this.mWifiRegexs = this.mCm.getTetherableWifiRegexs();
      this.mBluetoothRegexs = this.mCm.getTetherableBluetoothRegexs();
      if (this.mUsbRegexs.length == 0) {
        break label746;
      }
      i = 1;
      if (this.mWifiRegexs.length == 0) {
        break label751;
      }
      j = 1;
      if (this.mBluetoothRegexs.length == 0) {
        break label756;
      }
      k = 1;
      label408:
      if ((i == 0) || (Utils.isMonkeyRunning())) {
        getPreferenceScreen().removePreference(this.mUsbTether);
      }
      if ((j != 0) && (!Utils.isMonkeyRunning())) {
        break label762;
      }
      getPreferenceScreen().removePreference(this.mEnableWifiAp);
      getPreferenceScreen().removePreference(this.mCreateNetwork);
      label464:
      this.mConnectivityManager = ((ConnectivityManager)getSystemService("connectivity"));
      this.mConnectedDeviceManagerPreference = findPreference("connected_device_manager");
      this.mConnectedDeviceManagerPreference.setOnPreferenceClickListener(this);
      this.mConnectedDeviceManagerPreference.setSummary(String.valueOf(this.mConnectivityManager.getTetherConnectedSta().size()));
      this.TetherSettingsCategory = ((PreferenceGroup)findPreference("tether_settings_key"));
      if ((this.mWifiManager == null) || (this.mWifiManager.getWifiApState() != 13)) {
        break label789;
      }
      this.TetherSettingsCategory.addPreference(this.mConnectedDeviceManagerPreference);
      this.mConnectedDeviceManagerPreference.setEnabled(true);
      label571:
      if (!OPUtils.isAppPakExist(getActivity(), "com.oneplus.wifiapsettings")) {
        this.TetherSettingsCategory.removePreference(this.mConnectedDeviceManagerPreference);
      }
      if (k != 0) {
        break label812;
      }
      getPreferenceScreen().removePreference(this.mBluetoothTether);
    }
    for (;;)
    {
      onDataSaverChanged(this.mDataSaverBackend.isDataSaverEnabled());
      this.mIntentFilter = new IntentFilter();
      this.mIntentFilter.addAction("android.net.wifi.WIFI_HOTSPOT_CLIENTS_CHANGED");
      this.mIntentFilter.addAction("com.android.server.WifiManager.action.SHUT_DOWN_HOTSPOT");
      this.mIntentFilter.addAction("codeaurora.net.conn.TETHER_CONNECT_STATE_CHANGED");
      this.mIntentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
      paramBundle.registerReceiver(this.mReceiver, this.mIntentFilter);
      return;
      localObject = (PreferenceGroup)findPreference("tether_settings_key");
      this.mEnableWifiAp = ((SwitchPreference)findPreference("enable_wifi_ap"));
      ((PreferenceGroup)localObject).removePreference(findPreference("enable_wifi_ap_ext"));
      break;
      label729:
      getPreferenceScreen().removePreference(findPreference("tethering_help"));
      break label282;
      label746:
      i = 0;
      break label387;
      label751:
      j = 0;
      break label397;
      label756:
      k = 0;
      break label408;
      label762:
      this.mWifiApEnabler = new WifiApEnabler(paramBundle, this.mDataSaverBackend, this.mEnableWifiAp);
      initWifiTethering();
      break label464;
      label789:
      this.mConnectedDeviceManagerPreference.setEnabled(false);
      this.TetherSettingsCategory.removePreference(this.mConnectedDeviceManagerPreference);
      break label571;
      label812:
      localObject = (BluetoothPan)this.mBluetoothPan.get();
      if ((localObject != null) && (((BluetoothPan)localObject).isTetheringOn())) {
        this.mBluetoothTether.setChecked(true);
      } else {
        this.mBluetoothTether.setChecked(false);
      }
    }
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    if (paramInt == 1)
    {
      this.mDialog = new WifiApDialog(getActivity(), this, this.mWifiConfig);
      return this.mDialog;
    }
    return null;
  }
  
  public void onDataSaverChanged(boolean paramBoolean)
  {
    boolean bool = false;
    this.mDataSaverEnabled = paramBoolean;
    Object localObject = this.mEnableWifiAp;
    if (this.mDataSaverEnabled)
    {
      paramBoolean = false;
      ((Preference)localObject).setEnabled(paramBoolean);
      localObject = this.mUsbTether;
      if (!this.mDataSaverEnabled) {
        break label81;
      }
      paramBoolean = false;
      label40:
      ((SwitchPreference)localObject).setEnabled(paramBoolean);
      localObject = this.mBluetoothTether;
      if (!this.mDataSaverEnabled) {
        break label86;
      }
    }
    label81:
    label86:
    for (paramBoolean = bool;; paramBoolean = true)
    {
      ((SwitchPreference)localObject).setEnabled(paramBoolean);
      this.mDataSaverFooter.setVisible(this.mDataSaverEnabled);
      return;
      paramBoolean = true;
      break;
      paramBoolean = true;
      break label40;
    }
  }
  
  public void onDestroy()
  {
    this.mDataSaverBackend.remListener(this);
    try
    {
      getActivity().unregisterReceiver(this.mReceiver);
      BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      if ((localBluetoothAdapter != null) && (this.mBluetoothPan.get() != null)) {
        localBluetoothAdapter.closeProfileProxy(5, (BluetoothProfile)this.mBluetoothPan.get());
      }
      super.onDestroy();
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    boolean bool1 = ((Boolean)paramObject).booleanValue();
    boolean bool2 = getResources().getBoolean(2131558431);
    paramObject = getActivity();
    if (bool1)
    {
      if ((bool2) && (showNoSimCardDialog(getPrefContext())))
      {
        ((HotspotPreference)paramPreference).setChecked(false);
        return false;
      }
      if ((bool2) && (isNeedShowHelp(getPrefContext())))
      {
        paramObject = new Intent();
        ((Intent)paramObject).setAction("Hotspot_PreConfigure");
        ((Intent)paramObject).setPackage("com.qualcomm.qti.extsettings");
        getPrefContext().startActivity((Intent)paramObject);
        ((HotspotPreference)paramPreference).setChecked(false);
        return false;
      }
      paramPreference.setEnabled(false);
      ((SwitchPreference)paramPreference).setChecked(true);
      setWifiStaSapConcurrencyEnabled((Context)paramObject);
      startTethering(0);
      this.mConnectedDeviceManagerPreference.setEnabled(true);
      if (OPUtils.isAppPakExist(getActivity(), "com.oneplus.wifiapsettings"))
      {
        this.TetherSettingsCategory.addPreference(this.mConnectedDeviceManagerPreference);
        updateConnectedDeviceCount(this.mConnectivityManager.getTetherConnectedSta().size());
        return false;
      }
      this.mConnectedDeviceManagerPreference.setEnabled(false);
      this.TetherSettingsCategory.removePreference(this.mConnectedDeviceManagerPreference);
      return false;
    }
    this.mCm.stopTethering(0);
    this.mConnectedDeviceManagerPreference.setEnabled(false);
    this.TetherSettingsCategory.removePreference(this.mConnectedDeviceManagerPreference);
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mConnectedDeviceManagerPreference) {
      OPUtils.isAppPakExist(getActivity(), "com.oneplus.wifiapsettings");
    }
    try
    {
      paramPreference = new Intent("android.oem.intent.action.OPWIFIAP_SETTINGS");
      getActivity().startActivity(paramPreference);
      return true;
      return false;
    }
    catch (ActivityNotFoundException paramPreference)
    {
      for (;;) {}
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if (paramPreference == this.mUsbTether) {
      if (this.mUsbTether.isChecked()) {
        startTethering(1);
      }
    }
    for (;;)
    {
      return super.onPreferenceTreeClick(paramPreference);
      this.mCm.stopTethering(1);
      continue;
      if (paramPreference == this.mBluetoothTether)
      {
        if (this.mBluetoothTether.isChecked())
        {
          startTethering(2);
        }
        else
        {
          this.mCm.stopTethering(2);
          updateState();
        }
      }
      else if (paramPreference == this.mCreateNetwork)
      {
        showDialog(1);
      }
      else if ((getResources().getBoolean(2131558463)) && (paramPreference == this.mTetherHelp))
      {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
        localBuilder.setTitle(2131693782);
        localBuilder.setMessage(2131693783);
        localBuilder.setPositiveButton(2131690994, null);
        localBuilder.show();
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mConnectivityManager != null) {
      updateConnectedDeviceCount(this.mConnectivityManager.getTetherConnectedSta().size());
    }
  }
  
  public void onStart()
  {
    super.onStart();
    if (this.mUnavailable)
    {
      if (!isUiRestrictedByOnlyAdmin()) {
        getEmptyTextView().setText(2131689632);
      }
      getPreferenceScreen().removeAll();
      return;
    }
    Activity localActivity = getActivity();
    this.mStartTetheringCallback = new OnStartTetheringCallback(this);
    this.mMassStorageActive = "shared".equals(Environment.getExternalStorageState());
    this.mTetherChangeReceiver = new TetherChangeReceiver(null);
    Object localObject = new IntentFilter("android.net.conn.TETHER_STATE_CHANGED");
    ((IntentFilter)localObject).addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
    localObject = localActivity.registerReceiver(this.mTetherChangeReceiver, (IntentFilter)localObject);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.hardware.usb.action.USB_STATE");
    localActivity.registerReceiver(this.mTetherChangeReceiver, localIntentFilter);
    localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.MEDIA_SHARED");
    localIntentFilter.addAction("android.intent.action.MEDIA_UNSHARED");
    localIntentFilter.addDataScheme("file");
    localActivity.registerReceiver(this.mTetherChangeReceiver, localIntentFilter);
    localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
    localActivity.registerReceiver(this.mTetherChangeReceiver, localIntentFilter);
    if (localObject != null) {
      this.mTetherChangeReceiver.onReceive(localActivity, (Intent)localObject);
    }
    if (this.mWifiApEnabler != null)
    {
      this.mEnableWifiAp.setOnPreferenceChangeListener(this);
      this.mWifiApEnabler.resume();
    }
    updateState();
    registerConfigureReceiver(getActivity());
  }
  
  public void onStop()
  {
    super.onStop();
    if (this.mUnavailable) {
      return;
    }
    getActivity().unregisterReceiver(this.mTetherChangeReceiver);
    this.mTetherChangeReceiver = null;
    this.mStartTetheringCallback = null;
    if (this.mWifiApEnabler != null)
    {
      this.mEnableWifiAp.setOnPreferenceChangeListener(null);
      this.mWifiApEnabler.pause();
    }
    unRegisterConfigureReceiver();
  }
  
  public void onWhitelistStatusChanged(int paramInt, boolean paramBoolean) {}
  
  private static final class OnStartTetheringCallback
    extends ConnectivityManager.OnStartTetheringCallback
  {
    final WeakReference<TetherSettings> mTetherSettings;
    
    OnStartTetheringCallback(TetherSettings paramTetherSettings)
    {
      this.mTetherSettings = new WeakReference(paramTetherSettings);
    }
    
    private void update()
    {
      TetherSettings localTetherSettings = (TetherSettings)this.mTetherSettings.get();
      if (localTetherSettings != null) {
        TetherSettings.-wrap4(localTetherSettings);
      }
    }
    
    public void onTetheringFailed()
    {
      update();
    }
    
    public void onTetheringStarted()
    {
      update();
    }
  }
  
  private class TetherChangeReceiver
    extends BroadcastReceiver
  {
    private TetherChangeReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      Object localObject = paramIntent.getAction();
      if (((String)localObject).equals("android.net.conn.TETHER_STATE_CHANGED"))
      {
        localObject = paramIntent.getStringArrayListExtra("availableArray");
        ArrayList localArrayList = paramIntent.getStringArrayListExtra("activeArray");
        paramIntent = paramIntent.getStringArrayListExtra("erroredArray");
        TetherSettings.-wrap5(TetherSettings.this, (String[])((ArrayList)localObject).toArray(new String[((ArrayList)localObject).size()]), (String[])localArrayList.toArray(new String[localArrayList.size()]), (String[])paramIntent.toArray(new String[paramIntent.size()]));
        if ((TetherSettings.-get11(TetherSettings.this).getWifiApState() == 11) && (TetherSettings.-get7(TetherSettings.this)))
        {
          TetherSettings.-set2(TetherSettings.this, false);
          TetherSettings.-wrap1(TetherSettings.this, paramContext);
          TetherSettings.-get11(TetherSettings.this).setWifiApConfiguration(TetherSettings.-get10(TetherSettings.this));
          Log.d("TetheringSettings", "Restarting WifiAp due to prior config change.");
          TetherSettings.-wrap2(TetherSettings.this, 0);
        }
      }
      do
      {
        do
        {
          return;
          if (!((String)localObject).equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {
            break;
          }
        } while ((paramIntent.getIntExtra("wifi_state", 0) != 11) || (!TetherSettings.-get7(TetherSettings.this)));
        TetherSettings.-set2(TetherSettings.this, false);
        TetherSettings.-wrap1(TetherSettings.this, paramContext);
        TetherSettings.-get11(TetherSettings.this).setWifiApConfiguration(TetherSettings.-get10(TetherSettings.this));
        Log.d("TetheringSettings", "Restarting WifiAp due to prior config change.");
        TetherSettings.-wrap2(TetherSettings.this, 0);
        return;
        if (((String)localObject).equals("android.intent.action.MEDIA_SHARED"))
        {
          TetherSettings.-set1(TetherSettings.this, true);
          TetherSettings.-wrap4(TetherSettings.this);
          return;
        }
        if (((String)localObject).equals("android.intent.action.MEDIA_UNSHARED"))
        {
          TetherSettings.-set1(TetherSettings.this, false);
          TetherSettings.-wrap4(TetherSettings.this);
          return;
        }
        if (((String)localObject).equals("android.hardware.usb.action.USB_STATE"))
        {
          TetherSettings.-set3(TetherSettings.this, paramIntent.getBooleanExtra("connected", false));
          TetherSettings.-wrap4(TetherSettings.this);
          return;
        }
      } while (!((String)localObject).equals("android.bluetooth.adapter.action.STATE_CHANGED"));
      if (TetherSettings.-get1(TetherSettings.this)) {
        switch (paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE))
        {
        }
      }
      for (;;)
      {
        TetherSettings.-wrap4(TetherSettings.this);
        return;
        TetherSettings.-wrap2(TetherSettings.this, 2);
        TetherSettings.-set0(TetherSettings.this, false);
        continue;
        TetherSettings.-set0(TetherSettings.this, false);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\TetherSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */