package com.android.settings.fuelgauge;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.app.ApplicationErrorReport.BatteryInfo;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.BatteryStats;
import android.os.BatteryStats.Uid;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatterySipper.DrainType;
import com.android.internal.os.BatteryStatsHelper;
import com.android.internal.os.PowerProfile;
import com.android.internal.util.FastPrintWriter;
import com.android.settings.AppHeader;
import com.android.settings.DisplaySettings;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.WirelessSettings;
import com.android.settings.applications.InstalledAppDetails;
import com.android.settings.applications.LayoutPreference;
import com.android.settings.bluetooth.BluetoothSettings;
import com.android.settings.location.LocationSettings;
import com.android.settings.wifi.WifiSettings;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class PowerUsageDetail
  extends PowerUsageBase
  implements View.OnClickListener
{
  public static final int ACTION_APP_DETAILS = 5;
  public static final int ACTION_BLUETOOTH_SETTINGS = 3;
  public static final int ACTION_DISPLAY_SETTINGS = 1;
  public static final int ACTION_FORCE_STOP = 7;
  public static final int ACTION_LOCATION_SETTINGS = 6;
  public static final int ACTION_REPORT = 8;
  public static final int ACTION_WIFI_SETTINGS = 2;
  public static final int ACTION_WIRELESS_SETTINGS = 4;
  public static final String EXTRA_DETAIL_TYPES = "types";
  public static final String EXTRA_DETAIL_VALUES = "values";
  public static final String EXTRA_DRAIN_TYPE = "drainType";
  public static final String EXTRA_GAUGE = "gauge";
  public static final String EXTRA_ICON_ID = "iconId";
  public static final String EXTRA_ICON_PACKAGE = "iconPackage";
  public static final String EXTRA_NO_COVERAGE = "noCoverage";
  public static final String EXTRA_PERCENT = "percent";
  public static final String EXTRA_REPORT_CHECKIN_DETAILS = "report_checkin_details";
  public static final String EXTRA_REPORT_DETAILS = "report_details";
  public static final String EXTRA_SHOW_LOCATION_BUTTON = "showLocationButton";
  public static final String EXTRA_TITLE = "title";
  public static final String EXTRA_UID = "uid";
  public static final String EXTRA_USAGE_DURATION = "duration";
  public static final String EXTRA_USAGE_SINCE = "since";
  private static final String KEY_CONTROLS_PARENT = "controls_parent";
  private static final String KEY_DETAILS_PARENT = "details_parent";
  private static final String KEY_HIGH_POWER = "high_power";
  private static final String KEY_MESSAGES_PARENT = "messages_parent";
  private static final String KEY_PACKAGES_PARENT = "packages_parent";
  private static final String KEY_TWO_BUTTONS = "two_buttons";
  private static final String TAG = "PowerUsageDetail";
  public static final int USAGE_SINCE_RESET = 2;
  public static final int USAGE_SINCE_UNPLUGGED = 1;
  private static int[] sDrainTypeDesciptions = { 2131692491, 2131692492, 2131692490, 2131692498, 2131692500, 2131692494, 2131692496, 2131692503, 2131692507, 2131692508, 2131692510, 2131692495 };
  ApplicationInfo mApp;
  private final BroadcastReceiver mCheckKillProcessesReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      boolean bool = false;
      paramAnonymousContext = PowerUsageDetail.-get0(PowerUsageDetail.this);
      if (getResultCode() != 0) {
        bool = true;
      }
      paramAnonymousContext.setEnabled(bool);
    }
  };
  private PreferenceCategory mControlsParent;
  private PreferenceCategory mDetailsParent;
  private DevicePolicyManager mDpm;
  private BatterySipper.DrainType mDrainType;
  private Button mForceStopButton;
  private Preference mHighPower;
  ComponentName mInstaller;
  private PreferenceCategory mMessagesParent;
  private double mNoCoverage;
  private String[] mPackages;
  private PreferenceCategory mPackagesParent;
  private PackageManager mPm;
  private Button mReportButton;
  private boolean mShowLocationButton;
  private long mStartTime;
  private int[] mTypes;
  private int mUid;
  private int mUsageSince;
  private boolean mUsesGps;
  private double[] mValues;
  
  private void addControl(int paramInt1, int paramInt2, final int paramInt3)
  {
    Preference localPreference = new Preference(getPrefContext());
    localPreference.setTitle(paramInt2);
    localPreference.setLayoutResource(2130968717);
    localPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        PowerUsageDetail.-wrap0(PowerUsageDetail.this, paramInt3);
        return true;
      }
    });
    this.mControlsParent.addPreference(localPreference);
  }
  
  private void addHorizontalPreference(PreferenceCategory paramPreferenceCategory, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    Preference localPreference = new Preference(getPrefContext());
    localPreference.setLayoutResource(2130968717);
    localPreference.setTitle(paramCharSequence1);
    localPreference.setSummary(paramCharSequence2);
    localPreference.setSelectable(false);
    paramPreferenceCategory.addPreference(localPreference);
  }
  
  private void addMessage(int paramInt)
  {
    addHorizontalPreference(this.mMessagesParent, getString(paramInt), null);
  }
  
  private void checkForceStop()
  {
    if ((this.mPackages == null) || (this.mUid < 10000))
    {
      this.mForceStopButton.setEnabled(false);
      return;
    }
    int i = 0;
    while (i < this.mPackages.length)
    {
      if (this.mDpm.packageHasActiveAdmins(this.mPackages[i]))
      {
        this.mForceStopButton.setEnabled(false);
        return;
      }
      i += 1;
    }
    i = 0;
    for (;;)
    {
      if (i < this.mPackages.length) {}
      try
      {
        if ((this.mPm.getApplicationInfo(this.mPackages[i], 0).flags & 0x200000) == 0)
        {
          this.mForceStopButton.setEnabled(true);
          Intent localIntent = new Intent("android.intent.action.QUERY_PACKAGE_RESTART", Uri.fromParts("package", this.mPackages[0], null));
          localIntent.putExtra("android.intent.extra.PACKAGES", this.mPackages);
          localIntent.putExtra("android.intent.extra.UID", this.mUid);
          localIntent.putExtra("android.intent.extra.user_handle", UserHandle.getUserId(this.mUid));
          getActivity().sendOrderedBroadcast(localIntent, null, this.mCheckKillProcessesReceiver, null, 0, null, null);
          return;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        i += 1;
      }
    }
  }
  
  private void createDetails()
  {
    Object localObject2 = getArguments();
    Object localObject1 = getActivity();
    this.mUsageSince = ((Bundle)localObject2).getInt("since", 1);
    this.mUid = ((Bundle)localObject2).getInt("uid", 0);
    this.mPackages = ((Context)localObject1).getPackageManager().getPackagesForUid(this.mUid);
    this.mDrainType = ((BatterySipper.DrainType)((Bundle)localObject2).getSerializable("drainType"));
    this.mNoCoverage = ((Bundle)localObject2).getDouble("noCoverage", 0.0D);
    this.mShowLocationButton = ((Bundle)localObject2).getBoolean("showLocationButton");
    this.mTypes = ((Bundle)localObject2).getIntArray("types");
    this.mValues = ((Bundle)localObject2).getDoubleArray("values");
    localObject2 = (LayoutPreference)findPreference("two_buttons");
    this.mForceStopButton = ((Button)((LayoutPreference)localObject2).findViewById(2131362644));
    this.mReportButton = ((Button)((LayoutPreference)localObject2).findViewById(2131362282));
    this.mForceStopButton.setEnabled(false);
    if (this.mUid >= 10000)
    {
      this.mForceStopButton.setText(2131692087);
      this.mForceStopButton.setTag(Integer.valueOf(7));
      this.mForceStopButton.setOnClickListener(this);
      this.mReportButton.setText(17040308);
      this.mReportButton.setTag(Integer.valueOf(8));
      this.mReportButton.setOnClickListener(this);
      if ((this.mPackages == null) || (this.mPackages.length <= 0)) {}
    }
    try
    {
      this.mApp = ((Context)localObject1).getPackageManager().getApplicationInfo(this.mPackages[0], 0);
      boolean bool;
      if (Settings.Global.getInt(((Context)localObject1).getContentResolver(), "send_action_app_error", 0) != 0)
      {
        if (this.mApp != null) {
          this.mInstaller = ApplicationErrorReport.getErrorReportReceiver((Context)localObject1, this.mPackages[0], this.mApp.flags);
        }
        localObject1 = this.mReportButton;
        if (this.mInstaller != null)
        {
          bool = true;
          label306:
          ((Button)localObject1).setEnabled(bool);
          label311:
          if ((this.mApp == null) || (!PowerWhitelistBackend.getInstance().isWhitelisted(this.mApp.packageName))) {
            break label407;
          }
          this.mControlsParent.removePreference(findPreference("high_power"));
        }
      }
      for (;;)
      {
        refreshStats();
        fillDetailsSection();
        fillPackagesSection(this.mUid);
        fillControlsSection(this.mUid);
        fillMessagesSection(this.mUid);
        return;
        Log.d("PowerUsageDetail", "No packages!!");
        break;
        bool = false;
        break label306;
        removePreference("two_buttons");
        break label311;
        label407:
        this.mControlsParent.removePreference(findPreference("high_power"));
        continue;
        removePreference("two_buttons");
        this.mControlsParent.removePreference(findPreference("high_power"));
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;) {}
    }
  }
  
  private void doAction(int paramInt)
  {
    SettingsActivity localSettingsActivity = (SettingsActivity)getActivity();
    switch (paramInt)
    {
    default: 
      return;
    case 1: 
      localSettingsActivity.startPreferencePanel(DisplaySettings.class.getName(), null, 2131691547, null, null, 0);
      return;
    case 2: 
      localSettingsActivity.startPreferencePanel(WifiSettings.class.getName(), null, 2131691332, null, null, 0);
      return;
    case 3: 
      localSettingsActivity.startPreferencePanel(BluetoothSettings.class.getName(), null, 2131691235, null, null, 0);
      return;
    case 4: 
      localSettingsActivity.startPreferencePanel(WirelessSettings.class.getName(), null, 2131691003, null, null, 0);
      return;
    case 5: 
      startApplicationDetailsActivity();
      return;
    case 6: 
      localSettingsActivity.startPreferencePanel(LocationSettings.class.getName(), null, 2131691058, null, null, 0);
      return;
    case 7: 
      killProcesses();
      return;
    }
    reportBatteryUse();
  }
  
  private void fillControlsSection(int paramInt)
  {
    Object localObject2 = getActivity().getPackageManager();
    String[] arrayOfString = ((PackageManager)localObject2).getPackagesForUid(paramInt);
    Object localObject1 = null;
    if (arrayOfString != null) {}
    try
    {
      localObject2 = ((PackageManager)localObject2).getPackageInfo(arrayOfString[0], 0);
      localObject1 = localObject2;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      label53:
      int j;
      int i;
      for (;;) {}
    }
    if (localObject1 != null)
    {
      localObject1 = ((PackageInfo)localObject1).applicationInfo;
      j = 1;
      i = 1;
      paramInt = i;
      switch (-getcom-android-internal-os-BatterySipper$DrainTypeSwitchesValues()[this.mDrainType.ordinal()])
      {
      default: 
        paramInt = i;
      }
    }
    for (;;)
    {
      if (paramInt != 0) {
        this.mControlsParent.setTitle(null);
      }
      return;
      localObject1 = null;
      break;
      break label53;
      i = j;
      if (arrayOfString != null)
      {
        i = j;
        if (arrayOfString.length == 1)
        {
          addControl(2131692485, 2131692504, 5);
          i = 0;
        }
      }
      paramInt = i;
      if (this.mUsesGps)
      {
        paramInt = i;
        if (this.mShowLocationButton)
        {
          addControl(2131691058, 2131692505, 6);
          paramInt = 0;
          continue;
          addControl(2131691596, 2131692497, 1);
          paramInt = 0;
          continue;
          addControl(2131691332, 2131692499, 2);
          paramInt = 0;
          continue;
          addControl(2131691235, 2131692501, 3);
          paramInt = 0;
          continue;
          paramInt = i;
          if (this.mNoCoverage > 10.0D)
          {
            addControl(2131691003, 2131692493, 4);
            paramInt = 0;
          }
        }
      }
    }
  }
  
  private void fillDetailsSection()
  {
    if ((this.mTypes != null) && (this.mValues != null))
    {
      int i = 0;
      while (i < this.mTypes.length) {
        if (this.mValues[i] <= 0.0D)
        {
          i += 1;
        }
        else
        {
          String str2 = getString(this.mTypes[i]);
          switch (this.mTypes[i])
          {
          }
          for (;;)
          {
            String str1 = Utils.formatElapsedTime(getActivity(), this.mValues[i], true);
            for (;;)
            {
              addHorizontalPreference(this.mDetailsParent, str2, str1);
              break;
              str1 = Long.toString(this.mValues[i]);
              continue;
              str1 = Utils.formatPercentage((int)Math.floor(this.mValues[i]));
              continue;
              str1 = getActivity().getString(2131692511, new Object[] { Long.valueOf(this.mValues[i]) });
            }
            this.mUsesGps = true;
          }
        }
      }
    }
  }
  
  private void fillMessagesSection(int paramInt)
  {
    paramInt = 1;
    switch (-getcom-android-internal-os-BatterySipper$DrainTypeSwitchesValues()[this.mDrainType.ordinal()])
    {
    }
    for (;;)
    {
      if (paramInt != 0) {
        this.mMessagesParent.setTitle(null);
      }
      return;
      addMessage(2131692509);
      paramInt = 0;
    }
  }
  
  private void fillPackagesSection(int paramInt)
  {
    if (paramInt < 1)
    {
      removePackagesSection();
      return;
    }
    if ((this.mPackages == null) || (this.mPackages.length < 2))
    {
      removePackagesSection();
      return;
    }
    PackageManager localPackageManager = getPackageManager();
    paramInt = 0;
    while (paramInt < this.mPackages.length)
    {
      try
      {
        CharSequence localCharSequence = localPackageManager.getApplicationInfo(this.mPackages[paramInt], 0).loadLabel(localPackageManager);
        if (localCharSequence != null) {
          this.mPackages[paramInt] = localCharSequence.toString();
        }
        addHorizontalPreference(this.mPackagesParent, this.mPackages[paramInt], null);
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        for (;;) {}
      }
      paramInt += 1;
    }
  }
  
  private void killProcesses()
  {
    if (this.mPackages == null) {
      return;
    }
    ActivityManager localActivityManager = (ActivityManager)getActivity().getSystemService("activity");
    int j = UserHandle.getUserId(this.mUid);
    int i = 0;
    while (i < this.mPackages.length)
    {
      localActivityManager.forceStopPackageAsUser(this.mPackages[i], j);
      i += 1;
    }
    checkForceStop();
  }
  
  private void removePackagesSection()
  {
    getPreferenceScreen().removePreference(this.mPackagesParent);
  }
  
  private void reportBatteryUse()
  {
    boolean bool = false;
    if (this.mPackages == null) {
      return;
    }
    ApplicationErrorReport localApplicationErrorReport = new ApplicationErrorReport();
    localApplicationErrorReport.type = 3;
    localApplicationErrorReport.packageName = this.mPackages[0];
    localApplicationErrorReport.installerPackageName = this.mInstaller.getPackageName();
    localApplicationErrorReport.processName = this.mPackages[0];
    localApplicationErrorReport.time = System.currentTimeMillis();
    if ((this.mApp.flags & 0x1) != 0) {
      bool = true;
    }
    localApplicationErrorReport.systemApp = bool;
    Object localObject = getArguments();
    ApplicationErrorReport.BatteryInfo localBatteryInfo = new ApplicationErrorReport.BatteryInfo();
    localBatteryInfo.usagePercent = ((Bundle)localObject).getInt("percent", 1);
    localBatteryInfo.durationMicros = ((Bundle)localObject).getLong("duration", 0L);
    localBatteryInfo.usageDetails = ((Bundle)localObject).getString("report_details");
    localBatteryInfo.checkinDetails = ((Bundle)localObject).getString("report_checkin_details");
    localApplicationErrorReport.batteryInfo = localBatteryInfo;
    localObject = new Intent("android.intent.action.APP_ERROR");
    ((Intent)localObject).setComponent(this.mInstaller);
    ((Intent)localObject).putExtra("android.intent.extra.BUG_REPORT", localApplicationErrorReport);
    ((Intent)localObject).addFlags(268435456);
    startActivity((Intent)localObject);
  }
  
  private void setupHeader()
  {
    int j = 0;
    Object localObject1 = getArguments();
    String str2 = ((Bundle)localObject1).getString("title");
    String str1 = ((Bundle)localObject1).getString("iconPackage");
    int m = ((Bundle)localObject1).getInt("iconId", 0);
    Object localObject2 = null;
    Object localObject3 = null;
    k = -1;
    PackageManager localPackageManager = getActivity().getPackageManager();
    if (!TextUtils.isEmpty(str1)) {
      localObject1 = localObject2;
    }
    for (;;)
    {
      try
      {
        ApplicationInfo localApplicationInfo = localPackageManager.getPackageInfo(str1, 0).applicationInfo;
        localObject1 = localObject3;
        i = k;
        if (localApplicationInfo != null)
        {
          localObject1 = localObject2;
          localObject2 = localApplicationInfo.loadIcon(localPackageManager);
          localObject1 = localObject2;
          i = localApplicationInfo.uid;
          localObject1 = localObject2;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        int i = k;
        continue;
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = getActivity().getPackageManager().getDefaultActivityIcon();
      }
      localObject1 = str1;
      if (str1 == null)
      {
        localObject1 = str1;
        if (this.mPackages != null) {
          localObject1 = this.mPackages[0];
        }
      }
      if (this.mDrainType != BatterySipper.DrainType.APP) {
        j = 2131493779;
      }
      AppHeader.createAppHeader(this, (Drawable)localObject2, str2, (String)localObject1, i, j);
      return;
      localObject1 = localObject3;
      i = k;
      if (m != 0)
      {
        localObject1 = getActivity().getDrawable(m);
        i = k;
      }
    }
  }
  
  private void startApplicationDetailsActivity()
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("package", this.mPackages[0]);
    ((SettingsActivity)getActivity()).startPreferencePanel(InstalledAppDetails.class.getName(), localBundle, 2131692076, null, null, 0);
  }
  
  public static void startBatteryDetailPage(SettingsActivity paramSettingsActivity, BatteryStatsHelper paramBatteryStatsHelper, int paramInt, BatteryEntry paramBatteryEntry, boolean paramBoolean1, boolean paramBoolean2)
  {
    paramBatteryStatsHelper.getStats();
    paramInt = paramBatteryStatsHelper.getStats().getDischargeAmount(paramInt);
    Bundle localBundle = new Bundle();
    localBundle.putString("title", paramBatteryEntry.name);
    localBundle.putInt("percent", (int)(paramBatteryEntry.sipper.totalPowerMah * paramInt / paramBatteryStatsHelper.getTotalPower() + 0.5D));
    localBundle.putInt("gauge", (int)Math.ceil(paramBatteryEntry.sipper.totalPowerMah * 100.0D / paramBatteryStatsHelper.getMaxPower()));
    localBundle.putLong("duration", paramBatteryStatsHelper.getStatsPeriod());
    localBundle.putString("iconPackage", paramBatteryEntry.defaultPackageName);
    localBundle.putInt("iconId", paramBatteryEntry.iconId);
    localBundle.putDouble("noCoverage", paramBatteryEntry.sipper.noCoveragePercent);
    if (paramBatteryEntry.sipper.uidObj != null) {
      localBundle.putInt("uid", paramBatteryEntry.sipper.uidObj.getUid());
    }
    localBundle.putSerializable("drainType", paramBatteryEntry.sipper.drainType);
    localBundle.putBoolean("showLocationButton", paramBoolean1);
    int i;
    Object localObject1;
    Object localObject2;
    if (paramBoolean2)
    {
      paramBoolean1 = false;
      localBundle.putBoolean("hideInfoButton", paramBoolean1);
      i = UserHandle.myUserId();
      switch (-getcom-android-internal-os-BatterySipper$DrainTypeSwitchesValues()[paramBatteryEntry.sipper.drainType.ordinal()])
      {
      case 5: 
      default: 
        localObject1 = new int[] { 2131692479, 2131692482 };
        localObject2 = new double[2];
        localObject2[0] = paramBatteryEntry.sipper.usageTimeMs;
        localObject2[1] = paramBatteryEntry.sipper.totalPowerMah;
        paramInt = i;
      }
    }
    for (;;)
    {
      localBundle.putIntArray("types", (int[])localObject1);
      localBundle.putDoubleArray("values", (double[])localObject2);
      paramSettingsActivity.startPreferencePanelAsUser(PowerUsageDetail.class.getName(), localBundle, 2131692450, null, new UserHandle(paramInt));
      return;
      paramBoolean1 = true;
      break;
      BatteryStats.Uid localUid = paramBatteryEntry.sipper.uidObj;
      int[] arrayOfInt = new int[15];
      int[] tmp381_379 = arrayOfInt;
      tmp381_379[0] = 2131692464;
      int[] tmp387_381 = tmp381_379;
      tmp387_381[1] = 2131692465;
      int[] tmp393_387 = tmp387_381;
      tmp393_387[2] = 2131692466;
      int[] tmp399_393 = tmp393_387;
      tmp399_393[3] = 2131692467;
      int[] tmp405_399 = tmp399_393;
      tmp405_399[4] = 2131692468;
      int[] tmp411_405 = tmp405_399;
      tmp411_405[5] = 2131692471;
      int[] tmp417_411 = tmp411_405;
      tmp417_411[6] = 2131692470;
      int[] tmp424_417 = tmp417_411;
      tmp424_417[7] = 2131692472;
      int[] tmp431_424 = tmp424_417;
      tmp431_424[8] = 2131692474;
      int[] tmp438_431 = tmp431_424;
      tmp438_431[9] = 2131692473;
      int[] tmp445_438 = tmp438_431;
      tmp445_438[10] = 2131692475;
      int[] tmp452_445 = tmp445_438;
      tmp452_445[11] = 2131692476;
      int[] tmp459_452 = tmp452_445;
      tmp459_452[12] = 2131692477;
      int[] tmp466_459 = tmp459_452;
      tmp466_459[13] = 2131692478;
      int[] tmp473_466 = tmp466_459;
      tmp473_466[14] = 2131692482;
      tmp473_466;
      double[] arrayOfDouble = new double[15];
      arrayOfDouble[0] = paramBatteryEntry.sipper.cpuTimeMs;
      arrayOfDouble[1] = paramBatteryEntry.sipper.cpuFgTimeMs;
      arrayOfDouble[2] = paramBatteryEntry.sipper.wakeLockTimeMs;
      arrayOfDouble[3] = paramBatteryEntry.sipper.gpsTimeMs;
      arrayOfDouble[4] = paramBatteryEntry.sipper.wifiRunningTimeMs;
      arrayOfDouble[5] = paramBatteryEntry.sipper.mobileRxPackets;
      arrayOfDouble[6] = paramBatteryEntry.sipper.mobileTxPackets;
      arrayOfDouble[7] = paramBatteryEntry.sipper.mobileActive;
      arrayOfDouble[8] = paramBatteryEntry.sipper.wifiRxPackets;
      arrayOfDouble[9] = paramBatteryEntry.sipper.wifiTxPackets;
      arrayOfDouble[10] = 0.0D;
      arrayOfDouble[11] = 0.0D;
      arrayOfDouble[12] = paramBatteryEntry.sipper.cameraTimeMs;
      arrayOfDouble[13] = paramBatteryEntry.sipper.flashlightTimeMs;
      arrayOfDouble[14] = paramBatteryEntry.sipper.totalPowerMah;
      localObject1 = arrayOfInt;
      paramInt = i;
      localObject2 = arrayOfDouble;
      if (paramBatteryEntry.sipper.drainType == BatterySipper.DrainType.APP)
      {
        paramBatteryEntry = new StringWriter();
        localObject1 = new FastPrintWriter(paramBatteryEntry, false, 1024);
        paramBatteryStatsHelper.getStats().dumpLocked(paramSettingsActivity, (PrintWriter)localObject1, "", paramBatteryStatsHelper.getStatsType(), localUid.getUid());
        ((PrintWriter)localObject1).flush();
        localBundle.putString("report_details", paramBatteryEntry.toString());
        paramBatteryEntry = new StringWriter();
        localObject1 = new FastPrintWriter(paramBatteryEntry, false, 1024);
        paramBatteryStatsHelper.getStats().dumpCheckinLocked(paramSettingsActivity, (PrintWriter)localObject1, paramBatteryStatsHelper.getStatsType(), localUid.getUid());
        ((PrintWriter)localObject1).flush();
        localBundle.putString("report_checkin_details", paramBatteryEntry.toString());
        localObject1 = arrayOfInt;
        paramInt = i;
        localObject2 = arrayOfDouble;
        if (localUid.getUid() != 0)
        {
          paramInt = UserHandle.getUserId(localUid.getUid());
          localObject1 = arrayOfInt;
          localObject2 = arrayOfDouble;
          continue;
          localObject1 = new int[] { 2131692479, 2131692480, 2131692472, 2131692482 };
          localObject2 = new double[4];
          localObject2[0] = paramBatteryEntry.sipper.usageTimeMs;
          localObject2[1] = paramBatteryEntry.sipper.noCoveragePercent;
          localObject2[2] = paramBatteryEntry.sipper.mobileActive;
          localObject2[3] = paramBatteryEntry.sipper.totalPowerMah;
          paramInt = i;
          continue;
          localObject1 = new int[9];
          Object tmp935_933 = localObject1;
          tmp935_933[0] = 2131692468;
          Object tmp941_935 = tmp935_933;
          tmp941_935[1] = 2131692464;
          Object tmp947_941 = tmp941_935;
          tmp947_941[2] = 2131692465;
          Object tmp953_947 = tmp947_941;
          tmp953_947[3] = 2131692466;
          Object tmp959_953 = tmp953_947;
          tmp959_953[4] = 2131692471;
          Object tmp965_959 = tmp959_953;
          tmp965_959[5] = 2131692470;
          Object tmp971_965 = tmp965_959;
          tmp971_965[6] = 2131692474;
          Object tmp978_971 = tmp971_965;
          tmp978_971[7] = 2131692473;
          Object tmp985_978 = tmp978_971;
          tmp985_978[8] = 2131692482;
          tmp985_978;
          localObject2 = new double[9];
          localObject2[0] = paramBatteryEntry.sipper.wifiRunningTimeMs;
          localObject2[1] = paramBatteryEntry.sipper.cpuTimeMs;
          localObject2[2] = paramBatteryEntry.sipper.cpuFgTimeMs;
          localObject2[3] = paramBatteryEntry.sipper.wakeLockTimeMs;
          localObject2[4] = paramBatteryEntry.sipper.mobileRxPackets;
          localObject2[5] = paramBatteryEntry.sipper.mobileTxPackets;
          localObject2[6] = paramBatteryEntry.sipper.wifiRxPackets;
          localObject2[7] = paramBatteryEntry.sipper.wifiTxPackets;
          localObject2[8] = paramBatteryEntry.sipper.totalPowerMah;
          paramInt = i;
          continue;
          localObject1 = new int[9];
          Object tmp1123_1121 = localObject1;
          tmp1123_1121[0] = 2131692479;
          Object tmp1129_1123 = tmp1123_1121;
          tmp1129_1123[1] = 2131692464;
          Object tmp1135_1129 = tmp1129_1123;
          tmp1135_1129[2] = 2131692465;
          Object tmp1141_1135 = tmp1135_1129;
          tmp1141_1135[3] = 2131692466;
          Object tmp1147_1141 = tmp1141_1135;
          tmp1147_1141[4] = 2131692471;
          Object tmp1153_1147 = tmp1147_1141;
          tmp1153_1147[5] = 2131692470;
          Object tmp1159_1153 = tmp1153_1147;
          tmp1159_1153[6] = 2131692474;
          Object tmp1166_1159 = tmp1159_1153;
          tmp1166_1159[7] = 2131692473;
          Object tmp1173_1166 = tmp1166_1159;
          tmp1173_1166[8] = 2131692482;
          tmp1173_1166;
          localObject2 = new double[9];
          localObject2[0] = paramBatteryEntry.sipper.usageTimeMs;
          localObject2[1] = paramBatteryEntry.sipper.cpuTimeMs;
          localObject2[2] = paramBatteryEntry.sipper.cpuFgTimeMs;
          localObject2[3] = paramBatteryEntry.sipper.wakeLockTimeMs;
          localObject2[4] = paramBatteryEntry.sipper.mobileRxPackets;
          localObject2[5] = paramBatteryEntry.sipper.mobileTxPackets;
          localObject2[6] = paramBatteryEntry.sipper.wifiRxPackets;
          localObject2[7] = paramBatteryEntry.sipper.wifiTxPackets;
          localObject2[8] = paramBatteryEntry.sipper.totalPowerMah;
          paramInt = i;
          continue;
          localObject1 = new int[] { 2131692481, 2131692482, 2131692483 };
          localObject2 = new double[3];
          localObject2[0] = paramBatteryStatsHelper.getPowerProfile().getBatteryCapacity();
          localObject2[1] = paramBatteryStatsHelper.getComputedPower();
          localObject2[2] = paramBatteryStatsHelper.getMinDrainedPower();
          paramInt = i;
          continue;
          localObject1 = new int[] { 2131692481, 2131692482, 2131692483 };
          localObject2 = new double[3];
          localObject2[0] = paramBatteryStatsHelper.getPowerProfile().getBatteryCapacity();
          localObject2[1] = paramBatteryStatsHelper.getComputedPower();
          localObject2[2] = paramBatteryStatsHelper.getMaxDrainedPower();
          paramInt = i;
        }
      }
    }
  }
  
  protected int getMetricsCategory()
  {
    return 53;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (this.mHighPower != null) {
      this.mHighPower.setSummary(HighPowerDetail.getSummary(getActivity(), this.mApp.packageName));
    }
  }
  
  public void onClick(View paramView)
  {
    doAction(((Integer)paramView.getTag()).intValue());
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPm = getActivity().getPackageManager();
    this.mDpm = ((DevicePolicyManager)getActivity().getSystemService("device_policy"));
    addPreferencesFromResource(2131230826);
    this.mDetailsParent = ((PreferenceCategory)findPreference("details_parent"));
    this.mControlsParent = ((PreferenceCategory)findPreference("controls_parent"));
    this.mMessagesParent = ((PreferenceCategory)findPreference("messages_parent"));
    this.mPackagesParent = ((PreferenceCategory)findPreference("packages_parent"));
    createDetails();
  }
  
  public void onResume()
  {
    super.onResume();
    this.mStartTime = Process.getElapsedCpuTime();
    checkForceStop();
    if (this.mHighPower != null) {
      this.mHighPower.setSummary(HighPowerDetail.getSummary(getActivity(), this.mApp.packageName));
    }
    setupHeader();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\PowerUsageDetail.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */