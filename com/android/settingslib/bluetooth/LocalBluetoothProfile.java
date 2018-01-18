package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

public abstract interface LocalBluetoothProfile
{
  public abstract boolean connect(BluetoothDevice paramBluetoothDevice);
  
  public abstract boolean disconnect(BluetoothDevice paramBluetoothDevice);
  
  public abstract int getConnectionStatus(BluetoothDevice paramBluetoothDevice);
  
  public abstract int getDrawableResource(BluetoothClass paramBluetoothClass);
  
  public abstract int getNameResource(BluetoothDevice paramBluetoothDevice);
  
  public abstract int getOrdinal();
  
  public abstract int getPreferred(BluetoothDevice paramBluetoothDevice);
  
  public abstract int getSummaryResourceForDevice(BluetoothDevice paramBluetoothDevice);
  
  public abstract boolean isAutoConnectable();
  
  public abstract boolean isConnectable();
  
  public abstract boolean isPreferred(BluetoothDevice paramBluetoothDevice);
  
  public abstract boolean isProfileReady();
  
  public abstract void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\LocalBluetoothProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */