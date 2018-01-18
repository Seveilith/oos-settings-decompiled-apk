package com.android.setupwizardlib.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;

public class WizardManagerHelper
{
  private static final String ACTION_NEXT = "com.android.wizard.NEXT";
  @Deprecated
  private static final String EXTRA_ACTION_ID = "actionId";
  private static final String EXTRA_IS_FIRST_RUN = "firstRun";
  private static final String EXTRA_RESULT_CODE = "com.android.setupwizard.ResultCode";
  @Deprecated
  private static final String EXTRA_SCRIPT_URI = "scriptUri";
  public static final String EXTRA_THEME = "theme";
  public static final String EXTRA_USE_IMMERSIVE_MODE = "useImmersiveMode";
  private static final String EXTRA_WIZARD_BUNDLE = "wizardBundle";
  public static final String SETTINGS_GLOBAL_DEVICE_PROVISIONED = "device_provisioned";
  public static final String SETTINGS_SECURE_USER_SETUP_COMPLETE = "user_setup_complete";
  public static final String THEME_GLIF = "glif";
  public static final String THEME_GLIF_LIGHT = "glif_light";
  public static final String THEME_HOLO = "holo";
  public static final String THEME_HOLO_LIGHT = "holo_light";
  public static final String THEME_MATERIAL = "material";
  @Deprecated
  public static final String THEME_MATERIAL_BLUE = "material_blue";
  @Deprecated
  public static final String THEME_MATERIAL_BLUE_LIGHT = "material_blue_light";
  public static final String THEME_MATERIAL_LIGHT = "material_light";
  
  public static void copyWizardManagerExtras(Intent paramIntent1, Intent paramIntent2)
  {
    paramIntent2.putExtra("wizardBundle", paramIntent1.getBundleExtra("wizardBundle"));
    paramIntent2.putExtra("firstRun", paramIntent1.getBooleanExtra("firstRun", false));
    paramIntent2.putExtra("scriptUri", paramIntent1.getStringExtra("scriptUri"));
    paramIntent2.putExtra("actionId", paramIntent1.getStringExtra("actionId"));
  }
  
  public static Intent getNextIntent(Intent paramIntent, int paramInt)
  {
    return getNextIntent(paramIntent, paramInt, null);
  }
  
  public static Intent getNextIntent(Intent paramIntent1, int paramInt, Intent paramIntent2)
  {
    Intent localIntent = new Intent("com.android.wizard.NEXT");
    copyWizardManagerExtras(paramIntent1, localIntent);
    localIntent.putExtra("com.android.setupwizard.ResultCode", paramInt);
    if ((paramIntent2 != null) && (paramIntent2.getExtras() != null)) {
      localIntent.putExtras(paramIntent2.getExtras());
    }
    localIntent.putExtra("theme", paramIntent1.getStringExtra("theme"));
    return localIntent;
  }
  
  public static boolean isDeviceProvisioned(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return Settings.Global.getInt(paramContext.getContentResolver(), "device_provisioned", 0) == 1;
    }
    return Settings.Secure.getInt(paramContext.getContentResolver(), "device_provisioned", 0) == 1;
  }
  
  public static boolean isLightTheme(Intent paramIntent, boolean paramBoolean)
  {
    return isLightTheme(paramIntent.getStringExtra("theme"), paramBoolean);
  }
  
  public static boolean isLightTheme(String paramString, boolean paramBoolean)
  {
    if (("holo_light".equals(paramString)) || ("material_light".equals(paramString)) || ("material_blue_light".equals(paramString)) || ("glif_light".equals(paramString))) {
      return true;
    }
    if (("holo".equals(paramString)) || ("material".equals(paramString)) || ("material_blue".equals(paramString)) || ("glif".equals(paramString))) {
      return false;
    }
    return paramBoolean;
  }
  
  public static boolean isSetupWizardIntent(Intent paramIntent)
  {
    return paramIntent.getBooleanExtra("firstRun", false);
  }
  
  public static boolean isUserSetupComplete(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 14) {
      return Settings.Secure.getInt(paramContext.getContentResolver(), "user_setup_complete", 0) == 1;
    }
    return Settings.Secure.getInt(paramContext.getContentResolver(), "device_provisioned", 0) == 1;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\util\WizardManagerHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */