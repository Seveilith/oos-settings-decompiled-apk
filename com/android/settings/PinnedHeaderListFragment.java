package com.android.settings;

import android.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PinnedHeaderListFragment
  extends ListFragment
{
  public void clearPinnedHeaderView()
  {
    ((ViewGroup)getListView().getParent()).removeViewAt(0);
  }
  
  public void setPinnedHeaderView(View paramView)
  {
    ((ViewGroup)getListView().getParent()).addView(paramView, 0);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\PinnedHeaderListFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */