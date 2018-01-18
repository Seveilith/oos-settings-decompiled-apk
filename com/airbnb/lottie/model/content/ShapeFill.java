package com.airbnb.lottie.model.content;

import android.graphics.Path.FillType;
import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.animation.content.FillContent;
import com.airbnb.lottie.model.animatable.AnimatableColorValue;
import com.airbnb.lottie.model.animatable.AnimatableColorValue.Factory;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue.Factory;
import com.airbnb.lottie.model.layer.BaseLayer;
import org.json.JSONObject;

public class ShapeFill
  implements ContentModel
{
  @Nullable
  private final AnimatableColorValue color;
  private final boolean fillEnabled;
  private final Path.FillType fillType;
  private final String name;
  @Nullable
  private final AnimatableIntegerValue opacity;
  
  private ShapeFill(String paramString, boolean paramBoolean, Path.FillType paramFillType, @Nullable AnimatableColorValue paramAnimatableColorValue, @Nullable AnimatableIntegerValue paramAnimatableIntegerValue)
  {
    this.name = paramString;
    this.fillEnabled = paramBoolean;
    this.fillType = paramFillType;
    this.color = paramAnimatableColorValue;
    this.opacity = paramAnimatableIntegerValue;
  }
  
  @Nullable
  public AnimatableColorValue getColor()
  {
    return this.color;
  }
  
  public Path.FillType getFillType()
  {
    return this.fillType;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  @Nullable
  public AnimatableIntegerValue getOpacity()
  {
    return this.opacity;
  }
  
  public Content toContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer)
  {
    return new FillContent(paramLottieDrawable, paramBaseLayer, this);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("ShapeFill{color=");
    if (this.color != null)
    {
      localObject = Integer.toHexString(((Integer)this.color.getInitialValue()).intValue());
      localStringBuilder = localStringBuilder.append((String)localObject).append(", fillEnabled=").append(this.fillEnabled).append(", opacity=");
      if (this.opacity == null) {
        break label95;
      }
    }
    label95:
    for (Object localObject = this.opacity.getInitialValue();; localObject = "null")
    {
      return localObject + '}';
      localObject = "null";
      break;
    }
  }
  
  static class Factory
  {
    static ShapeFill newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      String str = paramJSONObject.optString("nm");
      Object localObject = paramJSONObject.optJSONObject("c");
      JSONObject localJSONObject;
      label36:
      boolean bool;
      if (localObject == null)
      {
        localObject = null;
        localJSONObject = paramJSONObject.optJSONObject("o");
        if (localJSONObject != null) {
          break label82;
        }
        paramLottieComposition = null;
        bool = paramJSONObject.optBoolean("fillEnabled");
        if (paramJSONObject.optInt("r", 1) == 1) {
          break label92;
        }
      }
      label82:
      label92:
      for (paramJSONObject = Path.FillType.EVEN_ODD;; paramJSONObject = Path.FillType.WINDING)
      {
        return new ShapeFill(str, bool, paramJSONObject, (AnimatableColorValue)localObject, paramLottieComposition, null);
        localObject = AnimatableColorValue.Factory.newInstance((JSONObject)localObject, paramLottieComposition);
        break;
        paramLottieComposition = AnimatableIntegerValue.Factory.newInstance(localJSONObject, paramLottieComposition);
        break label36;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\content\ShapeFill.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */