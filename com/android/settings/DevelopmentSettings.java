package com.android.settings;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.app.Application;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManager.Stub;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.IShortcutService;
import android.content.pm.IShortcutService.Stub;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbManager.Stub;
import android.hardware.usb.UsbManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.IMountService;
import android.os.storage.IMountService.Stub;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.service.persistentdata.PersistentDataBlockManager;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.IWebViewUpdateService;
import android.webkit.IWebViewUpdateService.Stub;
import android.webkit.WebViewProviderInfo;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.app.LocalePicker;
import com.android.settings.applications.BackgroundCheckSummary;
import com.android.settings.fuelgauge.InactiveApps;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class DevelopmentSettings
  extends RestrictedSettingsFragment
  implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, Preference.OnPreferenceChangeListener, SwitchBar.OnSwitchChangeListener, Indexable
{
  private static final String ACTUAL_LOGPERSIST_PROPERTY = "logd.logpersistd";
  private static final String ACTUAL_LOGPERSIST_PROPERTY_BUFFER = "logd.logpersistd.buffer";
  private static final String ACTUAL_LOGPERSIST_PROPERTY_ENABLE = "logd.logpersistd.enable";
  private static final String ADVANCED_REBOOT_KEY = "advanced_reboot";
  private static final String ANIMATOR_DURATION_SCALE_KEY = "animator_duration_scale";
  private static final String APP_PROCESS_LIMIT_KEY = "app_process_limit";
  private static final String APTX_HD_SUPPORT = "aptx_hd_support";
  private static final String BACKGROUND_CHECK_KEY = "background_check";
  private static final String BLUETOOTH_DISABLE_ABSOLUTE_VOLUME_KEY = "bluetooth_disable_absolute_volume";
  private static final String BLUETOOTH_DISABLE_ABSOLUTE_VOLUME_PROPERTY = "persist.bluetooth.disableabsvol";
  private static final String BT_HCI_SNOOP_LOG = "bt_hci_snoop_log";
  private static final String BUGREPORT = "bugreport";
  private static final String BUGREPORT_IN_POWER_KEY = "bugreport_in_power";
  private static final String CLEAR_ADB_KEYS = "clear_adb_keys";
  private static final String COLOR_TEMPERATURE_KEY = "color_temperature";
  private static final String COLOR_TEMPERATURE_PROPERTY = "persist.sys.debug.color_temp";
  private static final String DEBUG_APP_KEY = "debug_app";
  private static final String DEBUG_DEBUGGING_CATEGORY_KEY = "debug_debugging_category";
  private static final String DEBUG_HW_OVERDRAW_KEY = "debug_hw_overdraw";
  private static final String DEBUG_LAYOUT_KEY = "debug_layout";
  private static final String DEBUG_VIEW_ATTRIBUTES = "debug_view_attributes";
  private static final String DISABLE_DOZE_KEY = "disable_doze";
  private static final String DISABLE_OVERLAYS_KEY = "disable_overlays";
  private static final String ENABLE_ADB = "enable_adb";
  private static final String ENABLE_OEM_UNLOCK = "oem_unlock_enable";
  private static final String ENABLE_TERMINAL = "enable_terminal";
  private static final String FLASH_LOCKED_PROP = "ro.boot.flash.locked";
  private static final String FORCE_ALLOW_ON_EXTERNAL_KEY = "force_allow_on_external";
  private static final String FORCE_HARDWARE_UI_KEY = "force_hw_ui";
  private static final String FORCE_MSAA_KEY = "force_msaa";
  private static final String FORCE_RESIZABLE_KEY = "force_resizable_activities";
  private static final String FORCE_RTL_LAYOUT_KEY = "force_rtl_layout_all_locales";
  private static final String HARDWARE_UI_PROPERTY = "persist.sys.ui.hw";
  private static final String HDCP_CHECKING_KEY = "hdcp_checking";
  private static final String HDCP_CHECKING_PROPERTY = "persist.sys.hdcp_checking";
  private static final String IMMEDIATELY_DESTROY_ACTIVITIES_KEY = "immediately_destroy_activities";
  private static final String INACTIVE_APPS_KEY = "inactive_apps";
  private static final String KEEP_SCREEN_ON = "keep_screen_on";
  private static final String KEY_COLOR_MODE = "color_mode";
  private static final String KEY_CONVERT_FBE = "convert_to_file_encryption";
  private static final String LOCAL_BACKUP_PASSWORD = "local_backup_password";
  private static final String MOBILE_DATA_ALWAYS_ON = "mobile_data_always_on";
  private static final String MOCK_LOCATION_APP_KEY = "mock_location_app";
  private static final int[] MOCK_LOCATION_APP_OPS = { 58 };
  private static final String MSAA_PROPERTY = "debug.egl.force_msaa";
  private static final String ONEPLUS_GET_LOGS = "getlogs";
  private static final String OPENGL_TRACES_PROPERTY = "debug.egl.trace";
  private static final String OP_WIFI_VERBOSE_MULTI_BROADCAST = "op_enable_wifi_multi_broadcast";
  private static final String OP_WIFI_VERBOSE_MULTI_BROADCAST_KEY = "op_wifi_verbose_multi_broadcast";
  private static final String OP_WIRELESS_ADB_DEBUGGING_KEY = "op_wireless_adb_debugging";
  private static final String OP_WIRELESS_ADB_DEBUGGING_PROPERTY = "service.adb.tcp.port";
  private static final String OP_WIRELESS_ADB_DEBUGGING_PROPERTY_DISENABLE = "-1";
  private static final String OP_WIRELESS_ADB_DEBUGGING_PROPERTY_ENABLE = "5555";
  private static final String OTA_DISABLE_AUTOMATIC_UPDATE_KEY = "ota_disable_automatic_update";
  private static final String OVERLAY_DISPLAY_DEVICES_KEY = "overlay_display_devices";
  private static final String PACKAGE_MIME_TYPE = "application/vnd.android.package-archive";
  private static final String PERSISTENT_DATA_BLOCK_PROP = "ro.frp.pst";
  private static final String POINTER_LOCATION_KEY = "pointer_location";
  public static final String PREF_FILE = "development";
  public static final String PREF_SHOW = "show";
  private static final int REQUEST_CODE_ENABLE_OEM_UNLOCK = 0;
  private static final int RESULT_DEBUG_APP = 1000;
  private static final int RESULT_MOCK_LOCATION_APP = 1001;
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    private boolean isShowingDeveloperOptions(Context paramAnonymousContext)
    {
      return paramAnonymousContext.getSharedPreferences("development", 0).getBoolean("show", Build.TYPE.equals("eng"));
    }
    
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      if (!isShowingDeveloperOptions(paramAnonymousContext)) {
        return null;
      }
      ArrayList localArrayList = new ArrayList();
      if (!DevelopmentSettings.-wrap0()) {
        localArrayList.add("oem_unlock_enable");
      }
      if (!paramAnonymousContext.getResources().getBoolean(2131558453)) {
        localArrayList.add("color_temperature");
      }
      localArrayList.add("convert_to_file_encryption");
      localArrayList.add("enable_terminal");
      if (!DevelopmentSettings.-get0()) {
        localArrayList.add("op_wireless_adb_debugging");
      }
      return localArrayList;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      if (!isShowingDeveloperOptions(paramAnonymousContext)) {
        return null;
      }
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230756;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final String SELECT_LOGD_DEFAULT_SIZE_PROPERTY = "ro.logd.size";
  private static final String SELECT_LOGD_DEFAULT_SIZE_VALUE = "262144";
  private static final String SELECT_LOGD_MINIMUM_SIZE_VALUE = "65536";
  private static final String SELECT_LOGD_OFF_SIZE_MARKER_VALUE = "32768";
  private static final String SELECT_LOGD_RUNTIME_SNET_TAG_PROPERTY = "log.tag.snet_event_log";
  private static final String SELECT_LOGD_SIZE_KEY = "select_logd_size";
  private static final String SELECT_LOGD_SIZE_PROPERTY = "persist.logd.size";
  private static final String SELECT_LOGD_SNET_TAG_PROPERTY = "persist.log.tag.snet_event_log";
  private static final String SELECT_LOGD_SVELTE_DEFAULT_SIZE_VALUE = "65536";
  private static final String SELECT_LOGD_TAG_PROPERTY = "persist.log.tag";
  private static final String SELECT_LOGD_TAG_SILENCE = "Settings";
  private static final String SELECT_LOGPERSIST_KEY = "select_logpersist";
  private static final String SELECT_LOGPERSIST_PROPERTY = "persist.logd.logpersistd";
  private static final String SELECT_LOGPERSIST_PROPERTY_BUFFER = "persist.logd.logpersistd.buffer";
  private static final String SELECT_LOGPERSIST_PROPERTY_CLEAR = "clear";
  private static final String SELECT_LOGPERSIST_PROPERTY_SERVICE = "logcatd";
  private static final String SELECT_LOGPERSIST_PROPERTY_STOP = "stop";
  private static final String SHORTCUT_MANAGER_RESET_KEY = "reset_shortcut_manager_throttling";
  private static final String SHOW_ALL_ANRS_KEY = "show_all_anrs";
  private static final String SHOW_CPU_USAGE_KEY = "show_cpu_usage";
  private static final String SHOW_HW_LAYERS_UPDATES_KEY = "show_hw_layers_udpates";
  private static final String SHOW_HW_SCREEN_UPDATES_KEY = "show_hw_screen_udpates";
  private static final String SHOW_NON_RECTANGULAR_CLIP_KEY = "show_non_rect_clip";
  private static final String SHOW_SCREEN_UPDATES_KEY = "show_screen_updates";
  private static final String SHOW_TOUCHES_KEY = "show_touches";
  private static final String SIMULATE_COLOR_SPACE = "simulate_color_space";
  private static final String STRICT_MODE_KEY = "strict_mode";
  private static final String TAG = "DevelopmentSettings";
  private static final String TERMINAL_APP_PACKAGE = "com.android.terminal";
  private static final String TRACK_FRAME_TIME_KEY = "track_frame_time";
  private static final String TRANSITION_ANIMATION_SCALE_KEY = "transition_animation_scale";
  private static final String TUNER_UI_KEY = "tuner_ui";
  private static final String USB_AUDIO_KEY = "usb_audio";
  private static final String USB_CONFIGURATION_KEY = "select_usb_configuration";
  private static final String VERIFY_APPS_OVER_USB_KEY = "verify_apps_over_usb";
  private static final String WAIT_FOR_DEBUGGER_KEY = "wait_for_debugger";
  private static final String WEBVIEW_MULTIPROCESS_KEY = "enable_webview_multiprocess";
  private static final String WEBVIEW_PROVIDER_KEY = "select_webview_provider";
  private static final String WIFI_AGGRESSIVE_HANDOVER_KEY = "wifi_aggressive_handover";
  private static final String WIFI_ALLOW_SCAN_WITH_TRAFFIC_KEY = "wifi_allow_scan_with_traffic";
  private static final String WIFI_DISPLAY_CERTIFICATION_KEY = "wifi_display_certification";
  private static final String WIFI_VERBOSE_LOGGING_KEY = "wifi_verbose_logging";
  private static final String WINDOW_ANIMATION_SCALE_KEY = "window_animation_scale";
  private static boolean isSupportAptxHdSupport;
  private static boolean isSupportWirelessAdbDebugging;
  private Dialog mAdbDialog;
  private Dialog mAdbKeysDialog;
  private SwitchPreference mAdvancedReboot;
  private final ArrayList<Preference> mAllPrefs = new ArrayList();
  private ListPreference mAnimatorDurationScale;
  private ListPreference mAppProcessLimit;
  private SwitchPreference mAptxHdSupportSwitchPreference;
  private IBackupManager mBackupManager;
  private SwitchPreference mBluetoothDisableAbsVolume;
  private SwitchPreference mBtHciSnoopLog;
  private Preference mBugreport;
  private SwitchPreference mBugreportInPower;
  private Preference mClearAdbKeys;
  private ColorModePreference mColorModePreference;
  private SwitchPreference mColorTemperaturePreference;
  private String mDebugApp;
  private Preference mDebugAppPref;
  private ListPreference mDebugHwOverdraw;
  private SwitchPreference mDebugLayout;
  private SwitchPreference mDebugViewAttributes;
  private boolean mDialogClicked;
  private SwitchPreference mDisableDozeSwitch;
  private SwitchPreference mDisableOverlays;
  private final HashSet<Preference> mDisabledPrefs = new HashSet();
  private boolean mDontPokeProperties;
  private DevicePolicyManager mDpm;
  private SwitchPreference mEnableAdb;
  private Dialog mEnableDialog;
  private OPRestrictedSwitchPreference mEnableOemUnlock;
  private SwitchPreference mEnableTerminal;
  private SwitchPreference mForceAllowOnExternal;
  private SwitchPreference mForceHardwareUi;
  private SwitchPreference mForceMsaa;
  private SwitchPreference mForceResizable;
  private SwitchPreference mForceRtlLayout;
  private boolean mHaveDebugSettings;
  private SwitchPreference mImmediatelyDestroyActivities;
  private boolean mIsUnlocked;
  private OPRestrictedSwitchPreference mKeepScreenOn;
  private boolean mLastEnabledState;
  private ListPreference mLogdSize;
  private ListPreference mLogpersist;
  private Dialog mLogpersistClearDialog;
  private boolean mLogpersistCleared;
  private Preference mLogsPreference;
  private SwitchPreference mMobileDataAlwaysOn;
  private String mMockLocationApp;
  private Preference mMockLocationAppPref;
  private PersistentDataBlockManager mOemUnlockManager;
  private SwitchPreference mOtaDisableAutomaticUpdate;
  private ListPreference mOverlayDisplayDevices;
  private PreferenceScreen mPassword;
  private SwitchPreference mPointerLocation;
  private final ArrayList<SwitchPreference> mResetSwitchPrefs = new ArrayList();
  private SwitchPreference mShowAllANRs;
  private SwitchPreference mShowCpuUsage;
  private SwitchPreference mShowHwLayersUpdates;
  private SwitchPreference mShowHwScreenUpdates;
  private ListPreference mShowNonRectClip;
  private SwitchPreference mShowScreenUpdates;
  private SwitchPreference mShowTouches;
  private ListPreference mSimulateColorSpace;
  private SwitchPreference mStrictMode;
  private SwitchBar mSwitchBar;
  private TelephonyManager mTelephonyManager;
  private ListPreference mTrackFrameTime;
  private ListPreference mTransitionAnimationScale;
  private SwitchPreference mUSBAudio;
  private UserManager mUm;
  private boolean mUnavailable;
  private ListPreference mUsbConfiguration;
  private BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      DevelopmentSettings.-set0(DevelopmentSettings.this, paramAnonymousIntent.getBooleanExtra("unlocked", false));
      DevelopmentSettings.-wrap2(DevelopmentSettings.this);
    }
  };
  private SwitchPreference mVerboseMultiBroadcastSwitch;
  private SwitchPreference mVerifyAppsOverUsb;
  private SwitchPreference mWaitForDebugger;
  private SwitchPreference mWebViewMultiprocess;
  private ListPreference mWebViewProvider;
  private IWebViewUpdateService mWebViewUpdateService;
  private SwitchPreference mWifiAggressiveHandover;
  private SwitchPreference mWifiAllowScansWithTraffic;
  private SwitchPreference mWifiDisplayCertification;
  private WifiManager mWifiManager;
  private SwitchPreference mWifiVerboseLogging;
  private ListPreference mWindowAnimationScale;
  private IWindowManager mWindowManager;
  private SwitchPreference mWirelessAdbDebuggingSwitch;
  
  public DevelopmentSettings()
  {
    super("no_debugging_features");
  }
  
  private ListPreference addListPreference(String paramString)
  {
    paramString = (ListPreference)findPreference(paramString);
    this.mAllPrefs.add(paramString);
    paramString.setOnPreferenceChangeListener(this);
    return paramString;
  }
  
  private void confirmEnableOemUnlock()
  {
    DialogInterface.OnClickListener local3 = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          Utils.setOemUnlockEnabled(DevelopmentSettings.this.getActivity(), true);
        }
      }
    };
    DialogInterface.OnDismissListener local4 = new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        if (DevelopmentSettings.this.getActivity() == null) {
          return;
        }
        DevelopmentSettings.-wrap1(DevelopmentSettings.this);
      }
    };
    new AlertDialog.Builder(getActivity()).setTitle(2131689645).setMessage(2131689646).setPositiveButton(2131692097, local3).setNegativeButton(17039360, null).setOnDismissListener(local4).create().show();
  }
  
  private static int currentStrictModeActiveIndex()
  {
    if (TextUtils.isEmpty(SystemProperties.get("persist.sys.strictmode.visual"))) {
      return 0;
    }
    if (SystemProperties.getBoolean("persist.sys.strictmode.visual", false)) {
      return 1;
    }
    return 2;
  }
  
  private String defaultLogdSizeValue()
  {
    String str2 = SystemProperties.get("ro.logd.size");
    String str1;
    if (str2 != null)
    {
      str1 = str2;
      if (str2.length() != 0) {}
    }
    else
    {
      if (!SystemProperties.get("ro.config.low_ram").equals("true")) {
        break label39;
      }
      str1 = "65536";
    }
    return str1;
    label39:
    return "262144";
  }
  
  private void disableForUser(Preference paramPreference)
  {
    if (paramPreference != null)
    {
      paramPreference.setEnabled(false);
      this.mDisabledPrefs.add(paramPreference);
    }
  }
  
  private void dismissDialogs()
  {
    if (this.mAdbDialog != null)
    {
      this.mAdbDialog.dismiss();
      this.mAdbDialog = null;
    }
    if (this.mAdbKeysDialog != null)
    {
      this.mAdbKeysDialog.dismiss();
      this.mAdbKeysDialog = null;
    }
    if (this.mEnableDialog != null)
    {
      this.mEnableDialog.dismiss();
      this.mEnableDialog = null;
    }
    if (this.mLogpersistClearDialog != null)
    {
      this.mLogpersistClearDialog.dismiss();
      this.mLogpersistClearDialog = null;
    }
  }
  
  private boolean enableOemUnlockPreference()
  {
    if (!isBootloaderUnlocked()) {
      return isOemUnlockAllowed();
    }
    return false;
  }
  
  private boolean enableVerifierSetting()
  {
    Object localObject = getActivity().getContentResolver();
    if (Settings.Global.getInt((ContentResolver)localObject, "adb_enabled", 0) == 0) {
      return false;
    }
    if (Settings.Global.getInt((ContentResolver)localObject, "package_verifier_enable", 1) == 0) {
      return false;
    }
    localObject = getActivity().getPackageManager();
    Intent localIntent = new Intent("android.intent.action.PACKAGE_NEEDS_VERIFICATION");
    localIntent.setType("application/vnd.android.package-archive");
    localIntent.addFlags(1);
    return ((PackageManager)localObject).queryBroadcastReceivers(localIntent, 0).size() != 0;
  }
  
  private SwitchPreference findAndInitSwitchPref(String paramString)
  {
    SwitchPreference localSwitchPreference = (SwitchPreference)findPreference(paramString);
    if (localSwitchPreference == null) {
      throw new IllegalArgumentException("Cannot find preference with key = " + paramString);
    }
    this.mAllPrefs.add(localSwitchPreference);
    this.mResetSwitchPrefs.add(localSwitchPreference);
    return localSwitchPreference;
  }
  
  private boolean isBootloaderUnlocked()
  {
    boolean bool = false;
    int i = -1;
    if (this.mOemUnlockManager != null) {
      i = this.mOemUnlockManager.getFlashLockState();
    }
    if (i == 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isOemUnlockAllowed()
  {
    UserHandle localUserHandle = UserHandle.of(UserHandle.myUserId());
    return (!this.mUm.hasBaseUserRestriction("no_oem_unlock", localUserHandle)) && (!this.mUm.hasBaseUserRestriction("no_factory_reset", localUserHandle));
  }
  
  private static boolean isPackageInstalled(Context paramContext, String paramString)
  {
    boolean bool = false;
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo(paramString, 0);
      if (paramContext != null) {
        bool = true;
      }
      return bool;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  private boolean isSimLockedDevice()
  {
    int j = this.mTelephonyManager.getPhoneCount();
    int i = 0;
    while (i < j)
    {
      if (this.mTelephonyManager.getAllowedCarriers(i).size() > 0) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void removePreference(Preference paramPreference)
  {
    getPreferenceScreen().removePreference(paramPreference);
    this.mAllPrefs.remove(paramPreference);
    this.mResetSwitchPrefs.remove(paramPreference);
  }
  
  private boolean removePreferenceForProduction(Preference paramPreference)
  {
    if ("user".equals(Build.TYPE))
    {
      removePreference(paramPreference);
      return true;
    }
    return false;
  }
  
  private void resetAdvancedRebootOptions()
  {
    Settings.Secure.putInt(getActivity().getContentResolver(), "advanced_reboot", 0);
  }
  
  private void resetDangerousOptions()
  {
    this.mDontPokeProperties = true;
    int i = 0;
    if (i < this.mResetSwitchPrefs.size())
    {
      SwitchPreference localSwitchPreference = (SwitchPreference)this.mResetSwitchPrefs.get(i);
      if (this.mDisableDozeSwitch == localSwitchPreference) {}
      for (;;)
      {
        i += 1;
        break;
        if (localSwitchPreference.isChecked())
        {
          localSwitchPreference.setChecked(false);
          onPreferenceTreeClick(localSwitchPreference);
        }
      }
    }
    resetDebuggerOptions();
    writeLogpersistOption(null, true);
    writeLogdSizeOption(null);
    resetAdvancedRebootOptions();
    writeAnimationScaleOption(0, this.mWindowAnimationScale, null);
    writeAnimationScaleOption(1, this.mTransitionAnimationScale, null);
    writeAnimationScaleOption(2, this.mAnimatorDurationScale, null);
    if (usingDevelopmentColorSpace()) {
      writeSimulateColorSpace(Integer.valueOf(-1));
    }
    writeOverlayDisplayDevicesOptions(null);
    writeAppProcessLimitOptions(null);
    this.mHaveDebugSettings = false;
    updateAllOptions();
    this.mDontPokeProperties = false;
    pokeSystemProperties();
  }
  
  private static void resetDebuggerOptions()
  {
    try
    {
      ActivityManagerNative.getDefault().setDebugApp(null, false, true);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void resetShortcutManagerThrottling()
  {
    IShortcutService localIShortcutService = IShortcutService.Stub.asInterface(ServiceManager.getService("shortcut"));
    if (localIShortcutService != null) {}
    try
    {
      localIShortcutService.resetThrottling();
      Toast.makeText(getActivity(), 2131693705, 0).show();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("DevelopmentSettings", "Failed to reset rate limiting", localRemoteException);
    }
  }
  
  private void setBugreportStorageProviderStatus()
  {
    ComponentName localComponentName = new ComponentName("com.android.shell", "com.android.shell.BugreportStorageProvider");
    boolean bool = this.mBugreportInPower.isChecked();
    PackageManager localPackageManager = getPackageManager();
    if (bool) {}
    for (int i = 1;; i = 0)
    {
      localPackageManager.setComponentEnabledSetting(localComponentName, i, 0);
      return;
    }
  }
  
  private void setLogpersistOff(boolean paramBoolean)
  {
    SystemProperties.set("persist.logd.logpersistd.buffer", "");
    SystemProperties.set("logd.logpersistd.buffer", "");
    SystemProperties.set("persist.logd.logpersistd", "");
    if (paramBoolean) {}
    for (String str = "";; str = "stop")
    {
      SystemProperties.set("logd.logpersistd", str);
      pokeSystemProperties();
      if (!paramBoolean) {
        break;
      }
      updateLogpersistValues();
      return;
    }
    int i = 0;
    while (i < 3)
    {
      str = SystemProperties.get("logd.logpersistd");
      if ((str == null) || (str.equals(""))) {
        break;
      }
      try
      {
        Thread.sleep(100L);
        i += 1;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;) {}
      }
    }
  }
  
  private void setPrefsEnabledState(boolean paramBoolean)
  {
    int i = 0;
    if (i < this.mAllPrefs.size())
    {
      Preference localPreference = (Preference)this.mAllPrefs.get(i);
      if ((!paramBoolean) || (this.mDisabledPrefs.contains(localPreference))) {}
      for (boolean bool = false;; bool = true)
      {
        localPreference.setEnabled(bool);
        i += 1;
        break;
      }
    }
    updateAllOptions();
  }
  
  private static boolean showEnableOemUnlockPreference()
  {
    return !SystemProperties.get("ro.frp.pst").equals("");
  }
  
  private boolean showKeyguardConfirmation(Resources paramResources, int paramInt)
  {
    return new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(paramInt, paramResources.getString(2131689643));
  }
  
  private boolean showVerifierSetting()
  {
    return Settings.Global.getInt(getActivity().getContentResolver(), "verifier_setting_visible", 1) > 0;
  }
  
  private void startBackgroundCheckFragment()
  {
    ((SettingsActivity)getActivity()).startPreferencePanel(BackgroundCheckSummary.class.getName(), null, 2131693493, null, null, 0);
  }
  
  private void startInactiveAppsFragment()
  {
    ((SettingsActivity)getActivity()).startPreferencePanel(InactiveApps.class.getName(), null, 2131689749, null, null, 0);
  }
  
  private void updateAdvancedRebootOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mAdvancedReboot;
    if (Settings.Secure.getInt(getActivity().getContentResolver(), "advanced_reboot", 0) != 0) {
      bool = true;
    }
    localSwitchPreference.setChecked(bool);
  }
  
  private void updateAllOptions()
  {
    boolean bool2 = true;
    Object localObject = getActivity();
    ContentResolver localContentResolver = ((Context)localObject).getContentResolver();
    this.mHaveDebugSettings = false;
    SwitchPreference localSwitchPreference = this.mEnableAdb;
    if (Settings.Global.getInt(localContentResolver, "adb_enabled", 0) != 0)
    {
      bool1 = true;
      updateSwitchPreference(localSwitchPreference, bool1);
      if (this.mEnableTerminal != null)
      {
        localSwitchPreference = this.mEnableTerminal;
        if (((Context)localObject).getPackageManager().getApplicationEnabledSetting("com.android.terminal") != 1) {
          break label425;
        }
        bool1 = true;
        label75:
        updateSwitchPreference(localSwitchPreference, bool1);
      }
      localObject = this.mBugreportInPower;
      if (Settings.Secure.getInt(localContentResolver, "bugreport_in_power_menu", 0) == 0) {
        break label430;
      }
      bool1 = true;
      label101:
      updateSwitchPreference((SwitchPreference)localObject, bool1);
      localObject = this.mKeepScreenOn;
      if (Settings.Global.getInt(localContentResolver, "stay_on_while_plugged_in", 0) == 0) {
        break label435;
      }
      bool1 = true;
      label127:
      updateSwitchPreference((SwitchPreference)localObject, bool1);
      localObject = this.mBtHciSnoopLog;
      if (Settings.Secure.getInt(localContentResolver, "bluetooth_hci_log", 0) == 0) {
        break label440;
      }
      bool1 = true;
      label153:
      updateSwitchPreference((SwitchPreference)localObject, bool1);
      if (this.mEnableOemUnlock != null) {
        updateSwitchPreference(this.mEnableOemUnlock, Utils.isOemUnlockEnabled(getActivity()));
      }
      localObject = this.mDebugViewAttributes;
      if (Settings.Global.getInt(localContentResolver, "debug_view_attributes", 0) == 0) {
        break label445;
      }
      bool1 = true;
      label200:
      updateSwitchPreference((SwitchPreference)localObject, bool1);
      localObject = this.mForceAllowOnExternal;
      if (Settings.Global.getInt(localContentResolver, "force_allow_on_external", 0) == 0) {
        break label450;
      }
    }
    label425:
    label430:
    label435:
    label440:
    label445:
    label450:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      updateSwitchPreference((SwitchPreference)localObject, bool1);
      updateHdcpValues();
      updatePasswordSummary();
      updateDebuggerOptions();
      updateMockLocation();
      updateStrictModeVisualOptions();
      updatePointerLocationOptions();
      updateShowTouchesOptions();
      updateFlingerOptions();
      updateHardwareUiOptions();
      updateMsaaOptions();
      updateTrackFrameTimeOptions();
      updateShowNonRectClipOptions();
      updateShowHwScreenUpdatesOptions();
      updateShowHwLayersUpdatesOptions();
      updateDebugHwOverdrawOptions();
      updateDebugLayoutOptions();
      updateAnimationScaleOptions();
      updateOverlayDisplayDevicesOptions();
      updateImmediatelyDestroyActivitiesOptions();
      updateAppProcessLimitOptions();
      updateShowAllANRsOptions();
      updateVerifyAppsOverUsbOptions();
      updateOtaDisableAutomaticUpdateOptions();
      updateBugreportOptions();
      updateForceRtlOptions();
      updateLogdSizeValues();
      updateLogpersistValues();
      updateWifiDisplayCertificationOptions();
      updateWifiVerboseLoggingOptions();
      updateWifiAggressiveHandoverOptions();
      updateWifiAllowScansWithTrafficOptions();
      updateMobileDataAlwaysOnOptions();
      updateSimulateColorSpace();
      updateUSBAudioOptions();
      updateForceResizableOptions();
      updateWebViewMultiprocessOptions();
      updateWebViewProviderOptions();
      updateOemUnlockOptions();
      if (this.mColorTemperaturePreference != null) {
        updateColorTemperature();
      }
      updateBluetoothDisableAbsVolumeOptions();
      updateAdvancedRebootOptions();
      updateDisableDozeOptions();
      updateWirelessAdbDebuging();
      updateVerboseMultiBroadcast();
      updateAptxHdSupportSwitch();
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label75;
      bool1 = false;
      break label101;
      bool1 = false;
      break label127;
      bool1 = false;
      break label153;
      bool1 = false;
      break label200;
    }
  }
  
  private void updateAnimationScaleOptions()
  {
    updateAnimationScaleValue(0, this.mWindowAnimationScale);
    updateAnimationScaleValue(1, this.mTransitionAnimationScale);
    updateAnimationScaleValue(2, this.mAnimatorDurationScale);
  }
  
  private void updateAnimationScaleValue(int paramInt, ListPreference paramListPreference)
  {
    for (;;)
    {
      try
      {
        float f = this.mWindowManager.getAnimationScale(paramInt);
        if (f != 1.0F) {
          this.mHaveDebugSettings = true;
        }
        CharSequence[] arrayOfCharSequence = paramListPreference.getEntryValues();
        paramInt = 0;
        if (paramInt < arrayOfCharSequence.length)
        {
          if (f <= Float.parseFloat(arrayOfCharSequence[paramInt].toString()))
          {
            paramListPreference.setValueIndex(paramInt);
            paramListPreference.setSummary(paramListPreference.getEntries()[paramInt]);
          }
        }
        else
        {
          paramListPreference.setValueIndex(arrayOfCharSequence.length - 1);
          paramListPreference.setSummary(paramListPreference.getEntries()[0]);
          return;
        }
      }
      catch (RemoteException paramListPreference)
      {
        return;
      }
      paramInt += 1;
    }
  }
  
  private void updateAppProcessLimitOptions()
  {
    for (;;)
    {
      int i;
      try
      {
        int j = ActivityManagerNative.getDefault().getProcessLimit();
        CharSequence[] arrayOfCharSequence = this.mAppProcessLimit.getEntryValues();
        i = 0;
        if (i < arrayOfCharSequence.length)
        {
          if (Integer.parseInt(arrayOfCharSequence[i].toString()) >= j)
          {
            if (i != 0) {
              this.mHaveDebugSettings = true;
            }
            this.mAppProcessLimit.setValueIndex(i);
            this.mAppProcessLimit.setSummary(this.mAppProcessLimit.getEntries()[i]);
          }
        }
        else
        {
          this.mAppProcessLimit.setValueIndex(0);
          this.mAppProcessLimit.setSummary(this.mAppProcessLimit.getEntries()[0]);
          return;
        }
      }
      catch (RemoteException localRemoteException)
      {
        return;
      }
      i += 1;
    }
  }
  
  private void updateAptxHdSupportSwitch()
  {
    if (Settings.Secure.getInt(getActivity().getContentResolver(), "bluetooth_aptx_hd", 1) == 1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mAptxHdSupportSwitchPreference.setChecked(bool);
      return;
    }
  }
  
  private void updateBluetoothDisableAbsVolumeOptions()
  {
    updateSwitchPreference(this.mBluetoothDisableAbsVolume, SystemProperties.getBoolean("persist.bluetooth.disableabsvol", false));
  }
  
  private void updateBugreportOptions()
  {
    this.mBugreport.setEnabled(true);
    this.mBugreportInPower.setEnabled(true);
    setBugreportStorageProviderStatus();
  }
  
  private void updateColorTemperature()
  {
    updateSwitchPreference(this.mColorTemperaturePreference, SystemProperties.getBoolean("persist.sys.debug.color_temp", false));
  }
  
  private void updateCpuUsageOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mShowCpuUsage;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "show_processes", 0) != 0) {
      bool = true;
    }
    updateSwitchPreference(localSwitchPreference, bool);
  }
  
  private void updateDebugHwOverdrawOptions()
  {
    Object localObject2 = SystemProperties.get("debug.hwui.overdraw");
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = "";
    }
    localObject2 = this.mDebugHwOverdraw.getEntryValues();
    int i = 0;
    while (i < localObject2.length)
    {
      if (((String)localObject1).contentEquals(localObject2[i]))
      {
        this.mDebugHwOverdraw.setValueIndex(i);
        this.mDebugHwOverdraw.setSummary(this.mDebugHwOverdraw.getEntries()[i]);
        return;
      }
      i += 1;
    }
    this.mDebugHwOverdraw.setValueIndex(0);
    this.mDebugHwOverdraw.setSummary(this.mDebugHwOverdraw.getEntries()[0]);
  }
  
  private void updateDebugLayoutOptions()
  {
    updateSwitchPreference(this.mDebugLayout, SystemProperties.getBoolean("debug.layout", false));
  }
  
  private void updateDebuggerOptions()
  {
    this.mDebugApp = Settings.Global.getString(getActivity().getContentResolver(), "debug_app");
    Object localObject = this.mWaitForDebugger;
    boolean bool;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "wait_for_debugger", 0) != 0)
    {
      bool = true;
      updateSwitchPreference((SwitchPreference)localObject, bool);
      if ((this.mDebugApp == null) || (this.mDebugApp.length() <= 0)) {}
    }
    else
    {
      for (;;)
      {
        try
        {
          localObject = getActivity().getPackageManager().getApplicationInfo(this.mDebugApp, 512);
          localObject = getActivity().getPackageManager().getApplicationLabel((ApplicationInfo)localObject);
          if (localObject == null) {
            continue;
          }
          localObject = ((CharSequence)localObject).toString();
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          String str = this.mDebugApp;
          continue;
        }
        this.mDebugAppPref.setSummary(getResources().getString(2131689688, new Object[] { localObject }));
        this.mWaitForDebugger.setEnabled(true);
        this.mHaveDebugSettings = true;
        return;
        bool = false;
        break;
        localObject = this.mDebugApp;
      }
    }
    this.mDebugAppPref.setSummary(getResources().getString(2131689687));
    this.mWaitForDebugger.setEnabled(false);
  }
  
  private void updateDisableDozeOptions()
  {
    boolean bool = getContext().getResources().getBoolean(17956884);
    ContentResolver localContentResolver = getActivity().getContentResolver();
    int i;
    if (bool)
    {
      i = 1;
      if (Settings.System.getInt(localContentResolver, "doze_mode_enabaled", i) != 1) {
        break label64;
      }
    }
    label64:
    for (bool = true;; bool = false)
    {
      this.mDisableDozeSwitch.setChecked(bool);
      this.mDisableDozeSwitch.setEnabled(true);
      return;
      i = 0;
      break;
    }
  }
  
  private void updateFlingerOptions()
  {
    boolean bool2 = true;
    try
    {
      Object localObject = ServiceManager.getService("SurfaceFlinger");
      Parcel localParcel1;
      Parcel localParcel2;
      if (localObject != null)
      {
        localParcel1 = Parcel.obtain();
        localParcel2 = Parcel.obtain();
        localParcel1.writeInterfaceToken("android.ui.ISurfaceComposer");
        ((IBinder)localObject).transact(1010, localParcel1, localParcel2, 0);
        localParcel2.readInt();
        localParcel2.readInt();
        int i = localParcel2.readInt();
        localObject = this.mShowScreenUpdates;
        if (i == 0) {
          break label128;
        }
        bool1 = true;
        updateSwitchPreference((SwitchPreference)localObject, bool1);
        localParcel2.readInt();
        i = localParcel2.readInt();
        localObject = this.mDisableOverlays;
        if (i == 0) {
          break label133;
        }
      }
      label128:
      label133:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        updateSwitchPreference((SwitchPreference)localObject, bool1);
        localParcel2.recycle();
        localParcel1.recycle();
        return;
        bool1 = false;
        break;
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void updateForceResizableOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mForceResizable;
    if (Settings.Global.getInt(getContentResolver(), "force_resizable_activities", 0) != 0) {
      bool = true;
    }
    updateSwitchPreference(localSwitchPreference, bool);
  }
  
  private void updateForceRtlOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mForceRtlLayout;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "debug.force_rtl", 0) != 0) {
      bool = true;
    }
    updateSwitchPreference(localSwitchPreference, bool);
  }
  
  private void updateHardwareUiOptions()
  {
    updateSwitchPreference(this.mForceHardwareUi, SystemProperties.getBoolean("persist.sys.ui.hw", false));
  }
  
  private void updateHdcpValues()
  {
    ListPreference localListPreference = (ListPreference)findPreference("hdcp_checking");
    String str;
    String[] arrayOfString1;
    String[] arrayOfString2;
    int k;
    int i;
    if (localListPreference != null)
    {
      str = SystemProperties.get("persist.sys.hdcp_checking");
      arrayOfString1 = getResources().getStringArray(2131427331);
      arrayOfString2 = getResources().getStringArray(2131427332);
      k = 1;
      i = 0;
    }
    for (;;)
    {
      int j = k;
      if (i < arrayOfString1.length)
      {
        if (str.equals(arrayOfString1[i])) {
          j = i;
        }
      }
      else
      {
        localListPreference.setValue(arrayOfString1[j]);
        localListPreference.setSummary(arrayOfString2[j]);
        localListPreference.setOnPreferenceChangeListener(this);
        return;
      }
      i += 1;
    }
  }
  
  private void updateImmediatelyDestroyActivitiesOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mImmediatelyDestroyActivities;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "always_finish_activities", 0) != 0) {
      bool = true;
    }
    updateSwitchPreference(localSwitchPreference, bool);
  }
  
  private void updateLogdSizeValues()
  {
    Object localObject3;
    Object localObject2;
    Object localObject1;
    int i;
    String[] arrayOfString;
    int j;
    if (this.mLogdSize != null)
    {
      localObject3 = SystemProperties.get("persist.log.tag");
      localObject2 = SystemProperties.get("persist.logd.size");
      localObject1 = localObject2;
      if (localObject3 != null)
      {
        localObject1 = localObject2;
        if (((String)localObject3).startsWith("Settings")) {
          localObject1 = "32768";
        }
      }
      if (this.mLogpersist != null)
      {
        localObject2 = SystemProperties.get("logd.logpersistd.enable");
        if ((localObject2 != null) && (((String)localObject2).equals("true")) && (!((String)localObject1).equals("32768"))) {
          break label270;
        }
        writeLogpersistOption(null, true);
        this.mLogpersist.setEnabled(false);
      }
      if (localObject1 != null)
      {
        localObject2 = localObject1;
        if (((String)localObject1).length() != 0) {}
      }
      else
      {
        localObject2 = defaultLogdSizeValue();
      }
      localObject3 = getResources().getStringArray(2131427335);
      localObject1 = getResources().getStringArray(2131427333);
      i = 2;
      if (SystemProperties.get("ro.config.low_ram").equals("true"))
      {
        this.mLogdSize.setEntries(2131427334);
        localObject1 = getResources().getStringArray(2131427334);
        i = 1;
      }
      arrayOfString = getResources().getStringArray(2131427336);
      j = 0;
    }
    for (;;)
    {
      int k = i;
      if (j < localObject1.length)
      {
        if ((((String)localObject2).equals(localObject3[j])) || (((String)localObject2).equals(localObject1[j]))) {
          k = j;
        }
      }
      else
      {
        this.mLogdSize.setValue(localObject3[k]);
        this.mLogdSize.setSummary(arrayOfString[k]);
        this.mLogdSize.setOnPreferenceChangeListener(this);
        return;
        label270:
        if (!this.mLastEnabledState) {
          break;
        }
        this.mLogpersist.setEnabled(true);
        break;
      }
      j += 1;
    }
  }
  
  private void updateLogpersistValues()
  {
    if (this.mLogpersist == null) {
      return;
    }
    Object localObject2 = SystemProperties.get("logd.logpersistd");
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = "";
    }
    String str = SystemProperties.get("logd.logpersistd.buffer");
    if (str != null)
    {
      localObject2 = str;
      if (str.length() != 0) {}
    }
    else
    {
      localObject2 = "all";
    }
    int i = 0;
    int j;
    if (((String)localObject1).equals("logcatd"))
    {
      j = 1;
      if (((String)localObject2).equals("kernel")) {
        i = 3;
      }
    }
    else
    {
      this.mLogpersist.setValue(getResources().getStringArray(2131427337)[i]);
      this.mLogpersist.setSummary(getResources().getStringArray(2131427339)[i]);
      this.mLogpersist.setOnPreferenceChangeListener(this);
      if (i == 0) {
        break label278;
      }
      this.mLogpersistCleared = false;
    }
    label278:
    while (this.mLogpersistCleared)
    {
      return;
      i = j;
      if (((String)localObject2).equals("all")) {
        break;
      }
      i = j;
      if (((String)localObject2).contains("radio")) {
        break;
      }
      i = j;
      if (!((String)localObject2).contains("security")) {
        break;
      }
      i = j;
      if (!((String)localObject2).contains("kernel")) {
        break;
      }
      int k = 2;
      i = k;
      if (((String)localObject2).contains("default")) {
        break;
      }
      localObject1 = new String[4];
      localObject1[0] = "main";
      localObject1[1] = "events";
      localObject1[2] = "system";
      localObject1[3] = "crash";
      j = 0;
      for (;;)
      {
        i = k;
        if (j >= localObject1.length) {
          break;
        }
        if (!((String)localObject2).contains(localObject1[j]))
        {
          i = 1;
          break;
        }
        j += 1;
      }
    }
    SystemProperties.set("logd.logpersistd", "clear");
    pokeSystemProperties();
    this.mLogpersistCleared = true;
  }
  
  private void updateMobileDataAlwaysOnOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mMobileDataAlwaysOn;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "mobile_data_always_on", 0) != 0) {
      bool = true;
    }
    updateSwitchPreference(localSwitchPreference, bool);
  }
  
  private void updateMockLocation()
  {
    Object localObject1 = ((AppOpsManager)getSystemService("appops")).getPackagesForOps(MOCK_LOCATION_APP_OPS);
    if (localObject1 != null)
    {
      localObject3 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject3).hasNext()) {
        if (((AppOpsManager.OpEntry)((AppOpsManager.PackageOps)((Iterator)localObject3).next()).getOps().get(0)).getMode() == 0) {
          this.mMockLocationApp = ((AppOpsManager.PackageOps)((List)localObject1).get(0)).getPackageName();
        }
      }
    }
    if (!TextUtils.isEmpty(this.mMockLocationApp)) {
      localObject3 = this.mMockLocationApp;
    }
    try
    {
      localObject1 = getActivity().getPackageManager().getApplicationInfo(this.mMockLocationApp, 512);
      CharSequence localCharSequence = getPackageManager().getApplicationLabel((ApplicationInfo)localObject1);
      localObject1 = localObject3;
      if (localCharSequence != null) {
        localObject1 = localCharSequence.toString();
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        Object localObject2 = localObject3;
      }
    }
    this.mMockLocationAppPref.setSummary(getString(2131689649, new Object[] { localObject1 }));
    this.mHaveDebugSettings = true;
    return;
    this.mMockLocationAppPref.setSummary(getString(2131689648));
  }
  
  private void updateMsaaOptions()
  {
    updateSwitchPreference(this.mForceMsaa, SystemProperties.getBoolean("debug.egl.force_msaa", false));
  }
  
  private void updateOemUnlockOptions()
  {
    if (this.mEnableOemUnlock != null)
    {
      updateSwitchPreference(this.mEnableOemUnlock, Utils.isOemUnlockEnabled(getActivity()));
      updateOemUnlockSettingDescription();
      this.mEnableOemUnlock.setDisabledByAdmin(null);
      this.mEnableOemUnlock.setEnabled(enableOemUnlockPreference());
      if (this.mEnableOemUnlock.isEnabled()) {
        this.mEnableOemUnlock.checkRestrictionAndSetDisabled("no_factory_reset");
      }
      if (this.mEnableOemUnlock.isEnabled()) {
        this.mEnableOemUnlock.checkRestrictionAndSetDisabled("no_oem_unlock");
      }
    }
  }
  
  private void updateOemUnlockSettingDescription()
  {
    int i;
    if (this.mEnableOemUnlock != null)
    {
      i = 2131689644;
      if (!isBootloaderUnlocked()) {
        break label35;
      }
      i = 2131693851;
    }
    for (;;)
    {
      this.mEnableOemUnlock.setSummary(getString(i));
      return;
      label35:
      if (isSimLockedDevice()) {
        i = 2131693854;
      } else if (!isOemUnlockAllowed()) {
        i = 2131693853;
      }
    }
  }
  
  private void updateOtaDisableAutomaticUpdateOptions()
  {
    boolean bool = true;
    SwitchPreference localSwitchPreference = this.mOtaDisableAutomaticUpdate;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "ota_disable_automatic_update", 0) != 1) {}
    for (;;)
    {
      updateSwitchPreference(localSwitchPreference, bool);
      return;
      bool = false;
    }
  }
  
  private void updateOverlayDisplayDevicesOptions()
  {
    Object localObject2 = Settings.Global.getString(getActivity().getContentResolver(), "overlay_display_devices");
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = "";
    }
    localObject2 = this.mOverlayDisplayDevices.getEntryValues();
    int i = 0;
    while (i < localObject2.length)
    {
      if (((String)localObject1).contentEquals(localObject2[i]))
      {
        this.mOverlayDisplayDevices.setValueIndex(i);
        this.mOverlayDisplayDevices.setSummary(this.mOverlayDisplayDevices.getEntries()[i]);
        return;
      }
      i += 1;
    }
    this.mOverlayDisplayDevices.setValueIndex(0);
    this.mOverlayDisplayDevices.setSummary(this.mOverlayDisplayDevices.getEntries()[0]);
  }
  
  private void updatePasswordSummary()
  {
    try
    {
      if (this.mBackupManager.hasBackupPassword())
      {
        this.mPassword.setSummary(2131689745);
        return;
      }
      this.mPassword.setSummary(2131689744);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void updatePointerLocationOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mPointerLocation;
    if (Settings.System.getInt(getActivity().getContentResolver(), "pointer_location", 0) != 0) {
      bool = true;
    }
    updateSwitchPreference(localSwitchPreference, bool);
  }
  
  private void updateShowAllANRsOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mShowAllANRs;
    if (Settings.Secure.getInt(getActivity().getContentResolver(), "anr_show_background", 0) != 0) {
      bool = true;
    }
    updateSwitchPreference(localSwitchPreference, bool);
  }
  
  private void updateShowHwLayersUpdatesOptions()
  {
    updateSwitchPreference(this.mShowHwLayersUpdates, SystemProperties.getBoolean("debug.hwui.show_layers_updates", false));
  }
  
  private void updateShowHwScreenUpdatesOptions()
  {
    updateSwitchPreference(this.mShowHwScreenUpdates, SystemProperties.getBoolean("debug.hwui.show_dirty_regions", false));
  }
  
  private void updateShowNonRectClipOptions()
  {
    Object localObject2 = SystemProperties.get("debug.hwui.show_non_rect_clip");
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = "hide";
    }
    localObject2 = this.mShowNonRectClip.getEntryValues();
    int i = 0;
    while (i < localObject2.length)
    {
      if (((String)localObject1).contentEquals(localObject2[i]))
      {
        this.mShowNonRectClip.setValueIndex(i);
        this.mShowNonRectClip.setSummary(this.mShowNonRectClip.getEntries()[i]);
        return;
      }
      i += 1;
    }
    this.mShowNonRectClip.setValueIndex(0);
    this.mShowNonRectClip.setSummary(this.mShowNonRectClip.getEntries()[0]);
  }
  
  private void updateShowTouchesOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mShowTouches;
    if (Settings.System.getInt(getActivity().getContentResolver(), "show_touches", 0) != 0) {
      bool = true;
    }
    updateSwitchPreference(localSwitchPreference, bool);
  }
  
  private void updateSimulateColorSpace()
  {
    Object localObject = getContentResolver();
    int i;
    if (Settings.Secure.getInt((ContentResolver)localObject, "accessibility_display_daltonizer_enabled", 0) != 0) {
      i = 1;
    }
    while (i != 0)
    {
      localObject = Integer.toString(Settings.Secure.getInt((ContentResolver)localObject, "accessibility_display_daltonizer", -1));
      this.mSimulateColorSpace.setValue((String)localObject);
      if (this.mSimulateColorSpace.findIndexOfValue((String)localObject) < 0)
      {
        this.mSimulateColorSpace.setSummary(getString(2131689774, new Object[] { getString(2131689772) }));
        return;
        i = 0;
      }
      else
      {
        this.mSimulateColorSpace.setSummary("%s");
        return;
      }
    }
    this.mSimulateColorSpace.setValue(Integer.toString(-1));
  }
  
  private void updateStrictModeVisualOptions()
  {
    boolean bool = true;
    SwitchPreference localSwitchPreference = this.mStrictMode;
    if (currentStrictModeActiveIndex() == 1) {}
    for (;;)
    {
      updateSwitchPreference(localSwitchPreference, bool);
      return;
      bool = false;
    }
  }
  
  private void updateTrackFrameTimeOptions()
  {
    Object localObject2 = SystemProperties.get("debug.hwui.profile");
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = "";
    }
    localObject2 = this.mTrackFrameTime.getEntryValues();
    int i = 0;
    while (i < localObject2.length)
    {
      if (((String)localObject1).contentEquals(localObject2[i]))
      {
        this.mTrackFrameTime.setValueIndex(i);
        this.mTrackFrameTime.setSummary(this.mTrackFrameTime.getEntries()[i]);
        return;
      }
      i += 1;
    }
    this.mTrackFrameTime.setValueIndex(0);
    this.mTrackFrameTime.setSummary(this.mTrackFrameTime.getEntries()[0]);
  }
  
  private void updateUSBAudioOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mUSBAudio;
    if (Settings.Secure.getInt(getContentResolver(), "usb_audio_automatic_routing_disabled", 0) != 0) {
      bool = true;
    }
    updateSwitchPreference(localSwitchPreference, bool);
  }
  
  private void updateUsbConfigurationValues()
  {
    UsbManager localUsbManager;
    String[] arrayOfString1;
    String[] arrayOfString2;
    int k;
    int i;
    if (this.mUsbConfiguration != null)
    {
      localUsbManager = (UsbManager)getSystemService("usb");
      arrayOfString1 = getResources().getStringArray(2131427359);
      arrayOfString2 = getResources().getStringArray(2131427358);
      k = 0;
      i = 0;
    }
    for (;;)
    {
      int j = k;
      if (i < arrayOfString2.length)
      {
        if ((localUsbManager.isFunctionEnabled(arrayOfString1[i])) && (this.mIsUnlocked))
        {
          Log.d("DevelopmentSettings", "Set enable funtion: " + arrayOfString1[i]);
          j = i;
        }
      }
      else
      {
        this.mUsbConfiguration.setValue(arrayOfString1[j]);
        this.mUsbConfiguration.setSummary(arrayOfString2[j]);
        this.mUsbConfiguration.setOnPreferenceChangeListener(this);
        return;
      }
      i += 1;
    }
  }
  
  private void updateVerboseMultiBroadcast()
  {
    boolean bool = true;
    SwitchPreference localSwitchPreference = this.mVerboseMultiBroadcastSwitch;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "op_enable_wifi_multi_broadcast", 1) == 1) {}
    for (;;)
    {
      localSwitchPreference.setChecked(bool);
      return;
      bool = false;
    }
  }
  
  private void updateVerifyAppsOverUsbOptions()
  {
    boolean bool = true;
    SwitchPreference localSwitchPreference = this.mVerifyAppsOverUsb;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "verifier_verify_adb_installs", 1) != 0) {}
    for (;;)
    {
      updateSwitchPreference(localSwitchPreference, bool);
      this.mVerifyAppsOverUsb.setEnabled(enableVerifierSetting());
      return;
      bool = false;
    }
  }
  
  private void updateWebViewMultiprocessOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mWebViewMultiprocess;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "webview_multiprocess", 0) != 0) {
      bool = true;
    }
    updateSwitchPreference(localSwitchPreference, bool);
  }
  
  private void updateWebViewProviderOptions()
  {
    for (;;)
    {
      Object localObject1;
      ArrayList localArrayList;
      int i;
      try
      {
        localObject1 = this.mWebViewUpdateService.getValidWebViewPackages();
        if (localObject1 == null)
        {
          Log.e("DevelopmentSettings", "No WebView providers available");
          return;
        }
        Object localObject2 = new ArrayList();
        localArrayList = new ArrayList();
        i = 0;
        if (i < localObject1.length)
        {
          if (!Utils.isPackageEnabled(getActivity(), localObject1[i].packageName)) {
            break label202;
          }
          ((ArrayList)localObject2).add(localObject1[i].description);
          localArrayList.add(localObject1[i].packageName);
          break label202;
        }
        this.mWebViewProvider.setEntries((CharSequence[])((ArrayList)localObject2).toArray(new String[((ArrayList)localObject2).size()]));
        this.mWebViewProvider.setEntryValues((CharSequence[])localArrayList.toArray(new String[localArrayList.size()]));
        localObject2 = this.mWebViewUpdateService.getCurrentWebViewPackageName();
        localObject1 = localObject2;
        if (localObject2 != null) {
          break label209;
        }
        localObject1 = "";
      }
      catch (RemoteException localRemoteException) {}
      if (i < localArrayList.size())
      {
        if (((String)localObject1).contentEquals((CharSequence)localArrayList.get(i)))
        {
          this.mWebViewProvider.setValueIndex(i);
          return;
        }
        i += 1;
      }
      else
      {
        return;
        label202:
        i += 1;
        continue;
        label209:
        i = 0;
      }
    }
  }
  
  private void updateWifiAggressiveHandoverOptions()
  {
    if (this.mWifiManager.getAggressiveHandover() > 0) {}
    for (boolean bool = true;; bool = false)
    {
      updateSwitchPreference(this.mWifiAggressiveHandover, bool);
      return;
    }
  }
  
  private void updateWifiAllowScansWithTrafficOptions()
  {
    if (this.mWifiManager.getAllowScansWithTraffic() > 0) {}
    for (boolean bool = true;; bool = false)
    {
      updateSwitchPreference(this.mWifiAllowScansWithTraffic, bool);
      return;
    }
  }
  
  private void updateWifiDisplayCertificationOptions()
  {
    boolean bool = false;
    SwitchPreference localSwitchPreference = this.mWifiDisplayCertification;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "wifi_display_certification_on", 0) != 0) {
      bool = true;
    }
    updateSwitchPreference(localSwitchPreference, bool);
  }
  
  private void updateWifiVerboseLoggingOptions()
  {
    if (this.mWifiManager.getVerboseLoggingLevel() > 0) {}
    for (boolean bool = true;; bool = false)
    {
      updateSwitchPreference(this.mWifiVerboseLogging, bool);
      return;
    }
  }
  
  private void updateWirelessAdbDebuging()
  {
    boolean bool = "5555".equals(SystemProperties.get("service.adb.tcp.port"));
    this.mWirelessAdbDebuggingSwitch.setChecked(bool);
  }
  
  private boolean usingDevelopmentColorSpace()
  {
    Object localObject = getContentResolver();
    if (Settings.Secure.getInt((ContentResolver)localObject, "accessibility_display_daltonizer_enabled", 0) != 0) {}
    for (int i = 1; i != 0; i = 0)
    {
      localObject = Integer.toString(Settings.Secure.getInt((ContentResolver)localObject, "accessibility_display_daltonizer", -1));
      if (this.mSimulateColorSpace.findIndexOfValue((String)localObject) < 0) {
        break;
      }
      return true;
    }
    return false;
  }
  
  private void writeAdvancedRebootOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mAdvancedReboot.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "advanced_reboot", i);
      return;
    }
  }
  
  private void writeAnimationScaleOption(int paramInt, ListPreference paramListPreference, Object paramObject)
  {
    if (paramObject != null) {}
    for (;;)
    {
      try
      {
        f = Float.parseFloat(paramObject.toString());
        this.mWindowManager.setAnimationScale(paramInt, f);
        updateAnimationScaleValue(paramInt, paramListPreference);
        return;
      }
      catch (RemoteException paramListPreference) {}
      float f = 1.0F;
    }
  }
  
  private void writeAppProcessLimitOptions(Object paramObject)
  {
    if (paramObject != null) {}
    for (;;)
    {
      try
      {
        i = Integer.parseInt(paramObject.toString());
        ActivityManagerNative.getDefault().setProcessLimit(i);
        updateAppProcessLimitOptions();
        return;
      }
      catch (RemoteException paramObject) {}
      int i = -1;
    }
  }
  
  private void writeAptxHdSupportSwitch()
  {
    boolean bool = this.mAptxHdSupportSwitchPreference.isChecked();
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (bool) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "bluetooth_aptx_hd", i);
      return;
    }
  }
  
  private void writeBluetoothDisableAbsVolumeOptions()
  {
    if (this.mBluetoothDisableAbsVolume.isChecked()) {}
    for (String str = "true";; str = "false")
    {
      SystemProperties.set("persist.bluetooth.disableabsvol", str);
      return;
    }
  }
  
  private void writeBtHciSnoopLogOptions()
  {
    BluetoothAdapter.getDefaultAdapter().configHciSnoopLog(this.mBtHciSnoopLog.isChecked());
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mBtHciSnoopLog.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "bluetooth_hci_log", i);
      return;
    }
  }
  
  private void writeColorTemperature()
  {
    if (this.mColorTemperaturePreference.isChecked()) {}
    for (String str = "1";; str = "0")
    {
      SystemProperties.set("persist.sys.debug.color_temp", str);
      pokeSystemProperties();
      Toast.makeText(getActivity(), 2131693623, 1).show();
      return;
    }
  }
  
  private void writeCpuUsageOptions()
  {
    boolean bool = this.mShowCpuUsage.isChecked();
    Object localObject = getActivity().getContentResolver();
    if (bool) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt((ContentResolver)localObject, "show_processes", i);
      localObject = new Intent().setClassName("com.android.systemui", "com.android.systemui.LoadAverageService");
      if (!bool) {
        break;
      }
      getActivity().startService((Intent)localObject);
      return;
    }
    getActivity().stopService((Intent)localObject);
  }
  
  private void writeDebugHwOverdrawOptions(Object paramObject)
  {
    if (paramObject == null) {}
    for (paramObject = "";; paramObject = paramObject.toString())
    {
      SystemProperties.set("debug.hwui.overdraw", (String)paramObject);
      pokeSystemProperties();
      updateDebugHwOverdrawOptions();
      return;
    }
  }
  
  private void writeDebugLayoutOptions()
  {
    if (this.mDebugLayout.isChecked()) {}
    for (String str = "true";; str = "false")
    {
      SystemProperties.set("debug.layout", str);
      pokeSystemProperties();
      return;
    }
  }
  
  private void writeDebuggerOptions()
  {
    try
    {
      ActivityManagerNative.getDefault().setDebugApp(this.mDebugApp, this.mWaitForDebugger.isChecked(), true);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void writeDisableDozeOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mDisableDozeSwitch.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.System.putInt(localContentResolver, "doze_mode_enabaled", i);
      return;
    }
  }
  
  private void writeDisableOverlaysOption()
  {
    try
    {
      IBinder localIBinder = ServiceManager.getService("SurfaceFlinger");
      Parcel localParcel;
      if (localIBinder != null)
      {
        localParcel = Parcel.obtain();
        localParcel.writeInterfaceToken("android.ui.ISurfaceComposer");
        if (!this.mDisableOverlays.isChecked()) {
          break label61;
        }
      }
      label61:
      for (int i = 1;; i = 0)
      {
        localParcel.writeInt(i);
        localIBinder.transact(1008, localParcel, null, 0);
        localParcel.recycle();
        updateFlingerOptions();
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void writeForceResizableOptions()
  {
    ContentResolver localContentResolver = getContentResolver();
    if (this.mForceResizable.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "force_resizable_activities", i);
      return;
    }
  }
  
  private void writeForceRtlOptions()
  {
    boolean bool = this.mForceRtlLayout.isChecked();
    Object localObject = getActivity().getContentResolver();
    int i;
    if (bool)
    {
      i = 1;
      Settings.Global.putInt((ContentResolver)localObject, "debug.force_rtl", i);
      if (!bool) {
        break label68;
      }
    }
    label68:
    for (localObject = "1";; localObject = "0")
    {
      SystemProperties.set("debug.force_rtl", (String)localObject);
      LocalePicker.updateLocale(getActivity().getResources().getConfiguration().locale);
      return;
      i = 0;
      break;
    }
  }
  
  private void writeHardwareUiOptions()
  {
    if (this.mForceHardwareUi.isChecked()) {}
    for (String str = "true";; str = "false")
    {
      SystemProperties.set("persist.sys.ui.hw", str);
      pokeSystemProperties();
      return;
    }
  }
  
  private void writeImmediatelyDestroyActivitiesOptions()
  {
    try
    {
      ActivityManagerNative.getDefault().setAlwaysFinish(this.mImmediatelyDestroyActivities.isChecked());
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void writeLogdSizeOption(Object paramObject)
  {
    boolean bool;
    String str1;
    Object localObject;
    if (paramObject != null)
    {
      bool = paramObject.toString().equals("32768");
      str1 = SystemProperties.get("persist.log.tag");
      localObject = str1;
      if (str1 == null) {
        localObject = "";
      }
      str1 = ((String)localObject).replaceAll(",+Settings", "").replaceFirst("^Settings,*", "").replaceAll(",+", ",").replaceFirst(",+$", "");
      String str2 = str1;
      if (bool)
      {
        str2 = "65536";
        paramObject = SystemProperties.get("persist.log.tag.snet_event_log");
        if ((paramObject == null) || (((String)paramObject).length() == 0))
        {
          paramObject = SystemProperties.get("log.tag.snet_event_log");
          if ((paramObject == null) || (((String)paramObject).length() == 0)) {
            SystemProperties.set("persist.log.tag.snet_event_log", "I");
          }
        }
        paramObject = str1;
        if (str1.length() != 0) {
          paramObject = "," + str1;
        }
        str1 = "Settings" + (String)paramObject;
        paramObject = str2;
        str2 = str1;
      }
      if (!str2.equals(localObject)) {
        SystemProperties.set("persist.log.tag", str2);
      }
      str1 = defaultLogdSizeValue();
      if ((paramObject == null) || (paramObject.toString().length() == 0)) {
        break label272;
      }
    }
    label272:
    for (paramObject = paramObject.toString();; paramObject = str1)
    {
      localObject = paramObject;
      if (str1.equals(paramObject)) {
        localObject = "";
      }
      SystemProperties.set("persist.logd.size", (String)localObject);
      SystemProperties.set("ctl.start", "logd-reinit");
      pokeSystemProperties();
      updateLogdSizeValues();
      return;
      bool = false;
      break;
    }
  }
  
  private void writeLogpersistOption(Object paramObject, boolean paramBoolean)
  {
    if (this.mLogpersist == null) {
      return;
    }
    String str = SystemProperties.get("persist.log.tag");
    Object localObject = paramObject;
    boolean bool = paramBoolean;
    if (str != null)
    {
      localObject = paramObject;
      bool = paramBoolean;
      if (str.startsWith("Settings"))
      {
        localObject = null;
        bool = true;
      }
    }
    if ((localObject == null) || (localObject.toString().equals("")))
    {
      if (bool) {
        this.mLogpersistCleared = false;
      }
      do
      {
        do
        {
          setLogpersistOff(true);
          return;
        } while (this.mLogpersistCleared);
        paramObject = SystemProperties.get("logd.logpersistd");
      } while ((paramObject == null) || (!((String)paramObject).equals("logcatd")));
      if (this.mLogpersistClearDialog != null) {
        dismissDialogs();
      }
      this.mLogpersistClearDialog = new AlertDialog.Builder(getActivity()).setMessage(getActivity().getResources().getString(2131689664)).setTitle(2131689663).setPositiveButton(17039379, this).setNegativeButton(17039369, this).show();
      this.mLogpersistClearDialog.setOnDismissListener(this);
      return;
    }
    paramObject = SystemProperties.get("logd.logpersistd.buffer");
    int i;
    if ((paramObject == null) || (((String)paramObject).equals(localObject.toString())))
    {
      SystemProperties.set("persist.logd.logpersistd.buffer", localObject.toString());
      SystemProperties.set("persist.logd.logpersistd", "logcatd");
      pokeSystemProperties();
      i = 0;
    }
    for (;;)
    {
      if (i < 3)
      {
        paramObject = SystemProperties.get("logd.logpersistd");
        if ((paramObject == null) || (!((String)paramObject).equals("logcatd"))) {}
      }
      else
      {
        updateLogpersistValues();
        return;
        setLogpersistOff(false);
        break;
      }
      try
      {
        Thread.sleep(100L);
        i += 1;
      }
      catch (InterruptedException paramObject)
      {
        for (;;) {}
      }
    }
  }
  
  private void writeMobileDataAlwaysOnOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mMobileDataAlwaysOn.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "mobile_data_always_on", i);
      return;
    }
  }
  
  private void writeMockLocation()
  {
    AppOpsManager localAppOpsManager = (AppOpsManager)getSystemService("appops");
    Object localObject1 = localAppOpsManager.getPackagesForOps(MOCK_LOCATION_APP_OPS);
    if (localObject1 != null)
    {
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = (AppOpsManager.PackageOps)((Iterator)localObject1).next();
        if (((AppOpsManager.OpEntry)((AppOpsManager.PackageOps)localObject2).getOps().get(0)).getMode() != 2)
        {
          localObject2 = ((AppOpsManager.PackageOps)localObject2).getPackageName();
          try
          {
            localAppOpsManager.setMode(58, getActivity().getPackageManager().getApplicationInfo((String)localObject2, 512).uid, (String)localObject2, 2);
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException2) {}
        }
      }
    }
    if (!TextUtils.isEmpty(this.mMockLocationApp)) {}
    try
    {
      localAppOpsManager.setMode(58, getActivity().getPackageManager().getApplicationInfo(this.mMockLocationApp, 512).uid, this.mMockLocationApp, 0);
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException1) {}
  }
  
  private void writeMsaaOptions()
  {
    if (this.mForceMsaa.isChecked()) {}
    for (String str = "true";; str = "false")
    {
      SystemProperties.set("debug.egl.force_msaa", str);
      pokeSystemProperties();
      return;
    }
  }
  
  private void writeOtaDisableAutomaticUpdateOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mOtaDisableAutomaticUpdate.isChecked()) {}
    for (int i = 0;; i = 1)
    {
      Settings.Global.putInt(localContentResolver, "ota_disable_automatic_update", i);
      return;
    }
  }
  
  private void writeOverlayDisplayDevicesOptions(Object paramObject)
  {
    Settings.Global.putString(getActivity().getContentResolver(), "overlay_display_devices", (String)paramObject);
    updateOverlayDisplayDevicesOptions();
  }
  
  private void writePointerLocationOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mPointerLocation.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.System.putInt(localContentResolver, "pointer_location", i);
      return;
    }
  }
  
  private void writeShowAllANRsOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mShowAllANRs.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "anr_show_background", i);
      return;
    }
  }
  
  private void writeShowHwLayersUpdatesOptions()
  {
    if (this.mShowHwLayersUpdates.isChecked()) {}
    for (String str = "true";; str = null)
    {
      SystemProperties.set("debug.hwui.show_layers_updates", str);
      pokeSystemProperties();
      return;
    }
  }
  
  private void writeShowHwScreenUpdatesOptions()
  {
    if (this.mShowHwScreenUpdates.isChecked()) {}
    for (String str = "true";; str = null)
    {
      SystemProperties.set("debug.hwui.show_dirty_regions", str);
      pokeSystemProperties();
      return;
    }
  }
  
  private void writeShowNonRectClipOptions(Object paramObject)
  {
    if (paramObject == null) {}
    for (paramObject = "";; paramObject = paramObject.toString())
    {
      SystemProperties.set("debug.hwui.show_non_rect_clip", (String)paramObject);
      pokeSystemProperties();
      updateShowNonRectClipOptions();
      return;
    }
  }
  
  private void writeShowTouchesOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mShowTouches.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.System.putInt(localContentResolver, "show_touches", i);
      return;
    }
  }
  
  private void writeShowUpdatesOption()
  {
    try
    {
      IBinder localIBinder = ServiceManager.getService("SurfaceFlinger");
      Parcel localParcel;
      if (localIBinder != null)
      {
        localParcel = Parcel.obtain();
        localParcel.writeInterfaceToken("android.ui.ISurfaceComposer");
        if (!this.mShowScreenUpdates.isChecked()) {
          break label61;
        }
      }
      label61:
      for (int i = 1;; i = 0)
      {
        localParcel.writeInt(i);
        localIBinder.transact(1002, localParcel, null, 0);
        localParcel.recycle();
        updateFlingerOptions();
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void writeSimulateColorSpace(Object paramObject)
  {
    ContentResolver localContentResolver = getContentResolver();
    int i = Integer.parseInt(paramObject.toString());
    if (i < 0)
    {
      Settings.Secure.putInt(localContentResolver, "accessibility_display_daltonizer_enabled", 0);
      return;
    }
    Settings.Secure.putInt(localContentResolver, "accessibility_display_daltonizer_enabled", 1);
    Settings.Secure.putInt(localContentResolver, "accessibility_display_daltonizer", i);
  }
  
  private void writeStrictModeVisualOptions()
  {
    try
    {
      IWindowManager localIWindowManager = this.mWindowManager;
      if (this.mStrictMode.isChecked()) {}
      for (String str = "1";; str = "")
      {
        localIWindowManager.setStrictModeVisualIndicatorPreference(str);
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void writeTrackFrameTimeOptions(Object paramObject)
  {
    if (paramObject == null) {}
    for (paramObject = "";; paramObject = paramObject.toString())
    {
      SystemProperties.set("debug.hwui.profile", (String)paramObject);
      pokeSystemProperties();
      updateTrackFrameTimeOptions();
      return;
    }
  }
  
  private void writeUSBAudioOptions()
  {
    ContentResolver localContentResolver = getContentResolver();
    if (this.mUSBAudio.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "usb_audio_automatic_routing_disabled", i);
      return;
    }
  }
  
  private void writeUsbConfigurationOption(Object paramObject)
  {
    UsbManager localUsbManager = (UsbManager)getActivity().getSystemService("usb");
    paramObject = paramObject.toString();
    localUsbManager.setCurrentFunction((String)paramObject);
    if (((String)paramObject).equals("none"))
    {
      localUsbManager.setUsbDataUnlocked(false);
      return;
    }
    localUsbManager.setUsbDataUnlocked(true);
  }
  
  private void writeVerboseMultiBroadcast()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mVerboseMultiBroadcastSwitch.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "op_enable_wifi_multi_broadcast", i);
      return;
    }
  }
  
  private void writeVerifyAppsOverUsbOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mVerifyAppsOverUsb.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "verifier_verify_adb_installs", i);
      return;
    }
  }
  
  private void writeWebViewMultiprocessOptions()
  {
    boolean bool = this.mWebViewMultiprocess.isChecked();
    Object localObject = getActivity().getContentResolver();
    if (bool) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt((ContentResolver)localObject, "webview_multiprocess", i);
      try
      {
        localObject = this.mWebViewUpdateService.getCurrentWebViewPackageName();
        ActivityManagerNative.getDefault().killPackageDependents((String)localObject, -1);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        return;
      }
      catch (NullPointerException localNullPointerException) {}
    }
  }
  
  private boolean writeWebViewProviderOptions(Object paramObject)
  {
    try
    {
      IWebViewUpdateService localIWebViewUpdateService = this.mWebViewUpdateService;
      if (paramObject == null) {}
      for (String str = "";; str = paramObject.toString())
      {
        str = localIWebViewUpdateService.changeProviderAndSetting(str);
        updateWebViewProviderOptions();
        if (paramObject == null) {
          break;
        }
        return paramObject.equals(str);
      }
      return false;
    }
    catch (RemoteException paramObject)
    {
      return false;
    }
  }
  
  private void writeWifiAggressiveHandoverOptions()
  {
    WifiManager localWifiManager = this.mWifiManager;
    if (this.mWifiAggressiveHandover.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      localWifiManager.enableAggressiveHandover(i);
      return;
    }
  }
  
  private void writeWifiAllowScansWithTrafficOptions()
  {
    WifiManager localWifiManager = this.mWifiManager;
    if (this.mWifiAllowScansWithTraffic.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      localWifiManager.setAllowScansWithTraffic(i);
      return;
    }
  }
  
  private void writeWifiDisplayCertificationOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mWifiDisplayCertification.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "wifi_display_certification_on", i);
      return;
    }
  }
  
  private void writeWifiVerboseLoggingOptions()
  {
    WifiManager localWifiManager = this.mWifiManager;
    if (this.mWifiVerboseLogging.isChecked()) {}
    for (int i = 1;; i = 0)
    {
      localWifiManager.enableVerboseLogging(i);
      return;
    }
  }
  
  private void writeWirelessAdbDebuging()
  {
    if (this.mWirelessAdbDebuggingSwitch.isChecked()) {}
    for (String str = "5555";; str = "-1")
    {
      SystemProperties.set("service.adb.tcp.port", str);
      return;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 39;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mSwitchBar = ((SettingsActivity)getActivity()).getSwitchBar();
    if (this.mUnavailable)
    {
      this.mSwitchBar.setEnabled(false);
      return;
    }
    this.mSwitchBar.addOnSwitchChangeListener(this);
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 1000) {
      if (paramInt2 == -1)
      {
        this.mDebugApp = paramIntent.getAction();
        writeDebuggerOptions();
        updateDebuggerOptions();
      }
    }
    do
    {
      do
      {
        return;
        if (paramInt1 != 1001) {
          break;
        }
      } while (paramInt2 != -1);
      this.mMockLocationApp = paramIntent.getAction();
      writeMockLocation();
      updateMockLocation();
      return;
      if (paramInt1 != 0) {
        break;
      }
    } while (paramInt2 != -1);
    if (this.mEnableOemUnlock.isChecked())
    {
      confirmEnableOemUnlock();
      return;
    }
    Utils.setOemUnlockEnabled(getActivity(), false);
    return;
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramDialogInterface == this.mAdbDialog) {
      if (paramInt == -1)
      {
        this.mDialogClicked = true;
        Settings.Global.putInt(getActivity().getContentResolver(), "adb_enabled", 1);
        this.mVerifyAppsOverUsb.setEnabled(true);
        updateVerifyAppsOverUsbOptions();
        updateBugreportOptions();
        if (SystemProperties.getBoolean("sys.debug.watchdog", false)) {
          this.mEnableAdb.setEnabled(false);
        }
      }
    }
    do
    {
      do
      {
        return;
        this.mEnableAdb.setChecked(false);
        return;
        if (paramDialogInterface != this.mAdbKeysDialog) {
          break;
        }
      } while (paramInt != -1);
      try
      {
        IUsbManager.Stub.asInterface(ServiceManager.getService("usb")).clearUsbDebuggingKeys();
        return;
      }
      catch (RemoteException paramDialogInterface)
      {
        Log.e("DevelopmentSettings", "Unable to clear adb keys", paramDialogInterface);
        return;
      }
      if (paramDialogInterface == this.mEnableDialog)
      {
        if (paramInt == -1)
        {
          this.mDialogClicked = true;
          Settings.Global.putInt(getActivity().getContentResolver(), "development_settings_enabled", 1);
          this.mLastEnabledState = true;
          setPrefsEnabledState(this.mLastEnabledState);
          return;
        }
        this.mSwitchBar.setChecked(false);
        return;
      }
    } while (paramDialogInterface != this.mLogpersistClearDialog);
    if (paramInt == -1)
    {
      setLogpersistOff(true);
      return;
    }
    updateLogpersistValues();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
    this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    this.mWebViewUpdateService = IWebViewUpdateService.Stub.asInterface(ServiceManager.getService("webviewupdate"));
    this.mOemUnlockManager = ((PersistentDataBlockManager)getActivity().getSystemService("persistent_data_block"));
    this.mTelephonyManager = ((TelephonyManager)getSystemService("phone"));
    this.mDpm = ((DevicePolicyManager)getActivity().getSystemService("device_policy"));
    this.mUm = ((UserManager)getSystemService("user"));
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    setIfOnlyAvailableForAdmins(true);
    label796:
    String[] arrayOfString1;
    String[] arrayOfString2;
    String[] arrayOfString3;
    int i;
    if ((!isUiRestricted()) && (Utils.isDeviceProvisioned(getActivity())))
    {
      addPreferencesFromResource(2131230756);
      paramBundle = (PreferenceGroup)findPreference("debug_debugging_category");
      this.mEnableAdb = findAndInitSwitchPref("enable_adb");
      this.mClearAdbKeys = findPreference("clear_adb_keys");
      if ((!SystemProperties.getBoolean("ro.adb.secure", false)) && (paramBundle != null)) {
        paramBundle.removePreference(this.mClearAdbKeys);
      }
      this.mAllPrefs.add(this.mClearAdbKeys);
      this.mEnableTerminal = findAndInitSwitchPref("enable_terminal");
      if (!isPackageInstalled(getActivity(), "com.android.terminal"))
      {
        paramBundle.removePreference(this.mEnableTerminal);
        this.mEnableTerminal = null;
      }
      this.mLogsPreference = findPreference("getlogs");
      this.mDisableDozeSwitch = findAndInitSwitchPref("disable_doze");
      this.mVerboseMultiBroadcastSwitch = findAndInitSwitchPref("op_wifi_verbose_multi_broadcast");
      this.mWirelessAdbDebuggingSwitch = findAndInitSwitchPref("op_wireless_adb_debugging");
      this.mBugreport = findPreference("bugreport");
      this.mBugreportInPower = findAndInitSwitchPref("bugreport_in_power");
      this.mKeepScreenOn = ((OPRestrictedSwitchPreference)findAndInitSwitchPref("keep_screen_on"));
      this.mBtHciSnoopLog = findAndInitSwitchPref("bt_hci_snoop_log");
      this.mEnableOemUnlock = ((OPRestrictedSwitchPreference)findAndInitSwitchPref("oem_unlock_enable"));
      if (!showEnableOemUnlockPreference())
      {
        removePreference(this.mEnableOemUnlock);
        this.mEnableOemUnlock = null;
      }
      this.mDebugViewAttributes = findAndInitSwitchPref("debug_view_attributes");
      this.mForceAllowOnExternal = findAndInitSwitchPref("force_allow_on_external");
      this.mPassword = ((PreferenceScreen)findPreference("local_backup_password"));
      this.mAllPrefs.add(this.mPassword);
      this.mAdvancedReboot = findAndInitSwitchPref("advanced_reboot");
      if (!this.mUm.isAdminUser())
      {
        disableForUser(this.mEnableAdb);
        disableForUser(this.mClearAdbKeys);
        disableForUser(this.mEnableTerminal);
        disableForUser(this.mPassword);
        disableForUser(this.mAdvancedReboot);
      }
      this.mDebugAppPref = findPreference("debug_app");
      this.mAllPrefs.add(this.mDebugAppPref);
      this.mWaitForDebugger = findAndInitSwitchPref("wait_for_debugger");
      this.mMockLocationAppPref = findPreference("mock_location_app");
      this.mAllPrefs.add(this.mMockLocationAppPref);
      this.mVerifyAppsOverUsb = findAndInitSwitchPref("verify_apps_over_usb");
      if (!showVerifierSetting())
      {
        if (paramBundle == null) {
          break label899;
        }
        paramBundle.removePreference(this.mVerifyAppsOverUsb);
      }
      this.mStrictMode = findAndInitSwitchPref("strict_mode");
      this.mPointerLocation = findAndInitSwitchPref("pointer_location");
      this.mShowTouches = findAndInitSwitchPref("show_touches");
      this.mShowScreenUpdates = findAndInitSwitchPref("show_screen_updates");
      this.mDisableOverlays = findAndInitSwitchPref("disable_overlays");
      this.mForceHardwareUi = findAndInitSwitchPref("force_hw_ui");
      this.mForceMsaa = findAndInitSwitchPref("force_msaa");
      this.mTrackFrameTime = addListPreference("track_frame_time");
      this.mShowNonRectClip = addListPreference("show_non_rect_clip");
      this.mShowHwScreenUpdates = findAndInitSwitchPref("show_hw_screen_udpates");
      this.mShowHwLayersUpdates = findAndInitSwitchPref("show_hw_layers_udpates");
      this.mDebugLayout = findAndInitSwitchPref("debug_layout");
      this.mForceRtlLayout = findAndInitSwitchPref("force_rtl_layout_all_locales");
      this.mDebugHwOverdraw = addListPreference("debug_hw_overdraw");
      this.mWifiDisplayCertification = findAndInitSwitchPref("wifi_display_certification");
      this.mWifiVerboseLogging = findAndInitSwitchPref("wifi_verbose_logging");
      this.mWifiAggressiveHandover = findAndInitSwitchPref("wifi_aggressive_handover");
      this.mWifiAllowScansWithTraffic = findAndInitSwitchPref("wifi_allow_scan_with_traffic");
      this.mMobileDataAlwaysOn = findAndInitSwitchPref("mobile_data_always_on");
      this.mLogdSize = addListPreference("select_logd_size");
      if (!"1".equals(SystemProperties.get("ro.debuggable", "0"))) {
        break label910;
      }
      this.mLogpersist = addListPreference("select_logpersist");
      this.mUsbConfiguration = addListPreference("select_usb_configuration");
      localObject = getResources().getStringArray(2131427358);
      arrayOfString1 = getResources().getStringArray(2131427359);
      arrayOfString2 = new String[5];
      arrayOfString3 = new String[5];
      i = 0;
      label844:
      if (i >= localObject.length) {
        break label986;
      }
      if (i >= 3) {
        break label959;
      }
      arrayOfString2[i] = localObject[i];
      arrayOfString3[i] = arrayOfString1[i];
    }
    for (;;)
    {
      i += 1;
      break label844;
      this.mUnavailable = true;
      setPreferenceScreen(new PreferenceScreen(getPrefContext(), null));
      return;
      label899:
      this.mVerifyAppsOverUsb.setEnabled(false);
      break;
      label910:
      this.mLogpersist = ((ListPreference)findPreference("select_logpersist"));
      if (this.mLogpersist != null)
      {
        this.mLogpersist.setEnabled(false);
        if (paramBundle != null) {
          paramBundle.removePreference(this.mLogpersist);
        }
      }
      this.mLogpersist = null;
      break label796;
      label959:
      if (i > 3)
      {
        arrayOfString2[(i - 1)] = localObject[i];
        arrayOfString3[(i - 1)] = arrayOfString1[i];
      }
    }
    label986:
    this.mUsbConfiguration.setEntries(arrayOfString2);
    this.mUsbConfiguration.setEntryValues(arrayOfString3);
    this.mWebViewProvider = addListPreference("select_webview_provider");
    this.mWebViewMultiprocess = findAndInitSwitchPref("enable_webview_multiprocess");
    this.mBluetoothDisableAbsVolume = findAndInitSwitchPref("bluetooth_disable_absolute_volume");
    this.mWindowAnimationScale = addListPreference("window_animation_scale");
    this.mTransitionAnimationScale = addListPreference("transition_animation_scale");
    this.mAnimatorDurationScale = addListPreference("animator_duration_scale");
    this.mOverlayDisplayDevices = addListPreference("overlay_display_devices");
    this.mSimulateColorSpace = addListPreference("simulate_color_space");
    this.mUSBAudio = findAndInitSwitchPref("usb_audio");
    this.mForceResizable = findAndInitSwitchPref("force_resizable_activities");
    this.mImmediatelyDestroyActivities = ((SwitchPreference)findPreference("immediately_destroy_activities"));
    this.mAllPrefs.add(this.mImmediatelyDestroyActivities);
    this.mResetSwitchPrefs.add(this.mImmediatelyDestroyActivities);
    this.mAppProcessLimit = addListPreference("app_process_limit");
    this.mShowAllANRs = ((SwitchPreference)findPreference("show_all_anrs"));
    this.mAllPrefs.add(this.mShowAllANRs);
    this.mResetSwitchPrefs.add(this.mShowAllANRs);
    Object localObject = findPreference("hdcp_checking");
    if (localObject != null)
    {
      this.mAllPrefs.add(localObject);
      removePreferenceForProduction((Preference)localObject);
    }
    localObject = (PreferenceScreen)findPreference("convert_to_file_encryption");
    try
    {
      if (!IMountService.Stub.asInterface(ServiceManager.getService("mount")).isConvertibleToFBE()) {
        removePreference("convert_to_file_encryption");
      }
      for (;;)
      {
        this.mOtaDisableAutomaticUpdate = findAndInitSwitchPref("ota_disable_automatic_update");
        removePreference("ota_disable_automatic_update");
        this.mColorModePreference = ((ColorModePreference)findPreference("color_mode"));
        removePreference("color_mode");
        this.mColorModePreference = null;
        updateWebViewProviderOptions();
        this.mColorTemperaturePreference = ((SwitchPreference)findPreference("color_temperature"));
        if (!getResources().getBoolean(2131558453)) {
          break;
        }
        this.mAllPrefs.add(this.mColorTemperaturePreference);
        this.mResetSwitchPrefs.add(this.mColorTemperaturePreference);
        this.mAptxHdSupportSwitchPreference = findAndInitSwitchPref("aptx_hd_support");
        isSupportWirelessAdbDebugging = SettingsBaseApplication.mApplication.getPackageManager().hasSystemFeature("oem.service.adb.tcp.port.support");
        if ((!isSupportWirelessAdbDebugging) && (paramBundle != null)) {
          paramBundle.removePreference(this.mWirelessAdbDebuggingSwitch);
        }
        paramBundle = (PreferenceGroup)findPreference("debug_networking_category");
        isSupportAptxHdSupport = SettingsBaseApplication.mApplication.getPackageManager().hasSystemFeature("oem.aptx.hd.support");
        if ((paramBundle != null) && (!isSupportAptxHdSupport)) {
          break label1493;
        }
        return;
        if ("file".equals(SystemProperties.get("ro.crypto.type", "none")))
        {
          ((PreferenceScreen)localObject).setEnabled(false);
          ((PreferenceScreen)localObject).setSummary(getResources().getString(2131689761));
        }
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        removePreference("convert_to_file_encryption");
        continue;
        removePreference("color_temperature");
        this.mColorTemperaturePreference = null;
      }
      label1493:
      paramBundle.removePreference(this.mAptxHdSupportSwitchPreference);
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.hardware.usb.action.USB_STATE");
    if (getActivity().registerReceiver(this.mUsbReceiver, localIntentFilter) == null) {
      updateUsbConfigurationValues();
    }
    return super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  public void onDestroy()
  {
    dismissDialogs();
    super.onDestroy();
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    if (this.mUnavailable) {
      return;
    }
    this.mSwitchBar.removeOnSwitchChangeListener(this);
    this.mSwitchBar.hide();
    getActivity().unregisterReceiver(this.mUsbReceiver);
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    if (paramDialogInterface == this.mAdbDialog)
    {
      if (!this.mDialogClicked) {
        this.mEnableAdb.setChecked(false);
      }
      this.mAdbDialog = null;
    }
    do
    {
      return;
      if (paramDialogInterface == this.mEnableDialog)
      {
        if (!this.mDialogClicked) {
          this.mSwitchBar.setChecked(false);
        }
        this.mEnableDialog = null;
        return;
      }
    } while (paramDialogInterface != this.mLogpersistClearDialog);
    this.mLogpersistClearDialog = null;
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.mColorModePreference != null) {
      this.mColorModePreference.stopListening();
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if ("hdcp_checking".equals(paramPreference.getKey()))
    {
      SystemProperties.set("persist.sys.hdcp_checking", paramObject.toString());
      updateHdcpValues();
      pokeSystemProperties();
      return true;
    }
    if (paramPreference == this.mWebViewProvider)
    {
      if (paramObject == null)
      {
        Log.e("DevelopmentSettings", "Tried to set a null WebView provider");
        return false;
      }
      if (writeWebViewProviderOptions(paramObject)) {
        return true;
      }
      Toast.makeText(getActivity(), 2131689758, 0).show();
      return false;
    }
    if (paramPreference == this.mLogdSize)
    {
      writeLogdSizeOption(paramObject);
      return true;
    }
    if (paramPreference == this.mLogpersist)
    {
      writeLogpersistOption(paramObject, false);
      return true;
    }
    if (paramPreference == this.mUsbConfiguration)
    {
      writeUsbConfigurationOption(paramObject);
      return true;
    }
    if (paramPreference == this.mWindowAnimationScale)
    {
      writeAnimationScaleOption(0, this.mWindowAnimationScale, paramObject);
      return true;
    }
    if (paramPreference == this.mTransitionAnimationScale)
    {
      writeAnimationScaleOption(1, this.mTransitionAnimationScale, paramObject);
      return true;
    }
    if (paramPreference == this.mAnimatorDurationScale)
    {
      writeAnimationScaleOption(2, this.mAnimatorDurationScale, paramObject);
      return true;
    }
    if (paramPreference == this.mOverlayDisplayDevices)
    {
      writeOverlayDisplayDevicesOptions(paramObject);
      return true;
    }
    if (paramPreference == this.mTrackFrameTime)
    {
      writeTrackFrameTimeOptions(paramObject);
      return true;
    }
    if (paramPreference == this.mDebugHwOverdraw)
    {
      writeDebugHwOverdrawOptions(paramObject);
      return true;
    }
    if (paramPreference == this.mShowNonRectClip)
    {
      writeShowNonRectClipOptions(paramObject);
      return true;
    }
    if (paramPreference == this.mAppProcessLimit)
    {
      writeAppProcessLimitOptions(paramObject);
      return true;
    }
    if (paramPreference == this.mSimulateColorSpace)
    {
      writeSimulateColorSpace(paramObject);
      return true;
    }
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    int j = 1;
    int k = 1;
    int m = 1;
    int i = 1;
    if (Utils.isMonkeyRunning()) {
      return false;
    }
    if (paramPreference.getKey().equals("getlogs")) {}
    try
    {
      Intent localIntent = new Intent("com.oem.oemlogkit.startlog");
      localIntent.setFlags(805306368);
      getActivity().startActivity(localIntent);
      if (paramPreference == this.mEnableAdb) {
        if (this.mEnableAdb.isChecked())
        {
          this.mDialogClicked = false;
          if (this.mAdbDialog != null) {
            dismissDialogs();
          }
          this.mAdbDialog = new AlertDialog.Builder(getActivity()).setMessage(getActivity().getResources().getString(2131689674)).setTitle(2131689673).setPositiveButton(17039379, this).setNegativeButton(17039369, this).show();
          this.mAdbDialog.setOnDismissListener(this);
        }
      }
      do
      {
        return false;
        Settings.Global.putInt(getActivity().getContentResolver(), "adb_enabled", 0);
        this.mVerifyAppsOverUsb.setEnabled(false);
        this.mVerifyAppsOverUsb.setChecked(false);
        updateBugreportOptions();
        return false;
        if (paramPreference == this.mClearAdbKeys)
        {
          if (this.mAdbKeysDialog != null) {
            dismissDialogs();
          }
          this.mAdbKeysDialog = new AlertDialog.Builder(getActivity()).setMessage(2131689675).setPositiveButton(17039370, this).setNegativeButton(17039360, null).show();
          return false;
        }
        if (paramPreference == this.mEnableTerminal)
        {
          paramPreference = getActivity().getPackageManager();
          if (this.mEnableTerminal.isChecked()) {}
          for (;;)
          {
            paramPreference.setApplicationEnabledSetting("com.android.terminal", i, 0);
            return false;
            i = 0;
          }
        }
        if (paramPreference == this.mBugreportInPower)
        {
          paramPreference = getActivity().getContentResolver();
          if (this.mBugreportInPower.isChecked()) {}
          for (i = j;; i = 0)
          {
            Settings.Secure.putInt(paramPreference, "bugreport_in_power_menu", i);
            setBugreportStorageProviderStatus();
            return false;
          }
        }
        if (paramPreference == this.mKeepScreenOn)
        {
          paramPreference = getActivity().getContentResolver();
          if (this.mKeepScreenOn.isChecked()) {}
          for (i = 3;; i = 0)
          {
            Settings.Global.putInt(paramPreference, "stay_on_while_plugged_in", i);
            return false;
          }
        }
        if (paramPreference == this.mBtHciSnoopLog)
        {
          writeBtHciSnoopLogOptions();
          return false;
        }
        if ((paramPreference != this.mEnableOemUnlock) || (!this.mEnableOemUnlock.isEnabled())) {
          break label458;
        }
        if (!this.mEnableOemUnlock.isChecked()) {
          break;
        }
      } while (showKeyguardConfirmation(getResources(), 0));
      confirmEnableOemUnlock();
      return false;
      Utils.setOemUnlockEnabled(getActivity(), false);
      return false;
      label458:
      if (paramPreference == this.mMockLocationAppPref)
      {
        paramPreference = new Intent(getActivity(), AppPicker.class);
        paramPreference.putExtra("com.android.settings.extra.REQUESTIING_PERMISSION", "android.permission.ACCESS_MOCK_LOCATION");
        startActivityForResult(paramPreference, 1001);
        return false;
      }
      if (paramPreference == this.mDebugViewAttributes)
      {
        paramPreference = getActivity().getContentResolver();
        if (this.mDebugViewAttributes.isChecked()) {}
        for (i = k;; i = 0)
        {
          Settings.Global.putInt(paramPreference, "debug_view_attributes", i);
          return false;
        }
      }
      if (paramPreference == this.mForceAllowOnExternal)
      {
        paramPreference = getActivity().getContentResolver();
        if (this.mForceAllowOnExternal.isChecked()) {}
        for (i = m;; i = 0)
        {
          Settings.Global.putInt(paramPreference, "force_allow_on_external", i);
          return false;
        }
      }
      if (paramPreference == this.mDebugAppPref)
      {
        paramPreference = new Intent(getActivity(), AppPicker.class);
        paramPreference.putExtra("com.android.settings.extra.DEBUGGABLE", true);
        startActivityForResult(paramPreference, 1000);
        return false;
      }
      if (paramPreference == this.mWaitForDebugger)
      {
        writeDebuggerOptions();
        return false;
      }
      if (paramPreference == this.mVerifyAppsOverUsb)
      {
        writeVerifyAppsOverUsbOptions();
        return false;
      }
      if (paramPreference == this.mOtaDisableAutomaticUpdate)
      {
        writeOtaDisableAutomaticUpdateOptions();
        return false;
      }
      if (paramPreference == this.mStrictMode)
      {
        writeStrictModeVisualOptions();
        return false;
      }
      if (paramPreference == this.mPointerLocation)
      {
        writePointerLocationOptions();
        return false;
      }
      if (paramPreference == this.mShowTouches)
      {
        writeShowTouchesOptions();
        return false;
      }
      if (paramPreference == this.mShowScreenUpdates)
      {
        writeShowUpdatesOption();
        return false;
      }
      if (paramPreference == this.mDisableOverlays)
      {
        writeDisableOverlaysOption();
        return false;
      }
      if (paramPreference == this.mShowCpuUsage)
      {
        writeCpuUsageOptions();
        return false;
      }
      if (paramPreference == this.mImmediatelyDestroyActivities)
      {
        writeImmediatelyDestroyActivitiesOptions();
        return false;
      }
      if (paramPreference == this.mShowAllANRs)
      {
        writeShowAllANRsOptions();
        return false;
      }
      if (paramPreference == this.mForceHardwareUi)
      {
        writeHardwareUiOptions();
        return false;
      }
      if (paramPreference == this.mForceMsaa)
      {
        writeMsaaOptions();
        return false;
      }
      if (paramPreference == this.mShowHwScreenUpdates)
      {
        writeShowHwScreenUpdatesOptions();
        return false;
      }
      if (paramPreference == this.mShowHwLayersUpdates)
      {
        writeShowHwLayersUpdatesOptions();
        return false;
      }
      if (paramPreference == this.mDebugLayout)
      {
        writeDebugLayoutOptions();
        return false;
      }
      if (paramPreference == this.mForceRtlLayout)
      {
        writeForceRtlOptions();
        return false;
      }
      if (paramPreference == this.mWifiDisplayCertification)
      {
        writeWifiDisplayCertificationOptions();
        return false;
      }
      if (paramPreference == this.mWifiVerboseLogging)
      {
        writeWifiVerboseLoggingOptions();
        return false;
      }
      if (paramPreference == this.mWifiAggressiveHandover)
      {
        writeWifiAggressiveHandoverOptions();
        return false;
      }
      if (paramPreference == this.mWifiAllowScansWithTraffic)
      {
        writeWifiAllowScansWithTrafficOptions();
        return false;
      }
      if (paramPreference == this.mMobileDataAlwaysOn)
      {
        writeMobileDataAlwaysOnOptions();
        return false;
      }
      if (paramPreference == this.mColorTemperaturePreference)
      {
        writeColorTemperature();
        return false;
      }
      if (paramPreference == this.mUSBAudio)
      {
        writeUSBAudioOptions();
        return false;
      }
      if (paramPreference == this.mForceResizable)
      {
        writeForceResizableOptions();
        return false;
      }
      if ("inactive_apps".equals(paramPreference.getKey()))
      {
        startInactiveAppsFragment();
        return false;
      }
      if (paramPreference == this.mAdvancedReboot)
      {
        writeAdvancedRebootOptions();
        return false;
      }
      if ("background_check".equals(paramPreference.getKey()))
      {
        startBackgroundCheckFragment();
        return false;
      }
      if (paramPreference == this.mBluetoothDisableAbsVolume)
      {
        writeBluetoothDisableAbsVolumeOptions();
        return false;
      }
      if (paramPreference == this.mWebViewMultiprocess)
      {
        writeWebViewMultiprocessOptions();
        return false;
      }
      if ("reset_shortcut_manager_throttling".equals(paramPreference.getKey()))
      {
        resetShortcutManagerThrottling();
        return false;
      }
      if (paramPreference == this.mDisableDozeSwitch)
      {
        writeDisableDozeOptions();
        return false;
      }
      if (paramPreference == this.mVerboseMultiBroadcastSwitch)
      {
        System.out.println("zhuyang--mVerboseMultiBroadcastSwitch");
        writeVerboseMultiBroadcast();
        return false;
      }
      if (paramPreference == this.mWirelessAdbDebuggingSwitch)
      {
        System.out.println("zhuyang--mWirelessAdbDebuggingSwitch");
        writeWirelessAdbDebuging();
        return false;
      }
      if (paramPreference == this.mAptxHdSupportSwitchPreference)
      {
        writeAptxHdSupportSwitch();
        return false;
      }
      return super.onPreferenceTreeClick(paramPreference);
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      for (;;) {}
    }
  }
  
  public void onResume()
  {
    boolean bool = false;
    super.onResume();
    if (this.mUnavailable)
    {
      if (!isUiRestrictedByOnlyAdmin()) {
        getEmptyTextView().setText(2131689630);
      }
      getPreferenceScreen().removeAll();
      return;
    }
    RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfMaximumTimeToLockIsSet(getActivity());
    this.mKeepScreenOn.setDisabledByAdmin(localEnforcedAdmin);
    if (localEnforcedAdmin == null)
    {
      this.mDisabledPrefs.remove(this.mKeepScreenOn);
      if (Settings.Global.getInt(getActivity().getContentResolver(), "development_settings_enabled", 0) != 0) {
        bool = true;
      }
      this.mLastEnabledState = bool;
      this.mSwitchBar.setChecked(this.mLastEnabledState);
      setPrefsEnabledState(this.mLastEnabledState);
      if ((this.mHaveDebugSettings) && (!this.mLastEnabledState)) {
        break label171;
      }
    }
    for (;;)
    {
      this.mSwitchBar.show();
      if (this.mColorModePreference != null)
      {
        this.mColorModePreference.startListening();
        this.mColorModePreference.updateCurrentAndSupported();
      }
      return;
      this.mDisabledPrefs.add(this.mKeepScreenOn);
      break;
      label171:
      Settings.Global.putInt(getActivity().getContentResolver(), "development_settings_enabled", 1);
      this.mLastEnabledState = true;
      this.mSwitchBar.setChecked(this.mLastEnabledState);
      setPrefsEnabledState(this.mLastEnabledState);
    }
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    if ((paramSwitch != this.mSwitchBar.getSwitch()) || (SystemProperties.getBoolean("sys.debug.watchdog", false))) {
      return;
    }
    if (paramBoolean != this.mLastEnabledState)
    {
      if (paramBoolean)
      {
        this.mDialogClicked = false;
        if (this.mEnableDialog != null) {
          dismissDialogs();
        }
        this.mEnableDialog = new AlertDialog.Builder(getActivity()).setMessage(getActivity().getResources().getString(2131689677)).setTitle(2131689676).setPositiveButton(17039379, this).setNegativeButton(17039369, this).show();
        this.mEnableDialog.setOnDismissListener(this);
      }
    }
    else {
      return;
    }
    resetDangerousOptions();
    Settings.Global.putInt(getActivity().getContentResolver(), "development_settings_enabled", 0);
    this.mLastEnabledState = paramBoolean;
    setPrefsEnabledState(this.mLastEnabledState);
  }
  
  void pokeSystemProperties()
  {
    if (!this.mDontPokeProperties) {
      new SystemPropPoker().execute(new Void[0]);
    }
  }
  
  void updateSwitchPreference(SwitchPreference paramSwitchPreference, boolean paramBoolean)
  {
    paramSwitchPreference.setChecked(paramBoolean);
    if ((paramBoolean) && (SystemProperties.getBoolean("sys.debug.watchdog", false))) {
      this.mEnableAdb.setEnabled(false);
    }
    this.mHaveDebugSettings |= paramBoolean;
  }
  
  public static class SystemPropPoker
    extends AsyncTask<Void, Void, Void>
  {
    protected Void doInBackground(Void... paramVarArgs)
    {
      int i = 0;
      paramVarArgs = ServiceManager.listServices();
      int j = paramVarArgs.length;
      for (;;)
      {
        if (i < j)
        {
          Void localVoid = paramVarArgs[i];
          IBinder localIBinder = ServiceManager.checkService(localVoid);
          Parcel localParcel;
          if (localIBinder != null) {
            localParcel = Parcel.obtain();
          }
          try
          {
            localIBinder.transact(1599295570, localParcel, null, 0);
            localParcel.recycle();
            i += 1;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              Log.i("DevelopmentSettings", "Someone wrote a bad service '" + localVoid + "' that doesn't like to be poked: " + localException);
            }
          }
          catch (RemoteException localRemoteException)
          {
            for (;;) {}
          }
        }
      }
      return null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\DevelopmentSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */