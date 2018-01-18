package com.airbnb.lottie.animation.keyframe;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import com.airbnb.lottie.animation.Keyframe;
import java.util.List;

public class PathKeyframeAnimation
  extends KeyframeAnimation<PointF>
{
  private PathMeasure pathMeasure;
  private PathKeyframe pathMeasureKeyframe;
  private final PointF point = new PointF();
  private final float[] pos = new float[2];
  
  public PathKeyframeAnimation(List<? extends Keyframe<PointF>> paramList)
  {
    super(paramList);
  }
  
  public PointF getValue(Keyframe<PointF> paramKeyframe, float paramFloat)
  {
    PathKeyframe localPathKeyframe = (PathKeyframe)paramKeyframe;
    Path localPath = localPathKeyframe.getPath();
    if (localPath != null) {
      if (this.pathMeasureKeyframe != localPathKeyframe) {
        break label78;
      }
    }
    for (;;)
    {
      this.pathMeasure.getPosTan(this.pathMeasure.getLength() * paramFloat, this.pos, null);
      this.point.set(this.pos[0], this.pos[1]);
      return this.point;
      return (PointF)paramKeyframe.startValue;
      label78:
      this.pathMeasure = new PathMeasure(localPath, false);
      this.pathMeasureKeyframe = localPathKeyframe;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\PathKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */