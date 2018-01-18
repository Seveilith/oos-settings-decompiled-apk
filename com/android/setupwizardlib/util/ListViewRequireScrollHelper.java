package com.android.setupwizardlib.util;

import android.os.Build.VERSION;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.setupwizardlib.view.NavigationBar;

public class ListViewRequireScrollHelper
  extends AbstractRequireScrollHelper
  implements AbsListView.OnScrollListener
{
  private final ListView mListView;
  
  private ListViewRequireScrollHelper(NavigationBar paramNavigationBar, ListView paramListView)
  {
    super(paramNavigationBar);
    this.mListView = paramListView;
  }
  
  public static void requireScroll(NavigationBar paramNavigationBar, ListView paramListView)
  {
    new ListViewRequireScrollHelper(paramNavigationBar, paramListView).requireScroll();
  }
  
  public void onScroll(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 + paramInt2 >= paramInt3)
    {
      notifyScrolledToBottom();
      return;
    }
    notifyRequiresScroll();
  }
  
  public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {}
  
  protected void pageScrollDown()
  {
    if (Build.VERSION.SDK_INT >= 8)
    {
      int i = this.mListView.getHeight();
      this.mListView.smoothScrollBy(i, 500);
    }
  }
  
  protected void requireScroll()
  {
    if (Build.VERSION.SDK_INT >= 8)
    {
      super.requireScroll();
      this.mListView.setOnScrollListener(this);
      ListAdapter localListAdapter = this.mListView.getAdapter();
      if (this.mListView.getLastVisiblePosition() < localListAdapter.getCount()) {
        notifyRequiresScroll();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\util\ListViewRequireScrollHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */