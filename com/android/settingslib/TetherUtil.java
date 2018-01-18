package com.android.settingslib;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.telephony.CarrierConfigManager;

public class TetherUtil
{
  private static boolean isEntitlementCheckRequired(Context paramContext)
  {
    paramContext = (CarrierConfigManager)paramContext.getSystemService("carrier_config");
    try
    {
      boolean bool = paramContext.getConfig().getBoolean("require_entitlement_checks_bool");
      return bool;
    }
    catch (Exception paramContext)
    {
      paramContext.printStackTrace();
    }
    return false;
  }
  
  public static boolean isProvisioningNeeded(Context paramContext)
  {
    boolean bool = false;
    String[] arrayOfString = paramContext.getResources().getStringArray(17235994);
    if ((SystemProperties.getBoolean("net.tethering.noprovisioning", false)) || (arrayOfString == null)) {
      return false;
    }
    if (!isEntitlementCheckRequired(paramContext)) {
      return false;
    }
    if (arrayOfString.length == 2) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean setWifiTethering(boolean paramBoolean, Context paramContext)
  {
    return ((WifiManager)paramContext.getSystemService("wifi")).setWifiApEnabled(null, paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\TetherUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */