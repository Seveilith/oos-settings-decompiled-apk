package com.oneplus.settings.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchIndexableResource;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import java.util.Arrays;
import java.util.List;

public class OPRingPattern
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final String KEY_VIBRATE_WHEN_RINGING = "vibrate_when_ringing";
  public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230810;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final String TAG = "OPRingPattern";
  private final SettingsObserver mSettingsObserver = new SettingsObserver();
  private TwoStatePreference mVibrateWhenRinging;
  private boolean mVoiceCapable;
  
  private void initVibrateWhenRinging()
  {
    this.mVibrateWhenRinging = ((TwoStatePreference)findPreference("vibrate_when_ringing"));
    if (this.mVibrateWhenRinging == null)
    {
      Log.i("OPRingPattern", "Preference not found: vibrate_when_ringing");
      return;
    }
    if (!this.mVoiceCapable)
    {
      getPreferenceScreen().removePreference(this.mVibrateWhenRinging);
      this.mVibrateWhenRinging = null;
      return;
    }
    this.mVibrateWhenRinging.setPersistent(false);
    updateVibrateWhenRinging();
    this.mVibrateWhenRinging.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
        paramAnonymousPreference = OPRingPattern.-wrap0(OPRingPattern.this);
        if (bool) {}
        for (int i = 1;; i = 0) {
          return Settings.System.putInt(paramAnonymousPreference, "vibrate_when_ringing", i);
        }
      }
    });
  }
  
  private void updateVibrateWhenRinging()
  {
    boolean bool = false;
    if (this.mVibrateWhenRinging == null) {
      return;
    }
    TwoStatePreference localTwoStatePreference = this.mVibrateWhenRinging;
    if (Settings.System.getInt(getContentResolver(), "vibrate_when_ringing", 0) != 0) {
      bool = true;
    }
    localTwoStatePreference.setChecked(bool);
  }
  
  protected int getMetricsCategory()
  {
    return 76;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230810);
    this.mVoiceCapable = Utils.isVoiceCapable(getActivity());
    initVibrateWhenRinging();
    this.mSettingsObserver.register(true);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mSettingsObserver.register(false);
  }
  
  public void onPause()
  {
    super.onPause();
  }
  
  public void onResume()
  {
    boolean bool = false;
    super.onResume();
    if (this.mVibrateWhenRinging != null)
    {
      TwoStatePreference localTwoStatePreference = this.mVibrateWhenRinging;
      if (Settings.System.getInt(getContentResolver(), "vibrate_when_ringing", 0) != 0) {
        bool = true;
      }
      localTwoStatePreference.setChecked(bool);
    }
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    private final Uri VIBRATE_WHEN_RINGING_URI = Settings.System.getUriFor("vibrate_when_ringing");
    
    public SettingsObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      super.onChange(paramBoolean, paramUri);
      if (this.VIBRATE_WHEN_RINGING_URI.equals(paramUri)) {
        OPRingPattern.-wrap1(OPRingPattern.this);
      }
    }
    
    public void register(boolean paramBoolean)
    {
      ContentResolver localContentResolver = OPRingPattern.-wrap0(OPRingPattern.this);
      if (paramBoolean)
      {
        localContentResolver.registerContentObserver(this.VIBRATE_WHEN_RINGING_URI, false, this);
        return;
      }
      localContentResolver.unregisterContentObserver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\notification\OPRingPattern.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */