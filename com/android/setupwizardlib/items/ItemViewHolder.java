package com.android.setupwizardlib.items;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import com.android.setupwizardlib.DividerItemDecoration.DividedViewHolder;

class ItemViewHolder
  extends RecyclerView.ViewHolder
  implements DividerItemDecoration.DividedViewHolder
{
  private boolean mIsEnabled;
  private IItem mItem;
  
  public ItemViewHolder(View paramView)
  {
    super(paramView);
  }
  
  public IItem getItem()
  {
    return this.mItem;
  }
  
  public boolean isDividerAllowedAbove()
  {
    return this.mIsEnabled;
  }
  
  public boolean isDividerAllowedBelow()
  {
    return this.mIsEnabled;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mIsEnabled = paramBoolean;
    this.itemView.setClickable(paramBoolean);
    this.itemView.setEnabled(paramBoolean);
    this.itemView.setFocusable(paramBoolean);
  }
  
  public void setItem(IItem paramIItem)
  {
    this.mItem = paramIItem;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\ItemViewHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */