package com.android.settings;

import android.app.Activity;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManager.Stub;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Secure;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.util.OpFeatures;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settingslib.RestrictedLockUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrivacySettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final String AUTO_RESTORE = "auto_restore";
  private static final String BACKUP_DATA = "backup_data";
  private static final String BACKUP_INACTIVE = "backup_inactive";
  private static final String COLLECT_DIAGNOSTICS = "collect_diagnostics";
  private static final String CONFIGURE_ACCOUNT = "configure_account";
  private static final String DATA_MANAGEMENT = "data_management";
  private static final String FACTORY_RESET = "factory_reset";
  private static final String GSETTINGS_PROVIDER = "com.google.settings";
  private static final String NETWORK_RESET = "network_reset";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new PrivacySearchIndexProvider();
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new PrivacySettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
    }
  };
  private static final String TAG = "PrivacySettings";
  private SwitchPreference mAutoRestore;
  private PreferenceScreen mBackup;
  private IBackupManager mBackupManager;
  private PreferenceScreen mConfigure;
  private boolean mEnabled;
  private PreferenceScreen mManageData;
  private Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener()
  {
    public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
    {
      boolean bool2 = true;
      if (!(paramAnonymousPreference instanceof SwitchPreference)) {
        return true;
      }
      boolean bool3 = ((Boolean)paramAnonymousObject).booleanValue();
      boolean bool1 = false;
      if (paramAnonymousPreference == PrivacySettings.-get0(PrivacySettings.this)) {}
      try
      {
        PrivacySettings.-get1(PrivacySettings.this).setAutoRestore(bool3);
        bool1 = true;
        return bool1;
      }
      catch (RemoteException paramAnonymousPreference)
      {
        paramAnonymousPreference = PrivacySettings.-get0(PrivacySettings.this);
        bool1 = bool2;
        if (bool3) {
          bool1 = false;
        }
        paramAnonymousPreference.setChecked(bool1);
      }
      return false;
    }
  };
  
  private static boolean collectDiagnosticsEnabled(Context paramContext)
  {
    return paramContext.getResources().getBoolean(2131558469);
  }
  
  private static void getNonVisibleKeys(Context paramContext, Collection<String> paramCollection)
  {
    IBackupManager localIBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    int j = 0;
    try
    {
      boolean bool = localIBackupManager.isBackupServiceActive(UserHandle.myUserId());
      j = bool;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.w("PrivacySettings", "Failed querying backup manager service activity status. Assuming it is inactive.");
        continue;
        int i = 0;
        continue;
        if (!OpFeatures.isSupport(new int[] { 1 }))
        {
          paramCollection.add("backup_data");
          paramCollection.add("auto_restore");
          paramCollection.add("configure_account");
        }
      }
    }
    if (paramContext.getPackageManager().resolveContentProvider("com.google.settings", 0) == null)
    {
      i = 1;
      if ((i != 0) || (j != 0)) {
        paramCollection.add("backup_inactive");
      }
      if ((i != 0) || (j == 0)) {
        break label144;
      }
      if (RestrictedLockUtils.hasBaseUserRestriction(paramContext, "no_factory_reset", UserHandle.myUserId())) {
        paramCollection.add("factory_reset");
      }
      if (RestrictedLockUtils.hasBaseUserRestriction(paramContext, "no_network_reset", UserHandle.myUserId())) {
        paramCollection.add("network_reset");
      }
      if (!collectDiagnosticsEnabled(paramContext)) {
        paramCollection.add("collect_diagnostics");
      }
    }
  }
  
  private void setConfigureSummary(String paramString)
  {
    if (paramString != null)
    {
      this.mConfigure.setSummary(paramString);
      return;
    }
    this.mConfigure.setSummary(2131692633);
  }
  
  private void updateToggles()
  {
    boolean bool3 = true;
    ContentResolver localContentResolver = getContentResolver();
    boolean bool1 = false;
    Object localObject2 = null;
    String str3 = null;
    Intent localIntent2 = null;
    String str4 = null;
    Object localObject1 = localObject2;
    String str2 = str3;
    Intent localIntent1 = localIntent2;
    String str1 = str4;
    for (;;)
    {
      try
      {
        bool2 = this.mBackupManager.isBackupEnabled();
        bool1 = bool2;
        localObject1 = localObject2;
        str2 = str3;
        localIntent1 = localIntent2;
        str1 = str4;
        Object localObject3 = this.mBackupManager.getCurrentTransport();
        bool1 = bool2;
        localObject1 = localObject2;
        str2 = str3;
        localIntent1 = localIntent2;
        str1 = str4;
        localObject2 = validatedActivityIntent(this.mBackupManager.getConfigurationIntent((String)localObject3), "config");
        bool1 = bool2;
        localObject1 = localObject2;
        str2 = str3;
        localIntent1 = localIntent2;
        str1 = str4;
        str3 = this.mBackupManager.getDestinationString((String)localObject3);
        bool1 = bool2;
        localObject1 = localObject2;
        str2 = str3;
        localIntent1 = localIntent2;
        str1 = str4;
        localIntent2 = validatedActivityIntent(this.mBackupManager.getDataManagementIntent((String)localObject3), "management");
        bool1 = bool2;
        localObject1 = localObject2;
        str2 = str3;
        localIntent1 = localIntent2;
        str1 = str4;
        str4 = this.mBackupManager.getDataManagementLabel((String)localObject3);
        bool1 = bool2;
        localObject1 = localObject2;
        str2 = str3;
        localIntent1 = localIntent2;
        str1 = str4;
        localObject3 = this.mBackup;
        if (!bool2) {
          continue;
        }
        i = 2131692353;
        bool1 = bool2;
        localObject1 = localObject2;
        str2 = str3;
        localIntent1 = localIntent2;
        str1 = str4;
        ((PreferenceScreen)localObject3).setSummary(i);
        str1 = str4;
        localIntent1 = localIntent2;
        str2 = str3;
        localObject1 = localObject2;
        bool1 = bool2;
      }
      catch (RemoteException localRemoteException)
      {
        int i;
        this.mBackup.setEnabled(false);
        continue;
        boolean bool2 = false;
        continue;
        bool2 = false;
        continue;
        bool1 = false;
        continue;
        getPreferenceScreen().removePreference(this.mManageData);
      }
      localObject2 = this.mAutoRestore;
      if (Settings.Secure.getInt(localContentResolver, "backup_auto_restore", 1) != 1) {
        continue;
      }
      bool2 = bool3;
      ((SwitchPreference)localObject2).setChecked(bool2);
      this.mAutoRestore.setEnabled(bool1);
      if (localObject1 == null) {
        continue;
      }
      bool2 = bool1;
      this.mConfigure.setEnabled(bool2);
      this.mConfigure.setIntent((Intent)localObject1);
      setConfigureSummary(str2);
      if (localIntent1 == null) {
        continue;
      }
      if (!bool1) {
        continue;
      }
      this.mManageData.setIntent(localIntent1);
      if (str1 != null) {
        this.mManageData.setTitle(str1);
      }
      return;
      i = 2131692354;
    }
  }
  
  private Intent validatedActivityIntent(Intent paramIntent, String paramString)
  {
    Intent localIntent = paramIntent;
    if (paramIntent != null)
    {
      List localList = getPackageManager().queryIntentActivities(paramIntent, 0);
      if (localList != null)
      {
        localIntent = paramIntent;
        if (!localList.isEmpty()) {}
      }
      else
      {
        localIntent = null;
        Log.e("PrivacySettings", "Backup " + paramString + " intent " + null + " fails to resolve; ignoring");
      }
    }
    return localIntent;
  }
  
  protected int getHelpResource()
  {
    return 2131693022;
  }
  
  protected int getMetricsCategory()
  {
    return 81;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mEnabled = UserManager.get(getActivity()).isAdminUser();
    if (!this.mEnabled) {
      return;
    }
    addPreferencesFromResource(2131230830);
    paramBundle = getPreferenceScreen();
    this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    this.mBackup = ((PreferenceScreen)paramBundle.findPreference("backup_data"));
    this.mAutoRestore = ((SwitchPreference)paramBundle.findPreference("auto_restore"));
    this.mAutoRestore.setOnPreferenceChangeListener(this.preferenceChangeListener);
    this.mConfigure = ((PreferenceScreen)paramBundle.findPreference("configure_account"));
    this.mManageData = ((PreferenceScreen)paramBundle.findPreference("data_management"));
    HashSet localHashSet = new HashSet();
    getNonVisibleKeys(getActivity(), localHashSet);
    int i = paramBundle.getPreferenceCount() - 1;
    while (i >= 0)
    {
      Preference localPreference = paramBundle.getPreference(i);
      if (localHashSet.contains(localPreference.getKey())) {
        paramBundle.removePreference(localPreference);
      }
      i -= 1;
    }
    updateToggles();
    if (!OpFeatures.isSupport(new int[] { 1 }))
    {
      getActivity().setTitle(2131691893);
      paramBundle.removePreference(this.mBackup);
      paramBundle.removePreference(this.mConfigure);
      paramBundle.removePreference(this.mAutoRestore);
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mEnabled) {
      updateToggles();
    }
  }
  
  private static class PrivacySearchIndexProvider
    extends BaseSearchIndexProvider
  {
    boolean mIsPrimary;
    
    public PrivacySearchIndexProvider()
    {
      if (UserHandle.myUserId() == 0) {
        bool = true;
      }
      this.mIsPrimary = bool;
    }
    
    public List<String> getNonIndexableKeys(Context paramContext)
    {
      ArrayList localArrayList = new ArrayList();
      PrivacySettings.-wrap0(paramContext, localArrayList);
      return localArrayList;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramContext, boolean paramBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      if (!this.mIsPrimary) {
        return localArrayList;
      }
      paramContext = new SearchIndexableResource(paramContext);
      paramContext.xmlResId = 2131230830;
      localArrayList.add(paramContext);
      return localArrayList;
    }
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mSummaryLoader;
    
    public SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (!OpFeatures.isSupport(new int[] { 1 })) {
        return;
      }
      Object localObject;
      if (paramBoolean) {
        localObject = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
      }
      try
      {
        if (((IBackupManager)localObject).isBackupEnabled())
        {
          localObject = ((IBackupManager)localObject).getDestinationString(((IBackupManager)localObject).getCurrentTransport());
          if (localObject != null)
          {
            this.mSummaryLoader.setSummary(this, (CharSequence)localObject);
            return;
          }
          this.mSummaryLoader.setSummary(this, this.mContext.getString(2131692633));
          return;
        }
        this.mSummaryLoader.setSummary(this, this.mContext.getString(2131693593));
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\PrivacySettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */