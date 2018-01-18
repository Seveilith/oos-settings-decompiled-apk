package com.android.settings.notification;

import android.app.NotificationManager;
import android.app.NotificationManager.Policy;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;

public class ZenModeVisualInterruptionSettings
  extends ZenModeSettingsBase
{
  private static final String KEY_SCREEN_OFF = "screenOff";
  private static final String KEY_SCREEN_ON = "screenOn";
  private boolean mDisableListeners;
  private NotificationManager.Policy mPolicy;
  private SwitchPreference mScreenOff;
  private SwitchPreference mScreenOn;
  
  private int getNewSuppressedEffects(boolean paramBoolean, int paramInt)
  {
    int i = this.mPolicy.suppressedVisualEffects;
    if (paramBoolean) {
      return i | paramInt;
    }
    return i & paramInt;
  }
  
  private boolean isEffectSuppressed(int paramInt)
  {
    boolean bool = false;
    if ((this.mPolicy.suppressedVisualEffects & paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void savePolicy(int paramInt)
  {
    this.mPolicy = new NotificationManager.Policy(this.mPolicy.priorityCategories, this.mPolicy.priorityCallSenders, this.mPolicy.priorityMessageSenders, paramInt);
    NotificationManager.from(this.mContext).setNotificationPolicy(this.mPolicy);
  }
  
  private void updateControls()
  {
    this.mDisableListeners = true;
    this.mScreenOff.setChecked(isEffectSuppressed(1));
    this.mScreenOn.setChecked(isEffectSuppressed(2));
    this.mDisableListeners = false;
  }
  
  protected int getMetricsCategory()
  {
    return 262;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230898);
    paramBundle = getPreferenceScreen();
    this.mPolicy = NotificationManager.from(this.mContext).getNotificationPolicy();
    this.mScreenOff = ((SwitchPreference)paramBundle.findPreference("screenOff"));
    if (!getResources().getBoolean(17956933)) {
      this.mScreenOff.setSummary(2131693327);
    }
    this.mScreenOff.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        if (ZenModeVisualInterruptionSettings.-get0(ZenModeVisualInterruptionSettings.this)) {
          return true;
        }
        boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
        MetricsLogger.action(ZenModeVisualInterruptionSettings.this.mContext, 263, bool);
        if (ZenModeVisualInterruptionSettings.DEBUG) {
          Log.d("ZenModeSettings", "onPrefChange suppressWhenScreenOff=" + bool);
        }
        ZenModeVisualInterruptionSettings.-wrap1(ZenModeVisualInterruptionSettings.this, ZenModeVisualInterruptionSettings.-wrap0(ZenModeVisualInterruptionSettings.this, bool, 1));
        return true;
      }
    });
    this.mScreenOn = ((SwitchPreference)paramBundle.findPreference("screenOn"));
    this.mScreenOn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        if (ZenModeVisualInterruptionSettings.-get0(ZenModeVisualInterruptionSettings.this)) {
          return true;
        }
        boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
        MetricsLogger.action(ZenModeVisualInterruptionSettings.this.mContext, 269, bool);
        if (ZenModeVisualInterruptionSettings.DEBUG) {
          Log.d("ZenModeSettings", "onPrefChange suppressWhenScreenOn=" + bool);
        }
        ZenModeVisualInterruptionSettings.-wrap1(ZenModeVisualInterruptionSettings.this, ZenModeVisualInterruptionSettings.-wrap0(ZenModeVisualInterruptionSettings.this, bool, 2));
        return true;
      }
    });
  }
  
  protected void onZenModeChanged() {}
  
  protected void onZenModeConfigChanged()
  {
    this.mPolicy = NotificationManager.from(this.mContext).getNotificationPolicy();
    updateControls();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenModeVisualInterruptionSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */