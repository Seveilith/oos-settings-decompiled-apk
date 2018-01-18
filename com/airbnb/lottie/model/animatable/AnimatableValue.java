package com.airbnb.lottie.model.animatable;

import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;

public abstract interface AnimatableValue<K, A>
{
  public abstract BaseKeyframeAnimation<K, A> createAnimation();
  
  public abstract boolean hasAnimation();
  
  public static abstract interface Factory<V>
  {
    public abstract V valueFromObject(Object paramObject, float paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */