package com.airbnb.lottie.model.content;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.animation.content.ShapeContent;
import com.airbnb.lottie.model.animatable.AnimatableShapeValue;
import com.airbnb.lottie.model.animatable.AnimatableShapeValue.Factory;
import com.airbnb.lottie.model.layer.BaseLayer;
import org.json.JSONObject;

public class ShapePath
  implements ContentModel
{
  private final int index;
  private final String name;
  private final AnimatableShapeValue shapePath;
  
  private ShapePath(String paramString, int paramInt, AnimatableShapeValue paramAnimatableShapeValue)
  {
    this.name = paramString;
    this.index = paramInt;
    this.shapePath = paramAnimatableShapeValue;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public AnimatableShapeValue getShapePath()
  {
    return this.shapePath;
  }
  
  public Content toContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer)
  {
    return new ShapeContent(paramLottieDrawable, paramBaseLayer, this);
  }
  
  public String toString()
  {
    return "ShapePath{name=" + this.name + ", index=" + this.index + ", hasAnimation=" + this.shapePath.hasAnimation() + '}';
  }
  
  static class Factory
  {
    static ShapePath newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      paramLottieComposition = AnimatableShapeValue.Factory.newInstance(paramJSONObject.optJSONObject("ks"), paramLottieComposition);
      return new ShapePath(paramJSONObject.optString("nm"), paramJSONObject.optInt("ind"), paramLottieComposition, null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\content\ShapePath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */