package com.airbnb.lottie.animation.content;

import android.graphics.Path;
import android.graphics.Path.FillType;
import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.model.animatable.AnimatableShapeValue;
import com.airbnb.lottie.model.content.ShapePath;
import com.airbnb.lottie.model.content.ShapeTrimPath.Type;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.utils.Utils;
import java.util.List;

public class ShapeContent
  implements PathContent, BaseKeyframeAnimation.AnimationListener
{
  private boolean isPathValid;
  private final LottieDrawable lottieDrawable;
  private final String name;
  private final Path path = new Path();
  private final BaseKeyframeAnimation<?, Path> shapeAnimation;
  @Nullable
  private TrimPathContent trimPath;
  
  public ShapeContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer, ShapePath paramShapePath)
  {
    this.name = paramShapePath.getName();
    this.lottieDrawable = paramLottieDrawable;
    this.shapeAnimation = paramShapePath.getShapePath().createAnimation();
    paramBaseLayer.addAnimation(this.shapeAnimation);
    this.shapeAnimation.addUpdateListener(this);
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
      this.path.set((Path)this.shapeAnimation.getValue());
      this.path.setFillType(Path.FillType.EVEN_ODD);
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


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\ShapeContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */