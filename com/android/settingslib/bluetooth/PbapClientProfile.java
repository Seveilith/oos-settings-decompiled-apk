package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothPbapClient;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import com.android.settingslib.R.drawable;
import com.android.settingslib.R.string;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class PbapClientProfile
  implements LocalBluetoothProfile
{
  static final String NAME = "PbapClient";
  private static final int ORDINAL = 6;
  static final ParcelUuid[] SRC_UUIDS = { BluetoothUuid.PBAP_PSE };
  private static final String TAG = "PbapClientProfile";
  private static boolean V = false;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothPbapClient mService;
  
  PbapClientProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mLocalAdapter.getProfileProxy(paramContext, new PbapClientServiceListener(null), 17);
  }
  
  private void refreshProfiles()
  {
    Iterator localIterator = this.mDeviceManager.getCachedDevicesCopy().iterator();
    while (localIterator.hasNext()) {
      ((CachedBluetoothDevice)localIterator.next()).onUuidChanged();
    }
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    if (V) {
      Log.d("PbapClientProfile", "PBAPClientProfile got connect request");
    }
    if (this.mService == null) {
      return false;
    }
    Object localObject1 = getConnectedDevices();
    if (localObject1 != null)
    {
      Object localObject2 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject2).hasNext()) {
        if (((BluetoothDevice)((Iterator)localObject2).next()).equals(paramBluetoothDevice))
        {
          Log.d("PbapClientProfile", "Ignoring Connect");
          return true;
        }
      }
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (BluetoothDevice)((Iterator)localObject1).next();
        this.mService.disconnect(paramBluetoothDevice);
      }
    }
    Log.d("PbapClientProfile", "PBAPClientProfile attempting to connect to " + paramBluetoothDevice.getAddress());
    return this.mService.connect(paramBluetoothDevice);
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if (V) {
      Log.d("PbapClientProfile", "PBAPClientProfile got disconnect request");
    }
    if (this.mService == null) {
      return false;
    }
    return this.mService.disconnect(paramBluetoothDevice);
  }
  
  protected void finalize()
  {
    if (V) {
      Log.d("PbapClientProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(17, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("PbapClientProfile", "Error cleaning up PBAP Client proxy", localThrowable);
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
    return this.mService.getConnectionState(paramBluetoothDevice);
  }
  
  public int getDrawableResource(BluetoothClass paramBluetoothClass)
  {
    return R.drawable.ic_bt_cellphone;
  }
  
  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return R.string.bluetooth_profile_pbap;
  }
  
  public int getOrdinal()
  {
    return 6;
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
    return R.string.bluetooth_profile_pbap_summary;
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
  
  public boolean pbapClientExists()
  {
    return this.mService != null;
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
    return "PbapClient";
  }
  
  private final class PbapClientServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private PbapClientServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (PbapClientProfile.-get0()) {
        Log.d("PbapClientProfile", "Bluetooth service connected");
      }
      PbapClientProfile.-set1(PbapClientProfile.this, (BluetoothPbapClient)paramBluetoothProfile);
      List localList = PbapClientProfile.-get4(PbapClientProfile.this).getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = PbapClientProfile.-get1(PbapClientProfile.this).findDevice(localBluetoothDevice);
        paramBluetoothProfile = localCachedBluetoothDevice;
        if (localCachedBluetoothDevice == null)
        {
          Log.w("PbapClientProfile", "PbapClientProfile found new device: " + localBluetoothDevice);
          paramBluetoothProfile = PbapClientProfile.-get1(PbapClientProfile.this).addDevice(PbapClientProfile.-get2(PbapClientProfile.this), PbapClientProfile.-get3(PbapClientProfile.this), localBluetoothDevice);
        }
        paramBluetoothProfile.onProfileStateChanged(PbapClientProfile.this, 2);
        paramBluetoothProfile.refresh();
      }
      PbapClientProfile.-set0(PbapClientProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (PbapClientProfile.-get0()) {
        Log.d("PbapClientProfile", "Bluetooth service disconnected");
      }
      PbapClientProfile.-set0(PbapClientProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\PbapClientProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */