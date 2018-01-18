package com.android.setupwizardlib.items;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.setupwizardlib.R.layout;
import java.util.ArrayList;
import java.util.Iterator;

public class ButtonBarItem
  extends AbstractItem
  implements ItemInflater.ItemParent
{
  private final ArrayList<ButtonItem> mButtons = new ArrayList();
  private boolean mVisible = true;
  
  public ButtonBarItem() {}
  
  public ButtonBarItem(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void addChild(ItemHierarchy paramItemHierarchy)
  {
    if ((paramItemHierarchy instanceof ButtonItem))
    {
      this.mButtons.add((ButtonItem)paramItemHierarchy);
      return;
    }
    throw new UnsupportedOperationException("Cannot add non-button item to Button Bar");
  }
  
  public ItemHierarchy findItemById(int paramInt)
  {
    if (getId() == paramInt) {
      return this;
    }
    Iterator localIterator = this.mButtons.iterator();
    while (localIterator.hasNext())
    {
      ItemHierarchy localItemHierarchy = ((ButtonItem)localIterator.next()).findItemById(paramInt);
      if (localItemHierarchy != null) {
        return localItemHierarchy;
      }
    }
    return null;
  }
  
  public int getCount()
  {
    if (isVisible()) {
      return 1;
    }
    return 0;
  }
  
  public int getLayoutResource()
  {
    return R.layout.suw_items_button_bar;
  }
  
  public int getViewId()
  {
    return getId();
  }
  
  public boolean isEnabled()
  {
    return false;
  }
  
  public boolean isVisible()
  {
    return this.mVisible;
  }
  
  public void onBindView(View paramView)
  {
    LinearLayout localLinearLayout = (LinearLayout)paramView;
    localLinearLayout.removeAllViews();
    Iterator localIterator = this.mButtons.iterator();
    while (localIterator.hasNext()) {
      localLinearLayout.addView(((ButtonItem)localIterator.next()).createButton(localLinearLayout));
    }
    paramView.setId(getViewId());
  }
  
  public void setVisible(boolean paramBoolean)
  {
    this.mVisible = paramBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\ButtonBarItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */