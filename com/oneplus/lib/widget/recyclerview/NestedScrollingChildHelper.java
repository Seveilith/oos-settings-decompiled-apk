package com.oneplus.lib.widget.recyclerview;

import android.view.View;
import android.view.ViewParent;

class NestedScrollingChildHelper
{
  private boolean mIsNestedScrollingEnabled;
  private ViewParent mNestedScrollingParent;
  private int[] mTempNestedScrollConsumed;
  private final View mView;
  
  public NestedScrollingChildHelper(View paramView)
  {
    this.mView = paramView;
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if ((isNestedScrollingEnabled()) && (this.mNestedScrollingParent != null)) {
      return this.mNestedScrollingParent.onNestedFling(this.mView, paramFloat1, paramFloat2, paramBoolean);
    }
    return false;
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    if ((isNestedScrollingEnabled()) && (this.mNestedScrollingParent != null)) {
      return this.mNestedScrollingParent.onNestedPreFling(this.mView, paramFloat1, paramFloat2);
    }
    return false;
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if ((isNestedScrollingEnabled()) && (this.mNestedScrollingParent != null))
    {
      if ((paramInt1 != 0) || (paramInt2 != 0))
      {
        int i = 0;
        int j = 0;
        if (paramArrayOfInt2 != null)
        {
          this.mView.getLocationInWindow(paramArrayOfInt2);
          i = paramArrayOfInt2[0];
          j = paramArrayOfInt2[1];
        }
        int[] arrayOfInt = paramArrayOfInt1;
        if (paramArrayOfInt1 == null)
        {
          if (this.mTempNestedScrollConsumed == null) {
            this.mTempNestedScrollConsumed = new int[2];
          }
          arrayOfInt = this.mTempNestedScrollConsumed;
        }
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 0;
        this.mNestedScrollingParent.onNestedPreScroll(this.mView, paramInt1, paramInt2, arrayOfInt);
        if (paramArrayOfInt2 != null)
        {
          this.mView.getLocationInWindow(paramArrayOfInt2);
          paramArrayOfInt2[0] -= i;
          paramArrayOfInt2[1] -= j;
        }
        return (arrayOfInt[0] != 0) || (arrayOfInt[1] != 0);
      }
      if (paramArrayOfInt2 != null)
      {
        paramArrayOfInt2[0] = 0;
        paramArrayOfInt2[1] = 0;
      }
    }
    return false;
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    if ((isNestedScrollingEnabled()) && (this.mNestedScrollingParent != null))
    {
      if ((paramInt1 != 0) || (paramInt2 != 0)) {}
      while ((paramInt3 != 0) || (paramInt4 != 0))
      {
        int i = 0;
        int j = 0;
        if (paramArrayOfInt != null)
        {
          this.mView.getLocationInWindow(paramArrayOfInt);
          i = paramArrayOfInt[0];
          j = paramArrayOfInt[1];
        }
        this.mNestedScrollingParent.onNestedScroll(this.mView, paramInt1, paramInt2, paramInt3, paramInt4);
        if (paramArrayOfInt != null)
        {
          this.mView.getLocationInWindow(paramArrayOfInt);
          paramArrayOfInt[0] -= i;
          paramArrayOfInt[1] -= j;
        }
        return true;
      }
      if (paramArrayOfInt != null)
      {
        paramArrayOfInt[0] = 0;
        paramArrayOfInt[1] = 0;
      }
    }
    return false;
  }
  
  public boolean hasNestedScrollingParent()
  {
    return this.mNestedScrollingParent != null;
  }
  
  public boolean isNestedScrollingEnabled()
  {
    return this.mIsNestedScrollingEnabled;
  }
  
  public void onDetachedFromWindow()
  {
    this.mView.stopNestedScroll();
  }
  
  public void onStopNestedScroll(View paramView)
  {
    this.mView.stopNestedScroll();
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    if (this.mIsNestedScrollingEnabled) {
      this.mView.stopNestedScroll();
    }
    this.mIsNestedScrollingEnabled = paramBoolean;
  }
  
  public boolean startNestedScroll(int paramInt)
  {
    if (hasNestedScrollingParent()) {
      return true;
    }
    if (isNestedScrollingEnabled())
    {
      ViewParent localViewParent = this.mView.getParent();
      View localView = this.mView;
      while (localViewParent != null)
      {
        if (localViewParent.onStartNestedScroll(localView, this.mView, paramInt))
        {
          this.mNestedScrollingParent = localViewParent;
          localViewParent.onNestedScrollAccepted(localView, this.mView, paramInt);
          return true;
        }
        if ((localViewParent instanceof View)) {
          localView = (View)localViewParent;
        }
        localViewParent = localViewParent.getParent();
      }
    }
    return false;
  }
  
  public void stopNestedScroll()
  {
    if (this.mNestedScrollingParent != null)
    {
      this.mNestedScrollingParent.onStopNestedScroll(this.mView);
      this.mNestedScrollingParent = null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\recyclerview\NestedScrollingChildHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */