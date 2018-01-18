package com.android.settings.accounts;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import com.android.settings.AccessiblePreferenceCategory;
import com.android.settings.DimmableIconPreference;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Index;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settings.users.UserDialogs;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPPreferenceHeaderMargin;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class AccountSettings
  extends SettingsPreferenceFragment
  implements AuthenticatorHelper.OnAccountsUpdateListener, Preference.OnPreferenceClickListener, Indexable
{
  private static final String ADD_ACCOUNT_ACTION = "android.settings.ADD_ACCOUNT_SETTINGS";
  private static final String KEY_ACCOUNT = "account";
  private static final int ORDER_LAST = 1002;
  private static final int ORDER_NEXT_TO_LAST = 1001;
  private static final int ORDER_NEXT_TO_NEXT_TO_LAST = 1000;
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      Resources localResources = paramAnonymousContext.getResources();
      String str = localResources.getString(2131691059);
      List localList = UserManager.get(paramAnonymousContext).getProfiles(UserHandle.myUserId());
      int j = localList.size();
      int i = 0;
      while (i < j)
      {
        Object localObject = (UserInfo)localList.get(i);
        if (((UserInfo)localObject).isEnabled())
        {
          if (!RestrictedLockUtils.hasBaseUserRestriction(paramAnonymousContext, "no_modify_accounts", ((UserInfo)localObject).id))
          {
            SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
            localSearchIndexableRaw.title = localResources.getString(2131692699);
            localSearchIndexableRaw.screenTitle = str;
            localArrayList.add(localSearchIndexableRaw);
          }
          if ((((UserInfo)localObject).isManagedProfile()) && (((UserInfo)localObject).id != 999))
          {
            localObject = new SearchIndexableRaw(paramAnonymousContext);
            ((SearchIndexableRaw)localObject).title = localResources.getString(2131692703);
            ((SearchIndexableRaw)localObject).screenTitle = str;
            localArrayList.add(localObject);
            localObject = new SearchIndexableRaw(paramAnonymousContext);
            ((SearchIndexableRaw)localObject).title = localResources.getString(2131693741);
            ((SearchIndexableRaw)localObject).screenTitle = str;
            localArrayList.add(localObject);
          }
        }
        i += 1;
      }
      return localArrayList;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230725;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  public static final String TAG = "AccountSettings";
  private static final String TAG_CONFIRM_AUTO_SYNC_CHANGE = "confirmAutoSyncChange";
  private String[] mAuthorities;
  private int mAuthoritiesCount = 0;
  private ManagedProfileBroadcastReceiver mManagedProfileBroadcastReceiver = new ManagedProfileBroadcastReceiver(null);
  private Preference mProfileNotAvailablePreference;
  private SparseArray<ProfileData> mProfiles = new SparseArray();
  private UserManager mUm;
  
  private boolean accountTypeHasAnyRequestedAuthorities(AuthenticatorHelper paramAuthenticatorHelper, String paramString)
  {
    if (this.mAuthoritiesCount == 0) {
      return true;
    }
    paramAuthenticatorHelper = paramAuthenticatorHelper.getAuthoritiesForAccountType(paramString);
    if (paramAuthenticatorHelper == null)
    {
      Log.d("AccountSettings", "No sync authorities for account type: " + paramString);
      return false;
    }
    int i = 0;
    while (i < this.mAuthoritiesCount)
    {
      if (paramAuthenticatorHelper.contains(this.mAuthorities[i])) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void cleanUpPreferences()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null) {
      localPreferenceScreen.removeAll();
    }
    this.mProfiles.clear();
  }
  
  private ArrayList<AccountPreference> getAccountTypePreferences(AuthenticatorHelper paramAuthenticatorHelper, UserHandle paramUserHandle)
  {
    String[] arrayOfString = paramAuthenticatorHelper.getEnabledAccountTypes();
    ArrayList localArrayList = new ArrayList(arrayOfString.length);
    int j = 0;
    if (j < arrayOfString.length)
    {
      String str1 = arrayOfString[j];
      if (!Utils.showAccount(getActivity(), str1)) {}
      CharSequence localCharSequence;
      do
      {
        do
        {
          j += 1;
          break;
        } while (!accountTypeHasAnyRequestedAuthorities(paramAuthenticatorHelper, str1));
        localCharSequence = paramAuthenticatorHelper.getLabelForType(getActivity(), str1);
      } while (localCharSequence == null);
      String str2 = paramAuthenticatorHelper.getPackageForType(str1);
      int k = paramAuthenticatorHelper.getLabelIdForType(str1);
      Object localObject = AccountManager.get(getActivity()).getAccountsByTypeAsUser(str1, paramUserHandle);
      int i;
      if (localObject.length == 1) {
        if (paramAuthenticatorHelper.hasAccountPreferences(str1))
        {
          i = 0;
          label133:
          if (i == 0) {
            break label229;
          }
          Bundle localBundle = new Bundle();
          localBundle.putParcelable("account", localObject[0]);
          localBundle.putParcelable("android.intent.extra.USER", paramUserHandle);
          localArrayList.add(new AccountPreference(getPrefContext(), localCharSequence, str2, k, AccountSyncSettings.class.getName(), localBundle, paramAuthenticatorHelper.getDrawableForType(getActivity(), str1)));
        }
      }
      for (;;)
      {
        paramAuthenticatorHelper.preloadDrawableForType(getActivity(), str1);
        break;
        i = 1;
        break label133;
        i = 0;
        break label133;
        label229:
        localObject = new Bundle();
        ((Bundle)localObject).putString("account_type", str1);
        ((Bundle)localObject).putString("account_label", localCharSequence.toString());
        ((Bundle)localObject).putParcelable("android.intent.extra.USER", paramUserHandle);
        localArrayList.add(new AccountPreference(getPrefContext(), localCharSequence, str2, k, ManageAccountsSettings.class.getName(), (Bundle)localObject, paramAuthenticatorHelper.getDrawableForType(getActivity(), str1)));
      }
    }
    Collections.sort(localArrayList, new Comparator()
    {
      public int compare(AccountSettings.AccountPreference paramAnonymousAccountPreference1, AccountSettings.AccountPreference paramAnonymousAccountPreference2)
      {
        return AccountSettings.AccountPreference.-get0(paramAnonymousAccountPreference1).toString().compareTo(AccountSettings.AccountPreference.-get0(paramAnonymousAccountPreference2).toString());
      }
    });
    return localArrayList;
  }
  
  private String getWorkGroupSummary(Context paramContext, UserInfo paramUserInfo)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    paramContext = Utils.getAdminApplicationInfo(paramContext, paramUserInfo.id);
    if (paramContext == null) {
      return null;
    }
    return getString(2131693344, new Object[] { localPackageManager.getApplicationLabel(paramContext) });
  }
  
  private boolean isMultiAppEnable(List<UserInfo> paramList)
  {
    boolean bool2 = false;
    paramList = paramList.iterator();
    do
    {
      bool1 = bool2;
      if (!paramList.hasNext()) {
        break;
      }
    } while (((UserInfo)paramList.next()).id != 999);
    boolean bool1 = true;
    return bool1;
  }
  
  private void listenToAccountUpdates()
  {
    int j = this.mProfiles.size();
    int i = 0;
    while (i < j)
    {
      AuthenticatorHelper localAuthenticatorHelper = ((ProfileData)this.mProfiles.valueAt(i)).authenticatorHelper;
      if (localAuthenticatorHelper != null) {
        localAuthenticatorHelper.listenToAccountUpdates();
      }
      i += 1;
    }
  }
  
  private DimmableIconPreference newAddAccountPreference(Context paramContext)
  {
    paramContext = new DimmableIconPreference(getPrefContext());
    paramContext.setTitle(2131692699);
    paramContext.setIcon(2130837998);
    paramContext.setOnPreferenceClickListener(this);
    paramContext.setOrder(1000);
    return paramContext;
  }
  
  private Preference newAddOneplusAccountPreference(Context paramContext)
  {
    paramContext = new Preference(paramContext);
    paramContext.setTitle(2131690258);
    paramContext.setIcon(2130838209);
    paramContext.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        if (OPUtils.isAppExist(AccountSettings.this.getActivity(), "com.oneplus.account"))
        {
          paramAnonymousPreference = AccountManager.get(AccountSettings.this.getActivity());
          Bundle localBundle = new Bundle();
          localBundle.putString("come_from", "from_settings");
          paramAnonymousPreference.addAccount("com.oneplus.account", "", null, localBundle, AccountSettings.this.getActivity(), null, null);
        }
        return true;
      }
    });
    return paramContext;
  }
  
  private Preference newManagedProfileSettings()
  {
    Preference localPreference = new Preference(getPrefContext());
    localPreference.setTitle(2131693741);
    localPreference.setIcon(2130838092);
    localPreference.setOnPreferenceClickListener(this);
    localPreference.setOrder(1001);
    return localPreference;
  }
  
  private Preference newRemoveWorkProfilePreference(Context paramContext)
  {
    paramContext = new Preference(getPrefContext());
    paramContext.setTitle(2131692703);
    paramContext.setIcon(2130838003);
    paramContext.setOnPreferenceClickListener(this);
    paramContext.setOrder(1002);
    return paramContext;
  }
  
  private void stopListeningToAccountUpdates()
  {
    int j = this.mProfiles.size();
    int i = 0;
    while (i < j)
    {
      AuthenticatorHelper localAuthenticatorHelper = ((ProfileData)this.mProfiles.valueAt(i)).authenticatorHelper;
      if (localAuthenticatorHelper != null) {
        localAuthenticatorHelper.stopListeningToAccountUpdates();
      }
      i += 1;
    }
  }
  
  private void updateAccountTypes(ProfileData paramProfileData)
  {
    paramProfileData.preferenceGroup.removeAll();
    Object localObject = new OPPreferenceHeaderMargin(SettingsBaseApplication.mApplication);
    ((OPPreferenceHeaderMargin)localObject).setOrder(-1);
    paramProfileData.preferenceGroup.addPreference((Preference)localObject);
    if (paramProfileData.userInfo.isEnabled())
    {
      localObject = getAccountTypePreferences(paramProfileData.authenticatorHelper, paramProfileData.userInfo.getUserHandle());
      int j = ((ArrayList)localObject).size();
      int i = 0;
      while (i < j)
      {
        paramProfileData.preferenceGroup.addPreference((Preference)((ArrayList)localObject).get(i));
        i += 1;
      }
      if (paramProfileData.addAccountPreference != null) {
        paramProfileData.preferenceGroup.addPreference(paramProfileData.addAccountPreference);
      }
    }
    for (;;)
    {
      if (paramProfileData.removeWorkProfilePreference != null) {
        paramProfileData.preferenceGroup.addPreference(paramProfileData.removeWorkProfilePreference);
      }
      if (paramProfileData.managedProfilePreference != null) {
        paramProfileData.preferenceGroup.addPreference(paramProfileData.managedProfilePreference);
      }
      return;
      this.mProfileNotAvailablePreference.setEnabled(false);
      this.mProfileNotAvailablePreference.setIcon(2130837908);
      this.mProfileNotAvailablePreference.setTitle(null);
      this.mProfileNotAvailablePreference.setSummary(2131692700);
      paramProfileData.preferenceGroup.addPreference(this.mProfileNotAvailablePreference);
    }
  }
  
  private void updateProfileUi(UserInfo paramUserInfo, boolean paramBoolean, PreferenceScreen paramPreferenceScreen)
  {
    Activity localActivity = getActivity();
    ProfileData localProfileData = new ProfileData(null);
    localProfileData.userInfo = paramUserInfo;
    if (paramBoolean)
    {
      localProfileData.preferenceGroup = new AccessiblePreferenceCategory(getPrefContext());
      if (paramUserInfo.isManagedProfile())
      {
        localProfileData.preferenceGroup.setLayoutResource(2130969126);
        localProfileData.preferenceGroup.setTitle(2131689625);
        String str = getWorkGroupSummary(localActivity, paramUserInfo);
        localProfileData.preferenceGroup.setSummary(str);
        ((AccessiblePreferenceCategory)localProfileData.preferenceGroup).setContentDescription(getString(2131691590, new Object[] { str }));
        localProfileData.removeWorkProfilePreference = newRemoveWorkProfilePreference(localActivity);
        localProfileData.managedProfilePreference = newManagedProfileSettings();
        paramPreferenceScreen.addPreference(localProfileData.preferenceGroup);
      }
    }
    for (;;)
    {
      if (paramUserInfo.isEnabled())
      {
        localProfileData.authenticatorHelper = new AuthenticatorHelper(localActivity, paramUserInfo.getUserHandle(), this);
        if (!RestrictedLockUtils.hasBaseUserRestriction(localActivity, "no_modify_accounts", paramUserInfo.id))
        {
          localProfileData.addAccountPreference = newAddAccountPreference(localActivity);
          localProfileData.addAccountPreference.checkRestrictionAndSetDisabled("no_modify_accounts", paramUserInfo.id);
        }
      }
      this.mProfiles.put(paramUserInfo.id, localProfileData);
      Index.getInstance(getActivity()).updateFromClassNameResource(AccountSettings.class.getName(), true, true);
      return;
      localProfileData.preferenceGroup.setTitle(2131689624);
      ((AccessiblePreferenceCategory)localProfileData.preferenceGroup).setContentDescription(getString(2131691591));
      break;
      localProfileData.preferenceGroup = paramPreferenceScreen;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 8;
  }
  
  public void onAccountsUpdate(UserHandle paramUserHandle)
  {
    ProfileData localProfileData = (ProfileData)this.mProfiles.get(paramUserHandle.getIdentifier());
    if (localProfileData != null)
    {
      updateAccountTypes(localProfileData);
      return;
    }
    Log.w("AccountSettings", "Missing Settings screen for: " + paramUserHandle.getIdentifier());
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUm = ((UserManager)getSystemService("user"));
    this.mProfileNotAvailablePreference = new Preference(getPrefContext());
    this.mAuthorities = getActivity().getIntent().getStringArrayExtra("authorities");
    if (this.mAuthorities != null) {
      this.mAuthoritiesCount = this.mAuthorities.length;
    }
    setHasOptionsMenu(true);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenuInflater.inflate(2132017152, paramMenu);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public void onPause()
  {
    super.onPause();
    stopListeningToAccountUpdates();
    this.mManagedProfileBroadcastReceiver.unregister(getActivity());
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    int j = this.mProfiles.size();
    final int i = 0;
    while (i < j)
    {
      ProfileData localProfileData = (ProfileData)this.mProfiles.valueAt(i);
      if (paramPreference == localProfileData.addAccountPreference)
      {
        paramPreference = new Intent("android.settings.ADD_ACCOUNT_SETTINGS");
        paramPreference.putExtra("android.intent.extra.USER", localProfileData.userInfo.getUserHandle());
        paramPreference.putExtra("authorities", this.mAuthorities);
        startActivity(paramPreference);
        return true;
      }
      if (paramPreference == localProfileData.removeWorkProfilePreference)
      {
        i = localProfileData.userInfo.id;
        UserDialogs.createRemoveDialog(getActivity(), i, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            AccountSettings.-get0(AccountSettings.this).removeUser(i);
          }
        }).show();
        return true;
      }
      if (paramPreference == localProfileData.managedProfilePreference)
      {
        paramPreference = new Bundle();
        paramPreference.putParcelable("android.intent.extra.USER", localProfileData.userInfo.getUserHandle());
        ((SettingsActivity)getActivity()).startPreferencePanel(ManagedProfileSettings.class.getName(), paramPreference, 2131693741, null, null, 0);
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    UserHandle localUserHandle1 = Process.myUserHandle();
    if (this.mProfiles.size() == 1)
    {
      paramMenu.findItem(2131362838).setVisible(true).setOnMenuItemClickListener(new MasterSyncStateClickListener(localUserHandle1)).setChecked(ContentResolver.getMasterSyncAutomaticallyAsUser(localUserHandle1.getIdentifier()));
      paramMenu.findItem(2131362839).setVisible(false);
      paramMenu.findItem(2131362840).setVisible(false);
      return;
    }
    if (this.mProfiles.size() > 1)
    {
      UserHandle localUserHandle2 = ((ProfileData)this.mProfiles.valueAt(1)).userInfo.getUserHandle();
      paramMenu.findItem(2131362839).setVisible(true).setOnMenuItemClickListener(new MasterSyncStateClickListener(localUserHandle1)).setChecked(ContentResolver.getMasterSyncAutomaticallyAsUser(localUserHandle1.getIdentifier()));
      paramMenu.findItem(2131362840).setVisible(true).setOnMenuItemClickListener(new MasterSyncStateClickListener(localUserHandle2)).setChecked(ContentResolver.getMasterSyncAutomaticallyAsUser(localUserHandle2.getIdentifier()));
      paramMenu.findItem(2131362838).setVisible(false);
      return;
    }
    Log.w("AccountSettings", "Method onPrepareOptionsMenu called before mProfiles was initialized");
  }
  
  public void onResume()
  {
    super.onResume();
    cleanUpPreferences();
    updateUi();
    this.mManagedProfileBroadcastReceiver.register(getActivity());
    listenToAccountUpdates();
  }
  
  void updateUi()
  {
    addPreferencesFromResource(2131230725);
    if (Utils.isManagedProfile(this.mUm))
    {
      Log.e("AccountSettings", "We should not be showing settings for a managed profile");
      finish();
      return;
    }
    PreferenceScreen localPreferenceScreen = (PreferenceScreen)findPreference("account");
    int j;
    int i;
    label79:
    Object localObject;
    if (this.mUm.isLinkedUser())
    {
      updateProfileUi(this.mUm.getUserInfo(UserHandle.myUserId()), false, localPreferenceScreen);
      j = this.mProfiles.size();
      i = 0;
      if (i >= j) {
        break label250;
      }
      localObject = (ProfileData)this.mProfiles.valueAt(i);
      if (((ProfileData)localObject).userInfo.id != 999) {
        break label217;
      }
    }
    for (;;)
    {
      i += 1;
      break label79;
      localObject = this.mUm.getProfiles(UserHandle.myUserId());
      j = ((List)localObject).size();
      label152:
      boolean bool;
      label156:
      UserInfo localUserInfo;
      if (isMultiAppEnable((List)localObject)) {
        if (j > 2)
        {
          bool = true;
          i = 0;
          if (i < j)
          {
            localUserInfo = (UserInfo)((List)localObject).get(i);
            if (localUserInfo.id != 999) {
              break label205;
            }
          }
        }
      }
      for (;;)
      {
        i += 1;
        break label156;
        break;
        do
        {
          bool = false;
          break;
        } while (j <= 1);
        break label152;
        label205:
        updateProfileUi(localUserInfo, bool, localPreferenceScreen);
      }
      label217:
      if (!((ProfileData)localObject).preferenceGroup.equals(localPreferenceScreen)) {
        localPreferenceScreen.addPreference(((ProfileData)localObject).preferenceGroup);
      }
      updateAccountTypes((ProfileData)localObject);
    }
    label250:
    if (OPUtils.isAppExist(getActivity(), "com.oneplus.account")) {
      localPreferenceScreen.addPreference(newAddOneplusAccountPreference(getActivity()));
    }
  }
  
  private class AccountPreference
    extends Preference
    implements Preference.OnPreferenceClickListener
  {
    private final String mFragment;
    private final Bundle mFragmentArguments;
    private final CharSequence mTitle;
    private final int mTitleResId;
    private final String mTitleResPackageName;
    
    public AccountPreference(Context paramContext, CharSequence paramCharSequence, String paramString1, int paramInt, String paramString2, Bundle paramBundle, Drawable paramDrawable)
    {
      super();
      this.mTitle = paramCharSequence;
      this.mTitleResPackageName = paramString1;
      this.mTitleResId = paramInt;
      this.mFragment = paramString2;
      this.mFragmentArguments = paramBundle;
      setWidgetLayoutResource(2130968604);
      setLayoutResource(2130968837);
      setTitle(paramCharSequence);
      setIcon(paramDrawable);
      setOnPreferenceClickListener(this);
    }
    
    public boolean onPreferenceClick(Preference paramPreference)
    {
      if (this.mFragment != null)
      {
        Utils.startWithFragment(getContext(), this.mFragment, this.mFragmentArguments, null, 0, this.mTitleResPackageName, this.mTitleResId, null);
        return true;
      }
      return false;
    }
  }
  
  public static class ConfirmAutoSyncChangeFragment
    extends DialogFragment
  {
    private static final String SAVE_ENABLING = "enabling";
    private static final String SAVE_USER_HANDLE = "userHandle";
    private boolean mEnabling;
    private UserHandle mUserHandle;
    
    public static void show(AccountSettings paramAccountSettings, boolean paramBoolean, UserHandle paramUserHandle)
    {
      if (!paramAccountSettings.isAdded()) {
        return;
      }
      ConfirmAutoSyncChangeFragment localConfirmAutoSyncChangeFragment = new ConfirmAutoSyncChangeFragment();
      localConfirmAutoSyncChangeFragment.mEnabling = paramBoolean;
      localConfirmAutoSyncChangeFragment.mUserHandle = paramUserHandle;
      localConfirmAutoSyncChangeFragment.setTargetFragment(paramAccountSettings, 0);
      localConfirmAutoSyncChangeFragment.show(paramAccountSettings.getFragmentManager(), "confirmAutoSyncChange");
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Activity localActivity = getActivity();
      if (paramBundle != null)
      {
        this.mEnabling = paramBundle.getBoolean("enabling");
        this.mUserHandle = ((UserHandle)paramBundle.getParcelable("userHandle"));
      }
      paramBundle = new AlertDialog.Builder(localActivity);
      if (!this.mEnabling)
      {
        paramBundle.setTitle(2131692809);
        paramBundle.setMessage(2131692810);
      }
      for (;;)
      {
        paramBundle.setPositiveButton(17039370, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            ContentResolver.setMasterSyncAutomaticallyAsUser(AccountSettings.ConfirmAutoSyncChangeFragment.-get0(AccountSettings.ConfirmAutoSyncChangeFragment.this), AccountSettings.ConfirmAutoSyncChangeFragment.-get1(AccountSettings.ConfirmAutoSyncChangeFragment.this).getIdentifier());
          }
        });
        paramBundle.setNegativeButton(17039360, null);
        return paramBundle.create();
        paramBundle.setTitle(2131692807);
        paramBundle.setMessage(2131692808);
      }
    }
    
    public void onSaveInstanceState(Bundle paramBundle)
    {
      super.onSaveInstanceState(paramBundle);
      paramBundle.putBoolean("enabling", this.mEnabling);
      paramBundle.putParcelable("userHandle", this.mUserHandle);
    }
  }
  
  private class ManagedProfileBroadcastReceiver
    extends BroadcastReceiver
  {
    private boolean listeningToManagedProfileEvents;
    
    private ManagedProfileBroadcastReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      Log.v("AccountSettings", "Received broadcast: " + paramContext);
      if ((paramContext.equals("android.intent.action.MANAGED_PROFILE_REMOVED")) || (paramContext.equals("android.intent.action.MANAGED_PROFILE_ADDED")))
      {
        AccountSettings.-wrap2(AccountSettings.this);
        AccountSettings.-wrap0(AccountSettings.this);
        AccountSettings.this.updateUi();
        AccountSettings.-wrap1(AccountSettings.this);
        AccountSettings.this.getActivity().invalidateOptionsMenu();
        return;
      }
      Log.w("AccountSettings", "Cannot handle received broadcast: " + paramIntent.getAction());
    }
    
    public void register(Context paramContext)
    {
      if (!this.listeningToManagedProfileEvents)
      {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
        localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
        paramContext.registerReceiver(this, localIntentFilter);
        this.listeningToManagedProfileEvents = true;
      }
    }
    
    public void unregister(Context paramContext)
    {
      if (this.listeningToManagedProfileEvents)
      {
        paramContext.unregisterReceiver(this);
        this.listeningToManagedProfileEvents = false;
      }
    }
  }
  
  private class MasterSyncStateClickListener
    implements MenuItem.OnMenuItemClickListener
  {
    private final UserHandle mUserHandle;
    
    public MasterSyncStateClickListener(UserHandle paramUserHandle)
    {
      this.mUserHandle = paramUserHandle;
    }
    
    public boolean onMenuItemClick(MenuItem paramMenuItem)
    {
      if (ActivityManager.isUserAMonkey())
      {
        Log.d("AccountSettings", "ignoring monkey's attempt to flip sync state");
        return true;
      }
      AccountSettings localAccountSettings = AccountSettings.this;
      if (paramMenuItem.isChecked()) {}
      for (boolean bool = false;; bool = true)
      {
        AccountSettings.ConfirmAutoSyncChangeFragment.show(localAccountSettings, bool, this.mUserHandle);
        return true;
      }
    }
  }
  
  private static class ProfileData
  {
    public DimmableIconPreference addAccountPreference;
    public AuthenticatorHelper authenticatorHelper;
    public Preference managedProfilePreference;
    public PreferenceGroup preferenceGroup;
    public Preference removeWorkProfilePreference;
    public UserInfo userInfo;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accounts\AccountSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */