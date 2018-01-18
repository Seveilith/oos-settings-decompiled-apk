package com.android.setupwizardlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.setupwizardlib.items.ItemAdapter;
import com.android.setupwizardlib.items.ItemGroup;
import com.android.setupwizardlib.items.ItemInflater;

public class SetupWizardItemsLayout
  extends SetupWizardListLayout
{
  private ItemAdapter mAdapter;
  
  public SetupWizardItemsLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext, paramAttributeSet, 0);
  }
  
  public SetupWizardItemsLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet, paramInt);
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SuwSetupWizardItemsLayout, paramInt, 0);
    paramInt = paramAttributeSet.getResourceId(R.styleable.SuwSetupWizardItemsLayout_android_entries, 0);
    if (paramInt != 0)
    {
      this.mAdapter = new ItemAdapter((ItemGroup)new ItemInflater(paramContext).inflate(paramInt));
      setAdapter(this.mAdapter);
    }
    paramAttributeSet.recycle();
  }
  
  public ItemAdapter getAdapter()
  {
    return this.mAdapter;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\SetupWizardItemsLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */