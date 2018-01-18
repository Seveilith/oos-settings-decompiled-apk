package com.android.settings;

import android.speech.tts.TtsEngines;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import java.util.List;

public class VoiceInputOutputSettings
{
  private static final String KEY_TTS_SETTINGS = "tts_settings";
  private static final String KEY_VOICE_CATEGORY = "voice_category";
  private static final String TAG = "VoiceInputOutputSettings";
  private final SettingsPreferenceFragment mFragment;
  private PreferenceGroup mParent;
  private final TtsEngines mTtsEngines;
  private Preference mTtsSettingsPref;
  private PreferenceCategory mVoiceCategory;
  private Preference mVoiceInputSettingsPref;
  
  public VoiceInputOutputSettings(SettingsPreferenceFragment paramSettingsPreferenceFragment)
  {
    this.mFragment = paramSettingsPreferenceFragment;
    this.mTtsEngines = new TtsEngines(paramSettingsPreferenceFragment.getPreferenceScreen().getContext());
  }
  
  private void populateOrRemovePreferences()
  {
    if (!populateOrRemoveTtsPrefs()) {
      this.mFragment.getPreferenceScreen().removePreference(this.mVoiceCategory);
    }
  }
  
  private boolean populateOrRemoveTtsPrefs()
  {
    if (this.mTtsEngines.getEngines().isEmpty())
    {
      this.mVoiceCategory.removePreference(this.mTtsSettingsPref);
      return false;
    }
    return true;
  }
  
  public void onCreate()
  {
    this.mParent = this.mFragment.getPreferenceScreen();
    this.mVoiceCategory = ((PreferenceCategory)this.mParent.findPreference("voice_category"));
    this.mTtsSettingsPref = this.mVoiceCategory.findPreference("tts_settings");
    populateOrRemovePreferences();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\VoiceInputOutputSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */