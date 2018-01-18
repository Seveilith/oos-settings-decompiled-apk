package com.android.settings.notification;

import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.app.NotificationManager;
import android.app.NotificationManager.Policy;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.AppHeader;
import com.android.settings.ui.RadioButtonPreference;
import com.android.settings.ui.RadioButtonPreference.OnClickListener;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;
import java.util.Iterator;
import java.util.List;

public class AppNotificationSettings
  extends NotificationSettingsBase
  implements RadioButtonPreference.OnClickListener
{
  private static final Intent APP_NOTIFICATION_PREFS_CATEGORY_INTENT = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.NOTIFICATION_PREFERENCES");
  private static final boolean DEBUG = Log.isLoggable("AppNotificationSettings", 3);
  private static final String KEY_BLOCK_ALL_NOTIFICATIONS = "block_all_notifications";
  private static final String KEY_DENOISE_NOTIFICATION = "denoise_notification";
  private static final String KEY_DONOT_SILENCE_OR_BLOCK = "donot_silence_or_block";
  protected static final String KEY_LED_ENABLED = "led_enabled";
  private static final String TAG = "AppNotificationSettings";
  private NotificationBackend.AppRow mAppRow;
  private RadioButtonPreference mBlockAllNotificationsPreference;
  private RadioButtonPreference mDenoiseNotificationPreference;
  private boolean mDndVisualEffectsSuppressed;
  private RadioButtonPreference mDonotDilenceOrBlockPreference;
  private INotificationManager mINotificationManager;
  private OPRestrictedSwitchPreference mLedEnabled;
  
  private void applyConfigActivities(ArrayMap<String, NotificationBackend.AppRow> paramArrayMap, List<ResolveInfo> paramList)
  {
    Object localObject2;
    Object localObject1;
    if (DEBUG)
    {
      localObject2 = new StringBuilder().append("Found ").append(paramList.size()).append(" preference activities");
      if (paramList.size() == 0)
      {
        localObject1 = " ;_;";
        Log.d("AppNotificationSettings", (String)localObject1);
      }
    }
    else
    {
      paramList = paramList.iterator();
    }
    for (;;)
    {
      if (!paramList.hasNext()) {
        return;
      }
      localObject1 = ((ResolveInfo)paramList.next()).activityInfo;
      localObject2 = (NotificationBackend.AppRow)paramArrayMap.get(((ActivityInfo)localObject1).applicationInfo.packageName);
      if (localObject2 == null)
      {
        if (!DEBUG) {
          continue;
        }
        Log.v("AppNotificationSettings", "Ignoring notification preference activity (" + ((ActivityInfo)localObject1).name + ") for unknown package " + ((ActivityInfo)localObject1).packageName);
        continue;
        localObject1 = "";
        break;
      }
      if (((NotificationBackend.AppRow)localObject2).settingsIntent != null)
      {
        if (DEBUG) {
          Log.v("AppNotificationSettings", "Ignoring duplicate notification preference activity (" + ((ActivityInfo)localObject1).name + ") for package " + ((ActivityInfo)localObject1).packageName);
        }
      }
      else {
        ((NotificationBackend.AppRow)localObject2).settingsIntent = new Intent(APP_NOTIFICATION_PREFS_CATEGORY_INTENT).setClassName(((ActivityInfo)localObject1).packageName, ((ActivityInfo)localObject1).name);
      }
    }
  }
  
  private void collectConfigActivities(ArrayMap<String, NotificationBackend.AppRow> paramArrayMap)
  {
    applyConfigActivities(paramArrayMap, queryNotificationConfigActivities());
  }
  
  private List<ResolveInfo> queryNotificationConfigActivities()
  {
    if (DEBUG) {
      Log.d("AppNotificationSettings", "APP_NOTIFICATION_PREFS_CATEGORY_INTENT is " + APP_NOTIFICATION_PREFS_CATEGORY_INTENT);
    }
    return this.mPm.queryIntentActivities(APP_NOTIFICATION_PREFS_CATEGORY_INTENT, 0);
  }
  
  protected boolean checkCanBeVisible(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 64536) {
      return true;
    }
    return paramInt2 >= paramInt1;
  }
  
  protected int getMetricsCategory()
  {
    return 72;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (this.mAppRow == null) {
      return;
    }
    AppHeader.createAppHeader(this, this.mAppRow.icon, this.mAppRow.label, this.mAppRow.pkg, this.mAppRow.uid, this.mAppRow.settingsIntent);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230733);
    this.mImportance = ((ImportanceSeekBarPreference)findPreference("importance"));
    this.mLevelsCategory = ((PreferenceCategory)findPreference("levels"));
    this.mMoreCategory = ((PreferenceCategory)findPreference("more"));
    this.mPriority = ((OPRestrictedSwitchPreference)getPreferenceScreen().findPreference("bypass_dnd"));
    this.mVisibilityOverride = ((RestrictedDropDownPreference)getPreferenceScreen().findPreference("visibility_override"));
    this.mLedEnabled = ((OPRestrictedSwitchPreference)getPreferenceScreen().findPreference("led_enabled"));
    this.mBlock = ((OPRestrictedSwitchPreference)getPreferenceScreen().findPreference("block"));
    this.mSilent = ((OPRestrictedSwitchPreference)getPreferenceScreen().findPreference("silent"));
    this.mDonotDilenceOrBlockPreference = ((RadioButtonPreference)findPreference("donot_silence_or_block"));
    this.mDenoiseNotificationPreference = ((RadioButtonPreference)findPreference("denoise_notification"));
    this.mBlockAllNotificationsPreference = ((RadioButtonPreference)findPreference("block_all_notifications"));
    this.mINotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    if (this.mPkgInfo != null)
    {
      this.mAppRow = this.mBackend.loadAppRow(this.mContext, this.mPm, this.mPkgInfo);
      paramBundle = NotificationManager.from(this.mContext).getNotificationPolicy();
      if (paramBundle != null) {
        break label512;
      }
    }
    for (boolean bool = false;; bool = true)
    {
      this.mDndVisualEffectsSuppressed = bool;
      paramBundle = new ArrayMap();
      paramBundle.put(this.mAppRow.pkg, this.mAppRow);
      collectConfigActivities(paramBundle);
      setupImportancePrefs(this.mAppRow.systemApp, this.mAppRow.appImportance, this.mAppRow.banned);
      setupPriorityPref(this.mAppRow.appBypassDnd);
      setupVisOverridePref(this.mAppRow.appVisOverride);
      updateDependents(this.mAppRow.appImportance);
      if (this.mLedEnabled != null) {
        this.mLedEnabled.setChecked(this.mAppRow.ledEnabled);
      }
      if (("com.android.incallui".equals(this.mPkg)) || (("com.android.dialer".equals(this.mPkg)) && (this.mLedEnabled != null))) {
        this.mLedEnabled.setEnabled(false);
      }
      if (this.mLedEnabled != null) {
        this.mLedEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
          public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
          {
            boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
            return AppNotificationSettings.this.mBackend.setLedEnabled(AppNotificationSettings.this.mPkg, bool);
          }
        });
      }
      if (this.mLedEnabled != null) {
        getPreferenceScreen().removePreference(this.mLedEnabled);
      }
      if (this.mBlock != null) {
        getPreferenceScreen().removePreference(this.mBlock);
      }
      if (this.mSilent != null) {
        getPreferenceScreen().removePreference(this.mSilent);
      }
      if (this.mDonotDilenceOrBlockPreference != null) {
        this.mDonotDilenceOrBlockPreference.setOnClickListener(this);
      }
      if (this.mDenoiseNotificationPreference != null) {
        this.mDenoiseNotificationPreference.setOnClickListener(this);
      }
      if (this.mBlockAllNotificationsPreference != null) {
        this.mBlockAllNotificationsPreference.setOnClickListener(this);
      }
      return;
      label512:
      if (paramBundle.suppressedVisualEffects == 0) {
        break;
      }
    }
  }
  
  public void onRadioButtonClicked(RadioButtonPreference paramRadioButtonPreference)
  {
    int i = 0;
    if (paramRadioButtonPreference == this.mDonotDilenceOrBlockPreference)
    {
      this.mDonotDilenceOrBlockPreference.setChecked(true);
      this.mDenoiseNotificationPreference.setChecked(false);
      this.mBlockAllNotificationsPreference.setChecked(false);
      i = 0;
    }
    for (;;)
    {
      try
      {
        this.mINotificationManager.setOPLevel(this.mPkg, this.mUid, i);
        return;
      }
      catch (Exception paramRadioButtonPreference)
      {
        paramRadioButtonPreference.printStackTrace();
      }
      if (paramRadioButtonPreference == this.mDenoiseNotificationPreference)
      {
        this.mDonotDilenceOrBlockPreference.setChecked(false);
        this.mDenoiseNotificationPreference.setChecked(true);
        this.mBlockAllNotificationsPreference.setChecked(false);
        i = 1;
      }
      else if (paramRadioButtonPreference == this.mBlockAllNotificationsPreference)
      {
        this.mDonotDilenceOrBlockPreference.setChecked(false);
        this.mDenoiseNotificationPreference.setChecked(false);
        this.mBlockAllNotificationsPreference.setChecked(true);
        i = 2;
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    int i = 0;
    try
    {
      int j = this.mINotificationManager.getOPLevel(this.mPkg, this.mUid);
      i = j;
    }
    catch (Exception localException)
    {
      label168:
      label173:
      label178:
      do
      {
        for (;;)
        {
          RadioButtonPreference localRadioButtonPreference;
          localException.printStackTrace();
          continue;
          boolean bool = false;
          continue;
          bool = false;
          continue;
          bool = false;
        }
        if (this.mAppRow.appImportance == 1)
        {
          this.mDonotDilenceOrBlockPreference.setChecked(false);
          this.mDenoiseNotificationPreference.setChecked(true);
          this.mBlockAllNotificationsPreference.setChecked(false);
          return;
        }
      } while ((this.mAppRow.appImportance <= 1) || (this.mAppRow.appImportance >= 5));
      this.mDonotDilenceOrBlockPreference.setChecked(true);
      this.mDenoiseNotificationPreference.setChecked(false);
      this.mBlockAllNotificationsPreference.setChecked(false);
    }
    if (this.mDonotDilenceOrBlockPreference != null)
    {
      localRadioButtonPreference = this.mDonotDilenceOrBlockPreference;
      if (i == 0)
      {
        bool = true;
        localRadioButtonPreference.setChecked(bool);
      }
    }
    else
    {
      if (this.mDenoiseNotificationPreference != null)
      {
        localRadioButtonPreference = this.mDenoiseNotificationPreference;
        if (i != 1) {
          break label168;
        }
        bool = true;
        localRadioButtonPreference.setChecked(bool);
      }
      if (this.mBlockAllNotificationsPreference != null)
      {
        localRadioButtonPreference = this.mBlockAllNotificationsPreference;
        if (i != 2) {
          break label173;
        }
        bool = true;
        localRadioButtonPreference.setChecked(bool);
      }
      if (Settings.Secure.getInt(getContentResolver(), "accessibility_display_inversion_enabled", 0) == 1)
      {
        if (this.mAppRow.appImportance != 0) {
          break label178;
        }
        this.mDonotDilenceOrBlockPreference.setChecked(false);
        this.mDenoiseNotificationPreference.setChecked(false);
        this.mBlockAllNotificationsPreference.setChecked(true);
      }
      return;
    }
  }
  
  protected void updateDependents(int paramInt)
  {
    Object localObject = new LockPatternUtils(getActivity());
    boolean bool2 = ((LockPatternUtils)localObject).isSecure(UserHandle.myUserId());
    UserInfo localUserInfo = this.mUm.getProfileParent(UserHandle.myUserId());
    boolean bool1 = bool2;
    if (localUserInfo != null) {
      bool1 = bool2 | ((LockPatternUtils)localObject).isSecure(localUserInfo.id);
    }
    if (getPreferenceScreen().findPreference(this.mBlock.getKey()) != null)
    {
      setVisible(this.mSilent, checkCanBeVisible(1, paramInt));
      localObject = this.mSilent;
      if (paramInt == 2)
      {
        bool2 = true;
        ((OPRestrictedSwitchPreference)localObject).setChecked(bool2);
      }
    }
    else
    {
      localObject = this.mPriority;
      if (!checkCanBeVisible(3, paramInt)) {
        break label168;
      }
      if (!this.mDndVisualEffectsSuppressed) {
        break label163;
      }
      bool2 = false;
      label128:
      setVisibleForMore((Preference)localObject, bool2);
      localObject = this.mVisibilityOverride;
      if (!checkCanBeVisible(1, paramInt)) {
        break label173;
      }
    }
    for (;;)
    {
      setVisibleForMore((Preference)localObject, bool1);
      return;
      bool2 = false;
      break;
      label163:
      bool2 = true;
      break label128;
      label168:
      bool2 = false;
      break label128;
      label173:
      bool1 = false;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\AppNotificationSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */