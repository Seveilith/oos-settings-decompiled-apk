package com.android.settings.widget;

import android.content.res.Resources;
import android.text.SpannableStringBuilder;

public class InvertedChartAxis
  implements ChartAxis
{
  private float mSize;
  private final ChartAxis mWrapped;
  
  public InvertedChartAxis(ChartAxis paramChartAxis)
  {
    this.mWrapped = paramChartAxis;
  }
  
  public long buildLabel(Resources paramResources, SpannableStringBuilder paramSpannableStringBuilder, long paramLong)
  {
    return this.mWrapped.buildLabel(paramResources, paramSpannableStringBuilder, paramLong);
  }
  
  public float convertToPoint(long paramLong)
  {
    return this.mSize - this.mWrapped.convertToPoint(paramLong);
  }
  
  public long convertToValue(float paramFloat)
  {
    return this.mWrapped.convertToValue(this.mSize - paramFloat);
  }
  
  public float[] getTickPoints()
  {
    float[] arrayOfFloat = this.mWrapped.getTickPoints();
    int i = 0;
    while (i < arrayOfFloat.length)
    {
      arrayOfFloat[i] = (this.mSize - arrayOfFloat[i]);
      i += 1;
    }
    return arrayOfFloat;
  }
  
  public boolean setBounds(long paramLong1, long paramLong2)
  {
    return this.mWrapped.setBounds(paramLong1, paramLong2);
  }
  
  public boolean setSize(float paramFloat)
  {
    this.mSize = paramFloat;
    return this.mWrapped.setSize(paramFloat);
  }
  
  public int shouldAdjustAxis(long paramLong)
  {
    return this.mWrapped.shouldAdjustAxis(paramLong);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\InvertedChartAxis.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */