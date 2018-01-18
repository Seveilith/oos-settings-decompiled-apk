package com.airbnb.lottie.model.animatable;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.FloatKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.StaticKeyframeAnimation;
import com.airbnb.lottie.utils.JsonUtils;
import java.util.List;
import org.json.JSONObject;

public class AnimatableFloatValue
  extends BaseAnimatableValue<Float, Float>
{
  private AnimatableFloatValue()
  {
    super(Float.valueOf(0.0F));
  }
  
  private AnimatableFloatValue(List<Keyframe<Float>> paramList, Float paramFloat)
  {
    super(paramList, paramFloat);
  }
  
  public BaseKeyframeAnimation<Float, Float> createAnimation()
  {
    if (hasAnimation()) {
      return new FloatKeyframeAnimation(this.keyframes);
    }
    return new StaticKeyframeAnimation(this.initialValue);
  }
  
  public Float getInitialValue()
  {
    return (Float)this.initialValue;
  }
  
  public static final class Factory
  {
    static AnimatableFloatValue newInstance()
    {
      return new AnimatableFloatValue(null);
    }
    
    public static AnimatableFloatValue newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      return newInstance(paramJSONObject, paramLottieComposition, true);
    }
    
    public static AnimatableFloatValue newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition, boolean paramBoolean)
    {
      float f;
      if (!paramBoolean)
      {
        f = 1.0F;
        if (paramJSONObject != null) {
          break label51;
        }
      }
      for (;;)
      {
        paramJSONObject = AnimatableValueParser.newInstance(paramJSONObject, f, paramLottieComposition, AnimatableFloatValue.ValueFactory.INSTANCE).parseJson();
        return new AnimatableFloatValue(paramJSONObject.keyframes, (Float)paramJSONObject.initialValue, null);
        f = paramLottieComposition.getDpScale();
        break;
        label51:
        if (paramJSONObject.has("x")) {
          paramLottieComposition.addWarning("Lottie doesn't support expressions.");
        }
      }
    }
  }
  
  private static class ValueFactory
    implements AnimatableValue.Factory<Float>
  {
    static final ValueFactory INSTANCE = new ValueFactory();
    
    public Float valueFromObject(Object paramObject, float paramFloat)
    {
      return Float.valueOf(JsonUtils.valueFromObject(paramObject) * paramFloat);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableFloatValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */