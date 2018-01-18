package com.android.settings.fuelgauge;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.BatteryStats;
import android.os.BatteryStats.Uid;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatterySipper.DrainType;
import com.android.internal.os.BatteryStatsHelper;
import com.android.internal.os.PowerProfile;
import com.android.settings.Settings.BgOptimizeAppListActivity;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settingslib.BatteryInfo;
import com.android.settingslib.BatteryInfo.Callback;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.backgroundoptimize.Logutil;
import com.oneplus.settings.highpowerapp.HighPowerApp;
import com.oneplus.settings.highpowerapp.HighPowerAppModel;
import com.oneplus.settings.highpowerapp.IHighPowerAppObserver;
import com.oneplus.settings.utils.OPUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PowerUsageSummary
  extends PowerUsageBase
  implements IHighPowerAppObserver
{
  private static final boolean DEBUG = false;
  private static final String KEY_APP_LIST = "app_list";
  private static final String KEY_BATTERY_HISTORY = "battery_history";
  private static final int MAX_ITEMS_TO_LIST = 10;
  private static final int MENU_HELP = 6;
  private static final int MENU_HIGH_POWER_APPS = 4;
  private static final int MENU_HIGH_POWER_APPS_MANAGER = 5;
  private static final int MENU_STATS_TYPE = 1;
  private static final int MIN_AVERAGE_POWER_THRESHOLD_MILLI_AMP = 10;
  private static final int MIN_POWER_THRESHOLD_MILLI_AMP = 5;
  private static final int MSG_INTERVAL = 10000;
  private static final int MSG_UPDATE = 1000;
  private static final String OP_BACKGROYUND_OPTIMIZE = "op_backgroyund_optimize";
  private static final int SECONDS_IN_HOUR = 3600;
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new PowerUsageSummary.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader, null);
    }
  };
  static final String TAG = "PowerUsageSummary";
  private static final boolean USE_FAKE_DATA = false;
  private PreferenceGroup mAppListGroup;
  private View.OnClickListener mForceCloseListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      paramAnonymousView = paramAnonymousView.getTag();
      if ((paramAnonymousView != null) && ((paramAnonymousView instanceof String)))
      {
        PowerUsageSummary.-get0(PowerUsageSummary.this).stopApp((String)paramAnonymousView);
        PowerUsageSummary.-get0(PowerUsageSummary.this).update();
      }
    }
  };
  Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      }
      for (;;)
      {
        super.handleMessage(paramAnonymousMessage);
        return;
        Object localObject = (BatteryEntry)paramAnonymousMessage.obj;
        PowerGaugePreference localPowerGaugePreference = (PowerGaugePreference)PowerUsageSummary.this.findPreference(Integer.toString(((BatteryEntry)localObject).sipper.uidObj.getUid()));
        if (localPowerGaugePreference != null)
        {
          UserHandle localUserHandle = new UserHandle(UserHandle.getUserId(((BatteryEntry)localObject).sipper.getUid()));
          localPowerGaugePreference.setIcon(PowerUsageSummary.this.mUm.getBadgedIconForUser(((BatteryEntry)localObject).getIcon(), localUserHandle));
          localPowerGaugePreference.setTitle(((BatteryEntry)localObject).name);
          if (((BatteryEntry)localObject).sipper.drainType == BatterySipper.DrainType.APP)
          {
            localPowerGaugePreference.setContentDescription(((BatteryEntry)localObject).name);
            continue;
            localObject = PowerUsageSummary.this.getActivity();
            if (localObject != null)
            {
              ((Activity)localObject).reportFullyDrawn();
              continue;
              PowerUsageSummary.-get0(PowerUsageSummary.this).update();
              if (!PowerUsageSummary.-get1(PowerUsageSummary.this)) {
                PowerUsageSummary.-wrap0(PowerUsageSummary.this);
              }
            }
          }
        }
      }
    }
  };
  private HighPowerAppModel mHighPowerAppModel;
  private BatteryHistoryPreference mHistPref;
  private boolean mPauseUpdate;
  private int mStatsType = 0;
  
  private void addNotAvailableMessage()
  {
    if (getCachedPreference("not_available") == null)
    {
      Preference localPreference = new Preference(getPrefContext());
      localPreference.setKey("not_available");
      localPreference.setTitle(2131692428);
      this.mAppListGroup.addPreference(localPreference);
    }
  }
  
  private List<BatterySipper> concatHighPowerApp(List<BatterySipper> paramList, BatteryStats paramBatteryStats)
  {
    Object localObject1 = paramList;
    if (paramList == null) {
      localObject1 = new ArrayList();
    }
    paramList = new HashMap();
    Object localObject2 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      BatterySipper localBatterySipper = (BatterySipper)((Iterator)localObject2).next();
      paramList.put(Integer.valueOf(localBatterySipper.getUid()), localBatterySipper);
    }
    if (this.mHighPowerAppModel != null)
    {
      localObject2 = this.mHighPowerAppModel.getDataList();
      if ((localObject2 != null) && (((List)localObject2).size() > 0))
      {
        int i;
        double d;
        if (paramBatteryStats != null)
        {
          i = paramBatteryStats.getDischargeAmount(this.mStatsType);
          d = this.mStatsHelper.getTotalPower();
          if ((i != 0) && (0.0D != d)) {
            break label307;
          }
          d = 1.0D;
          label141:
          Logutil.loge("PowerUsageSummary", "concatHighPowerApp list.size=" + ((List)localObject2).size());
          paramBatteryStats = ((Iterable)localObject2).iterator();
        }
        for (;;)
        {
          if (!paramBatteryStats.hasNext()) {
            break label400;
          }
          localObject2 = (HighPowerApp)paramBatteryStats.next();
          if (!paramList.containsKey(Integer.valueOf(((HighPowerApp)localObject2).uid)))
          {
            Logutil.loge("PowerUsageSummary", "concatHighPowerApp add pkg=" + ((HighPowerApp)localObject2).pkgName + ", uid=" + ((HighPowerApp)localObject2).uid);
            localObject2 = new BatterySipper(BatterySipper.DrainType.APP, new FakeUid(((HighPowerApp)localObject2).uid), 1.0D);
            ((BatterySipper)localObject2).totalPowerMah = d;
            ((List)localObject1).add(localObject2);
            continue;
            i = 0;
            break;
            label307:
            d = 0.6D / i * d;
            break label141;
          }
          Logutil.loge("PowerUsageSummary", "concatHighPowerApp exist pkg=" + ((HighPowerApp)localObject2).pkgName + ", uid=" + ((HighPowerApp)localObject2).uid);
          localObject2 = (BatterySipper)paramList.get(Integer.valueOf(((HighPowerApp)localObject2).uid));
          if (((BatterySipper)localObject2).totalPowerMah < d) {
            ((BatterySipper)localObject2).totalPowerMah = d;
          }
        }
      }
    }
    label400:
    return (List<BatterySipper>)localObject1;
  }
  
  private static List<BatterySipper> getCoalescedUsageList(List<BatterySipper> paramList)
  {
    SparseArray localSparseArray = new SparseArray();
    ArrayList localArrayList = new ArrayList();
    int m = paramList.size();
    int j = 0;
    if (j < m)
    {
      BatterySipper localBatterySipper2 = (BatterySipper)paramList.get(j);
      int k;
      label109:
      BatterySipper localBatterySipper1;
      if (localBatterySipper2.getUid() > 0)
      {
        i = localBatterySipper2.getUid();
        if (isSharedGid(localBatterySipper2.getUid())) {
          i = UserHandle.getUid(0, UserHandle.getAppIdFromSharedAppGid(localBatterySipper2.getUid()));
        }
        k = i;
        if (isSystemUid(i))
        {
          if ("mediaserver".equals(localBatterySipper2.packageWithHighestDrain)) {
            k = i;
          }
        }
        else
        {
          localBatterySipper1 = localBatterySipper2;
          if (k != localBatterySipper2.getUid())
          {
            localBatterySipper1 = new BatterySipper(localBatterySipper2.drainType, new FakeUid(k), 0.0D);
            localBatterySipper1.add(localBatterySipper2);
            localBatterySipper1.packageWithHighestDrain = localBatterySipper2.packageWithHighestDrain;
            localBatterySipper1.mPackages = localBatterySipper2.mPackages;
          }
          i = localSparseArray.indexOfKey(k);
          if (i >= 0) {
            break label205;
          }
          localSparseArray.put(k, localBatterySipper1);
        }
      }
      for (;;)
      {
        j += 1;
        break;
        k = 1000;
        break label109;
        label205:
        localBatterySipper2 = (BatterySipper)localSparseArray.valueAt(i);
        localBatterySipper2.add(localBatterySipper1);
        if ((localBatterySipper2.packageWithHighestDrain == null) && (localBatterySipper1.packageWithHighestDrain != null)) {
          localBatterySipper2.packageWithHighestDrain = localBatterySipper1.packageWithHighestDrain;
        }
        if (localBatterySipper2.mPackages != null)
        {
          i = localBatterySipper2.mPackages.length;
          label264:
          if (localBatterySipper1.mPackages == null) {
            break label336;
          }
        }
        label336:
        for (k = localBatterySipper1.mPackages.length;; k = 0)
        {
          if (k <= 0) {
            break label339;
          }
          String[] arrayOfString = new String[i + k];
          if (i > 0) {
            System.arraycopy(localBatterySipper2.mPackages, 0, arrayOfString, 0, i);
          }
          System.arraycopy(localBatterySipper1.mPackages, 0, arrayOfString, i, k);
          localBatterySipper2.mPackages = arrayOfString;
          break;
          i = 0;
          break label264;
        }
        label339:
        continue;
        localArrayList.add(localBatterySipper2);
      }
    }
    j = localSparseArray.size();
    int i = 0;
    while (i < j)
    {
      localArrayList.add((BatterySipper)localSparseArray.valueAt(i));
      i += 1;
    }
    Collections.sort(localArrayList, new Comparator()
    {
      public int compare(BatterySipper paramAnonymousBatterySipper1, BatterySipper paramAnonymousBatterySipper2)
      {
        return Double.compare(paramAnonymousBatterySipper2.totalPowerMah, paramAnonymousBatterySipper1.totalPowerMah);
      }
    });
    return localArrayList;
  }
  
  private static List<BatterySipper> getFakeStats()
  {
    ArrayList localArrayList = new ArrayList();
    float f = 5.0F;
    Object localObject = BatterySipper.DrainType.values();
    int j = localObject.length;
    int i = 0;
    if (i < j)
    {
      BatterySipper.DrainType localDrainType = localObject[i];
      if (localDrainType == BatterySipper.DrainType.APP) {}
      for (;;)
      {
        i += 1;
        break;
        localArrayList.add(new BatterySipper(localDrainType, null, f));
        f += 5.0F;
      }
    }
    i = 0;
    while (i < 100)
    {
      localArrayList.add(new BatterySipper(BatterySipper.DrainType.APP, new FakeUid(i + 10000), f));
      i += 1;
    }
    localArrayList.add(new BatterySipper(BatterySipper.DrainType.APP, new FakeUid(0), f));
    localObject = new BatterySipper(BatterySipper.DrainType.APP, new FakeUid(UserHandle.getSharedAppGid(10000)), 10.0D);
    ((BatterySipper)localObject).packageWithHighestDrain = "dex2oat";
    localArrayList.add(localObject);
    localObject = new BatterySipper(BatterySipper.DrainType.APP, new FakeUid(UserHandle.getSharedAppGid(10001)), 10.0D);
    ((BatterySipper)localObject).packageWithHighestDrain = "dex2oat";
    localArrayList.add(localObject);
    localArrayList.add(new BatterySipper(BatterySipper.DrainType.APP, new FakeUid(UserHandle.getSharedAppGid(1007)), 9.0D));
    return localArrayList;
  }
  
  private int getHighPowerAppsTitle()
  {
    if (isSupportBackgroundManagerment()) {
      return 2131690020;
    }
    return 2131693459;
  }
  
  private static boolean isSharedGid(int paramInt)
  {
    boolean bool = false;
    if (UserHandle.getAppIdFromSharedAppGid(paramInt) > 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isSupportBackgroundManagerment()
  {
    return getActivity().getPackageManager().hasSystemFeature("oem.background.control");
  }
  
  private static boolean isSystemUid(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 1000)
    {
      bool1 = bool2;
      if (paramInt < 10000) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private void nextUpdate()
  {
    if (!OPUtils.isGuestMode())
    {
      Message localMessage = this.mHandler.obtainMessage(1000);
      this.mHandler.sendMessageDelayed(localMessage, 10000L);
    }
  }
  
  private void rebind(BatteryStats paramBatteryStats)
  {
    updatePreference(this.mHistPref);
    cacheRemoveAllPrefs(this.mAppListGroup);
    this.mAppListGroup.setOrderingAsAdded(false);
    int i = 0;
    int m = 0;
    if (paramBatteryStats != null)
    {
      int n = SettingsBaseApplication.mApplication.getColor(2131493777);
      Object localObject2 = getCoalescedUsageList(new ArrayList(this.mStatsHelper.getUsageList()));
      Object localObject1 = localObject2;
      if (!OPUtils.isGuestMode()) {
        localObject1 = concatHighPowerApp((List)localObject2, paramBatteryStats);
      }
      HashMap localHashMap = new HashMap();
      HashSet localHashSet = new HashSet();
      if ((this.mHighPowerAppModel == null) || (getActivity() == null)) {}
      while (getActivity().isFinishing()) {
        return;
      }
      localObject2 = this.mHighPowerAppModel.getDataList();
      i = 0;
      while ((localObject2 != null) && (i < ((List)localObject2).size()))
      {
        localHashSet.add(Integer.valueOf(((HighPowerApp)((List)localObject2).get(i)).uid));
        i += 1;
      }
      getPrefContext().getString(2131690303);
      int j;
      int k;
      label236:
      BatterySipper localBatterySipper;
      double d1;
      double d2;
      if (paramBatteryStats != null)
      {
        j = paramBatteryStats.getDischargeAmount(this.mStatsType);
        int i1 = ((List)localObject1).size();
        getActivity();
        k = 0;
        i = m;
        if (k >= i1) {
          break label846;
        }
        localBatterySipper = (BatterySipper)((List)localObject1).get(k);
        if (localBatterySipper.totalPowerMah * 3600.0D < 5.0D)
        {
          m = i;
          if (!localHashSet.contains(Integer.valueOf(localBatterySipper.getUid()))) {}
        }
        else
        {
          d1 = this.mStatsHelper.getTotalPower();
          d2 = localBatterySipper.totalPowerMah / d1 * j;
          d1 = d2;
          if ((int)(0.5D + d2) < 1)
          {
            d1 = d2;
            if (localHashSet.contains(Integer.valueOf(localBatterySipper.getUid()))) {
              d1 = 0.5D;
            }
          }
          if ((int)(0.5D + d1) >= 1) {
            break label388;
          }
          m = i;
        }
      }
      for (;;)
      {
        k += 1;
        i = m;
        break label236;
        j = 0;
        break;
        label388:
        if (localBatterySipper.drainType == BatterySipper.DrainType.OVERCOUNTED)
        {
          m = i;
          if (localBatterySipper.totalPowerMah >= this.mStatsHelper.getMaxRealPower() * 2.0D / 3.0D)
          {
            m = i;
            if (d1 >= 10.0D)
            {
              m = i;
              if ("user".equals(Build.TYPE)) {}
            }
          }
        }
        else if (localBatterySipper.drainType == BatterySipper.DrainType.UNACCOUNTED)
        {
          m = i;
          if (localBatterySipper.totalPowerMah >= this.mStatsHelper.getMaxRealPower() / 2.0D)
          {
            m = i;
            if (d1 >= 5.0D)
            {
              m = i;
              if ("user".equals(Build.TYPE)) {}
            }
          }
        }
        else
        {
          paramBatteryStats = new UserHandle(UserHandle.getUserId(localBatterySipper.getUid()));
          BatteryEntry localBatteryEntry = new BatteryEntry(getPrefContext(), this.mHandler, this.mUm, localBatterySipper);
          Drawable localDrawable = this.mUm.getBadgedIconForUser(localBatteryEntry.getIcon(), paramBatteryStats);
          CharSequence localCharSequence = this.mUm.getBadgedLabelForUser(localBatteryEntry.getLabel(), paramBatteryStats);
          String str = extractKeyFromSipper(localBatterySipper);
          localObject2 = (PowerGaugePreference)getCachedPreference(str);
          paramBatteryStats = (BatteryStats)localObject2;
          if (localObject2 == null)
          {
            paramBatteryStats = new PowerGaugePreference(getPrefContext(), localDrawable, localCharSequence, localBatteryEntry);
            paramBatteryStats.setKey(str);
          }
          d2 = localBatterySipper.totalPowerMah * 100.0D / this.mStatsHelper.getMaxPower();
          localBatterySipper.percent = d1;
          paramBatteryStats.setTitle(localBatteryEntry.getLabel());
          paramBatteryStats.setOrder(k + 1);
          paramBatteryStats.setPercent(d2, d1);
          paramBatteryStats.setOnButtonClickListener(this.mForceCloseListener);
          localHashMap.put(Integer.valueOf(localBatterySipper.getUid()), paramBatteryStats);
          if (((localBatterySipper.drainType != BatterySipper.DrainType.APP) || (localBatterySipper.uidObj.getUid() == 0)) && (localBatterySipper.drainType != BatterySipper.DrainType.USER)) {
            paramBatteryStats.setTint(n);
          }
          i = 1;
          this.mAppListGroup.addPreference(paramBatteryStats);
          Log.d("PowerUsageSummary", "rebind addPreference i= " + k + ", name=" + localBatteryEntry.getLabel() + ", totalPowerMah=" + localBatterySipper.totalPowerMah);
          m = i;
          if (this.mAppListGroup.getPreferenceCount() - getCachedCount() > 11) {
            m = i;
          }
        }
      }
      label846:
      setPowerState(localHashMap);
    }
    if (i == 0) {
      addNotAvailableMessage();
    }
    removeCachedPrefs(this.mAppListGroup);
    BatteryEntry.startRequestQueue();
  }
  
  private void setPowerState(Map<Integer, PowerGaugePreference> paramMap)
  {
    if (paramMap == null) {
      return;
    }
    Object localObject = paramMap.values().iterator();
    while (((Iterator)localObject).hasNext()) {
      ((PowerGaugePreference)((Iterator)localObject).next()).setState(-1);
    }
    if (this.mHighPowerAppModel != null)
    {
      localObject = this.mHighPowerAppModel.getDataList();
      if ((localObject != null) && (((List)localObject).size() > 0))
      {
        Logutil.loge("PowerUsageSummary", "setPowerState list.size=" + ((List)localObject).size());
        localObject = ((Iterable)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
          HighPowerApp localHighPowerApp = (HighPowerApp)((Iterator)localObject).next();
          if (paramMap.containsKey(Integer.valueOf(localHighPowerApp.uid)))
          {
            Logutil.loge("PowerUsageSummary", "setPowerState pkg=" + localHighPowerApp.pkgName + ", uid=" + localHighPowerApp.uid);
            ((PowerGaugePreference)paramMap.get(Integer.valueOf(localHighPowerApp.uid))).setState(localHighPowerApp.getState());
          }
        }
      }
    }
  }
  
  public void OnDataChanged()
  {
    refreshStats();
  }
  
  String extractKeyFromSipper(BatterySipper paramBatterySipper)
  {
    if (paramBatterySipper.uidObj != null) {
      return Integer.toString(paramBatterySipper.getUid());
    }
    if (paramBatterySipper.drainType != BatterySipper.DrainType.APP) {
      return paramBatterySipper.drainType.toString();
    }
    if (paramBatterySipper.getPackages() != null) {
      return TextUtils.concat(paramBatterySipper.getPackages()).toString();
    }
    Log.w("PowerUsageSummary", "Inappropriate BatterySipper without uid and package names: " + paramBatterySipper);
    return "-1";
  }
  
  protected int getHelpResource()
  {
    return 2131693019;
  }
  
  protected int getMetricsCategory()
  {
    return 54;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setAnimationAllowed(true);
    addPreferencesFromResource(2131230827);
    this.mHistPref = ((BatteryHistoryPreference)findPreference("battery_history"));
    this.mAppListGroup = ((PreferenceGroup)findPreference("app_list"));
    if (OPUtils.isGuestMode()) {
      removePreference("op_backgroyund_optimize");
    }
    this.mHighPowerAppModel = new HighPowerAppModel(getPrefContext(), null);
    try
    {
      if (getActivity().getIntent().getIntExtra("tracker_event", -1) == 1) {
        OPUtils.sendAppTracker("widget_power", 1);
      }
      return;
    }
    catch (Exception paramBundle)
    {
      paramBundle.printStackTrace();
    }
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    getHighPowerAppsTitle();
    if (!OPUtils.isGuestMode()) {}
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    BatteryEntry.clearUidCache();
    if (this.mHighPowerAppModel != null)
    {
      this.mHighPowerAppModel.releaseResource();
      this.mHighPowerAppModel = null;
    }
  }
  
  /* Error */
  public boolean onOptionsItemSelected(android.view.MenuItem paramMenuItem)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 377	com/android/settings/fuelgauge/PowerUsageSummary:getActivity	()Landroid/app/Activity;
    //   4: checkcast 711	com/android/settings/SettingsActivity
    //   7: astore 4
    //   9: aconst_null
    //   10: astore_3
    //   11: aconst_null
    //   12: astore_2
    //   13: aload_1
    //   14: invokeinterface 716 1 0
    //   19: tableswitch	default:+33->52, 1:+39->58, 2:+33->52, 3:+33->52, 4:+65->84, 5:+165->184
    //   52: aload_0
    //   53: aload_1
    //   54: invokespecial 718	com/android/settings/fuelgauge/PowerUsageBase:onOptionsItemSelected	(Landroid/view/MenuItem;)Z
    //   57: ireturn
    //   58: aload_0
    //   59: getfield 106	com/android/settings/fuelgauge/PowerUsageSummary:mStatsType	I
    //   62: ifne +14 -> 76
    //   65: aload_0
    //   66: iconst_2
    //   67: putfield 106	com/android/settings/fuelgauge/PowerUsageSummary:mStatsType	I
    //   70: aload_0
    //   71: invokevirtual 615	com/android/settings/fuelgauge/PowerUsageSummary:refreshStats	()V
    //   74: iconst_1
    //   75: ireturn
    //   76: aload_0
    //   77: iconst_0
    //   78: putfield 106	com/android/settings/fuelgauge/PowerUsageSummary:mStatsType	I
    //   81: goto -11 -> 70
    //   84: aload_0
    //   85: invokespecial 371	com/android/settings/fuelgauge/PowerUsageSummary:isSupportBackgroundManagerment	()Z
    //   88: ifeq +55 -> 143
    //   91: new 677	android/content/Intent
    //   94: dup
    //   95: ldc_w 720
    //   98: invokespecial 722	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   101: astore_1
    //   102: aload_0
    //   103: invokevirtual 377	com/android/settings/fuelgauge/PowerUsageSummary:getActivity	()Landroid/app/Activity;
    //   106: aload_1
    //   107: invokevirtual 726	android/app/Activity:startActivity	(Landroid/content/Intent;)V
    //   110: iconst_1
    //   111: ireturn
    //   112: astore_1
    //   113: aload_2
    //   114: astore_1
    //   115: ldc 63
    //   117: new 217	java/lang/StringBuilder
    //   120: dup
    //   121: invokespecial 218	java/lang/StringBuilder:<init>	()V
    //   124: ldc_w 728
    //   127: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   130: aload_1
    //   131: invokevirtual 636	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   134: invokevirtual 231	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   137: invokestatic 579	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   140: pop
    //   141: iconst_1
    //   142: ireturn
    //   143: new 730	android/os/Bundle
    //   146: dup
    //   147: invokespecial 731	android/os/Bundle:<init>	()V
    //   150: astore_1
    //   151: aload_1
    //   152: ldc_w 733
    //   155: ldc_w 735
    //   158: invokevirtual 740	java/lang/Class:getName	()Ljava/lang/String;
    //   161: invokevirtual 743	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   164: aload 4
    //   166: ldc_w 745
    //   169: invokevirtual 740	java/lang/Class:getName	()Ljava/lang/String;
    //   172: aload_1
    //   173: ldc_w 373
    //   176: aconst_null
    //   177: aconst_null
    //   178: iconst_0
    //   179: invokevirtual 749	com/android/settings/SettingsActivity:startPreferencePanel	(Ljava/lang/String;Landroid/os/Bundle;ILjava/lang/CharSequence;Landroid/app/Fragment;I)V
    //   182: iconst_1
    //   183: ireturn
    //   184: new 677	android/content/Intent
    //   187: dup
    //   188: ldc_w 751
    //   191: invokespecial 722	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   194: astore_1
    //   195: aload_0
    //   196: invokevirtual 377	com/android/settings/fuelgauge/PowerUsageSummary:getActivity	()Landroid/app/Activity;
    //   199: aload_1
    //   200: invokevirtual 726	android/app/Activity:startActivity	(Landroid/content/Intent;)V
    //   203: iconst_1
    //   204: ireturn
    //   205: astore_1
    //   206: aload_3
    //   207: astore_1
    //   208: ldc 63
    //   210: new 217	java/lang/StringBuilder
    //   213: dup
    //   214: invokespecial 218	java/lang/StringBuilder:<init>	()V
    //   217: ldc_w 728
    //   220: invokevirtual 224	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   223: aload_1
    //   224: invokevirtual 636	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   227: invokevirtual 231	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   230: invokestatic 579	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   233: pop
    //   234: iconst_1
    //   235: ireturn
    //   236: astore_2
    //   237: goto -29 -> 208
    //   240: astore_2
    //   241: goto -126 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	244	0	this	PowerUsageSummary
    //   0	244	1	paramMenuItem	android.view.MenuItem
    //   12	102	2	localObject1	Object
    //   236	1	2	localActivityNotFoundException1	ActivityNotFoundException
    //   240	1	2	localActivityNotFoundException2	ActivityNotFoundException
    //   10	197	3	localObject2	Object
    //   7	158	4	localSettingsActivity	SettingsActivity
    // Exception table:
    //   from	to	target	type
    //   91	102	112	android/content/ActivityNotFoundException
    //   184	195	205	android/content/ActivityNotFoundException
    //   195	203	236	android/content/ActivityNotFoundException
    //   102	110	240	android/content/ActivityNotFoundException
  }
  
  public void onPause()
  {
    BatteryEntry.stopRequestQueue();
    this.mHandler.removeMessages(1);
    super.onPause();
    this.mPauseUpdate = true;
    this.mHighPowerAppModel.unregisterObserver(this);
    this.mHandler.removeMessages(1000);
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if ("op_backgroyund_optimize".equals(paramPreference.getKey()))
    {
      paramPreference = null;
      try
      {
        Intent localIntent = new Intent("com.android.settings.action.BACKGROUND_OPTIMIZE");
        Log.d("PowerUsageSummary", "No activity found for " + paramPreference);
      }
      catch (ActivityNotFoundException localActivityNotFoundException)
      {
        try
        {
          localIntent.putExtra("classname", Settings.BgOptimizeAppListActivity.class.getName());
          getActivity().startActivity(localIntent);
          return true;
        }
        catch (ActivityNotFoundException paramPreference)
        {
          for (;;)
          {
            BatteryEntry localBatteryEntry;
            paramPreference = localBatteryEntry;
          }
        }
        localActivityNotFoundException = localActivityNotFoundException;
      }
      return true;
    }
    else
    {
      if (!(paramPreference instanceof PowerGaugePreference)) {
        return super.onPreferenceTreeClick(paramPreference);
      }
      localBatteryEntry = ((PowerGaugePreference)paramPreference).getInfo();
      PowerUsageDetail.startBatteryDetailPage((SettingsActivity)getActivity(), this.mStatsHelper, this.mStatsType, localBatteryEntry, true, true);
      return super.onPreferenceTreeClick(paramPreference);
    }
  }
  
  public void onResume()
  {
    super.onResume();
    this.mPauseUpdate = false;
    this.mHighPowerAppModel.registerObserver(this);
    this.mHighPowerAppModel.update();
    nextUpdate();
  }
  
  protected void refreshStats()
  {
    super.refreshStats();
    new GetDataTask(this, this.mStatsHelper).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  private static class GetDataTask
    extends AsyncTask<Void, Void, BatteryStats>
  {
    private final WeakReference<PowerUsageSummary> mReference;
    private final BatteryStatsHelper mStatsHelper;
    
    public GetDataTask(PowerUsageSummary paramPowerUsageSummary, BatteryStatsHelper paramBatteryStatsHelper)
    {
      this.mReference = new WeakReference(paramPowerUsageSummary);
      this.mStatsHelper = paramBatteryStatsHelper;
    }
    
    protected BatteryStats doInBackground(Void... paramVarArgs)
    {
      if (this.mStatsHelper.getPowerProfile().getAveragePower("screen.full") < 10.0D) {
        return null;
      }
      return this.mStatsHelper.getStats();
    }
    
    protected void onPostExecute(BatteryStats paramBatteryStats)
    {
      PowerUsageSummary localPowerUsageSummary = (PowerUsageSummary)this.mReference.get();
      if ((localPowerUsageSummary == null) || (localPowerUsageSummary.getActivity() == null) || (localPowerUsageSummary.getActivity().isFinishing())) {
        return;
      }
      PowerUsageSummary.-wrap1(localPowerUsageSummary, paramBatteryStats);
    }
  }
  
  private static class SummaryProvider
    extends BroadcastReceiver
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final Handler mHandler;
    private final SummaryLoader mLoader;
    
    private SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mLoader = paramSummaryLoader;
      this.mHandler = new Handler(Looper.getMainLooper());
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      this.mHandler.postDelayed(new Runnable()
      {
        public void run()
        {
          BatteryInfo.getBatteryInfo(PowerUsageSummary.SummaryProvider.-get0(PowerUsageSummary.SummaryProvider.this), new BatteryInfo.Callback()
          {
            public void onBatteryInfoLoaded(BatteryInfo paramAnonymous2BatteryInfo)
            {
              PowerUsageSummary.SummaryProvider.-get1(PowerUsageSummary.SummaryProvider.this).setSummary(PowerUsageSummary.SummaryProvider.this, paramAnonymous2BatteryInfo.mChargeLabelString);
            }
          });
        }
      }, 300L);
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        BatteryInfo.getBatteryInfo(this.mContext, new BatteryInfo.Callback()
        {
          public void onBatteryInfoLoaded(BatteryInfo paramAnonymousBatteryInfo)
          {
            PowerUsageSummary.SummaryProvider.-get1(PowerUsageSummary.SummaryProvider.this).setSummary(PowerUsageSummary.SummaryProvider.this, paramAnonymousBatteryInfo.mChargeLabelString);
          }
        });
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        localIntentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        this.mLoader.registerReceiver(this, localIntentFilter);
        return;
      }
      this.mHandler.removeCallbacks(null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\PowerUsageSummary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */