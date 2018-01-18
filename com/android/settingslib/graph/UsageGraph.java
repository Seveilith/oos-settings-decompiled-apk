package com.android.settingslib.graph;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;
import com.android.settingslib.R.color;
import com.android.settingslib.R.dimen;

public class UsageGraph
  extends View
{
  private static final int PATH_DELIM = -1;
  private int mAccentColor;
  private final int mCornerRadius;
  private final Drawable mDivider;
  private final int mDividerSize;
  private final Paint mDottedPaint;
  private final Paint mFillPaint;
  private final Paint mLinePaint;
  private final SparseIntArray mLocalPaths = new SparseIntArray();
  private float mMaxX = 100.0F;
  private float mMaxY = 100.0F;
  private float mMiddleDividerLoc = 0.5F;
  private int mMiddleDividerTint = -1;
  private final Path mPath = new Path();
  private final SparseIntArray mPaths = new SparseIntArray();
  private boolean mProjectUp;
  private boolean mShowProjection;
  private final Drawable mTintedDivider;
  private Paint mTintedPaint = new Paint();
  private int mTopDividerTint = -1;
  
  public UsageGraph(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramAttributeSet = paramContext.getResources();
    this.mLinePaint = new Paint();
    this.mLinePaint.setStyle(Paint.Style.STROKE);
    this.mLinePaint.setStrokeCap(Paint.Cap.ROUND);
    this.mLinePaint.setStrokeJoin(Paint.Join.ROUND);
    this.mLinePaint.setAntiAlias(true);
    this.mCornerRadius = paramAttributeSet.getDimensionPixelSize(R.dimen.usage_graph_line_corner_radius);
    this.mLinePaint.setPathEffect(new CornerPathEffect(this.mCornerRadius));
    this.mLinePaint.setStrokeWidth(paramAttributeSet.getDimensionPixelSize(R.dimen.usage_graph_line_width));
    this.mFillPaint = new Paint(this.mLinePaint);
    this.mFillPaint.setStyle(Paint.Style.FILL);
    this.mDottedPaint = new Paint(this.mLinePaint);
    this.mDottedPaint.setStyle(Paint.Style.STROKE);
    float f1 = paramAttributeSet.getDimensionPixelSize(R.dimen.usage_graph_dot_size);
    float f2 = paramAttributeSet.getDimensionPixelSize(R.dimen.usage_graph_dot_interval);
    this.mDottedPaint.setStrokeWidth(3.0F * f1);
    this.mDottedPaint.setPathEffect(new DashPathEffect(new float[] { f1, f2 }, 0.0F));
    this.mDottedPaint.setColor(paramContext.getColor(R.color.usage_graph_dots));
    TypedValue localTypedValue = new TypedValue();
    paramContext.getTheme().resolveAttribute(16843284, localTypedValue, true);
    this.mDivider = paramContext.getDrawable(localTypedValue.resourceId);
    this.mTintedDivider = paramContext.getDrawable(localTypedValue.resourceId);
    this.mDividerSize = paramAttributeSet.getDimensionPixelSize(R.dimen.usage_graph_divider_size);
  }
  
  private void calculateLocalPaths()
  {
    if (getWidth() == 0) {
      return;
    }
    this.mLocalPaths.clear();
    int k = 0;
    int i = -1;
    int j = 0;
    if (j < this.mPaths.size())
    {
      int m = this.mPaths.keyAt(j);
      int n = this.mPaths.valueAt(j);
      if (n == -1)
      {
        if ((j == this.mPaths.size() - 1) && (i != -1)) {
          this.mLocalPaths.put(k, i);
        }
        i = -1;
        this.mLocalPaths.put(k + 1, -1);
      }
      for (;;)
      {
        j += 1;
        break;
        m = getX(m);
        n = getY(n);
        k = m;
        int i2;
        if (this.mLocalPaths.size() > 0)
        {
          int i1 = this.mLocalPaths.keyAt(this.mLocalPaths.size() - 1);
          i2 = this.mLocalPaths.valueAt(this.mLocalPaths.size() - 1);
          if ((i2 != -1) && (!hasDiff(i1, m))) {
            break label203;
          }
        }
        label203:
        while (hasDiff(i2, n))
        {
          this.mLocalPaths.put(m, n);
          break;
        }
        i = n;
      }
    }
  }
  
  private void drawDivider(int paramInt1, Canvas paramCanvas, int paramInt2)
  {
    if (paramInt2 == -1)
    {
      Drawable localDrawable = this.mDivider;
      localDrawable.setBounds(0, paramInt1, paramCanvas.getWidth(), this.mDividerSize + paramInt1);
      localDrawable.draw(paramCanvas);
      return;
    }
    this.mTintedPaint.setColor(paramInt2);
    paramCanvas.drawLine(0.0F, paramInt1, paramCanvas.getWidth(), this.mDividerSize + paramInt1, this.mTintedPaint);
  }
  
  private void drawFilledPath(Canvas paramCanvas)
  {
    this.mPath.reset();
    float f = this.mLocalPaths.keyAt(0);
    this.mPath.moveTo(this.mLocalPaths.keyAt(0), this.mLocalPaths.valueAt(0));
    int i = 1;
    if (i < this.mLocalPaths.size())
    {
      int j = this.mLocalPaths.keyAt(i);
      int k = this.mLocalPaths.valueAt(i);
      if (k == -1)
      {
        this.mPath.lineTo(this.mLocalPaths.keyAt(i - 1), getHeight());
        this.mPath.lineTo(f, getHeight());
        this.mPath.close();
        j = i + 1;
        i = j;
        if (j < this.mLocalPaths.size())
        {
          f = this.mLocalPaths.keyAt(j);
          this.mPath.moveTo(this.mLocalPaths.keyAt(j), this.mLocalPaths.valueAt(j));
          i = j;
        }
      }
      for (;;)
      {
        i += 1;
        break;
        this.mPath.lineTo(j, k);
      }
    }
    paramCanvas.drawPath(this.mPath, this.mFillPaint);
  }
  
  private void drawLinePath(Canvas paramCanvas)
  {
    this.mPath.reset();
    this.mPath.moveTo(this.mLocalPaths.keyAt(0), this.mLocalPaths.valueAt(0));
    int i = 1;
    if (i < this.mLocalPaths.size())
    {
      int j = this.mLocalPaths.keyAt(i);
      int k = this.mLocalPaths.valueAt(i);
      if (k == -1)
      {
        j = i + 1;
        i = j;
        if (j < this.mLocalPaths.size())
        {
          this.mPath.moveTo(this.mLocalPaths.keyAt(j), this.mLocalPaths.valueAt(j));
          i = j;
        }
      }
      for (;;)
      {
        i += 1;
        break;
        this.mPath.lineTo(j, k);
      }
    }
    paramCanvas.drawPath(this.mPath, this.mLinePaint);
  }
  
  private void drawProjection(Canvas paramCanvas)
  {
    this.mPath.reset();
    int i = this.mLocalPaths.keyAt(this.mLocalPaths.size() - 2);
    int j = this.mLocalPaths.valueAt(this.mLocalPaths.size() - 2);
    this.mPath.moveTo(i, j);
    Log.i("UsageGraph", "mProjectUp = " + this.mProjectUp);
    Path localPath = this.mPath;
    float f = paramCanvas.getWidth();
    if (this.mProjectUp) {}
    for (i = 0;; i = paramCanvas.getHeight())
    {
      localPath.lineTo(f, i);
      paramCanvas.drawPath(this.mPath, this.mDottedPaint);
      return;
    }
  }
  
  private int getColor(int paramInt, float paramFloat)
  {
    return ((int)(255.0F * paramFloat) << 24 | 0xFFFFFF) & paramInt;
  }
  
  private int getX(float paramFloat)
  {
    return (int)(paramFloat / this.mMaxX * getWidth());
  }
  
  private int getY(float paramFloat)
  {
    return (int)(getHeight() * (1.0F - paramFloat / this.mMaxY));
  }
  
  private boolean hasDiff(int paramInt1, int paramInt2)
  {
    return Math.abs(paramInt2 - paramInt1) >= this.mCornerRadius;
  }
  
  private void updateGradient()
  {
    this.mFillPaint.setShader(new LinearGradient(0.0F, 0.0F, 0.0F, getHeight(), getColor(this.mAccentColor, 0.2F), 0, Shader.TileMode.CLAMP));
  }
  
  public void addPath(SparseIntArray paramSparseIntArray)
  {
    int i = 0;
    while (i < paramSparseIntArray.size())
    {
      this.mPaths.put(paramSparseIntArray.keyAt(i), paramSparseIntArray.valueAt(i));
      i += 1;
    }
    this.mPaths.put(paramSparseIntArray.keyAt(paramSparseIntArray.size() - 1) + 1, -1);
    calculateLocalPaths();
    postInvalidate();
  }
  
  void clearPaths()
  {
    this.mPaths.clear();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.mMiddleDividerLoc != 0.0F) {
      drawDivider(0, paramCanvas, this.mTopDividerTint);
    }
    drawDivider((int)((paramCanvas.getHeight() - this.mDividerSize) * this.mMiddleDividerLoc), paramCanvas, this.mMiddleDividerTint);
    drawDivider(paramCanvas.getHeight() - this.mDividerSize, paramCanvas, -1);
    if (this.mLocalPaths.size() == 0) {
      return;
    }
    if (this.mShowProjection) {
      drawProjection(paramCanvas);
    }
    drawFilledPath(paramCanvas);
    drawLinePath(paramCanvas);
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    updateGradient();
    calculateLocalPaths();
  }
  
  void setAccentColor(int paramInt)
  {
    this.mAccentColor = paramInt;
    this.mLinePaint.setColor(this.mAccentColor);
    updateGradient();
    postInvalidate();
  }
  
  void setDividerColors(int paramInt1, int paramInt2)
  {
    this.mMiddleDividerTint = paramInt1;
    this.mTopDividerTint = paramInt2;
  }
  
  void setDividerLoc(int paramInt)
  {
    this.mMiddleDividerLoc = (1.0F - paramInt / this.mMaxY);
  }
  
  void setMax(int paramInt1, int paramInt2)
  {
    this.mMaxX = paramInt1;
    this.mMaxY = paramInt2;
  }
  
  void setShowProjection(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mShowProjection = paramBoolean1;
    this.mProjectUp = paramBoolean2;
    postInvalidate();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\graph\UsageGraph.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */