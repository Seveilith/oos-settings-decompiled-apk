package com.airbnb.lottie.animation.keyframe;

import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.view.animation.Interpolator;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.animation.Keyframe.Factory;
import com.airbnb.lottie.model.animatable.AnimatableValue.Factory;
import com.airbnb.lottie.utils.JsonUtils;
import com.airbnb.lottie.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

public class PathKeyframe
  extends Keyframe<PointF>
{
  @Nullable
  private Path path;
  
  private PathKeyframe(LottieComposition paramLottieComposition, @Nullable PointF paramPointF1, @Nullable PointF paramPointF2, @Nullable Interpolator paramInterpolator, float paramFloat, @Nullable Float paramFloat1)
  {
    super(paramLottieComposition, paramPointF1, paramPointF2, paramInterpolator, paramFloat, paramFloat1);
  }
  
  @Nullable
  Path getPath()
  {
    return this.path;
  }
  
  public static class Factory
  {
    public static PathKeyframe newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition, AnimatableValue.Factory<PointF> paramFactory)
    {
      Keyframe localKeyframe = Keyframe.Factory.newInstance(paramJSONObject, paramLottieComposition, paramLottieComposition.getDpScale(), paramFactory);
      JSONArray localJSONArray = paramJSONObject.optJSONArray("ti");
      paramJSONObject = paramJSONObject.optJSONArray("to");
      label36:
      label85:
      int i;
      if (localJSONArray == null)
      {
        paramJSONObject = null;
        paramFactory = null;
        paramLottieComposition = new PathKeyframe(paramLottieComposition, (PointF)localKeyframe.startValue, (PointF)localKeyframe.endValue, localKeyframe.interpolator, localKeyframe.startFrame, localKeyframe.endFrame, null);
        if (localKeyframe.endValue != null) {
          break label122;
        }
        i = 0;
        label87:
        if (paramLottieComposition.endValue != null) {
          break label171;
        }
      }
      label122:
      label171:
      while (i != 0)
      {
        return paramLottieComposition;
        if (paramJSONObject == null) {
          break;
        }
        paramFactory = JsonUtils.pointFromJsonArray(paramJSONObject, paramLottieComposition.getDpScale());
        paramJSONObject = JsonUtils.pointFromJsonArray(localJSONArray, paramLottieComposition.getDpScale());
        break label36;
        if ((localKeyframe.startValue == null) || (!((PointF)localKeyframe.startValue).equals(((PointF)localKeyframe.endValue).x, ((PointF)localKeyframe.endValue).y))) {
          break label85;
        }
        i = 1;
        break label87;
      }
      PathKeyframe.access$102(paramLottieComposition, Utils.createPath((PointF)localKeyframe.startValue, (PointF)localKeyframe.endValue, paramFactory, paramJSONObject));
      return paramLottieComposition;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\keyframe\PathKeyframe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */