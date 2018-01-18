package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R.styleable;
import java.util.Collection;
import java.util.Iterator;

public class PercentageBarChart
  extends View
{
  private final Paint mEmptyPaint = new Paint();
  private Collection<Entry> mEntries;
  private int mMinTickWidth = 1;
  
  public PercentageBarChart(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.PercentageBarChart);
    this.mMinTickWidth = paramContext.getDimensionPixelSize(1, 1);
    int i = paramContext.getColor(0, -16777216);
    paramContext.recycle();
    this.mEmptyPaint.setColor(i);
    this.mEmptyPaint.setStyle(Paint.Style.FILL);
  }
  
  public static Entry createEntry(int paramInt1, float paramFloat, int paramInt2)
  {
    Paint localPaint = new Paint();
    localPaint.setColor(paramInt2);
    localPaint.setStyle(Paint.Style.FILL);
    return new Entry(paramInt1, paramFloat, localPaint);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i = getPaddingLeft();
    int j = getWidth() - getPaddingRight();
    int k = getPaddingTop();
    int m = getHeight() - getPaddingBottom();
    int n = j - i;
    Iterator localIterator;
    Entry localEntry;
    if (isLayoutRtl())
    {
      f1 = j;
      f2 = f1;
      if (this.mEntries != null)
      {
        localIterator = this.mEntries.iterator();
        for (;;)
        {
          f2 = f1;
          if (!localIterator.hasNext()) {
            break;
          }
          localEntry = (Entry)localIterator.next();
          if (localEntry.percentage == 0.0F) {}
          for (f2 = 0.0F;; f2 = Math.max(this.mMinTickWidth, n * localEntry.percentage))
          {
            f2 = f1 - f2;
            if (f2 >= i) {
              break;
            }
            paramCanvas.drawRect(i, k, f1, m, localEntry.paint);
            return;
          }
          paramCanvas.drawRect(f2, k, f1, m, localEntry.paint);
          f1 = f2;
        }
      }
      paramCanvas.drawRect(i, k, f2, m, this.mEmptyPaint);
      return;
    }
    float f1 = i;
    float f2 = f1;
    if (this.mEntries != null)
    {
      localIterator = this.mEntries.iterator();
      for (;;)
      {
        f2 = f1;
        if (!localIterator.hasNext()) {
          break;
        }
        localEntry = (Entry)localIterator.next();
        if (localEntry.percentage == 0.0F) {}
        for (f2 = 0.0F;; f2 = Math.max(this.mMinTickWidth, n * localEntry.percentage))
        {
          f2 = f1 + f2;
          if (f2 <= j) {
            break;
          }
          paramCanvas.drawRect(f1, k, j, m, localEntry.paint);
          return;
        }
        paramCanvas.drawRect(f1, k, f2, m, localEntry.paint);
        f1 = f2;
      }
    }
    paramCanvas.drawRect(f2, k, j, m, this.mEmptyPaint);
  }
  
  public void setBackgroundColor(int paramInt)
  {
    this.mEmptyPaint.setColor(paramInt);
  }
  
  public void setEntries(Collection<Entry> paramCollection)
  {
    this.mEntries = paramCollection;
  }
  
  public static class Entry
    implements Comparable<Entry>
  {
    public final int order;
    public final Paint paint;
    public final float percentage;
    
    protected Entry(int paramInt, float paramFloat, Paint paramPaint)
    {
      this.order = paramInt;
      this.percentage = paramFloat;
      this.paint = paramPaint;
    }
    
    public int compareTo(Entry paramEntry)
    {
      return this.order - paramEntry.order;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\PercentageBarChart.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */