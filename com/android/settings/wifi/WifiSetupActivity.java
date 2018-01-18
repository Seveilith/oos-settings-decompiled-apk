package com.android.settings.wifi;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.Theme;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Button;
import com.android.settings.ButtonBarHandler;
import com.android.settings.SetupWizardUtils;
import com.android.setupwizardlib.view.NavigationBar;
import com.android.setupwizardlib.view.NavigationBar.NavigationBarListener;

public class WifiSetupActivity
  extends WifiPickerActivity
  implements ButtonBarHandler, NavigationBar.NavigationBarListener
{
  private static final String EXTRA_AUTO_FINISH_ON_CONNECT = "wifi_auto_finish_on_connect";
  private static final String EXTRA_IS_NETWORK_REQUIRED = "is_network_required";
  private static final String EXTRA_IS_WIFI_REQUIRED = "is_wifi_required";
  private static final String EXTRA_REQUIRE_USER_NETWORK_SELECTION = "wifi_require_user_network_selection";
  private static final String PARAM_USER_SELECTED_NETWORK = "userSelectedNetwork";
  private static final int RESULT_SKIP = 1;
  private static final String TAG = "WifiSetupActivity";
  private boolean mAutoFinishOnConnection;
  private IntentFilter mFilter = new IntentFilter();
  private boolean mIsNetworkRequired;
  private boolean mIsWifiRequired;
  private NavigationBar mNavigationBar;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      WifiSetupActivity.-wrap0(WifiSetupActivity.this);
    }
  };
  private boolean mUserSelectedNetwork;
  private boolean mWifiConnected;
  
  private boolean isNetworkConnected()
  {
    boolean bool = false;
    Object localObject = (ConnectivityManager)getSystemService("connectivity");
    if (localObject == null) {
      return false;
    }
    localObject = ((ConnectivityManager)localObject).getActiveNetworkInfo();
    if (localObject != null) {
      bool = ((NetworkInfo)localObject).isConnected();
    }
    return bool;
  }
  
  private boolean isWifiConnected()
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
    if (localConnectivityManager != null) {}
    for (boolean bool = localConnectivityManager.getNetworkInfo(1).isConnected();; bool = false)
    {
      this.mWifiConnected = bool;
      return bool;
    }
  }
  
  private void refreshConnectionState()
  {
    if (isWifiConnected())
    {
      if ((this.mAutoFinishOnConnection) && (this.mUserSelectedNetwork))
      {
        Log.d("WifiSetupActivity", "Auto-finishing with connection");
        finish(-1);
        this.mUserSelectedNetwork = false;
      }
      setNextButtonText(2131689822);
      setNextButtonEnabled(true);
      return;
    }
    if ((!this.mIsWifiRequired) && ((!this.mIsNetworkRequired) || (isNetworkConnected())))
    {
      setNextButtonText(2131690971);
      setNextButtonEnabled(true);
      return;
    }
    setNextButtonText(2131690971);
    setNextButtonEnabled(false);
  }
  
  private void setNextButtonEnabled(boolean paramBoolean)
  {
    if (this.mNavigationBar != null) {
      this.mNavigationBar.getNextButton().setEnabled(paramBoolean);
    }
  }
  
  private void setNextButtonText(int paramInt)
  {
    if (this.mNavigationBar != null) {
      this.mNavigationBar.getNextButton().setText(paramInt);
    }
  }
  
  public void finish(int paramInt)
  {
    Log.d("WifiSetupActivity", "finishing, resultCode=" + paramInt);
    setResult(paramInt);
    finish();
  }
  
  Class<? extends PreferenceFragment> getWifiSettingsClass()
  {
    return WifiSettingsForSetupWizard.class;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return WifiSettingsForSetupWizard.class.getName().equals(paramString);
  }
  
  void networkSelected()
  {
    Log.d("WifiSetupActivity", "Network selected by user");
    this.mUserSelectedNetwork = true;
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getTheme(getIntent()), paramBoolean);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    boolean bool = false;
    super.onCreate(paramBundle);
    paramBundle = getIntent();
    this.mFilter.addAction("android.net.wifi.STATE_CHANGE");
    this.mFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    this.mAutoFinishOnConnection = paramBundle.getBooleanExtra("wifi_auto_finish_on_connect", false);
    this.mIsNetworkRequired = paramBundle.getBooleanExtra("is_network_required", false);
    this.mIsWifiRequired = paramBundle.getBooleanExtra("is_wifi_required", false);
    if (paramBundle.getBooleanExtra("wifi_require_user_network_selection", false)) {}
    for (;;)
    {
      this.mUserSelectedNetwork = bool;
      return;
      bool = true;
    }
  }
  
  public void onNavigateBack()
  {
    onBackPressed();
  }
  
  public void onNavigateNext()
  {
    if (this.mWifiConnected)
    {
      finish(-1);
      return;
    }
    if (isNetworkConnected()) {}
    for (int i = 2131691458;; i = 2131691459)
    {
      WifiSkipDialog.newInstance(i).show(getFragmentManager(), "dialog");
      return;
    }
  }
  
  public void onNavigationBarCreated(NavigationBar paramNavigationBar)
  {
    this.mNavigationBar = paramNavigationBar;
    paramNavigationBar.setNavigationBarListener(this);
    SetupWizardUtils.setImmersiveMode(this);
  }
  
  public void onPause()
  {
    unregisterReceiver(this.mReceiver);
    super.onPause();
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    this.mUserSelectedNetwork = paramBundle.getBoolean("userSelectedNetwork", true);
  }
  
  public void onResume()
  {
    super.onResume();
    registerReceiver(this.mReceiver, this.mFilter);
    refreshConnectionState();
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("userSelectedNetwork", this.mUserSelectedNetwork);
  }
  
  public static class WifiSkipDialog
    extends DialogFragment
  {
    public static WifiSkipDialog newInstance(int paramInt)
    {
      Bundle localBundle = new Bundle();
      localBundle.putInt("messageRes", paramInt);
      WifiSkipDialog localWifiSkipDialog = new WifiSkipDialog();
      localWifiSkipDialog.setArguments(localBundle);
      return localWifiSkipDialog;
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      int i = getArguments().getInt("messageRes");
      paramBundle = new AlertDialog.Builder(getActivity()).setMessage(i).setCancelable(false).setPositiveButton(2131691456, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          ((WifiSetupActivity)WifiSetupActivity.WifiSkipDialog.this.getActivity()).finish(1);
        }
      }).setNegativeButton(2131691457, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
      }).create();
      SetupWizardUtils.applyImmersiveFlags(paramBundle);
      return paramBundle;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiSetupActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */