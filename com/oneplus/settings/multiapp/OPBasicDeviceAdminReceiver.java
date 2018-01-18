package com.oneplus.settings.multiapp;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OPBasicDeviceAdminReceiver
  extends DeviceAdminReceiver
{
  private void enableProfile(Context paramContext)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    paramContext = getComponentName(paramContext);
    localDevicePolicyManager.setProfileName(paramContext, "Multi-App");
    localDevicePolicyManager.setProfileEnabled(paramContext);
  }
  
  public static ComponentName getComponentName(Context paramContext)
  {
    return new ComponentName(paramContext.getApplicationContext(), OPBasicDeviceAdminReceiver.class);
  }
  
  public void onProfileProvisioningComplete(Context paramContext, Intent paramIntent)
  {
    Log.e("OPMultiAppListSettings", "onProfileProvisioningComplete");
    enableProfile(paramContext);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\multiapp\OPBasicDeviceAdminReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */