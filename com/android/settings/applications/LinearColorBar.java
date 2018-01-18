package com.android.settings.applications;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.android.settings.Utils;

public class LinearColorBar
  extends LinearLayout
{
  static final int GRAY_COLOR = -11184811;
  public static final int REGION_ALL = 7;
  public static final int REGION_GREEN = 4;
  public static final int REGION_RED = 1;
  public static final int REGION_YELLOW = 2;
  static final int RIGHT_COLOR = -3221541;
  static final int WHITE_COLOR = -1;
  final Paint mColorGradientPaint = new Paint();
  final Path mColorPath = new Path();
  private int mColoredRegions = 7;
  final Paint mEdgeGradientPaint = new Paint();
  final Path mEdgePath = new Path();
  private float mGreenRatio;
  int mLastInterestingLeft;
  int mLastInterestingRight;
  int mLastLeftDiv;
  int mLastRegion;
  int mLastRightDiv;
  private int mLeftColor;
  int mLineWidth;
  private int mMiddleColor;
  private OnRegionTappedListener mOnRegionTappedListener;
  final Paint mPaint = new Paint();
  final Rect mRect = new Rect();
  private float mRedRatio;
  private int mRightColor = -3221541;
  private boolean mShowIndicator = true;
  private boolean mShowingGreen;
  private float mYellowRatio;
  
  public LinearColorBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setWillNotDraw(false);
    this.mPaint.setStyle(Paint.Style.FILL);
    this.mColorGradientPaint.setStyle(Paint.Style.FILL);
    this.mColorGradientPaint.setAntiAlias(true);
    this.mEdgeGradientPaint.setStyle(Paint.Style.STROKE);
    if (getResources().getDisplayMetrics().densityDpi >= 240) {}
    for (int i = 2;; i = 1)
    {
      this.mLineWidth = i;
      this.mEdgeGradientPaint.setStrokeWidth(this.mLineWidth);
      this.mEdgeGradientPaint.setAntiAlias(true);
      i = Utils.getColorAccent(paramContext);
      this.mMiddleColor = i;
      this.mLeftColor = i;
      return;
    }
  }
  
  private int pickColor(int paramInt1, int paramInt2)
  {
    if ((isPressed()) && ((this.mLastRegion & paramInt2) != 0)) {
      return -1;
    }
    if ((this.mColoredRegions & paramInt2) == 0) {
      return -11184811;
    }
    return paramInt1;
  }
  
  private void updateIndicator()
  {
    int j = getPaddingTop() - getPaddingBottom();
    int i = j;
    if (j < 0) {
      i = 0;
    }
    this.mRect.top = i;
    this.mRect.bottom = getHeight();
    if (!this.mShowIndicator) {
      return;
    }
    if (this.mShowingGreen) {
      this.mColorGradientPaint.setShader(new LinearGradient(0.0F, 0.0F, 0.0F, i - 2, this.mRightColor & 0xFFFFFF, this.mRightColor, Shader.TileMode.CLAMP));
    }
    for (;;)
    {
      this.mEdgeGradientPaint.setShader(new LinearGradient(0.0F, 0.0F, 0.0F, i / 2, 10526880, -6250336, Shader.TileMode.CLAMP));
      return;
      this.mColorGradientPaint.setShader(new LinearGradient(0.0F, 0.0F, 0.0F, i - 2, this.mMiddleColor & 0xFFFFFF, this.mMiddleColor, Shader.TileMode.CLAMP));
    }
  }
  
  protected void dispatchSetPressed(boolean paramBoolean)
  {
    invalidate();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int n = getWidth();
    int i1 = 0;
    int m = (int)(n * this.mRedRatio) + 0;
    int i = m + (int)(n * this.mYellowRatio);
    int k = (int)(n * this.mGreenRatio);
    int j;
    if (this.mShowingGreen) {
      j = i;
    }
    for (k = i + k;; k = i)
    {
      if ((this.mLastInterestingLeft != j) || (this.mLastInterestingRight != k))
      {
        this.mColorPath.reset();
        this.mEdgePath.reset();
        if ((this.mShowIndicator) && (j < k))
        {
          int i2 = this.mRect.top;
          this.mColorPath.moveTo(j, this.mRect.top);
          this.mColorPath.cubicTo(j, 0.0F, -2.0F, i2, -2.0F, 0.0F);
          this.mColorPath.lineTo(n + 2 - 1, 0.0F);
          this.mColorPath.cubicTo(n + 2 - 1, i2, k, 0.0F, k, this.mRect.top);
          this.mColorPath.close();
          float f = this.mLineWidth + 0.5F;
          this.mEdgePath.moveTo(-2.0F + f, 0.0F);
          this.mEdgePath.cubicTo(-2.0F + f, i2, j + f, 0.0F, j + f, this.mRect.top);
          this.mEdgePath.moveTo(n + 2 - 1 - f, 0.0F);
          this.mEdgePath.cubicTo(n + 2 - 1 - f, i2, k - f, 0.0F, k - f, this.mRect.top);
        }
        this.mLastInterestingLeft = j;
        this.mLastInterestingRight = k;
      }
      if (!this.mEdgePath.isEmpty())
      {
        paramCanvas.drawPath(this.mEdgePath, this.mEdgeGradientPaint);
        paramCanvas.drawPath(this.mColorPath, this.mColorGradientPaint);
      }
      k = i1;
      j = n;
      if (m > 0)
      {
        this.mRect.left = 0;
        this.mRect.right = m;
        this.mPaint.setColor(pickColor(this.mLeftColor, 1));
        paramCanvas.drawRect(this.mRect, this.mPaint);
        j = n - (m + 0);
        k = m;
      }
      this.mLastLeftDiv = m;
      this.mLastRightDiv = i;
      m = k;
      n = j;
      if (k < i)
      {
        this.mRect.left = k;
        this.mRect.right = i;
        this.mPaint.setColor(pickColor(this.mMiddleColor, 2));
        paramCanvas.drawRect(this.mRect, this.mPaint);
        n = j - (i - k);
        m = i;
      }
      i = m + n;
      if (m < i)
      {
        this.mRect.left = m;
        this.mRect.right = i;
        this.mPaint.setColor(pickColor(this.mRightColor, 4));
        paramCanvas.drawRect(this.mRect, this.mPaint);
      }
      return;
      j = m;
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    updateIndicator();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mOnRegionTappedListener != null) {}
    switch (paramMotionEvent.getAction())
    {
    default: 
      return super.onTouchEvent(paramMotionEvent);
    }
    int i = (int)paramMotionEvent.getX();
    if (i < this.mLastLeftDiv) {
      this.mLastRegion = 1;
    }
    for (;;)
    {
      invalidate();
      break;
      if (i < this.mLastRightDiv) {
        this.mLastRegion = 2;
      } else {
        this.mLastRegion = 4;
      }
    }
  }
  
  public boolean performClick()
  {
    if ((this.mOnRegionTappedListener != null) && (this.mLastRegion != 0))
    {
      this.mOnRegionTappedListener.onRegionTapped(this.mLastRegion);
      this.mLastRegion = 0;
    }
    return super.performClick();
  }
  
  public void setColoredRegions(int paramInt)
  {
    this.mColoredRegions = paramInt;
    invalidate();
  }
  
  public void setColors(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mLeftColor = paramInt1;
    this.mMiddleColor = paramInt2;
    this.mRightColor = paramInt3;
    updateIndicator();
    invalidate();
  }
  
  public void setOnRegionTappedListener(OnRegionTappedListener paramOnRegionTappedListener)
  {
    if (paramOnRegionTappedListener != this.mOnRegionTappedListener)
    {
      this.mOnRegionTappedListener = paramOnRegionTappedListener;
      if (paramOnRegionTappedListener == null) {
        break label25;
      }
    }
    label25:
    for (boolean bool = true;; bool = false)
    {
      setClickable(bool);
      return;
    }
  }
  
  public void setRatios(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.mRedRatio = paramFloat1;
    this.mYellowRatio = paramFloat2;
    this.mGreenRatio = paramFloat3;
    invalidate();
  }
  
  public void setShowIndicator(boolean paramBoolean)
  {
    this.mShowIndicator = paramBoolean;
    updateIndicator();
    invalidate();
  }
  
  public void setShowingGreen(boolean paramBoolean)
  {
    if (this.mShowingGreen != paramBoolean)
    {
      this.mShowingGreen = paramBoolean;
      updateIndicator();
      invalidate();
    }
  }
  
  public static abstract interface OnRegionTappedListener
  {
    public abstract void onRegionTapped(int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\LinearColorBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */