package com.oneplus.settings.timer.timepower;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.android.internal.app.AlertActivity;
import java.util.List;

public class PowerOffPromptActivity
  extends AlertActivity
  implements DialogInterface.OnClickListener
{
  static final String ACTION_TIMER_SHUTDOWN = "com.android.settings.Shutdown";
  static final String ACTION_TIMER_SHUTDOWN_LOCKED = "com.android.settings.ShutdownWhenLocked";
  private static final String EXTRA_IS_MISSED_REQUEST = "isMissedRequest";
  private static final int MSG_SHUTDOWN_NOW = 1;
  private static final String TAG = "PowerOffPromptActivity";
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      PowerOffPromptActivity.-wrap0(PowerOffPromptActivity.this);
    }
  };
  private boolean mIsCurrentLocked = false;
  private PowerManager.WakeLock mLock = null;
  private final Runnable mMoveToFrontRunnable = new Runnable()
  {
    public void run()
    {
      ActivityManager localActivityManager = (ActivityManager)PowerOffPromptActivity.this.getSystemService("activity");
      Object localObject = ((ActivityManager.RunningTaskInfo)localActivityManager.getRunningTasks(1).get(0)).topActivity;
      String str = ((ComponentName)localObject).getPackageName();
      localObject = ((ComponentName)localObject).getClassName();
      Log.d("PowerOffPromptActivity", "pkg:" + str);
      Log.d("PowerOffPromptActivity", "cls:" + (String)localObject);
      Log.d("PowerOffPromptActivity", "taskId:" + PowerOffPromptActivity.this.getTaskId());
      if (!((String)localObject).equals(PowerOffPromptActivity.NewStylePowerOffPromptActivity.class.getName())) {
        localActivityManager.moveTaskToFront(PowerOffPromptActivity.this.getTaskId(), 1);
      }
    }
  };
  private BroadcastReceiver mStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getStringExtra("state");
      if (paramAnonymousContext.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
        PowerOffPromptActivity.-get0(PowerOffPromptActivity.this).sendEmptyMessageDelayed(1, 60000L);
      }
      if ((paramAnonymousContext.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) || (paramAnonymousContext.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
        PowerOffPromptActivity.-get0(PowerOffPromptActivity.this).removeMessages(1);
      }
    }
  };
  private PowerManager pm = null;
  
  private void beginShutdown()
  {
    if (this.mLock != null)
    {
      this.mLock.release();
      this.mLock = null;
    }
    this.mLock = this.pm.newWakeLock(268435466, "TimepowerWakeLock");
    this.mLock.acquire();
    Intent localIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
    localIntent.putExtra("android.intent.extra.KEY_CONFIRM", false);
    localIntent.setFlags(268435456);
    startActivity(localIntent);
    finish();
  }
  
  private void lightScreen()
  {
    WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(2004, 7865472);
    getWindow().setAttributes(localLayoutParams);
    getWindow().setCloseOnTouchOutside(false);
    if (this.mIsCurrentLocked) {
      getWindow().setBackgroundDrawableResource(17170445);
    }
  }
  
  public void onBackPressed()
  {
    Log.i("PowerOffPromptActivity", "onBackPressed");
    this.mHandler.removeCallbacks(this.mMoveToFrontRunnable);
    this.mHandler.postDelayed(this.mMoveToFrontRunnable, 5000L);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case -1: 
      beginShutdown();
      return;
    }
    boolean bool = false;
    paramDialogInterface = getIntent();
    if (paramDialogInterface.hasExtra("isMissedRequest")) {
      bool = Boolean.valueOf(paramDialogInterface.getBooleanExtra("isMissedRequest", false)).booleanValue();
    }
    if (bool) {
      System.exit(0);
    }
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent().getAction();
    if ("com.android.settings.Shutdown".equals(paramBundle)) {
      this.mIsCurrentLocked = false;
    }
    for (;;)
    {
      if (this.mIsCurrentLocked) {
        lightScreen();
      }
      this.pm = ((PowerManager)getSystemService("power"));
      this.mLock = this.pm.newWakeLock(805306369, "TimepowerWakeLock");
      this.mLock.acquire();
      paramBundle = new IntentFilter();
      paramBundle.addAction("android.intent.action.PHONE_STATE");
      registerReceiver(this.mStateReceiver, paramBundle);
      paramBundle = this.mAlertParams;
      paramBundle.mTitle = "Power off";
      paramBundle.mPositiveButtonText = getString(17039370);
      paramBundle.mPositiveButtonListener = this;
      paramBundle.mNegativeButtonText = getString(17039360);
      paramBundle.mNegativeButtonListener = this;
      paramBundle.mMessage = getString(2131690704);
      setupAlert();
      getWindow().setCloseOnTouchOutside(false);
      this.mHandler.sendEmptyMessageDelayed(1, 60000L);
      return;
      if ("com.android.settings.ShutdownWhenLocked".equals(paramBundle)) {
        this.mIsCurrentLocked = true;
      }
    }
  }
  
  protected void onDestroy()
  {
    Log.i("PowerOffPromptActivity", "onDestroy");
    super.onDestroy();
    sendBroadcast(new Intent("com.android.settings.SET_CHANGED"));
    this.mHandler.removeMessages(1);
    this.mHandler.removeCallbacks(this.mMoveToFrontRunnable);
    if (this.mLock != null)
    {
      this.mLock.release();
      this.mLock = null;
    }
    if (this.mStateReceiver != null) {
      unregisterReceiver(this.mStateReceiver);
    }
  }
  
  protected void onUserLeaveHint()
  {
    Log.i("PowerOffPromptActivity", "onUserLeaveHint");
    super.onUserLeaveHint();
    this.mHandler.removeCallbacks(this.mMoveToFrontRunnable);
    this.mHandler.postDelayed(this.mMoveToFrontRunnable, 5000L);
  }
  
  public static class NewStylePowerOffPromptActivity
    extends PowerOffPromptActivity
  {}
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\timer\timepower\PowerOffPromptActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */