package com.android.setupwizardlib;

import android.content.Context;
import android.util.AttributeSet;
import com.android.setupwizardlib.items.RecyclerItemAdapter;

@Deprecated
public class SetupWizardRecyclerItemsLayout
  extends SetupWizardRecyclerLayout
{
  public SetupWizardRecyclerItemsLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public SetupWizardRecyclerItemsLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public RecyclerItemAdapter getAdapter()
  {
    return (RecyclerItemAdapter)super.getAdapter();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\SetupWizardRecyclerItemsLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */