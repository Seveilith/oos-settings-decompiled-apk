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
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.content.ContentModel;
import com.airbnb.lottie.model.content.ShapeGroup;
import com.airbnb.lottie.model.layer.BaseLayer;
import java.util.ArrayList;
import java.util.List;

public class ContentGroup
  implements DrawingContent, PathContent, BaseKeyframeAnimation.AnimationListener
{
  private final List<Content> contents;
  private final LottieDrawable lottieDrawable;
  private final Matrix matrix = new Matrix();
  private final String name;
  private final Path path = new Path();
  @Nullable
  private List<PathContent> pathContents;
  private final RectF rect = new RectF();
  @Nullable
  private TransformKeyframeAnimation transformAnimation;
  
  public ContentGroup(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, ShapeGroup paramShapeGroup)
  {
    this(paramLottieDrawable, paramBaseLayer, paramShapeGroup.getName(), contentsFromModels(paramLottieDrawable, paramBaseLayer, paramShapeGroup.getItems()), findTransform(paramShapeGroup.getItems()));
  }
  
  ContentGroup(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, String paramString, List<Content> paramList, @Nullable AnimatableTransform paramAnimatableTransform)
  {
    this.name = paramString;
    this.lottieDrawable = paramLottieDrawable;
    this.contents = paramList;
    int i;
    if (paramAnimatableTransform == null)
    {
      paramLottieDrawable = new ArrayList();
      i = paramList.size() - 1;
      if (i >= 0) {
        break label126;
      }
      i = paramLottieDrawable.size() - 1;
    }
    for (;;)
    {
      if (i < 0)
      {
        return;
        this.transformAnimation = paramAnimatableTransform.createAnimation();
        this.transformAnimation.addAnimationsToLayer(paramBaseLayer);
        this.transformAnimation.addListener(this);
        break;
        label126:
        paramBaseLayer = (Content)paramList.get(i);
        if (!(paramBaseLayer instanceof GreedyContent)) {}
        for (;;)
        {
          i -= 1;
          break;
          paramLottieDrawable.add((GreedyContent)paramBaseLayer);
        }
      }
      ((GreedyContent)paramLottieDrawable.get(i)).absorbContent(paramList.listIterator(paramList.size()));
      i -= 1;
    }
  }
  
  private static List<Content> contentsFromModels(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, List<ContentModel> paramList)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    int i = 0;
    if (i >= paramList.size()) {
      return localArrayList;
    }
    Content localContent = ((ContentModel)paramList.get(i)).toContent(paramLottieDrawable, paramBaseLayer);
    if (localContent == null) {}
    for (;;)
    {
      i += 1;
      break;
      localArrayList.add(localContent);
    }
  }
  
  @Nullable
  static AnimatableTransform findTransform(List<ContentModel> paramList)
  {
    int i = 0;
    ContentModel localContentModel;
    for (;;)
    {
      if (i >= paramList.size()) {
        return null;
      }
      localContentModel = (ContentModel)paramList.get(i);
      if ((localContentModel instanceof AnimatableTransform)) {
        break;
      }
      i += 1;
    }
    return (AnimatableTransform)localContentModel;
  }
  
  public void addColorFilter(@Nullable String paramString1, @Nullable String paramString2, @Nullable ColorFilter paramColorFilter)
  {
    int i = 0;
    if (i >= this.contents.size()) {
      return;
    }
    Content localContent = (Content)this.contents.get(i);
    if (!(localContent instanceof DrawingContent)) {}
    for (;;)
    {
      i += 1;
      break;
      DrawingContent localDrawingContent = (DrawingContent)localContent;
      if (paramString2 == null) {}
      while (paramString2.equals(localContent.getName()))
      {
        localDrawingContent.addColorFilter(paramString1, null, paramColorFilter);
        break;
      }
      localDrawingContent.addColorFilter(paramString1, paramString2, paramColorFilter);
    }
  }
  
  public void draw(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    this.matrix.set(paramMatrix);
    if (this.transformAnimation == null) {}
    int i;
    for (;;)
    {
      i = this.contents.size() - 1;
      if (i >= 0) {
        break;
      }
      return;
      this.matrix.preConcat(this.transformAnimation.getMatrix());
      paramInt = (int)(((Integer)this.transformAnimation.getOpacity().getValue()).intValue() / 100.0F * paramInt / 255.0F * 255.0F);
    }
    paramMatrix = this.contents.get(i);
    if (!(paramMatrix instanceof DrawingContent)) {}
    for (;;)
    {
      i -= 1;
      break;
      ((DrawingContent)paramMatrix).draw(paramCanvas, this.matrix, paramInt);
    }
  }
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix)
  {
    this.matrix.set(paramMatrix);
    if (this.transformAnimation == null) {}
    int i;
    for (;;)
    {
      this.rect.set(0.0F, 0.0F, 0.0F, 0.0F);
      i = this.contents.size() - 1;
      if (i >= 0) {
        break;
      }
      return;
      this.matrix.preConcat(this.transformAnimation.getMatrix());
    }
    paramMatrix = (Content)this.contents.get(i);
    if (!(paramMatrix instanceof DrawingContent)) {}
    for (;;)
    {
      i -= 1;
      break;
      ((DrawingContent)paramMatrix).getBounds(this.rect, this.matrix);
      if (!paramRectF.isEmpty()) {
        paramRectF.set(Math.min(paramRectF.left, this.rect.left), Math.min(paramRectF.top, this.rect.top), Math.max(paramRectF.right, this.rect.right), Math.max(paramRectF.bottom, this.rect.bottom));
      } else {
        paramRectF.set(this.rect);
      }
    }
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Path getPath()
  {
    this.matrix.reset();
    if (this.transformAnimation == null) {}
    int i;
    for (;;)
    {
      this.path.reset();
      i = this.contents.size() - 1;
      if (i >= 0) {
        break;
      }
      return this.path;
      this.matrix.set(this.transformAnimation.getMatrix());
    }
    Content localContent = (Content)this.contents.get(i);
    if (!(localContent instanceof PathContent)) {}
    for (;;)
    {
      i -= 1;
      break;
      this.path.addPath(((PathContent)localContent).getPath(), this.matrix);
    }
  }
  
  List<PathContent> getPathList()
  {
    if (this.pathContents != null) {
      return this.pathContents;
    }
    this.pathContents = new ArrayList();
    int i = 0;
    label25:
    Content localContent;
    if (i < this.contents.size())
    {
      localContent = (Content)this.contents.get(i);
      if ((localContent instanceof PathContent)) {
        break label66;
      }
    }
    for (;;)
    {
      i += 1;
      break label25;
      break;
      label66:
      this.pathContents.add((PathContent)localContent);
    }
  }
  
  Matrix getTransformationMatrix()
  {
    if (this.transformAnimation == null)
    {
      this.matrix.reset();
      return this.matrix;
    }
    return this.transformAnimation.getMatrix();
  }
  
  public void onValueChanged()
  {
    this.lottieDrawable.invalidateSelf();
  }
  
  public void setContents(List<Content> paramList1, List<Content> paramList2)
  {
    paramList2 = new ArrayList(paramList1.size() + this.contents.size());
    paramList2.addAll(paramList1);
    int i = this.contents.size() - 1;
    for (;;)
    {
      if (i < 0) {
        return;
      }
      paramList1 = (Content)this.contents.get(i);
      paramList1.setContents(paramList2, this.contents.subList(0, i));
      paramList2.add(paramList1);
      i -= 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\ContentGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */