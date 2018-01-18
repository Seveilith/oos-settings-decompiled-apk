package com.airbnb.lottie.animation.keyframe;

import android.support.annotation.FloatRange;
import com.airbnb.lottie.animation.Keyframe;
import java.util.Collections;

public class StaticKeyframeAnimation<K, A>
  extends BaseKeyframeAnimation<K, A>
{
  private final A initialValue;
  
  public StaticKeyframeAnimation(A paramA)
  {
    super(Collections.emptyList());
    this.initialValue = paramA;
  }
  
  public void addUpdateListener(BaseKeyframeAnimation.AnimationListener paramAnimationListener) {}
  
  public A getValue()
  {
    return (A)this.initialValue;
  }
  
  public A getValue(Keyframe<K> paramKeyframe, float paramFloat)
  {
    return (A)this.initialValue;
  }
  
  public void setProgress(@FloatRange(from=0.0D, to=1.0D) float paramFloat) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\StaticKeyframeAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */