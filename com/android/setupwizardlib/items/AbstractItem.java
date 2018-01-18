package com.android.setupwizardlib.items;

import android.content.Context;
import android.util.AttributeSet;

public abstract class AbstractItem
  extends AbstractItemHierarchy
  implements IItem
{
  public AbstractItem() {}
  
  public AbstractItem(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public ItemHierarchy findItemById(int paramInt)
  {
    if (paramInt == getId()) {
      return this;
    }
    return null;
  }
  
  public int getCount()
  {
    return 1;
  }
  
  public IItem getItemAt(int paramInt)
  {
    return this;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\AbstractItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */