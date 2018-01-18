package com.airbnb.lottie.model.animatable;

import android.graphics.PointF;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.SplitDimensionPathKeyframeAnimation;

public class AnimatableSplitDimensionPathValue
  implements AnimatableValue<PointF, PointF>
{
  private final AnimatableFloatValue animatableXDimension;
  private final AnimatableFloatValue animatableYDimension;
  
  AnimatableSplitDimensionPathValue(AnimatableFloatValue paramAnimatableFloatValue1, AnimatableFloatValue paramAnimatableFloatValue2)
  {
    this.animatableXDimension = paramAnimatableFloatValue1;
    this.animatableYDimension = paramAnimatableFloatValue2;
  }
  
  public BaseKeyframeAnimation<PointF, PointF> createAnimation()
  {
    return new SplitDimensionPathKeyframeAnimation(this.animatableXDimension.createAnimation(), this.animatableYDimension.createAnimation());
  }
  
  public boolean hasAnimation()
  {
    if (this.animatableXDimension.hasAnimation()) {}
    while (this.animatableYDimension.hasAnimation()) {
      return true;
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableSplitDimensionPathValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */