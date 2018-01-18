package com.android.setupwizardlib.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.android.setupwizardlib.R.id;
import com.android.setupwizardlib.R.layout;
import com.android.setupwizardlib.R.styleable;

public class SwitchItem
  extends Item
  implements CompoundButton.OnCheckedChangeListener
{
  private boolean mChecked = false;
  private OnCheckedChangeListener mListener;
  
  public SwitchItem() {}
  
  public SwitchItem(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SuwSwitchItem);
    this.mChecked = paramContext.getBoolean(R.styleable.SuwSwitchItem_android_checked, false);
    paramContext.recycle();
  }
  
  protected int getDefaultLayoutResource()
  {
    return R.layout.suw_items_switch;
  }
  
  public boolean isChecked()
  {
    return this.mChecked;
  }
  
  public void onBindView(View paramView)
  {
    super.onBindView(paramView);
    paramView = (SwitchCompat)paramView.findViewById(R.id.suw_items_switch);
    paramView.setOnCheckedChangeListener(null);
    paramView.setChecked(this.mChecked);
    paramView.setOnCheckedChangeListener(this);
    paramView.setEnabled(isEnabled());
  }
  
  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    this.mChecked = paramBoolean;
    if (this.mListener != null) {
      this.mListener.onCheckedChange(this, paramBoolean);
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    if (this.mChecked != paramBoolean)
    {
      this.mChecked = paramBoolean;
      notifyChanged();
      if (this.mListener != null) {
        this.mListener.onCheckedChange(this, paramBoolean);
      }
    }
  }
  
  public void setOnCheckedChangeListener(OnCheckedChangeListener paramOnCheckedChangeListener)
  {
    this.mListener = paramOnCheckedChangeListener;
  }
  
  public void toggle(View paramView)
  {
    if (this.mChecked) {}
    for (boolean bool = false;; bool = true)
    {
      this.mChecked = bool;
      ((SwitchCompat)paramView.findViewById(R.id.suw_items_switch)).setChecked(this.mChecked);
      return;
    }
  }
  
  public static abstract interface OnCheckedChangeListener
  {
    public abstract void onCheckedChange(SwitchItem paramSwitchItem, boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\SwitchItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */