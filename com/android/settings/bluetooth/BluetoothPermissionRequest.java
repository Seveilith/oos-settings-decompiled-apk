package com.android.settings.bluetooth;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.UserManager;
import android.util.Log;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public final class BluetoothPermissionRequest
  extends BroadcastReceiver
{
  private static final boolean DEBUG = false;
  private static final int NOTIFICATION_ID = 17301632;
  private static final String NOTIFICATION_TAG_MAP = "Message Access";
  private static final String NOTIFICATION_TAG_PBAP = "Phonebook Access";
  private static final String NOTIFICATION_TAG_SAP = "SIM Access";
  private static final String TAG = "BluetoothPermissionRequest";
  Context mContext;
  BluetoothDevice mDevice;
  int mRequestType;
  String mReturnClass = null;
  String mReturnPackage = null;
  
  private boolean checkUserChoice()
  {
    if ((this.mRequestType != 2) && (this.mRequestType != 3) && (this.mRequestType != 4)) {
      return false;
    }
    LocalBluetoothManager localLocalBluetoothManager = Utils.getLocalBtManager(this.mContext);
    CachedBluetoothDeviceManager localCachedBluetoothDeviceManager = localLocalBluetoothManager.getCachedDeviceManager();
    CachedBluetoothDevice localCachedBluetoothDevice2 = localCachedBluetoothDeviceManager.findDevice(this.mDevice);
    CachedBluetoothDevice localCachedBluetoothDevice1 = localCachedBluetoothDevice2;
    if (localCachedBluetoothDevice2 == null) {
      localCachedBluetoothDevice1 = localCachedBluetoothDeviceManager.addDevice(localLocalBluetoothManager.getBluetoothAdapter(), localLocalBluetoothManager.getProfileManager(), this.mDevice);
    }
    int i;
    if (this.mRequestType == 2)
    {
      i = localCachedBluetoothDevice1.getPhonebookPermissionChoice();
      if (i != 0) {}
    }
    do
    {
      do
      {
        do
        {
          return false;
          if (i == 1)
          {
            sendReplyIntentToReceiver(true);
            return true;
          }
          if (i == 2)
          {
            sendReplyIntentToReceiver(false);
            return true;
          }
          Log.e("BluetoothPermissionRequest", "Bad phonebookPermission: " + i);
          return false;
          if (this.mRequestType != 3) {
            break;
          }
          i = localCachedBluetoothDevice1.getMessagePermissionChoice();
        } while (i == 0);
        if (i == 1)
        {
          sendReplyIntentToReceiver(true);
          return true;
        }
        if (i == 2)
        {
          sendReplyIntentToReceiver(false);
          return true;
        }
        Log.e("BluetoothPermissionRequest", "Bad messagePermission: " + i);
        return false;
      } while (this.mRequestType != 4);
      i = localCachedBluetoothDevice1.getSimPermissionChoice();
    } while (i == 0);
    if (i == 1)
    {
      sendReplyIntentToReceiver(true);
      return true;
    }
    if (i == 2)
    {
      sendReplyIntentToReceiver(false);
      return true;
    }
    Log.e("BluetoothPermissionRequest", "Bad simPermission: " + i);
    return false;
  }
  
  private String getNotificationTag(int paramInt)
  {
    if (paramInt == 2) {
      return "Phonebook Access";
    }
    if (this.mRequestType == 3) {
      return "Message Access";
    }
    if (this.mRequestType == 4) {
      return "SIM Access";
    }
    return null;
  }
  
  private void sendReplyIntentToReceiver(boolean paramBoolean)
  {
    Intent localIntent = new Intent("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY");
    if ((this.mReturnPackage != null) && (this.mReturnClass != null)) {
      localIntent.setClassName(this.mReturnPackage, this.mReturnClass);
    }
    if (paramBoolean) {}
    for (int i = 1;; i = 2)
    {
      localIntent.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", i);
      localIntent.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
      localIntent.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
      this.mContext.sendBroadcast(localIntent, "android.permission.BLUETOOTH_ADMIN");
      return;
    }
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    this.mContext = paramContext;
    String str = paramIntent.getAction();
    if (str.equals("android.bluetooth.device.action.CONNECTION_ACCESS_REQUEST"))
    {
      if (com.android.settings.Utils.isManagedProfile((UserManager)paramContext.getSystemService("user"))) {
        return;
      }
      this.mDevice = ((BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
      this.mRequestType = paramIntent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 1);
      this.mReturnPackage = paramIntent.getStringExtra("android.bluetooth.device.extra.PACKAGE_NAME");
      this.mReturnClass = paramIntent.getStringExtra("android.bluetooth.device.extra.CLASS_NAME");
      if (checkUserChoice()) {
        return;
      }
      localIntent1 = new Intent(str);
      localIntent1.setClass(paramContext, BluetoothPermissionActivity.class);
      localIntent1.setFlags(402653184);
      localIntent1.setType(Integer.toString(this.mRequestType));
      localIntent1.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
      localIntent1.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
      localIntent1.putExtra("android.bluetooth.device.extra.PACKAGE_NAME", this.mReturnPackage);
      localIntent1.putExtra("android.bluetooth.device.extra.CLASS_NAME", this.mReturnClass);
      if (this.mDevice != null)
      {
        paramIntent = this.mDevice.getAddress();
        if (this.mDevice == null) {
          break label245;
        }
        str = this.mDevice.getName();
        localObject = (PowerManager)paramContext.getSystemService("power");
        if ((!((PowerManager)localObject).isScreenOn()) || (!LocalBluetoothPreferences.shouldShowDialogInForeground(paramContext, paramIntent, str))) {
          break label250;
        }
        paramContext.startActivity(localIntent1);
      }
    }
    label245:
    label250:
    while (!str.equals("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL"))
    {
      Intent localIntent1;
      for (;;)
      {
        return;
        paramIntent = null;
        continue;
        str = null;
      }
      Object localObject = ((PowerManager)localObject).newWakeLock(805306394, "ConnectionAccessActivity");
      ((PowerManager.WakeLock)localObject).setReferenceCounted(false);
      ((PowerManager.WakeLock)localObject).acquire();
      Intent localIntent2 = new Intent("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY");
      localIntent2.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
      localIntent2.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", 2);
      localIntent2.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
      if (this.mDevice != null)
      {
        paramIntent = this.mDevice.getAliasName();
        switch (this.mRequestType)
        {
        default: 
          str = paramContext.getString(2131690887);
          paramIntent = paramContext.getString(2131690889, new Object[] { paramIntent, paramIntent });
        }
      }
      for (;;)
      {
        paramIntent = new Notification.Builder(paramContext).setContentTitle(str).setTicker(paramIntent).setContentText(paramIntent).setSmallIcon(17301632).setAutoCancel(true).setPriority(2).setOnlyAlertOnce(false).setDefaults(-1).setContentIntent(PendingIntent.getActivity(paramContext, 0, localIntent1, 0)).setDeleteIntent(PendingIntent.getBroadcast(paramContext, 0, localIntent2, 0)).setColor(paramContext.getColor(17170523)).build();
        paramIntent.flags |= 0x20;
        ((NotificationManager)paramContext.getSystemService("notification")).notify(getNotificationTag(this.mRequestType), 17301632, paramIntent);
        ((PowerManager.WakeLock)localObject).release();
        return;
        paramIntent = null;
        break;
        str = paramContext.getString(2131690890);
        paramIntent = paramContext.getString(2131690891, new Object[] { paramIntent, paramIntent });
        continue;
        str = paramContext.getString(2131690894);
        paramIntent = paramContext.getString(2131690895, new Object[] { paramIntent, paramIntent });
        continue;
        str = paramContext.getString(2131690896);
        paramIntent = paramContext.getString(2131690897, new Object[] { paramIntent, paramIntent });
      }
    }
    paramContext = (NotificationManager)paramContext.getSystemService("notification");
    this.mRequestType = paramIntent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2);
    paramContext.cancel(getNotificationTag(this.mRequestType), 17301632);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\BluetoothPermissionRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */