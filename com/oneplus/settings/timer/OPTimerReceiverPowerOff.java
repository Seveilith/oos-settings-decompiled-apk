package com.oneplus.settings.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings.System;
import android.util.Log;
import com.oneplus.settings.timer.timepower.OPPowerOffPromptActivity;
import com.oneplus.settings.timer.timepower.SettingsUtil;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;

public class OPTimerReceiverPowerOff
  extends BroadcastReceiver
{
  private PowerManager.WakeLock mLock = null;
  private PowerManager pm = null;
  
  public static boolean checkSwitch(Context paramContext, boolean paramBoolean)
  {
    paramContext = Settings.System.getString(paramContext.getContentResolver(), "def_timepower_config");
    if (paramContext == null) {
      return false;
    }
    int[][] arrayOfInt = (int[][])Array.newInstance(Integer.TYPE, new int[] { 2, 2 });
    boolean[][] arrayOfBoolean = (boolean[][])Array.newInstance(Boolean.TYPE, new int[] { 2, 2 });
    int j = 0;
    int i = 0;
    while (j <= 6)
    {
      String str = paramContext.substring(j, j + 6);
      arrayOfInt[i][0] = Integer.parseInt(str.substring(0, 2));
      arrayOfInt[i][1] = Integer.parseInt(str.substring(2, 4));
      arrayOfBoolean[i][0] = intToBool(Integer.parseInt(str.substring(4, 5)));
      arrayOfBoolean[i][1] = intToBool(Integer.parseInt(str.substring(5, 6)));
      j += 6;
      i += 1;
    }
    if (paramBoolean)
    {
      if (arrayOfBoolean[0][1] != 0) {
        return true;
      }
    }
    else if (arrayOfBoolean[1][1] != 0) {
      return true;
    }
    return false;
  }
  
  static boolean intToBool(int paramInt)
  {
    boolean bool = false;
    if (paramInt != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isPowerOffEnable(Context paramContext)
  {
    return checkSwitch(paramContext, false);
  }
  
  public static boolean isPowerOnEnable(Context paramContext)
  {
    return checkSwitch(paramContext, true);
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    Object localObject1 = paramIntent.getAction();
    paramIntent = new long[2];
    paramIntent = SettingsUtil.getNearestTime(Settings.System.getString(paramContext.getContentResolver(), "def_timepower_config"));
    this.pm = ((PowerManager)paramContext.getSystemService("power"));
    Object localObject2;
    if ((((String)localObject1).equals("com.android.settings.action.REQUEST_POWER_OFF")) || ("android.intent.action.TIME_SET".equals(localObject1)) || ("android.intent.action.TIMEZONE_CHANGED".equals(localObject1)) || ("android.intent.action.BOOT_COMPLETED".equals(localObject1))) {
      if (isPowerOffEnable(paramContext))
      {
        localObject2 = new Intent("com.android.settings.POWER_OP_OFF");
        if (paramIntent[1] != 0L)
        {
          Object localObject3 = Calendar.getInstance();
          ((Calendar)localObject3).setTimeInMillis(paramIntent[0]);
          Log.d("boot", "Power on alarm with flag set:" + ((Calendar)localObject3).getTime().toString());
          localObject3 = (AlarmManager)paramContext.getSystemService("alarm");
          localObject2 = PendingIntent.getBroadcast(paramContext, 0, (Intent)localObject2, 134217728);
          ((AlarmManager)localObject3).setExact(0, paramIntent[1], (PendingIntent)localObject2);
        }
      }
    }
    for (;;)
    {
      if ((((String)localObject1).equals("com.android.settings.POWER_OP_ON")) || ("android.intent.action.TIME_SET".equals(localObject1)) || ("android.intent.action.TIMEZONE_CHANGED".equals(localObject1)) || ("android.intent.action.BOOT_COMPLETED".equals(localObject1)))
      {
        localObject2 = new Intent("com.android.settings.POWER_OP_ON");
        localObject1 = (AlarmManager)paramContext.getSystemService("alarm");
        localObject2 = PendingIntent.getBroadcast(paramContext, 1, (Intent)localObject2, 0);
        if (!isPowerOnEnable(paramContext)) {
          break;
        }
        ((AlarmManager)localObject1).setExact(5, paramIntent[0], (PendingIntent)localObject2);
      }
      return;
      if (((String)localObject1).equals("com.android.settings.POWER_OP_OFF"))
      {
        long l = (System.currentTimeMillis() - paramIntent[1] - 86400000L) % 86400000L;
        if ((l >= 0L) && (l > 60000L)) {}
        while ((l < 0L) && (l > -86340000L)) {
          return;
        }
        localObject2 = new Intent(paramContext, OPPowerOffPromptActivity.class);
        ((Intent)localObject2).setFlags(268435456);
        paramContext.startActivity((Intent)localObject2);
      }
      else if (((String)localObject1).equals("com.android.settings.POWER_CONFIRM_OP_OFF"))
      {
        if (this.mLock != null)
        {
          this.mLock.release();
          this.mLock = null;
        }
        this.mLock = this.pm.newWakeLock(268435466, "TimepowerWakeLock");
        this.mLock.acquire();
        localObject2 = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        ((Intent)localObject2).putExtra("android.intent.extra.KEY_CONFIRM", false);
        ((Intent)localObject2).setFlags(268435456);
        paramContext.startActivity((Intent)localObject2);
      }
      else if (((String)localObject1).equals("com.android.settings.POWER_CANCEL_OP_OFF"))
      {
        localObject2 = new Intent("com.android.settings.POWER_OP_OFF");
        ((AlarmManager)paramContext.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(paramContext, 0, (Intent)localObject2, 134217728));
      }
    }
    ((AlarmManager)localObject1).cancel((PendingIntent)localObject2);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\timer\OPTimerReceiverPowerOff.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */