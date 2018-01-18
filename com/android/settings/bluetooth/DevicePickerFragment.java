package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;

public final class DevicePickerFragment
  extends DeviceListPreferenceFragment
{
  private static final int MENU_ID_REFRESH = 1;
  private boolean mDeviceSelected;
  private String mLaunchClass;
  private String mLaunchPackage;
  private boolean mNeedAuth;
  private boolean mStartScanOnResume;
  
  public DevicePickerFragment()
  {
    super(null);
  }
  
  private void sendDevicePickedIntent(BluetoothDevice paramBluetoothDevice)
  {
    this.mDeviceSelected = true;
    Intent localIntent = new Intent("android.bluetooth.devicepicker.action.DEVICE_SELECTED");
    localIntent.putExtra("android.bluetooth.device.extra.DEVICE", paramBluetoothDevice);
    if ((this.mLaunchPackage != null) && (this.mLaunchClass != null)) {
      localIntent.setClassName(this.mLaunchPackage, this.mLaunchClass);
    }
    getActivity().sendBroadcast(localIntent);
  }
  
  void addPreferencesForActivity()
  {
    addPreferencesFromResource(2131230763);
    Intent localIntent = getActivity().getIntent();
    this.mNeedAuth = localIntent.getBooleanExtra("android.bluetooth.devicepicker.extra.NEED_AUTH", false);
    setFilter(localIntent.getIntExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 0));
    this.mLaunchPackage = localIntent.getStringExtra("android.bluetooth.devicepicker.extra.LAUNCH_PACKAGE");
    this.mLaunchClass = localIntent.getStringExtra("android.bluetooth.devicepicker.extra.DEVICE_PICKER_LAUNCH_CLASS");
  }
  
  protected int getMetricsCategory()
  {
    return 25;
  }
  
  void initDevicePreference(BluetoothDevicePreference paramBluetoothDevicePreference)
  {
    paramBluetoothDevicePreference.setWidgetLayoutResource(2130968904);
  }
  
  public void onBluetoothStateChanged(int paramInt)
  {
    super.onBluetoothStateChanged(paramInt);
    if (paramInt == 12) {
      this.mLocalAdapter.startScanning(false);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    boolean bool2 = false;
    super.onCreate(paramBundle);
    getActivity().setTitle(getString(2131690874));
    boolean bool1 = bool2;
    if (!((UserManager)getSystemService("user")).hasUserRestriction("no_config_bluetooth"))
    {
      bool1 = bool2;
      if (paramBundle == null) {
        bool1 = true;
      }
    }
    this.mStartScanOnResume = bool1;
    setHasOptionsMenu(true);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 1, 0, 2131691253).setEnabled(true).setShowAsAction(0);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (!this.mDeviceSelected)
    {
      Intent localIntent = new Intent("org.codeaurora.bluetooth.devicepicker.action.DEVICE_NOT_SELECTED");
      if ((this.mLaunchPackage != null) && (this.mLaunchClass != null)) {
        localIntent.setClassName(this.mLaunchPackage, this.mLaunchClass);
      }
      getActivity().sendBroadcast(localIntent);
    }
  }
  
  public void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt)
  {
    if (paramInt == 12)
    {
      paramCachedBluetoothDevice = paramCachedBluetoothDevice.getDevice();
      if (paramCachedBluetoothDevice.equals(this.mSelectedDevice))
      {
        sendDevicePickedIntent(paramCachedBluetoothDevice);
        finish();
      }
    }
  }
  
  void onDevicePreferenceClick(BluetoothDevicePreference paramBluetoothDevicePreference)
  {
    this.mLocalAdapter.stopScanning();
    LocalBluetoothPreferences.persistSelectedDeviceInPicker(getActivity(), this.mSelectedDevice.getAddress());
    if ((paramBluetoothDevicePreference.getCachedDevice().getBondState() != 12) && (this.mNeedAuth))
    {
      super.onDevicePreferenceClick(paramBluetoothDevicePreference);
      return;
    }
    sendDevicePickedIntent(this.mSelectedDevice);
    finish();
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    }
    this.mLocalAdapter.startScanning(true);
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    addCachedDevices();
    this.mDeviceSelected = false;
    if (this.mStartScanOnResume)
    {
      this.mLocalAdapter.startScanning(true);
      this.mStartScanOnResume = false;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\DevicePickerFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */