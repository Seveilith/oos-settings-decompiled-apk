package com.oneplus.settings.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

public class OPProgressDialog
  extends ProgressDialog
{
  private static final int MSG_DELAYSHOW = 0;
  private static final int MSG_TIMEOUT = 1;
  public static final String TAG = "OPProgressDialog";
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      }
      do
      {
        return;
        ((OPProgressDialog)paramAnonymousMessage.obj).show();
        return;
      } while (OPProgressDialog.-get1(OPProgressDialog.this) == null);
      OPProgressDialog.-get1(OPProgressDialog.this).onTimeOut(OPProgressDialog.this);
      OPProgressDialog.this.dismiss();
    }
  };
  private long mShowDelayTime = 0L;
  private long mTimeOut = 0L;
  private OnTimeOutListener mTimeOutListener = null;
  private Timer mTimer = null;
  
  public OPProgressDialog(Context paramContext)
  {
    super(paramContext);
  }
  
  public void dismiss()
  {
    this.mHandler.removeMessages(0);
    super.dismiss();
  }
  
  public void onStart()
  {
    super.onStart();
    if (this.mTimeOut > 0L)
    {
      this.mTimer = new Timer();
      TimerTask local2 = new TimerTask()
      {
        public void run()
        {
          Log.d("OPProgressDialog", "timerOutTast......");
          OPProgressDialog.-get0(OPProgressDialog.this).sendEmptyMessage(1);
        }
      };
      this.mTimer.schedule(local2, this.mTimeOut);
    }
  }
  
  protected void onStop()
  {
    super.onStop();
    if (this.mTimer != null)
    {
      this.mTimer.cancel();
      this.mTimer = null;
    }
  }
  
  public void setShowDelayTime(long paramLong)
  {
    this.mShowDelayTime = paramLong;
  }
  
  public void setTimeOut(long paramLong, OnTimeOutListener paramOnTimeOutListener)
  {
    this.mTimeOut = paramLong;
    if (paramOnTimeOutListener != null) {
      this.mTimeOutListener = paramOnTimeOutListener;
    }
  }
  
  public void showDelay()
  {
    this.mHandler.removeMessages(0);
    Message localMessage = this.mHandler.obtainMessage(0);
    localMessage.obj = this;
    this.mHandler.sendMessageDelayed(localMessage, this.mShowDelayTime);
  }
  
  public void showDelay(long paramLong)
  {
    this.mShowDelayTime = paramLong;
    showDelay();
  }
  
  public static abstract interface OnTimeOutListener
  {
    public abstract void onTimeOut(OPProgressDialog paramOPProgressDialog);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPProgressDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */