package com.android.settings.users;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionEntry;
import android.content.RestrictionsManager;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.users.AppRestrictionsHelper;
import com.android.settingslib.users.AppRestrictionsHelper.OnDisableUiForPackageListener;
import com.android.settingslib.users.AppRestrictionsHelper.SelectableAppInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class AppRestrictionsFragment
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, View.OnClickListener, Preference.OnPreferenceClickListener, AppRestrictionsHelper.OnDisableUiForPackageListener
{
  private static final int CUSTOM_REQUEST_CODE_START = 1000;
  private static final boolean DEBUG = false;
  private static final String DELIMITER = ";";
  public static final String EXTRA_NEW_USER = "new_user";
  public static final String EXTRA_USER_ID = "user_id";
  private static final int MAX_APP_RESTRICTIONS = 100;
  private static final String PKG_PREFIX = "pkg_";
  private static final String TAG = AppRestrictionsFragment.class.getSimpleName();
  private PreferenceGroup mAppList;
  private boolean mAppListChanged;
  private AsyncTask mAppLoadingTask;
  private int mCustomRequestCode = 1000;
  private HashMap<Integer, AppRestrictionsPreference> mCustomRequestMap = new HashMap();
  private boolean mFirstTime = true;
  private AppRestrictionsHelper mHelper;
  protected IPackageManager mIPm;
  private boolean mNewUser;
  protected PackageManager mPackageManager;
  private BroadcastReceiver mPackageObserver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      AppRestrictionsFragment.-wrap1(AppRestrictionsFragment.this, paramAnonymousIntent);
    }
  };
  protected boolean mRestrictedProfile;
  private PackageInfo mSysPackageInfo;
  protected UserHandle mUser;
  private BroadcastReceiver mUserBackgrounding = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (AppRestrictionsFragment.-get0(AppRestrictionsFragment.this)) {
        AppRestrictionsFragment.-get1(AppRestrictionsFragment.this).applyUserAppsStates(AppRestrictionsFragment.this);
      }
    }
  };
  protected UserManager mUserManager;
  
  private void addLocationAppRestrictionsPreference(AppRestrictionsHelper.SelectableAppInfo paramSelectableAppInfo, AppRestrictionsPreference paramAppRestrictionsPreference)
  {
    paramSelectableAppInfo = paramSelectableAppInfo.packageName;
    paramAppRestrictionsPreference.setIcon(2130838053);
    paramAppRestrictionsPreference.setKey(getKeyForPackage(paramSelectableAppInfo));
    paramSelectableAppInfo = RestrictionUtils.getRestrictions(getActivity(), this.mUser);
    RestrictionEntry localRestrictionEntry = (RestrictionEntry)paramSelectableAppInfo.get(0);
    paramAppRestrictionsPreference.setTitle(localRestrictionEntry.getTitle());
    paramAppRestrictionsPreference.setRestrictions(paramSelectableAppInfo);
    paramAppRestrictionsPreference.setSummary(localRestrictionEntry.getDescription());
    paramAppRestrictionsPreference.setChecked(localRestrictionEntry.getSelectedState());
    paramAppRestrictionsPreference.setPersistent(false);
    paramAppRestrictionsPreference.setOnPreferenceClickListener(this);
    paramAppRestrictionsPreference.setOrder(100);
    this.mAppList.addPreference(paramAppRestrictionsPreference);
  }
  
  private String findInArray(String[] paramArrayOfString1, String[] paramArrayOfString2, String paramString)
  {
    int i = 0;
    while (i < paramArrayOfString2.length)
    {
      if (paramArrayOfString2[i].equals(paramString)) {
        return paramArrayOfString1[i];
      }
      i += 1;
    }
    return paramString;
  }
  
  private int generateCustomActivityRequestCode(AppRestrictionsPreference paramAppRestrictionsPreference)
  {
    this.mCustomRequestCode += 1;
    this.mCustomRequestMap.put(Integer.valueOf(this.mCustomRequestCode), paramAppRestrictionsPreference);
    return this.mCustomRequestCode;
  }
  
  private String getKeyForPackage(String paramString)
  {
    return "pkg_" + paramString;
  }
  
  private String getPackageSummary(PackageInfo paramPackageInfo, AppRestrictionsHelper.SelectableAppInfo paramSelectableAppInfo)
  {
    if (paramSelectableAppInfo.masterEntry != null)
    {
      if ((this.mRestrictedProfile) && (paramPackageInfo.restrictedAccountType != null)) {
        return getString(2131693060, new Object[] { paramSelectableAppInfo.masterEntry.activityName });
      }
      return getString(2131693058, new Object[] { paramSelectableAppInfo.masterEntry.activityName });
    }
    if (paramPackageInfo.restrictedAccountType != null) {
      return getString(2131693059);
    }
    return null;
  }
  
  private boolean isAppEnabledForUser(PackageInfo paramPackageInfo)
  {
    boolean bool2 = false;
    if (paramPackageInfo == null) {
      return false;
    }
    int i = paramPackageInfo.applicationInfo.flags;
    int j = paramPackageInfo.applicationInfo.privateFlags;
    boolean bool1 = bool2;
    if ((0x800000 & i) != 0)
    {
      bool1 = bool2;
      if ((j & 0x1) == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static boolean isAppUnsupportedInRestrictedProfile(PackageInfo paramPackageInfo)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramPackageInfo.requiredAccountType != null)
    {
      bool1 = bool2;
      if (paramPackageInfo.restrictedAccountType == null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isPlatformSigned(PackageInfo paramPackageInfo)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramPackageInfo != null)
    {
      bool1 = bool2;
      if (paramPackageInfo.signatures != null) {
        bool1 = this.mSysPackageInfo.signatures[0].equals(paramPackageInfo.signatures[0]);
      }
    }
    return bool1;
  }
  
  private void onAppSettingsIconClicked(AppRestrictionsPreference paramAppRestrictionsPreference)
  {
    boolean bool = true;
    if (paramAppRestrictionsPreference.getKey().startsWith("pkg_"))
    {
      if (!paramAppRestrictionsPreference.isPanelOpen()) {
        break label41;
      }
      removeRestrictionsForApp(paramAppRestrictionsPreference);
    }
    for (;;)
    {
      if (paramAppRestrictionsPreference.isPanelOpen()) {
        bool = false;
      }
      paramAppRestrictionsPreference.setPanelOpen(bool);
      return;
      label41:
      requestRestrictionsForApp(paramAppRestrictionsPreference.getKey().substring("pkg_".length()), paramAppRestrictionsPreference, true);
    }
  }
  
  private void onPackageChanged(Intent paramIntent)
  {
    String str = paramIntent.getAction();
    paramIntent = (AppRestrictionsPreference)findPreference(getKeyForPackage(paramIntent.getData().getSchemeSpecificPart()));
    if (paramIntent == null) {
      return;
    }
    if (((!"android.intent.action.PACKAGE_ADDED".equals(str)) || (!paramIntent.isChecked())) && ((!"android.intent.action.PACKAGE_REMOVED".equals(str)) || (paramIntent.isChecked()))) {
      return;
    }
    paramIntent.setEnabled(true);
  }
  
  private void onRestrictionsReceived(AppRestrictionsPreference paramAppRestrictionsPreference, ArrayList<RestrictionEntry> paramArrayList)
  {
    removeRestrictionsForApp(paramAppRestrictionsPreference);
    int i = 1;
    Iterator localIterator = paramArrayList.iterator();
    label449:
    while (localIterator.hasNext())
    {
      RestrictionEntry localRestrictionEntry = (RestrictionEntry)localIterator.next();
      Object localObject1 = null;
      switch (localRestrictionEntry.getType())
      {
      }
      for (;;)
      {
        if (localObject1 == null) {
          break label449;
        }
        ((Preference)localObject1).setPersistent(false);
        ((Preference)localObject1).setOrder(paramAppRestrictionsPreference.getOrder() + i);
        ((Preference)localObject1).setKey(paramAppRestrictionsPreference.getKey().substring("pkg_".length()) + ";" + localRestrictionEntry.getKey());
        this.mAppList.addPreference((Preference)localObject1);
        ((Preference)localObject1).setOnPreferenceChangeListener(this);
        ((Preference)localObject1).setIcon(2130837908);
        AppRestrictionsPreference.-get2(paramAppRestrictionsPreference).add(localObject1);
        i += 1;
        break;
        localObject1 = new SwitchPreference(getPrefContext());
        ((Preference)localObject1).setTitle(localRestrictionEntry.getTitle());
        ((Preference)localObject1).setSummary(localRestrictionEntry.getDescription());
        ((SwitchPreference)localObject1).setChecked(localRestrictionEntry.getSelectedState());
        continue;
        Object localObject2 = new ListPreference(getPrefContext());
        ((Preference)localObject2).setTitle(localRestrictionEntry.getTitle());
        String str = localRestrictionEntry.getSelectedString();
        localObject1 = str;
        if (str == null) {
          localObject1 = localRestrictionEntry.getDescription();
        }
        ((Preference)localObject2).setSummary(findInArray(localRestrictionEntry.getChoiceEntries(), localRestrictionEntry.getChoiceValues(), (String)localObject1));
        ((ListPreference)localObject2).setEntryValues(localRestrictionEntry.getChoiceValues());
        ((ListPreference)localObject2).setEntries(localRestrictionEntry.getChoiceEntries());
        ((ListPreference)localObject2).setValue((String)localObject1);
        ((ListPreference)localObject2).setDialogTitle(localRestrictionEntry.getTitle());
        localObject1 = localObject2;
        continue;
        localObject1 = new MultiSelectListPreference(getPrefContext());
        ((Preference)localObject1).setTitle(localRestrictionEntry.getTitle());
        ((MultiSelectListPreference)localObject1).setEntryValues(localRestrictionEntry.getChoiceValues());
        ((MultiSelectListPreference)localObject1).setEntries(localRestrictionEntry.getChoiceEntries());
        localObject2 = new HashSet();
        Collections.addAll((Collection)localObject2, localRestrictionEntry.getAllSelectedStrings());
        ((MultiSelectListPreference)localObject1).setValues((Set)localObject2);
        ((MultiSelectListPreference)localObject1).setDialogTitle(localRestrictionEntry.getTitle());
      }
    }
    paramAppRestrictionsPreference.setRestrictions(paramArrayList);
    if ((i == 1) && (paramAppRestrictionsPreference.isImmutable()) && (paramAppRestrictionsPreference.isChecked())) {
      this.mAppList.removePreference(paramAppRestrictionsPreference);
    }
  }
  
  private void populateApps()
  {
    Activity localActivity = getActivity();
    if (localActivity == null) {
      return;
    }
    Object localObject1 = this.mPackageManager;
    IPackageManager localIPackageManager = this.mIPm;
    int i = this.mUser.getIdentifier();
    if (Utils.getExistingUser(this.mUserManager, this.mUser) == null) {
      return;
    }
    this.mAppList.removeAll();
    List localList = ((PackageManager)localObject1).queryBroadcastReceivers(new Intent("android.intent.action.GET_RESTRICTION_ENTRIES"), 0);
    Iterator localIterator = this.mHelper.getVisibleApps().iterator();
    boolean bool1;
    AppRestrictionsPreference localAppRestrictionsPreference;
    for (;;)
    {
      if (!localIterator.hasNext()) {
        break label487;
      }
      AppRestrictionsHelper.SelectableAppInfo localSelectableAppInfo = (AppRestrictionsHelper.SelectableAppInfo)localIterator.next();
      String str = localSelectableAppInfo.packageName;
      boolean bool2;
      if (str != null)
      {
        bool1 = str.equals(localActivity.getPackageName());
        localAppRestrictionsPreference = new AppRestrictionsPreference(getPrefContext(), this);
        bool2 = resolveInfoListHasPackage(localList, str);
        if (bool1)
        {
          addLocationAppRestrictionsPreference(localSelectableAppInfo, localAppRestrictionsPreference);
          this.mHelper.setPackageSelected(str, true);
        }
        else
        {
          localObject1 = null;
        }
      }
      try
      {
        localObject2 = localIPackageManager.getPackageInfo(str, 8256, i);
        localObject1 = localObject2;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Object localObject2;
          continue;
          Object localObject3 = null;
          continue;
          bool1 = false;
          continue;
          if ((!this.mNewUser) && (isAppEnabledForUser((PackageInfo)localObject1))) {
            localAppRestrictionsPreference.setChecked(true);
          }
        }
      }
      if ((localObject1 != null) && ((!this.mRestrictedProfile) || (!isAppUnsupportedInRestrictedProfile((PackageInfo)localObject1))))
      {
        if (localSelectableAppInfo.icon == null) {
          break;
        }
        localObject2 = localSelectableAppInfo.icon.mutate();
        localAppRestrictionsPreference.setIcon((Drawable)localObject2);
        localAppRestrictionsPreference.setChecked(false);
        localAppRestrictionsPreference.setTitle(localSelectableAppInfo.activityName);
        localAppRestrictionsPreference.setKey(getKeyForPackage(str));
        if ((!bool2) || (localSelectableAppInfo.masterEntry != null)) {
          break label457;
        }
        bool1 = true;
        AppRestrictionsPreference.-wrap0(localAppRestrictionsPreference, bool1);
        localAppRestrictionsPreference.setPersistent(false);
        localAppRestrictionsPreference.setOnPreferenceChangeListener(this);
        localAppRestrictionsPreference.setOnPreferenceClickListener(this);
        localAppRestrictionsPreference.setSummary(getPackageSummary((PackageInfo)localObject1, localSelectableAppInfo));
        if ((!((PackageInfo)localObject1).requiredForAllUsers) && (!isPlatformSigned((PackageInfo)localObject1))) {
          break label462;
        }
        localAppRestrictionsPreference.setChecked(true);
        localAppRestrictionsPreference.setImmutable(true);
        if (bool2)
        {
          if (localSelectableAppInfo.masterEntry == null) {
            requestRestrictionsForApp(str, localAppRestrictionsPreference, false);
          }
          if (localSelectableAppInfo.masterEntry != null)
          {
            localAppRestrictionsPreference.setImmutable(true);
            localAppRestrictionsPreference.setChecked(this.mHelper.isPackageSelected(str));
          }
          localAppRestrictionsPreference.setOrder((this.mAppList.getPreferenceCount() + 2) * 100);
          this.mHelper.setPackageSelected(str, localAppRestrictionsPreference.isChecked());
          this.mAppList.addPreference(localAppRestrictionsPreference);
        }
      }
    }
    label457:
    label462:
    label487:
    this.mAppListChanged = true;
    if ((this.mNewUser) && (this.mFirstTime))
    {
      this.mFirstTime = false;
      this.mHelper.applyUserAppsStates(this);
    }
  }
  
  private void removeRestrictionsForApp(AppRestrictionsPreference paramAppRestrictionsPreference)
  {
    Iterator localIterator = AppRestrictionsPreference.-get2(paramAppRestrictionsPreference).iterator();
    while (localIterator.hasNext())
    {
      Preference localPreference = (Preference)localIterator.next();
      this.mAppList.removePreference(localPreference);
    }
    AppRestrictionsPreference.-get2(paramAppRestrictionsPreference).clear();
  }
  
  private void requestRestrictionsForApp(String paramString, AppRestrictionsPreference paramAppRestrictionsPreference, boolean paramBoolean)
  {
    Bundle localBundle = this.mUserManager.getApplicationRestrictions(paramString, this.mUser);
    Intent localIntent = new Intent("android.intent.action.GET_RESTRICTION_ENTRIES");
    localIntent.setPackage(paramString);
    localIntent.putExtra("android.intent.extra.restrictions_bundle", localBundle);
    localIntent.addFlags(32);
    getActivity().sendOrderedBroadcast(localIntent, null, new RestrictionsResultReceiver(paramString, paramAppRestrictionsPreference, paramBoolean), null, -1, null, null);
  }
  
  private boolean resolveInfoListHasPackage(List<ResolveInfo> paramList, String paramString)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      if (((ResolveInfo)paramList.next()).activityInfo.packageName.equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  private void updateAllEntries(String paramString, boolean paramBoolean)
  {
    int i = 0;
    while (i < this.mAppList.getPreferenceCount())
    {
      Preference localPreference = this.mAppList.getPreference(i);
      if (((localPreference instanceof AppRestrictionsPreference)) && (paramString.equals(localPreference.getKey()))) {
        ((AppRestrictionsPreference)localPreference).setChecked(paramBoolean);
      }
      i += 1;
    }
  }
  
  protected PreferenceGroup getAppPreferenceGroup()
  {
    return getPreferenceScreen();
  }
  
  protected int getMetricsCategory()
  {
    return 97;
  }
  
  protected void init(Bundle paramBundle)
  {
    if (paramBundle != null) {
      this.mUser = new UserHandle(paramBundle.getInt("user_id"));
    }
    for (;;)
    {
      if (this.mUser == null) {
        this.mUser = Process.myUserHandle();
      }
      this.mHelper = new AppRestrictionsHelper(getContext(), this.mUser);
      this.mPackageManager = getActivity().getPackageManager();
      this.mIPm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
      this.mUserManager = ((UserManager)getActivity().getSystemService("user"));
      this.mRestrictedProfile = this.mUserManager.getUserInfo(this.mUser.getIdentifier()).isRestricted();
      try
      {
        this.mSysPackageInfo = this.mPackageManager.getPackageInfo("android", 64);
        addPreferencesFromResource(2131230735);
        this.mAppList = getAppPreferenceGroup();
        this.mAppList.setOrderingAsAdded(false);
        return;
        paramBundle = getArguments();
        if (paramBundle == null) {
          continue;
        }
        if (paramBundle.containsKey("user_id")) {
          this.mUser = new UserHandle(paramBundle.getInt("user_id"));
        }
        this.mNewUser = paramBundle.getBoolean("new_user", false);
      }
      catch (PackageManager.NameNotFoundException paramBundle)
      {
        for (;;) {}
      }
    }
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    AppRestrictionsPreference localAppRestrictionsPreference = (AppRestrictionsPreference)this.mCustomRequestMap.get(Integer.valueOf(paramInt1));
    if (localAppRestrictionsPreference == null)
    {
      Log.w(TAG, "Unknown requestCode " + paramInt1);
      return;
    }
    String str;
    if (paramInt2 == -1)
    {
      str = localAppRestrictionsPreference.getKey().substring("pkg_".length());
      ArrayList localArrayList = paramIntent.getParcelableArrayListExtra("android.intent.extra.restrictions_list");
      paramIntent = paramIntent.getBundleExtra("android.intent.extra.restrictions_bundle");
      if (localArrayList == null) {
        break label136;
      }
      localAppRestrictionsPreference.setRestrictions(localArrayList);
      this.mUserManager.setApplicationRestrictions(str, RestrictionsManager.convertRestrictionsToBundle(localArrayList), this.mUser);
    }
    for (;;)
    {
      this.mCustomRequestMap.remove(Integer.valueOf(paramInt1));
      return;
      label136:
      if (paramIntent != null) {
        this.mUserManager.setApplicationRestrictions(str, paramIntent, this.mUser);
      }
    }
  }
  
  public void onClick(View paramView)
  {
    AppRestrictionsPreference localAppRestrictionsPreference;
    if ((paramView.getTag() instanceof AppRestrictionsPreference))
    {
      localAppRestrictionsPreference = (AppRestrictionsPreference)paramView.getTag();
      if (paramView.getId() != 2131362411) {
        break label34;
      }
      onAppSettingsIconClicked(localAppRestrictionsPreference);
    }
    label34:
    while (localAppRestrictionsPreference.isImmutable()) {
      return;
    }
    if (localAppRestrictionsPreference.isChecked()) {}
    for (boolean bool = false;; bool = true)
    {
      localAppRestrictionsPreference.setChecked(bool);
      paramView = localAppRestrictionsPreference.getKey().substring("pkg_".length());
      if (!paramView.equals(getActivity().getPackageName())) {
        break;
      }
      ((RestrictionEntry)AppRestrictionsPreference.-get3(localAppRestrictionsPreference).get(0)).setSelectedState(localAppRestrictionsPreference.isChecked());
      RestrictionUtils.setRestrictions(getActivity(), AppRestrictionsPreference.-get3(localAppRestrictionsPreference), this.mUser);
      return;
    }
    this.mHelper.setPackageSelected(paramView, localAppRestrictionsPreference.isChecked());
    if ((localAppRestrictionsPreference.isChecked()) && (AppRestrictionsPreference.-get0(localAppRestrictionsPreference)) && (AppRestrictionsPreference.-get3(localAppRestrictionsPreference) == null)) {
      requestRestrictionsForApp(paramView, localAppRestrictionsPreference, false);
    }
    this.mAppListChanged = true;
    if (!this.mRestrictedProfile) {
      this.mHelper.applyUserAppState(paramView, localAppRestrictionsPreference.isChecked(), this);
    }
    updateAllEntries(localAppRestrictionsPreference.getKey(), localAppRestrictionsPreference.isChecked());
  }
  
  public void onDisableUiForPackage(String paramString)
  {
    paramString = (AppRestrictionsPreference)findPreference(getKeyForPackage(paramString));
    if (paramString != null) {
      paramString.setEnabled(false);
    }
  }
  
  public void onPause()
  {
    super.onPause();
    this.mNewUser = false;
    getActivity().unregisterReceiver(this.mUserBackgrounding);
    getActivity().unregisterReceiver(this.mPackageObserver);
    if (this.mAppListChanged) {
      new AsyncTask()
      {
        protected Void doInBackground(Void... paramAnonymousVarArgs)
        {
          AppRestrictionsFragment.-get1(AppRestrictionsFragment.this).applyUserAppsStates(AppRestrictionsFragment.this);
          return null;
        }
      }.execute(new Void[0]);
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    String str1 = paramPreference.getKey();
    if ((str1 != null) && (str1.contains(";")))
    {
      Object localObject = new StringTokenizer(str1, ";");
      str1 = ((StringTokenizer)localObject).nextToken();
      String str2 = ((StringTokenizer)localObject).nextToken();
      localObject = ((AppRestrictionsPreference)this.mAppList.findPreference("pkg_" + str1)).getRestrictions();
      RestrictionEntry localRestrictionEntry;
      if (localObject != null)
      {
        Iterator localIterator = ((Iterable)localObject).iterator();
        while (localIterator.hasNext())
        {
          localRestrictionEntry = (RestrictionEntry)localIterator.next();
          if (localRestrictionEntry.getKey().equals(str2)) {
            switch (localRestrictionEntry.getType())
            {
            default: 
              break;
            case 1: 
              localRestrictionEntry.setSelectedState(((Boolean)paramObject).booleanValue());
            }
          }
        }
      }
      for (;;)
      {
        this.mUserManager.setApplicationRestrictions(str1, RestrictionsManager.convertRestrictionsToBundle((List)localObject), this.mUser);
        return true;
        paramPreference = (ListPreference)paramPreference;
        localRestrictionEntry.setSelectedString((String)paramObject);
        paramPreference.setSummary(findInArray(localRestrictionEntry.getChoiceEntries(), localRestrictionEntry.getChoiceValues(), (String)paramObject));
        continue;
        paramPreference = (Set)paramObject;
        paramObject = new String[paramPreference.size()];
        paramPreference.toArray((Object[])paramObject);
        localRestrictionEntry.setAllSelectedStrings((String[])paramObject);
      }
    }
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    boolean bool = false;
    if (paramPreference.getKey().startsWith("pkg_"))
    {
      paramPreference = (AppRestrictionsPreference)paramPreference;
      String str;
      if (!paramPreference.isImmutable())
      {
        str = paramPreference.getKey().substring("pkg_".length());
        if (!paramPreference.isChecked()) {
          break label86;
        }
      }
      for (;;)
      {
        paramPreference.setChecked(bool);
        this.mHelper.setPackageSelected(str, bool);
        updateAllEntries(paramPreference.getKey(), bool);
        this.mAppListChanged = true;
        this.mHelper.applyUserAppState(str, bool, this);
        return true;
        label86:
        bool = true;
      }
    }
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    getActivity().registerReceiver(this.mUserBackgrounding, new IntentFilter("android.intent.action.USER_BACKGROUND"));
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addDataScheme("package");
    getActivity().registerReceiver(this.mPackageObserver, localIntentFilter);
    this.mAppListChanged = false;
    if ((this.mAppLoadingTask == null) || (this.mAppLoadingTask.getStatus() == AsyncTask.Status.FINISHED)) {
      this.mAppLoadingTask = new AppLoadingTask(null).execute(new Void[0]);
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("user_id", this.mUser.getIdentifier());
  }
  
  private class AppLoadingTask
    extends AsyncTask<Void, Void, Void>
  {
    private AppLoadingTask() {}
    
    protected Void doInBackground(Void... paramVarArgs)
    {
      AppRestrictionsFragment.-get1(AppRestrictionsFragment.this).fetchAndMergeApps();
      return null;
    }
    
    protected void onPostExecute(Void paramVoid)
    {
      AppRestrictionsFragment.-wrap3(AppRestrictionsFragment.this);
    }
  }
  
  static class AppRestrictionsPreference
    extends SwitchPreference
  {
    private boolean hasSettings;
    private boolean immutable;
    private View.OnClickListener listener;
    private List<Preference> mChildren = new ArrayList();
    private boolean panelOpen;
    private ArrayList<RestrictionEntry> restrictions;
    
    AppRestrictionsPreference(Context paramContext, View.OnClickListener paramOnClickListener)
    {
      super();
      setLayoutResource(2130968890);
      this.listener = paramOnClickListener;
    }
    
    private void setSettingsEnabled(boolean paramBoolean)
    {
      this.hasSettings = paramBoolean;
    }
    
    List<Preference> getChildren()
    {
      return this.mChildren;
    }
    
    ArrayList<RestrictionEntry> getRestrictions()
    {
      return this.restrictions;
    }
    
    boolean isImmutable()
    {
      return this.immutable;
    }
    
    boolean isPanelOpen()
    {
      return this.panelOpen;
    }
    
    public void onBindViewHolder(final PreferenceViewHolder paramPreferenceViewHolder)
    {
      int j = 8;
      boolean bool2 = false;
      super.onBindViewHolder(paramPreferenceViewHolder);
      View localView1 = paramPreferenceViewHolder.findViewById(2131362411);
      int i;
      if (this.hasSettings)
      {
        i = 0;
        localView1.setVisibility(i);
        View localView2 = paramPreferenceViewHolder.findViewById(2131362412);
        i = j;
        if (this.hasSettings) {
          i = 0;
        }
        localView2.setVisibility(i);
        localView1.setOnClickListener(this.listener);
        localView1.setTag(this);
        localView1 = paramPreferenceViewHolder.findViewById(2131362410);
        localView1.setOnClickListener(this.listener);
        localView1.setTag(this);
        paramPreferenceViewHolder = (ViewGroup)paramPreferenceViewHolder.findViewById(16908312);
        if (!isImmutable()) {
          break label191;
        }
        bool1 = false;
        label117:
        paramPreferenceViewHolder.setEnabled(bool1);
        if (paramPreferenceViewHolder.getChildCount() > 0)
        {
          paramPreferenceViewHolder = (Switch)paramPreferenceViewHolder.getChildAt(0);
          if (!isImmutable()) {
            break label197;
          }
        }
      }
      label191:
      label197:
      for (boolean bool1 = bool2;; bool1 = true)
      {
        paramPreferenceViewHolder.setEnabled(bool1);
        paramPreferenceViewHolder.setTag(this);
        paramPreferenceViewHolder.setClickable(true);
        paramPreferenceViewHolder.setFocusable(true);
        paramPreferenceViewHolder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
          public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
          {
            AppRestrictionsFragment.AppRestrictionsPreference.-get1(AppRestrictionsFragment.AppRestrictionsPreference.this).onClick(paramPreferenceViewHolder);
          }
        });
        return;
        i = 8;
        break;
        bool1 = true;
        break label117;
      }
    }
    
    void setImmutable(boolean paramBoolean)
    {
      this.immutable = paramBoolean;
    }
    
    void setPanelOpen(boolean paramBoolean)
    {
      this.panelOpen = paramBoolean;
    }
    
    void setRestrictions(ArrayList<RestrictionEntry> paramArrayList)
    {
      this.restrictions = paramArrayList;
    }
  }
  
  class RestrictionsResultReceiver
    extends BroadcastReceiver
  {
    private static final String CUSTOM_RESTRICTIONS_INTENT = "android.intent.extra.restrictions_intent";
    boolean invokeIfCustom;
    String packageName;
    AppRestrictionsFragment.AppRestrictionsPreference preference;
    
    RestrictionsResultReceiver(String paramString, AppRestrictionsFragment.AppRestrictionsPreference paramAppRestrictionsPreference, boolean paramBoolean)
    {
      this.packageName = paramString;
      this.preference = paramAppRestrictionsPreference;
      this.invokeIfCustom = paramBoolean;
    }
    
    private void assertSafeToStartCustomActivity(Intent paramIntent)
    {
      if ((paramIntent.getPackage() != null) && (paramIntent.getPackage().equals(this.packageName))) {
        return;
      }
      Object localObject = AppRestrictionsFragment.this.mPackageManager.queryIntentActivities(paramIntent, 0);
      if (((List)localObject).size() != 1) {
        return;
      }
      localObject = ((ResolveInfo)((List)localObject).get(0)).activityInfo;
      if (!this.packageName.equals(((ActivityInfo)localObject).packageName)) {
        throw new SecurityException("Application " + this.packageName + " is not allowed to start activity " + paramIntent);
      }
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramIntent = getResultExtras(true);
      paramContext = paramIntent.getParcelableArrayList("android.intent.extra.restrictions_list");
      paramIntent = (Intent)paramIntent.getParcelable("android.intent.extra.restrictions_intent");
      if ((paramContext != null) && (paramIntent == null))
      {
        AppRestrictionsFragment.-wrap2(AppRestrictionsFragment.this, this.preference, paramContext);
        if (AppRestrictionsFragment.this.mRestrictedProfile) {
          AppRestrictionsFragment.this.mUserManager.setApplicationRestrictions(this.packageName, RestrictionsManager.convertRestrictionsToBundle(paramContext), AppRestrictionsFragment.this.mUser);
        }
      }
      do
      {
        do
        {
          return;
        } while (paramIntent == null);
        this.preference.setRestrictions(paramContext);
      } while ((!this.invokeIfCustom) || (!AppRestrictionsFragment.this.isResumed()));
      assertSafeToStartCustomActivity(paramIntent);
      int i = AppRestrictionsFragment.-wrap0(AppRestrictionsFragment.this, this.preference);
      AppRestrictionsFragment.this.startActivityForResult(paramIntent, i);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\users\AppRestrictionsFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */