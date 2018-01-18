package com.oneplus.settings;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.oneplus.settings.defaultapp.DefaultAppLogic;
import java.util.Arrays;
import java.util.List;

public class OPDefaultAppSettings
  extends SettingsPreferenceFragment
{
  public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230789;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final String TAG = "OPApplicationsSettings";
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    DefaultAppLogic.getInstance(SettingsBaseApplication.mApplication).initDefaultAppSettings();
    addPreferencesFromResource(2131230789);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPDefaultAppSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */