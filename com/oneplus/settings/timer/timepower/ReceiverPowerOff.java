package com.oneplus.settings.timer.timepower;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import java.util.List;

public class ReceiverPowerOff
  extends BroadcastReceiver
{
  private static final String TAG = "ReceiverPowerOff";
  private static boolean mIsCalling = false;
  private static boolean mIsPoweroff = false;
  private Context mContext = null;
  private final Handler mHandler = new Handler();
  private final Runnable mPowerOffPromptRunnable = new Runnable()
  {
    public void run()
    {
      if ((ReceiverPowerOff.-get0(ReceiverPowerOff.this) != null) && (ReceiverPowerOff.-get3(ReceiverPowerOff.this) != null))
      {
        Object localObject2 = ((ActivityManager.RunningTaskInfo)((ActivityManager)ReceiverPowerOff.-get0(ReceiverPowerOff.this).getSystemService("activity")).getRunningTasks(1).get(0)).topActivity;
        Object localObject1 = ((ComponentName)localObject2).getPackageName();
        localObject2 = ((ComponentName)localObject2).getClassName();
        Log.d("ReceiverPowerOff", "pkg:" + (String)localObject1);
        Log.d("ReceiverPowerOff", "cls:" + (String)localObject2);
        if ((((String)localObject1).equals("com.android.incallui")) && (((String)localObject2).equals("com.android.incallui.OppoInCallActivity")))
        {
          ReceiverPowerOff.-get1(ReceiverPowerOff.this).removeCallbacks(ReceiverPowerOff.-get2(ReceiverPowerOff.this));
          ReceiverPowerOff.-get1(ReceiverPowerOff.this).postDelayed(ReceiverPowerOff.-get2(ReceiverPowerOff.this), 500L);
          return;
        }
        localObject1 = new Intent(ReceiverPowerOff.-get3(ReceiverPowerOff.this));
        ((Intent)localObject1).addCategory("android.intent.category.DEFAULT");
        ((Intent)localObject1).setFlags(268435456);
        ReceiverPowerOff.-get0(ReceiverPowerOff.this).startActivity((Intent)localObject1);
        return;
      }
      Log.e("ReceiverPowerOff", "mContext = " + ReceiverPowerOff.-get0(ReceiverPowerOff.this) + " mPoweroffAction = " + ReceiverPowerOff.-get3(ReceiverPowerOff.this));
    }
  };
  private String mPoweroffAction = null;
  
  private boolean isUsingTheme(Context paramContext)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "oem_is_using_theme", 0) == 1;
  }
  
  private void rememberShutdownRequestMissed(Context paramContext)
  {
    Settings.System.putInt(paramContext.getContentResolver(), "oem_shutdown_request_missed", 1);
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    String str2 = paramIntent.getAction();
    if (str2 == null) {
      return;
    }
    String str1 = "com.android.settings.Shutdown";
    if (((KeyguardManager)paramContext.getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
      str1 = "com.android.settings.ShutdownWhenLocked";
    }
    if (str2.equals("com.android.settings.POWER_OFF"))
    {
      if (System.currentTimeMillis() - paramIntent.getExtras().getLong("trigger_time") >= 60000L) {
        return;
      }
      if (mIsCalling)
      {
        mIsPoweroff = true;
        return;
      }
      Toast.makeText(paramContext, "phone want to turn off now !", 0).show();
      if (isUsingTheme(paramContext))
      {
        Log.i("ReceiverPowerOff", "time to shutdown when changing theme, so delay shutdown");
        rememberShutdownRequestMissed(paramContext);
        return;
      }
      paramIntent = new Intent(str1);
      paramIntent.setFlags(268435456);
      paramContext.startActivity(paramIntent);
    }
    do
    {
      do
      {
        do
        {
          return;
        } while (!str2.equals("android.intent.action.PHONE_STATE"));
        paramIntent = paramIntent.getStringExtra("state");
        if ((paramIntent.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)) || (paramIntent.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))) {
          mIsCalling = true;
        }
      } while (!paramIntent.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE));
      mIsCalling = false;
    } while (!mIsPoweroff);
    mIsPoweroff = false;
    this.mContext = paramContext;
    this.mPoweroffAction = str1;
    this.mHandler.removeCallbacks(this.mPowerOffPromptRunnable);
    this.mHandler.postDelayed(this.mPowerOffPromptRunnable, 500L);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\timer\timepower\ReceiverPowerOff.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */