package com.oneplus.settings.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchIndexableResource;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.notification.SettingPref;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import java.util.Arrays;
import java.util.List;

public class OPSilentMode
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final String KEY_MEDIA_RING = "media_ring";
  private static final String KEY_NOISE_TIPS = "noise_tips";
  private static final SettingPref[] PREFS = { PREF_MEDIA_RING_SETTING, PREF_NOISE_TIPS_SETTING };
  private static final SettingPref PREF_MEDIA_RING_SETTING = new SettingPref(2, "media_ring", "oem_zen_media_switch", 0, new int[0]);
  private static final SettingPref PREF_NOISE_TIPS_SETTING = new SettingPref(2, "noise_tips", "oem_vibrate_under_silent", 0, new int[0]);
  public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230815;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private PrefSettingsObserver mPrefSettingsObserver = new PrefSettingsObserver();
  
  protected int getMetricsCategory()
  {
    return 76;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230815);
  }
  
  public void onPause()
  {
    this.mPrefSettingsObserver.register(false);
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    SettingPref[] arrayOfSettingPref = PREFS;
    int i = 0;
    int j = arrayOfSettingPref.length;
    while (i < j)
    {
      arrayOfSettingPref[i].init(this);
      i += 1;
    }
    this.mPrefSettingsObserver.register(true);
  }
  
  private final class PrefSettingsObserver
    extends ContentObserver
  {
    public PrefSettingsObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      SettingPref[] arrayOfSettingPref = OPSilentMode.-get0();
      int i = 0;
      int j = arrayOfSettingPref.length;
      while (i < j)
      {
        SettingPref localSettingPref = arrayOfSettingPref[i];
        if (localSettingPref.getUri().equals(paramUri))
        {
          localSettingPref.update(OPSilentMode.this.getActivity());
          return;
        }
        i += 1;
      }
    }
    
    public void register(boolean paramBoolean)
    {
      ContentResolver localContentResolver = OPSilentMode.-wrap0(OPSilentMode.this);
      if (paramBoolean)
      {
        SettingPref[] arrayOfSettingPref = OPSilentMode.-get0();
        int j = arrayOfSettingPref.length;
        int i = 0;
        while (i < j)
        {
          localContentResolver.registerContentObserver(arrayOfSettingPref[i].getUri(), false, this);
          i += 1;
        }
      }
      localContentResolver.unregisterContentObserver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\notification\OPSilentMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */