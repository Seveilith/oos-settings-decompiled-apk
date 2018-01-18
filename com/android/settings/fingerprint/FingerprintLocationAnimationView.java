package com.android.settings.fingerprint;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.android.settings.Utils;

public class FingerprintLocationAnimationView
  extends View
  implements FingerprintFindSensorAnimation
{
  private static final long DELAY_BETWEEN_PHASE = 1000L;
  private static final float MAX_PULSE_ALPHA = 0.15F;
  private ValueAnimator mAlphaAnimator;
  private final Paint mDotPaint = new Paint();
  private final int mDotRadius = getResources().getDimensionPixelSize(2131755571);
  private final Interpolator mFastOutSlowInInterpolator;
  private final float mFractionCenterX = getResources().getFraction(2131886080, 1, 1);
  private final float mFractionCenterY = getResources().getFraction(2131886081, 1, 1);
  private final Interpolator mLinearOutSlowInInterpolator;
  private final int mMaxPulseRadius = getResources().getDimensionPixelSize(2131755572);
  private final Paint mPulsePaint = new Paint();
  private float mPulseRadius;
  private ValueAnimator mRadiusAnimator;
  private final Runnable mStartPhaseRunnable = new Runnable()
  {
    public void run()
    {
      FingerprintLocationAnimationView.-wrap0(FingerprintLocationAnimationView.this);
    }
  };
  
  public FingerprintLocationAnimationView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    int i = Utils.getColorAccent(paramContext);
    this.mDotPaint.setAntiAlias(true);
    this.mPulsePaint.setAntiAlias(true);
    this.mDotPaint.setColor(i);
    this.mPulsePaint.setColor(i);
    this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(paramContext, 17563662);
    this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(paramContext, 17563662);
  }
  
  private void drawDot(Canvas paramCanvas)
  {
    paramCanvas.drawCircle(getCenterX(), getCenterY(), this.mDotRadius, this.mDotPaint);
  }
  
  private void drawPulse(Canvas paramCanvas)
  {
    paramCanvas.drawCircle(getCenterX(), getCenterY(), this.mPulseRadius, this.mPulsePaint);
  }
  
  private float getCenterX()
  {
    return getWidth() / 2;
  }
  
  private float getCenterY()
  {
    return getHeight() / 2;
  }
  
  private void startAlphaAnimation()
  {
    this.mPulsePaint.setAlpha(38);
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.15F, 0.0F });
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        FingerprintLocationAnimationView.-get0(FingerprintLocationAnimationView.this).setAlpha((int)(((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue() * 255.0F));
        FingerprintLocationAnimationView.this.invalidate();
      }
    });
    localValueAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        FingerprintLocationAnimationView.-set0(FingerprintLocationAnimationView.this, null);
      }
    });
    localValueAnimator.setDuration(750L);
    localValueAnimator.setInterpolator(this.mFastOutSlowInInterpolator);
    localValueAnimator.setStartDelay(250L);
    localValueAnimator.start();
    this.mAlphaAnimator = localValueAnimator;
  }
  
  private void startPhase()
  {
    startRadiusAnimation();
    startAlphaAnimation();
  }
  
  private void startRadiusAnimation()
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, this.mMaxPulseRadius });
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        FingerprintLocationAnimationView.-set1(FingerprintLocationAnimationView.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
        FingerprintLocationAnimationView.this.invalidate();
      }
    });
    localValueAnimator.addListener(new AnimatorListenerAdapter()
    {
      boolean mCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.mCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        FingerprintLocationAnimationView.-set2(FingerprintLocationAnimationView.this, null);
        if (!this.mCancelled) {
          FingerprintLocationAnimationView.this.postDelayed(FingerprintLocationAnimationView.-get1(FingerprintLocationAnimationView.this), 1000L);
        }
      }
    });
    localValueAnimator.setDuration(1000L);
    localValueAnimator.setInterpolator(this.mLinearOutSlowInInterpolator);
    localValueAnimator.start();
    this.mRadiusAnimator = localValueAnimator;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    drawPulse(paramCanvas);
    drawDot(paramCanvas);
  }
  
  public void pauseAnimation()
  {
    stopAnimation();
  }
  
  public void startAnimation()
  {
    startPhase();
  }
  
  public void stopAnimation()
  {
    removeCallbacks(this.mStartPhaseRunnable);
    if (this.mRadiusAnimator != null) {
      this.mRadiusAnimator.cancel();
    }
    if (this.mAlphaAnimator != null) {
      this.mAlphaAnimator.cancel();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fingerprint\FingerprintLocationAnimationView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */