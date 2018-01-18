package com.oneplus.lib.widget.preference;

import android.content.Context;
import android.util.AttributeSet;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.style;
import com.oneplus.lib.preference.Preference;
import com.oneplus.lib.preference.SwitchPreference;
import com.oneplus.lib.widget.util.utils;
import java.lang.reflect.Field;

public class OPSwitchPreference
  extends SwitchPreference
{
  public OPSwitchPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPSwitchPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.op_switchPreferenceStyle);
  }
  
  public OPSwitchPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, R.style.OnePlus_DeviceDefault_Preference_Material_SwitchPreference);
  }
  
  public OPSwitchPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, utils.resolveDefStyleAttr(paramContext, paramInt1), paramInt2);
    setCanRecycleLayout(true);
  }
  
  private void setCanRecycleLayout(boolean paramBoolean)
  {
    try
    {
      Field localField = Preference.class.getDeclaredField("mCanRecycleLayout");
      localField.setAccessible(true);
      localField.setBoolean(this, paramBoolean);
      return;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      localIllegalAccessException.printStackTrace();
      return;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      localNoSuchFieldException.printStackTrace();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\preference\OPSwitchPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */