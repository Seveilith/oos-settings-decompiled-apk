package com.android.settings.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import java.util.ArrayList;

public class ConfigureNotificationSettings
  extends SettingsPreferenceFragment
{
  private static final String KEY_LOCK_SCREEN_NOTIFICATIONS = "lock_screen_notifications";
  private static final String KEY_LOCK_SCREEN_PROFILE_NOTIFICATIONS = "lock_screen_notifications_profile";
  private static final String KEY_NOTIFICATION_PULSE = "notification_pulse";
  private static final String TAG = "ConfigNotiSettings";
  private Context mContext;
  private RestrictedDropDownPreference mLockscreen;
  private RestrictedDropDownPreference mLockscreenProfile;
  private int mLockscreenSelectedValue;
  private int mLockscreenSelectedValueProfile;
  private TwoStatePreference mNotificationPulse;
  private int mProfileChallengeUserId;
  private boolean mSecure;
  private boolean mSecureProfile;
  private final SettingsObserver mSettingsObserver = new SettingsObserver();
  
  private boolean getLockscreenAllowPrivateNotifications(int paramInt)
  {
    boolean bool = false;
    if (Settings.Secure.getIntForUser(getContentResolver(), "lock_screen_allow_private_notifications", 0, paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean getLockscreenNotificationsEnabled(int paramInt)
  {
    boolean bool = false;
    if (Settings.Secure.getIntForUser(getContentResolver(), "lock_screen_show_notifications", 0, paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void initLockscreenNotifications()
  {
    this.mLockscreen = ((RestrictedDropDownPreference)getPreferenceScreen().findPreference("lock_screen_notifications"));
    if (this.mLockscreen == null)
    {
      Log.i("ConfigNotiSettings", "Preference not found: lock_screen_notifications");
      return;
    }
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    localArrayList1.add(getString(2131693214));
    localArrayList2.add(Integer.toString(2131693214));
    String str1 = getString(2131693212);
    String str2 = Integer.toString(2131693212);
    localArrayList1.add(str1);
    localArrayList2.add(str2);
    setRestrictedIfNotificationFeaturesDisabled(str1, str2, 12);
    if (this.mSecure)
    {
      str1 = getString(2131693213);
      str2 = Integer.toString(2131693213);
      localArrayList1.add(str1);
      localArrayList2.add(str2);
      setRestrictedIfNotificationFeaturesDisabled(str1, str2, 4);
    }
    this.mLockscreen.setEntries((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList1.size()]));
    this.mLockscreen.setEntryValues((CharSequence[])localArrayList2.toArray(new CharSequence[localArrayList2.size()]));
    updateLockscreenNotifications();
    if (this.mLockscreen.getEntries().length > 1)
    {
      this.mLockscreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          int k = 0;
          int m = Integer.parseInt((String)paramAnonymousObject);
          if (m == ConfigureNotificationSettings.-get0(ConfigureNotificationSettings.this)) {
            return false;
          }
          int i;
          if (m != 2131693214)
          {
            i = 1;
            if (m != 2131693212) {
              break label115;
            }
            j = 1;
            label45:
            paramAnonymousPreference = ConfigureNotificationSettings.-wrap0(ConfigureNotificationSettings.this);
            if (j == 0) {
              break label121;
            }
          }
          label115:
          label121:
          for (int j = 1;; j = 0)
          {
            Settings.Secure.putInt(paramAnonymousPreference, "lock_screen_allow_private_notifications", j);
            paramAnonymousPreference = ConfigureNotificationSettings.-wrap0(ConfigureNotificationSettings.this);
            j = k;
            if (i != 0) {
              j = 1;
            }
            Settings.Secure.putInt(paramAnonymousPreference, "lock_screen_show_notifications", j);
            ConfigureNotificationSettings.-set0(ConfigureNotificationSettings.this, m);
            return true;
            i = 0;
            break;
            j = 0;
            break label45;
          }
        }
      });
      return;
    }
    this.mLockscreen.setEnabled(false);
  }
  
  private void initLockscreenNotificationsForProfile()
  {
    this.mLockscreenProfile = ((RestrictedDropDownPreference)getPreferenceScreen().findPreference("lock_screen_notifications_profile"));
    if (this.mLockscreenProfile == null)
    {
      Log.i("ConfigNotiSettings", "Preference not found: lock_screen_notifications_profile");
      return;
    }
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    localArrayList1.add(getString(2131693219));
    localArrayList2.add(Integer.toString(2131693219));
    String str1 = getString(2131693217);
    String str2 = Integer.toString(2131693217);
    localArrayList1.add(str1);
    localArrayList2.add(str2);
    setRestrictedIfNotificationFeaturesDisabled(str1, str2, 12);
    if (this.mSecureProfile)
    {
      str1 = getString(2131693218);
      str2 = Integer.toString(2131693218);
      localArrayList1.add(str1);
      localArrayList2.add(str2);
      setRestrictedIfNotificationFeaturesDisabled(str1, str2, 4);
    }
    this.mLockscreenProfile.setOnPreClickListener(new -void_initLockscreenNotificationsForProfile__LambdaImpl0());
    this.mLockscreenProfile.setEntries((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList1.size()]));
    this.mLockscreenProfile.setEntryValues((CharSequence[])localArrayList2.toArray(new CharSequence[localArrayList2.size()]));
    updateLockscreenNotificationsForProfile();
    if (this.mLockscreenProfile.getEntries().length > 1)
    {
      this.mLockscreenProfile.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          int k = 0;
          int m = Integer.parseInt((String)paramAnonymousObject);
          if (m == ConfigureNotificationSettings.-get1(ConfigureNotificationSettings.this)) {
            return false;
          }
          int i;
          if (m != 2131693219)
          {
            i = 1;
            if (m != 2131693217) {
              break label129;
            }
            j = 1;
            label45:
            paramAnonymousPreference = ConfigureNotificationSettings.-wrap0(ConfigureNotificationSettings.this);
            if (j == 0) {
              break label135;
            }
          }
          label129:
          label135:
          for (int j = 1;; j = 0)
          {
            Settings.Secure.putIntForUser(paramAnonymousPreference, "lock_screen_allow_private_notifications", j, ConfigureNotificationSettings.-get2(ConfigureNotificationSettings.this));
            paramAnonymousPreference = ConfigureNotificationSettings.-wrap0(ConfigureNotificationSettings.this);
            j = k;
            if (i != 0) {
              j = 1;
            }
            Settings.Secure.putIntForUser(paramAnonymousPreference, "lock_screen_show_notifications", j, ConfigureNotificationSettings.-get2(ConfigureNotificationSettings.this));
            ConfigureNotificationSettings.-set1(ConfigureNotificationSettings.this, m);
            return true;
            i = 0;
            break;
            j = 0;
            break label45;
          }
        }
      });
      return;
    }
    this.mLockscreenProfile.setEnabled(false);
  }
  
  private void initPulse()
  {
    this.mNotificationPulse = ((TwoStatePreference)getPreferenceScreen().findPreference("notification_pulse"));
    if (this.mNotificationPulse == null)
    {
      Log.i("ConfigNotiSettings", "Preference not found: notification_pulse");
      return;
    }
    if (!getResources().getBoolean(17956933))
    {
      getPreferenceScreen().removePreference(this.mNotificationPulse);
      return;
    }
    updatePulse();
    this.mNotificationPulse.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
        paramAnonymousPreference = ConfigureNotificationSettings.-wrap0(ConfigureNotificationSettings.this);
        if (bool) {}
        for (int i = 1;; i = 0) {
          return Settings.System.putInt(paramAnonymousPreference, "notification_light_pulse", i);
        }
      }
    });
  }
  
  private void setRestrictedIfNotificationFeaturesDisabled(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfKeyguardFeaturesDisabled(this.mContext, paramInt, UserHandle.myUserId());
    if ((localEnforcedAdmin != null) && (this.mLockscreen != null))
    {
      paramCharSequence1 = new RestrictedDropDownPreference.RestrictedItem(paramCharSequence1, paramCharSequence2, localEnforcedAdmin);
      this.mLockscreen.addRestrictedItem(paramCharSequence1);
    }
  }
  
  private void updateLockscreenNotifications()
  {
    if (this.mLockscreen == null) {
      return;
    }
    boolean bool2 = getLockscreenNotificationsEnabled(UserHandle.myUserId());
    boolean bool1;
    int i;
    if (this.mSecure)
    {
      bool1 = getLockscreenAllowPrivateNotifications(UserHandle.myUserId());
      if (bool2) {
        break label63;
      }
      i = 2131693214;
    }
    for (;;)
    {
      this.mLockscreenSelectedValue = i;
      this.mLockscreen.setValue(Integer.toString(this.mLockscreenSelectedValue));
      return;
      bool1 = true;
      break;
      label63:
      if (bool1) {
        i = 2131693212;
      } else {
        i = 2131693213;
      }
    }
  }
  
  private void updateLockscreenNotificationsForProfile()
  {
    if (this.mProfileChallengeUserId == 55536) {
      return;
    }
    if (this.mLockscreenProfile == null) {
      return;
    }
    boolean bool2 = getLockscreenNotificationsEnabled(this.mProfileChallengeUserId);
    boolean bool1;
    int i;
    if (this.mSecureProfile)
    {
      bool1 = getLockscreenAllowPrivateNotifications(this.mProfileChallengeUserId);
      if (bool2) {
        break label76;
      }
      i = 2131693219;
    }
    for (;;)
    {
      this.mLockscreenSelectedValueProfile = i;
      this.mLockscreenProfile.setValue(Integer.toString(this.mLockscreenSelectedValueProfile));
      return;
      bool1 = true;
      break;
      label76:
      if (bool1) {
        i = 2131693217;
      } else {
        i = 2131693218;
      }
    }
  }
  
  private void updatePulse()
  {
    boolean bool = true;
    if (this.mNotificationPulse == null) {
      return;
    }
    try
    {
      TwoStatePreference localTwoStatePreference = this.mNotificationPulse;
      if (Settings.System.getInt(getContentResolver(), "notification_light_pulse") == 1) {}
      for (;;)
      {
        localTwoStatePreference.setChecked(bool);
        return;
        bool = false;
      }
      return;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException)
    {
      Log.e("ConfigNotiSettings", "notification_light_pulse not found");
    }
  }
  
  protected int getMetricsCategory()
  {
    return 337;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    boolean bool2 = false;
    super.onCreate(paramBundle);
    this.mContext = getActivity();
    this.mProfileChallengeUserId = Utils.getManagedProfileId(UserManager.get(this.mContext), UserHandle.myUserId());
    paramBundle = new LockPatternUtils(getActivity());
    int i;
    if (paramBundle.isSeparateProfileChallengeEnabled(this.mProfileChallengeUserId))
    {
      i = 0;
      this.mSecure = paramBundle.isSecure(UserHandle.myUserId());
      bool1 = bool2;
      if (this.mProfileChallengeUserId != 55536)
      {
        if (paramBundle.isSecure(this.mProfileChallengeUserId)) {
          break label131;
        }
        bool1 = bool2;
        if (i == 0) {}
      }
    }
    label131:
    for (boolean bool1 = this.mSecure;; bool1 = true)
    {
      this.mSecureProfile = bool1;
      addPreferencesFromResource(2131230746);
      initPulse();
      initLockscreenNotifications();
      return;
      i = 1;
      break;
    }
  }
  
  public void onPause()
  {
    super.onPause();
    this.mSettingsObserver.register(false);
  }
  
  public void onResume()
  {
    super.onResume();
    this.mSettingsObserver.register(true);
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    private final Uri LOCK_SCREEN_PRIVATE_URI = Settings.Secure.getUriFor("lock_screen_allow_private_notifications");
    private final Uri LOCK_SCREEN_SHOW_URI = Settings.Secure.getUriFor("lock_screen_show_notifications");
    private final Uri NOTIFICATION_LIGHT_PULSE_URI = Settings.System.getUriFor("notification_light_pulse");
    
    public SettingsObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      super.onChange(paramBoolean, paramUri);
      if (this.NOTIFICATION_LIGHT_PULSE_URI.equals(paramUri)) {
        ConfigureNotificationSettings.-wrap2(ConfigureNotificationSettings.this);
      }
      if ((this.LOCK_SCREEN_PRIVATE_URI.equals(paramUri)) || (this.LOCK_SCREEN_SHOW_URI.equals(paramUri))) {
        ConfigureNotificationSettings.-wrap1(ConfigureNotificationSettings.this);
      }
    }
    
    public void register(boolean paramBoolean)
    {
      ContentResolver localContentResolver = ConfigureNotificationSettings.-wrap0(ConfigureNotificationSettings.this);
      if (paramBoolean)
      {
        localContentResolver.registerContentObserver(this.NOTIFICATION_LIGHT_PULSE_URI, false, this);
        localContentResolver.registerContentObserver(this.LOCK_SCREEN_PRIVATE_URI, false, this);
        localContentResolver.registerContentObserver(this.LOCK_SCREEN_SHOW_URI, false, this);
        return;
      }
      localContentResolver.unregisterContentObserver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ConfigureNotificationSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */