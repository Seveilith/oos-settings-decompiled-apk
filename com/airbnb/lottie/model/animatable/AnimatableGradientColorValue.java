package com.airbnb.lottie.model.animatable;

import android.graphics.Color;
import android.support.annotation.IntRange;
import android.util.Log;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.Keyframe;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.GradientColorKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.StaticKeyframeAnimation;
import com.airbnb.lottie.model.content.GradientColor;
import com.airbnb.lottie.utils.MiscUtils;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class AnimatableGradientColorValue
  extends BaseAnimatableValue<GradientColor, GradientColor>
{
  private AnimatableGradientColorValue(List<Keyframe<GradientColor>> paramList, GradientColor paramGradientColor)
  {
    super(paramList, paramGradientColor);
  }
  
  public BaseKeyframeAnimation<GradientColor, GradientColor> createAnimation()
  {
    if (hasAnimation()) {
      return new GradientColorKeyframeAnimation(this.keyframes);
    }
    return new StaticKeyframeAnimation(this.initialValue);
  }
  
  public static final class Factory
  {
    public static AnimatableGradientColorValue newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      paramJSONObject = AnimatableValueParser.newInstance(paramJSONObject, 1.0F, paramLottieComposition, new AnimatableGradientColorValue.ValueFactory(paramJSONObject.optInt("p", paramJSONObject.optJSONArray("k").length() / 4), null)).parseJson();
      paramLottieComposition = (GradientColor)paramJSONObject.initialValue;
      return new AnimatableGradientColorValue(paramJSONObject.keyframes, paramLottieComposition, null);
    }
  }
  
  private static class ValueFactory
    implements AnimatableValue.Factory<GradientColor>
  {
    private final int colorPoints;
    
    private ValueFactory(int paramInt)
    {
      this.colorPoints = paramInt;
    }
    
    private void addOpacityStopsToGradientIfNeeded(GradientColor paramGradientColor, JSONArray paramJSONArray)
    {
      int k = 0;
      int i = this.colorPoints * 4;
      int j;
      double[] arrayOfDouble1;
      double[] arrayOfDouble2;
      if (paramJSONArray.length() > i)
      {
        j = (paramJSONArray.length() - i) / 2;
        arrayOfDouble1 = new double[j];
        arrayOfDouble2 = new double[j];
        j = 0;
        if (i < paramJSONArray.length()) {
          break label64;
        }
        i = k;
      }
      for (;;)
      {
        if (i >= paramGradientColor.getSize())
        {
          return;
          return;
          label64:
          if (i % 2 != 0)
          {
            arrayOfDouble2[j] = paramJSONArray.optDouble(i);
            j += 1;
          }
          for (;;)
          {
            i += 1;
            break;
            arrayOfDouble1[j] = paramJSONArray.optDouble(i);
          }
        }
        j = paramGradientColor.getColors()[i];
        j = Color.argb(getOpacityAtPosition(paramGradientColor.getPositions()[i], arrayOfDouble1, arrayOfDouble2), Color.red(j), Color.green(j), Color.blue(j));
        paramGradientColor.getColors()[i] = j;
        i += 1;
      }
    }
    
    @IntRange(from=0L, to=255L)
    private int getOpacityAtPosition(double paramDouble, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
    {
      int i = 1;
      for (;;)
      {
        if (i >= paramArrayOfDouble1.length) {
          return (int)(paramArrayOfDouble2[(paramArrayOfDouble2.length - 1)] * 255.0D);
        }
        double d1 = paramArrayOfDouble1[(i - 1)];
        double d2 = paramArrayOfDouble1[i];
        if (paramArrayOfDouble1[i] >= paramDouble)
        {
          paramDouble = (paramDouble - d1) / (d2 - d1);
          return (int)(MiscUtils.lerp(paramArrayOfDouble2[(i - 1)], paramArrayOfDouble2[i], paramDouble) * 255.0D);
        }
        i += 1;
      }
    }
    
    public GradientColor valueFromObject(Object paramObject, float paramFloat)
    {
      int i = 0;
      paramObject = (JSONArray)paramObject;
      float[] arrayOfFloat = new float[this.colorPoints];
      int[] arrayOfInt = new int[this.colorPoints];
      GradientColor localGradientColor = new GradientColor(arrayOfFloat, arrayOfInt);
      if (((JSONArray)paramObject).length() == this.colorPoints * 4) {}
      int k;
      int j;
      for (;;)
      {
        k = 0;
        j = 0;
        if (i < this.colorPoints * 4) {
          break;
        }
        addOpacityStopsToGradientIfNeeded(localGradientColor, (JSONArray)paramObject);
        return localGradientColor;
        Log.w("LOTTIE", "Unexpected gradient length: " + ((JSONArray)paramObject).length() + ". Expected " + this.colorPoints * 4 + ". This may affect the appearance of the gradient. Make sure to save your After Effects file before exporting an animation with gradients.");
      }
      int m = i / 4;
      double d = ((JSONArray)paramObject).optDouble(i);
      switch (i % 4)
      {
      }
      for (;;)
      {
        i += 1;
        break;
        arrayOfFloat[m] = ((float)d);
        continue;
        j = (int)(d * 255.0D);
        continue;
        k = (int)(d * 255.0D);
        continue;
        arrayOfInt[m] = Color.argb(255, j, k, (int)(d * 255.0D));
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\animatable\AnimatableGradientColorValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */