package com.airbnb.lottie.animation.content;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.model.animatable.AnimatableColorValue;
import com.airbnb.lottie.model.content.ShapeStroke;
import com.airbnb.lottie.model.content.ShapeStroke.LineCapType;
import com.airbnb.lottie.model.content.ShapeStroke.LineJoinType;
import com.airbnb.lottie.model.layer.BaseLayer;

public class StrokeContent
  extends BaseStrokeContent
{
  private final BaseKeyframeAnimation<Integer, Integer> colorAnimation;
  private final String name;
  
  public StrokeContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, ShapeStroke paramShapeStroke)
  {
    super(paramLottieDrawable, paramBaseLayer, paramShapeStroke.getCapType().toPaintCap(), paramShapeStroke.getJoinType().toPaintJoin(), paramShapeStroke.getOpacity(), paramShapeStroke.getWidth(), paramShapeStroke.getLineDashPattern(), paramShapeStroke.getDashOffset());
    this.name = paramShapeStroke.getName();
    this.colorAnimation = paramShapeStroke.getColor().createAnimation();
    this.colorAnimation.addUpdateListener(this);
    paramBaseLayer.addAnimation(this.colorAnimation);
  }
  
  public void addColorFilter(@Nullable String paramString1, @Nullable String paramString2, @Nullable ColorFilter paramColorFilter)
  {
    this.paint.setColorFilter(paramColorFilter);
  }
  
  public void draw(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    this.paint.setColor(((Integer)this.colorAnimation.getValue()).intValue());
    super.draw(paramCanvas, paramMatrix, paramInt);
  }
  
  public String getName()
  {
    return this.name;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\StrokeContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */