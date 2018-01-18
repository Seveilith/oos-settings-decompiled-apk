package com.airbnb.lottie.model.animatable;

import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.animation.Keyframe.Factory;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class AnimatableValueParser<T>
{
  private final LottieComposition composition;
  @Nullable
  private final JSONObject json;
  private final float scale;
  private final AnimatableValue.Factory<T> valueFactory;
  
  private AnimatableValueParser(@Nullable JSONObject paramJSONObject, float paramFloat, LottieComposition paramLottieComposition, AnimatableValue.Factory<T> paramFactory)
  {
    this.json = paramJSONObject;
    this.scale = paramFloat;
    this.composition = paramLottieComposition;
    this.valueFactory = paramFactory;
  }
  
  private static boolean hasKeyframes(Object paramObject)
  {
    if ((paramObject instanceof JSONArray))
    {
      paramObject = ((JSONArray)paramObject).opt(0);
      if ((paramObject instanceof JSONObject)) {
        break label27;
      }
    }
    label27:
    while (!((JSONObject)paramObject).has("t"))
    {
      return false;
      return false;
    }
    return true;
  }
  
  static <T> AnimatableValueParser<T> newInstance(@Nullable JSONObject paramJSONObject, float paramFloat, LottieComposition paramLottieComposition, AnimatableValue.Factory<T> paramFactory)
  {
    return new AnimatableValueParser(paramJSONObject, paramFloat, paramLottieComposition, paramFactory);
  }
  
  @Nullable
  private T parseInitialValue(List<Keyframe<T>> paramList)
  {
    if (this.json == null) {
      return null;
    }
    if (paramList.isEmpty()) {
      return (T)this.valueFactory.valueFromObject(this.json.opt("k"), this.scale);
    }
    return (T)((Keyframe)paramList.get(0)).startValue;
  }
  
  private List<Keyframe<T>> parseKeyframes()
  {
    if (this.json == null) {
      return Collections.emptyList();
    }
    Object localObject = this.json.opt("k");
    if (!hasKeyframes(localObject)) {
      return Collections.emptyList();
    }
    return Keyframe.Factory.parseKeyframes((JSONArray)localObject, this.composition, this.scale, this.valueFactory);
  }
  
  Result<T> parseJson()
  {
    List localList = parseKeyframes();
    return new Result(localList, parseInitialValue(localList));
  }
  
  static class Result<T>
  {
    @Nullable
    final T initialValue;
    final List<Keyframe<T>> keyframes;
    
    Result(List<Keyframe<T>> paramList, @Nullable T paramT)
    {
      this.keyframes = paramList;
      this.initialValue = paramT;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableValueParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */