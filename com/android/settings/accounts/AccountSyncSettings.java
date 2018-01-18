package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.SyncStatusInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.google.android.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class AccountSyncSettings
  extends AccountPreferenceBase
{
  public static final String ACCOUNT_KEY = "account";
  private static final int CANT_DO_ONETIME_SYNC_DIALOG = 102;
  private static final int FAILED_REMOVAL_DIALOG = 101;
  private static final int MENU_REMOVE_ACCOUNT_ID = 3;
  private static final int MENU_SYNC_CANCEL_ID = 2;
  private static final int MENU_SYNC_NOW_ID = 1;
  private static final int REALLY_REMOVE_DIALOG = 100;
  private Account mAccount;
  private TextView mErrorInfoView;
  private ArrayList<SyncAdapterType> mInvisibleAdapters = Lists.newArrayList();
  private ImageView mProviderIcon;
  private TextView mProviderId;
  private ArrayList<SyncStateSwitchPreference> mSwitches = new ArrayList();
  private TextView mUserId;
  
  private boolean accountExists(Account paramAccount)
  {
    if (paramAccount == null) {
      return false;
    }
    Account[] arrayOfAccount = AccountManager.get(getActivity()).getAccountsByTypeAsUser(paramAccount.type, this.mUserHandle);
    int j = arrayOfAccount.length;
    int i = 0;
    while (i < j)
    {
      if (arrayOfAccount[i].equals(paramAccount)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void addSyncStateSwitch(Account paramAccount, String paramString)
  {
    Object localObject = (SyncStateSwitchPreference)getCachedPreference(paramString);
    if (localObject == null)
    {
      paramAccount = new SyncStateSwitchPreference(getPrefContext(), paramAccount, paramString);
      getPreferenceScreen().addPreference(paramAccount);
    }
    for (;;)
    {
      paramAccount.setPersistent(false);
      localObject = getPackageManager().resolveContentProviderAsUser(paramString, 0, this.mUserHandle.getIdentifier());
      if (localObject != null) {
        break;
      }
      return;
      ((SyncStateSwitchPreference)localObject).setup(paramAccount, paramString);
      paramAccount = (Account)localObject;
    }
    localObject = ((ProviderInfo)localObject).loadLabel(getPackageManager());
    if (TextUtils.isEmpty((CharSequence)localObject))
    {
      Log.e("AccountSettings", "Provider needs a label for authority '" + paramString + "'");
      return;
    }
    paramAccount.setTitle(getString(2131692734, new Object[] { localObject }));
    paramAccount.setKey(paramString);
  }
  
  private void cancelSyncForEnabledProviders()
  {
    requestOrCancelSyncForEnabledProviders(false);
    Activity localActivity = getActivity();
    if (localActivity != null) {
      localActivity.invalidateOptionsMenu();
    }
  }
  
  private boolean isSyncing(List<SyncInfo> paramList, Account paramAccount, String paramString)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      SyncInfo localSyncInfo = (SyncInfo)paramList.next();
      if ((localSyncInfo.account.equals(paramAccount)) && (localSyncInfo.authority.equals(paramString))) {
        return true;
      }
    }
    return false;
  }
  
  private void requestOrCancelSync(Account paramAccount, String paramString, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      Bundle localBundle = new Bundle();
      localBundle.putBoolean("force", true);
      ContentResolver.requestSyncAsUser(paramAccount, paramString, this.mUserHandle.getIdentifier(), localBundle);
      return;
    }
    ContentResolver.cancelSyncAsUser(paramAccount, paramString, this.mUserHandle.getIdentifier());
  }
  
  private void requestOrCancelSyncForEnabledProviders(boolean paramBoolean)
  {
    int j = getPreferenceScreen().getPreferenceCount();
    int i = 0;
    Object localObject;
    if (i < j)
    {
      localObject = getPreferenceScreen().getPreference(i);
      if (!(localObject instanceof SyncStateSwitchPreference)) {}
      for (;;)
      {
        i += 1;
        break;
        localObject = (SyncStateSwitchPreference)localObject;
        if (((SyncStateSwitchPreference)localObject).isChecked()) {
          requestOrCancelSync(((SyncStateSwitchPreference)localObject).getAccount(), ((SyncStateSwitchPreference)localObject).getAuthority(), paramBoolean);
        }
      }
    }
    if (this.mAccount != null)
    {
      localObject = this.mInvisibleAdapters.iterator();
      while (((Iterator)localObject).hasNext())
      {
        SyncAdapterType localSyncAdapterType = (SyncAdapterType)((Iterator)localObject).next();
        requestOrCancelSync(this.mAccount, localSyncAdapterType.authority, paramBoolean);
      }
    }
  }
  
  private void setAccessibilityTitle()
  {
    Object localObject = ((UserManager)getSystemService("user")).getUserInfo(this.mUserHandle.getIdentifier());
    boolean bool;
    if (localObject != null)
    {
      bool = ((UserInfo)localObject).isManagedProfile();
      localObject = getActivity().getTitle();
      if (!bool) {
        break label80;
      }
    }
    label80:
    for (int i = 2131691592;; i = 2131691593)
    {
      String str = getString(i, new Object[] { localObject });
      getActivity().setTitle(Utils.createAccessibleSequence((CharSequence)localObject, str));
      return;
      bool = false;
      break;
    }
  }
  
  private void setFeedsState()
  {
    Object localObject1 = new Date();
    int m = this.mUserHandle.getIdentifier();
    List localList = ContentResolver.getCurrentSyncsAsUser(m);
    int i = 0;
    updateAccountSwitches();
    int k = 0;
    int n = getPreferenceScreen().getPreferenceCount();
    while (k < n)
    {
      Object localObject2 = getPreferenceScreen().getPreference(k);
      if (!(localObject2 instanceof SyncStateSwitchPreference))
      {
        k += 1;
      }
      else
      {
        localObject2 = (SyncStateSwitchPreference)localObject2;
        String str = ((SyncStateSwitchPreference)localObject2).getAuthority();
        Account localAccount = ((SyncStateSwitchPreference)localObject2).getAccount();
        Object localObject3 = ContentResolver.getSyncStatusAsUser(localAccount, str, m);
        boolean bool5 = ContentResolver.getSyncAutomaticallyAsUser(localAccount, str, m);
        boolean bool2;
        label124:
        boolean bool3;
        label132:
        boolean bool6;
        label172:
        int j;
        label198:
        long l;
        if (localObject3 == null)
        {
          bool2 = false;
          if (localObject3 != null) {
            break label402;
          }
          bool3 = false;
          bool6 = isSyncing(localList, localAccount, str);
          if ((localObject3 == null) || (((SyncStatusInfo)localObject3).lastFailureTime == 0L)) {
            break label418;
          }
          if (((SyncStatusInfo)localObject3).getLastFailureMesgAsInt(0) == 1) {
            break label412;
          }
          bool1 = true;
          boolean bool4 = bool1;
          if (!bool5) {
            bool4 = false;
          }
          j = i;
          if (bool4)
          {
            if (!bool6) {
              break label424;
            }
            j = i;
          }
          if (Log.isLoggable("AccountSettings", 2)) {
            Log.d("AccountSettings", "Update sync status: " + localAccount + " " + str + " active = " + bool6 + " pend =" + bool2);
          }
          if (localObject3 != null) {
            break label436;
          }
          l = 0L;
          label275:
          if (bool5) {
            break label446;
          }
          ((SyncStateSwitchPreference)localObject2).setSummary(2131692710);
          label288:
          i = ContentResolver.getIsSyncableAsUser(localAccount, str, m);
          if ((!bool6) || (i < 0)) {
            break label528;
          }
          if (!bool3) {
            break label522;
          }
          bool1 = false;
          label315:
          ((SyncStateSwitchPreference)localObject2).setActive(bool1);
          if ((!bool2) || (i < 0)) {
            break label540;
          }
          if (!bool3) {
            break label534;
          }
          bool1 = false;
          label339:
          ((SyncStateSwitchPreference)localObject2).setPending(bool1);
          ((SyncStateSwitchPreference)localObject2).setFailed(bool4);
          if (!ContentResolver.getMasterSyncAutomaticallyAsUser(m)) {
            break label546;
          }
          bool1 = false;
          label364:
          ((SyncStateSwitchPreference)localObject2).setOneTimeSyncMode(bool1);
          if (bool1) {
            break label552;
          }
        }
        label402:
        label412:
        label418:
        label424:
        label436:
        label446:
        label522:
        label528:
        label534:
        label540:
        label546:
        label552:
        for (boolean bool1 = bool5;; bool1 = true)
        {
          ((SyncStateSwitchPreference)localObject2).setChecked(bool1);
          i = j;
          break;
          bool2 = ((SyncStatusInfo)localObject3).pending;
          break label124;
          bool3 = ((SyncStatusInfo)localObject3).initialize;
          break label132;
          bool1 = false;
          break label172;
          bool1 = false;
          break label172;
          j = i;
          if (bool2) {
            break label198;
          }
          j = 1;
          break label198;
          l = ((SyncStatusInfo)localObject3).lastSuccessTime;
          break label275;
          if (bool6)
          {
            ((SyncStateSwitchPreference)localObject2).setSummary(2131692713);
            break label288;
          }
          if (l != 0L)
          {
            ((Date)localObject1).setTime(l);
            localObject3 = formatSyncDate((Date)localObject1);
            ((SyncStateSwitchPreference)localObject2).setSummary(getResources().getString(2131692712, new Object[] { localObject3 }));
            break label288;
          }
          ((SyncStateSwitchPreference)localObject2).setSummary("");
          break label288;
          bool1 = true;
          break label315;
          bool1 = false;
          break label315;
          bool1 = true;
          break label339;
          bool1 = false;
          break label339;
          bool1 = true;
          break label364;
        }
      }
    }
    localObject1 = this.mErrorInfoView;
    if (i != 0) {}
    for (i = 0;; i = 8)
    {
      ((TextView)localObject1).setVisibility(i);
      return;
    }
  }
  
  private void startSyncForEnabledProviders()
  {
    requestOrCancelSyncForEnabledProviders(true);
    Activity localActivity = getActivity();
    if (localActivity != null) {
      localActivity.invalidateOptionsMenu();
    }
  }
  
  private void updateAccountSwitches()
  {
    this.mInvisibleAdapters.clear();
    Object localObject1 = ContentResolver.getSyncAdapterTypesAsUser(this.mUserHandle.getIdentifier());
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    int j = localObject1.length;
    if (i < j)
    {
      Object localObject2 = localObject1[i];
      if (!((SyncAdapterType)localObject2).accountType.equals(this.mAccount.type)) {}
      for (;;)
      {
        i += 1;
        break;
        if (((SyncAdapterType)localObject2).isUserVisible())
        {
          if (Log.isLoggable("AccountSettings", 2)) {
            Log.d("AccountSettings", "updateAccountSwitches: added authority " + ((SyncAdapterType)localObject2).authority + " to accountType " + ((SyncAdapterType)localObject2).accountType);
          }
          localArrayList.add(((SyncAdapterType)localObject2).authority);
        }
        else
        {
          this.mInvisibleAdapters.add(localObject2);
        }
      }
    }
    if (Log.isLoggable("AccountSettings", 2)) {
      Log.d("AccountSettings", "looking for sync adapters that match account " + this.mAccount);
    }
    cacheRemoveAllPrefs(getPreferenceScreen());
    i = 0;
    j = localArrayList.size();
    while (i < j)
    {
      localObject1 = (String)localArrayList.get(i);
      int k = ContentResolver.getIsSyncableAsUser(this.mAccount, (String)localObject1, this.mUserHandle.getIdentifier());
      if (Log.isLoggable("AccountSettings", 2)) {
        Log.d("AccountSettings", "  found authority " + (String)localObject1 + " " + k);
      }
      if (k > 0) {
        addSyncStateSwitch(this.mAccount, (String)localObject1);
      }
      i += 1;
    }
    removeCachedPrefs(getPreferenceScreen());
  }
  
  protected int getHelpResource()
  {
    return 2131693020;
  }
  
  protected int getMetricsCategory()
  {
    return 9;
  }
  
  protected void initializeUi(View paramView)
  {
    this.mErrorInfoView = ((TextView)paramView.findViewById(2131361936));
    this.mErrorInfoView.setVisibility(8);
    this.mUserId = ((TextView)paramView.findViewById(2131362610));
    this.mProviderId = ((TextView)paramView.findViewById(2131362611));
    this.mProviderIcon = ((ImageView)paramView.findViewById(2131362609));
  }
  
  public void onAccountsUpdate(UserHandle paramUserHandle)
  {
    super.onAccountsUpdate(paramUserHandle);
    if (!accountExists(this.mAccount))
    {
      finish();
      return;
    }
    updateAccountSwitches();
    onSyncStateUpdated();
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    paramBundle = getArguments();
    if (paramBundle == null)
    {
      Log.e("AccountSettings", "No arguments provided when starting intent. ACCOUNT_KEY needed.");
      finish();
      return;
    }
    this.mAccount = ((Account)paramBundle.getParcelable("account"));
    if (!accountExists(this.mAccount))
    {
      Log.e("AccountSettings", "Account provided does not exist: " + this.mAccount);
      finish();
      return;
    }
    if (Log.isLoggable("AccountSettings", 2)) {
      Log.v("AccountSettings", "Got account: " + this.mAccount);
    }
    this.mUserId.setText(this.mAccount.name);
    this.mProviderId.setText(this.mAccount.type);
  }
  
  protected void onAuthDescriptionsUpdated()
  {
    super.onAuthDescriptionsUpdated();
    if (this.mAccount != null)
    {
      this.mProviderIcon.setImageDrawable(getDrawableForType(this.mAccount.type));
      this.mProviderId.setText(getLabelForType(this.mAccount.type));
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setPreferenceScreen(null);
    addPreferencesFromResource(2131230726);
    getPreferenceScreen().setOrderingAsAdded(false);
    setAccessibilityTitle();
    setHasOptionsMenu(true);
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    AlertDialog localAlertDialog = null;
    if (paramInt == 100) {
      localAlertDialog = new AlertDialog.Builder(getActivity()).setTitle(2131692730).setMessage(2131692731).setNegativeButton(17039360, null).setPositiveButton(2131692727, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = AccountSyncSettings.this.getActivity();
          AccountManager.get(paramAnonymousDialogInterface).removeAccountAsUser(AccountSyncSettings.-get0(AccountSyncSettings.this), paramAnonymousDialogInterface, new AccountManagerCallback()
          {
            public void run(AccountManagerFuture<Bundle> paramAnonymous2AccountManagerFuture)
            {
              if (!AccountSyncSettings.this.isResumed()) {
                return;
              }
              int i = 1;
              try
              {
                boolean bool = ((Bundle)paramAnonymous2AccountManagerFuture.getResult()).getBoolean("booleanResult");
                if (bool) {
                  i = 0;
                }
              }
              catch (OperationCanceledException paramAnonymous2AccountManagerFuture)
              {
                for (;;) {}
              }
              catch (IOException paramAnonymous2AccountManagerFuture)
              {
                for (;;) {}
              }
              catch (AuthenticatorException paramAnonymous2AccountManagerFuture)
              {
                for (;;) {}
              }
              if ((i == 0) || (AccountSyncSettings.this.getActivity() == null) || (AccountSyncSettings.this.getActivity().isFinishing()))
              {
                AccountSyncSettings.this.finish();
                return;
              }
              AccountSyncSettings.-wrap0(AccountSyncSettings.this, 101);
            }
          }, null, AccountSyncSettings.this.mUserHandle);
        }
      }).create();
    }
    do
    {
      return localAlertDialog;
      if (paramInt == 101) {
        return new AlertDialog.Builder(getActivity()).setTitle(2131692730).setPositiveButton(17039370, null).setMessage(2131692732).create();
      }
    } while (paramInt != 102);
    return new AlertDialog.Builder(getActivity()).setTitle(2131692735).setMessage(2131692736).setPositiveButton(17039370, null).create();
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    MenuItem localMenuItem1 = paramMenu.add(0, 1, 0, getString(2131692716)).setIcon(2130838007);
    MenuItem localMenuItem2 = paramMenu.add(0, 2, 0, getString(2131692717)).setIcon(17301560);
    if (!RestrictedLockUtils.hasBaseUserRestriction(getPrefContext(), "no_modify_accounts", this.mUserHandle.getIdentifier()))
    {
      MenuItem localMenuItem3 = paramMenu.add(0, 3, 0, getString(2131692727)).setIcon(2130838003);
      localMenuItem3.setShowAsAction(4);
      RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin2 = RestrictedLockUtils.checkIfRestrictionEnforced(getPrefContext(), "no_modify_accounts", this.mUserHandle.getIdentifier());
      RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin1 = localEnforcedAdmin2;
      if (localEnforcedAdmin2 == null) {
        localEnforcedAdmin1 = RestrictedLockUtils.checkIfAccountManagementDisabled(getPrefContext(), this.mAccount.type, this.mUserHandle.getIdentifier());
      }
      RestrictedLockUtils.setMenuItemAsDisabledByAdmin(getPrefContext(), localMenuItem3, localEnforcedAdmin1);
    }
    localMenuItem1.setShowAsAction(4);
    localMenuItem2.setShowAsAction(4);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968603, paramViewGroup, false);
    ViewGroup localViewGroup = (ViewGroup)localView.findViewById(2131361935);
    Utils.prepareCustomPreferencesList(paramViewGroup, localView, localViewGroup, false);
    localViewGroup.addView(super.onCreateView(paramLayoutInflater, localViewGroup, paramBundle));
    initializeUi(localView);
    return localView;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 1: 
      startSyncForEnabledProviders();
      return true;
    case 2: 
      cancelSyncForEnabledProviders();
      return true;
    }
    showDialog(100);
    return true;
  }
  
  public void onPause()
  {
    super.onPause();
    this.mAuthenticatorHelper.stopListeningToAccountUpdates();
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if ((paramPreference instanceof SyncStateSwitchPreference))
    {
      paramPreference = (SyncStateSwitchPreference)paramPreference;
      String str = paramPreference.getAuthority();
      Account localAccount = paramPreference.getAccount();
      int i = this.mUserHandle.getIdentifier();
      boolean bool1 = ContentResolver.getSyncAutomaticallyAsUser(localAccount, str, i);
      if (paramPreference.isOneTimeSyncMode()) {
        requestOrCancelSync(localAccount, str, true);
      }
      boolean bool2;
      do
      {
        do
        {
          return true;
          bool2 = paramPreference.isChecked();
        } while (bool2 == bool1);
        ContentResolver.setSyncAutomaticallyAsUser(localAccount, str, bool2, i);
      } while ((ContentResolver.getMasterSyncAutomaticallyAsUser(i)) && (bool2));
      requestOrCancelSync(localAccount, str, bool2);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    boolean bool2 = true;
    super.onPrepareOptionsMenu(paramMenu);
    if (ContentResolver.getCurrentSyncsAsUser(this.mUserHandle.getIdentifier()).isEmpty()) {}
    for (boolean bool1 = false;; bool1 = true)
    {
      MenuItem localMenuItem = paramMenu.findItem(1);
      if (bool1) {
        bool2 = false;
      }
      localMenuItem.setVisible(bool2);
      paramMenu.findItem(2).setVisible(bool1);
      return;
    }
  }
  
  public void onResume()
  {
    removePreference("dummy");
    this.mAuthenticatorHelper.listenToAccountUpdates();
    updateAuthDescriptions();
    onAccountsUpdate(Binder.getCallingUserHandle());
    super.onResume();
  }
  
  protected void onSyncStateUpdated()
  {
    if (!isResumed()) {
      return;
    }
    setFeedsState();
    Activity localActivity = getActivity();
    if (localActivity != null) {
      localActivity.invalidateOptionsMenu();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accounts\AccountSyncSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */