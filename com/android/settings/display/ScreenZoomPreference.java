package com.android.settings.display;

import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.PreferenceGroup;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.settingslib.display.DisplayDensityUtils;

public class ScreenZoomPreference
  extends PreferenceGroup
{
  public ScreenZoomPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, 2130772371, 16842891));
    if (TextUtils.isEmpty(getFragment())) {
      setFragment("com.android.settings.display.ScreenZoomSettings");
    }
    paramContext = new DisplayDensityUtils(paramContext);
    if (paramContext.getCurrentIndex() < 0)
    {
      setVisible(false);
      setEnabled(false);
    }
    while (!TextUtils.isEmpty(getSummary())) {
      return;
    }
    setSummary(paramContext.getEntries()[paramContext.getCurrentIndex()]);
  }
  
  protected boolean isOnSameScreenAsChildren()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\display\ScreenZoomPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */