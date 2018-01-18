package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import com.android.settingslib.R.string;

final class OppProfile
  implements LocalBluetoothProfile
{
  static final String NAME = "OPP";
  private static final int ORDINAL = 2;
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }
  
  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    return 0;
  }
  
  public int getDrawableResource(BluetoothClass paramBluetoothClass)
  {
    return 0;
  }
  
  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return R.string.bluetooth_profile_opp;
  }
  
  public int getOrdinal()
  {
    return 2;
  }
  
  public int getPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return 0;
  }
  
  public int getSummaryResourceForDevice(BluetoothDevice paramBluetoothDevice)
  {
    return 0;
  }
  
  public boolean isAutoConnectable()
  {
    return false;
  }
  
  public boolean isConnectable()
  {
    return false;
  }
  
  public boolean isPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }
  
  public boolean isProfileReady()
  {
    return true;
  }
  
  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean) {}
  
  public String toString()
  {
    return "OPP";
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\OppProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */