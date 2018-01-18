package com.android.settings.vpn2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.SystemProperties;
import android.security.KeyStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.android.internal.net.VpnProfile;
import java.net.InetAddress;

class ConfigDialog
  extends AlertDialog
  implements TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener
{
  private CheckBox mAlwaysOnVpn;
  private TextView mDnsServers;
  private boolean mEditing;
  private boolean mExists;
  private Spinner mIpsecCaCert;
  private TextView mIpsecIdentifier;
  private TextView mIpsecSecret;
  private Spinner mIpsecServerCert;
  private Spinner mIpsecUserCert;
  private final KeyStore mKeyStore = KeyStore.getInstance();
  private TextView mL2tpSecret;
  private final DialogInterface.OnClickListener mListener;
  private CheckBox mMppe;
  private TextView mName;
  private TextView mPassword;
  private final VpnProfile mProfile;
  private TextView mRoutes;
  private CheckBox mSaveLogin;
  private TextView mSearchDomains;
  private TextView mServer;
  private CheckBox mShowOptions;
  private Spinner mType;
  private TextView mUsername;
  private View mView;
  
  ConfigDialog(Context paramContext, DialogInterface.OnClickListener paramOnClickListener, VpnProfile paramVpnProfile, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramContext);
    this.mListener = paramOnClickListener;
    this.mProfile = paramVpnProfile;
    this.mEditing = paramBoolean1;
    this.mExists = paramBoolean2;
  }
  
  private void changeType(int paramInt)
  {
    this.mMppe.setVisibility(8);
    this.mView.findViewById(2131362682).setVisibility(8);
    this.mView.findViewById(2131362684).setVisibility(8);
    this.mView.findViewById(2131362687).setVisibility(8);
    this.mView.findViewById(2131362689).setVisibility(8);
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      this.mMppe.setVisibility(0);
      return;
    case 1: 
      this.mView.findViewById(2131362682).setVisibility(0);
    case 3: 
      this.mView.findViewById(2131362684).setVisibility(0);
      return;
    case 2: 
      this.mView.findViewById(2131362682).setVisibility(0);
    case 4: 
      this.mView.findViewById(2131362687).setVisibility(0);
    }
    this.mView.findViewById(2131362689).setVisibility(0);
  }
  
  private void loadCertificates(Spinner paramSpinner, String paramString1, int paramInt, String paramString2)
  {
    Context localContext = getContext();
    Object localObject;
    String[] arrayOfString;
    if (paramInt == 0)
    {
      localObject = "";
      arrayOfString = this.mKeyStore.list(paramString1);
      if ((arrayOfString != null) && (arrayOfString.length != 0)) {
        break label108;
      }
      paramString1 = new String[1];
      paramString1[0] = localObject;
      label45:
      localObject = new ArrayAdapter(localContext, 17367048, paramString1);
      ((ArrayAdapter)localObject).setDropDownViewResource(17367049);
      paramSpinner.setAdapter((SpinnerAdapter)localObject);
      paramInt = 1;
    }
    for (;;)
    {
      if (paramInt < paramString1.length)
      {
        if (paramString1[paramInt].equals(paramString2)) {
          paramSpinner.setSelection(paramInt);
        }
      }
      else
      {
        return;
        localObject = localContext.getString(paramInt);
        break;
        label108:
        paramString1 = new String[arrayOfString.length + 1];
        paramString1[0] = localObject;
        System.arraycopy(arrayOfString, 0, paramString1, 1, arrayOfString.length);
        break label45;
      }
      paramInt += 1;
    }
  }
  
  private void showAdvancedOptions()
  {
    this.mView.findViewById(2131362693).setVisibility(0);
    this.mShowOptions.setVisibility(8);
  }
  
  private void updateSaveLoginStatus()
  {
    if (this.mAlwaysOnVpn.isChecked())
    {
      this.mSaveLogin.setChecked(true);
      this.mSaveLogin.setEnabled(false);
      return;
    }
    this.mSaveLogin.setChecked(this.mProfile.saveLogin);
    this.mSaveLogin.setEnabled(true);
  }
  
  private boolean validate(boolean paramBoolean)
  {
    if ((!this.mAlwaysOnVpn.isChecked()) || (getProfile().isValidLockdownProfile()))
    {
      if (paramBoolean) {
        break label60;
      }
      if ((this.mUsername.getText().length() != 0) && (this.mPassword.getText().length() != 0)) {
        return true;
      }
    }
    else
    {
      return false;
    }
    return false;
    label60:
    if ((this.mName.getText().length() == 0) || (this.mServer.getText().length() == 0)) {}
    while ((!validateAddresses(this.mDnsServers.getText().toString(), false)) || (!validateAddresses(this.mRoutes.getText().toString(), true))) {
      return false;
    }
    switch (this.mType.getSelectedItemPosition())
    {
    default: 
      return false;
    case 0: 
    case 5: 
      return true;
    case 1: 
    case 3: 
      return this.mIpsecSecret.getText().length() != 0;
    }
    return this.mIpsecUserCert.getSelectedItemPosition() != 0;
  }
  
  private boolean validateAddresses(String paramString, boolean paramBoolean)
  {
    for (;;)
    {
      int i;
      try
      {
        String[] arrayOfString = paramString.split(" ");
        i = 0;
        int k = arrayOfString.length;
        if (i < k)
        {
          Object localObject = arrayOfString[i];
          if (!((String)localObject).isEmpty())
          {
            int j = 32;
            paramString = (String)localObject;
            if (paramBoolean)
            {
              localObject = ((String)localObject).split("/", 2);
              paramString = localObject[0];
              j = Integer.parseInt(localObject[1]);
            }
            paramString = InetAddress.parseNumericAddress(paramString).getAddress();
            int m = paramString[3];
            int n = paramString[2];
            int i1 = paramString[1];
            int i2 = paramString[0];
            int i3 = paramString.length;
            if ((i3 != 4) || (j < 0)) {
              return false;
            }
            if (j > 32) {
              continue;
            }
            if ((j < 32) && ((m & 0xFF | (n & 0xFF) << 8 | (i1 & 0xFF) << 16 | (i2 & 0xFF) << 24) << j != 0)) {
              continue;
            }
          }
        }
        else
        {
          return true;
        }
      }
      catch (Exception paramString)
      {
        return false;
      }
      i += 1;
    }
  }
  
  public void afterTextChanged(Editable paramEditable)
  {
    getButton(-1).setEnabled(validate(this.mEditing));
  }
  
  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  VpnProfile getProfile()
  {
    VpnProfile localVpnProfile = new VpnProfile(this.mProfile.key);
    localVpnProfile.name = this.mName.getText().toString();
    localVpnProfile.type = this.mType.getSelectedItemPosition();
    localVpnProfile.server = this.mServer.getText().toString().trim();
    localVpnProfile.username = this.mUsername.getText().toString();
    localVpnProfile.password = this.mPassword.getText().toString();
    localVpnProfile.searchDomains = this.mSearchDomains.getText().toString().trim();
    localVpnProfile.dnsServers = this.mDnsServers.getText().toString().trim();
    localVpnProfile.routes = this.mRoutes.getText().toString().trim();
    boolean bool;
    switch (localVpnProfile.type)
    {
    default: 
      if ((localVpnProfile.username.isEmpty()) && (localVpnProfile.password.isEmpty()))
      {
        bool = false;
        label214:
        if (this.mSaveLogin.isChecked()) {
          break label399;
        }
        if (!this.mEditing) {
          break label404;
        }
      }
      break;
    }
    for (;;)
    {
      localVpnProfile.saveLogin = bool;
      return localVpnProfile;
      localVpnProfile.mppe = this.mMppe.isChecked();
      break;
      localVpnProfile.l2tpSecret = this.mL2tpSecret.getText().toString();
      localVpnProfile.ipsecIdentifier = this.mIpsecIdentifier.getText().toString();
      localVpnProfile.ipsecSecret = this.mIpsecSecret.getText().toString();
      break;
      localVpnProfile.l2tpSecret = this.mL2tpSecret.getText().toString();
      if (this.mIpsecUserCert.getSelectedItemPosition() != 0) {
        localVpnProfile.ipsecUserCert = ((String)this.mIpsecUserCert.getSelectedItem());
      }
      if (this.mIpsecCaCert.getSelectedItemPosition() != 0) {
        localVpnProfile.ipsecCaCert = ((String)this.mIpsecCaCert.getSelectedItem());
      }
      if (this.mIpsecServerCert.getSelectedItemPosition() == 0) {
        break;
      }
      localVpnProfile.ipsecServerCert = ((String)this.mIpsecServerCert.getSelectedItem());
      break;
      bool = true;
      break label214;
      label399:
      bool = true;
      continue;
      label404:
      bool = false;
    }
  }
  
  boolean isEditing()
  {
    return this.mEditing;
  }
  
  public boolean isVpnAlwaysOn()
  {
    return this.mAlwaysOnVpn.isChecked();
  }
  
  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if (paramCompoundButton == this.mAlwaysOnVpn)
    {
      updateSaveLoginStatus();
      getButton(-1).setEnabled(validate(this.mEditing));
    }
  }
  
  public void onClick(View paramView)
  {
    if (paramView == this.mShowOptions) {
      showAdvancedOptions();
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    this.mView = getLayoutInflater().inflate(2130969104, null);
    setView(this.mView);
    Context localContext = getContext();
    this.mName = ((TextView)this.mView.findViewById(2131362120));
    this.mType = ((Spinner)this.mView.findViewById(2131362212));
    this.mServer = ((TextView)this.mView.findViewById(2131362680));
    this.mUsername = ((TextView)this.mView.findViewById(2131362698));
    this.mPassword = ((TextView)this.mView.findViewById(2131362699));
    this.mSearchDomains = ((TextView)this.mView.findViewById(2131362694));
    this.mDnsServers = ((TextView)this.mView.findViewById(2131362695));
    this.mRoutes = ((TextView)this.mView.findViewById(2131362696));
    this.mMppe = ((CheckBox)this.mView.findViewById(2131362681));
    this.mL2tpSecret = ((TextView)this.mView.findViewById(2131362683));
    this.mIpsecIdentifier = ((TextView)this.mView.findViewById(2131362685));
    this.mIpsecSecret = ((TextView)this.mView.findViewById(2131362686));
    this.mIpsecUserCert = ((Spinner)this.mView.findViewById(2131362688));
    this.mIpsecCaCert = ((Spinner)this.mView.findViewById(2131362690));
    this.mIpsecServerCert = ((Spinner)this.mView.findViewById(2131362691));
    this.mSaveLogin = ((CheckBox)this.mView.findViewById(2131362700));
    this.mShowOptions = ((CheckBox)this.mView.findViewById(2131362692));
    this.mAlwaysOnVpn = ((CheckBox)this.mView.findViewById(2131362701));
    this.mName.setText(this.mProfile.name);
    this.mType.setSelection(this.mProfile.type);
    this.mServer.setText(this.mProfile.server);
    if (this.mProfile.saveLogin)
    {
      this.mUsername.setText(this.mProfile.username);
      this.mPassword.setText(this.mProfile.password);
    }
    this.mSearchDomains.setText(this.mProfile.searchDomains);
    this.mDnsServers.setText(this.mProfile.dnsServers);
    this.mRoutes.setText(this.mProfile.routes);
    this.mMppe.setChecked(this.mProfile.mppe);
    this.mL2tpSecret.setText(this.mProfile.l2tpSecret);
    this.mIpsecIdentifier.setText(this.mProfile.ipsecIdentifier);
    this.mIpsecSecret.setText(this.mProfile.ipsecSecret);
    loadCertificates(this.mIpsecUserCert, "USRPKEY_", 0, this.mProfile.ipsecUserCert);
    loadCertificates(this.mIpsecCaCert, "CACERT_", 2131692852, this.mProfile.ipsecCaCert);
    loadCertificates(this.mIpsecServerCert, "USRCERT_", 2131692853, this.mProfile.ipsecServerCert);
    this.mSaveLogin.setChecked(this.mProfile.saveLogin);
    this.mAlwaysOnVpn.setChecked(this.mProfile.key.equals(VpnUtils.getLockdownVpn()));
    this.mAlwaysOnVpn.setOnCheckedChangeListener(this);
    updateSaveLoginStatus();
    if (SystemProperties.getBoolean("persist.radio.imsregrequired", false)) {
      this.mAlwaysOnVpn.setVisibility(8);
    }
    this.mName.addTextChangedListener(this);
    this.mType.setOnItemSelectedListener(this);
    this.mServer.addTextChangedListener(this);
    this.mUsername.addTextChangedListener(this);
    this.mPassword.addTextChangedListener(this);
    this.mDnsServers.addTextChangedListener(this);
    this.mRoutes.addTextChangedListener(this);
    this.mIpsecSecret.addTextChangedListener(this);
    this.mIpsecUserCert.setOnItemSelectedListener(this);
    this.mShowOptions.setOnClickListener(this);
    boolean bool2 = validate(true);
    if ((!this.mEditing) && (bool2))
    {
      bool1 = false;
      this.mEditing = bool1;
      if (!this.mEditing) {
        break label937;
      }
      setTitle(2131692859);
      this.mView.findViewById(2131362679).setVisibility(0);
      changeType(this.mProfile.type);
      this.mSaveLogin.setVisibility(8);
      if ((!this.mProfile.searchDomains.isEmpty()) || (!this.mProfile.dnsServers.isEmpty()) || (!this.mProfile.routes.isEmpty())) {
        break label930;
      }
      label830:
      if (this.mExists) {
        setButton(-3, localContext.getString(2131692860), this.mListener);
      }
      setButton(-1, localContext.getString(2131692856), this.mListener);
      label872:
      setButton(-2, localContext.getString(2131692854), this.mListener);
      super.onCreate(paramBundle);
      paramBundle = getButton(-1);
      if (!this.mEditing) {
        break label983;
      }
    }
    label930:
    label937:
    label983:
    for (boolean bool1 = bool2;; bool1 = validate(false))
    {
      paramBundle.setEnabled(bool1);
      getWindow().setSoftInputMode(20);
      return;
      bool1 = true;
      break;
      showAdvancedOptions();
      break label830;
      setTitle(localContext.getString(2131692861, new Object[] { this.mProfile.name }));
      setButton(-1, localContext.getString(2131692857), this.mListener);
      break label872;
    }
  }
  
  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    if (paramAdapterView == this.mType) {
      changeType(paramInt);
    }
    getButton(-1).setEnabled(validate(this.mEditing));
  }
  
  public void onNothingSelected(AdapterView<?> paramAdapterView) {}
  
  public void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    if (this.mShowOptions.isChecked()) {
      showAdvancedOptions();
    }
  }
  
  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\vpn2\ConfigDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */