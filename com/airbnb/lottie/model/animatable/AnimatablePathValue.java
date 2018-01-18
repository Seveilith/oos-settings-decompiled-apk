package com.airbnb.lottie.model.animatable;

import android.graphics.PointF;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.PathKeyframe;
import com.airbnb.lottie.animation.keyframe.PathKeyframe.Factory;
import com.airbnb.lottie.animation.keyframe.PathKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.StaticKeyframeAnimation;
import com.airbnb.lottie.utils.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class AnimatablePathValue
  implements AnimatableValue<PointF, PointF>
{
  private PointF initialPoint;
  private final List<PathKeyframe> keyframes = new ArrayList();
  
  AnimatablePathValue()
  {
    this.initialPoint = new PointF(0.0F, 0.0F);
  }
  
  AnimatablePathValue(Object paramObject, LottieComposition paramLottieComposition)
  {
    if (!hasKeyframes(paramObject))
    {
      this.initialPoint = JsonUtils.pointFromJsonArray((JSONArray)paramObject, paramLottieComposition.getDpScale());
      return;
    }
    paramObject = (JSONArray)paramObject;
    int j = ((JSONArray)paramObject).length();
    for (;;)
    {
      if (i >= j)
      {
        Keyframe.setEndFrames(this.keyframes);
        return;
      }
      PathKeyframe localPathKeyframe = PathKeyframe.Factory.newInstance(((JSONArray)paramObject).optJSONObject(i), paramLottieComposition, ValueFactory.INSTANCE);
      this.keyframes.add(localPathKeyframe);
      i += 1;
    }
  }
  
  public static AnimatableValue<PointF, PointF> createAnimatablePathOrSplitDimensionPath(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
  {
    if (!paramJSONObject.has("k")) {
      return new AnimatableSplitDimensionPathValue(AnimatableFloatValue.Factory.newInstance(paramJSONObject.optJSONObject("x"), paramLottieComposition), AnimatableFloatValue.Factory.newInstance(paramJSONObject.optJSONObject("y"), paramLottieComposition));
    }
    return new AnimatablePathValue(paramJSONObject.opt("k"), paramLottieComposition);
  }
  
  private boolean hasKeyframes(Object paramObject)
  {
    if ((paramObject instanceof JSONArray))
    {
      paramObject = ((JSONArray)paramObject).opt(0);
      if ((paramObject instanceof JSONObject)) {
        break label27;
      }
    }
    label27:
    while (!((JSONObject)paramObject).has("t"))
    {
      return false;
      return false;
    }
    return true;
  }
  
  public BaseKeyframeAnimation<PointF, PointF> createAnimation()
  {
    if (hasAnimation()) {
      return new PathKeyframeAnimation(this.keyframes);
    }
    return new StaticKeyframeAnimation(this.initialPoint);
  }
  
  public boolean hasAnimation()
  {
    return !this.keyframes.isEmpty();
  }
  
  public String toString()
  {
    return "initialPoint=" + this.initialPoint;
  }
  
  private static class ValueFactory
    implements AnimatableValue.Factory<PointF>
  {
    private static final AnimatableValue.Factory<PointF> INSTANCE = new ValueFactory();
    
    public PointF valueFromObject(Object paramObject, float paramFloat)
    {
      return JsonUtils.pointFromJsonArray((JSONArray)paramObject, paramFloat);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatablePathValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */