package com.oneplus.settings.defaultapp.view;

import android.content.Context;
import android.util.AttributeSet;
import com.oneplus.settings.defaultapp.DefaultAppUtils;

public class DefaultMusicPreference
  extends BaseDefaultPreference
{
  public DefaultMusicPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected String getType()
  {
    return DefaultAppUtils.getKeyTypeString(2);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\defaultapp\view\DefaultMusicPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */