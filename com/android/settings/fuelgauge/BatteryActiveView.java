package com.android.settings.fuelgauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;

public class BatteryActiveView
  extends View
{
  private final Paint mPaint = new Paint();
  private BatteryActiveProvider mProvider;
  
  public BatteryActiveView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void drawColor(Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    if (paramInt3 == 0) {
      return;
    }
    this.mPaint.setColor(paramInt3);
    float f1 = paramInt1 / paramFloat;
    float f2 = getWidth();
    paramFloat = paramInt2 / paramFloat;
    paramCanvas.drawRect(f2 * f1, 0.0F, getWidth() * paramFloat, getHeight(), this.mPaint);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.mProvider == null) {
      return;
    }
    SparseIntArray localSparseIntArray = this.mProvider.getColorArray();
    float f = (float)this.mProvider.getPeriod();
    int i = 0;
    while (i < localSparseIntArray.size() - 1)
    {
      drawColor(paramCanvas, localSparseIntArray.keyAt(i), localSparseIntArray.keyAt(i + 1), localSparseIntArray.valueAt(i), f);
      i += 1;
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (getWidth() != 0) {
      postInvalidate();
    }
  }
  
  public void setProvider(BatteryActiveProvider paramBatteryActiveProvider)
  {
    this.mProvider = paramBatteryActiveProvider;
    if (getWidth() != 0) {
      postInvalidate();
    }
  }
  
  public static abstract interface BatteryActiveProvider
  {
    public abstract SparseIntArray getColorArray();
    
    public abstract long getPeriod();
    
    public abstract boolean hasData();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\BatteryActiveView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */