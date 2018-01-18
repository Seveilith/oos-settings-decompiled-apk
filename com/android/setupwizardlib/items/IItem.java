package com.android.setupwizardlib.items;

import android.view.View;

public abstract interface IItem
{
  public abstract int getLayoutResource();
  
  public abstract boolean isEnabled();
  
  public abstract void onBindView(View paramView);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\IItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */