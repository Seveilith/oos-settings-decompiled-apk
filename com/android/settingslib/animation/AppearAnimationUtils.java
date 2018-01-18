package com.android.settingslib.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.view.RenderNodeAnimator;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.android.settingslib.R.dimen;

public class AppearAnimationUtils
  implements AppearAnimationCreator<View>
{
  public static final long DEFAULT_APPEAR_DURATION = 220L;
  protected boolean mAppearing;
  protected final float mDelayScale;
  private final long mDuration;
  private final Interpolator mInterpolator;
  private final AppearAnimationProperties mProperties = new AppearAnimationProperties();
  protected RowTranslationScaler mRowTranslationScaler;
  private final float mStartTranslation;
  
  public AppearAnimationUtils(Context paramContext)
  {
    this(paramContext, 220L, 1.0F, 1.0F, AnimationUtils.loadInterpolator(paramContext, 17563662));
  }
  
  public AppearAnimationUtils(Context paramContext, long paramLong, float paramFloat1, float paramFloat2, Interpolator paramInterpolator)
  {
    this.mInterpolator = paramInterpolator;
    this.mStartTranslation = (paramContext.getResources().getDimensionPixelOffset(R.dimen.appear_y_translation_start) * paramFloat1);
    this.mDelayScale = paramFloat2;
    this.mDuration = paramLong;
    this.mAppearing = true;
  }
  
  private <T> AppearAnimationProperties getDelays(T[] paramArrayOfT)
  {
    long l1 = -1L;
    this.mProperties.maxDelayColIndex = -1;
    this.mProperties.maxDelayRowIndex = -1;
    this.mProperties.delays = new long[paramArrayOfT.length][];
    int i = 0;
    while (i < paramArrayOfT.length)
    {
      this.mProperties.delays[i] = new long[1];
      long l3 = calculateDelay(i, 0);
      this.mProperties.delays[i][0] = l3;
      long l2 = l1;
      if (paramArrayOfT[i] != null)
      {
        l2 = l1;
        if (l3 > l1)
        {
          l2 = l3;
          this.mProperties.maxDelayColIndex = 0;
          this.mProperties.maxDelayRowIndex = i;
        }
      }
      i += 1;
      l1 = l2;
    }
    return this.mProperties;
  }
  
  private <T> AppearAnimationProperties getDelays(T[][] paramArrayOfT)
  {
    long l1 = -1L;
    this.mProperties.maxDelayColIndex = -1;
    this.mProperties.maxDelayRowIndex = -1;
    this.mProperties.delays = new long[paramArrayOfT.length][];
    int i = 0;
    while (i < paramArrayOfT.length)
    {
      T[] arrayOfT = paramArrayOfT[i];
      this.mProperties.delays[i] = new long[arrayOfT.length];
      int j = 0;
      while (j < arrayOfT.length)
      {
        long l3 = calculateDelay(i, j);
        this.mProperties.delays[i][j] = l3;
        long l2 = l1;
        if (paramArrayOfT[i][j] != null)
        {
          l2 = l1;
          if (l3 > l1)
          {
            l2 = l3;
            this.mProperties.maxDelayColIndex = j;
            this.mProperties.maxDelayRowIndex = i;
          }
        }
        j += 1;
        l1 = l2;
      }
      i += 1;
    }
    return this.mProperties;
  }
  
  private <T> void startAnimations(AppearAnimationProperties paramAppearAnimationProperties, T[] paramArrayOfT, Runnable paramRunnable, AppearAnimationCreator<T> paramAppearAnimationCreator)
  {
    if ((paramAppearAnimationProperties.maxDelayRowIndex == -1) || (paramAppearAnimationProperties.maxDelayColIndex == -1))
    {
      paramRunnable.run();
      return;
    }
    int i = 0;
    if (i < paramAppearAnimationProperties.delays.length)
    {
      long l1 = paramAppearAnimationProperties.delays[i][0];
      T ? = null;
      Object localObject = ?;
      if (paramAppearAnimationProperties.maxDelayRowIndex == i)
      {
        localObject = ?;
        if (paramAppearAnimationProperties.maxDelayColIndex == 0) {
          localObject = paramRunnable;
        }
      }
      float f;
      label102:
      long l2;
      if (this.mRowTranslationScaler != null)
      {
        f = this.mRowTranslationScaler.getRowTranslationScale(i, paramAppearAnimationProperties.delays.length);
        f *= this.mStartTranslation;
        ? = paramArrayOfT[i];
        l2 = this.mDuration;
        if (!this.mAppearing) {
          break label170;
        }
      }
      for (;;)
      {
        paramAppearAnimationCreator.createAnimation(?, l1, l2, f, this.mAppearing, this.mInterpolator, (Runnable)localObject);
        i += 1;
        break;
        f = 1.0F;
        break label102;
        label170:
        f = -f;
      }
    }
  }
  
  private <T> void startAnimations(AppearAnimationProperties paramAppearAnimationProperties, T[][] paramArrayOfT, Runnable paramRunnable, AppearAnimationCreator<T> paramAppearAnimationCreator)
  {
    if ((paramAppearAnimationProperties.maxDelayRowIndex == -1) || (paramAppearAnimationProperties.maxDelayColIndex == -1))
    {
      paramRunnable.run();
      return;
    }
    int i = 0;
    while (i < paramAppearAnimationProperties.delays.length)
    {
      long[] arrayOfLong = paramAppearAnimationProperties.delays[i];
      float f2;
      int j;
      label82:
      long l1;
      T ?;
      Object localObject;
      long l2;
      if (this.mRowTranslationScaler != null)
      {
        f1 = this.mRowTranslationScaler.getRowTranslationScale(i, paramAppearAnimationProperties.delays.length);
        f2 = f1 * this.mStartTranslation;
        j = 0;
        if (j >= arrayOfLong.length) {
          break label203;
        }
        l1 = arrayOfLong[j];
        ? = null;
        localObject = ?;
        if (paramAppearAnimationProperties.maxDelayRowIndex == i)
        {
          localObject = ?;
          if (paramAppearAnimationProperties.maxDelayColIndex == j) {
            localObject = paramRunnable;
          }
        }
        ? = paramArrayOfT[i][j];
        l2 = this.mDuration;
        if (!this.mAppearing) {
          break label195;
        }
      }
      label195:
      for (float f1 = f2;; f1 = -f2)
      {
        paramAppearAnimationCreator.createAnimation(?, l1, l2, f1, this.mAppearing, this.mInterpolator, (Runnable)localObject);
        j += 1;
        break label82;
        f1 = 1.0F;
        break;
      }
      label203:
      i += 1;
    }
  }
  
  public static void startTranslationYAnimation(View paramView, long paramLong1, long paramLong2, float paramFloat, Interpolator paramInterpolator)
  {
    RenderNodeAnimator localRenderNodeAnimator;
    if (paramView.isHardwareAccelerated())
    {
      localRenderNodeAnimator = new RenderNodeAnimator(1, paramFloat);
      localRenderNodeAnimator.setTarget(paramView);
    }
    for (paramView = localRenderNodeAnimator;; paramView = ObjectAnimator.ofFloat(paramView, View.TRANSLATION_Y, new float[] { paramView.getTranslationY(), paramFloat }))
    {
      paramView.setInterpolator(paramInterpolator);
      paramView.setDuration(paramLong2);
      paramView.setStartDelay(paramLong1);
      paramView.start();
      return;
    }
  }
  
  protected long calculateDelay(int paramInt1, int paramInt2)
  {
    return ((paramInt1 * 40 + paramInt2 * (Math.pow(paramInt1, 0.4D) + 0.4D) * 20.0D) * this.mDelayScale);
  }
  
  public void createAnimation(final View paramView, long paramLong1, long paramLong2, float paramFloat, boolean paramBoolean, Interpolator paramInterpolator, final Runnable paramRunnable)
  {
    float f;
    label27:
    label41:
    Object localObject;
    if (paramView != null)
    {
      if (!paramBoolean) {
        break label160;
      }
      f = 0.0F;
      paramView.setAlpha(f);
      if (!paramBoolean) {
        break label166;
      }
      f = paramFloat;
      paramView.setTranslationY(f);
      if (!paramBoolean) {
        break label172;
      }
      f = 1.0F;
      if (!paramView.isHardwareAccelerated()) {
        break label178;
      }
      localObject = new RenderNodeAnimator(11, f);
      ((RenderNodeAnimator)localObject).setTarget(paramView);
      label67:
      ((Animator)localObject).setInterpolator(paramInterpolator);
      ((Animator)localObject).setDuration(paramLong2);
      ((Animator)localObject).setStartDelay(paramLong1);
      if (paramView.hasOverlappingRendering())
      {
        paramView.setLayerType(2, null);
        ((Animator)localObject).addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            paramView.setLayerType(0, null);
          }
        });
      }
      if (paramRunnable != null) {
        ((Animator)localObject).addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            paramRunnable.run();
          }
        });
      }
      ((Animator)localObject).start();
      if (!paramBoolean) {
        break label205;
      }
      paramFloat = 0.0F;
    }
    label160:
    label166:
    label172:
    label178:
    label205:
    for (;;)
    {
      startTranslationYAnimation(paramView, paramLong1, paramLong2, paramFloat, paramInterpolator);
      return;
      f = 1.0F;
      break;
      f = 0.0F;
      break label27;
      f = 0.0F;
      break label41;
      localObject = ObjectAnimator.ofFloat(paramView, View.ALPHA, new float[] { paramView.getAlpha(), f });
      break label67;
    }
  }
  
  public Interpolator getInterpolator()
  {
    return this.mInterpolator;
  }
  
  public float getStartTranslation()
  {
    return this.mStartTranslation;
  }
  
  public void startAnimation(View[] paramArrayOfView, Runnable paramRunnable)
  {
    startAnimation(paramArrayOfView, paramRunnable, this);
  }
  
  public <T> void startAnimation(T[] paramArrayOfT, Runnable paramRunnable, AppearAnimationCreator<T> paramAppearAnimationCreator)
  {
    startAnimations(getDelays(paramArrayOfT), paramArrayOfT, paramRunnable, paramAppearAnimationCreator);
  }
  
  public void startAnimation2d(View[][] paramArrayOfView, Runnable paramRunnable)
  {
    startAnimation2d(paramArrayOfView, paramRunnable, this);
  }
  
  public <T> void startAnimation2d(T[][] paramArrayOfT, Runnable paramRunnable, AppearAnimationCreator<T> paramAppearAnimationCreator)
  {
    startAnimations(getDelays(paramArrayOfT), paramArrayOfT, paramRunnable, paramAppearAnimationCreator);
  }
  
  public class AppearAnimationProperties
  {
    public long[][] delays;
    public int maxDelayColIndex;
    public int maxDelayRowIndex;
    
    public AppearAnimationProperties() {}
  }
  
  public static abstract interface RowTranslationScaler
  {
    public abstract float getRowTranslationScale(int paramInt1, int paramInt2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\animation\AppearAnimationUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */