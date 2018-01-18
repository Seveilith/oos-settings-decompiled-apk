package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothSap;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import com.android.settingslib.R.drawable;
import com.android.settingslib.R.string;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class SapProfile
  implements LocalBluetoothProfile
{
  static final String NAME = "SAP";
  private static final int ORDINAL = 10;
  private static final String TAG = "SapProfile";
  static final ParcelUuid[] UUIDS = { BluetoothUuid.SAP };
  private static boolean V = true;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothSap mService;
  
  SapProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mLocalAdapter.getProfileProxy(paramContext, new SapServiceListener(null), 10);
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
      Log.d("SapProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(10, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("SapProfile", "Error cleaning up SAP proxy", localThrowable);
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
    return R.string.bluetooth_profile_sap;
  }
  
  public int getOrdinal()
  {
    return 10;
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
      return R.string.bluetooth_sap_profile_summary_use_for;
    }
    return R.string.bluetooth_sap_profile_summary_connected;
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
    return "SAP";
  }
  
  private final class SapServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private SapServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (SapProfile.-get0()) {
        Log.d("SapProfile", "Bluetooth service connected");
      }
      SapProfile.-set1(SapProfile.this, (BluetoothSap)paramBluetoothProfile);
      List localList = SapProfile.-get4(SapProfile.this).getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = SapProfile.-get1(SapProfile.this).findDevice(localBluetoothDevice);
        paramBluetoothProfile = localCachedBluetoothDevice;
        if (localCachedBluetoothDevice == null)
        {
          Log.w("SapProfile", "SapProfile found new device: " + localBluetoothDevice);
          paramBluetoothProfile = SapProfile.-get1(SapProfile.this).addDevice(SapProfile.-get2(SapProfile.this), SapProfile.-get3(SapProfile.this), localBluetoothDevice);
        }
        paramBluetoothProfile.onProfileStateChanged(SapProfile.this, 2);
        paramBluetoothProfile.refresh();
      }
      SapProfile.-get3(SapProfile.this).callServiceConnectedListeners();
      SapProfile.-set0(SapProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (SapProfile.-get0()) {
        Log.d("SapProfile", "Bluetooth service disconnected");
      }
      SapProfile.-get3(SapProfile.this).callServiceDisconnectedListeners();
      SapProfile.-set0(SapProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\SapProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */