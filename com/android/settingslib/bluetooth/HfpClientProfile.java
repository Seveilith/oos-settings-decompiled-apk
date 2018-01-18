package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
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

final class HfpClientProfile
  implements LocalBluetoothProfile
{
  static final String NAME = "HEADSET_CLIENT";
  private static final int ORDINAL = 0;
  static final ParcelUuid[] SRC_UUIDS = { BluetoothUuid.HSP_AG, BluetoothUuid.Handsfree_AG };
  private static final String TAG = "HfpClientProfile";
  private static boolean V = false;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothHeadsetClient mService;
  
  HfpClientProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mLocalAdapter.getProfileProxy(paramContext, new HfpClientServiceListener(null), 16);
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
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
          Log.d("HfpClientProfile", "Ignoring Connect");
          return true;
        }
      }
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (BluetoothDevice)((Iterator)localObject1).next();
        this.mService.disconnect((BluetoothDevice)localObject2);
      }
    }
    return this.mService.connect(paramBluetoothDevice);
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return false;
    }
    if (this.mService.getPriority(paramBluetoothDevice) > 100) {
      this.mService.setPriority(paramBluetoothDevice, 100);
    }
    return this.mService.disconnect(paramBluetoothDevice);
  }
  
  protected void finalize()
  {
    if (V) {
      Log.d("HfpClientProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(16, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("HfpClientProfile", "Error cleaning up HfpClient proxy", localThrowable);
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
    return R.drawable.ic_bt_headset_hfp;
  }
  
  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return R.string.bluetooth_profile_headset;
  }
  
  public int getOrdinal()
  {
    return 0;
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
      return R.string.bluetooth_headset_profile_summary_use_for;
    }
    return R.string.bluetooth_headset_profile_summary_connected;
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
    return "HEADSET_CLIENT";
  }
  
  private final class HfpClientServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private HfpClientServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (HfpClientProfile.-get0()) {
        Log.d("HfpClientProfile", "Bluetooth service connected");
      }
      HfpClientProfile.-set1(HfpClientProfile.this, (BluetoothHeadsetClient)paramBluetoothProfile);
      List localList = HfpClientProfile.-get4(HfpClientProfile.this).getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = HfpClientProfile.-get1(HfpClientProfile.this).findDevice(localBluetoothDevice);
        paramBluetoothProfile = localCachedBluetoothDevice;
        if (localCachedBluetoothDevice == null)
        {
          Log.w("HfpClientProfile", "HfpClient profile found new device: " + localBluetoothDevice);
          paramBluetoothProfile = HfpClientProfile.-get1(HfpClientProfile.this).addDevice(HfpClientProfile.-get2(HfpClientProfile.this), HfpClientProfile.-get3(HfpClientProfile.this), localBluetoothDevice);
        }
        paramBluetoothProfile.onProfileStateChanged(HfpClientProfile.this, 2);
        paramBluetoothProfile.refresh();
      }
      HfpClientProfile.-set0(HfpClientProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (HfpClientProfile.-get0()) {
        Log.d("HfpClientProfile", "Bluetooth service disconnected");
      }
      HfpClientProfile.-set0(HfpClientProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\HfpClientProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */