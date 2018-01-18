package com.oneplus.settings.timer.timepower;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;

public class OPShutdownActivity
  extends Activity
{
  private static final int DIALOG = 1;
  private static final String TAG = "ShutdownActivity";
  public static CountDownTimer sCountDownTimer = null;
  private String mMessage;
  private int mSecondsCountdown;
  private TelephonyManager mTelephonyManager;
  
  private void cancelCountDownTimer()
  {
    if (sCountDownTimer != null)
    {
      Log.d("ShutdownActivity", "cancel sCountDownTimer");
      sCountDownTimer.cancel();
      sCountDownTimer = null;
    }
  }
  
  private void fireShutDown()
  {
    if (!SystemProperties.getBoolean("sys.debug.watchdog", false))
    {
      Intent localIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
      localIntent.putExtra("android.intent.extra.KEY_CONFIRM", false);
      localIntent.setFlags(8388608);
      localIntent.setFlags(268435456);
      startActivity(localIntent);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    PowerManager localPowerManager = (PowerManager)getSystemService("power");
    Log.d("ShutdownActivity", "screen is on ? ----- " + localPowerManager.isScreenOn());
    this.mTelephonyManager = ((TelephonyManager)getSystemService("phone"));
    getWindow().addFlags(4718592);
    if (paramBundle == null) {
      this.mSecondsCountdown = 11;
    }
    for (;;)
    {
      sCountDownTimer = new CountDownTimer(this.mSecondsCountdown * 1000, 1000L)
      {
        public void onFinish()
        {
          if (OPShutdownActivity.-get1(OPShutdownActivity.this).getCallState() != 0)
          {
            Log.d("ShutdownActivity", "phone is incall, countdown end");
            OPShutdownActivity.this.finish();
            return;
          }
          Log.d("ShutdownActivity", "count down timer arrived, shutdown phone");
          OPShutdownActivity.-wrap1(OPShutdownActivity.this);
          OPShutdownActivity.sCountDownTimer = null;
        }
        
        public void onTick(long paramAnonymousLong)
        {
          OPShutdownActivity.-set1(OPShutdownActivity.this, (int)(paramAnonymousLong / 1000L));
          if (OPShutdownActivity.-get0(OPShutdownActivity.this) > 1) {
            OPShutdownActivity.-set0(OPShutdownActivity.this, OPShutdownActivity.this.getString(2131690716, new Object[] { Integer.valueOf(OPShutdownActivity.-get0(OPShutdownActivity.this)) }));
          }
          for (;;)
          {
            Log.d("ShutdownActivity", "showDialog time = " + paramAnonymousLong / 1000L);
            OPShutdownActivity.this.showDialog(1);
            return;
            OPShutdownActivity.-set0(OPShutdownActivity.this, OPShutdownActivity.this.getString(2131690717, new Object[] { Integer.valueOf(OPShutdownActivity.-get0(OPShutdownActivity.this)) }));
          }
        }
      };
      Log.d("ShutdownActivity", "ShutdonwActivity onCreate");
      if (sCountDownTimer != null) {
        break;
      }
      finish();
      return;
      this.mSecondsCountdown = paramBundle.getInt("lefttime");
      this.mMessage = paramBundle.getString("message");
    }
    sCountDownTimer.start();
  }
  
  protected Dialog onCreateDialog(int paramInt)
  {
    Log.d("ShutdownActivity", "onCreateDialog");
    new AlertDialog.Builder(this).setCancelable(false).setIcon(17301543).setTitle("power off").setMessage(this.mMessage).setPositiveButton(17039379, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        OPShutdownActivity.-wrap0(OPShutdownActivity.this);
        OPShutdownActivity.-wrap1(OPShutdownActivity.this);
      }
    }).setNegativeButton(17039369, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        OPShutdownActivity.-wrap0(OPShutdownActivity.this);
        OPShutdownActivity.this.finish();
      }
    }).create();
  }
  
  protected void onPrepareDialog(int paramInt, Dialog paramDialog)
  {
    ((AlertDialog)paramDialog).setMessage(this.mMessage);
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("lefttime", this.mSecondsCountdown);
    paramBundle.putString("message", this.mMessage);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\timer\timepower\OPShutdownActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */