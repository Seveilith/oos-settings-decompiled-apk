package com.android.settings.tts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.EngineInfo;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TtsEngines;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Checkable;
import com.android.settings.SeekBarPreference;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Set;

public class TextToSpeechSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, TtsEnginePreference.RadioButtonGroupState
{
  private static final boolean DBG = false;
  private static final int GET_SAMPLE_TEXT = 1983;
  private static final String KEY_DEFAULT_PITCH = "tts_default_pitch";
  private static final String KEY_DEFAULT_RATE = "tts_default_rate";
  private static final String KEY_ENGINE_PREFERENCE_SECTION = "tts_engine_preference_section";
  private static final String KEY_PLAY_EXAMPLE = "tts_play_example";
  private static final String KEY_RESET_SPEECH_PITCH = "reset_speech_pitch";
  private static final String KEY_RESET_SPEECH_RATE = "reset_speech_rate";
  private static final String KEY_STATUS = "tts_status";
  private static final int MAX_SPEECH_PITCH = 400;
  private static final int MAX_SPEECH_RATE = 600;
  private static final int MIN_SPEECH_PITCH = 25;
  private static final int MIN_SPEECH_RATE = 10;
  private static final String TAG = "TextToSpeechSettings";
  private static final int VOICE_DATA_INTEGRITY_CHECK = 1977;
  private List<String> mAvailableStrLocals;
  private Checkable mCurrentChecked;
  private Locale mCurrentDefaultLocale;
  private String mCurrentEngine;
  private int mDefaultPitch = 100;
  private SeekBarPreference mDefaultPitchPref;
  private int mDefaultRate = 100;
  private SeekBarPreference mDefaultRatePref;
  private PreferenceCategory mEnginePreferenceCategory;
  private Preference mEngineStatus;
  private TtsEngines mEnginesHelper = null;
  private final TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener()
  {
    public void onInit(int paramAnonymousInt)
    {
      TextToSpeechSettings.this.onInitEngine(paramAnonymousInt);
    }
  };
  private Preference mPlayExample;
  private String mPreviousEngine;
  private Preference mResetSpeechPitch;
  private Preference mResetSpeechRate;
  private String mSampleText = null;
  private TextToSpeech mTts = null;
  private final TextToSpeech.OnInitListener mUpdateListener = new TextToSpeech.OnInitListener()
  {
    public void onInit(int paramAnonymousInt)
    {
      TextToSpeechSettings.this.onUpdateEngine(paramAnonymousInt);
    }
  };
  
  private void checkDefaultLocale()
  {
    Locale localLocale1 = this.mTts.getDefaultLanguage();
    if (localLocale1 == null)
    {
      Log.e("TextToSpeechSettings", "Failed to get default language from engine " + this.mCurrentEngine);
      updateWidgetState(false);
      updateEngineStatus(2131689615);
      return;
    }
    Locale localLocale2 = this.mCurrentDefaultLocale;
    this.mCurrentDefaultLocale = this.mEnginesHelper.parseLocaleString(localLocale1.toString());
    if (!Objects.equals(localLocale2, this.mCurrentDefaultLocale)) {
      this.mSampleText = null;
    }
    this.mTts.setLanguage(localLocale1);
    if ((evaluateDefaultLocale()) && (this.mSampleText == null)) {
      getSampleText();
    }
  }
  
  private void checkVoiceData(String paramString)
  {
    Intent localIntent = new Intent("android.speech.tts.engine.CHECK_TTS_DATA");
    localIntent.setPackage(paramString);
    try
    {
      startActivityForResult(localIntent, 1977);
      return;
    }
    catch (ActivityNotFoundException paramString)
    {
      Log.e("TextToSpeechSettings", "Failed to check TTS data, no activity found for " + localIntent + ")");
    }
  }
  
  private void displayNetworkAlert()
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
    localBuilder.setTitle(17039380).setMessage(getActivity().getString(2131689610)).setCancelable(false).setPositiveButton(17039370, null);
    localBuilder.create().show();
  }
  
  private boolean evaluateDefaultLocale()
  {
    if ((this.mCurrentDefaultLocale == null) || (this.mAvailableStrLocals == null)) {
      return false;
    }
    int j = 1;
    int i;
    do
    {
      try
      {
        Object localObject2 = this.mCurrentDefaultLocale.getISO3Language();
        Object localObject1 = localObject2;
        if (!TextUtils.isEmpty(this.mCurrentDefaultLocale.getISO3Country())) {
          localObject1 = (String)localObject2 + "-" + this.mCurrentDefaultLocale.getISO3Country();
        }
        localObject2 = localObject1;
        if (!TextUtils.isEmpty(this.mCurrentDefaultLocale.getVariant())) {
          localObject2 = (String)localObject1 + "-" + this.mCurrentDefaultLocale.getVariant();
        }
        localObject1 = this.mAvailableStrLocals.iterator();
        boolean bool;
        do
        {
          i = j;
          if (!((Iterator)localObject1).hasNext()) {
            break;
          }
          bool = ((String)((Iterator)localObject1).next()).equalsIgnoreCase((String)localObject2);
        } while (!bool);
        i = 0;
        j = this.mTts.setLanguage(this.mCurrentDefaultLocale);
        if ((j == -2) || (j == -1))
        {
          updateEngineStatus(2131689615);
          updateWidgetState(false);
          return false;
        }
      }
      catch (MissingResourceException localMissingResourceException)
      {
        updateEngineStatus(2131689615);
        updateWidgetState(false);
        return false;
      }
    } while (i != 0);
    if (isNetworkRequiredForSynthesis()) {
      updateEngineStatus(2131689614);
    }
    for (;;)
    {
      updateWidgetState(true);
      return true;
      updateEngineStatus(2131689613);
    }
  }
  
  private String getDefaultSampleString()
  {
    if ((this.mTts != null) && (this.mTts.getLanguage() != null)) {
      try
      {
        String str = this.mTts.getLanguage().getISO3Language();
        String[] arrayOfString1 = getActivity().getResources().getStringArray(2131427366);
        String[] arrayOfString2 = getActivity().getResources().getStringArray(2131427367);
        int i = 0;
        while (i < arrayOfString1.length)
        {
          if (arrayOfString2[i].equals(str))
          {
            str = arrayOfString1[i];
            return str;
          }
          i += 1;
        }
        return getString(2131689611);
      }
      catch (MissingResourceException localMissingResourceException) {}
    }
  }
  
  private void getSampleText()
  {
    Object localObject2 = this.mTts.getCurrentEngine();
    Object localObject1 = localObject2;
    if (TextUtils.isEmpty((CharSequence)localObject2)) {
      localObject1 = this.mTts.getDefaultEngine();
    }
    localObject2 = new Intent("android.speech.tts.engine.GET_SAMPLE_TEXT");
    ((Intent)localObject2).putExtra("language", this.mCurrentDefaultLocale.getLanguage());
    ((Intent)localObject2).putExtra("country", this.mCurrentDefaultLocale.getCountry());
    ((Intent)localObject2).putExtra("variant", this.mCurrentDefaultLocale.getVariant());
    ((Intent)localObject2).setPackage((String)localObject1);
    try
    {
      startActivityForResult((Intent)localObject2, 1983);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("TextToSpeechSettings", "Failed to get sample text, no activity found for " + localObject2 + ")");
    }
  }
  
  private int getSeekBarProgressFromValue(String paramString, int paramInt)
  {
    if (paramString.equals("tts_default_rate")) {
      return paramInt - 10;
    }
    if (paramString.equals("tts_default_pitch")) {
      return paramInt - 25;
    }
    return paramInt;
  }
  
  private int getValueFromSeekBarProgress(String paramString, int paramInt)
  {
    if (paramString.equals("tts_default_rate")) {
      return paramInt + 10;
    }
    if (paramString.equals("tts_default_pitch")) {
      return paramInt + 25;
    }
    return paramInt;
  }
  
  private void initSettings()
  {
    Object localObject1 = getContentResolver();
    this.mDefaultRate = Settings.Secure.getInt((ContentResolver)localObject1, "tts_default_rate", 100);
    this.mDefaultPitch = Settings.Secure.getInt((ContentResolver)localObject1, "tts_default_pitch", 100);
    this.mDefaultRatePref.setProgress(getSeekBarProgressFromValue("tts_default_rate", this.mDefaultRate));
    this.mDefaultRatePref.setOnPreferenceChangeListener(this);
    this.mDefaultRatePref.setMax(getSeekBarProgressFromValue("tts_default_rate", 600));
    this.mDefaultPitchPref.setProgress(getSeekBarProgressFromValue("tts_default_pitch", this.mDefaultPitch));
    this.mDefaultPitchPref.setOnPreferenceChangeListener(this);
    this.mDefaultPitchPref.setMax(getSeekBarProgressFromValue("tts_default_pitch", 400));
    if (this.mTts != null)
    {
      this.mCurrentEngine = this.mTts.getCurrentEngine();
      this.mTts.setSpeechRate(this.mDefaultRate / 100.0F);
      this.mTts.setPitch(this.mDefaultPitch / 100.0F);
    }
    if ((getActivity() instanceof SettingsActivity))
    {
      localObject1 = (SettingsActivity)getActivity();
      this.mEnginePreferenceCategory.removeAll();
      Iterator localIterator = this.mEnginesHelper.getEngines().iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = (TextToSpeech.EngineInfo)localIterator.next();
        localObject2 = new TtsEnginePreference(getPrefContext(), (TextToSpeech.EngineInfo)localObject2, this, (SettingsActivity)localObject1);
        this.mEnginePreferenceCategory.addPreference((Preference)localObject2);
      }
    }
    throw new IllegalStateException("TextToSpeechSettings used outside a Settings");
    checkVoiceData(this.mCurrentEngine);
  }
  
  private boolean isNetworkRequiredForSynthesis()
  {
    Set localSet = this.mTts.getFeatures(this.mCurrentDefaultLocale);
    if (localSet == null) {
      return false;
    }
    return (localSet.contains("networkTts")) && (!localSet.contains("embeddedTts"));
  }
  
  private void onSampleTextReceived(int paramInt, Intent paramIntent)
  {
    String str2 = getDefaultSampleString();
    String str1 = str2;
    if (paramInt == 0)
    {
      str1 = str2;
      if (paramIntent != null)
      {
        str1 = str2;
        if (paramIntent != null)
        {
          str1 = str2;
          if (paramIntent.getStringExtra("sampleText") != null) {
            str1 = paramIntent.getStringExtra("sampleText");
          }
        }
      }
    }
    this.mSampleText = str1;
    if (this.mSampleText != null)
    {
      updateWidgetState(true);
      return;
    }
    Log.e("TextToSpeechSettings", "Did not have a sample string for the requested language. Using default");
  }
  
  private void onVoiceDataIntegrityCheckDone(Intent paramIntent)
  {
    String str = this.mTts.getCurrentEngine();
    if (str == null)
    {
      Log.e("TextToSpeechSettings", "Voice data check complete, but no engine bound");
      return;
    }
    if (paramIntent == null)
    {
      Log.e("TextToSpeechSettings", "Engine failed voice data integrity check (null return)" + this.mTts.getCurrentEngine());
      return;
    }
    Settings.Secure.putString(getContentResolver(), "tts_default_synth", str);
    this.mAvailableStrLocals = paramIntent.getStringArrayListExtra("availableVoices");
    if (this.mAvailableStrLocals == null)
    {
      Log.e("TextToSpeechSettings", "Voice data check complete, but no available voices found");
      this.mAvailableStrLocals = new ArrayList();
    }
    if (evaluateDefaultLocale()) {
      getSampleText();
    }
    int j = this.mEnginePreferenceCategory.getPreferenceCount();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        Object localObject = this.mEnginePreferenceCategory.getPreference(i);
        if ((localObject instanceof TtsEnginePreference))
        {
          localObject = (TtsEnginePreference)localObject;
          if (((TtsEnginePreference)localObject).getKey().equals(str)) {
            ((TtsEnginePreference)localObject).setVoiceDataDetails(paramIntent);
          }
        }
      }
      else
      {
        return;
      }
      i += 1;
    }
  }
  
  private void setTtsUtteranceProgressListener()
  {
    if (this.mTts == null) {
      return;
    }
    this.mTts.setOnUtteranceProgressListener(new UtteranceProgressListener()
    {
      public void onDone(String paramAnonymousString) {}
      
      public void onError(String paramAnonymousString)
      {
        Log.e("TextToSpeechSettings", "Error while trying to synthesize sample text");
      }
      
      public void onStart(String paramAnonymousString) {}
    });
  }
  
  private void speakSampleText()
  {
    boolean bool = isNetworkRequiredForSynthesis();
    if ((!bool) || ((bool) && (this.mTts.isLanguageAvailable(this.mCurrentDefaultLocale) >= 0)))
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("utteranceId", "Sample");
      this.mTts.speak(this.mSampleText, 0, localHashMap);
      return;
    }
    Log.w("TextToSpeechSettings", "Network required for sample synthesis for requested language");
    displayNetworkAlert();
  }
  
  private void updateDefaultEngine(String paramString)
  {
    updateWidgetState(false);
    updateEngineStatus(2131689616);
    this.mPreviousEngine = this.mTts.getCurrentEngine();
    if (this.mTts != null) {}
    try
    {
      this.mTts.shutdown();
      this.mTts = null;
      this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mUpdateListener, paramString);
      setTtsUtteranceProgressListener();
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.e("TextToSpeechSettings", "Error shutting down TTS engine" + localException);
      }
    }
  }
  
  private void updateEngineStatus(int paramInt)
  {
    Locale localLocale2 = this.mCurrentDefaultLocale;
    Locale localLocale1 = localLocale2;
    if (localLocale2 == null) {
      localLocale1 = Locale.getDefault();
    }
    this.mEngineStatus.setSummary(getString(paramInt, new Object[] { localLocale1.getDisplayName() }));
  }
  
  private void updateSpeechPitchValue(int paramInt)
  {
    this.mDefaultPitch = getValueFromSeekBarProgress("tts_default_pitch", paramInt);
    try
    {
      Settings.Secure.putInt(getContentResolver(), "tts_default_pitch", this.mDefaultPitch);
      if (this.mTts != null) {
        this.mTts.setPitch(this.mDefaultPitch / 100.0F);
      }
      return;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.e("TextToSpeechSettings", "could not persist default TTS pitch setting", localNumberFormatException);
    }
  }
  
  private void updateSpeechRate(int paramInt)
  {
    this.mDefaultRate = getValueFromSeekBarProgress("tts_default_rate", paramInt);
    try
    {
      Settings.Secure.putInt(getContentResolver(), "tts_default_rate", this.mDefaultRate);
      if (this.mTts != null) {
        this.mTts.setSpeechRate(this.mDefaultRate / 100.0F);
      }
      return;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.e("TextToSpeechSettings", "could not persist default TTS rate setting", localNumberFormatException);
    }
  }
  
  private void updateWidgetState(boolean paramBoolean)
  {
    this.mPlayExample.setEnabled(paramBoolean);
    this.mDefaultRatePref.setEnabled(paramBoolean);
    this.mEngineStatus.setEnabled(paramBoolean);
  }
  
  public Checkable getCurrentChecked()
  {
    return this.mCurrentChecked;
  }
  
  public String getCurrentKey()
  {
    return this.mCurrentEngine;
  }
  
  protected int getMetricsCategory()
  {
    return 94;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 1983) {
      onSampleTextReceived(paramInt2, paramIntent);
    }
    while (paramInt1 != 1977) {
      return;
    }
    onVoiceDataIntegrityCheckDone(paramIntent);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230872);
    getActivity().setVolumeControlStream(3);
    this.mPlayExample = findPreference("tts_play_example");
    this.mPlayExample.setOnPreferenceClickListener(this);
    this.mPlayExample.setEnabled(false);
    this.mResetSpeechRate = findPreference("reset_speech_rate");
    this.mResetSpeechRate.setOnPreferenceClickListener(this);
    this.mResetSpeechPitch = findPreference("reset_speech_pitch");
    this.mResetSpeechPitch.setOnPreferenceClickListener(this);
    this.mEnginePreferenceCategory = ((PreferenceCategory)findPreference("tts_engine_preference_section"));
    this.mDefaultPitchPref = ((SeekBarPreference)findPreference("tts_default_pitch"));
    this.mDefaultRatePref = ((SeekBarPreference)findPreference("tts_default_rate"));
    this.mEngineStatus = findPreference("tts_status");
    updateEngineStatus(2131689616);
    this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mInitListener);
    this.mEnginesHelper = new TtsEngines(getActivity().getApplicationContext());
    setTtsUtteranceProgressListener();
    initSettings();
    setRetainInstance(true);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (this.mTts != null)
    {
      this.mTts.shutdown();
      this.mTts = null;
    }
  }
  
  public void onInitEngine(int paramInt)
  {
    if (paramInt == 0)
    {
      checkDefaultLocale();
      return;
    }
    updateWidgetState(false);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if ("tts_default_rate".equals(paramPreference.getKey())) {
      updateSpeechRate(((Integer)paramObject).intValue());
    }
    for (;;)
    {
      return true;
      if ("tts_default_pitch".equals(paramPreference.getKey())) {
        updateSpeechPitchValue(((Integer)paramObject).intValue());
      }
    }
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mPlayExample)
    {
      speakSampleText();
      return true;
    }
    int i;
    if (paramPreference == this.mResetSpeechRate)
    {
      i = getSeekBarProgressFromValue("tts_default_rate", 100);
      this.mDefaultRatePref.setProgress(i);
      updateSpeechRate(i);
      return true;
    }
    if (paramPreference == this.mResetSpeechPitch)
    {
      i = getSeekBarProgressFromValue("tts_default_pitch", 100);
      this.mDefaultPitchPref.setProgress(i);
      updateSpeechPitchValue(i);
      return true;
    }
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    if ((this.mTts == null) || (this.mCurrentDefaultLocale == null)) {
      return;
    }
    Locale localLocale = this.mTts.getDefaultLanguage();
    if ((this.mCurrentDefaultLocale == null) || (this.mCurrentDefaultLocale.equals(localLocale))) {
      return;
    }
    updateWidgetState(false);
    checkDefaultLocale();
  }
  
  public void onUpdateEngine(int paramInt)
  {
    if (paramInt == 0)
    {
      checkVoiceData(this.mTts.getCurrentEngine());
      return;
    }
    if (this.mPreviousEngine != null)
    {
      this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mInitListener, this.mPreviousEngine);
      setTtsUtteranceProgressListener();
    }
    this.mPreviousEngine = null;
  }
  
  public void setCurrentChecked(Checkable paramCheckable)
  {
    this.mCurrentChecked = paramCheckable;
  }
  
  public void setCurrentKey(String paramString)
  {
    this.mCurrentEngine = paramString;
    updateDefaultEngine(this.mCurrentEngine);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\tts\TextToSpeechSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */