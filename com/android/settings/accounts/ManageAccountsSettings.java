package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.SyncStatusInfo;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.settings.AccountPreference;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.location.LocationSettings;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ManageAccountsSettings
  extends AccountPreferenceBase
  implements AuthenticatorHelper.OnAccountsUpdateListener
{
  private static final String ACCOUNT_KEY = "account";
  public static final String KEY_ACCOUNT_LABEL = "account_label";
  public static final String KEY_ACCOUNT_TYPE = "account_type";
  private static final String LAUNCHING_LOCATION_SETTINGS = "com.android.settings.accounts.LAUNCHING_LOCATION_SETTINGS";
  private static final int MENU_SYNC_CANCEL_ID = 2;
  private static final int MENU_SYNC_NOW_ID = 1;
  private static final int REQUEST_SHOW_SYNC_SETTINGS = 1;
  private final int UPDATE_PREF_MESSAGE = 1;
  private final int UPDATE_VIEW_MESSAGE = 2;
  private String mAccountType;
  private String[] mAuthorities;
  private TextView mErrorInfoView;
  private Account mFirstAccount;
  Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
      case 1: 
        AccountPreference localAccountPreference;
        do
        {
          return;
          localAccountPreference = (AccountPreference)paramAnonymousMessage.obj;
          paramAnonymousMessage = paramAnonymousMessage.getData();
          localAccountPreference.setSyncStatus(paramAnonymousMessage.getInt("status", 1), paramAnonymousMessage.getBoolean("updateSummary", true));
          paramAnonymousMessage = paramAnonymousMessage.getString("summary", "");
        } while ((paramAnonymousMessage == null) || (paramAnonymousMessage.isEmpty()));
        localAccountPreference.setSummary(paramAnonymousMessage);
        return;
      }
      boolean bool = ((Boolean)paramAnonymousMessage.obj).booleanValue();
      paramAnonymousMessage = ManageAccountsSettings.-get0(ManageAccountsSettings.this);
      if (bool) {}
      for (int i = 0;; i = 8)
      {
        paramAnonymousMessage.setVisibility(i);
        return;
      }
    }
  };
  
  private void addAuthenticatorSettings()
  {
    PreferenceScreen localPreferenceScreen = addPreferencesForType(this.mAccountType, getPreferenceScreen());
    if (localPreferenceScreen != null) {
      updatePreferenceIntents(localPreferenceScreen);
    }
  }
  
  private boolean isSafeIntent(PackageManager paramPackageManager, Intent paramIntent)
  {
    AuthenticatorDescription localAuthenticatorDescription = this.mAuthenticatorHelper.getAccountTypeDescription(this.mAccountType);
    paramIntent = paramPackageManager.resolveActivityAsUser(paramIntent, 0, this.mUserHandle.getIdentifier());
    if (paramIntent == null) {
      return false;
    }
    ActivityInfo localActivityInfo = paramIntent.activityInfo;
    paramIntent = localActivityInfo.applicationInfo;
    try
    {
      if (localActivityInfo.exported)
      {
        if (localActivityInfo.permission == null) {
          return true;
        }
        if (paramPackageManager.checkPermission(localActivityInfo.permission, localAuthenticatorDescription.packageName) == 0) {
          return true;
        }
      }
      paramPackageManager = paramPackageManager.getApplicationInfo(localAuthenticatorDescription.packageName, 0);
      int i = paramIntent.uid;
      int j = paramPackageManager.uid;
      return i == j;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager)
    {
      Log.e("AccountSettings", "Intent considered unsafe due to exception.", paramPackageManager);
    }
    return false;
  }
  
  private boolean isSyncEnabled(int paramInt, Account paramAccount, String paramString)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (ContentResolver.getSyncAutomaticallyAsUser(paramAccount, paramString, paramInt))
    {
      bool1 = bool2;
      if (ContentResolver.getMasterSyncAutomaticallyAsUser(paramInt))
      {
        bool1 = bool2;
        if (ContentResolver.getIsSyncableAsUser(paramAccount, paramString, paramInt) > 0) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  private boolean isSyncing(List<SyncInfo> paramList, Account paramAccount, String paramString)
  {
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      SyncInfo localSyncInfo = (SyncInfo)paramList.get(i);
      if ((localSyncInfo.account.equals(paramAccount)) && (localSyncInfo.authority.equals(paramString))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void requestOrCancelSyncForAccounts(boolean paramBoolean)
  {
    int k = this.mUserHandle.getIdentifier();
    SyncAdapterType[] arrayOfSyncAdapterType = ContentResolver.getSyncAdapterTypesAsUser(k);
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("force", true);
    int m = getPreferenceScreen().getPreferenceCount();
    int i = 0;
    while (i < m)
    {
      Object localObject = getPreferenceScreen().getPreference(i);
      if ((localObject instanceof AccountPreference))
      {
        localObject = ((AccountPreference)localObject).getAccount();
        int j = 0;
        if (j < arrayOfSyncAdapterType.length)
        {
          SyncAdapterType localSyncAdapterType = arrayOfSyncAdapterType[j];
          if ((arrayOfSyncAdapterType[j].accountType.equals(this.mAccountType)) && (ContentResolver.getSyncAutomaticallyAsUser((Account)localObject, localSyncAdapterType.authority, k)))
          {
            if (!paramBoolean) {
              break label150;
            }
            ContentResolver.requestSyncAsUser((Account)localObject, localSyncAdapterType.authority, k, localBundle);
          }
          for (;;)
          {
            j += 1;
            break;
            label150:
            ContentResolver.cancelSyncAsUser((Account)localObject, localSyncAdapterType.authority, k);
          }
        }
      }
      i += 1;
    }
  }
  
  private void showAccountsIfNeeded()
  {
    if (getActivity() == null) {
      return;
    }
    Account[] arrayOfAccount = AccountManager.get(getActivity()).getAccountsAsUser(this.mUserHandle.getIdentifier());
    getPreferenceScreen().removeAll();
    this.mFirstAccount = null;
    addPreferencesFromResource(2131230778);
    int j = 0;
    int n = arrayOfAccount.length;
    if (j < n)
    {
      Account localAccount = arrayOfAccount[j];
      if (!Utils.showAccount(getActivity(), localAccount.type)) {}
      while ((this.mAccountType != null) && (!localAccount.type.equals(this.mAccountType)))
      {
        j += 1;
        break;
      }
      Object localObject1 = getAuthoritiesForAccountType(localAccount.type);
      int k = 1;
      int i = k;
      int m;
      Object localObject2;
      int i1;
      if (this.mAuthorities != null)
      {
        i = k;
        if (localObject1 != null)
        {
          m = 0;
          localObject2 = this.mAuthorities;
          i1 = localObject2.length;
          k = 0;
        }
      }
      for (;;)
      {
        i = m;
        if (k < i1)
        {
          if (((ArrayList)localObject1).contains(localObject2[k])) {
            i = 1;
          }
        }
        else
        {
          if (i == 0) {
            break;
          }
          localObject2 = getDrawableForType(localAccount.type);
          localObject1 = new AccountPreference(getPrefContext(), localAccount, (Drawable)localObject2, (ArrayList)localObject1, false);
          getPreferenceScreen().addPreference((Preference)localObject1);
          if (this.mFirstAccount != null) {
            break;
          }
          this.mFirstAccount = localAccount;
          break;
        }
        k += 1;
      }
    }
    if ((this.mAccountType != null) && (this.mFirstAccount != null))
    {
      addAuthenticatorSettings();
      return;
    }
    finish();
  }
  
  private void showSyncState()
  {
    if ((getActivity() == null) || (getActivity().isFinishing())) {
      return;
    }
    int i3 = this.mUserHandle.getIdentifier();
    List localList = ContentResolver.getCurrentSyncsAsUser(i3);
    boolean bool1 = false;
    Date localDate = new Date();
    Object localObject1 = ContentResolver.getSyncAdapterTypesAsUser(i3);
    HashSet localHashSet = new HashSet();
    int i = 0;
    int j = localObject1.length;
    Object localObject2;
    while (i < j)
    {
      localObject2 = localObject1[i];
      if (((SyncAdapterType)localObject2).isUserVisible()) {
        localHashSet.add(((SyncAdapterType)localObject2).authority);
      }
      i += 1;
    }
    int n = 0;
    int i4 = getPreferenceScreen().getPreferenceCount();
    for (;;)
    {
      if (n < i4) {
        try
        {
          localObject1 = getPreferenceScreen().getPreference(n);
          if (!(localObject1 instanceof AccountPreference))
          {
            n += 1;
          }
          else
          {
            localObject1 = (AccountPreference)localObject1;
            localObject2 = ((AccountPreference)localObject1).getAccount();
            i = 0;
            int m = 0;
            long l1 = 0L;
            j = 0;
            int i1 = 0;
            Object localObject3 = ((AccountPreference)localObject1).getAuthorities();
            int k = 0;
            int i2 = 0;
            boolean bool2;
            long l2;
            if (localObject3 != null)
            {
              localObject3 = ((Iterable)localObject3).iterator();
              j = i2;
              k = i1;
              i = m;
              bool2 = bool1;
              l2 = l1;
              m = i;
              i1 = k;
              i2 = j;
              if (((Iterator)localObject3).hasNext())
              {
                String str = (String)((Iterator)localObject3).next();
                SyncStatusInfo localSyncStatusInfo = ContentResolver.getSyncStatusAsUser((Account)localObject2, str, i3);
                boolean bool3 = isSyncEnabled(i3, (Account)localObject2, str);
                boolean bool4 = ContentResolver.isSyncPending((Account)localObject2, str);
                int i5 = isSyncing(localList, (Account)localObject2, str);
                if ((localSyncStatusInfo != null) && (bool3) && (localSyncStatusInfo.lastFailureTime != 0L)) {
                  if (localSyncStatusInfo.getLastFailureMesgAsInt(0) != 1)
                  {
                    m = 1;
                    label330:
                    i1 = m;
                    label334:
                    bool2 = bool1;
                    m = k;
                    if (i1 != 0)
                    {
                      if (i5 == 0) {
                        break label445;
                      }
                      m = k;
                      bool2 = bool1;
                    }
                    label358:
                    i1 = j | i5;
                    l2 = l1;
                    if (localSyncStatusInfo != null)
                    {
                      l2 = l1;
                      if (l1 < localSyncStatusInfo.lastSuccessTime) {
                        l2 = localSyncStatusInfo.lastSuccessTime;
                      }
                    }
                    if ((!bool3) || (!localHashSet.contains(str))) {
                      break label466;
                    }
                  }
                }
                label445:
                label466:
                for (j = 1;; j = 0)
                {
                  i += j;
                  bool1 = bool2;
                  l1 = l2;
                  k = m;
                  j = i1;
                  break;
                  m = 0;
                  break label330;
                  i1 = 0;
                  break label334;
                  bool2 = bool1;
                  m = k;
                  if (bool4) {
                    break label358;
                  }
                  m = 1;
                  bool2 = true;
                  break label358;
                }
              }
            }
            else
            {
              bool2 = bool1;
              l2 = l1;
              m = i;
              i1 = j;
              i2 = k;
              if (Log.isLoggable("AccountSettings", 2))
              {
                Log.v("AccountSettings", "no syncadapters found for " + localObject2);
                i2 = k;
                i1 = j;
                m = i;
                l2 = l1;
                bool2 = bool1;
              }
            }
            localObject2 = new Message();
            ((Message)localObject2).what = 1;
            ((Message)localObject2).obj = localObject1;
            localObject1 = new Bundle();
            if (i1 != 0)
            {
              ((Bundle)localObject1).putInt("status", 2);
              ((Bundle)localObject1).putBoolean("updateSummary", true);
            }
            for (;;)
            {
              ((Message)localObject2).setData((Bundle)localObject1);
              this.mHandler.sendMessage((Message)localObject2);
              bool1 = bool2;
              break;
              if (m == 0)
              {
                ((Bundle)localObject1).putInt("status", 1);
                ((Bundle)localObject1).putBoolean("updateSummary", true);
              }
              else if (m > 0)
              {
                if (i2 != 0)
                {
                  ((Bundle)localObject1).putInt("status", 3);
                  ((Bundle)localObject1).putBoolean("updateSummary", true);
                }
                else
                {
                  ((Bundle)localObject1).putInt("status", 0);
                  if (l2 > 0L)
                  {
                    ((Bundle)localObject1).putBoolean("updateSummary", false);
                    localDate.setTime(l2);
                    localObject3 = formatSyncDate(localDate);
                    ((Bundle)localObject1).putString("summary", getResources().getString(2131692712, new Object[] { localObject3 }));
                  }
                  else
                  {
                    ((Bundle)localObject1).putBoolean("updateSummary", true);
                  }
                }
              }
              else
              {
                ((Bundle)localObject1).putInt("status", 1);
                ((Bundle)localObject1).putBoolean("updateSummary", true);
              }
            }
            localMessage = new Message();
          }
        }
        catch (Exception localException) {}
      }
    }
    Message localMessage;
    localMessage.what = 2;
    localMessage.obj = Boolean.valueOf(bool1);
    this.mHandler.sendMessage(localMessage);
  }
  
  private void startAccountSettings(AccountPreference paramAccountPreference)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("account", paramAccountPreference.getAccount());
    localBundle.putParcelable("android.intent.extra.USER", this.mUserHandle);
    ((SettingsActivity)getActivity()).startPreferencePanel(AccountSyncSettings.class.getCanonicalName(), localBundle, 2131692697, paramAccountPreference.getAccount().name, this, 1);
  }
  
  private void updatePreferenceIntents(PreferenceGroup paramPreferenceGroup)
  {
    final PackageManager localPackageManager = getActivity().getPackageManager();
    int i = 0;
    if (i < paramPreferenceGroup.getPreferenceCount())
    {
      Preference localPreference = paramPreferenceGroup.getPreference(i);
      if ((localPreference instanceof PreferenceGroup)) {
        updatePreferenceIntents((PreferenceGroup)localPreference);
      }
      Intent localIntent = localPreference.getIntent();
      if (localIntent != null)
      {
        if (!localIntent.getAction().equals("android.settings.LOCATION_SOURCE_SETTINGS")) {
          break label97;
        }
        localPreference.setOnPreferenceClickListener(new FragmentStarter(LocationSettings.class.getName(), 2131691058));
      }
      for (;;)
      {
        i += 1;
        break;
        label97:
        if (localPackageManager.resolveActivityAsUser(localIntent, 65536, this.mUserHandle.getIdentifier()) == null)
        {
          paramPreferenceGroup.removePreference(localPreference);
          break;
        }
        localIntent.putExtra("account", this.mFirstAccount);
        localIntent.setFlags(localIntent.getFlags() | 0x10000000);
        localPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
          public boolean onPreferenceClick(Preference paramAnonymousPreference)
          {
            paramAnonymousPreference = paramAnonymousPreference.getIntent();
            if (ManageAccountsSettings.-wrap0(ManageAccountsSettings.this, localPackageManager, paramAnonymousPreference)) {
              ManageAccountsSettings.this.getActivity().startActivityAsUser(paramAnonymousPreference, ManageAccountsSettings.this.mUserHandle);
            }
            for (;;)
            {
              return true;
              Log.e("AccountSettings", "Refusing to launch authenticator intent because it exploits Settings permissions: " + paramAnonymousPreference);
            }
          }
        });
      }
    }
  }
  
  protected int getMetricsCategory()
  {
    return 11;
  }
  
  public void onAccountsUpdate(UserHandle paramUserHandle)
  {
    showAccountsIfNeeded();
    onSyncStateUpdated();
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    paramBundle = getActivity();
    this.mErrorInfoView = ((TextView)getView().findViewById(2131361936));
    this.mErrorInfoView.setVisibility(8);
    this.mAuthorities = paramBundle.getIntent().getStringArrayExtra("authorities");
    paramBundle = getArguments();
    if ((paramBundle != null) && (paramBundle.containsKey("account_label"))) {
      getActivity().setTitle(paramBundle.getString("account_label"));
    }
  }
  
  protected void onAuthDescriptionsUpdated()
  {
    int i = 0;
    while (i < getPreferenceScreen().getPreferenceCount())
    {
      Object localObject = getPreferenceScreen().getPreference(i);
      if ((localObject instanceof AccountPreference))
      {
        localObject = (AccountPreference)localObject;
        ((AccountPreference)localObject).setSummary(getLabelForType(((AccountPreference)localObject).getAccount().type));
      }
      i += 1;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getArguments();
    if ((paramBundle != null) && (paramBundle.containsKey("account_type"))) {
      this.mAccountType = paramBundle.getString("account_type");
    }
    addPreferencesFromResource(2131230778);
    setHasOptionsMenu(true);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 1, 0, getString(2131692716)).setIcon(2130838007);
    paramMenu.add(0, 2, 0, getString(2131692717)).setIcon(17301560);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968745, paramViewGroup, false);
    ViewGroup localViewGroup = (ViewGroup)localView.findViewById(2131361935);
    Utils.prepareCustomPreferencesList(paramViewGroup, localView, localViewGroup, false);
    localViewGroup.addView(super.onCreateView(paramLayoutInflater, localViewGroup, paramBundle));
    return localView;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 1: 
      requestOrCancelSyncForAccounts(true);
      return true;
    }
    requestOrCancelSyncForAccounts(false);
    return true;
  }
  
  public void onPause()
  {
    super.onPause();
    this.mAuthenticatorHelper.stopListeningToAccountUpdates();
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if ((paramPreference instanceof AccountPreference))
    {
      startAccountSettings((AccountPreference)paramPreference);
      return true;
    }
    return false;
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
    super.onResume();
    this.mAuthenticatorHelper.listenToAccountUpdates();
    updateAuthDescriptions();
    showAccountsIfNeeded();
    new Thread()
    {
      public void run()
      {
        ManageAccountsSettings.-wrap1(ManageAccountsSettings.this);
      }
    }.start();
  }
  
  public void onStop()
  {
    super.onStop();
    Activity localActivity = getActivity();
    localActivity.getActionBar().setDisplayOptions(0, 16);
    localActivity.getActionBar().setCustomView(null);
  }
  
  protected void onSyncStateUpdated()
  {
    new Thread()
    {
      public void run()
      {
        ManageAccountsSettings.-wrap1(ManageAccountsSettings.this);
      }
    }.start();
    Activity localActivity = getActivity();
    if (localActivity != null) {
      localActivity.invalidateOptionsMenu();
    }
  }
  
  private class FragmentStarter
    implements Preference.OnPreferenceClickListener
  {
    private final String mClass;
    private final int mTitleRes;
    
    public FragmentStarter(String paramString, int paramInt)
    {
      this.mClass = paramString;
      this.mTitleRes = paramInt;
    }
    
    public boolean onPreferenceClick(Preference paramPreference)
    {
      ((SettingsActivity)ManageAccountsSettings.this.getActivity()).startPreferencePanel(this.mClass, null, this.mTitleRes, null, null, 0);
      if (this.mClass.equals(LocationSettings.class.getName()))
      {
        paramPreference = new Intent("com.android.settings.accounts.LAUNCHING_LOCATION_SETTINGS");
        ManageAccountsSettings.this.getActivity().sendBroadcast(paramPreference, "android.permission.WRITE_SECURE_SETTINGS");
      }
      return true;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accounts\ManageAccountsSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */