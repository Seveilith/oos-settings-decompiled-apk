package com.android.settings.ui;

import android.content.Context;
import android.support.v7.preference.PreferenceCategory;
import android.util.AttributeSet;

public class OPPreferenceThinDivider
  extends PreferenceCategory
{
  private Context mContext;
  
  public OPPreferenceThinDivider(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPPreferenceThinDivider(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPPreferenceThinDivider(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    this.mContext = paramContext;
    setLayoutResource(2130968841);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ui\OPPreferenceThinDivider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */