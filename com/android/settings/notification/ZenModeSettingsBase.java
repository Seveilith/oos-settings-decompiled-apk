package com.android.settings.notification;

import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Global;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import com.android.settings.RestrictedSettingsFragment;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class ZenModeSettingsBase
  extends RestrictedSettingsFragment
{
  protected static final boolean DEBUG = Log.isLoggable("ZenModeSettings", 3);
  protected static final String TAG = "ZenModeSettings";
  protected Context mContext;
  private final Handler mHandler = new Handler();
  protected Set<Map.Entry<String, AutomaticZenRule>> mRules;
  private final SettingsObserver mSettingsObserver = new SettingsObserver(null);
  protected int mZenMode;
  
  public ZenModeSettingsBase()
  {
    super("no_adjust_volume");
  }
  
  private Set<Map.Entry<String, AutomaticZenRule>> getZenModeRules()
  {
    return NotificationManager.from(this.mContext).getAutomaticZenRules().entrySet();
  }
  
  private void updateZenMode(boolean paramBoolean)
  {
    int i = Settings.Global.getInt(getContentResolver(), "zen_mode", this.mZenMode);
    if (i == this.mZenMode) {
      return;
    }
    this.mZenMode = i;
    if (DEBUG) {
      Log.d("ZenModeSettings", "updateZenMode mZenMode=" + this.mZenMode);
    }
    if (paramBoolean) {
      onZenModeChanged();
    }
  }
  
  protected String addZenRule(AutomaticZenRule paramAutomaticZenRule)
  {
    boolean bool = true;
    try
    {
      paramAutomaticZenRule = NotificationManager.from(this.mContext).addAutomaticZenRule(paramAutomaticZenRule);
      if (NotificationManager.from(this.mContext).getAutomaticZenRule(paramAutomaticZenRule) != null) {}
      for (;;)
      {
        maybeRefreshRules(bool, true);
        return paramAutomaticZenRule;
        bool = false;
      }
      return null;
    }
    catch (Exception paramAutomaticZenRule) {}
  }
  
  protected void maybeRefreshRules(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      this.mRules = getZenModeRules();
      if (DEBUG) {
        Log.d("ZenModeSettings", "Refreshed mRules=" + this.mRules);
      }
      if (paramBoolean2) {
        onZenModeConfigChanged();
      }
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mContext = getActivity();
    updateZenMode(false);
    maybeRefreshRules(true, false);
    if (DEBUG) {
      Log.d("ZenModeSettings", "Loaded mRules=" + this.mRules);
    }
  }
  
  public void onPause()
  {
    super.onPause();
    this.mSettingsObserver.unregister();
  }
  
  public void onResume()
  {
    super.onResume();
    updateZenMode(true);
    maybeRefreshRules(true, true);
    this.mSettingsObserver.register();
    if (isUiRestricted())
    {
      if (isUiRestrictedByOnlyAdmin())
      {
        getPreferenceScreen().removeAll();
        return;
      }
      finish();
    }
  }
  
  protected abstract void onZenModeChanged();
  
  protected abstract void onZenModeConfigChanged();
  
  protected boolean removeZenRule(String paramString)
  {
    boolean bool = NotificationManager.from(this.mContext).removeAutomaticZenRule(paramString);
    maybeRefreshRules(bool, true);
    return bool;
  }
  
  protected void setZenMode(int paramInt, Uri paramUri)
  {
    NotificationManager.from(this.mContext).setZenMode(paramInt, paramUri, "ZenModeSettings");
  }
  
  protected boolean setZenRule(String paramString, AutomaticZenRule paramAutomaticZenRule)
  {
    boolean bool = NotificationManager.from(this.mContext).updateAutomaticZenRule(paramString, paramAutomaticZenRule);
    maybeRefreshRules(bool, true);
    return bool;
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    private final Uri ZEN_MODE_CONFIG_ETAG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
    private final Uri ZEN_MODE_URI = Settings.Global.getUriFor("zen_mode");
    
    private SettingsObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      super.onChange(paramBoolean, paramUri);
      if (this.ZEN_MODE_URI.equals(paramUri)) {
        ZenModeSettingsBase.-wrap1(ZenModeSettingsBase.this, true);
      }
      if (this.ZEN_MODE_CONFIG_ETAG_URI.equals(paramUri)) {
        ZenModeSettingsBase.this.maybeRefreshRules(true, true);
      }
    }
    
    public void register()
    {
      ZenModeSettingsBase.-wrap0(ZenModeSettingsBase.this).registerContentObserver(this.ZEN_MODE_URI, false, this);
      ZenModeSettingsBase.-wrap0(ZenModeSettingsBase.this).registerContentObserver(this.ZEN_MODE_CONFIG_ETAG_URI, false, this);
    }
    
    public void unregister()
    {
      ZenModeSettingsBase.-wrap0(ZenModeSettingsBase.this).unregisterContentObserver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenModeSettingsBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */