package com.android.settings.tts;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TtsEngines;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class TtsEngineSettingsFragment
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener
{
  private static final boolean DBG = false;
  private static final String KEY_ENGINE_LOCALE = "tts_default_lang";
  private static final String KEY_ENGINE_SETTINGS = "tts_engine_settings";
  private static final String KEY_INSTALL_DATA = "tts_install_data";
  private static final String STATE_KEY_LOCALE_ENTRIES = "locale_entries";
  private static final String STATE_KEY_LOCALE_ENTRY_VALUES = "locale_entry_values";
  private static final String STATE_KEY_LOCALE_VALUE = "locale_value";
  private static final String TAG = "TtsEngineSettings";
  private static final int VOICE_DATA_INTEGRITY_CHECK = 1977;
  private Intent mEngineSettingsIntent;
  private Preference mEngineSettingsPreference;
  private TtsEngines mEnginesHelper;
  private Preference mInstallVoicesPreference;
  private final BroadcastReceiver mLanguagesChangedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.speech.tts.engine.TTS_DATA_INSTALLED".equals(paramAnonymousIntent.getAction())) {
        TtsEngineSettingsFragment.-wrap0(TtsEngineSettingsFragment.this);
      }
    }
  };
  private ListPreference mLocalePreference;
  private int mSelectedLocaleIndex = -1;
  private TextToSpeech mTts;
  private final TextToSpeech.OnInitListener mTtsInitListener = new TextToSpeech.OnInitListener()
  {
    public void onInit(int paramAnonymousInt)
    {
      if (paramAnonymousInt != 0)
      {
        TtsEngineSettingsFragment.this.finishFragment();
        return;
      }
      TtsEngineSettingsFragment.this.getActivity().runOnUiThread(new Runnable()
      {
        public void run()
        {
          TtsEngineSettingsFragment.-get0(TtsEngineSettingsFragment.this).setEnabled(true);
        }
      });
    }
  };
  private Intent mVoiceDataDetails;
  
  private final void checkTtsData()
  {
    Intent localIntent = new Intent("android.speech.tts.engine.CHECK_TTS_DATA");
    localIntent.setPackage(getEngineName());
    try
    {
      startActivityForResult(localIntent, 1977);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("TtsEngineSettings", "Failed to check TTS data, no activity found for " + localIntent + ")");
    }
  }
  
  private String getEngineLabel()
  {
    return getArguments().getString("label");
  }
  
  private String getEngineName()
  {
    return getArguments().getString("name");
  }
  
  private void installVoiceData()
  {
    if (TextUtils.isEmpty(getEngineName())) {
      return;
    }
    Intent localIntent = new Intent("android.speech.tts.engine.INSTALL_TTS_DATA");
    localIntent.setPackage(getEngineName());
    try
    {
      Log.v("TtsEngineSettings", "Installing voice data: " + localIntent.toUri(0));
      startActivity(localIntent);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("TtsEngineSettings", "Failed to install TTS data, no acitivty found for " + localIntent + ")");
    }
  }
  
  private void setLocalePreference(int paramInt)
  {
    if (paramInt < 0)
    {
      this.mLocalePreference.setValue("");
      this.mLocalePreference.setSummary(2131689603);
      return;
    }
    this.mLocalePreference.setValueIndex(paramInt);
    this.mLocalePreference.setSummary(this.mLocalePreference.getEntries()[paramInt]);
  }
  
  private void updateDefaultLocalePref(ArrayList<String> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.size() == 0))
    {
      this.mLocalePreference.setEnabled(false);
      return;
    }
    Locale localLocale = null;
    if (!this.mEnginesHelper.isLocaleSetToDefaultForEngine(getEngineName())) {
      localLocale = this.mEnginesHelper.getLocalePrefForEngine(getEngineName());
    }
    Object localObject2 = new ArrayList(paramArrayList.size());
    int i = 0;
    while (i < paramArrayList.size())
    {
      localObject1 = this.mEnginesHelper.parseLocaleString((String)paramArrayList.get(i));
      if (localObject1 != null) {
        ((ArrayList)localObject2).add(new Pair(((Locale)localObject1).getDisplayName(), localObject1));
      }
      i += 1;
    }
    Collections.sort((List)localObject2, new Comparator()
    {
      public int compare(Pair<String, Locale> paramAnonymousPair1, Pair<String, Locale> paramAnonymousPair2)
      {
        return ((String)paramAnonymousPair1.first).compareToIgnoreCase((String)paramAnonymousPair2.first);
      }
    });
    this.mSelectedLocaleIndex = 0;
    Object localObject1 = new CharSequence[paramArrayList.size() + 1];
    paramArrayList = new CharSequence[paramArrayList.size() + 1];
    localObject1[0] = getActivity().getString(2131689602);
    paramArrayList[0] = "";
    i = 1;
    localObject2 = ((Iterable)localObject2).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      Pair localPair = (Pair)((Iterator)localObject2).next();
      if (((Locale)localPair.second).equals(localLocale)) {
        this.mSelectedLocaleIndex = i;
      }
      localObject1[i] = ((CharSequence)localPair.first);
      paramArrayList[i] = ((Locale)localPair.second).toString();
      i += 1;
    }
    this.mLocalePreference.setEntries((CharSequence[])localObject1);
    this.mLocalePreference.setEntryValues(paramArrayList);
    this.mLocalePreference.setEnabled(true);
    setLocalePreference(this.mSelectedLocaleIndex);
  }
  
  private void updateLanguageTo(Locale paramLocale)
  {
    int k = -1;
    Object localObject;
    int i;
    if (paramLocale != null)
    {
      localObject = paramLocale.toString();
      i = 0;
    }
    int j;
    for (;;)
    {
      j = k;
      if (i < this.mLocalePreference.getEntryValues().length)
      {
        if (((String)localObject).equalsIgnoreCase(this.mLocalePreference.getEntryValues()[i].toString())) {
          j = i;
        }
      }
      else
      {
        if (j != -1) {
          break label83;
        }
        Log.w("TtsEngineSettings", "updateLanguageTo called with unknown locale argument");
        return;
        localObject = "";
        break;
      }
      i += 1;
    }
    label83:
    this.mLocalePreference.setSummary(this.mLocalePreference.getEntries()[j]);
    this.mSelectedLocaleIndex = j;
    this.mEnginesHelper.updateLocalePrefForEngine(getEngineName(), paramLocale);
    if (getEngineName().equals(this.mTts.getCurrentEngine()))
    {
      localObject = this.mTts;
      if (paramLocale == null) {
        break label151;
      }
    }
    for (;;)
    {
      ((TextToSpeech)localObject).setLanguage(paramLocale);
      return;
      label151:
      paramLocale = Locale.getDefault();
    }
  }
  
  private void updateVoiceDetails(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      Log.e("TtsEngineSettings", "Engine failed voice data integrity check (null return)" + this.mTts.getCurrentEngine());
      return;
    }
    this.mVoiceDataDetails = paramIntent;
    paramIntent = this.mVoiceDataDetails.getStringArrayListExtra("availableVoices");
    ArrayList localArrayList = this.mVoiceDataDetails.getStringArrayListExtra("unavailableVoices");
    if ((localArrayList != null) && (localArrayList.size() > 0)) {
      this.mInstallVoicesPreference.setEnabled(true);
    }
    while (paramIntent == null)
    {
      Log.e("TtsEngineSettings", "TTS data check failed (available == null).");
      this.mLocalePreference.setEnabled(false);
      return;
      this.mInstallVoicesPreference.setEnabled(false);
    }
    updateDefaultLocalePref(paramIntent);
  }
  
  protected int getMetricsCategory()
  {
    return 93;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 1977)
    {
      if (paramInt2 != 0) {
        updateVoiceDetails(paramIntent);
      }
    }
    else {
      return;
    }
    Log.e("TtsEngineSettings", "CheckVoiceData activity failed");
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230871);
    this.mEnginesHelper = new TtsEngines(getActivity());
    Object localObject1 = getPreferenceScreen();
    this.mLocalePreference = ((ListPreference)((PreferenceScreen)localObject1).findPreference("tts_default_lang"));
    this.mLocalePreference.setOnPreferenceChangeListener(this);
    this.mEngineSettingsPreference = ((PreferenceScreen)localObject1).findPreference("tts_engine_settings");
    this.mEngineSettingsPreference.setOnPreferenceClickListener(this);
    this.mInstallVoicesPreference = ((PreferenceScreen)localObject1).findPreference("tts_install_data");
    this.mInstallVoicesPreference.setOnPreferenceClickListener(this);
    ((PreferenceScreen)localObject1).setTitle(getEngineLabel());
    ((PreferenceScreen)localObject1).setKey(getEngineName());
    this.mEngineSettingsPreference.setTitle(getResources().getString(2131689617, new Object[] { getEngineLabel() }));
    this.mEngineSettingsIntent = this.mEnginesHelper.getSettingsIntent(getEngineName());
    if (this.mEngineSettingsIntent == null) {
      this.mEngineSettingsPreference.setEnabled(false);
    }
    this.mInstallVoicesPreference.setEnabled(false);
    if (paramBundle == null)
    {
      this.mLocalePreference.setEnabled(false);
      this.mLocalePreference.setEntries(new CharSequence[0]);
      this.mLocalePreference.setEntryValues(new CharSequence[0]);
      this.mVoiceDataDetails = ((Intent)getArguments().getParcelable("voices"));
      this.mTts = new TextToSpeech(getActivity().getApplicationContext(), this.mTtsInitListener, getEngineName());
      checkTtsData();
      getActivity().registerReceiver(this.mLanguagesChangedReceiver, new IntentFilter("android.speech.tts.engine.TTS_DATA_INSTALLED"));
      return;
    }
    localObject1 = paramBundle.getCharSequenceArray("locale_entries");
    Object localObject2 = paramBundle.getCharSequenceArray("locale_entry_values");
    paramBundle = paramBundle.getCharSequence("locale_value");
    this.mLocalePreference.setEntries((CharSequence[])localObject1);
    this.mLocalePreference.setEntryValues((CharSequence[])localObject2);
    localObject2 = this.mLocalePreference;
    if (paramBundle != null)
    {
      paramBundle = paramBundle.toString();
      label331:
      ((ListPreference)localObject2).setValue(paramBundle);
      paramBundle = this.mLocalePreference;
      if (localObject1.length <= 0) {
        break label362;
      }
    }
    label362:
    for (boolean bool = true;; bool = false)
    {
      paramBundle.setEnabled(bool);
      break;
      paramBundle = null;
      break label331;
    }
  }
  
  public void onDestroy()
  {
    getActivity().unregisterReceiver(this.mLanguagesChangedReceiver);
    this.mTts.shutdown();
    super.onDestroy();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (paramPreference == this.mLocalePreference)
    {
      paramPreference = (String)paramObject;
      if (!TextUtils.isEmpty(paramPreference)) {}
      for (paramPreference = this.mEnginesHelper.parseLocaleString(paramPreference);; paramPreference = null)
      {
        updateLanguageTo(paramPreference);
        return true;
      }
    }
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mInstallVoicesPreference)
    {
      installVoiceData();
      return true;
    }
    if (paramPreference == this.mEngineSettingsPreference)
    {
      startActivity(this.mEngineSettingsIntent);
      return true;
    }
    return false;
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putCharSequenceArray("locale_entries", this.mLocalePreference.getEntries());
    paramBundle.putCharSequenceArray("locale_entry_values", this.mLocalePreference.getEntryValues());
    paramBundle.putCharSequence("locale_value", this.mLocalePreference.getValue());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\tts\TtsEngineSettingsFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */