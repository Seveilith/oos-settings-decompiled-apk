package com.airbnb.lottie.animation.keyframe;

import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.utils.MiscUtils;
import java.util.List;

public class IntegerKeyframeAnimation
  extends KeyframeAnimation<Integer>
{
  public IntegerKeyframeAnimation(List<Keyframe<Integer>> paramList)
  {
    super(paramList);
  }
  
  Integer getValue(Keyframe<Integer> paramKeyframe, float paramFloat)
  {
    if (paramKeyframe.startValue == null) {}
    while (paramKeyframe.endValue == null) {
      throw new IllegalStateException("Missing values for keyframe.");
    }
    return Integer.valueOf(MiscUtils.lerp(((Integer)paramKeyframe.startValue).intValue(), ((Integer)paramKeyframe.endValue).intValue(), paramFloat));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\IntegerKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */