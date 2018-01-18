package com.android.settings.datausage;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.INetworkStatsSession;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkStatsHistory;
import android.net.NetworkStatsHistory.Entry;
import android.net.NetworkTemplate;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.text.format.Formatter;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import com.android.settings.AppHeader;
import com.android.settings.Utils;
import com.android.settingslib.AppItem;
import com.android.settingslib.NetworkPolicyEditor;
import com.android.settingslib.net.ChartData;
import com.android.settingslib.net.ChartDataLoader;
import com.android.settingslib.net.UidDetail;
import com.oneplus.settings.ui.OPProgressDialog;
import com.oneplus.settings.ui.OPProgressDialog.OnTimeOutListener;
import com.oneplus.settings.utils.OPFirewallRule;
import com.oneplus.settings.utils.OPFirewallUtils;
import java.util.Iterator;
import java.util.List;

public class AppDataUsage
  extends DataUsageBase
  implements Preference.OnPreferenceChangeListener, DataSaverBackend.Listener
{
  public static final String ARG_APP_ITEM = "app_item";
  public static final String ARG_NETWORK_TEMPLATE = "network_template";
  public static final String ARG_SUBID = "arg_subid";
  private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  private static final int KEEP_ALIVE_SECONDS = 30;
  private static final String KEY_APP_LIST = "app_list";
  private static final String KEY_APP_SETTINGS = "app_settings";
  private static final String KEY_BACKGROUND_USAGE = "background_usage";
  private static final String KEY_CYCLE = "cycle";
  private static final String KEY_DISABLE_MOBILE = "disabled_mobile";
  private static final String KEY_DISABLE_WIFI = "disabled_wifi";
  private static final String KEY_FOREGROUND_USAGE = "foreground_usage";
  private static final String KEY_PF_CYCLE = "pf_cycle";
  private static final String KEY_RESTRICT_BACKGROUND = "restrict_background";
  private static final String KEY_TOTAL_USAGE = "total_usage";
  private static final String KEY_UNRESTRICTED_DATA = "unrestricted_data_saver";
  private static final int LOADER_CHART_DATA = 2;
  private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
  public static UidDetail OSUidDetail;
  public static int SYSTEM_UID = 1000;
  private AppItem mAppItem;
  private PreferenceCategory mAppList;
  private Preference mAppSettings;
  private Intent mAppSettingsIntent;
  private Preference mBackgroundUsage;
  private ChartData mChartData;
  private final LoaderManager.LoaderCallbacks<ChartData> mChartDataCallbacks = new LoaderManager.LoaderCallbacks()
  {
    public Loader<ChartData> onCreateLoader(int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      return new ChartDataLoader(AppDataUsage.this.getActivity(), AppDataUsage.-get7(AppDataUsage.this), paramAnonymousBundle);
    }
    
    public void onLoadFinished(Loader<ChartData> paramAnonymousLoader, ChartData paramAnonymousChartData)
    {
      AppDataUsage.-set0(AppDataUsage.this, paramAnonymousChartData);
      AppDataUsage.-get3(AppDataUsage.this).updateCycleList(AppDataUsage.-get6(AppDataUsage.this), AppDataUsage.-get1(AppDataUsage.this));
      AppDataUsage.-wrap2(AppDataUsage.this);
    }
    
    public void onLoaderReset(Loader<ChartData> paramAnonymousLoader) {}
  };
  private SpinnerPreference mCycle;
  private CycleAdapter mCycleAdapter;
  private AdapterView.OnItemSelectedListener mCycleListener = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      paramAnonymousAdapterView = (CycleAdapter.CycleItem)AppDataUsage.-get2(AppDataUsage.this).getSelectedItem();
      AppDataUsage.-wrap2(AppDataUsage.this);
    }
    
    public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView)
    {
      AppDataUsage.-wrap2(AppDataUsage.this);
    }
  };
  private Preference mCycleText;
  private DataSaverBackend mDataSaverBackend;
  private SwitchPreference mDisabledData;
  private SwitchPreference mDisabledWifi;
  private long mEnd;
  private Preference mForegroundUsage;
  private Drawable mIcon;
  private CharSequence mLabel;
  private String mPackageName;
  private final ArraySet<String> mPackages = new ArraySet();
  private NetworkPolicy mPolicy;
  private SwitchPreference mRestrictBackground;
  private long mStart;
  private INetworkStatsSession mStatsSession;
  private int mSubId = 0;
  private NetworkTemplate mTemplate;
  private Preference mTotalUsage;
  private SwitchPreference mUnrestrictedData;
  
  private void addUid(int paramInt)
  {
    String[] arrayOfString = getPackageManager().getPackagesForUid(paramInt);
    if (arrayOfString != null)
    {
      paramInt = 0;
      while (paramInt < arrayOfString.length)
      {
        this.mPackages.add(arrayOfString[paramInt]);
        paramInt += 1;
      }
    }
  }
  
  private void bindData()
  {
    long l2;
    long l1;
    if ((this.mChartData == null) || (this.mStart == 0L))
    {
      l2 = 0L;
      l1 = 0L;
    }
    for (;;)
    {
      Object localObject = getContext();
      this.mTotalUsage.setSummary(Formatter.formatFileSize((Context)localObject, l1 + l2));
      this.mForegroundUsage.setSummary(Formatter.formatFileSize((Context)localObject, l2));
      this.mBackgroundUsage.setSummary(Formatter.formatFileSize((Context)localObject, l1));
      return;
      l2 = System.currentTimeMillis();
      localObject = this.mChartData.detailDefault.getValues(this.mStart, this.mEnd, l2, null);
      l1 = ((NetworkStatsHistory.Entry)localObject).rxBytes + ((NetworkStatsHistory.Entry)localObject).txBytes;
      localObject = this.mChartData.detailForeground.getValues(this.mStart, this.mEnd, l2, (NetworkStatsHistory.Entry)localObject);
      l2 = ((NetworkStatsHistory.Entry)localObject).rxBytes + ((NetworkStatsHistory.Entry)localObject).txBytes;
    }
  }
  
  private boolean getAppRestrictBackground()
  {
    boolean bool = false;
    int i = this.mAppItem.key;
    if ((this.services.mPolicyManager.getUidPolicy(i) & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean getUnrestrictData()
  {
    if (this.mDataSaverBackend != null) {
      return this.mDataSaverBackend.isWhitelisted(this.mAppItem.key);
    }
    return false;
  }
  
  private void updateFireWallState()
  {
    boolean bool2 = true;
    Object localObject = OPDataUsageUtils.getApplicationInfoByUid(getPrefContext(), this.mAppItem.key);
    if ((localObject == null) || (((List)localObject).isEmpty())) {}
    do
    {
      return;
      localObject = (ApplicationInfo)((List)localObject).get(0);
    } while (localObject == null);
    localObject = OPFirewallUtils.selectFirewallRuleByPkg(getContext(), ((ApplicationInfo)localObject).packageName);
    SwitchPreference localSwitchPreference;
    if ((localObject != null) && (((OPFirewallRule)localObject).getMobile() != null))
    {
      localSwitchPreference = this.mDisabledData;
      if (((OPFirewallRule)localObject).getMobile().intValue() != 0)
      {
        bool1 = true;
        localSwitchPreference.setChecked(bool1);
        label93:
        if ((localObject == null) || (((OPFirewallRule)localObject).getWlan() == null)) {
          break label150;
        }
        localSwitchPreference = this.mDisabledWifi;
        if (((OPFirewallRule)localObject).getWlan().intValue() == 0) {
          break label145;
        }
      }
    }
    label145:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      localSwitchPreference.setChecked(bool1);
      return;
      bool1 = false;
      break;
      this.mDisabledData.setChecked(false);
      break label93;
    }
    label150:
    this.mDisabledWifi.setChecked(false);
  }
  
  private void updatePrefs()
  {
    updatePrefs(getAppRestrictBackground(), getUnrestrictData());
  }
  
  private void updatePrefs(boolean paramBoolean1, boolean paramBoolean2)
  {
    SwitchPreference localSwitchPreference;
    if (this.mRestrictBackground != null)
    {
      localSwitchPreference = this.mRestrictBackground;
      if (!paramBoolean1) {
        break label45;
      }
    }
    label45:
    for (boolean bool = false;; bool = true)
    {
      localSwitchPreference.setChecked(bool);
      if (this.mUnrestrictedData != null)
      {
        if (!paramBoolean1) {
          break;
        }
        this.mUnrestrictedData.setVisible(false);
      }
      return;
    }
    this.mUnrestrictedData.setVisible(true);
    this.mUnrestrictedData.setChecked(paramBoolean2);
  }
  
  protected int getMetricsCategory()
  {
    return 343;
  }
  
  public void onBlacklistStatusChanged(int paramInt, boolean paramBoolean)
  {
    if (this.mAppItem.uids.get(paramInt, false)) {
      updatePrefs(paramBoolean, getUnrestrictData());
    }
  }
  
  /* Error */
  public void onCreate(Bundle paramBundle)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial 401	com/android/settings/datausage/DataUsageBase:onCreate	(Landroid/os/Bundle;)V
    //   5: aload_0
    //   6: invokevirtual 405	com/android/settings/datausage/AppDataUsage:getArguments	()Landroid/os/Bundle;
    //   9: astore 4
    //   11: aload_0
    //   12: aload_0
    //   13: getfield 298	com/android/settings/datausage/AppDataUsage:services	Lcom/android/settings/datausage/TemplatePreference$NetworkServices;
    //   16: getfield 409	com/android/settings/datausage/TemplatePreference$NetworkServices:mStatsService	Landroid/net/INetworkStatsService;
    //   19: invokeinterface 415 1 0
    //   24: putfield 154	com/android/settings/datausage/AppDataUsage:mStatsSession	Landroid/net/INetworkStatsSession;
    //   27: aload 4
    //   29: ifnull +703 -> 732
    //   32: aload 4
    //   34: ldc 24
    //   36: invokevirtual 421	android/os/Bundle:getParcelable	(Ljava/lang/String;)Landroid/os/Parcelable;
    //   39: checkcast 291	com/android/settingslib/AppItem
    //   42: astore_1
    //   43: aload_0
    //   44: aload_1
    //   45: putfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   48: aload 4
    //   50: ifnull +687 -> 737
    //   53: aload 4
    //   55: ldc 27
    //   57: invokevirtual 421	android/os/Bundle:getParcelable	(Ljava/lang/String;)Landroid/os/Parcelable;
    //   60: checkcast 423	android/net/NetworkTemplate
    //   63: astore_1
    //   64: aload_0
    //   65: aload_1
    //   66: putfield 425	com/android/settings/datausage/AppDataUsage:mTemplate	Landroid/net/NetworkTemplate;
    //   69: aload_0
    //   70: getfield 425	com/android/settings/datausage/AppDataUsage:mTemplate	Landroid/net/NetworkTemplate;
    //   73: ifnonnull +20 -> 93
    //   76: aload_0
    //   77: invokevirtual 236	com/android/settings/datausage/AppDataUsage:getContext	()Landroid/content/Context;
    //   80: astore_1
    //   81: aload_0
    //   82: aload_1
    //   83: aload_1
    //   84: invokestatic 431	com/android/settings/datausage/DataUsageSummary:getDefaultSubscriptionId	(Landroid/content/Context;)I
    //   87: invokestatic 435	com/android/settings/datausage/DataUsageSummary:getDefaultTemplate	(Landroid/content/Context;I)Landroid/net/NetworkTemplate;
    //   90: putfield 425	com/android/settings/datausage/AppDataUsage:mTemplate	Landroid/net/NetworkTemplate;
    //   93: aload_0
    //   94: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   97: ifnonnull +691 -> 788
    //   100: aload 4
    //   102: ifnull +640 -> 742
    //   105: aload 4
    //   107: ldc_w 437
    //   110: iconst_m1
    //   111: invokevirtual 441	android/os/Bundle:getInt	(Ljava/lang/String;I)I
    //   114: istore_2
    //   115: iload_2
    //   116: iconst_m1
    //   117: if_icmpne +643 -> 760
    //   120: aload_0
    //   121: invokevirtual 445	com/android/settings/datausage/AppDataUsage:getActivity	()Landroid/app/Activity;
    //   124: invokevirtual 450	android/app/Activity:finish	()V
    //   127: aload_0
    //   128: ldc_w 451
    //   131: invokevirtual 454	com/android/settings/datausage/AppDataUsage:addPreferencesFromResource	(I)V
    //   134: aload_0
    //   135: aload_0
    //   136: ldc 65
    //   138: invokevirtual 458	com/android/settings/datausage/AppDataUsage:findPreference	(Ljava/lang/CharSequence;)Landroid/support/v7/preference/Preference;
    //   141: putfield 238	com/android/settings/datausage/AppDataUsage:mTotalUsage	Landroid/support/v7/preference/Preference;
    //   144: aload_0
    //   145: aload_0
    //   146: ldc 56
    //   148: invokevirtual 458	com/android/settings/datausage/AppDataUsage:findPreference	(Ljava/lang/CharSequence;)Landroid/support/v7/preference/Preference;
    //   151: putfield 252	com/android/settings/datausage/AppDataUsage:mForegroundUsage	Landroid/support/v7/preference/Preference;
    //   154: aload_0
    //   155: aload_0
    //   156: ldc 44
    //   158: invokevirtual 458	com/android/settings/datausage/AppDataUsage:findPreference	(Ljava/lang/CharSequence;)Landroid/support/v7/preference/Preference;
    //   161: putfield 254	com/android/settings/datausage/AppDataUsage:mBackgroundUsage	Landroid/support/v7/preference/Preference;
    //   164: aload 4
    //   166: ifnull +660 -> 826
    //   169: aload 4
    //   171: ldc 30
    //   173: invokevirtual 461	android/os/Bundle:getInt	(Ljava/lang/String;)I
    //   176: istore_2
    //   177: aload_0
    //   178: iload_2
    //   179: putfield 212	com/android/settings/datausage/AppDataUsage:mSubId	I
    //   182: aload_0
    //   183: invokevirtual 162	com/android/settings/datausage/AppDataUsage:getPrefContext	()Landroid/content/Context;
    //   186: aload_0
    //   187: getfield 212	com/android/settings/datausage/AppDataUsage:mSubId	I
    //   190: invokestatic 465	com/android/settings/datausage/OPDataUsageUtils:getDataUsageSectionTimeMillByAccountDay	(Landroid/content/Context;I)[J
    //   193: astore_1
    //   194: aload_0
    //   195: aload_1
    //   196: iconst_0
    //   197: laload
    //   198: putfield 233	com/android/settings/datausage/AppDataUsage:mStart	J
    //   201: aload_0
    //   202: aload_1
    //   203: iconst_1
    //   204: laload
    //   205: putfield 268	com/android/settings/datausage/AppDataUsage:mEnd	J
    //   208: aload_0
    //   209: invokevirtual 162	com/android/settings/datausage/AppDataUsage:getPrefContext	()Landroid/content/Context;
    //   212: aload_0
    //   213: getfield 233	com/android/settings/datausage/AppDataUsage:mStart	J
    //   216: aload_0
    //   217: getfield 268	com/android/settings/datausage/AppDataUsage:mEnd	J
    //   220: invokestatic 471	com/android/settings/Utils:formatDateRange	(Landroid/content/Context;JJ)Ljava/lang/String;
    //   223: astore_1
    //   224: aload_0
    //   225: aload_0
    //   226: ldc 59
    //   228: invokevirtual 458	com/android/settings/datausage/AppDataUsage:findPreference	(Ljava/lang/CharSequence;)Landroid/support/v7/preference/Preference;
    //   231: putfield 473	com/android/settings/datausage/AppDataUsage:mCycleText	Landroid/support/v7/preference/Preference;
    //   234: aload_0
    //   235: getfield 473	com/android/settings/datausage/AppDataUsage:mCycleText	Landroid/support/v7/preference/Preference;
    //   238: aload_1
    //   239: invokevirtual 476	android/support/v7/preference/Preference:setTitle	(Ljava/lang/CharSequence;)V
    //   242: aload_0
    //   243: aload_0
    //   244: ldc 50
    //   246: invokevirtual 458	com/android/settings/datausage/AppDataUsage:findPreference	(Ljava/lang/CharSequence;)Landroid/support/v7/preference/Preference;
    //   249: checkcast 359	android/support/v14/preference/SwitchPreference
    //   252: putfield 143	com/android/settings/datausage/AppDataUsage:mDisabledData	Landroid/support/v14/preference/SwitchPreference;
    //   255: aload_0
    //   256: aload_0
    //   257: ldc 53
    //   259: invokevirtual 458	com/android/settings/datausage/AppDataUsage:findPreference	(Ljava/lang/CharSequence;)Landroid/support/v7/preference/Preference;
    //   262: checkcast 359	android/support/v14/preference/SwitchPreference
    //   265: putfield 146	com/android/settings/datausage/AppDataUsage:mDisabledWifi	Landroid/support/v14/preference/SwitchPreference;
    //   268: aload_0
    //   269: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   272: getfield 294	com/android/settingslib/AppItem:key	I
    //   275: invokestatic 481	android/os/UserHandle:getUserId	(I)I
    //   278: istore_2
    //   279: aload_0
    //   280: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   283: getfield 294	com/android/settingslib/AppItem:key	I
    //   286: sipush 1000
    //   289: if_icmple +9 -> 298
    //   292: invokestatic 486	com/oneplus/settings/utils/OPUtils:isGuestMode	()Z
    //   295: ifeq +536 -> 831
    //   298: aload_0
    //   299: getfield 143	com/android/settings/datausage/AppDataUsage:mDisabledData	Landroid/support/v14/preference/SwitchPreference;
    //   302: iconst_0
    //   303: invokevirtual 381	android/support/v14/preference/SwitchPreference:setVisible	(Z)V
    //   306: aload_0
    //   307: getfield 146	com/android/settings/datausage/AppDataUsage:mDisabledWifi	Landroid/support/v14/preference/SwitchPreference;
    //   310: iconst_0
    //   311: invokevirtual 381	android/support/v14/preference/SwitchPreference:setVisible	(Z)V
    //   314: aload_0
    //   315: aload_0
    //   316: ldc 47
    //   318: invokevirtual 458	com/android/settings/datausage/AppDataUsage:findPreference	(Ljava/lang/CharSequence;)Landroid/support/v7/preference/Preference;
    //   321: checkcast 488	com/android/settings/datausage/SpinnerPreference
    //   324: putfield 135	com/android/settings/datausage/AppDataUsage:mCycle	Lcom/android/settings/datausage/SpinnerPreference;
    //   327: aload_0
    //   328: getfield 135	com/android/settings/datausage/AppDataUsage:mCycle	Lcom/android/settings/datausage/SpinnerPreference;
    //   331: iconst_0
    //   332: invokevirtual 489	com/android/settings/datausage/SpinnerPreference:setVisible	(Z)V
    //   335: aload_0
    //   336: new 491	com/android/settings/datausage/CycleAdapter
    //   339: dup
    //   340: aload_0
    //   341: invokevirtual 236	com/android/settings/datausage/AppDataUsage:getContext	()Landroid/content/Context;
    //   344: aload_0
    //   345: getfield 135	com/android/settings/datausage/AppDataUsage:mCycle	Lcom/android/settings/datausage/SpinnerPreference;
    //   348: aload_0
    //   349: getfield 216	com/android/settings/datausage/AppDataUsage:mCycleListener	Landroid/widget/AdapterView$OnItemSelectedListener;
    //   352: iconst_0
    //   353: invokespecial 494	com/android/settings/datausage/CycleAdapter:<init>	(Landroid/content/Context;Lcom/android/settings/datausage/CycleAdapter$SpinnerInterface;Landroid/widget/AdapterView$OnItemSelectedListener;Z)V
    //   356: putfield 139	com/android/settings/datausage/AppDataUsage:mCycleAdapter	Lcom/android/settings/datausage/CycleAdapter;
    //   359: aload_0
    //   360: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   363: getfield 294	com/android/settingslib/AppItem:key	I
    //   366: ifle +586 -> 952
    //   369: aload_0
    //   370: getfield 210	com/android/settings/datausage/AppDataUsage:mPackages	Landroid/util/ArraySet;
    //   373: invokevirtual 497	android/util/ArraySet:size	()I
    //   376: ifeq +78 -> 454
    //   379: aload_0
    //   380: invokevirtual 168	com/android/settings/datausage/AppDataUsage:getPackageManager	()Landroid/content/pm/PackageManager;
    //   383: astore_1
    //   384: aload_1
    //   385: aload_0
    //   386: getfield 210	com/android/settings/datausage/AppDataUsage:mPackages	Landroid/util/ArraySet;
    //   389: iconst_0
    //   390: invokevirtual 500	android/util/ArraySet:valueAt	(I)Ljava/lang/Object;
    //   393: checkcast 502	java/lang/String
    //   396: iconst_0
    //   397: invokevirtual 506	android/content/pm/PackageManager:getApplicationInfo	(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;
    //   400: astore 4
    //   402: aload_0
    //   403: aload_1
    //   404: aload_1
    //   405: aload 4
    //   407: aload 4
    //   409: invokevirtual 510	android/content/pm/PackageManager:loadUnbadgedItemIcon	(Landroid/content/pm/PackageItemInfo;Landroid/content/pm/ApplicationInfo;)Landroid/graphics/drawable/Drawable;
    //   412: new 478	android/os/UserHandle
    //   415: dup
    //   416: aload_0
    //   417: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   420: getfield 294	com/android/settingslib/AppItem:key	I
    //   423: invokestatic 481	android/os/UserHandle:getUserId	(I)I
    //   426: invokespecial 512	android/os/UserHandle:<init>	(I)V
    //   429: invokevirtual 516	android/content/pm/PackageManager:getUserBadgedIcon	(Landroid/graphics/drawable/Drawable;Landroid/os/UserHandle;)Landroid/graphics/drawable/Drawable;
    //   432: putfield 518	com/android/settings/datausage/AppDataUsage:mIcon	Landroid/graphics/drawable/Drawable;
    //   435: aload_0
    //   436: aload 4
    //   438: aload_1
    //   439: invokevirtual 522	android/content/pm/ApplicationInfo:loadLabel	(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;
    //   442: putfield 524	com/android/settings/datausage/AppDataUsage:mLabel	Ljava/lang/CharSequence;
    //   445: aload_0
    //   446: aload 4
    //   448: getfield 340	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   451: putfield 526	com/android/settings/datausage/AppDataUsage:mPackageName	Ljava/lang/String;
    //   454: aload_0
    //   455: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   458: getfield 294	com/android/settingslib/AppItem:key	I
    //   461: sipush 1000
    //   464: if_icmpne +397 -> 861
    //   467: aload_0
    //   468: ldc 68
    //   470: invokevirtual 530	com/android/settings/datausage/AppDataUsage:removePreference	(Ljava/lang/String;)V
    //   473: aload_0
    //   474: ldc 62
    //   476: invokevirtual 530	com/android/settings/datausage/AppDataUsage:removePreference	(Ljava/lang/String;)V
    //   479: aload_0
    //   480: new 315	com/android/settings/datausage/DataSaverBackend
    //   483: dup
    //   484: aload_0
    //   485: invokevirtual 236	com/android/settings/datausage/AppDataUsage:getContext	()Landroid/content/Context;
    //   488: invokespecial 533	com/android/settings/datausage/DataSaverBackend:<init>	(Landroid/content/Context;)V
    //   491: putfield 313	com/android/settings/datausage/AppDataUsage:mDataSaverBackend	Lcom/android/settings/datausage/DataSaverBackend;
    //   494: aload_0
    //   495: aload_0
    //   496: ldc 41
    //   498: invokevirtual 458	com/android/settings/datausage/AppDataUsage:findPreference	(Ljava/lang/CharSequence;)Landroid/support/v7/preference/Preference;
    //   501: putfield 535	com/android/settings/datausage/AppDataUsage:mAppSettings	Landroid/support/v7/preference/Preference;
    //   504: aload_0
    //   505: new 537	android/content/Intent
    //   508: dup
    //   509: ldc_w 539
    //   512: invokespecial 541	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   515: putfield 543	com/android/settings/datausage/AppDataUsage:mAppSettingsIntent	Landroid/content/Intent;
    //   518: aload_0
    //   519: getfield 543	com/android/settings/datausage/AppDataUsage:mAppSettingsIntent	Landroid/content/Intent;
    //   522: ldc_w 545
    //   525: invokevirtual 549	android/content/Intent:addCategory	(Ljava/lang/String;)Landroid/content/Intent;
    //   528: pop
    //   529: aload_0
    //   530: invokevirtual 168	com/android/settings/datausage/AppDataUsage:getPackageManager	()Landroid/content/pm/PackageManager;
    //   533: astore_1
    //   534: iconst_0
    //   535: istore_3
    //   536: aload_0
    //   537: getfield 210	com/android/settings/datausage/AppDataUsage:mPackages	Landroid/util/ArraySet;
    //   540: invokeinterface 555 1 0
    //   545: astore 4
    //   547: iload_3
    //   548: istore_2
    //   549: aload 4
    //   551: invokeinterface 560 1 0
    //   556: ifeq +39 -> 595
    //   559: aload 4
    //   561: invokeinterface 564 1 0
    //   566: checkcast 502	java/lang/String
    //   569: astore 5
    //   571: aload_0
    //   572: getfield 543	com/android/settings/datausage/AppDataUsage:mAppSettingsIntent	Landroid/content/Intent;
    //   575: aload 5
    //   577: invokevirtual 567	android/content/Intent:setPackage	(Ljava/lang/String;)Landroid/content/Intent;
    //   580: pop
    //   581: aload_1
    //   582: aload_0
    //   583: getfield 543	com/android/settings/datausage/AppDataUsage:mAppSettingsIntent	Landroid/content/Intent;
    //   586: iconst_0
    //   587: invokevirtual 571	android/content/pm/PackageManager:resolveActivity	(Landroid/content/Intent;I)Landroid/content/pm/ResolveInfo;
    //   590: ifnull -43 -> 547
    //   593: iconst_1
    //   594: istore_2
    //   595: iload_2
    //   596: ifne +14 -> 610
    //   599: aload_0
    //   600: ldc 41
    //   602: invokevirtual 530	com/android/settings/datausage/AppDataUsage:removePreference	(Ljava/lang/String;)V
    //   605: aload_0
    //   606: aconst_null
    //   607: putfield 535	com/android/settings/datausage/AppDataUsage:mAppSettings	Landroid/support/v7/preference/Preference;
    //   610: aload_0
    //   611: getfield 210	com/android/settings/datausage/AppDataUsage:mPackages	Landroid/util/ArraySet;
    //   614: invokevirtual 497	android/util/ArraySet:size	()I
    //   617: iconst_1
    //   618: if_icmple +288 -> 906
    //   621: aload_0
    //   622: aload_0
    //   623: ldc 38
    //   625: invokevirtual 458	com/android/settings/datausage/AppDataUsage:findPreference	(Ljava/lang/CharSequence;)Landroid/support/v7/preference/Preference;
    //   628: checkcast 573	android/support/v7/preference/PreferenceCategory
    //   631: putfield 126	com/android/settings/datausage/AppDataUsage:mAppList	Landroid/support/v7/preference/PreferenceCategory;
    //   634: new 575	java/util/concurrent/LinkedBlockingQueue
    //   637: dup
    //   638: aload_0
    //   639: getfield 210	com/android/settings/datausage/AppDataUsage:mPackages	Landroid/util/ArraySet;
    //   642: invokevirtual 497	android/util/ArraySet:size	()I
    //   645: invokespecial 576	java/util/concurrent/LinkedBlockingQueue:<init>	(I)V
    //   648: astore_1
    //   649: new 578	java/util/concurrent/ThreadPoolExecutor
    //   652: dup
    //   653: getstatic 198	com/android/settings/datausage/AppDataUsage:CORE_POOL_SIZE	I
    //   656: getstatic 200	com/android/settings/datausage/AppDataUsage:MAXIMUM_POOL_SIZE	I
    //   659: ldc2_w 579
    //   662: getstatic 586	java/util/concurrent/TimeUnit:SECONDS	Ljava/util/concurrent/TimeUnit;
    //   665: aload_1
    //   666: invokespecial 589	java/util/concurrent/ThreadPoolExecutor:<init>	(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;)V
    //   669: astore_1
    //   670: iconst_1
    //   671: istore_2
    //   672: iload_2
    //   673: aload_0
    //   674: getfield 210	com/android/settings/datausage/AppDataUsage:mPackages	Landroid/util/ArraySet;
    //   677: invokevirtual 497	android/util/ArraySet:size	()I
    //   680: if_icmpge +232 -> 912
    //   683: new 14	com/android/settings/datausage/AppDataUsage$AppPrefLoader
    //   686: dup
    //   687: aload_0
    //   688: aconst_null
    //   689: invokespecial 592	com/android/settings/datausage/AppDataUsage$AppPrefLoader:<init>	(Lcom/android/settings/datausage/AppDataUsage;Lcom/android/settings/datausage/AppDataUsage$AppPrefLoader;)V
    //   692: aload_1
    //   693: iconst_1
    //   694: anewarray 502	java/lang/String
    //   697: dup
    //   698: iconst_0
    //   699: aload_0
    //   700: getfield 210	com/android/settings/datausage/AppDataUsage:mPackages	Landroid/util/ArraySet;
    //   703: iload_2
    //   704: invokevirtual 500	android/util/ArraySet:valueAt	(I)Ljava/lang/Object;
    //   707: checkcast 502	java/lang/String
    //   710: aastore
    //   711: invokevirtual 596	com/android/settings/datausage/AppDataUsage$AppPrefLoader:executeOnExecutor	(Ljava/util/concurrent/Executor;[Ljava/lang/Object;)Landroid/os/AsyncTask;
    //   714: pop
    //   715: iload_2
    //   716: iconst_1
    //   717: iadd
    //   718: istore_2
    //   719: goto -47 -> 672
    //   722: astore_1
    //   723: new 598	java/lang/RuntimeException
    //   726: dup
    //   727: aload_1
    //   728: invokespecial 601	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   731: athrow
    //   732: aconst_null
    //   733: astore_1
    //   734: goto -691 -> 43
    //   737: aconst_null
    //   738: astore_1
    //   739: goto -675 -> 64
    //   742: aload_0
    //   743: invokevirtual 445	com/android/settings/datausage/AppDataUsage:getActivity	()Landroid/app/Activity;
    //   746: invokevirtual 605	android/app/Activity:getIntent	()Landroid/content/Intent;
    //   749: ldc_w 437
    //   752: iconst_m1
    //   753: invokevirtual 608	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   756: istore_2
    //   757: goto -642 -> 115
    //   760: aload_0
    //   761: iload_2
    //   762: invokespecial 610	com/android/settings/datausage/AppDataUsage:addUid	(I)V
    //   765: aload_0
    //   766: new 291	com/android/settingslib/AppItem
    //   769: dup
    //   770: iload_2
    //   771: invokespecial 611	com/android/settingslib/AppItem:<init>	(I)V
    //   774: putfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   777: aload_0
    //   778: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   781: iload_2
    //   782: invokevirtual 612	com/android/settingslib/AppItem:addUid	(I)V
    //   785: goto -658 -> 127
    //   788: iconst_0
    //   789: istore_2
    //   790: iload_2
    //   791: aload_0
    //   792: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   795: getfield 388	com/android/settingslib/AppItem:uids	Landroid/util/SparseBooleanArray;
    //   798: invokevirtual 613	android/util/SparseBooleanArray:size	()I
    //   801: if_icmpge -674 -> 127
    //   804: aload_0
    //   805: aload_0
    //   806: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   809: getfield 388	com/android/settingslib/AppItem:uids	Landroid/util/SparseBooleanArray;
    //   812: iload_2
    //   813: invokevirtual 616	android/util/SparseBooleanArray:keyAt	(I)I
    //   816: invokespecial 610	com/android/settings/datausage/AppDataUsage:addUid	(I)V
    //   819: iload_2
    //   820: iconst_1
    //   821: iadd
    //   822: istore_2
    //   823: goto -33 -> 790
    //   826: iconst_0
    //   827: istore_2
    //   828: goto -651 -> 177
    //   831: iload_2
    //   832: sipush 999
    //   835: if_icmpeq -537 -> 298
    //   838: aload_0
    //   839: getfield 143	com/android/settings/datausage/AppDataUsage:mDisabledData	Landroid/support/v14/preference/SwitchPreference;
    //   842: aload_0
    //   843: invokevirtual 620	android/support/v14/preference/SwitchPreference:setOnPreferenceChangeListener	(Landroid/support/v7/preference/Preference$OnPreferenceChangeListener;)V
    //   846: aload_0
    //   847: getfield 146	com/android/settings/datausage/AppDataUsage:mDisabledWifi	Landroid/support/v14/preference/SwitchPreference;
    //   850: aload_0
    //   851: invokevirtual 620	android/support/v14/preference/SwitchPreference:setOnPreferenceChangeListener	(Landroid/support/v7/preference/Preference$OnPreferenceChangeListener;)V
    //   854: aload_0
    //   855: invokespecial 622	com/android/settings/datausage/AppDataUsage:updateFireWallState	()V
    //   858: goto -544 -> 314
    //   861: aload_0
    //   862: aload_0
    //   863: ldc 62
    //   865: invokevirtual 458	com/android/settings/datausage/AppDataUsage:findPreference	(Ljava/lang/CharSequence;)Landroid/support/v7/preference/Preference;
    //   868: checkcast 359	android/support/v14/preference/SwitchPreference
    //   871: putfield 376	com/android/settings/datausage/AppDataUsage:mRestrictBackground	Landroid/support/v14/preference/SwitchPreference;
    //   874: aload_0
    //   875: getfield 376	com/android/settings/datausage/AppDataUsage:mRestrictBackground	Landroid/support/v14/preference/SwitchPreference;
    //   878: aload_0
    //   879: invokevirtual 620	android/support/v14/preference/SwitchPreference:setOnPreferenceChangeListener	(Landroid/support/v7/preference/Preference$OnPreferenceChangeListener;)V
    //   882: aload_0
    //   883: aload_0
    //   884: ldc 68
    //   886: invokevirtual 458	com/android/settings/datausage/AppDataUsage:findPreference	(Ljava/lang/CharSequence;)Landroid/support/v7/preference/Preference;
    //   889: checkcast 359	android/support/v14/preference/SwitchPreference
    //   892: putfield 378	com/android/settings/datausage/AppDataUsage:mUnrestrictedData	Landroid/support/v14/preference/SwitchPreference;
    //   895: aload_0
    //   896: getfield 378	com/android/settings/datausage/AppDataUsage:mUnrestrictedData	Landroid/support/v14/preference/SwitchPreference;
    //   899: aload_0
    //   900: invokevirtual 620	android/support/v14/preference/SwitchPreference:setOnPreferenceChangeListener	(Landroid/support/v7/preference/Preference$OnPreferenceChangeListener;)V
    //   903: goto -424 -> 479
    //   906: aload_0
    //   907: ldc 38
    //   909: invokevirtual 530	com/android/settings/datausage/AppDataUsage:removePreference	(Ljava/lang/String;)V
    //   912: aload_0
    //   913: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   916: getfield 294	com/android/settingslib/AppItem:key	I
    //   919: getstatic 202	com/android/settings/datausage/AppDataUsage:SYSTEM_UID	I
    //   922: if_icmpne +29 -> 951
    //   925: getstatic 624	com/android/settings/datausage/AppDataUsage:OSUidDetail	Lcom/android/settingslib/net/UidDetail;
    //   928: ifnull +23 -> 951
    //   931: aload_0
    //   932: getstatic 624	com/android/settings/datausage/AppDataUsage:OSUidDetail	Lcom/android/settingslib/net/UidDetail;
    //   935: getfield 629	com/android/settingslib/net/UidDetail:icon	Landroid/graphics/drawable/Drawable;
    //   938: putfield 518	com/android/settings/datausage/AppDataUsage:mIcon	Landroid/graphics/drawable/Drawable;
    //   941: aload_0
    //   942: getstatic 624	com/android/settings/datausage/AppDataUsage:OSUidDetail	Lcom/android/settingslib/net/UidDetail;
    //   945: getfield 632	com/android/settingslib/net/UidDetail:label	Ljava/lang/CharSequence;
    //   948: putfield 524	com/android/settings/datausage/AppDataUsage:mLabel	Ljava/lang/CharSequence;
    //   951: return
    //   952: aload_0
    //   953: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   956: getfield 294	com/android/settingslib/AppItem:key	I
    //   959: bipush -4
    //   961: if_icmpne +146 -> 1107
    //   964: aload_0
    //   965: aload_0
    //   966: invokevirtual 236	com/android/settings/datausage/AppDataUsage:getContext	()Landroid/content/Context;
    //   969: ldc_w 633
    //   972: invokevirtual 639	android/content/Context:getString	(I)Ljava/lang/String;
    //   975: putfield 524	com/android/settings/datausage/AppDataUsage:mLabel	Ljava/lang/CharSequence;
    //   978: getstatic 624	com/android/settings/datausage/AppDataUsage:OSUidDetail	Lcom/android/settingslib/net/UidDetail;
    //   981: ifnull +13 -> 994
    //   984: aload_0
    //   985: getstatic 624	com/android/settings/datausage/AppDataUsage:OSUidDetail	Lcom/android/settingslib/net/UidDetail;
    //   988: getfield 629	com/android/settingslib/net/UidDetail:icon	Landroid/graphics/drawable/Drawable;
    //   991: putfield 518	com/android/settings/datausage/AppDataUsage:mIcon	Landroid/graphics/drawable/Drawable;
    //   994: aload_0
    //   995: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   998: getfield 294	com/android/settingslib/AppItem:key	I
    //   1001: bipush -5
    //   1003: if_icmpne +210 -> 1213
    //   1006: aload_0
    //   1007: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   1010: getfield 294	com/android/settingslib/AppItem:key	I
    //   1013: invokestatic 481	android/os/UserHandle:getUserId	(I)I
    //   1016: istore_2
    //   1017: aload_0
    //   1018: invokevirtual 445	com/android/settings/datausage/AppDataUsage:getActivity	()Landroid/app/Activity;
    //   1021: invokestatic 644	android/os/UserManager:get	(Landroid/content/Context;)Landroid/os/UserManager;
    //   1024: astore_1
    //   1025: aload_1
    //   1026: iload_2
    //   1027: invokevirtual 648	android/os/UserManager:getUserInfo	(I)Landroid/content/pm/UserInfo;
    //   1030: astore 4
    //   1032: aload_0
    //   1033: invokevirtual 168	com/android/settings/datausage/AppDataUsage:getPackageManager	()Landroid/content/pm/PackageManager;
    //   1036: pop
    //   1037: aload 4
    //   1039: ifnull +30 -> 1069
    //   1042: aload_0
    //   1043: aload_0
    //   1044: invokevirtual 445	com/android/settings/datausage/AppDataUsage:getActivity	()Landroid/app/Activity;
    //   1047: aload_1
    //   1048: aload 4
    //   1050: invokestatic 654	com/android/settingslib/Utils:getUserIcon	(Landroid/content/Context;Landroid/os/UserManager;Landroid/content/pm/UserInfo;)Lcom/android/settingslib/drawable/UserIconDrawable;
    //   1053: putfield 518	com/android/settings/datausage/AppDataUsage:mIcon	Landroid/graphics/drawable/Drawable;
    //   1056: aload_0
    //   1057: aload_0
    //   1058: invokevirtual 445	com/android/settings/datausage/AppDataUsage:getActivity	()Landroid/app/Activity;
    //   1061: aload 4
    //   1063: invokestatic 658	com/android/settingslib/Utils:getUserLabel	(Landroid/content/Context;Landroid/content/pm/UserInfo;)Ljava/lang/String;
    //   1066: putfield 524	com/android/settings/datausage/AppDataUsage:mLabel	Ljava/lang/CharSequence;
    //   1069: aload_0
    //   1070: aload_0
    //   1071: invokevirtual 445	com/android/settings/datausage/AppDataUsage:getActivity	()Landroid/app/Activity;
    //   1074: invokevirtual 662	android/app/Activity:getPackageName	()Ljava/lang/String;
    //   1077: putfield 526	com/android/settings/datausage/AppDataUsage:mPackageName	Ljava/lang/String;
    //   1080: aload_0
    //   1081: ldc 68
    //   1083: invokevirtual 530	com/android/settings/datausage/AppDataUsage:removePreference	(Ljava/lang/String;)V
    //   1086: aload_0
    //   1087: ldc 41
    //   1089: invokevirtual 530	com/android/settings/datausage/AppDataUsage:removePreference	(Ljava/lang/String;)V
    //   1092: aload_0
    //   1093: ldc 62
    //   1095: invokevirtual 530	com/android/settings/datausage/AppDataUsage:removePreference	(Ljava/lang/String;)V
    //   1098: aload_0
    //   1099: ldc 38
    //   1101: invokevirtual 530	com/android/settings/datausage/AppDataUsage:removePreference	(Ljava/lang/String;)V
    //   1104: goto -192 -> 912
    //   1107: aload_0
    //   1108: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   1111: getfield 294	com/android/settingslib/AppItem:key	I
    //   1114: bipush -5
    //   1116: if_icmpne +20 -> 1136
    //   1119: aload_0
    //   1120: aload_0
    //   1121: invokevirtual 236	com/android/settings/datausage/AppDataUsage:getContext	()Landroid/content/Context;
    //   1124: ldc_w 663
    //   1127: invokevirtual 639	android/content/Context:getString	(I)Ljava/lang/String;
    //   1130: putfield 524	com/android/settings/datausage/AppDataUsage:mLabel	Ljava/lang/CharSequence;
    //   1133: goto -139 -> 994
    //   1136: aload_0
    //   1137: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   1140: getfield 294	com/android/settingslib/AppItem:key	I
    //   1143: invokestatic 668	com/android/settingslib/net/UidDetailProvider:getUserIdForKey	(I)I
    //   1146: istore_2
    //   1147: aload_0
    //   1148: invokevirtual 445	com/android/settings/datausage/AppDataUsage:getActivity	()Landroid/app/Activity;
    //   1151: invokestatic 644	android/os/UserManager:get	(Landroid/content/Context;)Landroid/os/UserManager;
    //   1154: astore_1
    //   1155: aload_1
    //   1156: iload_2
    //   1157: invokevirtual 648	android/os/UserManager:getUserInfo	(I)Landroid/content/pm/UserInfo;
    //   1160: astore 4
    //   1162: aload_0
    //   1163: invokevirtual 168	com/android/settings/datausage/AppDataUsage:getPackageManager	()Landroid/content/pm/PackageManager;
    //   1166: pop
    //   1167: aload 4
    //   1169: ifnull +30 -> 1199
    //   1172: aload_0
    //   1173: aload_0
    //   1174: invokevirtual 445	com/android/settings/datausage/AppDataUsage:getActivity	()Landroid/app/Activity;
    //   1177: aload_1
    //   1178: aload 4
    //   1180: invokestatic 654	com/android/settingslib/Utils:getUserIcon	(Landroid/content/Context;Landroid/os/UserManager;Landroid/content/pm/UserInfo;)Lcom/android/settingslib/drawable/UserIconDrawable;
    //   1183: putfield 518	com/android/settings/datausage/AppDataUsage:mIcon	Landroid/graphics/drawable/Drawable;
    //   1186: aload_0
    //   1187: aload_0
    //   1188: invokevirtual 445	com/android/settings/datausage/AppDataUsage:getActivity	()Landroid/app/Activity;
    //   1191: aload 4
    //   1193: invokestatic 658	com/android/settingslib/Utils:getUserLabel	(Landroid/content/Context;Landroid/content/pm/UserInfo;)Ljava/lang/String;
    //   1196: putfield 524	com/android/settings/datausage/AppDataUsage:mLabel	Ljava/lang/CharSequence;
    //   1199: aload_0
    //   1200: aload_0
    //   1201: invokevirtual 445	com/android/settings/datausage/AppDataUsage:getActivity	()Landroid/app/Activity;
    //   1204: invokevirtual 662	android/app/Activity:getPackageName	()Ljava/lang/String;
    //   1207: putfield 526	com/android/settings/datausage/AppDataUsage:mPackageName	Ljava/lang/String;
    //   1210: goto -216 -> 994
    //   1213: aload_0
    //   1214: getfield 289	com/android/settings/datausage/AppDataUsage:mAppItem	Lcom/android/settingslib/AppItem;
    //   1217: getfield 294	com/android/settingslib/AppItem:key	I
    //   1220: invokestatic 668	com/android/settingslib/net/UidDetailProvider:getUserIdForKey	(I)I
    //   1223: istore_2
    //   1224: goto -207 -> 1017
    //   1227: astore_1
    //   1228: goto -774 -> 454
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1231	0	this	AppDataUsage
    //   0	1231	1	paramBundle	Bundle
    //   114	1110	2	i	int
    //   535	13	3	j	int
    //   9	1183	4	localObject	Object
    //   569	7	5	str	String
    // Exception table:
    //   from	to	target	type
    //   11	27	722	android/os/RemoteException
    //   384	454	1227	android/content/pm/PackageManager$NameNotFoundException
  }
  
  public void onDataSaverChanged(boolean paramBoolean) {}
  
  public void onDestroy()
  {
    TrafficStats.closeQuietly(this.mStatsSession);
    if (OSUidDetail != null)
    {
      OSUidDetail.icon.setCallback(null);
      OSUidDetail = null;
    }
    super.onDestroy();
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.mDataSaverBackend != null) {
      this.mDataSaverBackend.remListener(this);
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (Utils.isMonkeyRunning()) {
      return false;
    }
    if (paramPreference == this.mRestrictBackground)
    {
      paramPreference = this.mDataSaverBackend;
      i = this.mAppItem.key;
      String str = this.mPackageName;
      if (((Boolean)paramObject).booleanValue()) {}
      for (boolean bool = false;; bool = true)
      {
        paramPreference.setIsBlacklisted(i, str, bool);
        return true;
      }
    }
    if (paramPreference == this.mUnrestrictedData)
    {
      this.mDataSaverBackend.setIsWhitelisted(this.mAppItem.key, this.mPackageName, ((Boolean)paramObject).booleanValue());
      return true;
    }
    if (paramPreference == this.mDisabledWifi)
    {
      i = this.mAppItem.key;
      new UpdateRuleTask(getPrefContext(), i, ((Boolean)paramObject).booleanValue(), 1).execute(new Void[0]);
    }
    while (paramPreference != this.mDisabledData) {
      return false;
    }
    int i = this.mAppItem.key;
    new UpdateRuleTask(getPrefContext(), i, ((Boolean)paramObject).booleanValue(), 0).execute(new Void[0]);
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if (paramPreference == this.mAppSettings)
    {
      getActivity().startActivityAsUser(this.mAppSettingsIntent, new UserHandle(UserHandle.getUserId(this.mAppItem.key)));
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mDataSaverBackend != null) {
      this.mDataSaverBackend.addListener(this);
    }
    this.mPolicy = this.services.mPolicyEditor.getPolicy(this.mTemplate);
    getLoaderManager().restartLoader(2, ChartDataLoader.buildArgs(this.mTemplate, this.mAppItem), this.mChartDataCallbacks);
    updatePrefs();
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    paramBundle = setPinnedHeaderView(2130968613);
    int i;
    if (this.mPackages.size() != 0)
    {
      paramView = (String)this.mPackages.valueAt(0);
      i = 0;
      if (paramView == null) {
        break label87;
      }
    }
    for (;;)
    {
      try
      {
        int j = getPackageManager().getPackageUid(paramView, 0);
        i = j;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        label87:
        continue;
      }
      AppHeader.setupHeaderView(getActivity(), this.mIcon, this.mLabel, paramView, i, AppHeader.includeAppInfo(this), 0, paramBundle, null);
      return;
      paramView = null;
      break;
      i = 0;
    }
  }
  
  public void onWhitelistStatusChanged(int paramInt, boolean paramBoolean)
  {
    if (this.mAppItem.uids.get(paramInt, false)) {
      updatePrefs(getAppRestrictBackground(), paramBoolean);
    }
  }
  
  private class AppPrefLoader
    extends AsyncTask<String, Void, Preference>
  {
    private AppPrefLoader() {}
    
    protected Preference doInBackground(String... paramVarArgs)
    {
      PackageManager localPackageManager = AppDataUsage.-wrap1(AppDataUsage.this);
      paramVarArgs = paramVarArgs[0];
      try
      {
        paramVarArgs = localPackageManager.getApplicationInfo(paramVarArgs, 0);
        Preference localPreference = new Preference(AppDataUsage.-wrap0(AppDataUsage.this));
        localPreference.setIcon(paramVarArgs.loadIcon(localPackageManager));
        localPreference.setTitle(paramVarArgs.loadLabel(localPackageManager));
        localPreference.setSelectable(false);
        return localPreference;
      }
      catch (PackageManager.NameNotFoundException paramVarArgs) {}
      return null;
    }
    
    protected void onPostExecute(Preference paramPreference)
    {
      if ((paramPreference != null) && (AppDataUsage.-get0(AppDataUsage.this) != null)) {
        AppDataUsage.-get0(AppDataUsage.this).addPreference(paramPreference);
      }
    }
  }
  
  class UpdateRuleTask
    extends AsyncTask<Void, Integer, Integer>
  {
    public static final int TYPE_MOBILE = 0;
    public static final int TYPE_WLAN = 1;
    private Context mContext;
    OPProgressDialog progressDialog;
    private boolean state;
    private int type;
    private int uid;
    
    public UpdateRuleTask(Context paramContext, int paramInt1, boolean paramBoolean, int paramInt2)
    {
      this.mContext = paramContext;
      this.uid = paramInt1;
      this.state = paramBoolean;
      this.type = paramInt2;
    }
    
    protected Integer doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = OPDataUsageUtils.getApplicationInfoByUid(this.mContext, this.uid);
      Iterator localIterator = paramVarArgs.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = (ApplicationInfo)localIterator.next();
        if (localObject != null)
        {
          if (this.type == 0)
          {
            if (this.state) {}
            for (i = 1;; i = 0)
            {
              localObject = new OPFirewallRule(((ApplicationInfo)localObject).packageName, null, Integer.valueOf(i));
              OPFirewallUtils.addOrUpdateRole(AppDataUsage.this.getContext(), (OPFirewallRule)localObject);
              break;
            }
          }
          if (this.state) {}
          for (int i = 1;; i = 0)
          {
            localObject = new OPFirewallRule(((ApplicationInfo)localObject).packageName, Integer.valueOf(i), null);
            OPFirewallUtils.addOrUpdateRole(AppDataUsage.this.getContext(), (OPFirewallRule)localObject);
            break;
          }
        }
      }
      return Integer.valueOf(paramVarArgs.size());
    }
    
    protected void onPostExecute(Integer paramInteger)
    {
      super.onPostExecute(paramInteger);
      if (this.type == 0) {
        AppDataUsage.-get4(AppDataUsage.this).setChecked(this.state);
      }
      for (;;)
      {
        if (this.progressDialog != null) {
          this.progressDialog.dismiss();
        }
        return;
        AppDataUsage.-get5(AppDataUsage.this).setChecked(this.state);
      }
    }
    
    protected void onPreExecute()
    {
      super.onPreExecute();
      this.progressDialog = new OPProgressDialog(this.mContext);
      this.progressDialog.setMessage(this.mContext.getString(2131691999));
      this.progressDialog.setTimeOut(5000L, new OPProgressDialog.OnTimeOutListener()
      {
        public void onTimeOut(OPProgressDialog paramAnonymousOPProgressDialog)
        {
          Log.d("UpdateRuleTask", "UpdateRuleTask onTimeOut");
        }
      });
      this.progressDialog.showDelay(1000L);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\AppDataUsage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */