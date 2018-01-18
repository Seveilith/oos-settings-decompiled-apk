package com.android.settings.datausage;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.INetworkStatsService;
import android.net.INetworkStatsSession;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkStats;
import android.net.NetworkStats.Entry;
import android.net.NetworkStatsHistory;
import android.net.NetworkStatsHistory.Entry;
import android.net.NetworkTemplate;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.settings.Utils;
import com.android.settings.widget.ChartDataUsageView.DataUsageChartListener;
import com.android.settingslib.AppItem;
import com.android.settingslib.NetworkPolicyEditor;
import com.android.settingslib.net.ChartData;
import com.android.settingslib.net.ChartDataLoader;
import com.android.settingslib.net.SummaryForAllUidLoader;
import com.android.settingslib.net.UidDetail;
import com.android.settingslib.net.UidDetailProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class DataUsageList
  extends DataUsageBase
{
  public static final String EXTRA_NETWORK_TEMPLATE = "network_template";
  public static final String EXTRA_SUB_ID = "sub_id";
  private static final String KEY_APPS_GROUP = "apps_group";
  private static final String KEY_CHART_DATA = "chart_data";
  private static final String KEY_CHART_DATA_DEPRECATED = "chart_data_deprecated";
  private static final String KEY_USAGE_AMOUNT = "usage_amount";
  private static final String KEY_USAGE_SUMMARY = "usage_summary";
  private static final int LOADER_CHART_DATA = 2;
  private static final int LOADER_SUMMARY = 3;
  private static final boolean LOGD = false;
  private static final String TAG = "DataUsage";
  private static long mSelectLeft;
  private static long mSelectRight;
  private PreferenceGroup mApps;
  private boolean mBinding;
  private ChartDataUsagePreference mChart;
  private ChartData mChartData;
  private final LoaderManager.LoaderCallbacks<ChartData> mChartDataCallbacks = new LoaderManager.LoaderCallbacks()
  {
    public Loader<ChartData> onCreateLoader(int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      return new ChartDataLoader(DataUsageList.this.getActivity(), DataUsageList.-get9(DataUsageList.this), paramAnonymousBundle);
    }
    
    public void onLoadFinished(Loader<ChartData> paramAnonymousLoader, ChartData paramAnonymousChartData)
    {
      DataUsageList.this.setLoading(false, false);
      DataUsageList.-set0(DataUsageList.this, paramAnonymousChartData);
      if (DataUsageList.-get5(DataUsageList.this))
      {
        DataUsageList.-get3(DataUsageList.this).bindNetworkStats(DataUsageList.-get2(DataUsageList.this).network);
        DataUsageList.-get3(DataUsageList.this).bindDetailNetworkStats(DataUsageList.-get2(DataUsageList.this).detail);
      }
      for (;;)
      {
        DataUsageList.-wrap6(DataUsageList.this, true);
        return;
        DataUsageList.-get1(DataUsageList.this).setNetworkStats(DataUsageList.-get2(DataUsageList.this).network);
      }
    }
    
    public void onLoaderReset(Loader<ChartData> paramAnonymousLoader)
    {
      DataUsageList.-set0(DataUsageList.this, null);
      if (DataUsageList.-get5(DataUsageList.this))
      {
        DataUsageList.-get3(DataUsageList.this).bindNetworkStats(null);
        DataUsageList.-get3(DataUsageList.this).bindDetailNetworkStats(null);
        return;
      }
      DataUsageList.-get1(DataUsageList.this).setNetworkStats(null);
    }
  };
  private ChartDataUsageDeprecatedPreference mChartDeprecated;
  private ChartDataUsageView.DataUsageChartListener mChartListener = new ChartDataUsageView.DataUsageChartListener()
  {
    public void onInspectRangeChanged()
    {
      DataUsageList.-wrap5(DataUsageList.this);
      DataUsageList.-get3(DataUsageList.this).setInspectRangeChanged();
    }
    
    public void onLimitChanged()
    {
      DataUsageList.-wrap1(DataUsageList.this, DataUsageList.-get3(DataUsageList.this).getLimitBytes());
      DataUsageList.-wrap4(DataUsageList.this);
    }
    
    public void onWarningChanged()
    {
      DataUsageList.-wrap2(DataUsageList.this, DataUsageList.-get3(DataUsageList.this).getWarningBytes());
    }
    
    public void requestLimitEdit() {}
    
    public void requestWarningEdit() {}
  };
  private CycleAdapter mCycleAdapter;
  private AdapterView.OnItemSelectedListener mCycleListener = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      paramAnonymousAdapterView = OPDataUsageUtils.getDataUsageSectionTimeMillByAccountDay(DataUsageList.-wrap0(DataUsageList.this), DataUsageList.-get10(DataUsageList.this));
      paramAnonymousAdapterView = new CycleAdapter.CycleItem(DataUsageList.-wrap0(DataUsageList.this), paramAnonymousAdapterView[0], paramAnonymousAdapterView[1]);
      if (DataUsageList.-get5(DataUsageList.this)) {
        DataUsageList.-get3(DataUsageList.this).setVisibleRange(paramAnonymousAdapterView.start, paramAnonymousAdapterView.end, DataUsageList.-get6(), DataUsageList.-get7());
      }
      for (;;)
      {
        DataUsageList.-wrap5(DataUsageList.this);
        return;
        DataUsageList.-get1(DataUsageList.this).setVisibleRange(paramAnonymousAdapterView.start, paramAnonymousAdapterView.end);
      }
    }
    
    public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
  };
  private Spinner mCycleSpinner;
  private boolean mDataSelectionEnable = false;
  private ExecutorService mExecutor = Executors.newFixedThreadPool(5);
  private View mHeader;
  private final Map<String, Boolean> mMobileDataEnabled = new HashMap();
  private boolean mShowDataUsage = false;
  private INetworkStatsSession mStatsSession;
  private int mSubId;
  private final LoaderManager.LoaderCallbacks<NetworkStats> mSummaryCallbacks = new LoaderManager.LoaderCallbacks()
  {
    private void updateEmptyVisible()
    {
      int j = 1;
      int i;
      if (DataUsageList.-get0(DataUsageList.this).getPreferenceCount() != 0)
      {
        i = 1;
        if (DataUsageList.this.getPreferenceScreen().getPreferenceCount() == 0) {
          break label100;
        }
      }
      for (;;)
      {
        if (i != j)
        {
          if ((DataUsageList.-get0(DataUsageList.this).getPreferenceCount() == 0) && (!DataUsageList.-get8(DataUsageList.this))) {
            break label105;
          }
          DataUsageList.this.getPreferenceScreen().addPreference(DataUsageList.-get12(DataUsageList.this));
          DataUsageList.this.getPreferenceScreen().addPreference(DataUsageList.-get0(DataUsageList.this));
        }
        return;
        i = 0;
        break;
        label100:
        j = 0;
      }
      label105:
      DataUsageList.this.getPreferenceScreen().removeAll();
    }
    
    public Loader<NetworkStats> onCreateLoader(int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      return new SummaryForAllUidLoader(DataUsageList.this.getActivity(), DataUsageList.-get9(DataUsageList.this), paramAnonymousBundle);
    }
    
    public void onLoadFinished(Loader<NetworkStats> paramAnonymousLoader, NetworkStats paramAnonymousNetworkStats)
    {
      paramAnonymousLoader = DataUsageList.this.services.mPolicyManager.getUidsWithPolicy(1);
      DataUsageList.this.bindStats(paramAnonymousNetworkStats, paramAnonymousLoader);
      updateEmptyVisible();
    }
    
    public void onLoaderReset(Loader<NetworkStats> paramAnonymousLoader)
    {
      DataUsageList.this.bindStats(null, new int[0]);
      updateEmptyVisible();
    }
  };
  private NetworkTemplate mTemplate;
  private UidDetailProvider mUidDetailProvider;
  private Preference mUsageAmount;
  private Preference mUsageSummary;
  private TextView tv_filter_datetime;
  
  private static long accumulate(int paramInt1, SparseArray<AppItem> paramSparseArray, NetworkStats.Entry paramEntry, int paramInt2, ArrayList<AppItem> paramArrayList, long paramLong)
  {
    int i = paramEntry.uid;
    AppItem localAppItem2 = (AppItem)paramSparseArray.get(paramInt1);
    AppItem localAppItem1 = localAppItem2;
    if (localAppItem2 == null)
    {
      localAppItem1 = new AppItem(paramInt1);
      localAppItem1.category = paramInt2;
      paramArrayList.add(localAppItem1);
      paramSparseArray.put(localAppItem1.key, localAppItem1);
    }
    localAppItem1.addUid(i);
    localAppItem1.total += paramEntry.rxBytes + paramEntry.txBytes;
    return Math.max(paramLong, localAppItem1.total);
  }
  
  public static boolean hasReadyMobileRadio(Context paramContext)
  {
    ConnectivityManager localConnectivityManager = ConnectivityManager.from(paramContext);
    TelephonyManager localTelephonyManager = TelephonyManager.from(paramContext);
    paramContext = SubscriptionManager.from(paramContext).getActiveSubscriptionInfoList();
    if (paramContext == null) {
      return false;
    }
    boolean bool2 = true;
    paramContext = paramContext.iterator();
    if (paramContext.hasNext())
    {
      if (localTelephonyManager.getSimState(((SubscriptionInfo)paramContext.next()).getSimSlotIndex()) == 5) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        bool2 &= bool1;
        break;
      }
    }
    if (localConnectivityManager.isNetworkSupported(0)) {
      return bool2;
    }
    return false;
  }
  
  public static boolean hasReadyMobileRadio(Context paramContext, int paramInt)
  {
    boolean bool2 = false;
    ConnectivityManager localConnectivityManager = ConnectivityManager.from(paramContext);
    if (TelephonyManager.from(paramContext).getSimState(SubscriptionManager.getSlotId(paramInt)) == 5) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      if (localConnectivityManager.isNetworkSupported(0)) {
        bool2 = bool1;
      }
      return bool2;
    }
  }
  
  private void setPolicyLimitBytes(long paramLong)
  {
    this.services.mPolicyEditor.setPolicyLimitBytes(this.mTemplate, paramLong);
    updatePolicy(false);
  }
  
  private void setPolicyWarningBytes(long paramLong)
  {
    this.services.mPolicyEditor.setPolicyWarningBytes(this.mTemplate, paramLong);
    updatePolicy(false);
  }
  
  private void startAppDataUsage(AppItem paramAppItem)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("arg_subid", this.mSubId);
    localBundle.putInt("uid", paramAppItem.key);
    localBundle.putParcelable("app_item", paramAppItem);
    localBundle.putParcelable("network_template", this.mTemplate);
    startFragment(this, AppDataUsage.class.getName(), 2131693384, 0, localBundle);
  }
  
  private void updateBody()
  {
    this.mBinding = true;
    if (!isAdded())
    {
      Log.d("DataUsage", "updateBody is not Added");
      return;
    }
    Object localObject = getActivity();
    try
    {
      getLoaderManager().restartLoader(2, ChartDataLoader.buildArgs(this.mTemplate, null), this.mChartDataCallbacks);
      getActivity().invalidateOptionsMenu();
      this.mBinding = false;
      if (!this.mDataSelectionEnable)
      {
        int j = ((Context)localObject).getColor(2131493683);
        int i = j;
        if (this.mSubId != -1)
        {
          localObject = this.services.mSubscriptionManager.getActiveSubscriptionInfo(this.mSubId);
          i = j;
          if (localObject != null) {
            i = ((SubscriptionInfo)localObject).getIconTint();
          }
        }
        j = Color.argb(127, Color.red(i), Color.green(i), Color.blue(i));
        this.mChart.setColors(i, j);
      }
      if (!this.mShowDataUsage) {
        getPreferenceScreen().removePreference(this.mUsageSummary);
      }
      return;
    }
    catch (RejectedExecutionException localRejectedExecutionException)
    {
      for (;;)
      {
        localRejectedExecutionException.printStackTrace();
      }
    }
  }
  
  private void updateDetailData()
  {
    long l2;
    Object localObject2;
    Object localObject1;
    if (this.mDataSelectionEnable) {
      if (this.mShowDataUsage)
      {
        l1 = this.mChartDeprecated.getInspectLeft();
        l2 = this.mChartDeprecated.getInspectRight();
        mSelectLeft = this.mChartDeprecated.getInspectLeft();
        mSelectRight = this.mChartDeprecated.getInspectRight();
        Log.d("DataUsage", "Will get left and right data here:" + new Date(l1).toString() + "-->" + new Date(l2).toString());
        long l3 = System.currentTimeMillis();
        localObject2 = getActivity();
        localObject1 = null;
        if (this.mChartData != null) {
          localObject1 = this.mChartData.network.getValues(l1, l2, l3, null);
        }
        getLoaderManager().restartLoader(3, SummaryForAllUidLoader.buildArgs(this.mTemplate, l1, l2), this.mSummaryCallbacks);
        if (localObject1 == null) {
          break label301;
        }
      }
    }
    label301:
    for (long l1 = ((NetworkStatsHistory.Entry)localObject1).rxBytes + ((NetworkStatsHistory.Entry)localObject1).txBytes;; l1 = 0L)
    {
      localObject1 = Formatter.formatFileSize((Context)localObject2, l1);
      this.mUsageAmount.setTitle(getString(2131693641, new Object[] { localObject1 }));
      if (this.mShowDataUsage)
      {
        localObject2 = Utils.formatDateRange((Context)localObject2, mSelectLeft, mSelectRight);
        this.mUsageSummary.setSummary(getString(2131692824, new Object[] { localObject1, localObject2 }));
      }
      return;
      l1 = this.mChartDeprecated.getInspectStart();
      l2 = this.mChartDeprecated.getInspectEnd();
      break;
      l1 = this.mChart.getInspectStart();
      l2 = this.mChart.getInspectEnd();
      break;
    }
  }
  
  private void updatePolicy(boolean paramBoolean)
  {
    NetworkPolicy localNetworkPolicy = this.services.mPolicyEditor.getPolicy(this.mTemplate);
    if ((isNetworkPolicyModifiable(localNetworkPolicy, this.mSubId)) && (isMobileDataAvailable(this.mSubId)))
    {
      if (this.mDataSelectionEnable) {
        this.mChartDeprecated.bindNetworkPolicy(localNetworkPolicy);
      }
      for (;;)
      {
        this.mHeader.findViewById(2131361971).setVisibility(0);
        ImageView localImageView = (ImageView)this.mHeader.findViewById(2131361971);
        localImageView.setVisibility(8);
        localImageView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new Bundle();
            paramAnonymousView.putParcelable("network_template", DataUsageList.-get11(DataUsageList.this));
            DataUsageList.this.startFragment(DataUsageList.this, BillingCycleSettings.class.getName(), 2131693636, 0, paramAnonymousView);
          }
        });
        if ((paramBoolean) && (this.mCycleAdapter.updateCycleList(localNetworkPolicy, this.mChartData))) {
          updateDetailData();
        }
        return;
        if (localNetworkPolicy.limitBytes != -1L) {
          this.services.mPolicyEditor.setPolicyLimitBytes(this.mTemplate, -1L);
        }
        this.mChart.setNetworkPolicy(localNetworkPolicy);
      }
    }
    if (this.mDataSelectionEnable) {
      this.mChartDeprecated.bindNetworkPolicy(null);
    }
    for (;;)
    {
      this.mHeader.findViewById(2131361971).setVisibility(8);
      break;
      this.mChart.setNetworkPolicy(null);
    }
  }
  
  public void bindStats(NetworkStats paramNetworkStats, int[] paramArrayOfInt)
  {
    ArrayList localArrayList = new ArrayList();
    long l1 = 0L;
    int n = ActivityManager.getCurrentUser();
    UserManager localUserManager = UserManager.get(getContext());
    List localList = localUserManager.getUserProfiles();
    SparseArray localSparseArray = new SparseArray();
    PackageManager localPackageManager = getContext().getPackageManager();
    Object localObject1 = null;
    try
    {
      localObject2 = localPackageManager.getApplicationInfo("com.android.dialer", 1);
      localObject1 = localObject2;
    }
    catch (Exception localException1)
    {
      for (;;)
      {
        try
        {
          Object localObject2;
          int m;
          ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(localPackageManager.getPackagesForUid(i)[0], 0);
          if ((j != 999) || (localApplicationInfo == null)) {
            break;
          }
          int i1 = localApplicationInfo.flags;
          if ((i1 & 0x1) <= 0) {
            break;
          }
          m += 1;
        }
        catch (Exception localException2)
        {
          Log.d("DataUsage", "get dialer getApplicationInfo failed " + localException2);
        }
        localException1 = localException1;
        Log.d("DataUsage", "get dialer getApplicationInfo failed " + localException1);
        continue;
        k = 0;
      }
      if (!UserHandle.isApp(i)) {
        break label346;
      }
      if (!localList.contains(new UserHandle(j))) {
        break label315;
      }
      long l2 = l1;
      if (j == n) {
        break label289;
      }
      l2 = accumulate(UidDetailProvider.buildKeyForUser(j), localSparseArray, localException1, 0, localArrayList, l1);
      label289:
      int j = 2;
      l1 = l2;
      for (;;)
      {
        l1 = accumulate(i, localSparseArray, localException1, j, localArrayList, l1);
        break;
        label315:
        if (localUserManager.getUserInfo(j) == null)
        {
          i = -4;
          j = 2;
        }
        else
        {
          i = UidDetailProvider.buildKeyForUser(j);
          j = 0;
          continue;
          label346:
          if ((i == -4) || (i == -5))
          {
            j = 2;
          }
          else if ((localObject1 != null) && (i == ((ApplicationInfo)localObject1).uid) && (getContext().getResources().getBoolean(2131558454)))
          {
            j = 2;
          }
          else
          {
            i = 1000;
            j = 2;
          }
        }
      }
      label410:
      j = paramArrayOfInt.length;
      int i = 0;
      if (i >= j) {
        break label518;
      }
      int k = paramArrayOfInt[i];
      if (localList.contains(new UserHandle(UserHandle.getUserId(k)))) {
        break label456;
      }
      for (;;)
      {
        i += 1;
        break;
        label456:
        localObject1 = (AppItem)localSparseArray.get(k);
        paramNetworkStats = (NetworkStats)localObject1;
        if (localObject1 == null)
        {
          paramNetworkStats = new AppItem(k);
          paramNetworkStats.total = -1L;
          localArrayList.add(paramNetworkStats);
          localSparseArray.put(paramNetworkStats.key, paramNetworkStats);
        }
        paramNetworkStats.restricted = true;
      }
      label518:
      Collections.sort(localArrayList);
      this.mApps.removeAll();
      paramNetworkStats = OPUtils.getCorpUserInfo(getContext());
      i = 0;
      if (i >= localArrayList.size()) {
        return;
      }
      if (l1 == 0L) {
        break label657;
      }
      j = (int)(((AppItem)localArrayList.get(i)).total * 100L / l1);
      label578:
      paramArrayOfInt = new AppDataUsagePreference(getContext(), (AppItem)localArrayList.get(i), j, this.mUidDetailProvider, this.mExecutor);
      paramArrayOfInt.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
      {
        public boolean onPreferenceClick(Preference paramAnonymousPreference)
        {
          AppItem localAppItem = ((AppDataUsagePreference)paramAnonymousPreference).getItem();
          if ((localAppItem.key == 1000) || (localAppItem.key == -4))
          {
            AppDataUsage.OSUidDetail = new UidDetail();
            AppDataUsage.OSUidDetail.icon = paramAnonymousPreference.getIcon();
            AppDataUsage.OSUidDetail.label = paramAnonymousPreference.getTitle();
          }
          DataUsageList.-wrap3(DataUsageList.this, localAppItem);
          return true;
        }
      });
      if ((paramNetworkStats == null) || (paramNetworkStats.id != 999) || (((AppItem)localArrayList.get(i)).key >= 0)) {
        break label663;
      }
      for (;;)
      {
        i += 1;
        break;
        label657:
        j = 0;
        break label578;
        label663:
        this.mApps.addPreference(paramArrayOfInt);
      }
    }
    localObject2 = null;
    if (paramNetworkStats != null)
    {
      k = paramNetworkStats.size();
      m = 0;
      if (m >= k) {
        break label410;
      }
      localObject2 = paramNetworkStats.getValues(m, (NetworkStats.Entry)localObject2);
      i = ((NetworkStats.Entry)localObject2).uid;
      j = UserHandle.getUserId(i);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 341;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    boolean bool2 = false;
    super.onCreate(paramBundle);
    paramBundle = getActivity();
    if (!isBandwidthControlEnabled())
    {
      Log.w("DataUsage", "No bandwidth control; leaving");
      getActivity().finish();
    }
    for (;;)
    {
      try
      {
        this.mStatsSession = this.services.mStatsService.openSession();
        this.mUidDetailProvider = new UidDetailProvider(paramBundle);
        addPreferencesFromResource(2131230752);
        this.mUsageAmount = findPreference("usage_amount");
        this.mChart = ((ChartDataUsagePreference)findPreference("chart_data"));
        this.mApps = ((PreferenceGroup)findPreference("apps_group"));
        Object localObject = getArguments();
        this.mSubId = ((Bundle)localObject).getInt("sub_id", -1);
        this.mTemplate = ((NetworkTemplate)((Bundle)localObject).getParcelable("network_template"));
        this.mChartDeprecated = ((ChartDataUsageDeprecatedPreference)findPreference("chart_data_deprecated"));
        this.mChartDeprecated.setListener(this.mChartListener);
        this.mChartDeprecated.bindNetworkPolicy(null);
        localObject = this.mChart;
        if (this.mTemplate.isMatchRuleMobile())
        {
          bool1 = false;
          ((ChartDataUsagePreference)localObject).setShowWifi(bool1);
          this.mChart.setSubId(this.mSubId);
          localObject = this.mChartDeprecated;
          if (!this.mTemplate.isMatchRuleMobile()) {
            break label309;
          }
          bool1 = bool2;
          ((ChartDataUsageDeprecatedPreference)localObject).setShowWifi(bool1);
          this.mChartDeprecated.setSubId(this.mSubId);
          this.mUsageSummary = findPreference("usage_summary");
          this.mDataSelectionEnable = BillingCycleSettings.isDataSelectionEnable(paramBundle);
          this.mShowDataUsage = BillingCycleSettings.isShowDataUsage(paramBundle);
          if (!this.mDataSelectionEnable) {
            break;
          }
          ((PreferenceGroup)this.mUsageAmount).removePreference(this.mChart);
          this.mChart = null;
          return;
        }
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeException(paramBundle);
      }
      boolean bool1 = true;
      continue;
      label309:
      bool1 = true;
    }
    ((PreferenceGroup)this.mUsageAmount).removePreference(this.mChartDeprecated);
    this.mChartDeprecated = null;
  }
  
  public void onDestroy()
  {
    this.mUidDetailProvider.clearCache();
    this.mUidDetailProvider = null;
    this.mExecutor.shutdownNow();
    TrafficStats.closeQuietly(this.mStatsSession);
    super.onDestroy();
  }
  
  public void onResume()
  {
    super.onResume();
    updateBody();
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        try
        {
          Thread.sleep(250L);
          DataUsageList.this.services.mStatsService.forceUpdate();
          return null;
        }
        catch (InterruptedException paramAnonymousVarArgs)
        {
          for (;;) {}
        }
        catch (RemoteException paramAnonymousVarArgs)
        {
          for (;;) {}
        }
      }
      
      protected void onPostExecute(Void paramAnonymousVoid)
      {
        if (!DataUsageList.this.isAdded())
        {
          Log.d("DataUsage", "AsyncTask is not Added");
          return;
        }
        DataUsageList.-wrap4(DataUsageList.this);
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    this.mHeader = setPinnedHeaderView(2130968621);
    this.mCycleSpinner = ((Spinner)this.mHeader.findViewById(2131361969));
    this.mCycleSpinner.setVisibility(8);
    this.tv_filter_datetime = ((TextView)this.mHeader.findViewById(2131361970));
    paramView = OPDataUsageUtils.getDataUsageSectionTimeMillByAccountDay(getPrefContext(), this.mSubId);
    paramView = Utils.formatDateRange(getPrefContext(), paramView[0], paramView[1]);
    this.tv_filter_datetime.setText(paramView);
    this.mCycleAdapter = new CycleAdapter(getContext(), new CycleAdapter.SpinnerInterface()
    {
      public Object getSelectedItem()
      {
        return DataUsageList.-get4(DataUsageList.this).getSelectedItem();
      }
      
      public void setAdapter(CycleAdapter paramAnonymousCycleAdapter)
      {
        DataUsageList.-get4(DataUsageList.this).setAdapter(paramAnonymousCycleAdapter);
      }
      
      public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener paramAnonymousOnItemSelectedListener)
      {
        DataUsageList.-get4(DataUsageList.this).setOnItemSelectedListener(paramAnonymousOnItemSelectedListener);
      }
      
      public void setSelection(int paramAnonymousInt)
      {
        DataUsageList.-get4(DataUsageList.this).setSelection(paramAnonymousInt);
      }
    }, this.mCycleListener, true);
    setLoading(true, true);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\DataUsageList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */