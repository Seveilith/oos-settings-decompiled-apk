package com.airbnb.lottie.animation.keyframe;

import com.airbnb.lottie.animation.Keyframe;
import java.util.List;

public abstract class KeyframeAnimation<T>
  extends BaseKeyframeAnimation<T, T>
{
  KeyframeAnimation(List<? extends Keyframe<T>> paramList)
  {
    super(paramList);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\KeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */