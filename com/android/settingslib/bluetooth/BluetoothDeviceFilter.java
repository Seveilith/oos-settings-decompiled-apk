package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.os.ParcelUuid;
import android.util.Log;

public final class BluetoothDeviceFilter
{
  public static final Filter ALL_FILTER = new AllFilter(null);
  public static final Filter BONDED_DEVICE_FILTER = new BondedDeviceFilter(null);
  private static final Filter[] FILTERS = { ALL_FILTER, new AudioFilter(null), new TransferFilter(null), new PanuFilter(null), new NapFilter(null) };
  private static final String TAG = "BluetoothDeviceFilter";
  public static final Filter UNBONDED_DEVICE_FILTER = new UnbondedDeviceFilter(null);
  
  public static Filter getFilter(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < FILTERS.length)) {
      return FILTERS[paramInt];
    }
    Log.w("BluetoothDeviceFilter", "Invalid filter type " + paramInt + " for device picker");
    return ALL_FILTER;
  }
  
  private static final class AllFilter
    implements BluetoothDeviceFilter.Filter
  {
    public boolean matches(BluetoothDevice paramBluetoothDevice)
    {
      return true;
    }
  }
  
  private static final class AudioFilter
    extends BluetoothDeviceFilter.ClassUuidFilter
  {
    private AudioFilter()
    {
      super();
    }
    
    boolean matches(ParcelUuid[] paramArrayOfParcelUuid, BluetoothClass paramBluetoothClass)
    {
      if (paramArrayOfParcelUuid != null)
      {
        if (BluetoothUuid.containsAnyUuid(paramArrayOfParcelUuid, A2dpProfile.SINK_UUIDS)) {
          return true;
        }
        if (BluetoothUuid.containsAnyUuid(paramArrayOfParcelUuid, HeadsetProfile.UUIDS)) {
          return true;
        }
      }
      else if ((paramBluetoothClass != null) && ((paramBluetoothClass.doesClassMatch(1)) || (paramBluetoothClass.doesClassMatch(0))))
      {
        return true;
      }
      return false;
    }
  }
  
  private static final class BondedDeviceFilter
    implements BluetoothDeviceFilter.Filter
  {
    public boolean matches(BluetoothDevice paramBluetoothDevice)
    {
      return paramBluetoothDevice.getBondState() == 12;
    }
  }
  
  private static abstract class ClassUuidFilter
    implements BluetoothDeviceFilter.Filter
  {
    public boolean matches(BluetoothDevice paramBluetoothDevice)
    {
      return matches(paramBluetoothDevice.getUuids(), paramBluetoothDevice.getBluetoothClass());
    }
    
    abstract boolean matches(ParcelUuid[] paramArrayOfParcelUuid, BluetoothClass paramBluetoothClass);
  }
  
  public static abstract interface Filter
  {
    public abstract boolean matches(BluetoothDevice paramBluetoothDevice);
  }
  
  private static final class NapFilter
    extends BluetoothDeviceFilter.ClassUuidFilter
  {
    private NapFilter()
    {
      super();
    }
    
    boolean matches(ParcelUuid[] paramArrayOfParcelUuid, BluetoothClass paramBluetoothClass)
    {
      if ((paramArrayOfParcelUuid != null) && (BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.NAP))) {
        return true;
      }
      if (paramBluetoothClass != null) {
        return paramBluetoothClass.doesClassMatch(5);
      }
      return false;
    }
  }
  
  private static final class PanuFilter
    extends BluetoothDeviceFilter.ClassUuidFilter
  {
    private PanuFilter()
    {
      super();
    }
    
    boolean matches(ParcelUuid[] paramArrayOfParcelUuid, BluetoothClass paramBluetoothClass)
    {
      if ((paramArrayOfParcelUuid != null) && (BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.PANU))) {
        return true;
      }
      if (paramBluetoothClass != null) {
        return paramBluetoothClass.doesClassMatch(4);
      }
      return false;
    }
  }
  
  private static final class TransferFilter
    extends BluetoothDeviceFilter.ClassUuidFilter
  {
    private TransferFilter()
    {
      super();
    }
    
    boolean matches(ParcelUuid[] paramArrayOfParcelUuid, BluetoothClass paramBluetoothClass)
    {
      if ((paramArrayOfParcelUuid != null) && (BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.ObexObjectPush))) {
        return true;
      }
      if (paramBluetoothClass != null) {
        return paramBluetoothClass.doesClassMatch(2);
      }
      return false;
    }
  }
  
  private static final class UnbondedDeviceFilter
    implements BluetoothDeviceFilter.Filter
  {
    public boolean matches(BluetoothDevice paramBluetoothDevice)
    {
      return paramBluetoothDevice.getBondState() != 12;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\BluetoothDeviceFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */