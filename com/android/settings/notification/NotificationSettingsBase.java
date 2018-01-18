package com.android.settings.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;
import java.util.ArrayList;

public abstract class NotificationSettingsBase
  extends SettingsPreferenceFragment
{
  private static final boolean DEBUG = Log.isLoggable("NotifiSettingsBase", 3);
  protected static final String KEY_BLOCK = "block";
  protected static final String KEY_BYPASS_DND = "bypass_dnd";
  protected static final String KEY_IMPORTANCE = "importance";
  protected static final String KEY_LEVELS = "levels";
  protected static final String KEY_MORE = "more";
  protected static final String KEY_SILENT = "silent";
  protected static final String KEY_VISIBILITY_OVERRIDE = "visibility_override";
  private static final String TAG = "NotifiSettingsBase";
  private static final String TUNER_SETTING = "show_importance_slider";
  protected final NotificationBackend mBackend = new NotificationBackend();
  protected OPRestrictedSwitchPreference mBlock;
  protected Context mContext;
  protected boolean mCreated;
  protected ImportanceSeekBarPreference mImportance;
  protected PreferenceCategory mLevelsCategory;
  protected PreferenceCategory mMoreCategory;
  protected String mPkg;
  protected PackageInfo mPkgInfo;
  protected PackageManager mPm;
  protected OPRestrictedSwitchPreference mPriority;
  protected boolean mShowSlider = false;
  protected OPRestrictedSwitchPreference mSilent;
  protected RestrictedLockUtils.EnforcedAdmin mSuspendedAppsAdmin;
  protected int mUid;
  protected UserManager mUm;
  protected int mUserId;
  protected RestrictedDropDownPreference mVisibilityOverride;
  
  private PackageInfo findPackageInfo(String paramString, int paramInt)
  {
    String[] arrayOfString = this.mPm.getPackagesForUid(paramInt);
    if ((arrayOfString != null) && (paramString != null))
    {
      int i = arrayOfString.length;
      paramInt = 0;
      while (paramInt < i)
      {
        if (paramString.equals(arrayOfString[paramInt])) {
          try
          {
            PackageInfo localPackageInfo = this.mPm.getPackageInfo(paramString, 64);
            return localPackageInfo;
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException)
          {
            Log.w("NotifiSettingsBase", "Failed to load package " + paramString, localNameNotFoundException);
          }
        }
        paramInt += 1;
      }
    }
    return null;
  }
  
  private int getGlobalVisibility()
  {
    int i = 64536;
    if (!getLockscreenNotificationsEnabled()) {
      i = -1;
    }
    while (getLockscreenAllowPrivateNotifications()) {
      return i;
    }
    return 0;
  }
  
  private void setRestrictedIfNotificationFeaturesDisabled(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfKeyguardFeaturesDisabled(this.mContext, paramInt, this.mUserId);
    if (localEnforcedAdmin != null)
    {
      paramCharSequence1 = new RestrictedDropDownPreference.RestrictedItem(paramCharSequence1, paramCharSequence2, localEnforcedAdmin);
      this.mVisibilityOverride.addRestrictedItem(paramCharSequence1);
    }
  }
  
  protected boolean getLockscreenAllowPrivateNotifications()
  {
    boolean bool = false;
    if (Settings.Secure.getInt(getContentResolver(), "lock_screen_allow_private_notifications", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  protected boolean getLockscreenNotificationsEnabled()
  {
    boolean bool = false;
    if (Settings.Secure.getInt(getContentResolver(), "lock_screen_show_notifications", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  protected boolean isRemoveLevelsAndImportanceAPP()
  {
    return ("com.oneplus.deskclock".equals(this.mPkg)) || ("com.android.dialer".equals(this.mPkg)) || ("com.android.incallui".equals(this.mPkg)) || ("com.oneplus.opbackup".equals(this.mPkg));
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (DEBUG) {
      Log.d("NotifiSettingsBase", "onActivityCreated mCreated=" + this.mCreated);
    }
    if (this.mCreated)
    {
      Log.w("NotifiSettingsBase", "onActivityCreated: ignoring duplicate call");
      return;
    }
    this.mCreated = true;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mContext = getActivity();
    Intent localIntent = getActivity().getIntent();
    Bundle localBundle = getArguments();
    if (DEBUG) {
      Log.d("NotifiSettingsBase", "onCreate getIntent()=" + localIntent);
    }
    if ((localIntent == null) && (localBundle == null))
    {
      Log.w("NotifiSettingsBase", "No intent");
      toastAndFinish();
      return;
    }
    this.mPm = getPackageManager();
    this.mUm = ((UserManager)this.mContext.getSystemService("user"));
    if ((localBundle != null) && (localBundle.containsKey("package")))
    {
      paramBundle = localBundle.getString("package");
      this.mPkg = paramBundle;
      if ((localBundle == null) || (!localBundle.containsKey("uid"))) {
        break label256;
      }
    }
    label256:
    for (int i = localBundle.getInt("uid");; i = localIntent.getIntExtra("app_uid", -1))
    {
      this.mUid = i;
      if ((this.mUid != -1) && (!TextUtils.isEmpty(this.mPkg))) {
        break label269;
      }
      Log.w("NotifiSettingsBase", "Missing extras: app_package was " + this.mPkg + ", " + "app_uid" + " was " + this.mUid);
      toastAndFinish();
      return;
      paramBundle = localIntent.getStringExtra("app_package");
      break;
    }
    label269:
    this.mUserId = UserHandle.getUserId(this.mUid);
    if (DEBUG) {
      Log.d("NotifiSettingsBase", "Load details for pkg=" + this.mPkg + " uid=" + this.mUid);
    }
    this.mPkgInfo = findPackageInfo(this.mPkg, this.mUid);
    if (this.mPkgInfo == null)
    {
      Log.w("NotifiSettingsBase", "Failed to find package info: app_package was " + this.mPkg + ", " + "app_uid" + " was " + this.mUid);
      toastAndFinish();
      return;
    }
    this.mSuspendedAppsAdmin = RestrictedLockUtils.checkIfApplicationIsSuspended(this.mContext, this.mPkg, this.mUserId);
    if (Settings.Secure.getInt(getContentResolver(), "show_importance_slider", 0) == 1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mShowSlider = bool;
      return;
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if ((this.mUid != -1) && (getPackageManager().getPackagesForUid(this.mUid) == null))
    {
      finish();
      return;
    }
    this.mSuspendedAppsAdmin = RestrictedLockUtils.checkIfApplicationIsSuspended(this.mContext, this.mPkg, this.mUserId);
    if (this.mImportance != null) {
      this.mImportance.setDisabledByAdmin(this.mSuspendedAppsAdmin);
    }
    if (this.mPriority != null) {
      this.mPriority.setDisabledByAdmin(this.mSuspendedAppsAdmin);
    }
    if (this.mBlock != null) {
      this.mBlock.setDisabledByAdmin(this.mSuspendedAppsAdmin);
    }
    if (("com.oneplus.deskclock".equals(this.mPkg)) || ("com.android.dialer".equals(this.mPkg)) || ("com.android.incallui".equals(this.mPkg)) || ("com.google.android.calendar".equals(this.mPkg)) || ("com.oneplus.calendar".equals(this.mPkg))) {
      this.mBlock.setEnabled(false);
    }
    if (this.mSilent != null) {
      this.mSilent.setDisabledByAdmin(this.mSuspendedAppsAdmin);
    }
    if (this.mVisibilityOverride != null) {
      this.mVisibilityOverride.setDisabledByAdmin(this.mSuspendedAppsAdmin);
    }
    if ((this.mShowSlider) && (this.mLevelsCategory != null)) {
      getPreferenceScreen().removePreference(this.mLevelsCategory);
    }
    if (isRemoveLevelsAndImportanceAPP())
    {
      if (this.mLevelsCategory != null) {
        getPreferenceScreen().removePreference(this.mLevelsCategory);
      }
      if (this.mImportance != null) {
        setVisible(this.mImportance, false);
      }
    }
  }
  
  protected void setVisible(Preference paramPreference, boolean paramBoolean)
  {
    if (getPreferenceScreen().findPreference(paramPreference.getKey()) != null) {}
    for (boolean bool = true; bool == paramBoolean; bool = false) {
      return;
    }
    if (paramBoolean)
    {
      getPreferenceScreen().addPreference(paramPreference);
      return;
    }
    getPreferenceScreen().removePreference(paramPreference);
  }
  
  protected void setVisibleForMore(Preference paramPreference, boolean paramBoolean)
  {
    boolean bool = false;
    if (getPreferenceScreen().findPreference(paramPreference.getKey()) != null) {
      bool = true;
    }
    if (bool == paramBoolean) {
      return;
    }
    if (paramBoolean)
    {
      getPreferenceScreen().addPreference(this.mMoreCategory);
      this.mMoreCategory.addPreference(paramPreference);
    }
    for (;;)
    {
      if (this.mMoreCategory.getPreferenceCount() == 0) {
        getPreferenceScreen().removePreference(this.mMoreCategory);
      }
      return;
      this.mMoreCategory.removePreference(paramPreference);
    }
  }
  
  protected void setupImportancePrefs(boolean paramBoolean1, int paramInt, boolean paramBoolean2)
  {
    boolean bool = true;
    int i = 0;
    if (this.mShowSlider)
    {
      setVisible(this.mBlock, false);
      setVisible(this.mSilent, false);
      this.mImportance.setDisabledByAdmin(this.mSuspendedAppsAdmin);
      ImportanceSeekBarPreference localImportanceSeekBarPreference = this.mImportance;
      if (paramBoolean1)
      {
        i = 1;
        localImportanceSeekBarPreference.setMinimumProgress(i);
        this.mImportance.setMax(5);
        this.mImportance.setProgress(paramInt);
        localImportanceSeekBarPreference = this.mImportance;
        if (paramInt != 64536) {
          break label122;
        }
      }
      label122:
      for (paramBoolean1 = bool;; paramBoolean1 = false)
      {
        localImportanceSeekBarPreference.setAutoOn(paramBoolean1);
        this.mImportance.setCallback(new ImportanceSeekBarPreference.Callback()
        {
          public void onImportanceChanged(int paramAnonymousInt, boolean paramAnonymousBoolean)
          {
            if (paramAnonymousBoolean) {
              NotificationSettingsBase.this.mBackend.setImportance(NotificationSettingsBase.this.mPkg, NotificationSettingsBase.this.mUid, paramAnonymousInt);
            }
            NotificationSettingsBase.this.updateDependents(paramAnonymousInt);
          }
        });
        return;
        i = 0;
        break;
      }
    }
    setVisible(this.mImportance, false);
    if (paramBoolean1)
    {
      setVisible(this.mBlock, false);
      setVisible(this.mSilent, false);
      return;
    }
    if (paramInt != 0)
    {
      paramBoolean1 = paramBoolean2;
      this.mBlock.setChecked(paramBoolean1);
      this.mBlock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          if (((Boolean)paramAnonymousObject).booleanValue()) {}
          for (int i = 0;; i = 64536)
          {
            NotificationSettingsBase.this.mBackend.setImportance(NotificationSettingsBase.this.mPkgInfo.packageName, NotificationSettingsBase.this.mUid, i);
            NotificationSettingsBase.this.updateDependents(i);
            return true;
          }
        }
      });
      this.mSilent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          if (((Boolean)paramAnonymousObject).booleanValue()) {}
          for (int i = 2;; i = 64536)
          {
            NotificationSettingsBase.this.mBackend.setImportance(NotificationSettingsBase.this.mPkgInfo.packageName, NotificationSettingsBase.this.mUid, i);
            NotificationSettingsBase.this.updateDependents(i);
            return true;
          }
        }
      });
      if (!paramBoolean2) {
        break label221;
      }
      paramInt = i;
    }
    label221:
    for (;;)
    {
      updateDependents(paramInt);
      return;
      paramBoolean1 = true;
      break;
    }
  }
  
  protected void setupPriorityPref(boolean paramBoolean)
  {
    this.mPriority.setDisabledByAdmin(this.mSuspendedAppsAdmin);
    this.mPriority.setChecked(paramBoolean);
    this.mPriority.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
        return NotificationSettingsBase.this.mBackend.setBypassZenMode(NotificationSettingsBase.this.mPkgInfo.packageName, NotificationSettingsBase.this.mUid, bool);
      }
    });
  }
  
  protected void setupVisOverridePref(int paramInt)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    this.mVisibilityOverride.clearRestrictedItems();
    if ((getLockscreenNotificationsEnabled()) && (getLockscreenAllowPrivateNotifications()))
    {
      str1 = getString(2131693212);
      str2 = Integer.toString(64536);
      localArrayList1.add(str1);
      localArrayList2.add(str2);
      setRestrictedIfNotificationFeaturesDisabled(str1, str2, 12);
    }
    String str1 = getString(2131693213);
    String str2 = Integer.toString(0);
    localArrayList1.add(str1);
    localArrayList2.add(str2);
    setRestrictedIfNotificationFeaturesDisabled(str1, str2, 4);
    localArrayList1.add(getString(2131693214));
    localArrayList2.add(Integer.toString(-1));
    this.mVisibilityOverride.setEntries((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList1.size()]));
    this.mVisibilityOverride.setEntryValues((CharSequence[])localArrayList2.toArray(new CharSequence[localArrayList2.size()]));
    if (paramInt == 64536) {
      this.mVisibilityOverride.setValue(Integer.toString(getGlobalVisibility()));
    }
    for (;;)
    {
      this.mVisibilityOverride.setSummary("%s");
      this.mVisibilityOverride.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          int j = Integer.parseInt((String)paramAnonymousObject);
          int i = j;
          if (j == NotificationSettingsBase.-wrap0(NotificationSettingsBase.this)) {
            i = 64536;
          }
          NotificationSettingsBase.this.mBackend.setVisibilityOverride(NotificationSettingsBase.this.mPkgInfo.packageName, NotificationSettingsBase.this.mUid, i);
          return true;
        }
      });
      return;
      this.mVisibilityOverride.setValue(Integer.toString(paramInt));
    }
  }
  
  protected void toastAndFinish()
  {
    Toast.makeText(this.mContext, 2131692136, 0).show();
    getActivity().finish();
  }
  
  abstract void updateDependents(int paramInt);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\NotificationSettingsBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */