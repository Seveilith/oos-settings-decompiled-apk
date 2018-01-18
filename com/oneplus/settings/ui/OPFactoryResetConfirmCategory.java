package com.oneplus.settings.ui;

import android.content.Context;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OPFactoryResetConfirmCategory
  extends PreferenceCategory
  implements View.OnClickListener
{
  private LayoutInflater inflater;
  private Button mConfirmButton;
  private Context mContext;
  private CharSequence mFingerprintName;
  private int mLayoutResId = 2130968826;
  public OnFactoryResetConfirmListener mOnFactoryResetConfirmListener;
  
  public OPFactoryResetConfirmCategory(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPFactoryResetConfirmCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPFactoryResetConfirmCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    this.mContext = paramContext;
    setLayoutResource(this.mLayoutResId);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mConfirmButton = ((Button)paramPreferenceViewHolder.findViewById(2131362210));
    this.mConfirmButton.setEnabled(true);
    this.mConfirmButton.setOnClickListener(this);
  }
  
  public void onClick(View paramView)
  {
    if (this.mOnFactoryResetConfirmListener != null) {
      this.mOnFactoryResetConfirmListener.onFactoryResetConfirmClick();
    }
  }
  
  public void setOnFactoryResetConfirmListener(OnFactoryResetConfirmListener paramOnFactoryResetConfirmListener)
  {
    this.mOnFactoryResetConfirmListener = paramOnFactoryResetConfirmListener;
  }
  
  public static abstract interface OnFactoryResetConfirmListener
  {
    public abstract void onFactoryResetConfirmClick();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPFactoryResetConfirmCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */