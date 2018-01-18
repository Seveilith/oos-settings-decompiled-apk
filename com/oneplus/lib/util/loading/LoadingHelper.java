package com.oneplus.lib.util.loading;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

public abstract class LoadingHelper
{
  private static final long PROMPT_MIN_SHOW_TIME_DEFAULT = 500L;
  private static final long WILL_SHOW_PROMPT_TIME_DEFAULT = 300L;
  private static Handler mHandler = new Handler(Looper.getMainLooper());
  private long mProgreeMinShowTime = 500L;
  private Object mProgreeView;
  private Runnable mShowProgreeRunnable;
  private long mShowProgreeTime;
  private long mWillShowProgreeTime = 300L;
  
  private void doFinish(final FinishShowCallback paramFinishShowCallback, final boolean paramBoolean)
  {
    if (Looper.myLooper() == Looper.getMainLooper())
    {
      if (paramBoolean) {
        hideProgree(this.mProgreeView);
      }
      if (paramFinishShowCallback != null) {
        paramFinishShowCallback.finish(true);
      }
      return;
    }
    mHandler.post(new Runnable()
    {
      public void run()
      {
        if (paramBoolean) {
          LoadingHelper.this.hideProgree(LoadingHelper.-get0(LoadingHelper.this));
        }
        if (paramFinishShowCallback != null) {
          paramFinishShowCallback.finish(true);
        }
      }
    });
  }
  
  public void beginShowProgress()
  {
    this.mShowProgreeRunnable = new Runnable()
    {
      public void run()
      {
        LoadingHelper.-set1(LoadingHelper.this, null);
        LoadingHelper.-set0(LoadingHelper.this, LoadingHelper.this.showProgree());
        LoadingHelper.-set2(LoadingHelper.this, SystemClock.elapsedRealtime());
      }
    };
    mHandler.postDelayed(this.mShowProgreeRunnable, this.mWillShowProgreeTime);
  }
  
  public void finishShowProgress(final FinishShowCallback paramFinishShowCallback)
  {
    if (this.mShowProgreeRunnable != null)
    {
      mHandler.removeCallbacks(this.mShowProgreeRunnable);
      doFinish(paramFinishShowCallback, false);
      return;
    }
    long l1 = SystemClock.elapsedRealtime();
    long l2 = this.mShowProgreeTime;
    l1 = this.mProgreeMinShowTime - (l1 - l2);
    if (l1 > 0L)
    {
      mHandler.postDelayed(new Runnable()
      {
        public void run()
        {
          LoadingHelper.-wrap0(LoadingHelper.this, paramFinishShowCallback, true);
        }
      }, l1);
      return;
    }
    doFinish(paramFinishShowCallback, true);
  }
  
  protected abstract void hideProgree(Object paramObject);
  
  public LoadingHelper setProgreeMinShowTime(long paramLong)
  {
    this.mProgreeMinShowTime = paramLong;
    return this;
  }
  
  public LoadingHelper setWillShowProgreeTime(long paramLong)
  {
    this.mWillShowProgreeTime = paramLong;
    return this;
  }
  
  protected abstract Object showProgree();
  
  public static abstract interface FinishShowCallback
  {
    public abstract void finish(boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\util\loading\LoadingHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */