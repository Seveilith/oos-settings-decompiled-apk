package com.airbnb.lottie.model.content;

import android.graphics.Path.FillType;
import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.animation.content.GradientFillContent;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableGradientColorValue;
import com.airbnb.lottie.model.animatable.AnimatableGradientColorValue.Factory;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue.Factory;
import com.airbnb.lottie.model.animatable.AnimatablePointValue;
import com.airbnb.lottie.model.animatable.AnimatablePointValue.Factory;
import com.airbnb.lottie.model.layer.BaseLayer;
import org.json.JSONException;
import org.json.JSONObject;

public class GradientFill
  implements ContentModel
{
  private final AnimatablePointValue endPoint;
  private final Path.FillType fillType;
  private final AnimatableGradientColorValue gradientColor;
  private final GradientType gradientType;
  @Nullable
  private final AnimatableFloatValue highlightAngle;
  @Nullable
  private final AnimatableFloatValue highlightLength;
  private final String name;
  private final AnimatableIntegerValue opacity;
  private final AnimatablePointValue startPoint;
  
  private GradientFill(String paramString, GradientType paramGradientType, Path.FillType paramFillType, AnimatableGradientColorValue paramAnimatableGradientColorValue, AnimatableIntegerValue paramAnimatableIntegerValue, AnimatablePointValue paramAnimatablePointValue1, AnimatablePointValue paramAnimatablePointValue2, AnimatableFloatValue paramAnimatableFloatValue1, AnimatableFloatValue paramAnimatableFloatValue2)
  {
    this.gradientType = paramGradientType;
    this.fillType = paramFillType;
    this.gradientColor = paramAnimatableGradientColorValue;
    this.opacity = paramAnimatableIntegerValue;
    this.startPoint = paramAnimatablePointValue1;
    this.endPoint = paramAnimatablePointValue2;
    this.name = paramString;
    this.highlightLength = paramAnimatableFloatValue1;
    this.highlightAngle = paramAnimatableFloatValue2;
  }
  
  public AnimatablePointValue getEndPoint()
  {
    return this.endPoint;
  }
  
  public Path.FillType getFillType()
  {
    return this.fillType;
  }
  
  public AnimatableGradientColorValue getGradientColor()
  {
    return this.gradientColor;
  }
  
  public GradientType getGradientType()
  {
    return this.gradientType;
  }
  
  @Nullable
  AnimatableFloatValue getHighlightAngle()
  {
    return this.highlightAngle;
  }
  
  @Nullable
  AnimatableFloatValue getHighlightLength()
  {
    return this.highlightLength;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public AnimatableIntegerValue getOpacity()
  {
    return this.opacity;
  }
  
  public AnimatablePointValue getStartPoint()
  {
    return this.startPoint;
  }
  
  public Content toContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer)
  {
    return new GradientFillContent(paramLottieDrawable, paramBaseLayer, this);
  }
  
  static class Factory
  {
    static GradientFill newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      String str = paramJSONObject.optString("nm");
      JSONObject localJSONObject = paramJSONObject.optJSONObject("g");
      Object localObject1;
      label30:
      label46:
      Path.FillType localFillType;
      label62:
      GradientType localGradientType;
      label78:
      Object localObject2;
      if (localJSONObject == null)
      {
        localObject1 = localJSONObject;
        if (localObject1 != null) {
          break label175;
        }
        localObject1 = null;
        localJSONObject = paramJSONObject.optJSONObject("o");
        if (localJSONObject != null) {
          break label184;
        }
        localJSONObject = null;
        if (paramJSONObject.optInt("r", 1) == 1) {
          break label195;
        }
        localFillType = Path.FillType.EVEN_ODD;
        if (paramJSONObject.optInt("t", 1) == 1) {
          break label203;
        }
        localGradientType = GradientType.Radial;
        localObject2 = paramJSONObject.optJSONObject("s");
        if (localObject2 != null) {
          break label211;
        }
        localObject2 = null;
        label94:
        paramJSONObject = paramJSONObject.optJSONObject("e");
        if (paramJSONObject != null) {
          break label222;
        }
      }
      label175:
      label184:
      label195:
      label203:
      label211:
      label222:
      for (paramJSONObject = null;; paramJSONObject = AnimatablePointValue.Factory.newInstance(paramJSONObject, paramLottieComposition))
      {
        return new GradientFill(str, localGradientType, localFillType, (AnimatableGradientColorValue)localObject1, localJSONObject, (AnimatablePointValue)localObject2, paramJSONObject, null, null, null);
        localObject1 = localJSONObject;
        if (!localJSONObject.has("k")) {
          break;
        }
        int i = localJSONObject.optInt("p");
        localObject1 = localJSONObject.optJSONObject("k");
        try
        {
          ((JSONObject)localObject1).put("p", i);
        }
        catch (JSONException localJSONException) {}
        break;
        localObject1 = AnimatableGradientColorValue.Factory.newInstance((JSONObject)localObject1, paramLottieComposition);
        break label30;
        AnimatableIntegerValue localAnimatableIntegerValue = AnimatableIntegerValue.Factory.newInstance(localJSONException, paramLottieComposition);
        break label46;
        localFillType = Path.FillType.WINDING;
        break label62;
        localGradientType = GradientType.Linear;
        break label78;
        localObject2 = AnimatablePointValue.Factory.newInstance((JSONObject)localObject2, paramLottieComposition);
        break label94;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\content\GradientFill.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */