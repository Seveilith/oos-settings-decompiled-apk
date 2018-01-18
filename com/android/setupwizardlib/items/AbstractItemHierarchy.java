package com.android.setupwizardlib.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.setupwizardlib.R.styleable;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractItemHierarchy
  implements ItemHierarchy
{
  private int mId = 0;
  private ArrayList<ItemHierarchy.Observer> mObservers = new ArrayList();
  
  public AbstractItemHierarchy() {}
  
  public AbstractItemHierarchy(Context paramContext, AttributeSet paramAttributeSet)
  {
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SuwAbstractItem);
    this.mId = paramContext.getResourceId(R.styleable.SuwAbstractItem_android_id, 0);
    paramContext.recycle();
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public void notifyChanged()
  {
    Iterator localIterator = this.mObservers.iterator();
    while (localIterator.hasNext()) {
      ((ItemHierarchy.Observer)localIterator.next()).onChanged(this);
    }
  }
  
  public void registerObserver(ItemHierarchy.Observer paramObserver)
  {
    this.mObservers.add(paramObserver);
  }
  
  public void setId(int paramInt)
  {
    this.mId = paramInt;
  }
  
  public void unregisterObserver(ItemHierarchy.Observer paramObserver)
  {
    this.mObservers.remove(paramObserver);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\AbstractItemHierarchy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */