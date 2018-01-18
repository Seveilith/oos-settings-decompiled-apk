package com.android.settings.applications;

import com.android.settings.utils.ManagedServiceSettings;
import com.android.settings.utils.ManagedServiceSettings.Config;

public class VrListenerSettings
  extends ManagedServiceSettings
{
  private static final ManagedServiceSettings.Config CONFIG = getVrListenerConfig();
  private static final String TAG = VrListenerSettings.class.getSimpleName();
  
  private static final ManagedServiceSettings.Config getVrListenerConfig()
  {
    ManagedServiceSettings.Config localConfig = new ManagedServiceSettings.Config();
    localConfig.tag = TAG;
    localConfig.setting = "enabled_vr_listeners";
    localConfig.intentAction = "android.service.vr.VrListenerService";
    localConfig.permission = "android.permission.BIND_VR_LISTENER_SERVICE";
    localConfig.noun = "vr listener";
    localConfig.warningDialogTitle = 2131693245;
    localConfig.warningDialogSummary = 2131693246;
    localConfig.emptyText = 2131693244;
    return localConfig;
  }
  
  protected ManagedServiceSettings.Config getConfig()
  {
    return CONFIG;
  }
  
  protected int getMetricsCategory()
  {
    return 334;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\VrListenerSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */