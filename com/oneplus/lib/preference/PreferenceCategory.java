package com.oneplus.lib.preference;

import android.content.Context;
import android.util.AttributeSet;
import com.oneplus.commonctrl.R.attr;

public class PreferenceCategory
  extends PreferenceGroup
{
  private static final String TAG = "PreferenceCategory";
  
  public PreferenceCategory(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public PreferenceCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.op_preferenceCategoryStyle);
  }
  
  public PreferenceCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public PreferenceCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public boolean isEnabled()
  {
    return false;
  }
  
  protected boolean onPrepareAddPreference(Preference paramPreference)
  {
    if ((paramPreference instanceof PreferenceCategory)) {
      throw new IllegalArgumentException("Cannot add a PreferenceCategory directly to a PreferenceCategory");
    }
    return super.onPrepareAddPreference(paramPreference);
  }
  
  public boolean shouldDisableDependents()
  {
    return !super.isEnabled();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\PreferenceCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */