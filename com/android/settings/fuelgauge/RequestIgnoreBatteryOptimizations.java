package com.android.settings.fuelgauge;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.IDeviceIdleController;
import android.os.IDeviceIdleController.Stub;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;

public class RequestIgnoreBatteryOptimizations
  extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private static final String DEVICE_IDLE_SERVICE = "deviceidle";
  static final String TAG = "RequestIgnoreBatteryOptimizations";
  IDeviceIdleController mDeviceIdleService;
  String mPackageName;
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    case -2: 
    default: 
      return;
    }
    try
    {
      this.mDeviceIdleService.addPowerSaveWhitelistApp(this.mPackageName);
      setResult(-1);
      return;
    }
    catch (RemoteException paramDialogInterface)
    {
      for (;;)
      {
        Log.w("RequestIgnoreBatteryOptimizations", "Unable to reach IDeviceIdleController", paramDialogInterface);
      }
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mDeviceIdleService = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
    paramBundle = getIntent().getData();
    if (paramBundle == null)
    {
      Log.w("RequestIgnoreBatteryOptimizations", "No data supplied for IGNORE_BATTERY_OPTIMIZATION_SETTINGS in: " + getIntent());
      finish();
      return;
    }
    this.mPackageName = paramBundle.getSchemeSpecificPart();
    if (this.mPackageName == null)
    {
      Log.w("RequestIgnoreBatteryOptimizations", "No data supplied for IGNORE_BATTERY_OPTIMIZATION_SETTINGS in: " + getIntent());
      finish();
      return;
    }
    if (((PowerManager)getSystemService(PowerManager.class)).isIgnoringBatteryOptimizations(this.mPackageName))
    {
      Log.i("RequestIgnoreBatteryOptimizations", "Not should prompt, already ignoring optimizations: " + this.mPackageName);
      finish();
      return;
    }
    try
    {
      paramBundle = getPackageManager().getApplicationInfo(this.mPackageName, 0);
      if (getPackageManager().checkPermission("android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS", this.mPackageName) != 0)
      {
        Log.w("RequestIgnoreBatteryOptimizations", "Requested package " + this.mPackageName + " does not hold permission " + "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
        finish();
        return;
      }
    }
    catch (PackageManager.NameNotFoundException paramBundle)
    {
      Log.w("RequestIgnoreBatteryOptimizations", "Requested package doesn't exist: " + this.mPackageName);
      finish();
      return;
    }
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mTitle = getText(2131693465);
    localAlertParams.mMessage = getString(2131693466, new Object[] { paramBundle.loadLabel(getPackageManager()) });
    localAlertParams.mPositiveButtonText = getText(2131690771);
    localAlertParams.mNegativeButtonText = getText(2131690772);
    localAlertParams.mPositiveButtonListener = this;
    localAlertParams.mNegativeButtonListener = this;
    setupAlert();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\RequestIgnoreBatteryOptimizations.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */