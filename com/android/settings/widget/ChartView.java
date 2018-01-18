package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.internal.util.Preconditions;
import com.android.settings.R.styleable;

public class ChartView
  extends FrameLayout
{
  private static final int SWEEP_GRAVITY = 8388659;
  private Rect mContent = new Rect();
  ChartAxis mHoriz;
  @ViewDebug.ExportedProperty
  private int mOptimalWidth = -1;
  private float mOptimalWidthWeight = 0.0F;
  ChartAxis mVert;
  
  public ChartView(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public ChartView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ChartView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ChartView, paramInt, 0);
    setOptimalWidth(paramContext.getDimensionPixelSize(0, -1), paramContext.getFloat(1, 0.0F));
    paramContext.recycle();
    setClipToPadding(false);
    setClipChildren(false);
  }
  
  void init(ChartAxis paramChartAxis1, ChartAxis paramChartAxis2)
  {
    this.mHoriz = ((ChartAxis)Preconditions.checkNotNull(paramChartAxis1, "missing horiz"));
    this.mVert = ((ChartAxis)Preconditions.checkNotNull(paramChartAxis2, "missing vert"));
  }
  
  protected void layoutSweep(ChartSweepView paramChartSweepView)
  {
    Rect localRect1 = new Rect(this.mContent);
    Rect localRect2 = new Rect();
    layoutSweep(paramChartSweepView, localRect1, localRect2);
    paramChartSweepView.layout(localRect2.left, localRect2.top, localRect2.right, localRect2.bottom);
  }
  
  protected void layoutSweep(ChartSweepView paramChartSweepView, Rect paramRect1, Rect paramRect2)
  {
    Rect localRect = paramChartSweepView.getMargins();
    if (paramChartSweepView.getFollowAxis() == 1)
    {
      paramRect1.top += localRect.top + (int)paramChartSweepView.getPoint();
      paramRect1.bottom = paramRect1.top;
      paramRect1.left += localRect.left;
      paramRect1.right += localRect.right;
      Gravity.apply(8388659, paramRect1.width(), paramChartSweepView.getMeasuredHeight(), paramRect1, paramRect2);
      return;
    }
    paramRect1.left += localRect.left + (int)paramChartSweepView.getPoint();
    paramRect1.right = paramRect1.left;
    paramRect1.top += localRect.top;
    paramRect1.bottom += localRect.bottom;
    Gravity.apply(8388659, paramChartSweepView.getMeasuredWidth(), paramRect1.height(), paramRect1, paramRect2);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mContent.set(getPaddingLeft(), getPaddingTop(), paramInt3 - paramInt1 - getPaddingRight(), paramInt4 - paramInt2 - getPaddingBottom());
    paramInt2 = this.mContent.width();
    paramInt3 = this.mContent.height();
    this.mHoriz.setSize(paramInt2);
    this.mVert.setSize(paramInt3);
    Rect localRect1 = new Rect();
    Rect localRect2 = new Rect();
    paramInt1 = 0;
    if (paramInt1 < getChildCount())
    {
      View localView = getChildAt(paramInt1);
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
      localRect1.set(this.mContent);
      if ((localView instanceof ChartNetworkSeriesView))
      {
        Gravity.apply(localLayoutParams.gravity, paramInt2, paramInt3, localRect1, localRect2);
        localView.layout(localRect2.left, localRect2.top, localRect2.right, localRect2.bottom);
      }
      for (;;)
      {
        paramInt1 += 1;
        break;
        if ((localView instanceof ChartGridView))
        {
          Gravity.apply(localLayoutParams.gravity, paramInt2, paramInt3, localRect1, localRect2);
          localView.layout(localRect2.left, localRect2.top, localRect2.right, localRect2.bottom + localView.getPaddingBottom());
        }
        else if ((localView instanceof ChartSweepView))
        {
          layoutSweep((ChartSweepView)localView, localRect1, localRect2);
          localView.layout(localRect2.left, localRect2.top, localRect2.right, localRect2.bottom);
        }
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    paramInt1 = getMeasuredWidth() - this.mOptimalWidth;
    if ((this.mOptimalWidth > 0) && (paramInt1 > 0)) {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec((int)(this.mOptimalWidth + paramInt1 * this.mOptimalWidthWeight), 1073741824), paramInt2);
    }
  }
  
  public void setOptimalWidth(int paramInt, float paramFloat)
  {
    this.mOptimalWidth = paramInt;
    this.mOptimalWidthWeight = paramFloat;
    requestLayout();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\ChartView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */