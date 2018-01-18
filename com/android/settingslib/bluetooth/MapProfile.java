package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothMap;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import com.android.settingslib.R.drawable;
import com.android.settingslib.R.string;
import java.util.ArrayList;
import java.util.List;

public final class MapProfile
  implements LocalBluetoothProfile
{
  static final String NAME = "MAP";
  private static final String TAG = "MapProfile";
  static final ParcelUuid[] UUIDS = { BluetoothUuid.MAP, BluetoothUuid.MNS, BluetoothUuid.MAS };
  private static boolean V = true;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothMap mService;
  
  MapProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mLocalAdapter.getProfileProxy(paramContext, new MapServiceListener(null), 9);
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    if (V) {
      Log.d("MapProfile", "connect() - should not get called");
    }
    return false;
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return false;
    }
    List localList = this.mService.getConnectedDevices();
    if ((!localList.isEmpty()) && (((BluetoothDevice)localList.get(0)).equals(paramBluetoothDevice)))
    {
      if (this.mService.getPriority(paramBluetoothDevice) > 100) {
        this.mService.setPriority(paramBluetoothDevice, 100);
      }
      return this.mService.disconnect(paramBluetoothDevice);
    }
    return false;
  }
  
  protected void finalize()
  {
    if (V) {
      Log.d("MapProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(9, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("MapProfile", "Error cleaning up MAP proxy", localThrowable);
    }
  }
  
  public List<BluetoothDevice> getConnectedDevices()
  {
    if (this.mService == null) {
      return new ArrayList(0);
    }
    return this.mService.getDevicesMatchingConnectionStates(new int[] { 2, 1, 3 });
  }
  
  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return 0;
    }
    List localList = this.mService.getConnectedDevices();
    if (V) {
      Log.d("MapProfile", "getConnectionStatus: status is: " + this.mService.getConnectionState(paramBluetoothDevice));
    }
    if ((!localList.isEmpty()) && (((BluetoothDevice)localList.get(0)).equals(paramBluetoothDevice))) {
      return this.mService.getConnectionState(paramBluetoothDevice);
    }
    return 0;
  }
  
  public int getDrawableResource(BluetoothClass paramBluetoothClass)
  {
    return R.drawable.ic_bt_cellphone;
  }
  
  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return R.string.bluetooth_profile_map;
  }
  
  public int getOrdinal()
  {
    return 9;
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
      return R.string.bluetooth_map_profile_summary_use_for;
    }
    return R.string.bluetooth_map_profile_summary_connected;
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
    if (V) {
      Log.d("MapProfile", "isProfileReady(): " + this.mIsProfileReady);
    }
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
    return "MAP";
  }
  
  private final class MapServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private MapServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (MapProfile.-get0()) {
        Log.d("MapProfile", "Bluetooth service connected");
      }
      MapProfile.-set1(MapProfile.this, (BluetoothMap)paramBluetoothProfile);
      List localList = MapProfile.-get4(MapProfile.this).getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = MapProfile.-get1(MapProfile.this).findDevice(localBluetoothDevice);
        paramBluetoothProfile = localCachedBluetoothDevice;
        if (localCachedBluetoothDevice == null)
        {
          Log.w("MapProfile", "MapProfile found new device: " + localBluetoothDevice);
          paramBluetoothProfile = MapProfile.-get1(MapProfile.this).addDevice(MapProfile.-get2(MapProfile.this), MapProfile.-get3(MapProfile.this), localBluetoothDevice);
        }
        paramBluetoothProfile.onProfileStateChanged(MapProfile.this, 2);
        paramBluetoothProfile.refresh();
      }
      MapProfile.-get3(MapProfile.this).callServiceConnectedListeners();
      MapProfile.-set0(MapProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (MapProfile.-get0()) {
        Log.d("MapProfile", "Bluetooth service disconnected");
      }
      MapProfile.-get3(MapProfile.this).callServiceDisconnectedListeners();
      MapProfile.-set0(MapProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\MapProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */