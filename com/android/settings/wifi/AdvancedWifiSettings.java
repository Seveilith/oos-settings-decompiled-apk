package com.android.settings.wifi;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.security.Credentials;
import android.security.KeyStore;
import android.security.KeyStore.State;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.ChooseLockGeneric;
import com.android.settings.ChooseLockSettingsHelper;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.Settings.WifiP2pSettingsActivity;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedWifiSettings
  extends RestrictedSettingsFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, WapiCertMgmtDialog.RefreshAdvance, Indexable
{
  private static final int AUTO_CONNECT_DEFAULT_VALUE = 0;
  private static final int AUTO_CONNECT_DISABLE = 1;
  private static final int AUTO_CONNECT_ENABLED = 0;
  private static final String CELLULAR_TO_WLAN_CONNECT_TYPE = "cellular_to_wlan_type";
  private static final int CELLULAR_TO_WLAN_CONNECT_TYPE_ASK = 2;
  private static final int CELLULAR_TO_WLAN_CONNECT_TYPE_AUTO = 0;
  private static final int CELLULAR_TO_WLAN_CONNECT_TYPE_MANUAL = 1;
  private static final String CELLULAR_TO_WLAN_HINT = "cellular_to_wlan_hint";
  private static final int CELLULAR_WLAN_DEFAULT_VALUE = 0;
  private static final String DEFAULT_CERTIFICATE_PATH = "/data/misc/wapi_certificate";
  private static final int DO_NOT_NOTIFY_USER = -1;
  private static final String KEY_AUTO_CONNECT_ENABLE = "auto_connect_type";
  private static final String KEY_CELLULAR_TO_WLAN = "cellular_to_wlan";
  private static final String KEY_CELLULAR_TO_WLAN_HINT = "cellular_to_wlan_hint";
  private static final String KEY_CONNECT_NOTIFY = "notify_ap_connected";
  private static final String KEY_CURRENT_GATEWAY = "current_gateway";
  private static final String KEY_CURRENT_NETMASK = "current_netmask";
  private static final String KEY_ENABLE_HS2 = "enable_hs2";
  private static final String KEY_FREQUENCY_BAND = "frequency_band";
  private static final String KEY_INSTALL_CREDENTIALS = "install_credentials";
  private static final String KEY_PRIORITY_SETTINGS = "wifi_priority_settings";
  private static final String KEY_WAPI_CERT_INSTALL = "wapi_cert_install";
  private static final String KEY_WAPI_CERT_UNINSTALL = "wapi_cert_uninstall";
  private static final String KEY_WIFI_DIRECT = "wifi_direct";
  private static final String KEY_WLAN_TO_CELLULAR_HINT = "wlan_to_cellular_hint";
  private static final String KEY_WPS_PIN = "wps_pin_entry";
  private static final String KEY_WPS_PUSH = "wps_push_button";
  private static final int NOTIFY_USER = 0;
  private static final String NOTIFY_USER_CONNECT = "notify_user_when_connect_cmcc";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      paramAnonymousContext = new ArrayList();
      paramAnonymousContext.add("wifi_priority_settings");
      paramAnonymousContext.add("auto_connect_type");
      paramAnonymousContext.add("enable_hs2");
      paramAnonymousContext.add("notify_ap_connected");
      paramAnonymousContext.add("cellular_to_wlan_hint");
      paramAnonymousContext.add("cellular_to_wlan");
      paramAnonymousContext.add("wlan_to_cellular_hint");
      paramAnonymousContext.add("wifi_direct");
      return paramAnonymousContext;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230884;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final String TAG = "AdvancedWifiSettings";
  private static final int WAPI_INSTALL_DIALOG_ID = 3;
  private static final int WAPI_UNINSTALL_DIALOG_ID = 4;
  private static final String WIFI_AUTO_CONNECT_TYPE = "wifi_auto_connect_type";
  private static final int WIFI_HS2_DISABLED = 0;
  private static final int WIFI_HS2_ENABLED = 1;
  private static final String WLAN_TO_CELLULAR_HINT = "wlan_to_cellular_hint";
  ListPreference frequencyPref;
  private boolean isCertificate = false;
  private CheckBoxPreference mAutoConnectEnablePref;
  private CheckBoxPreference mCellularToWlanHintPref;
  private ListPreference mCellularToWlanPref;
  private ContentObserver mFrequencyPrefObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      if (Settings.System.getInt(AdvancedWifiSettings.-wrap0(AdvancedWifiSettings.this), "airplane_mode_on", 0) == 0) {}
      for (paramAnonymousBoolean = true;; paramAnonymousBoolean = false)
      {
        if (AdvancedWifiSettings.this.frequencyPref != null) {
          AdvancedWifiSettings.this.frequencyPref.setEnabled(paramAnonymousBoolean);
        }
        return;
      }
    }
  };
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      AdvancedWifiSettings.this.certificationExist();
      if (AdvancedWifiSettings.-get1(AdvancedWifiSettings.this) != null) {
        AdvancedWifiSettings.-get1(AdvancedWifiSettings.this).setEnabled(AdvancedWifiSettings.-get0(AdvancedWifiSettings.this));
      }
    }
  };
  private final KeyStore mKeyStore = KeyStore.getInstance();
  private boolean mUnavailable;
  private Preference mUnstallCertPreference;
  private Preference mWapiCertInstall;
  private Dialog mWapiCertMgmtDialog;
  private Preference mWapiCertUninstall;
  private WifiManager mWifiManager;
  
  public AdvancedWifiSettings()
  {
    super("no_config_wifi");
  }
  
  private int getCellularToWlanValue()
  {
    if (isAutoConnectEnabled()) {
      return 0;
    }
    return Settings.Global.getInt(getContentResolver(), "cellular_to_wlan_type", 0);
  }
  
  private boolean ifNotifyConnect()
  {
    boolean bool = false;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "notify_user_when_connect_cmcc", 0) == 0) {
      bool = true;
    }
    return bool;
  }
  
  private void initPreferences()
  {
    Object localObject1 = getActivity();
    Object localObject2 = new Intent("android.credentials.INSTALL_AS_USER");
    ((Intent)localObject2).setClassName("com.android.certinstaller", "com.android.certinstaller.CertInstallerMain");
    ((Intent)localObject2).putExtra("install_as_uid", 1010);
    findPreference("install_credentials").setIntent((Intent)localObject2);
    localObject2 = (SwitchPreference)findPreference("enable_hs2");
    boolean bool;
    label112:
    label216:
    label281:
    label314:
    label366:
    int i;
    if ((localObject2 != null) && (getResources().getBoolean(17957072)))
    {
      ((SwitchPreference)localObject2).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
      {
        public boolean onPreferenceClick(Preference paramAnonymousPreference)
        {
          ContentResolver localContentResolver;
          if ("enable_hs2".equals(paramAnonymousPreference.getKey()))
          {
            localContentResolver = AdvancedWifiSettings.-wrap0(AdvancedWifiSettings.this);
            if (!((SwitchPreference)paramAnonymousPreference).isChecked()) {
              break label42;
            }
          }
          label42:
          for (int i = 1;; i = 0)
          {
            Settings.Global.putInt(localContentResolver, "wifi_hotspot2_enabled", i);
            return true;
          }
        }
      });
      if (Settings.Global.getInt(getContentResolver(), "wifi_hotspot2_enabled", 0) == 1)
      {
        bool = true;
        ((SwitchPreference)localObject2).setChecked(bool);
        localObject1 = new Intent((Context)localObject1, Settings.WifiP2pSettingsActivity.class);
        localObject2 = findPreference("wifi_direct");
        if (localObject2 != null)
        {
          ((Preference)localObject2).setIntent((Intent)localObject1);
          getPreferenceScreen().removePreference((Preference)localObject2);
        }
        findPreference("wps_push_button").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
          public boolean onPreferenceClick(Preference paramAnonymousPreference)
          {
            new AdvancedWifiSettings.WpsFragment(0).show(AdvancedWifiSettings.this.getFragmentManager(), "wps_push_button");
            return true;
          }
        });
        findPreference("wps_pin_entry").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
          public boolean onPreferenceClick(Preference paramAnonymousPreference)
          {
            new AdvancedWifiSettings.WpsFragment(1).show(AdvancedWifiSettings.this.getFragmentManager(), "wps_pin_entry");
            return true;
          }
        });
        this.frequencyPref = ((ListPreference)findPreference("frequency_band"));
        if (Settings.System.getInt(getContentResolver(), "airplane_mode_on", 0) != 0) {
          break label588;
        }
        bool = true;
        if (!this.mWifiManager.isWifiEnabled()) {
          this.frequencyPref.setEnabled(bool);
        }
        if ((this.mWifiManager == null) || (this.mWifiManager.getWifiApState() != 13)) {
          break label593;
        }
        this.frequencyPref.setEnabled(false);
        this.frequencyPref.setValue(String.valueOf(0));
        updateFrequencyBandSummary(this.frequencyPref, 0);
        localObject1 = findPreference("wifi_priority_settings");
        if (localObject1 == null) {
          break label685;
        }
        if (!getResources().getBoolean(2131558424)) {
          getPreferenceScreen().removePreference((Preference)localObject1);
        }
        this.mAutoConnectEnablePref = ((CheckBoxPreference)findPreference("auto_connect_type"));
        if (this.mAutoConnectEnablePref != null)
        {
          if (!getResources().getBoolean(2131558425)) {
            break label697;
          }
          this.mAutoConnectEnablePref.setChecked(isAutoConnectEnabled());
          this.mAutoConnectEnablePref.setOnPreferenceChangeListener(this);
        }
        this.mCellularToWlanPref = ((ListPreference)findPreference("cellular_to_wlan"));
        if (this.mCellularToWlanPref != null)
        {
          if (!getResources().getBoolean(2131558426)) {
            break label712;
          }
          i = getCellularToWlanValue();
          this.mCellularToWlanPref.setValue(String.valueOf(i));
          updateCellToWlanSummary(this.mCellularToWlanPref, i);
          this.mCellularToWlanPref.setOnPreferenceChangeListener(this);
        }
        label432:
        localObject1 = (CheckBoxPreference)findPreference("wlan_to_cellular_hint");
        if (localObject1 != null)
        {
          if (!getResources().getBoolean(2131558429)) {
            break label727;
          }
          ((CheckBoxPreference)localObject1).setChecked(isWlanToCellHintEnable());
          ((CheckBoxPreference)localObject1).setOnPreferenceChangeListener(this);
        }
        label472:
        localObject1 = (CheckBoxPreference)findPreference("notify_ap_connected");
        if (localObject1 != null)
        {
          if (!getResources().getBoolean(2131558427)) {
            break label739;
          }
          ((CheckBoxPreference)localObject1).setChecked(ifNotifyConnect());
          ((CheckBoxPreference)localObject1).setOnPreferenceChangeListener(this);
        }
      }
    }
    for (;;)
    {
      this.mCellularToWlanHintPref = ((CheckBoxPreference)findPreference("cellular_to_wlan_hint"));
      if (this.mCellularToWlanHintPref != null)
      {
        if (!getResources().getBoolean(2131558428)) {
          break label751;
        }
        this.mCellularToWlanHintPref.setChecked(isCellularToWlanHintEnable());
        this.mCellularToWlanHintPref.setOnPreferenceChangeListener(this);
      }
      return;
      bool = false;
      break;
      if (localObject2 == null) {
        break label112;
      }
      getPreferenceScreen().removePreference((Preference)localObject2);
      break label112;
      label588:
      bool = false;
      break label216;
      label593:
      if (this.mWifiManager.isDualBandSupported())
      {
        this.frequencyPref.setOnPreferenceChangeListener(this);
        i = Settings.Global.getInt(getContentResolver(), "wifi_frequency_band", 0);
        if (i != -1)
        {
          this.frequencyPref.setValue(String.valueOf(i));
          updateFrequencyBandSummary(this.frequencyPref, i);
          break label281;
        }
        Log.e("AdvancedWifiSettings", "Failed to fetch frequency band");
        break label281;
      }
      if (this.frequencyPref == null) {
        break label281;
      }
      getPreferenceScreen().removePreference(this.frequencyPref);
      break label281;
      label685:
      Log.d("AdvancedWifiSettings", "Fail to get priority pref...");
      break label314;
      label697:
      getPreferenceScreen().removePreference(this.mAutoConnectEnablePref);
      break label366;
      label712:
      getPreferenceScreen().removePreference(this.mCellularToWlanPref);
      break label432;
      label727:
      getPreferenceScreen().removePreference((Preference)localObject1);
      break label472;
      label739:
      getPreferenceScreen().removePreference((Preference)localObject1);
    }
    label751:
    getPreferenceScreen().removePreference(this.mCellularToWlanHintPref);
  }
  
  private void initWapiCertInstallPreference()
  {
    Preference localPreference = findPreference("wapi_cert_install");
    if (localPreference != null)
    {
      Log.e("AdvancedWifiSettings", "initWapiCertInstallPreference pref != null");
      localPreference.setOnPreferenceClickListener(this);
      return;
    }
    Log.e("AdvancedWifiSettings", "initWapiCertInstallPreference pref == null");
  }
  
  private void initWapiCertUninstallPreference()
  {
    this.mUnstallCertPreference = findPreference("wapi_cert_uninstall");
    this.mUnstallCertPreference.setEnabled(this.isCertificate);
    if (this.mUnstallCertPreference != null)
    {
      Log.e("AdvancedWifiSettings", "initWapiCertUninstallPreference pref != null");
      this.mUnstallCertPreference.setOnPreferenceClickListener(this);
      return;
    }
    Log.e("AdvancedWifiSettings", "initWapiCertUninstallPreference pref == null");
  }
  
  private boolean isAutoConnectEnabled()
  {
    boolean bool = false;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "wifi_auto_connect_type", 0) == 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isCellularToWlanHintEnable()
  {
    boolean bool = false;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "cellular_to_wlan_hint", 0) == 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isWlanToCellHintEnable()
  {
    boolean bool = false;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "wlan_to_cellular_hint", 0) == 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean runKeyguardConfirmation(int paramInt)
  {
    Resources localResources = getActivity().getResources();
    ChooseLockSettingsHelper localChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity(), this);
    CharSequence localCharSequence;
    switch (localChooseLockSettingsHelper.lockMode())
    {
    default: 
      localCharSequence = localResources.getText(2131692003);
    }
    for (;;)
    {
      return localChooseLockSettingsHelper.launchConfirmationActivityExt(paramInt, localCharSequence, localResources.getText(2131690110));
      localCharSequence = localResources.getText(2131692004);
      continue;
      localCharSequence = localResources.getText(2131692005);
    }
  }
  
  private void setApConnectedNotify(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 0;; i = -1)
    {
      Settings.Global.putInt(getActivity().getContentResolver(), "notify_user_when_connect_cmcc", i);
      return;
    }
  }
  
  private void setAutoConnectTypeEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 0;; i = 1)
    {
      Settings.Global.putInt(getActivity().getContentResolver(), "wifi_auto_connect_type", i);
      return;
    }
  }
  
  private void setCellToWlanType(int paramInt)
  {
    try
    {
      Settings.Global.putInt(getContentResolver(), "cellular_to_wlan_type", paramInt);
      return;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Toast.makeText(getActivity(), 2131693764, 0).show();
    }
  }
  
  private void setCellularToWlanHintEnable(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 0;; i = -1)
    {
      Settings.Global.putInt(getActivity().getContentResolver(), "cellular_to_wlan_hint", i);
      return;
    }
  }
  
  private void setWlanToCellularHintEnable(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 0;; i = -1)
    {
      Settings.Global.putInt(getActivity().getContentResolver(), "wlan_to_cellular_hint", i);
      return;
    }
  }
  
  private void showWapiCertInstallDialog()
  {
    WapiCertMgmtDialog localWapiCertMgmtDialog = new WapiCertMgmtDialog(getActivity());
    localWapiCertMgmtDialog.setMode(0);
    localWapiCertMgmtDialog.setTitle(2131690106);
    this.mWapiCertMgmtDialog = localWapiCertMgmtDialog;
    localWapiCertMgmtDialog.setRefreshAdvance(this);
    localWapiCertMgmtDialog.show();
  }
  
  private void showWapiCertUninstallDialog()
  {
    WapiCertMgmtDialog localWapiCertMgmtDialog = new WapiCertMgmtDialog(getActivity());
    localWapiCertMgmtDialog.setMode(1);
    localWapiCertMgmtDialog.setTitle(2131690110);
    this.mWapiCertMgmtDialog = localWapiCertMgmtDialog;
    localWapiCertMgmtDialog.setRefreshAdvance(this);
    localWapiCertMgmtDialog.show();
  }
  
  private void updateAutoConnectPref(boolean paramBoolean)
  {
    setAutoConnectTypeEnabled(paramBoolean);
    this.mAutoConnectEnablePref.setChecked(paramBoolean);
  }
  
  private void updateCellToWlanSummary(Preference paramPreference, int paramInt)
  {
    paramPreference.setSummary(getResources().getStringArray(2131427446)[paramInt]);
  }
  
  private void updateCellularToWifiPrefs(boolean paramBoolean)
  {
    if (!paramBoolean) {
      updateCellularToWlanHintPref(true);
    }
    if (paramBoolean) {}
    for (int i = 0;; i = 1)
    {
      Settings.Global.putInt(getContentResolver(), "cellular_to_wlan_type", i);
      this.mCellularToWlanPref.setValue(String.valueOf(i));
      updateCellToWlanSummary(this.mCellularToWlanPref, i);
      return;
    }
  }
  
  private void updateCellularToWlanHintPref(boolean paramBoolean)
  {
    this.mCellularToWlanHintPref.setChecked(paramBoolean);
    setCellularToWlanHintEnable(paramBoolean);
  }
  
  private void updateFrequencyBandSummary(Preference paramPreference, int paramInt)
  {
    paramPreference.setSummary(getResources().getStringArray(2131427479)[paramInt]);
  }
  
  public void certificationExist()
  {
    getActivity();
    ArrayList localArrayList = new ArrayList();
    Object localObject = new File("/data/misc/wapi_certificate");
    try
    {
      if (!((File)localObject).isDirectory()) {
        return;
      }
      localObject = ((File)localObject).listFiles();
      int i = 0;
      while (i < localObject.length)
      {
        localArrayList.add(localObject[i].getName());
        this.isCertificate = true;
        i += 1;
      }
      if (i == 0) {
        this.isCertificate = false;
      }
      return;
    }
    catch (Exception localException) {}
  }
  
  protected int getMetricsCategory()
  {
    return 104;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    getEmptyTextView().setText(2131691468);
    if (this.mUnavailable) {
      getPreferenceScreen().removeAll();
    }
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if ((paramInt1 == 56) && (paramInt2 == -1)) {
      showWapiCertUninstallDialog();
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (isUiRestricted())
    {
      this.mUnavailable = true;
      setPreferenceScreen(new PreferenceScreen(getPrefContext(), null));
    }
    for (;;)
    {
      getContentResolver().registerContentObserver(Settings.Global.getUriFor("airplane_mode_on"), true, this.mFrequencyPrefObserver, -1);
      return;
      addPreferencesFromResource(2131230884);
      Log.e("AdvancedWifiSettings", "Oncreate findpref.");
      this.mWapiCertInstall = findPreference("wapi_cert_install");
      this.mWapiCertUninstall = findPreference("wapi_cert_uninstall");
    }
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return super.onCreateDialog(paramInt);
    case 3: 
      localWapiCertMgmtDialog = new WapiCertMgmtDialog(getActivity());
      localWapiCertMgmtDialog.setMode(0);
      localWapiCertMgmtDialog.setTitle(2131690106);
      return localWapiCertMgmtDialog;
    }
    WapiCertMgmtDialog localWapiCertMgmtDialog = new WapiCertMgmtDialog(getActivity());
    localWapiCertMgmtDialog.setMode(1);
    localWapiCertMgmtDialog.setTitle(2131690110);
    return localWapiCertMgmtDialog;
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    getContentResolver().unregisterContentObserver(this.mFrequencyPrefObserver);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    Activity localActivity = getActivity();
    String str = paramPreference.getKey();
    if ("frequency_band".equals(str)) {}
    for (;;)
    {
      try
      {
        int i = Integer.parseInt((String)paramObject);
        this.mWifiManager.setFrequencyBand(i, true);
        updateFrequencyBandSummary(paramPreference, i);
        if ("wlan_to_cellular_hint".equals(str)) {
          setWlanToCellularHintEnable(((Boolean)paramObject).booleanValue());
        }
        if ("auto_connect_type".equals(str))
        {
          bool = ((Boolean)paramObject).booleanValue();
          setAutoConnectTypeEnabled(bool);
          updateCellularToWifiPrefs(bool);
          if (!bool) {
            updateCellularToWlanHintPref(true);
          }
        }
        if ("cellular_to_wlan".equals(str))
        {
          i = Integer.parseInt((String)paramObject);
          setCellToWlanType(i);
          this.mCellularToWlanPref.setValue(String.valueOf(i));
          updateCellToWlanSummary(this.mCellularToWlanPref, i);
          if (i != 0) {
            break label261;
          }
          bool = true;
          updateAutoConnectPref(bool);
          if (i != 0) {
            updateCellularToWlanHintPref(true);
          }
        }
        if ("notify_ap_connected".equals(str)) {
          setApConnectedNotify(((Boolean)paramObject).booleanValue());
        }
        if ("cellular_to_wlan_hint".equals(str))
        {
          bool = ((Boolean)paramObject).booleanValue();
          setCellularToWlanHintEnable(bool);
          if (!bool) {
            Toast.makeText(getActivity(), getResources().getString(2131693760), 1).show();
          }
        }
        return true;
      }
      catch (NumberFormatException paramPreference)
      {
        Toast.makeText(localActivity, 2131690302, 0).show();
        return false;
      }
      label261:
      boolean bool = false;
    }
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    paramPreference = paramPreference.getKey();
    Log.e("AdvancedWifiSettings", "onPreferenceClick key " + paramPreference);
    if (paramPreference == null) {
      return true;
    }
    if (paramPreference.equals("wapi_cert_install"))
    {
      Log.e("AdvancedWifiSettings", "onPreferenceClick key 1" + paramPreference);
      if (this.mKeyStore.state() != KeyStore.State.UNLOCKED)
      {
        getActivity().getWindow().setWindowAnimations(0);
        Credentials.getInstance().unlock(getActivity());
        return false;
      }
      showWapiCertInstallDialog();
    }
    do
    {
      do
      {
        return true;
      } while (!paramPreference.equals("wapi_cert_uninstall"));
      Log.e("AdvancedWifiSettings", "onPreferenceClick key 2" + paramPreference);
    } while (runKeyguardConfirmation(56));
    startActivityForResult(new Intent(getActivity(), ChooseLockGeneric.class), 20);
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    certificationExist();
    if (!this.mUnavailable)
    {
      initPreferences();
      initWapiCertInstallPreference();
      initWapiCertUninstallPreference();
    }
  }
  
  public void refreashAdvance()
  {
    this.mHandler.sendEmptyMessage(0);
  }
  
  public static class WpsFragment
    extends DialogFragment
  {
    private static int mWpsSetup;
    
    public WpsFragment() {}
    
    public WpsFragment(int paramInt)
    {
      mWpsSetup = paramInt;
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      return new WpsDialog(getActivity(), mWpsSetup);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\AdvancedWifiSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */