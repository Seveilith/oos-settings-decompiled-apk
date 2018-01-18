package com.android.settings.applications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

public class UsageAccessDetails
  extends AppInfoWithHeader
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private static final String KEY_APP_OPS_PREFERENCE_SCREEN = "app_ops_preference_screen";
  private static final String KEY_APP_OPS_SETTINGS_DESC = "app_ops_settings_description";
  private static final String KEY_APP_OPS_SETTINGS_PREFS = "app_ops_settings_preference";
  private static final String KEY_APP_OPS_SETTINGS_SWITCH = "app_ops_settings_switch";
  private AppOpsManager mAppOpsManager;
  private DevicePolicyManager mDpm;
  private Intent mSettingsIntent;
  private SwitchPreference mSwitchPref;
  private AppStateUsageBridge mUsageBridge;
  private Preference mUsageDesc;
  private Preference mUsagePrefs;
  private AppStateUsageBridge.UsageState mUsageState;
  
  private void setHasAccess(boolean paramBoolean)
  {
    AppOpsManager localAppOpsManager = this.mAppOpsManager;
    int j = this.mPackageInfo.applicationInfo.uid;
    String str = this.mPackageName;
    if (paramBoolean) {}
    for (int i = 0;; i = 1)
    {
      localAppOpsManager.setMode(43, j, str, i);
      return;
    }
  }
  
  protected AlertDialog createDialog(int paramInt1, int paramInt2)
  {
    return null;
  }
  
  protected int getMetricsCategory()
  {
    return 183;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getActivity();
    this.mUsageBridge = new AppStateUsageBridge(paramBundle, this.mState, null);
    this.mAppOpsManager = ((AppOpsManager)paramBundle.getSystemService("appops"));
    this.mDpm = ((DevicePolicyManager)paramBundle.getSystemService(DevicePolicyManager.class));
    addPreferencesFromResource(2131230734);
    this.mSwitchPref = ((SwitchPreference)findPreference("app_ops_settings_switch"));
    this.mUsagePrefs = findPreference("app_ops_settings_preference");
    this.mUsageDesc = findPreference("app_ops_settings_description");
    getPreferenceScreen().setTitle(2131693444);
    this.mSwitchPref.setTitle(2131693445);
    this.mUsagePrefs.setTitle(2131693446);
    this.mUsageDesc.setSummary(2131693447);
    this.mSwitchPref.setOnPreferenceChangeListener(this);
    this.mUsagePrefs.setOnPreferenceClickListener(this);
    this.mSettingsIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.USAGE_ACCESS_CONFIG").setPackage(this.mPackageName);
  }
  
  public void onDestroy()
  {
    if (this.mUsageBridge != null) {
      this.mUsageBridge.release();
    }
    super.onDestroy();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    boolean bool = false;
    if (paramPreference == this.mSwitchPref)
    {
      if ((this.mUsageState != null) && (((Boolean)paramObject).booleanValue() != this.mUsageState.isPermissible()))
      {
        if ((this.mUsageState.isPermissible()) && (this.mDpm.isProfileOwnerApp(this.mPackageName))) {
          new AlertDialog.Builder(getContext()).setIcon(17302320).setTitle(17039380).setMessage(2131693529).setPositiveButton(2131690994, null).show();
        }
        if (!this.mUsageState.isPermissible()) {
          break label116;
        }
      }
      for (;;)
      {
        setHasAccess(bool);
        refreshUi();
        return true;
        label116:
        bool = true;
      }
    }
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mUsagePrefs)
    {
      if (this.mSettingsIntent != null) {}
      try
      {
        getActivity().startActivityAsUser(this.mSettingsIntent, new UserHandle(this.mUserId));
        return true;
      }
      catch (ActivityNotFoundException paramPreference)
      {
        for (;;)
        {
          Log.w(TAG, "Unable to launch app usage access settings " + this.mSettingsIntent, paramPreference);
        }
      }
    }
    return false;
  }
  
  protected boolean refreshUi()
  {
    this.mUsageState = this.mUsageBridge.getUsageInfo(this.mPackageName, this.mPackageInfo.applicationInfo.uid);
    boolean bool = this.mUsageState.isPermissible();
    this.mSwitchPref.setChecked(bool);
    this.mSwitchPref.setEnabled(this.mUsageState.permissionDeclared);
    this.mUsagePrefs.setEnabled(bool);
    ResolveInfo localResolveInfo = this.mPm.resolveActivityAsUser(this.mSettingsIntent, 128, this.mUserId);
    if (localResolveInfo != null)
    {
      if (findPreference("app_ops_settings_preference") == null) {
        getPreferenceScreen().addPreference(this.mUsagePrefs);
      }
      Bundle localBundle = localResolveInfo.activityInfo.metaData;
      this.mSettingsIntent.setComponent(new ComponentName(localResolveInfo.activityInfo.packageName, localResolveInfo.activityInfo.name));
      if ((localBundle != null) && (localBundle.containsKey("android.settings.metadata.USAGE_ACCESS_REASON"))) {
        this.mSwitchPref.setSummary(localBundle.getString("android.settings.metadata.USAGE_ACCESS_REASON"));
      }
    }
    for (;;)
    {
      return true;
      if (findPreference("app_ops_settings_preference") != null) {
        getPreferenceScreen().removePreference(this.mUsagePrefs);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\UsageAccessDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */