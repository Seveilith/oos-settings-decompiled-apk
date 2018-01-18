package com.airbnb.lottie.model.animatable;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.ColorKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.StaticKeyframeAnimation;
import com.airbnb.lottie.model.ColorFactory;
import java.util.List;
import org.json.JSONObject;

public class AnimatableColorValue
  extends BaseAnimatableValue<Integer, Integer>
{
  private AnimatableColorValue(List<Keyframe<Integer>> paramList, Integer paramInteger)
  {
    super(paramList, paramInteger);
  }
  
  public BaseKeyframeAnimation<Integer, Integer> createAnimation()
  {
    if (hasAnimation()) {
      return new ColorKeyframeAnimation(this.keyframes);
    }
    return new StaticKeyframeAnimation(this.initialValue);
  }
  
  public String toString()
  {
    return "AnimatableColorValue{initialValue=" + this.initialValue + '}';
  }
  
  public static final class Factory
  {
    public static AnimatableColorValue newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      paramJSONObject = AnimatableValueParser.newInstance(paramJSONObject, 1.0F, paramLottieComposition, ColorFactory.INSTANCE).parseJson();
      return new AnimatableColorValue(paramJSONObject.keyframes, (Integer)paramJSONObject.initialValue, null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableColorValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */