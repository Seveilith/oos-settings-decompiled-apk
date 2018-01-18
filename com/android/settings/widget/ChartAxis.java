package com.android.settings.widget;

import android.content.res.Resources;
import android.text.SpannableStringBuilder;

public abstract interface ChartAxis
{
  public abstract long buildLabel(Resources paramResources, SpannableStringBuilder paramSpannableStringBuilder, long paramLong);
  
  public abstract float convertToPoint(long paramLong);
  
  public abstract long convertToValue(float paramFloat);
  
  public abstract float[] getTickPoints();
  
  public abstract boolean setBounds(long paramLong1, long paramLong2);
  
  public abstract boolean setSize(float paramFloat);
  
  public abstract int shouldAdjustAxis(long paramLong);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\ChartAxis.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */