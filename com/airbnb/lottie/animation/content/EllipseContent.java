package com.airbnb.lottie.animation.content;

import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.model.animatable.AnimatablePointValue;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.model.content.CircleShape;
import com.airbnb.lottie.model.content.ShapeTrimPath.Type;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.utils.Utils;
import java.util.List;

public class EllipseContent
  implements PathContent, BaseKeyframeAnimation.AnimationListener
{
  private static final float ELLIPSE_CONTROL_POINT_PERCENTAGE = 0.55228F;
  private boolean isPathValid;
  private final LottieDrawable lottieDrawable;
  private final String name;
  private final Path path = new Path();
  private final BaseKeyframeAnimation<?, PointF> positionAnimation;
  private final BaseKeyframeAnimation<?, PointF> sizeAnimation;
  @Nullable
  private TrimPathContent trimPath;
  
  public EllipseContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, CircleShape paramCircleShape)
  {
    this.name = paramCircleShape.getName();
    this.lottieDrawable = paramLottieDrawable;
    this.sizeAnimation = paramCircleShape.getSize().createAnimation();
    this.positionAnimation = paramCircleShape.getPosition().createAnimation();
    paramBaseLayer.addAnimation(this.sizeAnimation);
    paramBaseLayer.addAnimation(this.positionAnimation);
    this.sizeAnimation.addUpdateListener(this);
    this.positionAnimation.addUpdateListener(this);
  }
  
  private void invalidate()
  {
    this.isPathValid = false;
    this.lottieDrawable.invalidateSelf();
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Path getPath()
  {
    if (!this.isPathValid)
    {
      this.path.reset();
      PointF localPointF = (PointF)this.sizeAnimation.getValue();
      float f1 = localPointF.x / 2.0F;
      float f2 = localPointF.y / 2.0F;
      float f3 = f1 * 0.55228F;
      float f4 = f2 * 0.55228F;
      this.path.reset();
      this.path.moveTo(0.0F, -f2);
      this.path.cubicTo(0.0F + f3, -f2, f1, 0.0F - f4, f1, 0.0F);
      this.path.cubicTo(f1, 0.0F + f4, 0.0F + f3, f2, 0.0F, f2);
      this.path.cubicTo(0.0F - f3, f2, -f1, 0.0F + f4, -f1, 0.0F);
      this.path.cubicTo(-f1, 0.0F - f4, 0.0F - f3, -f2, 0.0F, -f2);
      localPointF = (PointF)this.positionAnimation.getValue();
      this.path.offset(localPointF.x, localPointF.y);
      this.path.close();
      Utils.applyTrimPathIfNeeded(this.path, this.trimPath);
      this.isPathValid = true;
      return this.path;
    }
    return this.path;
  }
  
  public void onValueChanged()
  {
    invalidate();
  }
  
  public void setContents(List<Content> paramList1, List<Content> paramList2)
  {
    int i = 0;
    if (i >= paramList1.size()) {
      return;
    }
    paramList2 = (Content)paramList1.get(i);
    if (!(paramList2 instanceof TrimPathContent)) {}
    for (;;)
    {
      i += 1;
      break;
      if (((TrimPathContent)paramList2).getType() == ShapeTrimPath.Type.Simultaneously)
      {
        this.trimPath = ((TrimPathContent)paramList2);
        this.trimPath.addListener(this);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\EllipseContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */