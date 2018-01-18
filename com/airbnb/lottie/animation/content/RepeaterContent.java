package com.airbnb.lottie.animation.content;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.animation.keyframe.TransformKeyframeAnimation;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.content.Repeater;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.utils.MiscUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class RepeaterContent
  implements DrawingContent, PathContent, GreedyContent, BaseKeyframeAnimation.AnimationListener
{
  private ContentGroup contentGroup;
  private final BaseKeyframeAnimation<Float, Float> copies;
  private final BaseLayer layer;
  private final LottieDrawable lottieDrawable;
  private final Matrix matrix = new Matrix();
  private final String name;
  private final BaseKeyframeAnimation<Float, Float> offset;
  private final Path path = new Path();
  private final TransformKeyframeAnimation transform;
  
  public RepeaterContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, Repeater paramRepeater)
  {
    this.lottieDrawable = paramLottieDrawable;
    this.layer = paramBaseLayer;
    this.name = paramRepeater.getName();
    this.copies = paramRepeater.getCopies().createAnimation();
    paramBaseLayer.addAnimation(this.copies);
    this.copies.addUpdateListener(this);
    this.offset = paramRepeater.getOffset().createAnimation();
    paramBaseLayer.addAnimation(this.offset);
    this.offset.addUpdateListener(this);
    this.transform = paramRepeater.getTransform().createAnimation();
    this.transform.addAnimationsToLayer(paramBaseLayer);
    this.transform.addListener(this);
  }
  
  public void absorbContent(ListIterator<Content> paramListIterator)
  {
    label16:
    ArrayList localArrayList;
    if (this.contentGroup == null)
    {
      if (paramListIterator.hasPrevious()) {
        break label62;
      }
      localArrayList = new ArrayList();
    }
    for (;;)
    {
      if (!paramListIterator.hasPrevious())
      {
        Collections.reverse(localArrayList);
        this.contentGroup = new ContentGroup(this.lottieDrawable, this.layer, "Repeater", localArrayList, null);
        return;
        return;
        label62:
        if (paramListIterator.previous() != this) {
          break;
        }
        break label16;
      }
      localArrayList.add(paramListIterator.previous());
      paramListIterator.remove();
    }
  }
  
  public void addColorFilter(@Nullable String paramString1, @Nullable String paramString2, @Nullable ColorFilter paramColorFilter)
  {
    this.contentGroup.addColorFilter(paramString1, paramString2, paramColorFilter);
  }
  
  public void draw(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    float f1 = ((Float)this.copies.getValue()).floatValue();
    float f2 = ((Float)this.offset.getValue()).floatValue();
    float f3 = ((Float)this.transform.getStartOpacity().getValue()).floatValue() / 100.0F;
    float f4 = ((Float)this.transform.getEndOpacity().getValue()).floatValue() / 100.0F;
    int i = (int)f1 - 1;
    for (;;)
    {
      if (i < 0) {
        return;
      }
      this.matrix.set(paramMatrix);
      this.matrix.preConcat(this.transform.getMatrixForRepeater(i + f2));
      float f5 = paramInt;
      float f6 = MiscUtils.lerp(f3, f4, i / f1);
      this.contentGroup.draw(paramCanvas, this.matrix, (int)(f5 * f6));
      i -= 1;
    }
  }
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix)
  {
    this.contentGroup.getBounds(paramRectF, paramMatrix);
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Path getPath()
  {
    Path localPath = this.contentGroup.getPath();
    this.path.reset();
    float f1 = ((Float)this.copies.getValue()).floatValue();
    float f2 = ((Float)this.offset.getValue()).floatValue();
    int i = (int)f1 - 1;
    for (;;)
    {
      if (i < 0) {
        return this.path;
      }
      this.matrix.set(this.transform.getMatrixForRepeater(i + f2));
      this.path.addPath(localPath, this.matrix);
      i -= 1;
    }
  }
  
  public void onValueChanged()
  {
    this.lottieDrawable.invalidateSelf();
  }
  
  public void setContents(List<Content> paramList1, List<Content> paramList2)
  {
    this.contentGroup.setContents(paramList1, paramList2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\RepeaterContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */