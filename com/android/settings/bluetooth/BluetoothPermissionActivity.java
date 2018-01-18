package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.internal.app.AlertController.AlertParams;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public class BluetoothPermissionActivity
  extends AlertActivity
  implements DialogInterface.OnClickListener, Preference.OnPreferenceChangeListener
{
  private static final boolean DEBUG = true;
  private static final String TAG = "BluetoothPermissionActivity";
  private BluetoothDevice mDevice;
  private Button mOkButton;
  private BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL"))
      {
        if (paramAnonymousIntent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2) != BluetoothPermissionActivity.-get1(BluetoothPermissionActivity.this)) {
          return;
        }
        paramAnonymousContext = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        if (BluetoothPermissionActivity.-get0(BluetoothPermissionActivity.this).equals(paramAnonymousContext)) {
          BluetoothPermissionActivity.-wrap0(BluetoothPermissionActivity.this);
        }
      }
    }
  };
  private boolean mReceiverRegistered = false;
  private int mRequestType = 0;
  private String mReturnClass = null;
  private String mReturnPackage = null;
  private View mView;
  private TextView messageView;
  
  private View createConnectionDialogView()
  {
    String str = createRemoteName();
    this.mView = getLayoutInflater().inflate(2130968628, null);
    this.messageView = ((TextView)this.mView.findViewById(2131361987));
    this.messageView.setText(getString(2131690889, new Object[] { str }));
    return this.mView;
  }
  
  private View createMapDialogView()
  {
    String str = createRemoteName();
    this.mView = getLayoutInflater().inflate(2130968628, null);
    this.messageView = ((TextView)this.mView.findViewById(2131361987));
    this.messageView.setText(getString(2131690895, new Object[] { str, str }));
    return this.mView;
  }
  
  private View createPhonebookDialogView()
  {
    String str = createRemoteName();
    this.mView = getLayoutInflater().inflate(2130968628, null);
    this.messageView = ((TextView)this.mView.findViewById(2131361987));
    this.messageView.setText(getString(2131690891, new Object[] { str, str }));
    return this.mView;
  }
  
  private String createRemoteName()
  {
    String str1 = null;
    if (this.mDevice != null) {
      str1 = this.mDevice.getAliasName();
    }
    String str2 = str1;
    if (str1 == null) {
      str2 = getString(2131689591);
    }
    return str2;
  }
  
  private View createSapDialogView()
  {
    String str = createRemoteName();
    this.mView = getLayoutInflater().inflate(2130968628, null);
    this.messageView = ((TextView)this.mView.findViewById(2131361987));
    this.messageView.setText(getString(2131690897, new Object[] { str, str }));
    return this.mView;
  }
  
  private void dismissDialog()
  {
    dismiss();
  }
  
  private void onNegative()
  {
    Log.d("BluetoothPermissionActivity", "onNegative");
    boolean bool = true;
    if (this.mRequestType == 3)
    {
      LocalBluetoothManager localLocalBluetoothManager = Utils.getLocalBtManager(this);
      CachedBluetoothDeviceManager localCachedBluetoothDeviceManager = localLocalBluetoothManager.getCachedDeviceManager();
      CachedBluetoothDevice localCachedBluetoothDevice2 = localCachedBluetoothDeviceManager.findDevice(this.mDevice);
      CachedBluetoothDevice localCachedBluetoothDevice1 = localCachedBluetoothDevice2;
      if (localCachedBluetoothDevice2 == null) {
        localCachedBluetoothDevice1 = localCachedBluetoothDeviceManager.addDevice(localLocalBluetoothManager.getBluetoothAdapter(), localLocalBluetoothManager.getProfileManager(), this.mDevice);
      }
      bool = localCachedBluetoothDevice1.checkAndIncreaseMessageRejectionCount();
    }
    sendReplyIntentToReceiver(false, bool);
  }
  
  private void onPositive()
  {
    Log.d("BluetoothPermissionActivity", "onPositive");
    sendReplyIntentToReceiver(true, true);
    finish();
  }
  
  private void sendReplyIntentToReceiver(boolean paramBoolean1, boolean paramBoolean2)
  {
    Intent localIntent = new Intent("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY");
    if ((this.mReturnPackage != null) && (this.mReturnClass != null)) {
      localIntent.setClassName(this.mReturnPackage, this.mReturnClass);
    }
    Log.i("BluetoothPermissionActivity", "sendReplyIntentToReceiver() Request type: " + this.mRequestType + " mReturnPackage" + this.mReturnPackage + " mReturnClass" + this.mReturnClass);
    if (paramBoolean1) {}
    for (int i = 1;; i = 2)
    {
      localIntent.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", i);
      localIntent.putExtra("android.bluetooth.device.extra.ALWAYS_ALLOWED", paramBoolean2);
      localIntent.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
      localIntent.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
      sendBroadcast(localIntent, "android.permission.BLUETOOTH_ADMIN");
      return;
    }
  }
  
  private void showDialog(String paramString, int paramInt)
  {
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mTitle = paramString;
    Log.i("BluetoothPermissionActivity", "showDialog() Request type: " + this.mRequestType + " this: " + this);
    switch (paramInt)
    {
    }
    for (;;)
    {
      localAlertParams.mPositiveButtonText = getString(2131690771);
      localAlertParams.mPositiveButtonListener = this;
      localAlertParams.mNegativeButtonText = getString(2131690772);
      localAlertParams.mNegativeButtonListener = this;
      this.mOkButton = this.mAlert.getButton(-1);
      setupAlert();
      return;
      localAlertParams.mView = createConnectionDialogView();
      continue;
      localAlertParams.mView = createPhonebookDialogView();
      continue;
      localAlertParams.mView = createMapDialogView();
      continue;
      localAlertParams.mView = createSapDialogView();
    }
  }
  
  public void onBackPressed()
  {
    Log.i("BluetoothPermissionActivity", "Back button pressed! ignoring");
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case -1: 
      onPositive();
      return;
    }
    onNegative();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent();
    if (!paramBundle.getAction().equals("android.bluetooth.device.action.CONNECTION_ACCESS_REQUEST"))
    {
      Log.e("BluetoothPermissionActivity", "Error: this activity may be started only with intent ACTION_CONNECTION_ACCESS_REQUEST");
      finish();
      return;
    }
    this.mDevice = ((BluetoothDevice)paramBundle.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
    this.mReturnPackage = paramBundle.getStringExtra("android.bluetooth.device.extra.PACKAGE_NAME");
    this.mReturnClass = paramBundle.getStringExtra("android.bluetooth.device.extra.CLASS_NAME");
    this.mRequestType = paramBundle.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2);
    Log.i("BluetoothPermissionActivity", "onCreate() Request type: " + this.mRequestType);
    if (this.mRequestType == 1) {
      showDialog(getString(2131690887), this.mRequestType);
    }
    for (;;)
    {
      registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL"));
      this.mReceiverRegistered = true;
      return;
      if (this.mRequestType == 2)
      {
        showDialog(getString(2131690890), this.mRequestType);
      }
      else if (this.mRequestType == 3)
      {
        showDialog(getString(2131690894), this.mRequestType);
      }
      else
      {
        if (this.mRequestType != 4) {
          break;
        }
        showDialog(getString(2131690896), this.mRequestType);
      }
    }
    Log.e("BluetoothPermissionActivity", "Error: bad request type: " + this.mRequestType);
    finish();
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mReceiverRegistered)
    {
      unregisterReceiver(this.mReceiver);
      this.mReceiverRegistered = false;
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\BluetoothPermissionActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */