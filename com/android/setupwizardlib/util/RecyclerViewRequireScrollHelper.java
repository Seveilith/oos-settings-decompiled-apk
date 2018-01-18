package com.android.setupwizardlib.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import com.android.setupwizardlib.view.NavigationBar;

public class RecyclerViewRequireScrollHelper
  extends AbstractRequireScrollHelper
{
  private final RecyclerView mRecyclerView;
  
  private RecyclerViewRequireScrollHelper(NavigationBar paramNavigationBar, RecyclerView paramRecyclerView)
  {
    super(paramNavigationBar);
    this.mRecyclerView = paramRecyclerView;
  }
  
  private boolean canScrollDown()
  {
    boolean bool2 = false;
    int i = this.mRecyclerView.computeVerticalScrollOffset();
    int j = this.mRecyclerView.computeVerticalScrollRange() - this.mRecyclerView.computeVerticalScrollExtent();
    boolean bool1 = bool2;
    if (j != 0)
    {
      bool1 = bool2;
      if (i < j - 1) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static void requireScroll(NavigationBar paramNavigationBar, RecyclerView paramRecyclerView)
  {
    new RecyclerViewRequireScrollHelper(paramNavigationBar, paramRecyclerView).requireScroll();
  }
  
  protected void pageScrollDown()
  {
    int i = this.mRecyclerView.getHeight();
    this.mRecyclerView.smoothScrollBy(0, i);
  }
  
  protected void requireScroll()
  {
    super.requireScroll();
    this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (!RecyclerViewRequireScrollHelper.-wrap0(RecyclerViewRequireScrollHelper.this))
        {
          RecyclerViewRequireScrollHelper.this.notifyScrolledToBottom();
          return;
        }
        RecyclerViewRequireScrollHelper.this.notifyRequiresScroll();
      }
    });
    if (canScrollDown()) {
      notifyRequiresScroll();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\util\RecyclerViewRequireScrollHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */