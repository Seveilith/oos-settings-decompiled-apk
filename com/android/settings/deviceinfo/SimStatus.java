package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.CarrierConfigManager;
import android.telephony.CellBroadcastMessage;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import com.android.internal.telephony.DefaultPhoneNotifier;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.DeviceInfoUtils;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPSNSUtils;
import java.util.List;
import org.codeaurora.ims.utils.QtiImsExtUtils;

public class SimStatus
  extends SettingsPreferenceFragment
{
  static final String CB_AREA_INFO_RECEIVED_ACTION = "android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED";
  static final String CB_AREA_INFO_SENDER_PERMISSION = "android.permission.RECEIVE_EMERGENCY_BROADCAST";
  private static final String COUNTRY_ABBREVIATION_BRAZIL = "br";
  static final String GET_LATEST_CB_AREA_INFO_ACTION = "android.cellbroadcastreceiver.GET_LATEST_CB_AREA_INFO";
  private static final String KEY_DATA_STATE = "data_state";
  private static final String KEY_ICCID = "iccid";
  private static final String KEY_IMEI = "imei";
  private static final String KEY_IMEI_SV = "imei_sv";
  private static final String KEY_LATEST_AREA_INFO = "latest_area_info";
  private static final String KEY_NETWORK_TYPE = "network_type";
  private static final String KEY_OPERATOR_NAME = "operator_name";
  private static final String KEY_PHONE_NUMBER = "number";
  private static final String KEY_ROAMING_STATE = "roaming_state";
  private static final String KEY_SERVICE_STATE = "service_state";
  private static final String KEY_SIGNAL_STRENGTH = "signal_strength";
  private static final String TAG = "SimStatus";
  private BroadcastReceiver mAreaInfoReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED".equals(paramAnonymousIntent.getAction()))
      {
        paramAnonymousContext = paramAnonymousIntent.getExtras();
        if (paramAnonymousContext == null) {
          return;
        }
        paramAnonymousContext = (CellBroadcastMessage)paramAnonymousContext.get("message");
        if ((paramAnonymousContext != null) && (paramAnonymousContext.getServiceCategory() == 50) && (SimStatus.-get2(SimStatus.this).getSubscriptionId() == paramAnonymousContext.getSubId()))
        {
          paramAnonymousContext = paramAnonymousContext.getMessageBody();
          SimStatus.-wrap1(SimStatus.this, paramAnonymousContext);
        }
      }
    }
  };
  private CarrierConfigManager mCarrierConfigManager;
  private String mDefaultText;
  private TabHost.TabContentFactory mEmptyTabContent = new TabHost.TabContentFactory()
  {
    public View createTabContent(String paramAnonymousString)
    {
      return new View(SimStatus.-get3(SimStatus.this).getContext());
    }
  };
  private ListView mListView;
  private Phone mPhone = null;
  private PhoneStateListener mPhoneStateListener;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (("android.intent.action.ACTION_SUBINFO_CONTENT_CHANGE".equals(paramAnonymousIntent.getAction())) && (SimStatus.-get1(SimStatus.this) != null) && (SimStatus.-get3(SimStatus.this) != null))
      {
        SimStatus.-get3(SimStatus.this).setup();
        SimStatus.-get3(SimStatus.this).setOnTabChangedListener(SimStatus.-get4(SimStatus.this));
        SimStatus.-get3(SimStatus.this).clearAllTabs();
        int i = 0;
        while (i < SimStatus.-get1(SimStatus.this).size())
        {
          TelephonyManager.getDefault().getSimState(SubscriptionManager.getSlotId(i));
          SimStatus.-get3(SimStatus.this).addTab(SimStatus.-wrap0(SimStatus.this, String.valueOf(i), String.valueOf(OPSNSUtils.getSimName(SimStatus.this.getContext(), i))));
          i += 1;
        }
      }
    }
  };
  private Resources mRes;
  private ServiceState mSS = null;
  private List<SubscriptionInfo> mSelectableSubInfos;
  private boolean mShowICCID;
  private boolean mShowLatestAreaInfo;
  private Preference mSignalStrength;
  private SubscriptionInfo mSir;
  private TabHost mTabHost;
  private TabHost.OnTabChangeListener mTabListener = new TabHost.OnTabChangeListener()
  {
    public void onTabChanged(String paramAnonymousString)
    {
      int i = Integer.parseInt(paramAnonymousString);
      SimStatus.-set0(SimStatus.this, (SubscriptionInfo)SimStatus.-get1(SimStatus.this).get(i));
      if ((SimStatus.-get0(SimStatus.this) != null) && (SimStatus.-get5(SimStatus.this) != null)) {
        SimStatus.-get5(SimStatus.this).listen(SimStatus.-get0(SimStatus.this), 0);
      }
      SimStatus.-wrap4(SimStatus.this);
      SimStatus.-get5(SimStatus.this).listen(SimStatus.-get0(SimStatus.this), 321);
      SimStatus.-wrap2(SimStatus.this);
      SimStatus.-wrap3(SimStatus.this);
      SimStatus.-wrap5(SimStatus.this);
    }
  };
  private TabWidget mTabWidget;
  private TelephonyManager mTelephonyManager;
  
  private TabHost.TabSpec buildTabSpec(String paramString1, String paramString2)
  {
    return this.mTabHost.newTabSpec(paramString1).setIndicator(paramString2).setContent(this.mEmptyTabContent);
  }
  
  private void removePreferenceFromScreen(String paramString)
  {
    paramString = findPreference(paramString);
    if (paramString != null) {
      getPreferenceScreen().removePreference(paramString);
    }
  }
  
  private void setSummaryText(String paramString1, String paramString2)
  {
    String str = paramString2;
    if (TextUtils.isEmpty(paramString2)) {
      str = this.mDefaultText;
    }
    paramString1 = findPreference(paramString1);
    if (paramString1 != null) {
      paramString1.setSummary(str);
    }
  }
  
  private void updateAreaInfo(String paramString)
  {
    if (paramString != null) {
      setSummaryText("latest_area_info", paramString);
    }
  }
  
  private void updateDataState()
  {
    int i = DefaultPhoneNotifier.convertDataState(this.mPhone.getDataConnectionState());
    String str = this.mRes.getString(2131690818);
    switch (i)
    {
    }
    for (;;)
    {
      setSummaryText("data_state", str);
      return;
      str = this.mRes.getString(2131690816);
      continue;
      str = this.mRes.getString(2131690817);
      continue;
      str = this.mRes.getString(2131690815);
      continue;
      str = this.mRes.getString(2131690814);
    }
  }
  
  private void updateNetworkType()
  {
    localObject1 = null;
    int i = this.mSir.getSubscriptionId();
    int j = this.mTelephonyManager.getDataNetworkType(this.mSir.getSubscriptionId());
    int k = this.mTelephonyManager.getVoiceNetworkType(this.mSir.getSubscriptionId());
    if (j != 0)
    {
      localObject1 = this.mTelephonyManager;
      localObject1 = TelephonyManager.getNetworkTypeName(j);
    }
    for (;;)
    {
      int n = 0;
      try
      {
        localObject2 = SettingsBaseApplication.mApplication.createPackageContext("com.android.systemui", 0);
        int m = ((Context)localObject2).getResources().getIdentifier("config_show4GForLTE", "bool", "com.android.systemui");
        boolean bool = ((Context)localObject2).getResources().getBoolean(m);
        n = bool;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        for (;;)
        {
          Object localObject2;
          Log.e("SimStatus", "NameNotFoundException for show4GFotLTE");
          continue;
          Object localObject3 = localObject1;
          if (((String)localObject1).equals("LTE_CA"))
          {
            localObject3 = "4G_CA";
            continue;
            localObject1 = getResources().getString(2131693796);
          }
        }
      }
      localObject2 = localObject1;
      if (localObject1 != null)
      {
        localObject2 = localObject1;
        if (n != 0)
        {
          if (!((String)localObject1).equals("LTE")) {
            break;
          }
          localObject2 = "4G";
        }
      }
      localObject1 = localObject2;
      if (QtiImsExtUtils.isCarrierOneSupported()) {
        if (13 != j)
        {
          localObject1 = localObject2;
          if (13 != k) {}
        }
        else
        {
          if (!this.mTelephonyManager.isImsRegisteredForSubscriber(i)) {
            break label312;
          }
          localObject1 = getResources().getString(2131693795);
        }
      }
      localObject2 = localObject1;
      if (this.mSS != null)
      {
        localObject2 = localObject1;
        if (this.mSS.isUsingCarrierAggregation())
        {
          localObject2 = localObject1;
          if (!TextUtils.isEmpty((CharSequence)localObject1))
          {
            localObject2 = localObject1;
            if (TextUtils.equals("4G", (CharSequence)localObject1)) {
              localObject2 = "4G+";
            }
          }
        }
      }
      setSummaryText("network_type", (String)localObject2);
      return;
      if (k != 0)
      {
        localObject1 = this.mTelephonyManager;
        localObject1 = TelephonyManager.getNetworkTypeName(k);
      }
    }
  }
  
  private void updatePhoneInfos()
  {
    if (this.mSir != null)
    {
      Phone localPhone = PhoneFactory.getPhone(SubscriptionManager.getPhoneId(this.mSir.getSubscriptionId()));
      if ((UserManager.get(getContext()).isAdminUser()) && (SubscriptionManager.isValidSubscriptionId(this.mSir.getSubscriptionId())))
      {
        if (localPhone == null)
        {
          Log.e("SimStatus", "Unable to locate a phone object for the given Subscription ID.");
          return;
        }
        this.mPhone = localPhone;
        if ((this.mPhoneStateListener != null) && (this.mTelephonyManager != null)) {
          this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        }
        this.mPhoneStateListener = new PhoneStateListener(this.mSir.getSubscriptionId())
        {
          public void onDataConnectionStateChanged(int paramAnonymousInt)
          {
            SimStatus.-wrap2(SimStatus.this);
            SimStatus.-wrap3(SimStatus.this);
          }
          
          public void onServiceStateChanged(ServiceState paramAnonymousServiceState)
          {
            SimStatus.-wrap6(SimStatus.this, paramAnonymousServiceState);
            SimStatus.-wrap3(SimStatus.this);
          }
          
          public void onSignalStrengthsChanged(SignalStrength paramAnonymousSignalStrength)
          {
            SimStatus.this.updateSignalStrength(paramAnonymousSignalStrength);
          }
        };
      }
    }
  }
  
  private void updatePreference()
  {
    if ((this.mPhone.getPhoneType() != 2) && ("br".equals(this.mTelephonyManager.getSimCountryIso(this.mSir.getSubscriptionId())))) {
      this.mShowLatestAreaInfo = true;
    }
    this.mShowICCID = this.mCarrierConfigManager.getConfigForSubId(this.mSir.getSubscriptionId()).getBoolean("show_iccid_in_sim_status_bool");
    setSummaryText("number", DeviceInfoUtils.getFormattedPhoneNumber(getContext(), this.mSir));
    setSummaryText("imei", this.mPhone.getImei());
    setSummaryText("imei_sv", this.mPhone.getDeviceSvn());
    if (!this.mShowICCID) {
      removePreferenceFromScreen("iccid");
    }
    for (;;)
    {
      if (!this.mShowLatestAreaInfo) {
        removePreferenceFromScreen("latest_area_info");
      }
      return;
      setSummaryText("iccid", this.mTelephonyManager.getSimSerialNumber(this.mSir.getSubscriptionId()));
    }
  }
  
  private void updateServiceState(ServiceState paramServiceState)
  {
    this.mSS = paramServiceState;
    int i = paramServiceState.getState();
    String str = this.mRes.getString(2131690818);
    switch (i)
    {
    default: 
      setSummaryText("service_state", str);
      if (paramServiceState.getRoaming()) {
        setSummaryText("roaming_state", this.mRes.getString(2131690809));
      }
      break;
    }
    for (;;)
    {
      if (!"true".equals(SystemProperties.get("persist.radio.ptcrb.enable", "false"))) {
        break label192;
      }
      setSummaryText("operator_name", paramServiceState.getOperatorAlphaShort());
      return;
      str = this.mRes.getString(2131690805);
      break;
      this.mSignalStrength.setSummary("0");
      str = this.mRes.getString(2131690806);
      break;
      str = this.mRes.getString(2131690808);
      this.mSignalStrength.setSummary("0");
      break;
      setSummaryText("roaming_state", this.mRes.getString(2131690810));
    }
    label192:
    setSummaryText("operator_name", OPSNSUtils.getLocalString(getContext(), paramServiceState.getOperatorAlphaLong()));
  }
  
  protected int getMetricsCategory()
  {
    return 43;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
    super.onCreate(paramBundle);
    this.mTelephonyManager = ((TelephonyManager)getSystemService("phone"));
    this.mCarrierConfigManager = ((CarrierConfigManager)getSystemService("carrier_config"));
    this.mSelectableSubInfos = SubscriptionManager.from(getContext()).getActiveSubscriptionInfoList();
    addPreferencesFromResource(2131230759);
    this.mRes = getResources();
    this.mDefaultText = this.mRes.getString(2131690786);
    this.mSignalStrength = findPreference("signal_strength");
    paramBundle = new IntentFilter("android.intent.action.ACTION_SUBINFO_CONTENT_CHANGE");
    getContext().registerReceiver(this.mReceiver, paramBundle);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    Object localObject = null;
    if (this.mSelectableSubInfos == null) {
      this.mSir = null;
    }
    do
    {
      return super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
      if (this.mSelectableSubInfos.size() > 0) {
        localObject = (SubscriptionInfo)this.mSelectableSubInfos.get(0);
      }
      this.mSir = ((SubscriptionInfo)localObject);
    } while (this.mSelectableSubInfos.size() <= 1);
    localObject = paramLayoutInflater.inflate(2130968729, paramViewGroup, false);
    ViewGroup localViewGroup = (ViewGroup)((View)localObject).findViewById(2131361935);
    Utils.prepareCustomPreferencesList(paramViewGroup, (View)localObject, localViewGroup, false);
    localViewGroup.addView(super.onCreateView(paramLayoutInflater, localViewGroup, paramBundle));
    this.mTabHost = ((TabHost)((View)localObject).findViewById(16908306));
    this.mTabWidget = ((TabWidget)((View)localObject).findViewById(16908307));
    this.mListView = ((ListView)((View)localObject).findViewById(16908298));
    this.mTabHost.setup();
    this.mTabHost.setOnTabChangedListener(this.mTabListener);
    this.mTabHost.clearAllTabs();
    int i = 0;
    while (i < this.mSelectableSubInfos.size())
    {
      this.mTabHost.addTab(buildTabSpec(String.valueOf(i), String.valueOf(OPSNSUtils.getSimName(getContext(), i))));
      i += 1;
    }
    return (View)localObject;
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    try
    {
      getContext().unregisterReceiver(this.mReceiver);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.mPhone != null) {
      this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }
    if (this.mShowLatestAreaInfo) {
      getContext().unregisterReceiver(this.mAreaInfoReceiver);
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mPhone != null)
    {
      updatePreference();
      updateSignalStrength(this.mPhone.getSignalStrength());
      updateServiceState(this.mPhone.getServiceState());
      updateDataState();
      this.mTelephonyManager.listen(this.mPhoneStateListener, 321);
      if (this.mShowLatestAreaInfo)
      {
        getContext().registerReceiver(this.mAreaInfoReceiver, new IntentFilter("android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED"), "android.permission.RECEIVE_EMERGENCY_BROADCAST", null);
        Intent localIntent = new Intent("android.cellbroadcastreceiver.GET_LATEST_CB_AREA_INFO");
        getContext().sendBroadcastAsUser(localIntent, UserHandle.ALL, "android.permission.RECEIVE_EMERGENCY_BROADCAST");
      }
    }
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    updatePhoneInfos();
  }
  
  void updateSignalStrength(SignalStrength paramSignalStrength)
  {
    Resources localResources;
    int j;
    if (this.mSignalStrength != null)
    {
      i = this.mPhone.getServiceState().getState();
      if (isAdded())
      {
        localResources = getResources();
        j = this.mPhone.getServiceState().getDataRegState();
        if ((1 != i) || (1 != j)) {
          break label73;
        }
      }
      label73:
      while (3 == i)
      {
        this.mSignalStrength.setSummary("0");
        return;
        Log.e("SimStatus", "fragment not attached to activity");
        return;
      }
      if (paramSignalStrength.getLteLevel() != 0) {
        break label150;
      }
    }
    label150:
    for (int i = paramSignalStrength.getDbm();; i = paramSignalStrength.getLteDbm())
    {
      int k = paramSignalStrength.getAsuLevel();
      j = i;
      if (-1 == i) {
        j = 0;
      }
      i = k;
      if (-1 == k) {
        i = 0;
      }
      this.mSignalStrength.setSummary(localResources.getString(2131693109, new Object[] { Integer.valueOf(j), Integer.valueOf(i) }));
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\SimStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */