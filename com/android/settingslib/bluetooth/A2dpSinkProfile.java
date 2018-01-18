package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
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

final class A2dpSinkProfile
  implements LocalBluetoothProfile
{
  static final String NAME = "A2DPSink";
  private static final int ORDINAL = 5;
  static final ParcelUuid[] SRC_UUIDS = { BluetoothUuid.AudioSource, BluetoothUuid.AdvAudioDist };
  private static final String TAG = "A2dpSinkProfile";
  private static boolean V = true;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothA2dpSink mService;
  
  A2dpSinkProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mLocalAdapter.getProfileProxy(paramContext, new A2dpSinkServiceListener(null), 11);
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
          Log.d("A2dpSinkProfile", "Ignoring Connect");
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
      Log.d("A2dpSinkProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(11, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("A2dpSinkProfile", "Error cleaning up A2DP proxy", localThrowable);
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
    return R.drawable.ic_bt_headphones_a2dp;
  }
  
  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return R.string.bluetooth_profile_a2dp;
  }
  
  public int getOrdinal()
  {
    return 5;
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
      return R.string.bluetooth_a2dp_profile_summary_use_for;
    }
    return R.string.bluetooth_a2dp_profile_summary_connected;
  }
  
  boolean isA2dpPlaying()
  {
    if (this.mService == null) {
      return false;
    }
    List localList = this.mService.getConnectedDevices();
    return (!localList.isEmpty()) && (this.mService.isA2dpPlaying((BluetoothDevice)localList.get(0)));
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
    return "A2DPSink";
  }
  
  private final class A2dpSinkServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private A2dpSinkServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (A2dpSinkProfile.-get0()) {
        Log.d("A2dpSinkProfile", "Bluetooth service connected");
      }
      A2dpSinkProfile.-set1(A2dpSinkProfile.this, (BluetoothA2dpSink)paramBluetoothProfile);
      List localList = A2dpSinkProfile.-get4(A2dpSinkProfile.this).getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = A2dpSinkProfile.-get1(A2dpSinkProfile.this).findDevice(localBluetoothDevice);
        paramBluetoothProfile = localCachedBluetoothDevice;
        if (localCachedBluetoothDevice == null)
        {
          Log.w("A2dpSinkProfile", "A2dpSinkProfile found new device: " + localBluetoothDevice);
          paramBluetoothProfile = A2dpSinkProfile.-get1(A2dpSinkProfile.this).addDevice(A2dpSinkProfile.-get2(A2dpSinkProfile.this), A2dpSinkProfile.-get3(A2dpSinkProfile.this), localBluetoothDevice);
        }
        paramBluetoothProfile.onProfileStateChanged(A2dpSinkProfile.this, 2);
        paramBluetoothProfile.refresh();
      }
      A2dpSinkProfile.-set0(A2dpSinkProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (A2dpSinkProfile.-get0()) {
        Log.d("A2dpSinkProfile", "Bluetooth service disconnected");
      }
      A2dpSinkProfile.-set0(A2dpSinkProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\A2dpSinkProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */