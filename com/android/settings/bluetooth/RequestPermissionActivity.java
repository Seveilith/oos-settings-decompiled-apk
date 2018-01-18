package com.android.settings.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import com.android.settingslib.bluetooth.BluetoothDiscoverableTimeoutReceiver;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public class RequestPermissionActivity
  extends Activity
  implements DialogInterface.OnClickListener
{
  private static final int MAX_DISCOVERABLE_TIMEOUT = 3600;
  private static final int REQUEST_CODE_START_BT = 1;
  static final int RESULT_BT_STARTING_OR_STARTED = -1000;
  private static final String TAG = "RequestPermissionActivity";
  private AlertDialog mDialog;
  private boolean mEnableOnly;
  private LocalBluetoothAdapter mLocalAdapter;
  private boolean mNeededToEnableBluetooth;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent == null) {
        return;
      }
      if ((RequestPermissionActivity.-get0(RequestPermissionActivity.this)) && ("android.bluetooth.adapter.action.STATE_CHANGED".equals(paramAnonymousIntent.getAction())) && (paramAnonymousIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) == 12) && (RequestPermissionActivity.-get1(RequestPermissionActivity.this))) {
        RequestPermissionActivity.-wrap0(RequestPermissionActivity.this);
      }
    }
  };
  private int mTimeout = 120;
  private boolean mUserConfirmed;
  
  private void createDialog()
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    if (this.mNeededToEnableBluetooth)
    {
      localBuilder.setMessage(getString(2131690884));
      localBuilder.setCancelable(false);
      this.mDialog = localBuilder.create();
      this.mDialog.show();
      if (getResources().getBoolean(2131558416)) {
        onClick(null, -1);
      }
      return;
    }
    if (this.mTimeout == 0) {
      localBuilder.setMessage(getString(2131690878));
    }
    for (;;)
    {
      localBuilder.setPositiveButton(getString(2131690774), this);
      localBuilder.setNegativeButton(getString(2131690775), this);
      break;
      localBuilder.setMessage(getString(2131690877, new Object[] { Integer.valueOf(this.mTimeout) }));
    }
  }
  
  private boolean parseIntent()
  {
    Object localObject = getIntent();
    if ((localObject != null) && (((Intent)localObject).getAction().equals("android.bluetooth.adapter.action.REQUEST_ENABLE"))) {
      this.mEnableOnly = true;
    }
    for (;;)
    {
      localObject = Utils.getLocalBtManager(this);
      if (localObject != null) {
        break label148;
      }
      Log.e("RequestPermissionActivity", "Error: there's a problem starting Bluetooth");
      setResult(0);
      return true;
      if ((localObject == null) || (!((Intent)localObject).getAction().equals("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE"))) {
        break;
      }
      this.mTimeout = ((Intent)localObject).getIntExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 120);
      Log.d("RequestPermissionActivity", "Setting Bluetooth Discoverable Timeout = " + this.mTimeout);
      if ((this.mTimeout < 1) || (this.mTimeout > 3600)) {
        this.mTimeout = 120;
      }
    }
    Log.e("RequestPermissionActivity", "Error: this activity may be started only with intent android.bluetooth.adapter.action.REQUEST_ENABLE or android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
    setResult(0);
    return true;
    label148:
    this.mLocalAdapter = ((LocalBluetoothManager)localObject).getBluetoothAdapter();
    return false;
  }
  
  private void proceedAndFinish()
  {
    int i;
    if (this.mEnableOnly) {
      i = -1;
    }
    for (;;)
    {
      if (this.mDialog != null) {
        this.mDialog.dismiss();
      }
      setResult(i);
      finish();
      return;
      if (this.mLocalAdapter.setScanMode(23, this.mTimeout))
      {
        long l = System.currentTimeMillis() + this.mTimeout * 1000L;
        LocalBluetoothPreferences.persistDiscoverableEndTimestamp(this, l);
        if (this.mTimeout > 0) {
          BluetoothDiscoverableTimeoutReceiver.setDiscoverableAlarm(this, l);
        }
        int j = this.mTimeout;
        i = j;
        if (j < 1) {
          i = 1;
        }
      }
      else
      {
        i = 0;
      }
    }
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 != 1)
    {
      Log.e("RequestPermissionActivity", "Unexpected onActivityResult " + paramInt1 + ' ' + paramInt2);
      setResult(0);
      finish();
      return;
    }
    if (paramInt2 != 64536)
    {
      setResult(paramInt2);
      finish();
      return;
    }
    this.mUserConfirmed = true;
    if (this.mLocalAdapter.getBluetoothState() == 12)
    {
      proceedAndFinish();
      return;
    }
    createDialog();
  }
  
  public void onBackPressed()
  {
    setResult(0);
    super.onBackPressed();
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case -1: 
      proceedAndFinish();
      return;
    }
    setResult(0);
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (parseIntent())
    {
      finish();
      return;
    }
    int i = this.mLocalAdapter.getState();
    switch (i)
    {
    default: 
      Log.e("RequestPermissionActivity", "Unknown adapter state: " + i);
      return;
    case 10: 
    case 11: 
    case 13: 
      registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
      paramBundle = new Intent();
      paramBundle.setClass(this, RequestPermissionHelperActivity.class);
      if (this.mEnableOnly) {
        paramBundle.setAction("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON");
      }
      for (;;)
      {
        startActivityForResult(paramBundle, 1);
        this.mNeededToEnableBluetooth = true;
        return;
        paramBundle.setAction("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON_AND_DISCOVERABLE");
        paramBundle.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", this.mTimeout);
      }
    }
    if (this.mEnableOnly)
    {
      proceedAndFinish();
      return;
    }
    createDialog();
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mNeededToEnableBluetooth) {
      unregisterReceiver(this.mReceiver);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\RequestPermissionActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */