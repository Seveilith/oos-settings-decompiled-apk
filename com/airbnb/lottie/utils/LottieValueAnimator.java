package com.airbnb.lottie.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.support.annotation.FloatRange;

public class LottieValueAnimator
  extends ValueAnimator
{
  private boolean isReversed = false;
  private float maxProgress = 1.0F;
  private float minProgress = 0.0F;
  private long originalDuration;
  private float progress = 0.0F;
  private boolean systemAnimationsAreDisabled = false;
  
  public LottieValueAnimator()
  {
    setFloatValues(new float[] { 0.0F, 1.0F });
    addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        LottieValueAnimator.this.updateValues(LottieValueAnimator.this.minProgress, LottieValueAnimator.this.maxProgress);
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        LottieValueAnimator.this.updateValues(LottieValueAnimator.this.minProgress, LottieValueAnimator.this.maxProgress);
      }
    });
    addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        if (LottieValueAnimator.this.systemAnimationsAreDisabled) {
          return;
        }
        LottieValueAnimator.access$302(LottieValueAnimator.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      }
    });
  }
  
  private void setProgressInternal(@FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    float f;
    if (paramFloat < this.minProgress)
    {
      f = this.minProgress;
      this.progress = f;
      if (getDuration() > 0L) {
        break label81;
      }
    }
    label81:
    for (int i = 1;; i = 0)
    {
      if (i == 0) {
        setCurrentPlayTime(((f - this.minProgress) / (this.maxProgress - this.minProgress) * (float)getDuration()));
      }
      return;
      f = paramFloat;
      if (paramFloat <= this.maxProgress) {
        break;
      }
      f = this.maxProgress;
      break;
    }
  }
  
  public void forceUpdate()
  {
    setProgressInternal(getProgress());
  }
  
  public float getMaxProgress()
  {
    return this.maxProgress;
  }
  
  public float getMinProgress()
  {
    return this.minProgress;
  }
  
  public float getProgress()
  {
    return this.progress;
  }
  
  public void resumeAnimation()
  {
    float f = this.progress;
    start();
    setProgress(f);
  }
  
  public ValueAnimator setDuration(long paramLong)
  {
    this.originalDuration = paramLong;
    updateValues(this.minProgress, this.maxProgress);
    return this;
  }
  
  public void setIsReversed(boolean paramBoolean)
  {
    this.isReversed = paramBoolean;
    updateValues(this.minProgress, this.maxProgress);
  }
  
  public void setMaxProgress(float paramFloat)
  {
    this.maxProgress = paramFloat;
    updateValues(this.minProgress, paramFloat);
  }
  
  public void setMinProgress(float paramFloat)
  {
    this.minProgress = paramFloat;
    updateValues(paramFloat, this.maxProgress);
  }
  
  public void setProgress(@FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    if (this.progress == paramFloat) {
      return;
    }
    setProgressInternal(paramFloat);
  }
  
  public void start()
  {
    if (!this.systemAnimationsAreDisabled)
    {
      super.start();
      return;
    }
    setProgress(getMaxProgress());
    end();
  }
  
  public void systemAnimationsAreDisabled()
  {
    this.systemAnimationsAreDisabled = true;
  }
  
  public void updateValues(float paramFloat1, float paramFloat2)
  {
    float f1 = Math.min(paramFloat1, paramFloat2);
    paramFloat1 = Math.max(paramFloat1, paramFloat2);
    if (!this.isReversed)
    {
      paramFloat2 = f1;
      if (this.isReversed) {
        break label76;
      }
    }
    label76:
    for (float f2 = paramFloat1;; f2 = f1)
    {
      setFloatValues(new float[] { paramFloat2, f2 });
      super.setDuration(((float)this.originalDuration * (paramFloat1 - f1)));
      setProgress(getProgress());
      return;
      paramFloat2 = paramFloat1;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\utils\LottieValueAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */