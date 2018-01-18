package com.oneplus.settings.timer.timepower;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

public class TimepowerPreference
  extends Preference
{
  private View.OnClickListener mSettingsViewClicklistener;
  
  public TimepowerPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TimepowerPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842894);
  }
  
  public TimepowerPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setLayoutResource(2130968871);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    paramPreferenceViewHolder.findViewById(2131362390).setClickable(false);
    paramPreferenceViewHolder.findViewById(2131362391).setOnClickListener(this.mSettingsViewClicklistener);
  }
  
  public void setViewClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mSettingsViewClicklistener = paramOnClickListener;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\timer\timepower\TimepowerPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */