package com.android.settingslib.bluetooth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothDiscoverableTimeoutReceiver
  extends BroadcastReceiver
{
  private static final String INTENT_DISCOVERABLE_TIMEOUT = "android.bluetooth.intent.DISCOVERABLE_TIMEOUT";
  private static final String TAG = "BluetoothDiscoverableTimeoutReceiver";
  
  public static void cancelDiscoverableAlarm(Context paramContext)
  {
    Log.d("BluetoothDiscoverableTimeoutReceiver", "cancelDiscoverableAlarm(): Enter");
    Object localObject = new Intent("android.bluetooth.intent.DISCOVERABLE_TIMEOUT");
    ((Intent)localObject).setClass(paramContext, BluetoothDiscoverableTimeoutReceiver.class);
    localObject = PendingIntent.getBroadcast(paramContext, 0, (Intent)localObject, 536870912);
    if (localObject != null) {
      ((AlarmManager)paramContext.getSystemService("alarm")).cancel((PendingIntent)localObject);
    }
  }
  
  public static void setDiscoverableAlarm(Context paramContext, long paramLong)
  {
    Log.d("BluetoothDiscoverableTimeoutReceiver", "setDiscoverableAlarm(): alarmTime = " + paramLong);
    Intent localIntent = new Intent("android.bluetooth.intent.DISCOVERABLE_TIMEOUT");
    localIntent.setClass(paramContext, BluetoothDiscoverableTimeoutReceiver.class);
    PendingIntent localPendingIntent = PendingIntent.getBroadcast(paramContext, 0, localIntent, 0);
    AlarmManager localAlarmManager = (AlarmManager)paramContext.getSystemService("alarm");
    if (localPendingIntent != null)
    {
      localAlarmManager.cancel(localPendingIntent);
      Log.d("BluetoothDiscoverableTimeoutReceiver", "setDiscoverableAlarm(): cancel prev alarm");
    }
    localAlarmManager.set(0, paramLong, PendingIntent.getBroadcast(paramContext, 0, localIntent, 0));
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = LocalBluetoothAdapter.getInstance();
    if ((paramContext != null) && (paramContext.getState() == 12))
    {
      Log.d("BluetoothDiscoverableTimeoutReceiver", "Disable discoverable...");
      paramContext.setScanMode(21);
      return;
    }
    Log.e("BluetoothDiscoverableTimeoutReceiver", "localBluetoothAdapter is NULL!!");
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\BluetoothDiscoverableTimeoutReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */