package com.android.setupwizardlib.util;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.android.setupwizardlib.view.NavigationBar;

public abstract class AbstractRequireScrollHelper
  implements View.OnClickListener
{
  private final NavigationBar mNavigationBar;
  private boolean mScrollNeeded;
  private boolean mScrollNotified = false;
  
  protected AbstractRequireScrollHelper(NavigationBar paramNavigationBar)
  {
    this.mNavigationBar = paramNavigationBar;
  }
  
  protected void notifyRequiresScroll()
  {
    if ((this.mScrollNeeded) || (this.mScrollNotified)) {
      return;
    }
    this.mNavigationBar.post(new Runnable()
    {
      public void run()
      {
        AbstractRequireScrollHelper.-get0(AbstractRequireScrollHelper.this).getNextButton().setVisibility(8);
        AbstractRequireScrollHelper.-get0(AbstractRequireScrollHelper.this).getMoreButton().setVisibility(0);
      }
    });
    this.mScrollNeeded = true;
  }
  
  protected void notifyScrolledToBottom()
  {
    if (this.mScrollNeeded)
    {
      this.mNavigationBar.post(new Runnable()
      {
        public void run()
        {
          AbstractRequireScrollHelper.-get0(AbstractRequireScrollHelper.this).getNextButton().setVisibility(0);
          AbstractRequireScrollHelper.-get0(AbstractRequireScrollHelper.this).getMoreButton().setVisibility(8);
        }
      });
      this.mScrollNeeded = false;
      this.mScrollNotified = true;
    }
  }
  
  public void onClick(View paramView)
  {
    pageScrollDown();
  }
  
  protected abstract void pageScrollDown();
  
  protected void requireScroll()
  {
    this.mNavigationBar.getMoreButton().setOnClickListener(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\util\AbstractRequireScrollHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */