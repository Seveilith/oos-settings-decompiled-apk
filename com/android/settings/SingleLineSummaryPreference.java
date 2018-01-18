package com.android.settings;

import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.settingslib.RestrictedPreference;

public class SingleLineSummaryPreference
  extends RestrictedPreference
{
  public SingleLineSummaryPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = (TextView)paramPreferenceViewHolder.findViewById(16908304);
    paramPreferenceViewHolder.setSingleLine();
    paramPreferenceViewHolder.setEllipsize(TextUtils.TruncateAt.END);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SingleLineSummaryPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */