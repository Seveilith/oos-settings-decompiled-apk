package com.android.settingslib.bluetooth;

public abstract interface BluetoothCallback
{
  public abstract void onBluetoothStateChanged(int paramInt);
  
  public abstract void onConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt);
  
  public abstract void onDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice);
  
  public abstract void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt);
  
  public abstract void onDeviceDeleted(CachedBluetoothDevice paramCachedBluetoothDevice);
  
  public abstract void onScanningStateChanged(boolean paramBoolean);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\BluetoothCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */