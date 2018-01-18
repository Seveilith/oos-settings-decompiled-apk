package com.airbnb.lottie.model.content;

import android.graphics.PointF;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.animation.content.EllipseContent;
import com.airbnb.lottie.model.animatable.AnimatablePathValue;
import com.airbnb.lottie.model.animatable.AnimatablePointValue;
import com.airbnb.lottie.model.animatable.AnimatablePointValue.Factory;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.model.layer.BaseLayer;
import org.json.JSONObject;

public class CircleShape
  implements ContentModel
{
  private final String name;
  private final AnimatableValue<PointF, PointF> position;
  private final AnimatablePointValue size;
  
  private CircleShape(String paramString, AnimatableValue<PointF, PointF> paramAnimatableValue, AnimatablePointValue paramAnimatablePointValue)
  {
    this.name = paramString;
    this.position = paramAnimatableValue;
    this.size = paramAnimatablePointValue;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public AnimatableValue<PointF, PointF> getPosition()
  {
    return this.position;
  }
  
  public AnimatablePointValue getSize()
  {
    return this.size;
  }
  
  public Content toContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer)
  {
    return new EllipseContent(paramLottieDrawable, paramBaseLayer, this);
  }
  
  static class Factory
  {
    static CircleShape newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      return new CircleShape(paramJSONObject.optString("nm"), AnimatablePathValue.createAnimatablePathOrSplitDimensionPath(paramJSONObject.optJSONObject("p"), paramLottieComposition), AnimatablePointValue.Factory.newInstance(paramJSONObject.optJSONObject("s"), paramLottieComposition), null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\content\CircleShape.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */