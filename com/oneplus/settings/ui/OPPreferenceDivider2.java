package com.oneplus.settings.ui;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;

public class OPPreferenceDivider2
  extends PreferenceCategory
{
  public OPPreferenceDivider2(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPPreferenceDivider2(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPPreferenceDivider2(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    setLayoutResource(2130968833);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPPreferenceDivider2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */