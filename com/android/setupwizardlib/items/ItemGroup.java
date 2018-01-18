package com.android.setupwizardlib.items;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemGroup
  extends AbstractItemHierarchy
  implements ItemInflater.ItemParent, ItemHierarchy.Observer
{
  private List<ItemHierarchy> mChildren = new ArrayList();
  private int mCount = 0;
  private boolean mDirty = false;
  private SparseIntArray mHierarchyStart = new SparseIntArray();
  
  public ItemGroup() {}
  
  public ItemGroup(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private static int binarySearch(SparseIntArray paramSparseIntArray, int paramInt)
  {
    int j = paramSparseIntArray.size();
    int i = 0;
    j -= 1;
    while (i <= j)
    {
      int k = i + j >>> 1;
      int m = paramSparseIntArray.valueAt(k);
      if (m < paramInt) {
        i = k + 1;
      } else if (m > paramInt) {
        j = k - 1;
      } else {
        return paramSparseIntArray.keyAt(k);
      }
    }
    return paramSparseIntArray.keyAt(i - 1);
  }
  
  private int getItemIndex(int paramInt)
  {
    updateDataIfNeeded();
    if ((paramInt < 0) || (paramInt >= this.mCount)) {
      throw new IndexOutOfBoundsException("size=" + this.mCount + "; index=" + paramInt);
    }
    paramInt = binarySearch(this.mHierarchyStart, paramInt);
    if (paramInt < 0) {
      throw new IllegalStateException("Cannot have item start index < 0");
    }
    return paramInt;
  }
  
  private void onHierarchyChanged()
  {
    onChanged(null);
  }
  
  private void updateDataIfNeeded()
  {
    if (this.mDirty)
    {
      this.mCount = 0;
      this.mHierarchyStart.clear();
      int i = 0;
      while (i < this.mChildren.size())
      {
        ItemHierarchy localItemHierarchy = (ItemHierarchy)this.mChildren.get(i);
        if (localItemHierarchy.getCount() > 0) {
          this.mHierarchyStart.put(i, this.mCount);
        }
        this.mCount += localItemHierarchy.getCount();
        i += 1;
      }
      this.mDirty = false;
    }
  }
  
  public void addChild(ItemHierarchy paramItemHierarchy)
  {
    this.mChildren.add(paramItemHierarchy);
    paramItemHierarchy.registerObserver(this);
    onHierarchyChanged();
  }
  
  public void clear()
  {
    if (this.mChildren.size() == 0) {
      return;
    }
    Iterator localIterator = this.mChildren.iterator();
    while (localIterator.hasNext()) {
      ((ItemHierarchy)localIterator.next()).unregisterObserver(this);
    }
    this.mChildren.clear();
    onHierarchyChanged();
  }
  
  public ItemHierarchy findItemById(int paramInt)
  {
    if (paramInt == getId()) {
      return this;
    }
    Iterator localIterator = this.mChildren.iterator();
    while (localIterator.hasNext())
    {
      ItemHierarchy localItemHierarchy = ((ItemHierarchy)localIterator.next()).findItemById(paramInt);
      if (localItemHierarchy != null) {
        return localItemHierarchy;
      }
    }
    return null;
  }
  
  public int getCount()
  {
    updateDataIfNeeded();
    return this.mCount;
  }
  
  public IItem getItemAt(int paramInt)
  {
    int i = getItemIndex(paramInt);
    return ((ItemHierarchy)this.mChildren.get(i)).getItemAt(paramInt - this.mHierarchyStart.get(i));
  }
  
  public void onChanged(ItemHierarchy paramItemHierarchy)
  {
    this.mDirty = true;
    notifyChanged();
  }
  
  public boolean removeChild(ItemHierarchy paramItemHierarchy)
  {
    if (this.mChildren.remove(paramItemHierarchy))
    {
      paramItemHierarchy.unregisterObserver(this);
      onHierarchyChanged();
      return true;
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\ItemGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */