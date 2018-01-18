package com.android.settings.fuelgauge;

import android.os.IDeviceIdleController;
import android.os.IDeviceIdleController.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArraySet;
import android.util.Log;

public class PowerWhitelistBackend
{
  private static final String DEVICE_IDLE_SERVICE = "deviceidle";
  private static final PowerWhitelistBackend INSTANCE = new PowerWhitelistBackend();
  private static final String TAG = "PowerWhitelistBackend";
  private final IDeviceIdleController mDeviceIdleService = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
  private final ArraySet<String> mSysWhitelistedApps = new ArraySet();
  private final ArraySet<String> mWhitelistedApps = new ArraySet();
  
  public PowerWhitelistBackend()
  {
    refreshList();
  }
  
  public static PowerWhitelistBackend getInstance()
  {
    return INSTANCE;
  }
  
  private void refreshList()
  {
    int j = 0;
    this.mSysWhitelistedApps.clear();
    this.mWhitelistedApps.clear();
    try
    {
      String[] arrayOfString = this.mDeviceIdleService.getFullPowerWhitelist();
      int k = arrayOfString.length;
      int i = 0;
      String str;
      while (i < k)
      {
        str = arrayOfString[i];
        this.mWhitelistedApps.add(str);
        i += 1;
      }
      arrayOfString = this.mDeviceIdleService.getSystemPowerWhitelist();
      k = arrayOfString.length;
      i = j;
      while (i < k)
      {
        str = arrayOfString[i];
        this.mSysWhitelistedApps.add(str);
        i += 1;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("PowerWhitelistBackend", "Unable to reach IDeviceIdleController", localRemoteException);
    }
  }
  
  public void addApp(String paramString)
  {
    try
    {
      this.mDeviceIdleService.addPowerSaveWhitelistApp(paramString);
      this.mWhitelistedApps.add(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      Log.w("PowerWhitelistBackend", "Unable to reach IDeviceIdleController", paramString);
    }
  }
  
  public int getWhitelistSize()
  {
    return this.mWhitelistedApps.size();
  }
  
  public boolean isSysWhitelisted(String paramString)
  {
    return this.mSysWhitelistedApps.contains(paramString);
  }
  
  public boolean isWhitelisted(String paramString)
  {
    return this.mWhitelistedApps.contains(paramString);
  }
  
  public void removeApp(String paramString)
  {
    try
    {
      this.mDeviceIdleService.removePowerSaveWhitelistApp(paramString);
      this.mWhitelistedApps.remove(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      Log.w("PowerWhitelistBackend", "Unable to reach IDeviceIdleController", paramString);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\PowerWhitelistBackend.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */