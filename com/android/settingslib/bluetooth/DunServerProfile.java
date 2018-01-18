package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothDun;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;
import com.android.settingslib.R.drawable;
import com.android.settingslib.R.string;

final class DunServerProfile
  implements LocalBluetoothProfile
{
  static final String NAME = "DUN Server";
  private static final int ORDINAL = 11;
  private static final String TAG = "DunServerProfile";
  private static boolean V = true;
  private boolean mIsProfileReady;
  private BluetoothDun mService;
  
  DunServerProfile(Context paramContext)
  {
    BluetoothAdapter.getDefaultAdapter().getProfileProxy(paramContext, new DunServiceListener(null), 21);
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    return false;
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
      Log.d("DunServerProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(21, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("DunServerProfile", "Error cleaning up DUN proxy", localThrowable);
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
    return R.string.bluetooth_profile_dun;
  }
  
  public int getOrdinal()
  {
    return 11;
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
      return R.string.bluetooth_dun_profile_summary_use_for;
    }
    return R.string.bluetooth_dun_profile_summary_connected;
  }
  
  public boolean isAutoConnectable()
  {
    return false;
  }
  
  public boolean isConnectable()
  {
    return true;
  }
  
  public boolean isPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return true;
  }
  
  public boolean isProfileReady()
  {
    return this.mIsProfileReady;
  }
  
  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean) {}
  
  public String toString()
  {
    return "DUN Server";
  }
  
  private final class DunServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private DunServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (DunServerProfile.-get0()) {
        Log.d("DunServerProfile", "Bluetooth service connected");
      }
      DunServerProfile.-set1(DunServerProfile.this, (BluetoothDun)paramBluetoothProfile);
      DunServerProfile.-set0(DunServerProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (DunServerProfile.-get0()) {
        Log.d("DunServerProfile", "Bluetooth service disconnected");
      }
      DunServerProfile.-set0(DunServerProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\DunServerProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */