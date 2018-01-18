package com.airbnb.lottie.model.animatable;

import com.airbnb.lottie.animation.Keyframe;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class BaseAnimatableValue<V, O>
  implements AnimatableValue<V, O>
{
  final V initialValue;
  final List<Keyframe<V>> keyframes;
  
  BaseAnimatableValue(V paramV)
  {
    this(Collections.emptyList(), paramV);
  }
  
  BaseAnimatableValue(List<Keyframe<V>> paramList, V paramV)
  {
    this.keyframes = paramList;
    this.initialValue = paramV;
  }
  
  O convertType(V paramV)
  {
    return paramV;
  }
  
  public O getInitialValue()
  {
    return (O)convertType(this.initialValue);
  }
  
  public boolean hasAnimation()
  {
    return !this.keyframes.isEmpty();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("parseInitialValue=").append(this.initialValue);
    if (this.keyframes.isEmpty()) {}
    for (;;)
    {
      return localStringBuilder.toString();
      localStringBuilder.append(", values=").append(Arrays.toString(this.keyframes.toArray()));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\BaseAnimatableValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */