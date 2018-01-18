package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.speech.tts.TtsEngines;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.InputDevice;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.TextServicesManager;
import com.android.internal.app.LocaleHelper;
import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocalePicker.LocaleInfo;
import com.android.internal.app.LocaleStore;
import com.android.internal.app.LocaleStore.LocaleInfo;
import com.android.settings.Settings.KeyboardLayoutPickerActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SubSettings;
import com.android.settings.UserDictionarySettings;
import com.android.settings.Utils;
import com.android.settings.VoiceInputOutputSettings;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.oneplus.settings.utils.OPUtils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class InputMethodAndLanguageSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, InputManager.InputDeviceListener, KeyboardLayoutDialogFragment.OnSetupKeyboardLayoutsListener, Indexable, InputMethodPreference.OnSavePreferenceListener
{
  private static final String KEY_CURRENT_INPUT_METHOD = "current_input_method";
  private static final String KEY_INPUT_METHOD_SELECTOR = "input_method_selector";
  private static final String KEY_PHONE_LANGUAGE = "phone_language";
  private static final String KEY_PHONE_LANGUAGE_H2OS = "phone_language_h2os";
  private static final String KEY_PREVIOUSLY_ENABLED_SUBTYPES = "previously_enabled_subtypes";
  private static final String KEY_SPELL_CHECKERS = "spellcheckers_settings";
  private static final String KEY_USER_DICTIONARY_SETTINGS = "key_user_dictionary_settings";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      String str = paramAnonymousContext.getString(2131692224);
      if (paramAnonymousContext.getAssets().getLocales().length > 1)
      {
        localObject1 = InputMethodAndLanguageSettings.-wrap1(paramAnonymousContext);
        localObject2 = new SearchIndexableRaw(paramAnonymousContext);
        ((SearchIndexableRaw)localObject2).key = "phone_language";
        ((SearchIndexableRaw)localObject2).title = paramAnonymousContext.getString(2131692226);
        ((SearchIndexableRaw)localObject2).summaryOn = ((String)localObject1);
        ((SearchIndexableRaw)localObject2).summaryOff = ((String)localObject1);
        ((SearchIndexableRaw)localObject2).screenTitle = str;
        localArrayList.add(localObject2);
      }
      Object localObject1 = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject1).key = "spellcheckers_settings";
      ((SearchIndexableRaw)localObject1).title = paramAnonymousContext.getString(2131692897);
      ((SearchIndexableRaw)localObject1).screenTitle = str;
      ((SearchIndexableRaw)localObject1).keywords = paramAnonymousContext.getString(2131693135);
      localArrayList.add(localObject1);
      if (UserDictionaryList.getUserDictionaryLocalesSet(paramAnonymousContext) != null)
      {
        localObject1 = new SearchIndexableRaw(paramAnonymousContext);
        ((SearchIndexableRaw)localObject1).key = "user_dict_settings";
        ((SearchIndexableRaw)localObject1).title = paramAnonymousContext.getString(2131692263);
        ((SearchIndexableRaw)localObject1).screenTitle = str;
        localArrayList.add(localObject1);
      }
      localObject1 = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject1).key = "keyboard_settings";
      ((SearchIndexableRaw)localObject1).title = paramAnonymousContext.getString(2131692225);
      ((SearchIndexableRaw)localObject1).screenTitle = str;
      ((SearchIndexableRaw)localObject1).keywords = paramAnonymousContext.getString(2131693149);
      localArrayList.add(localObject1);
      Object localObject2 = InputMethodSettingValuesWrapper.getInstance(paramAnonymousContext);
      ((InputMethodSettingValuesWrapper)localObject2).refreshAllInputMethodAndSubtypes();
      localObject1 = ((InputMethodSettingValuesWrapper)localObject2).getCurrentInputMethodName(paramAnonymousContext).toString();
      Object localObject3 = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject3).key = "current_input_method";
      ((SearchIndexableRaw)localObject3).title = paramAnonymousContext.getString(2131692287);
      ((SearchIndexableRaw)localObject3).summaryOn = ((String)localObject1);
      ((SearchIndexableRaw)localObject3).summaryOff = ((String)localObject1);
      ((SearchIndexableRaw)localObject3).screenTitle = str;
      localArrayList.add(localObject3);
      localObject1 = (InputMethodManager)paramAnonymousContext.getSystemService("input_method");
      localObject2 = ((InputMethodSettingValuesWrapper)localObject2).getInputMethodList();
      if (localObject2 == null) {}
      Object localObject4;
      Object localObject5;
      for (int i = 0;; i = ((List)localObject2).size())
      {
        j = 0;
        while (j < i)
        {
          localObject3 = (InputMethodInfo)((List)localObject2).get(j);
          localObject4 = InputMethodAndSubtypeUtil.getSubtypeLocaleNameListAsSentence(((InputMethodManager)localObject1).getEnabledInputMethodSubtypeList((InputMethodInfo)localObject3, true), paramAnonymousContext, (InputMethodInfo)localObject3);
          localObject5 = ((InputMethodInfo)localObject3).getServiceInfo();
          localObject5 = new ComponentName(((ServiceInfo)localObject5).packageName, ((ServiceInfo)localObject5).name);
          SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
          localSearchIndexableRaw.key = ((ComponentName)localObject5).flattenToString();
          localSearchIndexableRaw.title = ((InputMethodInfo)localObject3).loadLabel(paramAnonymousContext.getPackageManager()).toString();
          localSearchIndexableRaw.summaryOn = ((String)localObject4);
          localSearchIndexableRaw.summaryOff = ((String)localObject4);
          localSearchIndexableRaw.screenTitle = str;
          localArrayList.add(localSearchIndexableRaw);
          j += 1;
        }
      }
      localObject2 = (InputManager)paramAnonymousContext.getSystemService("input");
      int j = 0;
      localObject3 = InputDevice.getDeviceIds();
      i = 0;
      if (i < localObject3.length)
      {
        localObject4 = InputDevice.getDevice(localObject3[i]);
        int k = j;
        if (localObject4 != null)
        {
          k = j;
          if (!((InputDevice)localObject4).isVirtual())
          {
            k = j;
            if (((InputDevice)localObject4).isFullKeyboard())
            {
              k = 1;
              localObject1 = ((InputManager)localObject2).getCurrentKeyboardLayoutForInputDevice(((InputDevice)localObject4).getIdentifier());
              if (localObject1 == null) {
                break label704;
              }
              localObject1 = ((InputManager)localObject2).getKeyboardLayout((String)localObject1);
              label620:
              if (localObject1 == null) {
                break label710;
              }
            }
          }
        }
        label704:
        label710:
        for (localObject1 = ((KeyboardLayout)localObject1).toString();; localObject1 = paramAnonymousContext.getString(2131692261))
        {
          localObject5 = new SearchIndexableRaw(paramAnonymousContext);
          ((SearchIndexableRaw)localObject5).key = ((InputDevice)localObject4).getName();
          ((SearchIndexableRaw)localObject5).title = ((InputDevice)localObject4).getName();
          ((SearchIndexableRaw)localObject5).summaryOn = ((String)localObject1);
          ((SearchIndexableRaw)localObject5).summaryOff = ((String)localObject1);
          ((SearchIndexableRaw)localObject5).screenTitle = str;
          localArrayList.add(localObject5);
          i += 1;
          j = k;
          break;
          localObject1 = null;
          break label620;
        }
      }
      if (j != 0)
      {
        localObject1 = new SearchIndexableRaw(paramAnonymousContext);
        ((SearchIndexableRaw)localObject1).key = "builtin_keyboard_settings";
        ((SearchIndexableRaw)localObject1).title = paramAnonymousContext.getString(2131692300);
        ((SearchIndexableRaw)localObject1).screenTitle = str;
        localArrayList.add(localObject1);
      }
      if (!new TtsEngines(paramAnonymousContext).getEngines().isEmpty())
      {
        localObject1 = new SearchIndexableRaw(paramAnonymousContext);
        ((SearchIndexableRaw)localObject1).key = "tts_settings";
        ((SearchIndexableRaw)localObject1).title = paramAnonymousContext.getString(2131689596);
        ((SearchIndexableRaw)localObject1).screenTitle = str;
        ((SearchIndexableRaw)localObject1).keywords = paramAnonymousContext.getString(2131693137);
        localArrayList.add(localObject1);
      }
      localObject1 = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject1).key = "pointer_settings_category";
      ((SearchIndexableRaw)localObject1).title = paramAnonymousContext.getString(2131692253);
      ((SearchIndexableRaw)localObject1).screenTitle = str;
      localArrayList.add(localObject1);
      localObject1 = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject1).key = "pointer_speed";
      ((SearchIndexableRaw)localObject1).title = paramAnonymousContext.getString(2131692254);
      ((SearchIndexableRaw)localObject1).screenTitle = str;
      localArrayList.add(localObject1);
      if (InputMethodAndLanguageSettings.-wrap0())
      {
        localObject1 = new SearchIndexableRaw(paramAnonymousContext);
        ((SearchIndexableRaw)localObject1).key = "vibrate_input_devices";
        ((SearchIndexableRaw)localObject1).title = paramAnonymousContext.getString(2131692256);
        ((SearchIndexableRaw)localObject1).summaryOn = paramAnonymousContext.getString(2131692257);
        ((SearchIndexableRaw)localObject1).summaryOff = paramAnonymousContext.getString(2131692257);
        ((SearchIndexableRaw)localObject1).screenTitle = str;
        localArrayList.add(localObject1);
      }
      return localArrayList;
    }
  };
  private static final boolean SHOW_INPUT_METHOD_SWITCHER_SETTINGS = false;
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new InputMethodAndLanguageSettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
    }
  };
  private int mDefaultInputMethodSelectorVisibility = 0;
  private DevicePolicyManager mDpm;
  private PreferenceCategory mGameControllerCategory;
  private Handler mHandler;
  private PreferenceCategory mHardKeyboardCategory;
  private final ArrayList<PreferenceScreen> mHardKeyboardPreferenceList = new ArrayList();
  private InputManager mIm;
  private InputMethodManager mImm;
  private final ArrayList<InputMethodPreference> mInputMethodPreferenceList = new ArrayList();
  private InputMethodSettingValuesWrapper mInputMethodSettingValues;
  private Intent mIntentWaitingForResult;
  private PreferenceCategory mKeyboardSettingsCategory;
  private Preference mLanguagePref;
  private Preference mLanguagePrefH2OS;
  private SettingsObserver mSettingsObserver;
  private ListPreference mShowInputMethodSelectorPref;
  private boolean mShowsOnlyFullImeAndKeyboardList;
  
  private static String getLocaleName(Context paramContext)
  {
    Locale localLocale = paramContext.getResources().getConfiguration().locale;
    paramContext = LocalePicker.getAllAssetLocales(paramContext, true).iterator();
    while (paramContext.hasNext())
    {
      LocalePicker.LocaleInfo localLocaleInfo = (LocalePicker.LocaleInfo)paramContext.next();
      if (localLocaleInfo.getLocale().equals(localLocale)) {
        return localLocaleInfo.getLabel();
      }
    }
    return localLocale.getDisplayName(localLocale);
  }
  
  private static String getLocaleNames(Context paramContext)
  {
    if (!OPUtils.isO2()) {
      return getLocaleName(paramContext);
    }
    LocaleList localLocaleList = LocalePicker.getLocales();
    Locale[] arrayOfLocale = new Locale[localLocaleList.size()];
    int i = 0;
    while (i < localLocaleList.size())
    {
      localLocale = localLocaleList.get(i);
      paramContext = localLocale;
      if ("zh-CN".equals(LocaleStore.getLocaleInfo(localLocale).getId())) {
        paramContext = Locale.forLanguageTag("zh-Hans-CN");
      }
      arrayOfLocale[i] = paramContext;
      i += 1;
    }
    paramContext = new LocaleList(arrayOfLocale);
    Locale localLocale = Locale.getDefault();
    return LocaleHelper.toSentenceCase(LocaleHelper.getDisplayLocaleList(paramContext, localLocale, 4), localLocale);
  }
  
  private static boolean haveInputDeviceWithVibrator()
  {
    int[] arrayOfInt = InputDevice.getDeviceIds();
    int i = 0;
    if (i < arrayOfInt.length)
    {
      InputDevice localInputDevice = InputDevice.getDevice(arrayOfInt[i]);
      if ((localInputDevice == null) || (localInputDevice.isVirtual())) {}
      while (!localInputDevice.getVibrator().hasVibrator())
      {
        i += 1;
        break;
      }
      return true;
    }
    return false;
  }
  
  private int loadInputMethodSelectorVisibility()
  {
    return Settings.Secure.getInt(getContentResolver(), "input_method_selector_visibility", this.mDefaultInputMethodSelectorVisibility);
  }
  
  private HashMap<String, HashSet<String>> loadPreviouslyEnabledSubtypeIdsMap()
  {
    return InputMethodAndSubtypeUtil.parseInputMethodsAndSubtypesString(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("previously_enabled_subtypes", null));
  }
  
  private void restorePreviouslyEnabledSubtypesOf(InputMethodInfo paramInputMethodInfo)
  {
    HashMap localHashMap = loadPreviouslyEnabledSubtypeIdsMap();
    paramInputMethodInfo = paramInputMethodInfo.getId();
    HashSet localHashSet = (HashSet)localHashMap.remove(paramInputMethodInfo);
    if (localHashSet == null) {
      return;
    }
    savePreviouslyEnabledSubtypeIdsMap(localHashMap);
    InputMethodAndSubtypeUtil.enableInputMethodSubtypesOf(getContentResolver(), paramInputMethodInfo, localHashSet);
  }
  
  private void saveEnabledSubtypesOf(InputMethodInfo paramInputMethodInfo)
  {
    HashSet localHashSet = new HashSet();
    Object localObject = this.mImm.getEnabledInputMethodSubtypeList(paramInputMethodInfo, true).iterator();
    while (((Iterator)localObject).hasNext()) {
      localHashSet.add(Integer.toString(((InputMethodSubtype)((Iterator)localObject).next()).hashCode()));
    }
    localObject = loadPreviouslyEnabledSubtypeIdsMap();
    ((HashMap)localObject).put(paramInputMethodInfo.getId(), localHashSet);
    savePreviouslyEnabledSubtypeIdsMap((HashMap)localObject);
  }
  
  private void saveInputMethodSelectorVisibility(String paramString)
  {
    try
    {
      int i = Integer.valueOf(paramString).intValue();
      Settings.Secure.putInt(getContentResolver(), "input_method_selector_visibility", i);
      updateInputMethodSelectorSummary(i);
      return;
    }
    catch (NumberFormatException paramString) {}
  }
  
  private void savePreviouslyEnabledSubtypeIdsMap(HashMap<String, HashSet<String>> paramHashMap)
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    paramHashMap = InputMethodAndSubtypeUtil.buildInputMethodsAndSubtypesString(paramHashMap);
    localSharedPreferences.edit().putString("previously_enabled_subtypes", paramHashMap).apply();
  }
  
  private void showKeyboardLayoutDialog(InputDeviceIdentifier paramInputDeviceIdentifier)
  {
    if ((KeyboardLayoutDialogFragment)getFragmentManager().findFragmentByTag("keyboardLayout") == null)
    {
      paramInputDeviceIdentifier = new KeyboardLayoutDialogFragment(paramInputDeviceIdentifier);
      paramInputDeviceIdentifier.setTargetFragment(this, 0);
      paramInputDeviceIdentifier.show(getActivity().getFragmentManager(), "keyboardLayout");
    }
  }
  
  private void updateCurrentImeName()
  {
    Object localObject2 = getActivity();
    if ((localObject2 == null) || (this.mImm == null)) {
      return;
    }
    Preference localPreference = getPreferenceScreen().findPreference("current_input_method");
    if (localPreference != null)
    {
      localObject2 = this.mInputMethodSettingValues.getCurrentInputMethodName((Context)localObject2);
      if (TextUtils.isEmpty((CharSequence)localObject2)) {}
    }
    try
    {
      localPreference.setSummary((CharSequence)localObject2);
      return;
    }
    finally
    {
      localObject1 = finally;
      throw ((Throwable)localObject1);
    }
  }
  
  private void updateGameControllers()
  {
    boolean bool = true;
    if (haveInputDeviceWithVibrator())
    {
      getPreferenceScreen().addPreference(this.mGameControllerCategory);
      SwitchPreference localSwitchPreference = (SwitchPreference)this.mGameControllerCategory.findPreference("vibrate_input_devices");
      if (Settings.System.getInt(getContentResolver(), "vibrate_input_devices", 1) > 0) {}
      for (;;)
      {
        localSwitchPreference.setChecked(bool);
        return;
        bool = false;
      }
    }
    getPreferenceScreen().removePreference(this.mGameControllerCategory);
  }
  
  private void updateHardKeyboards()
  {
    if (this.mHardKeyboardCategory == null) {
      return;
    }
    this.mHardKeyboardPreferenceList.clear();
    int[] arrayOfInt = InputDevice.getDeviceIds();
    int i = 0;
    Object localObject;
    if (i < arrayOfInt.length)
    {
      InputDevice localInputDevice = InputDevice.getDevice(arrayOfInt[i]);
      if ((localInputDevice == null) || (localInputDevice.isVirtual())) {}
      while (!localInputDevice.isFullKeyboard())
      {
        i += 1;
        break;
      }
      final InputDeviceIdentifier localInputDeviceIdentifier = localInputDevice.getIdentifier();
      localObject = this.mIm.getCurrentKeyboardLayoutForInputDevice(localInputDeviceIdentifier);
      label96:
      PreferenceScreen localPreferenceScreen;
      if (localObject != null)
      {
        localObject = this.mIm.getKeyboardLayout((String)localObject);
        localPreferenceScreen = new PreferenceScreen(getPrefContext(), null);
        localPreferenceScreen.setTitle(localInputDevice.getName());
        if (localObject == null) {
          break label166;
        }
        localPreferenceScreen.setSummary(((KeyboardLayout)localObject).toString());
      }
      for (;;)
      {
        localPreferenceScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
          public boolean onPreferenceClick(Preference paramAnonymousPreference)
          {
            InputMethodAndLanguageSettings.-wrap2(InputMethodAndLanguageSettings.this, localInputDeviceIdentifier);
            return true;
          }
        });
        this.mHardKeyboardPreferenceList.add(localPreferenceScreen);
        break;
        localObject = null;
        break label96;
        label166:
        localPreferenceScreen.setSummary(2131692261);
      }
    }
    if (!this.mHardKeyboardPreferenceList.isEmpty())
    {
      for (i = this.mHardKeyboardCategory.getPreferenceCount();; i = j)
      {
        j = i - 1;
        if (i <= 0) {
          break;
        }
        localObject = this.mHardKeyboardCategory.getPreference(j);
        if (((Preference)localObject).getOrder() < 1000) {
          this.mHardKeyboardCategory.removePreference((Preference)localObject);
        }
      }
      Collections.sort(this.mHardKeyboardPreferenceList);
      int j = this.mHardKeyboardPreferenceList.size();
      i = 0;
      while (i < j)
      {
        localObject = (Preference)this.mHardKeyboardPreferenceList.get(i);
        ((Preference)localObject).setOrder(i);
        this.mHardKeyboardCategory.addPreference((Preference)localObject);
        i += 1;
      }
      getPreferenceScreen().addPreference(this.mHardKeyboardCategory);
      return;
    }
    getPreferenceScreen().removePreference(this.mHardKeyboardCategory);
  }
  
  private void updateInputDevices()
  {
    updateHardKeyboards();
    updateGameControllers();
  }
  
  private void updateInputMethodPreferenceViews()
  {
    if (this.mKeyboardSettingsCategory == null) {
      return;
    }
    synchronized (this.mInputMethodPreferenceList)
    {
      Iterator localIterator = this.mInputMethodPreferenceList.iterator();
      if (localIterator.hasNext())
      {
        localObject3 = (InputMethodPreference)localIterator.next();
        this.mKeyboardSettingsCategory.removePreference((Preference)localObject3);
      }
    }
    this.mInputMethodPreferenceList.clear();
    Object localObject3 = this.mDpm.getPermittedInputMethodsForCurrentUser();
    Context localContext = getPrefContext();
    final Object localObject2;
    int j;
    int i;
    Object localObject4;
    if (this.mShowsOnlyFullImeAndKeyboardList)
    {
      localObject2 = this.mInputMethodSettingValues.getInputMethodList();
      break label300;
      if (j >= i) {
        break label207;
      }
      localObject4 = (InputMethodInfo)((List)localObject2).get(j);
      if (localObject3 == null) {
        break label312;
      }
    }
    label207:
    label300:
    label312:
    for (boolean bool = ((List)localObject3).contains(((InputMethodInfo)localObject4).getPackageName());; bool = true)
    {
      localObject4 = new InputMethodPreference(localContext, (InputMethodInfo)localObject4, this.mShowsOnlyFullImeAndKeyboardList, bool, this);
      this.mInputMethodPreferenceList.add(localObject4);
      j += 1;
      break;
      localObject2 = this.mImm.getEnabledInputMethodList();
      while (localObject2 != null)
      {
        i = ((List)localObject2).size();
        break;
        localObject2 = Collator.getInstance();
        Collections.sort(this.mInputMethodPreferenceList, new Comparator()
        {
          public int compare(InputMethodPreference paramAnonymousInputMethodPreference1, InputMethodPreference paramAnonymousInputMethodPreference2)
          {
            return paramAnonymousInputMethodPreference1.compareTo(paramAnonymousInputMethodPreference2, localObject2);
          }
        });
        j = 0;
        while (j < i)
        {
          localObject2 = (InputMethodPreference)this.mInputMethodPreferenceList.get(j);
          this.mKeyboardSettingsCategory.addPreference((Preference)localObject2);
          InputMethodAndSubtypeUtil.removeUnnecessaryNonPersistentPreference((Preference)localObject2);
          ((InputMethodPreference)localObject2).updatePreferenceViews();
          j += 1;
        }
        updateCurrentImeName();
        InputMethodAndSubtypeUtil.loadInputMethodSubtypeList(this, getContentResolver(), this.mInputMethodSettingValues.getInputMethodList(), null);
        return;
      }
      i = 0;
      j = 0;
      break;
    }
  }
  
  private void updateInputMethodSelectorSummary(int paramInt)
  {
    String[] arrayOfString = getResources().getStringArray(2131427456);
    if (arrayOfString.length > paramInt)
    {
      this.mShowInputMethodSelectorPref.setSummary(arrayOfString[paramInt]);
      this.mShowInputMethodSelectorPref.setValue(String.valueOf(paramInt));
    }
  }
  
  private void updateUserDictionaryPreference(Preference paramPreference)
  {
    if (paramPreference == null) {
      return;
    }
    final TreeSet localTreeSet = UserDictionaryList.getUserDictionaryLocalesSet(getActivity());
    if (localTreeSet == null)
    {
      getPreferenceScreen().removePreference(paramPreference);
      return;
    }
    paramPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        Bundle localBundle = new Bundle();
        if (localTreeSet.size() <= 1) {
          if (!localTreeSet.isEmpty()) {
            localBundle.putString("locale", (String)localTreeSet.first());
          }
        }
        for (paramAnonymousPreference = UserDictionarySettings.class;; paramAnonymousPreference = UserDictionaryList.class)
        {
          InputMethodAndLanguageSettings.this.startFragment(InputMethodAndLanguageSettings.this, paramAnonymousPreference.getCanonicalName(), -1, -1, localBundle);
          return true;
        }
      }
    });
  }
  
  protected int getMetricsCategory()
  {
    return 57;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (this.mIntentWaitingForResult != null)
    {
      paramIntent = (InputDeviceIdentifier)this.mIntentWaitingForResult.getParcelableExtra("input_device_identifier");
      this.mIntentWaitingForResult = null;
      showKeyboardLayoutDialog(paramIntent);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230774);
    paramBundle = getActivity();
    this.mImm = ((InputMethodManager)getSystemService("input_method"));
    this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(paramBundle);
    try
    {
      this.mDefaultInputMethodSelectorVisibility = Integer.valueOf(getString(2131689901)).intValue();
      if (paramBundle.getAssets().getLocales().length == 1)
      {
        getPreferenceScreen().removePreference(findPreference("phone_language"));
        getPreferenceScreen().removePreference(findPreference("phone_language_h2os"));
      }
      for (;;)
      {
        new VoiceInputOutputSettings(this).onCreate();
        this.mHardKeyboardCategory = ((PreferenceCategory)findPreference("hard_keyboard"));
        this.mKeyboardSettingsCategory = ((PreferenceCategory)findPreference("keyboard_settings_category"));
        this.mGameControllerCategory = ((PreferenceCategory)findPreference("game_controller_settings_category"));
        Intent localIntent1 = paramBundle.getIntent();
        this.mShowsOnlyFullImeAndKeyboardList = "android.settings.INPUT_METHOD_SETTINGS".equals(localIntent1.getAction());
        if (this.mShowsOnlyFullImeAndKeyboardList)
        {
          getPreferenceScreen().removeAll();
          if (this.mHardKeyboardCategory != null) {
            getPreferenceScreen().addPreference(this.mHardKeyboardCategory);
          }
          if (this.mKeyboardSettingsCategory != null)
          {
            this.mKeyboardSettingsCategory.removeAll();
            getPreferenceScreen().addPreference(this.mKeyboardSettingsCategory);
          }
        }
        this.mIm = ((InputManager)paramBundle.getSystemService("input"));
        updateInputDevices();
        Preference localPreference = findPreference("spellcheckers_settings");
        if (localPreference != null)
        {
          InputMethodAndSubtypeUtil.removeUnnecessaryNonPersistentPreference(localPreference);
          Intent localIntent2 = new Intent("android.intent.action.MAIN");
          localIntent2.setClass(paramBundle, SubSettings.class);
          localIntent2.putExtra(":settings:show_fragment", SpellCheckersSettings.class.getName());
          localIntent2.putExtra(":settings:show_fragment_title_resid", 2131692897);
          localPreference.setIntent(localIntent2);
        }
        this.mHandler = new Handler();
        this.mSettingsObserver = new SettingsObserver(this.mHandler, paramBundle);
        this.mDpm = ((DevicePolicyManager)getActivity().getSystemService("device_policy"));
        paramBundle = (InputDeviceIdentifier)localIntent1.getParcelableExtra("input_device_identifier");
        if ((this.mShowsOnlyFullImeAndKeyboardList) && (paramBundle != null)) {
          showKeyboardLayoutDialog(paramBundle);
        }
        return;
        if (OPUtils.isO2())
        {
          this.mLanguagePref = findPreference("phone_language");
          getPreferenceScreen().removePreference(findPreference("phone_language_h2os"));
        }
        else
        {
          this.mLanguagePrefH2OS = findPreference("phone_language_h2os");
          getPreferenceScreen().removePreference(findPreference("phone_language"));
        }
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
  }
  
  public void onInputDeviceAdded(int paramInt)
  {
    updateInputDevices();
  }
  
  public void onInputDeviceChanged(int paramInt)
  {
    updateInputDevices();
  }
  
  public void onInputDeviceRemoved(int paramInt)
  {
    updateInputDevices();
  }
  
  public void onPause()
  {
    super.onPause();
    this.mIm.unregisterInputDeviceListener(this);
    this.mSettingsObserver.pause();
    ContentResolver localContentResolver = getContentResolver();
    List localList = this.mInputMethodSettingValues.getInputMethodList();
    if (this.mHardKeyboardPreferenceList.isEmpty()) {}
    for (boolean bool = false;; bool = true)
    {
      InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(this, localContentResolver, localList, bool);
      return;
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    int i = 0;
    if (Utils.isMonkeyRunning()) {
      return false;
    }
    if ((paramPreference instanceof PreferenceScreen)) {
      if (paramPreference.getFragment() == null) {}
    }
    SwitchPreference localSwitchPreference;
    do
    {
      do
      {
        for (;;)
        {
          return super.onPreferenceTreeClick(paramPreference);
          if ("current_input_method".equals(paramPreference.getKey())) {
            ((InputMethodManager)getSystemService("input_method")).showInputMethodPicker(false);
          }
        }
      } while (!(paramPreference instanceof SwitchPreference));
      localSwitchPreference = (SwitchPreference)paramPreference;
    } while (localSwitchPreference != this.mGameControllerCategory.findPreference("vibrate_input_devices"));
    paramPreference = getContentResolver();
    if (localSwitchPreference.isChecked()) {
      i = 1;
    }
    Settings.System.putInt(paramPreference, "vibrate_input_devices", i);
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    this.mSettingsObserver.resume();
    this.mIm.registerInputDeviceListener(this, null);
    Object localObject1 = findPreference("spellcheckers_settings");
    Object localObject2;
    if (localObject1 != null)
    {
      localObject2 = (TextServicesManager)getSystemService("textservices");
      if (((TextServicesManager)localObject2).isSpellCheckerEnabled()) {
        break label135;
      }
      ((Preference)localObject1).setSummary(2131693336);
    }
    for (;;)
    {
      if (!this.mShowsOnlyFullImeAndKeyboardList)
      {
        if (this.mLanguagePref != null)
        {
          localObject1 = getLocaleNames(getActivity());
          this.mLanguagePref.setSummary((CharSequence)localObject1);
        }
        if (this.mLanguagePrefH2OS != null)
        {
          localObject1 = getLocaleNames(getActivity());
          this.mLanguagePrefH2OS.setSummary((CharSequence)localObject1);
        }
        updateUserDictionaryPreference(findPreference("key_user_dictionary_settings"));
      }
      updateInputDevices();
      this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
      updateInputMethodPreferenceViews();
      return;
      label135:
      localObject2 = ((TextServicesManager)localObject2).getCurrentSpellChecker();
      if (localObject2 != null) {
        ((Preference)localObject1).setSummary(((SpellCheckerInfo)localObject2).loadLabel(getPackageManager()));
      } else {
        ((Preference)localObject1).setSummary(2131693672);
      }
    }
  }
  
  public void onSaveInputMethodPreference(InputMethodPreference paramInputMethodPreference)
  {
    InputMethodInfo localInputMethodInfo = paramInputMethodPreference.getInputMethodInfo();
    if (!paramInputMethodPreference.isChecked()) {
      saveEnabledSubtypesOf(localInputMethodInfo);
    }
    if (getResources().getConfiguration().keyboard == 2) {}
    for (boolean bool = true;; bool = false)
    {
      InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(this, getContentResolver(), this.mImm.getInputMethodList(), bool);
      this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
      if (paramInputMethodPreference.isChecked()) {
        restorePreviouslyEnabledSubtypesOf(localInputMethodInfo);
      }
      paramInputMethodPreference = this.mInputMethodPreferenceList.iterator();
      while (paramInputMethodPreference.hasNext()) {
        ((InputMethodPreference)paramInputMethodPreference.next()).updatePreferenceViews();
      }
    }
  }
  
  public void onSetupKeyboardLayouts(InputDeviceIdentifier paramInputDeviceIdentifier)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.setClass(getActivity(), Settings.KeyboardLayoutPickerActivity.class);
    localIntent.putExtra("input_device_identifier", paramInputDeviceIdentifier);
    this.mIntentWaitingForResult = localIntent;
    startActivityForResult(localIntent, 0);
  }
  
  private class SettingsObserver
    extends ContentObserver
  {
    private Context mContext;
    
    public SettingsObserver(Handler paramHandler, Context paramContext)
    {
      super();
      this.mContext = paramContext;
    }
    
    public void onChange(boolean paramBoolean)
    {
      InputMethodAndLanguageSettings.-wrap3(InputMethodAndLanguageSettings.this);
    }
    
    public void pause()
    {
      this.mContext.getContentResolver().unregisterContentObserver(this);
    }
    
    public void resume()
    {
      ContentResolver localContentResolver = this.mContext.getContentResolver();
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, this);
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("selected_input_method_subtype"), false, this);
    }
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mSummaryLoader;
    
    public SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        String str = InputMethodAndLanguageSettings.-wrap1(this.mContext);
        this.mSummaryLoader.setSummary(this, str);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\InputMethodAndLanguageSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */