package com.android.settings.accounts;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.CharSequences;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.google.android.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class ChooseAccountActivity
  extends SettingsPreferenceFragment
{
  private static final String TAG = "ChooseAccountActivity";
  private HashMap<String, ArrayList<String>> mAccountTypeToAuthorities = null;
  public HashSet<String> mAccountTypesFilter;
  private PreferenceGroup mAddAccountGroup;
  private AuthenticatorDescription[] mAuthDescs;
  private String[] mAuthorities;
  private final ArrayList<ProviderEntry> mProviderList = new ArrayList();
  private Map<String, AuthenticatorDescription> mTypeToAuthDescription = new HashMap();
  private UserManager mUm;
  private UserHandle mUserHandle;
  
  private void finishAccountActivity()
  {
    Activity localActivity = getActivity();
    if (localActivity != null) {
      localActivity.finish();
    }
  }
  
  private void finishWithAccountType(String paramString)
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("selected_account", paramString);
    localIntent.putExtra("android.intent.extra.USER", this.mUserHandle);
    setResult(-1, localIntent);
    finish();
  }
  
  private void onAuthDescriptionsUpdated()
  {
    int j = 0;
    Object localObject2;
    Object localObject3;
    int i;
    if (j < this.mAuthDescs.length)
    {
      localObject1 = this.mAuthDescs[j].type;
      if (!Utils.showAccount(getPreferenceScreen().getContext(), (String)localObject1)) {}
      for (;;)
      {
        j += 1;
        break;
        localObject2 = getLabelForType((String)localObject1);
        localObject3 = getAuthoritiesForAccountType((String)localObject1);
        int k = 1;
        i = k;
        if (this.mAuthorities != null)
        {
          i = k;
          if (this.mAuthorities.length > 0)
          {
            i = k;
            if (localObject3 != null)
            {
              int m = 0;
              k = 0;
              label93:
              i = m;
              if (k < this.mAuthorities.length)
              {
                if (!((ArrayList)localObject3).contains(this.mAuthorities[k])) {
                  break label186;
                }
                i = 1;
              }
            }
          }
        }
        k = i;
        if (i != 0)
        {
          k = i;
          if (this.mAccountTypesFilter != null) {
            if (!this.mAccountTypesFilter.contains(localObject1)) {
              break label193;
            }
          }
        }
        label186:
        label193:
        for (k = i;; k = 0)
        {
          if (k == 0) {
            break label198;
          }
          if ("com.oneplus.account".equals(localObject1)) {
            break;
          }
          this.mProviderList.add(new ProviderEntry((CharSequence)localObject2, (String)localObject1));
          break;
          k += 1;
          break label93;
        }
        label198:
        if (Log.isLoggable("ChooseAccountActivity", 2)) {
          Log.v("ChooseAccountActivity", "Skipped pref " + (CharSequence)localObject2 + ": has no authority we need");
        }
      }
    }
    Object localObject1 = getPreferenceScreen().getContext();
    if (this.mProviderList.size() == 1)
    {
      localObject2 = RestrictedLockUtils.checkIfAccountManagementDisabled((Context)localObject1, ProviderEntry.-get1((ProviderEntry)this.mProviderList.get(0)), this.mUserHandle.getIdentifier());
      if (localObject2 != null)
      {
        setResult(0, RestrictedLockUtils.getShowAdminSupportDetailsIntent((Context)localObject1, (RestrictedLockUtils.EnforcedAdmin)localObject2));
        finishAccountActivity();
      }
    }
    for (;;)
    {
      return;
      finishWithAccountType(ProviderEntry.-get1((ProviderEntry)this.mProviderList.get(0)));
      return;
      if (this.mProviderList.size() <= 0) {
        break;
      }
      Collections.sort(this.mProviderList);
      this.mAddAccountGroup.removeAll();
      localObject1 = this.mProviderList.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (ProviderEntry)((Iterator)localObject1).next();
        localObject3 = getDrawableForType(ProviderEntry.-get1((ProviderEntry)localObject2));
        localObject3 = new ProviderPreference(getPreferenceScreen().getContext(), ProviderEntry.-get1((ProviderEntry)localObject2), (Drawable)localObject3, ProviderEntry.-get0((ProviderEntry)localObject2));
        String str = this.mAddAccountGroup.getKey();
        if ((str == null) || (!str.equals("Exchange")))
        {
          ((ProviderPreference)localObject3).checkAccountManagementAndSetDisabled(this.mUserHandle.getIdentifier());
          if ((!TextUtils.isEmpty(ProviderEntry.-get0((ProviderEntry)localObject2))) && (ProviderEntry.-get0((ProviderEntry)localObject2) != null)) {
            this.mAddAccountGroup.addPreference((Preference)localObject3);
          }
        }
      }
    }
    if (Log.isLoggable("ChooseAccountActivity", 2))
    {
      localObject1 = new StringBuilder();
      localObject2 = this.mAuthorities;
      i = 0;
      j = localObject2.length;
      while (i < j)
      {
        ((StringBuilder)localObject1).append(localObject2[i]);
        ((StringBuilder)localObject1).append(' ');
        i += 1;
      }
      Log.v("ChooseAccountActivity", "No providers found for authorities: " + localObject1);
    }
    setResult(0);
    finishAccountActivity();
  }
  
  private void updateAuthDescriptions()
  {
    this.mAuthDescs = AccountManager.get(getContext()).getAuthenticatorTypesAsUser(this.mUserHandle.getIdentifier());
    int i = 0;
    while (i < this.mAuthDescs.length)
    {
      this.mTypeToAuthDescription.put(this.mAuthDescs[i].type, this.mAuthDescs[i]);
      i += 1;
    }
    onAuthDescriptionsUpdated();
  }
  
  public ArrayList<String> getAuthoritiesForAccountType(String paramString)
  {
    if (this.mAccountTypeToAuthorities == null)
    {
      this.mAccountTypeToAuthorities = Maps.newHashMap();
      SyncAdapterType[] arrayOfSyncAdapterType = ContentResolver.getSyncAdapterTypesAsUser(this.mUserHandle.getIdentifier());
      int i = 0;
      int j = arrayOfSyncAdapterType.length;
      while (i < j)
      {
        SyncAdapterType localSyncAdapterType = arrayOfSyncAdapterType[i];
        ArrayList localArrayList2 = (ArrayList)this.mAccountTypeToAuthorities.get(localSyncAdapterType.accountType);
        ArrayList localArrayList1 = localArrayList2;
        if (localArrayList2 == null)
        {
          localArrayList1 = new ArrayList();
          this.mAccountTypeToAuthorities.put(localSyncAdapterType.accountType, localArrayList1);
        }
        if (Log.isLoggable("ChooseAccountActivity", 2)) {
          Log.d("ChooseAccountActivity", "added authority " + localSyncAdapterType.authority + " to accountType " + localSyncAdapterType.accountType);
        }
        localArrayList1.add(localSyncAdapterType.authority);
        i += 1;
      }
    }
    return (ArrayList)this.mAccountTypeToAuthorities.get(paramString);
  }
  
  protected Drawable getDrawableForType(String paramString)
  {
    Object localObject4 = null;
    Object localObject1 = localObject4;
    if (this.mTypeToAuthDescription.containsKey(paramString)) {}
    try
    {
      localObject1 = (AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString);
      Context localContext = getActivity().createPackageContextAsUser(((AuthenticatorDescription)localObject1).packageName, 0, this.mUserHandle);
      localObject1 = getPackageManager().getUserBadgedIcon(localContext.getDrawable(((AuthenticatorDescription)localObject1).iconId), this.mUserHandle);
      if (localObject1 != null) {
        return (Drawable)localObject1;
      }
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      for (;;)
      {
        Log.w("ChooseAccountActivity", "No icon resource for account type " + paramString);
        Object localObject2 = localObject4;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        Log.w("ChooseAccountActivity", "No icon name for account type " + paramString);
        Object localObject3 = localObject4;
      }
    }
    return getPackageManager().getDefaultActivityIcon();
  }
  
  protected CharSequence getLabelForType(String paramString)
  {
    Object localObject = null;
    if (this.mTypeToAuthDescription.containsKey(paramString)) {}
    try
    {
      localObject = (AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString);
      localObject = getActivity().createPackageContextAsUser(((AuthenticatorDescription)localObject).packageName, 0, this.mUserHandle).getResources().getText(((AuthenticatorDescription)localObject).labelId);
      return (CharSequence)localObject;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      Log.w("ChooseAccountActivity", "No label resource for account type " + paramString);
      return null;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("ChooseAccountActivity", "No label name for account type " + paramString);
    }
    return null;
  }
  
  protected int getMetricsCategory()
  {
    return 10;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230727);
    this.mAuthorities = getIntent().getStringArrayExtra("authorities");
    paramBundle = getIntent().getStringArrayExtra("account_types");
    if (paramBundle != null)
    {
      this.mAccountTypesFilter = new HashSet();
      int i = 0;
      int j = paramBundle.length;
      while (i < j)
      {
        Object localObject = paramBundle[i];
        this.mAccountTypesFilter.add(localObject);
        i += 1;
      }
    }
    this.mAddAccountGroup = getPreferenceScreen();
    this.mUm = UserManager.get(getContext());
    this.mUserHandle = Utils.getSecureTargetUser(getActivity().getActivityToken(), this.mUm, null, getIntent().getExtras());
    updateAuthDescriptions();
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if ((paramPreference instanceof ProviderPreference))
    {
      paramPreference = (ProviderPreference)paramPreference;
      if (Log.isLoggable("ChooseAccountActivity", 2)) {
        Log.v("ChooseAccountActivity", "Attempting to add account of type " + paramPreference.getAccountType());
      }
      finishWithAccountType(paramPreference.getAccountType());
    }
    return true;
  }
  
  private static class ProviderEntry
    implements Comparable<ProviderEntry>
  {
    private final CharSequence name;
    private final String type;
    
    ProviderEntry(CharSequence paramCharSequence, String paramString)
    {
      this.name = paramCharSequence;
      this.type = paramString;
    }
    
    public int compareTo(ProviderEntry paramProviderEntry)
    {
      if (this.name == null) {
        return -1;
      }
      if (paramProviderEntry.name == null) {
        return 1;
      }
      return CharSequences.compareToIgnoreCase(this.name, paramProviderEntry.name);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accounts\ChooseAccountActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */