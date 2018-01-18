package com.android.settings.datausage;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.INetworkStatsService;
import android.net.INetworkStatsSession;
import android.net.NetworkStats;
import android.net.NetworkTemplate;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.BidiFormatter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.format.Formatter.BytesResult;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.settings.SummaryPreference;
import com.android.settings.Utils;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.net.DataUsageController.DataUsageInfo;
import java.util.ArrayList;
import java.util.List;

public class DataUsageSummary
  extends DataUsageBase
  implements Indexable
{
  private static final String KEY_LIMIT_SUMMARY = "limit_summary";
  private static final String KEY_RESTRICT_BACKGROUND = "restrict_background";
  private static final String KEY_STATUS_HEADER = "status_header";
  static final boolean LOGD = false;
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      ArrayList localArrayList = new ArrayList();
      if (ConnectivityManager.from(paramAnonymousContext).isNetworkSupported(0)) {
        localArrayList.add("restrict_background");
      }
      return localArrayList;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      SearchIndexableResource localSearchIndexableResource = new SearchIndexableResource(paramAnonymousContext);
      localSearchIndexableResource.xmlResId = 2131230749;
      localArrayList.add(localSearchIndexableResource);
      if (DataUsageSummary.hasMobileData(paramAnonymousContext))
      {
        localSearchIndexableResource = new SearchIndexableResource(paramAnonymousContext);
        localSearchIndexableResource.xmlResId = 2131230750;
        localArrayList.add(localSearchIndexableResource);
      }
      if (DataUsageSummary.hasWifiRadio(paramAnonymousContext))
      {
        paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
        paramAnonymousContext.xmlResId = 2131230754;
        localArrayList.add(paramAnonymousContext);
      }
      return localArrayList;
    }
  };
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new DataUsageSummary.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
    }
  };
  private static final String TAG = "DataUsageSummary";
  public static final boolean TEST_RADIOS = false;
  public static final String TEST_RADIOS_PROP = "test.radios";
  private DataUsageController mDataUsageController;
  private int mDataUsageTemplate;
  private NetworkTemplate mDefaultTemplate;
  private Preference mLimitPreference;
  private SummaryPreference mSummaryPreference;
  
  private void addEthernetSection()
  {
    ((TemplatePreferenceCategory)inflatePreferences(2131230751)).setTemplate(NetworkTemplate.buildTemplateEthernet(), 0, this.services);
  }
  
  private void addMobileSection(int paramInt)
  {
    TemplatePreferenceCategory localTemplatePreferenceCategory = (TemplatePreferenceCategory)inflatePreferences(2131230750);
    localTemplatePreferenceCategory.setTemplate(getNetworkTemplate(paramInt), paramInt, this.services);
    localTemplatePreferenceCategory.pushTemplates(this.services);
  }
  
  private void addWifiSection()
  {
    ((TemplatePreferenceCategory)inflatePreferences(2131230754)).setTemplate(NetworkTemplate.buildTemplateWifiWildcard(), 0, this.services);
  }
  
  private static CharSequence formatTitle(Context paramContext, String paramString, long paramLong)
  {
    SpannableString localSpannableString = new SpannableString(paramContext.getString(17039522).replace("%1$s", "^1").replace("%2$s", "^2"));
    verySmallSpanExcept(localSpannableString, "^1");
    paramContext = Formatter.formatBytes(paramContext.getResources(), paramLong, 1);
    paramContext = TextUtils.expandTemplate(localSpannableString, new CharSequence[] { paramContext.value, paramContext.units });
    paramString = new SpannableString(paramString.replace("%1$s", "^1"));
    verySmallSpanExcept(paramString, "^1");
    return TextUtils.expandTemplate(paramString, new CharSequence[] { BidiFormatter.getInstance().unicodeWrap(paramContext) });
  }
  
  public static int getDefaultSubscriptionId(Context paramContext)
  {
    SubscriptionManager localSubscriptionManager = SubscriptionManager.from(paramContext);
    if (localSubscriptionManager == null) {
      return -1;
    }
    SubscriptionInfo localSubscriptionInfo = localSubscriptionManager.getDefaultDataSubscriptionInfo();
    paramContext = localSubscriptionInfo;
    if (localSubscriptionInfo == null)
    {
      paramContext = localSubscriptionManager.getAllSubscriptionInfoList();
      if (paramContext.size() == 0) {
        return -1;
      }
      paramContext = (SubscriptionInfo)paramContext.get(0);
    }
    return paramContext.getSubscriptionId();
  }
  
  public static NetworkTemplate getDefaultTemplate(Context paramContext, int paramInt)
  {
    if ((hasMobileData(paramContext)) && (paramInt != -1))
    {
      paramContext = TelephonyManager.from(paramContext);
      return NetworkTemplate.normalize(NetworkTemplate.buildTemplateMobileAll(paramContext.getSubscriberId(paramInt)), paramContext.getMergedSubscriberIds());
    }
    if (hasWifiRadio(paramContext)) {
      return NetworkTemplate.buildTemplateWifiWildcard();
    }
    return NetworkTemplate.buildTemplateEthernet();
  }
  
  private NetworkTemplate getNetworkTemplate(int paramInt)
  {
    return NetworkTemplate.normalize(NetworkTemplate.buildTemplateMobileAll(this.services.mTelephonyManager.getSubscriberId(paramInt)), this.services.mTelephonyManager.getMergedSubscriberIds());
  }
  
  public static boolean hasMobileData(Context paramContext)
  {
    return ConnectivityManager.from(paramContext).isNetworkSupported(0);
  }
  
  public static boolean hasWifiRadio(Context paramContext)
  {
    return ConnectivityManager.from(paramContext).isNetworkSupported(1);
  }
  
  private Preference inflatePreferences(int paramInt)
  {
    PreferenceScreen localPreferenceScreen = getPreferenceManager().inflateFromResource(getPrefContext(), paramInt, null);
    Preference localPreference = localPreferenceScreen.getPreference(0);
    localPreferenceScreen.removeAll();
    localPreferenceScreen = getPreferenceScreen();
    localPreference.setOrder(localPreferenceScreen.getPreferenceCount());
    localPreferenceScreen.addPreference(localPreference);
    return localPreference;
  }
  
  private void updateState()
  {
    Object localObject1 = this.mDataUsageController.getDataUsageInfo(this.mDefaultTemplate);
    Object localObject2 = getContext();
    if (this.mSummaryPreference != null)
    {
      this.mSummaryPreference.setTitle(formatTitle((Context)localObject2, getString(this.mDataUsageTemplate), ((DataUsageController.DataUsageInfo)localObject1).usageLevel));
      long l2 = ((DataUsageController.DataUsageInfo)localObject1).limitLevel;
      long l1 = l2;
      if (l2 <= 0L) {
        l1 = ((DataUsageController.DataUsageInfo)localObject1).warningLevel;
      }
      l2 = l1;
      if (((DataUsageController.DataUsageInfo)localObject1).usageLevel > l1) {
        l2 = ((DataUsageController.DataUsageInfo)localObject1).usageLevel;
      }
      this.mSummaryPreference.setSummary(((DataUsageController.DataUsageInfo)localObject1).period);
      this.mSummaryPreference.setLabels(Formatter.formatFileSize((Context)localObject2, 0L), Formatter.formatFileSize((Context)localObject2, l2));
      this.mSummaryPreference.setRatios((float)((DataUsageController.DataUsageInfo)localObject1).usageLevel / (float)l2, 0.0F, (float)(l2 - ((DataUsageController.DataUsageInfo)localObject1).usageLevel) / (float)l2);
    }
    String str;
    Preference localPreference;
    if (this.mLimitPreference != null)
    {
      str = Formatter.formatFileSize((Context)localObject2, ((DataUsageController.DataUsageInfo)localObject1).warningLevel);
      localObject2 = Formatter.formatFileSize((Context)localObject2, ((DataUsageController.DataUsageInfo)localObject1).limitLevel);
      localPreference = this.mLimitPreference;
      if (((DataUsageController.DataUsageInfo)localObject1).limitLevel > 0L) {
        break label272;
      }
    }
    label272:
    for (int i = 2131693634;; i = 2131693635)
    {
      localPreference.setSummary(getString(i, new Object[] { str, localObject2 }));
      localObject1 = getPreferenceScreen();
      i = 1;
      while (i < ((PreferenceScreen)localObject1).getPreferenceCount())
      {
        ((TemplatePreferenceCategory)((PreferenceScreen)localObject1).getPreference(i)).pushTemplates(this.services);
        i += 1;
      }
    }
  }
  
  private static void verySmallSpanExcept(SpannableString paramSpannableString, CharSequence paramCharSequence)
  {
    int i = TextUtils.indexOf(paramSpannableString, paramCharSequence);
    if (i == -1) {
      paramSpannableString.setSpan(new RelativeSizeSpan(0.64000005F), 0, paramSpannableString.length(), 18);
    }
    do
    {
      return;
      if (i > 0) {
        paramSpannableString.setSpan(new RelativeSizeSpan(0.64000005F), 0, i, 18);
      }
      i += paramCharSequence.length();
    } while (i >= paramSpannableString.length());
    paramSpannableString.setSpan(new RelativeSizeSpan(0.64000005F), i, paramSpannableString.length(), 18);
  }
  
  protected int getMetricsCategory()
  {
    return 37;
  }
  
  public boolean hasEthernet(Context paramContext)
  {
    boolean bool = ConnectivityManager.from(paramContext).isNetworkSupported(9);
    try
    {
      paramContext = this.services.mStatsService.openSession();
      long l;
      if (paramContext != null)
      {
        l = paramContext.getSummaryForNetwork(NetworkTemplate.buildTemplateEthernet(), Long.MIN_VALUE, Long.MAX_VALUE).getTotalBytes();
        TrafficStats.closeQuietly(paramContext);
      }
      while ((bool) && (l > 0L))
      {
        return true;
        l = 0L;
      }
      return false;
    }
    catch (RemoteException paramContext)
    {
      throw new RuntimeException(paramContext);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    boolean bool1 = hasMobileData(getContext());
    this.mDataUsageController = new DataUsageController(getContext());
    addPreferencesFromResource(2131230749);
    int i = getDefaultSubscriptionId(getContext());
    if (i == -1) {
      bool1 = false;
    }
    this.mDefaultTemplate = getDefaultTemplate(getContext(), i);
    if (bool1)
    {
      this.mLimitPreference = findPreference("limit_summary");
      if ((!bool1) || (!isAdmin())) {
        break label169;
      }
    }
    for (;;)
    {
      if (!bool1) {
        break label178;
      }
      paramBundle = this.services.mSubscriptionManager.getActiveSubscriptionInfoList();
      if ((paramBundle == null) || (paramBundle.size() == 0)) {
        addMobileSection(i);
      }
      i = 0;
      while ((paramBundle != null) && (i < paramBundle.size()))
      {
        addMobileSection(((SubscriptionInfo)paramBundle.get(i)).getSubscriptionId());
        i += 1;
      }
      removePreference("limit_summary");
      break;
      label169:
      removePreference("restrict_background");
    }
    label178:
    boolean bool2 = hasWifiRadio(getContext());
    if (bool2) {
      addWifiSection();
    }
    if (hasEthernet(getContext())) {
      addEthernetSection();
    }
    if (bool1) {
      i = 2131693631;
    }
    for (;;)
    {
      this.mDataUsageTemplate = i;
      this.mSummaryPreference = ((SummaryPreference)findPreference("status_header"));
      setHasOptionsMenu(true);
      return;
      if (bool2) {
        i = 2131693632;
      } else {
        i = 2131693633;
      }
    }
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if (UserManager.get(getContext()).isAdminUser()) {
      paramMenuInflater.inflate(2132017153, paramMenu);
    }
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return false;
    case 2131362841: 
      paramMenuItem = new Intent("android.intent.action.MAIN");
      if (Utils.isNetworkSettingsApkAvailable(getContext()))
      {
        Log.d("DataUsageSummary", "qti MobileNetworkSettings Enabled");
        paramMenuItem.setComponent(new ComponentName("com.qualcomm.qti.networksetting", "com.qualcomm.qti.networksetting.MobileNetworkSettings"));
        startActivity(paramMenuItem);
        return true;
      }
      paramMenuItem.setComponent(new ComponentName("com.android.phone", "com.android.phone.MobileNetworkSettings"));
      startActivity(paramMenuItem);
      return true;
    }
    try
    {
      paramMenuItem = new Intent();
      paramMenuItem.setClassName("com.qualcomm.qti.appnetaccess", "com.qualcomm.qti.appnetaccess.NetworkControl");
      paramMenuItem.setAction("android.intent.networkcontrol");
      startActivity(paramMenuItem);
      return true;
    }
    catch (ActivityNotFoundException paramMenuItem)
    {
      Log.d("DataUsageSummary", "activity NetworkControl not found");
    }
    return true;
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    Activity localActivity = getActivity();
    paramMenu = paramMenu.findItem(2131362842);
    if (localActivity.getResources().getBoolean(2131558434))
    {
      paramMenu.setVisible(true);
      return;
    }
    paramMenu.setVisible(false);
  }
  
  public void onResume()
  {
    super.onResume();
    updateState();
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Activity mActivity;
    private final DataUsageController mDataController;
    private final SummaryLoader mSummaryLoader;
    
    public SummaryProvider(Activity paramActivity, SummaryLoader paramSummaryLoader)
    {
      this.mActivity = paramActivity;
      this.mSummaryLoader = paramSummaryLoader;
      this.mDataController = new DataUsageController(paramActivity);
    }
    
    public void setListening(boolean paramBoolean)
    {
      Object localObject;
      if (paramBoolean)
      {
        localObject = this.mDataController.getDataUsageInfo();
        if (localObject != null) {
          break label51;
        }
        localObject = Formatter.formatFileSize(this.mActivity, 0L);
      }
      for (;;)
      {
        this.mSummaryLoader.setSummary(this, this.mActivity.getString(2131693581, new Object[] { localObject }));
        return;
        label51:
        if (((DataUsageController.DataUsageInfo)localObject).limitLevel <= 0L) {
          localObject = Formatter.formatFileSize(this.mActivity, ((DataUsageController.DataUsageInfo)localObject).usageLevel);
        } else {
          localObject = Utils.formatPercentage(((DataUsageController.DataUsageInfo)localObject).usageLevel, ((DataUsageController.DataUsageInfo)localObject).limitLevel);
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\DataUsageSummary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */