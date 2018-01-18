package com.airbnb.lottie.animation.keyframe;

import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.model.DocumentData;
import java.util.List;

public class TextKeyframeAnimation
  extends KeyframeAnimation<DocumentData>
{
  public TextKeyframeAnimation(List<? extends Keyframe<DocumentData>> paramList)
  {
    super(paramList);
  }
  
  DocumentData getValue(Keyframe<DocumentData> paramKeyframe, float paramFloat)
  {
    return (DocumentData)paramKeyframe.startValue;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\TextKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */