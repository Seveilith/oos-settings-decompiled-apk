package com.android.settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

public class MultiLinePreference
  extends Preference
{
  public MultiLinePreference(Context paramContext)
  {
    super(paramContext);
  }
  
  public MultiLinePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public MultiLinePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = (TextView)paramPreferenceViewHolder.findViewById(16908310);
    if (paramPreferenceViewHolder != null) {
      paramPreferenceViewHolder.setSingleLine(false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\MultiLinePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */