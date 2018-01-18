package com.android.settings.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.net.IpConfiguration;
import android.net.IpConfiguration.IpAssignment;
import android.net.IpConfiguration.ProxySettings;
import android.net.LinkAddress;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkUtils;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.UserManager;
import android.security.KeyStore;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.settings.ProxySelector;
import com.android.settings.Utils;
import com.android.settingslib.wifi.AccessPoint;
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;

public class WifiConfigController
  implements TextWatcher, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, TextView.OnEditorActionListener, View.OnKeyListener, View.OnFocusChangeListener
{
  private static final int ASCII = 0;
  private static final String DEFAULT_CERTIFICATE_PATH = "/data/misc/wapi_certificate";
  private static final int DHCP = 0;
  private static final int HEX = 1;
  public static final int PROXY_NONE = 0;
  public static final int PROXY_PAC = 2;
  public static final int PROXY_STATIC = 1;
  private static final int SAFE_SELECT_8021XEAP = 3;
  private static final int STATIC_IP = 1;
  private static final String SYSTEM_CA_STORE_PATH = "/system/etc/security/cacerts";
  private static final String TAG = "WifiConfigController";
  public static final int WIFI_EAP_METHOD_AKA = 5;
  public static final int WIFI_EAP_METHOD_AKA_PRIME = 6;
  public static final int WIFI_EAP_METHOD_PEAP = 0;
  public static final int WIFI_EAP_METHOD_PWD = 3;
  public static final int WIFI_EAP_METHOD_SIM = 4;
  public static final int WIFI_EAP_METHOD_TLS = 1;
  public static final int WIFI_EAP_METHOD_TTLS = 2;
  public static final int WIFI_PEAP_PHASE2_GTC = 2;
  public static final int WIFI_PEAP_PHASE2_MSCHAPV2 = 1;
  public static final int WIFI_PEAP_PHASE2_NONE = 0;
  private final AccessPoint mAccessPoint;
  private int mAccessPointSecurity;
  private ArrayList<String> mCerPathString;
  public int mCert_Cnt = 0;
  private boolean mCert_Set = false;
  private String mCertificateAs;
  private String mCertificateAsPath;
  private Spinner mCertificateSpinner;
  private TextView mCertificateText;
  private String mCertificateUser;
  private String mCertificateUserPath;
  private Spinner mCfgCertificateSpinner;
  private final WifiConfigUiBase mConfigUi;
  private Context mContext;
  private TextView mDns1View;
  private TextView mDns2View;
  private String mDoNotProvideEapUserCertString;
  private String mDoNotValidateEapServerString;
  private TextView mEapAnonymousView;
  private Spinner mEapCaCertSpinner;
  private TextView mEapDomainView;
  private TextView mEapIdentityView;
  private Spinner mEapMethodSpinner;
  private Spinner mEapUserCertSpinner;
  private TextView mGatewayView;
  private ProxyInfo mHttpProxy = null;
  private TextView mIpAddressView;
  private IpConfiguration.IpAssignment mIpAssignment = IpConfiguration.IpAssignment.UNASSIGNED;
  private Spinner mIpSettingsSpinner;
  private String[] mLevels;
  private int mMode;
  private String mMultipleCertSetString;
  private TextView mNetworkPrefixLengthView;
  private TextView mPasswordView;
  private ArrayAdapter<String> mPhase2Adapter;
  private final ArrayAdapter<String> mPhase2FullAdapter;
  private final ArrayAdapter<String> mPhase2PeapAdapter;
  private Spinner mPhase2Spinner;
  private TextView mProxyExclusionListView;
  private TextView mProxyHostView;
  private TextView mProxyPacView;
  private TextView mProxyPortView;
  private IpConfiguration.ProxySettings mProxySettings = IpConfiguration.ProxySettings.UNASSIGNED;
  private Spinner mProxySettingsSpinner;
  public int mPsk_key_type = 0;
  private Spinner mSecuritySpinner;
  private CheckBox mSharedCheckBox;
  private Spinner mSimCardSpinner;
  private ArrayList<String> mSimDisplayNames;
  private TextView mSsidView;
  private StaticIpConfiguration mStaticIpConfiguration = null;
  private SubscriptionManager mSubscriptionManager = null;
  private TelephonyManager mTelephonyManager;
  private final Handler mTextViewChangedHandler;
  private String mUnspecifiedCertString;
  private String mUseSystemCertsString;
  private final View mView;
  private Spinner mWapiKeyTypeSpinner;
  private int selectedSimCardNumber;
  
  public WifiConfigController(WifiConfigUiBase paramWifiConfigUiBase, View paramView, AccessPoint paramAccessPoint, int paramInt)
  {
    this.mConfigUi = paramWifiConfigUiBase;
    this.mView = paramView;
    this.mAccessPoint = paramAccessPoint;
    int i;
    if (paramAccessPoint == null)
    {
      i = 0;
      this.mAccessPointSecurity = i;
      this.mMode = paramInt;
      this.mTextViewChangedHandler = new Handler();
      this.mContext = this.mConfigUi.getContext();
      paramAccessPoint = this.mContext.getResources();
      this.mTelephonyManager = ((TelephonyManager)this.mContext.getSystemService("phone"));
      this.mSimDisplayNames = new ArrayList();
      this.mLevels = paramAccessPoint.getStringArray(2131427393);
      this.mPhase2PeapAdapter = new ArrayAdapter(this.mContext, 17367048, paramAccessPoint.getStringArray(2131427401));
      this.mPhase2PeapAdapter.setDropDownViewResource(17367049);
      this.mPhase2FullAdapter = new ArrayAdapter(this.mContext, 17367048, paramAccessPoint.getStringArray(2131427402));
      this.mPhase2FullAdapter.setDropDownViewResource(17367049);
      this.mUnspecifiedCertString = this.mContext.getString(2131691425);
      this.mMultipleCertSetString = this.mContext.getString(2131691426);
      this.mUseSystemCertsString = this.mContext.getString(2131691427);
      this.mDoNotProvideEapUserCertString = this.mContext.getString(2131691428);
      this.mDoNotValidateEapServerString = this.mContext.getString(2131691429);
      this.mIpSettingsSpinner = ((Spinner)this.mView.findViewById(2131362770));
      this.mIpSettingsSpinner.setOnItemSelectedListener(this);
      this.mProxySettingsSpinner = ((Spinner)this.mView.findViewById(2131362761));
      this.mProxySettingsSpinner.setOnItemSelectedListener(this);
      this.mSharedCheckBox = ((CheckBox)this.mView.findViewById(2131362777));
      if (this.mAccessPoint != null) {
        break label600;
      }
      this.mConfigUi.setTitle(2131691365);
      this.mView.findViewById(2131362755).setVisibility(8);
      this.mView.findViewById(2131362753).setVisibility(8);
      this.mView.findViewById(2131362754).setVisibility(8);
      this.mSsidView = ((TextView)this.mView.findViewById(2131362720));
      this.mSsidView.addTextChangedListener(this);
      this.mSecuritySpinner = ((Spinner)this.mView.findViewById(2131362721));
      this.mSecuritySpinner.setOnItemSelectedListener(this);
      this.mView.findViewById(2131362212).setVisibility(0);
      showIpConfigFields();
      showProxyFields();
      this.mView.findViewById(2131362756).setVisibility(0);
      ((CheckBox)this.mView.findViewById(2131362757)).setOnCheckedChangeListener(this);
      this.mConfigUi.setSubmitButton(paramAccessPoint.getString(2131691453));
    }
    label600:
    ViewGroup localViewGroup;
    boolean bool1;
    label723:
    label765:
    label951:
    label962:
    label997:
    label1004:
    NetworkInfo.DetailedState localDetailedState;
    String str;
    for (;;)
    {
      paramWifiConfigUiBase = (UserManager)this.mContext.getSystemService("user");
      if (!UserManager.isSplitSystemUser()) {
        this.mSharedCheckBox.setVisibility(8);
      }
      this.mConfigUi.setCancelButton(paramAccessPoint.getString(2131691455));
      if (this.mConfigUi.getSubmitButton() != null) {
        enableSubmitIfAppropriate();
      }
      return;
      i = paramAccessPoint.getSecurity();
      break;
      this.mConfigUi.setTitle(this.mAccessPoint.getSsid());
      localViewGroup = (ViewGroup)this.mView.findViewById(2131362248);
      boolean bool2 = false;
      bool1 = false;
      if (this.mAccessPoint.isSaved())
      {
        paramWifiConfigUiBase = this.mAccessPoint.getConfig();
        if (paramWifiConfigUiBase.getIpAssignment() != IpConfiguration.IpAssignment.STATIC) {
          break label951;
        }
        this.mIpSettingsSpinner.setSelection(1);
        bool2 = true;
        paramView = paramWifiConfigUiBase.getStaticIpConfiguration();
        bool1 = bool2;
        if (paramView != null)
        {
          bool1 = bool2;
          if (paramView.ipAddress != null)
          {
            addRow(localViewGroup, 2131691407, paramView.ipAddress.getAddress().getHostAddress());
            bool1 = bool2;
          }
        }
        this.mSharedCheckBox.setEnabled(paramWifiConfigUiBase.shared);
        if (!paramWifiConfigUiBase.shared) {
          bool1 = true;
        }
        if (paramWifiConfigUiBase.getProxySettings() != IpConfiguration.ProxySettings.STATIC) {
          break label962;
        }
        this.mProxySettingsSpinner.setSelection(1);
        bool1 = true;
        bool2 = bool1;
        if (paramWifiConfigUiBase != null)
        {
          bool2 = bool1;
          if (paramWifiConfigUiBase.isPasspoint())
          {
            addRow(localViewGroup, 2131691408, String.format(this.mContext.getString(2131691409), new Object[] { paramWifiConfigUiBase.providerFriendlyName }));
            bool2 = bool1;
          }
        }
      }
      if (((!this.mAccessPoint.isSaved()) && (!this.mAccessPoint.isActive())) || (this.mMode != 0))
      {
        showSecurityFields();
        showIpConfigFields();
        showProxyFields();
        paramWifiConfigUiBase = (CheckBox)this.mView.findViewById(2131362757);
        this.mView.findViewById(2131362756).setVisibility(0);
        paramWifiConfigUiBase.setOnCheckedChangeListener(this);
        paramWifiConfigUiBase.setChecked(bool2);
        paramWifiConfigUiBase = this.mView.findViewById(2131362758);
        if (!bool2) {
          break label997;
        }
      }
      for (paramInt = 0;; paramInt = 8)
      {
        paramWifiConfigUiBase.setVisibility(paramInt);
        if (this.mMode != 2) {
          break label1004;
        }
        this.mConfigUi.setSubmitButton(paramAccessPoint.getString(2131691453));
        break;
        this.mIpSettingsSpinner.setSelection(0);
        break label723;
        if (paramWifiConfigUiBase.getProxySettings() == IpConfiguration.ProxySettings.PAC)
        {
          this.mProxySettingsSpinner.setSelection(2);
          bool1 = true;
          break label765;
        }
        this.mProxySettingsSpinner.setSelection(0);
        break label765;
      }
      if (this.mMode == 1)
      {
        this.mConfigUi.setSubmitButton(paramAccessPoint.getString(2131691449));
      }
      else
      {
        localDetailedState = this.mAccessPoint.getDetailedState();
        str = getSignalString();
        if ((localDetailedState != null) || (str == null)) {
          break label1111;
        }
        this.mConfigUi.setSubmitButton(paramAccessPoint.getString(2131691449));
        label1072:
        if ((!this.mAccessPoint.isSaved()) && (!this.mAccessPoint.isActive())) {
          break label1345;
        }
        this.mConfigUi.setForgetButton(paramAccessPoint.getString(2131691451));
      }
    }
    label1111:
    if (localDetailedState != null)
    {
      bool1 = this.mAccessPoint.isEphemeral();
      WifiConfiguration localWifiConfiguration = this.mAccessPoint.getConfig();
      paramView = null;
      paramWifiConfigUiBase = paramView;
      if (localWifiConfiguration != null)
      {
        paramWifiConfigUiBase = paramView;
        if (localWifiConfiguration.isPasspoint()) {
          paramWifiConfigUiBase = localWifiConfiguration.providerFriendlyName;
        }
      }
      addRow(localViewGroup, 2131691404, AccessPoint.getSummary(this.mConfigUi.getContext(), localDetailedState, bool1, paramWifiConfigUiBase));
    }
    if (str != null) {
      addRow(localViewGroup, 2131691403, str);
    }
    paramWifiConfigUiBase = this.mAccessPoint.getInfo();
    if ((paramWifiConfigUiBase != null) && (paramWifiConfigUiBase.getLinkSpeed() != -1)) {
      addRow(localViewGroup, 2131691405, String.format(paramAccessPoint.getString(2131691319), new Object[] { Integer.valueOf(paramWifiConfigUiBase.getLinkSpeed()) }));
    }
    if ((paramWifiConfigUiBase != null) && (paramWifiConfigUiBase.getFrequency() != -1))
    {
      paramInt = paramWifiConfigUiBase.getFrequency();
      paramWifiConfigUiBase = null;
      if ((paramInt < 2400) || (paramInt >= 2500)) {
        break label1347;
      }
      paramWifiConfigUiBase = paramAccessPoint.getString(2131691317);
    }
    for (;;)
    {
      if (paramWifiConfigUiBase != null) {
        addRow(localViewGroup, 2131691406, paramWifiConfigUiBase);
      }
      addRow(localViewGroup, 2131691402, this.mAccessPoint.getSecurityString(false));
      this.mView.findViewById(2131362769).setVisibility(8);
      break label1072;
      label1345:
      break;
      label1347:
      if ((paramInt >= 4900) && (paramInt < 5900)) {
        paramWifiConfigUiBase = paramAccessPoint.getString(2131691318);
      } else {
        Log.e("WifiConfigController", "Unexpected frequency " + paramInt);
      }
    }
  }
  
  private void addRow(ViewGroup paramViewGroup, int paramInt, String paramString)
  {
    View localView = this.mConfigUi.getLayoutInflater().inflate(2130969115, paramViewGroup, false);
    ((TextView)localView.findViewById(2131362120)).setText(paramInt);
    ((TextView)localView.findViewById(2131362778)).setText(paramString);
    paramViewGroup.addView(localView);
  }
  
  private Inet4Address getIPv4Address(String paramString)
  {
    try
    {
      paramString = (Inet4Address)NetworkUtils.numericToInetAddress(paramString);
      return paramString;
    }
    catch (IllegalArgumentException|ClassCastException paramString) {}
    return null;
  }
  
  private void getSIMInfo()
  {
    this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
    int i = 0;
    if (i < this.mTelephonyManager.getSimCount())
    {
      Object localObject = this.mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(i);
      if (localObject != null) {}
      for (localObject = String.valueOf(((SubscriptionInfo)localObject).getDisplayName());; localObject = this.mContext.getString(2131693093, new Object[] { Integer.valueOf(i + 1) }))
      {
        this.mSimDisplayNames.add(localObject);
        i += 1;
        break;
      }
    }
  }
  
  private String getSignalString()
  {
    int i = this.mAccessPoint.getLevel();
    if ((i > -1) && (i < this.mLevels.length)) {
      return this.mLevels[i];
    }
    return null;
  }
  
  private void handleCertificateChange(int paramInt)
  {
    try
    {
      String[] arrayOfString = (String[])this.mCerPathString.toArray(new String[0]);
      this.mCertificateUser = ("\"" + arrayOfString[paramInt] + "/user.cer" + "\"");
      this.mCertificateUserPath = (arrayOfString[paramInt] + "/user.cer");
      this.mCertificateAs = ("\"" + arrayOfString[paramInt] + "/as.cer" + "\"");
      this.mCertificateAsPath = (arrayOfString[paramInt] + "/as.cer");
      return;
    }
    catch (Exception localException) {}
  }
  
  private boolean ipAndProxyFieldsAreValid()
  {
    if ((this.mIpSettingsSpinner != null) && (this.mIpSettingsSpinner.getSelectedItemPosition() == 1)) {}
    for (Object localObject = IpConfiguration.IpAssignment.STATIC;; localObject = IpConfiguration.IpAssignment.DHCP)
    {
      this.mIpAssignment = ((IpConfiguration.IpAssignment)localObject);
      if (this.mIpAssignment != IpConfiguration.IpAssignment.STATIC) {
        break;
      }
      this.mStaticIpConfiguration = new StaticIpConfiguration();
      if (validateIpConfigFields(this.mStaticIpConfiguration) == 0) {
        break;
      }
      return false;
    }
    int i = this.mProxySettingsSpinner.getSelectedItemPosition();
    this.mProxySettings = IpConfiguration.ProxySettings.NONE;
    this.mHttpProxy = null;
    if ((i == 1) && (this.mProxyHostView != null))
    {
      this.mProxySettings = IpConfiguration.ProxySettings.STATIC;
      localObject = this.mProxyHostView.getText().toString();
      str2 = this.mProxyPortView.getText().toString();
      str1 = this.mProxyExclusionListView.getText().toString();
      i = 0;
      try
      {
        j = Integer.parseInt(str2);
        i = j;
        k = ProxySelector.validate((String)localObject, str2, str1);
        i = j;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        for (;;)
        {
          k = 2131690921;
        }
      }
      if (k == 0) {
        this.mHttpProxy = new ProxyInfo((String)localObject, i, str1);
      }
    }
    while ((i != 2) || (this.mProxyPacView == null))
    {
      String str2;
      String str1;
      int j;
      int k;
      return true;
      return false;
    }
    this.mProxySettings = IpConfiguration.ProxySettings.PAC;
    localObject = this.mProxyPacView.getText();
    if (TextUtils.isEmpty((CharSequence)localObject)) {
      return false;
    }
    localObject = Uri.parse(((CharSequence)localObject).toString());
    if (localObject == null) {
      return false;
    }
    this.mHttpProxy = new ProxyInfo((Uri)localObject);
    return true;
  }
  
  private void loadCertificates(Spinner paramSpinner, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    Context localContext = this.mConfigUi.getContext();
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(this.mUnspecifiedCertString);
    if (paramBoolean1) {
      localArrayList.add(this.mMultipleCertSetString);
    }
    if (paramBoolean2) {
      localArrayList.add(this.mUseSystemCertsString);
    }
    localArrayList.addAll(Arrays.asList(KeyStore.getInstance().list(paramString1, 1010)));
    localArrayList.add(paramString2);
    paramString1 = new ArrayAdapter(localContext, 17367048, (String[])localArrayList.toArray(new String[localArrayList.size()]));
    paramString1.setDropDownViewResource(17367049);
    paramSpinner.setAdapter(paramString1);
  }
  
  private void setAnonymousIdentInvisible()
  {
    this.mView.findViewById(2131362748).setVisibility(8);
    this.mEapAnonymousView.setText("");
  }
  
  private void setCaCertInvisible()
  {
    this.mView.findViewById(2131362738).setVisibility(8);
    setSelection(this.mEapCaCertSpinner, this.mUnspecifiedCertString);
  }
  
  private void setCertificateSpinnerAdapter()
  {
    Object localObject1 = this.mConfigUi.getContext();
    ArrayList localArrayList = new ArrayList();
    this.mCerPathString = new ArrayList();
    this.mCert_Set = false;
    this.mCert_Cnt = 0;
    Object localObject2 = new File("/data/misc/wapi_certificate");
    for (;;)
    {
      int i;
      try
      {
        if (!((File)localObject2).isDirectory())
        {
          Log.e("WifiConfigController", " WifiConfigController.java->setCertificateSpinnerAdapter(), No Install Directory Present !! ");
          return;
        }
        localObject2 = ((File)localObject2).listFiles();
        this.mCert_Cnt = localObject2.length;
        i = 0;
        if (i < localObject2.length)
        {
          if (localObject2[i].isDirectory())
          {
            File localFile1 = new File(localObject2[i].getAbsoluteFile() + "/as.cer");
            File localFile2 = new File(localObject2[i].getAbsoluteFile() + "/user.cer");
            if ((localFile1.exists()) && (localFile2.exists()))
            {
              localArrayList.add(localObject2[i].getName());
              this.mCerPathString.add(localObject2[i].getAbsoluteFile().toString());
            }
          }
        }
        else
        {
          this.mCert_Set = true;
          localObject1 = new ArrayAdapter((Context)localObject1, 17367048, (String[])localArrayList.toArray(new String[0]));
          ((ArrayAdapter)localObject1).setDropDownViewResource(17367049);
          this.mCertificateSpinner.setAdapter((SpinnerAdapter)localObject1);
          return;
        }
      }
      catch (Exception localException)
      {
        return;
      }
      i += 1;
    }
  }
  
  private void setDomainInvisible()
  {
    this.mView.findViewById(2131362741).setVisibility(8);
    this.mEapDomainView.setText("");
  }
  
  private void setIdentityInvisible()
  {
    this.mView.findViewById(2131362746).setVisibility(8);
    this.mPhase2Spinner.setSelection(0);
  }
  
  private void setPasswordInvisible()
  {
    this.mPasswordView.setText("");
    this.mView.findViewById(2131362750).setVisibility(8);
    this.mView.findViewById(2131362752).setVisibility(8);
  }
  
  private void setPhase2Invisible()
  {
    this.mView.findViewById(2131362736).setVisibility(8);
    this.mPhase2Spinner.setSelection(0);
  }
  
  private void setSelection(Spinner paramSpinner, String paramString)
  {
    ArrayAdapter localArrayAdapter;
    int i;
    if (paramString != null)
    {
      localArrayAdapter = (ArrayAdapter)paramSpinner.getAdapter();
      i = localArrayAdapter.getCount() - 1;
    }
    for (;;)
    {
      if (i >= 0)
      {
        if (paramString.equals(localArrayAdapter.getItem(i))) {
          paramSpinner.setSelection(i);
        }
      }
      else {
        return;
      }
      i -= 1;
    }
  }
  
  private void setSimCardInvisible()
  {
    this.mView.findViewById(2131362734).setVisibility(8);
  }
  
  private void setUserCertInvisible()
  {
    this.mView.findViewById(2131362744).setVisibility(8);
    setSelection(this.mEapUserCertSpinner, this.mUnspecifiedCertString);
  }
  
  private void setVisibility(int paramInt1, int paramInt2)
  {
    View localView = this.mView.findViewById(paramInt1);
    if (localView != null) {
      localView.setVisibility(paramInt2);
    }
  }
  
  private void showEapFieldsByMethod(int paramInt)
  {
    this.mView.findViewById(2131362732).setVisibility(0);
    this.mView.findViewById(2131362746).setVisibility(0);
    this.mView.findViewById(2131362741).setVisibility(0);
    this.mView.findViewById(2131362738).setVisibility(0);
    this.mView.findViewById(2131362750).setVisibility(0);
    this.mView.findViewById(2131362752).setVisibility(0);
    this.mConfigUi.getContext();
    switch (paramInt)
    {
    }
    for (;;)
    {
      Object localObject;
      if (this.mView.findViewById(2131362738).getVisibility() != 8)
      {
        localObject = (String)this.mEapCaCertSpinner.getSelectedItem();
        if ((((String)localObject).equals(this.mDoNotValidateEapServerString)) || (((String)localObject).equals(this.mUnspecifiedCertString))) {
          setDomainInvisible();
        }
      }
      return;
      setPhase2Invisible();
      setCaCertInvisible();
      setDomainInvisible();
      setAnonymousIdentInvisible();
      setUserCertInvisible();
      setSimCardInvisible();
      continue;
      this.mView.findViewById(2131362744).setVisibility(0);
      setPhase2Invisible();
      setAnonymousIdentInvisible();
      setPasswordInvisible();
      setSimCardInvisible();
      continue;
      if (this.mPhase2Adapter != this.mPhase2PeapAdapter)
      {
        this.mPhase2Adapter = this.mPhase2PeapAdapter;
        this.mPhase2Spinner.setAdapter(this.mPhase2Adapter);
      }
      this.mView.findViewById(2131362736).setVisibility(0);
      this.mView.findViewById(2131362748).setVisibility(0);
      setUserCertInvisible();
      setSimCardInvisible();
      continue;
      if (this.mPhase2Adapter != this.mPhase2FullAdapter)
      {
        this.mPhase2Adapter = this.mPhase2FullAdapter;
        this.mPhase2Spinner.setAdapter(this.mPhase2Adapter);
      }
      this.mView.findViewById(2131362736).setVisibility(0);
      this.mView.findViewById(2131362748).setVisibility(0);
      setUserCertInvisible();
      setSimCardInvisible();
      continue;
      if (this.mAccessPoint != null)
      {
        localObject = this.mAccessPoint.getConfig();
        ArrayAdapter localArrayAdapter = new ArrayAdapter(this.mContext, 17367048, (String[])this.mSimDisplayNames.toArray(new String[this.mSimDisplayNames.size()]));
        localArrayAdapter.setDropDownViewResource(17367049);
        this.mSimCardSpinner.setAdapter(localArrayAdapter);
        this.mView.findViewById(2131362734).setVisibility(0);
        if (localObject != null) {
          this.mSimCardSpinner.setSelection(((WifiConfiguration)localObject).SIMNum - 1);
        }
        setPhase2Invisible();
        setAnonymousIdentInvisible();
        setCaCertInvisible();
        setDomainInvisible();
        setUserCertInvisible();
        setPasswordInvisible();
        setIdentityInvisible();
      }
    }
  }
  
  private void showIpConfigFields()
  {
    Object localObject2 = null;
    this.mView.findViewById(2131362769).setVisibility(0);
    Object localObject1 = localObject2;
    if (this.mAccessPoint != null)
    {
      localObject1 = localObject2;
      if (this.mAccessPoint.isSaved()) {
        localObject1 = this.mAccessPoint.getConfig();
      }
    }
    if (this.mIpSettingsSpinner.getSelectedItemPosition() == 1)
    {
      this.mView.findViewById(2131362771).setVisibility(0);
      if (this.mIpAddressView == null)
      {
        this.mIpAddressView = ((TextView)this.mView.findViewById(2131362772));
        this.mIpAddressView.addTextChangedListener(this);
        this.mGatewayView = ((TextView)this.mView.findViewById(2131362773));
        this.mGatewayView.addTextChangedListener(this);
        this.mGatewayView.setOnFocusChangeListener(this);
        this.mNetworkPrefixLengthView = ((TextView)this.mView.findViewById(2131362774));
        this.mNetworkPrefixLengthView.addTextChangedListener(this);
        this.mNetworkPrefixLengthView.setOnFocusChangeListener(this);
        this.mDns1View = ((TextView)this.mView.findViewById(2131362775));
        this.mDns1View.addTextChangedListener(this);
        this.mDns1View.setOnFocusChangeListener(this);
        this.mDns2View = ((TextView)this.mView.findViewById(2131362776));
        this.mDns2View.addTextChangedListener(this);
      }
      if (localObject1 != null)
      {
        localObject1 = ((WifiConfiguration)localObject1).getStaticIpConfiguration();
        if (localObject1 != null)
        {
          if (((StaticIpConfiguration)localObject1).ipAddress != null)
          {
            this.mIpAddressView.setText(((StaticIpConfiguration)localObject1).ipAddress.getAddress().getHostAddress());
            this.mNetworkPrefixLengthView.setText(Integer.toString(((StaticIpConfiguration)localObject1).ipAddress.getNetworkPrefixLength()));
          }
          if (((StaticIpConfiguration)localObject1).gateway != null) {
            this.mGatewayView.setText(((StaticIpConfiguration)localObject1).gateway.getHostAddress());
          }
          localObject1 = ((StaticIpConfiguration)localObject1).dnsServers.iterator();
          if (((Iterator)localObject1).hasNext()) {
            this.mDns1View.setText(((InetAddress)((Iterator)localObject1).next()).getHostAddress());
          }
          if (((Iterator)localObject1).hasNext()) {
            this.mDns2View.setText(((InetAddress)((Iterator)localObject1).next()).getHostAddress());
          }
        }
      }
      return;
    }
    this.mView.findViewById(2131362771).setVisibility(8);
  }
  
  private void showProxyFields()
  {
    Object localObject2 = null;
    this.mView.findViewById(2131362759).setVisibility(0);
    Object localObject1 = localObject2;
    if (this.mAccessPoint != null)
    {
      localObject1 = localObject2;
      if (this.mAccessPoint.isSaved()) {
        localObject1 = this.mAccessPoint.getConfig();
      }
    }
    if (this.mProxySettingsSpinner.getSelectedItemPosition() == 1)
    {
      setVisibility(2131362762, 0);
      setVisibility(2131362765, 0);
      setVisibility(2131362763, 8);
      if (this.mProxyHostView == null)
      {
        this.mProxyHostView = ((TextView)this.mView.findViewById(2131362766));
        this.mProxyHostView.addTextChangedListener(this);
        this.mProxyPortView = ((TextView)this.mView.findViewById(2131362767));
        this.mProxyPortView.addTextChangedListener(this);
        this.mProxyExclusionListView = ((TextView)this.mView.findViewById(2131362768));
        this.mProxyExclusionListView.addTextChangedListener(this);
      }
      if (localObject1 != null)
      {
        localObject1 = ((WifiConfiguration)localObject1).getHttpProxy();
        if (localObject1 != null)
        {
          this.mProxyHostView.setText(((ProxyInfo)localObject1).getHost());
          this.mProxyPortView.setText(Integer.toString(((ProxyInfo)localObject1).getPort()));
          this.mProxyExclusionListView.setText(((ProxyInfo)localObject1).getExclusionListAsString());
        }
      }
    }
    do
    {
      do
      {
        return;
        if (this.mProxySettingsSpinner.getSelectedItemPosition() != 2) {
          break;
        }
        setVisibility(2131362762, 8);
        setVisibility(2131362765, 8);
        setVisibility(2131362763, 0);
        if (this.mProxyPacView == null)
        {
          this.mProxyPacView = ((TextView)this.mView.findViewById(2131362764));
          this.mProxyPacView.addTextChangedListener(this);
        }
      } while (localObject1 == null);
      localObject1 = ((WifiConfiguration)localObject1).getHttpProxy();
    } while (localObject1 == null);
    this.mProxyPacView.setText(((ProxyInfo)localObject1).getPacFileUrl().toString());
    return;
    setVisibility(2131362762, 8);
    setVisibility(2131362765, 8);
    setVisibility(2131362763, 8);
  }
  
  private void showSecurityFields()
  {
    if (this.mAccessPointSecurity == 0)
    {
      this.mView.findViewById(2131362730).setVisibility(8);
      this.mView.findViewById(2131362755).setVisibility(8);
      this.mView.findViewById(2131362753).setVisibility(8);
      this.mView.findViewById(2131362754).setVisibility(8);
      return;
    }
    this.mView.findViewById(2131362730).setVisibility(0);
    Object localObject1;
    if ((this.mAccessPointSecurity != 5) && (this.mPasswordView == null))
    {
      this.mPasswordView = ((TextView)this.mView.findViewById(2131362699));
      this.mPasswordView.addTextChangedListener(this);
      this.mPasswordView.setOnEditorActionListener(this);
      this.mPasswordView.setOnKeyListener(this);
      ((CheckBox)this.mView.findViewById(2131362723)).setOnCheckedChangeListener(this);
      this.mWapiKeyTypeSpinner = ((Spinner)this.mView.findViewById(2131362755));
      this.mWapiKeyTypeSpinner.setOnItemSelectedListener(this);
      if ((this.mAccessPoint != null) && (this.mAccessPoint.isSaved()) && (this.mWapiKeyTypeSpinner != null))
      {
        this.mPasswordView.setHint(2131691424);
        localObject1 = this.mAccessPoint.getConfig();
        if (localObject1 != null)
        {
          if (((WifiConfiguration)localObject1).wapiPskType != 1) {
            break label389;
          }
          Log.e("WifiConfigController", "wapiPskType: in  " + ((WifiConfiguration)localObject1).wapiPskType);
          this.mWapiKeyTypeSpinner.setSelection(1);
        }
      }
    }
    if (this.mAccessPointSecurity == 4)
    {
      this.mView.findViewById(2131362699).setVisibility(0);
      this.mView.findViewById(2131362751).setVisibility(0);
      this.mView.findViewById(2131362723).setVisibility(0);
      this.mView.findViewById(2131362755).setVisibility(0);
      this.mView.findViewById(2131362753).setVisibility(8);
      this.mView.findViewById(2131362754).setVisibility(8);
    }
    for (;;)
    {
      if (this.mAccessPointSecurity == 3) {
        break label680;
      }
      this.mView.findViewById(2131362731).setVisibility(8);
      return;
      label389:
      Log.e("WifiConfigController", "wapiPskType: in  " + ((WifiConfiguration)localObject1).wapiPskType);
      this.mWapiKeyTypeSpinner.setSelection(0);
      break;
      if (this.mAccessPointSecurity == 5)
      {
        this.mView.findViewById(2131362699).setVisibility(8);
        this.mView.findViewById(2131362751).setVisibility(8);
        this.mView.findViewById(2131362723).setVisibility(8);
        this.mView.findViewById(2131362755).setVisibility(8);
        this.mCertificateText = ((TextView)this.mView.findViewById(2131362753));
        this.mCertificateSpinner = ((Spinner)this.mView.findViewById(2131362754));
        this.mCertificateSpinner.setOnItemSelectedListener(this);
        setCertificateSpinnerAdapter();
        this.mView.findViewById(2131362753).setVisibility(0);
        this.mView.findViewById(2131362754).setVisibility(0);
      }
      else if ((this.mAccessPointSecurity != 4) && (this.mAccessPointSecurity != 5))
      {
        this.mView.findViewById(2131362699).setVisibility(0);
        this.mView.findViewById(2131362751).setVisibility(0);
        this.mView.findViewById(2131362723).setVisibility(0);
        this.mView.findViewById(2131362755).setVisibility(8);
        this.mView.findViewById(2131362753).setVisibility(8);
        this.mView.findViewById(2131362754).setVisibility(8);
      }
    }
    label680:
    this.mView.findViewById(2131362731).setVisibility(0);
    if (this.mEapMethodSpinner == null)
    {
      getSIMInfo();
      this.mEapMethodSpinner = ((Spinner)this.mView.findViewById(2131362733));
      this.mEapMethodSpinner.setOnItemSelectedListener(this);
      int j;
      label1058:
      Object localObject2;
      if ((!Utils.isWifiOnly(this.mContext)) && (this.mContext.getResources().getBoolean(17957037)))
      {
        this.mPhase2Spinner = ((Spinner)this.mView.findViewById(2131362737));
        this.mEapCaCertSpinner = ((Spinner)this.mView.findViewById(2131362739));
        this.mEapCaCertSpinner.setOnItemSelectedListener(this);
        this.mEapDomainView = ((TextView)this.mView.findViewById(2131362742));
        this.mEapDomainView.addTextChangedListener(this);
        this.mEapUserCertSpinner = ((Spinner)this.mView.findViewById(2131362745));
        this.mEapUserCertSpinner.setOnItemSelectedListener(this);
        this.mSimCardSpinner = ((Spinner)this.mView.findViewById(2131362735));
        this.mEapIdentityView = ((TextView)this.mView.findViewById(2131362747));
        this.mEapAnonymousView = ((TextView)this.mView.findViewById(2131362749));
        loadCertificates(this.mEapCaCertSpinner, "CACERT_", this.mDoNotValidateEapServerString, false, true);
        loadCertificates(this.mEapUserCertSpinner, "USRPKEY_", this.mDoNotProvideEapUserCertString, false, false);
        if ((this.mAccessPoint == null) || (!this.mAccessPoint.isSaved())) {
          break label1381;
        }
        localObject1 = this.mAccessPoint.getConfig().enterpriseConfig;
        int i = ((WifiEnterpriseConfig)localObject1).getEapMethod();
        j = ((WifiEnterpriseConfig)localObject1).getPhase2Method();
        this.mEapMethodSpinner.setSelection(i);
        showEapFieldsByMethod(i);
        switch (i)
        {
        case 1: 
        case 2: 
        case 3: 
        default: 
          this.mPhase2Spinner.setSelection(j);
          if (!TextUtils.isEmpty(((WifiEnterpriseConfig)localObject1).getCaPath()))
          {
            setSelection(this.mEapCaCertSpinner, this.mUseSystemCertsString);
            this.mEapDomainView.setText(((WifiEnterpriseConfig)localObject1).getDomainSuffixMatch());
            localObject2 = ((WifiEnterpriseConfig)localObject1).getClientCertificateAlias();
            if (!TextUtils.isEmpty((CharSequence)localObject2)) {
              break label1368;
            }
            setSelection(this.mEapUserCertSpinner, this.mDoNotProvideEapUserCertString);
          }
          break;
        }
      }
      for (;;)
      {
        this.mEapIdentityView.setText(((WifiEnterpriseConfig)localObject1).getIdentity());
        this.mEapAnonymousView.setText(((WifiEnterpriseConfig)localObject1).getAnonymousIdentity());
        return;
        localObject1 = this.mContext.getResources().getStringArray(2131427387);
        localObject1 = new ArrayAdapter(this.mContext, 17367048, (Object[])localObject1);
        ((ArrayAdapter)localObject1).setDropDownViewResource(17367049);
        this.mEapMethodSpinner.setAdapter((SpinnerAdapter)localObject1);
        break;
        switch (j)
        {
        case 1: 
        case 2: 
        default: 
          Log.e("WifiConfigController", "Invalid phase 2 method " + j);
          break;
        case 0: 
          this.mPhase2Spinner.setSelection(0);
          break;
        case 3: 
          this.mPhase2Spinner.setSelection(1);
          break;
        case 4: 
          this.mPhase2Spinner.setSelection(2);
          break;
          localObject2 = this.mAccessPoint.getConfig();
          this.mSimCardSpinner.setSelection(((WifiConfiguration)localObject2).SIMNum - 1);
          break;
          localObject2 = ((WifiEnterpriseConfig)localObject1).getCaCertificateAliases();
          if (localObject2 == null)
          {
            setSelection(this.mEapCaCertSpinner, this.mDoNotValidateEapServerString);
            break label1058;
          }
          if (localObject2.length == 1)
          {
            setSelection(this.mEapCaCertSpinner, localObject2[0]);
            break label1058;
          }
          loadCertificates(this.mEapCaCertSpinner, "CACERT_", this.mDoNotValidateEapServerString, true, true);
          setSelection(this.mEapCaCertSpinner, this.mMultipleCertSetString);
          break label1058;
          label1368:
          setSelection(this.mEapUserCertSpinner, (String)localObject2);
        }
      }
      label1381:
      showEapFieldsByMethod(this.mEapMethodSpinner.getSelectedItemPosition());
      return;
    }
    showEapFieldsByMethod(this.mEapMethodSpinner.getSelectedItemPosition());
  }
  
  private int validateIpConfigFields(StaticIpConfiguration paramStaticIpConfiguration)
  {
    if (this.mIpAddressView == null) {
      return 0;
    }
    Object localObject = this.mIpAddressView.getText().toString();
    if (TextUtils.isEmpty((CharSequence)localObject)) {
      return 2131691471;
    }
    localObject = getIPv4Address((String)localObject);
    if ((localObject == null) || (((Inet4Address)localObject).equals(Inet4Address.ANY))) {
      return 2131691471;
    }
    int i = -1;
    try
    {
      j = Integer.parseInt(this.mNetworkPrefixLengthView.getText().toString());
      if ((j < 0) || (j > 32)) {
        break label426;
      }
      i = j;
      paramStaticIpConfiguration.ipAddress = new LinkAddress((InetAddress)localObject, j);
    }
    catch (IllegalArgumentException paramStaticIpConfiguration)
    {
      String str;
      return 2131691471;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      int j;
      for (;;)
      {
        j = i;
        if (!this.mNetworkPrefixLengthView.isFocused())
        {
          this.mNetworkPrefixLengthView.setText(this.mConfigUi.getContext().getString(2131689809));
          j = i;
        }
      }
      for (;;)
      {
        try
        {
          localObject = NetworkUtils.getNetworkPart((InetAddress)localObject, j).getAddress();
          localObject[(localObject.length - 1)] = 1;
          this.mGatewayView.setText(InetAddress.getByAddress((byte[])localObject).getHostAddress());
          localObject = this.mDns1View.getText().toString();
          if ((TextUtils.isEmpty((CharSequence)localObject)) && (!this.mDns1View.isFocused())) {
            break;
          }
          localObject = getIPv4Address((String)localObject);
          if (localObject != null) {
            break label401;
          }
          return 2131691473;
        }
        catch (UnknownHostException localUnknownHostException)
        {
          Log.e("WifiConfigController", "UnknownHostException occurs while handling static gateway", localUnknownHostException);
          continue;
        }
        catch (RuntimeException localRuntimeException)
        {
          Log.e("WifiConfigController", "Runtime exception occurs while handling static gateway", localRuntimeException);
          continue;
        }
        if (localRuntimeException.isMulticastAddress()) {
          return 2131691472;
        }
        paramStaticIpConfiguration.gateway = localRuntimeException;
      }
      this.mDns1View.setText(this.mConfigUi.getContext().getString(2131689806));
      while (this.mDns2View.length() > 0)
      {
        Inet4Address localInet4Address = getIPv4Address(this.mDns2View.getText().toString());
        if (localInet4Address == null)
        {
          return 2131691473;
          paramStaticIpConfiguration.dnsServers.add(localInet4Address);
        }
        else
        {
          paramStaticIpConfiguration.dnsServers.add(localInet4Address);
        }
      }
      return 0;
    }
    str = this.mGatewayView.getText().toString();
    if ((!TextUtils.isEmpty(str)) || (this.mGatewayView.isFocused()))
    {
      localObject = getIPv4Address(str);
      if (localObject != null) {
        break label321;
      }
      return 2131691472;
    }
    label321:
    label401:
    label426:
    return 2131691474;
  }
  
  public void afterTextChanged(Editable paramEditable)
  {
    this.mTextViewChangedHandler.post(new Runnable()
    {
      public void run()
      {
        WifiConfigController.this.showWarningMessagesIfAppropriate();
        WifiConfigController.this.enableSubmitIfAppropriate();
      }
    });
  }
  
  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  void enableSubmitIfAppropriate()
  {
    Button localButton = this.mConfigUi.getSubmitButton();
    if (localButton == null) {
      return;
    }
    localButton.setEnabled(isSubmittable());
  }
  
  public AccessPoint getAccessPoint()
  {
    return this.mAccessPoint;
  }
  
  WifiConfiguration getConfig()
  {
    if (this.mMode == 0) {
      return null;
    }
    WifiConfiguration localWifiConfiguration = new WifiConfiguration();
    if (this.mAccessPoint == null)
    {
      localWifiConfiguration.SSID = AccessPoint.convertToQuotedString(this.mSsidView.getText().toString());
      localWifiConfiguration.hiddenSSID = true;
    }
    for (;;)
    {
      localWifiConfiguration.shared = this.mSharedCheckBox.isChecked();
      switch (this.mAccessPointSecurity)
      {
      default: 
        return null;
        if (!this.mAccessPoint.isSaved()) {
          localWifiConfiguration.SSID = AccessPoint.convertToQuotedString(this.mAccessPoint.getSsidStr());
        } else {
          localWifiConfiguration.networkId = this.mAccessPoint.getConfig().networkId;
        }
        break;
      }
    }
    localWifiConfiguration.allowedKeyManagement.set(0);
    for (;;)
    {
      localWifiConfiguration.setIpConfiguration(new IpConfiguration(this.mIpAssignment, this.mProxySettings, this.mStaticIpConfiguration, this.mHttpProxy));
      return localWifiConfiguration;
      localWifiConfiguration.allowedKeyManagement.set(0);
      localWifiConfiguration.allowedAuthAlgorithms.set(0);
      localWifiConfiguration.allowedAuthAlgorithms.set(1);
      if (this.mPasswordView.length() != 0)
      {
        int i = this.mPasswordView.length();
        Object localObject = this.mPasswordView.getText().toString();
        if ((i == 10) || (i == 26)) {}
        while (i == 58)
        {
          if (!((String)localObject).matches("[0-9A-Fa-f]*")) {
            break label289;
          }
          localWifiConfiguration.wepKeys[0] = localObject;
          break;
        }
        label289:
        localWifiConfiguration.wepKeys[0] = ('"' + (String)localObject + '"');
        continue;
        localWifiConfiguration.allowedKeyManagement.set(1);
        if (this.mPasswordView.length() != 0)
        {
          localObject = this.mPasswordView.getText().toString();
          if (((String)localObject).matches("[0-9A-Fa-f]{64}"))
          {
            localWifiConfiguration.preSharedKey = ((String)localObject);
          }
          else
          {
            localWifiConfiguration.preSharedKey = ('"' + (String)localObject + '"');
            continue;
            localWifiConfiguration.allowedKeyManagement.set(6);
            if (this.mPasswordView.length() != 0)
            {
              localObject = this.mPasswordView.getText().toString();
              if (!((String)localObject).matches("[0-9A-Fa-f]{64}")) {
                break label500;
              }
            }
            label500:
            for (localWifiConfiguration.wapiPsk = ((String)localObject);; localWifiConfiguration.wapiPsk = ('"' + (String)localObject + '"'))
            {
              localWifiConfiguration.wapiPskType = this.mWapiKeyTypeSpinner.getSelectedItemPosition();
              Log.e("WifiConfigController", "wapiPskType  WAPI PSK key type  " + localWifiConfiguration.wapiPskType);
              break;
            }
            localWifiConfiguration.allowedKeyManagement.set(7);
            localWifiConfiguration.wapiASCert = this.mCertificateAs;
            localWifiConfiguration.wapiUserCert = this.mCertificateUser;
            continue;
            localWifiConfiguration.allowedKeyManagement.set(2);
            localWifiConfiguration.allowedKeyManagement.set(3);
            localWifiConfiguration.enterpriseConfig = new WifiEnterpriseConfig();
            i = this.mEapMethodSpinner.getSelectedItemPosition();
            int j = this.mPhase2Spinner.getSelectedItemPosition();
            localWifiConfiguration.enterpriseConfig.setEapMethod(i);
            switch (i)
            {
            case 1: 
            case 2: 
            case 3: 
            default: 
              localWifiConfiguration.enterpriseConfig.setPhase2Method(j);
              localObject = (String)this.mEapCaCertSpinner.getSelectedItem();
              localWifiConfiguration.enterpriseConfig.setCaCertificateAliases(null);
              localWifiConfiguration.enterpriseConfig.setCaPath(null);
              localWifiConfiguration.enterpriseConfig.setDomainSuffixMatch(this.mEapDomainView.getText().toString());
              if ((((String)localObject).equals(this.mUnspecifiedCertString)) || (((String)localObject).equals(this.mDoNotValidateEapServerString)))
              {
                label740:
                if ((localWifiConfiguration.enterpriseConfig.getCaCertificateAliases() != null) && (localWifiConfiguration.enterpriseConfig.getCaPath() != null)) {
                  Log.e("WifiConfigController", "ca_cert (" + localWifiConfiguration.enterpriseConfig.getCaCertificateAliases() + ") and ca_path (" + localWifiConfiguration.enterpriseConfig.getCaPath() + ") should not both be non-null");
                }
                String str = (String)this.mEapUserCertSpinner.getSelectedItem();
                if (!str.equals(this.mUnspecifiedCertString))
                {
                  localObject = str;
                  if (!str.equals(this.mDoNotProvideEapUserCertString)) {}
                }
                else
                {
                  localObject = "";
                }
                localWifiConfiguration.enterpriseConfig.setClientCertificateAlias((String)localObject);
                if ((i != 4) && (i != 5)) {
                  break label1166;
                }
                label880:
                localWifiConfiguration.enterpriseConfig.setIdentity("");
                localWifiConfiguration.enterpriseConfig.setAnonymousIdentity("");
              }
              break;
            }
            for (;;)
            {
              if (!this.mPasswordView.isShown()) {
                break label1254;
              }
              if (this.mPasswordView.length() <= 0) {
                break;
              }
              localWifiConfiguration.enterpriseConfig.setPassword(this.mPasswordView.getText().toString());
              break;
              switch (j)
              {
              default: 
                Log.e("WifiConfigController", "Unknown phase2 method" + j);
                break;
              case 0: 
                localWifiConfiguration.enterpriseConfig.setPhase2Method(0);
                break;
              case 1: 
                localWifiConfiguration.enterpriseConfig.setPhase2Method(3);
                break;
              case 2: 
                localWifiConfiguration.enterpriseConfig.setPhase2Method(4);
                break;
                this.selectedSimCardNumber = (this.mSimCardSpinner.getSelectedItemPosition() + 1);
                localWifiConfiguration.SIMNum = this.selectedSimCardNumber;
                break;
                if (((String)localObject).equals(this.mUseSystemCertsString))
                {
                  localWifiConfiguration.enterpriseConfig.setCaPath("/system/etc/security/cacerts");
                  break label740;
                }
                if (((String)localObject).equals(this.mMultipleCertSetString))
                {
                  if (this.mAccessPoint == null) {
                    break label740;
                  }
                  if (!this.mAccessPoint.isSaved()) {
                    Log.e("WifiConfigController", "Multiple certs can only be set when editing saved network");
                  }
                  localWifiConfiguration.enterpriseConfig.setCaCertificateAliases(this.mAccessPoint.getConfig().enterpriseConfig.getCaCertificateAliases());
                  break label740;
                }
                localWifiConfiguration.enterpriseConfig.setCaCertificateAliases(new String[] { localObject });
                break label740;
                label1166:
                if (i == 6) {
                  break label880;
                }
                if (i == 3)
                {
                  localWifiConfiguration.enterpriseConfig.setIdentity(this.mEapIdentityView.getText().toString());
                  localWifiConfiguration.enterpriseConfig.setAnonymousIdentity("");
                }
                else
                {
                  localWifiConfiguration.enterpriseConfig.setIdentity(this.mEapIdentityView.getText().toString());
                  localWifiConfiguration.enterpriseConfig.setAnonymousIdentity(this.mEapAnonymousView.getText().toString());
                }
                break;
              }
            }
            label1254:
            localWifiConfiguration.enterpriseConfig.setPassword(this.mPasswordView.getText().toString());
          }
        }
      }
    }
  }
  
  public int getMode()
  {
    return this.mMode;
  }
  
  void hideForgetButton()
  {
    Button localButton = this.mConfigUi.getForgetButton();
    if (localButton == null) {
      return;
    }
    localButton.setVisibility(8);
  }
  
  void hideSubmitButton()
  {
    Button localButton = this.mConfigUi.getSubmitButton();
    if (localButton == null) {
      return;
    }
    localButton.setVisibility(8);
  }
  
  boolean isSubmittable()
  {
    int i = 0;
    int j = i;
    if (this.mPasswordView != null)
    {
      if ((this.mAccessPointSecurity == 1) && (this.mPasswordView.length() == 0)) {
        j = 1;
      }
    }
    else
    {
      label31:
      i = j;
      if (this.mPasswordView != null)
      {
        i = j;
        if (this.mAccessPointSecurity == 4)
        {
          if (this.mPasswordView.length() < 8) {
            break label423;
          }
          i = j;
          if (this.mWapiKeyTypeSpinner.getSelectedItemPosition() == 1) {
            if (this.mPasswordView.getText().toString().matches("[0-9A-Fa-f]*"))
            {
              i = j;
              if (this.mPasswordView.length() % 2 != 1) {}
            }
            else
            {
              i = 1;
            }
          }
        }
      }
      label113:
      j = i;
      if (this.mPasswordView != null)
      {
        j = i;
        if (this.mPasswordView.length() == 0)
        {
          j = i;
          if (this.mAccessPoint != null)
          {
            j = i;
            if (this.mAccessPoint.isSaved()) {
              j = 0;
            }
          }
        }
      }
      if ((this.mSsidView == null) || (this.mSsidView.length() != 0)) {
        break label428;
      }
    }
    label174:
    for (bool2 = false;; bool2 = ipAndProxyFieldsAreValid())
    {
      boolean bool1 = bool2;
      String str;
      if (this.mEapCaCertSpinner != null)
      {
        bool1 = bool2;
        if (this.mView.findViewById(2131362738).getVisibility() != 8)
        {
          str = (String)this.mEapCaCertSpinner.getSelectedItem();
          bool3 = bool2;
        }
      }
      try
      {
        if (str.equals(this.mUnspecifiedCertString))
        {
          bool3 = bool2;
          if (this.mSecuritySpinner != null)
          {
            i = this.mSecuritySpinner.getSelectedItemPosition();
            bool3 = bool2;
            if (i == 3) {
              bool3 = false;
            }
          }
        }
      }
      catch (NullPointerException localNullPointerException)
      {
        for (;;)
        {
          localNullPointerException.printStackTrace();
          bool3 = bool2;
        }
      }
      bool1 = bool3;
      if (str.equals(this.mUseSystemCertsString))
      {
        bool1 = bool3;
        if (this.mEapDomainView != null)
        {
          bool1 = bool3;
          if (this.mView.findViewById(2131362741).getVisibility() != 8)
          {
            bool1 = bool3;
            if (TextUtils.isEmpty(this.mEapDomainView.getText().toString())) {
              bool1 = false;
            }
          }
        }
      }
      bool2 = bool1;
      if (this.mEapUserCertSpinner != null)
      {
        bool2 = bool1;
        if (this.mView.findViewById(2131362744).getVisibility() != 8)
        {
          bool2 = bool1;
          if (((String)this.mEapUserCertSpinner.getSelectedItem()).equals(this.mUnspecifiedCertString)) {
            bool2 = false;
          }
        }
      }
      return bool2;
      j = i;
      if (this.mAccessPointSecurity != 2) {
        break label31;
      }
      j = i;
      if (this.mPasswordView.length() >= 8) {
        break label31;
      }
      break;
      label423:
      i = 1;
      break label113;
      label428:
      if (j != 0) {
        break label174;
      }
    }
  }
  
  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if (paramCompoundButton.getId() == 2131362723)
    {
      j = this.mPasswordView.getSelectionEnd();
      paramCompoundButton = this.mPasswordView;
      if (paramBoolean)
      {
        i = 144;
        paramCompoundButton.setInputType(i | 0x1);
        if (j >= 0) {
          ((EditText)this.mPasswordView).setSelection(j);
        }
      }
    }
    while (paramCompoundButton.getId() != 2131362757) {
      for (;;)
      {
        int j;
        return;
        int i = 128;
      }
    }
    if (paramBoolean)
    {
      this.mView.findViewById(2131362758).setVisibility(0);
      return;
    }
    this.mView.findViewById(2131362758).setVisibility(8);
  }
  
  public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramTextView == this.mPasswordView) && (paramInt == 6) && (isSubmittable()))
    {
      this.mConfigUi.dispatchSubmit();
      return true;
    }
    return false;
  }
  
  public void onFocusChange(View paramView, boolean paramBoolean)
  {
    if (!paramBoolean) {
      if ((this.mIpSettingsSpinner == null) || (this.mIpSettingsSpinner.getSelectedItemPosition() != 1)) {
        break label62;
      }
    }
    label62:
    for (paramView = IpConfiguration.IpAssignment.STATIC;; paramView = IpConfiguration.IpAssignment.DHCP)
    {
      this.mIpAssignment = paramView;
      if (this.mIpAssignment == IpConfiguration.IpAssignment.STATIC)
      {
        this.mStaticIpConfiguration = new StaticIpConfiguration();
        validateIpConfigFields(this.mStaticIpConfiguration);
      }
      return;
    }
  }
  
  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    if (paramAdapterView == this.mSecuritySpinner)
    {
      this.mAccessPointSecurity = paramInt;
      showSecurityFields();
    }
    for (;;)
    {
      showWarningMessagesIfAppropriate();
      if (paramAdapterView != this.mCertificateSpinner) {
        enableSubmitIfAppropriate();
      }
      return;
      if ((paramAdapterView == this.mEapMethodSpinner) || (paramAdapterView == this.mEapCaCertSpinner))
      {
        showSecurityFields();
      }
      else if (paramAdapterView == this.mProxySettingsSpinner)
      {
        showProxyFields();
      }
      else if (paramAdapterView == this.mCertificateSpinner)
      {
        paramInt = this.mCertificateSpinner.getSelectedItemPosition();
        Log.e("WifiConfigController", " ############# WifiConfigController.java->onItemSelected() Cert_selected: " + paramInt);
        if ((paramInt >= 0) && (paramInt < this.mCert_Cnt)) {
          handleCertificateChange(paramInt);
        }
      }
      else if (paramAdapterView == this.mWapiKeyTypeSpinner)
      {
        if (this.mPsk_key_type != this.mWapiKeyTypeSpinner.getSelectedItemPosition())
        {
          this.mPsk_key_type = this.mWapiKeyTypeSpinner.getSelectedItemPosition();
          Log.e("WifiConfigController", "wapiPskType  WAPI PSK key type changed to " + this.mPsk_key_type);
          if (this.mPasswordView != null) {
            this.mPasswordView.setText("");
          }
        }
      }
      else
      {
        showIpConfigFields();
      }
    }
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramView == this.mPasswordView) && (paramInt == 66) && (isSubmittable()))
    {
      this.mConfigUi.dispatchSubmit();
      return true;
    }
    return false;
  }
  
  public void onNothingSelected(AdapterView<?> paramAdapterView) {}
  
  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  void showWarningMessagesIfAppropriate()
  {
    this.mView.findViewById(2131362740).setVisibility(8);
    this.mView.findViewById(2131362743).setVisibility(8);
    if ((this.mEapCaCertSpinner != null) && (this.mView.findViewById(2131362738).getVisibility() != 8))
    {
      String str = (String)this.mEapCaCertSpinner.getSelectedItem();
      if (str.equals(this.mDoNotValidateEapServerString)) {
        this.mView.findViewById(2131362740).setVisibility(0);
      }
      if ((str.equals(this.mUseSystemCertsString)) && (this.mEapDomainView != null) && (this.mView.findViewById(2131362741).getVisibility() != 8) && (TextUtils.isEmpty(this.mEapDomainView.getText().toString()))) {
        this.mView.findViewById(2131362743).setVisibility(0);
      }
    }
  }
  
  public void updatePassword()
  {
    TextView localTextView = (TextView)this.mView.findViewById(2131362699);
    if (((CheckBox)this.mView.findViewById(2131362723)).isChecked()) {}
    for (int i = 144;; i = 128)
    {
      localTextView.setInputType(i | 0x1);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiConfigController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */