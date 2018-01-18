package com.oneplus.settings.ui;

import android.content.Context;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import java.lang.reflect.Field;

public class OPRadioButtonPreferenceV7
  extends CheckBoxPreference
{
  public OPRadioButtonPreferenceV7(Context paramContext)
  {
    super(paramContext);
    initViews();
  }
  
  public OPRadioButtonPreferenceV7(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews();
  }
  
  public OPRadioButtonPreferenceV7(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews();
  }
  
  private void initViews()
  {
    setLayoutResource(2130968837);
    setWidgetLayoutResource(2130968928);
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
  
  protected void onClick() {}
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPRadioButtonPreferenceV7.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */