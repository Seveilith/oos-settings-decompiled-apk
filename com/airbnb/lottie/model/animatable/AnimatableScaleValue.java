package com.airbnb.lottie.model.animatable;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.ScaleKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.StaticKeyframeAnimation;
import com.airbnb.lottie.model.ScaleXY;
import com.airbnb.lottie.model.ScaleXY.Factory;
import java.util.List;
import org.json.JSONObject;

public class AnimatableScaleValue
  extends BaseAnimatableValue<ScaleXY, ScaleXY>
{
  private AnimatableScaleValue()
  {
    super(new ScaleXY());
  }
  
  AnimatableScaleValue(List<Keyframe<ScaleXY>> paramList, ScaleXY paramScaleXY)
  {
    super(paramList, paramScaleXY);
  }
  
  public BaseKeyframeAnimation<ScaleXY, ScaleXY> createAnimation()
  {
    if (hasAnimation()) {
      return new ScaleKeyframeAnimation(this.keyframes);
    }
    return new StaticKeyframeAnimation(this.initialValue);
  }
  
  static final class Factory
  {
    static AnimatableScaleValue newInstance()
    {
      return new AnimatableScaleValue(null);
    }
    
    static AnimatableScaleValue newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      paramJSONObject = AnimatableValueParser.newInstance(paramJSONObject, 1.0F, paramLottieComposition, ScaleXY.Factory.INSTANCE).parseJson();
      return new AnimatableScaleValue(paramJSONObject.keyframes, (ScaleXY)paramJSONObject.initialValue);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableScaleValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */