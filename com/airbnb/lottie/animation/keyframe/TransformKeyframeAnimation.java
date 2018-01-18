package com.airbnb.lottie.animation.keyframe;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import com.airbnb.lottie.model.ScaleXY;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.animatable.AnimatablePathValue;
import com.airbnb.lottie.model.animatable.AnimatableScaleValue;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.model.layer.BaseLayer;

public class TransformKeyframeAnimation
{
  private final BaseKeyframeAnimation<PointF, PointF> anchorPoint;
  @Nullable
  private final BaseKeyframeAnimation<?, Float> endOpacity;
  private final Matrix matrix = new Matrix();
  private final BaseKeyframeAnimation<Integer, Integer> opacity;
  private final BaseKeyframeAnimation<?, PointF> position;
  private final BaseKeyframeAnimation<Float, Float> rotation;
  private final BaseKeyframeAnimation<ScaleXY, ScaleXY> scale;
  @Nullable
  private final BaseKeyframeAnimation<?, Float> startOpacity;
  
  public TransformKeyframeAnimation(AnimatableTransform paramAnimatableTransform)
  {
    this.anchorPoint = paramAnimatableTransform.getAnchorPoint().createAnimation();
    this.position = paramAnimatableTransform.getPosition().createAnimation();
    this.scale = paramAnimatableTransform.getScale().createAnimation();
    this.rotation = paramAnimatableTransform.getRotation().createAnimation();
    this.opacity = paramAnimatableTransform.getOpacity().createAnimation();
    if (paramAnimatableTransform.getStartOpacity() == null) {}
    for (this.startOpacity = null; paramAnimatableTransform.getEndOpacity() == null; this.startOpacity = paramAnimatableTransform.getStartOpacity().createAnimation())
    {
      this.endOpacity = null;
      return;
    }
    this.endOpacity = paramAnimatableTransform.getEndOpacity().createAnimation();
  }
  
  public void addAnimationsToLayer(BaseLayer paramBaseLayer)
  {
    paramBaseLayer.addAnimation(this.anchorPoint);
    paramBaseLayer.addAnimation(this.position);
    paramBaseLayer.addAnimation(this.scale);
    paramBaseLayer.addAnimation(this.rotation);
    paramBaseLayer.addAnimation(this.opacity);
    if (this.startOpacity == null) {}
    while (this.endOpacity == null)
    {
      return;
      paramBaseLayer.addAnimation(this.startOpacity);
    }
    paramBaseLayer.addAnimation(this.endOpacity);
  }
  
  public void addListener(BaseKeyframeAnimation.AnimationListener paramAnimationListener)
  {
    this.anchorPoint.addUpdateListener(paramAnimationListener);
    this.position.addUpdateListener(paramAnimationListener);
    this.scale.addUpdateListener(paramAnimationListener);
    this.rotation.addUpdateListener(paramAnimationListener);
    this.opacity.addUpdateListener(paramAnimationListener);
    if (this.startOpacity == null) {}
    while (this.endOpacity == null)
    {
      return;
      this.startOpacity.addUpdateListener(paramAnimationListener);
    }
    this.endOpacity.addUpdateListener(paramAnimationListener);
  }
  
  @Nullable
  public BaseKeyframeAnimation<?, Float> getEndOpacity()
  {
    return this.endOpacity;
  }
  
  public Matrix getMatrix()
  {
    this.matrix.reset();
    Object localObject = (PointF)this.position.getValue();
    if ((((PointF)localObject).x != 0.0F) || (((PointF)localObject).y != 0.0F)) {
      this.matrix.preTranslate(((PointF)localObject).x, ((PointF)localObject).y);
    }
    float f = ((Float)this.rotation.getValue()).floatValue();
    if (f != 0.0F) {
      this.matrix.preRotate(f);
    }
    localObject = (ScaleXY)this.scale.getValue();
    if ((((ScaleXY)localObject).getScaleX() != 1.0F) || (((ScaleXY)localObject).getScaleY() != 1.0F)) {
      this.matrix.preScale(((ScaleXY)localObject).getScaleX(), ((ScaleXY)localObject).getScaleY());
    }
    localObject = (PointF)this.anchorPoint.getValue();
    if ((((PointF)localObject).x != 0.0F) || (((PointF)localObject).y != 0.0F)) {
      this.matrix.preTranslate(-((PointF)localObject).x, -((PointF)localObject).y);
    }
    return this.matrix;
  }
  
  public Matrix getMatrixForRepeater(float paramFloat)
  {
    PointF localPointF1 = (PointF)this.position.getValue();
    PointF localPointF2 = (PointF)this.anchorPoint.getValue();
    ScaleXY localScaleXY = (ScaleXY)this.scale.getValue();
    float f = ((Float)this.rotation.getValue()).floatValue();
    this.matrix.reset();
    this.matrix.preTranslate(localPointF1.x * paramFloat, localPointF1.y * paramFloat);
    this.matrix.preScale((float)Math.pow(localScaleXY.getScaleX(), paramFloat), (float)Math.pow(localScaleXY.getScaleY(), paramFloat));
    this.matrix.preRotate(f * paramFloat, localPointF2.x, localPointF2.y);
    return this.matrix;
  }
  
  public BaseKeyframeAnimation<?, Integer> getOpacity()
  {
    return this.opacity;
  }
  
  @Nullable
  public BaseKeyframeAnimation<?, Float> getStartOpacity()
  {
    return this.startOpacity;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\TransformKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */