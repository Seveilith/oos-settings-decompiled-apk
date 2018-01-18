package com.android.settings.bluetooth;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public class RequestPermissionHelperActivity
  extends AlertActivity
  implements DialogInterface.OnClickListener
{
  public static final String ACTION_INTERNAL_REQUEST_BT_ON = "com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON";
  public static final String ACTION_INTERNAL_REQUEST_BT_ON_AND_DISCOVERABLE = "com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON_AND_DISCOVERABLE";
  private static final String TAG = "RequestPermissionHelperActivity";
  private boolean mEnableOnly;
  private LocalBluetoothAdapter mLocalAdapter;
  private int mTimeout;
  
  private boolean parseIntent()
  {
    Object localObject = getIntent();
    if ((localObject != null) && (((Intent)localObject).getAction().equals("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON"))) {
      this.mEnableOnly = true;
    }
    for (;;)
    {
      localObject = Utils.getLocalBtManager(this);
      if (localObject != null) {
        break label93;
      }
      Log.e("RequestPermissionHelperActivity", "Error: there's a problem starting Bluetooth");
      setResult(0);
      return true;
      if ((localObject == null) || (!((Intent)localObject).getAction().equals("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON_AND_DISCOVERABLE"))) {
        break;
      }
      this.mEnableOnly = false;
      this.mTimeout = ((Intent)localObject).getIntExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 120);
    }
    setResult(0);
    return true;
    label93:
    this.mLocalAdapter = ((LocalBluetoothManager)localObject).getBluetoothAdapter();
    return false;
  }
  
  void createDialog()
  {
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    if (this.mEnableOnly) {
      localAlertParams.mMessage = getString(2131690876);
    }
    for (;;)
    {
      localAlertParams.mPositiveButtonText = getString(2131690774);
      localAlertParams.mPositiveButtonListener = this;
      localAlertParams.mNegativeButtonText = getString(2131690775);
      localAlertParams.mNegativeButtonListener = this;
      setupAlert();
      return;
      if (this.mTimeout == 0) {
        localAlertParams.mMessage = getString(2131690883);
      } else {
        localAlertParams.mMessage = getString(2131690882, new Object[] { Integer.valueOf(this.mTimeout) });
      }
    }
  }
  
  public void onBackPressed()
  {
    setResult(0);
    super.onBackPressed();
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    int i;
    int j;
    switch (paramInt)
    {
    default: 
      return;
    case -1: 
      i = 0;
      j = 30;
    }
    try
    {
      int k;
      do
      {
        paramInt = this.mLocalAdapter.getBluetoothState();
        i = paramInt;
        Thread.sleep(100L);
        i = paramInt;
        if (paramInt != 13) {
          break;
        }
        k = j - 1;
        i = paramInt;
        j = k;
      } while (k > 0);
      i = paramInt;
    }
    catch (InterruptedException paramDialogInterface)
    {
      for (;;) {}
    }
    if ((i == 11) || (i == 12)) {
      paramInt = 64536;
    }
    for (;;)
    {
      setResult(paramInt);
      return;
      if (this.mLocalAdapter.enable()) {
        break;
      }
      paramInt = 0;
      continue;
      paramInt = 0;
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (parseIntent())
    {
      finish();
      return;
    }
    createDialog();
    if (getResources().getBoolean(2131558416))
    {
      onClick(null, -1);
      dismiss();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\RequestPermissionHelperActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */