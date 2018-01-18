package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import com.android.setupwizardlib.R.styleable;

public class Illustration
  extends FrameLayout
{
  private float mAspectRatio = 0.0F;
  private Drawable mBackground;
  private float mBaselineGridSize;
  private Drawable mIllustration;
  private final Rect mIllustrationBounds = new Rect();
  private float mScale = 1.0F;
  private final Rect mViewBounds = new Rect();
  
  public Illustration(Context paramContext)
  {
    super(paramContext);
    init(null, 0);
  }
  
  public Illustration(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramAttributeSet, 0);
  }
  
  @TargetApi(11)
  public Illustration(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramAttributeSet, paramInt);
  }
  
  private void init(AttributeSet paramAttributeSet, int paramInt)
  {
    if (paramAttributeSet != null)
    {
      paramAttributeSet = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.SuwIllustration, paramInt, 0);
      this.mAspectRatio = paramAttributeSet.getFloat(R.styleable.SuwIllustration_suwAspectRatio, 0.0F);
      paramAttributeSet.recycle();
    }
    this.mBaselineGridSize = (getResources().getDisplayMetrics().density * 8.0F);
    setWillNotDraw(false);
  }
  
  private boolean shouldMirrorDrawable(Drawable paramDrawable, int paramInt)
  {
    if (paramInt == 1)
    {
      if (Build.VERSION.SDK_INT >= 19) {
        return paramDrawable.isAutoMirrored();
      }
      if (Build.VERSION.SDK_INT >= 17) {
        return (0x400000 & getContext().getApplicationInfo().flags) != 0;
      }
    }
    return false;
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    if (this.mBackground != null)
    {
      paramCanvas.save();
      paramCanvas.translate(0.0F, this.mIllustrationBounds.height());
      paramCanvas.scale(this.mScale, this.mScale, 0.0F, 0.0F);
      if ((Build.VERSION.SDK_INT > 17) && (shouldMirrorDrawable(this.mBackground, getLayoutDirection())))
      {
        paramCanvas.scale(-1.0F, 1.0F);
        paramCanvas.translate(-this.mBackground.getBounds().width(), 0.0F);
      }
      this.mBackground.draw(paramCanvas);
      paramCanvas.restore();
    }
    if (this.mIllustration != null)
    {
      paramCanvas.save();
      if ((Build.VERSION.SDK_INT > 17) && (shouldMirrorDrawable(this.mIllustration, getLayoutDirection())))
      {
        paramCanvas.scale(-1.0F, 1.0F);
        paramCanvas.translate(-this.mIllustrationBounds.width(), 0.0F);
      }
      this.mIllustration.draw(paramCanvas);
      paramCanvas.restore();
    }
    super.onDraw(paramCanvas);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int k = paramInt3 - paramInt1;
    int i1 = paramInt4 - paramInt2;
    if (this.mIllustration != null)
    {
      int n = this.mIllustration.getIntrinsicWidth();
      int m = this.mIllustration.getIntrinsicHeight();
      this.mViewBounds.set(0, 0, k, i1);
      int j = m;
      int i = n;
      if (this.mAspectRatio != 0.0F)
      {
        this.mScale = (k / n);
        i = k;
        j = (int)(m * this.mScale);
      }
      Gravity.apply(55, i, j, this.mViewBounds, this.mIllustrationBounds);
      this.mIllustration.setBounds(this.mIllustrationBounds);
    }
    if (this.mBackground != null) {
      this.mBackground.setBounds(0, 0, (int)Math.ceil(k / this.mScale), (int)Math.ceil((i1 - this.mIllustrationBounds.height()) / this.mScale));
    }
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mAspectRatio != 0.0F)
    {
      int i = (int)(View.MeasureSpec.getSize(paramInt1) / this.mAspectRatio);
      setPadding(0, (int)(i - i % this.mBaselineGridSize), 0, 0);
    }
    if (Build.VERSION.SDK_INT >= 21) {
      setOutlineProvider(ViewOutlineProvider.BOUNDS);
    }
    super.onMeasure(paramInt1, paramInt2);
  }
  
  public void setAspectRatio(float paramFloat)
  {
    this.mAspectRatio = paramFloat;
    invalidate();
    requestLayout();
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    if (paramDrawable == this.mBackground) {
      return;
    }
    this.mBackground = paramDrawable;
    invalidate();
    requestLayout();
  }
  
  @Deprecated
  public void setForeground(Drawable paramDrawable)
  {
    setIllustration(paramDrawable);
  }
  
  public void setIllustration(Drawable paramDrawable)
  {
    if (paramDrawable == this.mIllustration) {
      return;
    }
    this.mIllustration = paramDrawable;
    invalidate();
    requestLayout();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\view\Illustration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */