package com.airbnb.lottie.animation.keyframe;

import android.graphics.PointF;
import com.airbnb.lottie.animation.Keyframe;
import java.util.List;

public class PointKeyframeAnimation
  extends KeyframeAnimation<PointF>
{
  private final PointF point = new PointF();
  
  public PointKeyframeAnimation(List<Keyframe<PointF>> paramList)
  {
    super(paramList);
  }
  
  public PointF getValue(Keyframe<PointF> paramKeyframe, float paramFloat)
  {
    if (paramKeyframe.startValue == null) {}
    while (paramKeyframe.endValue == null) {
      throw new IllegalStateException("Missing values for keyframe.");
    }
    PointF localPointF1 = (PointF)paramKeyframe.startValue;
    paramKeyframe = (PointF)paramKeyframe.endValue;
    PointF localPointF2 = this.point;
    float f1 = localPointF1.x;
    float f2 = paramKeyframe.x;
    float f3 = localPointF1.x;
    float f4 = localPointF1.y;
    localPointF2.set(f1 + (f2 - f3) * paramFloat, (paramKeyframe.y - localPointF1.y) * paramFloat + f4);
    return this.point;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\PointKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */