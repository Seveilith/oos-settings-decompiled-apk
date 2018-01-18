package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.KeyCharacterMap;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.content.PackageMonitor;
import com.android.internal.view.RotationPolicy;
import com.android.internal.view.RotationPolicy.RotationPolicyListener;
import com.android.settings.DialogCreatable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.oneplus.settings.SettingsBaseApplication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccessibilitySettings
  extends SettingsPreferenceFragment
  implements DialogCreatable, Preference.OnPreferenceChangeListener, Indexable
{
  private static final String AUTOCLICK_PREFERENCE_SCREEN = "autoclick_preference_screen";
  private static final String CAPTIONING_PREFERENCE_SCREEN = "captioning_preference_screen";
  private static final long DELAY_UPDATE_SERVICES_MILLIS = 1000L;
  private static final String DISPLAY_DALTONIZER_PREFERENCE_SCREEN = "daltonizer_preference_screen";
  private static final String DISPLAY_MAGNIFICATION_PREFERENCE_SCREEN = "screen_magnification_preference_screen";
  private static final String ENABLE_ACCESSIBILITY_GESTURE_PREFERENCE_SCREEN = "enable_global_gesture_preference_screen";
  static final String EXTRA_CHECKED = "checked";
  static final String EXTRA_COMPONENT_NAME = "component_name";
  static final String EXTRA_PREFERENCE_KEY = "preference_key";
  static final String EXTRA_SETTINGS_COMPONENT_NAME = "settings_component_name";
  static final String EXTRA_SETTINGS_TITLE = "settings_title";
  static final String EXTRA_SUMMARY = "summary";
  static final String EXTRA_TITLE = "title";
  private static final String FONT_SIZE_PREFERENCE_SCREEN = "font_size_preference_screen";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      PackageManager localPackageManager = paramAnonymousContext.getPackageManager();
      Object localObject1 = (AccessibilityManager)paramAnonymousContext.getSystemService("accessibility");
      String str = paramAnonymousContext.getResources().getString(2131692324);
      localObject1 = ((AccessibilityManager)localObject1).getInstalledAccessibilityServiceList();
      int j = ((List)localObject1).size();
      int i = 0;
      if (i < j)
      {
        AccessibilityServiceInfo localAccessibilityServiceInfo = (AccessibilityServiceInfo)((List)localObject1).get(i);
        if ((localAccessibilityServiceInfo == null) || (localAccessibilityServiceInfo.getResolveInfo() == null)) {}
        for (;;)
        {
          i += 1;
          break;
          Object localObject2 = localAccessibilityServiceInfo.getResolveInfo().serviceInfo;
          localObject2 = new ComponentName(((ServiceInfo)localObject2).packageName, ((ServiceInfo)localObject2).name);
          SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
          localSearchIndexableRaw.key = ((ComponentName)localObject2).flattenToString();
          localSearchIndexableRaw.title = localAccessibilityServiceInfo.getResolveInfo().loadLabel(localPackageManager).toString();
          localSearchIndexableRaw.summaryOn = paramAnonymousContext.getString(2131692353);
          localSearchIndexableRaw.summaryOff = paramAnonymousContext.getString(2131692354);
          localSearchIndexableRaw.screenTitle = str;
          localArrayList.add(localSearchIndexableRaw);
        }
      }
      return localArrayList;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230723;
      localArrayList.add(paramAnonymousContext);
      return localArrayList;
    }
  };
  private static final String SELECT_LONG_PRESS_TIMEOUT_PREFERENCE = "select_long_press_timeout_preference";
  private static final String SERVICES_CATEGORY = "services_category";
  private static final String SYSTEM_CATEGORY = "system_category";
  private static final String TOGGLE_HIGH_TEXT_CONTRAST_PREFERENCE = "toggle_high_text_contrast_preference";
  private static final String TOGGLE_INVERSION_PREFERENCE = "toggle_inversion_preference";
  private static final String TOGGLE_LARGE_POINTER_ICON = "toggle_large_pointer_icon";
  private static final String TOGGLE_LOCK_SCREEN_ROTATION_PREFERENCE = "toggle_lock_screen_rotation_preference";
  private static final String TOGGLE_MASTER_MONO = "toggle_master_mono";
  private static final String TOGGLE_POWER_BUTTON_ENDS_CALL_PREFERENCE = "toggle_power_button_ends_call_preference";
  private static final String TOGGLE_SPEAK_PASSWORD_PREFERENCE = "toggle_speak_password_preference";
  static final Set<ComponentName> sInstalledServices = new HashSet();
  private PreferenceScreen mAutoclickPreferenceScreen;
  private PreferenceScreen mCaptioningPreferenceScreen;
  private PreferenceScreen mDisplayDaltonizerPreferenceScreen;
  private PreferenceScreen mDisplayMagnificationPreferenceScreen;
  private DevicePolicyManager mDpm;
  private PreferenceScreen mFontSizePreferenceScreen;
  private PreferenceScreen mGlobalGesturePreferenceScreen;
  private final Handler mHandler = new Handler();
  private int mLongPressTimeoutDefault;
  private final Map<String, String> mLongPressTimeoutValuetoTitleMap = new HashMap();
  private Preference mNoServicesMessagePreference;
  private final RotationPolicy.RotationPolicyListener mRotationPolicyListener = new RotationPolicy.RotationPolicyListener()
  {
    public void onChange()
    {
      AccessibilitySettings.-wrap1(AccessibilitySettings.this);
    }
  };
  private ListPreference mSelectLongPressTimeoutPreference;
  private PreferenceCategory mServicesCategory;
  private final SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      AccessibilitySettings.-wrap2(AccessibilitySettings.this);
    }
  };
  private final PackageMonitor mSettingsPackageMonitor = new PackageMonitor()
  {
    private void sendUpdate()
    {
      AccessibilitySettings.-get0(AccessibilitySettings.this).postDelayed(AccessibilitySettings.-get2(AccessibilitySettings.this), 1000L);
    }
    
    public void onPackageAdded(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }
    
    public void onPackageAppeared(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }
    
    public void onPackageDisappeared(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }
    
    public void onPackageRemoved(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }
  };
  private PreferenceCategory mSystemsCategory;
  private SwitchPreference mToggleHighTextContrastPreference;
  private ContentObserver mToggleInversionObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      if (AccessibilitySettings.-get1(AccessibilitySettings.this) != null) {
        if (Settings.Secure.getInt(AccessibilitySettings.-wrap0(AccessibilitySettings.this), "accessibility_display_inversion_enabled", 0) == 0) {
          break label40;
        }
      }
      label40:
      for (paramAnonymousBoolean = true;; paramAnonymousBoolean = false)
      {
        AccessibilitySettings.-get1(AccessibilitySettings.this).setChecked(paramAnonymousBoolean);
        return;
      }
    }
  };
  private SwitchPreference mToggleInversionPreference;
  private SwitchPreference mToggleLargePointerIconPreference;
  private SwitchPreference mToggleLockScreenRotationPreference;
  private SwitchPreference mToggleMasterMonoPreference;
  private SwitchPreference mTogglePowerButtonEndsCallPreference;
  private SwitchPreference mToggleSpeakPasswordPreference;
  private final Runnable mUpdateRunnable = new Runnable()
  {
    public void run()
    {
      if (AccessibilitySettings.this.getActivity() != null) {
        AccessibilitySettings.-wrap2(AccessibilitySettings.this);
      }
    }
  };
  
  private void handleDisplayMagnificationPreferenceScreenClick()
  {
    boolean bool = true;
    Bundle localBundle = this.mDisplayMagnificationPreferenceScreen.getExtras();
    localBundle.putString("title", getString(2131692330));
    localBundle.putCharSequence("summary", getActivity().getResources().getText(2131692332));
    if (Settings.Secure.getInt(getContentResolver(), "accessibility_display_magnification_enabled", 0) == 1) {}
    for (;;)
    {
      localBundle.putBoolean("checked", bool);
      super.onPreferenceTreeClick(this.mDisplayMagnificationPreferenceScreen);
      return;
      bool = false;
    }
  }
  
  private void handleLockScreenRotationPreferenceClick()
  {
    Application localApplication = SettingsBaseApplication.mApplication;
    if (this.mToggleLockScreenRotationPreference.isChecked()) {}
    for (boolean bool = false;; bool = true)
    {
      RotationPolicy.setRotationLockForAccessibility(localApplication, bool);
      return;
    }
  }
  
  private void handleLongPressTimeoutPreferenceChange(String paramString)
  {
    Settings.Secure.putInt(getContentResolver(), "long_press_timeout", Integer.parseInt(paramString));
    this.mSelectLongPressTimeoutPreference.setSummary((CharSequence)this.mLongPressTimeoutValuetoTitleMap.get(paramString));
  }
  
  private void handleToggleEnableAccessibilityGesturePreferenceClick()
  {
    boolean bool = true;
    Bundle localBundle = this.mGlobalGesturePreferenceScreen.getExtras();
    localBundle.putString("title", getString(2131692333));
    localBundle.putString("summary", getString(2131692336));
    if (Settings.Global.getInt(getContentResolver(), "enable_accessibility_global_gesture_enabled", 0) == 1) {}
    for (;;)
    {
      localBundle.putBoolean("checked", bool);
      super.onPreferenceTreeClick(this.mGlobalGesturePreferenceScreen);
      return;
      bool = false;
    }
  }
  
  private void handleToggleInversionPreferenceChange(boolean paramBoolean)
  {
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "accessibility_display_inversion_enabled", i);
      return;
    }
  }
  
  private void handleToggleLargePointerIconPreferenceClick()
  {
    ContentResolver localContentResolver = getContentResolver();
    if (this.mToggleLargePointerIconPreference.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "accessibility_large_pointer_icon", i);
      return;
    }
  }
  
  private void handleToggleMasterMonoPreferenceClick()
  {
    ContentResolver localContentResolver = getContentResolver();
    if (this.mToggleMasterMonoPreference.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.System.putIntForUser(localContentResolver, "master_mono", i, -2);
      return;
    }
  }
  
  private void handleTogglePowerButtonEndsCallPreferenceClick()
  {
    ContentResolver localContentResolver = getContentResolver();
    if (this.mTogglePowerButtonEndsCallPreference.isChecked()) {}
    for (int i = 2;; i = 1)
    {
      Settings.Secure.putInt(localContentResolver, "incall_power_button_behavior", i);
      return;
    }
  }
  
  private void handleToggleSpeakPasswordPreferenceClick()
  {
    ContentResolver localContentResolver = getContentResolver();
    if (this.mToggleSpeakPasswordPreference.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "speak_password", i);
      return;
    }
  }
  
  private void handleToggleTextContrastPreferenceClick()
  {
    ContentResolver localContentResolver = getContentResolver();
    if (this.mToggleHighTextContrastPreference.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "high_text_contrast_enabled", i);
      return;
    }
  }
  
  private void initializeAllPreferences()
  {
    this.mServicesCategory = ((PreferenceCategory)findPreference("services_category"));
    this.mSystemsCategory = ((PreferenceCategory)findPreference("system_category"));
    this.mToggleHighTextContrastPreference = ((SwitchPreference)findPreference("toggle_high_text_contrast_preference"));
    this.mToggleInversionPreference = ((SwitchPreference)findPreference("toggle_inversion_preference"));
    this.mToggleInversionPreference.setOnPreferenceChangeListener(this);
    this.mTogglePowerButtonEndsCallPreference = ((SwitchPreference)findPreference("toggle_power_button_ends_call_preference"));
    if ((KeyCharacterMap.deviceHasKey(26)) && (Utils.isVoiceCapable(getActivity()))) {}
    for (;;)
    {
      this.mToggleLockScreenRotationPreference = ((SwitchPreference)findPreference("toggle_lock_screen_rotation_preference"));
      if (!RotationPolicy.isRotationSupported(getActivity())) {
        this.mSystemsCategory.removePreference(this.mToggleLockScreenRotationPreference);
      }
      this.mToggleSpeakPasswordPreference = ((SwitchPreference)findPreference("toggle_speak_password_preference"));
      this.mToggleLargePointerIconPreference = ((SwitchPreference)findPreference("toggle_large_pointer_icon"));
      this.mToggleMasterMonoPreference = ((SwitchPreference)findPreference("toggle_master_mono"));
      this.mSelectLongPressTimeoutPreference = ((ListPreference)findPreference("select_long_press_timeout_preference"));
      this.mSelectLongPressTimeoutPreference.setOnPreferenceChangeListener(this);
      if (this.mLongPressTimeoutValuetoTitleMap.size() != 0) {
        break;
      }
      String[] arrayOfString1 = getResources().getStringArray(2131427419);
      this.mLongPressTimeoutDefault = Integer.parseInt(arrayOfString1[0]);
      String[] arrayOfString2 = getResources().getStringArray(2131427418);
      int j = arrayOfString1.length;
      i = 0;
      while (i < j)
      {
        this.mLongPressTimeoutValuetoTitleMap.put(arrayOfString1[i], arrayOfString2[i]);
        i += 1;
      }
      this.mSystemsCategory.removePreference(this.mTogglePowerButtonEndsCallPreference);
    }
    this.mCaptioningPreferenceScreen = ((PreferenceScreen)findPreference("captioning_preference_screen"));
    this.mDisplayMagnificationPreferenceScreen = ((PreferenceScreen)findPreference("screen_magnification_preference_screen"));
    this.mFontSizePreferenceScreen = ((PreferenceScreen)findPreference("font_size_preference_screen"));
    this.mAutoclickPreferenceScreen = ((PreferenceScreen)findPreference("autoclick_preference_screen"));
    this.mDisplayDaltonizerPreferenceScreen = ((PreferenceScreen)findPreference("daltonizer_preference_screen"));
    this.mGlobalGesturePreferenceScreen = ((PreferenceScreen)findPreference("enable_global_gesture_preference_screen"));
    int i = getActivity().getResources().getInteger(17694800);
    if ((!KeyCharacterMap.deviceHasKey(26)) || (i != 1)) {
      this.mSystemsCategory.removePreference(this.mGlobalGesturePreferenceScreen);
    }
  }
  
  private void updateAllPreferences()
  {
    updateServicesPreferences();
    updateSystemPreferences();
  }
  
  private void updateAutoclickSummary(Preference paramPreference)
  {
    int i = 1;
    if (Settings.Secure.getInt(getContentResolver(), "accessibility_autoclick_enabled", 0) == 1) {}
    while (i == 0)
    {
      paramPreference.setSummary(2131692354);
      return;
      i = 0;
    }
    i = Settings.Secure.getInt(getContentResolver(), "accessibility_autoclick_delay", 600);
    paramPreference.setSummary(ToggleAutoclickPreferenceFragment.getAutoclickPreferenceSummary(getResources(), i));
  }
  
  private void updateFeatureSummary(String paramString, Preference paramPreference)
  {
    int i = 1;
    if (Settings.Secure.getInt(getContentResolver(), paramString, 0) == 1) {
      if (i == 0) {
        break label34;
      }
    }
    label34:
    for (i = 2131692353;; i = 2131692354)
    {
      paramPreference.setSummary(i);
      return;
      i = 0;
      break;
    }
  }
  
  private void updateFontSizeSummary(Preference paramPreference)
  {
    float f = Settings.System.getFloat(getContext().getContentResolver(), "font_scale", 1.0F);
    Resources localResources = getContext().getResources();
    paramPreference.setSummary(localResources.getStringArray(2131427379)[ToggleFontSizePreferenceFragment.fontSizeValueToIndex(f, localResources.getStringArray(2131427380))]);
  }
  
  private void updateLockScreenRotationCheckbox()
  {
    Application localApplication = SettingsBaseApplication.mApplication;
    SwitchPreference localSwitchPreference;
    if (localApplication != null)
    {
      localSwitchPreference = this.mToggleLockScreenRotationPreference;
      if (!RotationPolicy.isRotationLocked(localApplication)) {
        break label28;
      }
    }
    label28:
    for (boolean bool = false;; bool = true)
    {
      localSwitchPreference.setChecked(bool);
      return;
    }
  }
  
  private void updateMasterMono()
  {
    if (Settings.System.getIntForUser(getContentResolver(), "master_mono", 0, -2) == 1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mToggleMasterMonoPreference.setChecked(bool);
      return;
    }
  }
  
  private void updateServicesPreferences()
  {
    this.mServicesCategory.removeAll();
    List localList1 = AccessibilityManager.getInstance(getActivity()).getInstalledAccessibilityServiceList();
    Set localSet = AccessibilityUtils.getEnabledServicesFromSettings(getActivity());
    List localList2 = this.mDpm.getPermittedAccessibilityServices(UserHandle.myUserId());
    int i;
    int j;
    label67:
    AccessibilityServiceInfo localAccessibilityServiceInfo;
    RestrictedPreference localRestrictedPreference;
    String str2;
    Object localObject;
    ComponentName localComponentName;
    boolean bool1;
    label178:
    String str1;
    label192:
    boolean bool2;
    if (Settings.Secure.getInt(getContentResolver(), "accessibility_enabled", 0) == 1)
    {
      i = 1;
      j = 0;
      int k = localList1.size();
      if (j >= k) {
        break label494;
      }
      localAccessibilityServiceInfo = (AccessibilityServiceInfo)localList1.get(j);
      localRestrictedPreference = new RestrictedPreference(getActivity());
      str2 = localAccessibilityServiceInfo.getResolveInfo().loadLabel(getPackageManager()).toString();
      localObject = localAccessibilityServiceInfo.getResolveInfo().serviceInfo;
      localComponentName = new ComponentName(((ServiceInfo)localObject).packageName, ((ServiceInfo)localObject).name);
      localRestrictedPreference.setKey(localComponentName.flattenToString());
      localRestrictedPreference.setTitle(str2);
      if (i == 0) {
        break label429;
      }
      bool1 = localSet.contains(localComponentName);
      if (!bool1) {
        break label435;
      }
      str1 = getString(2131692353);
      String str3 = ((ServiceInfo)localObject).packageName;
      if (localList2 == null) {
        break label447;
      }
      bool2 = localList2.contains(str3);
      label215:
      if ((!bool2) && (!bool1)) {
        break label453;
      }
      localRestrictedPreference.setEnabled(true);
    }
    for (;;)
    {
      localRestrictedPreference.setSummary(str1);
      localRestrictedPreference.setOrder(j);
      localRestrictedPreference.setFragment(ToggleAccessibilityServicePreferenceFragment.class.getName());
      localRestrictedPreference.setPersistent(true);
      localObject = localRestrictedPreference.getExtras();
      ((Bundle)localObject).putString("preference_key", localRestrictedPreference.getKey());
      ((Bundle)localObject).putBoolean("checked", bool1);
      ((Bundle)localObject).putString("title", str2);
      str2 = localAccessibilityServiceInfo.loadDescription(getPackageManager());
      str1 = str2;
      if (TextUtils.isEmpty(str2)) {
        str1 = getString(2131692399);
      }
      ((Bundle)localObject).putString("summary", str1);
      str1 = localAccessibilityServiceInfo.getSettingsActivityName();
      if (!TextUtils.isEmpty(str1))
      {
        ((Bundle)localObject).putString("settings_title", getString(2131692352));
        ((Bundle)localObject).putString("settings_component_name", new ComponentName(localAccessibilityServiceInfo.getResolveInfo().serviceInfo.packageName, str1).flattenToString());
      }
      ((Bundle)localObject).putParcelable("component_name", localComponentName);
      this.mServicesCategory.addPreference(localRestrictedPreference);
      j += 1;
      break label67;
      i = 0;
      break;
      label429:
      bool1 = false;
      break label178;
      label435:
      str1 = getString(2131692354);
      break label192;
      label447:
      bool2 = true;
      break label215;
      label453:
      localObject = RestrictedLockUtils.checkIfAccessibilityServiceDisallowed(getActivity(), ((ServiceInfo)localObject).packageName, UserHandle.myUserId());
      if (localObject != null) {
        localRestrictedPreference.setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin)localObject);
      } else {
        localRestrictedPreference.setEnabled(false);
      }
    }
    label494:
    if (this.mServicesCategory.getPreferenceCount() == 0)
    {
      if (this.mNoServicesMessagePreference == null)
      {
        this.mNoServicesMessagePreference = new Preference(getPrefContext());
        this.mNoServicesMessagePreference.setPersistent(false);
        this.mNoServicesMessagePreference.setLayoutResource(2130969078);
        this.mNoServicesMessagePreference.setSelectable(false);
        this.mNoServicesMessagePreference.setSummary(getString(2131692398));
      }
      this.mServicesCategory.addPreference(this.mNoServicesMessagePreference);
    }
  }
  
  private void updateSystemPreferences()
  {
    Object localObject = this.mToggleHighTextContrastPreference;
    boolean bool;
    if (Settings.Secure.getInt(getContentResolver(), "high_text_contrast_enabled", 0) == 1)
    {
      bool = true;
      ((SwitchPreference)localObject).setChecked(bool);
      localObject = this.mToggleInversionPreference;
      if (Settings.Secure.getInt(getContentResolver(), "accessibility_display_inversion_enabled", 0) != 1) {
        break label286;
      }
      bool = true;
      label49:
      ((SwitchPreference)localObject).setChecked(bool);
      if ((KeyCharacterMap.deviceHasKey(26)) && (Utils.isVoiceCapable(getActivity())))
      {
        if (Settings.Secure.getInt(getContentResolver(), "incall_power_button_behavior", 1) != 2) {
          break label291;
        }
        bool = true;
        label89:
        this.mTogglePowerButtonEndsCallPreference.setChecked(bool);
      }
      updateLockScreenRotationCheckbox();
      if (Settings.Secure.getInt(getContentResolver(), "speak_password", 0) == 0) {
        break label296;
      }
      bool = true;
      label117:
      this.mToggleSpeakPasswordPreference.setChecked(bool);
      localObject = this.mToggleLargePointerIconPreference;
      if (Settings.Secure.getInt(getContentResolver(), "accessibility_large_pointer_icon", 0) == 0) {
        break label301;
      }
      bool = true;
      label146:
      ((SwitchPreference)localObject).setChecked(bool);
      updateMasterMono();
      localObject = String.valueOf(Settings.Secure.getInt(getContentResolver(), "long_press_timeout", this.mLongPressTimeoutDefault));
      this.mSelectLongPressTimeoutPreference.setValue((String)localObject);
      this.mSelectLongPressTimeoutPreference.setSummary((CharSequence)this.mLongPressTimeoutValuetoTitleMap.get(localObject));
      updateFeatureSummary("accessibility_captioning_enabled", this.mCaptioningPreferenceScreen);
      updateFeatureSummary("accessibility_display_magnification_enabled", this.mDisplayMagnificationPreferenceScreen);
      updateFeatureSummary("accessibility_display_daltonizer_enabled", this.mDisplayDaltonizerPreferenceScreen);
      updateFontSizeSummary(this.mFontSizePreferenceScreen);
      updateAutoclickSummary(this.mAutoclickPreferenceScreen);
      if (Settings.Global.getInt(getContentResolver(), "enable_accessibility_global_gesture_enabled", 0) != 1) {
        break label306;
      }
    }
    label286:
    label291:
    label296:
    label301:
    label306:
    for (int i = 1;; i = 0)
    {
      if (i == 0) {
        break label311;
      }
      this.mGlobalGesturePreferenceScreen.setSummary(2131692334);
      return;
      bool = false;
      break;
      bool = false;
      break label49;
      bool = false;
      break label89;
      bool = false;
      break label117;
      bool = false;
      break label146;
    }
    label311:
    this.mGlobalGesturePreferenceScreen.setSummary(2131692335);
  }
  
  protected int getHelpResource()
  {
    return 2131693008;
  }
  
  protected int getMetricsCategory()
  {
    return 2;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230723);
    initializeAllPreferences();
    this.mDpm = ((DevicePolicyManager)getActivity().getSystemService("device_policy"));
  }
  
  public void onPause()
  {
    this.mSettingsPackageMonitor.unregister();
    this.mSettingsContentObserver.unregister(getContentResolver());
    if (RotationPolicy.isRotationSupported(getActivity())) {
      RotationPolicy.unregisterRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
    }
    getContentResolver().unregisterContentObserver(this.mToggleInversionObserver);
    super.onPause();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (this.mSelectLongPressTimeoutPreference == paramPreference)
    {
      handleLongPressTimeoutPreferenceChange((String)paramObject);
      return true;
    }
    if (this.mToggleInversionPreference == paramPreference)
    {
      handleToggleInversionPreferenceChange(((Boolean)paramObject).booleanValue());
      return true;
    }
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if (this.mToggleHighTextContrastPreference == paramPreference)
    {
      handleToggleTextContrastPreferenceClick();
      return true;
    }
    if (this.mTogglePowerButtonEndsCallPreference == paramPreference)
    {
      handleTogglePowerButtonEndsCallPreferenceClick();
      return true;
    }
    if (this.mToggleLockScreenRotationPreference == paramPreference)
    {
      handleLockScreenRotationPreferenceClick();
      return true;
    }
    if (this.mToggleSpeakPasswordPreference == paramPreference)
    {
      handleToggleSpeakPasswordPreferenceClick();
      return true;
    }
    if (this.mToggleLargePointerIconPreference == paramPreference)
    {
      handleToggleLargePointerIconPreferenceClick();
      return true;
    }
    if (this.mToggleMasterMonoPreference == paramPreference)
    {
      handleToggleMasterMonoPreferenceClick();
      return true;
    }
    if (this.mGlobalGesturePreferenceScreen == paramPreference)
    {
      handleToggleEnableAccessibilityGesturePreferenceClick();
      return true;
    }
    if (this.mDisplayMagnificationPreferenceScreen == paramPreference)
    {
      handleDisplayMagnificationPreferenceScreenClick();
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onResume()
  {
    super.onResume();
    updateAllPreferences();
    this.mSettingsPackageMonitor.register(getActivity(), getActivity().getMainLooper(), false);
    this.mSettingsContentObserver.register(getContentResolver());
    if (RotationPolicy.isRotationSupported(getActivity())) {
      RotationPolicy.registerRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
    }
    getContentResolver().registerContentObserver(Settings.Secure.getUriFor("accessibility_display_inversion_enabled"), true, this.mToggleInversionObserver, -1);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\AccessibilitySettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */