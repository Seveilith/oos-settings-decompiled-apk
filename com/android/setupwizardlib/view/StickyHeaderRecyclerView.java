package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;

public class StickyHeaderRecyclerView
  extends HeaderRecyclerView
{
  private int mStatusBarInset = 0;
  private View mSticky;
  private RectF mStickyRect = new RectF();
  
  public StickyHeaderRecyclerView(Context paramContext)
  {
    super(paramContext);
  }
  
  public StickyHeaderRecyclerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public StickyHeaderRecyclerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mStickyRect.contains(paramMotionEvent.getX(), paramMotionEvent.getY()))
    {
      paramMotionEvent.offsetLocation(-this.mStickyRect.left, -this.mStickyRect.top);
      return getHeader().dispatchTouchEvent(paramMotionEvent);
    }
    return super.dispatchTouchEvent(paramMotionEvent);
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    int j;
    View localView1;
    int i;
    if (this.mSticky != null)
    {
      View localView2 = getHeader();
      j = paramCanvas.save();
      if (localView2 == null) {
        break label80;
      }
      localView1 = localView2;
      if (localView2 == null) {
        break label89;
      }
      i = this.mSticky.getTop();
      label45:
      if ((localView1.getTop() + i < this.mStatusBarInset) || (!localView1.isShown())) {
        break label94;
      }
      this.mStickyRect.setEmpty();
    }
    for (;;)
    {
      paramCanvas.restoreToCount(j);
      return;
      label80:
      localView1 = this.mSticky;
      break;
      label89:
      i = 0;
      break label45;
      label94:
      this.mStickyRect.set(0.0F, -i + this.mStatusBarInset, localView1.getWidth(), localView1.getHeight() - i + this.mStatusBarInset);
      paramCanvas.translate(0.0F, this.mStickyRect.top);
      paramCanvas.clipRect(0, 0, localView1.getWidth(), localView1.getHeight());
      localView1.draw(paramCanvas);
    }
  }
  
  @TargetApi(21)
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    if (getFitsSystemWindows())
    {
      this.mStatusBarInset = paramWindowInsets.getSystemWindowInsetTop();
      paramWindowInsets.replaceSystemWindowInsets(paramWindowInsets.getSystemWindowInsetLeft(), 0, paramWindowInsets.getSystemWindowInsetRight(), paramWindowInsets.getSystemWindowInsetBottom());
    }
    return paramWindowInsets;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mSticky == null) {
      updateStickyView();
    }
    if (this.mSticky != null)
    {
      View localView = getHeader();
      if ((localView != null) && (localView.getHeight() == 0)) {
        localView.layout(0, -localView.getMeasuredHeight(), localView.getMeasuredWidth(), 0);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (this.mSticky != null) {
      measureChild(getHeader(), paramInt1, paramInt2);
    }
  }
  
  public void updateStickyView()
  {
    View localView = getHeader();
    if (localView != null) {
      this.mSticky = localView.findViewWithTag("sticky");
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\view\StickyHeaderRecyclerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */