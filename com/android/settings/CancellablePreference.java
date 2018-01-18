package com.android.settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class CancellablePreference
  extends Preference
  implements View.OnClickListener
{
  private boolean mCancellable;
  private OnCancelListener mListener;
  
  public CancellablePreference(Context paramContext)
  {
    super(paramContext);
    setWidgetLayoutResource(2130968635);
  }
  
  public CancellablePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setWidgetLayoutResource(2130968635);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = (ImageView)paramPreferenceViewHolder.findViewById(2131362005);
    if (this.mCancellable) {}
    for (int i = 0;; i = 4)
    {
      paramPreferenceViewHolder.setVisibility(i);
      paramPreferenceViewHolder.setOnClickListener(this);
      return;
    }
  }
  
  public void onClick(View paramView)
  {
    if (this.mListener != null) {
      this.mListener.onCancel(this);
    }
  }
  
  public void setCancellable(boolean paramBoolean)
  {
    this.mCancellable = paramBoolean;
    notifyChanged();
  }
  
  public void setOnCancelListener(OnCancelListener paramOnCancelListener)
  {
    this.mListener = paramOnCancelListener;
  }
  
  public static abstract interface OnCancelListener
  {
    public abstract void onCancel(CancellablePreference paramCancellablePreference);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\CancellablePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */