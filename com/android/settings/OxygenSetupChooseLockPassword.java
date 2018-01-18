package com.android.settings;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.widget.LinearLayout;

public class OxygenSetupChooseLockPassword
  extends ChooseLockPassword
{
  public static Intent createIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, long paramLong)
  {
    Intent localIntent = ChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean, paramLong);
    localIntent.setClass(paramContext, OxygenSetupChooseLockPassword.class);
    localIntent.putExtra("extra_prefs_show_button_bar", false);
    return localIntent;
  }
  
  public static Intent createIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, String paramString)
  {
    paramString = ChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean, paramString);
    paramString.setClass(paramContext, OxygenSetupChooseLockPassword.class);
    paramString.putExtra("extra_prefs_show_button_bar", false);
    return paramString;
  }
  
  public static Intent createIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
  {
    Intent localIntent = ChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean1, paramBoolean2);
    localIntent.setClass(paramContext, OxygenSetupChooseLockPassword.class);
    localIntent.putExtra("extra_prefs_show_button_bar", false);
    return localIntent;
  }
  
  Class<? extends Fragment> getFragmentClass()
  {
    return SetupChooseLockPasswordFragment.class;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return SetupChooseLockPasswordFragment.class.getName().equals(paramString);
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getOxygenTheme(getIntent()), paramBoolean);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    ((LinearLayout)findViewById(2131362554)).setFitsSystemWindows(false);
  }
  
  public static class SetupChooseLockPasswordFragment
    extends ChooseLockPassword.ChooseLockPasswordFragment
  {
    protected Intent getRedactionInterstitialIntent(Context paramContext)
    {
      return null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\OxygenSetupChooseLockPassword.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */