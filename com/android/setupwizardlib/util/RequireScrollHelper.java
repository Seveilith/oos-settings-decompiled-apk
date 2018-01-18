package com.android.setupwizardlib.util;

import com.android.setupwizardlib.view.BottomScrollView;
import com.android.setupwizardlib.view.BottomScrollView.BottomScrollListener;
import com.android.setupwizardlib.view.NavigationBar;

public class RequireScrollHelper
  extends AbstractRequireScrollHelper
  implements BottomScrollView.BottomScrollListener
{
  private final BottomScrollView mScrollView;
  
  private RequireScrollHelper(NavigationBar paramNavigationBar, BottomScrollView paramBottomScrollView)
  {
    super(paramNavigationBar);
    this.mScrollView = paramBottomScrollView;
  }
  
  public static void requireScroll(NavigationBar paramNavigationBar, BottomScrollView paramBottomScrollView)
  {
    new RequireScrollHelper(paramNavigationBar, paramBottomScrollView).requireScroll();
  }
  
  public void onRequiresScroll()
  {
    notifyRequiresScroll();
  }
  
  public void onScrolledToBottom()
  {
    notifyScrolledToBottom();
  }
  
  protected void pageScrollDown()
  {
    this.mScrollView.pageScroll(130);
  }
  
  protected void requireScroll()
  {
    super.requireScroll();
    this.mScrollView.setBottomScrollListener(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\util\RequireScrollHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */