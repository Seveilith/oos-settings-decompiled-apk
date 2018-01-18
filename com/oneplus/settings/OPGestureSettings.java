package com.oneplus.settings;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.oneplus.settings.gestures.OPGestureUtils;
import com.oneplus.settings.ui.OPGesturePreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPGestureSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, Indexable
{
  private static final String ANTI_MISOPERATION_SCREEN_TOUCH = "anti_misoperation_of_the_screen_touch_enable";
  private static String BLACK_SCREEN_GESTURES = "black_screen_gestures";
  private static final String BLACK_SCREEN_SETTINGS_KEY = "black_screen_setting_key";
  private static final String DOUBLE_CLICK_LIGHT_SCREEN_KEY = "double_click_light_screen_key";
  private static final String DRAW_O_START_CAMERA_KEY = "draw_o_start_camera_key";
  private static final String FINGERPRINT_GESTURE_CONTROL_KEY = "fingerprint_gesture_control";
  private static final String FINGERPRINT_GESTURE_SWIPE_DOWN_UP_KEY = "op_fingerprint_gesture_swipe_down_up";
  private static final String FINGERPRINT_LONG_PRESS_CAMERA_SHOT_KEY = "op_fingerprint_long_press_camera_shot";
  private static final String FLASH_LIGHT_KEY = "open_light_device_key";
  private static final String MUSCI_CONTROL_KEY = "music_control_key";
  private static final String MUSIC_CONTROL_NEXT_KEY = "music_control_next_key";
  private static final String MUSIC_CONTROL_PAUSE_KEY = "music_control_pause_key";
  private static final String MUSIC_CONTROL_PREV_KEY = "music_control_prev_key";
  private static final String MUSIC_CONTROL_START_KEY = "music_control_start_key";
  private static String MUSIC_ROOT_KEY;
  private static final String ROTATION_SILENT_KEY = "rotation_silent_enable";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new OPGestureSearchIndexProvider();
  private static String STARTUP_ROOT_KEY = "quick_startup";
  private static final String THREE_SCEENTSHOTS_KEY = "three_screenshots_enable";
  private int isDoubleClickEnable;
  private int isFlashlightEnable;
  private int isMusicControlEnable;
  private int isMusicNextEnable;
  private int isMusicPauseEnable;
  private int isMusicPlayEnable;
  private int isMusicPrevEnable;
  private int isStartUpCameraEnable;
  private boolean isSupportThreeScrrenShot = false;
  private PreferenceCategory mBlackScreenPrefererce;
  private int mBlackSettingValues;
  private SwitchPreference mCameraPerference;
  private Context mContext;
  private SwitchPreference mDoubleLightScreenPreference;
  private OPGesturePreference mDrawMStartAppPreference;
  private OPGesturePreference mDrawOStartAppPreference;
  private OPGesturePreference mDrawSStartAppPreference;
  private OPGesturePreference mDrawVStartAppPreference;
  private OPGesturePreference mDrawWStartAppPreference;
  private PreferenceCategory mFingerprintGestureCategory;
  private SwitchPreference mFingerprintGestureLongpressCamera;
  private SwitchPreference mFingerprintGestureSwipeDownUp;
  private SwitchPreference mFlashLightPreference;
  private SwitchPreference mMusicControlPreference;
  private SwitchPreference mMusicNextPreference;
  private SwitchPreference mMusicPausePreference;
  private SwitchPreference mMusicPreference;
  private PreferenceCategory mMusicPrefererce;
  private SwitchPreference mMusicPrevPreference;
  private SwitchPreference mMusicStartPreference;
  private SwitchPreference mRotationSilent;
  private PreferenceCategory mStartUpPreferece;
  private SwitchPreference mThreeSwipeScreenShot;
  private UserManager mUm;
  private PreferenceScreen root;
  
  static
  {
    MUSIC_ROOT_KEY = "music_control";
  }
  
  private void getConfig()
  {
    int i = 1;
    this.mBlackSettingValues = Settings.System.getInt(getActivity().getContentResolver(), "oem_acc_blackscreen_gestrue_enable", 0);
    this.isFlashlightEnable = OPGestureUtils.get(this.mBlackSettingValues, 0);
    this.isMusicPlayEnable = OPGestureUtils.get(this.mBlackSettingValues, 1);
    this.isMusicPauseEnable = OPGestureUtils.get(this.mBlackSettingValues, 2);
    this.isMusicNextEnable = OPGestureUtils.get(this.mBlackSettingValues, 3);
    this.isMusicPrevEnable = OPGestureUtils.get(this.mBlackSettingValues, 4);
    if (this.isMusicPlayEnable == 1) {}
    for (;;)
    {
      this.isMusicControlEnable = i;
      this.isStartUpCameraEnable = OPGestureUtils.get(this.mBlackSettingValues, 6);
      this.isDoubleClickEnable = OPGestureUtils.get(this.mBlackSettingValues, 7);
      return;
      i = 0;
    }
  }
  
  private static List<String> getNonVisibleKeys()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add("double_click_light_screen_key");
    localArrayList.add("music_control_key");
    localArrayList.add("rotation_silent_enable");
    localArrayList.add("three_screenshots_enable");
    localArrayList.add("anti_misoperation_of_the_screen_touch_enable");
    return localArrayList;
  }
  
  private void initBlackScreenView()
  {
    boolean bool2 = false;
    this.mStartUpPreferece = ((PreferenceCategory)findPreference(STARTUP_ROOT_KEY));
    this.mMusicPrefererce = ((PreferenceCategory)findPreference(MUSIC_ROOT_KEY));
    this.mBlackScreenPrefererce = ((PreferenceCategory)findPreference(BLACK_SCREEN_GESTURES));
    this.mCameraPerference = ((SwitchPreference)findPreference("draw_o_start_camera_key"));
    this.mCameraPerference.setOnPreferenceClickListener(this);
    if (OPUtils.isSurportGesture20(this.mContext)) {
      this.mBlackScreenPrefererce.removePreference(this.mCameraPerference);
    }
    this.mDoubleLightScreenPreference = ((SwitchPreference)findPreference("double_click_light_screen_key"));
    this.mDoubleLightScreenPreference.setOnPreferenceClickListener(this);
    this.mMusicControlPreference = ((SwitchPreference)findPreference("music_control_key"));
    this.mMusicControlPreference.setOnPreferenceClickListener(this);
    this.mFlashLightPreference = ((SwitchPreference)findPreference("open_light_device_key"));
    this.mFlashLightPreference.setOnPreferenceClickListener(this);
    if (OPUtils.isSurportGesture20(this.mContext)) {
      this.mBlackScreenPrefererce.removePreference(this.mFlashLightPreference);
    }
    getConfig();
    SwitchPreference localSwitchPreference;
    if (!OPUtils.isSurportGesture20(this.mContext))
    {
      localSwitchPreference = this.mCameraPerference;
      if (this.isStartUpCameraEnable == 0)
      {
        bool1 = false;
        localSwitchPreference.setChecked(bool1);
      }
    }
    else
    {
      localSwitchPreference = this.mDoubleLightScreenPreference;
      if (this.isDoubleClickEnable != 0) {
        break label278;
      }
      bool1 = false;
      label219:
      localSwitchPreference.setChecked(bool1);
      localSwitchPreference = this.mMusicControlPreference;
      if (this.isMusicControlEnable != 0) {
        break label283;
      }
      bool1 = false;
      label238:
      localSwitchPreference.setChecked(bool1);
      if (!OPUtils.isSurportGesture20(this.mContext))
      {
        localSwitchPreference = this.mFlashLightPreference;
        if (this.isFlashlightEnable != 0) {
          break label288;
        }
      }
    }
    label278:
    label283:
    label288:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      localSwitchPreference.setChecked(bool1);
      return;
      bool1 = true;
      break;
      bool1 = true;
      break label219;
      bool1 = true;
      break label238;
    }
  }
  
  private void initFingerprintGesture()
  {
    this.mFingerprintGestureCategory = ((PreferenceCategory)findPreference("fingerprint_gesture_control"));
    if (OPUtils.isSurportBackFingerprint(this.mContext))
    {
      this.mFingerprintGestureSwipeDownUp = ((SwitchPreference)findPreference("op_fingerprint_gesture_swipe_down_up"));
      this.mFingerprintGestureSwipeDownUp.setChecked(isSystemUINavigationEnabled(this.mContext));
      this.mFingerprintGestureSwipeDownUp.setOnPreferenceChangeListener(this);
      this.mFingerprintGestureLongpressCamera = ((SwitchPreference)findPreference("op_fingerprint_long_press_camera_shot"));
      this.mFingerprintGestureLongpressCamera.setChecked(isFingerprintLongpressCameraShotEnabled(this.mContext));
      this.mFingerprintGestureLongpressCamera.setOnPreferenceChangeListener(this);
      return;
    }
    getPreferenceScreen().removePreference(this.mFingerprintGestureCategory);
    getPreferenceScreen().removePreference(findPreference("preference_divider_line_1"));
  }
  
  private void initGestureSummary()
  {
    Activity localActivity = getActivity();
    if (localActivity == null) {
      return;
    }
    this.mDrawOStartAppPreference.setSummary(OPGestureUtils.getGestureSummarybyGestureKey(localActivity, "oneplus_draw_o_start_app"));
    this.mDrawVStartAppPreference.setSummary(OPGestureUtils.getGestureSummarybyGestureKey(localActivity, "oneplus_draw_v_start_app"));
    this.mDrawSStartAppPreference.setSummary(OPGestureUtils.getGestureSummarybyGestureKey(localActivity, "oneplus_draw_s_start_app"));
    this.mDrawMStartAppPreference.setSummary(OPGestureUtils.getGestureSummarybyGestureKey(localActivity, "oneplus_draw_m_start_app"));
    this.mDrawWStartAppPreference.setSummary(OPGestureUtils.getGestureSummarybyGestureKey(localActivity, "oneplus_draw_w_start_app"));
  }
  
  private void initGestureViews()
  {
    this.mDrawOStartAppPreference = ((OPGesturePreference)findPreference("oneplus_draw_o_start_app"));
    this.mDrawVStartAppPreference = ((OPGesturePreference)findPreference("oneplus_draw_v_start_app"));
    this.mDrawSStartAppPreference = ((OPGesturePreference)findPreference("oneplus_draw_s_start_app"));
    this.mDrawMStartAppPreference = ((OPGesturePreference)findPreference("oneplus_draw_m_start_app"));
    this.mDrawWStartAppPreference = ((OPGesturePreference)findPreference("oneplus_draw_w_start_app"));
    if (!OPUtils.isSurportGesture20(this.mContext))
    {
      this.mBlackScreenPrefererce.removePreference(this.mDrawOStartAppPreference);
      this.mBlackScreenPrefererce.removePreference(this.mDrawVStartAppPreference);
      this.mBlackScreenPrefererce.removePreference(this.mDrawSStartAppPreference);
      this.mBlackScreenPrefererce.removePreference(this.mDrawMStartAppPreference);
      this.mBlackScreenPrefererce.removePreference(this.mDrawWStartAppPreference);
    }
  }
  
  private void initSensorView()
  {
    boolean bool2 = false;
    this.root = getPreferenceScreen();
    this.isSupportThreeScrrenShot = this.mContext.getPackageManager().hasSystemFeature("oem.threeScreenshot.support");
    this.mThreeSwipeScreenShot = ((SwitchPreference)findPreference("three_screenshots_enable"));
    this.mThreeSwipeScreenShot.setOnPreferenceClickListener(this);
    this.mRotationSilent = ((SwitchPreference)findPreference("rotation_silent_enable"));
    this.mRotationSilent.setOnPreferenceClickListener(this);
    int i = Settings.System.getInt(getActivity().getContentResolver(), "oem_acc_sensor_three_finger", 0);
    SwitchPreference localSwitchPreference = this.mThreeSwipeScreenShot;
    if (i == 0)
    {
      bool1 = false;
      localSwitchPreference.setChecked(bool1);
      localSwitchPreference = this.mRotationSilent;
      if (Settings.System.getInt(getActivity().getContentResolver(), "oem_acc_sensor_rotate_silent", 0) != 0) {
        break label158;
      }
    }
    label158:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      localSwitchPreference.setChecked(bool1);
      if (!this.isSupportThreeScrrenShot) {
        this.root.removePreference(this.mThreeSwipeScreenShot);
      }
      return;
      bool1 = true;
      break;
    }
  }
  
  private static boolean isFingerprintLongpressCameraShotEnabled(Context paramContext)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "op_fingerprint_long_press_camera_shot", 0) == 1;
  }
  
  private static boolean isSystemUINavigationEnabled(Context paramContext)
  {
    return Settings.Secure.getInt(paramContext.getContentResolver(), "system_navigation_keys_enabled", 0) == 1;
  }
  
  private void toggleMusicController(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      OPGestureUtils.set1(this.mContext, 1);
      OPGestureUtils.set1(this.mContext, 2);
      OPGestureUtils.set1(this.mContext, 3);
      OPGestureUtils.set1(this.mContext, 4);
      return;
    }
    OPGestureUtils.set0(this.mContext, 1);
    OPGestureUtils.set0(this.mContext, 2);
    OPGestureUtils.set0(this.mContext, 3);
    OPGestureUtils.set0(this.mContext, 4);
  }
  
  public boolean checkIfNeedPasswordToPowerOn()
  {
    return Settings.Global.getInt(getActivity().getContentResolver(), "require_password_to_decrypt", 0) == 1;
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUm = ((UserManager)getSystemService("user"));
    addPreferencesFromResource(2131230795);
    this.mContext = getActivity();
    initFingerprintGesture();
    initBlackScreenView();
    initGestureViews();
    initSensorView();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    int j = 0;
    int i = 0;
    boolean bool = ((Boolean)paramObject).booleanValue();
    paramPreference = paramPreference.getKey();
    if ("op_fingerprint_gesture_swipe_down_up".equals(paramPreference))
    {
      paramPreference = getContentResolver();
      if (bool) {
        i = 1;
      }
      Settings.Secure.putInt(paramPreference, "system_navigation_keys_enabled", i);
    }
    while (!"op_fingerprint_long_press_camera_shot".equals(paramPreference)) {
      return true;
    }
    paramPreference = getContentResolver();
    i = j;
    if (bool) {
      i = 1;
    }
    Settings.System.putInt(paramPreference, "op_fingerprint_long_press_camera_shot", i);
    return true;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    int i = 0;
    int j = 0;
    if (paramPreference.getKey().equals("draw_o_start_camera_key"))
    {
      if (this.mCameraPerference.isChecked())
      {
        OPGestureUtils.set1(this.mContext, 6);
        return true;
      }
      OPGestureUtils.set0(this.mContext, 6);
      return true;
    }
    if (paramPreference.getKey().equals("double_click_light_screen_key"))
    {
      if (this.mDoubleLightScreenPreference.isChecked())
      {
        OPGestureUtils.set1(this.mContext, 7);
        return true;
      }
      OPGestureUtils.set0(this.mContext, 7);
      return true;
    }
    if (paramPreference.getKey().equals("music_control_key"))
    {
      toggleMusicController(this.mMusicControlPreference.isChecked());
      return true;
    }
    if (paramPreference.getKey().equals("music_control_next_key"))
    {
      if (this.mMusicNextPreference.isChecked())
      {
        OPGestureUtils.set1(this.mContext, 3);
        return true;
      }
      OPGestureUtils.set0(this.mContext, 3);
      return true;
    }
    if (paramPreference.getKey().equals("music_control_prev_key"))
    {
      if (this.mMusicPrevPreference.isChecked())
      {
        OPGestureUtils.set1(this.mContext, 4);
        return true;
      }
      OPGestureUtils.set0(this.mContext, 4);
      return true;
    }
    if (paramPreference.getKey().equals("open_light_device_key"))
    {
      if (this.mFlashLightPreference.isChecked())
      {
        OPGestureUtils.set1(this.mContext, 0);
        return true;
      }
      OPGestureUtils.set0(this.mContext, 0);
      return true;
    }
    if (paramPreference.getKey().equals("music_control_start_key"))
    {
      if (this.mMusicStartPreference.isChecked())
      {
        OPGestureUtils.set1(this.mContext, 1);
        return true;
      }
      OPGestureUtils.set0(this.mContext, 1);
      return true;
    }
    if (paramPreference.getKey().equals("music_control_pause_key"))
    {
      if (this.mMusicPausePreference.isChecked())
      {
        OPGestureUtils.set1(this.mContext, 2);
        return true;
      }
      OPGestureUtils.set0(this.mContext, 2);
      return true;
    }
    if (paramPreference.getKey().equals("three_screenshots_enable"))
    {
      paramPreference = getActivity().getContentResolver();
      if (this.mThreeSwipeScreenShot.isChecked()) {}
      for (i = 1;; i = 0)
      {
        Settings.System.putInt(paramPreference, "oem_acc_sensor_three_finger", i);
        if ((this.mUm != null) && (this.mUm.isUserRunning(999)))
        {
          paramPreference = getActivity().getContentResolver();
          i = j;
          if (this.mThreeSwipeScreenShot.isChecked()) {
            i = 1;
          }
          Settings.System.putIntForUser(paramPreference, "oem_acc_sensor_three_finger", i, 999);
        }
        return true;
      }
    }
    if (paramPreference.getKey().equals("rotation_silent_enable"))
    {
      paramPreference = getActivity().getContentResolver();
      if (this.mRotationSilent.isChecked()) {
        i = 1;
      }
      Settings.System.putInt(paramPreference, "oem_acc_sensor_rotate_silent", i);
      return true;
    }
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    initGestureSummary();
  }
  
  private static class OPGestureSearchIndexProvider
    extends BaseSearchIndexProvider
  {
    boolean mIsPrimary;
    
    public OPGestureSearchIndexProvider()
    {
      if (UserHandle.myUserId() == 0) {
        bool = true;
      }
      this.mIsPrimary = bool;
    }
    
    public List<String> getNonIndexableKeys(Context paramContext)
    {
      Object localObject = new ArrayList();
      if (!this.mIsPrimary) {
        localObject = OPGestureSettings.-wrap0();
      }
      if (!this.mIsPrimary)
      {
        ((List)localObject).add("open_light_device_key");
        ((List)localObject).add("draw_o_start_camera_key");
      }
      if ((this.mIsPrimary) && (OPUtils.isSurportGesture20(paramContext))) {
        return (List<String>)localObject;
      }
      ((List)localObject).add("oneplus_draw_o_start_app");
      ((List)localObject).add("oneplus_draw_v_start_app");
      ((List)localObject).add("oneplus_draw_s_start_app");
      ((List)localObject).add("oneplus_draw_m_start_app");
      ((List)localObject).add("oneplus_draw_w_start_app");
      return (List<String>)localObject;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramContext, boolean paramBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      if (!this.mIsPrimary) {
        return localArrayList;
      }
      paramContext = new SearchIndexableResource(paramContext);
      paramContext.xmlResId = 2131230795;
      localArrayList.add(paramContext);
      return localArrayList;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPGestureSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */