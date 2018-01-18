package com.airbnb.lottie.model.animatable;

import android.graphics.Path;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.ShapeKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.StaticKeyframeAnimation;
import com.airbnb.lottie.model.content.ShapeData;
import com.airbnb.lottie.model.content.ShapeData.Factory;
import com.airbnb.lottie.utils.MiscUtils;
import java.util.List;
import org.json.JSONObject;

public class AnimatableShapeValue
  extends BaseAnimatableValue<ShapeData, Path>
{
  private final Path convertTypePath = new Path();
  
  private AnimatableShapeValue(List<Keyframe<ShapeData>> paramList, ShapeData paramShapeData)
  {
    super(paramList, paramShapeData);
  }
  
  Path convertType(ShapeData paramShapeData)
  {
    this.convertTypePath.reset();
    MiscUtils.getPathFromData(paramShapeData, this.convertTypePath);
    return this.convertTypePath;
  }
  
  public BaseKeyframeAnimation<ShapeData, Path> createAnimation()
  {
    if (hasAnimation()) {
      return new ShapeKeyframeAnimation(this.keyframes);
    }
    return new StaticKeyframeAnimation(convertType((ShapeData)this.initialValue));
  }
  
  public static final class Factory
  {
    public static AnimatableShapeValue newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      paramJSONObject = AnimatableValueParser.newInstance(paramJSONObject, paramLottieComposition.getDpScale(), paramLottieComposition, ShapeData.Factory.INSTANCE).parseJson();
      return new AnimatableShapeValue(paramJSONObject.keyframes, (ShapeData)paramJSONObject.initialValue, null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableShapeValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */