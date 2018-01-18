package com.android.settings;

import android.content.Intent;
import android.content.res.Resources.Theme;
import com.android.settings.notification.OxygenRedactionInterstitial;
import com.android.settings.notification.OxygenRedactionInterstitial.RedactionInterstitialFragment;

public class SetupRedactionInterstitial
  extends OxygenRedactionInterstitial
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":settings:show_fragment", SetupRedactionInterstitialFragment.class.getName());
    return localIntent;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return SetupRedactionInterstitialFragment.class.getName().equals(paramString);
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getOxygenTheme(getIntent()), paramBoolean);
  }
  
  public static class SetupRedactionInterstitialFragment
    extends OxygenRedactionInterstitial.RedactionInterstitialFragment
  {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SetupRedactionInterstitial.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */