package com.oneplus.settings;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.KeyStore;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;

public class OPCredentialsManagementSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final String KEY_CREDENTIALS_INSTALL = "credentials_install";
  private static final String KEY_CREDENTIALS_MANAGER = "credentials_management";
  private static final String KEY_CREDENTIAL_STORAGE_TYPE = "credential_storage_type";
  private static final String KEY_RESET_CREDENTIALS = "credentials_reset";
  private static final String PACKAGE_MIME_TYPE = "application/vnd.android.package-archive";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new SecuritySearchIndexProvider();
  static final String TAG = "SecuritySettings";
  private static final String TRUST_AGENT_CLICK_INTENT = "trust_agent_click_intent";
  private boolean mIsPrimary;
  private KeyStore mKeyStore;
  private Preference mResetCredentials;
  
  private void createCredentialPreference()
  {
    Object localObject = getPreferenceScreen();
    this.mResetCredentials = ((PreferenceScreen)localObject).findPreference("credentials_reset");
    UserManager localUserManager = (UserManager)getActivity().getSystemService("user");
    this.mKeyStore = KeyStore.getInstance();
    if (!localUserManager.hasUserRestriction("no_config_credentials"))
    {
      localObject = ((PreferenceScreen)localObject).findPreference("credential_storage_type");
      if (this.mKeyStore.isHardwareBacked()) {}
      for (int i = 2131692604;; i = 2131692605)
      {
        ((Preference)localObject).setSummary(i);
        return;
      }
    }
    ((PreferenceScreen)localObject).removePreference(((PreferenceScreen)localObject).findPreference("credentials_reset"));
    ((PreferenceScreen)localObject).removePreference(((PreferenceScreen)localObject).findPreference("credentials_install"));
    ((PreferenceScreen)localObject).removePreference(((PreferenceScreen)localObject).findPreference("credential_storage_type"));
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230788);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
  }
  
  public void onResume()
  {
    super.onResume();
    createCredentialPreference();
    Preference localPreference;
    if (this.mResetCredentials != null)
    {
      localPreference = this.mResetCredentials;
      if (!this.mKeyStore.isEmpty()) {
        break label38;
      }
    }
    label38:
    for (boolean bool = false;; bool = true)
    {
      localPreference.setEnabled(bool);
      return;
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
  }
  
  private static class SecuritySearchIndexProvider
    extends BaseSearchIndexProvider
  {
    boolean mIsPrimary;
    
    public SecuritySearchIndexProvider()
    {
      if (UserHandle.myUserId() == 0) {
        bool = true;
      }
      this.mIsPrimary = bool;
    }
    
    public List<String> getNonIndexableKeys(Context paramContext)
    {
      ArrayList localArrayList = new ArrayList();
      if (((UserManager)paramContext.getSystemService("user")).hasUserRestriction("no_config_credentials")) {
        localArrayList.add("credentials_management");
      }
      return localArrayList;
    }
    
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramContext, boolean paramBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      Resources localResources = paramContext.getResources();
      String str = localResources.getString(2131692593);
      SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramContext);
      localSearchIndexableRaw.title = str;
      localSearchIndexableRaw.screenTitle = str;
      localArrayList.add(localSearchIndexableRaw);
      if (!this.mIsPrimary)
      {
        if (UserManager.get(paramContext).isLinkedUser())
        {
          i = 2131691056;
          localSearchIndexableRaw = new SearchIndexableRaw(paramContext);
          localSearchIndexableRaw.title = localResources.getString(i);
          localSearchIndexableRaw.screenTitle = str;
          localArrayList.add(localSearchIndexableRaw);
        }
      }
      else if (!((UserManager)paramContext.getSystemService("user")).hasUserRestriction("no_config_credentials")) {
        if (!KeyStore.getInstance().isHardwareBacked()) {
          break label188;
        }
      }
      label188:
      for (int i = 2131692604;; i = 2131692605)
      {
        paramContext = new SearchIndexableRaw(paramContext);
        paramContext.title = localResources.getString(i);
        paramContext.screenTitle = str;
        localArrayList.add(paramContext);
        return localArrayList;
        i = 2131691054;
        break;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPCredentialsManagementSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */