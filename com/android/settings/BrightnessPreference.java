package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

public class BrightnessPreference
  extends Preference
{
  public BrightnessPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected void onClick()
  {
    getContext().startActivityAsUser(new Intent("android.intent.action.SHOW_BRIGHTNESS_DIALOG"), UserHandle.CURRENT_OR_SELF);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\BrightnessPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */