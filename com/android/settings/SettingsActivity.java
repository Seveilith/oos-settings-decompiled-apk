package com.android.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.BackStackEntry;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.PreferenceFragment.OnPreferenceStartFragmentCallback;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager.OnPreferenceTreeClickListener;
import android.telephony.CarrierConfigManager;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toolbar;
import com.android.internal.util.ArrayUtils;
import com.android.settings.accessibility.AccessibilitySettings;
import com.android.settings.accessibility.AccessibilitySettingsForSetupWizard;
import com.android.settings.accessibility.CaptionPropertiesFragment;
import com.android.settings.accessibility.ToggleDaltonizerPreferenceFragment;
import com.android.settings.accounts.AccountSettings;
import com.android.settings.accounts.AccountSyncSettings;
import com.android.settings.accounts.ChooseAccountActivity;
import com.android.settings.accounts.ManagedProfileSettings;
import com.android.settings.applications.AdvancedAppSettings;
import com.android.settings.applications.DrawOverlayDetails;
import com.android.settings.applications.InstalledAppDetails;
import com.android.settings.applications.ManageApplications;
import com.android.settings.applications.ManageAssist;
import com.android.settings.applications.NotificationApps;
import com.android.settings.applications.ProcessStatsSummary;
import com.android.settings.applications.ProcessStatsUi;
import com.android.settings.applications.UsageAccessDetails;
import com.android.settings.applications.VrListenerSettings;
import com.android.settings.applications.WriteSettingsDetails;
import com.android.settings.bluetooth.BluetoothSettings;
import com.android.settings.dashboard.DashboardSummary;
import com.android.settings.dashboard.SearchResultsSummary;
import com.android.settings.datausage.DataSaverSummary;
import com.android.settings.datausage.DataUsageList;
import com.android.settings.datausage.DataUsageMeteredSettings;
import com.android.settings.datausage.DataUsageSummary;
import com.android.settings.deviceinfo.ImeiInformation;
import com.android.settings.deviceinfo.PrivateVolumeForget;
import com.android.settings.deviceinfo.PrivateVolumeSettings;
import com.android.settings.deviceinfo.PublicVolumeSettings;
import com.android.settings.deviceinfo.SimStatus;
import com.android.settings.deviceinfo.Status;
import com.android.settings.deviceinfo.StorageSettings;
import com.android.settings.fuelgauge.BatterySaverSettings;
import com.android.settings.fuelgauge.PowerUsageDetail;
import com.android.settings.fuelgauge.PowerUsageSummary;
import com.android.settings.inputmethod.AvailableVirtualKeyboardFragment;
import com.android.settings.inputmethod.InputMethodAndLanguageSettings;
import com.android.settings.inputmethod.KeyboardLayoutPickerFragment;
import com.android.settings.inputmethod.KeyboardLayoutPickerFragment2;
import com.android.settings.inputmethod.PhysicalKeyboardFragment;
import com.android.settings.inputmethod.SpellCheckersSettings;
import com.android.settings.inputmethod.UserDictionaryList;
import com.android.settings.localepicker.LocaleListEditor;
import com.android.settings.location.LocationSettings;
import com.android.settings.nfc.AndroidBeam;
import com.android.settings.nfc.PaymentSettings;
import com.android.settings.notification.AppNotificationSettings;
import com.android.settings.notification.ConfigureNotificationSettings;
import com.android.settings.notification.NotificationAccessSettings;
import com.android.settings.notification.NotificationStation;
import com.android.settings.notification.OtherSoundSettings;
import com.android.settings.notification.SoundSettings;
import com.android.settings.notification.ZenAccessSettings;
import com.android.settings.notification.ZenModeAutomationSettings;
import com.android.settings.notification.ZenModeEventRuleSettings;
import com.android.settings.notification.ZenModePrioritySettings;
import com.android.settings.notification.ZenModeScheduleRuleSettings;
import com.android.settings.notification.ZenModeSettings;
import com.android.settings.notification.ZenModeVisualInterruptionSettings;
import com.android.settings.print.PrintJobSettingsFragment;
import com.android.settings.print.PrintSettingsFragment;
import com.android.settings.qstile.DevelopmentTiles;
import com.android.settings.search.DynamicIndexableContentMonitor;
import com.android.settings.search.Index;
import com.android.settings.sim.SimSettings;
import com.android.settings.tts.TextToSpeechSettings;
import com.android.settings.users.UserSettings;
import com.android.settings.vpn2.VpnSettings;
import com.android.settings.wfd.WifiDisplaySettings;
import com.android.settings.widget.SwitchBar;
import com.android.settings.wifi.AdvancedWifiSettings;
import com.android.settings.wifi.SavedAccessPointsWifiSettings;
import com.android.settings.wifi.WifiAPITest;
import com.android.settings.wifi.WifiInfo;
import com.android.settings.wifi.WifiSettings;
import com.android.settings.wifi.p2p.WifiP2pSettings;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.SettingsDrawerActivity;
import com.android.settingslib.drawer.Tile;
import com.oneplus.lib.design.widget.AppBarLayout;
import com.oneplus.lib.design.widget.CollapsingToolbarLayout;
import com.oneplus.lib.design.widget.CoordinatorLayout;
import com.oneplus.settings.OPApplicationsSettings;
import com.oneplus.settings.OPButtonsSettings;
import com.oneplus.settings.OPCredentialsManagementSettings;
import com.oneplus.settings.OPDefaultHomeSettings;
import com.oneplus.settings.OPDeviceName;
import com.oneplus.settings.OPFontStyleSettings;
import com.oneplus.settings.OPGestureSettings;
import com.oneplus.settings.OPStatusBarCustomizeSettings;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.backgroundoptimize.BgOptimizeApps;
import com.oneplus.settings.backgroundoptimize.funcswitch.BgOptimizeSwitch;
import com.oneplus.settings.better.OPAppLocker;
import com.oneplus.settings.better.OPGamingMode;
import com.oneplus.settings.better.OPNightMode;
import com.oneplus.settings.better.OPReadingMode;
import com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionApps;
import com.oneplus.settings.faceunlock.OPFaceUnlockSettings;
import com.oneplus.settings.notification.OPNotificationAndNotdisturb;
import com.oneplus.settings.notification.OPRingPattern;
import com.oneplus.settings.notification.OPSilentMode;
import com.oneplus.settings.opfinger.OPFingerPrintEditFragments;
import com.oneplus.settings.others.OPOthersSettings;
import com.oneplus.settings.product.OPPreInstalledAppList;
import com.oneplus.settings.product.OPProductInfoSettings;
import com.oneplus.settings.quickpay.QuickPaySettings;
import com.oneplus.settings.storage.OPStorageSettings;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SettingsActivity
  extends SettingsDrawerActivity
  implements PreferenceManager.OnPreferenceTreeClickListener, PreferenceFragment.OnPreferenceStartFragmentCallback, ButtonBarHandler, FragmentManager.OnBackStackChangedListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, MenuItem.OnActionExpandListener
{
  private static final String ACTION_TIMER_SWITCH = "qualcomm.intent.action.TIMER_SWITCH";
  private static final int ACTIVATE_SEARCH = 0;
  private static final int ACTIVATE_SEARCH_DELAY = 300;
  public static final String BACK_STACK_PREFS = ":settings:prefs";
  private static final String EMPTY_QUERY = "";
  private static final String[] ENTRY_FRAGMENTS = { WirelessSettings.class.getName(), WifiSettings.class.getName(), AdvancedWifiSettings.class.getName(), SavedAccessPointsWifiSettings.class.getName(), BluetoothSettings.class.getName(), SimSettings.class.getName(), TetherSettings.class.getName(), WifiP2pSettings.class.getName(), VpnSettings.class.getName(), DateTimeSettings.class.getName(), OtherDeviceFunctionsSettings.class.getName(), LocaleListEditor.class.getName(), InputMethodAndLanguageSettings.class.getName(), AvailableVirtualKeyboardFragment.class.getName(), SpellCheckersSettings.class.getName(), UserDictionaryList.class.getName(), UserDictionarySettings.class.getName(), HomeSettings.class.getName(), DisplaySettings.class.getName(), DeviceInfoSettings.class.getName(), ManageApplications.class.getName(), NotificationApps.class.getName(), ManageAssist.class.getName(), ProcessStatsUi.class.getName(), NotificationStation.class.getName(), LocationSettings.class.getName(), SecuritySettings.class.getName(), UsageAccessDetails.class.getName(), PrivacySettings.class.getName(), DeviceAdminSettings.class.getName(), AccessibilitySettings.class.getName(), AccessibilitySettingsForSetupWizard.class.getName(), CaptionPropertiesFragment.class.getName(), ToggleDaltonizerPreferenceFragment.class.getName(), TextToSpeechSettings.class.getName(), StorageSettings.class.getName(), PrivateVolumeForget.class.getName(), PrivateVolumeSettings.class.getName(), PublicVolumeSettings.class.getName(), DevelopmentSettings.class.getName(), AndroidBeam.class.getName(), WifiDisplaySettings.class.getName(), PowerUsageSummary.class.getName(), AccountSyncSettings.class.getName(), AccountSettings.class.getName(), CryptKeeperSettings.class.getName(), DataUsageSummary.class.getName(), DreamSettings.class.getName(), UserSettings.class.getName(), NotificationAccessSettings.class.getName(), ZenAccessSettings.class.getName(), PrintSettingsFragment.class.getName(), PrintJobSettingsFragment.class.getName(), TrustedCredentialsSettings.class.getName(), PaymentSettings.class.getName(), KeyboardLayoutPickerFragment.class.getName(), KeyboardLayoutPickerFragment2.class.getName(), PhysicalKeyboardFragment.class.getName(), ZenModeSettings.class.getName(), SoundSettings.class.getName(), ConfigureNotificationSettings.class.getName(), ChooseLockPassword.ChooseLockPasswordFragment.class.getName(), ChooseLockPattern.ChooseLockPatternFragment.class.getName(), InstalledAppDetails.class.getName(), BatterySaverSettings.class.getName(), AppNotificationSettings.class.getName(), OtherSoundSettings.class.getName(), ApnSettings.class.getName(), ApnEditor.class.getName(), WifiCallingSettings.class.getName(), ZenModePrioritySettings.class.getName(), ZenModeAutomationSettings.class.getName(), ZenModeScheduleRuleSettings.class.getName(), ZenModeEventRuleSettings.class.getName(), ZenModeVisualInterruptionSettings.class.getName(), ProcessStatsUi.class.getName(), PowerUsageDetail.class.getName(), ProcessStatsSummary.class.getName(), DrawOverlayDetails.class.getName(), WriteSettingsDetails.class.getName(), AdvancedAppSettings.class.getName(), WallpaperTypeSettings.class.getName(), VrListenerSettings.class.getName(), ManagedProfileSettings.class.getName(), ChooseAccountActivity.class.getName(), IccLockSettings.class.getName(), ImeiInformation.class.getName(), SimStatus.class.getName(), Status.class.getName(), TestingSettings.class.getName(), WifiAPITest.class.getName(), WifiInfo.class.getName(), OPNotificationAndNotdisturb.class.getName(), OPStorageSettings.class.getName(), OPApplicationsSettings.class.getName(), OPDeviceName.class.getName(), OPButtonsSettings.class.getName(), OPGestureSettings.class.getName(), OPCredentialsManagementSettings.class.getName(), OPFingerPrintEditFragments.class.getName(), OPRingPattern.class.getName(), OPSilentMode.class.getName(), OPStatusBarCustomizeSettings.class.getName(), DataUsageMeteredSettings.class.getName(), DataUsageList.class.getName(), Settings.OPRingModeActivity.class.getName(), Settings.OPSilentModeActivity.class.getName(), DataSaverSummary.class.getName(), OPNightMode.class.getName(), OPReadingMode.class.getName(), OPGamingMode.class.getName(), QuickPaySettings.class.getName(), OPOthersSettings.class.getName(), OPPreInstalledAppList.class.getName(), OPProductInfoSettings.class.getName(), BgOptimizeApps.class.getName(), BgOptimizeSwitch.class.getName(), OPDefaultHomeSettings.class.getName(), OPFontStyleSettings.class.getName(), OPAppLocker.class.getName(), DisplaySizeAdaptionApps.class.getName(), OPFaceUnlockSettings.class.getName() };
  public static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
  public static final String EXTRA_HIDE_DRAWER = ":settings:hide_drawer";
  public static final String EXTRA_LAUNCH_ACTIVITY_ACTION = ":settings:launch_activity_action";
  protected static final String EXTRA_PREFS_SET_BACK_TEXT = "extra_prefs_set_back_text";
  protected static final String EXTRA_PREFS_SET_NEXT_TEXT = "extra_prefs_set_next_text";
  protected static final String EXTRA_PREFS_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";
  private static final String EXTRA_PREFS_SHOW_SKIP = "extra_prefs_show_skip";
  public static final String EXTRA_SHOW_FRAGMENT = ":settings:show_fragment";
  public static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args";
  public static final String EXTRA_SHOW_FRAGMENT_AS_SHORTCUT = ":settings:show_fragment_as_shortcut";
  public static final String EXTRA_SHOW_FRAGMENT_AS_SUBSETTING = ":settings:show_fragment_as_subsetting";
  public static final String EXTRA_SHOW_FRAGMENT_TITLE = ":settings:show_fragment_title";
  public static final String EXTRA_SHOW_FRAGMENT_TITLE_RESID = ":settings:show_fragment_title_resid";
  public static final String EXTRA_SHOW_FRAGMENT_TITLE_RES_PACKAGE_NAME = ":settings:show_fragment_title_res_package_name";
  static final String EXTRA_SHOW_MENU = "show_drawer_menu";
  private static final String EXTRA_UI_OPTIONS = "settings:ui_options";
  private static final String[] LIKE_SHORTCUT_INTENT_ACTION_ARRAY = { "android.settings.APPLICATION_DETAILS_SETTINGS" };
  private static final int LOADER_ID_INDEXABLE_CONTENT_MONITOR = 1;
  private static final String LOG_TAG = "Settings";
  private static final String LTE_4G_FRAGMENT = "com.android.settings.Lte4GEnableSetting";
  public static final String META_DATA_KEY_FRAGMENT_CLASS = "com.android.settings.FRAGMENT_CLASS";
  public static final String META_DATA_KEY_LAUNCH_ACTIVITY_ACTION = "com.android.settings.ACTIVITY_ACTION";
  private static final String MOBILENETWORK_FRAGMENT = "com.android.settings.MobileNetworkMain";
  private static final String MSG_DATA_FORCE_REFRESH = "msg_data_force_refresh";
  private static final String ONEPLUS_CLOUD_PACKAGE = "com.oneplus.cloud";
  private static final String PROFILEMGR_MAIN_FRAGMENT = "com.android.settings.ProfileMgrMain";
  private static final int REQUEST_SUGGESTION = 42;
  private static final String SAVE_KEY_CATEGORIES = ":settings:categories";
  private static final String SAVE_KEY_HOME_ACTIVITIES_COUNT = ":settings:home_activities_count";
  private static final String SAVE_KEY_SEARCH_MENU_EXPANDED = ":settings:search_menu_expanded";
  private static final String SAVE_KEY_SEARCH_QUERY = ":settings:search_query";
  private static final String SAVE_KEY_SHOW_HOME_AS_UP = ":settings:show_home_as_up";
  private static final String SAVE_KEY_SHOW_SEARCH = ":settings:show_search";
  private static final String SYSTEM_UPDATE = "android.settings.SystemUpdateActivity";
  private String[] SETTINGS_FOR_RESTRICTED = { Settings.WifiSettingsActivity.class.getName(), Settings.BluetoothSettingsActivity.class.getName(), Settings.MobileNetworkMainActivity.class.getName(), Settings.TetherSettingsActivity.class.getName(), Settings.DataUsageSummaryActivity.class.getName(), Settings.RoamingSettingsActivity.class.getName(), Settings.SimSettingsActivity.class.getName(), Settings.Lte4GEnableActivity.class.getName(), Settings.WirelessSettingsActivity.class.getName(), Settings.HomeSettingsActivity.class.getName(), Settings.SoundSettingsActivity.class.getName(), Settings.DisplaySettingsActivity.class.getName(), Settings.StorageSettingsActivity.class.getName(), Settings.ManageApplicationsActivity.class.getName(), Settings.PowerUsageSummaryActivity.class.getName(), Settings.ProfileMgrMainActivity.class.getName(), Settings.LocationSettingsActivity.class.getName(), Settings.SecuritySettingsActivity.class.getName(), Settings.InputMethodAndLanguageSettingsActivity.class.getName(), Settings.UserSettingsActivity.class.getName(), Settings.AccountSettingsActivity.class.getName(), Settings.DateTimeSettingsActivity.class.getName(), Settings.DeviceInfoSettingsActivity.class.getName(), Settings.AccessibilitySettingsActivity.class.getName(), Settings.PrintSettingsActivity.class.getName(), Settings.PaymentSettingsActivity.class.getName(), Settings.TimerSwitchSettingsActivity.class.getName(), Settings.SystemUpdateActivity.class.getName(), Settings.OtherDeviceFunctionsSettingsActivity.class.getName(), Settings.OPCloudServiceSettings.class.getName(), Settings.OPOthersSettingsActivity.class.getName() };
  private ActionBar mActionBar;
  private String mActivityAction;
  private AppBarLayout mAppBarLayout;
  private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.BATTERY_CHANGED".equals(paramAnonymousIntent.getAction()))
      {
        boolean bool = Utils.isBatteryPresent(paramAnonymousIntent);
        if (SettingsActivity.-get0(SettingsActivity.this) != bool)
        {
          SettingsActivity.-set0(SettingsActivity.this, bool);
          SettingsActivity.-wrap1(SettingsActivity.this);
        }
      }
    }
  };
  private boolean mBatteryPresent = true;
  private ArrayList<DashboardCategory> mCategories = new ArrayList();
  private ViewGroup mContent;
  private CoordinatorLayout mCoordinatorLayout;
  private ComponentName mCurrentSuggestion;
  private SharedPreferences mDevelopmentPreferences;
  private SharedPreferences.OnSharedPreferenceChangeListener mDevelopmentPreferencesListener;
  private boolean mDisplayHomeAsUpEnabled;
  private boolean mDisplaySearch;
  private final DynamicIndexableContentMonitor mDynamicIndexableContentMonitor = new DynamicIndexableContentMonitor();
  private String mFragmentClass;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      switch (paramAnonymousMessage.what)
      {
      }
      do
      {
        return;
      } while (!SettingsActivity.-get3(SettingsActivity.this));
      if (!Utils.isLowStorage(SettingsActivity.this))
      {
        System.currentTimeMillis();
        Index.getInstance(SettingsActivity.this.getApplicationContext()).update();
        return;
      }
      Log.w("Settings", "Cannot update the Indexer as we are running low on storage space!");
    }
  };
  private CharSequence mInitialTitle;
  private int mInitialTitleResId;
  private boolean mIsShortcut;
  private boolean mIsShowingDashboard;
  private int mMainContentId = 2131362550;
  private boolean mNeedToRevertToInitialFragment = false;
  private Button mNextButton;
  private Intent mResultIntentData;
  private MenuItem mSearchMenuItem;
  private boolean mSearchMenuItemExpanded = false;
  private String mSearchQuery;
  private SearchResultsSummary mSearchResultsFragment;
  private SearchView mSearchView;
  private SwitchBar mSwitchBar;
  private CollapsingToolbarLayout mToolbarLayout;
  private final BroadcastReceiver mUserAddRemoveReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ((paramAnonymousContext.equals("android.intent.action.USER_ADDED")) || (paramAnonymousContext.equals("android.intent.action.USER_REMOVED"))) {
        Index.getInstance(SettingsActivity.this.getApplicationContext()).update();
      }
    }
  };
  
  private void doUpdateTilesList()
  {
    PackageManager localPackageManager = getPackageManager();
    Object localObject1 = UserManager.get(this);
    boolean bool2 = ((UserManager)localObject1).isAdminUser();
    String str = getPackageName();
    setTileEnabled(new ComponentName(str, Settings.WifiSettingsActivity.class.getName()), localPackageManager.hasSystemFeature("android.hardware.wifi"), bool2, localPackageManager);
    setTileEnabled(new ComponentName(str, Settings.BluetoothSettingsActivity.class.getName()), localPackageManager.hasSystemFeature("android.hardware.bluetooth"), bool2, localPackageManager);
    Object localObject2 = new ComponentName(str, Settings.TetherSettingsActivity.class.getName());
    boolean bool1;
    label164:
    label205:
    label381:
    ComponentName localComponentName;
    if (!getResources().getBoolean(2131558436))
    {
      bool1 = localPackageManager.hasSystemFeature("android.hardware.wifi");
      setTileEnabled((ComponentName)localObject2, bool1, bool2, localPackageManager);
      localObject2 = new ComponentName(str, Settings.MobileNetworkMainActivity.class.getName());
      if (!getResources().getBoolean(2131558436)) {
        break label884;
      }
      bool1 = localPackageManager.hasSystemFeature("android.hardware.telephony");
      setTileEnabled((ComponentName)localObject2, bool1, bool2, localPackageManager);
      localObject2 = new ComponentName(str, Settings.AccessibilitySettingsActivity.class.getName());
      if (!getResources().getBoolean(2131558436)) {
        break label889;
      }
      bool1 = false;
      setTileEnabled((ComponentName)localObject2, bool1, bool2, localPackageManager);
      setTileEnabled(new ComponentName(str, Settings.Lte4GEnableActivity.class.getName()), getResources().getBoolean(2131558435), bool2, localPackageManager);
      setTileEnabled(new ComponentName(str, Settings.DataUsageSummaryActivity.class.getName()), Utils.isBandwidthControlEnabled(), bool2, localPackageManager);
      setTileEnabled(new ComponentName(str, Settings.RoamingSettingsActivity.class.getName()), getResources().getBoolean(2131558430), bool2, localPackageManager);
      setTileEnabled(new ComponentName(str, Settings.SimSettingsActivity.class.getName()), Utils.showSimCardTile(this), bool2, localPackageManager);
      setTileEnabled(new ComponentName(str, Settings.PowerUsageSummaryActivity.class.getName()), this.mBatteryPresent, bool2, localPackageManager);
      localObject2 = new ComponentName(str, Settings.UserSettingsActivity.class.getName());
      if (!UserManager.supportsMultipleUsers()) {
        break label899;
      }
      if (!Utils.isMonkeyRunning()) {
        break label894;
      }
      bool1 = false;
      setTileEnabled((ComponentName)localObject2, bool1, bool2, localPackageManager);
      localObject2 = NfcAdapter.getDefaultAdapter(this);
      localComponentName = new ComponentName(str, Settings.PaymentSettingsActivity.class.getName());
      if ((!localPackageManager.hasSystemFeature("android.hardware.nfc")) || (!localPackageManager.hasSystemFeature("android.hardware.nfc.hce"))) {
        break label909;
      }
      if (localObject2 == null) {
        break label904;
      }
      bool1 = true;
      label440:
      setTileEnabled(localComponentName, bool1, bool2, localPackageManager);
      localObject2 = new ComponentName(str, Settings.PrintSettingsActivity.class.getName());
      if (getResources().getBoolean(2131558436)) {
        break label914;
      }
      bool1 = localPackageManager.hasSystemFeature("android.software.print");
      label487:
      setTileEnabled((ComponentName)localObject2, bool1, bool2, localPackageManager);
      localObject2 = new ComponentName(str, Settings.DeviceInfoSettingsActivity.class.getName());
      if (!getResources().getBoolean(2131558436)) {
        break label919;
      }
      bool1 = false;
      label528:
      setTileEnabled((ComponentName)localObject2, bool1, bool2, localPackageManager);
      setTileEnabled(new ComponentName(str, Settings.OtherDeviceFunctionsSettingsActivity.class.getName()), getResources().getBoolean(2131558436), bool2, localPackageManager);
      setTileEnabled(new ComponentName(str, Settings.SystemUpdateActivity.class.getName()), getResources().getBoolean(2131558436), bool2, localPackageManager);
      setTileEnabled(new ComponentName(str, Settings.ProfileMgrMainActivity.class.getName()), getResources().getBoolean(2131558438), bool2, localPackageManager);
      setTileEnabled(new ComponentName(str, Settings.OPOthersSettingsActivity.class.getName()), true, bool2, localPackageManager);
      localObject2 = new ComponentName(str, Settings.OPCloudServiceSettings.class.getName());
      if ((OPUtils.isAppExist(getApplicationContext(), "com.oneplus.cloud")) && (!OPUtils.isGuestMode())) {
        break label924;
      }
      bool1 = false;
      label689:
      setTileEnabled((ComponentName)localObject2, bool1, bool2, localPackageManager);
      setTileEnabled(new ComponentName(str, Settings.OPFontStyleSettingsActivity.class.getName()), OPUtils.isSupportFontStyleSetting(), bool2, localPackageManager);
      setTileEnabled(new ComponentName(str, Settings.OPDefaultHomeSettingsActivity.class.getName()), isShowLauncherTile(), bool2, localPackageManager);
      if (!this.mDevelopmentPreferences.getBoolean("show", Build.TYPE.equals("eng"))) {
        break label934;
      }
      if (!((UserManager)localObject1).hasUserRestriction("no_debugging_features")) {
        break label929;
      }
      bool1 = false;
      label784:
      setTileEnabled(new ComponentName(str, Settings.DevelopmentSettingsActivity.class.getName()), bool1, bool2, localPackageManager);
      DevelopmentTiles.setTilesEnabled(this, bool1);
      localObject1 = new Intent("qualcomm.intent.action.TIMER_SWITCH");
      localObject1 = getBaseContext().getPackageManager().queryIntentActivities((Intent)localObject1, 0);
      if ((localObject1 != null) && (!((List)localObject1).isEmpty())) {
        break label939;
      }
    }
    label884:
    label889:
    label894:
    label899:
    label904:
    label909:
    label914:
    label919:
    label924:
    label929:
    label934:
    label939:
    for (;;)
    {
      setTileEnabled(new ComponentName(str, Settings.TimerSwitchSettingsActivity.class.getName()), false, bool2, localPackageManager);
      if (!bool2) {
        break label942;
      }
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label164;
      bool1 = true;
      break label205;
      bool1 = true;
      break label381;
      bool1 = false;
      break label381;
      bool1 = false;
      break label440;
      bool1 = false;
      break label440;
      bool1 = false;
      break label487;
      bool1 = true;
      break label528;
      bool1 = true;
      break label689;
      bool1 = true;
      break label784;
      bool1 = false;
      break label784;
    }
    label942:
    localObject1 = getDashboardCategories().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = ((DashboardCategory)((Iterator)localObject1).next()).tiles.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localComponentName = ((Tile)((Iterator)localObject2).next()).intent.getComponent();
        if ((str.equals(localComponentName.getPackageName())) && (!ArrayUtils.contains(this.SETTINGS_FOR_RESTRICTED, localComponentName.getClassName()))) {
          setTileEnabled(localComponentName, false, bool2, localPackageManager);
        }
      }
    }
  }
  
  private List getLauncherPackageName()
  {
    String str = SettingsBaseApplication.mApplication.getPackageName();
    ArrayList localArrayList = new ArrayList();
    StringBuilder localStringBuilder = new StringBuilder();
    Object localObject = new Intent("android.intent.action.MAIN");
    ((Intent)localObject).addCategory("android.intent.category.HOME");
    localObject = SettingsBaseApplication.mApplication.getPackageManager().queryIntentActivities((Intent)localObject, 131072).iterator();
    while (((Iterator)localObject).hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject).next();
      if (!localResolveInfo.activityInfo.packageName.equals(str))
      {
        localArrayList.add(localResolveInfo.activityInfo.packageName);
        localStringBuilder.append(localResolveInfo.activityInfo.packageName).append(",");
      }
    }
    Log.d("SettingsActivity", "getLauncherPackageName count:" + localArrayList + ", pkgs:" + localStringBuilder.toString());
    if ((localArrayList == null) || (localArrayList.size() == 0)) {
      return null;
    }
    return localArrayList;
  }
  
  private void getMetaData()
  {
    try
    {
      ActivityInfo localActivityInfo = getPackageManager().getActivityInfo(getComponentName(), 128);
      if (localActivityInfo != null)
      {
        if (localActivityInfo.metaData == null) {
          return;
        }
        this.mFragmentClass = localActivityInfo.metaData.getString("com.android.settings.FRAGMENT_CLASS");
        this.mActivityAction = localActivityInfo.metaData.getString("com.android.settings.ACTIVITY_ACTION");
        return;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.d("Settings", "Cannot get Metadata for: " + getComponentName().toString());
      return;
    }
  }
  
  private String getMetricsTag()
  {
    Object localObject2 = getClass().getName();
    Object localObject1 = localObject2;
    if (getIntent() != null)
    {
      localObject1 = localObject2;
      if (getIntent().hasExtra(":settings:show_fragment")) {
        localObject1 = getIntent().getStringExtra(":settings:show_fragment");
      }
    }
    localObject2 = localObject1;
    if (((String)localObject1).startsWith("com.android.settings.")) {
      localObject2 = ((String)localObject1).replace("com.android.settings.", "");
    }
    return (String)localObject2;
  }
  
  private String getStartingFragmentClass(Intent paramIntent)
  {
    if (this.mFragmentClass != null) {
      return this.mFragmentClass;
    }
    String str = paramIntent.getComponent().getClassName();
    if (str.equals(getClass().getName())) {
      return null;
    }
    if ((!"com.android.settings.ManageApplications".equals(str)) && (!"com.android.settings.RunningServices".equals(str)))
    {
      paramIntent = str;
      if (!"com.android.settings.applications.StorageUse".equals(str)) {}
    }
    else
    {
      paramIntent = ManageApplications.class.getName();
    }
    return paramIntent;
  }
  
  private static boolean isLikeShortCutIntent(Intent paramIntent)
  {
    paramIntent = paramIntent.getAction();
    if (paramIntent == null) {
      return false;
    }
    int i = 0;
    while (i < LIKE_SHORTCUT_INTENT_ACTION_ARRAY.length)
    {
      if (LIKE_SHORTCUT_INTENT_ACTION_ARRAY[i].equals(paramIntent)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private static boolean isShortCutIntent(Intent paramIntent)
  {
    paramIntent = paramIntent.getCategories();
    if (paramIntent != null) {
      return paramIntent.contains("com.android.settings.SHORTCUT");
    }
    return false;
  }
  
  private boolean isShowLauncherTile()
  {
    List localList = getLauncherPackageName();
    return (localList != null) && (localList.size() > 1);
  }
  
  private void revertToInitialFragment()
  {
    if ((this.mCoordinatorLayout != null) && (this.mAppBarLayout != null) && (this.mToolbarLayout != null))
    {
      this.mCoordinatorLayout.setNestedScrollingEnabled(false);
      this.mAppBarLayout.setExpanded(false);
      this.mToolbarLayout.setTitleEnabled(true);
    }
    this.mNeedToRevertToInitialFragment = false;
    this.mSearchResultsFragment = null;
    this.mSearchMenuItemExpanded = false;
    getFragmentManager().popBackStackImmediate(":settings:prefs", 1);
    if (this.mSearchMenuItem != null) {
      this.mSearchMenuItem.collapseActionView();
    }
  }
  
  private void setCoordinatorLayoutEnable(boolean paramBoolean)
  {
    if ((this.mCoordinatorLayout != null) && (this.mAppBarLayout != null) && (this.mToolbarLayout != null))
    {
      this.mCoordinatorLayout.setNestedScrollingEnabled(paramBoolean);
      this.mAppBarLayout.setExpanded(paramBoolean);
      this.mToolbarLayout.setTitleEnabled(paramBoolean);
    }
  }
  
  private void setTileEnabled(ComponentName paramComponentName, boolean paramBoolean1, boolean paramBoolean2, PackageManager paramPackageManager)
  {
    if (paramBoolean2) {
      paramBoolean2 = paramBoolean1;
    }
    for (;;)
    {
      setTileEnabled(paramComponentName, paramBoolean2);
      return;
      paramBoolean2 = paramBoolean1;
      if (getPackageName().equals(paramComponentName.getPackageName()))
      {
        paramBoolean2 = paramBoolean1;
        if (!ArrayUtils.contains(this.SETTINGS_FOR_RESTRICTED, paramComponentName.getClassName())) {
          paramBoolean2 = false;
        }
      }
    }
  }
  
  private int setTitleFromBackStack()
  {
    int i = getFragmentManager().getBackStackEntryCount();
    if (i == 0)
    {
      if (this.mInitialTitleResId > 0)
      {
        setTitle(this.mInitialTitleResId);
        return 0;
      }
      setTitle(this.mInitialTitle);
      return 0;
    }
    setTitleFromBackStackEntry(getFragmentManager().getBackStackEntryAt(i - 1));
    return i;
  }
  
  private void setTitleFromBackStackEntry(FragmentManager.BackStackEntry paramBackStackEntry)
  {
    int i = paramBackStackEntry.getBreadCrumbTitleRes();
    if (i > 0) {}
    for (paramBackStackEntry = getText(i);; paramBackStackEntry = paramBackStackEntry.getBreadCrumbTitle())
    {
      if (paramBackStackEntry != null) {
        setTitle(paramBackStackEntry);
      }
      return;
    }
  }
  
  private void setTitleFromIntent(Intent paramIntent)
  {
    int i = paramIntent.getIntExtra(":settings:show_fragment_title_resid", -1);
    if (i > 0)
    {
      this.mInitialTitle = null;
      this.mInitialTitleResId = i;
      paramIntent = paramIntent.getStringExtra(":settings:show_fragment_title_res_package_name");
      if (paramIntent != null) {
        try
        {
          this.mInitialTitle = createPackageContextAsUser(paramIntent, 0, new UserHandle(UserHandle.myUserId())).getResources().getText(this.mInitialTitleResId);
          setTitle(this.mInitialTitle);
          this.mInitialTitleResId = -1;
          return;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          Log.w("Settings", "Could not find package" + paramIntent);
          return;
        }
      }
      setTitle(this.mInitialTitleResId);
      return;
    }
    this.mInitialTitleResId = -1;
    paramIntent = paramIntent.getStringExtra(":settings:show_fragment_title");
    if (paramIntent != null) {}
    for (;;)
    {
      this.mInitialTitle = paramIntent;
      setTitle(this.mInitialTitle);
      return;
      paramIntent = getTitle();
    }
  }
  
  private void setToolBar()
  {
    if (this.mIsShowingDashboard)
    {
      this.mCoordinatorLayout = ((CoordinatorLayout)findViewById(2131362546));
      this.mAppBarLayout = ((AppBarLayout)findViewById(2131362547));
      this.mToolbarLayout = ((CollapsingToolbarLayout)findViewById(2131362548));
      if (this.mActionBar != null) {
        this.mActionBar.hide();
      }
      setActionBar((Toolbar)findViewById(2131362549));
    }
  }
  
  private Fragment switchToFragment(String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt, CharSequence paramCharSequence, boolean paramBoolean3)
  {
    if (paramString.equals(SimSettings.class.getName()))
    {
      Log.i("Settings", "switchToFragment, launch simSettings  ");
      startActivity(new Intent("com.android.settings.sim.SIM_SUB_INFO_SETTINGS"));
      finish();
      return null;
    }
    if ("com.android.settings.Lte4GEnableSetting".equals(paramString))
    {
      paramString = new Intent("android.settings.SETTINGS");
      paramString.addFlags(268435456);
      startActivity(paramString);
      finish();
      return null;
    }
    if ("com.android.settings.ProfileMgrMain".equals(paramString))
    {
      paramString = new Intent();
      paramString.setAction("com.codeaurora.STARTPROFILE");
      paramString.setPackage("com.android.profile");
      startActivity(paramString);
      finish();
      return null;
    }
    if ("com.android.settings.MobileNetworkMain".equals(paramString))
    {
      paramString = new Intent();
      paramString.setAction("android.settings.DATA_ROAMING_SETTINGS");
      paramString.setPackage("com.qualcomm.qti.networksetting");
      startActivity(paramString);
      finish();
      return null;
    }
    if ("android.settings.SystemUpdateActivity".equals(paramString))
    {
      SystemUpdateHandle();
      return null;
    }
    if ((!paramBoolean1) || (isValidFragment(paramString)))
    {
      paramString = Fragment.instantiate(this, paramString, paramBundle);
      paramBundle = getFragmentManager().beginTransaction();
      paramBundle.replace(this.mMainContentId, paramString);
      if (paramBoolean3) {
        TransitionManager.beginDelayedTransition(this.mContent);
      }
      if (paramBoolean2) {
        paramBundle.addToBackStack(":settings:prefs");
      }
      if (paramInt <= 0) {
        break label300;
      }
      paramBundle.setBreadCrumbTitle(paramInt);
    }
    for (;;)
    {
      paramBundle.commitAllowingStateLoss();
      getFragmentManager().executePendingTransactions();
      return paramString;
      throw new IllegalArgumentException("Invalid fragment for this activity: " + paramString);
      label300:
      if (paramCharSequence != null) {
        paramBundle.setBreadCrumbTitle(paramCharSequence);
      }
    }
  }
  
  private void switchToSearchResultsFragmentIfNeeded()
  {
    if (this.mSearchResultsFragment != null) {
      return;
    }
    setCoordinatorLayoutEnable(false);
    Fragment localFragment = getFragmentManager().findFragmentById(this.mMainContentId);
    if ((localFragment != null) && ((localFragment instanceof SearchResultsSummary))) {}
    for (this.mSearchResultsFragment = ((SearchResultsSummary)localFragment);; this.mSearchResultsFragment = ((SearchResultsSummary)switchToFragment(SearchResultsSummary.class.getName(), null, false, true, 2131693116, null, true)))
    {
      this.mSearchResultsFragment.setSearchView(this.mSearchView);
      this.mSearchMenuItemExpanded = true;
      return;
    }
  }
  
  private void updateSearchIndex()
  {
    getWindow().getDecorView().post(new Runnable()
    {
      public void run()
      {
        SettingsActivity.-get2(SettingsActivity.this).post(new Runnable()
        {
          public void run()
          {
            if (SettingsActivity.-get3(SettingsActivity.this))
            {
              SettingsActivity.this.getWindow().setBackgroundDrawableResource(2130838560);
              if (!Utils.isLowStorage(SettingsActivity.this))
              {
                System.currentTimeMillis();
                Index.getInstance(SettingsActivity.this.getApplicationContext()).update();
                SettingsActivity.-get1(SettingsActivity.this).register(SettingsActivity.this, 1);
              }
            }
            else
            {
              return;
            }
            Log.w("Settings", "Cannot update the Indexer as we are running low on storage space!");
          }
        });
      }
    });
  }
  
  private void updateTilesList()
  {
    AsyncTask.execute(new Runnable()
    {
      public void run()
      {
        SettingsActivity.-wrap0(SettingsActivity.this);
      }
    });
  }
  
  public void SystemUpdateHandle()
  {
    Object localObject = ((CarrierConfigManager)getBaseContext().getSystemService("carrier_config")).getConfig();
    if (((PersistableBundle)localObject).getBoolean("ci_action_on_sys_update_bool")) {
      Utils.ciActionOnSysUpdate(getBaseContext(), (PersistableBundle)localObject);
    }
    localObject = new Intent("android.settings.SYSTEM_UPDATE_SETTINGS");
    localObject = getBaseContext().getPackageManager().queryIntentActivities((Intent)localObject, 0);
    int j = ((List)localObject).size();
    int i = 0;
    while (i < j)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)((List)localObject).get(i);
      if ((localResolveInfo.activityInfo.applicationInfo.flags & 0x1) != 0)
      {
        localObject = new Intent().setClassName(localResolveInfo.activityInfo.packageName, localResolveInfo.activityInfo.name);
        ((Intent)localObject).addFlags(268435456);
        startActivity((Intent)localObject);
        finish();
        return;
      }
      i += 1;
    }
  }
  
  public void finishPreferencePanel(Fragment paramFragment, int paramInt, Intent paramIntent)
  {
    setResult(paramInt, paramIntent);
    finish();
  }
  
  public Intent getIntent()
  {
    Intent localIntent1 = super.getIntent();
    Object localObject = getStartingFragmentClass(localIntent1);
    if (localObject != null)
    {
      Intent localIntent2 = new Intent(localIntent1);
      localIntent2.putExtra(":settings:show_fragment", (String)localObject);
      localObject = localIntent1.getExtras();
      if (localObject != null) {}
      for (localObject = new Bundle((Bundle)localObject);; localObject = new Bundle())
      {
        ((Bundle)localObject).putParcelable("intent", localIntent1);
        localIntent2.putExtra(":settings:show_fragment_args", (Bundle)localObject);
        return localIntent2;
      }
    }
    if (this.mActivityAction != null)
    {
      localObject = new Intent(localIntent1);
      ((Intent)localObject).putExtra(":settings:launch_activity_action", this.mActivityAction);
      return (Intent)localObject;
    }
    return localIntent1;
  }
  
  public Button getNextButton()
  {
    return this.mNextButton;
  }
  
  public Intent getResultIntentData()
  {
    return this.mResultIntentData;
  }
  
  public SharedPreferences getSharedPreferences(String paramString, int paramInt)
  {
    if (paramString.equals(getPackageName() + "_preferences")) {
      return new SharedPreferencesLogger(this, getMetricsTag());
    }
    return super.getSharedPreferences(paramString, paramInt);
  }
  
  public SwitchBar getSwitchBar()
  {
    return this.mSwitchBar;
  }
  
  public boolean hasNextButton()
  {
    return this.mNextButton != null;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    int i = 0;
    while (i < ENTRY_FRAGMENTS.length)
    {
      if (ENTRY_FRAGMENTS[i].equals(paramString)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void needToRevertToInitialFragment()
  {
    this.mNeedToRevertToInitialFragment = true;
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramInt1 == 42) && (this.mCurrentSuggestion != null) && (paramInt2 != 0)) {
      getPackageManager().setComponentEnabledSetting(this.mCurrentSuggestion, 2, 1);
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onBackStackChanged()
  {
    setTitleFromBackStack();
  }
  
  public boolean onClose()
  {
    return false;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    Index.getInstance(this).update();
    setToolBar();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    setRequestedOrientation(1);
    super.onCreate(paramBundle);
    System.currentTimeMillis();
    updateSearchIndex();
    if (OPUtils.isWhiteModeOn(getContentResolver())) {
      getWindow().getDecorView().setSystemUiVisibility(8192);
    }
    getMetaData();
    Intent localIntent = getIntent();
    if (localIntent.hasExtra(":settings:launch_activity_action"))
    {
      if (this.mActivityAction != null) {}
      try
      {
        startActivity(new Intent(this.mActivityAction));
        finish();
        return;
      }
      catch (ActivityNotFoundException paramBundle)
      {
        for (;;)
        {
          Log.w("Settings", "Activity not found for action: " + this.mActivityAction);
        }
      }
    }
    if (localIntent.hasExtra("settings:ui_options")) {
      getWindow().setUiOptions(localIntent.getIntExtra("settings:ui_options", 0));
    }
    if (localIntent.getBooleanExtra(":settings:hide_drawer", false)) {
      setIsDrawerPresent(false);
    }
    this.mDevelopmentPreferences = getSharedPreferences("development", 0);
    Object localObject = localIntent.getStringExtra(":settings:show_fragment");
    boolean bool;
    String str;
    label291:
    label312:
    int i;
    if ((!isShortCutIntent(localIntent)) && (!isLikeShortCutIntent(localIntent)))
    {
      bool = localIntent.getBooleanExtra(":settings:show_fragment_as_shortcut", false);
      this.mIsShortcut = bool;
      str = localIntent.getComponent().getClassName();
      if ((str.equals(Settings.class.getName())) || (str.equals(Settings.WirelessSettings.class.getName())) || (str.equals(Settings.DeviceSettings.class.getName())) || (str.equals(Settings.PersonalSettings.class.getName()))) {
        break label793;
      }
      bool = str.equals(Settings.WirelessSettings.class.getName());
      this.mIsShowingDashboard = bool;
      if ((this instanceof SubSettings)) {
        break label798;
      }
      bool = localIntent.getBooleanExtra(":settings:show_fragment_as_subsetting", false);
      if (bool)
      {
        i = getThemeResId();
        if ((i != 2131821591) && (i != 2131821593)) {
          setTheme(2131821584);
        }
      }
      if (!this.mIsShowingDashboard) {
        setTheme(2131821580);
      }
      if (!this.mIsShowingDashboard) {
        break label803;
      }
      i = 2130968984;
      label367:
      setContentView(i);
      this.mContent = ((ViewGroup)findViewById(this.mMainContentId));
      this.mActionBar = getActionBar();
      getFragmentManager().addOnBackStackChangedListener(this);
      if (paramBundle == null) {
        break label810;
      }
      setToolBar();
      this.mSearchMenuItemExpanded = paramBundle.getBoolean(":settings:search_menu_expanded");
      this.mSearchQuery = paramBundle.getString(":settings:search_query");
      setTitleFromIntent(localIntent);
      localObject = paramBundle.getParcelableArrayList(":settings:categories");
      if (localObject != null)
      {
        this.mCategories.clear();
        this.mCategories.addAll((Collection)localObject);
        setTitleFromBackStack();
      }
      this.mDisplayHomeAsUpEnabled = paramBundle.getBoolean(":settings:show_home_as_up");
      this.mDisplaySearch = paramBundle.getBoolean(":settings:show_search");
      label492:
      if (this.mActionBar != null)
      {
        paramBundle = this.mActionBar;
        if (!this.mIsShowingDashboard) {
          break label935;
        }
        bool = false;
        label513:
        paramBundle.setDisplayHomeAsUpEnabled(bool);
        this.mActionBar.setHomeButtonEnabled(this.mDisplayHomeAsUpEnabled);
      }
      this.mSwitchBar = ((SwitchBar)findViewById(2131362551));
      if (this.mSwitchBar != null) {
        this.mSwitchBar.setMetricsTag(getMetricsTag());
      }
      if (localIntent.getBooleanExtra("extra_prefs_show_button_bar", false))
      {
        paramBundle = findViewById(2131362444);
        if (paramBundle != null)
        {
          paramBundle.setVisibility(0);
          paramBundle = (Button)findViewById(2131362445);
          paramBundle.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              SettingsActivity.this.setResult(0, SettingsActivity.this.getResultIntentData());
              SettingsActivity.this.finish();
            }
          });
          localObject = (Button)findViewById(2131362143);
          ((Button)localObject).setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              SettingsActivity.this.setResult(-1, SettingsActivity.this.getResultIntentData());
              SettingsActivity.this.finish();
            }
          });
          this.mNextButton = ((Button)findViewById(2131362013));
          this.mNextButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              SettingsActivity.this.setResult(-1, SettingsActivity.this.getResultIntentData());
              SettingsActivity.this.finish();
            }
          });
          if (localIntent.hasExtra("extra_prefs_set_next_text"))
          {
            str = localIntent.getStringExtra("extra_prefs_set_next_text");
            if (!TextUtils.isEmpty(str)) {
              break label940;
            }
            this.mNextButton.setVisibility(8);
          }
          label702:
          if (localIntent.hasExtra("extra_prefs_set_back_text"))
          {
            str = localIntent.getStringExtra("extra_prefs_set_back_text");
            if (!TextUtils.isEmpty(str)) {
              break label952;
            }
            paramBundle.setVisibility(8);
          }
        }
      }
    }
    for (;;)
    {
      if (localIntent.getBooleanExtra("extra_prefs_show_skip", false)) {
        ((Button)localObject).setVisibility(0);
      }
      paramBundle = NfcAdapter.getDefaultAdapter(this);
      if (paramBundle != null) {
        paramBundle.setNdefPushMessage(new NdefMessage(NdefRecord.createApplicationRecord("com.android.settings"), new NdefRecord[0]), this, new Activity[0]);
      }
      return;
      bool = true;
      break;
      label793:
      bool = true;
      break label291;
      label798:
      bool = true;
      break label312;
      label803:
      i = 2130968985;
      break label367;
      label810:
      if (!this.mIsShowingDashboard)
      {
        this.mDisplaySearch = false;
        if (this.mIsShortcut) {
          this.mDisplayHomeAsUpEnabled = bool;
        }
        for (;;)
        {
          setTitleFromIntent(localIntent);
          switchToFragment((String)localObject, localIntent.getBundleExtra(":settings:show_fragment_args"), true, false, this.mInitialTitleResId, this.mInitialTitle, false);
          break;
          if (bool) {
            this.mDisplayHomeAsUpEnabled = true;
          } else {
            this.mDisplayHomeAsUpEnabled = false;
          }
        }
      }
      setToolBar();
      this.mDisplayHomeAsUpEnabled = false;
      this.mDisplaySearch = true;
      this.mInitialTitleResId = 2131693115;
      switchToFragment(DashboardSummary.class.getName(), null, false, false, this.mInitialTitleResId, this.mInitialTitle, false);
      break label492;
      label935:
      bool = true;
      break label513;
      label940:
      this.mNextButton.setText(str);
      break label702;
      label952:
      paramBundle.setText(str);
    }
  }
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    if ((!this.mDisplaySearch) || (OPUtils.isGuestMode())) {
      return false;
    }
    getMenuInflater().inflate(2132017156, paramMenu);
    String str = this.mSearchQuery;
    this.mSearchMenuItem = paramMenu.findItem(2131362852);
    this.mSearchView = ((SearchView)this.mSearchMenuItem.getActionView());
    if ((this.mSearchMenuItem == null) || (this.mSearchView == null)) {
      return false;
    }
    if (this.mSearchResultsFragment != null) {
      this.mSearchResultsFragment.setSearchView(this.mSearchView);
    }
    this.mSearchMenuItem.setOnActionExpandListener(this);
    int i = this.mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
    paramMenu = (TextView)this.mSearchView.findViewById(i);
    if (OPUtils.isAndroidModeOn(getContentResolver())) {
      paramMenu.setTextColor(getResources().getColor(2131493558));
    }
    for (;;)
    {
      this.mSearchView.setOnQueryTextListener(this);
      this.mSearchView.setOnCloseListener(this);
      if (this.mSearchMenuItemExpanded) {
        this.mSearchMenuItem.expandActionView();
      }
      this.mSearchView.setQuery(str, true);
      return true;
      paramMenu.setTextColor(getResources().getColor(2131493559));
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (this.mDevelopmentPreferencesListener != null)
    {
      this.mDevelopmentPreferences.unregisterOnSharedPreferenceChangeListener(this.mDevelopmentPreferencesListener);
      this.mDevelopmentPreferencesListener = null;
    }
  }
  
  public boolean onMenuItemActionCollapse(MenuItem paramMenuItem)
  {
    if ((paramMenuItem.getItemId() == this.mSearchMenuItem.getItemId()) && (this.mSearchMenuItemExpanded)) {
      revertToInitialFragment();
    }
    return true;
  }
  
  public boolean onMenuItemActionExpand(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == this.mSearchMenuItem.getItemId())
    {
      if (OPUtils.mAppUpdated)
      {
        Index.getInstance(getApplicationContext()).update();
        OPUtils.setAppUpdated(false);
      }
      switchToSearchResultsFragmentIfNeeded();
    }
    return true;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  public boolean onPreferenceStartFragment(PreferenceFragment paramPreferenceFragment, Preference paramPreference)
  {
    paramPreferenceFragment = paramPreference.getTitle();
    if (paramPreference.getFragment().equals(WallpaperTypeSettings.class.getName())) {
      paramPreferenceFragment = getString(2131691629);
    }
    startPreferencePanel(paramPreference.getFragment(), paramPreference.getExtras(), -1, paramPreferenceFragment, null, 0);
    return true;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    return false;
  }
  
  public void onProfileTileOpen()
  {
    if (!this.mIsShowingDashboard) {
      finish();
    }
  }
  
  public boolean onQueryTextChange(String paramString)
  {
    this.mSearchQuery = paramString;
    if (this.mSearchResultsFragment == null) {
      return false;
    }
    return this.mSearchResultsFragment.onQueryTextChange(paramString);
  }
  
  public boolean onQueryTextSubmit(String paramString)
  {
    switchToSearchResultsFragmentIfNeeded();
    this.mSearchQuery = paramString;
    return this.mSearchResultsFragment.onQueryTextSubmit(paramString);
  }
  
  protected void onResume()
  {
    super.onResume();
    if (isInMultiWindowMode()) {
      getWindow().setBackgroundDrawableResource(2130838560);
    }
    if ((getIntent() != null) && (getIntent().getBooleanExtra("show_drawer_menu", false))) {
      this.mActionBar.setHomeAsUpIndicator(2130838205);
    }
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (this.mCategories.size() > 0) {
      paramBundle.putParcelableArrayList(":settings:categories", this.mCategories);
    }
    paramBundle.putBoolean(":settings:show_home_as_up", this.mDisplayHomeAsUpEnabled);
    paramBundle.putBoolean(":settings:show_search", this.mDisplaySearch);
    boolean bool;
    if (this.mDisplaySearch)
    {
      if (this.mSearchMenuItem == null) {
        break label104;
      }
      bool = this.mSearchMenuItem.isActionViewExpanded();
      paramBundle.putBoolean(":settings:search_menu_expanded", bool);
      if (this.mSearchView == null) {
        break label109;
      }
    }
    label104:
    label109:
    for (String str = this.mSearchView.getQuery().toString();; str = "")
    {
      paramBundle.putString(":settings:search_query", str);
      return;
      bool = false;
      break;
    }
  }
  
  protected void onStart()
  {
    super.onStart();
    if (this.mNeedToRevertToInitialFragment) {
      revertToInitialFragment();
    }
    this.mDevelopmentPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
      public void onSharedPreferenceChanged(SharedPreferences paramAnonymousSharedPreferences, String paramAnonymousString)
      {
        SettingsActivity.-wrap1(SettingsActivity.this);
      }
    };
    this.mDevelopmentPreferences.registerOnSharedPreferenceChangeListener(this.mDevelopmentPreferencesListener);
    registerReceiver(this.mBatteryInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    registerReceiver(this.mUserAddRemoveReceiver, new IntentFilter("android.intent.action.USER_ADDED"));
    registerReceiver(this.mUserAddRemoveReceiver, new IntentFilter("android.intent.action.USER_REMOVED"));
    if ((!this.mDisplaySearch) || (TextUtils.isEmpty(this.mSearchQuery))) {}
    for (;;)
    {
      updateTilesList();
      return;
      onQueryTextSubmit(this.mSearchQuery);
    }
  }
  
  protected void onStop()
  {
    super.onStop();
    try
    {
      unregisterReceiver(this.mBatteryInfoReceiver);
      unregisterReceiver(this.mUserAddRemoveReceiver);
      this.mDynamicIndexableContentMonitor.unregister();
      return;
    }
    catch (Exception localException) {}
  }
  
  protected void onTileClicked(Tile paramTile)
  {
    if (this.mIsShowingDashboard)
    {
      openTile(paramTile);
      return;
    }
    super.onTileClicked(paramTile);
  }
  
  protected void setMainContentId(int paramInt)
  {
    this.mMainContentId = paramInt;
  }
  
  public void setResultIntentData(Intent paramIntent)
  {
    this.mResultIntentData = paramIntent;
  }
  
  public boolean shouldUpRecreateTask(Intent paramIntent)
  {
    return super.shouldUpRecreateTask(new Intent(this, SettingsActivity.class));
  }
  
  public void startPreferenceFragment(Fragment paramFragment, boolean paramBoolean)
  {
    FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
    localFragmentTransaction.replace(this.mMainContentId, paramFragment);
    if (paramBoolean)
    {
      localFragmentTransaction.setTransition(4097);
      localFragmentTransaction.addToBackStack(":settings:prefs");
    }
    for (;;)
    {
      localFragmentTransaction.commitAllowingStateLoss();
      return;
      localFragmentTransaction.setTransition(4099);
    }
  }
  
  public void startPreferencePanel(String paramString, Bundle paramBundle, int paramInt1, CharSequence paramCharSequence, Fragment paramFragment, int paramInt2)
  {
    String str = null;
    if (paramInt1 < 0) {
      if (paramCharSequence == null) {
        break label39;
      }
    }
    label39:
    for (str = paramCharSequence.toString();; str = "")
    {
      Utils.startWithFragment(this, paramString, paramBundle, paramFragment, paramInt2, paramInt1, str, this.mIsShortcut);
      return;
    }
  }
  
  public void startPreferencePanelAsUser(String paramString, Bundle paramBundle, int paramInt, CharSequence paramCharSequence, UserHandle paramUserHandle)
  {
    if (paramUserHandle.getIdentifier() == UserHandle.myUserId())
    {
      startPreferencePanel(paramString, paramBundle, paramInt, paramCharSequence, null, 0);
      return;
    }
    String str = null;
    if (paramInt < 0) {
      if (paramCharSequence == null) {
        break label60;
      }
    }
    label60:
    for (str = paramCharSequence.toString();; str = "")
    {
      Utils.startWithFragmentAsUser(this, paramString, paramBundle, paramInt, str, this.mIsShortcut, paramUserHandle);
      return;
    }
  }
  
  public void startSuggestion(Intent paramIntent)
  {
    this.mCurrentSuggestion = paramIntent.getComponent();
    startActivityForResult(paramIntent, 42);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */