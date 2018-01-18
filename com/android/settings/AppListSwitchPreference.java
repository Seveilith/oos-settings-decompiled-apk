package com.android.settings;

import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;

public class AppListSwitchPreference
  extends AppListPreference
{
  private static final String TAG = "AppListSwitchPref";
  private Checkable mSwitch;
  
  public AppListSwitchPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 0, 2131821534);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mSwitch = ((Checkable)paramPreferenceViewHolder.findViewById(16908352));
    paramPreferenceViewHolder = this.mSwitch;
    if (getValue() != null) {}
    for (boolean bool = true;; bool = false)
    {
      paramPreferenceViewHolder.setChecked(bool);
      return;
    }
  }
  
  protected void onClick()
  {
    if (getValue() != null) {
      if (callChangeListener(null)) {
        setValue(null);
      }
    }
    String str;
    do
    {
      return;
      if ((getEntryValues() == null) || (getEntryValues().length == 0))
      {
        Log.e("AppListSwitchPref", "Attempting to show dialog with zero entries: " + getKey());
        return;
      }
      if (getEntryValues().length != 1) {
        break;
      }
      str = getEntryValues()[0].toString();
    } while (!callChangeListener(str));
    setValue(str);
    return;
    super.onClick();
  }
  
  public void setValue(String paramString)
  {
    super.setValue(paramString);
    Checkable localCheckable;
    if (this.mSwitch != null)
    {
      localCheckable = this.mSwitch;
      if (paramString == null) {
        break label31;
      }
    }
    label31:
    for (boolean bool = true;; bool = false)
    {
      localCheckable.setChecked(bool);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\AppListSwitchPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */