package com.oneplus.settings;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import java.util.ArrayList;
import java.util.List;

public class OPStatusBarCustomizeIconSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new StatusBarCustomizeIndexProvider();
  private static final String TAG = "OPStatusBarCustomizeSettings";
  private Context mContext;
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230816);
    this.mContext = getActivity();
    ((SettingsActivity)getActivity()).setTitle(this.mContext.getResources().getString(2131690297));
  }
  
  private static class StatusBarCustomizeIndexProvider
    extends BaseSearchIndexProvider
  {
    boolean mIsPrimary;
    
    public StatusBarCustomizeIndexProvider()
    {
      if (UserHandle.myUserId() == 0) {
        bool = true;
      }
      this.mIsPrimary = bool;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramContext, boolean paramBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      if (!this.mIsPrimary) {
        return localArrayList;
      }
      paramContext = new SearchIndexableResource(paramContext);
      paramContext.xmlResId = 2131230816;
      localArrayList.add(paramContext);
      return localArrayList;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPStatusBarCustomizeIconSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */