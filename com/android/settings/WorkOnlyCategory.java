package com.android.settings;

import android.content.Context;
import android.os.UserManager;
import android.support.v7.preference.PreferenceCategory;
import android.util.AttributeSet;

public class WorkOnlyCategory
  extends PreferenceCategory
  implements SelfAvailablePreference
{
  public WorkOnlyCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public boolean isAvailable(Context paramContext)
  {
    return Utils.getManagedProfile(UserManager.get(paramContext)) != null;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\WorkOnlyCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */