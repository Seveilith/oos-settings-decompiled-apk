package com.android.settings.datausage;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Resources;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.TelephonyManager;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settingslib.NetworkPolicyEditor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataUsageMeteredSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      paramAnonymousContext = new ArrayList();
      paramAnonymousContext.add("mobile");
      return paramAnonymousContext;
    }
    
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      Resources localResources = paramAnonymousContext.getResources();
      Object localObject1 = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject1).title = localResources.getString(2131692769);
      ((SearchIndexableRaw)localObject1).screenTitle = localResources.getString(2131692769);
      localArrayList.add(localObject1);
      localObject1 = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject1).title = localResources.getString(2131692827);
      ((SearchIndexableRaw)localObject1).screenTitle = localResources.getString(2131692769);
      localArrayList.add(localObject1);
      localObject1 = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject1).title = localResources.getString(2131692829);
      ((SearchIndexableRaw)localObject1).screenTitle = localResources.getString(2131692769);
      localArrayList.add(localObject1);
      localObject1 = (WifiManager)paramAnonymousContext.getSystemService("wifi");
      if ((DataUsageSummary.hasWifiRadio(paramAnonymousContext)) && (((WifiManager)localObject1).isWifiEnabled())) {
        localObject1 = ((WifiManager)localObject1).getConfiguredNetworks().iterator();
      }
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = (WifiConfiguration)((Iterator)localObject1).next();
        if (((WifiConfiguration)localObject2).SSID != null)
        {
          localObject2 = ((WifiConfiguration)localObject2).SSID;
          SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
          localSearchIndexableRaw.title = WifiInfo.removeDoubleQuotes((String)localObject2);
          localSearchIndexableRaw.screenTitle = localResources.getString(2131692769);
          localArrayList.add(localSearchIndexableRaw);
          continue;
          paramAnonymousContext = new SearchIndexableRaw(paramAnonymousContext);
          paramAnonymousContext.title = localResources.getString(2131692830);
          paramAnonymousContext.screenTitle = localResources.getString(2131692769);
          localArrayList.add(paramAnonymousContext);
        }
      }
      return localArrayList;
    }
  };
  private static final boolean SHOW_MOBILE_CATEGORY = false;
  private PreferenceCategory mMobileCategory;
  private NetworkPolicyEditor mPolicyEditor;
  private NetworkPolicyManager mPolicyManager;
  private PreferenceCategory mWifiCategory;
  private Preference mWifiDisabled;
  private WifiManager mWifiManager;
  
  private Preference buildMobilePref(Context paramContext)
  {
    paramContext = TelephonyManager.from(paramContext);
    Object localObject = NetworkTemplate.buildTemplateMobileAll(paramContext.getSubscriberId());
    localObject = new MeteredPreference(getPrefContext(), (NetworkTemplate)localObject);
    ((MeteredPreference)localObject).setTitle(paramContext.getNetworkOperatorName());
    return (Preference)localObject;
  }
  
  private Preference buildWifiPref(Context paramContext, WifiConfiguration paramWifiConfiguration)
  {
    paramWifiConfiguration = paramWifiConfiguration.SSID;
    paramContext = new MeteredPreference(paramContext, NetworkTemplate.buildTemplateWifi(paramWifiConfiguration));
    paramContext.setTitle(WifiInfo.removeDoubleQuotes(paramWifiConfiguration));
    return paramContext;
  }
  
  private void updateNetworks(Context paramContext)
  {
    getPreferenceScreen().removePreference(this.mMobileCategory);
    this.mWifiCategory.removeAll();
    Object localObject;
    if ((DataUsageSummary.hasWifiRadio(paramContext)) && (this.mWifiManager.isWifiEnabled()))
    {
      localObject = this.mWifiManager.getConfiguredNetworks();
      if (localObject != null) {
        localObject = ((Iterable)localObject).iterator();
      }
    }
    else
    {
      while (((Iterator)localObject).hasNext())
      {
        WifiConfiguration localWifiConfiguration = (WifiConfiguration)((Iterator)localObject).next();
        if (localWifiConfiguration.SSID != null)
        {
          this.mWifiCategory.addPreference(buildWifiPref(paramContext, localWifiConfiguration));
          continue;
          this.mWifiCategory.addPreference(this.mWifiDisabled);
        }
      }
    }
  }
  
  protected int getMetricsCategory()
  {
    return 68;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getActivity();
    this.mPolicyManager = NetworkPolicyManager.from(paramBundle);
    this.mWifiManager = ((WifiManager)paramBundle.getSystemService("wifi"));
    this.mPolicyEditor = new NetworkPolicyEditor(this.mPolicyManager);
    this.mPolicyEditor.read();
    addPreferencesFromResource(2131230753);
    this.mMobileCategory = ((PreferenceCategory)findPreference("mobile"));
    this.mWifiCategory = ((PreferenceCategory)findPreference("wifi"));
    this.mWifiDisabled = findPreference("wifi_disabled");
    updateNetworks(paramBundle);
  }
  
  private class MeteredPreference
    extends SwitchPreference
  {
    private boolean mBinding;
    private final NetworkTemplate mTemplate;
    
    public MeteredPreference(Context paramContext, NetworkTemplate paramNetworkTemplate)
    {
      super();
      this.mTemplate = paramNetworkTemplate;
      setPersistent(false);
      this.mBinding = true;
      this$1 = DataUsageMeteredSettings.-get0(DataUsageMeteredSettings.this).getPolicyMaybeUnquoted(paramNetworkTemplate);
      if (DataUsageMeteredSettings.this != null) {
        if (DataUsageMeteredSettings.this.limitBytes != -1L)
        {
          setChecked(true);
          setEnabled(false);
        }
      }
      for (;;)
      {
        this.mBinding = false;
        return;
        setChecked(DataUsageMeteredSettings.this.metered);
        continue;
        setChecked(false);
      }
    }
    
    protected void notifyChanged()
    {
      super.notifyChanged();
      if (!this.mBinding)
      {
        DataUsageMeteredSettings.-get0(DataUsageMeteredSettings.this).setPolicyMetered(this.mTemplate, isChecked());
        BackupManager.dataChanged("com.android.providers.settings");
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\DataUsageMeteredSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */