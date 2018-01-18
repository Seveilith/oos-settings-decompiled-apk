package com.android.setupwizardlib.items;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ItemAdapter
  extends BaseAdapter
  implements ItemHierarchy.Observer
{
  private final ItemHierarchy mItemHierarchy;
  private ViewTypes mViewTypes = new ViewTypes(null);
  
  public ItemAdapter(ItemHierarchy paramItemHierarchy)
  {
    this.mItemHierarchy = paramItemHierarchy;
    this.mItemHierarchy.registerObserver(this);
    refreshViewTypes();
  }
  
  private void refreshViewTypes()
  {
    int i = 0;
    while (i < getCount())
    {
      IItem localIItem = getItem(i);
      this.mViewTypes.add(localIItem.getLayoutResource());
      i += 1;
    }
  }
  
  public ItemHierarchy findItemById(int paramInt)
  {
    return this.mItemHierarchy.findItemById(paramInt);
  }
  
  public int getCount()
  {
    return this.mItemHierarchy.getCount();
  }
  
  public IItem getItem(int paramInt)
  {
    return this.mItemHierarchy.getItemAt(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    paramInt = getItem(paramInt).getLayoutResource();
    return this.mViewTypes.get(paramInt);
  }
  
  public ItemHierarchy getRootItemHierarchy()
  {
    return this.mItemHierarchy;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    IItem localIItem = getItem(paramInt);
    View localView = paramView;
    if (paramView == null) {
      localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(localIItem.getLayoutResource(), paramViewGroup, false);
    }
    localIItem.onBindView(localView);
    return localView;
  }
  
  public int getViewTypeCount()
  {
    return this.mViewTypes.size();
  }
  
  public boolean isEnabled(int paramInt)
  {
    return getItem(paramInt).isEnabled();
  }
  
  public void onChanged(ItemHierarchy paramItemHierarchy)
  {
    refreshViewTypes();
    notifyDataSetChanged();
  }
  
  private static class ViewTypes
  {
    private SparseIntArray mPositionMap = new SparseIntArray();
    private int nextPosition = 0;
    
    public int add(int paramInt)
    {
      if (this.mPositionMap.indexOfKey(paramInt) < 0)
      {
        this.mPositionMap.put(paramInt, this.nextPosition);
        this.nextPosition += 1;
      }
      return this.mPositionMap.get(paramInt);
    }
    
    public int get(int paramInt)
    {
      return this.mPositionMap.get(paramInt);
    }
    
    public int size()
    {
      return this.mPositionMap.size();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\ItemAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */