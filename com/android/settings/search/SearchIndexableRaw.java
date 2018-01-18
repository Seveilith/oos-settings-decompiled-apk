package com.android.settings.search;

import android.content.Context;
import android.provider.SearchIndexableData;

public class SearchIndexableRaw
  extends SearchIndexableData
{
  public String entries;
  public String keywords;
  public String screenTitle;
  public String summaryOff;
  public String summaryOn;
  public String title;
  
  public SearchIndexableRaw(Context paramContext)
  {
    super(paramContext);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\search\SearchIndexableRaw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */