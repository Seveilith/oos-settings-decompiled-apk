package com.android.settings.applications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
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

public class DrawOverlayDetails
  extends AppInfoWithHeader
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private static final int[] APP_OPS_OP_CODE = { 24 };
  private static final String KEY_APP_OPS_SETTINGS_DESC = "app_ops_settings_description";
  private static final String KEY_APP_OPS_SETTINGS_PREFS = "app_ops_settings_preference";
  private static final String KEY_APP_OPS_SETTINGS_SWITCH = "app_ops_settings_switch";
  private static final String LOG_TAG = "DrawOverlayDetails";
  private AppOpsManager mAppOpsManager;
  private AppStateOverlayBridge mOverlayBridge;
  private Preference mOverlayDesc;
  private Preference mOverlayPrefs;
  private AppStateOverlayBridge.OverlayState mOverlayState;
  private Intent mSettingsIntent;
  private SwitchPreference mSwitchPref;
  
  private boolean canDrawOverlay(String paramString)
  {
    return this.mAppOpsManager.noteOpNoThrow(24, this.mPackageInfo.applicationInfo.uid, paramString) == 0;
  }
  
  public static CharSequence getSummary(Context paramContext, AppStateOverlayBridge.OverlayState paramOverlayState)
  {
    if (paramOverlayState.isPermissible()) {}
    for (int i = 2131693544;; i = 2131693545) {
      return paramContext.getString(i);
    }
  }
  
  public static CharSequence getSummary(Context paramContext, ApplicationsState.AppEntry paramAppEntry)
  {
    if ((paramAppEntry.extraInfo instanceof AppStateOverlayBridge.OverlayState)) {
      paramAppEntry = (AppStateOverlayBridge.OverlayState)paramAppEntry.extraInfo;
    }
    for (;;)
    {
      return getSummary(paramContext, paramAppEntry);
      if ((paramAppEntry.extraInfo instanceof AppStateAppOpsBridge.PermissionState)) {
        paramAppEntry = new AppStateOverlayBridge.OverlayState((AppStateAppOpsBridge.PermissionState)paramAppEntry.extraInfo);
      } else {
        paramAppEntry = new AppStateOverlayBridge(paramContext, null, null).getOverlayInfo(paramAppEntry.info.packageName, paramAppEntry.info.uid);
      }
    }
  }
  
  public static CharSequence getSummary(Context paramContext, String paramString)
  {
    int i = 2131693544;
    Object localObject = paramContext.getPackageManager();
    int j;
    try
    {
      localObject = ((PackageManager)localObject).getApplicationInfo(paramString, 0);
      j = ((ApplicationInfo)localObject).uid;
      if ((((ApplicationInfo)localObject).flags & 0x1) != 0)
      {
        localObject = paramContext.getString(2131693544);
        return (CharSequence)localObject;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("DrawOverlayDetails", "Package " + paramString + " not found", localNameNotFoundException);
      return paramContext.getString(2131693545);
    }
    AppOpsManager localAppOpsManager = (AppOpsManager)paramContext.getSystemService("appops");
    if (j == -1) {
      return paramContext.getString(2131693545);
    }
    if (localAppOpsManager.noteOpNoThrow(24, j, paramString) == 0) {}
    for (;;)
    {
      return paramContext.getString(i);
      i = 2131693545;
    }
  }
  
  private void setCanDrawOverlay(boolean paramBoolean)
  {
    AppOpsManager localAppOpsManager = this.mAppOpsManager;
    int j = this.mPackageInfo.applicationInfo.uid;
    String str = this.mPackageName;
    if (paramBoolean) {}
    for (int i = 0;; i = 2)
    {
      localAppOpsManager.setMode(24, j, str, i);
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
    this.mOverlayBridge = new AppStateOverlayBridge(paramBundle, this.mState, null);
    this.mAppOpsManager = ((AppOpsManager)paramBundle.getSystemService("appops"));
    addPreferencesFromResource(2131230734);
    this.mSwitchPref = ((SwitchPreference)findPreference("app_ops_settings_switch"));
    this.mOverlayPrefs = findPreference("app_ops_settings_preference");
    this.mOverlayDesc = findPreference("app_ops_settings_description");
    getPreferenceScreen().setTitle(2131693532);
    this.mSwitchPref.setTitle(2131693536);
    this.mOverlayPrefs.setTitle(2131693537);
    this.mOverlayDesc.setSummary(2131693538);
    this.mSwitchPref.setOnPreferenceChangeListener(this);
    this.mOverlayPrefs.setOnPreferenceClickListener(this);
    this.mSettingsIntent = new Intent("android.intent.action.MAIN").setAction("android.settings.action.MANAGE_OVERLAY_PERMISSION");
  }
  
  public void onDestroy()
  {
    if (this.mOverlayBridge != null) {
      this.mOverlayBridge.release();
    }
    super.onDestroy();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    boolean bool = false;
    if (paramPreference == this.mSwitchPref)
    {
      if ((this.mOverlayState != null) && (((Boolean)paramObject).booleanValue() != this.mOverlayState.isPermissible())) {
        if (!this.mOverlayState.isPermissible()) {
          break label56;
        }
      }
      for (;;)
      {
        setCanDrawOverlay(bool);
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
    if (paramPreference == this.mOverlayPrefs)
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
          Log.w("DrawOverlayDetails", "Unable to launch app draw overlay settings " + this.mSettingsIntent, paramPreference);
        }
      }
    }
    return false;
  }
  
  protected boolean refreshUi()
  {
    this.mOverlayState = this.mOverlayBridge.getOverlayInfo(this.mPackageName, this.mPackageInfo.applicationInfo.uid);
    boolean bool = this.mOverlayState.isPermissible();
    this.mSwitchPref.setChecked(bool);
    this.mSwitchPref.setEnabled(this.mOverlayState.permissionDeclared);
    this.mOverlayPrefs.setEnabled(bool);
    getPreferenceScreen().removePreference(this.mOverlayPrefs);
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\DrawOverlayDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */