package com.android.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;

public class DividerPreference
  extends Preference
{
  private Boolean mAllowAbove;
  private Boolean mAllowBelow;
  
  public DividerPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DividerPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.DividerPreference, 0, 0);
    if (paramContext.hasValue(0)) {
      this.mAllowAbove = Boolean.valueOf(paramContext.getBoolean(0, false));
    }
    if (paramContext.hasValue(1)) {
      this.mAllowBelow = Boolean.valueOf(paramContext.getBoolean(1, false));
    }
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    if (this.mAllowAbove != null) {
      paramPreferenceViewHolder.setDividerAllowedAbove(this.mAllowAbove.booleanValue());
    }
    if (this.mAllowBelow != null) {
      paramPreferenceViewHolder.setDividerAllowedBelow(this.mAllowBelow.booleanValue());
    }
  }
  
  public void setDividerAllowedAbove(boolean paramBoolean)
  {
    this.mAllowAbove = Boolean.valueOf(paramBoolean);
    notifyChanged();
  }
  
  public void setDividerAllowedBelow(boolean paramBoolean)
  {
    this.mAllowBelow = Boolean.valueOf(paramBoolean);
    notifyChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\DividerPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */