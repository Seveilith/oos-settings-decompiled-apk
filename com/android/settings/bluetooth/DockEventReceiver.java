package com.android.settings.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public final class DockEventReceiver
  extends BroadcastReceiver
{
  public static final String ACTION_DOCK_SHOW_UI = "com.android.settings.bluetooth.action.DOCK_SHOW_UI";
  private static final boolean DEBUG = false;
  private static final int EXTRA_INVALID = -1234;
  private static final String TAG = "DockEventReceiver";
  private static PowerManager.WakeLock sStartingService;
  private static final Object sStartingServiceSync = new Object();
  
  private static void beginStartingService(Context paramContext, Intent paramIntent)
  {
    synchronized (sStartingServiceSync)
    {
      if (sStartingService == null) {
        sStartingService = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "StartingDockService");
      }
      sStartingService.acquire();
      try
      {
        if (paramContext.startService(paramIntent) == null) {
          Log.e("DockEventReceiver", "Can't start DockService");
        }
        return;
      }
      catch (Exception paramContext)
      {
        for (;;)
        {
          paramContext.printStackTrace();
        }
      }
    }
  }
  
  public static void finishStartingService(Service paramService, int paramInt)
  {
    synchronized (sStartingServiceSync)
    {
      if ((sStartingService != null) && (paramService.stopSelfResult(paramInt)))
      {
        Log.d("DockEventReceiver", "finishStartingService: stopping service");
        sStartingService.release();
      }
      return;
    }
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent == null) {
      return;
    }
    int i = paramIntent.getIntExtra("android.intent.extra.DOCK_STATE", paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", 64302));
    BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
    if (("android.intent.action.DOCK_EVENT".equals(paramIntent.getAction())) || ("com.android.settings.bluetooth.action.DOCK_SHOW_UI".endsWith(paramIntent.getAction())))
    {
      if ((localBluetoothDevice == null) && (("com.android.settings.bluetooth.action.DOCK_SHOW_UI".endsWith(paramIntent.getAction())) || ((i != 0) && (i != 3)))) {
        return;
      }
      switch (i)
      {
      default: 
        Log.e("DockEventReceiver", "Unknown state: " + i);
      }
    }
    do
    {
      int j;
      do
      {
        return;
        paramIntent = new Intent(paramIntent);
        paramIntent.setClass(paramContext, DockService.class);
        beginStartingService(paramContext, paramIntent);
        return;
        if ((!"android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(paramIntent.getAction())) && (!"android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED".equals(paramIntent.getAction()))) {
          break;
        }
        i = paramIntent.getIntExtra("android.bluetooth.profile.extra.STATE", 2);
        j = paramIntent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 0);
        if (localBluetoothDevice == null) {
          return;
        }
      } while ((i != 0) || (j == 3));
      paramIntent = new Intent(paramIntent);
      paramIntent.setClass(paramContext, DockService.class);
      beginStartingService(paramContext, paramIntent);
      return;
    } while ((!"android.bluetooth.adapter.action.STATE_CHANGED".equals(paramIntent.getAction())) || (paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) == 11));
    paramIntent = new Intent(paramIntent);
    paramIntent.setClass(paramContext, DockService.class);
    beginStartingService(paramContext, paramIntent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\DockEventReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */