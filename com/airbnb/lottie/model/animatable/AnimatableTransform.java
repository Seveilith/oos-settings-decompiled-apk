package com.airbnb.lottie.model.animatable;

import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.Log;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.animation.content.ModifierContent;
import com.airbnb.lottie.animation.keyframe.TransformKeyframeAnimation;
import com.airbnb.lottie.model.ScaleXY;
import com.airbnb.lottie.model.content.ContentModel;
import com.airbnb.lottie.model.layer.BaseLayer;
import java.util.Collections;
import org.json.JSONObject;

public class AnimatableTransform
  implements ModifierContent, ContentModel
{
  private final AnimatablePathValue anchorPoint;
  @Nullable
  private final AnimatableFloatValue endOpacity;
  private final AnimatableIntegerValue opacity;
  private final AnimatableValue<PointF, PointF> position;
  private final AnimatableFloatValue rotation;
  private final AnimatableScaleValue scale;
  @Nullable
  private final AnimatableFloatValue startOpacity;
  
  private AnimatableTransform(AnimatablePathValue paramAnimatablePathValue, AnimatableValue<PointF, PointF> paramAnimatableValue, AnimatableScaleValue paramAnimatableScaleValue, AnimatableFloatValue paramAnimatableFloatValue1, AnimatableIntegerValue paramAnimatableIntegerValue, @Nullable AnimatableFloatValue paramAnimatableFloatValue2, @Nullable AnimatableFloatValue paramAnimatableFloatValue3)
  {
    this.anchorPoint = paramAnimatablePathValue;
    this.position = paramAnimatableValue;
    this.scale = paramAnimatableScaleValue;
    this.rotation = paramAnimatableFloatValue1;
    this.opacity = paramAnimatableIntegerValue;
    this.startOpacity = paramAnimatableFloatValue2;
    this.endOpacity = paramAnimatableFloatValue3;
  }
  
  public TransformKeyframeAnimation createAnimation()
  {
    return new TransformKeyframeAnimation(this);
  }
  
  public AnimatablePathValue getAnchorPoint()
  {
    return this.anchorPoint;
  }
  
  @Nullable
  public AnimatableFloatValue getEndOpacity()
  {
    return this.endOpacity;
  }
  
  public AnimatableIntegerValue getOpacity()
  {
    return this.opacity;
  }
  
  public AnimatableValue<PointF, PointF> getPosition()
  {
    return this.position;
  }
  
  public AnimatableFloatValue getRotation()
  {
    return this.rotation;
  }
  
  public AnimatableScaleValue getScale()
  {
    return this.scale;
  }
  
  @Nullable
  public AnimatableFloatValue getStartOpacity()
  {
    return this.startOpacity;
  }
  
  @Nullable
  public Content toContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer)
  {
    return null;
  }
  
  public static class Factory
  {
    public static AnimatableTransform newInstance()
    {
      return new AnimatableTransform(new AnimatablePathValue(), new AnimatablePathValue(), AnimatableScaleValue.Factory.newInstance(), AnimatableFloatValue.Factory.newInstance(), AnimatableIntegerValue.Factory.newInstance(), AnimatableFloatValue.Factory.newInstance(), AnimatableFloatValue.Factory.newInstance(), null);
    }
    
    public static AnimatableTransform newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      Object localObject1 = paramJSONObject.optJSONObject("a");
      Object localObject2;
      label45:
      Object localObject3;
      label77:
      Object localObject4;
      label90:
      label103:
      Object localObject5;
      label133:
      Object localObject6;
      if (localObject1 == null)
      {
        Log.w("LOTTIE", "Layer has no transform property. You may be using an unsupported layer type such as a camera.");
        localObject1 = new AnimatablePathValue();
        localObject2 = paramJSONObject.optJSONObject("p");
        if (localObject2 != null) {
          break label200;
        }
        throwMissingTransform("position");
        localObject2 = null;
        localObject3 = paramJSONObject.optJSONObject("s");
        if (localObject3 != null) {
          break label209;
        }
        localObject3 = new AnimatableScaleValue(Collections.emptyList(), new ScaleXY());
        localObject4 = paramJSONObject.optJSONObject("r");
        if (localObject4 == null) {
          break label220;
        }
        if (localObject4 != null) {
          break label231;
        }
        throwMissingTransform("rotation");
        localObject4 = null;
        localObject5 = paramJSONObject.optJSONObject("o");
        if (localObject5 != null) {
          break label243;
        }
        localObject5 = new AnimatableIntegerValue(Collections.emptyList(), Integer.valueOf(100));
        localObject6 = paramJSONObject.optJSONObject("so");
        if (localObject6 != null) {
          break label254;
        }
        localObject6 = null;
        label149:
        paramJSONObject = paramJSONObject.optJSONObject("eo");
        if (paramJSONObject != null) {
          break label266;
        }
      }
      label200:
      label209:
      label220:
      label231:
      label243:
      label254:
      label266:
      for (paramJSONObject = null;; paramJSONObject = AnimatableFloatValue.Factory.newInstance(paramJSONObject, paramLottieComposition, false))
      {
        return new AnimatableTransform((AnimatablePathValue)localObject1, (AnimatableValue)localObject2, (AnimatableScaleValue)localObject3, (AnimatableFloatValue)localObject4, (AnimatableIntegerValue)localObject5, (AnimatableFloatValue)localObject6, paramJSONObject, null);
        localObject1 = new AnimatablePathValue(((JSONObject)localObject1).opt("k"), paramLottieComposition);
        break;
        localObject2 = AnimatablePathValue.createAnimatablePathOrSplitDimensionPath((JSONObject)localObject2, paramLottieComposition);
        break label45;
        localObject3 = AnimatableScaleValue.Factory.newInstance((JSONObject)localObject3, paramLottieComposition);
        break label77;
        localObject4 = paramJSONObject.optJSONObject("rz");
        break label90;
        localObject4 = AnimatableFloatValue.Factory.newInstance((JSONObject)localObject4, paramLottieComposition, false);
        break label103;
        localObject5 = AnimatableIntegerValue.Factory.newInstance((JSONObject)localObject5, paramLottieComposition);
        break label133;
        localObject6 = AnimatableFloatValue.Factory.newInstance((JSONObject)localObject6, paramLottieComposition, false);
        break label149;
      }
    }
    
    private static void throwMissingTransform(String paramString)
    {
      throw new IllegalArgumentException("Missing transform for " + paramString);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableTransform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */