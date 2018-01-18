package com.android.settings.accessibility;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;

public class DividerAllowedBelowPreference
  extends Preference
{
  public DividerAllowedBelowPreference(Context paramContext)
  {
    super(paramContext);
  }
  
  public DividerAllowedBelowPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public DividerAllowedBelowPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder.setDividerAllowedBelow(true);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\DividerAllowedBelowPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */