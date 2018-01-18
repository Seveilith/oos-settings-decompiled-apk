package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.R.string;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class BluetoothEventManager
{
  private static final String TAG = "BluetoothEventManager";
  private final IntentFilter mAdapterIntentFilter;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      Object localObject = paramAnonymousIntent.getAction();
      BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
      localObject = (BluetoothEventManager.Handler)BluetoothEventManager.-get3(BluetoothEventManager.this).get(localObject);
      if (localObject != null) {
        ((BluetoothEventManager.Handler)localObject).onReceive(paramAnonymousContext, paramAnonymousIntent, localBluetoothDevice);
      }
    }
  };
  private final Collection<BluetoothCallback> mCallbacks = new ArrayList();
  private Context mContext;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private final Map<String, Handler> mHandlerMap;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final BroadcastReceiver mProfileBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      Object localObject = paramAnonymousIntent.getAction();
      BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
      localObject = (BluetoothEventManager.Handler)BluetoothEventManager.-get3(BluetoothEventManager.this).get(localObject);
      if (localObject != null) {
        ((BluetoothEventManager.Handler)localObject).onReceive(paramAnonymousContext, paramAnonymousIntent, localBluetoothDevice);
      }
    }
  };
  private final IntentFilter mProfileIntentFilter;
  private LocalBluetoothProfileManager mProfileManager;
  private Handler mReceiverHandler;
  
  BluetoothEventManager(LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, Context paramContext)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mAdapterIntentFilter = new IntentFilter();
    this.mProfileIntentFilter = new IntentFilter();
    this.mHandlerMap = new HashMap();
    this.mContext = paramContext;
    addHandler("android.bluetooth.adapter.action.STATE_CHANGED", new AdapterStateChangedHandler(null));
    addHandler("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED", new ConnectionStateChangedHandler(null));
    addHandler("android.bluetooth.adapter.action.DISCOVERY_STARTED", new ScanningStateChangedHandler(true));
    addHandler("android.bluetooth.adapter.action.DISCOVERY_FINISHED", new ScanningStateChangedHandler(false));
    addHandler("android.bluetooth.device.action.FOUND", new DeviceFoundHandler(null));
    addHandler("android.bluetooth.device.action.DISAPPEARED", new DeviceDisappearedHandler(null));
    addHandler("android.bluetooth.device.action.NAME_CHANGED", new NameChangedHandler(null));
    addHandler("android.bluetooth.device.action.ALIAS_CHANGED", new NameChangedHandler(null));
    addHandler("android.bluetooth.device.action.BOND_STATE_CHANGED", new BondStateChangedHandler(null));
    addHandler("android.bluetooth.device.action.PAIRING_CANCEL", new PairingCancelHandler(null));
    addHandler("android.bluetooth.device.action.CLASS_CHANGED", new ClassChangedHandler(null));
    addHandler("android.bluetooth.device.action.UUID", new UuidChangedHandler(null));
    addHandler("android.intent.action.DOCK_EVENT", new DockEventHandler(null));
    this.mContext.registerReceiver(this.mBroadcastReceiver, this.mAdapterIntentFilter, null, this.mReceiverHandler);
    this.mContext.registerReceiver(this.mProfileBroadcastReceiver, this.mProfileIntentFilter, null, this.mReceiverHandler);
  }
  
  private void addHandler(String paramString, Handler paramHandler)
  {
    this.mHandlerMap.put(paramString, paramHandler);
    this.mAdapterIntentFilter.addAction(paramString);
  }
  
  private void dispatchConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt)
  {
    synchronized (this.mCallbacks)
    {
      Iterator localIterator = this.mCallbacks.iterator();
      if (localIterator.hasNext()) {
        ((BluetoothCallback)localIterator.next()).onConnectionStateChanged(paramCachedBluetoothDevice, paramInt);
      }
    }
  }
  
  private void setDefaultBtName()
  {
    String str = this.mContext.getResources().getString(17039491);
    Log.d("BluetoothEventManager", "custom bluetooth name: " + str);
    if (!TextUtils.isEmpty(str)) {
      this.mLocalAdapter.setName(str);
    }
  }
  
  void addProfileHandler(String paramString, Handler paramHandler)
  {
    this.mHandlerMap.put(paramString, paramHandler);
    this.mProfileIntentFilter.addAction(paramString);
  }
  
  void dispatchDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    synchronized (this.mCallbacks)
    {
      Iterator localIterator = this.mCallbacks.iterator();
      if (localIterator.hasNext()) {
        ((BluetoothCallback)localIterator.next()).onDeviceAdded(paramCachedBluetoothDevice);
      }
    }
  }
  
  boolean readPairedDevices()
  {
    Object localObject = this.mLocalAdapter.getBondedDevices();
    if (localObject == null) {
      return false;
    }
    boolean bool = false;
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      BluetoothDevice localBluetoothDevice = (BluetoothDevice)((Iterator)localObject).next();
      if (this.mDeviceManager.findDevice(localBluetoothDevice) == null)
      {
        dispatchDeviceAdded(this.mDeviceManager.addDevice(this.mLocalAdapter, this.mProfileManager, localBluetoothDevice));
        bool = true;
      }
    }
    return bool;
  }
  
  public void registerCallback(BluetoothCallback paramBluetoothCallback)
  {
    synchronized (this.mCallbacks)
    {
      this.mCallbacks.add(paramBluetoothCallback);
      return;
    }
  }
  
  void registerProfileIntentReceiver()
  {
    this.mContext.registerReceiver(this.mProfileBroadcastReceiver, this.mProfileIntentFilter, null, this.mReceiverHandler);
  }
  
  void setProfileManager(LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mProfileManager = paramLocalBluetoothProfileManager;
  }
  
  public void setReceiverHandler(Handler paramHandler)
  {
    this.mContext.unregisterReceiver(this.mBroadcastReceiver);
    this.mContext.unregisterReceiver(this.mProfileBroadcastReceiver);
    this.mReceiverHandler = paramHandler;
    this.mContext.registerReceiver(this.mBroadcastReceiver, this.mAdapterIntentFilter, null, this.mReceiverHandler);
    registerProfileIntentReceiver();
  }
  
  public void unregisterCallback(BluetoothCallback paramBluetoothCallback)
  {
    synchronized (this.mCallbacks)
    {
      this.mCallbacks.remove(paramBluetoothCallback);
      return;
    }
  }
  
  private class AdapterStateChangedHandler
    implements BluetoothEventManager.Handler
  {
    private AdapterStateChangedHandler() {}
    
    public void onReceive(Context arg1, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      int i = paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
      if (i == 10)
      {
        ???.unregisterReceiver(BluetoothEventManager.-get5(BluetoothEventManager.this));
        BluetoothEventManager.this.registerProfileIntentReceiver();
      }
      BluetoothEventManager.-get4(BluetoothEventManager.this).setBluetoothStateInt(i);
      ??? = PreferenceManager.getDefaultSharedPreferences(BluetoothEventManager.-get1(BluetoothEventManager.this));
      boolean bool = ???.getBoolean("is_first_boot", true);
      Log.d("BluetoothEventManager", "isFirstBoot: " + bool + " state: " + i);
      if ((bool) && (i == 12))
      {
        BluetoothEventManager.-wrap1(BluetoothEventManager.this);
        ??? = ???.edit();
        ???.putBoolean("is_first_boot", false);
        ???.apply();
      }
      synchronized (BluetoothEventManager.-get0(BluetoothEventManager.this))
      {
        paramIntent = BluetoothEventManager.-get0(BluetoothEventManager.this).iterator();
        if (paramIntent.hasNext()) {
          ((BluetoothCallback)paramIntent.next()).onBluetoothStateChanged(i);
        }
      }
      BluetoothEventManager.-get2(BluetoothEventManager.this).onBluetoothStateChanged(i);
    }
  }
  
  private class BondStateChangedHandler
    implements BluetoothEventManager.Handler
  {
    private BondStateChangedHandler() {}
    
    private void showUnbondMessage(Context paramContext, String paramString, int paramInt)
    {
      switch (paramInt)
      {
      case 3: 
      default: 
        Log.w("BluetoothEventManager", "showUnbondMessage: Not displaying any message for reason: " + paramInt);
        return;
      case 1: 
        paramInt = R.string.bluetooth_pairing_pin_error_message;
      }
      for (;;)
      {
        Utils.showError(paramContext, paramString, paramInt);
        return;
        paramInt = R.string.bluetooth_pairing_rejected_error_message;
        continue;
        paramInt = R.string.bluetooth_pairing_device_down_error_message;
        continue;
        paramInt = R.string.bluetooth_pairing_error_message;
      }
    }
    
    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice arg3)
    {
      if (??? == null)
      {
        Log.e("BluetoothEventManager", "ACTION_BOND_STATE_CHANGED with no EXTRA_DEVICE");
        return;
      }
      int i = paramIntent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
      Object localObject2 = BluetoothEventManager.-get2(BluetoothEventManager.this).findDevice(???);
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        Log.w("BluetoothEventManager", "CachedBluetoothDevice for device " + ??? + " not found, calling readPairedDevices().");
        if (!BluetoothEventManager.this.readPairedDevices())
        {
          Log.e("BluetoothEventManager", "Got bonding state changed for " + ??? + ", but we have no record of that device.");
          return;
        }
        localObject2 = BluetoothEventManager.-get2(BluetoothEventManager.this).findDevice(???);
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          Log.e("BluetoothEventManager", "Got bonding state changed for " + ??? + ", but device not added in cache.");
          return;
        }
      }
      synchronized (BluetoothEventManager.-get0(BluetoothEventManager.this))
      {
        localObject2 = BluetoothEventManager.-get0(BluetoothEventManager.this).iterator();
        if (((Iterator)localObject2).hasNext()) {
          ((BluetoothCallback)((Iterator)localObject2).next()).onDeviceBondStateChanged((CachedBluetoothDevice)localObject1, i);
        }
      }
      ((CachedBluetoothDevice)localObject1).onBondingStateChanged(i);
      if (i == 10)
      {
        i = paramIntent.getIntExtra("android.bluetooth.device.extra.REASON", Integer.MIN_VALUE);
        showUnbondMessage(paramContext, ((CachedBluetoothDevice)localObject1).getName(), i);
      }
    }
  }
  
  private class ClassChangedHandler
    implements BluetoothEventManager.Handler
  {
    private ClassChangedHandler() {}
    
    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      BluetoothEventManager.-get2(BluetoothEventManager.this).onBtClassChanged(paramBluetoothDevice);
    }
  }
  
  private class ConnectionStateChangedHandler
    implements BluetoothEventManager.Handler
  {
    private ConnectionStateChangedHandler() {}
    
    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      paramContext = BluetoothEventManager.-get2(BluetoothEventManager.this).findDevice(paramBluetoothDevice);
      int i = paramIntent.getIntExtra("android.bluetooth.adapter.extra.CONNECTION_STATE", Integer.MIN_VALUE);
      BluetoothEventManager.-wrap0(BluetoothEventManager.this, paramContext, i);
    }
  }
  
  private class DeviceDisappearedHandler
    implements BluetoothEventManager.Handler
  {
    private DeviceDisappearedHandler() {}
    
    public void onReceive(Context paramContext, Intent arg2, BluetoothDevice paramBluetoothDevice)
    {
      paramContext = BluetoothEventManager.-get2(BluetoothEventManager.this).findDevice(paramBluetoothDevice);
      if (paramContext == null)
      {
        Log.w("BluetoothEventManager", "received ACTION_DISAPPEARED for an unknown device: " + paramBluetoothDevice);
        return;
      }
      if (CachedBluetoothDeviceManager.onDeviceDisappeared(paramContext)) {
        synchronized (BluetoothEventManager.-get0(BluetoothEventManager.this))
        {
          paramBluetoothDevice = BluetoothEventManager.-get0(BluetoothEventManager.this).iterator();
          if (paramBluetoothDevice.hasNext()) {
            ((BluetoothCallback)paramBluetoothDevice.next()).onDeviceDeleted(paramContext);
          }
        }
      }
    }
  }
  
  private class DeviceFoundHandler
    implements BluetoothEventManager.Handler
  {
    private DeviceFoundHandler() {}
    
    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      short s = paramIntent.getShortExtra("android.bluetooth.device.extra.RSSI", (short)Short.MIN_VALUE);
      BluetoothClass localBluetoothClass = (BluetoothClass)paramIntent.getParcelableExtra("android.bluetooth.device.extra.CLASS");
      String str = paramIntent.getStringExtra("android.bluetooth.device.extra.NAME");
      paramIntent = BluetoothEventManager.-get2(BluetoothEventManager.this).findDevice(paramBluetoothDevice);
      paramContext = paramIntent;
      if (paramIntent == null)
      {
        paramContext = BluetoothEventManager.-get2(BluetoothEventManager.this).addDevice(BluetoothEventManager.-get4(BluetoothEventManager.this), BluetoothEventManager.-get6(BluetoothEventManager.this), paramBluetoothDevice);
        Log.d("BluetoothEventManager", "DeviceFoundHandler created new CachedBluetoothDevice: " + paramContext);
        BluetoothEventManager.this.dispatchDeviceAdded(paramContext);
      }
      paramContext.setRssi(s);
      paramContext.setBtClass(localBluetoothClass);
      paramContext.setNewName(str);
      paramContext.setVisible(true);
    }
  }
  
  private class DockEventHandler
    implements BluetoothEventManager.Handler
  {
    private DockEventHandler() {}
    
    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      if ((paramIntent.getIntExtra("android.intent.extra.DOCK_STATE", 1) == 0) && (paramBluetoothDevice != null) && (paramBluetoothDevice.getBondState() == 10))
      {
        paramContext = BluetoothEventManager.-get2(BluetoothEventManager.this).findDevice(paramBluetoothDevice);
        if (paramContext != null) {
          paramContext.setVisible(false);
        }
      }
    }
  }
  
  static abstract interface Handler
  {
    public abstract void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice);
  }
  
  private class NameChangedHandler
    implements BluetoothEventManager.Handler
  {
    private NameChangedHandler() {}
    
    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      BluetoothEventManager.-get2(BluetoothEventManager.this).onDeviceNameUpdated(paramBluetoothDevice);
    }
  }
  
  private class PairingCancelHandler
    implements BluetoothEventManager.Handler
  {
    private PairingCancelHandler() {}
    
    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      if (paramBluetoothDevice == null)
      {
        Log.e("BluetoothEventManager", "ACTION_PAIRING_CANCEL with no EXTRA_DEVICE");
        return;
      }
      paramIntent = BluetoothEventManager.-get2(BluetoothEventManager.this).findDevice(paramBluetoothDevice);
      if (paramIntent == null)
      {
        Log.e("BluetoothEventManager", "ACTION_PAIRING_CANCEL with no cached device");
        return;
      }
      int i = R.string.bluetooth_pairing_error_message;
      if ((paramContext != null) && (paramIntent != null)) {
        Utils.showError(paramContext, paramIntent.getName(), i);
      }
    }
  }
  
  private class ScanningStateChangedHandler
    implements BluetoothEventManager.Handler
  {
    private final boolean mStarted;
    
    ScanningStateChangedHandler(boolean paramBoolean)
    {
      this.mStarted = paramBoolean;
    }
    
    public void onReceive(Context arg1, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      synchronized (BluetoothEventManager.-get0(BluetoothEventManager.this))
      {
        paramIntent = BluetoothEventManager.-get0(BluetoothEventManager.this).iterator();
        if (paramIntent.hasNext()) {
          ((BluetoothCallback)paramIntent.next()).onScanningStateChanged(this.mStarted);
        }
      }
      BluetoothEventManager.-get2(BluetoothEventManager.this).onScanningStateChanged(this.mStarted);
    }
  }
  
  private class UuidChangedHandler
    implements BluetoothEventManager.Handler
  {
    private UuidChangedHandler() {}
    
    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      BluetoothEventManager.-get2(BluetoothEventManager.this).onUuidChanged(paramBluetoothDevice);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\BluetoothEventManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */