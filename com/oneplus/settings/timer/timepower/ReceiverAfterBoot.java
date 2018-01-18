package com.oneplus.settings.timer.timepower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings.System;
import android.util.Log;

public class ReceiverAfterBoot
  extends BroadcastReceiver
{
  private static final boolean DEBUG = false;
  private static final String KEY_INTERNAL_SDCARD_STATE = "persist.sys.oppo.iSdCardState";
  private static final String TAG = "ReceiverAfterBoot";
  
  private void setInternalSdState(Context paramContext)
  {
    String str = Settings.System.getString(paramContext.getContentResolver(), "persist.sys.oppo.iSdCardState");
    if ((str == null) || (str.isEmpty())) {}
    while ("mounted".equals(str)) {
      return;
    }
    Settings.System.putString(paramContext.getContentResolver(), "persist.sys.oppo.iSdCardState", "mounted");
  }
  
  private void statisticsPowerOnTimes(Context paramContext)
  {
    int i = Settings.System.getInt(paramContext.getContentResolver(), "oem_pwoer_on_times", 0);
    Settings.System.putInt(paramContext.getContentResolver(), "oem_pwoer_on_times", i + 1);
  }
  
  private void writeUsingThemeFlag(Context paramContext, boolean paramBoolean)
  {
    int i = 0;
    if (Settings.System.getInt(paramContext.getContentResolver(), "oem_is_using_theme", 0) != 0)
    {
      paramContext = paramContext.getContentResolver();
      if (paramBoolean) {
        i = 1;
      }
      Settings.System.putInt(paramContext, "oem_is_using_theme", i);
    }
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    Log.i("BOOTCOMPLETED", "==========ReceiverAfterBoot :[" + paramIntent.getAction() + "] =====");
    if ("android.intent.action.BOOT_COMPLETED".equals(paramIntent.getAction()))
    {
      Log.i("BOOTCOMPLETED", "==========ReceiverAfterBoot : ACTION_BOOT_COMPLETED=====");
      writeUsingThemeFlag(paramContext, false);
      statisticsPowerOnTimes(paramContext);
      setInternalSdState(paramContext);
    }
    long[] arrayOfLong = new long[2];
    Object localObject;
    if (SystemProperties.getInt("persist.sys.quick.test.mode", 0) != 4)
    {
      arrayOfLong = SettingsUtil.getNearestTime(Settings.System.getString(paramContext.getContentResolver(), "def_timepower_config"));
      localObject = new Intent("com.android.settings.POWER_ON");
      if (arrayOfLong[0] == 0L) {
        break label326;
      }
      SettingsUtil.setAlarm(paramContext, (Intent)localObject, arrayOfLong[0], 0);
      label129:
      if (!"android.fpd.boot_completed".equals(paramIntent.getAction()))
      {
        localObject = new Intent("com.android.settings.POWER_OFF");
        if (arrayOfLong[1] == 0L) {
          break label348;
        }
        Bundle localBundle = new Bundle();
        localBundle.putLong("trigger_time", arrayOfLong[1]);
        ((Intent)localObject).putExtras(localBundle);
        SettingsUtil.setAlarm(paramContext, (Intent)localObject, arrayOfLong[1], 1);
      }
    }
    for (;;)
    {
      if ((!"android.intent.action.BOOT_COMPLETED".equals(paramIntent.getAction())) && ("android.intent.action.TIME_SET".equals(paramIntent.getAction()))) {}
      return;
      long l = System.currentTimeMillis();
      arrayOfLong[1] = (60000L + l);
      arrayOfLong[0] = (120000L + l);
      localObject = paramContext.getSharedPreferences("sp_count", 0);
      int i = ((SharedPreferences)localObject).getInt("count", 1);
      ((SharedPreferences)localObject).edit().putInt("count", i + 1).commit();
      Log.d("ReceiverAfterBoot", "-------Total test times:" + i + "--------");
      break;
      label326:
      if (paramContext.getPackageManager().hasSystemFeature("oppo.hw.manufacturer.mtk")) {}
      SettingsUtil.cancelAlarm(paramContext, (Intent)localObject, 0);
      break label129;
      label348:
      SettingsUtil.cancelAlarm(paramContext, (Intent)localObject, 1);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\timer\timepower\ReceiverAfterBoot.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */