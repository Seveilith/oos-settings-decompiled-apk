package com.android.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LegalSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final String KEY_COPYRIGHT = "copyright";
  private static final String KEY_FROM_SETTINGS = "key_from_settings";
  private static final String KEY_LEGAL_NOTICES = "op_legal_notices";
  private static final int KEY_LEGAL_NOTICES_TYPE = 1;
  private static final String KEY_LICENSE = "license";
  private static final String KEY_NOTICES_TYPE = "op_legal_notices_type";
  private static final String KEY_PERMISSION_AGREEMENT = "op_permission_agreement";
  private static final int KEY_PERMISSION_AGREEMENT_TYPE = 4;
  private static final String KEY_PRIVACE_POLICY = "op_privacy_policy";
  private static final int KEY_PRIVACE_POLICY_TYPE = 3;
  private static final String KEY_TERMS = "terms";
  private static final String KEY_USER_AGREEMENT = "op_user_agreements";
  private static final int KEY_USER_AGREEMENT_TYPE = 2;
  private static final String KEY_WALLPAPER_ATTRIBUTIONS = "wallpaper_attributions";
  private static final String KEY_WEBVIEW_LICENSE = "webview_license";
  private static final String ONEPLUS_A5000 = "ONEPLUS A5000";
  private static final String ONEPLUS_A5010 = "ONEPLUS A5010";
  private static final String OPLEGAL_NOTICES_ACTION = "android.oem.intent.action.OP_LEGAL";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    private boolean checkIntentAction(Context paramAnonymousContext, String paramAnonymousString)
    {
      paramAnonymousString = new Intent(paramAnonymousString);
      paramAnonymousContext = paramAnonymousContext.getPackageManager().queryIntentActivities(paramAnonymousString, 0);
      int j = paramAnonymousContext.size();
      int i = 0;
      while (i < j)
      {
        if ((((ResolveInfo)paramAnonymousContext.get(i)).activityInfo.applicationInfo.flags & 0x1) != 0) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      ArrayList localArrayList = new ArrayList();
      localArrayList.add("terms");
      if (!checkIntentAction(paramAnonymousContext, "android.settings.LICENSE")) {
        localArrayList.add("license");
      }
      if (!checkIntentAction(paramAnonymousContext, "android.settings.COPYRIGHT")) {
        localArrayList.add("copyright");
      }
      if (!checkIntentAction(paramAnonymousContext, "android.settings.WEBVIEW_LICENSE")) {
        localArrayList.add("webview_license");
      }
      if (OPUtils.isO2()) {
        localArrayList.add("op_permission_agreement");
      }
      return localArrayList;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230720;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final String THEME_MODE_CUST_FLAG = "/sys/module/param_read_write/parameters/cust_flag";
  private static final String THEME_MODE_CUST_FLAG_STAR_WAR_VALUE = "2";
  
  private void startLegalActivity(int paramInt)
  {
    Intent localIntent = new Intent("android.oem.intent.action.OP_LEGAL");
    localIntent.putExtra("op_legal_notices_type", paramInt);
    localIntent.putExtra("key_from_settings", true);
    startActivity(localIntent);
  }
  
  protected int getMetricsCategory()
  {
    return 225;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230720);
    paramBundle = getActivity();
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    Utils.updatePreferenceToSpecificActivityOrRemove(paramBundle, localPreferenceScreen, "terms", 1);
    Utils.updatePreferenceToSpecificActivityOrRemove(paramBundle, localPreferenceScreen, "license", 1);
    Utils.updatePreferenceToSpecificActivityOrRemove(paramBundle, localPreferenceScreen, "copyright", 1);
    Utils.updatePreferenceToSpecificActivityOrRemove(paramBundle, localPreferenceScreen, "webview_license", 1);
    if (OPUtils.isO2()) {
      localPreferenceScreen.removePreference(findPreference("op_permission_agreement"));
    }
    if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5000"))
    {
      findPreference("wallpaper_attributions").setSummary(2131690761);
      localPreferenceScreen.removePreference(findPreference("icon_attributions"));
    }
    for (;;)
    {
      if ("2".equals(OPUtils.readFile("/sys/module/param_read_write/parameters/cust_flag"))) {
        findPreference("wallpaper_attributions").setSummary(2131690763);
      }
      localPreferenceScreen.removePreference(findPreference("op_legal_notices"));
      return;
      if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5010"))
      {
        findPreference("wallpaper_attributions").setSummary(2131690762);
        localPreferenceScreen.removePreference(findPreference("icon_attributions"));
      }
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    try
    {
      if (paramPreference.getKey().equals("op_legal_notices"))
      {
        startLegalActivity(1);
        return true;
      }
      if (paramPreference.getKey().equals("op_user_agreements"))
      {
        startLegalActivity(2);
        return true;
      }
      if (paramPreference.getKey().equals("op_privacy_policy"))
      {
        startLegalActivity(3);
        return true;
      }
      if (paramPreference.getKey().equals("op_permission_agreement")) {
        startLegalActivity(4);
      }
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      for (;;) {}
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\LegalSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */