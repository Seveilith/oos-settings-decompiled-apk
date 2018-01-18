package com.android.settings.search;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.SearchIndexableResource;
import android.provider.SearchIndexablesContract;
import android.provider.SearchIndexablesProvider;
import java.util.Iterator;

public class SettingsSearchIndexablesProvider
  extends SearchIndexablesProvider
{
  private static final String TAG = "SettingsSearchIndexablesProvider";
  
  public boolean onCreate()
  {
    return true;
  }
  
  public Cursor queryNonIndexableKeys(String[] paramArrayOfString)
  {
    return new MatrixCursor(SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS);
  }
  
  public Cursor queryRawData(String[] paramArrayOfString)
  {
    return new MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
  }
  
  public Cursor queryXmlResources(String[] paramArrayOfString)
  {
    paramArrayOfString = new MatrixCursor(SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS);
    Iterator localIterator = SearchIndexableResources.values().iterator();
    while (localIterator.hasNext())
    {
      SearchIndexableResource localSearchIndexableResource = (SearchIndexableResource)localIterator.next();
      paramArrayOfString.addRow(new Object[] { Integer.valueOf(localSearchIndexableResource.rank), Integer.valueOf(localSearchIndexableResource.xmlResId), localSearchIndexableResource.className, Integer.valueOf(localSearchIndexableResource.iconResId), null, null, null });
    }
    return paramArrayOfString;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\search\SettingsSearchIndexablesProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */