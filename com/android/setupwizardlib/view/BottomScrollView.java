package com.android.setupwizardlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class BottomScrollView
  extends ScrollView
{
  private final Runnable mCheckScrollRunnable = new Runnable()
  {
    public void run()
    {
      BottomScrollView.-wrap0(BottomScrollView.this);
    }
  };
  private BottomScrollListener mListener;
  private boolean mRequiringScroll = false;
  private int mScrollThreshold;
  
  public BottomScrollView(Context paramContext)
  {
    super(paramContext);
  }
  
  public BottomScrollView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public BottomScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private void checkScroll()
  {
    if (this.mListener != null)
    {
      if (getScrollY() < this.mScrollThreshold) {
        break label28;
      }
      this.mListener.onScrolledToBottom();
    }
    label28:
    while (this.mRequiringScroll) {
      return;
    }
    this.mRequiringScroll = true;
    this.mListener.onRequiresScroll();
  }
  
  public int getScrollThreshold()
  {
    return this.mScrollThreshold;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    View localView = getChildAt(0);
    if (localView != null) {
      this.mScrollThreshold = Math.max(0, localView.getMeasuredHeight() - paramInt4 + paramInt2 - getPaddingBottom());
    }
    if (paramInt4 - paramInt2 > 0) {
      post(this.mCheckScrollRunnable);
    }
  }
  
  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt4 != paramInt2) {
      checkScroll();
    }
  }
  
  public void setBottomScrollListener(BottomScrollListener paramBottomScrollListener)
  {
    this.mListener = paramBottomScrollListener;
  }
  
  public static abstract interface BottomScrollListener
  {
    public abstract void onRequiresScroll();
    
    public abstract void onScrolledToBottom();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\view\BottomScrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */