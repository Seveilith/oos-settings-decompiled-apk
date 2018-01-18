package com.airbnb.lottie.model.animatable;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.IntegerKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.StaticKeyframeAnimation;
import com.airbnb.lottie.utils.JsonUtils;
import java.util.List;
import org.json.JSONObject;

public class AnimatableIntegerValue
  extends BaseAnimatableValue<Integer, Integer>
{
  private AnimatableIntegerValue()
  {
    super(Integer.valueOf(100));
  }
  
  AnimatableIntegerValue(List<Keyframe<Integer>> paramList, Integer paramInteger)
  {
    super(paramList, paramInteger);
  }
  
  public BaseKeyframeAnimation<Integer, Integer> createAnimation()
  {
    if (hasAnimation()) {
      return new IntegerKeyframeAnimation(this.keyframes);
    }
    return new StaticKeyframeAnimation(this.initialValue);
  }
  
  public Integer getInitialValue()
  {
    return (Integer)this.initialValue;
  }
  
  public static final class Factory
  {
    static AnimatableIntegerValue newInstance()
    {
      return new AnimatableIntegerValue(null);
    }
    
    public static AnimatableIntegerValue newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      if (paramJSONObject == null) {}
      for (;;)
      {
        paramJSONObject = AnimatableValueParser.newInstance(paramJSONObject, 1.0F, paramLottieComposition, AnimatableIntegerValue.ValueFactory.access$100()).parseJson();
        paramLottieComposition = (Integer)paramJSONObject.initialValue;
        return new AnimatableIntegerValue(paramJSONObject.keyframes, paramLottieComposition);
        if (paramJSONObject.has("x")) {
          paramLottieComposition.addWarning("Lottie doesn't support expressions.");
        }
      }
    }
  }
  
  private static class ValueFactory
    implements AnimatableValue.Factory<Integer>
  {
    private static final ValueFactory INSTANCE = new ValueFactory();
    
    public Integer valueFromObject(Object paramObject, float paramFloat)
    {
      return Integer.valueOf(Math.round(JsonUtils.valueFromObject(paramObject) * paramFloat));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableIntegerValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */