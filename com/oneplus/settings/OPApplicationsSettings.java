package com.oneplus.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import com.android.settings.Settings.DisplaySizeAdaptionAppListActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPApplicationsSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final String KEY_DISPLAY_SIZE_ADAPTION = "display_size_adaption";
  private static final String ONEPLUSE_MULTI_APPLICATIONS_SETTING_KEY = "onepluse_multi_applications_setting";
  private static final String ONEPLUS_APPLICATIONS_MANAGER_SETTINGS_KEY = "oneplus_applications_manager_settings";
  private static final String ONEPLUS_APPLICATIONS_PERMISSION_MANAGER_SETTINGS_KEY = "oneplus_applications_permission_settings";
  private static final String ONEPLUS_DEFAULT_APPLICATIONS_SETTINGS_KEY = "onepluse_default_applications_setting";
  public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      paramAnonymousContext = new ArrayList();
      if (!OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) {
        paramAnonymousContext.add("display_size_adaption");
      }
      return paramAnonymousContext;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230786;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final String TAG = "OPApplicationsSettings";
  private static final String USER_ENJOY_PLAY_KEY = "user_enjoy_plan";
  private Context mContext;
  private Preference mDisplaySizeAdaptionPreference;
  private Preference mTimerShutdownPreference;
  private SwitchPreference mUserPlanPreference;
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230786);
    if (OPUtils.isGuestMode()) {
      getPreferenceScreen().removePreference(findPreference("onepluse_multi_applications_setting"));
    }
    this.mDisplaySizeAdaptionPreference = findPreference("display_size_adaption");
    if ((!OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) && (this.mDisplaySizeAdaptionPreference != null)) {
      getPreferenceScreen().removePreference(this.mDisplaySizeAdaptionPreference);
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    Object localObject = paramPreference.getKey();
    if ((!"oneplus_applications_permission_settings".equals(localObject)) || (Build.VERSION.IS_CTA_BUILD)) {}
    try
    {
      if (OPUtils.isAppExist(getActivity(), "com.oneplus.security")) {}
      for (paramPreference = new Intent("com.oneplus.security.action.OPPERMISSION");; paramPreference = new Intent("android.intent.action.MANAGE_PERMISSIONS"))
      {
        startActivity(paramPreference);
        return true;
      }
      if ("onepluse_multi_applications_setting".equals(localObject)) {}
      try
      {
        paramPreference = new Intent();
        paramPreference.setAction("android.intent.action.ONEPLUS_MULTI_APP_LIST_ACTION");
        startActivity(paramPreference);
        return true;
      }
      catch (ActivityNotFoundException paramPreference)
      {
        return true;
      }
      if (paramPreference.getKey().equals("display_size_adaption"))
      {
        paramPreference = null;
        try
        {
          localObject = new Intent("com.android.settings.action.DISPLAYSIZEADAPTION");
          Log.d("OPApplicationsSettings", "No activity found for " + paramPreference);
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          try
          {
            ((Intent)localObject).putExtra("classname", Settings.DisplaySizeAdaptionAppListActivity.class.getName());
            startActivity((Intent)localObject);
            return true;
          }
          catch (ActivityNotFoundException paramPreference)
          {
            for (;;)
            {
              paramPreference = localActivityNotFoundException;
            }
          }
          localActivityNotFoundException = localActivityNotFoundException;
        }
        return true;
      }
      else
      {
        return super.onPreferenceTreeClick(paramPreference);
      }
    }
    catch (ActivityNotFoundException paramPreference) {}
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPApplicationsSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */