package com.android.settings;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.UserHandle;

public class OxygenSetupChooseLockPattern
  extends OxygenChooseLockPattern
{
  public static Intent createIntent(Context paramContext, boolean paramBoolean, long paramLong)
  {
    Intent localIntent = OxygenChooseLockPattern.createIntent(paramContext, paramBoolean, paramLong, UserHandle.myUserId());
    localIntent.setClass(paramContext, OxygenSetupChooseLockPattern.class);
    return localIntent;
  }
  
  public static Intent createIntent(Context paramContext, boolean paramBoolean, String paramString)
  {
    paramString = OxygenChooseLockPattern.createIntent(paramContext, paramBoolean, paramString, UserHandle.myUserId());
    paramString.setClass(paramContext, OxygenSetupChooseLockPattern.class);
    return paramString;
  }
  
  public static Intent createIntent(Context paramContext, boolean paramBoolean1, boolean paramBoolean2)
  {
    Intent localIntent = OxygenChooseLockPattern.createIntent(paramContext, paramBoolean1, paramBoolean2, UserHandle.myUserId());
    localIntent.setClass(paramContext, OxygenSetupChooseLockPattern.class);
    return localIntent;
  }
  
  Class<? extends Fragment> getFragmentClass()
  {
    return SetupChooseLockPatternFragment.class;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return SetupChooseLockPatternFragment.class.getName().equals(paramString);
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getOxygenTheme(getIntent()), paramBoolean);
  }
  
  public static class SetupChooseLockPatternFragment
    extends OxygenChooseLockPattern.ChooseLockPatternFragment
  {
    protected Intent getRedactionInterstitialIntent(Context paramContext)
    {
      return null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\OxygenSetupChooseLockPattern.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */