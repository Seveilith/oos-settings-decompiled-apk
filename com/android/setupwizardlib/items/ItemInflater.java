package com.android.setupwizardlib.items;

import android.content.Context;

public class ItemInflater
  extends GenericInflater<ItemHierarchy>
{
  private static final String TAG = "ItemInflater";
  private final Context mContext;
  
  public ItemInflater(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    setDefaultPackage(Item.class.getPackage().getName() + ".");
  }
  
  public ItemInflater cloneInContext(Context paramContext)
  {
    return new ItemInflater(paramContext);
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  protected void onAddChildItem(ItemHierarchy paramItemHierarchy1, ItemHierarchy paramItemHierarchy2)
  {
    if ((paramItemHierarchy1 instanceof ItemParent))
    {
      ((ItemParent)paramItemHierarchy1).addChild(paramItemHierarchy2);
      return;
    }
    throw new IllegalArgumentException("Cannot add child item to " + paramItemHierarchy1);
  }
  
  public static abstract interface ItemParent
  {
    public abstract void addChild(ItemHierarchy paramItemHierarchy);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\ItemInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */