package com.android.settingslib.net;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.INetworkStatsService;
import android.net.INetworkStatsService.Stub;
import android.net.INetworkStatsSession;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkStatsHistory;
import android.net.NetworkStatsHistory.Entry;
import android.net.NetworkTemplate;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import com.android.settingslib.R.integer;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class DataUsageController
{
  private static final boolean DEBUG = Log.isLoggable("DataUsageController", 3);
  private static final int FIELDS = 10;
  private static final StringBuilder PERIOD_BUILDER = new StringBuilder(50);
  private static final Formatter PERIOD_FORMATTER = new Formatter(PERIOD_BUILDER, Locale.getDefault());
  private static final String TAG = "DataUsageController";
  private Callback mCallback;
  private final ConnectivityManager mConnectivityManager;
  private final Context mContext;
  private NetworkNameProvider mNetworkController;
  private final NetworkPolicyManager mPolicyManager;
  private INetworkStatsSession mSession;
  private final INetworkStatsService mStatsService;
  private SubscriptionManager mSubscriptionManager;
  private final TelephonyManager mTelephonyManager;
  
  public DataUsageController(Context paramContext)
  {
    this.mContext = paramContext;
    this.mTelephonyManager = TelephonyManager.from(paramContext);
    this.mConnectivityManager = ConnectivityManager.from(paramContext);
    this.mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
    this.mPolicyManager = NetworkPolicyManager.from(this.mContext);
    this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
  }
  
  private static Time addMonth(Time paramTime, int paramInt)
  {
    Time localTime = new Time(paramTime);
    localTime.set(paramTime.monthDay, paramTime.month + paramInt, paramTime.year);
    localTime.normalize(false);
    return localTime;
  }
  
  private NetworkPolicy findNetworkPolicy(NetworkTemplate paramNetworkTemplate)
  {
    if ((this.mPolicyManager == null) || (paramNetworkTemplate == null)) {
      return null;
    }
    NetworkPolicy[] arrayOfNetworkPolicy = this.mPolicyManager.getNetworkPolicies();
    if (arrayOfNetworkPolicy == null) {
      return null;
    }
    int j = arrayOfNetworkPolicy.length;
    int i = 0;
    while (i < j)
    {
      NetworkPolicy localNetworkPolicy = arrayOfNetworkPolicy[i];
      if ((localNetworkPolicy != null) && (paramNetworkTemplate.equals(localNetworkPolicy.template))) {
        return localNetworkPolicy;
      }
      i += 1;
    }
    return null;
  }
  
  private String formatDateRange(long paramLong1, long paramLong2)
  {
    synchronized (PERIOD_BUILDER)
    {
      PERIOD_BUILDER.setLength(0);
      String str = DateUtils.formatDateRange(this.mContext, PERIOD_FORMATTER, paramLong1, paramLong2, 65552, null).toString();
      return str;
    }
  }
  
  private static String getActiveSubscriberId(Context paramContext)
  {
    return TelephonyManager.from(paramContext).getSubscriberId(SubscriptionManager.getDefaultDataSubscriptionId());
  }
  
  private INetworkStatsSession getSession()
  {
    if (this.mSession == null) {}
    try
    {
      this.mSession = this.mStatsService.openSession();
      return this.mSession;
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        Log.w("DataUsageController", "Failed to open stats session", localRuntimeException);
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.w("DataUsageController", "Failed to open stats session", localRemoteException);
      }
    }
  }
  
  private static String historyEntryToString(NetworkStatsHistory.Entry paramEntry)
  {
    if (paramEntry == null) {
      return null;
    }
    return "Entry[" + "bucketDuration=" + paramEntry.bucketDuration + ",bucketStart=" + paramEntry.bucketStart + ",activeTime=" + paramEntry.activeTime + ",rxBytes=" + paramEntry.rxBytes + ",rxPackets=" + paramEntry.rxPackets + ",txBytes=" + paramEntry.txBytes + ",txPackets=" + paramEntry.txPackets + ",operations=" + paramEntry.operations + ']';
  }
  
  private DataUsageInfo warn(String paramString)
  {
    Log.w("DataUsageController", "Failed to get data usage, " + paramString);
    return null;
  }
  
  public DataUsageInfo getDataUsageInfo()
  {
    String str = getActiveSubscriberId(this.mContext);
    if (str == null) {
      return warn("no subscriber id");
    }
    return getDataUsageInfo(NetworkTemplate.normalize(NetworkTemplate.buildTemplateMobileAll(str), this.mTelephonyManager.getMergedSubscriberIds()));
  }
  
  public DataUsageInfo getDataUsageInfo(NetworkTemplate paramNetworkTemplate)
  {
    Object localObject = getSession();
    if (localObject == null) {
      return warn("no stats session");
    }
    NetworkPolicy localNetworkPolicy = findNetworkPolicy(paramNetworkTemplate);
    for (;;)
    {
      long l3;
      try
      {
        paramNetworkTemplate = ((INetworkStatsSession)localObject).getHistoryForNetwork(paramNetworkTemplate, 10);
        l3 = System.currentTimeMillis();
        if ((localNetworkPolicy != null) && (localNetworkPolicy.cycleDay > 0))
        {
          if (DEBUG) {
            Log.d("DataUsageController", "Cycle day=" + localNetworkPolicy.cycleDay + " tz=" + localNetworkPolicy.cycleTimezone);
          }
          localObject = new Time(localNetworkPolicy.cycleTimezone);
          ((Time)localObject).setToNow();
          Time localTime = new Time((Time)localObject);
          localTime.set(localNetworkPolicy.cycleDay, localTime.month, localTime.year);
          localTime.normalize(false);
          if (((Time)localObject).after(localTime))
          {
            l1 = localTime.toMillis(false);
            l2 = addMonth(localTime, 1).toMillis(false);
            l4 = System.currentTimeMillis();
            paramNetworkTemplate = paramNetworkTemplate.getValues(l1, l2, l3, null);
            long l5 = System.currentTimeMillis();
            if (DEBUG) {
              Log.d("DataUsageController", String.format("history call from %s to %s now=%s took %sms: %s", new Object[] { new Date(l1), new Date(l2), new Date(l3), Long.valueOf(l5 - l4), historyEntryToString(paramNetworkTemplate) }));
            }
            if (paramNetworkTemplate == null) {
              return warn("no entry data");
            }
          }
          else
          {
            l1 = addMonth(localTime, -1).toMillis(false);
            l2 = localTime.toMillis(false);
            continue;
          }
          l3 = paramNetworkTemplate.rxBytes;
          long l4 = paramNetworkTemplate.txBytes;
          paramNetworkTemplate = new DataUsageInfo();
          paramNetworkTemplate.startDate = l1;
          paramNetworkTemplate.usageLevel = (l3 + l4);
          paramNetworkTemplate.period = formatDateRange(l1, l2);
          if (localNetworkPolicy != null)
          {
            if (localNetworkPolicy.limitBytes <= 0L) {
              break label484;
            }
            l1 = localNetworkPolicy.limitBytes;
            paramNetworkTemplate.limitLevel = l1;
            if (localNetworkPolicy.warningBytes <= 0L) {
              break label489;
            }
            l1 = localNetworkPolicy.warningBytes;
            paramNetworkTemplate.warningLevel = l1;
            if ((paramNetworkTemplate == null) || (this.mNetworkController == null)) {
              break label482;
            }
            paramNetworkTemplate.carrier = this.mNetworkController.getMobileDataNetworkName();
            return paramNetworkTemplate;
          }
          paramNetworkTemplate.warningLevel = getDefaultWarningLevel();
          continue;
        }
        long l2 = l3;
      }
      catch (RemoteException paramNetworkTemplate)
      {
        return warn("remote call failed");
      }
      catch (NullPointerException paramNetworkTemplate)
      {
        return warn("NullPointerException");
      }
      long l1 = l3 - 2419200000L;
      continue;
      label482:
      return paramNetworkTemplate;
      label484:
      l1 = 0L;
      continue;
      label489:
      l1 = 0L;
    }
  }
  
  public long getDefaultWarningLevel()
  {
    return this.mContext.getResources().getInteger(R.integer.default_data_warning_level_mb) * 1048576L;
  }
  
  public DataUsageInfo getWifiDataUsageInfo()
  {
    return getDataUsageInfo(NetworkTemplate.buildTemplateWifiWildcard());
  }
  
  public boolean isMobileDataEnabled()
  {
    return this.mTelephonyManager.getDataEnabled();
  }
  
  public boolean isMobileDataSupported()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mConnectivityManager.isNetworkSupported(0))
    {
      bool1 = bool2;
      if (this.mTelephonyManager.getSimState() == 5) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public void setMobileDataEnabled(boolean paramBoolean)
  {
    Log.d("DataUsageController", "setMobileDataEnabled: enabled=" + paramBoolean);
    int[] arrayOfInt = this.mSubscriptionManager.getActiveSubscriptionIdList();
    if (arrayOfInt.length == 0)
    {
      this.mTelephonyManager.setDataEnabled(paramBoolean);
      if (this.mCallback != null) {
        this.mCallback.onMobileDataEnabled(paramBoolean);
      }
      return;
    }
    int i = 0;
    label67:
    if (i < arrayOfInt.length)
    {
      if (arrayOfInt[i] < 0) {
        break label97;
      }
      this.mTelephonyManager.setDataEnabled(arrayOfInt[i], paramBoolean);
    }
    for (;;)
    {
      i += 1;
      break label67;
      break;
      label97:
      Log.d("DataUsageController", "setMobileDataEnabled: negative subId[i]=" + arrayOfInt[i]);
    }
  }
  
  public void setNetworkController(NetworkNameProvider paramNetworkNameProvider)
  {
    this.mNetworkController = paramNetworkNameProvider;
  }
  
  public static abstract interface Callback
  {
    public abstract void onMobileDataEnabled(boolean paramBoolean);
  }
  
  public static class DataUsageInfo
  {
    public String carrier;
    public long limitLevel;
    public String period;
    public long startDate;
    public long usageLevel;
    public long warningLevel;
  }
  
  public static abstract interface NetworkNameProvider
  {
    public abstract String getMobileDataNetworkName();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\net\DataUsageController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */