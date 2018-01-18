package com.airbnb.lottie.animation.content;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.model.animatable.AnimatableGradientColorValue;
import com.airbnb.lottie.model.animatable.AnimatablePointValue;
import com.airbnb.lottie.model.content.GradientColor;
import com.airbnb.lottie.model.content.GradientStroke;
import com.airbnb.lottie.model.content.GradientType;
import com.airbnb.lottie.model.content.ShapeStroke.LineCapType;
import com.airbnb.lottie.model.content.ShapeStroke.LineJoinType;
import com.airbnb.lottie.model.layer.BaseLayer;

public class GradientStrokeContent
  extends BaseStrokeContent
{
  private static final int CACHE_STEPS_MS = 32;
  private final RectF boundsRect = new RectF();
  private final int cacheSteps;
  private final BaseKeyframeAnimation<GradientColor, GradientColor> colorAnimation;
  private final BaseKeyframeAnimation<PointF, PointF> endPointAnimation;
  private final LongSparseArray<LinearGradient> linearGradientCache = new LongSparseArray();
  private final String name;
  private final LongSparseArray<RadialGradient> radialGradientCache = new LongSparseArray();
  private final BaseKeyframeAnimation<PointF, PointF> startPointAnimation;
  private final GradientType type;
  
  public GradientStrokeContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, GradientStroke paramGradientStroke)
  {
    super(paramLottieDrawable, paramBaseLayer, paramGradientStroke.getCapType().toPaintCap(), paramGradientStroke.getJoinType().toPaintJoin(), paramGradientStroke.getOpacity(), paramGradientStroke.getWidth(), paramGradientStroke.getLineDashPattern(), paramGradientStroke.getDashOffset());
    this.name = paramGradientStroke.getName();
    this.type = paramGradientStroke.getGradientType();
    this.cacheSteps = ((int)(paramLottieDrawable.getComposition().getDuration() / 32L));
    this.colorAnimation = paramGradientStroke.getGradientColor().createAnimation();
    this.colorAnimation.addUpdateListener(this);
    paramBaseLayer.addAnimation(this.colorAnimation);
    this.startPointAnimation = paramGradientStroke.getStartPoint().createAnimation();
    this.startPointAnimation.addUpdateListener(this);
    paramBaseLayer.addAnimation(this.startPointAnimation);
    this.endPointAnimation = paramGradientStroke.getEndPoint().createAnimation();
    this.endPointAnimation.addUpdateListener(this);
    paramBaseLayer.addAnimation(this.endPointAnimation);
  }
  
  private int getGradientHash()
  {
    int m = Math.round(this.startPointAnimation.getProgress() * this.cacheSteps);
    int k = Math.round(this.endPointAnimation.getProgress() * this.cacheSteps);
    int j = Math.round(this.colorAnimation.getProgress() * this.cacheSteps);
    int i = 17;
    if (m == 0) {
      if (k != 0) {
        break label80;
      }
    }
    for (;;)
    {
      if (j != 0) {
        break label90;
      }
      return i;
      i = m * 527;
      break;
      label80:
      i = i * 31 * k;
    }
    label90:
    return i * 31 * j;
  }
  
  private LinearGradient getLinearGradient()
  {
    int i = getGradientHash();
    Object localObject1 = (LinearGradient)this.linearGradientCache.get(i);
    if (localObject1 == null)
    {
      localObject1 = (PointF)this.startPointAnimation.getValue();
      PointF localPointF = (PointF)this.endPointAnimation.getValue();
      Object localObject2 = (GradientColor)this.colorAnimation.getValue();
      int[] arrayOfInt = ((GradientColor)localObject2).getColors();
      localObject2 = ((GradientColor)localObject2).getPositions();
      int j = (int)(this.boundsRect.left + this.boundsRect.width() / 2.0F + ((PointF)localObject1).x);
      float f1 = this.boundsRect.top;
      float f2 = this.boundsRect.height() / 2.0F;
      int k = (int)(((PointF)localObject1).y + (f1 + f2));
      int m = (int)(this.boundsRect.left + this.boundsRect.width() / 2.0F + localPointF.x);
      int n = (int)(this.boundsRect.top + this.boundsRect.height() / 2.0F + localPointF.y);
      localObject1 = new LinearGradient(j, k, m, n, arrayOfInt, (float[])localObject2, Shader.TileMode.CLAMP);
      this.linearGradientCache.put(i, localObject1);
      return (LinearGradient)localObject1;
    }
    return (LinearGradient)localObject1;
  }
  
  private RadialGradient getRadialGradient()
  {
    int i = getGradientHash();
    Object localObject1 = (RadialGradient)this.radialGradientCache.get(i);
    if (localObject1 == null)
    {
      localObject1 = (PointF)this.startPointAnimation.getValue();
      PointF localPointF = (PointF)this.endPointAnimation.getValue();
      Object localObject2 = (GradientColor)this.colorAnimation.getValue();
      int[] arrayOfInt = ((GradientColor)localObject2).getColors();
      localObject2 = ((GradientColor)localObject2).getPositions();
      int j = (int)(this.boundsRect.left + this.boundsRect.width() / 2.0F + ((PointF)localObject1).x);
      float f1 = this.boundsRect.top;
      float f2 = this.boundsRect.height() / 2.0F;
      int k = (int)(((PointF)localObject1).y + (f1 + f2));
      int m = (int)(this.boundsRect.left + this.boundsRect.width() / 2.0F + localPointF.x);
      f1 = this.boundsRect.top;
      f2 = this.boundsRect.height() / 2.0F;
      int n = (int)(localPointF.y + (f1 + f2));
      f1 = (float)Math.hypot(m - j, n - k);
      localObject1 = new RadialGradient(j, k, f1, arrayOfInt, (float[])localObject2, Shader.TileMode.CLAMP);
      this.radialGradientCache.put(i, localObject1);
      return (RadialGradient)localObject1;
    }
    return (RadialGradient)localObject1;
  }
  
  public void addColorFilter(@Nullable String paramString1, @Nullable String paramString2, @Nullable ColorFilter paramColorFilter) {}
  
  public void draw(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    getBounds(this.boundsRect, paramMatrix);
    if (this.type != GradientType.Linear) {
      this.paint.setShader(getRadialGradient());
    }
    for (;;)
    {
      super.draw(paramCanvas, paramMatrix, paramInt);
      return;
      this.paint.setShader(getLinearGradient());
    }
  }
  
  public String getName()
  {
    return this.name;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\GradientStrokeContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */