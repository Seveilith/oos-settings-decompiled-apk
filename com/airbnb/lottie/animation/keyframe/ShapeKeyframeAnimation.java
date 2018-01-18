package com.airbnb.lottie.animation.keyframe;

import android.graphics.Path;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.model.content.ShapeData;
import com.airbnb.lottie.utils.MiscUtils;
import java.util.List;

public class ShapeKeyframeAnimation
  extends BaseKeyframeAnimation<ShapeData, Path>
{
  private final Path tempPath = new Path();
  private final ShapeData tempShapeData = new ShapeData();
  
  public ShapeKeyframeAnimation(List<Keyframe<ShapeData>> paramList)
  {
    super(paramList);
  }
  
  public Path getValue(Keyframe<ShapeData> paramKeyframe, float paramFloat)
  {
    ShapeData localShapeData = (ShapeData)paramKeyframe.startValue;
    paramKeyframe = (ShapeData)paramKeyframe.endValue;
    this.tempShapeData.interpolateBetween(localShapeData, paramKeyframe, paramFloat);
    MiscUtils.getPathFromData(this.tempShapeData, this.tempPath);
    return this.tempPath;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\ShapeKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */