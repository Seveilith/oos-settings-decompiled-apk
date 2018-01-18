package com.android.settings.inputmethod;

import android.content.Context;
import android.support.v14.preference.SwitchPreference;

class SwitchWithNoTextPreference
  extends SwitchPreference
{
  private static final String EMPTY_TEXT = "";
  
  SwitchWithNoTextPreference(Context paramContext)
  {
    super(paramContext);
    setSwitchTextOn("");
    setSwitchTextOff("");
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\SwitchWithNoTextPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */