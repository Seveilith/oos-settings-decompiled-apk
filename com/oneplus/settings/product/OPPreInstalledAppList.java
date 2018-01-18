package com.oneplus.settings.product;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.view.View;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.SettingsBaseApplication;

public class OPPreInstalledAppList
  extends SettingsPreferenceFragment
{
  public static final String[] sOneplusH2PreIinstalledAppsCompany = SettingsBaseApplication.mApplication.getResources().getStringArray(2131427495);
  public static final String[] sOneplusH2PreIinstalledAppsFunction = SettingsBaseApplication.mApplication.getResources().getStringArray(2131427496);
  public static final String[] sOneplusH2PreIinstalledAppsName = SettingsBaseApplication.mApplication.getResources().getStringArray(2131427494);
  private final String ONEPLUS_PRE_INSTALL_APP_CATEGORY = "oneplus_pre_install_app_category";
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    addPreferencesFromResource(2131230805);
    PreferenceCategory localPreferenceCategory = (PreferenceCategory)getPreferenceScreen().findPreference("oneplus_pre_install_app_category");
    int i = 0;
    while (i < sOneplusH2PreIinstalledAppsName.length)
    {
      Preference localPreference = new Preference(getContext());
      localPreference.setTitle(sOneplusH2PreIinstalledAppsName[i]);
      localPreference.setSummary(sOneplusH2PreIinstalledAppsFunction[i] + " / " + sOneplusH2PreIinstalledAppsCompany[i]);
      localPreferenceCategory.addPreference(localPreference);
      i += 1;
    }
    super.onViewCreated(paramView, paramBundle);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\product\OPPreInstalledAppList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */