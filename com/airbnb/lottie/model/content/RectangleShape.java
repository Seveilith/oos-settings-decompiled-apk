package com.airbnb.lottie.model.content;

import android.graphics.PointF;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.animation.content.RectangleContent;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue.Factory;
import com.airbnb.lottie.model.animatable.AnimatablePathValue;
import com.airbnb.lottie.model.animatable.AnimatablePointValue;
import com.airbnb.lottie.model.animatable.AnimatablePointValue.Factory;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.model.layer.BaseLayer;
import org.json.JSONObject;

public class RectangleShape
  implements ContentModel
{
  private final AnimatableFloatValue cornerRadius;
  private final String name;
  private final AnimatableValue<PointF, PointF> position;
  private final AnimatablePointValue size;
  
  private RectangleShape(String paramString, AnimatableValue<PointF, PointF> paramAnimatableValue, AnimatablePointValue paramAnimatablePointValue, AnimatableFloatValue paramAnimatableFloatValue)
  {
    this.name = paramString;
    this.position = paramAnimatableValue;
    this.size = paramAnimatablePointValue;
    this.cornerRadius = paramAnimatableFloatValue;
  }
  
  public AnimatableFloatValue getCornerRadius()
  {
    return this.cornerRadius;
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
    return new RectangleContent(paramLottieDrawable, paramBaseLayer, this);
  }
  
  public String toString()
  {
    return "RectangleShape{cornerRadius=" + this.cornerRadius.getInitialValue() + ", position=" + this.position + ", size=" + this.size + '}';
  }
  
  static class Factory
  {
    static RectangleShape newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      return new RectangleShape(paramJSONObject.optString("nm"), AnimatablePathValue.createAnimatablePathOrSplitDimensionPath(paramJSONObject.optJSONObject("p"), paramLottieComposition), AnimatablePointValue.Factory.newInstance(paramJSONObject.optJSONObject("s"), paramLottieComposition), AnimatableFloatValue.Factory.newInstance(paramJSONObject.optJSONObject("r"), paramLottieComposition), null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\content\RectangleShape.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */