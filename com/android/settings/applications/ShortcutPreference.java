package com.android.settings.applications;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import com.android.settings.Utils;

public class ShortcutPreference
  extends Preference
{
  private final String mPrefKey;
  private final Class mTarget;
  private final int mTitle;
  
  public ShortcutPreference(Context paramContext, Class paramClass, String paramString, int paramInt1, int paramInt2)
  {
    super(paramContext);
    this.mTarget = paramClass;
    this.mPrefKey = paramString;
    this.mTitle = paramInt2;
    setTitle(paramInt1);
    setKey(this.mPrefKey);
  }
  
  public void performClick()
  {
    super.performClick();
    Bundle localBundle = new Bundle();
    localBundle.putString(":settings:fragment_args_key", this.mPrefKey);
    Utils.startWithFragment(getContext(), this.mTarget.getName(), localBundle, null, 0, this.mTitle, null);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ShortcutPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */