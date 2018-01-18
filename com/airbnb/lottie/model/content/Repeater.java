package com.airbnb.lottie.model.content;

import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.animation.content.RepeaterContent;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue.Factory;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.animatable.AnimatableTransform.Factory;
import com.airbnb.lottie.model.layer.BaseLayer;
import org.json.JSONObject;

public class Repeater
  implements ContentModel
{
  private final AnimatableFloatValue copies;
  private final String name;
  private final AnimatableFloatValue offset;
  private final AnimatableTransform transform;
  
  Repeater(String paramString, AnimatableFloatValue paramAnimatableFloatValue1, AnimatableFloatValue paramAnimatableFloatValue2, AnimatableTransform paramAnimatableTransform)
  {
    this.name = paramString;
    this.copies = paramAnimatableFloatValue1;
    this.offset = paramAnimatableFloatValue2;
    this.transform = paramAnimatableTransform;
  }
  
  public AnimatableFloatValue getCopies()
  {
    return this.copies;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public AnimatableFloatValue getOffset()
  {
    return this.offset;
  }
  
  public AnimatableTransform getTransform()
  {
    return this.transform;
  }
  
  @Nullable
  public Content toContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer)
  {
    return new RepeaterContent(paramLottieDrawable, paramBaseLayer, this);
  }
  
  static final class Factory
  {
    static Repeater newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      return new Repeater(paramJSONObject.optString("nm"), AnimatableFloatValue.Factory.newInstance(paramJSONObject.optJSONObject("c"), paramLottieComposition, false), AnimatableFloatValue.Factory.newInstance(paramJSONObject.optJSONObject("o"), paramLottieComposition, false), AnimatableTransform.Factory.newInstance(paramJSONObject.optJSONObject("tr"), paramLottieComposition));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\content\Repeater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */