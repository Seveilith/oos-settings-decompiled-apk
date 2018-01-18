package com.airbnb.lottie.animation.keyframe;

import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.model.content.GradientColor;
import java.util.List;

public class GradientColorKeyframeAnimation
  extends KeyframeAnimation<GradientColor>
{
  private final GradientColor gradientColor;
  
  public GradientColorKeyframeAnimation(List<? extends Keyframe<GradientColor>> paramList)
  {
    super(paramList);
    paramList = (GradientColor)((Keyframe)paramList.get(0)).startValue;
    if (paramList != null) {}
    for (int i = paramList.getSize();; i = 0)
    {
      this.gradientColor = new GradientColor(new float[i], new int[i]);
      return;
    }
  }
  
  GradientColor getValue(Keyframe<GradientColor> paramKeyframe, float paramFloat)
  {
    this.gradientColor.lerp((GradientColor)paramKeyframe.startValue, (GradientColor)paramKeyframe.endValue, paramFloat);
    return this.gradientColor;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\GradientColorKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */