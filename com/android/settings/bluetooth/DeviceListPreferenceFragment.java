package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.util.Log;
import android.view.View;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter.Filter;
import com.android.settingslib.bluetooth.BluetoothEventManager;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.drawer.SettingsDrawerActivity;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class DeviceListPreferenceFragment
  extends RestrictedSettingsFragment
  implements BluetoothCallback
{
  private static final String KEY_BT_DEVICE_LIST = "bt_device_list";
  private static final String KEY_BT_SCAN = "bt_scan";
  private static final String TAG = "DeviceListPreferenceFragment";
  private final int REFRESH_MSG = 1;
  private boolean isDrawring = false;
  boolean isFirstAdd = true;
  Handler mAddDeviceHandler;
  HandlerThread mAddDeviceThread;
  private PreferenceGroup mDeviceListGroup;
  final WeakHashMap<CachedBluetoothDevice, BluetoothDevicePreference> mDevicePreferenceMap = new WeakHashMap();
  private BluetoothDeviceFilter.Filter mFilter = BluetoothDeviceFilter.ALL_FILTER;
  Handler mHandler = new Handler();
  private Field mListener = null;
  private Object mListenerObject = null;
  LocalBluetoothAdapter mLocalAdapter;
  LocalBluetoothManager mLocalManager;
  private Method mNotifyHierarchyChanged = null;
  List<BluetoothDevicePreference> mPreferenceList = new ArrayList();
  Runnable mRunnable = new Runnable()
  {
    public void run()
    {
      if ((DeviceListPreferenceFragment.this.mPreferenceList != null) && (DeviceListPreferenceFragment.this.mPreferenceList.size() > 0))
      {
        ArrayList localArrayList = new ArrayList();
        localArrayList.addAll(DeviceListPreferenceFragment.this.mPreferenceList);
        Message localMessage = DeviceListPreferenceFragment.this.mAddDeviceHandler.obtainMessage();
        localMessage.what = 1;
        localMessage.obj = localArrayList;
        DeviceListPreferenceFragment.this.mAddDeviceHandler.sendMessage(localMessage);
      }
      DeviceListPreferenceFragment.this.mPreferenceList.clear();
    }
  };
  BluetoothDevice mSelectedDevice;
  
  DeviceListPreferenceFragment(String paramString)
  {
    super(paramString);
  }
  
  private void initPreferenceListener()
  {
    try
    {
      Class localClass = PreferenceGroup.class.getSuperclass();
      this.mNotifyHierarchyChanged = localClass.getDeclaredMethod("notifyHierarchyChanged", new Class[0]);
      this.mNotifyHierarchyChanged.setAccessible(true);
      this.mListener = localClass.getDeclaredField("mListener");
      this.mListener.setAccessible(true);
      this.mListenerObject = this.mListener.get(this.mDeviceListGroup);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void resetPreferenceListener()
  {
    if ((this.mListener != null) && (this.mListenerObject != null)) {}
    try
    {
      this.mListener.set(this.mDeviceListGroup, this.mListenerObject);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void setDrawerListener()
  {
    Activity localActivity = getActivity();
    if (!(localActivity instanceof SettingsDrawerActivity)) {
      return;
    }
    ((SettingsDrawerActivity)localActivity).setDrawerListener(new DrawerLayout.DrawerListener()
    {
      public void onDrawerClosed(View paramAnonymousView) {}
      
      public void onDrawerOpened(View paramAnonymousView) {}
      
      public void onDrawerSlide(View paramAnonymousView, float paramAnonymousFloat) {}
      
      public void onDrawerStateChanged(int paramAnonymousInt)
      {
        if (paramAnonymousInt == 0)
        {
          DeviceListPreferenceFragment.-set0(DeviceListPreferenceFragment.this, false);
          DeviceListPreferenceFragment.-wrap0(DeviceListPreferenceFragment.this);
        }
        while (DeviceListPreferenceFragment.-get0(DeviceListPreferenceFragment.this)) {
          try
          {
            if (DeviceListPreferenceFragment.-get2(DeviceListPreferenceFragment.this) != null)
            {
              PreferenceGroup localPreferenceGroup = DeviceListPreferenceFragment.-get1(DeviceListPreferenceFragment.this);
              DeviceListPreferenceFragment.-get2(DeviceListPreferenceFragment.this).invoke(localPreferenceGroup, new Object[0]);
            }
            return;
          }
          catch (Exception localException)
          {
            localException.printStackTrace();
            return;
          }
        }
        DeviceListPreferenceFragment.-set0(DeviceListPreferenceFragment.this, true);
        DeviceListPreferenceFragment.-wrap1(DeviceListPreferenceFragment.this);
      }
    });
  }
  
  private void setPreferenceListenerNull()
  {
    if (this.mListener == null) {
      initPreferenceListener();
    }
    if (this.mListener != null) {}
    try
    {
      this.mListener.set(this.mDeviceListGroup, null);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void updateProgressUi(boolean paramBoolean)
  {
    if ((this.mDeviceListGroup instanceof BluetoothProgressCategory)) {
      ((BluetoothProgressCategory)this.mDeviceListGroup).setProgress(paramBoolean);
    }
  }
  
  void addCachedDevices()
  {
    Object localObject = this.mLocalManager.getCachedDeviceManager().getCachedDevicesCopy();
    Log.w("DeviceListPreferenceFragment", "oneplus addCachedDevices cachedDevice size = " + ((Collection)localObject).size());
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      onDeviceAddedNormal((CachedBluetoothDevice)((Iterator)localObject).next());
    }
  }
  
  abstract void addPreferencesForActivity();
  
  protected void cancelRunnable()
  {
    Log.w("DeviceListPreferenceFragment", "cancelRunnable !!");
    this.mHandler.removeCallbacks(this.mRunnable);
    this.mPreferenceList.clear();
    this.isFirstAdd = true;
  }
  
  void createDevicePreference(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    if (this.mDeviceListGroup == null)
    {
      Log.w("DeviceListPreferenceFragment", "Trying to create a device preference before the list group/category exists!");
      return;
    }
    final String str = paramCachedBluetoothDevice.getDevice().getAddress();
    final BluetoothDevicePreference localBluetoothDevicePreference = (BluetoothDevicePreference)getCachedPreference(str);
    if (localBluetoothDevicePreference == null)
    {
      localBluetoothDevicePreference = new BluetoothDevicePreference(getPrefContext(), paramCachedBluetoothDevice);
      localBluetoothDevicePreference.setKey(str);
      Log.w("DeviceListPreferenceFragment", "createDevicePreference cachedDevice = " + paramCachedBluetoothDevice);
      this.mDeviceListGroup.getPreferenceCount();
      if (this.isFirstAdd)
      {
        this.isFirstAdd = false;
        this.mAddDeviceHandler.post(new Runnable()
        {
          public void run()
          {
            DeviceListPreferenceFragment.this.filterBondedDevice(str, localBluetoothDevicePreference);
          }
        });
      }
    }
    for (;;)
    {
      initDevicePreference(localBluetoothDevicePreference);
      this.mDevicePreferenceMap.put(paramCachedBluetoothDevice, localBluetoothDevicePreference);
      return;
      if (this.mPreferenceList.isEmpty()) {
        this.mHandler.postDelayed(this.mRunnable, 1000L);
      }
      filterBondedDevice(str, localBluetoothDevicePreference);
      continue;
      Log.w("DeviceListPreferenceFragment", "createDevicePreference preference.rebind() ");
      this.mDeviceListGroup.addPreference(localBluetoothDevicePreference);
    }
  }
  
  void createDevicePreferenceNormal(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    if (this.mDeviceListGroup == null)
    {
      Log.w("DeviceListPreferenceFragment", "Trying to create a device preference before the list group/category exists!");
      return;
    }
    String str = paramCachedBluetoothDevice.getDevice().getAddress();
    BluetoothDevicePreference localBluetoothDevicePreference = (BluetoothDevicePreference)getCachedPreference(str);
    if (localBluetoothDevicePreference == null)
    {
      localBluetoothDevicePreference = new BluetoothDevicePreference(getPrefContext(), paramCachedBluetoothDevice);
      localBluetoothDevicePreference.setKey(str);
      Log.w("DeviceListPreferenceFragment", "createDevicePreferenceNormal cachedDevice = " + paramCachedBluetoothDevice);
      this.mDeviceListGroup.addPreference(localBluetoothDevicePreference);
    }
    for (;;)
    {
      initDevicePreference(localBluetoothDevicePreference);
      this.mDevicePreferenceMap.put(paramCachedBluetoothDevice, localBluetoothDevicePreference);
      return;
      Log.w("DeviceListPreferenceFragment", "createDevicePreferenceNormal preference.rebind() ");
      this.mDeviceListGroup.addPreference(localBluetoothDevicePreference);
    }
  }
  
  void filterBondedDevice(String paramString, BluetoothDevicePreference paramBluetoothDevicePreference)
  {
    Object localObject = this.mLocalAdapter.getBondedDevices();
    if (((Set)localObject).size() > 0)
    {
      localObject = ((Set)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)((Iterator)localObject).next();
        Log.w("DeviceListPreferenceFragment", "mAddDeviceHandler device.getAddress() = " + localBluetoothDevice.getAddress() + "  key = " + paramString);
        if (!localBluetoothDevice.getAddress().equals(paramString))
        {
          Log.w("DeviceListPreferenceFragment", "has bonded device addPreference key = " + paramString);
          this.mDeviceListGroup.addPreference(paramBluetoothDevicePreference);
        }
      }
    }
    Log.w("DeviceListPreferenceFragment", "no bonded device addPreference key = " + paramString);
    this.mDeviceListGroup.addPreference(paramBluetoothDevicePreference);
  }
  
  void initDevicePreference(BluetoothDevicePreference paramBluetoothDevicePreference) {}
  
  public void onBluetoothStateChanged(int paramInt)
  {
    if (paramInt == 10) {
      updateProgressUi(false);
    }
  }
  
  public void onConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt) {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mLocalManager = Utils.getLocalBtManager(getActivity());
    if (this.mLocalManager == null)
    {
      Log.e("DeviceListPreferenceFragment", "Bluetooth is not supported on this device");
      return;
    }
    this.mLocalAdapter = this.mLocalManager.getBluetoothAdapter();
    addPreferencesForActivity();
    this.mDeviceListGroup = ((PreferenceCategory)findPreference("bt_device_list"));
    this.mAddDeviceThread = new HandlerThread("add bluetooth");
    this.mAddDeviceThread.start();
    this.mAddDeviceHandler = new Handler(this.mAddDeviceThread.getLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        if (paramAnonymousMessage.what == 1)
        {
          DeviceListPreferenceFragment.-wrap1(DeviceListPreferenceFragment.this);
          paramAnonymousMessage = ((ArrayList)paramAnonymousMessage.obj).iterator();
        }
        for (;;)
        {
          BluetoothDevicePreference localBluetoothDevicePreference;
          if (paramAnonymousMessage.hasNext())
          {
            localBluetoothDevicePreference = (BluetoothDevicePreference)paramAnonymousMessage.next();
            if ((!paramAnonymousMessage.hasNext()) && (!DeviceListPreferenceFragment.-get0(DeviceListPreferenceFragment.this))) {
              DeviceListPreferenceFragment.-wrap0(DeviceListPreferenceFragment.this);
            }
            if (DeviceListPreferenceFragment.this.mLocalAdapter.getBluetoothState() == 12) {}
          }
          else
          {
            return;
          }
          DeviceListPreferenceFragment.this.filterBondedDevice(localBluetoothDevicePreference.getCachedDevice().getDevice().getAddress(), localBluetoothDevicePreference);
        }
      }
    };
    setDrawerListener();
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mHandler.removeCallbacks(this.mRunnable);
    this.mAddDeviceThread.quit();
  }
  
  public void onDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    if (this.mDevicePreferenceMap.get(paramCachedBluetoothDevice) != null) {
      return;
    }
    if (this.mLocalAdapter.getBluetoothState() != 12) {
      return;
    }
    if (this.mFilter.matches(paramCachedBluetoothDevice.getDevice())) {
      createDevicePreference(paramCachedBluetoothDevice);
    }
  }
  
  public void onDeviceAddedNormal(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    Log.w("DeviceListPreferenceFragment", "oneplus bluetooth onDeviceAddedNormal");
    if (this.mDevicePreferenceMap.get(paramCachedBluetoothDevice) != null) {
      return;
    }
    Log.w("DeviceListPreferenceFragment", "oneplus bluetooth onDeviceAddedNormal is not null");
    if (this.mLocalAdapter.getBluetoothState() != 12) {
      return;
    }
    Log.d("DeviceListPreferenceFragment", "oneplus mDevicePreferenceMap.size() = " + this.mDevicePreferenceMap.size());
    boolean bool = this.mFilter.matches(paramCachedBluetoothDevice.getDevice());
    Log.w("DeviceListPreferenceFragment", "oneplus needCreateDevicePreferenceNormal = " + bool);
    if (bool) {
      createDevicePreferenceNormal(paramCachedBluetoothDevice);
    }
  }
  
  public void onDeviceDeleted(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    paramCachedBluetoothDevice = (BluetoothDevicePreference)this.mDevicePreferenceMap.remove(paramCachedBluetoothDevice);
    if (paramCachedBluetoothDevice != null) {
      this.mDeviceListGroup.removePreference(paramCachedBluetoothDevice);
    }
  }
  
  void onDevicePreferenceClick(BluetoothDevicePreference paramBluetoothDevicePreference)
  {
    paramBluetoothDevicePreference.onClicked();
  }
  
  public void onPause()
  {
    super.onPause();
    if ((this.mLocalManager == null) || (isUiRestricted())) {
      return;
    }
    removeAllDevices();
    this.mLocalManager.setForegroundActivity(null);
    this.mLocalManager.getEventManager().unregisterCallback(this);
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if ("bt_scan".equals(paramPreference.getKey()))
    {
      this.mLocalAdapter.startScanning(true);
      return true;
    }
    if ((paramPreference instanceof BluetoothDevicePreference))
    {
      paramPreference = (BluetoothDevicePreference)paramPreference;
      this.mSelectedDevice = paramPreference.getCachedDevice().getDevice();
      onDevicePreferenceClick(paramPreference);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onResume()
  {
    super.onResume();
    if ((this.mLocalManager == null) || (isUiRestricted())) {
      return;
    }
    this.mLocalManager.setForegroundActivity(getActivity());
    this.mLocalManager.getEventManager().registerCallback(this);
    updateProgressUi(this.mLocalAdapter.isDiscovering());
  }
  
  public void onScanningStateChanged(boolean paramBoolean)
  {
    updateProgressUi(paramBoolean);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
  }
  
  void removeAllDevices()
  {
    this.mLocalAdapter.stopScanning();
    this.mDevicePreferenceMap.clear();
    this.mDeviceListGroup.removeAll();
  }
  
  void setDeviceListGroup(PreferenceGroup paramPreferenceGroup)
  {
    this.mDeviceListGroup = paramPreferenceGroup;
  }
  
  final void setFilter(int paramInt)
  {
    this.mFilter = BluetoothDeviceFilter.getFilter(paramInt);
  }
  
  final void setFilter(BluetoothDeviceFilter.Filter paramFilter)
  {
    this.mFilter = paramFilter;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\DeviceListPreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */