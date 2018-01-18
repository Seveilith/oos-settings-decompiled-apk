package com.android.settings.accounts;

import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.ContextThemeWrapper;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener;
import java.util.ArrayList;
import java.util.Date;

abstract class AccountPreferenceBase
  extends SettingsPreferenceFragment
  implements AuthenticatorHelper.OnAccountsUpdateListener
{
  public static final String ACCOUNT_TYPES_FILTER_KEY = "account_types";
  public static final String AUTHORITIES_FILTER_KEY = "authorities";
  protected static final String TAG = "AccountSettings";
  protected AuthenticatorHelper mAuthenticatorHelper;
  private java.text.DateFormat mDateFormat;
  private final Handler mHandler = new Handler();
  private Object mStatusChangeListenerHandle;
  private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver()
  {
    public void onStatusChanged(int paramAnonymousInt)
    {
      AccountPreferenceBase.-get0(AccountPreferenceBase.this).post(new Runnable()
      {
        public void run()
        {
          AccountPreferenceBase.this.onSyncStateUpdated();
        }
      });
    }
  };
  private java.text.DateFormat mTimeFormat;
  private UserManager mUm;
  protected UserHandle mUserHandle;
  
  public PreferenceScreen addPreferencesForType(String paramString, PreferenceScreen paramPreferenceScreen)
  {
    Object localObject5 = null;
    Object localObject4 = localObject5;
    Object localObject2;
    Object localObject3;
    Object localObject1;
    if (this.mAuthenticatorHelper.containsAccountType(paramString))
    {
      localObject2 = null;
      localObject3 = null;
      localObject1 = null;
    }
    try
    {
      AuthenticatorDescription localAuthenticatorDescription = this.mAuthenticatorHelper.getAccountTypeDescription(paramString);
      localObject4 = localObject5;
      if (localAuthenticatorDescription != null)
      {
        localObject4 = localObject5;
        localObject1 = localAuthenticatorDescription;
        localObject2 = localAuthenticatorDescription;
        localObject3 = localAuthenticatorDescription;
        if (localAuthenticatorDescription.accountPreferencesId != 0)
        {
          localObject4 = localObject5;
          localObject1 = localAuthenticatorDescription;
          localObject2 = localAuthenticatorDescription;
          localObject3 = localAuthenticatorDescription;
          if (Utils.showAccount(getActivity(), paramString))
          {
            localObject1 = localAuthenticatorDescription;
            localObject2 = localAuthenticatorDescription;
            localObject3 = localAuthenticatorDescription;
            localObject4 = getActivity().createPackageContextAsUser(localAuthenticatorDescription.packageName, 0, this.mUserHandle);
            localObject1 = localAuthenticatorDescription;
            localObject2 = localAuthenticatorDescription;
            localObject3 = localAuthenticatorDescription;
            paramString = getResources().newTheme();
            localObject1 = localAuthenticatorDescription;
            localObject2 = localAuthenticatorDescription;
            localObject3 = localAuthenticatorDescription;
            paramString.applyStyle(2131821449, true);
            localObject1 = localAuthenticatorDescription;
            localObject2 = localAuthenticatorDescription;
            localObject3 = localAuthenticatorDescription;
            localObject4 = new ContextThemeWrapper((Context)localObject4, 0);
            localObject1 = localAuthenticatorDescription;
            localObject2 = localAuthenticatorDescription;
            localObject3 = localAuthenticatorDescription;
            ((Context)localObject4).getTheme().setTo(paramString);
            localObject1 = localAuthenticatorDescription;
            localObject2 = localAuthenticatorDescription;
            localObject3 = localAuthenticatorDescription;
            localObject4 = getPreferenceManager().inflateFromResource((Context)localObject4, localAuthenticatorDescription.accountPreferencesId, paramPreferenceScreen);
          }
        }
      }
      return (PreferenceScreen)localObject4;
    }
    catch (Throwable paramString)
    {
      Log.w("AccountSettings", "Couldn't load preferences.xml file from " + ((AuthenticatorDescription)localObject1).packageName);
      paramString.printStackTrace();
      return null;
    }
    catch (Resources.NotFoundException paramString)
    {
      Log.w("AccountSettings", "Couldn't load preferences.xml file from " + ((AuthenticatorDescription)localObject2).packageName);
      return null;
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      Log.w("AccountSettings", "Couldn't load preferences.xml file from " + ((AuthenticatorDescription)localObject3).packageName);
    }
    return null;
  }
  
  protected String formatSyncDate(Date paramDate)
  {
    return this.mDateFormat.format(paramDate) + " " + this.mTimeFormat.format(paramDate);
  }
  
  public ArrayList<String> getAuthoritiesForAccountType(String paramString)
  {
    return this.mAuthenticatorHelper.getAuthoritiesForAccountType(paramString);
  }
  
  protected Drawable getDrawableForType(String paramString)
  {
    return this.mAuthenticatorHelper.getDrawableForType(getActivity(), paramString);
  }
  
  protected CharSequence getLabelForType(String paramString)
  {
    return this.mAuthenticatorHelper.getLabelForType(getActivity(), paramString);
  }
  
  public void onAccountsUpdate(UserHandle paramUserHandle) {}
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    paramBundle = getActivity();
    this.mDateFormat = android.text.format.DateFormat.getDateFormat(paramBundle);
    this.mTimeFormat = android.text.format.DateFormat.getTimeFormat(paramBundle);
  }
  
  protected void onAuthDescriptionsUpdated() {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUm = ((UserManager)getSystemService("user"));
    paramBundle = getActivity();
    this.mUserHandle = Utils.getSecureTargetUser(paramBundle.getActivityToken(), this.mUm, getArguments(), paramBundle.getIntent().getExtras());
    this.mAuthenticatorHelper = new AuthenticatorHelper(paramBundle, this.mUserHandle, this);
  }
  
  public void onPause()
  {
    super.onPause();
    ContentResolver.removeStatusChangeListener(this.mStatusChangeListenerHandle);
  }
  
  public void onResume()
  {
    super.onResume();
    this.mStatusChangeListenerHandle = ContentResolver.addStatusChangeListener(13, this.mSyncStatusObserver);
    onSyncStateUpdated();
  }
  
  protected void onSyncStateUpdated() {}
  
  public void updateAuthDescriptions()
  {
    this.mAuthenticatorHelper.updateAuthDescriptions(getActivity());
    onAuthDescriptionsUpdated();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accounts\AccountPreferenceBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */