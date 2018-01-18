package com.android.settings.applications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.AppOpsManager.PackageOps;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import java.util.Iterator;

public class WriteSettingsDetails
  extends AppInfoWithHeader
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private static final int[] APP_OPS_OP_CODE = { 23 };
  private static final String KEY_APP_OPS_PREFERENCE_SCREEN = "app_ops_preference_screen";
  private static final String KEY_APP_OPS_SETTINGS_DESC = "app_ops_settings_description";
  private static final String KEY_APP_OPS_SETTINGS_PREFS = "app_ops_settings_preference";
  private static final String KEY_APP_OPS_SETTINGS_SWITCH = "app_ops_settings_switch";
  private static final String LOG_TAG = "WriteSettingsDetails";
  private AppStateWriteSettingsBridge mAppBridge;
  private AppOpsManager mAppOpsManager;
  private Intent mSettingsIntent;
  private SwitchPreference mSwitchPref;
  private Preference mWriteSettingsDesc;
  private Preference mWriteSettingsPrefs;
  private AppStateWriteSettingsBridge.WriteSettingsState mWriteSettingsState;
  
  private boolean canWriteSettings(String paramString)
  {
    return this.mAppOpsManager.noteOpNoThrow(23, this.mPackageInfo.applicationInfo.uid, paramString) == 0;
  }
  
  public static CharSequence getSummary(Context paramContext, AppStateWriteSettingsBridge.WriteSettingsState paramWriteSettingsState)
  {
    if (paramWriteSettingsState.isPermissible()) {}
    for (int i = 2131693555;; i = 2131693556) {
      return paramContext.getString(i);
    }
  }
  
  public static CharSequence getSummary(Context paramContext, ApplicationsState.AppEntry paramAppEntry)
  {
    if ((paramAppEntry.extraInfo instanceof AppStateWriteSettingsBridge.WriteSettingsState)) {
      paramAppEntry = (AppStateWriteSettingsBridge.WriteSettingsState)paramAppEntry.extraInfo;
    }
    for (;;)
    {
      return getSummary(paramContext, paramAppEntry);
      if ((paramAppEntry.extraInfo instanceof AppStateAppOpsBridge.PermissionState)) {
        paramAppEntry = new AppStateWriteSettingsBridge.WriteSettingsState((AppStateAppOpsBridge.PermissionState)paramAppEntry.extraInfo);
      } else {
        paramAppEntry = new AppStateWriteSettingsBridge(paramContext, null, null).getWriteSettingsInfo(paramAppEntry.info.packageName, paramAppEntry.info.uid);
      }
    }
  }
  
  public static CharSequence getSummary(Context paramContext, String paramString)
  {
    int k = 2131693556;
    int i = 0;
    Object localObject1 = paramContext.getPackageManager();
    int j;
    Object localObject2;
    try
    {
      j = ((PackageManager)localObject1).getApplicationInfo(paramString, 0).flags;
      if ((j & 0x1) != 0) {
        i = 1;
      }
      localObject1 = (AppOpsManager)paramContext.getSystemService("appops");
      localObject2 = ((AppOpsManager)localObject1).getPackagesForOps(APP_OPS_OP_CODE);
      if (localObject2 == null) {
        return paramContext.getString(2131693556);
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("WriteSettingsDetails", "Package " + paramString + " not found", localNameNotFoundException);
      return paramContext.getString(2131693556);
    }
    if (i != 0) {}
    for (i = 0;; i = -1)
    {
      localObject2 = ((Iterable)localObject2).iterator();
      AppOpsManager.PackageOps localPackageOps;
      do
      {
        j = i;
        if (!((Iterator)localObject2).hasNext()) {
          break;
        }
        localPackageOps = (AppOpsManager.PackageOps)((Iterator)localObject2).next();
      } while (!paramString.equals(localPackageOps.getPackageName()));
      j = localPackageOps.getUid();
      if (j != -1) {
        break;
      }
      return paramContext.getString(2131693556);
    }
    i = k;
    if (localNameNotFoundException.noteOpNoThrow(23, j, paramString) == 0) {
      i = 2131693555;
    }
    return paramContext.getString(i);
  }
  
  private void setCanWriteSettings(boolean paramBoolean)
  {
    AppOpsManager localAppOpsManager = this.mAppOpsManager;
    int j = this.mPackageInfo.applicationInfo.uid;
    String str = this.mPackageName;
    if (paramBoolean) {}
    for (int i = 0;; i = 2)
    {
      localAppOpsManager.setMode(23, j, str, i);
      return;
    }
  }
  
  protected AlertDialog createDialog(int paramInt1, int paramInt2)
  {
    return null;
  }
  
  protected int getMetricsCategory()
  {
    return 221;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getActivity();
    this.mAppBridge = new AppStateWriteSettingsBridge(paramBundle, this.mState, null);
    this.mAppOpsManager = ((AppOpsManager)paramBundle.getSystemService("appops"));
    addPreferencesFromResource(2131230734);
    this.mSwitchPref = ((SwitchPreference)findPreference("app_ops_settings_switch"));
    this.mWriteSettingsPrefs = findPreference("app_ops_settings_preference");
    this.mWriteSettingsDesc = findPreference("app_ops_settings_description");
    getPreferenceScreen().setTitle(2131693546);
    this.mSwitchPref.setTitle(2131693553);
    this.mWriteSettingsPrefs.setTitle(2131693552);
    this.mWriteSettingsDesc.setSummary(2131693554);
    this.mSwitchPref.setOnPreferenceChangeListener(this);
    this.mWriteSettingsPrefs.setOnPreferenceClickListener(this);
    this.mSettingsIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.USAGE_ACCESS_CONFIG").setPackage(this.mPackageName);
  }
  
  public void onDestroy()
  {
    if (this.mAppBridge != null) {
      this.mAppBridge.release();
    }
    super.onDestroy();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    boolean bool = false;
    if (paramPreference == this.mSwitchPref)
    {
      if ((this.mWriteSettingsState != null) && (((Boolean)paramObject).booleanValue() != this.mWriteSettingsState.isPermissible())) {
        if (!this.mWriteSettingsState.isPermissible()) {
          break label56;
        }
      }
      for (;;)
      {
        setCanWriteSettings(bool);
        refreshUi();
        return true;
        label56:
        bool = true;
      }
    }
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mWriteSettingsPrefs)
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
          Log.w("WriteSettingsDetails", "Unable to launch write system settings " + this.mSettingsIntent, paramPreference);
        }
      }
    }
    return false;
  }
  
  protected boolean refreshUi()
  {
    if (this.mPackageInfo != null) {
      this.mWriteSettingsState = this.mAppBridge.getWriteSettingsInfo(this.mPackageName, this.mPackageInfo.applicationInfo.uid);
    }
    if (this.mWriteSettingsState != null)
    {
      boolean bool = this.mWriteSettingsState.isPermissible();
      this.mSwitchPref.setChecked(bool);
      this.mSwitchPref.setEnabled(this.mWriteSettingsState.permissionDeclared);
      this.mWriteSettingsPrefs.setEnabled(bool);
      getPreferenceScreen().removePreference(this.mWriteSettingsPrefs);
    }
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\WriteSettingsDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */