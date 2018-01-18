package com.android.settingslib.animation;

import android.view.animation.Interpolator;

public abstract interface AppearAnimationCreator<T>
{
  public abstract void createAnimation(T paramT, long paramLong1, long paramLong2, float paramFloat, boolean paramBoolean, Interpolator paramInterpolator, Runnable paramRunnable);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\animation\AppearAnimationCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */