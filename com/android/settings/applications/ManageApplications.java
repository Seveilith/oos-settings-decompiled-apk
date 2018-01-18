package com.android.settings.applications;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.icu.text.AlphabeticIndex;
import android.icu.text.AlphabeticIndex.Bucket;
import android.icu.text.AlphabeticIndex.ImmutableIndex;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.LocaleList;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.PreferenceFrameLayout;
import android.preference.PreferenceFrameLayout.LayoutParams;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.settings.AppHeader;
import com.android.settings.InstrumentedFragment;
import com.android.settings.Settings.AllApplicationsActivity;
import com.android.settings.Settings.BgOptimizeAppListActivity;
import com.android.settings.Settings.DisplaySizeAdaptionAppListActivity;
import com.android.settings.Settings.DomainsURLsAppListActivity;
import com.android.settings.Settings.HighPowerApplicationsActivity;
import com.android.settings.Settings.NotificationAppListActivity;
import com.android.settings.Settings.OverlaySettingsActivity;
import com.android.settings.Settings.StorageUseActivity;
import com.android.settings.Settings.UsageAccessSettingsActivity;
import com.android.settings.Settings.WriteSettingsActivity;
import com.android.settings.Utils;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.fuelgauge.HighPowerDetail;
import com.android.settings.fuelgauge.PowerWhitelistBackend;
import com.android.settings.notification.AppNotificationSettings;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.NotificationBackend.AppRow;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.AppFilter;
import com.android.settingslib.applications.ApplicationsState.Callbacks;
import com.android.settingslib.applications.ApplicationsState.CompoundFilter;
import com.android.settingslib.applications.ApplicationsState.Session;
import com.android.settingslib.applications.ApplicationsState.VolumeFilter;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.backgroundoptimize.AppBgOptimizeBridge;
import com.oneplus.settings.backgroundoptimize.BgOptimizeDetail;
import com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge;
import com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionDetail;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageApplications
  extends InstrumentedFragment
  implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener
{
  private static final int ADVANCED_SETTINGS = 2;
  public static final String APP_CHG = "chg";
  static final boolean DEBUG = Log.isLoggable("ManageApplications", 3);
  public static final String EXTRA_CLASSNAME = "classname";
  private static final String EXTRA_FILTER_MODE = "filterMode";
  private static final String EXTRA_HAS_BRIDGE = "hasBridge";
  private static final String EXTRA_HAS_ENTRIES = "hasEntries";
  private static final String EXTRA_SHOW_SYSTEM = "showSystem";
  private static final String EXTRA_SORT_ORDER = "sortOrder";
  public static final String EXTRA_VOLUME_NAME = "volumeName";
  public static final String EXTRA_VOLUME_UUID = "volumeUuid";
  public static final ApplicationsState.AppFilter[] FILTERS = { new ApplicationsState.CompoundFilter(AppStatePowerBridge.FILTER_POWER_WHITELISTED, ApplicationsState.FILTER_ALL_ENABLED), new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_WITHOUT_DISABLED_UNTIL_USED, ApplicationsState.FILTER_ALL_ENABLED), ApplicationsState.FILTER_EVERYTHING, ApplicationsState.FILTER_ALL_ENABLED, ApplicationsState.FILTER_DISABLED, AppStateNotificationBridge.FILTER_APP_NOTIFICATION_BLOCKED, AppStateNotificationBridge.FILTER_APP_NOTIFICATION_SILENCED, AppStateNotificationBridge.FILTER_APP_NOTIFICATION_HIDE_SENSITIVE, AppStateNotificationBridge.FILTER_APP_NOTIFICATION_HIDE_ALL, AppStateNotificationBridge.FILTER_APP_NOTIFICATION_PRIORITY, ApplicationsState.FILTER_PERSONAL, ApplicationsState.FILTER_WORK, ApplicationsState.FILTER_WITH_DOMAIN_URLS, AppStateUsageBridge.FILTER_APP_USAGE, AppStateOverlayBridge.FILTER_SYSTEM_ALERT_WINDOW, AppStateWriteSettingsBridge.FILTER_WRITE_SETTINGS, AppBgOptimizeBridge.FILTER_APP_BG_All, AppBgOptimizeBridge.FILTER_APP_BG_NOT_OPTIMIZE, DisplaySizeAdaptionBridge.FILTER_APP_All, DisplaySizeAdaptionBridge.FILTER_APP_FULL_SCREEN, DisplaySizeAdaptionBridge.FILTER_APP_ORIGINAL_SIZE };
  public static final int FILTER_APPS_ALL = 2;
  public static final int FILTER_APPS_BACKGROUND_OPTIMIZE_ALL = 16;
  public static final int FILTER_APPS_BACKGROUND_OPTIMIZE_NOT = 17;
  public static final int FILTER_APPS_BLOCKED = 5;
  public static final int FILTER_APPS_DISABLED = 4;
  public static final int FILTER_APPS_DISPLAY_SIZE_ADAPTION_ALL = 18;
  public static final int FILTER_APPS_DISPLAY_SIZE_ADAPTION_FULL_SCREEN = 19;
  public static final int FILTER_APPS_DISPLAY_SIZE_ADAPTION_ORIGINAL_SIZE = 20;
  public static final int FILTER_APPS_ENABLED = 3;
  public static final int FILTER_APPS_HIDE_NOTIFICATIONS = 8;
  public static final int FILTER_APPS_PERSONAL = 10;
  public static final int FILTER_APPS_POWER_WHITELIST = 0;
  public static final int FILTER_APPS_POWER_WHITELIST_ALL = 1;
  public static final int FILTER_APPS_PRIORITY = 9;
  public static final int FILTER_APPS_SENSITIVE = 7;
  public static final int FILTER_APPS_SILENT = 6;
  public static final int FILTER_APPS_USAGE_ACCESS = 13;
  public static final int FILTER_APPS_WITH_DOMAIN_URLS = 12;
  public static final int FILTER_APPS_WITH_OVERLAY = 14;
  public static final int FILTER_APPS_WORK = 11;
  public static final int FILTER_APPS_WRITE_SETTINGS = 15;
  public static final int[] FILTER_LABELS = { 2131693460, 2131693400, 2131693400, 2131693401, 2131692120, 2131693404, 2131690424, 2131693407, 2131693408, 2131693406, 2131693402, 2131693403, 2131693405, 2131693400, 2131693543, 2131693549, 2131693400, 2131690437, 2131693400, 2131690518, 2131690519 };
  private static final int INSTALLED_APP_DETAILS = 1;
  public static final int LIST_TYPE_BACKGROUND_OPTIMIZE = 8;
  public static final int LIST_TYPE_DISPLAY_SIZE_ADAPION = 9;
  public static final int LIST_TYPE_DOMAINS_URLS = 2;
  public static final int LIST_TYPE_HIGH_POWER = 5;
  public static final int LIST_TYPE_MAIN = 0;
  public static final int LIST_TYPE_NOTIFICATION = 1;
  public static final int LIST_TYPE_OVERLAY = 6;
  public static final int LIST_TYPE_STORAGE = 3;
  public static final int LIST_TYPE_USAGE_ACCESS = 4;
  public static final int LIST_TYPE_WRITE_SETTINGS = 7;
  public static final int SIZE_EXTERNAL = 2;
  public static final int SIZE_INTERNAL = 1;
  public static final int SIZE_TOTAL = 0;
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new ManageApplications.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader, null);
    }
  };
  static final String TAG = "ManageApplications";
  private int curPosition = 0;
  private View emptyView;
  public ApplicationsAdapter mApplications;
  private ApplicationsState mApplicationsState;
  private String mCurrentPkgName;
  private int mCurrentUid;
  public int mFilter;
  private FilterSpinnerAdapter mFilterAdapter;
  private Spinner mFilterSpinner;
  private boolean mFinishAfterDialog;
  private LayoutInflater mInflater;
  CharSequence mInvalidSizeStr;
  private View mListContainer;
  public int mListType;
  private ListView mListView;
  private View mLoadingContainer;
  private NotificationBackend mNotifBackend;
  private Menu mOptionsMenu;
  private ResetAppsHelper mResetAppsHelper;
  private View mRootView;
  private boolean mShowSystem;
  private int mSortOrder = 2131362846;
  private View mSpinnerHeader;
  private ExecutorService mThreadPool = Executors.newFixedThreadPool(5);
  private String mVolumeName;
  private String mVolumeUuid;
  
  private void createHeader()
  {
    Activity localActivity = getActivity();
    FrameLayout localFrameLayout = (FrameLayout)this.mRootView.findViewById(2131362092);
    this.mSpinnerHeader = ((ViewGroup)localActivity.getLayoutInflater().inflate(2130968621, localFrameLayout, false));
    this.mFilterSpinner = ((Spinner)this.mSpinnerHeader.findViewById(2131361969));
    this.mSpinnerHeader.findViewById(2131361970).setVisibility(8);
    this.mFilterAdapter = new FilterSpinnerAdapter(this);
    this.mFilterSpinner.setAdapter(this.mFilterAdapter);
    this.mFilterSpinner.setOnItemSelectedListener(this);
    localFrameLayout.addView(this.mSpinnerHeader, 0);
    this.mFilterAdapter.enableFilter(getDefaultFilter());
    int i;
    if (this.mListType == 0)
    {
      i = 0;
      if (!OPUtils.hasMultiAppProfiles(UserManager.get(getActivity()))) {
        break label344;
      }
      if (UserManager.get(getActivity()).getUserProfiles().size() > 2) {
        i = 1;
      }
    }
    for (;;)
    {
      if (i != 0)
      {
        this.mFilterAdapter.enableFilter(10);
        this.mFilterAdapter.enableFilter(11);
      }
      if (this.mListType == 1)
      {
        this.mFilterAdapter.enableFilter(5);
        this.mFilterAdapter.enableFilter(6);
        this.mFilterAdapter.enableFilter(7);
        this.mFilterAdapter.enableFilter(8);
        this.mFilterAdapter.enableFilter(9);
      }
      if (this.mListType == 5) {
        this.mFilterAdapter.enableFilter(1);
      }
      if (this.mListType == 3) {
        this.mApplications.setOverrideFilter(new ApplicationsState.VolumeFilter(this.mVolumeUuid));
      }
      if (this.mListType == 8)
      {
        this.mFilterAdapter.enableFilter(16);
        this.mFilterAdapter.enableFilter(17);
      }
      if (this.mListType == 9)
      {
        this.mFilterAdapter.enableFilter(18);
        this.mFilterAdapter.enableFilter(19);
        this.mFilterAdapter.enableFilter(20);
      }
      return;
      label344:
      if (UserManager.get(getActivity()).getUserProfiles().size() > 1) {
        i = 1;
      }
    }
  }
  
  private int getDefaultFilter()
  {
    switch (this.mListType)
    {
    case 3: 
    default: 
      return 2;
    case 2: 
      return 12;
    case 4: 
      return 13;
    case 5: 
      return 0;
    case 6: 
      return 14;
    case 7: 
      return 15;
    case 8: 
      return 16;
    }
    return 18;
  }
  
  private boolean isFastScrollEnabled()
  {
    boolean bool = false;
    switch (this.mListType)
    {
    case 2: 
    default: 
      return false;
    }
    if (this.mSortOrder == 2131362846) {
      bool = true;
    }
    return bool;
  }
  
  private void startAppInfoFragment(Class<?> paramClass, int paramInt)
  {
    AppInfoBase.startAppInfoFragment(paramClass, paramInt, this.mCurrentPkgName, this.mCurrentUid, this, 1);
  }
  
  private void startApplicationDetailsActivity()
  {
    switch (this.mListType)
    {
    default: 
      startAppInfoFragment(InstalledAppDetails.class, 2131692076);
    }
    do
    {
      Activity localActivity;
      do
      {
        return;
        startAppInfoFragment(AppNotificationSettings.class, 2131693222);
        return;
        startAppInfoFragment(AppLaunchSettings.class, 2131692078);
        return;
        startAppInfoFragment(UsageAccessDetails.class, 2131693444);
        return;
        startAppInfoFragment(AppStorageSettings.class, 2131691689);
        return;
        HighPowerDetail.show(this, this.mCurrentPkgName, 1, this.mFinishAfterDialog);
        return;
        startAppInfoFragment(DrawOverlayDetails.class, 2131693541);
        return;
        startAppInfoFragment(WriteSettingsDetails.class, 2131693551);
        return;
        localActivity = getActivity();
      } while ((localActivity == null) || (localActivity.isFinishing()));
      BgOptimizeDetail.show(this, this.mCurrentPkgName, 1, this.mFinishAfterDialog);
      return;
    } while ((getActivity() == null) || (getActivity().isFinishing()));
    DisplaySizeAdaptionDetail.show(this, this.mCurrentPkgName, 1, this.mFinishAfterDialog);
  }
  
  protected int getMetricsCategory()
  {
    switch (this.mListType)
    {
    default: 
      return 0;
    case 0: 
      return 65;
    case 1: 
      return 133;
    case 2: 
      return 143;
    case 3: 
      return 182;
    case 4: 
      return 95;
    case 5: 
      return 184;
    case 6: 
      return 221;
    case 7: 
      return 221;
    case 8: 
      return 65;
    }
    return 65;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramInt1 == 1) && (this.mCurrentPkgName != null))
    {
      if (this.mListType == 1)
      {
        ApplicationsAdapter.-get1(this.mApplications).forceUpdate(this.mCurrentPkgName, this.mCurrentUid);
        return;
      }
      if ((this.mListType == 5) || (this.mListType == 6)) {}
      for (;;)
      {
        if (!this.mFinishAfterDialog) {
          break label101;
        }
        getActivity().onBackPressed();
        return;
        if ((this.mListType != 7) && (this.mListType != 8)) {
          if (this.mListType != 9) {
            break;
          }
        }
      }
      label101:
      ApplicationsAdapter.-get1(this.mApplications).forceUpdate(this.mCurrentPkgName, this.mCurrentUid);
      return;
    }
    this.mApplicationsState.requestSize(this.mCurrentPkgName, UserHandle.getUserId(this.mCurrentUid));
  }
  
  public void onCreate(Bundle paramBundle)
  {
    String str1 = null;
    super.onCreate(paramBundle);
    setHasOptionsMenu(true);
    this.mApplicationsState = ApplicationsState.getInstance(getActivity().getApplication());
    Intent localIntent = getActivity().getIntent();
    Bundle localBundle = getArguments();
    if (localBundle != null) {
      str1 = localBundle.getString("classname");
    }
    String str2 = str1;
    if (str1 == null) {
      str2 = localIntent.getComponent().getClassName();
    }
    if (str2.equals(Settings.AllApplicationsActivity.class.getName()))
    {
      this.mShowSystem = true;
      if (paramBundle == null) {
        break label432;
      }
      this.mSortOrder = paramBundle.getInt("sortOrder", this.mSortOrder);
      this.mShowSystem = paramBundle.getBoolean("showSystem", this.mShowSystem);
    }
    label432:
    for (this.mFilter = paramBundle.getInt("filterMode", this.mFilter);; this.mFilter = getDefaultFilter())
    {
      this.mInvalidSizeStr = getActivity().getText(2131692150);
      this.mResetAppsHelper = new ResetAppsHelper(getActivity());
      return;
      if (str2.equals(Settings.NotificationAppListActivity.class.getName()))
      {
        this.mListType = 1;
        this.mNotifBackend = new NotificationBackend();
        break;
      }
      if (str2.equals(Settings.DomainsURLsAppListActivity.class.getName()))
      {
        this.mListType = 2;
        break;
      }
      if (str2.equals(Settings.StorageUseActivity.class.getName()))
      {
        if ((localBundle != null) && (localBundle.containsKey("volumeUuid")))
        {
          this.mVolumeUuid = localBundle.getString("volumeUuid");
          this.mVolumeName = localBundle.getString("volumeName");
        }
        for (this.mListType = 3;; this.mListType = 0)
        {
          this.mSortOrder = 2131362847;
          break;
        }
      }
      if (str2.equals(Settings.UsageAccessSettingsActivity.class.getName()))
      {
        this.mListType = 4;
        break;
      }
      if (str2.equals(Settings.HighPowerApplicationsActivity.class.getName()))
      {
        this.mListType = 5;
        this.mShowSystem = true;
        break;
      }
      if (str2.equals(Settings.OverlaySettingsActivity.class.getName()))
      {
        this.mListType = 6;
        break;
      }
      if (str2.equals(Settings.WriteSettingsActivity.class.getName()))
      {
        this.mListType = 7;
        break;
      }
      if (str2.equals(Settings.BgOptimizeAppListActivity.class.getName()))
      {
        this.mListType = 8;
        break;
      }
      if (str2.equals(Settings.DisplaySizeAdaptionAppListActivity.class.getName()))
      {
        this.mListType = 9;
        break;
      }
      this.mListType = 0;
      break;
    }
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if ((this.mListType == 2) || (this.mListType == 9)) {
      return;
    }
    Activity localActivity = getActivity();
    if (this.mListType == 0) {}
    for (int i = 2131693006;; i = 2131693005)
    {
      HelpUtils.prepareHelpMenuItem(localActivity, paramMenu, i, getClass().getName());
      this.mOptionsMenu = paramMenu;
      paramMenuInflater.inflate(2132017154, paramMenu);
      updateOptionsMenu();
      return;
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mInflater = paramLayoutInflater;
    this.mRootView = paramLayoutInflater.inflate(2130968746, null);
    this.mLoadingContainer = this.mRootView.findViewById(2131362189);
    this.mLoadingContainer.setVisibility(0);
    this.mListContainer = this.mRootView.findViewById(2131362198);
    if (this.mListContainer != null)
    {
      this.emptyView = this.mListContainer.findViewById(16908292);
      paramLayoutInflater = (ListView)this.mListContainer.findViewById(16908298);
      if (this.emptyView != null) {
        paramLayoutInflater.setEmptyView(this.emptyView);
      }
      paramLayoutInflater.setOnItemClickListener(this);
      paramLayoutInflater.setSaveEnabled(true);
      paramLayoutInflater.setItemsCanFocus(true);
      paramLayoutInflater.setTextFilterEnabled(true);
      this.mListView = paramLayoutInflater;
      paramLayoutInflater = new TextView(SettingsBaseApplication.mApplication);
      paramLayoutInflater.setLayoutParams(new AbsListView.LayoutParams(-1, getResources().getDimensionPixelSize(2131755328)));
      this.mListView.addHeaderView(paramLayoutInflater);
      this.mApplications = new ApplicationsAdapter(this.mApplicationsState, this, this.mFilter);
      if (paramBundle != null)
      {
        ApplicationsAdapter.-set2(this.mApplications, paramBundle.getBoolean("hasEntries", false));
        ApplicationsAdapter.-set1(this.mApplications, paramBundle.getBoolean("hasBridge", false));
      }
      this.mListView.setAdapter(this.mApplications);
      this.mListView.setRecyclerListener(this.mApplications);
      this.mListView.setFastScrollEnabled(isFastScrollEnabled());
      Utils.prepareCustomPreferencesList(paramViewGroup, this.mRootView, this.mListView, false);
    }
    if ((paramViewGroup instanceof PreferenceFrameLayout)) {
      ((PreferenceFrameLayout.LayoutParams)this.mRootView.getLayoutParams()).removeBorders = true;
    }
    createHeader();
    this.mResetAppsHelper.onRestoreInstanceState(paramBundle);
    return this.mRootView;
  }
  
  public void onDestroy()
  {
    if (this.mThreadPool != null) {
      this.mThreadPool.shutdownNow();
    }
    super.onDestroy();
  }
  
  public void onDestroyOptionsMenu()
  {
    this.mOptionsMenu = null;
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    if (this.mApplications != null) {
      this.mApplications.release();
    }
    this.mRootView = null;
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    paramInt = Math.max(0, paramInt - 1);
    if ((this.mApplications != null) && (this.mApplications.getCount() > paramInt))
    {
      paramAdapterView = this.mApplications.getAppEntry(paramInt);
      this.mCurrentPkgName = paramAdapterView.info.packageName;
      this.mCurrentUid = paramAdapterView.info.uid;
      startApplicationDetailsActivity();
    }
  }
  
  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    if ((paramInt == 0) && (this.curPosition == paramInt) && (this.mFilter != 2))
    {
      this.curPosition = paramInt;
      return;
    }
    this.curPosition = paramInt;
    this.mFilter = this.mFilterAdapter.getFilter(paramInt);
    this.mApplications.setFilter(this.mFilter);
    if ((this.mFilter != 2) && (this.emptyView != null)) {
      this.emptyView.setVisibility(0);
    }
    if (this.mListView.getCount() != 0) {
      this.emptyView.setVisibility(4);
    }
    if (DEBUG) {
      Log.d("ManageApplications", "Selecting filter " + this.mFilter);
    }
  }
  
  public void onNothingSelected(AdapterView<?> paramAdapterView) {}
  
  /* Error */
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 847 1 0
    //   6: istore_2
    //   7: aload_1
    //   8: invokeinterface 847 1 0
    //   13: lookupswitch	default:+75->88, 16908332:+214->227, 2131362843:+156->169, 2131362844:+117->130, 2131362845:+117->130, 2131362846:+77->90, 2131362847:+77->90, 2131362848:+147->160, 2131362849:+223->236
    //   88: iconst_0
    //   89: ireturn
    //   90: aload_0
    //   91: iload_2
    //   92: putfield 336	com/android/settings/applications/ManageApplications:mSortOrder	I
    //   95: aload_0
    //   96: getfield 191	com/android/settings/applications/ManageApplications:mListView	Landroid/widget/ListView;
    //   99: aload_0
    //   100: invokespecial 736	com/android/settings/applications/ManageApplications:isFastScrollEnabled	()Z
    //   103: invokevirtual 739	android/widget/ListView:setFastScrollEnabled	(Z)V
    //   106: aload_0
    //   107: getfield 432	com/android/settings/applications/ManageApplications:mApplications	Lcom/android/settings/applications/ManageApplications$ApplicationsAdapter;
    //   110: ifnull +14 -> 124
    //   113: aload_0
    //   114: getfield 432	com/android/settings/applications/ManageApplications:mApplications	Lcom/android/settings/applications/ManageApplications$ApplicationsAdapter;
    //   117: aload_0
    //   118: getfield 336	com/android/settings/applications/ManageApplications:mSortOrder	I
    //   121: invokevirtual 850	com/android/settings/applications/ManageApplications$ApplicationsAdapter:rebuild	(I)V
    //   124: aload_0
    //   125: invokevirtual 654	com/android/settings/applications/ManageApplications:updateOptionsMenu	()V
    //   128: iconst_1
    //   129: ireturn
    //   130: aload_0
    //   131: getfield 202	com/android/settings/applications/ManageApplications:mShowSystem	Z
    //   134: ifeq +21 -> 155
    //   137: iconst_0
    //   138: istore_3
    //   139: aload_0
    //   140: iload_3
    //   141: putfield 202	com/android/settings/applications/ManageApplications:mShowSystem	Z
    //   144: aload_0
    //   145: getfield 432	com/android/settings/applications/ManageApplications:mApplications	Lcom/android/settings/applications/ManageApplications$ApplicationsAdapter;
    //   148: iconst_0
    //   149: invokevirtual 852	com/android/settings/applications/ManageApplications$ApplicationsAdapter:rebuild	(Z)V
    //   152: goto -28 -> 124
    //   155: iconst_1
    //   156: istore_3
    //   157: goto -18 -> 139
    //   160: aload_0
    //   161: getfield 602	com/android/settings/applications/ManageApplications:mResetAppsHelper	Lcom/android/settings/applications/ResetAppsHelper;
    //   164: invokevirtual 855	com/android/settings/applications/ResetAppsHelper:buildResetDialog	()V
    //   167: iconst_1
    //   168: ireturn
    //   169: aload_0
    //   170: getfield 409	com/android/settings/applications/ManageApplications:mListType	I
    //   173: iconst_1
    //   174: if_icmpne +28 -> 202
    //   177: aload_0
    //   178: invokevirtual 349	com/android/settings/applications/ManageApplications:getActivity	()Landroid/app/Activity;
    //   181: checkcast 857	com/android/settings/SettingsActivity
    //   184: ldc_w 859
    //   187: invokevirtual 572	java/lang/Class:getName	()Ljava/lang/String;
    //   190: aconst_null
    //   191: ldc_w 860
    //   194: aconst_null
    //   195: aload_0
    //   196: iconst_2
    //   197: invokevirtual 864	com/android/settings/SettingsActivity:startPreferencePanel	(Ljava/lang/String;Landroid/os/Bundle;ILjava/lang/CharSequence;Landroid/app/Fragment;I)V
    //   200: iconst_1
    //   201: ireturn
    //   202: aload_0
    //   203: invokevirtual 349	com/android/settings/applications/ManageApplications:getActivity	()Landroid/app/Activity;
    //   206: checkcast 857	com/android/settings/SettingsActivity
    //   209: ldc_w 866
    //   212: invokevirtual 572	java/lang/Class:getName	()Ljava/lang/String;
    //   215: aconst_null
    //   216: ldc_w 867
    //   219: aconst_null
    //   220: aload_0
    //   221: iconst_2
    //   222: invokevirtual 864	com/android/settings/SettingsActivity:startPreferencePanel	(Ljava/lang/String;Landroid/os/Bundle;ILjava/lang/CharSequence;Landroid/app/Fragment;I)V
    //   225: iconst_1
    //   226: ireturn
    //   227: aload_0
    //   228: invokevirtual 349	com/android/settings/applications/ManageApplications:getActivity	()Landroid/app/Activity;
    //   231: invokevirtual 870	android/app/Activity:finish	()V
    //   234: iconst_1
    //   235: ireturn
    //   236: aconst_null
    //   237: astore_1
    //   238: new 555	android/content/Intent
    //   241: dup
    //   242: ldc_w 872
    //   245: invokespecial 873	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   248: astore 4
    //   250: aload 4
    //   252: ldc 47
    //   254: ldc_w 875
    //   257: invokevirtual 572	java/lang/Class:getName	()Ljava/lang/String;
    //   260: invokevirtual 879	android/content/Intent:putExtra	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   263: pop
    //   264: aload_0
    //   265: invokevirtual 349	com/android/settings/applications/ManageApplications:getActivity	()Landroid/app/Activity;
    //   268: aload 4
    //   270: invokevirtual 883	android/app/Activity:startActivity	(Landroid/content/Intent;)V
    //   273: iconst_1
    //   274: ireturn
    //   275: astore 4
    //   277: ldc -124
    //   279: new 818	java/lang/StringBuilder
    //   282: dup
    //   283: invokespecial 819	java/lang/StringBuilder:<init>	()V
    //   286: ldc_w 885
    //   289: invokevirtual 825	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   292: aload_1
    //   293: invokevirtual 888	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   296: invokevirtual 831	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   299: invokestatic 835	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   302: pop
    //   303: iconst_1
    //   304: ireturn
    //   305: astore_1
    //   306: aload 4
    //   308: astore_1
    //   309: goto -32 -> 277
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	312	0	this	ManageApplications
    //   0	312	1	paramMenuItem	MenuItem
    //   6	86	2	i	int
    //   138	19	3	bool	boolean
    //   248	21	4	localIntent	Intent
    //   275	32	4	localActivityNotFoundException	android.content.ActivityNotFoundException
    // Exception table:
    //   from	to	target	type
    //   238	250	275	android/content/ActivityNotFoundException
    //   250	273	305	android/content/ActivityNotFoundException
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.mApplications != null) {
      this.mApplications.pause();
    }
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    updateOptionsMenu();
  }
  
  public void onResume()
  {
    super.onResume();
    updateView();
    updateOptionsMenu();
    if (this.mApplications != null)
    {
      this.mApplications.resume(this.mSortOrder);
      ApplicationsAdapter.-wrap2(this.mApplications);
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    this.mResetAppsHelper.onSaveInstanceState(paramBundle);
    paramBundle.putInt("sortOrder", this.mSortOrder);
    paramBundle.putBoolean("showSystem", this.mShowSystem);
    paramBundle.putBoolean("hasEntries", ApplicationsAdapter.-get3(this.mApplications));
    paramBundle.putBoolean("hasBridge", ApplicationsAdapter.-get2(this.mApplications));
    paramBundle.putInt("filterMode", this.mFilter);
  }
  
  public void onStop()
  {
    super.onStop();
    this.mResetAppsHelper.stop();
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    if (this.mListType == 3)
    {
      paramView = (FrameLayout)this.mRootView.findViewById(2131362092);
      AppHeader.createAppHeader(getActivity(), null, this.mVolumeName, null, -1, paramView);
    }
  }
  
  public void setHasDisabled(boolean paramBoolean)
  {
    if (this.mListType != 0) {
      return;
    }
    this.mFilterAdapter.setFilterEnabled(3, paramBoolean);
    this.mFilterAdapter.setFilterEnabled(4, paramBoolean);
  }
  
  void updateOptionsMenu()
  {
    boolean bool2 = true;
    if (this.mOptionsMenu == null) {
      return;
    }
    MenuItem localMenuItem = this.mOptionsMenu.findItem(2131362843);
    if (this.mListType == 1)
    {
      bool1 = true;
      localMenuItem.setVisible(bool1);
      localMenuItem = this.mOptionsMenu.findItem(2131362846);
      if (this.mListType != 3) {
        break label289;
      }
      if (this.mSortOrder == 2131362846) {
        break label284;
      }
      bool1 = true;
      label74:
      localMenuItem.setVisible(bool1);
      localMenuItem = this.mOptionsMenu.findItem(2131362847);
      if (this.mListType != 3) {
        break label299;
      }
      if (this.mSortOrder == 2131362847) {
        break label294;
      }
      bool1 = true;
      label115:
      localMenuItem.setVisible(bool1);
      localMenuItem = this.mOptionsMenu.findItem(2131362844);
      if (this.mShowSystem) {
        break label309;
      }
      if (this.mListType == 5) {
        break label304;
      }
      bool1 = true;
      label153:
      localMenuItem.setVisible(bool1);
      localMenuItem = this.mOptionsMenu.findItem(2131362845);
      if (!this.mShowSystem) {
        break label319;
      }
      if (this.mListType == 5) {
        break label314;
      }
      bool1 = true;
      label191:
      localMenuItem.setVisible(bool1);
      localMenuItem = this.mOptionsMenu.findItem(2131362849);
      if (this.mListType != 8) {
        break label324;
      }
    }
    label284:
    label289:
    label294:
    label299:
    label304:
    label309:
    label314:
    label319:
    label324:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      localMenuItem.setVisible(bool1);
      if (this.mListType == 8)
      {
        this.mOptionsMenu.findItem(2131362844).setVisible(false);
        this.mOptionsMenu.findItem(2131362848).setVisible(false);
      }
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label74;
      bool1 = false;
      break label74;
      bool1 = false;
      break label115;
      bool1 = false;
      break label115;
      bool1 = false;
      break label153;
      bool1 = false;
      break label153;
      bool1 = false;
      break label191;
      bool1 = false;
      break label191;
    }
  }
  
  public void updateView()
  {
    updateOptionsMenu();
    Activity localActivity = getActivity();
    if (localActivity != null) {
      localActivity.invalidateOptionsMenu();
    }
  }
  
  class ApplicationsAdapter
    extends BaseAdapter
    implements Filterable, ApplicationsState.Callbacks, AppStateBaseBridge.Callback, AbsListView.RecyclerListener, SectionIndexer
  {
    private final ManageApplications.SectionInfo[] EMPTY_SECTIONS = new ManageApplications.SectionInfo[0];
    private final ArrayList<View> mActive = new ArrayList();
    private ArrayList<ApplicationsState.AppEntry> mBaseEntries;
    private final Handler mBgHandler;
    private final Context mContext;
    CharSequence mCurFilterPrefix;
    private Map<String, String> mDomainsSymmaryCaches = new HashMap();
    private ArrayList<ApplicationsState.AppEntry> mEntries;
    private final AppStateBaseBridge mExtraInfoBridge;
    private final Handler mFgHandler;
    private Filter mFilter = new Filter()
    {
      protected Filter.FilterResults performFiltering(CharSequence paramAnonymousCharSequence)
      {
        paramAnonymousCharSequence = ManageApplications.ApplicationsAdapter.this.applyPrefixFilter(paramAnonymousCharSequence, ManageApplications.ApplicationsAdapter.-get0(ManageApplications.ApplicationsAdapter.this));
        Filter.FilterResults localFilterResults = new Filter.FilterResults();
        localFilterResults.values = paramAnonymousCharSequence;
        localFilterResults.count = paramAnonymousCharSequence.size();
        return localFilterResults;
      }
      
      protected void publishResults(CharSequence paramAnonymousCharSequence, Filter.FilterResults paramAnonymousFilterResults)
      {
        ManageApplications.ApplicationsAdapter.this.mCurFilterPrefix = paramAnonymousCharSequence;
        ManageApplications.ApplicationsAdapter.-set0(ManageApplications.ApplicationsAdapter.this, (ArrayList)paramAnonymousFilterResults.values);
        ManageApplications.ApplicationsAdapter.-wrap1(ManageApplications.ApplicationsAdapter.this);
        ManageApplications.ApplicationsAdapter.this.notifyDataSetChanged();
      }
    };
    private int mFilterMode;
    private boolean mHasReceivedBridgeCallback;
    private boolean mHasReceivedLoadEntries;
    private AlphabeticIndex.ImmutableIndex mIndex;
    private int mLastSortMode = -1;
    private final ManageApplications mManageApplications;
    private ApplicationsState.AppFilter mOverrideFilter;
    private PackageManager mPm;
    private int[] mPositionToSectionIndex;
    private boolean mResumed;
    private ManageApplications.SectionInfo[] mSections = this.EMPTY_SECTIONS;
    private final ApplicationsState.Session mSession;
    private final ApplicationsState mState;
    private int mWhichSize = 0;
    
    public ApplicationsAdapter(ApplicationsState paramApplicationsState, ManageApplications paramManageApplications, int paramInt)
    {
      this.mState = paramApplicationsState;
      this.mFgHandler = new Handler();
      this.mBgHandler = new Handler(this.mState.getBackgroundLooper());
      this.mSession = paramApplicationsState.newSession(this);
      this.mManageApplications = paramManageApplications;
      this.mContext = paramManageApplications.getActivity();
      this.mPm = this.mContext.getPackageManager();
      this.mFilterMode = paramInt;
      if (this.mManageApplications.mListType == 1)
      {
        this.mExtraInfoBridge = new AppStateNotificationBridge(this.mContext, this.mState, this, ManageApplications.-get6(paramManageApplications));
        return;
      }
      if (this.mManageApplications.mListType == 4)
      {
        this.mExtraInfoBridge = new AppStateUsageBridge(this.mContext, this.mState, this);
        return;
      }
      if (this.mManageApplications.mListType == 5)
      {
        this.mExtraInfoBridge = new AppStatePowerBridge(this.mState, this);
        return;
      }
      if (this.mManageApplications.mListType == 6)
      {
        this.mExtraInfoBridge = new AppStateOverlayBridge(this.mContext, this.mState, this);
        return;
      }
      if (this.mManageApplications.mListType == 7)
      {
        this.mExtraInfoBridge = new AppStateWriteSettingsBridge(this.mContext, this.mState, this);
        return;
      }
      if (this.mManageApplications.mListType == 8)
      {
        this.mExtraInfoBridge = new AppBgOptimizeBridge(this.mContext, this.mState, this);
        return;
      }
      if (this.mManageApplications.mListType == 9)
      {
        this.mExtraInfoBridge = new DisplaySizeAdaptionBridge(this.mContext, this.mState, this);
        return;
      }
      this.mExtraInfoBridge = null;
    }
    
    private CharSequence getDomainsSummary(String paramString)
    {
      if (this.mPm.getIntentVerificationStatusAsUser(paramString, UserHandle.myUserId()) == 3) {
        return this.mContext.getString(2131693419);
      }
      paramString = Utils.getHandledDomains(this.mPm, paramString);
      if (paramString.size() == 0) {
        return this.mContext.getString(2131693419);
      }
      if (paramString.size() == 1) {
        return this.mContext.getString(2131693420, new Object[] { paramString.valueAt(0) });
      }
      return this.mContext.getString(2131693421, new Object[] { paramString.valueAt(0) });
    }
    
    private boolean packageNameEquals(PackageItemInfo paramPackageItemInfo1, PackageItemInfo paramPackageItemInfo2)
    {
      if ((paramPackageItemInfo1 == null) || (paramPackageItemInfo2 == null)) {
        return false;
      }
      if ((paramPackageItemInfo1.packageName == null) || (paramPackageItemInfo2.packageName == null)) {
        return false;
      }
      return paramPackageItemInfo1.packageName.equals(paramPackageItemInfo2.packageName);
    }
    
    private void rebuildSections()
    {
      if ((this.mEntries != null) && (ManageApplications.-get4(this.mManageApplications).isFastScrollEnabled()))
      {
        Object localObject2;
        Object localObject1;
        if (this.mIndex == null)
        {
          localObject2 = this.mContext.getResources().getConfiguration().getLocales();
          localObject1 = localObject2;
          if (((LocaleList)localObject2).size() == 0) {
            localObject1 = new LocaleList(new Locale[] { Locale.ENGLISH });
          }
          localObject2 = new AlphabeticIndex(((LocaleList)localObject1).get(0));
          j = ((LocaleList)localObject1).size();
          i = 1;
          while (i < j)
          {
            ((AlphabeticIndex)localObject2).addLabels(new Locale[] { ((LocaleList)localObject1).get(i) });
            i += 1;
          }
          ((AlphabeticIndex)localObject2).addLabels(new Locale[] { Locale.ENGLISH });
          this.mIndex = ((AlphabeticIndex)localObject2).buildImmutableIndex();
        }
        ArrayList localArrayList = new ArrayList();
        int j = -1;
        int n = this.mEntries.size();
        this.mPositionToSectionIndex = new int[n];
        int i = 0;
        while (i < n)
        {
          localObject2 = ((ApplicationsState.AppEntry)this.mEntries.get(i)).label;
          AlphabeticIndex.ImmutableIndex localImmutableIndex = this.mIndex;
          localObject1 = localObject2;
          if (TextUtils.isEmpty((CharSequence)localObject2)) {
            localObject1 = "";
          }
          int m = localImmutableIndex.getBucketIndex((CharSequence)localObject1);
          int k = j;
          if (m != j)
          {
            k = m;
            localArrayList.add(new ManageApplications.SectionInfo(this.mIndex.getBucket(m).getLabel(), i));
          }
          this.mPositionToSectionIndex[i] = (localArrayList.size() - 1);
          i += 1;
          j = k;
        }
        this.mSections = ((ManageApplications.SectionInfo[])localArrayList.toArray(this.EMPTY_SECTIONS));
        return;
      }
      this.mSections = this.EMPTY_SECTIONS;
      this.mPositionToSectionIndex = null;
    }
    
    private ArrayList<ApplicationsState.AppEntry> removeDuplicateIgnoringUser(ArrayList<ApplicationsState.AppEntry> paramArrayList)
    {
      int j = paramArrayList.size();
      ArrayList localArrayList = new ArrayList(j);
      Object localObject = null;
      int i = 0;
      while (i < j)
      {
        ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)paramArrayList.get(i);
        ApplicationInfo localApplicationInfo = localAppEntry.info;
        if (!packageNameEquals((PackageItemInfo)localObject, localAppEntry.info)) {
          localArrayList.add(localAppEntry);
        }
        localObject = localApplicationInfo;
        i += 1;
      }
      localArrayList.trimToSize();
      return localArrayList;
    }
    
    private void updateLoading()
    {
      View localView1 = ManageApplications.-get5(this.mManageApplications);
      View localView2 = ManageApplications.-get3(this.mManageApplications);
      if ((this.mHasReceivedLoadEntries) && (this.mSession.getAllApps().size() != 0)) {}
      for (boolean bool = true;; bool = false)
      {
        Utils.handleLoadingContainer(localView1, localView2, bool, false);
        return;
      }
    }
    
    private void updateSummary(AppViewHolder paramAppViewHolder)
    {
      switch (this.mManageApplications.mListType)
      {
      case 3: 
      default: 
        paramAppViewHolder.updateSizeText(this.mManageApplications.mInvalidSizeStr, this.mWhichSize);
        return;
      }
      try
      {
        if (paramAppViewHolder.entry.extraInfo != null)
        {
          paramAppViewHolder.summary.setText(InstalledAppDetails.getNotificationSummary((NotificationBackend.AppRow)paramAppViewHolder.entry.extraInfo, this.mContext));
          return;
        }
        paramAppViewHolder.summary.setText(null);
        return;
      }
      catch (ClassCastException paramAppViewHolder) {}
      Object localObject = paramAppViewHolder.entry.info.packageName;
      paramAppViewHolder.summary.setTag(localObject);
      String str = getSummaryFromCache((String)localObject);
      if (str == null)
      {
        new LoadSummaryAsyncTask((String)localObject).executeOnExecutor(ManageApplications.-get9(ManageApplications.this), new String[] { localObject });
        return;
      }
      paramAppViewHolder.summary.setText(str);
      return;
      if (paramAppViewHolder.entry.extraInfo != null)
      {
        localObject = paramAppViewHolder.summary;
        if (new AppStateUsageBridge.UsageState((AppStateAppOpsBridge.PermissionState)paramAppViewHolder.entry.extraInfo).isPermissible()) {}
        for (int i = 2131693335;; i = 2131693336)
        {
          ((TextView)localObject).setText(i);
          return;
        }
      }
      paramAppViewHolder.summary.setText(null);
      return;
      paramAppViewHolder.summary.setText(HighPowerDetail.getSummary(this.mContext, paramAppViewHolder.entry));
      return;
      paramAppViewHolder.summary.setText(DrawOverlayDetails.getSummary(this.mContext, paramAppViewHolder.entry));
      return;
      paramAppViewHolder.summary.setText(WriteSettingsDetails.getSummary(this.mContext, paramAppViewHolder.entry));
      return;
      paramAppViewHolder.summary.setText(BgOptimizeDetail.getSummary(this.mContext, paramAppViewHolder.entry));
      return;
      paramAppViewHolder.summary.setText(DisplaySizeAdaptionDetail.getSummary(this.mContext, paramAppViewHolder.entry));
      return;
    }
    
    public void addSummaryToCache(String paramString1, String paramString2)
    {
      if ((this.mDomainsSymmaryCaches != null) && (getSummaryFromCache(paramString1) == null)) {
        this.mDomainsSymmaryCaches.put(paramString1, paramString2);
      }
    }
    
    ArrayList<ApplicationsState.AppEntry> applyPrefixFilter(CharSequence paramCharSequence, ArrayList<ApplicationsState.AppEntry> paramArrayList)
    {
      if ((paramCharSequence == null) || (paramCharSequence.length() == 0)) {
        return paramArrayList;
      }
      paramCharSequence = ApplicationsState.normalize(paramCharSequence.toString());
      String str1 = " " + paramCharSequence;
      ArrayList localArrayList = new ArrayList();
      int i = 0;
      while (i < paramArrayList.size())
      {
        ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)paramArrayList.get(i);
        String str2 = localAppEntry.getNormalizedLabel();
        if ((str2.startsWith(paramCharSequence)) || (str2.indexOf(str1) != -1)) {
          localArrayList.add(localAppEntry);
        }
        i += 1;
      }
      return localArrayList;
    }
    
    public boolean areAllItemsEnabled()
    {
      return false;
    }
    
    public ApplicationsState.AppEntry getAppEntry(int paramInt)
    {
      return (ApplicationsState.AppEntry)this.mEntries.get(paramInt);
    }
    
    public int getCount()
    {
      if (this.mEntries != null) {
        return this.mEntries.size();
      }
      return 0;
    }
    
    public Filter getFilter()
    {
      return this.mFilter;
    }
    
    public Object getItem(int paramInt)
    {
      return this.mEntries.get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return ((ApplicationsState.AppEntry)this.mEntries.get(paramInt)).id;
    }
    
    public int getPositionForSection(int paramInt)
    {
      return this.mSections[paramInt].position;
    }
    
    public int getSectionForPosition(int paramInt)
    {
      return this.mPositionToSectionIndex[paramInt];
    }
    
    public Object[] getSections()
    {
      return this.mSections;
    }
    
    public String getSummaryFromCache(String paramString)
    {
      if (this.mDomainsSymmaryCaches != null) {
        return (String)this.mDomainsSymmaryCaches.get(paramString);
      }
      return null;
    }
    
    public View getView(int paramInt, View arg2, ViewGroup paramViewGroup)
    {
      paramViewGroup = AppViewHolder.createOrRecycle(ManageApplications.-get2(this.mManageApplications), ???);
      View localView = paramViewGroup.rootView;
      for (;;)
      {
        synchronized ((ApplicationsState.AppEntry)this.mEntries.get(paramInt))
        {
          paramViewGroup.entry = ???;
          if (???.label != null) {
            paramViewGroup.appName.setText(???.label);
          }
          this.mState.ensureIcon(???);
          if (???.icon != null) {
            paramViewGroup.appIcon.setImageDrawable(???.icon);
          }
          updateSummary(paramViewGroup);
          if ((???.info.flags & 0x800000) == 0)
          {
            paramViewGroup.disabled.setVisibility(0);
            paramViewGroup.disabled.setText(2131692125);
            this.mActive.remove(localView);
            this.mActive.add(localView);
            localView.setEnabled(isEnabled(paramInt));
            return localView;
          }
          if (!???.info.enabled)
          {
            paramViewGroup.disabled.setVisibility(0);
            paramViewGroup.disabled.setText(2131692124);
          }
        }
        paramViewGroup.disabled.setVisibility(8);
      }
    }
    
    public boolean isEnabled(int paramInt)
    {
      boolean bool = true;
      if (this.mManageApplications.mListType != 5) {
        return true;
      }
      ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)this.mEntries.get(paramInt);
      if (PowerWhitelistBackend.getInstance().isSysWhitelisted(localAppEntry.info.packageName)) {
        bool = false;
      }
      return bool;
    }
    
    public void onAllSizesComputed()
    {
      if (this.mLastSortMode == 2131362847) {
        rebuild(false);
      }
    }
    
    public void onExtraInfoUpdated()
    {
      this.mHasReceivedBridgeCallback = true;
      rebuild(false);
    }
    
    public void onLauncherInfoChanged()
    {
      if (!ManageApplications.-get7(this.mManageApplications)) {
        rebuild(false);
      }
    }
    
    public void onLoadEntriesCompleted()
    {
      this.mHasReceivedLoadEntries = true;
      rebuild(false);
    }
    
    public void onMovedToScrapHeap(View paramView)
    {
      this.mActive.remove(paramView);
    }
    
    public void onPackageIconChanged() {}
    
    public void onPackageListChanged()
    {
      rebuild(false);
    }
    
    public void onPackageSizeChanged(String arg1)
    {
      int i = 0;
      while (i < this.mActive.size())
      {
        AppViewHolder localAppViewHolder = (AppViewHolder)((View)this.mActive.get(i)).getTag();
        if (localAppViewHolder.entry.info.packageName.equals(???)) {
          synchronized (localAppViewHolder.entry)
          {
            updateSummary(localAppViewHolder);
            if ((localAppViewHolder.entry.info.packageName.equals(ManageApplications.-get0(this.mManageApplications))) && (this.mLastSortMode == 2131362847)) {
              rebuild(false);
            }
            return;
          }
        }
        i += 1;
      }
    }
    
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> paramArrayList)
    {
      Object localObject;
      if (this.mFilterMode != 0)
      {
        localObject = paramArrayList;
        if (this.mFilterMode != 1) {}
      }
      else
      {
        localObject = removeDuplicateIgnoringUser(paramArrayList);
      }
      this.mBaseEntries = ((ArrayList)localObject);
      if (this.mBaseEntries != null)
      {
        this.mEntries = applyPrefixFilter(this.mCurFilterPrefix, this.mBaseEntries);
        rebuildSections();
      }
      for (;;)
      {
        notifyDataSetChanged();
        if ((this.mSession.getAllApps().size() != 0) && (ManageApplications.-get3(this.mManageApplications).getVisibility() != 0)) {
          Utils.handleLoadingContainer(ManageApplications.-get5(this.mManageApplications), ManageApplications.-get3(this.mManageApplications), true, false);
        }
        if (this.mManageApplications.mListType != 4) {
          break;
        }
        return;
        this.mEntries = null;
        this.mSections = this.EMPTY_SECTIONS;
        this.mPositionToSectionIndex = null;
      }
      this.mManageApplications.setHasDisabled(this.mState.haveDisabledApps());
    }
    
    public void onRunningStateChanged(boolean paramBoolean)
    {
      this.mManageApplications.getActivity().setProgressBarIndeterminateVisibility(paramBoolean);
    }
    
    public void pause()
    {
      if (this.mResumed)
      {
        this.mResumed = false;
        this.mSession.pause();
        if (this.mExtraInfoBridge != null) {
          this.mExtraInfoBridge.pause();
        }
      }
    }
    
    public void rebuild(int paramInt)
    {
      if (paramInt == this.mLastSortMode) {
        return;
      }
      this.mLastSortMode = paramInt;
      rebuild(true);
    }
    
    public void rebuild(boolean paramBoolean)
    {
      Object localObject1;
      Object localObject2;
      if ((this.mHasReceivedLoadEntries) && ((this.mExtraInfoBridge == null) || (this.mHasReceivedBridgeCallback)))
      {
        if (ManageApplications.DEBUG) {
          Log.i("ManageApplications", "Rebuilding app list...");
        }
        if (!Environment.isExternalStorageEmulated()) {
          break label140;
        }
        this.mWhichSize = 0;
        localObject1 = ManageApplications.FILTERS[this.mFilterMode];
        if (this.mOverrideFilter != null) {
          localObject1 = this.mOverrideFilter;
        }
        localObject2 = localObject1;
        if (!ManageApplications.-get7(this.mManageApplications)) {
          localObject2 = new ApplicationsState.CompoundFilter((ApplicationsState.AppFilter)localObject1, ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER);
        }
        switch (this.mLastSortMode)
        {
        default: 
          localObject1 = ApplicationsState.ALPHA_COMPARATOR;
        }
      }
      for (;;)
      {
        this.mBgHandler.post(new -void_rebuild_boolean_eraseold_LambdaImpl0((ApplicationsState.AppFilter)localObject2, (Comparator)localObject1));
        return;
        return;
        label140:
        this.mWhichSize = 1;
        break;
        switch (this.mWhichSize)
        {
        default: 
          localObject1 = ApplicationsState.SIZE_COMPARATOR;
          break;
        case 1: 
          localObject1 = ApplicationsState.INTERNAL_SIZE_COMPARATOR;
          break;
        case 2: 
          localObject1 = ApplicationsState.EXTERNAL_SIZE_COMPARATOR;
        }
      }
    }
    
    public void release()
    {
      this.mSession.release();
      if (this.mExtraInfoBridge != null) {
        this.mExtraInfoBridge.release();
      }
    }
    
    public void resume(int paramInt)
    {
      if (ManageApplications.DEBUG) {
        Log.i("ManageApplications", "Resume!  mResumed=" + this.mResumed);
      }
      if (!this.mResumed)
      {
        this.mResumed = true;
        this.mSession.resume();
        this.mLastSortMode = paramInt;
        if (this.mExtraInfoBridge != null) {
          this.mExtraInfoBridge.resume();
        }
        rebuild(false);
        return;
      }
      rebuild(paramInt);
    }
    
    public void setFilter(int paramInt)
    {
      this.mFilterMode = paramInt;
      rebuild(true);
    }
    
    public void setOverrideFilter(ApplicationsState.AppFilter paramAppFilter)
    {
      this.mOverrideFilter = paramAppFilter;
      rebuild(true);
    }
    
    private class LoadSummaryAsyncTask
      extends AsyncTask<String, Void, String>
    {
      private String packageName;
      
      public LoadSummaryAsyncTask(String paramString)
      {
        this.packageName = paramString;
      }
      
      protected String doInBackground(String... paramVarArgs)
      {
        paramVarArgs = paramVarArgs[0];
        return ManageApplications.ApplicationsAdapter.-wrap0(ManageApplications.ApplicationsAdapter.this, paramVarArgs).toString();
      }
      
      protected void onPostExecute(String paramString)
      {
        super.onPostExecute(paramString);
        TextView localTextView = (TextView)ManageApplications.-get4(ManageApplications.this).findViewWithTag(this.packageName);
        if (localTextView != null) {
          localTextView.setText(paramString);
        }
        ManageApplications.ApplicationsAdapter.this.addSummaryToCache(this.packageName, paramString);
      }
    }
  }
  
  static class FilterSpinnerAdapter
    extends ArrayAdapter<CharSequence>
  {
    private final ArrayList<Integer> mFilterOptions = new ArrayList();
    private final ManageApplications mManageApplications;
    
    public FilterSpinnerAdapter(ManageApplications paramManageApplications)
    {
      super(2130968700);
      setDropDownViewResource(17367049);
      this.mManageApplications = paramManageApplications;
    }
    
    private CharSequence getFilterString(int paramInt)
    {
      return this.mManageApplications.getString(ManageApplications.FILTER_LABELS[paramInt]);
    }
    
    public void disableFilter(int paramInt)
    {
      if (!this.mFilterOptions.remove(Integer.valueOf(paramInt))) {
        return;
      }
      if (ManageApplications.DEBUG) {
        Log.d("ManageApplications", "Disabling filter " + paramInt);
      }
      Collections.sort(this.mFilterOptions);
      View localView = ManageApplications.-get8(this.mManageApplications);
      if (this.mFilterOptions.size() > 1) {}
      for (int i = 0;; i = 8)
      {
        localView.setVisibility(i);
        notifyDataSetChanged();
        if ((this.mManageApplications.mFilter == paramInt) && (this.mFilterOptions.size() > 0))
        {
          if (ManageApplications.DEBUG) {
            Log.d("ManageApplications", "Auto selecting filter " + this.mFilterOptions.get(0));
          }
          ManageApplications.-get1(this.mManageApplications).setSelection(0);
          this.mManageApplications.onItemSelected(null, null, 0, 0L);
        }
        return;
      }
    }
    
    public void enableFilter(int paramInt)
    {
      if (this.mFilterOptions.contains(Integer.valueOf(paramInt))) {
        return;
      }
      if (ManageApplications.DEBUG) {
        Log.d("ManageApplications", "Enabling filter " + paramInt);
      }
      this.mFilterOptions.add(Integer.valueOf(paramInt));
      Collections.sort(this.mFilterOptions);
      View localView = ManageApplications.-get8(this.mManageApplications);
      if (this.mFilterOptions.size() > 1) {}
      for (int i = 0;; i = 8)
      {
        localView.setVisibility(i);
        notifyDataSetChanged();
        if (this.mFilterOptions.size() == 1)
        {
          if (ManageApplications.DEBUG) {
            Log.d("ManageApplications", "Auto selecting filter " + paramInt);
          }
          ManageApplications.-get1(this.mManageApplications).setSelection(0);
          this.mManageApplications.onItemSelected(null, null, 0, 0L);
        }
        return;
      }
    }
    
    public int getCount()
    {
      return this.mFilterOptions.size();
    }
    
    public int getFilter(int paramInt)
    {
      return ((Integer)this.mFilterOptions.get(paramInt)).intValue();
    }
    
    public CharSequence getItem(int paramInt)
    {
      return getFilterString(((Integer)this.mFilterOptions.get(paramInt)).intValue());
    }
    
    public void setFilterEnabled(int paramInt, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        enableFilter(paramInt);
        return;
      }
      disableFilter(paramInt);
    }
  }
  
  private static class SectionInfo
  {
    final String label;
    final int position;
    
    public SectionInfo(String paramString, int paramInt)
    {
      this.label = paramString;
      this.position = paramInt;
    }
    
    public String toString()
    {
      return this.label;
    }
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mLoader;
    private ApplicationsState.Session mSession;
    
    private SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mLoader = paramSummaryLoader;
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean) {
        new AppCounter(this.mContext)
        {
          protected boolean includeInCount(ApplicationInfo paramAnonymousApplicationInfo)
          {
            if ((paramAnonymousApplicationInfo.flags & 0x80) != 0) {
              return true;
            }
            if ((paramAnonymousApplicationInfo.flags & 0x1) == 0) {
              return true;
            }
            Intent localIntent = new Intent("android.intent.action.MAIN", null).addCategory("android.intent.category.LAUNCHER").setPackage(paramAnonymousApplicationInfo.packageName);
            int i = UserHandle.getUserId(paramAnonymousApplicationInfo.uid);
            paramAnonymousApplicationInfo = this.mPm.queryIntentActivitiesAsUser(localIntent, 786944, i);
            return (paramAnonymousApplicationInfo != null) && (paramAnonymousApplicationInfo.size() != 0);
          }
          
          protected void onCountComplete(int paramAnonymousInt)
          {
            ManageApplications.SummaryProvider.-get1(ManageApplications.SummaryProvider.this).setSummary(ManageApplications.SummaryProvider.this, ManageApplications.SummaryProvider.-get0(ManageApplications.SummaryProvider.this).getString(2131693583, new Object[] { Integer.valueOf(paramAnonymousInt) }));
          }
        }.execute(new Void[0]);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ManageApplications.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */