package com.airbnb.lottie.animation.keyframe;

import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.model.ScaleXY;
import com.airbnb.lottie.utils.MiscUtils;
import java.util.List;

public class ScaleKeyframeAnimation
  extends KeyframeAnimation<ScaleXY>
{
  public ScaleKeyframeAnimation(List<Keyframe<ScaleXY>> paramList)
  {
    super(paramList);
  }
  
  public ScaleXY getValue(Keyframe<ScaleXY> paramKeyframe, float paramFloat)
  {
    if (paramKeyframe.startValue == null) {}
    while (paramKeyframe.endValue == null) {
      throw new IllegalStateException("Missing values for keyframe.");
    }
    ScaleXY localScaleXY = (ScaleXY)paramKeyframe.startValue;
    paramKeyframe = (ScaleXY)paramKeyframe.endValue;
    return new ScaleXY(MiscUtils.lerp(localScaleXY.getScaleX(), paramKeyframe.getScaleX(), paramFloat), MiscUtils.lerp(localScaleXY.getScaleY(), paramKeyframe.getScaleY(), paramFloat));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\ScaleKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */