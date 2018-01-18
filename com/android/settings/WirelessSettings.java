package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Global;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.ims.ImsManager;
import com.android.settings.nfc.NfcEnabler;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.codeaurora.wfcservice.IWFCService;
import org.codeaurora.wfcservice.IWFCService.Stub;
import org.codeaurora.wfcservice.IWFCServiceCB;
import org.codeaurora.wfcservice.IWFCServiceCB.Stub;

public class WirelessSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final boolean DEBUG = true;
  public static final String EXIT_ECM_RESULT = "exit_ecm_result";
  private static final String KEY_ANDROID_BEAM_SETTINGS = "android_beam_settings";
  private static final String KEY_MANAGE_MOBILE_PLAN = "manage_mobile_plan";
  private static final String KEY_MOBILE_NETWORK_SETTINGS = "mobile_network_settings";
  private static final String KEY_PROXY_SETTINGS = "proxy_settings";
  private static final String KEY_TETHER_SETTINGS = "tether_settings";
  private static final String KEY_TOGGLE_AIRPLANE = "toggle_airplane";
  private static final String KEY_TOGGLE_NFC = "toggle_nfc";
  private static final String KEY_VPN_SETTINGS = "vpn_settings";
  private static final String KEY_WFC_ENHANCED_SETTINGS = "wifi_calling_enhanced_settings";
  private static final String KEY_WFC_SETTINGS = "wifi_calling_settings";
  private static final String KEY_WIMAX_SETTINGS = "wimax_settings";
  private static final int MANAGE_MOBILE_PLAN_DIALOG_ID = 1;
  public static final int REQUEST_CODE_EXIT_ECM = 1;
  private static final String SAVED_MANAGE_MOBILE_PLAN_MSG = "mManageMobilePlanMessage";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      boolean bool = false;
      ArrayList localArrayList = new ArrayList();
      int i;
      if (((UserManager)paramAnonymousContext.getSystemService("user")).isAdminUser())
      {
        i = 0;
        if (i == 0) {
          bool = paramAnonymousContext.getResources().getBoolean(17956973);
        }
        if (!bool) {
          localArrayList.add("wimax_settings");
        }
        if (i != 0) {
          localArrayList.add("vpn_settings");
        }
        NfcManager localNfcManager = (NfcManager)paramAnonymousContext.getSystemService("nfc");
        if ((localNfcManager != null) && (localNfcManager.getDefaultAdapter() == null))
        {
          localArrayList.add("toggle_nfc");
          localArrayList.add("android_beam_settings");
        }
        if ((i != 0) || (Utils.isWifiOnly(paramAnonymousContext)))
        {
          localArrayList.add("mobile_network_settings");
          localArrayList.add("manage_mobile_plan");
        }
        if (!paramAnonymousContext.getResources().getBoolean(2131558420)) {
          localArrayList.add("manage_mobile_plan");
        }
        if (paramAnonymousContext.getPackageManager().hasSystemFeature("android.hardware.type.television")) {
          localArrayList.add("toggle_airplane");
        }
        localArrayList.add("proxy_settings");
        paramAnonymousContext = (ConnectivityManager)paramAnonymousContext.getSystemService("connectivity");
        if ((i != 0) || (!paramAnonymousContext.isTetheringSupported())) {
          break label250;
        }
      }
      for (;;)
      {
        localArrayList.add("mobile_network_settings");
        localArrayList.add("voice_over_lte");
        localArrayList.add("manage_mobile_plan");
        localArrayList.add("wifi_calling_settings");
        localArrayList.add("wifi_calling_enhanced_settings");
        return localArrayList;
        i = 1;
        break;
        label250:
        localArrayList.add("tether_settings");
      }
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230891;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final String TAG = "WirelessSettings";
  private static final String VOICE_OVER_LTE = "voice_over_lte";
  private AirplaneModeEnabler mAirplaneModeEnabler;
  private SwitchPreference mAirplaneModePreference;
  private PreferenceScreen mButtonWfc;
  private IWFCServiceCB mCallback = new IWFCServiceCB.Stub()
  {
    public void updateWFCMessage(final String paramAnonymousString)
    {
      if ((!WirelessSettings.-get2(WirelessSettings.this)) || (paramAnonymousString == null))
      {
        Log.e("WirelessSettings", "updateWFCMessage fail.");
        return;
      }
      WirelessSettings.this.getActivity().runOnUiThread(new Runnable()
      {
        public void run()
        {
          Log.d("WirelessSettings", "new UI thread.");
          WirelessSettings.-get0(WirelessSettings.this).setSummary(paramAnonymousString);
        }
      });
    }
  };
  private ConnectivityManager mCm;
  private ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      Log.i("WirelessSettings", "AIDLExample connect service");
      WirelessSettings.-set1(WirelessSettings.this, IWFCService.Stub.asInterface(paramAnonymousIBinder));
      try
      {
        WirelessSettings.-get3(WirelessSettings.this).registerCallback(WirelessSettings.-get1(WirelessSettings.this));
        return;
      }
      catch (RemoteException paramAnonymousComponentName) {}
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.i("WirelessSettings", " AIDLExample disconnect service");
      WirelessSettings.-set1(WirelessSettings.this, null);
    }
  };
  private boolean mEnhancedWFCSettingsEnabled = false;
  boolean mIsNetworkSettingsAvailable = false;
  private boolean mLteEnabled = false;
  private String mManageMobilePlanMessage;
  private NfcAdapter mNfcAdapter;
  private NfcEnabler mNfcEnabler;
  private PackageManager mPm;
  private TelephonyManager mTm;
  private UserManager mUm;
  private SwitchPreference mVoLtePreference;
  private IWFCService mWFCService;
  
  private void log(String paramString)
  {
    Log.d("WirelessSettings", paramString);
  }
  
  private void unbindWFCService()
  {
    if (!this.mEnhancedWFCSettingsEnabled) {
      return;
    }
    if (this.mWFCService != null) {}
    try
    {
      Log.d("WirelessSettings", "WFCService unbindService");
      this.mWFCService.unregisterCallback(this.mCallback);
      getActivity().unbindService(this.mConnection);
      Log.d("WirelessSettings", "WFCService unbind error ");
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("WirelessSettings", "WFCService unregister error " + localRemoteException);
      }
    }
  }
  
  private void updateCallback()
  {
    Log.i("WirelessSettings", "call back from settings is called");
  }
  
  protected int getHelpResource()
  {
    return 2131693016;
  }
  
  protected int getMetricsCategory()
  {
    return 110;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 1)
    {
      boolean bool = paramIntent.getBooleanExtra("exit_ecm_result", false);
      this.mAirplaneModeEnabler.setAirplaneModeInECM(Boolean.valueOf(bool).booleanValue(), this.mAirplaneModePreference.isChecked());
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null) {
      this.mManageMobilePlanMessage = paramBundle.getString("mManageMobilePlanMessage");
    }
    log("onCreate: mManageMobilePlanMessage=" + this.mManageMobilePlanMessage);
    this.mCm = ((ConnectivityManager)getSystemService("connectivity"));
    this.mTm = ((TelephonyManager)getSystemService("phone"));
    this.mPm = getPackageManager();
    this.mUm = ((UserManager)getSystemService("user"));
    addPreferencesFromResource(2131230891);
    boolean bool = this.mUm.isAdminUser();
    paramBundle = getActivity();
    this.mAirplaneModePreference = ((SwitchPreference)findPreference("toggle_airplane"));
    Object localObject1 = (SwitchPreference)findPreference("toggle_nfc");
    Object localObject2 = (RestrictedPreference)findPreference("android_beam_settings");
    this.mAirplaneModeEnabler = new AirplaneModeEnabler(paramBundle, this.mAirplaneModePreference);
    this.mNfcEnabler = new NfcEnabler(paramBundle, (SwitchPreference)localObject1, (RestrictedPreference)localObject2);
    this.mEnhancedWFCSettingsEnabled = getActivity().getResources().getBoolean(2131558468);
    this.mButtonWfc = ((PreferenceScreen)findPreference("wifi_calling_enhanced_settings"));
    removePreference("wifi_calling_settings");
    this.mButtonWfc = ((PreferenceScreen)findPreference("wifi_calling_settings"));
    removePreference("wifi_calling_enhanced_settings");
    if (this.mEnhancedWFCSettingsEnabled)
    {
      localObject3 = new Intent();
      ((Intent)localObject3).setAction("com.qualcomm.qti.wfcservice.IWFCService");
      ((Intent)localObject3).setPackage("com.qualcomm.qti.wfcservice");
      paramBundle.bindService((Intent)localObject3, this.mConnection, 1);
    }
    Object localObject3 = Settings.Global.getString(paramBundle.getContentResolver(), "airplane_mode_toggleable_radios");
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    Preference localPreference = findPreference("wimax_settings");
    if (localPreference != null) {
      localPreferenceScreen.removePreference(localPreference);
    }
    label392:
    label471:
    label556:
    int i;
    if ((localObject3 != null) && (((String)localObject3).contains("wifi")))
    {
      if ((!bool) || (RestrictedLockUtils.hasBaseUserRestriction(paramBundle, "no_config_vpn", UserHandle.myUserId()))) {
        removePreference("vpn_settings");
      }
      if (((localObject3 != null) && (((String)localObject3).contains("bluetooth"))) && ((localObject3 == null) || (!((String)localObject3).contains("nfc")))) {
        break label663;
      }
      this.mNfcAdapter = NfcAdapter.getDefaultAdapter(paramBundle);
      if (this.mNfcAdapter == null)
      {
        getPreferenceScreen().removePreference((Preference)localObject1);
        getPreferenceScreen().removePreference((Preference)localObject2);
        this.mNfcEnabler = null;
      }
      if ((bool) && (!Utils.isWifiOnly(getActivity())) && (!RestrictedLockUtils.hasBaseUserRestriction(paramBundle, "no_config_mobile_networks", UserHandle.myUserId()))) {
        break label688;
      }
      removePreference("mobile_network_settings");
      removePreference("manage_mobile_plan");
      getResources().getBoolean(2131558420);
      if (findPreference("manage_mobile_plan") != null) {
        removePreference("manage_mobile_plan");
      }
      if (this.mPm.hasSystemFeature("android.hardware.type.television")) {
        removePreference("toggle_airplane");
      }
      localObject1 = findPreference("proxy_settings");
      localObject2 = (DevicePolicyManager)paramBundle.getSystemService("device_policy");
      getPreferenceScreen().removePreference((Preference)localObject1);
      if (((DevicePolicyManager)localObject2).getGlobalProxyAdmin() != null) {
        break label702;
      }
      bool = true;
      ((Preference)localObject1).setEnabled(bool);
      localObject1 = (ConnectivityManager)paramBundle.getSystemService("connectivity");
      bool = paramBundle.getResources().getBoolean(2131558436);
      removePreference("mobile_network_settings");
      if (RestrictedLockUtils.checkIfRestrictionEnforced(paramBundle, "no_config_tethering", UserHandle.myUserId()) == null) {
        break label707;
      }
      i = 1;
      label605:
      if (((((ConnectivityManager)localObject1).isTetheringSupported()) || (i != 0)) && (!RestrictedLockUtils.hasBaseUserRestriction(paramBundle, "no_config_tethering", UserHandle.myUserId())) && (!bool)) {
        break label712;
      }
      getPreferenceScreen().removePreference(findPreference("tether_settings"));
    }
    label663:
    label688:
    label702:
    label707:
    label712:
    while (i != 0)
    {
      return;
      findPreference("vpn_settings").setDependency("toggle_airplane");
      break;
      findPreference("toggle_nfc").setDependency("toggle_airplane");
      findPreference("android_beam_settings").setDependency("toggle_airplane");
      break label392;
      this.mIsNetworkSettingsAvailable = Utils.isNetworkSettingsApkAvailable(getActivity());
      break label471;
      bool = false;
      break label556;
      i = 0;
      break label605;
    }
    paramBundle = findPreference("tether_settings");
    paramBundle.setTitle(com.android.settingslib.Utils.getTetheringLabel((ConnectivityManager)localObject1));
    if (getResources().getBoolean(2131558466))
    {
      ((RestrictedPreference)paramBundle).useAdminDisabledSummary(false);
      paramBundle.setSummary(2131693788);
    }
    if (TetherSettings.isProvisioningNeededButUnavailable(getActivity())) {}
    for (bool = false;; bool = true)
    {
      paramBundle.setEnabled(bool);
      this.mLteEnabled = getActivity().getResources().getBoolean(2131558452);
      this.mVoLtePreference = ((SwitchPreference)findPreference("voice_over_lte"));
      if (!this.mLteEnabled) {
        break;
      }
      this.mVoLtePreference.setChecked(ImsManager.isEnhanced4gLteModeSettingEnabledByUser(getActivity()));
      return;
    }
    getPreferenceScreen().removePreference(this.mVoLtePreference);
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    log("onCreateDialog: dialogId=" + paramInt);
    switch (paramInt)
    {
    default: 
      return super.onCreateDialog(paramInt);
    }
    new AlertDialog.Builder(getActivity()).setMessage(this.mManageMobilePlanMessage).setCancelable(false).setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        WirelessSettings.-wrap0(WirelessSettings.this, "MANAGE_MOBILE_PLAN_DIALOG.onClickListener id=" + paramAnonymousInt);
        WirelessSettings.-set0(WirelessSettings.this, null);
      }
    }).create();
  }
  
  public void onDestroy()
  {
    unbindWFCService();
    super.onDestroy();
  }
  
  public void onManageMobilePlanClick()
  {
    log("onManageMobilePlanClick:");
    this.mManageMobilePlanMessage = null;
    Object localObject1 = getActivity().getResources();
    Object localObject2 = this.mCm.getActiveNetworkInfo();
    List localList;
    if ((this.mTm.hasIccCard()) && (localObject2 != null))
    {
      localObject2 = new Intent("android.intent.action.ACTION_CARRIER_SETUP");
      localList = this.mTm.getCarrierPackageNamesForIntent((Intent)localObject2);
      if ((localList == null) || (localList.isEmpty()))
      {
        localObject2 = this.mCm.getMobileProvisioningUrl();
        if (TextUtils.isEmpty((CharSequence)localObject2)) {
          break label235;
        }
        localObject1 = Intent.makeMainSelectorActivity("android.intent.action.MAIN", "android.intent.category.APP_BROWSER");
        ((Intent)localObject1).setData(Uri.parse((String)localObject2));
        ((Intent)localObject1).setFlags(272629760);
      }
    }
    for (;;)
    {
      try
      {
        startActivity((Intent)localObject1);
        if (!TextUtils.isEmpty(this.mManageMobilePlanMessage))
        {
          log("onManageMobilePlanClick: message=" + this.mManageMobilePlanMessage);
          showDialog(1);
        }
        return;
        if (localList.size() != 1) {
          Log.w("WirelessSettings", "Multiple matching carrier apps found, launching the first.");
        }
        ((Intent)localObject2).setPackage((String)localList.get(0));
        startActivity((Intent)localObject2);
        return;
      }
      catch (ActivityNotFoundException localActivityNotFoundException)
      {
        Log.w("WirelessSettings", "onManageMobilePlanClick: startActivity failed" + localActivityNotFoundException);
        continue;
      }
      label235:
      localObject2 = this.mTm.getSimOperatorName();
      if (TextUtils.isEmpty((CharSequence)localObject2))
      {
        localObject2 = this.mTm.getNetworkOperatorName();
        if (TextUtils.isEmpty((CharSequence)localObject2)) {
          this.mManageMobilePlanMessage = localActivityNotFoundException.getString(2131691939);
        } else {
          this.mManageMobilePlanMessage = localActivityNotFoundException.getString(2131691940, new Object[] { localObject2 });
        }
      }
      else
      {
        this.mManageMobilePlanMessage = localActivityNotFoundException.getString(2131691940, new Object[] { localObject2 });
        continue;
        if (!this.mTm.hasIccCard()) {
          this.mManageMobilePlanMessage = localActivityNotFoundException.getString(2131691941);
        } else {
          this.mManageMobilePlanMessage = localActivityNotFoundException.getString(2131691942);
        }
      }
    }
  }
  
  public void onMobileNetworkSettingsClick()
  {
    try
    {
      startActivity(new Intent("oneplus.intent.action.SIM_AND_NETWORK_SETTINGS"));
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException) {}
  }
  
  public void onPause()
  {
    super.onPause();
    this.mAirplaneModeEnabler.pause();
    if (this.mNfcEnabler != null) {
      this.mNfcEnabler.pause();
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    log("onPreferenceTreeClick: preference=" + paramPreference);
    if ((paramPreference == this.mAirplaneModePreference) && (Boolean.parseBoolean(SystemProperties.get("ril.cdma.inecmmode"))))
    {
      startActivityForResult(new Intent("android.intent.action.ACTION_SHOW_NOTICE_ECM_BLOCK_OTHERS", null), 1);
      return true;
    }
    if (paramPreference == findPreference("manage_mobile_plan")) {
      onManageMobilePlanClick();
    }
    do
    {
      for (;;)
      {
        return super.onPreferenceTreeClick(paramPreference);
        if ((!this.mLteEnabled) || (paramPreference != this.mVoLtePreference)) {
          break;
        }
        ImsManager.setEnhanced4gLteModeSetting(getActivity(), this.mVoLtePreference.isChecked());
      }
    } while ((paramPreference != findPreference("mobile_network_settings")) || (!this.mIsNetworkSettingsAvailable));
    onMobileNetworkSettingsClick();
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    this.mAirplaneModeEnabler.resume();
    if (this.mNfcEnabler != null) {
      this.mNfcEnabler.resume();
    }
    Activity localActivity = getActivity();
    if ((ImsManager.isWfcEnabledByPlatform(localActivity)) && (this.mButtonWfc != null))
    {
      getPreferenceScreen().addPreference(this.mButtonWfc);
      if (!this.mEnhancedWFCSettingsEnabled) {
        this.mButtonWfc.setSummary(WifiCallingSettings.getWfcModeSummary(localActivity, ImsManager.getWfcMode(localActivity)));
      }
    }
    do
    {
      return;
      if (!ImsManager.isWfcEnabledByUser(localActivity))
      {
        this.mButtonWfc.setSummary(2131692124);
        return;
      }
      this.mButtonWfc.setSummary(SystemProperties.get("sys.wificall.status.msg"));
      return;
      log("WFC not supported. Remove WFC menu");
    } while (this.mButtonWfc == null);
    getPreferenceScreen().removePreference(this.mButtonWfc);
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (!TextUtils.isEmpty(this.mManageMobilePlanMessage)) {
      paramBundle.putString("mManageMobilePlanMessage", this.mManageMobilePlanMessage);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\WirelessSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */