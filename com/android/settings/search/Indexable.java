package com.android.settings.search;

import android.content.Context;
import android.provider.SearchIndexableResource;
import java.util.List;

public abstract interface Indexable
{
  public static abstract interface SearchIndexProvider
  {
    public abstract List<String> getNonIndexableKeys(Context paramContext);
    
    public abstract List<SearchIndexableRaw> getRawDataToIndex(Context paramContext, boolean paramBoolean);
    
    public abstract List<SearchIndexableResource> getXmlResourcesToIndex(Context paramContext, boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\search\Indexable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */