package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;
import com.android.settingslib.R.drawable;
import com.android.settingslib.R.string;
import java.util.HashMap;
import java.util.Iterator;

public final class PanProfile
  implements LocalBluetoothProfile
{
  static final String NAME = "PAN";
  private static final int ORDINAL = 4;
  private static final String TAG = "PanProfile";
  private static boolean V = true;
  private final HashMap<BluetoothDevice, Integer> mDeviceRoleMap = new HashMap();
  private boolean mIsProfileReady;
  private BluetoothPan mService;
  
  PanProfile(Context paramContext)
  {
    BluetoothAdapter.getDefaultAdapter().getProfileProxy(paramContext, new PanServiceListener(null), 5);
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return false;
    }
    Object localObject = this.mService.getConnectedDevices();
    if (localObject != null)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)((Iterator)localObject).next();
        this.mService.disconnect(localBluetoothDevice);
      }
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
      Log.d("PanProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(5, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("PanProfile", "Error cleaning up PAN proxy", localThrowable);
    }
  }
  
  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return 0;
    }
    return this.mService.getConnectionState(paramBluetoothDevice);
  }
  
  public int getDrawableResource(BluetoothClass paramBluetoothClass)
  {
    return R.drawable.ic_bt_network_pan;
  }
  
  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    if (isLocalRoleNap(paramBluetoothDevice)) {
      return R.string.bluetooth_profile_pan_nap;
    }
    return R.string.bluetooth_profile_pan;
  }
  
  public int getOrdinal()
  {
    return 4;
  }
  
  public int getPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return -1;
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
      return R.string.bluetooth_pan_profile_summary_use_for;
    }
    if (isLocalRoleNap(paramBluetoothDevice)) {
      return R.string.bluetooth_pan_nap_profile_summary_connected;
    }
    return R.string.bluetooth_pan_user_profile_summary_connected;
  }
  
  public boolean isAutoConnectable()
  {
    return false;
  }
  
  public boolean isConnectable()
  {
    return true;
  }
  
  boolean isLocalRoleNap(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mDeviceRoleMap.containsKey(paramBluetoothDevice)) {
      return ((Integer)this.mDeviceRoleMap.get(paramBluetoothDevice)).intValue() == 1;
    }
    return false;
  }
  
  public boolean isPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return true;
  }
  
  public boolean isProfileReady()
  {
    return this.mIsProfileReady;
  }
  
  void setLocalRole(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    this.mDeviceRoleMap.put(paramBluetoothDevice, Integer.valueOf(paramInt));
  }
  
  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean) {}
  
  public String toString()
  {
    return "PAN";
  }
  
  private final class PanServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private PanServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (PanProfile.-get0()) {
        Log.d("PanProfile", "Bluetooth service connected");
      }
      PanProfile.-set1(PanProfile.this, (BluetoothPan)paramBluetoothProfile);
      PanProfile.-set0(PanProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (PanProfile.-get0()) {
        Log.d("PanProfile", "Bluetooth service disconnected");
      }
      PanProfile.-set0(PanProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\PanProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */