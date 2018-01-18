package com.android.settings;

import android.content.Context;
import android.content.Intent;

public class ManagedLockPasswordProvider
{
  static ManagedLockPasswordProvider get(Context paramContext, int paramInt)
  {
    return new ManagedLockPasswordProvider();
  }
  
  Intent createIntent(boolean paramBoolean, String paramString)
  {
    return null;
  }
  
  String getPickerOptionTitle(boolean paramBoolean)
  {
    return "";
  }
  
  int getResIdForLockUnlockScreen(boolean paramBoolean)
  {
    if (paramBoolean) {
      return 2131230844;
    }
    return 2131230843;
  }
  
  int getResIdForLockUnlockSubScreen()
  {
    return 2131230845;
  }
  
  boolean isManagedPasswordChoosable()
  {
    return false;
  }
  
  boolean isSettingManagedPasswordSupported()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ManagedLockPasswordProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */