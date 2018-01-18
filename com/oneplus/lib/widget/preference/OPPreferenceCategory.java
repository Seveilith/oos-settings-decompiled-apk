package com.oneplus.lib.widget.preference;

import android.content.Context;
import android.util.AttributeSet;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.style;
import com.oneplus.lib.preference.PreferenceCategory;
import com.oneplus.lib.widget.util.utils;

public class OPPreferenceCategory
  extends PreferenceCategory
{
  public OPPreferenceCategory(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPPreferenceCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.op_preferenceCategoryStyle);
  }
  
  public OPPreferenceCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, R.style.Oneplus_DeviceDefault_Preference_Material_Category);
  }
  
  public OPPreferenceCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, utils.resolveDefStyleAttr(paramContext, paramInt1), paramInt2);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\preference\OPPreferenceCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */