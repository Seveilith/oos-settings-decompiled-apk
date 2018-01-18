package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class CachedBluetoothDeviceManager
{
  private static final boolean DEBUG = true;
  private static final String TAG = "CachedBluetoothDeviceManager";
  private final LocalBluetoothManager mBtManager;
  private final List<CachedBluetoothDevice> mCachedDevices = new ArrayList();
  private Context mContext;
  
  CachedBluetoothDeviceManager(Context paramContext, LocalBluetoothManager paramLocalBluetoothManager)
  {
    this.mContext = paramContext;
    this.mBtManager = paramLocalBluetoothManager;
  }
  
  private void log(String paramString)
  {
    Log.d("CachedBluetoothDeviceManager", paramString);
  }
  
  public static boolean onDeviceDisappeared(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    boolean bool = false;
    paramCachedBluetoothDevice.setVisible(false);
    if (paramCachedBluetoothDevice.getBondState() == 10) {
      bool = true;
    }
    return bool;
  }
  
  public CachedBluetoothDevice addDevice(LocalBluetoothAdapter arg1, LocalBluetoothProfileManager paramLocalBluetoothProfileManager, BluetoothDevice paramBluetoothDevice)
  {
    paramLocalBluetoothProfileManager = new CachedBluetoothDevice(this.mContext, ???, paramLocalBluetoothProfileManager, paramBluetoothDevice);
    synchronized (this.mCachedDevices)
    {
      this.mCachedDevices.add(paramLocalBluetoothProfileManager);
      this.mBtManager.getEventManager().dispatchDeviceAdded(paramLocalBluetoothProfileManager);
      return paramLocalBluetoothProfileManager;
    }
  }
  
  public void clearNonBondedDevices()
  {
    try
    {
      int i = this.mCachedDevices.size() - 1;
      while (i >= 0)
      {
        if (((CachedBluetoothDevice)this.mCachedDevices.get(i)).getBondState() == 10) {
          this.mCachedDevices.remove(i);
        }
        i -= 1;
      }
      return;
    }
    finally {}
  }
  
  public CachedBluetoothDevice findDevice(BluetoothDevice paramBluetoothDevice)
  {
    Iterator localIterator = this.mCachedDevices.iterator();
    while (localIterator.hasNext())
    {
      CachedBluetoothDevice localCachedBluetoothDevice = (CachedBluetoothDevice)localIterator.next();
      if (localCachedBluetoothDevice.getDevice().equals(paramBluetoothDevice)) {
        return localCachedBluetoothDevice;
      }
    }
    return null;
  }
  
  public Collection<CachedBluetoothDevice> getCachedDevicesCopy()
  {
    try
    {
      ArrayList localArrayList = new ArrayList(this.mCachedDevices);
      return localArrayList;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public String getName(BluetoothDevice paramBluetoothDevice)
  {
    Object localObject = findDevice(paramBluetoothDevice);
    if (localObject != null) {
      return ((CachedBluetoothDevice)localObject).getName();
    }
    localObject = paramBluetoothDevice.getAliasName();
    if (localObject != null) {
      return (String)localObject;
    }
    return paramBluetoothDevice.getAddress();
  }
  
  public void onBluetoothStateChanged(int paramInt)
  {
    if (paramInt == 13) {}
    for (;;)
    {
      try
      {
        paramInt = this.mCachedDevices.size() - 1;
        if (paramInt >= 0)
        {
          CachedBluetoothDevice localCachedBluetoothDevice = (CachedBluetoothDevice)this.mCachedDevices.get(paramInt);
          if (localCachedBluetoothDevice.getBondState() != 12)
          {
            localCachedBluetoothDevice.setVisible(false);
            this.mCachedDevices.remove(paramInt);
          }
          else
          {
            localCachedBluetoothDevice.clearProfileConnectionState();
          }
        }
      }
      finally {}
      return;
      paramInt -= 1;
    }
  }
  
  public void onBtClassChanged(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      paramBluetoothDevice = findDevice(paramBluetoothDevice);
      if (paramBluetoothDevice != null) {
        paramBluetoothDevice.refreshBtClass();
      }
      return;
    }
    finally {}
  }
  
  public void onDeviceNameUpdated(BluetoothDevice paramBluetoothDevice)
  {
    paramBluetoothDevice = findDevice(paramBluetoothDevice);
    if (paramBluetoothDevice != null) {
      paramBluetoothDevice.refreshName();
    }
  }
  
  public void onScanningStateChanged(boolean paramBoolean)
  {
    if (!paramBoolean) {
      return;
    }
    try
    {
      int i = this.mCachedDevices.size() - 1;
      while (i >= 0)
      {
        ((CachedBluetoothDevice)this.mCachedDevices.get(i)).setVisible(false);
        i -= 1;
      }
      return;
    }
    finally {}
  }
  
  public void onUuidChanged(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      paramBluetoothDevice = findDevice(paramBluetoothDevice);
      if (paramBluetoothDevice != null) {
        paramBluetoothDevice.onUuidChanged();
      }
      return;
    }
    finally {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\CachedBluetoothDeviceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */