package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.android.setupwizardlib.R.styleable;

public class IntrinsicSizeFrameLayout
  extends FrameLayout
{
  private int mIntrinsicHeight = 0;
  private int mIntrinsicWidth = 0;
  
  public IntrinsicSizeFrameLayout(Context paramContext)
  {
    super(paramContext);
    init(paramContext, null, 0);
  }
  
  public IntrinsicSizeFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext, paramAttributeSet, 0);
  }
  
  @TargetApi(11)
  public IntrinsicSizeFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet, paramInt);
  }
  
  private int getIntrinsicMeasureSpec(int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 0) {
      return paramInt1;
    }
    paramInt2 = View.MeasureSpec.getMode(paramInt1);
    int i = View.MeasureSpec.getSize(paramInt1);
    if (paramInt2 == 0) {
      return View.MeasureSpec.makeMeasureSpec(this.mIntrinsicHeight, 1073741824);
    }
    if (paramInt2 == Integer.MIN_VALUE) {
      return View.MeasureSpec.makeMeasureSpec(Math.min(i, this.mIntrinsicHeight), 1073741824);
    }
    return paramInt1;
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SuwMaxSizeFrameLayout, paramInt, 0);
    this.mIntrinsicHeight = paramContext.getDimensionPixelSize(R.styleable.SuwMaxSizeFrameLayout_android_height, 0);
    this.mIntrinsicWidth = paramContext.getDimensionPixelSize(R.styleable.SuwMaxSizeFrameLayout_android_width, 0);
    paramContext.recycle();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(getIntrinsicMeasureSpec(paramInt1, this.mIntrinsicWidth), getIntrinsicMeasureSpec(paramInt2, this.mIntrinsicHeight));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\view\IntrinsicSizeFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */