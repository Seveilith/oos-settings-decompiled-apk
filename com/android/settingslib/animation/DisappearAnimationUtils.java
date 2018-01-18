package com.android.settingslib.animation;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class DisappearAnimationUtils
  extends AppearAnimationUtils
{
  private static final AppearAnimationUtils.RowTranslationScaler ROW_TRANSLATION_SCALER = new AppearAnimationUtils.RowTranslationScaler()
  {
    public float getRowTranslationScale(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      return (float)(Math.pow(paramAnonymousInt2 - paramAnonymousInt1, 2.0D) / paramAnonymousInt2);
    }
  };
  
  public DisappearAnimationUtils(Context paramContext)
  {
    this(paramContext, 220L, 1.0F, 1.0F, AnimationUtils.loadInterpolator(paramContext, 17563663));
  }
  
  public DisappearAnimationUtils(Context paramContext, long paramLong, float paramFloat1, float paramFloat2, Interpolator paramInterpolator)
  {
    this(paramContext, paramLong, paramFloat1, paramFloat2, paramInterpolator, ROW_TRANSLATION_SCALER);
  }
  
  public DisappearAnimationUtils(Context paramContext, long paramLong, float paramFloat1, float paramFloat2, Interpolator paramInterpolator, AppearAnimationUtils.RowTranslationScaler paramRowTranslationScaler)
  {
    super(paramContext, paramLong, paramFloat1, paramFloat2, paramInterpolator);
    this.mRowTranslationScaler = paramRowTranslationScaler;
    this.mAppearing = false;
  }
  
  protected long calculateDelay(int paramInt1, int paramInt2)
  {
    return ((paramInt1 * 60 + paramInt2 * (Math.pow(paramInt1, 0.4D) + 0.4D) * 10.0D) * this.mDelayScale);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\animation\DisappearAnimationUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */