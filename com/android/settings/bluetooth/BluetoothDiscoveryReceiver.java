package com.android.settings.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class BluetoothDiscoveryReceiver
  extends BroadcastReceiver
{
  private static final String TAG = "BluetoothDiscoveryReceiver";
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramIntent = paramIntent.getAction();
    Log.v("BluetoothDiscoveryReceiver", "Received: " + paramIntent);
    if ((paramIntent.equals("android.bluetooth.adapter.action.DISCOVERY_STARTED")) || (paramIntent.equals("android.bluetooth.adapter.action.DISCOVERY_FINISHED"))) {
      LocalBluetoothPreferences.persistDiscoveringTimestamp(paramContext);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\BluetoothDiscoveryReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */