package com.oneplus.lib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import com.oneplus.commonctrl.R.dimen;
import com.oneplus.commonctrl.R.id;
import com.oneplus.commonctrl.R.styleable;

public class OPButtonBarLayout
  extends LinearLayout
{
  private boolean mAllowStacking;
  private int mLastWidthSize = -1;
  
  public OPButtonBarLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OPButtonBarLayout);
    this.mAllowStacking = paramContext.getBoolean(R.styleable.OPButtonBarLayout_op_allowStacking, true);
    paramContext.recycle();
  }
  
  private boolean isStacked()
  {
    return getOrientation() == 1;
  }
  
  private void setStacked(boolean paramBoolean)
  {
    label17:
    label46:
    View localView;
    if (paramBoolean)
    {
      i = 1;
      setOrientation(i);
      if (!paramBoolean) {
        break label228;
      }
      i = 5;
      setGravity(i);
      if (!paramBoolean) {
        break label234;
      }
      setPadding(0, 0, 0, getContext().getResources().getDimensionPixelSize(R.dimen.oneplus_contorl_layout_margin_bottom1));
      localView = findViewById(R.id.spacer);
      if (localView != null)
      {
        if (!paramBoolean) {
          break label293;
        }
        i = 8;
        label65:
        localView.setVisibility(i);
      }
      localView = findViewById(R.id.spacer2);
      if (localView != null)
      {
        if (!paramBoolean) {
          break label298;
        }
        i = 8;
        label89:
        localView.setVisibility(i);
      }
      localView = findViewById(16908313);
      if (localView != null)
      {
        if (!paramBoolean) {
          break label303;
        }
        i = getContext().getResources().getDimensionPixelSize(R.dimen.oneplus_contorl_list_item_height_one_line1);
        label123:
        localView.setMinimumHeight(i);
      }
      localView = findViewById(16908314);
      if (localView != null)
      {
        if (!paramBoolean) {
          break label324;
        }
        i = getContext().getResources().getDimensionPixelSize(R.dimen.oneplus_contorl_list_item_height_one_line1);
        label157:
        localView.setMinimumHeight(i);
      }
      localView = findViewById(16908315);
      if (localView != null) {
        if (!paramBoolean) {
          break label345;
        }
      }
    }
    label228:
    label234:
    label293:
    label298:
    label303:
    label324:
    label345:
    for (int i = getContext().getResources().getDimensionPixelSize(R.dimen.oneplus_contorl_list_item_height_one_line1);; i = (int)TypedValue.applyDimension(1, 36.0F, getContext().getResources().getDisplayMetrics()))
    {
      localView.setMinimumHeight(i);
      i = getChildCount() - 2;
      while (i >= 0)
      {
        bringChildToFront(getChildAt(i));
        i -= 1;
      }
      i = 0;
      break;
      i = 80;
      break label17;
      setPadding(getContext().getResources().getDimensionPixelSize(R.dimen.oneplus_contorl_layout_margin_top1), getContext().getResources().getDimensionPixelSize(R.dimen.oneplus_contorl_layout_margin_right1), getContext().getResources().getDimensionPixelSize(R.dimen.oneplus_contorl_layout_margin_bottom1), getContext().getResources().getDimensionPixelSize(R.dimen.oneplus_contorl_layout_margin_left1));
      break label46;
      i = 4;
      break label65;
      i = 4;
      break label89;
      i = (int)TypedValue.applyDimension(1, 36.0F, getContext().getResources().getDisplayMetrics());
      break label123;
      i = (int)TypedValue.applyDimension(1, 36.0F, getContext().getResources().getDisplayMetrics());
      break label157;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.getSize(paramInt1);
    if (this.mAllowStacking)
    {
      if ((j > this.mLastWidthSize) && (isStacked())) {
        setStacked(false);
      }
      this.mLastWidthSize = j;
    }
    int i = 0;
    if ((!isStacked()) && (View.MeasureSpec.getMode(paramInt1) == 1073741824))
    {
      j = View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE);
      i = 1;
      super.onMeasure(j, paramInt2);
      j = i;
      if (this.mAllowStacking)
      {
        if (!isStacked()) {
          break label114;
        }
        j = i;
      }
    }
    for (;;)
    {
      if (j != 0) {
        super.onMeasure(paramInt1, paramInt2);
      }
      return;
      j = paramInt1;
      break;
      label114:
      j = i;
      if ((getMeasuredWidthAndState() & 0xFF000000) == 16777216)
      {
        setStacked(true);
        j = 1;
      }
    }
  }
  
  public void setAllowStacking(boolean paramBoolean)
  {
    if (this.mAllowStacking != paramBoolean)
    {
      this.mAllowStacking = paramBoolean;
      if ((!this.mAllowStacking) && (getOrientation() == 1)) {
        setStacked(false);
      }
      requestLayout();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\OPButtonBarLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */