package com.android.settingslib;

import android.content.Context;
import android.provider.Settings.Global;

public class WirelessUtils
{
  public static boolean isAirplaneModeOn(Context paramContext)
  {
    boolean bool = false;
    if (Settings.Global.getInt(paramContext.getContentResolver(), "airplane_mode_on", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isRadioAllowed(Context paramContext, String paramString)
  {
    if (!isAirplaneModeOn(paramContext)) {
      return true;
    }
    paramContext = Settings.Global.getString(paramContext.getContentResolver(), "airplane_mode_toggleable_radios");
    if (paramContext != null) {
      return paramContext.contains(paramString);
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\WirelessUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */