package com.android.settings.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import com.android.internal.util.Preconditions;
import com.android.settings.Utils;

public class ChartGridView
  extends View
{
  private Drawable mBorder;
  private ChartAxis mHoriz;
  private int mLabelColor;
  private Layout mLabelEnd;
  private Layout mLabelMid;
  private int mLabelSize;
  private Layout mLabelStart;
  private Drawable mPrimary;
  private Drawable mSecondary;
  private ChartAxis mVert;
  
  public ChartGridView(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public ChartGridView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ChartGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setWillNotDraw(false);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, com.android.settings.R.styleable.ChartGridView, paramInt, 0);
    this.mPrimary = paramAttributeSet.getDrawable(2);
    this.mSecondary = paramAttributeSet.getDrawable(3);
    this.mBorder = paramAttributeSet.getDrawable(4);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet.getResourceId(0, -1), com.android.internal.R.styleable.TextAppearance);
    this.mLabelSize = paramContext.getDimensionPixelSize(0, 0);
    paramContext.recycle();
    this.mLabelColor = paramAttributeSet.getColorStateList(1).getDefaultColor();
    paramAttributeSet.recycle();
  }
  
  private Layout makeLabel(CharSequence paramCharSequence)
  {
    Resources localResources = getResources();
    TextPaint localTextPaint = new TextPaint(1);
    localTextPaint.density = localResources.getDisplayMetrics().density;
    localTextPaint.setCompatibilityScaling(localResources.getCompatibilityInfo().applicationScale);
    localTextPaint.setColor(this.mLabelColor);
    localTextPaint.setTextSize(this.mLabelSize);
    return new StaticLayout(paramCharSequence, localTextPaint, (int)Math.ceil(Layout.getDesiredWidth(paramCharSequence, localTextPaint)), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
  }
  
  void init(ChartAxis paramChartAxis1, ChartAxis paramChartAxis2)
  {
    this.mHoriz = ((ChartAxis)Preconditions.checkNotNull(paramChartAxis1, "missing horiz"));
    this.mVert = ((ChartAxis)Preconditions.checkNotNull(paramChartAxis2, "missing vert"));
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int j = getWidth();
    int k = getHeight() - getPaddingBottom();
    Object localObject = this.mSecondary;
    int m;
    float[] arrayOfFloat;
    int n;
    float f;
    int i1;
    if (localObject != null)
    {
      m = ((Drawable)localObject).getIntrinsicHeight();
      arrayOfFloat = this.mVert.getTickPoints();
      i = 0;
      n = arrayOfFloat.length;
      while (i < n)
      {
        f = arrayOfFloat[i];
        i1 = (int)Math.min(m + f, k);
        ((Drawable)localObject).setBounds(0, (int)f, j, i1);
        ((Drawable)localObject).draw(paramCanvas);
        i += 1;
      }
    }
    localObject = this.mPrimary;
    if (localObject != null)
    {
      m = ((Drawable)localObject).getIntrinsicWidth();
      ((Drawable)localObject).getIntrinsicHeight();
      arrayOfFloat = this.mHoriz.getTickPoints();
      i = 0;
      n = arrayOfFloat.length;
      while (i < n)
      {
        f = arrayOfFloat[i];
        i1 = (int)Math.min(m + f, j);
        ((Drawable)localObject).setBounds((int)f, 0, i1, k);
        ((Drawable)localObject).draw(paramCanvas);
        i += 1;
      }
    }
    this.mBorder.setBounds(0, 0, j, k);
    this.mBorder.draw(paramCanvas);
    if (this.mLabelStart != null) {}
    for (int i = this.mLabelStart.getHeight() / 8;; i = 0)
    {
      localObject = this.mLabelStart;
      if (localObject != null)
      {
        m = paramCanvas.save();
        paramCanvas.translate(0.0F, k + i);
        ((Layout)localObject).draw(paramCanvas);
        paramCanvas.restoreToCount(m);
      }
      localObject = this.mLabelMid;
      if (localObject != null)
      {
        m = paramCanvas.save();
        paramCanvas.translate((j - ((Layout)localObject).getWidth()) / 2, k + i);
        ((Layout)localObject).draw(paramCanvas);
        paramCanvas.restoreToCount(m);
      }
      localObject = this.mLabelEnd;
      if (localObject != null)
      {
        m = paramCanvas.save();
        paramCanvas.translate(j - ((Layout)localObject).getWidth(), k + i);
        ((Layout)localObject).draw(paramCanvas);
        paramCanvas.restoreToCount(m);
      }
      return;
    }
  }
  
  void setBounds(long paramLong1, long paramLong2)
  {
    Context localContext = getContext();
    long l = (paramLong1 + paramLong2) / 2L;
    this.mLabelStart = makeLabel(Utils.formatDateRange(localContext, paramLong1, paramLong1));
    this.mLabelMid = makeLabel(Utils.formatDateRange(localContext, l, l));
    this.mLabelEnd = makeLabel(Utils.formatDateRange(localContext, paramLong2, paramLong2));
    invalidate();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\ChartGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */