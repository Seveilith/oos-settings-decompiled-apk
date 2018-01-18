package com.oneplus.settings.notification;

import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Secure;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.notification.NotificationAccessSettings;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import java.util.Arrays;
import java.util.List;

public class OPNotificationAdvancedSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final String KEY_LOCK_SCREEN_NOTIFICATIONS = "lock_screen_notifications";
  private static final String KEY_NOTIFICATION_ACCESS = "manage_notification_access";
  public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230801;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final String TAG = "OPNotificationAdvancedSettings";
  private DropDownPreference mLockscreen;
  private int mLockscreenSelectedValue;
  private Preference mNotificationAccess;
  private boolean mSecure;
  private final SettingsObserver mSettingsObserver = new SettingsObserver();
  
  private boolean getLockscreenAllowPrivateNotifications()
  {
    boolean bool = false;
    if (Settings.Secure.getInt(getContentResolver(), "lock_screen_allow_private_notifications", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean getLockscreenNotificationsEnabled()
  {
    boolean bool = false;
    if (Settings.Secure.getInt(getContentResolver(), "lock_screen_show_notifications", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void initLockscreenNotifications()
  {
    this.mLockscreen = ((DropDownPreference)findPreference("lock_screen_notifications"));
    if (this.mLockscreen == null)
    {
      Log.i("OPNotificationAdvancedSettings", "Preference not found: lock_screen_notifications");
      return;
    }
    boolean bool1 = isSecureNotificationsDisabled();
    boolean bool2 = isUnredactedNotificationsDisabled();
    if ((bool1) || (bool2)) {
      if ((this.mSecure) && (!bool1)) {
        break label163;
      }
    }
    for (;;)
    {
      this.mLockscreen.setEntryValues(new CharSequence[] { getResources().getString(2131693214), getResources().getString(2131693214) });
      updateLockscreenNotifications();
      if (this.mLockscreen.getEntryValues().length <= 1) {
        break label201;
      }
      this.mLockscreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          int k = 0;
          int m = ((Integer)paramAnonymousObject).intValue();
          if (m == OPNotificationAdvancedSettings.-get0(OPNotificationAdvancedSettings.this)) {
            return true;
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
            paramAnonymousPreference = OPNotificationAdvancedSettings.-wrap0(OPNotificationAdvancedSettings.this);
            if (j == 0) {
              break label121;
            }
          }
          label115:
          label121:
          for (int j = 1;; j = 0)
          {
            Settings.Secure.putInt(paramAnonymousPreference, "lock_screen_allow_private_notifications", j);
            paramAnonymousPreference = OPNotificationAdvancedSettings.-wrap0(OPNotificationAdvancedSettings.this);
            j = k;
            if (i != 0) {
              j = 1;
            }
            Settings.Secure.putInt(paramAnonymousPreference, "lock_screen_show_notifications", j);
            OPNotificationAdvancedSettings.-set0(OPNotificationAdvancedSettings.this, m);
            return true;
            i = 0;
            break;
            j = 0;
            break label45;
          }
        }
      });
      return;
      this.mLockscreen.setEntryValues(new CharSequence[] { getResources().getString(2131693212), getResources().getString(2131693212) });
      break;
      label163:
      this.mLockscreen.setEntryValues(new CharSequence[] { getResources().getString(2131693213), getResources().getString(2131693213) });
    }
    label201:
    this.mLockscreen.setEnabled(false);
  }
  
  private boolean isSecureNotificationsDisabled()
  {
    boolean bool2 = false;
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)getSystemService("device_policy");
    boolean bool1 = bool2;
    if (localDevicePolicyManager != null)
    {
      bool1 = bool2;
      if ((localDevicePolicyManager.getKeyguardDisabledFeatures(null) & 0x4) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isUnredactedNotificationsDisabled()
  {
    boolean bool2 = false;
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)getSystemService("device_policy");
    boolean bool1 = bool2;
    if (localDevicePolicyManager != null)
    {
      bool1 = bool2;
      if ((localDevicePolicyManager.getKeyguardDisabledFeatures(null) & 0x8) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private void refreshNotificationListeners()
  {
    int i;
    if (this.mNotificationAccess != null)
    {
      i = NotificationAccessSettings.getEnabledListenersCount(getActivity());
      if (i == 0) {
        this.mNotificationAccess.setSummary(getResources().getString(2131693236));
      }
    }
    else
    {
      return;
    }
    this.mNotificationAccess.setSummary(String.format(getResources().getQuantityString(2131951640, i, new Object[] { Integer.valueOf(i) }), new Object[0]));
  }
  
  private void updateLockscreenNotifications()
  {
    if (this.mLockscreen == null) {
      return;
    }
    boolean bool2 = getLockscreenNotificationsEnabled();
    boolean bool1;
    int i;
    if (this.mSecure)
    {
      bool1 = getLockscreenAllowPrivateNotifications();
      if (bool2) {
        break label43;
      }
      i = 2131693214;
    }
    for (;;)
    {
      this.mLockscreenSelectedValue = i;
      return;
      bool1 = true;
      break;
      label43:
      if (bool1) {
        i = 2131693212;
      } else {
        i = 2131693213;
      }
    }
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230801);
    this.mSecure = new LockPatternUtils(getActivity()).isSecure(UserHandle.myUserId());
    initLockscreenNotifications();
    this.mNotificationAccess = findPreference("manage_notification_access");
  }
  
  public void onPause()
  {
    this.mSettingsObserver.register(false);
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    refreshNotificationListeners();
    this.mSettingsObserver.register(true);
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    private final Uri LOCK_SCREEN_PRIVATE_URI = Settings.Secure.getUriFor("lock_screen_allow_private_notifications");
    private final Uri LOCK_SCREEN_SHOW_URI = Settings.Secure.getUriFor("lock_screen_show_notifications");
    
    public SettingsObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      super.onChange(paramBoolean, paramUri);
      if ((this.LOCK_SCREEN_PRIVATE_URI.equals(paramUri)) || (this.LOCK_SCREEN_SHOW_URI.equals(paramUri))) {
        OPNotificationAdvancedSettings.-wrap1(OPNotificationAdvancedSettings.this);
      }
    }
    
    public void register(boolean paramBoolean)
    {
      ContentResolver localContentResolver = OPNotificationAdvancedSettings.-wrap0(OPNotificationAdvancedSettings.this);
      if (paramBoolean)
      {
        localContentResolver.registerContentObserver(this.LOCK_SCREEN_PRIVATE_URI, false, this);
        localContentResolver.registerContentObserver(this.LOCK_SCREEN_SHOW_URI, false, this);
        return;
      }
      localContentResolver.unregisterContentObserver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\notification\OPNotificationAdvancedSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */