package com.android.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import com.android.setupwizardlib.util.SystemBarHelper;
import com.android.setupwizardlib.util.WizardManagerHelper;

public class SetupWizardUtils
{
  public static final String EXTRA_SCRIPT_URI = "scriptUri";
  private static final String TAG = "SetupWizardUtils";
  
  public static void applyImmersiveFlags(Dialog paramDialog)
  {
    SystemBarHelper.hideSystemBars(paramDialog);
  }
  
  public static void copySetupExtras(Intent paramIntent1, Intent paramIntent2)
  {
    paramIntent2.putExtra("theme", paramIntent1.getStringExtra("theme"));
    paramIntent2.putExtra("useImmersiveMode", paramIntent1.getBooleanExtra("useImmersiveMode", false));
  }
  
  public static int getOxygenSettingTheme()
  {
    return 2131821456;
  }
  
  public static int getOxygenTheme(Intent paramIntent)
  {
    if (WizardManagerHelper.isLightTheme(paramIntent, true)) {
      return 2131821568;
    }
    return 2131821566;
  }
  
  public static int getSettingTheme()
  {
    return 2131821455;
  }
  
  public static int getTheme(Intent paramIntent)
  {
    if (WizardManagerHelper.isLightTheme(paramIntent, true)) {
      return 2131821567;
    }
    return 2131821565;
  }
  
  public static int getTransparentTheme(Intent paramIntent)
  {
    if (WizardManagerHelper.isLightTheme(paramIntent, true)) {
      return 2131821570;
    }
    return 2131821569;
  }
  
  public static boolean isUsingWizardManager(Activity paramActivity)
  {
    return paramActivity.getIntent().hasExtra("scriptUri");
  }
  
  public static void setImmersiveMode(Activity paramActivity)
  {
    if (paramActivity.getIntent().getBooleanExtra("useImmersiveMode", false)) {
      SystemBarHelper.hideSystemBars(paramActivity.getWindow());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SetupWizardUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */