package com.android.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.UiModeManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.IPowerManager.Stub;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;
import com.android.internal.view.RotationPolicy;
import com.android.internal.view.RotationPolicy.RotationPolicyListener;
import com.android.settings.accessibility.ToggleFontSizePreferenceFragment;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.OneplusColorManager;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.ColorPickerPreference;
import com.oneplus.settings.ui.OPBrightnessSeekbarPreferenceCategory;
import com.oneplus.settings.ui.OPBrightnessSeekbarPreferenceCategory.OPCallbackBrightness;
import com.oneplus.settings.ui.OPNightModeLevelPreference;
import com.oneplus.settings.ui.OPNightModeLevelPreference.OPNightModeLevelPreferenceChangeListener;
import com.oneplus.settings.utils.OPUtils;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DisplaySettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, OPBrightnessSeekbarPreferenceCategory.OPCallbackBrightness, OPNightModeLevelPreference.OPNightModeLevelPreferenceChangeListener, Indexable
{
  private static final float BRIGHTNESS_ADJ_RESOLUTION = 100.0F;
  private static final int FALLBACK_SCREEN_TIMEOUT_VALUE = 30000;
  public static final String FILE_FONT_WARING = "font_waring";
  private static final String KEY_AUTO_BRIGHTNESS = "auto_brightness";
  private static final String KEY_AUTO_ROTATE = "auto_rotate";
  private static final String KEY_BACK_TOP_THEME = "back_topic_theme";
  private static final String KEY_CAMERA_DOUBLE_TAP_POWER_GESTURE = "camera_double_tap_power_gesture";
  private static final String KEY_CAMERA_GESTURE = "camera_gesture";
  private static final String KEY_DARK_MODE = "dark_mode";
  private static final String KEY_DARK_MODE_ACTION = "oem_black_mode";
  private static final String KEY_DISPLAY_SYSTEM = "display_system";
  private static final String KEY_DOZE = "doze";
  private static final String KEY_FONT_SIZE = "font_size";
  private static final String KEY_HAND_UP_PROXIMITY = "oneplus_hand_up_proximity";
  public static final String KEY_IS_CHECKED = "is_checked";
  private static final String KEY_LAST_COLOR = "last_color";
  private static final String KEY_LED_SETTINGS = "led_settings";
  private static final String KEY_LIFT_TO_WAKE = "lift_to_wake";
  private static final String KEY_LOCKGUARD_WALLPAPER = "lockguard_wallpaper_settings";
  private static final String KEY_MANUAL_BRIGHT = "manual_brightness_displays";
  private static final String KEY_MSM_SCREEN_BETTER = "msm_screen_better_settings";
  private static final String KEY_NETWORK_NAME_DISPLAYED = "network_operator_display";
  private static final String KEY_NIGHT_MODE = "night_mode";
  private static final String KEY_NIGHT_MODE_ENABLED = "night_mode_enabled_op";
  private static final String KEY_NIGHT_MODE_LEVEL = "night_mode_level_op";
  private static final String KEY_PROX_WAKE = "oneplus_proximity_wake";
  private static final String KEY_READING_MODE = "oneplus_reading_mode";
  private static final String KEY_SCREEN_BETTER = "screen_better_settings";
  private static final String KEY_SCREEN_BRIGHTNESS = "screen_brightness";
  private static final String KEY_SCREEN_COLOR_MODE = "screen_color_mode";
  private static final String KEY_SCREEN_SAVER = "screensaver";
  private static final String KEY_SCREEN_TIMEOUT = "screen_timeout";
  private static final String KEY_TAP_TO_WAKE = "tap_to_wake";
  private static final String KEY_THEME_ACCENT_COLOR = "theme_accent_color";
  private static final String KEY_THEME_MODE = "op_theme_mode";
  private static final String KEY_VR_DISPLAY_PREF = "vr_display_pref";
  private static final String KEY_WALLPAPER = "wallpaper";
  private static final int MAX_COLOR_COUNT = 7;
  public static final int MGS_THEME_STAR_WAR_MODE_CHANGE = 101;
  public static final int MSG_THEME_MODE_CHANGE = 100;
  public static final String NIGHT_MODE_ENABLED = "night_mode_enabled";
  private static final String NOTIFY_LIGHT_ENABLE_KEY = "notify_light_enable";
  private static final String OEM_BLACK_MODE_ACCENT_COLOR = "oem_black_mode_accent_color";
  private static final String OEM_BLACK_MODE_ACCENT_COLOR_INDEX = "oem_black_mode_accent_color_index";
  private static final String OEM_WHITE_MODE_ACCENT_COLOR = "oem_white_mode_accent_color";
  private static final String OEM_WHITE_MODE_ACCENT_COLOR_INDEX = "oem_white_mode_accent_color_index";
  private static final String OP_SYS_SRGB_PROPERTY = "sys.srgb";
  private static final String OXYGEN_THEME_INTENT = "com.oneplus.oxygen.changetheme";
  private static final String OXYGEN_THEME_INTENT_EXTRA = "oxygen_theme_status";
  public static final String PROX_WAKE_ENABLED = "prox_wake_enabled";
  public static final String SCREEN_AUTO_BRIGHTNESS_ADJ = "screen_auto_brightness_adj";
  private static final int SCREEN_COLOR_MODE_ADAPTIVE_MODEL_SETTINGS_VALUE = 5;
  private static final int SCREEN_COLOR_MODE_BASIC_SETTINGS_VALUE = 2;
  private static final int SCREEN_COLOR_MODE_DCI_P3_SETTINGS_VALUE = 4;
  private static final int SCREEN_COLOR_MODE_DEFAULT_SETTINGS_VALUE = 1;
  private static final int SCREEN_COLOR_MODE_DEFINED_SETTINGS_VALUE = 3;
  private static final int SCREEN_COLOR_MODE_SOFT_SETTINGS_VALUE = 6;
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      ArrayList localArrayList = new ArrayList();
      if (!paramAnonymousContext.getResources().getBoolean(17956974)) {
        localArrayList.add("screensaver");
      }
      if (!DisplaySettings.-wrap0(paramAnonymousContext.getResources())) {
        localArrayList.add("auto_brightness");
      }
      if (!DisplaySettings.-wrap4(paramAnonymousContext)) {
        localArrayList.add("lift_to_wake");
      }
      if (!DisplaySettings.-wrap3(paramAnonymousContext)) {
        localArrayList.add("doze");
      }
      if (!RotationPolicy.isRotationLockToggleVisible(paramAnonymousContext)) {
        localArrayList.add("auto_rotate");
      }
      if (!DisplaySettings.-wrap5(paramAnonymousContext.getResources())) {
        localArrayList.add("tap_to_wake");
      }
      if (!DisplaySettings.-wrap2(paramAnonymousContext.getResources())) {
        localArrayList.add("camera_gesture");
      }
      if (!DisplaySettings.-wrap1(paramAnonymousContext.getResources())) {
        localArrayList.add("camera_double_tap_power_gesture");
      }
      if (!DisplaySettings.-wrap6(paramAnonymousContext)) {
        localArrayList.add("vr_display_pref");
      }
      localArrayList.add("msm_screen_better_settings");
      localArrayList.add("back_topic_theme");
      localArrayList.add("wallpaper");
      localArrayList.add("lockguard_wallpaper_settings");
      localArrayList.add("network_operator_display");
      if (OPUtils.isGuestMode())
      {
        localArrayList.add("night_mode_enabled_op");
        localArrayList.add("night_mode_level_op");
        localArrayList.add("screen_color_mode");
      }
      if (OPUtils.isFeatureSupport(SettingsBaseApplication.mApplication, "oem.lift_up_display.support")) {
        localArrayList.add("oneplus_proximity_wake");
      }
      for (;;)
      {
        if (!DisplaySettings.-get0()) {
          localArrayList.add("oneplus_reading_mode");
        }
        return localArrayList;
        localArrayList.add("oneplus_hand_up_proximity");
      }
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230764;
      localArrayList.add(paramAnonymousContext);
      return localArrayList;
    }
  };
  private static final String SHARED_PREFERENCES_NAME = "customization_settings";
  private static final String SHOW_NETWORK_NAME_MODE = "show_network_name_mode";
  private static final int SHOW_NETWORK_NAME_OFF = 0;
  private static final int SHOW_NETWORK_NAME_ON = 1;
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new DisplaySettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader, null);
    }
  };
  private static final String TAG = "DisplaySettings";
  private static final int THEME_ANDROID_MODE = 2;
  private static final int THEME_DARK_MODE = 1;
  private static final int THEME_LIGHT_MODE = 0;
  private static final String THEME_MODE_ACTION = "android.settings.OEM_THEME_MODE";
  private static final String THEME_MODE_CUST_FLAG = "/sys/module/param_read_write/parameters/cust_flag";
  private static final String THEME_MODE_CUST_FLAG_STAR_WAR_VALUE = "2";
  private static final int THEME_MODE_STAR_WAR_VALUE = 1;
  private static final String TOGGLE_LOCK_SCREEN_ROTATION_PREFERENCE = "toggle_lock_screen_rotation_preference";
  private static boolean isSupportReadingMode = false;
  private static final String sDCI_P3Path = "/sys/devices/virtual/graphics/fb0/DCI_P3";
  private static final String sOPEN_VALUE = "mode = 1";
  private static final String sRGBPath = "/sys/devices/virtual/graphics/fb0/SRGB";
  private boolean isAutoSwitchClickedDrivenBrightnessChange;
  private ColorPickerPreference mAccentColorPreference;
  private SwitchPreference mAutoBrightnessPreference;
  private boolean mAutomatic;
  private boolean mAutomaticAvailable;
  private SwitchPreference mBacktopThemePreference;
  private int[] mBlackColorStringIds;
  private String[] mBlackColors;
  private OPBrightnessSeekbarPreferenceCategory mBrightPreference;
  private BrightnessObserver mBrightnessObserver;
  private OneplusColorManager mCM;
  private SwitchPreference mCameraDoubleTapPowerGesturePreference;
  private SwitchPreference mCameraGesturePreference;
  private String[] mColors;
  private Context mContext;
  private int mCurentValue = 0;
  private int mDarkModeEnable;
  private SwitchPreference mDarkModePreferce;
  private DarkModeRunnable mDarkModeRunnable = new DarkModeRunnable();
  private DefaultHandler mDefaultHandler;
  private int mDefaultThemeMode = 0;
  private SwitchPreference mDozePreference;
  private boolean mExternalChange;
  private Preference mFontSizePref;
  private SwitchPreference mHandUpProximityPreference;
  private Handler mHandler;
  private Preference mLedSettingsPreference;
  private SwitchPreference mLiftToWakePreference;
  private Preference mLockWallPaperPreference;
  private int mMaximumBacklight;
  private int mMinimumBacklight;
  private Preference mMsmScreenPreference;
  private SwitchPreference mNetworkNameDisplayedPreference = null;
  private boolean mNewController = false;
  private SwitchPreference mNightModeEnabledPreference;
  private Handler mNightModeHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      if (DisplaySettings.-get2(DisplaySettings.this) != null) {
        DisplaySettings.-get2(DisplaySettings.this).setNightModeLevel(paramAnonymousMessage.what);
      }
    }
  };
  private int mNightModeLevel = -1;
  private OPNightModeLevelPreference mNightModeLevelPreference;
  private ContentObserver mNightModeObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      int i;
      if (Settings.Secure.getIntForUser(DisplaySettings.this.getContentResolver(), "night_display_activated", 0, -2) != 0)
      {
        i = 1;
        if (Settings.System.getIntForUser(DisplaySettings.this.getContentResolver(), "reading_mode_status", 0, -2) == 0) {
          break label84;
        }
      }
      label84:
      for (int j = 1;; j = 0)
      {
        if (i == 0) {
          break label90;
        }
        DisplaySettings.-get4(DisplaySettings.this).setEnabled(false);
        DisplaySettings.-get4(DisplaySettings.this).setSummary(DisplaySettings.this.getResources().getString(2131690095));
        return;
        i = 0;
        break;
      }
      label90:
      if (j != 0)
      {
        DisplaySettings.-get4(DisplaySettings.this).setEnabled(false);
        DisplaySettings.-get4(DisplaySettings.this).setSummary(DisplaySettings.this.getResources().getString(2131690403));
        return;
      }
      DisplaySettings.-wrap10(DisplaySettings.this);
      DisplaySettings.-get4(DisplaySettings.this).setEnabled(true);
    }
  };
  private ListPreference mNightModePreference;
  private int mNotifyLightEnable;
  private SwitchPreference mNotifyLightPreference;
  private IPowerManager mPower;
  private int mPreValue = 0;
  private SwitchPreference mProxWakePreference;
  private Preference mReadingModePreference;
  private final RotationPolicy.RotationPolicyListener mRotationPolicyListener = new RotationPolicy.RotationPolicyListener()
  {
    public void onChange()
    {
      DisplaySettings.-wrap8(DisplaySettings.this);
    }
  };
  private PreferenceCategory mScreenBrightnessRootPreference;
  private Preference mScreenColorModePreference;
  private Preference mScreenPreference;
  private Preference mScreenSaverPreference;
  private TimeoutListPreference mScreenTimeoutPreference;
  private PreferenceCategory mSystemRootPreference;
  private SwitchPreference mTapToWakePreference;
  private ListPreference mThemeModePreference;
  private SwitchPreference mToggleLockScreenRotationPreference;
  private int[] mWhiteColorStringIds;
  private String[] mWhiteColors;
  
  private static boolean allowAllRotations(Context paramContext)
  {
    return Resources.getSystem().getBoolean(17956920);
  }
  
  private void disablePreferenceIfManaged(String paramString1, String paramString2)
  {
    paramString1 = (RestrictedPreference)findPreference(paramString1);
    if (paramString1 != null)
    {
      paramString1.setDisabledByAdmin(null);
      if (RestrictedLockUtils.hasBaseUserRestriction(getActivity(), paramString2, UserHandle.myUserId())) {
        paramString1.setEnabled(false);
      }
    }
    else
    {
      return;
    }
    paramString1.checkRestrictionAndSetDisabled(paramString2);
  }
  
  private int getColorIndex()
  {
    if (OPUtils.isBlackModeOn(this.mContext.getContentResolver()))
    {
      int i = Settings.System.getInt(getActivity().getContentResolver(), "oem_black_mode_accent_color_index", 0);
      if (i > 7) {
        return i - 7;
      }
      return i;
    }
    return Settings.System.getInt(getActivity().getContentResolver(), "oem_white_mode_accent_color_index", 0);
  }
  
  private SharedPreferences getPrefs()
  {
    return this.mContext.getSharedPreferences("customization_settings", 0);
  }
  
  private int getThemeModeValue(int paramInt)
  {
    if (paramInt == 2) {
      return 0;
    }
    if (paramInt == 0) {
      return 1;
    }
    if (paramInt == 1) {
      return 2;
    }
    return paramInt;
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
  
  private void initAccentColors(Resources paramResources)
  {
    this.mWhiteColors = new String[] { paramResources.getString(2131493723), paramResources.getString(2131493724), paramResources.getString(2131493725), paramResources.getString(2131493726), paramResources.getString(2131493727), paramResources.getString(2131493728), paramResources.getString(2131493729), paramResources.getString(2131493730) };
    this.mBlackColors = new String[] { paramResources.getString(2131493731), paramResources.getString(2131493732), paramResources.getString(2131493733), paramResources.getString(2131493734), paramResources.getString(2131493735), paramResources.getString(2131493736), paramResources.getString(2131493737), paramResources.getString(2131493738) };
    if (OPUtils.isWhiteModeOn(this.mContext.getContentResolver())) {
      this.mColors = this.mWhiteColors;
    }
    for (;;)
    {
      this.mWhiteColorStringIds = new int[] { 2131690556, 2131690557, 2131690558, 2131690559, 2131690560, 2131690561, 2131690562, 2131690563 };
      this.mBlackColorStringIds = new int[] { 2131690564, 2131690565, 2131690566, 2131690567, 2131690568, 2131690569, 2131690570, 2131690571 };
      return;
      if (OPUtils.isBlackModeOn(this.mContext.getContentResolver())) {
        this.mColors = this.mBlackColors;
      }
    }
  }
  
  private boolean isAccentColorPreferenceEnabled()
  {
    int i = Settings.System.getInt(getActivity().getContentResolver(), "oem_special_theme", 1);
    return (OPUtils.isWhiteModeOn(this.mContext.getContentResolver())) || ((OPUtils.isBlackModeOn(this.mContext.getContentResolver())) && (i != 1));
  }
  
  private static boolean isAutomaticBrightnessAvailable(Resources paramResources)
  {
    return paramResources.getBoolean(17956900);
  }
  
  private static boolean isCameraDoubleTapPowerGestureAvailable(Resources paramResources)
  {
    return paramResources.getBoolean(17957038);
  }
  
  private static boolean isCameraGestureAvailable(Resources paramResources)
  {
    if (paramResources.getInteger(17694884) != -1) {}
    for (int i = 1; (i == 0) || (SystemProperties.getBoolean("gesture.disable_camera_launch", false)); i = 0) {
      return false;
    }
    return true;
  }
  
  private static boolean isDozeAvailable(Context paramContext)
  {
    if (Build.IS_DEBUGGABLE) {}
    for (String str1 = SystemProperties.get("debug.doze.component");; str1 = null)
    {
      String str2 = str1;
      if (TextUtils.isEmpty(str1)) {
        str2 = paramContext.getResources().getString(17039451);
      }
      if (!TextUtils.isEmpty(str2)) {
        break;
      }
      return false;
    }
    return true;
  }
  
  private static boolean isLiftToWakeAvailable(Context paramContext)
  {
    boolean bool2 = false;
    paramContext = (SensorManager)paramContext.getSystemService("sensor");
    boolean bool1 = bool2;
    if (paramContext != null)
    {
      bool1 = bool2;
      if (paramContext.getDefaultSensor(23) != null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static boolean isProxWakeAvailable(Context paramContext)
  {
    boolean bool2 = false;
    paramContext = (SensorManager)paramContext.getSystemService("sensor");
    boolean bool1 = bool2;
    if (paramContext != null)
    {
      bool1 = bool2;
      if (paramContext.getDefaultSensor(8) != null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static boolean isTapToWakeAvailable(Resources paramResources)
  {
    return paramResources.getBoolean(17957032);
  }
  
  private static boolean isVrDisplayModeAvailable(Context paramContext)
  {
    return paramContext.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance");
  }
  
  private void killSelf()
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory("android.intent.category.HOME");
    startActivity(localIntent);
    finish();
  }
  
  private void onSaveNightModeSeekBarVale(int paramInt)
  {
    Settings.System.putInt(getContentResolver(), "oem_nightmode_progress_status", paramInt);
  }
  
  private void resetDefinedScreenColorModeValue()
  {
    int i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_screen_better_value", 43, -2);
    if (this.mCM != null)
    {
      this.mCM.setColorBalance(100 - i);
      this.mCM.saveScreenBetter();
    }
  }
  
  private void saveColorInfo(int paramInt)
  {
    if (OPUtils.isBlackModeOn(this.mContext.getContentResolver()))
    {
      if (paramInt > 7) {}
      for (int i = paramInt - 7;; i = paramInt)
      {
        Settings.System.putString(getActivity().getContentResolver(), "oem_black_mode_accent_color", this.mColors[i]);
        Settings.System.putInt(getActivity().getContentResolver(), "oem_black_mode_accent_color_index", paramInt);
        return;
      }
    }
    Settings.System.putString(getActivity().getContentResolver(), "oem_white_mode_accent_color", this.mColors[paramInt]);
    Settings.System.putInt(getActivity().getContentResolver(), "oem_white_mode_accent_color_index", paramInt);
  }
  
  private void sendTheme(int paramInt, boolean paramBoolean)
  {
    getPrefs();
    saveColorInfo(paramInt);
    Intent localIntent = new Intent("android.settings.OEM_COLOR_MODE");
    localIntent.putExtra("oem_color_mode", paramInt);
    localIntent.addFlags(268435456);
    getContext().sendBroadcast(localIntent);
  }
  
  private void setBrightness(int paramInt)
  {
    int i = paramInt;
    if (paramInt < 2) {
      i = 2;
    }
    try
    {
      this.mPower.setTemporaryScreenBrightnessSettingOverride(i);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void setBrightnessAdj(float paramFloat)
  {
    try
    {
      this.mPower.setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void updateAutoSwitchDrivenSlider()
  {
    if ((!this.mAutomatic) || (this.mNewController))
    {
      int i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness", this.mMaximumBacklight, -2);
      Log.d("display", "value from database is when closing switch " + i);
      this.mBrightPreference.setMax(this.mMaximumBacklight - this.mMinimumBacklight);
      this.mBrightPreference.setBrightness(String.valueOf(i - this.mMinimumBacklight));
      return;
    }
    updateBrightnessAutomatically();
  }
  
  private void updateBrightnessAutomatically()
  {
    float f = Settings.System.getFloatForUser(this.mContext.getContentResolver(), "screen_auto_brightness_adj", 0.0F, -2);
    this.mBrightPreference.setMax(100);
    this.mBrightPreference.setBrightness(String.valueOf((int)((1.0F + f) * 100.0F / 2.0F)));
  }
  
  private void updateDozeMode(boolean paramBoolean)
  {
    int j = 2131690286;
    SwitchPreference localSwitchPreference;
    if (this.mProxWakePreference != null)
    {
      localSwitchPreference = this.mProxWakePreference;
      if (!paramBoolean) {
        break label77;
      }
    }
    label77:
    for (int i = 2131690726;; i = 2131690286)
    {
      localSwitchPreference.setSummary(i);
      this.mProxWakePreference.setEnabled(paramBoolean);
      if (this.mHandUpProximityPreference != null)
      {
        localSwitchPreference = this.mHandUpProximityPreference;
        i = j;
        if (paramBoolean) {
          i = 2131690444;
        }
        localSwitchPreference.setSummary(i);
        this.mHandUpProximityPreference.setEnabled(paramBoolean);
      }
      return;
    }
  }
  
  private void updateFontSizeSummary()
  {
    Object localObject = this.mFontSizePref.getContext();
    float f = Settings.System.getFloat(((Context)localObject).getContentResolver(), "font_scale", 1.0F);
    localObject = ((Context)localObject).getResources();
    String[] arrayOfString = ((Resources)localObject).getStringArray(2131427379);
    int i = ToggleFontSizePreferenceFragment.fontSizeValueToIndex(f, ((Resources)localObject).getStringArray(2131427380));
    this.mFontSizePref.setSummary(arrayOfString[i]);
  }
  
  private void updateLockScreenRotation()
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
  
  private void updateMode()
  {
    boolean bool = false;
    System.out.println("updateMode mAutomaticAvailable : " + this.mAutomaticAvailable);
    if (this.mAutomaticAvailable)
    {
      if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -2) != 0) {
        bool = true;
      }
      this.mAutomatic = bool;
      this.mAutoBrightnessPreference.setChecked(this.mAutomatic);
    }
  }
  
  private void updateNotifyLightStatus(int paramInt)
  {
    Settings.System.putInt(getActivity().getContentResolver(), "oem_acc_breath_light", paramInt);
    Settings.System.putInt(getActivity().getContentResolver(), "notification_light_pulse", paramInt);
    Settings.System.putInt(getActivity().getContentResolver(), "battery_led_low_power", paramInt);
    Settings.System.putInt(getActivity().getContentResolver(), "battery_led_charging", paramInt);
  }
  
  private void updateScreenColorModePreference()
  {
    int i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1, -2);
    if (1 == i) {
      this.mScreenColorModePreference.setSummary(getResources().getString(2131690092));
    }
    do
    {
      return;
      if (2 == i)
      {
        this.mScreenColorModePreference.setSummary(getResources().getString(2131690093));
        return;
      }
      if (3 == i)
      {
        this.mScreenColorModePreference.setSummary(getResources().getString(2131690094));
        return;
      }
      if (4 == i)
      {
        this.mScreenColorModePreference.setSummary(getResources().getString(2131690755));
        return;
      }
      if (5 == i)
      {
        this.mScreenColorModePreference.setSummary(getResources().getString(2131690515));
        return;
      }
    } while (6 != i);
    this.mScreenColorModePreference.setSummary(getResources().getString(2131690534));
  }
  
  private void updateScreenSaverSummary()
  {
    if (this.mScreenSaverPreference != null) {
      this.mScreenSaverPreference.setSummary(DreamSettings.getSummaryTextWithDreamName(getActivity()));
    }
  }
  
  private void updateSlider()
  {
    int j;
    if ((!this.mAutomatic) || (this.mNewController))
    {
      j = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness", this.mMaximumBacklight, -2);
      if (this.isAutoSwitchClickedDrivenBrightnessChange) {
        this.isAutoSwitchClickedDrivenBrightnessChange = false;
      }
    }
    else
    {
      if (this.isAutoSwitchClickedDrivenBrightnessChange)
      {
        this.isAutoSwitchClickedDrivenBrightnessChange = false;
        return;
      }
      updateBrightnessAutomatically();
      return;
    }
    int i = 0;
    if (this.mPreValue == 0)
    {
      this.mPreValue = (j - this.mMinimumBacklight);
      i = 1;
    }
    if (this.mCurentValue == 0)
    {
      this.mCurentValue = (j - this.mMinimumBacklight);
      i = 1;
    }
    this.mCurentValue = (j - this.mMinimumBacklight);
    if ((this.mPreValue == this.mCurentValue) && (i == 0))
    {
      Log.d("display", "mPreValue==mCurentValue ! ");
      return;
    }
    Log.d("display", "updateSlider (mMaximumBacklight - mMinimumBacklight) : " + this.mMaximumBacklight);
    this.mBrightPreference.setMax(this.mMaximumBacklight);
    this.mBrightPreference.setBrightness(String.valueOf(j));
    this.mPreValue = this.mCurentValue;
  }
  
  private void updateState()
  {
    updateFontSizeSummary();
    updateScreenSaverSummary();
    this.mNotifyLightEnable = Settings.System.getInt(getContentResolver(), "oem_acc_breath_light", 0);
    Object localObject;
    boolean bool;
    int i;
    if (this.mLedSettingsPreference != null)
    {
      localObject = this.mLedSettingsPreference;
      if (this.mNotifyLightEnable == 1)
      {
        bool = true;
        ((Preference)localObject).setEnabled(bool);
      }
    }
    else
    {
      if (this.mAutoBrightnessPreference != null)
      {
        i = Settings.System.getInt(getContentResolver(), "screen_brightness_mode", 0);
        localObject = this.mAutoBrightnessPreference;
        if (i == 0) {
          break label784;
        }
        bool = true;
        label83:
        ((SwitchPreference)localObject).setChecked(bool);
      }
      if (this.mNetworkNameDisplayedPreference != null)
      {
        i = Settings.System.getInt(getContentResolver(), "show_network_name_mode", 1);
        localObject = this.mNetworkNameDisplayedPreference;
        if (i == 0) {
          break label789;
        }
        bool = true;
        label119:
        ((SwitchPreference)localObject).setChecked(bool);
      }
      if (this.mLiftToWakePreference != null)
      {
        i = Settings.Secure.getInt(getContentResolver(), "wake_gesture_enabled", 0);
        localObject = this.mLiftToWakePreference;
        if (i == 0) {
          break label794;
        }
        bool = true;
        label156:
        ((SwitchPreference)localObject).setChecked(bool);
      }
      if (this.mDozePreference != null)
      {
        if (Settings.Secure.getInt(getContentResolver(), "doze_enabled", 1) != 1) {
          break label799;
        }
        bool = true;
        label186:
        this.mDozePreference.setChecked(bool);
        updateDozeMode(bool);
      }
      if (this.mProxWakePreference != null)
      {
        i = Settings.System.getInt(getContentResolver(), "prox_wake_enabled", 0);
        localObject = this.mProxWakePreference;
        if (i == 0) {
          break label804;
        }
        bool = true;
        label229:
        ((SwitchPreference)localObject).setChecked(bool);
      }
      if (this.mHandUpProximityPreference != null)
      {
        i = Settings.System.getInt(getContentResolver(), "prox_wake_enabled", 0);
        localObject = this.mHandUpProximityPreference;
        if (i == 0) {
          break label809;
        }
        bool = true;
        label265:
        ((SwitchPreference)localObject).setChecked(bool);
      }
      if (this.mTapToWakePreference != null)
      {
        i = Settings.Secure.getInt(getContentResolver(), "double_tap_to_wake", 0);
        localObject = this.mTapToWakePreference;
        if (i == 0) {
          break label814;
        }
        bool = true;
        label302:
        ((SwitchPreference)localObject).setChecked(bool);
      }
      if (this.mCameraGesturePreference != null)
      {
        i = Settings.Secure.getInt(getContentResolver(), "camera_gesture_disabled", 0);
        localObject = this.mCameraGesturePreference;
        if (i != 0) {
          break label819;
        }
        bool = true;
        label339:
        ((SwitchPreference)localObject).setChecked(bool);
      }
      if (this.mCameraDoubleTapPowerGesturePreference != null)
      {
        i = Settings.Secure.getInt(getContentResolver(), "camera_double_tap_power_gesture_disabled", 0);
        localObject = this.mCameraDoubleTapPowerGesturePreference;
        if (i != 0) {
          break label824;
        }
        bool = true;
        label376:
        ((SwitchPreference)localObject).setChecked(bool);
      }
      if (this.mAccentColorPreference != null)
      {
        getPrefs();
        i = getColorIndex();
        this.mAccentColorPreference.setOnPreferenceChangeListener(null);
        if ((!isAccentColorPreferenceEnabled()) || (this.mColors == null)) {
          break label829;
        }
        this.mAccentColorPreference.setColor(this.mColors[i]);
        label434:
        this.mAccentColorPreference.setEnabled(isAccentColorPreferenceEnabled());
        this.mAccentColorPreference.setOnPreferenceChangeListener(this);
        if ((!getActivity().getPackageManager().hasSystemFeature("oem.op_dark_mode.support")) || (OPUtils.isGuestMode())) {
          this.mSystemRootPreference.removePreference(this.mAccentColorPreference);
        }
      }
      if (Settings.Global.getInt(getContentResolver(), "night_mode_enabled", 0) == 0) {
        break label852;
      }
      bool = true;
      label502:
      Log.d("TAG", "resetNightAndSaturationMode--opNightModeEnabled:" + bool);
      if (this.mNightModeEnabledPreference != null) {
        this.mNightModeEnabledPreference.setChecked(bool);
      }
      if ((this.mNightModeEnabledPreference != null) && (this.mNightModeLevelPreference != null) && (this.mScreenColorModePreference != null))
      {
        if (!bool) {
          break label857;
        }
        this.mScreenBrightnessRootPreference.addPreference(this.mNightModeLevelPreference);
        this.mScreenColorModePreference.setEnabled(false);
        this.mScreenColorModePreference.setSummary(getResources().getString(2131690095));
      }
      label606:
      if ((this.mNightModeLevelPreference != null) && (this.mMsmScreenPreference != null) && (this.mScreenBrightnessRootPreference != null))
      {
        if (!bool) {
          break label884;
        }
        this.mScreenBrightnessRootPreference.addPreference(this.mNightModeLevelPreference);
        this.mMsmScreenPreference.setEnabled(false);
        this.mMsmScreenPreference.setSummary(getResources().getString(2131690095));
      }
      label668:
      if (this.mNightModeEnabledPreference != null) {
        this.mScreenBrightnessRootPreference.removePreference(this.mNightModeEnabledPreference);
      }
      if (this.mNightModeLevelPreference != null) {
        this.mScreenBrightnessRootPreference.removePreference(this.mNightModeLevelPreference);
      }
      if (this.mScreenColorModePreference != null)
      {
        if (Settings.Secure.getIntForUser(getContentResolver(), "night_display_activated", 0, -2) == 0) {
          break label915;
        }
        i = 1;
        label731:
        if (Settings.System.getIntForUser(getContentResolver(), "reading_mode_status", 0, -2) == 0) {
          break label920;
        }
      }
    }
    label784:
    label789:
    label794:
    label799:
    label804:
    label809:
    label814:
    label819:
    label824:
    label829:
    label852:
    label857:
    label884:
    label915:
    label920:
    for (int j = 1;; j = 0)
    {
      if (i == 0) {
        break label925;
      }
      this.mScreenColorModePreference.setEnabled(false);
      this.mScreenColorModePreference.setSummary(getResources().getString(2131690095));
      return;
      bool = false;
      break;
      bool = false;
      break label83;
      bool = false;
      break label119;
      bool = false;
      break label156;
      bool = false;
      break label186;
      bool = false;
      break label229;
      bool = false;
      break label265;
      bool = false;
      break label302;
      bool = false;
      break label339;
      bool = false;
      break label376;
      this.mAccentColorPreference.setColor(this.mContext.getResources().getString(2131493723));
      break label434;
      bool = false;
      break label502;
      this.mScreenBrightnessRootPreference.removePreference(this.mNightModeLevelPreference);
      updateScreenColorModePreference();
      this.mScreenColorModePreference.setEnabled(true);
      break label606;
      this.mScreenBrightnessRootPreference.removePreference(this.mNightModeLevelPreference);
      this.mMsmScreenPreference.setSummary(null);
      this.mMsmScreenPreference.setEnabled(true);
      break label668;
      i = 0;
      break label731;
    }
    label925:
    if (j != 0)
    {
      this.mScreenColorModePreference.setEnabled(false);
      this.mScreenColorModePreference.setSummary(getResources().getString(2131690403));
      return;
    }
    updateScreenColorModePreference();
    this.mScreenColorModePreference.setEnabled(true);
  }
  
  private void updateThemeModePreferenceDescription(int paramInt)
  {
    int i = Settings.System.getInt(getActivity().getContentResolver(), "oem_special_theme", 0);
    CharSequence[] arrayOfCharSequence = this.mThemeModePreference.getEntries();
    if (i == 1) {
      this.mThemeModePreference.setSummary(arrayOfCharSequence[3]);
    }
    while (this.mThemeModePreference == null) {
      return;
    }
    this.mThemeModePreference.setSummary(arrayOfCharSequence[paramInt]);
  }
  
  private void updateTimeoutPreferenceDescription(long paramLong)
  {
    if (!isAdded()) {
      return;
    }
    TimeoutListPreference localTimeoutListPreference = this.mScreenTimeoutPreference;
    Object localObject;
    if (localTimeoutListPreference.isDisabledByAdmin()) {
      localObject = this.mContext.getResources().getString(2131693595);
    }
    for (;;)
    {
      localTimeoutListPreference.setSummary((CharSequence)localObject);
      return;
      if (paramLong < 0L)
      {
        localObject = "";
      }
      else
      {
        localObject = localTimeoutListPreference.getEntries();
        CharSequence[] arrayOfCharSequence = localTimeoutListPreference.getEntryValues();
        if ((localObject == null) || (localObject.length == 0))
        {
          localObject = "";
        }
        else
        {
          int j = 0;
          int i = 0;
          while (i < arrayOfCharSequence.length)
          {
            if (paramLong >= Long.parseLong(arrayOfCharSequence[i].toString())) {
              j = i;
            }
            i += 1;
          }
          localObject = this.mContext.getResources().getString(2131691625, new Object[] { localObject[j] });
        }
      }
    }
  }
  
  protected int getHelpResource()
  {
    return 2131693001;
  }
  
  protected int getMetricsCategory()
  {
    return 46;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    final Object localObject1 = getActivity();
    ((Activity)localObject1).getContentResolver();
    this.mContext = getActivity();
    this.mDefaultHandler = new DefaultHandler(((Activity)localObject1).getApplication());
    this.mCM = new OneplusColorManager(SettingsBaseApplication.mApplication);
    addPreferencesFromResource(2131230764);
    paramBundle = this.mContext.getResources();
    initAccentColors(paramBundle);
    this.mScreenBrightnessRootPreference = ((PreferenceCategory)findPreference("screen_brightness"));
    this.mSystemRootPreference = ((PreferenceCategory)findPreference("display_system"));
    this.mScreenSaverPreference = findPreference("screensaver");
    if ((this.mScreenSaverPreference != null) && (!getResources().getBoolean(17956974))) {
      getPreferenceScreen().removePreference(this.mScreenSaverPreference);
    }
    this.mScreenTimeoutPreference = ((TimeoutListPreference)findPreference("screen_timeout"));
    this.mFontSizePref = findPreference("font_size");
    label254:
    label283:
    label354:
    int i;
    label462:
    boolean bool;
    if (isAutomaticBrightnessAvailable(getResources()))
    {
      this.mAutoBrightnessPreference = ((SwitchPreference)findPreference("auto_brightness"));
      this.mAutoBrightnessPreference.setOnPreferenceChangeListener(this);
      getResources().getBoolean(17957068);
      this.mScreenBrightnessRootPreference.removePreference((SwitchPreference)findPreference("network_operator_display"));
      if (!isLiftToWakeAvailable((Context)localObject1)) {
        break label1416;
      }
      this.mLiftToWakePreference = ((SwitchPreference)findPreference("lift_to_wake"));
      this.mLiftToWakePreference.setOnPreferenceChangeListener(this);
      if (!isDozeAvailable((Context)localObject1)) {
        break label1425;
      }
      this.mDozePreference = ((SwitchPreference)findPreference("doze"));
      this.mDozePreference.setOnPreferenceChangeListener(this);
      if (!isProxWakeAvailable((Context)localObject1)) {
        break label1462;
      }
      this.mProxWakePreference = ((SwitchPreference)findPreference("oneplus_proximity_wake"));
      this.mHandUpProximityPreference = ((SwitchPreference)findPreference("oneplus_hand_up_proximity"));
      if (!OPUtils.isFeatureSupport(SettingsBaseApplication.mApplication, "oem.lift_up_display.support")) {
        break label1434;
      }
      this.mSystemRootPreference.removePreference((SwitchPreference)findPreference("oneplus_proximity_wake"));
      this.mHandUpProximityPreference.setOnPreferenceChangeListener(this);
      if (!isVrDisplayModeAvailable((Context)localObject1)) {
        break label1499;
      }
      Object localObject2 = (DropDownPreference)findPreference("vr_display_pref");
      ((DropDownPreference)localObject2).setEntries(new CharSequence[] { ((Activity)localObject1).getString(2131693248), ((Activity)localObject1).getString(2131693249) });
      ((DropDownPreference)localObject2).setEntryValues(new CharSequence[] { "0", "1" });
      i = ActivityManager.getCurrentUser();
      ((DropDownPreference)localObject2).setValueIndex(Settings.Secure.getIntForUser(((Context)localObject1).getContentResolver(), "vr_display_mode", 0, i));
      ((DropDownPreference)localObject2).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          int i = Integer.parseInt((String)paramAnonymousObject);
          int j = ActivityManager.getCurrentUser();
          if (!Settings.Secure.putIntForUser(localObject1.getContentResolver(), "vr_display_mode", i, j)) {
            Log.e("DisplaySettings", "Could not change setting for vr_display_mode");
          }
          return true;
        }
      });
      this.mHandler = new Handler();
      this.mDarkModePreferce = ((SwitchPreference)findPreference("dark_mode"));
      this.mDarkModePreferce.setOnPreferenceChangeListener(this);
      this.mDarkModeEnable = Settings.System.getInt(getActivity().getContentResolver(), "oem_black_mode", 0);
      localObject1 = this.mDarkModePreferce;
      if (this.mDarkModeEnable != 0) {
        break label1519;
      }
      bool = false;
      label527:
      ((SwitchPreference)localObject1).setChecked(bool);
      this.mSystemRootPreference.removePreference(this.mDarkModePreferce);
      this.mThemeModePreference = ((ListPreference)findPreference("op_theme_mode"));
      if ("2".equals(OPUtils.readFile("/sys/module/param_read_write/parameters/cust_flag")))
      {
        localObject1 = getResources().getStringArray(2131427471);
        localObject2 = getResources().getStringArray(2131427473);
        this.mThemeModePreference.setEntries((CharSequence[])localObject1);
        this.mThemeModePreference.setEntryValues((CharSequence[])localObject2);
      }
      i = Settings.System.getInt(getActivity().getContentResolver(), "oem_black_mode", this.mDefaultThemeMode);
      int j = Settings.System.getInt(getActivity().getContentResolver(), "oem_special_theme", 1);
      if ((i != 1) || (j != 1)) {
        break label1525;
      }
      this.mThemeModePreference.setValue(String.valueOf(3));
      label667:
      this.mThemeModePreference.setOnPreferenceChangeListener(this);
      updateThemeModePreferenceDescription(getThemeModeValue(i));
      if ((!getActivity().getPackageManager().hasSystemFeature("oem.op_dark_mode.support")) || (OPUtils.isGuestMode())) {
        this.mSystemRootPreference.removePreference(this.mThemeModePreference);
      }
      this.mToggleLockScreenRotationPreference = ((SwitchPreference)findPreference("toggle_lock_screen_rotation_preference"));
      if (!RotationPolicy.isRotationSupported(getActivity())) {
        removePreference("toggle_lock_screen_rotation_preference");
      }
      this.mBacktopThemePreference = ((SwitchPreference)findPreference("back_topic_theme"));
      this.mBacktopThemePreference.setOnPreferenceClickListener(this);
      if (this.mBacktopThemePreference != null) {
        this.mSystemRootPreference.removePreference(this.mBacktopThemePreference);
      }
      this.mMsmScreenPreference = findPreference("msm_screen_better_settings");
      this.mMsmScreenPreference.setOnPreferenceClickListener(this);
      this.mNotifyLightEnable = Settings.System.getInt(getActivity().getContentResolver(), "oem_acc_breath_light", 0);
      this.mNotifyLightPreference = ((SwitchPreference)findPreference("notify_light_enable"));
      this.mNotifyLightPreference.setOnPreferenceChangeListener(this);
      localObject1 = this.mNotifyLightPreference;
      if (this.mNotifyLightEnable != 0) {
        break label1539;
      }
      bool = false;
      label860:
      ((SwitchPreference)localObject1).setChecked(bool);
      this.mLockWallPaperPreference = findPreference("lockguard_wallpaper_settings");
      this.mLockWallPaperPreference.setOnPreferenceClickListener(this);
      this.mSystemRootPreference.removePreference(this.mLockWallPaperPreference);
      this.mSystemRootPreference.removePreference(this.mSystemRootPreference.findPreference("camera_gesture"));
      this.mSystemRootPreference.removePreference(this.mSystemRootPreference.findPreference("lift_to_wake"));
      this.mSystemRootPreference.removePreference(this.mSystemRootPreference.findPreference("tap_to_wake"));
      this.mSystemRootPreference.removePreference(this.mSystemRootPreference.findPreference("auto_rotate"));
      localObject1 = (PowerManager)getActivity().getSystemService("power");
      this.mMinimumBacklight = ((PowerManager)localObject1).getMinimumScreenBrightnessSetting();
      this.mMaximumBacklight = ((PowerManager)localObject1).getMaximumScreenBrightnessSetting();
      this.mAutomaticAvailable = getActivity().getResources().getBoolean(17956900);
      this.mPower = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
      this.mNewController = this.mContext.getPackageManager().hasSystemFeature("oem.autobrightctl.animation.support");
      this.mBrightPreference = ((OPBrightnessSeekbarPreferenceCategory)findPreference("manual_brightness_displays"));
      this.mBrightPreference.setCallback(this);
      this.mBrightnessObserver = new BrightnessObserver(this.mHandler);
      this.mBrightnessObserver.startObserving();
      this.mAccentColorPreference = ((ColorPickerPreference)findPreference("theme_accent_color"));
      this.mLedSettingsPreference = findPreference("led_settings");
      if (!OPUtils.isWhiteModeOn(this.mContext.getContentResolver())) {
        break label1545;
      }
      this.mAccentColorPreference.setColorPalette(this.mWhiteColors, this.mWhiteColorStringIds);
      this.mAccentColorPreference.setDefaultColor(paramBundle.getString(2131493723));
      label1154:
      this.mAccentColorPreference.setMessageText(2131689943);
      updateState();
      this.mScreenBrightnessRootPreference.removePreference(this.mMsmScreenPreference);
      if (OPUtils.isGuestMode())
      {
        this.mSystemRootPreference.removePreference(this.mNotifyLightPreference);
        this.mSystemRootPreference.removePreference(this.mLedSettingsPreference);
      }
      this.mNightModeEnabledPreference = ((SwitchPreference)findPreference("night_mode_enabled_op"));
      if (this.mNightModeEnabledPreference != null) {
        this.mNightModeEnabledPreference.setOnPreferenceChangeListener(this);
      }
      this.mNightModeLevelPreference = ((OPNightModeLevelPreference)findPreference("night_mode_level_op"));
      this.mScreenColorModePreference = findPreference("screen_color_mode");
      if (this.mNightModeLevelPreference != null) {
        this.mNightModeLevelPreference.setOPColorModeSeekBarChangeListener(this);
      }
      if (this.mScreenBrightnessRootPreference != null) {
        this.mScreenBrightnessRootPreference.removePreference(findPreference("wallpaper"));
      }
      isSupportReadingMode = this.mContext.getPackageManager().hasSystemFeature("oem.read_mode.support");
      this.mReadingModePreference = findPreference("oneplus_reading_mode");
      if ((this.mReadingModePreference != null) && (!isSupportReadingMode)) {
        break label1607;
      }
    }
    for (;;)
    {
      if ((OPUtils.isGuestMode()) && (this.mScreenBrightnessRootPreference != null))
      {
        if (this.mNightModeEnabledPreference != null) {
          this.mScreenBrightnessRootPreference.removePreference(this.mNightModeEnabledPreference);
        }
        if (this.mNightModeLevelPreference != null) {
          this.mScreenBrightnessRootPreference.removePreference(this.mNightModeLevelPreference);
        }
        if (this.mScreenColorModePreference != null) {
          this.mScreenBrightnessRootPreference.removePreference(this.mScreenColorModePreference);
        }
      }
      return;
      removePreference("auto_brightness");
      break;
      label1416:
      removePreference("lift_to_wake");
      break label254;
      label1425:
      removePreference("doze");
      break label283;
      label1434:
      this.mSystemRootPreference.removePreference((SwitchPreference)findPreference("oneplus_hand_up_proximity"));
      this.mProxWakePreference.setOnPreferenceChangeListener(this);
      break label354;
      label1462:
      this.mSystemRootPreference.removePreference((SwitchPreference)findPreference("oneplus_proximity_wake"));
      this.mSystemRootPreference.removePreference((SwitchPreference)findPreference("oneplus_hand_up_proximity"));
      break label354;
      label1499:
      this.mSystemRootPreference.removePreference((DropDownPreference)findPreference("vr_display_pref"));
      break label462;
      label1519:
      bool = true;
      break label527;
      label1525:
      this.mThemeModePreference.setValue(String.valueOf(i));
      break label667;
      label1539:
      bool = true;
      break label860;
      label1545:
      if (OPUtils.isBlackModeOn(this.mContext.getContentResolver()))
      {
        this.mAccentColorPreference.setColorPalette(this.mBlackColors, this.mBlackColorStringIds);
        this.mAccentColorPreference.setDefaultColor(paramBundle.getString(2131493731));
        break label1154;
      }
      this.mAccentColorPreference.setDefaultColor(paramBundle.getString(2131493723));
      break label1154;
      label1607:
      this.mScreenBrightnessRootPreference.removePreference(this.mReadingModePreference);
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (this.mBrightnessObserver != null) {
      this.mBrightnessObserver.stopObserving();
    }
  }
  
  public void onOPBrightValueChanged(int paramInt1, int paramInt2)
  {
    this.mExternalChange = false;
    if (this.mNewController)
    {
      paramInt2 += this.mMinimumBacklight;
      paramInt1 = paramInt2;
      if (paramInt2 > PowerManager.BRIGHTNESS_ON) {
        paramInt1 = PowerManager.BRIGHTNESS_ON;
      }
      setBrightnessAdj(paramInt1);
      setBrightness(paramInt1);
      return;
    }
    if (!this.mAutomatic)
    {
      paramInt2 += this.mMinimumBacklight;
      paramInt1 = paramInt2;
      if (paramInt2 > PowerManager.BRIGHTNESS_ON) {
        paramInt1 = PowerManager.BRIGHTNESS_ON;
      }
      setBrightness(paramInt1);
      return;
    }
    setBrightnessAdj(paramInt2 / 50.0F - 1.0F);
  }
  
  public void onPause()
  {
    super.onPause();
    this.mAccentColorPreference.onDismiss(null);
    if (RotationPolicy.isRotationSupported(getActivity())) {
      RotationPolicy.unregisterRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    Object localObject = paramPreference.getKey();
    if ("screen_timeout".equals(localObject)) {}
    boolean bool;
    label529:
    label534:
    label539:
    label544:
    label549:
    label554:
    label559:
    label564:
    String str;
    try
    {
      i = Integer.parseInt((String)paramObject);
      Settings.System.putInt(getContentResolver(), "screen_off_timeout", i);
      updateTimeoutPreferenceDescription(i);
      ContentResolver localContentResolver;
      if (paramPreference == this.mAutoBrightnessPreference)
      {
        bool = ((Boolean)paramObject).booleanValue();
        localContentResolver = getContentResolver();
        if (bool)
        {
          i = 1;
          Settings.System.putInt(localContentResolver, "screen_brightness_mode", i);
        }
      }
      else
      {
        if (paramPreference == this.mNetworkNameDisplayedPreference)
        {
          bool = ((Boolean)paramObject).booleanValue();
          localContentResolver = getContentResolver();
          if (!bool) {
            break label529;
          }
          i = 1;
          Settings.System.putInt(localContentResolver, "show_network_name_mode", i);
        }
        if (paramPreference == this.mLiftToWakePreference)
        {
          bool = ((Boolean)paramObject).booleanValue();
          localContentResolver = getContentResolver();
          if (!bool) {
            break label534;
          }
          i = 1;
          Settings.Secure.putInt(localContentResolver, "wake_gesture_enabled", i);
        }
        if (paramPreference == this.mDozePreference)
        {
          bool = ((Boolean)paramObject).booleanValue();
          localContentResolver = getContentResolver();
          if (!bool) {
            break label539;
          }
          i = 1;
          Settings.Secure.putInt(localContentResolver, "doze_enabled", i);
          updateDozeMode(bool);
        }
        if (paramPreference == this.mProxWakePreference)
        {
          bool = ((Boolean)paramObject).booleanValue();
          localContentResolver = getContentResolver();
          if (!bool) {
            break label544;
          }
          i = 1;
          Settings.System.putInt(localContentResolver, "prox_wake_enabled", i);
        }
        if (paramPreference == this.mHandUpProximityPreference)
        {
          bool = ((Boolean)paramObject).booleanValue();
          localContentResolver = getContentResolver();
          if (!bool) {
            break label549;
          }
          i = 1;
          Settings.System.putInt(localContentResolver, "prox_wake_enabled", i);
        }
        if (paramPreference == this.mTapToWakePreference)
        {
          bool = ((Boolean)paramObject).booleanValue();
          localContentResolver = getContentResolver();
          if (!bool) {
            break label554;
          }
          i = 1;
          Settings.Secure.putInt(localContentResolver, "double_tap_to_wake", i);
        }
        if (paramPreference == this.mCameraGesturePreference)
        {
          bool = ((Boolean)paramObject).booleanValue();
          localContentResolver = getContentResolver();
          if (!bool) {
            break label559;
          }
          i = 0;
          Settings.Secure.putInt(localContentResolver, "camera_gesture_disabled", i);
        }
        if (paramPreference == this.mCameraDoubleTapPowerGesturePreference)
        {
          bool = ((Boolean)paramObject).booleanValue();
          localContentResolver = getContentResolver();
          if (!bool) {
            break label564;
          }
          i = 0;
          Settings.Secure.putInt(localContentResolver, "camera_double_tap_power_gesture_disabled", i);
        }
        if (paramPreference != this.mNightModePreference) {}
      }
    }
    catch (NumberFormatException localNumberFormatException2)
    {
      try
      {
        for (;;)
        {
          i = Integer.parseInt((String)paramObject);
          ((UiModeManager)getSystemService("uimode")).setNightMode(i);
          if (paramPreference != this.mDarkModePreferce) {
            break;
          }
          bool = ((Boolean)paramObject).booleanValue();
          if (this.mDarkModeRunnable == null) {
            this.mDarkModeRunnable = new DarkModeRunnable();
          }
          this.mDarkModeRunnable.setValue(bool);
          this.mHandler.removeCallbacks(this.mDarkModeRunnable);
          this.mHandler.postDelayed(this.mDarkModeRunnable, 300L);
          return true;
          localNumberFormatException2 = localNumberFormatException2;
          Log.e("DisplaySettings", "could not persist screen timeout setting", localNumberFormatException2);
          continue;
          i = 0;
          continue;
          i = 0;
          continue;
          i = 0;
          continue;
          i = 0;
          continue;
          i = 0;
          continue;
          i = 0;
          continue;
          i = 0;
          continue;
          i = 1;
        }
        i = 1;
      }
      catch (NumberFormatException localNumberFormatException3)
      {
        for (;;)
        {
          Log.e("DisplaySettings", "could not persist night mode setting", localNumberFormatException3);
        }
        if (paramPreference == this.mNotifyLightPreference)
        {
          bool = ((Boolean)paramObject).booleanValue();
          if (bool) {}
          for (i = 1;; i = 0)
          {
            updateNotifyLightStatus(i);
            if (this.mLedSettingsPreference != null) {
              this.mLedSettingsPreference.setEnabled(bool);
            }
            return true;
          }
        }
        if ("theme_accent_color".equals(localObject))
        {
          str = (String)paramObject;
          if (!TextUtils.isEmpty(str)) {
            break label908;
          }
          sendTheme(0, false);
        }
      }
    }
    if ("op_theme_mode".equals(localObject)) {}
    for (;;)
    {
      try
      {
        i = Integer.parseInt((String)paramObject);
        if (i == 3) {
          continue;
        }
        Settings.System.putInt(getContentResolver(), "oem_black_mode", i);
        Settings.Global.putInt(getContentResolver(), "oem_black_mode", i);
        Settings.System.putInt(getContentResolver(), "oem_special_theme", 0);
        OPUtils.sendAppTracker("op_theme_mode", i);
        localObject = this.mDefaultHandler.obtainMessage(100);
        ((Message)localObject).arg1 = i;
        this.mDefaultHandler.sendMessageDelayed((Message)localObject, 100L);
        updateThemeModePreferenceDescription(getThemeModeValue(i));
      }
      catch (NumberFormatException localNumberFormatException1)
      {
        label908:
        Log.e("DisplaySettings", "could not persist screen timeout setting", localNumberFormatException1);
        continue;
        i = 0;
        continue;
        if ((i < 100) || (i > 199)) {
          continue;
        }
        this.mNightModeLevel = 2;
        continue;
        if ((i < 200) || (i > 299)) {
          continue;
        }
        this.mNightModeLevel = 3;
        continue;
        if ((i < 300) || (i > 399)) {
          continue;
        }
        this.mNightModeLevel = 4;
        continue;
        if ((i < 400) || (i > 499)) {
          continue;
        }
        this.mNightModeLevel = 5;
        continue;
        if ((i < 500) || (i > 600)) {
          continue;
        }
        this.mNightModeLevel = 6;
        continue;
        if ((this.mNightModeLevelPreference == null) || (this.mScreenColorModePreference == null)) {
          continue;
        }
        this.mScreenBrightnessRootPreference.removePreference(this.mNightModeLevelPreference);
        this.mScreenColorModePreference.setEnabled(true);
        updateScreenColorModePreference();
        continue;
      }
      if (paramPreference == this.mNightModeEnabledPreference)
      {
        bool = ((Boolean)paramObject).booleanValue();
        paramPreference = getContentResolver();
        if (!bool) {
          continue;
        }
        i = 1;
        Settings.Global.putInt(paramPreference, "night_mode_enabled", i);
        i = Settings.System.getInt(this.mContext.getContentResolver(), "oem_nightmode_progress_status", 400);
        if (!bool) {
          continue;
        }
        if ((this.mNightModeLevelPreference != null) && (this.mScreenColorModePreference != null))
        {
          this.mScreenBrightnessRootPreference.addPreference(this.mNightModeLevelPreference);
          this.mScreenColorModePreference.setEnabled(false);
          this.mScreenColorModePreference.setSummary(getResources().getString(2131690095));
        }
        if ((i < 0) || (i >= 100)) {
          continue;
        }
        this.mNightModeLevel = 1;
        this.mNightModeHandler.sendEmptyMessage(this.mNightModeLevel);
      }
      return true;
      if (OPUtils.isBlackModeOn(this.mContext.getContentResolver()))
      {
        i = 0;
        if (i < this.mColors.length)
        {
          if (str.equals(this.mColors[i]))
          {
            if (i == 0)
            {
              sendTheme(0, false);
              break;
            }
            sendTheme(i + 7, false);
            break;
          }
          i += 1;
          continue;
        }
        break;
      }
      if (!OPUtils.isWhiteModeOn(this.mContext.getContentResolver())) {
        break;
      }
      i = 0;
      if (i < this.mColors.length)
      {
        if (str.equals(this.mColors[i]))
        {
          sendTheme(i, false);
          break;
        }
        i += 1;
        continue;
      }
      break;
      Settings.System.putInt(getContentResolver(), "oem_black_mode", 1);
      Settings.Global.putInt(getContentResolver(), "oem_black_mode", 1);
      Settings.System.putInt(getContentResolver(), "oem_special_theme", 1);
      OPUtils.sendAppTracker("op_theme_mode", 3);
      localObject = this.mDefaultHandler.obtainMessage(101);
      ((Message)localObject).arg1 = 1;
      this.mDefaultHandler.sendMessageDelayed((Message)localObject, 100L);
      updateThemeModePreferenceDescription(getThemeModeValue(i));
    }
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference.getKey().equals("msm_screen_better_settings"))
    {
      paramPreference = new Intent();
      paramPreference.setComponent(new ComponentName("com.android.settings", "com.android.settings.OPScreenBetterActivity"));
      getActivity().startActivity(paramPreference);
      return true;
    }
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if (this.mToggleLockScreenRotationPreference == paramPreference) {
      handleLockScreenRotationPreferenceClick();
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    if (paramSeekBar != null)
    {
      if ((paramSeekBar.getProgress() < 0) || (paramSeekBar.getProgress() >= 100)) {
        break label54;
      }
      if (this.mNightModeLevel != 1)
      {
        this.mNightModeLevel = 1;
        this.mNightModeHandler.sendEmptyMessage(this.mNightModeLevel);
      }
    }
    for (;;)
    {
      onSaveNightModeSeekBarVale(paramSeekBar.getProgress());
      return;
      label54:
      if ((paramSeekBar.getProgress() >= 100) && (paramSeekBar.getProgress() <= 199))
      {
        if (this.mNightModeLevel != 2)
        {
          this.mNightModeLevel = 2;
          this.mNightModeHandler.sendEmptyMessage(this.mNightModeLevel);
        }
      }
      else if ((paramSeekBar.getProgress() >= 200) && (paramSeekBar.getProgress() <= 299))
      {
        if (this.mNightModeLevel != 3)
        {
          this.mNightModeLevel = 3;
          this.mNightModeHandler.sendEmptyMessage(this.mNightModeLevel);
        }
      }
      else if ((paramSeekBar.getProgress() >= 300) && (paramSeekBar.getProgress() <= 399))
      {
        if (this.mNightModeLevel != 4)
        {
          this.mNightModeLevel = 4;
          this.mNightModeHandler.sendEmptyMessage(this.mNightModeLevel);
        }
      }
      else if ((paramSeekBar.getProgress() >= 400) && (paramSeekBar.getProgress() <= 499))
      {
        if (this.mNightModeLevel != 5)
        {
          this.mNightModeLevel = 5;
          this.mNightModeHandler.sendEmptyMessage(this.mNightModeLevel);
        }
      }
      else if ((paramSeekBar.getProgress() >= 500) && (paramSeekBar.getProgress() <= 600) && (this.mNightModeLevel != 6))
      {
        this.mNightModeLevel = 6;
        this.mNightModeHandler.sendEmptyMessage(this.mNightModeLevel);
      }
    }
  }
  
  public void onResume()
  {
    boolean bool = false;
    super.onResume();
    updateState();
    long l1 = Settings.System.getLong(getActivity().getContentResolver(), "screen_off_timeout", 30000L);
    this.mScreenTimeoutPreference.setValue(String.valueOf(l1));
    this.mScreenTimeoutPreference.setOnPreferenceChangeListener(this);
    Object localObject = (DevicePolicyManager)getActivity().getSystemService("device_policy");
    if (localObject != null)
    {
      RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfMaximumTimeToLockIsSet(getActivity());
      long l2 = ((DevicePolicyManager)localObject).getMaximumTimeToLockForUserAndProfiles(UserHandle.myUserId());
      this.mScreenTimeoutPreference.removeUnusableTimeouts(l2, localEnforcedAdmin);
    }
    updateTimeoutPreferenceDescription(l1);
    disablePreferenceIfManaged("wallpaper", "no_set_wallpaper");
    updateLockScreenRotation();
    if (RotationPolicy.isRotationSupported(getActivity())) {
      RotationPolicy.registerRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
    }
    localObject = this.mBacktopThemePreference;
    if (Settings.System.getInt(getActivity().getContentResolver(), "oem_acc_backgap_theme", 0) == 0) {}
    for (;;)
    {
      ((SwitchPreference)localObject).setChecked(bool);
      return;
      bool = true;
    }
  }
  
  public void onStart()
  {
    super.onStart();
    getContentResolver().registerContentObserver(Settings.Secure.getUriFor("night_display_activated"), true, this.mNightModeObserver, -1);
    getContentResolver().registerContentObserver(Settings.System.getUriFor("reading_mode_status"), true, this.mNightModeObserver, -1);
  }
  
  public void onStartTrackingTouch(SeekBar paramSeekBar) {}
  
  public void onStop()
  {
    super.onStop();
    getContentResolver().unregisterContentObserver(this.mNightModeObserver);
  }
  
  public void onStopTrackingTouch(SeekBar paramSeekBar) {}
  
  public void saveBrightnessDataBase(final int paramInt)
  {
    if (this.mNewController)
    {
      AsyncTask.execute(new Runnable()
      {
        public void run()
        {
          if (paramInt < 2) {}
          for (int i = 2;; i = paramInt)
          {
            Settings.System.putIntForUser(DisplaySettings.-get3(DisplaySettings.this).getContentResolver(), "screen_brightness", i, -2);
            Settings.System.putFloatForUser(DisplaySettings.-get3(DisplaySettings.this).getContentResolver(), "screen_auto_brightness_adj", i, -2);
            return;
          }
        }
      });
      return;
    }
    if (!this.mAutomatic)
    {
      AsyncTask.execute(new Runnable()
      {
        public void run()
        {
          if (paramInt < 2) {}
          for (int i = 2;; i = paramInt)
          {
            Settings.System.putIntForUser(DisplaySettings.-get3(DisplaySettings.this).getContentResolver(), "screen_brightness", i, -2);
            return;
          }
        }
      });
      return;
    }
    AsyncTask.execute(new Runnable()
    {
      public void run()
      {
        Settings.System.putFloatForUser(DisplaySettings.-get3(DisplaySettings.this).getContentResolver(), "screen_auto_brightness_adj", this.val$adj, -2);
      }
    });
  }
  
  private class BrightnessObserver
    extends ContentObserver
  {
    private final Uri BRIGHTNESS_ADJ_URI = Settings.System.getUriFor("screen_auto_brightness_adj");
    private final Uri BRIGHTNESS_MODE_URI = Settings.System.getUriFor("screen_brightness_mode");
    private final Uri BRIGHTNESS_URI = Settings.System.getUriFor("screen_brightness");
    private final Uri SCREEN_TIMEOUT_URI = Settings.System.getUriFor("screen_off_timeout");
    
    public BrightnessObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      onChange(paramBoolean, null);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if (paramBoolean) {
        return;
      }
      for (;;)
      {
        try
        {
          DisplaySettings.-set1(DisplaySettings.this, true);
          if (this.BRIGHTNESS_MODE_URI.equals(paramUri))
          {
            DisplaySettings.-wrap9(DisplaySettings.this);
            DisplaySettings.-set0(DisplaySettings.this, true);
            DisplaySettings.-wrap7(DisplaySettings.this);
            DisplaySettings.-wrap11(DisplaySettings.this);
            return;
          }
          if ((!this.BRIGHTNESS_URI.equals(paramUri)) || (DisplaySettings.-get1(DisplaySettings.this)))
          {
            if ((!this.BRIGHTNESS_ADJ_URI.equals(paramUri)) || (!DisplaySettings.-get1(DisplaySettings.this))) {
              break label139;
            }
            DisplaySettings.-wrap11(DisplaySettings.this);
            continue;
          }
          DisplaySettings.-wrap11(DisplaySettings.this);
        }
        finally
        {
          DisplaySettings.-set1(DisplaySettings.this, false);
        }
        continue;
        label139:
        if (this.SCREEN_TIMEOUT_URI.equals(paramUri))
        {
          int i = Settings.System.getInt(DisplaySettings.-get3(DisplaySettings.this).getContentResolver(), "screen_off_timeout", 30000);
          DisplaySettings.-wrap12(DisplaySettings.this, i);
        }
        else
        {
          DisplaySettings.-wrap9(DisplaySettings.this);
          DisplaySettings.-wrap11(DisplaySettings.this);
        }
      }
    }
    
    public void startObserving()
    {
      ContentResolver localContentResolver = DisplaySettings.-get3(DisplaySettings.this).getContentResolver();
      localContentResolver.unregisterContentObserver(this);
      localContentResolver.registerContentObserver(this.BRIGHTNESS_MODE_URI, false, this, -1);
      localContentResolver.registerContentObserver(this.BRIGHTNESS_URI, false, this, -1);
      localContentResolver.registerContentObserver(this.BRIGHTNESS_ADJ_URI, false, this, -1);
      localContentResolver.registerContentObserver(this.SCREEN_TIMEOUT_URI, false, this, -1);
    }
    
    public void stopObserving()
    {
      DisplaySettings.-get3(DisplaySettings.this).getContentResolver().unregisterContentObserver(this);
    }
  }
  
  class DarkModeRunnable
    implements Runnable
  {
    boolean dValue = false;
    
    public DarkModeRunnable() {}
    
    public DarkModeRunnable(boolean paramBoolean)
    {
      this.dValue = paramBoolean;
    }
    
    public void run()
    {
      Object localObject = DisplaySettings.-get3(DisplaySettings.this).getContentResolver();
      if (this.dValue) {}
      for (int i = 1;; i = 0)
      {
        Settings.System.putInt((ContentResolver)localObject, "oem_black_mode", i);
        localObject = new Intent("android.settings.OEM_THEME_MODE");
        ((Intent)localObject).addFlags(268435456);
        ((Intent)localObject).putExtra("oem_black_mode", this.dValue);
        DisplaySettings.-get3(DisplaySettings.this).sendBroadcast((Intent)localObject);
        return;
      }
    }
    
    public void setValue(boolean paramBoolean)
    {
      this.dValue = paramBoolean;
    }
  }
  
  static class DefaultHandler
    extends Handler
  {
    private final WeakReference<Context> mReference;
    
    public DefaultHandler(Context paramContext)
    {
      this.mReference = new WeakReference(paramContext);
    }
    
    public void handleMessage(Message paramMessage)
    {
      Context localContext = (Context)this.mReference.get();
      if (localContext == null) {
        return;
      }
      switch (paramMessage.what)
      {
      default: 
        return;
      case 100: 
        localIntent = new Intent("android.settings.OEM_THEME_MODE");
        localIntent.addFlags(268435456);
        localIntent.putExtra("oem_theme_mode", paramMessage.arg1);
        localIntent.putExtra("special_theme", false);
        localContext.sendBroadcast(localIntent);
        return;
      }
      Intent localIntent = new Intent("android.settings.OEM_THEME_MODE");
      localIntent.addFlags(268435456);
      localIntent.putExtra("oem_theme_mode", paramMessage.arg1);
      localIntent.putExtra("special_theme", true);
      localContext.sendBroadcast(localIntent);
    }
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mLoader;
    
    private SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mLoader = paramSummaryLoader;
    }
    
    private void updateSummary()
    {
      SummaryLoader localSummaryLoader;
      Context localContext;
      if (Settings.System.getInt(this.mContext.getContentResolver(), "screen_brightness_mode", 1) == 1)
      {
        i = 1;
        localSummaryLoader = this.mLoader;
        localContext = this.mContext;
        if (i == 0) {
          break label52;
        }
      }
      label52:
      for (int i = 2131693586;; i = 2131693587)
      {
        localSummaryLoader.setSummary(this, localContext.getString(i));
        return;
        i = 0;
        break;
      }
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean) {
        updateSummary();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\DisplaySettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */