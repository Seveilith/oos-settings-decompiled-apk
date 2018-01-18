package com.airbnb.lottie.animation.content;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.content.ShapeTrimPath.Type;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseStrokeContent
  implements DrawingContent, BaseKeyframeAnimation.AnimationListener
{
  private final List<BaseKeyframeAnimation<?, Float>> dashPatternAnimations;
  @Nullable
  private final BaseKeyframeAnimation<?, Float> dashPatternOffsetAnimation;
  private final float[] dashPatternValues;
  private final LottieDrawable lottieDrawable;
  private final BaseKeyframeAnimation<?, Integer> opacityAnimation;
  final Paint paint = new Paint(1);
  private final Path path = new Path();
  private final List<PathGroup> pathGroups = new ArrayList();
  private final PathMeasure pm = new PathMeasure();
  private final RectF rect = new RectF();
  private final Path trimPathPath = new Path();
  private final BaseKeyframeAnimation<?, Float> widthAnimation;
  
  BaseStrokeContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, Paint.Cap paramCap, Paint.Join paramJoin, AnimatableIntegerValue paramAnimatableIntegerValue, AnimatableFloatValue paramAnimatableFloatValue1, List<AnimatableFloatValue> paramList, AnimatableFloatValue paramAnimatableFloatValue2)
  {
    this.lottieDrawable = paramLottieDrawable;
    this.paint.setStyle(Paint.Style.STROKE);
    this.paint.setStrokeCap(paramCap);
    this.paint.setStrokeJoin(paramJoin);
    this.opacityAnimation = paramAnimatableIntegerValue.createAnimation();
    this.widthAnimation = paramAnimatableFloatValue1.createAnimation();
    int i;
    if (paramAnimatableFloatValue2 != null)
    {
      this.dashPatternOffsetAnimation = paramAnimatableFloatValue2.createAnimation();
      this.dashPatternAnimations = new ArrayList(paramList.size());
      this.dashPatternValues = new float[paramList.size()];
      i = 0;
      label172:
      if (i < paramList.size()) {
        break label272;
      }
      paramBaseLayer.addAnimation(this.opacityAnimation);
      paramBaseLayer.addAnimation(this.widthAnimation);
      i = 0;
      label203:
      if (i < this.dashPatternAnimations.size()) {
        break label306;
      }
      if (this.dashPatternOffsetAnimation != null) {
        break label333;
      }
      label224:
      this.opacityAnimation.addUpdateListener(this);
      this.widthAnimation.addUpdateListener(this);
      i = j;
    }
    for (;;)
    {
      if (i >= paramList.size())
      {
        if (this.dashPatternOffsetAnimation != null) {
          break label371;
        }
        return;
        this.dashPatternOffsetAnimation = null;
        break;
        label272:
        this.dashPatternAnimations.add(((AnimatableFloatValue)paramList.get(i)).createAnimation());
        i += 1;
        break label172;
        label306:
        paramBaseLayer.addAnimation((BaseKeyframeAnimation)this.dashPatternAnimations.get(i));
        i += 1;
        break label203;
        label333:
        paramBaseLayer.addAnimation(this.dashPatternOffsetAnimation);
        break label224;
      }
      ((BaseKeyframeAnimation)this.dashPatternAnimations.get(i)).addUpdateListener(this);
      i += 1;
    }
    label371:
    this.dashPatternOffsetAnimation.addUpdateListener(this);
  }
  
  private void applyDashPatternIfNeeded(Matrix paramMatrix)
  {
    L.beginSection("StrokeContent#applyDashPattern");
    int i;
    if (!this.dashPatternAnimations.isEmpty())
    {
      f = Utils.getScale(paramMatrix);
      i = 0;
      if (i < this.dashPatternAnimations.size()) {
        break label92;
      }
      if (this.dashPatternOffsetAnimation == null) {
        break label187;
      }
    }
    label92:
    label187:
    for (float f = ((Float)this.dashPatternOffsetAnimation.getValue()).floatValue();; f = 0.0F)
    {
      this.paint.setPathEffect(new DashPathEffect(this.dashPatternValues, f));
      L.endSection("StrokeContent#applyDashPattern");
      return;
      L.endSection("StrokeContent#applyDashPattern");
      return;
      this.dashPatternValues[i] = ((Float)((BaseKeyframeAnimation)this.dashPatternAnimations.get(i)).getValue()).floatValue();
      if (i % 2 != 0) {
        if (this.dashPatternValues[i] < 0.1F) {
          this.dashPatternValues[i] = 0.1F;
        }
      }
      for (;;)
      {
        paramMatrix = this.dashPatternValues;
        paramMatrix[i] *= f;
        i += 1;
        break;
        if (this.dashPatternValues[i] < 1.0F) {
          this.dashPatternValues[i] = 1.0F;
        }
      }
    }
  }
  
  private void applyTrimPath(Canvas paramCanvas, PathGroup paramPathGroup, Matrix paramMatrix)
  {
    L.beginSection("StrokeContent#applyTrimPath");
    int i;
    if (paramPathGroup.trimPath != null)
    {
      this.path.reset();
      i = paramPathGroup.paths.size() - 1;
      if (i >= 0) {
        break label181;
      }
      this.pm.setPath(this.path, false);
    }
    float f2;
    float f5;
    float f6;
    for (float f1 = this.pm.getLength();; f1 = this.pm.getLength() + f1) {
      if (!this.pm.nextContour())
      {
        f2 = ((Float)paramPathGroup.trimPath.getOffset().getValue()).floatValue() * f1 / 360.0F;
        f5 = ((Float)paramPathGroup.trimPath.getStart().getValue()).floatValue() * f1 / 100.0F + f2;
        f6 = ((Float)paramPathGroup.trimPath.getEnd().getValue()).floatValue() * f1 / 100.0F + f2;
        i = paramPathGroup.paths.size() - 1;
        f2 = 0.0F;
        if (i >= 0) {
          break label232;
        }
        L.endSection("StrokeContent#applyTrimPath");
        return;
        L.endSection("StrokeContent#applyTrimPath");
        return;
        label181:
        this.path.addPath(((PathContent)paramPathGroup.paths.get(i)).getPath(), paramMatrix);
        i -= 1;
        break;
      }
    }
    label232:
    this.trimPathPath.set(((PathContent)paramPathGroup.paths.get(i)).getPath());
    this.trimPathPath.transform(paramMatrix);
    this.pm.setPath(this.trimPathPath, false);
    float f7 = this.pm.getLength();
    float f3;
    if ((f6 > f1) && (f6 - f1 < f2 + f7) && (f2 < f6 - f1)) {
      if (f5 > f1)
      {
        f3 = (f5 - f1) / f7;
        label338:
        f4 = Math.min((f6 - f1) / f7, 1.0F);
        Utils.applyTrimPathIfNeeded(this.trimPathPath, f3, f4, 0.0F);
        paramCanvas.drawPath(this.trimPathPath, this.paint);
      }
    }
    label463:
    for (;;)
    {
      i -= 1;
      f2 += f7;
      break;
      f3 = 0.0F;
      break label338;
      if (f2 + f7 < f5) {}
      for (int j = 1;; j = 0)
      {
        if ((j != 0) || (f2 > f6)) {
          break label463;
        }
        if ((f2 + f7 > f6) || (f5 >= f2)) {
          break label465;
        }
        paramCanvas.drawPath(this.trimPathPath, this.paint);
        break;
      }
    }
    label465:
    if (f5 < f2)
    {
      f3 = 0.0F;
      label476:
      if (f6 <= f2 + f7) {
        break label530;
      }
    }
    label530:
    for (float f4 = 1.0F;; f4 = (f6 - f2) / f7)
    {
      Utils.applyTrimPathIfNeeded(this.trimPathPath, f3, f4, 0.0F);
      paramCanvas.drawPath(this.trimPathPath, this.paint);
      break;
      f3 = (f5 - f2) / f7;
      break label476;
    }
  }
  
  public void draw(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    L.beginSection("StrokeContent#draw");
    float f = paramInt / 255.0F;
    paramInt = (int)(((Integer)this.opacityAnimation.getValue()).intValue() * f / 100.0F * 255.0F);
    this.paint.setAlpha(paramInt);
    this.paint.setStrokeWidth(((Float)this.widthAnimation.getValue()).floatValue() * Utils.getScale(paramMatrix));
    if (this.paint.getStrokeWidth() <= 0.0F)
    {
      L.endSection("StrokeContent#draw");
      return;
    }
    applyDashPatternIfNeeded(paramMatrix);
    paramInt = 0;
    if (paramInt >= this.pathGroups.size())
    {
      L.endSection("StrokeContent#draw");
      return;
    }
    PathGroup localPathGroup = (PathGroup)this.pathGroups.get(paramInt);
    int i;
    if (localPathGroup.trimPath == null)
    {
      L.beginSection("StrokeContent#buildPath");
      this.path.reset();
      i = localPathGroup.paths.size() - 1;
    }
    for (;;)
    {
      if (i < 0)
      {
        L.endSection("StrokeContent#buildPath");
        L.beginSection("StrokeContent#drawPath");
        paramCanvas.drawPath(this.path, this.paint);
        L.endSection("StrokeContent#drawPath");
        for (;;)
        {
          paramInt += 1;
          break;
          applyTrimPath(paramCanvas, localPathGroup, paramMatrix);
        }
      }
      this.path.addPath(((PathContent)localPathGroup.paths.get(i)).getPath(), paramMatrix);
      i -= 1;
    }
  }
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix)
  {
    L.beginSection("StrokeContent#getBounds");
    this.path.reset();
    int i = 0;
    if (i >= this.pathGroups.size())
    {
      this.path.computeBounds(this.rect, false);
      float f1 = ((Float)this.widthAnimation.getValue()).floatValue();
      paramMatrix = this.rect;
      float f2 = this.rect.left;
      float f3 = f1 / 2.0F;
      float f4 = this.rect.top;
      float f5 = f1 / 2.0F;
      float f6 = this.rect.right;
      float f7 = f1 / 2.0F;
      float f8 = this.rect.bottom;
      paramMatrix.set(f2 - f3, f4 - f5, f6 + f7, f1 / 2.0F + f8);
      paramRectF.set(this.rect);
      paramRectF.set(paramRectF.left - 1.0F, paramRectF.top - 1.0F, paramRectF.right + 1.0F, paramRectF.bottom + 1.0F);
      L.endSection("StrokeContent#getBounds");
      return;
    }
    PathGroup localPathGroup = (PathGroup)this.pathGroups.get(i);
    int j = 0;
    for (;;)
    {
      if (j >= localPathGroup.paths.size())
      {
        i += 1;
        break;
      }
      this.path.addPath(((PathContent)localPathGroup.paths.get(j)).getPath(), paramMatrix);
      j += 1;
    }
  }
  
  public void onValueChanged()
  {
    this.lottieDrawable.invalidateSelf();
  }
  
  public void setContents(List<Content> paramList1, List<Content> paramList2)
  {
    int i = paramList1.size() - 1;
    TrimPathContent localTrimPathContent = null;
    if (i < 0) {
      if (localTrimPathContent != null) {
        break label95;
      }
    }
    for (;;)
    {
      i = paramList2.size() - 1;
      paramList1 = null;
      if (i >= 0) {
        break label104;
      }
      if (paramList1 != null) {
        break label233;
      }
      return;
      localContent = (Content)paramList1.get(i);
      if (!(localContent instanceof TrimPathContent)) {}
      for (;;)
      {
        i -= 1;
        break;
        if (((TrimPathContent)localContent).getType() == ShapeTrimPath.Type.Individually) {
          localTrimPathContent = (TrimPathContent)localContent;
        }
      }
      label95:
      localTrimPathContent.addListener(this);
    }
    label104:
    Content localContent = (Content)paramList2.get(i);
    if (!(localContent instanceof TrimPathContent)) {}
    for (;;)
    {
      if (!(localContent instanceof PathContent))
      {
        i -= 1;
        break;
        if (((TrimPathContent)localContent).getType() == ShapeTrimPath.Type.Individually)
        {
          if (paramList1 == null) {}
          for (;;)
          {
            paramList1 = new PathGroup((TrimPathContent)localContent, null);
            ((TrimPathContent)localContent).addListener(this);
            break;
            this.pathGroups.add(paramList1);
          }
        }
      }
    }
    if (paramList1 != null) {}
    for (;;)
    {
      paramList1.paths.add((PathContent)localContent);
      break;
      paramList1 = new PathGroup(localTrimPathContent, null);
    }
    label233:
    this.pathGroups.add(paramList1);
  }
  
  private static final class PathGroup
  {
    private final List<PathContent> paths = new ArrayList();
    @Nullable
    private final TrimPathContent trimPath;
    
    private PathGroup(@Nullable TrimPathContent paramTrimPathContent)
    {
      this.trimPath = paramTrimPathContent;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\BaseStrokeContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */