package com.airbnb.lottie.animation.keyframe;

import android.graphics.PointF;
import com.airbnb.lottie.animation.Keyframe;
import java.util.Collections;
import java.util.List;

public class SplitDimensionPathKeyframeAnimation
  extends BaseKeyframeAnimation<PointF, PointF>
{
  private final PointF point = new PointF();
  private final BaseKeyframeAnimation<Float, Float> xAnimation;
  private final BaseKeyframeAnimation<Float, Float> yAnimation;
  
  public SplitDimensionPathKeyframeAnimation(BaseKeyframeAnimation<Float, Float> paramBaseKeyframeAnimation1, BaseKeyframeAnimation<Float, Float> paramBaseKeyframeAnimation2)
  {
    super(Collections.emptyList());
    this.xAnimation = paramBaseKeyframeAnimation1;
    this.yAnimation = paramBaseKeyframeAnimation2;
  }
  
  public PointF getValue()
  {
    return getValue(null, 0.0F);
  }
  
  PointF getValue(Keyframe<PointF> paramKeyframe, float paramFloat)
  {
    return this.point;
  }
  
  public void setProgress(float paramFloat)
  {
    this.xAnimation.setProgress(paramFloat);
    this.yAnimation.setProgress(paramFloat);
    this.point.set(((Float)this.xAnimation.getValue()).floatValue(), ((Float)this.yAnimation.getValue()).floatValue());
    int i = 0;
    for (;;)
    {
      if (i >= this.listeners.size()) {
        return;
      }
      ((BaseKeyframeAnimation.AnimationListener)this.listeners.get(i)).onValueChanged();
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\SplitDimensionPathKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */