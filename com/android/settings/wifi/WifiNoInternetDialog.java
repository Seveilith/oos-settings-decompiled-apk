package com.android.settings.wifi;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest.Builder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.internal.app.AlertController.AlertParams;

public final class WifiNoInternetDialog
  extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private static final String TAG = "WifiNoInternetDialog";
  private CheckBox mAlwaysAllow;
  private ConnectivityManager mCM;
  private Network mNetwork;
  private ConnectivityManager.NetworkCallback mNetworkCallback;
  private String mNetworkName;
  
  private void createDialog()
  {
    this.mAlert.setIcon(2130838075);
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mTitle = this.mNetworkName;
    localAlertParams.mMessage = getString(2131691442);
    localAlertParams.mPositiveButtonText = getString(2131690771);
    localAlertParams.mNegativeButtonText = getString(2131690772);
    localAlertParams.mPositiveButtonListener = this;
    localAlertParams.mNegativeButtonListener = this;
    View localView = LayoutInflater.from(localAlertParams.mContext).inflate(17367089, null);
    localAlertParams.mView = localView;
    this.mAlwaysAllow = ((CheckBox)localView.findViewById(16909109));
    this.mAlwaysAllow.setText(getString(2131691443));
    setupAlert();
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    boolean bool1;
    if (paramInt == -1)
    {
      bool1 = true;
      label7:
      if (!bool1) {
        break label54;
      }
    }
    boolean bool2;
    label54:
    for (paramDialogInterface = "Connect";; paramDialogInterface = "Ignore")
    {
      bool2 = this.mAlwaysAllow.isChecked();
      switch (paramInt)
      {
      default: 
        return;
        bool1 = false;
        break label7;
      }
    }
    this.mCM.setAcceptUnvalidated(this.mNetwork, bool1, bool2);
    StringBuilder localStringBuilder = new StringBuilder().append(paramDialogInterface).append(" network=").append(this.mNetwork);
    if (bool2) {}
    for (paramDialogInterface = " and remember";; paramDialogInterface = "")
    {
      Log.d("WifiNoInternetDialog", paramDialogInterface);
      return;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent();
    if ((paramBundle != null) && (paramBundle.getAction().equals("android.net.conn.PROMPT_UNVALIDATED")) && ("netId".equals(paramBundle.getScheme()))) {}
    try
    {
      this.mNetwork = new Network(Integer.parseInt(paramBundle.getData().getSchemeSpecificPart()));
      if (this.mNetwork == null)
      {
        Log.e("WifiNoInternetDialog", "Can't determine network from '" + paramBundle.getData() + "' , exiting");
        finish();
        return;
        Log.e("WifiNoInternetDialog", "Unexpected intent " + paramBundle + ", exiting");
        finish();
        return;
      }
    }
    catch (NullPointerException|NumberFormatException localNullPointerException)
    {
      for (;;)
      {
        this.mNetwork = null;
      }
      paramBundle = new NetworkRequest.Builder().clearCapabilities().build();
      this.mNetworkCallback = new ConnectivityManager.NetworkCallback()
      {
        public void onCapabilitiesChanged(Network paramAnonymousNetwork, NetworkCapabilities paramAnonymousNetworkCapabilities)
        {
          if ((WifiNoInternetDialog.-get0(WifiNoInternetDialog.this).equals(paramAnonymousNetwork)) && (paramAnonymousNetworkCapabilities.hasCapability(16)))
          {
            Log.d("WifiNoInternetDialog", "Network " + WifiNoInternetDialog.-get0(WifiNoInternetDialog.this) + " validated");
            WifiNoInternetDialog.this.finish();
          }
        }
        
        public void onLost(Network paramAnonymousNetwork)
        {
          if (WifiNoInternetDialog.-get0(WifiNoInternetDialog.this).equals(paramAnonymousNetwork))
          {
            Log.d("WifiNoInternetDialog", "Network " + WifiNoInternetDialog.-get0(WifiNoInternetDialog.this) + " disconnected");
            WifiNoInternetDialog.this.finish();
          }
        }
      };
      this.mCM = ((ConnectivityManager)getSystemService("connectivity"));
      this.mCM.registerNetworkCallback(paramBundle, this.mNetworkCallback);
      paramBundle = this.mCM.getNetworkInfo(this.mNetwork);
      if ((paramBundle != null) && (paramBundle.isConnectedOrConnecting()))
      {
        this.mNetworkName = paramBundle.getExtraInfo();
        if (this.mNetworkName != null) {
          this.mNetworkName = this.mNetworkName.replaceAll("^\"|\"$", "");
        }
        createDialog();
        return;
      }
      Log.d("WifiNoInternetDialog", "Network " + this.mNetwork + " is not connected: " + paramBundle);
      finish();
    }
  }
  
  protected void onDestroy()
  {
    if (this.mNetworkCallback != null)
    {
      this.mCM.unregisterNetworkCallback(this.mNetworkCallback);
      this.mNetworkCallback = null;
    }
    super.onDestroy();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiNoInternetDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */