package com.android.settings.wifi;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.provider.Settings.Global;

public class WifiScanModeActivity
  extends Activity
{
  private String mApp;
  private DialogFragment mDialog;
  
  private void createDialog()
  {
    if (this.mDialog == null)
    {
      this.mDialog = AlertDialogFragment.newInstance(this.mApp);
      this.mDialog.show(getFragmentManager(), "dialog");
    }
  }
  
  private void dismissDialog()
  {
    if (this.mDialog != null)
    {
      this.mDialog.dismiss();
      this.mDialog = null;
    }
  }
  
  private void doNegativeClick()
  {
    setResult(0);
    finish();
  }
  
  private void doPositiveClick()
  {
    Settings.Global.putInt(getContentResolver(), "wifi_scan_always_enabled", 1);
    setResult(-1);
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Intent localIntent = getIntent();
    if (paramBundle == null) {
      if ((localIntent != null) && (localIntent.getAction().equals("android.net.wifi.action.REQUEST_SCAN_ALWAYS_AVAILABLE"))) {
        this.mApp = getCallingPackage();
      }
    }
    try
    {
      paramBundle = getPackageManager();
      for (this.mApp = ((String)paramBundle.getApplicationLabel(paramBundle.getApplicationInfo(this.mApp, 0)));; this.mApp = paramBundle.getString("app"))
      {
        createDialog();
        return;
        finish();
        return;
      }
    }
    catch (PackageManager.NameNotFoundException paramBundle)
    {
      for (;;) {}
    }
  }
  
  public void onPause()
  {
    super.onPause();
    dismissDialog();
  }
  
  public void onResume()
  {
    super.onResume();
    createDialog();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putString("app", this.mApp);
  }
  
  public static class AlertDialogFragment
    extends DialogFragment
  {
    private final String mApp;
    
    public AlertDialogFragment()
    {
      this.mApp = null;
    }
    
    public AlertDialogFragment(String paramString)
    {
      this.mApp = paramString;
    }
    
    static AlertDialogFragment newInstance(String paramString)
    {
      return new AlertDialogFragment(paramString);
    }
    
    public void onCancel(DialogInterface paramDialogInterface)
    {
      WifiScanModeActivity.-wrap0((WifiScanModeActivity)getActivity());
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      new AlertDialog.Builder(getActivity()).setMessage(getString(2131691435, new Object[] { this.mApp })).setPositiveButton(2131691437, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          WifiScanModeActivity.-wrap1((WifiScanModeActivity)WifiScanModeActivity.AlertDialogFragment.this.getActivity());
        }
      }).setNegativeButton(2131691438, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          WifiScanModeActivity.-wrap0((WifiScanModeActivity)WifiScanModeActivity.AlertDialogFragment.this.getActivity());
        }
      }).create();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiScanModeActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */