package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.styleable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class ButtonBarLayout
  extends LinearLayout
{
  private static final int ALLOW_STACKING_MIN_HEIGHT_DP = 320;
  private boolean mAllowStacking;
  private int mLastWidthSize = -1;
  
  public ButtonBarLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    if (ConfigurationHelper.getScreenHeightDp(getResources()) >= 320) {}
    for (boolean bool = true;; bool = false)
    {
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ButtonBarLayout);
      this.mAllowStacking = paramContext.getBoolean(R.styleable.ButtonBarLayout_allowStacking, bool);
      paramContext.recycle();
      return;
    }
  }
  
  private boolean isStacked()
  {
    return getOrientation() == 1;
  }
  
  private void setStacked(boolean paramBoolean)
  {
    int i = 0;
    if (paramBoolean) {
      i = 1;
    }
    setOrientation(i);
    View localView;
    if (paramBoolean)
    {
      i = 5;
      setGravity(i);
      localView = findViewById(R.id.spacer);
      if (localView != null) {
        if (!paramBoolean) {
          break label81;
        }
      }
    }
    label81:
    for (i = 8;; i = 4)
    {
      localView.setVisibility(i);
      i = getChildCount() - 2;
      while (i >= 0)
      {
        bringChildToFront(getChildAt(i));
        i -= 1;
      }
      i = 80;
      break;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int m = View.MeasureSpec.getSize(paramInt1);
    if (this.mAllowStacking)
    {
      if ((m > this.mLastWidthSize) && (isStacked())) {
        setStacked(false);
      }
      this.mLastWidthSize = m;
    }
    int j = 0;
    int i;
    if ((!isStacked()) && (View.MeasureSpec.getMode(paramInt1) == 1073741824))
    {
      i = View.MeasureSpec.makeMeasureSpec(m, Integer.MIN_VALUE);
      j = 1;
    }
    int k;
    for (;;)
    {
      super.onMeasure(i, paramInt2);
      k = j;
      if (this.mAllowStacking)
      {
        if (!isStacked()) {
          break;
        }
        k = j;
      }
      if (k != 0) {
        super.onMeasure(paramInt1, paramInt2);
      }
      return;
      i = paramInt1;
    }
    if (Build.VERSION.SDK_INT >= 11) {
      if ((ViewCompat.getMeasuredWidthAndState(this) & 0xFF000000) == 16777216) {
        i = 1;
      }
    }
    for (;;)
    {
      k = j;
      if (i == 0) {
        break;
      }
      setStacked(true);
      k = 1;
      break;
      i = 0;
      continue;
      k = 0;
      i = 0;
      int n = getChildCount();
      while (i < n)
      {
        k += getChildAt(i).getMeasuredWidth();
        i += 1;
      }
      if (getPaddingLeft() + k + getPaddingRight() > m) {
        i = 1;
      } else {
        i = 0;
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


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\ButtonBarLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */