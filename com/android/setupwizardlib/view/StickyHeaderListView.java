package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ListView;
import com.android.setupwizardlib.R.styleable;

public class StickyHeaderListView
  extends ListView
{
  private int mStatusBarInset = 0;
  private View mSticky;
  private View mStickyContainer;
  private RectF mStickyRect = new RectF();
  
  public StickyHeaderListView(Context paramContext)
  {
    super(paramContext);
    init(null, 16842868);
  }
  
  public StickyHeaderListView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramAttributeSet, 16842868);
  }
  
  public StickyHeaderListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramAttributeSet, paramInt);
  }
  
  private void init(AttributeSet paramAttributeSet, int paramInt)
  {
    paramAttributeSet = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.SuwStickyHeaderListView, paramInt, 0);
    paramInt = paramAttributeSet.getResourceId(R.styleable.SuwStickyHeaderListView_suwHeader, 0);
    if (paramInt != 0) {
      addHeaderView(LayoutInflater.from(getContext()).inflate(paramInt, this, false), null, false);
    }
    paramAttributeSet.recycle();
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mStickyRect.contains(paramMotionEvent.getX(), paramMotionEvent.getY()))
    {
      paramMotionEvent.offsetLocation(-this.mStickyRect.left, -this.mStickyRect.top);
      return this.mStickyContainer.dispatchTouchEvent(paramMotionEvent);
    }
    return super.dispatchTouchEvent(paramMotionEvent);
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    int j;
    View localView;
    int i;
    if (this.mSticky != null)
    {
      j = paramCanvas.save();
      if (this.mStickyContainer == null) {
        break label80;
      }
      localView = this.mStickyContainer;
      if (this.mStickyContainer == null) {
        break label89;
      }
      i = this.mSticky.getTop();
      label45:
      if ((localView.getTop() + i < this.mStatusBarInset) || (!localView.isShown())) {
        break label94;
      }
      this.mStickyRect.setEmpty();
    }
    for (;;)
    {
      paramCanvas.restoreToCount(j);
      return;
      label80:
      localView = this.mSticky;
      break;
      label89:
      i = 0;
      break label45;
      label94:
      this.mStickyRect.set(0.0F, -i + this.mStatusBarInset, localView.getWidth(), localView.getHeight() - i + this.mStatusBarInset);
      paramCanvas.translate(0.0F, this.mStickyRect.top);
      paramCanvas.clipRect(0, 0, localView.getWidth(), localView.getHeight());
      localView.draw(paramCanvas);
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
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    if (this.mSticky != null) {}
    for (int i = 1;; i = 0)
    {
      paramAccessibilityEvent.setItemCount(paramAccessibilityEvent.getItemCount() - i);
      paramAccessibilityEvent.setFromIndex(Math.max(paramAccessibilityEvent.getFromIndex() - i, 0));
      if (Build.VERSION.SDK_INT >= 14) {
        paramAccessibilityEvent.setToIndex(Math.max(paramAccessibilityEvent.getToIndex() - i, 0));
      }
      return;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mSticky == null) {
      updateStickyView();
    }
  }
  
  public void updateStickyView()
  {
    this.mSticky = findViewWithTag("sticky");
    this.mStickyContainer = findViewWithTag("stickyContainer");
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\view\StickyHeaderListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */