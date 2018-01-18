package com.android.setupwizardlib.items;

public abstract interface ItemHierarchy
{
  public abstract ItemHierarchy findItemById(int paramInt);
  
  public abstract int getCount();
  
  public abstract IItem getItemAt(int paramInt);
  
  public abstract void registerObserver(Observer paramObserver);
  
  public abstract void unregisterObserver(Observer paramObserver);
  
  public static abstract interface Observer
  {
    public abstract void onChanged(ItemHierarchy paramItemHierarchy);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\ItemHierarchy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */