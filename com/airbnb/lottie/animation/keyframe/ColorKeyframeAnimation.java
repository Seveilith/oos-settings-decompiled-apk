package com.airbnb.lottie.animation.keyframe;

import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.utils.GammaEvaluator;
import java.util.List;

public class ColorKeyframeAnimation
  extends KeyframeAnimation<Integer>
{
  public ColorKeyframeAnimation(List<Keyframe<Integer>> paramList)
  {
    super(paramList);
  }
  
  public Integer getValue(Keyframe<Integer> paramKeyframe, float paramFloat)
  {
    if (paramKeyframe.startValue == null) {}
    while (paramKeyframe.endValue == null) {
      throw new IllegalStateException("Missing values for keyframe.");
    }
    return Integer.valueOf(GammaEvaluator.evaluate(paramFloat, ((Integer)paramKeyframe.startValue).intValue(), ((Integer)paramKeyframe.endValue).intValue()));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\ColorKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */