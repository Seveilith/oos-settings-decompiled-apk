package com.oneplus.settings.timer.timepower;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import com.oneplus.settings.ui.OPTimerDialog;

public class OPPowerOffPromptActivity
  extends Activity
{
  private static final int MSG_CANCEL = 1000;
  private static final String TAG = "OPPowerOffPromptActivity";
  private static final int TYPE_NEGATIVE = 2;
  private static final int TYPE_POSITIVE = 1;
  private OPTimerDialog alertDialog;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      Button localButton1;
      Button localButton2;
      if (OPPowerOffPromptActivity.-get0(OPPowerOffPromptActivity.this) != null)
      {
        localButton1 = OPPowerOffPromptActivity.-get0(OPPowerOffPromptActivity.this).getNButton();
        localButton2 = OPPowerOffPromptActivity.-get0(OPPowerOffPromptActivity.this).getPButton();
        switch (paramAnonymousMessage.what)
        {
        }
      }
      do
      {
        do
        {
          do
          {
            do
            {
              return;
            } while (OPPowerOffPromptActivity.-get4(OPPowerOffPromptActivity.this));
            return;
            if (OPPowerOffPromptActivity.-get2(OPPowerOffPromptActivity.this) > 0)
            {
              paramAnonymousMessage = OPPowerOffPromptActivity.this;
              OPPowerOffPromptActivity.-set1(paramAnonymousMessage, OPPowerOffPromptActivity.-get2(paramAnonymousMessage) - 1);
              if (localButton1 != null)
              {
                paramAnonymousMessage = (String)localButton1.getText();
                localButton1.setText(OPPowerOffPromptActivity.-get0(OPPowerOffPromptActivity.this).getTimeText(paramAnonymousMessage, OPPowerOffPromptActivity.-get2(OPPowerOffPromptActivity.this)));
              }
              OPPowerOffPromptActivity.-get1(OPPowerOffPromptActivity.this).sendEmptyMessageDelayed(2, 1000L);
              return;
            }
          } while (localButton1 == null);
          if (localButton1.isEnabled())
          {
            localButton1.performClick();
            return;
          }
          localButton1.setEnabled(true);
          return;
          if (OPPowerOffPromptActivity.-get3(OPPowerOffPromptActivity.this) <= 0) {
            break;
          }
          paramAnonymousMessage = OPPowerOffPromptActivity.this;
          OPPowerOffPromptActivity.-set2(paramAnonymousMessage, OPPowerOffPromptActivity.-get3(paramAnonymousMessage) - 1);
          if (localButton2 != null) {
            OPPowerOffPromptActivity.-get0(OPPowerOffPromptActivity.this).setMessage(String.format(OPPowerOffPromptActivity.this.getResources().getString(2131690250), new Object[] { Integer.valueOf(OPPowerOffPromptActivity.-get3(OPPowerOffPromptActivity.this)) }));
          }
        } while (OPPowerOffPromptActivity.-get1(OPPowerOffPromptActivity.this) == null);
        OPPowerOffPromptActivity.-get1(OPPowerOffPromptActivity.this).sendEmptyMessageDelayed(1, 1000L);
        return;
      } while ((localButton2 == null) || (OPPowerOffPromptActivity.-get5(OPPowerOffPromptActivity.this)));
      if (localButton2.isEnabled())
      {
        int i = OPPowerOffPromptActivity.-get6(OPPowerOffPromptActivity.this).getCallState();
        if (i != 0)
        {
          Log.d("OPPowerOffPromptActivity", "Cancel auto shutdown while phone state is:" + i);
          OPPowerOffPromptActivity.-wrap0(OPPowerOffPromptActivity.this);
          return;
        }
        Log.d("OPPowerOffPromptActivity", "Perform auto shutdown");
        localButton2.performClick();
        return;
      }
      localButton2.setEnabled(true);
    }
  };
  private PowerManager.WakeLock mLock = null;
  private int mNegativeCount = 0;
  private int mPositiveCount = 60;
  private boolean mResume = false;
  private ProgressDialog mShutdownDialog;
  private boolean mStatus = false;
  private TelephonyManager mTelephonyManager;
  private PowerManager.WakeLock mWakeLock;
  private PowerManager pm = null;
  
  private void acquireWakeLock()
  {
    if (this.mWakeLock == null)
    {
      this.mWakeLock = ((PowerManager)getSystemService("power")).newWakeLock(536870913, "TimepowerWakeLock");
      this.mWakeLock.acquire();
    }
  }
  
  private void cancel()
  {
    sendBroadcast(new Intent("com.android.settings.POWER_CANCEL_OP_OFF"));
    this.mStatus = true;
    this.alertDialog.dismiss();
    finish();
  }
  
  private void dismissShutdownDialog()
  {
    if (this.mShutdownDialog != null) {
      this.mShutdownDialog.dismiss();
    }
  }
  
  private void raiseScreenUp()
  {
    this.pm = ((PowerManager)getSystemService("power"));
    this.mLock = this.pm.newWakeLock(805306374, "TimepowerWakeLock");
    this.mLock.acquire();
    this.mLock.release();
    this.mLock = null;
  }
  
  private void releaseWakeLock()
  {
    if ((this.mWakeLock != null) && (this.mWakeLock.isHeld()))
    {
      this.mWakeLock.release();
      this.mWakeLock = null;
    }
  }
  
  private void showDialog(Context paramContext)
  {
    if ((isFinishing()) || (isDestroyed())) {
      return;
    }
    if (this.mShutdownDialog != null) {
      this.mShutdownDialog.show();
    }
  }
  
  public void finish()
  {
    super.finish();
    releaseWakeLock();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    this.mTelephonyManager = ((TelephonyManager)getSystemService("phone"));
    if (paramBundle != null) {
      this.mPositiveCount = paramBundle.getInt("time");
    }
    getWindow().addFlags(1574016);
    super.onCreate(paramBundle);
    this.pm = ((PowerManager)getSystemService("power"));
    raiseScreenUp();
    this.alertDialog = new OPTimerDialog(this);
    this.alertDialog.setTitle(getResources().getString(2131690705));
    this.alertDialog.setMessage(String.format(getResources().getString(2131690250), new Object[] { Integer.valueOf(this.mPositiveCount) }));
    this.alertDialog.setPositiveButton(getResources().getString(2131690706), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (OPPowerOffPromptActivity.-get0(OPPowerOffPromptActivity.this) != null)
        {
          OPPowerOffPromptActivity.-get0(OPPowerOffPromptActivity.this).dismiss();
          OPPowerOffPromptActivity.-set0(OPPowerOffPromptActivity.this, null);
        }
        if (!SystemProperties.getBoolean("sys.debug.watchdog", false))
        {
          OPPowerOffPromptActivity.-wrap1(OPPowerOffPromptActivity.this, OPPowerOffPromptActivity.this);
          paramAnonymousDialogInterface = new Intent("com.android.settings.POWER_CONFIRM_OP_OFF");
          OPPowerOffPromptActivity.this.sendBroadcast(paramAnonymousDialogInterface);
        }
      }
    }, 60);
    this.alertDialog.setNegativeButton(getResources().getString(2131690707), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        OPPowerOffPromptActivity.-wrap0(OPPowerOffPromptActivity.this);
      }
    }, 10);
    this.alertDialog.show();
    this.alertDialog.setButtonType(-1, this.mPositiveCount, true);
    this.mHandler.sendEmptyMessageDelayed(1, 200L);
    this.mShutdownDialog = new ProgressDialog(this);
    this.mShutdownDialog.setMessage(getString(17039665));
    this.mShutdownDialog.setCancelable(false);
  }
  
  protected void onPause()
  {
    super.onPause();
    this.mResume = false;
    if ((this.alertDialog != null) && (this.alertDialog.isShowing())) {
      this.mHandler.obtainMessage(1000);
    }
    dismissShutdownDialog();
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("time", this.mPositiveCount);
  }
  
  protected void onStart()
  {
    super.onStart();
    this.mResume = true;
    acquireWakeLock();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\timer\timepower\OPPowerOffPromptActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */