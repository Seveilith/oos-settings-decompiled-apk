package com.android.settings.applications;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.util.OpFeatures;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.Callbacks;
import com.android.settingslib.applications.ApplicationsState.Session;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.defaultapp.DefaultAppLogic;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedAppSettings
  extends SettingsPreferenceFragment
  implements ApplicationsState.Callbacks, Indexable
{
  private static final String KEY_ADVANCED_APPS = "advanced_apps";
  private static final String KEY_APP_DOMAIN_URLS = "domain_urls";
  private static final String KEY_APP_PERM = "manage_perms";
  private static final String KEY_DEFAULT_APPS = "default_apps";
  private static final String KEY_HIGH_POWER_APPS = "high_power_apps";
  private static final String KEY_LINKER_LUNCH = "op_linked_launch";
  private static final String KEY_SYSTEM_ALERT_WINDOW = "system_alert_window";
  private static final String KEY_WRITE_SETTINGS_APPS = "write_settings_apps";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      paramAnonymousContext = new ArrayList();
      paramAnonymousContext.add("op_linked_launch");
      paramAnonymousContext.add("default_emergency_app");
      return paramAnonymousContext;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230728;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  static final String TAG = "AdvancedAppSettings";
  private PreferenceCategory mAdvanceAppsPreferenceCategory;
  private Preference mAppDomainURLsPreference;
  private Preference mAppPermsPreference;
  private Preference mHighPowerPreference;
  private Preference mLinkedLunchPreference;
  private final PermissionsSummaryHelper.PermissionsResultCallback mPermissionCallback = new PermissionsSummaryHelper.PermissionsResultCallback()
  {
    public void onAppWithPermissionsCountsResult(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (AdvancedAppSettings.this.getActivity() == null) {
        return;
      }
      AdvancedAppSettings.-set0(AdvancedAppSettings.this, null);
      if (paramAnonymousInt2 != 0)
      {
        AdvancedAppSettings.-get0(AdvancedAppSettings.this).setSummary(AdvancedAppSettings.this.getContext().getString(2131693414, new Object[] { Integer.valueOf(paramAnonymousInt1), Integer.valueOf(paramAnonymousInt2) }));
        return;
      }
      AdvancedAppSettings.-get0(AdvancedAppSettings.this).setSummary(null);
    }
  };
  private BroadcastReceiver mPermissionReceiver;
  private ApplicationsState.Session mSession;
  private Preference mSystemAlertWindowPreference;
  private Preference mWriteSettingsPreference;
  
  protected int getMetricsCategory()
  {
    return 130;
  }
  
  public void onAllSizesComputed() {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    DefaultAppLogic.getInstance(SettingsBaseApplication.mApplication).initDefaultAppSettings();
    addPreferencesFromResource(2131230728);
    paramBundle = getPreferenceScreen().findPreference("manage_perms");
    if ((Build.VERSION.IS_CTA_BUILD) && (OPUtils.isAppExist(getActivity(), "com.oneplus.security"))) {
      paramBundle.setIntent(new Intent("com.oneplus.security.action.OPPERMISSION"));
    }
    for (;;)
    {
      this.mSession = ApplicationsState.getInstance(getActivity().getApplication()).newSession(this);
      this.mAppPermsPreference = findPreference("manage_perms");
      this.mAppDomainURLsPreference = findPreference("domain_urls");
      this.mHighPowerPreference = findPreference("high_power_apps");
      this.mSystemAlertWindowPreference = findPreference("system_alert_window");
      this.mWriteSettingsPreference = findPreference("write_settings_apps");
      this.mAdvanceAppsPreferenceCategory = ((PreferenceCategory)findPreference("advanced_apps"));
      this.mLinkedLunchPreference = findPreference("op_linked_launch");
      this.mAdvanceAppsPreferenceCategory.removePreference(this.mLinkedLunchPreference);
      if (OPUtils.isAppExist(getActivity(), "com.oneplus.security")) {
        if (!OpFeatures.isSupport(new int[] { 22 })) {}
      }
      return;
      paramBundle.setIntent(new Intent("android.intent.action.MANAGE_PERMISSIONS"));
    }
  }
  
  public void onDestroy()
  {
    if (this.mSession != null) {
      this.mSession.release();
    }
    super.onDestroy();
  }
  
  public void onLauncherInfoChanged() {}
  
  public void onLoadEntriesCompleted() {}
  
  public void onPackageIconChanged() {}
  
  public void onPackageListChanged() {}
  
  public void onPackageSizeChanged(String paramString) {}
  
  /* Error */
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    // Byte code:
    //   0: ldc 31
    //   2: aload_1
    //   3: invokevirtual 211	android/support/v7/preference/Preference:getKey	()Ljava/lang/String;
    //   6: invokevirtual 217	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   9: ifeq +54 -> 63
    //   12: aconst_null
    //   13: astore_1
    //   14: new 136	android/content/Intent
    //   17: dup
    //   18: ldc -37
    //   20: invokespecial 141	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   23: astore_2
    //   24: aload_0
    //   25: invokevirtual 126	com/android/settings/applications/AdvancedAppSettings:getActivity	()Landroid/app/Activity;
    //   28: aload_2
    //   29: invokevirtual 222	android/app/Activity:startActivity	(Landroid/content/Intent;)V
    //   32: iconst_1
    //   33: ireturn
    //   34: astore_2
    //   35: ldc 42
    //   37: new 224	java/lang/StringBuilder
    //   40: dup
    //   41: invokespecial 225	java/lang/StringBuilder:<init>	()V
    //   44: ldc -29
    //   46: invokevirtual 231	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: aload_1
    //   50: invokevirtual 234	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   53: invokevirtual 237	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   56: invokestatic 243	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   59: pop
    //   60: goto -28 -> 32
    //   63: aload_0
    //   64: aload_1
    //   65: invokespecial 245	com/android/settings/SettingsPreferenceFragment:onPreferenceTreeClick	(Landroid/support/v7/preference/Preference;)Z
    //   68: ireturn
    //   69: astore_1
    //   70: aload_2
    //   71: astore_1
    //   72: goto -37 -> 35
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	75	0	this	AdvancedAppSettings
    //   0	75	1	paramPreference	Preference
    //   23	6	2	localIntent	Intent
    //   34	37	2	localActivityNotFoundException	android.content.ActivityNotFoundException
    // Exception table:
    //   from	to	target	type
    //   14	24	34	android/content/ActivityNotFoundException
    //   24	32	69	android/content/ActivityNotFoundException
  }
  
  public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> paramArrayList) {}
  
  public void onRunningStateChanged(boolean paramBoolean) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AdvancedAppSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */