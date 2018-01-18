package com.android.settings;

import android.content.Context;
import android.support.v7.preference.ListPreference;
import android.util.AttributeSet;

public class WarnedListPreference
  extends ListPreference
{
  public WarnedListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void click()
  {
    super.onClick();
  }
  
  protected void onClick() {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\WarnedListPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */