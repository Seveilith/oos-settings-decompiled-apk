package com.android.settings.accounts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;

public class ManagedProfileSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener
{
  private static final String KEY_CONTACT = "contacts_search";
  private static final String KEY_WORK_MODE = "work_mode";
  private static final String TAG = "ManagedProfileSettings";
  private OPRestrictedSwitchPreference mContactPrefrence;
  private Context mContext;
  private ManagedProfileBroadcastReceiver mManagedProfileBroadcastReceiver;
  private UserHandle mManagedUser;
  private UserManager mUserManager;
  private SwitchPreference mWorkModePreference;
  
  private UserHandle getManagedUserFromArgument()
  {
    Object localObject = getArguments();
    if (localObject != null)
    {
      localObject = (UserHandle)((Bundle)localObject).getParcelable("android.intent.extra.USER");
      if ((localObject != null) && (this.mUserManager.isManagedProfile(((UserHandle)localObject).getIdentifier()))) {
        return (UserHandle)localObject;
      }
    }
    return null;
  }
  
  private void loadDataAndPopulateUi()
  {
    boolean bool2 = true;
    Object localObject;
    if (this.mWorkModePreference != null)
    {
      localObject = this.mWorkModePreference;
      if (this.mUserManager.isQuietModeEnabled(this.mManagedUser))
      {
        bool1 = false;
        ((SwitchPreference)localObject).setChecked(bool1);
      }
    }
    else if (this.mContactPrefrence != null)
    {
      int i = Settings.Secure.getIntForUser(getContentResolver(), "managed_profile_contact_remote_search", 0, this.mManagedUser.getIdentifier());
      localObject = this.mContactPrefrence;
      if (i == 0) {
        break label111;
      }
    }
    label111:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      ((OPRestrictedSwitchPreference)localObject).setChecked(bool1);
      localObject = RestrictedLockUtils.checkIfRemoteContactSearchDisallowed(this.mContext, this.mManagedUser.getIdentifier());
      this.mContactPrefrence.setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin)localObject);
      return;
      bool1 = true;
      break;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 401;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230780);
    this.mWorkModePreference = ((SwitchPreference)findPreference("work_mode"));
    this.mWorkModePreference.setOnPreferenceChangeListener(this);
    this.mContactPrefrence = ((OPRestrictedSwitchPreference)findPreference("contacts_search"));
    this.mContactPrefrence.setOnPreferenceChangeListener(this);
    this.mContext = getActivity().getApplicationContext();
    this.mUserManager = ((UserManager)getSystemService("user"));
    this.mManagedUser = getManagedUserFromArgument();
    if (this.mManagedUser == null) {
      getActivity().finish();
    }
    this.mManagedProfileBroadcastReceiver = new ManagedProfileBroadcastReceiver(null);
    this.mManagedProfileBroadcastReceiver.register(getActivity());
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mManagedProfileBroadcastReceiver.unregister(getActivity());
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (paramPreference == this.mWorkModePreference)
    {
      if (((Boolean)paramObject).booleanValue())
      {
        this.mUserManager.trySetQuietModeDisabled(this.mManagedUser.getIdentifier(), null);
        return true;
      }
      this.mUserManager.setQuietModeEnabled(this.mManagedUser.getIdentifier(), true);
      return true;
    }
    if (paramPreference == this.mContactPrefrence)
    {
      if (((Boolean)paramObject).booleanValue()) {}
      for (int i = 1;; i = 0)
      {
        Settings.Secure.putIntForUser(getContentResolver(), "managed_profile_contact_remote_search", i, this.mManagedUser.getIdentifier());
        return true;
      }
    }
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    loadDataAndPopulateUi();
  }
  
  private class ManagedProfileBroadcastReceiver
    extends BroadcastReceiver
  {
    private ManagedProfileBroadcastReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      Log.v("ManagedProfileSettings", "Received broadcast: " + paramContext);
      if (paramContext.equals("android.intent.action.MANAGED_PROFILE_REMOVED"))
      {
        if (paramIntent.getIntExtra("android.intent.extra.user_handle", 55536) == ManagedProfileSettings.-get0(ManagedProfileSettings.this).getIdentifier()) {
          ManagedProfileSettings.this.getActivity().finish();
        }
        return;
      }
      if ((paramContext.equals("android.intent.action.MANAGED_PROFILE_AVAILABLE")) || (paramContext.equals("android.intent.action.MANAGED_PROFILE_UNAVAILABLE")))
      {
        if (paramIntent.getIntExtra("android.intent.extra.user_handle", 55536) == ManagedProfileSettings.-get0(ManagedProfileSettings.this).getIdentifier())
        {
          paramContext = ManagedProfileSettings.-get2(ManagedProfileSettings.this);
          if (!ManagedProfileSettings.-get1(ManagedProfileSettings.this).isQuietModeEnabled(ManagedProfileSettings.-get0(ManagedProfileSettings.this))) {
            break label148;
          }
        }
        label148:
        for (boolean bool = false;; bool = true)
        {
          paramContext.setChecked(bool);
          return;
        }
      }
      Log.w("ManagedProfileSettings", "Cannot handle received broadcast: " + paramIntent.getAction());
    }
    
    public void register(Context paramContext)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
      localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
      localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
      paramContext.registerReceiver(this, localIntentFilter);
    }
    
    public void unregister(Context paramContext)
    {
      paramContext.unregisterReceiver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accounts\ManagedProfileSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */