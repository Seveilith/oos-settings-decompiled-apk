package com.android.settings;

import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.settingslib.RestrictedPreference;

public class GearPreference
  extends RestrictedPreference
  implements View.OnClickListener
{
  private OnGearClickListener mOnGearClickListener;
  
  public GearPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setWidgetLayoutResource(2130968931);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = paramPreferenceViewHolder.findViewById(2131362454);
    paramPreferenceViewHolder.setOnClickListener(this);
    paramPreferenceViewHolder.setEnabled(true);
  }
  
  public void onClick(View paramView)
  {
    if ((paramView.getId() == 2131362454) && (this.mOnGearClickListener != null)) {
      this.mOnGearClickListener.onGearClick(this);
    }
  }
  
  public void setOnGearClickListener(OnGearClickListener paramOnGearClickListener)
  {
    this.mOnGearClickListener = paramOnGearClickListener;
    notifyChanged();
  }
  
  public static abstract interface OnGearClickListener
  {
    public abstract void onGearClick(GearPreference paramGearPreference);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\GearPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */