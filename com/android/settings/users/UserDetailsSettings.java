package com.android.settings.users;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.RestrictedLockUtils;
import java.util.Iterator;

public class UserDetailsSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener
{
  private static final int DIALOG_CONFIRM_ENABLE_CALLING = 2;
  private static final int DIALOG_CONFIRM_ENABLE_CALLING_AND_SMS = 3;
  private static final int DIALOG_CONFIRM_REMOVE = 1;
  static final String EXTRA_USER_GUEST = "guest_user";
  static final String EXTRA_USER_ID = "user_id";
  private static final String KEY_ENABLE_TELEPHONY = "enable_calling";
  private static final String KEY_REMOVE_USER = "remove_user";
  private static final String TAG = UserDetailsSettings.class.getSimpleName();
  private Bundle mDefaultGuestRestrictions;
  private boolean mGuestUser;
  private SwitchPreference mPhonePref;
  private Preference mRemoveUserPref;
  private UserInfo mUserInfo;
  private UserManager mUserManager;
  
  void enableCallsAndSms(boolean paramBoolean)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    this.mPhonePref.setChecked(paramBoolean);
    if (this.mGuestUser)
    {
      localObject1 = this.mDefaultGuestRestrictions;
      if (paramBoolean) {}
      for (paramBoolean = bool1;; paramBoolean = true)
      {
        ((Bundle)localObject1).putBoolean("no_outgoing_calls", paramBoolean);
        this.mDefaultGuestRestrictions.putBoolean("no_sms", true);
        this.mUserManager.setDefaultGuestRestrictions(this.mDefaultGuestRestrictions);
        localObject1 = this.mUserManager.getUsers(true).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (UserInfo)((Iterator)localObject1).next();
          if (((UserInfo)localObject2).isGuest())
          {
            localObject2 = UserHandle.of(((UserInfo)localObject2).id);
            Iterator localIterator = this.mDefaultGuestRestrictions.keySet().iterator();
            while (localIterator.hasNext())
            {
              String str = (String)localIterator.next();
              this.mUserManager.setUserRestriction(str, this.mDefaultGuestRestrictions.getBoolean(str), (UserHandle)localObject2);
            }
          }
        }
      }
    }
    Object localObject1 = UserHandle.of(this.mUserInfo.id);
    Object localObject2 = this.mUserManager;
    if (paramBoolean)
    {
      bool1 = false;
      ((UserManager)localObject2).setUserRestriction("no_outgoing_calls", bool1, (UserHandle)localObject1);
      localObject2 = this.mUserManager;
      if (!paramBoolean) {
        break label241;
      }
    }
    label241:
    for (paramBoolean = bool2;; paramBoolean = true)
    {
      ((UserManager)localObject2).setUserRestriction("no_sms", paramBoolean, (UserHandle)localObject1);
      return;
      bool1 = true;
      break;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 98;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    boolean bool = false;
    super.onCreate(paramBundle);
    paramBundle = getActivity();
    this.mUserManager = ((UserManager)paramBundle.getSystemService("user"));
    addPreferencesFromResource(2131230875);
    this.mPhonePref = ((SwitchPreference)findPreference("enable_calling"));
    this.mRemoveUserPref = findPreference("remove_user");
    this.mGuestUser = getArguments().getBoolean("guest_user", false);
    if (!this.mGuestUser)
    {
      int i = getArguments().getInt("user_id", -1);
      if (i == -1) {
        throw new RuntimeException("Arguments to this fragment must contain the user id");
      }
      this.mUserInfo = this.mUserManager.getUserInfo(i);
      localSwitchPreference = this.mPhonePref;
      if (this.mUserManager.hasUserRestriction("no_outgoing_calls", new UserHandle(i))) {}
      for (bool = false;; bool = true)
      {
        localSwitchPreference.setChecked(bool);
        this.mRemoveUserPref.setOnPreferenceClickListener(this);
        if (RestrictedLockUtils.hasBaseUserRestriction(paramBundle, "no_remove_user", UserHandle.myUserId())) {
          removePreference("remove_user");
        }
        this.mPhonePref.setOnPreferenceChangeListener(this);
        return;
      }
    }
    removePreference("remove_user");
    this.mPhonePref.setTitle(2131692960);
    this.mDefaultGuestRestrictions = this.mUserManager.getDefaultGuestRestrictions();
    SwitchPreference localSwitchPreference = this.mPhonePref;
    if (this.mDefaultGuestRestrictions.getBoolean("no_outgoing_calls")) {}
    for (;;)
    {
      localSwitchPreference.setChecked(bool);
      break;
      bool = true;
    }
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    if (getActivity() == null) {
      return null;
    }
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unsupported dialogId " + paramInt);
    case 1: 
      UserDialogs.createRemoveDialog(getActivity(), this.mUserInfo.id, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          UserDetailsSettings.this.removeUser();
        }
      });
    case 2: 
      UserDialogs.createEnablePhoneCallsDialog(getActivity(), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          UserDetailsSettings.this.enableCallsAndSms(true);
        }
      });
    }
    UserDialogs.createEnablePhoneCallsAndSmsDialog(getActivity(), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        UserDetailsSettings.this.enableCallsAndSms(true);
      }
    });
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (Boolean.TRUE.equals(paramObject))
    {
      if (this.mGuestUser) {}
      for (int i = 2;; i = 3)
      {
        showDialog(i);
        return false;
      }
    }
    enableCallsAndSms(false);
    return true;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mRemoveUserPref)
    {
      if (!this.mUserManager.isAdminUser()) {
        throw new RuntimeException("Only admins can remove a user");
      }
      showDialog(1);
      return true;
    }
    return false;
  }
  
  void removeUser()
  {
    this.mUserManager.removeUser(this.mUserInfo.id);
    finishFragment();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\users\UserDetailsSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */