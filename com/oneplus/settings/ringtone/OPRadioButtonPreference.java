package com.oneplus.settings.ringtone;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import java.lang.reflect.Field;

public class OPRadioButtonPreference
  extends CheckBoxPreference
{
  private RingData mData;
  
  public OPRadioButtonPreference(Context paramContext)
  {
    super(paramContext);
    initViews();
  }
  
  public OPRadioButtonPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews();
  }
  
  public OPRadioButtonPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews();
  }
  
  private void initViews()
  {
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
  
  public RingData getData()
  {
    return this.mData;
  }
  
  protected void onClick() {}
  
  public void setData(RingData paramRingData)
  {
    this.mData = paramRingData;
  }
  
  public static class RingData
  {
    String mData;
    String mimetype;
    String title;
    
    public RingData(String paramString1, String paramString2, String paramString3)
    {
      this.mData = paramString1;
      this.title = paramString2;
      this.mimetype = paramString3;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ringtone\OPRadioButtonPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */