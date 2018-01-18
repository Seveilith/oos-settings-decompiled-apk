package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothInputDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;
import com.android.settingslib.R.drawable;
import com.android.settingslib.R.string;
import java.util.List;

public final class HidProfile
  implements LocalBluetoothProfile
{
  static final String NAME = "HID";
  private static final int ORDINAL = 3;
  private static final String TAG = "HidProfile";
  private static boolean V = true;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothInputDevice mService;
  
  HidProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    paramLocalBluetoothAdapter.getProfileProxy(paramContext, new InputDeviceServiceListener(null), 4);
  }
  
  public static int getHidClassDrawable(BluetoothClass paramBluetoothClass)
  {
    switch (paramBluetoothClass.getDeviceClass())
    {
    default: 
      return R.drawable.ic_bt_misc_hid;
    case 1344: 
    case 1472: 
      return R.drawable.ic_lockscreen_ime;
    }
    return R.drawable.ic_bt_pointing_hid;
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return false;
    }
    return this.mService.connect(paramBluetoothDevice);
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return false;
    }
    return this.mService.disconnect(paramBluetoothDevice);
  }
  
  protected void finalize()
  {
    if (V) {
      Log.d("HidProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(4, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("HidProfile", "Error cleaning up HID proxy", localThrowable);
    }
  }
  
  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return 0;
    }
    List localList = this.mService.getConnectedDevices();
    if ((!localList.isEmpty()) && (((BluetoothDevice)localList.get(0)).equals(paramBluetoothDevice))) {
      return this.mService.getConnectionState(paramBluetoothDevice);
    }
    return 0;
  }
  
  public int getDrawableResource(BluetoothClass paramBluetoothClass)
  {
    if (paramBluetoothClass == null) {
      return R.drawable.ic_lockscreen_ime;
    }
    return getHidClassDrawable(paramBluetoothClass);
  }
  
  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return R.string.bluetooth_profile_hid;
  }
  
  public int getOrdinal()
  {
    return 3;
  }
  
  public int getPreferred(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return 0;
    }
    return this.mService.getPriority(paramBluetoothDevice);
  }
  
  public int getSummaryResourceForDevice(BluetoothDevice paramBluetoothDevice)
  {
    int i = getConnectionStatus(paramBluetoothDevice);
    switch (i)
    {
    case 1: 
    default: 
      return Utils.getConnectionStateSummary(i);
    case 0: 
      return R.string.bluetooth_hid_profile_summary_use_for;
    }
    return R.string.bluetooth_hid_profile_summary_connected;
  }
  
  public boolean isAutoConnectable()
  {
    return true;
  }
  
  public boolean isConnectable()
  {
    return true;
  }
  
  public boolean isPreferred(BluetoothDevice paramBluetoothDevice)
  {
    boolean bool = false;
    if (this.mService == null) {
      return false;
    }
    if (this.mService.getPriority(paramBluetoothDevice) > 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isProfileReady()
  {
    return this.mIsProfileReady;
  }
  
  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
  {
    if (this.mService == null) {
      return;
    }
    if (paramBoolean)
    {
      if (this.mService.getPriority(paramBluetoothDevice) < 100) {
        this.mService.setPriority(paramBluetoothDevice, 100);
      }
      return;
    }
    this.mService.setPriority(paramBluetoothDevice, 0);
  }
  
  public String toString()
  {
    return "HID";
  }
  
  private final class InputDeviceServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private InputDeviceServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (HidProfile.-get0()) {
        Log.d("HidProfile", "Bluetooth service connected");
      }
      HidProfile.-set1(HidProfile.this, (BluetoothInputDevice)paramBluetoothProfile);
      List localList = HidProfile.-get4(HidProfile.this).getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = HidProfile.-get1(HidProfile.this).findDevice(localBluetoothDevice);
        paramBluetoothProfile = localCachedBluetoothDevice;
        if (localCachedBluetoothDevice == null)
        {
          Log.w("HidProfile", "HidProfile found new device: " + localBluetoothDevice);
          paramBluetoothProfile = HidProfile.-get1(HidProfile.this).addDevice(HidProfile.-get2(HidProfile.this), HidProfile.-get3(HidProfile.this), localBluetoothDevice);
        }
        paramBluetoothProfile.onProfileStateChanged(HidProfile.this, 2);
        paramBluetoothProfile.refresh();
      }
      HidProfile.-set0(HidProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (HidProfile.-get0()) {
        Log.d("HidProfile", "Bluetooth service disconnected");
      }
      HidProfile.-set0(HidProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\HidProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */