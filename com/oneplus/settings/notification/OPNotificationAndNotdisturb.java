package com.oneplus.settings.notification;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPNotificationAndNotdisturb
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final String KEY_RING_SETTINGS = "ring_settings";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new NotificationAndNotdisturbSearchIndexProvider();
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230803);
    if (OPUtils.isGuestMode()) {
      getPreferenceScreen().removePreference(findPreference("ring_settings"));
    }
  }
  
  private static class NotificationAndNotdisturbSearchIndexProvider
    extends BaseSearchIndexProvider
  {
    boolean mIsPrimary;
    
    public NotificationAndNotdisturbSearchIndexProvider()
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
      paramContext.xmlResId = 2131230803;
      localArrayList.add(paramContext);
      return localArrayList;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\notification\OPNotificationAndNotdisturb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */