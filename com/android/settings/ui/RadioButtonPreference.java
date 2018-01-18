package com.android.settings.ui;

import android.content.Context;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

public class RadioButtonPreference
  extends CheckBoxPreference
{
  private OnClickListener mListener = null;
  
  public RadioButtonPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RadioButtonPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842895);
  }
  
  public RadioButtonPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setWidgetLayoutResource(2130968928);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = (TextView)paramPreferenceViewHolder.findViewById(16908310);
    if (paramPreferenceViewHolder != null)
    {
      paramPreferenceViewHolder.setSingleLine(false);
      paramPreferenceViewHolder.setMaxLines(3);
    }
  }
  
  public void onClick()
  {
    if (this.mListener != null) {
      this.mListener.onRadioButtonClicked(this);
    }
  }
  
  public void setOnClickListener(OnClickListener paramOnClickListener)
  {
    this.mListener = paramOnClickListener;
  }
  
  public static abstract interface OnClickListener
  {
    public abstract void onRadioButtonClicked(RadioButtonPreference paramRadioButtonPreference);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ui\RadioButtonPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */