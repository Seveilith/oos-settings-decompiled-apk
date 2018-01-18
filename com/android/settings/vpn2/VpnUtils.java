package com.android.settings.vpn2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.security.KeyStore;

public class VpnUtils
{
  public static void clearLockdownVpn(Context paramContext)
  {
    KeyStore.getInstance().delete("LOCKDOWN_VPN");
    ((ConnectivityManager)paramContext.getSystemService(ConnectivityManager.class)).updateLockdownVpn();
  }
  
  public static String getLockdownVpn()
  {
    byte[] arrayOfByte = KeyStore.getInstance().get("LOCKDOWN_VPN");
    if (arrayOfByte == null) {
      return null;
    }
    return new String(arrayOfByte);
  }
  
  public static boolean isVpnLockdown(String paramString)
  {
    return paramString.equals(getLockdownVpn());
  }
  
  public static void setLockdownVpn(Context paramContext, String paramString)
  {
    KeyStore.getInstance().put("LOCKDOWN_VPN", paramString.getBytes(), -1, 0);
    ((ConnectivityManager)paramContext.getSystemService(ConnectivityManager.class)).updateLockdownVpn();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\vpn2\VpnUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */