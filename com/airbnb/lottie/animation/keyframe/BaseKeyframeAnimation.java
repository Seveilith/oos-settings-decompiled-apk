package com.airbnb.lottie.animation.keyframe;

import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.animation.Interpolator;
import com.airbnb.lottie.animation.Keyframe;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseKeyframeAnimation<K, A>
{
  @Nullable
  private Keyframe<K> cachedKeyframe;
  private boolean isDiscrete = false;
  private final List<? extends Keyframe<K>> keyframes;
  final List<AnimationListener> listeners = new ArrayList();
  private float progress = 0.0F;
  
  BaseKeyframeAnimation(List<? extends Keyframe<K>> paramList)
  {
    this.keyframes = paramList;
  }
  
  private Keyframe<K> getCurrentKeyframe()
  {
    int i = 0;
    if (!this.keyframes.isEmpty()) {
      if (this.cachedKeyframe != null) {
        break label66;
      }
    }
    label66:
    while (!this.cachedKeyframe.containsProgress(this.progress))
    {
      Keyframe localKeyframe2 = (Keyframe)this.keyframes.get(0);
      localKeyframe1 = localKeyframe2;
      if (this.progress >= localKeyframe2.getStartProgress()) {
        break;
      }
      this.cachedKeyframe = localKeyframe2;
      return localKeyframe2;
      throw new IllegalStateException("There are no keyframes");
    }
    return this.cachedKeyframe;
    Keyframe localKeyframe1 = (Keyframe)this.keyframes.get(i);
    i += 1;
    if (localKeyframe1.containsProgress(this.progress)) {}
    for (;;)
    {
      this.cachedKeyframe = localKeyframe1;
      return localKeyframe1;
      if (i < this.keyframes.size()) {
        break;
      }
    }
  }
  
  private float getCurrentKeyframeProgress()
  {
    if (!this.isDiscrete)
    {
      Keyframe localKeyframe = getCurrentKeyframe();
      if (!localKeyframe.isStatic())
      {
        float f1 = this.progress;
        float f2 = localKeyframe.getStartProgress();
        float f3 = localKeyframe.getEndProgress();
        float f4 = localKeyframe.getStartProgress();
        return localKeyframe.interpolator.getInterpolation((f1 - f2) / (f3 - f4));
      }
    }
    else
    {
      return 0.0F;
    }
    return 0.0F;
  }
  
  @FloatRange(from=0.0D, to=1.0D)
  private float getEndProgress()
  {
    if (!this.keyframes.isEmpty()) {
      return ((Keyframe)this.keyframes.get(this.keyframes.size() - 1)).getEndProgress();
    }
    return 1.0F;
  }
  
  @FloatRange(from=0.0D, to=1.0D)
  private float getStartDelayProgress()
  {
    if (!this.keyframes.isEmpty()) {
      return ((Keyframe)this.keyframes.get(0)).getStartProgress();
    }
    return 0.0F;
  }
  
  public void addUpdateListener(AnimationListener paramAnimationListener)
  {
    this.listeners.add(paramAnimationListener);
  }
  
  public float getProgress()
  {
    return this.progress;
  }
  
  public A getValue()
  {
    return (A)getValue(getCurrentKeyframe(), getCurrentKeyframeProgress());
  }
  
  abstract A getValue(Keyframe<K> paramKeyframe, float paramFloat);
  
  public void setIsDiscrete()
  {
    this.isDiscrete = true;
  }
  
  public void setProgress(@FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    float f;
    if (paramFloat < getStartDelayProgress()) {
      f = 0.0F;
    }
    while (f == this.progress)
    {
      return;
      f = paramFloat;
      if (paramFloat > getEndProgress()) {
        f = 1.0F;
      }
    }
    this.progress = f;
    int i = 0;
    for (;;)
    {
      if (i >= this.listeners.size()) {
        return;
      }
      ((AnimationListener)this.listeners.get(i)).onValueChanged();
      i += 1;
    }
  }
  
  public static abstract interface AnimationListener
  {
    public abstract void onValueChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\BaseKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */