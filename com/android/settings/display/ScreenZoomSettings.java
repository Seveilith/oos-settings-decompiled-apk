package com.android.settings.display;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import com.android.settings.PreviewSeekBarPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settingslib.display.DisplayDensityUtils;
import java.util.ArrayList;
import java.util.List;

public class ScreenZoomSettings
  extends PreviewSeekBarPreferenceFragment
  implements Indexable
{
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      Object localObject = paramAnonymousContext.getResources();
      paramAnonymousContext = new SearchIndexableRaw(paramAnonymousContext);
      paramAnonymousContext.title = ((Resources)localObject).getString(2131693561);
      paramAnonymousContext.screenTitle = ((Resources)localObject).getString(2131693561);
      paramAnonymousContext.keywords = ((Resources)localObject).getString(2131693563);
      localObject = new ArrayList(1);
      ((List)localObject).add(paramAnonymousContext);
      return (List<SearchIndexableRaw>)localObject;
    }
  };
  private int mDefaultDensity;
  private int[] mValues;
  
  protected void commit()
  {
    int i = this.mValues[this.mCurrentIndex];
    if (i == this.mDefaultDensity)
    {
      DisplayDensityUtils.clearForcedDisplayDensity(0);
      return;
    }
    DisplayDensityUtils.setForcedDisplayDensity(0, i);
  }
  
  protected Configuration createConfig(Configuration paramConfiguration, int paramInt)
  {
    paramConfiguration = new Configuration(paramConfiguration);
    paramConfiguration.densityDpi = this.mValues[paramInt];
    return paramConfiguration;
  }
  
  protected int getMetricsCategory()
  {
    return 339;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mActivityLayoutResId = 2130968966;
    this.mPreviewSampleResIds = new int[] { 2130968967, 2130968970 };
    paramBundle = new DisplayDensityUtils(getContext());
    int i = paramBundle.getCurrentIndex();
    if (i < 0)
    {
      i = getResources().getDisplayMetrics().densityDpi;
      this.mValues = new int[] { i };
      this.mEntries = new String[] { getString(DisplayDensityUtils.SUMMARY_DEFAULT) };
      this.mInitialIndex = 0;
      this.mDefaultDensity = i;
      return;
    }
    this.mValues = paramBundle.getValues();
    this.mEntries = paramBundle.getEntries();
    this.mInitialIndex = i;
    this.mDefaultDensity = paramBundle.getDefaultDensity();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\display\ScreenZoomSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */