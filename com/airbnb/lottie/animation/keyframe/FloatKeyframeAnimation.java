package com.airbnb.lottie.animation.keyframe;

import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.utils.MiscUtils;
import java.util.List;

public class FloatKeyframeAnimation
  extends KeyframeAnimation<Float>
{
  public FloatKeyframeAnimation(List<Keyframe<Float>> paramList)
  {
    super(paramList);
  }
  
  Float getValue(Keyframe<Float> paramKeyframe, float paramFloat)
  {
    if (paramKeyframe.startValue == null) {}
    while (paramKeyframe.endValue == null) {
      throw new IllegalStateException("Missing values for keyframe.");
    }
    return Float.valueOf(MiscUtils.lerp(((Float)paramKeyframe.startValue).floatValue(), ((Float)paramKeyframe.endValue).floatValue(), paramFloat));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\FloatKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */