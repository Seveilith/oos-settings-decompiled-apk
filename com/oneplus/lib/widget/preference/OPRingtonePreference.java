package com.oneplus.lib.widget.preference;

import android.content.Context;
import android.util.AttributeSet;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.style;
import com.oneplus.lib.preference.RingtonePreference;
import com.oneplus.lib.widget.util.utils;

public class OPRingtonePreference
  extends RingtonePreference
{
  public OPRingtonePreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPRingtonePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.op_ringtonePreferenceStyle);
  }
  
  public OPRingtonePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, R.style.OnePlus_DeviceDefault_Preference_Material_RingtonePreference);
  }
  
  public OPRingtonePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, utils.resolveDefStyleAttr(paramContext, paramInt1), paramInt2);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\preference\OPRingtonePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */