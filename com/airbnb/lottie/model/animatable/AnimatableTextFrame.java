package com.airbnb.lottie.model.animatable;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.animation.keyframe.TextKeyframeAnimation;
import com.airbnb.lottie.model.DocumentData;
import com.airbnb.lottie.model.DocumentData.Factory;
import java.util.List;
import org.json.JSONObject;

public class AnimatableTextFrame
  extends BaseAnimatableValue<DocumentData, DocumentData>
{
  AnimatableTextFrame(List<Keyframe<DocumentData>> paramList, DocumentData paramDocumentData)
  {
    super(paramList, paramDocumentData);
  }
  
  public TextKeyframeAnimation createAnimation()
  {
    return new TextKeyframeAnimation(this.keyframes);
  }
  
  public static final class Factory
  {
    public static AnimatableTextFrame newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      if (paramJSONObject == null) {}
      for (;;)
      {
        paramJSONObject = AnimatableValueParser.newInstance(paramJSONObject, 1.0F, paramLottieComposition, AnimatableTextFrame.ValueFactory.access$000()).parseJson();
        return new AnimatableTextFrame(paramJSONObject.keyframes, (DocumentData)paramJSONObject.initialValue);
        if (paramJSONObject.has("x")) {
          paramLottieComposition.addWarning("Lottie doesn't support expressions.");
        }
      }
    }
  }
  
  private static class ValueFactory
    implements AnimatableValue.Factory<DocumentData>
  {
    private static final ValueFactory INSTANCE = new ValueFactory();
    
    public DocumentData valueFromObject(Object paramObject, float paramFloat)
    {
      return DocumentData.Factory.newInstance((JSONObject)paramObject);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableTextFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */