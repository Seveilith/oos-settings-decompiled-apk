package com.android.settings.applications;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.icu.text.ListFormatter;
import android.net.INetworkStatsService;
import android.net.INetworkStatsService.Stub;
import android.net.INetworkStatsSession;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryStats;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.IWebViewUpdateService;
import android.webkit.IWebViewUpdateService.Stub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.DeviceAdminAdd;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.datausage.AppDataUsage;
import com.android.settings.datausage.DataUsageList;
import com.android.settings.datausage.DataUsageSummary;
import com.android.settings.fuelgauge.BatteryEntry;
import com.android.settings.fuelgauge.PowerUsageDetail;
import com.android.settings.notification.AppNotificationSettings;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.NotificationBackend.AppRow;
import com.android.settingslib.AppItem;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.net.ChartData;
import com.android.settingslib.net.ChartDataLoader;
import com.oneplus.settings.SettingsBaseApplication;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class InstalledAppDetails
  extends AppInfoBase
  implements View.OnClickListener, Preference.OnPreferenceClickListener
{
  private static final int DLG_DISABLE = 2;
  private static final int DLG_FORCE_STOP = 1;
  private static final int DLG_SPECIAL_DISABLE = 3;
  private static final String KEY_BATTERY = "battery";
  private static final String KEY_DATA = "data_settings";
  private static final String KEY_GOOGLE__INPUTMETHOD = "com.google.android.inputmethod.latin";
  private static final String KEY_HEADER = "header_view";
  private static final String KEY_LATIN_INPUTMETHOD = "com.android.inputmethod.latin";
  private static final String KEY_LAUNCH = "preferred_settings";
  private static final String KEY_MEMORY = "memory";
  private static final String KEY_NOTIFICATION = "notification_settings";
  private static final String KEY_PERMISSION = "permission_settings";
  private static final String KEY_STORAGE = "storage_settings";
  private static final int LOADER_CHART_DATA = 2;
  private static final String LOG_TAG = "InstalledAppDetails";
  private static final String NOTIFICATION_TUNER_SETTING = "show_importance_slider";
  private static final int REQUEST_REMOVE_DEVICE_ADMIN = 1;
  public static final int REQUEST_UNINSTALL = 0;
  private static final int SUB_INFO_FRAGMENT = 1;
  public static final int UNINSTALL_ALL_USERS_MENU = 1;
  public static final int UNINSTALL_UPDATES = 2;
  private final NotificationBackend mBackend = new NotificationBackend();
  private BatteryStatsHelper mBatteryHelper;
  private Preference mBatteryPreference;
  private ChartData mChartData;
  private final BroadcastReceiver mCheckKillProcessesReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      boolean bool = false;
      paramAnonymousContext = InstalledAppDetails.this;
      if (getResultCode() != 0) {
        bool = true;
      }
      InstalledAppDetails.-wrap4(paramAnonymousContext, bool);
    }
  };
  private final LoaderManager.LoaderCallbacks<ChartData> mDataCallbacks = new LoaderManager.LoaderCallbacks()
  {
    public Loader<ChartData> onCreateLoader(int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      return new ChartDataLoader(InstalledAppDetails.this.getActivity(), InstalledAppDetails.-get4(InstalledAppDetails.this), paramAnonymousBundle);
    }
    
    public void onLoadFinished(Loader<ChartData> paramAnonymousLoader, ChartData paramAnonymousChartData)
    {
      InstalledAppDetails.-set0(InstalledAppDetails.this, paramAnonymousChartData);
      InstalledAppDetails.-get1(InstalledAppDetails.this).setSummary(InstalledAppDetails.-wrap0(InstalledAppDetails.this));
    }
    
    public void onLoaderReset(Loader<ChartData> paramAnonymousLoader) {}
  };
  private Preference mDataPreference;
  private boolean mDisableAfterUninstall;
  private Button mForceStopButton;
  private LayoutPreference mHeader;
  private final HashSet<String> mHomePackages = new HashSet();
  private boolean mInitialized;
  private Preference mLaunchPreference;
  private Preference mMemoryPreference;
  private Preference mNotificationPreference;
  private final PermissionsSummaryHelper.PermissionsResultCallback mPermissionCallback = new PermissionsSummaryHelper.PermissionsResultCallback()
  {
    public void onPermissionSummaryResult(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, List<CharSequence> paramAnonymousList)
    {
      if (InstalledAppDetails.this.getActivity() == null) {
        return;
      }
      Resources localResources = InstalledAppDetails.this.getResources();
      if (paramAnonymousInt2 == 0)
      {
        paramAnonymousList = localResources.getString(2131693399);
        InstalledAppDetails.-get3(InstalledAppDetails.this).setOnPreferenceClickListener(null);
        InstalledAppDetails.-get3(InstalledAppDetails.this).setEnabled(false);
        if (!Build.VERSION.IS_CTA_BUILD) {
          InstalledAppDetails.-get3(InstalledAppDetails.this).setSummary(paramAnonymousList);
        }
        return;
      }
      paramAnonymousList = new ArrayList(paramAnonymousList);
      if (paramAnonymousInt3 > 0) {
        paramAnonymousList.add(localResources.getQuantityString(2131951645, paramAnonymousInt3, new Object[] { Integer.valueOf(paramAnonymousInt3) }));
      }
      if (paramAnonymousList.size() == 0) {}
      for (paramAnonymousList = localResources.getString(2131693398);; paramAnonymousList = ListFormatter.getInstance().format(paramAnonymousList))
      {
        InstalledAppDetails.-get3(InstalledAppDetails.this).setOnPreferenceClickListener(InstalledAppDetails.this);
        InstalledAppDetails.-get3(InstalledAppDetails.this).setEnabled(true);
        break;
      }
    }
  };
  private Preference mPermissionsPreference;
  private boolean mShowUninstalled;
  private BatterySipper mSipper;
  protected ProcStatsPackageEntry mStats;
  protected ProcStatsData mStatsManager;
  private INetworkStatsSession mStatsSession;
  private Preference mStoragePreference;
  private Button mUninstallButton;
  private boolean mUpdatedSysApp = false;
  
  private void addAppInstallerInfoPref(PreferenceScreen paramPreferenceScreen)
  {
    Object localObject = null;
    try
    {
      String str = getContext().getPackageManager().getInstallerPackageName(this.mPackageName);
      localObject = str;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;)
      {
        Log.e(TAG, "Exception while retrieving the package installer of " + this.mPackageName, localIllegalArgumentException);
      }
      CharSequence localCharSequence = Utils.getApplicationLabel(getContext(), (String)localObject);
      if (localCharSequence != null) {
        break label74;
      }
      return;
      localPreferenceCategory = new PreferenceCategory(getPrefContext());
      localPreferenceCategory.setTitle(2131692177);
      paramPreferenceScreen.addPreference(localPreferenceCategory);
      paramPreferenceScreen = new Preference(getPrefContext());
      paramPreferenceScreen.setTitle(2131692178);
      paramPreferenceScreen.setKey("app_info_store");
      paramPreferenceScreen.setSummary(getString(2131692179, new Object[] { localCharSequence }));
      localObject = resolveIntent(new Intent("android.intent.action.SHOW_APP_INFO").setPackage((String)localObject));
      if (localObject == null) {
        break label192;
      }
    }
    if (localObject == null) {
      return;
    }
    label74:
    PreferenceCategory localPreferenceCategory;
    ((Intent)localObject).putExtra("android.intent.extra.PACKAGE_NAME", this.mPackageName);
    paramPreferenceScreen.setIntent((Intent)localObject);
    for (;;)
    {
      localPreferenceCategory.addPreference(paramPreferenceScreen);
      return;
      label192:
      paramPreferenceScreen.setEnabled(false);
    }
  }
  
  private void addDynamicPrefs()
  {
    if (Utils.isManagedProfile(UserManager.get(getContext()))) {
      return;
    }
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (DefaultHomePreference.hasHomePreference(this.mPackageName, getContext())) {
      localPreferenceScreen.addPreference(new ShortcutPreference(getPrefContext(), AdvancedAppSettings.class, "default_home", 2131693656, 2131693411));
    }
    if (DefaultBrowserPreference.hasBrowserPreference(this.mPackageName, getContext())) {
      localPreferenceScreen.addPreference(new ShortcutPreference(getPrefContext(), AdvancedAppSettings.class, "default_browser", 2131693437, 2131693411));
    }
    if (DefaultPhonePreference.hasPhonePreference(this.mPackageName, getContext())) {
      localPreferenceScreen.addPreference(new ShortcutPreference(getPrefContext(), AdvancedAppSettings.class, "default_phone_app", 2131693439, 2131693411));
    }
    if (DefaultEmergencyPreference.hasEmergencyPreference(this.mPackageName, getContext())) {
      localPreferenceScreen.addPreference(new ShortcutPreference(getPrefContext(), AdvancedAppSettings.class, "default_emergency_app", 2131692111, 2131693411));
    }
    if (DefaultSmsPreference.hasSmsPreference(this.mPackageName, getContext())) {
      localPreferenceScreen.addPreference(new ShortcutPreference(getPrefContext(), AdvancedAppSettings.class, "default_sms_app", 2131691932, 2131693411));
    }
    boolean bool1 = hasPermission("android.permission.SYSTEM_ALERT_WINDOW");
    boolean bool2 = hasPermission("android.permission.WRITE_SETTINGS");
    if ((bool1) || (bool2))
    {
      PreferenceCategory localPreferenceCategory = new PreferenceCategory(getPrefContext());
      localPreferenceCategory.setTitle(2131693410);
      localPreferenceScreen.addPreference(localPreferenceCategory);
      Preference localPreference;
      if (bool1)
      {
        localPreference = new Preference(getPrefContext());
        localPreference.setTitle(2131693532);
        localPreference.setKey("system_alert_window");
        localPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
          public boolean onPreferenceClick(Preference paramAnonymousPreference)
          {
            InstalledAppDetails.-wrap2(InstalledAppDetails.this, DrawOverlayDetails.class, InstalledAppDetails.this.getString(2131693532));
            return true;
          }
        });
        localPreferenceCategory.addPreference(localPreference);
      }
      if (bool2)
      {
        localPreference = new Preference(getPrefContext());
        localPreference.setTitle(2131693546);
        localPreference.setKey("write_settings_apps");
        localPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
          public boolean onPreferenceClick(Preference paramAnonymousPreference)
          {
            InstalledAppDetails.-wrap2(InstalledAppDetails.this, WriteSettingsDetails.class, InstalledAppDetails.this.getString(2131693546));
            return true;
          }
        });
        localPreferenceCategory.addPreference(localPreference);
      }
    }
    addAppInstallerInfoPref(localPreferenceScreen);
  }
  
  private void checkForceStop()
  {
    if (this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName))
    {
      updateForceStopButton(false);
      return;
    }
    if ((this.mAppEntry.info.flags & 0x200000) == 0)
    {
      updateForceStopButton(true);
      return;
    }
    Intent localIntent = new Intent("android.intent.action.QUERY_PACKAGE_RESTART", Uri.fromParts("package", this.mAppEntry.info.packageName, null));
    localIntent.putExtra("android.intent.extra.PACKAGES", new String[] { this.mAppEntry.info.packageName });
    localIntent.putExtra("android.intent.extra.UID", this.mAppEntry.info.uid);
    localIntent.putExtra("android.intent.extra.user_handle", UserHandle.getUserId(this.mAppEntry.info.uid));
    SettingsBaseApplication.mApplication.getApplicationContext().sendOrderedBroadcastAsUser(localIntent, UserHandle.CURRENT, null, this.mCheckKillProcessesReceiver, null, 0, null, null);
  }
  
  private void clearExtraInfo()
  {
    if ((this.mAppEntry != null) && (this.mAppEntry.extraInfo != null)) {
      this.mAppEntry.extraInfo = null;
    }
  }
  
  private void forceStopPackage(String paramString)
  {
    ((ActivityManager)SettingsBaseApplication.mApplication.getSystemService("activity")).forceStopPackage(paramString);
    int i = UserHandle.getUserId(this.mAppEntry.info.uid);
    this.mState.invalidatePackage(paramString, i);
    paramString = this.mState.getEntry(paramString, i);
    if (paramString != null) {
      this.mAppEntry = paramString;
    }
    checkForceStop();
  }
  
  private CharSequence getDataSummary()
  {
    if (this.mChartData != null)
    {
      long l = this.mChartData.detail.getTotalBytes();
      if (l == 0L) {
        return getString(2131693517);
      }
      Activity localActivity = getActivity();
      return getString(2131693385, new Object[] { Formatter.formatFileSize(localActivity, l), DateUtils.formatDateTime(localActivity, this.mChartData.detail.getStart(), 65552) });
    }
    return getString(2131692149);
  }
  
  public static CharSequence getNotificationSummary(NotificationBackend.AppRow paramAppRow, Context paramContext)
  {
    int i;
    ArrayList localArrayList;
    StringBuffer localStringBuffer;
    if (Settings.Secure.getInt(paramContext.getContentResolver(), "show_importance_slider", 0) == 1)
    {
      i = 1;
      localArrayList = new ArrayList();
      localStringBuffer = new StringBuffer();
      if (i == 0) {
        break label199;
      }
      if (paramAppRow.appImportance != 64536) {
        localArrayList.add(paramContext.getString(2131693397, new Object[] { Integer.valueOf(paramAppRow.appImportance) }));
      }
      label77:
      if (new LockPatternUtils(paramContext).isSecure(UserHandle.myUserId()))
      {
        if (paramAppRow.appVisOverride != 0) {
          break label309;
        }
        localArrayList.add(paramContext.getString(2131693393));
      }
    }
    for (;;)
    {
      if (paramAppRow.appBypassDnd) {
        localArrayList.add(paramContext.getString(2131693395));
      }
      int j = localArrayList.size();
      i = 0;
      while (i < j)
      {
        if (i > 0) {
          localStringBuffer.append(paramContext.getString(2131693396));
        }
        localStringBuffer.append((String)localArrayList.get(i));
        i += 1;
      }
      i = 0;
      break;
      label199:
      if (paramAppRow.banned)
      {
        localArrayList.add(paramContext.getString(2131693391));
        break label77;
      }
      if (paramAppRow.appImportance == 0)
      {
        localArrayList.add(paramContext.getString(2131690426));
        break label77;
      }
      if (paramAppRow.appImportance == 1)
      {
        localArrayList.add(paramContext.getString(2131690424));
        break label77;
      }
      if ((paramAppRow.appImportance <= 1) || (paramAppRow.appImportance >= 5)) {
        break label77;
      }
      localArrayList.add(paramContext.getString(2131690422));
      break label77;
      label309:
      if (paramAppRow.appVisOverride == -1) {
        localArrayList.add(paramContext.getString(2131693394));
      }
    }
    return localStringBuffer.toString();
  }
  
  public static CharSequence getNotificationSummary(ApplicationsState.AppEntry paramAppEntry, Context paramContext)
  {
    return getNotificationSummary(paramAppEntry, paramContext, new NotificationBackend());
  }
  
  public static CharSequence getNotificationSummary(ApplicationsState.AppEntry paramAppEntry, Context paramContext, NotificationBackend paramNotificationBackend)
  {
    return getNotificationSummary(paramNotificationBackend.loadAppRow(paramContext, paramContext.getPackageManager(), paramAppEntry.info), paramContext);
  }
  
  public static NetworkTemplate getTemplate(Context paramContext)
  {
    if (DataUsageList.hasReadyMobileRadio(paramContext)) {
      return NetworkTemplate.buildTemplateMobileWildcard();
    }
    if (DataUsageSummary.hasWifiRadio(paramContext)) {
      return NetworkTemplate.buildTemplateWifiWildcard();
    }
    return NetworkTemplate.buildTemplateEthernet();
  }
  
  private boolean handleDisableable(Button paramButton)
  {
    if ((this.mHomePackages.contains(this.mAppEntry.info.packageName)) || ("com.android.inputmethod.latin".contains(this.mAppEntry.info.packageName)) || ("com.google.android.inputmethod.latin".contains(this.mAppEntry.info.packageName)) || (Utils.isSystemPackage(getResources(), this.mPm, this.mPackageInfo)))
    {
      paramButton.setText(2131692096);
      return false;
    }
    if ((!this.mAppEntry.info.enabled) || (isDisabledUntilUsed()))
    {
      paramButton.setText(2131692097);
      return true;
    }
    paramButton.setText(2131692096);
    return true;
  }
  
  private void handleHeader()
  {
    this.mHeader = ((LayoutPreference)findPreference("header_view"));
    View localView = this.mHeader.findViewById(2131362178);
    this.mForceStopButton = ((Button)localView.findViewById(2131362282));
    this.mForceStopButton.setText(2131692087);
    this.mUninstallButton = ((Button)localView.findViewById(2131362644));
    this.mForceStopButton.setEnabled(false);
    localView = this.mHeader.findViewById(2131362177);
    final Intent localIntent = new Intent("android.intent.action.APPLICATION_PREFERENCES");
    localIntent.setPackage(this.mPackageName);
    localIntent = resolveIntent(localIntent);
    if (localIntent != null)
    {
      localView.setVisibility(0);
      localView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          InstalledAppDetails.this.startActivity(localIntent);
        }
      });
      return;
    }
    localView.setVisibility(8);
  }
  
  private boolean hasPermission(String paramString)
  {
    if ((this.mPackageInfo == null) || (this.mPackageInfo.requestedPermissions == null)) {
      return false;
    }
    int i = 0;
    while (i < this.mPackageInfo.requestedPermissions.length)
    {
      if (this.mPackageInfo.requestedPermissions[i].equals(paramString)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void initUninstallButtons()
  {
    int i = 0;
    if ((this.mAppEntry.info.flags & 0x1) != 0) {
      i = 1;
    }
    boolean bool2 = true;
    boolean bool1;
    if (i != 0)
    {
      bool1 = handleDisableable(this.mUninstallButton);
      bool2 = bool1;
      if (i != 0)
      {
        bool2 = bool1;
        if (this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName)) {
          bool2 = false;
        }
      }
      if (isProfileOrDeviceOwner(this.mPackageInfo.packageName)) {
        bool2 = false;
      }
      if (Utils.isDeviceProvisioningPackage(getResources(), this.mAppEntry.info.packageName)) {
        bool2 = false;
      }
      if (this.mDpm.isUninstallInQueue(this.mPackageName)) {
        bool2 = false;
      }
      bool1 = bool2;
      if (bool2)
      {
        bool1 = bool2;
        if (this.mHomePackages.contains(this.mPackageInfo.packageName))
        {
          if (i == 0) {
            break label259;
          }
          bool1 = false;
        }
      }
    }
    for (;;)
    {
      if (this.mAppsControlDisallowedBySystem) {
        bool1 = false;
      }
      try
      {
        bool2 = IWebViewUpdateService.Stub.asInterface(ServiceManager.getService("webviewupdate")).isFallbackPackage(this.mAppEntry.info.packageName);
        if (bool2) {
          bool1 = false;
        }
        this.mUninstallButton.setEnabled(bool1);
        if (bool1) {
          this.mUninstallButton.setOnClickListener(this);
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Object localObject;
        throw new RuntimeException(localRemoteException);
      }
      bool1 = bool2;
      if ((this.mPackageInfo.applicationInfo.flags & 0x800000) == 0)
      {
        bool1 = bool2;
        if (this.mUserManager.getUsers().size() >= 2) {
          bool1 = false;
        }
      }
      this.mUninstallButton.setText(2131692093);
      break;
      label259:
      localObject = new ArrayList();
      localObject = this.mPm.getHomeActivities((List)localObject);
      if (localObject == null)
      {
        if (this.mHomePackages.size() > 1) {
          bool1 = true;
        } else {
          bool1 = false;
        }
      }
      else
      {
        bool1 = bool2;
        if (this.mPackageInfo.packageName != null) {
          if (this.mPackageInfo.packageName.equals(((ComponentName)localObject).getPackageName())) {
            bool1 = false;
          } else {
            bool1 = true;
          }
        }
      }
    }
  }
  
  private boolean isDisabledUntilUsed()
  {
    return this.mAppEntry.info.enabledSetting == 4;
  }
  
  private boolean isProfileOrDeviceOwner(String paramString)
  {
    Object localObject = this.mUserManager.getUsers();
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)getContext().getSystemService("device_policy");
    if (localDevicePolicyManager.isDeviceOwnerAppOnAnyUser(paramString)) {
      return true;
    }
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      ComponentName localComponentName = localDevicePolicyManager.getProfileOwnerAsUser(((UserInfo)((Iterator)localObject).next()).id);
      if ((localComponentName != null) && (localComponentName.getPackageName().equals(paramString))) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isSingleUser()
  {
    int i = this.mUserManager.getUserCount();
    if (i != 1) {
      UserManager localUserManager = this.mUserManager;
    }
    return (UserManager.isSplitSystemUser()) && (i == 2);
  }
  
  private Intent resolveIntent(Intent paramIntent)
  {
    Intent localIntent = null;
    ResolveInfo localResolveInfo = getContext().getPackageManager().resolveActivity(paramIntent, 0);
    if (localResolveInfo != null) {
      localIntent = new Intent(paramIntent.getAction()).setClassName(localResolveInfo.activityInfo.packageName, localResolveInfo.activityInfo.name);
    }
    return localIntent;
  }
  
  private void setAppLabelAndIcon(PackageInfo paramPackageInfo)
  {
    String str1 = null;
    View localView = this.mHeader.findViewById(2131361955);
    this.mState.ensureIcon(this.mAppEntry);
    String str2 = this.mAppEntry.label;
    Drawable localDrawable = this.mAppEntry.icon;
    if (paramPackageInfo != null) {
      str1 = paramPackageInfo.versionName;
    }
    setupAppSnippet(localView, str2, localDrawable, str1);
  }
  
  public static void setupAppSnippet(View paramView, CharSequence paramCharSequence1, Drawable paramDrawable, CharSequence paramCharSequence2)
  {
    LayoutInflater.from(paramView.getContext()).inflate(2130969109, (ViewGroup)paramView.findViewById(16908312));
    ((ImageView)paramView.findViewById(16908294)).setImageDrawable(paramDrawable);
    ((TextView)paramView.findViewById(16908310)).setText(paramCharSequence1);
    paramCharSequence1 = (TextView)paramView.findViewById(2131362718);
    if (!TextUtils.isEmpty(paramCharSequence2))
    {
      paramCharSequence1.setSelected(true);
      paramCharSequence1.setVisibility(0);
      paramCharSequence1.setText(paramView.getContext().getString(2131692152, new Object[] { String.valueOf(paramCharSequence2) }));
      return;
    }
    paramCharSequence1.setVisibility(4);
  }
  
  private boolean signaturesMatch(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null)) {
      try
      {
        int i = this.mPm.checkSignatures(paramString1, paramString2);
        if (i >= 0) {
          return true;
        }
      }
      catch (Exception paramString1) {}
    }
    return false;
  }
  
  private void startAppInfoFragment(Class<?> paramClass, CharSequence paramCharSequence)
  {
    startAppInfoFragment(paramClass, paramCharSequence, this, this.mAppEntry);
  }
  
  public static void startAppInfoFragment(Class<?> paramClass, CharSequence paramCharSequence, SettingsPreferenceFragment paramSettingsPreferenceFragment, ApplicationsState.AppEntry paramAppEntry)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("package", paramAppEntry.info.packageName);
    localBundle.putInt("uid", paramAppEntry.info.uid);
    localBundle.putBoolean("hideInfoButton", true);
    ((SettingsActivity)paramSettingsPreferenceFragment.getActivity()).startPreferencePanel(paramClass.getName(), localBundle, -1, paramCharSequence, paramSettingsPreferenceFragment, 1);
  }
  
  private void startManagePermissionsActivity()
  {
    Intent localIntent;
    if (Build.VERSION.IS_CTA_BUILD)
    {
      localIntent = new Intent("com.oneplus.security.action.OPPERMISSION_APP");
      localIntent.putExtra("packageName", this.mAppEntry.info.packageName);
    }
    for (;;)
    {
      try
      {
        startActivity(localIntent);
        return;
      }
      catch (ActivityNotFoundException localActivityNotFoundException)
      {
        Log.w("InstalledAppDetails", "No app can handle android.intent.action.MANAGE_APP_PERMISSIONS");
      }
      localIntent = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
      localIntent.putExtra("android.intent.extra.PACKAGE_NAME", this.mAppEntry.info.packageName);
      localIntent.putExtra("hideInfoButton", true);
    }
  }
  
  private void uninstallPkg(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    stopListeningToPackageRemove();
    paramString = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + paramString));
    paramString.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", paramBoolean1);
    startActivityForResult(paramString, 0);
    this.mDisableAfterUninstall = paramBoolean2;
  }
  
  private void updateBattery()
  {
    if (this.mSipper != null)
    {
      this.mBatteryPreference.setEnabled(true);
      int i = this.mBatteryHelper.getStats().getDischargeAmount(0);
      i = (int)(this.mSipper.totalPowerMah / this.mBatteryHelper.getTotalPower() * i + 0.5D);
      this.mBatteryPreference.setSummary(getString(2131693467, new Object[] { Integer.valueOf(i) }));
      return;
    }
    this.mBatteryPreference.setEnabled(false);
    this.mBatteryPreference.setSummary(getString(2131693468));
  }
  
  private void updateDynamicPrefs()
  {
    int j = 2131690771;
    Preference localPreference = findPreference("default_home");
    if (localPreference != null)
    {
      if (DefaultHomePreference.isHomeDefault(this.mPackageName, getContext()))
      {
        i = 2131690771;
        localPreference.setSummary(i);
      }
    }
    else
    {
      localPreference = findPreference("default_browser");
      if (localPreference != null)
      {
        if (!DefaultBrowserPreference.isBrowserDefault(this.mPackageName, getContext())) {
          break label239;
        }
        i = 2131690771;
        label69:
        localPreference.setSummary(i);
      }
      localPreference = findPreference("default_phone_app");
      if (localPreference != null)
      {
        if (!DefaultPhonePreference.isPhoneDefault(this.mPackageName, getContext())) {
          break label246;
        }
        i = 2131690771;
        label104:
        localPreference.setSummary(i);
      }
      localPreference = findPreference("default_emergency_app");
      if (localPreference != null)
      {
        if (!DefaultEmergencyPreference.isEmergencyDefault(this.mPackageName, getContext())) {
          break label253;
        }
        i = 2131690771;
        label139:
        localPreference.setSummary(i);
      }
      localPreference = findPreference("default_sms_app");
      if (localPreference != null) {
        if (!DefaultSmsPreference.isSmsDefault(this.mPackageName, getContext())) {
          break label260;
        }
      }
    }
    label239:
    label246:
    label253:
    label260:
    for (int i = j;; i = 2131690772)
    {
      localPreference.setSummary(i);
      localPreference = findPreference("system_alert_window");
      if (localPreference != null) {
        localPreference.setSummary(DrawOverlayDetails.getSummary(getContext(), this.mAppEntry));
      }
      localPreference = findPreference("write_settings_apps");
      if (localPreference != null) {
        localPreference.setSummary(WriteSettingsDetails.getSummary(getContext(), this.mAppEntry));
      }
      return;
      i = 2131690772;
      break;
      i = 2131690772;
      break label69;
      i = 2131690772;
      break label104;
      i = 2131690772;
      break label139;
    }
  }
  
  private void updateForceStopButton(boolean paramBoolean)
  {
    if (this.mAppsControlDisallowedBySystem)
    {
      this.mForceStopButton.setEnabled(false);
      return;
    }
    this.mForceStopButton.setEnabled(paramBoolean);
    this.mForceStopButton.setOnClickListener(this);
  }
  
  protected AlertDialog createDialog(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return null;
    case 2: 
      new AlertDialog.Builder(getActivity()).setMessage(getActivity().getText(2131692172)).setPositiveButton(2131692171, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          new InstalledAppDetails.DisableChanger(InstalledAppDetails.this, InstalledAppDetails.this.mAppEntry.info, 3).execute(new Object[] { (Object)null });
        }
      }).setNegativeButton(2131692134, null).create();
    case 3: 
      new AlertDialog.Builder(getActivity()).setMessage(getActivity().getText(2131692172)).setPositiveButton(2131692171, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          InstalledAppDetails.-wrap3(InstalledAppDetails.this, InstalledAppDetails.this.mAppEntry.info.packageName, false, true);
        }
      }).setNegativeButton(2131692134, null).create();
    }
    new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131692164)).setMessage(getActivity().getText(2131692165)).setPositiveButton(2131692133, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        InstalledAppDetails.-wrap1(InstalledAppDetails.this, InstalledAppDetails.this.mAppEntry.info.packageName);
      }
    }).setNegativeButton(2131692134, null).create();
  }
  
  protected int getMetricsCategory()
  {
    return 20;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (this.mFinishing) {
      return;
    }
    handleHeader();
    this.mNotificationPreference = findPreference("notification_settings");
    this.mNotificationPreference.setOnPreferenceClickListener(this);
    this.mStoragePreference = findPreference("storage_settings");
    this.mStoragePreference.setOnPreferenceClickListener(this);
    this.mPermissionsPreference = findPreference("permission_settings");
    this.mPermissionsPreference.setOnPreferenceClickListener(this);
    this.mDataPreference = findPreference("data_settings");
    if (this.mDataPreference != null) {
      this.mDataPreference.setOnPreferenceClickListener(this);
    }
    this.mBatteryPreference = findPreference("battery");
    this.mBatteryPreference.setEnabled(false);
    this.mBatteryPreference.setOnPreferenceClickListener(this);
    this.mMemoryPreference = findPreference("memory");
    this.mMemoryPreference.setOnPreferenceClickListener(this);
    this.mLaunchPreference = findPreference("preferred_settings");
    if ((this.mAppEntry != null) && (this.mAppEntry.info != null))
    {
      if (((this.mAppEntry.info.flags & 0x800000) != 0) && (this.mAppEntry.info.enabled))
      {
        this.mLaunchPreference.setOnPreferenceClickListener(this);
        return;
      }
      this.mLaunchPreference.setEnabled(false);
      return;
    }
    this.mLaunchPreference.setEnabled(false);
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    switch (paramInt1)
    {
    default: 
      return;
    case 0: 
      if (this.mDisableAfterUninstall)
      {
        this.mDisableAfterUninstall = false;
        new DisableChanger(this, this.mAppEntry.info, 3).execute(new Object[] { (Object)null });
      }
      break;
    }
    if (!refreshUi())
    {
      setIntentAndFinish(true, true);
      return;
    }
    startListeningToPackageRemove();
  }
  
  public void onClick(View paramView)
  {
    if (this.mAppEntry == null)
    {
      setIntentAndFinish(true, true);
      return;
    }
    Object localObject = this.mAppEntry.info.packageName;
    if (paramView == this.mUninstallButton)
    {
      if (this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName))
      {
        stopListeningToPackageRemove();
        paramView = getActivity();
        localObject = new Intent(paramView, DeviceAdminAdd.class);
        ((Intent)localObject).putExtra("android.app.extra.DEVICE_ADMIN_PACKAGE_NAME", this.mPackageName);
        paramView.startActivityForResult((Intent)localObject, 1);
        return;
      }
      paramView = RestrictedLockUtils.checkIfUninstallBlocked(getActivity(), (String)localObject, this.mUserId);
      if (!this.mAppsControlDisallowedBySystem)
      {
        bool = RestrictedLockUtils.hasBaseUserRestriction(getActivity(), (String)localObject, this.mUserId);
        if ((paramView != null) && (!bool)) {
          break label203;
        }
        if ((this.mAppEntry.info.flags & 0x1) == 0) {
          break label240;
        }
        if ((this.mAppEntry.info.enabled) && (!isDisabledUntilUsed())) {
          break label212;
        }
        new DisableChanger(this, this.mAppEntry.info, 0).execute(new Object[] { (Object)null });
      }
    }
    label203:
    label212:
    label240:
    while (paramView != this.mForceStopButton)
    {
      for (;;)
      {
        return;
        boolean bool = true;
      }
      RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), paramView);
      return;
      if ((this.mUpdatedSysApp) && (isSingleUser()))
      {
        showDialogInner(3, 0);
        return;
      }
      showDialogInner(2, 0);
      return;
      if ((this.mAppEntry.info.flags & 0x800000) == 0)
      {
        uninstallPkg((String)localObject, true, false);
        return;
      }
      uninstallPkg((String)localObject, false, false);
      return;
    }
    if ((this.mAppsControlDisallowedAdmin == null) || (this.mAppsControlDisallowedBySystem))
    {
      showDialogInner(1, 0);
      return;
    }
    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), this.mAppsControlDisallowedAdmin);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    clearExtraInfo();
    setHasOptionsMenu(true);
    addPreferencesFromResource(2131230772);
    addDynamicPrefs();
    if (Utils.isBandwidthControlEnabled()) {
      paramBundle = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
    }
    for (;;)
    {
      try
      {
        this.mStatsSession = paramBundle.openSession();
        this.mBatteryHelper = new BatteryStatsHelper(getActivity(), true);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeException(paramBundle);
      }
      removePreference("data_settings");
    }
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 2, 0, 2131692099).setShowAsAction(0);
    paramMenu.add(0, 1, 1, 2131692094).setShowAsAction(0);
  }
  
  public void onDestroy()
  {
    TrafficStats.closeQuietly(this.mStatsSession);
    super.onDestroy();
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return false;
    case 1: 
      uninstallPkg(this.mAppEntry.info.packageName, true, false);
      return true;
    }
    uninstallPkg(this.mAppEntry.info.packageName, false, false);
    return true;
  }
  
  protected void onPackageRemoved()
  {
    getActivity().finishActivity(1);
    super.onPackageRemoved();
  }
  
  public void onPause()
  {
    getLoaderManager().destroyLoader(2);
    super.onPause();
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mStoragePreference)
    {
      startAppInfoFragment(AppStorageSettings.class, this.mStoragePreference.getTitle());
      return true;
    }
    if (paramPreference == this.mNotificationPreference)
    {
      startAppInfoFragment(AppNotificationSettings.class, getString(2131693222));
      return true;
    }
    if (paramPreference == this.mPermissionsPreference)
    {
      startManagePermissionsActivity();
      return true;
    }
    if (paramPreference == this.mLaunchPreference)
    {
      startAppInfoFragment(AppLaunchSettings.class, this.mLaunchPreference.getTitle());
      return true;
    }
    if (paramPreference == this.mMemoryPreference)
    {
      ProcessStatsBase.launchMemoryDetail((SettingsActivity)getActivity(), this.mStatsManager.getMemInfo(), this.mStats, false);
      return true;
    }
    if (paramPreference == this.mDataPreference)
    {
      startAppInfoFragment(AppDataUsage.class, getString(2131693384));
      return true;
    }
    if (paramPreference == this.mBatteryPreference)
    {
      paramPreference = new BatteryEntry(getActivity(), null, this.mUserManager, this.mSipper);
      PowerUsageDetail.startBatteryDetailPage((SettingsActivity)getActivity(), this.mBatteryHelper, 0, paramPreference, true, false);
      return true;
    }
    return false;
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    boolean bool2 = false;
    if (this.mFinishing) {
      return;
    }
    boolean bool1 = true;
    if (this.mUpdatedSysApp)
    {
      bool1 = false;
      paramMenu.findItem(1).setVisible(bool1);
      if ((this.mAppEntry.info.flags & 0x80) == 0) {
        break label208;
      }
      bool1 = true;
      label54:
      this.mUpdatedSysApp = bool1;
      paramMenu = paramMenu.findItem(2);
      bool1 = bool2;
      if (this.mUpdatedSysApp) {
        if (!this.mAppsControlDisallowedBySystem) {
          break label213;
        }
      }
    }
    label208:
    label213:
    for (bool1 = bool2;; bool1 = true)
    {
      paramMenu.setVisible(bool1);
      if (paramMenu.isVisible()) {
        RestrictedLockUtils.setMenuItemAsDisabledByAdmin(getActivity(), paramMenu, this.mAppsControlDisallowedAdmin);
      }
      return;
      if (this.mAppEntry == null)
      {
        bool1 = false;
        break;
      }
      if ((this.mAppEntry.info.flags & 0x1) != 0)
      {
        bool1 = false;
        break;
      }
      if ((this.mPackageInfo == null) || (this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName)))
      {
        bool1 = false;
        break;
      }
      if (UserHandle.myUserId() != 0)
      {
        bool1 = false;
        break;
      }
      if (this.mUserManager.getUsers().size() >= 2) {
        break;
      }
      bool1 = false;
      break;
      bool1 = false;
      break label54;
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mFinishing) {
      return;
    }
    this.mState.requestSize(this.mPackageName, this.mUserId);
    AppItem localAppItem = new AppItem(this.mAppEntry.info.uid);
    localAppItem.addUid(this.mAppEntry.info.uid);
    if (this.mStatsSession != null) {
      getLoaderManager().restartLoader(2, ChartDataLoader.buildArgs(getTemplate(getContext()), localAppItem), this.mDataCallbacks);
    }
    new BatteryUpdater(null).execute(new Void[0]);
    new MemoryUpdater(null).execute(new Void[0]);
    updateDynamicPrefs();
  }
  
  protected boolean refreshUi()
  {
    retrieveAppEntry();
    if (this.mAppEntry == null) {
      return false;
    }
    if (this.mPackageInfo == null) {
      return false;
    }
    Object localObject1 = new ArrayList();
    this.mPm.getHomeActivities((List)localObject1);
    this.mHomePackages.clear();
    int i = 0;
    while (i < ((List)localObject1).size())
    {
      Object localObject2 = (ResolveInfo)((List)localObject1).get(i);
      String str = ((ResolveInfo)localObject2).activityInfo.packageName;
      this.mHomePackages.add(str);
      localObject2 = ((ResolveInfo)localObject2).activityInfo.metaData;
      if (localObject2 != null)
      {
        localObject2 = ((Bundle)localObject2).getString("android.app.home.alternate");
        if (signaturesMatch((String)localObject2, str)) {
          this.mHomePackages.add(localObject2);
        }
      }
      i += 1;
    }
    checkForceStop();
    setAppLabelAndIcon(this.mPackageInfo);
    initUninstallButtons();
    localObject1 = getActivity();
    this.mStoragePreference.setSummary(AppStorageSettings.getSummary(this.mAppEntry, (Context)localObject1));
    PermissionsSummaryHelper.getPermissionSummary(getContext(), this.mPackageName, this.mPermissionCallback);
    this.mLaunchPreference.setSummary(AppUtils.getLaunchByDefaultSummary(this.mAppEntry, this.mUsbManager, this.mPm, (Context)localObject1));
    this.mNotificationPreference.setSummary(getNotificationSummary(this.mAppEntry, (Context)localObject1, this.mBackend));
    if (this.mDataPreference != null) {
      this.mDataPreference.setSummary(getDataSummary());
    }
    updateBattery();
    boolean bool;
    if (!this.mInitialized)
    {
      this.mInitialized = true;
      if ((this.mAppEntry.info.flags & 0x800000) == 0)
      {
        bool = true;
        this.mShowUninstalled = bool;
      }
    }
    for (;;)
    {
      return true;
      bool = false;
      break;
      try
      {
        localObject1 = ((Activity)localObject1).getPackageManager().getApplicationInfo(this.mAppEntry.info.packageName, 8704);
        if (!this.mShowUninstalled)
        {
          i = ((ApplicationInfo)localObject1).flags;
          return (i & 0x800000) != 0;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    }
    return false;
  }
  
  private class BatteryUpdater
    extends AsyncTask<Void, Void, Void>
  {
    private BatteryUpdater() {}
    
    protected Void doInBackground(Void... paramVarArgs)
    {
      InstalledAppDetails.-get0(InstalledAppDetails.this).create((Bundle)null);
      InstalledAppDetails.-get0(InstalledAppDetails.this).refreshStats(0, InstalledAppDetails.this.mUserManager.getUserProfiles());
      paramVarArgs = InstalledAppDetails.-get0(InstalledAppDetails.this).getUsageList();
      int j = paramVarArgs.size();
      int i = 0;
      for (;;)
      {
        if (i < j)
        {
          BatterySipper localBatterySipper = (BatterySipper)paramVarArgs.get(i);
          if (localBatterySipper.getUid() == InstalledAppDetails.this.mPackageInfo.applicationInfo.uid) {
            InstalledAppDetails.-set1(InstalledAppDetails.this, localBatterySipper);
          }
        }
        else
        {
          return null;
        }
        i += 1;
      }
    }
    
    protected void onPostExecute(Void paramVoid)
    {
      if (InstalledAppDetails.this.getActivity() == null) {
        return;
      }
      InstalledAppDetails.this.refreshUi();
    }
  }
  
  private static class DisableChanger
    extends AsyncTask<Object, Object, Object>
  {
    final WeakReference<InstalledAppDetails> mActivity;
    final ApplicationInfo mInfo;
    final PackageManager mPm;
    final int mState;
    
    DisableChanger(InstalledAppDetails paramInstalledAppDetails, ApplicationInfo paramApplicationInfo, int paramInt)
    {
      this.mPm = paramInstalledAppDetails.mPm;
      this.mActivity = new WeakReference(paramInstalledAppDetails);
      this.mInfo = paramApplicationInfo;
      this.mState = paramInt;
    }
    
    protected Object doInBackground(Object... paramVarArgs)
    {
      this.mPm.setApplicationEnabledSetting(this.mInfo.packageName, this.mState, 0);
      return null;
    }
  }
  
  private class MemoryUpdater
    extends AsyncTask<Void, Void, ProcStatsPackageEntry>
  {
    private MemoryUpdater() {}
    
    protected ProcStatsPackageEntry doInBackground(Void... paramVarArgs)
    {
      if (InstalledAppDetails.this.getActivity() == null) {
        return null;
      }
      if (InstalledAppDetails.this.mPackageInfo == null) {
        return null;
      }
      if (InstalledAppDetails.this.mStatsManager == null)
      {
        InstalledAppDetails.this.mStatsManager = new ProcStatsData(InstalledAppDetails.this.getActivity(), false);
        InstalledAppDetails.this.mStatsManager.setDuration(ProcessStatsBase.sDurations[0]);
      }
      InstalledAppDetails.this.mStatsManager.refreshStats(true);
      ProcStatsPackageEntry localProcStatsPackageEntry;
      Iterator localIterator;
      do
      {
        paramVarArgs = InstalledAppDetails.this.mStatsManager.getEntries().iterator();
        while (!localIterator.hasNext())
        {
          if (!paramVarArgs.hasNext()) {
            break;
          }
          localProcStatsPackageEntry = (ProcStatsPackageEntry)paramVarArgs.next();
          localIterator = localProcStatsPackageEntry.mEntries.iterator();
        }
      } while (((ProcStatsEntry)localIterator.next()).mUid != InstalledAppDetails.this.mPackageInfo.applicationInfo.uid);
      localProcStatsPackageEntry.updateMetrics();
      return localProcStatsPackageEntry;
      return null;
    }
    
    protected void onPostExecute(ProcStatsPackageEntry paramProcStatsPackageEntry)
    {
      if (InstalledAppDetails.this.getActivity() == null) {
        return;
      }
      if (paramProcStatsPackageEntry != null)
      {
        InstalledAppDetails.this.mStats = paramProcStatsPackageEntry;
        InstalledAppDetails.-get2(InstalledAppDetails.this).setEnabled(true);
        double d1 = Math.max(paramProcStatsPackageEntry.mRunWeight, paramProcStatsPackageEntry.mBgWeight);
        double d2 = InstalledAppDetails.this.mStatsManager.getMemInfo().weightToRam;
        InstalledAppDetails.-get2(InstalledAppDetails.this).setSummary(InstalledAppDetails.this.getString(2131693506, new Object[] { Formatter.formatShortFileSize(InstalledAppDetails.this.getContext(), (d1 * d2)) }));
        return;
      }
      InstalledAppDetails.-get2(InstalledAppDetails.this).setEnabled(false);
      InstalledAppDetails.-get2(InstalledAppDetails.this).setSummary(InstalledAppDetails.this.getString(2131693507));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\InstalledAppDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */