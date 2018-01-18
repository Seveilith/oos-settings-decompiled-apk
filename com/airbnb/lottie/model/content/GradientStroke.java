package com.airbnb.lottie.model.content;

import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.animation.content.GradientStrokeContent;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue.Factory;
import com.airbnb.lottie.model.animatable.AnimatableGradientColorValue;
import com.airbnb.lottie.model.animatable.AnimatableGradientColorValue.Factory;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue.Factory;
import com.airbnb.lottie.model.animatable.AnimatablePointValue;
import com.airbnb.lottie.model.animatable.AnimatablePointValue.Factory;
import com.airbnb.lottie.model.layer.BaseLayer;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class GradientStroke
  implements ContentModel
{
  private final ShapeStroke.LineCapType capType;
  @Nullable
  private final AnimatableFloatValue dashOffset;
  private final AnimatablePointValue endPoint;
  private final AnimatableGradientColorValue gradientColor;
  private final GradientType gradientType;
  private final ShapeStroke.LineJoinType joinType;
  private final List<AnimatableFloatValue> lineDashPattern;
  private final String name;
  private final AnimatableIntegerValue opacity;
  private final AnimatablePointValue startPoint;
  private final AnimatableFloatValue width;
  
  private GradientStroke(String paramString, GradientType paramGradientType, AnimatableGradientColorValue paramAnimatableGradientColorValue, AnimatableIntegerValue paramAnimatableIntegerValue, AnimatablePointValue paramAnimatablePointValue1, AnimatablePointValue paramAnimatablePointValue2, AnimatableFloatValue paramAnimatableFloatValue1, ShapeStroke.LineCapType paramLineCapType, ShapeStroke.LineJoinType paramLineJoinType, List<AnimatableFloatValue> paramList, @Nullable AnimatableFloatValue paramAnimatableFloatValue2)
  {
    this.name = paramString;
    this.gradientType = paramGradientType;
    this.gradientColor = paramAnimatableGradientColorValue;
    this.opacity = paramAnimatableIntegerValue;
    this.startPoint = paramAnimatablePointValue1;
    this.endPoint = paramAnimatablePointValue2;
    this.width = paramAnimatableFloatValue1;
    this.capType = paramLineCapType;
    this.joinType = paramLineJoinType;
    this.lineDashPattern = paramList;
    this.dashOffset = paramAnimatableFloatValue2;
  }
  
  public ShapeStroke.LineCapType getCapType()
  {
    return this.capType;
  }
  
  @Nullable
  public AnimatableFloatValue getDashOffset()
  {
    return this.dashOffset;
  }
  
  public AnimatablePointValue getEndPoint()
  {
    return this.endPoint;
  }
  
  public AnimatableGradientColorValue getGradientColor()
  {
    return this.gradientColor;
  }
  
  public GradientType getGradientType()
  {
    return this.gradientType;
  }
  
  public ShapeStroke.LineJoinType getJoinType()
  {
    return this.joinType;
  }
  
  public List<AnimatableFloatValue> getLineDashPattern()
  {
    return this.lineDashPattern;
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
  
  public AnimatableFloatValue getWidth()
  {
    return this.width;
  }
  
  public Content toContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer)
  {
    return new GradientStrokeContent(paramLottieDrawable, paramBaseLayer, this);
  }
  
  static class Factory
  {
    static GradientStroke newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      String str1 = paramJSONObject.optString("nm");
      Object localObject2 = paramJSONObject.optJSONObject("g");
      Object localObject1;
      label34:
      Object localObject3;
      label50:
      label66:
      Object localObject4;
      AnimatablePointValue localAnimatablePointValue;
      label82:
      label98:
      AnimatableFloatValue localAnimatableFloatValue;
      ShapeStroke.LineCapType localLineCapType;
      ShapeStroke.LineJoinType localLineJoinType;
      JSONArray localJSONArray;
      ArrayList localArrayList;
      if (localObject2 == null)
      {
        localObject1 = localObject2;
        localObject2 = null;
        if (localObject1 != null) {
          break label218;
        }
        localObject1 = localObject2;
        localObject3 = paramJSONObject.optJSONObject("o");
        localObject2 = null;
        if (localObject3 != null) {
          break label227;
        }
        if (paramJSONObject.optInt("t", 1) == 1) {
          break label238;
        }
        localObject3 = GradientType.Radial;
        localObject4 = paramJSONObject.optJSONObject("s");
        localAnimatablePointValue = null;
        if (localObject4 != null) {
          break label246;
        }
        localJSONObject = paramJSONObject.optJSONObject("e");
        localObject4 = null;
        if (localJSONObject != null) {
          break label257;
        }
        localAnimatableFloatValue = AnimatableFloatValue.Factory.newInstance(paramJSONObject.optJSONObject("w"), paramLottieComposition);
        localLineCapType = ShapeStroke.LineCapType.values()[(paramJSONObject.optInt("lc") - 1)];
        localLineJoinType = ShapeStroke.LineJoinType.values()[(paramJSONObject.optInt("lj") - 1)];
        localJSONObject = null;
        localJSONArray = null;
        localArrayList = new ArrayList();
        if (paramJSONObject.has("d")) {
          break label268;
        }
        paramLottieComposition = localJSONArray;
      }
      label218:
      label227:
      label238:
      label246:
      label257:
      label268:
      int i;
      for (;;)
      {
        return new GradientStroke(str1, (GradientType)localObject3, (AnimatableGradientColorValue)localObject1, (AnimatableIntegerValue)localObject2, localAnimatablePointValue, (AnimatablePointValue)localObject4, localAnimatableFloatValue, localLineCapType, localLineJoinType, localArrayList, paramLottieComposition, null);
        localObject1 = localObject2;
        if (!((JSONObject)localObject2).has("k")) {
          break;
        }
        localObject1 = ((JSONObject)localObject2).optJSONObject("k");
        break;
        localObject1 = AnimatableGradientColorValue.Factory.newInstance((JSONObject)localObject1, paramLottieComposition);
        break label34;
        localObject2 = AnimatableIntegerValue.Factory.newInstance((JSONObject)localObject3, paramLottieComposition);
        break label50;
        localObject3 = GradientType.Linear;
        break label66;
        localAnimatablePointValue = AnimatablePointValue.Factory.newInstance((JSONObject)localObject4, paramLottieComposition);
        break label82;
        localObject4 = AnimatablePointValue.Factory.newInstance(localJSONObject, paramLottieComposition);
        break label98;
        localJSONArray = paramJSONObject.optJSONArray("d");
        i = 0;
        paramJSONObject = localJSONObject;
        if (i < localJSONArray.length()) {
          break label324;
        }
        paramLottieComposition = paramJSONObject;
        if (localArrayList.size() == 1)
        {
          localArrayList.add(localArrayList.get(0));
          paramLottieComposition = paramJSONObject;
        }
      }
      label324:
      JSONObject localJSONObject = localJSONArray.optJSONObject(i);
      String str2 = localJSONObject.optString("n");
      if (!str2.equals("o"))
      {
        if (!str2.equals("d")) {
          break label402;
        }
        label361:
        localArrayList.add(AnimatableFloatValue.Factory.newInstance(localJSONObject.optJSONObject("v"), paramLottieComposition));
      }
      for (;;)
      {
        i += 1;
        break;
        paramJSONObject = AnimatableFloatValue.Factory.newInstance(localJSONObject.optJSONObject("v"), paramLottieComposition);
        continue;
        label402:
        if (str2.equals("g")) {
          break label361;
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\content\GradientStroke.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */