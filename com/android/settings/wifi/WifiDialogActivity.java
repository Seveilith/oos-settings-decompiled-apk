package com.android.settings.wifi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.SetupWizardUtils;
import com.android.settingslib.wifi.AccessPoint;
import com.android.setupwizardlib.util.WizardManagerHelper;

public class WifiDialogActivity
  extends Activity
  implements WifiDialog.WifiDialogListener, DialogInterface.OnDismissListener
{
  private static final String KEY_ACCESS_POINT_STATE = "access_point_state";
  private static final String KEY_WIFI_CONFIGURATION = "wifi_configuration";
  private static final int RESULT_CONNECTED = 1;
  private static final int RESULT_FORGET = 2;
  private static final String TAG = "WifiDialogActivity";
  
  public void finish()
  {
    super.finish();
    overridePendingTransition(0, 0);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    Object localObject = getIntent();
    if (WizardManagerHelper.isSetupWizardIntent((Intent)localObject)) {
      setTheme(SetupWizardUtils.getTransparentTheme((Intent)localObject));
    }
    super.onCreate(paramBundle);
    localObject = ((Intent)localObject).getBundleExtra("access_point_state");
    paramBundle = null;
    if (localObject != null) {
      paramBundle = new AccessPoint(this, (Bundle)localObject);
    }
    paramBundle = new WifiDialog(this, this, paramBundle, 1);
    paramBundle.show();
    paramBundle.setOnDismissListener(this);
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    finish();
  }
  
  public void onForget(WifiDialog paramWifiDialog)
  {
    Object localObject = (WifiManager)getSystemService(WifiManager.class);
    paramWifiDialog = paramWifiDialog.getController().getAccessPoint();
    if (paramWifiDialog != null)
    {
      if (paramWifiDialog.isSaved()) {
        break label134;
      }
      if ((paramWifiDialog.getNetworkInfo() == null) || (paramWifiDialog.getNetworkInfo().getState() == NetworkInfo.State.DISCONNECTED)) {
        break label103;
      }
      ((WifiManager)localObject).disableEphemeralNetwork(AccessPoint.convertToQuotedString(paramWifiDialog.getSsidStr()));
    }
    for (;;)
    {
      localObject = new Intent();
      if (paramWifiDialog != null)
      {
        Bundle localBundle = new Bundle();
        paramWifiDialog.saveWifiState(localBundle);
        ((Intent)localObject).putExtra("access_point_state", localBundle);
      }
      setResult(2);
      finish();
      return;
      label103:
      Log.e("WifiDialogActivity", "Failed to forget invalid network " + paramWifiDialog.getConfig());
      continue;
      label134:
      ((WifiManager)localObject).forget(paramWifiDialog.getConfig().networkId, null);
    }
  }
  
  public void onSubmit(WifiDialog paramWifiDialog)
  {
    WifiConfiguration localWifiConfiguration = paramWifiDialog.getController().getConfig();
    paramWifiDialog = paramWifiDialog.getController().getAccessPoint();
    Object localObject1 = (WifiManager)getSystemService(WifiManager.class);
    if (localWifiConfiguration == null) {
      if ((paramWifiDialog != null) && (paramWifiDialog.isSaved())) {
        ((WifiManager)localObject1).connect(paramWifiDialog.getConfig(), null);
      }
    }
    for (;;)
    {
      localObject1 = new Intent();
      Object localObject2;
      if (paramWifiDialog != null)
      {
        localObject2 = new Bundle();
        paramWifiDialog.saveWifiState((Bundle)localObject2);
        ((Intent)localObject1).putExtra("access_point_state", (Bundle)localObject2);
      }
      if (localWifiConfiguration != null) {
        ((Intent)localObject1).putExtra("wifi_configuration", localWifiConfiguration);
      }
      setResult(1, (Intent)localObject1);
      finish();
      return;
      ((WifiManager)localObject1).save(localWifiConfiguration, null);
      if (paramWifiDialog != null)
      {
        localObject2 = paramWifiDialog.getNetworkInfo();
        if ((localObject2 == null) || (!((NetworkInfo)localObject2).isConnected())) {
          ((WifiManager)localObject1).connect(localWifiConfiguration, null);
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiDialogActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */