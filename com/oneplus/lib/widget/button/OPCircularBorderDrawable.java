package com.oneplus.lib.widget.button;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

class OPCircularBorderDrawable
  extends Drawable
{
  private static final float DRAW_STROKE_WIDTH_MULTIPLE = 1.3333F;
  float mBorderWidth;
  private int mBottomInnerStrokeColor;
  private int mBottomOuterStrokeColor;
  private boolean mInvalidateShader = true;
  final Paint mPaint = new Paint(1);
  final Rect mRect = new Rect();
  final RectF mRectF = new RectF();
  private ColorStateList mTint;
  private int mTintColor;
  private PorterDuffColorFilter mTintFilter;
  private PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_IN;
  private int mTopInnerStrokeColor;
  private int mTopOuterStrokeColor;
  
  public OPCircularBorderDrawable()
  {
    this.mPaint.setStyle(Paint.Style.STROKE);
  }
  
  private static int compositeAlpha(int paramInt1, int paramInt2)
  {
    return 255 - (255 - paramInt2) * (255 - paramInt1) / 255;
  }
  
  private static int compositeColors(int paramInt1, int paramInt2)
  {
    int i = Color.alpha(paramInt2);
    int j = Color.alpha(paramInt1);
    int k = compositeAlpha(j, i);
    return Color.argb(k, compositeComponent(Color.red(paramInt1), j, Color.red(paramInt2), i, k), compositeComponent(Color.green(paramInt1), j, Color.green(paramInt2), i, k), compositeComponent(Color.blue(paramInt1), j, Color.blue(paramInt2), i, k));
  }
  
  private static int compositeComponent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramInt5 == 0) {
      return 0;
    }
    return (paramInt1 * 255 * paramInt2 + paramInt3 * paramInt4 * (255 - paramInt2)) / (paramInt5 * 255);
  }
  
  private Shader createGradientShader()
  {
    Object localObject = this.mRect;
    copyBounds((Rect)localObject);
    float f1 = this.mBorderWidth / ((Rect)localObject).height();
    int i = compositeColors(this.mTopOuterStrokeColor, this.mTintColor);
    int j = compositeColors(this.mTopInnerStrokeColor, this.mTintColor);
    int k = compositeColors(setAlphaComponent(this.mTopInnerStrokeColor, 0), this.mTintColor);
    int m = compositeColors(setAlphaComponent(this.mBottomInnerStrokeColor, 0), this.mTintColor);
    int n = compositeColors(this.mBottomInnerStrokeColor, this.mTintColor);
    int i1 = compositeColors(this.mBottomOuterStrokeColor, this.mTintColor);
    float f2 = ((Rect)localObject).top;
    float f3 = ((Rect)localObject).bottom;
    localObject = Shader.TileMode.CLAMP;
    return new LinearGradient(0.0F, f2, 0.0F, f3, new int[] { i, j, k, m, n, i1 }, new float[] { 0.0F, f1, 0.5F, 0.5F, 1.0F - f1, 1.0F }, (Shader.TileMode)localObject);
  }
  
  public static int setAlphaComponent(int paramInt1, int paramInt2)
  {
    if ((paramInt2 < 0) || (paramInt2 > 255)) {
      throw new IllegalArgumentException("alpha must be between 0 and 255.");
    }
    return 0xFFFFFF & paramInt1 | paramInt2 << 24;
  }
  
  private PorterDuffColorFilter updateTintFilter(ColorStateList paramColorStateList, PorterDuff.Mode paramMode)
  {
    if ((paramColorStateList == null) || (paramMode == null)) {
      return null;
    }
    return new PorterDuffColorFilter(paramColorStateList.getColorForState(getState(), 0), paramMode);
  }
  
  public void draw(Canvas paramCanvas)
  {
    if ((this.mTintFilter != null) && (this.mPaint.getColorFilter() == null)) {
      this.mPaint.setColorFilter(this.mTintFilter);
    }
    for (int i = 1;; i = 0)
    {
      if (this.mInvalidateShader)
      {
        this.mPaint.setShader(createGradientShader());
        this.mInvalidateShader = false;
      }
      float f = this.mPaint.getStrokeWidth() / 2.0F;
      RectF localRectF = this.mRectF;
      copyBounds(this.mRect);
      localRectF.set(this.mRect);
      localRectF.left += f;
      localRectF.top += f;
      localRectF.right -= f;
      localRectF.bottom -= f;
      paramCanvas.drawOval(localRectF, this.mPaint);
      if (i != 0) {
        this.mPaint.setColorFilter(null);
      }
      return;
    }
  }
  
  public int getOpacity()
  {
    if (this.mBorderWidth > 0.0F) {
      return -3;
    }
    return -2;
  }
  
  public void getOutline(Outline paramOutline)
  {
    copyBounds(this.mRect);
    paramOutline.setOval(this.mRect);
  }
  
  public boolean getPadding(Rect paramRect)
  {
    int i = Math.round(this.mBorderWidth);
    paramRect.set(i, i, i, i);
    return true;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    this.mInvalidateShader = true;
  }
  
  public void setAlpha(int paramInt)
  {
    this.mPaint.setAlpha(paramInt);
    invalidateSelf();
  }
  
  void setBorderWidth(float paramFloat)
  {
    if (this.mBorderWidth != paramFloat)
    {
      this.mBorderWidth = paramFloat;
      this.mPaint.setStrokeWidth(1.3333F * paramFloat);
      this.mInvalidateShader = true;
      invalidateSelf();
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mPaint.setColorFilter(paramColorFilter);
    invalidateSelf();
  }
  
  void setGradientColors(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mTopOuterStrokeColor = paramInt1;
    this.mTopInnerStrokeColor = paramInt2;
    this.mBottomOuterStrokeColor = paramInt3;
    this.mBottomInnerStrokeColor = paramInt4;
  }
  
  void setTintColor(int paramInt)
  {
    this.mTintColor = paramInt;
    this.mInvalidateShader = true;
    invalidateSelf();
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    this.mTint = paramColorStateList;
    this.mTintFilter = updateTintFilter(paramColorStateList, this.mTintMode);
    invalidateSelf();
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    this.mTintMode = paramMode;
    this.mTintFilter = updateTintFilter(this.mTint, paramMode);
    invalidateSelf();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\button\OPCircularBorderDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */