package com.airbnb.lottie.model.animatable;

import android.graphics.PointF;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.PointKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.StaticKeyframeAnimation;
import com.airbnb.lottie.model.PointFFactory;
import java.util.List;
import org.json.JSONObject;

public class AnimatablePointValue
  extends BaseAnimatableValue<PointF, PointF>
{
  private AnimatablePointValue(List<Keyframe<PointF>> paramList, PointF paramPointF)
  {
    super(paramList, paramPointF);
  }
  
  public BaseKeyframeAnimation<PointF, PointF> createAnimation()
  {
    if (hasAnimation()) {
      return new PointKeyframeAnimation(this.keyframes);
    }
    return new StaticKeyframeAnimation(this.initialValue);
  }
  
  public static final class Factory
  {
    public static AnimatablePointValue newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      paramJSONObject = AnimatableValueParser.newInstance(paramJSONObject, paramLottieComposition.getDpScale(), paramLottieComposition, PointFFactory.INSTANCE).parseJson();
      return new AnimatablePointValue(paramJSONObject.keyframes, (PointF)paramJSONObject.initialValue, null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatablePointValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */