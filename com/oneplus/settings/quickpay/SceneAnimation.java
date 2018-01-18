package com.oneplus.settings.quickpay;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class SceneAnimation
{
  private final int MSG_PLAY = 0;
  private final int MSG_STOP = 1;
  private int mDuration;
  private int[] mDurations;
  private int[] mFrameRess;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        SceneAnimation.-get1(SceneAnimation.this).setImageResource(SceneAnimation.-get0(SceneAnimation.this)[0]);
        return;
      }
      int i = paramAnonymousMessage.arg1;
      SceneAnimation.-get1(SceneAnimation.this).setImageResource(SceneAnimation.-get0(SceneAnimation.this)[i]);
      if (i == SceneAnimation.-get2(SceneAnimation.this))
      {
        SceneAnimation.-wrap0(SceneAnimation.this, 0);
        return;
      }
      SceneAnimation.-wrap0(SceneAnimation.this, i + 1);
    }
  };
  private ImageView mImageView;
  private int mLastFrameNo;
  private boolean starting = false;
  
  public SceneAnimation(ImageView paramImageView, int[] paramArrayOfInt, int paramInt)
  {
    this.mImageView = paramImageView;
    this.mFrameRess = paramArrayOfInt;
    this.mDuration = paramInt;
    this.mLastFrameNo = (paramArrayOfInt.length - 1);
    this.mImageView.setImageResource(this.mFrameRess[0]);
    stop();
  }
  
  public SceneAnimation(ImageView paramImageView, int[] paramArrayOfInt, int paramInt, long paramLong)
  {
    this.mImageView = paramImageView;
    this.mFrameRess = paramArrayOfInt;
    this.mDuration = paramInt;
    this.mLastFrameNo = (paramArrayOfInt.length - 1);
    this.mImageView.setImageResource(this.mFrameRess[0]);
    stop();
  }
  
  public SceneAnimation(ImageView paramImageView, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    this.mImageView = paramImageView;
    this.mFrameRess = paramArrayOfInt1;
    this.mDurations = paramArrayOfInt2;
    this.mLastFrameNo = (paramArrayOfInt1.length - 1);
    this.mImageView.setImageResource(this.mFrameRess[0]);
    stop();
  }
  
  private void play(int paramInt)
  {
    if (!this.starting)
    {
      this.mHandler.removeMessages(0);
      return;
    }
    Message localMessage = this.mHandler.obtainMessage();
    localMessage.arg1 = paramInt;
    localMessage.what = 0;
    this.mHandler.sendMessageDelayed(localMessage, this.mDuration);
  }
  
  public boolean isStarting()
  {
    return this.starting;
  }
  
  public void play()
  {
    this.starting = true;
    play(0);
  }
  
  public void stop()
  {
    this.starting = false;
    this.mHandler.removeMessages(0);
    this.mHandler.sendEmptyMessage(1);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\quickpay\SceneAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */