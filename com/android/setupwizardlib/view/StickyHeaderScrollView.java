package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;

public class StickyHeaderScrollView
  extends BottomScrollView
{
  private int mStatusBarInset = 0;
  private View mSticky;
  private View mStickyContainer;
  
  public StickyHeaderScrollView(Context paramContext)
  {
    super(paramContext);
  }
  
  public StickyHeaderScrollView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public StickyHeaderScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private void updateStickyHeaderPosition()
  {
    View localView;
    if ((Build.VERSION.SDK_INT >= 11) && (this.mSticky != null))
    {
      if (this.mStickyContainer == null) {
        break label73;
      }
      localView = this.mStickyContainer;
      if (this.mStickyContainer == null) {
        break label81;
      }
    }
    label73:
    label81:
    for (int i = this.mSticky.getTop();; i = 0)
    {
      if ((localView.getTop() - getScrollY() + i < this.mStatusBarInset) || (!localView.isShown())) {
        break label86;
      }
      localView.setTranslationY(0.0F);
      return;
      localView = this.mSticky;
      break;
    }
    label86:
    localView.setTranslationY(getScrollY() - i);
  }
  
  @TargetApi(21)
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    WindowInsets localWindowInsets = paramWindowInsets;
    if (getFitsSystemWindows())
    {
      this.mStatusBarInset = paramWindowInsets.getSystemWindowInsetTop();
      localWindowInsets = paramWindowInsets.replaceSystemWindowInsets(paramWindowInsets.getSystemWindowInsetLeft(), 0, paramWindowInsets.getSystemWindowInsetRight(), paramWindowInsets.getSystemWindowInsetBottom());
    }
    return localWindowInsets;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mSticky == null) {
      updateStickyView();
    }
    updateStickyHeaderPosition();
  }
  
  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    updateStickyHeaderPosition();
  }
  
  public void updateStickyView()
  {
    this.mSticky = findViewWithTag("sticky");
    this.mStickyContainer = findViewWithTag("stickyContainer");
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\view\StickyHeaderScrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */