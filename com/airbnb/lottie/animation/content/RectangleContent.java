package com.airbnb.lottie.animation.content;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatablePointValue;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.model.content.RectangleShape;
import com.airbnb.lottie.model.content.ShapeTrimPath.Type;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.utils.Utils;
import java.util.List;

public class RectangleContent
  implements PathContent, BaseKeyframeAnimation.AnimationListener
{
  private final BaseKeyframeAnimation<?, Float> cornerRadiusAnimation;
  private boolean isPathValid;
  private final LottieDrawable lottieDrawable;
  private final String name;
  private final Path path = new Path();
  private final BaseKeyframeAnimation<?, PointF> positionAnimation;
  private final RectF rect = new RectF();
  private final BaseKeyframeAnimation<?, PointF> sizeAnimation;
  @Nullable
  private TrimPathContent trimPath;
  
  public RectangleContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, RectangleShape paramRectangleShape)
  {
    this.name = paramRectangleShape.getName();
    this.lottieDrawable = paramLottieDrawable;
    this.positionAnimation = paramRectangleShape.getPosition().createAnimation();
    this.sizeAnimation = paramRectangleShape.getSize().createAnimation();
    this.cornerRadiusAnimation = paramRectangleShape.getCornerRadius().createAnimation();
    paramBaseLayer.addAnimation(this.positionAnimation);
    paramBaseLayer.addAnimation(this.sizeAnimation);
    paramBaseLayer.addAnimation(this.cornerRadiusAnimation);
    this.positionAnimation.addUpdateListener(this);
    this.sizeAnimation.addUpdateListener(this);
    this.cornerRadiusAnimation.addUpdateListener(this);
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
    PointF localPointF;
    float f3;
    float f4;
    float f1;
    if (!this.isPathValid)
    {
      this.path.reset();
      localPointF = (PointF)this.sizeAnimation.getValue();
      f3 = localPointF.x / 2.0F;
      f4 = localPointF.y / 2.0F;
      if (this.cornerRadiusAnimation == null) {
        break label511;
      }
      f1 = ((Float)this.cornerRadiusAnimation.getValue()).floatValue();
      float f2 = Math.min(f3, f4);
      if (f1 <= f2) {
        break label516;
      }
      f1 = f2;
    }
    label511:
    label516:
    for (;;)
    {
      localPointF = (PointF)this.positionAnimation.getValue();
      this.path.moveTo(localPointF.x + f3, localPointF.y - f4 + f1);
      this.path.lineTo(localPointF.x + f3, localPointF.y + f4 - f1);
      if (f1 > 0.0F)
      {
        this.rect.set(localPointF.x + f3 - 2.0F * f1, localPointF.y + f4 - 2.0F * f1, localPointF.x + f3, localPointF.y + f4);
        this.path.arcTo(this.rect, 0.0F, 90.0F, false);
      }
      this.path.lineTo(localPointF.x - f3 + f1, localPointF.y + f4);
      if (f1 > 0.0F)
      {
        this.rect.set(localPointF.x - f3, localPointF.y + f4 - 2.0F * f1, localPointF.x - f3 + 2.0F * f1, localPointF.y + f4);
        this.path.arcTo(this.rect, 90.0F, 90.0F, false);
      }
      this.path.lineTo(localPointF.x - f3, localPointF.y - f4 + f1);
      if (f1 > 0.0F)
      {
        this.rect.set(localPointF.x - f3, localPointF.y - f4, localPointF.x - f3 + 2.0F * f1, localPointF.y - f4 + 2.0F * f1);
        this.path.arcTo(this.rect, 180.0F, 90.0F, false);
      }
      this.path.lineTo(localPointF.x + f3 - f1, localPointF.y - f4);
      if (f1 > 0.0F)
      {
        this.rect.set(localPointF.x + f3 - 2.0F * f1, localPointF.y - f4, f3 + localPointF.x, localPointF.y - f4 + f1 * 2.0F);
        this.path.arcTo(this.rect, 270.0F, 90.0F, false);
      }
      this.path.close();
      Utils.applyTrimPathIfNeeded(this.path, this.trimPath);
      this.isPathValid = true;
      return this.path;
      return this.path;
      f1 = 0.0F;
      break;
    }
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


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\RectangleContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */