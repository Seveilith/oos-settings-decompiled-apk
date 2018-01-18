package com.airbnb.lottie.model.animatable;

import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieComposition;
import org.json.JSONObject;

public class AnimatableTextProperties
{
  @Nullable
  public final AnimatableColorValue color;
  @Nullable
  public final AnimatableColorValue stroke;
  @Nullable
  public final AnimatableFloatValue strokeWidth;
  @Nullable
  public final AnimatableFloatValue tracking;
  
  AnimatableTextProperties(@Nullable AnimatableColorValue paramAnimatableColorValue1, @Nullable AnimatableColorValue paramAnimatableColorValue2, @Nullable AnimatableFloatValue paramAnimatableFloatValue1, @Nullable AnimatableFloatValue paramAnimatableFloatValue2)
  {
    this.color = paramAnimatableColorValue1;
    this.stroke = paramAnimatableColorValue2;
    this.strokeWidth = paramAnimatableFloatValue1;
    this.tracking = paramAnimatableFloatValue2;
  }
  
  public static final class Factory
  {
    public static AnimatableTextProperties newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      Object localObject3 = null;
      if (paramJSONObject == null) {}
      while (!paramJSONObject.has("a")) {
        return new AnimatableTextProperties(null, null, null, null);
      }
      JSONObject localJSONObject = paramJSONObject.optJSONObject("a");
      paramJSONObject = localJSONObject.optJSONObject("fc");
      Object localObject1;
      label64:
      Object localObject2;
      if (paramJSONObject == null)
      {
        paramJSONObject = null;
        localObject1 = localJSONObject.optJSONObject("sc");
        if (localObject1 != null) {
          break label116;
        }
        localObject1 = null;
        localObject2 = localJSONObject.optJSONObject("sw");
        if (localObject2 != null) {
          break label125;
        }
        localObject2 = null;
        label78:
        localJSONObject = localJSONObject.optJSONObject("t");
        if (localJSONObject != null) {
          break label134;
        }
      }
      label116:
      label125:
      label134:
      for (paramLottieComposition = (LottieComposition)localObject3;; paramLottieComposition = AnimatableFloatValue.Factory.newInstance(localJSONObject, paramLottieComposition))
      {
        return new AnimatableTextProperties(paramJSONObject, (AnimatableColorValue)localObject1, (AnimatableFloatValue)localObject2, paramLottieComposition);
        paramJSONObject = AnimatableColorValue.Factory.newInstance(paramJSONObject, paramLottieComposition);
        break;
        localObject1 = AnimatableColorValue.Factory.newInstance((JSONObject)localObject1, paramLottieComposition);
        break label64;
        localObject2 = AnimatableFloatValue.Factory.newInstance((JSONObject)localObject2, paramLottieComposition);
        break label78;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableTextProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */