package com.android.settings.bluetooth;

import android.app.KeyguardManager;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.PowerManager;
import android.text.TextUtils;

public final class BluetoothPairingRequest
  extends BroadcastReceiver
{
  private static final int NOTIFICATION_ID = 17301632;
  
  private boolean isScreenLocked(Context paramContext)
  {
    return ((KeyguardManager)paramContext.getSystemService("keyguard")).isKeyguardLocked();
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    String str = paramIntent.getAction();
    BluetoothDevice localBluetoothDevice;
    int i;
    Object localObject2;
    if (str.equals("android.bluetooth.device.action.PAIRING_REQUEST"))
    {
      localBluetoothDevice = (BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
      i = paramIntent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", Integer.MIN_VALUE);
      localObject2 = new Intent();
      ((Intent)localObject2).setClass(paramContext, BluetoothPairingDialog.class);
      ((Intent)localObject2).putExtra("android.bluetooth.device.extra.DEVICE", localBluetoothDevice);
      ((Intent)localObject2).putExtra("android.bluetooth.device.extra.PAIRING_VARIANT", i);
      if ((i != 2) && (i != 4)) {}
    }
    label99:
    label138:
    label150:
    label325:
    label340:
    label346:
    label353:
    do
    {
      do
      {
        ((Intent)localObject2).putExtra("android.bluetooth.device.extra.PAIRING_KEY", paramIntent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", Integer.MIN_VALUE));
        break label325;
        break label325;
        ((Intent)localObject2).setAction("android.bluetooth.device.action.PAIRING_REQUEST");
        ((Intent)localObject2).setFlags(268435456);
        Object localObject3 = (PowerManager)paramContext.getSystemService("power");
        Object localObject1;
        if (localBluetoothDevice != null)
        {
          str = localBluetoothDevice.getAddress();
          if (localBluetoothDevice == null) {
            break label340;
          }
          localObject1 = localBluetoothDevice.getName();
          boolean bool = LocalBluetoothPreferences.shouldShowDialogInForeground(paramContext, str, (String)localObject1);
          if ((((PowerManager)localObject3).isInteractive()) && (bool) && (!isScreenLocked(paramContext))) {
            break label346;
          }
          localObject1 = paramContext.getResources();
          localObject3 = new Notification.Builder(paramContext).setSmallIcon(17301632).setTicker(((Resources)localObject1).getString(2131690870));
          localObject2 = PendingIntent.getActivity(paramContext, 0, (Intent)localObject2, 134217728);
          str = paramIntent.getStringExtra("android.bluetooth.device.extra.NAME");
          paramIntent = str;
          if (TextUtils.isEmpty(str)) {
            if (localBluetoothDevice == null) {
              break label353;
            }
          }
        }
        for (paramIntent = localBluetoothDevice.getAliasName();; paramIntent = paramContext.getString(17039374))
        {
          ((Notification.Builder)localObject3).setContentTitle(((Resources)localObject1).getString(2131690871)).setContentText(((Resources)localObject1).getString(2131690872, new Object[] { paramIntent })).setContentIntent((PendingIntent)localObject2).setAutoCancel(true).setDefaults(1).setColor(paramContext.getColor(17170523));
          ((NotificationManager)paramContext.getSystemService("notification")).notify(17301632, ((Notification.Builder)localObject3).getNotification());
          return;
          if (i != 5) {
            break label99;
          }
          break;
          str = null;
          break label138;
          localObject1 = null;
          break label150;
          paramContext.startActivity((Intent)localObject2);
          return;
        }
        if (str.equals("android.bluetooth.device.action.PAIRING_CANCEL"))
        {
          paramIntent = new Intent();
          paramIntent.setClass(paramContext, BluetoothPairingDialog.class);
          paramIntent.setAction("android.bluetooth.device.action.PAIRING_REQUEST");
          paramIntent.setFlags(268435456);
          paramIntent = PendingIntent.getActivity(paramContext, 0, paramIntent, 536870912);
          if (paramIntent != null) {
            paramIntent.cancel();
          }
          ((NotificationManager)paramContext.getSystemService("notification")).cancel(17301632);
          return;
        }
      } while (!"android.bluetooth.device.action.BOND_STATE_CHANGED".equals(str));
      i = paramIntent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
    } while ((paramIntent.getIntExtra("android.bluetooth.device.extra.PREVIOUS_BOND_STATE", Integer.MIN_VALUE) != 11) || (i != 10));
    ((NotificationManager)paramContext.getSystemService("notification")).cancel(17301632);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\BluetoothPairingRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */