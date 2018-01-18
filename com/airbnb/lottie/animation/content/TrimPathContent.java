package com.airbnb.lottie.animation.content;

import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.content.ShapeTrimPath;
import com.airbnb.lottie.model.content.ShapeTrimPath.Type;
import com.airbnb.lottie.model.layer.BaseLayer;
import java.util.ArrayList;
import java.util.List;

public class TrimPathContent
  implements Content, BaseKeyframeAnimation.AnimationListener
{
  private final BaseKeyframeAnimation<?, Float> endAnimation;
  private final List<BaseKeyframeAnimation.AnimationListener> listeners = new ArrayList();
  private String name;
  private final BaseKeyframeAnimation<?, Float> offsetAnimation;
  private final BaseKeyframeAnimation<?, Float> startAnimation;
  private final ShapeTrimPath.Type type;
  
  public TrimPathContent(BaseLayer paramBaseLayer, ShapeTrimPath paramShapeTrimPath)
  {
    this.name = paramShapeTrimPath.getName();
    this.type = paramShapeTrimPath.getType();
    this.startAnimation = paramShapeTrimPath.getStart().createAnimation();
    this.endAnimation = paramShapeTrimPath.getEnd().createAnimation();
    this.offsetAnimation = paramShapeTrimPath.getOffset().createAnimation();
    paramBaseLayer.addAnimation(this.startAnimation);
    paramBaseLayer.addAnimation(this.endAnimation);
    paramBaseLayer.addAnimation(this.offsetAnimation);
    this.startAnimation.addUpdateListener(this);
    this.endAnimation.addUpdateListener(this);
    this.offsetAnimation.addUpdateListener(this);
  }
  
  void addListener(BaseKeyframeAnimation.AnimationListener paramAnimationListener)
  {
    this.listeners.add(paramAnimationListener);
  }
  
  public BaseKeyframeAnimation<?, Float> getEnd()
  {
    return this.endAnimation;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public BaseKeyframeAnimation<?, Float> getOffset()
  {
    return this.offsetAnimation;
  }
  
  public BaseKeyframeAnimation<?, Float> getStart()
  {
    return this.startAnimation;
  }
  
  ShapeTrimPath.Type getType()
  {
    return this.type;
  }
  
  public void onValueChanged()
  {
    int i = 0;
    for (;;)
    {
      if (i >= this.listeners.size()) {
        return;
      }
      ((BaseKeyframeAnimation.AnimationListener)this.listeners.get(i)).onValueChanged();
      i += 1;
    }
  }
  
  public void setContents(List<Content> paramList1, List<Content> paramList2) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\TrimPathContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */