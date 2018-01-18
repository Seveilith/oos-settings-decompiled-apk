package com.airbnb.lottie.animation.content;

import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.model.content.PolystarShape;
import com.airbnb.lottie.model.content.PolystarShape.Type;
import com.airbnb.lottie.model.content.ShapeTrimPath.Type;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.utils.Utils;
import java.util.List;

public class PolystarContent
  implements PathContent, BaseKeyframeAnimation.AnimationListener
{
  private static final float POLYGON_MAGIC_NUMBER = 0.25F;
  private static final float POLYSTAR_MAGIC_NUMBER = 0.47829F;
  @Nullable
  private final BaseKeyframeAnimation<?, Float> innerRadiusAnimation;
  @Nullable
  private final BaseKeyframeAnimation<?, Float> innerRoundednessAnimation;
  private boolean isPathValid;
  private final LottieDrawable lottieDrawable;
  private final String name;
  private final BaseKeyframeAnimation<?, Float> outerRadiusAnimation;
  private final BaseKeyframeAnimation<?, Float> outerRoundednessAnimation;
  private final Path path = new Path();
  private final BaseKeyframeAnimation<?, Float> pointsAnimation;
  private final BaseKeyframeAnimation<?, PointF> positionAnimation;
  private final BaseKeyframeAnimation<?, Float> rotationAnimation;
  @Nullable
  private TrimPathContent trimPath;
  private final PolystarShape.Type type;
  
  public PolystarContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, PolystarShape paramPolystarShape)
  {
    this.lottieDrawable = paramLottieDrawable;
    this.name = paramPolystarShape.getName();
    this.type = paramPolystarShape.getType();
    this.pointsAnimation = paramPolystarShape.getPoints().createAnimation();
    this.positionAnimation = paramPolystarShape.getPosition().createAnimation();
    this.rotationAnimation = paramPolystarShape.getRotation().createAnimation();
    this.outerRadiusAnimation = paramPolystarShape.getOuterRadius().createAnimation();
    this.outerRoundednessAnimation = paramPolystarShape.getOuterRoundedness().createAnimation();
    if (this.type != PolystarShape.Type.Star)
    {
      this.innerRadiusAnimation = null;
      this.innerRoundednessAnimation = null;
      paramBaseLayer.addAnimation(this.pointsAnimation);
      paramBaseLayer.addAnimation(this.positionAnimation);
      paramBaseLayer.addAnimation(this.rotationAnimation);
      paramBaseLayer.addAnimation(this.outerRadiusAnimation);
      paramBaseLayer.addAnimation(this.outerRoundednessAnimation);
      if (this.type == PolystarShape.Type.Star) {
        break label239;
      }
    }
    for (;;)
    {
      this.pointsAnimation.addUpdateListener(this);
      this.positionAnimation.addUpdateListener(this);
      this.rotationAnimation.addUpdateListener(this);
      this.outerRadiusAnimation.addUpdateListener(this);
      this.outerRoundednessAnimation.addUpdateListener(this);
      if (this.type == PolystarShape.Type.Star) {
        break label258;
      }
      return;
      this.innerRadiusAnimation = paramPolystarShape.getInnerRadius().createAnimation();
      this.innerRoundednessAnimation = paramPolystarShape.getInnerRoundedness().createAnimation();
      break;
      label239:
      paramBaseLayer.addAnimation(this.innerRadiusAnimation);
      paramBaseLayer.addAnimation(this.innerRoundednessAnimation);
    }
    label258:
    this.outerRadiusAnimation.addUpdateListener(this);
    this.outerRoundednessAnimation.addUpdateListener(this);
  }
  
  private void createPolygonPath()
  {
    int i = (int)Math.floor(((Float)this.pointsAnimation.getValue()).floatValue());
    double d1;
    float f5;
    float f1;
    float f2;
    double d3;
    label147:
    float f4;
    float f3;
    if (this.rotationAnimation != null)
    {
      d1 = ((Float)this.rotationAnimation.getValue()).floatValue();
      d1 = Math.toRadians(d1 - 90.0D);
      f5 = (float)(6.283185307179586D / i);
      float f6 = ((Float)this.outerRoundednessAnimation.getValue()).floatValue() / 100.0F;
      float f7 = ((Float)this.outerRadiusAnimation.getValue()).floatValue();
      f1 = (float)(f7 * Math.cos(d1));
      f2 = (float)(f7 * Math.sin(d1));
      this.path.moveTo(f1, f2);
      d3 = f5;
      double d2 = Math.ceil(i);
      i = 0;
      d1 += d3;
      if (i >= d2) {
        break label365;
      }
      f4 = (float)(f7 * Math.cos(d1));
      f3 = (float)(f7 * Math.sin(d1));
      if (f6 == 0.0F) {
        break label351;
      }
      float f9 = (float)(Math.atan2(f2, f1) - 1.5707963267948966D);
      float f8 = (float)Math.cos(f9);
      f9 = (float)Math.sin(f9);
      float f11 = (float)(Math.atan2(f3, f4) - 1.5707963267948966D);
      float f10 = (float)Math.cos(f11);
      f11 = (float)Math.sin(f11);
      this.path.cubicTo(f1 - f8 * (f7 * f6 * 0.25F), f2 - f7 * f6 * 0.25F * f9, f4 + f10 * (f7 * f6 * 0.25F), f11 * (f7 * f6 * 0.25F) + f3, f4, f3);
    }
    for (;;)
    {
      d3 = f5;
      i += 1;
      f1 = f4;
      d1 += d3;
      f2 = f3;
      break label147;
      d1 = 0.0D;
      break;
      label351:
      this.path.lineTo(f4, f3);
    }
    label365:
    PointF localPointF = (PointF)this.positionAnimation.getValue();
    this.path.offset(localPointF.x, localPointF.y);
    this.path.close();
  }
  
  private void createStarPath()
  {
    float f5 = ((Float)this.pointsAnimation.getValue()).floatValue();
    double d1;
    float f14;
    float f18;
    label89:
    float f7;
    float f8;
    float f1;
    label129:
    float f2;
    label139:
    float f9;
    float f3;
    float f4;
    label203:
    double d2;
    int j;
    float f11;
    float f10;
    label228:
    label245:
    float f12;
    label273:
    float f15;
    float f16;
    double d3;
    if (this.rotationAnimation != null)
    {
      d1 = ((Float)this.rotationAnimation.getValue()).floatValue();
      d1 = Math.toRadians(d1 - 90.0D);
      float f17 = (float)(6.283185307179586D / f5);
      f14 = f17 / 2.0F;
      f18 = f5 - (int)f5;
      if (f18 == 0.0F) {
        break label389;
      }
      d1 += (1.0F - f18) * f14;
      f7 = ((Float)this.outerRadiusAnimation.getValue()).floatValue();
      f8 = ((Float)this.innerRadiusAnimation.getValue()).floatValue();
      if (this.innerRoundednessAnimation != null) {
        break label392;
      }
      f1 = 0.0F;
      if (this.outerRoundednessAnimation != null) {
        break label413;
      }
      f2 = 0.0F;
      if (f18 == 0.0F) {
        break label434;
      }
      f9 = f8 + (f7 - f8) * f18;
      f3 = (float)(f9 * Math.cos(d1));
      f4 = (float)(f9 * Math.sin(d1));
      this.path.moveTo(f3, f4);
      d1 += f17 * f18 / 2.0F;
      d2 = Math.ceil(f5) * 2.0D;
      j = 0;
      i = 0;
      f11 = f3;
      f10 = f4;
      if (j >= d2) {
        break label816;
      }
      if (i != 0) {
        break label479;
      }
      f3 = f8;
      if ((f9 == 0.0F) || (j != d2 - 2.0D)) {
        break label486;
      }
      f12 = f17 * f18 / 2.0F;
      f4 = f3;
      if (f9 != 0.0F)
      {
        f4 = f3;
        if (j == d2 - 1.0D) {
          f4 = f9;
        }
      }
      f15 = (float)(f4 * Math.cos(d1));
      f16 = (float)(f4 * Math.sin(d1));
      if ((f1 != 0.0F) || (f2 != 0.0F)) {
        break label493;
      }
      this.path.lineTo(f15, f16);
      d3 = f12;
      if (i == 0) {
        break label810;
      }
    }
    label389:
    label392:
    label413:
    label434:
    label479:
    label486:
    label493:
    label570:
    label579:
    label588:
    label597:
    label730:
    label737:
    label744:
    label751:
    label766:
    label795:
    label810:
    for (int i = 0;; i = 1)
    {
      j += 1;
      f10 = f16;
      f11 = f15;
      d1 += d3;
      break label228;
      d1 = 0.0D;
      break;
      break label89;
      f1 = ((Float)this.innerRoundednessAnimation.getValue()).floatValue() / 100.0F;
      break label129;
      f2 = ((Float)this.outerRoundednessAnimation.getValue()).floatValue() / 100.0F;
      break label139;
      f3 = (float)(f7 * Math.cos(d1));
      f4 = (float)(f7 * Math.sin(d1));
      this.path.moveTo(f3, f4);
      d1 += f14;
      f9 = 0.0F;
      break label203;
      f3 = f7;
      break label245;
      f12 = f14;
      break label273;
      f4 = (float)(Math.atan2(f10, f11) - 1.5707963267948966D);
      f3 = (float)Math.cos(f4);
      float f21 = (float)Math.sin(f4);
      f4 = (float)(Math.atan2(f16, f15) - 1.5707963267948966D);
      float f19 = (float)Math.cos(f4);
      float f20 = (float)Math.sin(f4);
      float f6;
      float f13;
      if (i == 0)
      {
        f4 = f2;
        if (i != 0) {
          break label730;
        }
        f5 = f1;
        if (i != 0) {
          break label737;
        }
        f6 = f7;
        if (i != 0) {
          break label744;
        }
        f13 = f8;
        f3 = f6 * f4 * 0.47829F * f3;
        f4 = f4 * f6 * 0.47829F * f21;
        f6 = f13 * f5 * 0.47829F * f19;
        f5 = f13 * f5 * 0.47829F * f20;
        if (f18 == 0.0F) {
          break label751;
        }
        if (j == 0) {
          break label766;
        }
        if (j != d2 - 1.0D) {
          break label795;
        }
        f13 = f6 * f18;
        f6 = f5 * f18;
        f5 = f13;
      }
      for (;;)
      {
        this.path.cubicTo(f11 - f3, f10 - f4, f5 + f15, f6 + f16, f15, f16);
        break;
        f4 = f1;
        break label570;
        f5 = f2;
        break label579;
        f6 = f8;
        break label588;
        f13 = f7;
        break label597;
        f13 = f5;
        f5 = f6;
        f6 = f13;
        continue;
        f13 = f5;
        f4 *= f18;
        f3 *= f18;
        f5 = f6;
        f6 = f13;
        continue;
        f13 = f5;
        f5 = f6;
        f6 = f13;
      }
    }
    label816:
    PointF localPointF = (PointF)this.positionAnimation.getValue();
    this.path.offset(localPointF.x, localPointF.y);
    this.path.close();
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
      switch (this.type)
      {
      }
    }
    for (;;)
    {
      this.path.close();
      Utils.applyTrimPathIfNeeded(this.path, this.trimPath);
      this.isPathValid = true;
      return this.path;
      return this.path;
      createStarPath();
      continue;
      createPolygonPath();
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


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\PolystarContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */