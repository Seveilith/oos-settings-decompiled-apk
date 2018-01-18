package com.android.settings.notification;

import android.app.NotificationManager;
import android.app.NotificationManager.Policy;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

public class ZenModeSettings
  extends ZenModeSettingsBase
{
  private static final String KEY_PRIORITY_SETTINGS = "priority_settings";
  private static final String KEY_VISUAL_SETTINGS = "visual_interruptions_settings";
  private NotificationManager.Policy mPolicy;
  private Preference mPrioritySettings;
  private Preference mVisualSettings;
  
  private String appendLowercase(String paramString, boolean paramBoolean, int paramInt)
  {
    if (paramBoolean) {
      return getResources().getString(2131692146, new Object[] { paramString, getResources().getString(paramInt).toLowerCase() });
    }
    return paramString;
  }
  
  private boolean isCategoryEnabled(NotificationManager.Policy paramPolicy, int paramInt)
  {
    boolean bool = false;
    if ((paramPolicy.priorityCategories & paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isEffectSuppressed(int paramInt)
  {
    boolean bool = false;
    if ((this.mPolicy.suppressedVisualEffects & paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void updateControls()
  {
    updatePrioritySettingsSummary();
    updateVisualSettingsSummary();
  }
  
  private void updatePrioritySettingsSummary()
  {
    Object localObject2 = appendLowercase(appendLowercase(getResources().getString(2131693306), isCategoryEnabled(this.mPolicy, 1), 2131693307), isCategoryEnabled(this.mPolicy, 2), 2131693308);
    Object localObject1 = localObject2;
    if (isCategoryEnabled(this.mPolicy, 4))
    {
      if (this.mPolicy.priorityMessageSenders == 0) {
        localObject1 = appendLowercase((String)localObject2, true, 2131693300);
      }
    }
    else
    {
      if (!isCategoryEnabled(this.mPolicy, 8)) {
        break label138;
      }
      if (this.mPolicy.priorityCallSenders != 0) {
        break label126;
      }
      localObject2 = appendLowercase((String)localObject1, true, 2131693309);
    }
    for (;;)
    {
      this.mPrioritySettings.setSummary((CharSequence)localObject2);
      return;
      localObject1 = appendLowercase((String)localObject2, true, 2131693301);
      break;
      label126:
      localObject2 = appendLowercase((String)localObject1, true, 2131693310);
      continue;
      label138:
      localObject2 = localObject1;
      if (isCategoryEnabled(this.mPolicy, 16)) {
        localObject2 = appendLowercase((String)localObject1, true, 2131693311);
      }
    }
  }
  
  private void updateVisualSettingsSummary()
  {
    String str = getString(2131693328);
    if ((isEffectSuppressed(2)) && (isEffectSuppressed(1))) {
      str = getString(2131693331);
    }
    for (;;)
    {
      this.mVisualSettings.setSummary(str);
      return;
      if (isEffectSuppressed(2)) {
        str = getString(2131693329);
      } else if (isEffectSuppressed(1)) {
        str = getString(2131693330);
      }
    }
  }
  
  protected int getHelpResource()
  {
    return 2131693003;
  }
  
  protected int getMetricsCategory()
  {
    return 76;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230897);
    paramBundle = getPreferenceScreen();
    this.mPrioritySettings = paramBundle.findPreference("priority_settings");
    this.mVisualSettings = paramBundle.findPreference("visual_interruptions_settings");
    this.mPolicy = NotificationManager.from(this.mContext).getNotificationPolicy();
  }
  
  public void onResume()
  {
    super.onResume();
    if (isUiRestricted()) {}
  }
  
  protected void onZenModeChanged()
  {
    updateControls();
  }
  
  protected void onZenModeConfigChanged()
  {
    this.mPolicy = NotificationManager.from(this.mContext).getNotificationPolicy();
    updateControls();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenModeSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */