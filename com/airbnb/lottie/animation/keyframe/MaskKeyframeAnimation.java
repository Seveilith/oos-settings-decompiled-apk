package com.airbnb.lottie.animation.keyframe;

import android.graphics.Path;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.animatable.AnimatableShapeValue;
import com.airbnb.lottie.model.content.Mask;
import com.airbnb.lottie.model.content.ShapeData;
import java.util.ArrayList;
import java.util.List;

public class MaskKeyframeAnimation
{
  private final List<BaseKeyframeAnimation<ShapeData, Path>> maskAnimations;
  private final List<Mask> masks;
  private final List<BaseKeyframeAnimation<Integer, Integer>> opacityAnimations;
  
  public MaskKeyframeAnimation(List<Mask> paramList)
  {
    this.masks = paramList;
    this.maskAnimations = new ArrayList(paramList.size());
    this.opacityAnimations = new ArrayList(paramList.size());
    int i = 0;
    for (;;)
    {
      if (i >= paramList.size()) {
        return;
      }
      this.maskAnimations.add(((Mask)paramList.get(i)).getMaskPath().createAnimation());
      AnimatableIntegerValue localAnimatableIntegerValue = ((Mask)paramList.get(i)).getOpacity();
      this.opacityAnimations.add(localAnimatableIntegerValue.createAnimation());
      i += 1;
    }
  }
  
  public List<BaseKeyframeAnimation<ShapeData, Path>> getMaskAnimations()
  {
    return this.maskAnimations;
  }
  
  public List<Mask> getMasks()
  {
    return this.masks;
  }
  
  public List<BaseKeyframeAnimation<Integer, Integer>> getOpacityAnimations()
  {
    return this.opacityAnimations;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\MaskKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */