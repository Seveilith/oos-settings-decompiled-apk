package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settingslib.RestrictedSwitchPreference;

public class OPRestrictedSwitchPreference
  extends RestrictedSwitchPreference
{
  public OPRestrictedSwitchPreference(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPRestrictedSwitchPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPRestrictedSwitchPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    setLayoutResource(2130968837);
    setWidgetLayoutResource(2130968844);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPRestrictedSwitchPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */