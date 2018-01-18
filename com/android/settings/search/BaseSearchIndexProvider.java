package com.android.settings.search;

import android.content.Context;
import android.provider.SearchIndexableResource;
import java.util.Collections;
import java.util.List;

public class BaseSearchIndexProvider
  implements Indexable.SearchIndexProvider
{
  private static final List<String> EMPTY_LIST = ;
  
  public List<String> getNonIndexableKeys(Context paramContext)
  {
    return EMPTY_LIST;
  }
  
  public List<SearchIndexableRaw> getRawDataToIndex(Context paramContext, boolean paramBoolean)
  {
    return null;
  }
  
  public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramContext, boolean paramBoolean)
  {
    return null;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\search\BaseSearchIndexProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */