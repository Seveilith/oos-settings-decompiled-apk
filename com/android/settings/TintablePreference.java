package com.android.settings;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TintablePreference
  extends Preference
{
  private int mTintColor;
  
  public TintablePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    if (this.mTintColor != 0)
    {
      ((ImageView)paramPreferenceViewHolder.findViewById(16908294)).setImageTintList(ColorStateList.valueOf(this.mTintColor));
      return;
    }
    ((ImageView)paramPreferenceViewHolder.findViewById(16908294)).setImageTintList(null);
  }
  
  public void setTint(int paramInt)
  {
    this.mTintColor = paramInt;
    notifyChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\TintablePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */