package com.oneplus.settings.receiver;

import android.app.AppOpsManager;
import android.app.Application;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.util.Log;
import com.android.settings.bluetooth.Utils;
import com.android.settingslib.TetherUtil;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPPrefUtil;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.OPZenModeUtils;

public class SettingsReceiver
  extends BroadcastReceiver
{
  private static final String ACTION_OTG_AUTO_SHUTDOWN = "oneplus.intent.action.otg_auto_shutdown";
  private static final String ACTION_THREE_KEY = "com.oem.intent.action.THREE_KEY_MODE";
  private static final String BOOT_BROADCAST = "android.intent.action.BOOT_COMPLETED";
  private static final int NO_OEM_FONT_DIALOG = 0;
  private static final int SYSTEM_DEFALUT_FONT = 1;
  private AppOpsManager mAppOpsManager;
  private PackageManager mPackageManager;
  private UserManager mUm;
  private int mZenMode = 0;
  
  private void setBluetoothScanMode()
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          String str = Settings.System.getString(SettingsBaseApplication.mApplication.getContentResolver(), "oem_oneplus_devicename");
          LocalBluetoothAdapter localLocalBluetoothAdapter = Utils.getLocalBtManager(SettingsBaseApplication.mApplication).getBluetoothAdapter();
          int i = Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "bluetooth_default_scan_mode", 23);
          if ((str != null) && (localLocalBluetoothAdapter != null))
          {
            Log.d("SettingsReceiver", "bluetooth scan mode = " + i);
            localLocalBluetoothAdapter.setName(str);
            localLocalBluetoothAdapter.setScanMode(i);
            OPPrefUtil.putInt("oneplus_bluetooth_scan_mode_flag", 1);
          }
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    }).start();
  }
  
  private void setFontMode(int paramInt)
  {
    Intent localIntent = new Intent("android.settings.OEM_FONT_MODE");
    localIntent.putExtra("oem_font_mode", paramInt);
    localIntent.putExtra("oem_font_dialog", 0);
    localIntent.addFlags(268435456);
    SettingsBaseApplication.mApplication.sendBroadcast(localIntent);
  }
  
  public void onReceive(final Context paramContext, Intent paramIntent)
  {
    String str1 = paramIntent.getAction();
    this.mZenMode = NotificationManager.from(paramContext).getZenMode();
    Log.d("SettingsReceiver", "action = " + str1);
    if (("android.intent.action.PACKAGE_REMOVED".equals(str1)) || ("android.intent.action.PACKAGE_ADDED".equals(str1))) {
      OPUtils.setAppUpdated(true);
    }
    if ((!"android.intent.action.PACKAGE_REMOVED".equals(str1)) || (paramIntent.getData() == null) || (paramIntent.getBooleanExtra("android.intent.extra.REPLACING", false))) {}
    for (;;)
    {
      if (("android.intent.action.PACKAGE_ADDED".equals(str1)) && (paramIntent.getData() != null) && ("com.oneplus.cloud".equals(paramIntent.getData().getSchemeSpecificPart()))) {
        OPUtils.isExist_Cloud_Package = null;
      }
      if (str1.equals("codeaurora.net.conn.TETHER_AUTO_SHUT_DOWN_SOFTAP")) {
        TetherUtil.setWifiTethering(false, paramContext);
      }
      if (str1.equals("com.oem.intent.action.THREE_KEY_MODE")) {
        OPZenModeUtils.getInstance(paramContext).sendAppTrackerDelay();
      }
      if ((str1.equals("android.intent.action.BOOT_COMPLETED")) && (paramContext.getSharedPreferences("App_Tracker", 0).getInt("zen_mode", 0) != this.mZenMode)) {
        OPZenModeUtils.getInstance(paramContext).sendAppTrackerDelay();
      }
      int i;
      if ("android.intent.action.LOCALE_CHANGED".equals(str1))
      {
        if (!OPUtils.isSupportFontStyleSetting())
        {
          Log.i("SettingsReceiver", "! isSupportFontStyleSetting Language change");
          setFontMode(1);
        }
      }
      else
      {
        if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(str1))
        {
          i = paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
          Log.d("SettingsReceiver", "android.bluetooth.adapter.action.STATE_CHANGED");
          int j = OPPrefUtil.getInt("oneplus_bluetooth_scan_mode_flag", 0);
          if ((i == 12) && (j == 0)) {
            setBluetoothScanMode();
          }
        }
        if (str1.equals("com.oem.intent.action.BOOT_COMPLETED"))
        {
          this.mUm = ((UserManager)paramContext.getSystemService("user"));
          if ((this.mUm != null) && (this.mUm.isUserRunning(999))) {
            Log.d("SettingsReceiver", "Handle Parallel App Requirement");
          }
        }
      }
      try
      {
        i = Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "oem_acc_sensor_three_finger", 0);
        Settings.System.putIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "oem_acc_sensor_three_finger", i, 999);
        new Thread(new Runnable()
        {
          public void run()
          {
            if (OPUtils.isO2()) {
              OPUtils.installMultiApp(paramContext, "com.google.android.gms", 999);
            }
            OPUtils.installMultiApp(paramContext, "com.oneplus.ifaaservice", 999);
          }
        }).start();
        if ("oneplus.intent.action.otg_auto_shutdown".equals(paramIntent.getAction()))
        {
          SystemProperties.set("persist.sys.oem.otg_support", "false");
          Settings.Global.putInt(paramContext.getContentResolver(), "oneplus_otg_auto_disable", 0);
          if (Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "oneplus_otg_auto_disable_is_first", 0, 0) == 0)
          {
            paramIntent = new Intent("oneplus.intent.action.OTG_SETTINGS");
            paramIntent.setFlags(268435456);
            paramIntent = PendingIntent.getActivity(paramContext, 0, paramIntent, 1073741824);
            localObject = new Notification.Builder(paramContext).setContentIntent(paramIntent).setAutoCancel(true).setContentTitle(paramContext.getResources().getString(2131690551)).setSmallIcon(2130838212);
            paramIntent = (NotificationManager)paramContext.getSystemService("notification");
            localObject = ((Notification.Builder)localObject).build();
            ((Notification)localObject).flags |= 0x8;
            paramIntent.notify(2131690551, (Notification)localObject);
            Settings.System.putInt(paramContext.getContentResolver(), "oneplus_otg_auto_disable_is_first", 1);
          }
        }
        return;
        String str2 = paramIntent.getData().getSchemeSpecificPart();
        Log.d("SettingsReceiver", "ACTION_PACKAGE_REMOVED pkgName= " + str2);
        if (this.mAppOpsManager == null) {
          this.mAppOpsManager = ((AppOpsManager)paramContext.getSystemService("appops"));
        }
        if (this.mPackageManager == null) {
          this.mPackageManager = paramContext.getPackageManager();
        }
        Object localObject = null;
        try
        {
          ApplicationInfo localApplicationInfo = this.mPackageManager.getApplicationInfoByUserId(str2, 1, 999);
          localObject = localApplicationInfo;
          Log.d("SettingsReceiver", "multiAppInfo = " + localObject);
          if (localObject != null) {}
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException2)
        {
          try
          {
            localObject = this.mPackageManager.getApplicationInfoByUserId(str2, 1, 0);
            this.mAppOpsManager.setMode(69, ((ApplicationInfo)localObject).uid, str2, 1);
            OPUtils.removeMultiApp(paramContext, str2);
            if (!"com.oneplus.cloud".equals(str2)) {
              continue;
            }
            OPUtils.isExist_Cloud_Package = null;
            continue;
            localNameNotFoundException2 = localNameNotFoundException2;
            Log.d("SettingsReceiver", "Not found pkg = " + str2 + " in user " + 999);
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException1)
          {
            for (;;)
            {
              localNameNotFoundException1.printStackTrace();
            }
          }
        }
        Log.i("SettingsReceiver", " isSupportFontStyleSetting Language change");
        setFontMode(Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "oem_font_mode", 1, 0));
      }
      catch (Exception localException)
      {
        for (;;)
        {
          localException.printStackTrace();
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\receiver\SettingsReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */