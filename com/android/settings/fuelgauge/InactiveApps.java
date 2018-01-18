package com.android.settings.fuelgauge;

import android.app.Activity;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Iterator;

public class InactiveApps
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener
{
  private UsageStatsManager mUsageStats;
  
  private void init()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    localPreferenceScreen.removeAll();
    localPreferenceScreen.setOrderingAsAdded(false);
    Object localObject = getActivity();
    PackageManager localPackageManager = ((Context)localObject).getPackageManager();
    localObject = (UsageStatsManager)((Context)localObject).getSystemService(UsageStatsManager.class);
    localObject = new Intent("android.intent.action.MAIN");
    ((Intent)localObject).addCategory("android.intent.category.LAUNCHER");
    localObject = localPackageManager.queryIntentActivities((Intent)localObject, 0).iterator();
    while (((Iterator)localObject).hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject).next();
      String str = localResolveInfo.activityInfo.applicationInfo.packageName;
      Preference localPreference = new Preference(getPrefContext());
      localPreference.setTitle(localResolveInfo.loadLabel(localPackageManager));
      localPreference.setIcon(localResolveInfo.loadIcon(localPackageManager));
      localPreference.setKey(str);
      updateSummary(localPreference);
      localPreference.setOnPreferenceClickListener(this);
      localPreferenceScreen.addPreference(localPreference);
    }
  }
  
  private void updateSummary(Preference paramPreference)
  {
    if (this.mUsageStats.isAppInactive(paramPreference.getKey())) {}
    for (int i = 2131689750;; i = 2131689751)
    {
      paramPreference.setSummary(i);
      return;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 238;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUsageStats = ((UsageStatsManager)getActivity().getSystemService(UsageStatsManager.class));
    addPreferencesFromResource(2131230770);
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    String str = paramPreference.getKey();
    UsageStatsManager localUsageStatsManager = this.mUsageStats;
    if (this.mUsageStats.isAppInactive(str)) {}
    for (boolean bool = false;; bool = true)
    {
      localUsageStatsManager.setAppInactive(str, bool);
      updateSummary(paramPreference);
      return false;
    }
  }
  
  public void onResume()
  {
    super.onResume();
    init();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\InactiveApps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */