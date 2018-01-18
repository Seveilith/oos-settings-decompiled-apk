package com.android.settings.fuelgauge;

import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.settings.DividerPreference;

public class WallOfTextPreference
  extends DividerPreference
{
  public WallOfTextPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    ((TextView)paramPreferenceViewHolder.findViewById(16908304)).setMaxLines(20);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\WallOfTextPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */