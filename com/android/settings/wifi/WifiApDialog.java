package com.android.settings.wifi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;

public class WifiApDialog
  extends AlertDialog
  implements View.OnClickListener, TextWatcher, AdapterView.OnItemSelectedListener
{
  static final int BUTTON_SUBMIT = -1;
  private static final String KEY_BANDINDEX = "mBandIndex";
  public static final int OPEN_INDEX = 0;
  private static final String TAG = "WifiApDialog";
  private static final int WIFI_STASAP_CONCURRENCY_DISABLED_VALUE = 0;
  private static final int WIFI_STASAP_CONCURRENCY_ENABLED_VALUE = 1;
  public static final int WPA2_INDEX = 1;
  private static int apBandTemp = 0;
  private int mBandIndex = 0;
  private Context mContext;
  private final DialogInterface.OnClickListener mListener;
  private EditText mPassword;
  private int mSecurityTypeIndex = 0;
  private TextView mSsid;
  private View mView;
  WifiConfiguration mWifiConfig;
  WifiManager mWifiManager;
  private boolean mWifiStaSapConcurrencySupport = false;
  private String nameTemp = null;
  private Method setWifiStaSapConcurrencyEnabledMethod;
  
  public WifiApDialog(Context paramContext, DialogInterface.OnClickListener paramOnClickListener, WifiConfiguration paramWifiConfiguration)
  {
    super(paramContext);
    this.mListener = paramOnClickListener;
    this.mWifiConfig = paramWifiConfiguration;
    if (paramWifiConfiguration != null)
    {
      this.mSecurityTypeIndex = getSecurityTypeIndex(paramWifiConfiguration);
      apBandTemp = paramWifiConfiguration.apBand;
    }
    this.mWifiManager = ((WifiManager)paramContext.getSystemService("wifi"));
    this.mContext = paramContext;
  }
  
  public static int getSecurityTypeIndex(WifiConfiguration paramWifiConfiguration)
  {
    if (paramWifiConfiguration.allowedKeyManagement.get(4)) {
      return 1;
    }
    return 0;
  }
  
  private void setWifiStaSapConcurrencyEnabled(int paramInt)
  {
    Settings.System.putIntForUser(this.mContext.getContentResolver(), "oem_share_wifi", paramInt, -2);
    if ((this.mWifiManager != null) && (this.mWifiManager.getWifiApState() == 11)) {}
    try
    {
      this.setWifiStaSapConcurrencyEnabledMethod = this.mWifiManager.getClass().getDeclaredMethod("setWifiStaSapConcurrencyEnabled", new Class[] { Integer.TYPE });
      this.setWifiStaSapConcurrencyEnabledMethod.invoke(this.mWifiManager, new Object[] { Integer.valueOf(paramInt) });
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void showSecurityFields()
  {
    if (this.mSecurityTypeIndex == 0)
    {
      this.mView.findViewById(2131362722).setVisibility(8);
      return;
    }
    this.mView.findViewById(2131362722).setVisibility(0);
  }
  
  private void validate()
  {
    String str = this.mSsid.getText().toString();
    if ((this.mSsid != null) && (str.trim().length() == 0)) {}
    while (((this.mSecurityTypeIndex == 1) && (this.mPassword.length() < 8)) || ((this.mSsid != null) && (Charset.forName("UTF-8").encode(str).limit() > 32)))
    {
      getButton(-1).setEnabled(false);
      return;
    }
    getButton(-1).setEnabled(true);
  }
  
  public void afterTextChanged(Editable paramEditable)
  {
    validate();
  }
  
  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  public void cancelConfig()
  {
    if (this.mWifiConfig != null) {
      this.mWifiConfig.apBand = apBandTemp;
    }
  }
  
  public void confirmConfig()
  {
    if (this.mWifiConfig != null) {
      this.mWifiConfig.apBand = this.mBandIndex;
    }
    if (((CheckBox)this.mView.findViewById(2131362727)).isChecked()) {
      setWifiStaSapConcurrencyEnabled(1);
    }
    while (((CheckBox)this.mView.findViewById(2131362728)).isChecked())
    {
      Settings.Secure.putInt(this.mContext.getContentResolver(), "hotspot_auto_shut_down", 1);
      return;
      setWifiStaSapConcurrencyEnabled(0);
    }
    Settings.Secure.putInt(this.mContext.getContentResolver(), "hotspot_auto_shut_down", 0);
  }
  
  public WifiConfiguration getConfig()
  {
    WifiConfiguration localWifiConfiguration = new WifiConfiguration();
    localWifiConfiguration.SSID = this.mSsid.getText().toString();
    localWifiConfiguration.apBand = this.mBandIndex;
    switch (this.mSecurityTypeIndex)
    {
    default: 
      return null;
    case 0: 
      localWifiConfiguration.allowedKeyManagement.set(0);
      return localWifiConfiguration;
    }
    localWifiConfiguration.allowedKeyManagement.set(4);
    localWifiConfiguration.allowedAuthAlgorithms.set(0);
    if (this.mPassword.length() != 0) {
      localWifiConfiguration.preSharedKey = this.mPassword.getText().toString();
    }
    return localWifiConfiguration;
  }
  
  public void onClick(View paramView)
  {
    if ((paramView.getId() == 2131362727) || (paramView.getId() == 2131362728)) {
      return;
    }
    EditText localEditText = this.mPassword;
    if (((CheckBox)paramView).isChecked()) {}
    for (int i = 144;; i = 128)
    {
      localEditText.setInputType(i | 0x1);
      this.mPassword.setSelection(this.mPassword.getText().length());
      return;
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    this.mView = getLayoutInflater().inflate(2130969111, null);
    Spinner localSpinner1 = (Spinner)this.mView.findViewById(2131362721);
    final Spinner localSpinner2 = (Spinner)this.mView.findViewById(2131362724);
    setView(this.mView);
    setInverseBackgroundForced(true);
    Context localContext = getContext();
    this.mWifiStaSapConcurrencySupport = localContext.getPackageManager().hasSystemFeature("oem.wifi.stasap.concurrency.support");
    setTitle(2131691502);
    this.mView.findViewById(2131362212).setVisibility(0);
    this.mSsid = ((TextView)this.mView.findViewById(2131362720));
    this.mPassword = ((EditText)this.mView.findViewById(2131362699));
    Object localObject2 = this.mWifiManager.getCountryCode();
    Object localObject1;
    if ((!this.mWifiManager.isDualBandSupported()) || (localObject2 == null))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (!this.mWifiManager.isDualBandSupported())
      {
        localObject1 = "Device do not support 5GHz ";
        localStringBuilder = localStringBuilder.append((String)localObject1);
        if (localObject2 != null) {
          break label598;
        }
        localObject1 = " NO country code";
        label198:
        Log.i("WifiApDialog", (String)localObject1 + " forbid 5GHz");
        localObject2 = ArrayAdapter.createFromResource(this.mContext, 2131427389, 17367048);
        localObject1 = localObject2;
        if (this.mWifiConfig != null)
        {
          this.mWifiConfig.apBand = 0;
          localObject1 = localObject2;
        }
        label255:
        ((ArrayAdapter)localObject1).setDropDownViewResource(17367049);
        setButton(-1, localContext.getString(2131691453), this.mListener);
        setButton(-2, localContext.getString(2131691455), this.mListener);
        if (this.mWifiConfig != null)
        {
          this.mSsid.setText(this.mWifiConfig.SSID);
          if (this.mWifiConfig.apBand != 0) {
            break label622;
          }
          this.mBandIndex = 0;
          label333:
          localSpinner1.setSelection(this.mSecurityTypeIndex);
          if (this.mSecurityTypeIndex == 1) {
            this.mPassword.setText(this.mWifiConfig.preSharedKey);
          }
        }
        localSpinner2.setAdapter((SpinnerAdapter)localObject1);
        localSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
          boolean mInit = true;
          
          public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
          {
            if ((!this.mInit) && (WifiApDialog.this.mWifiConfig != null))
            {
              WifiApDialog.-set0(WifiApDialog.this, paramAnonymousInt);
              Log.i("WifiApDialog", "config on channelIndex : " + WifiApDialog.-get0(WifiApDialog.this) + " Band: " + WifiApDialog.this.mWifiConfig.apBand);
              return;
            }
            this.mInit = false;
            localSpinner2.setSelection(WifiApDialog.-get0(WifiApDialog.this));
          }
          
          public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
        });
        this.mSsid.addTextChangedListener(new TextWatcher()
        {
          String name;
          int num;
          
          public void afterTextChanged(Editable paramAnonymousEditable)
          {
            WifiApDialog.-wrap0(WifiApDialog.this);
            if ((WifiApDialog.-get1(WifiApDialog.this) != null) && (WifiApDialog.-get1(WifiApDialog.this).length() != 0))
            {
              this.name = WifiApDialog.-get1(WifiApDialog.this).getText().toString();
              this.num = this.name.getBytes().length;
              if (this.num > 32)
              {
                WifiApDialog.-get1(WifiApDialog.this).setText(WifiApDialog.-get2(WifiApDialog.this));
                paramAnonymousEditable = WifiApDialog.-get1(WifiApDialog.this).getText();
                if ((paramAnonymousEditable instanceof Spannable)) {
                  Selection.setSelection((Spannable)paramAnonymousEditable, paramAnonymousEditable.length());
                }
              }
            }
          }
          
          public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            if ((WifiApDialog.-get1(WifiApDialog.this) != null) && (WifiApDialog.-get1(WifiApDialog.this).length() != 0) && ((WifiApDialog.-get1(WifiApDialog.this).getText() instanceof Spannable))) {
              WifiApDialog.-set1(WifiApDialog.this, WifiApDialog.-get1(WifiApDialog.this).getText().toString());
            }
          }
          
          public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        });
        this.mPassword.addTextChangedListener(this);
        ((CheckBox)this.mView.findViewById(2131362723)).setOnClickListener(this);
        if (this.mWifiStaSapConcurrencySupport)
        {
          ((TextView)this.mView.findViewById(2131362726)).setVisibility(0);
          ((CheckBox)this.mView.findViewById(2131362727)).setVisibility(0);
        }
        ((CheckBox)this.mView.findViewById(2131362727)).setOnClickListener(this);
        if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_share_wifi", 0, -2) == 0) {
          break label630;
        }
        bool = true;
        label501:
        ((CheckBox)this.mView.findViewById(2131362727)).setChecked(bool);
        ((CheckBox)this.mView.findViewById(2131362728)).setOnClickListener(this);
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "hotspot_auto_shut_down", 0, 0) == 0) {
          break label635;
        }
      }
    }
    label598:
    label622:
    label630:
    label635:
    for (boolean bool = true;; bool = false)
    {
      ((CheckBox)this.mView.findViewById(2131362728)).setChecked(bool);
      localSpinner1.setOnItemSelectedListener(this);
      super.onCreate(paramBundle);
      showSecurityFields();
      validate();
      return;
      localObject1 = "";
      break;
      localObject1 = "";
      break label198;
      localObject1 = ArrayAdapter.createFromResource(this.mContext, 2131427388, 17367048);
      break label255;
      this.mBandIndex = 1;
      break label333;
      bool = false;
      break label501;
    }
  }
  
  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    this.mSecurityTypeIndex = paramInt;
    showSecurityFields();
    validate();
  }
  
  public void onNothingSelected(AdapterView<?> paramAdapterView) {}
  
  public void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    EditText localEditText = this.mPassword;
    if (((CheckBox)this.mView.findViewById(2131362723)).isChecked()) {}
    for (int i = 144;; i = 128)
    {
      localEditText.setInputType(i | 0x1);
      this.mBandIndex = paramBundle.getInt("mBandIndex");
      if (this.mWifiConfig != null) {
        this.mWifiConfig.apBand = this.mBandIndex;
      }
      return;
    }
  }
  
  public Bundle onSaveInstanceState()
  {
    Bundle localBundle = super.onSaveInstanceState();
    localBundle.putInt("mBandIndex", this.mBandIndex);
    return localBundle;
  }
  
  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiApDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */