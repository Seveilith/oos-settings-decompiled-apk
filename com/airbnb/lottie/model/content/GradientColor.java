package com.airbnb.lottie.model.content;

import com.airbnb.lottie.utils.GammaEvaluator;
import com.airbnb.lottie.utils.MiscUtils;

public class GradientColor
{
  private final int[] colors;
  private final float[] positions;
  
  public GradientColor(float[] paramArrayOfFloat, int[] paramArrayOfInt)
  {
    this.positions = paramArrayOfFloat;
    this.colors = paramArrayOfInt;
  }
  
  public int[] getColors()
  {
    return this.colors;
  }
  
  public float[] getPositions()
  {
    return this.positions;
  }
  
  public int getSize()
  {
    return this.colors.length;
  }
  
  public void lerp(GradientColor paramGradientColor1, GradientColor paramGradientColor2, float paramFloat)
  {
    int i;
    if (paramGradientColor1.colors.length == paramGradientColor2.colors.length) {
      i = 0;
    }
    for (;;)
    {
      if (i >= paramGradientColor1.colors.length)
      {
        return;
        throw new IllegalArgumentException("Cannot interpolate between gradients. Lengths vary (" + paramGradientColor1.colors.length + " vs " + paramGradientColor2.colors.length + ")");
      }
      this.positions[i] = MiscUtils.lerp(paramGradientColor1.positions[i], paramGradientColor2.positions[i], paramFloat);
      this.colors[i] = GammaEvaluator.evaluate(paramFloat, paramGradientColor1.colors[i], paramGradientColor2.colors[i]);
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\content\GradientColor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */