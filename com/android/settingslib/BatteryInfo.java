package com.android.settingslib;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.BatteryStats;
import android.os.BatteryStats.HistoryItem;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.SparseIntArray;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settingslib.graph.UsageView;

public class BatteryInfo
{
  public String batteryPercentString;
  public int mBatteryLevel;
  public String mChargeLabelString;
  private boolean mCharging;
  public boolean mDischarging = true;
  private BatteryStats mStats;
  public String remainingLabel;
  public long remainingTimeUs = 0L;
  private long timePeriod;
  
  public static BatteryInfo getBatteryInfo(Context paramContext, Intent paramIntent, BatteryStats paramBatteryStats, long paramLong)
  {
    return getBatteryInfo(paramContext, paramIntent, paramBatteryStats, paramLong, false);
  }
  
  public static BatteryInfo getBatteryInfo(Context paramContext, Intent paramIntent, BatteryStats paramBatteryStats, long paramLong, boolean paramBoolean)
  {
    BatteryInfo localBatteryInfo = new BatteryInfo();
    localBatteryInfo.mStats = paramBatteryStats;
    localBatteryInfo.mBatteryLevel = Utils.getBatteryLevel(paramIntent);
    localBatteryInfo.batteryPercentString = Utils.formatPercentage(localBatteryInfo.mBatteryLevel);
    boolean bool;
    Resources localResources;
    if (paramIntent.getIntExtra("plugged", 0) != 0)
    {
      bool = true;
      localBatteryInfo.mCharging = bool;
      localResources = paramContext.getResources();
      if (localBatteryInfo.mCharging) {
        break label211;
      }
      paramLong = paramBatteryStats.computeBatteryTimeRemaining(paramLong);
      if (paramLong <= 0L) {
        break label192;
      }
      localBatteryInfo.remainingTimeUs = paramLong;
      paramContext = Formatter.formatShortElapsedTime(paramContext, paramLong / 1000L);
      if (!paramBoolean) {
        break label176;
      }
      i = R.string.power_remaining_duration_only_short;
      label109:
      localBatteryInfo.remainingLabel = localResources.getString(i, new Object[] { paramContext });
      if (!paramBoolean) {
        break label184;
      }
    }
    label176:
    label184:
    for (int i = R.string.power_discharging_duration_short;; i = R.string.power_discharging_duration)
    {
      localBatteryInfo.mChargeLabelString = localResources.getString(i, new Object[] { localBatteryInfo.batteryPercentString, paramContext });
      return localBatteryInfo;
      bool = false;
      break;
      i = R.string.power_remaining_duration_only;
      break label109;
    }
    label192:
    localBatteryInfo.remainingLabel = null;
    localBatteryInfo.mChargeLabelString = localBatteryInfo.batteryPercentString;
    return localBatteryInfo;
    label211:
    paramLong = paramBatteryStats.computeChargeTimeRemaining(paramLong);
    paramBatteryStats = Utils.getBatteryStatus(localResources, paramIntent, paramBoolean);
    i = paramIntent.getIntExtra("status", 1);
    if ((paramLong > 0L) && (i != 5))
    {
      localBatteryInfo.mDischarging = false;
      localBatteryInfo.remainingTimeUs = paramLong;
      paramContext = Formatter.formatShortElapsedTime(paramContext, paramLong / 1000L);
      i = paramIntent.getIntExtra("plugged", 0);
      if (i == 1) {
        if (paramBoolean) {
          i = R.string.power_charging_duration_ac_short;
        }
      }
      for (;;)
      {
        localBatteryInfo.remainingLabel = localResources.getString(R.string.power_remaining_duration_only, new Object[] { paramContext });
        localBatteryInfo.mChargeLabelString = localResources.getString(i, new Object[] { localBatteryInfo.batteryPercentString, paramContext });
        return localBatteryInfo;
        i = R.string.power_charging_duration_ac;
        continue;
        if (i == 2)
        {
          if (paramBoolean) {
            i = R.string.power_charging_duration_usb_short;
          } else {
            i = R.string.power_charging_duration_usb;
          }
        }
        else if (i == 4)
        {
          if (paramBoolean) {
            i = R.string.power_charging_duration_wireless_short;
          } else {
            i = R.string.power_charging_duration_wireless;
          }
        }
        else if (paramBoolean) {
          i = R.string.power_charging_duration_short;
        } else {
          i = R.string.power_charging_duration;
        }
      }
    }
    localBatteryInfo.remainingLabel = paramBatteryStats;
    localBatteryInfo.mChargeLabelString = localResources.getString(R.string.power_charging, new Object[] { localBatteryInfo.batteryPercentString, paramBatteryStats });
    return localBatteryInfo;
  }
  
  public static void getBatteryInfo(Context paramContext, Callback paramCallback)
  {
    getBatteryInfo(paramContext, paramCallback, false);
  }
  
  public static void getBatteryInfo(Context paramContext, final Callback paramCallback, final boolean paramBoolean)
  {
    new AsyncTask()
    {
      protected BatteryStats doInBackground(Void... paramAnonymousVarArgs)
      {
        paramAnonymousVarArgs = new BatteryStatsHelper(this.val$context, true);
        paramAnonymousVarArgs.create((Bundle)null);
        return paramAnonymousVarArgs.getStats();
      }
      
      protected void onPostExecute(BatteryStats paramAnonymousBatteryStats)
      {
        long l = SystemClock.elapsedRealtime();
        Intent localIntent = this.val$context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        paramAnonymousBatteryStats = BatteryInfo.getBatteryInfo(this.val$context, localIntent, paramAnonymousBatteryStats, l * 1000L, paramBoolean);
        paramCallback.onBatteryInfoLoaded(paramAnonymousBatteryStats);
      }
    }.execute(new Void[0]);
  }
  
  private static void parse(BatteryStats paramBatteryStats, long paramLong, BatteryDataParser... paramVarArgs)
  {
    long l1 = 0L;
    long l6 = 0L;
    long l2 = 0L;
    int k = -1;
    long l11 = 0L;
    long l3 = 0L;
    long l4 = 0L;
    int j = 0;
    int i = 0;
    int m = 0;
    int n = 1;
    long l9 = l2;
    long l7 = l6;
    long l5 = l4;
    long l10 = l3;
    long l8 = l1;
    BatteryStats.HistoryItem localHistoryItem;
    if (paramBatteryStats.startIteratingHistoryLocked())
    {
      localHistoryItem = new BatteryStats.HistoryItem();
      for (;;)
      {
        l9 = l2;
        l7 = l6;
        j = i;
        l5 = l4;
        l10 = l3;
        l8 = l1;
        if (!paramBatteryStats.getNextHistoryLocked(localHistoryItem)) {
          break;
        }
        j = m + 1;
        int i1 = n;
        l7 = l6;
        if (n != 0)
        {
          i1 = 0;
          l7 = localHistoryItem.time;
        }
        if (localHistoryItem.cmd != 5)
        {
          l8 = l4;
          l9 = l3;
          l5 = l1;
          if (localHistoryItem.cmd != 7) {}
        }
        else
        {
          if ((localHistoryItem.currentTime > 15552000000L + l3) || (localHistoryItem.time < 300000L + l7)) {
            l1 = 0L;
          }
          l3 = localHistoryItem.currentTime;
          l4 = localHistoryItem.time;
          l8 = l4;
          l9 = l3;
          l5 = l1;
          if (l1 == 0L)
          {
            l5 = l3 - (l4 - l7);
            l9 = l3;
            l8 = l4;
          }
        }
        n = i1;
        l6 = l7;
        l4 = l8;
        l3 = l9;
        m = j;
        l1 = l5;
        if (localHistoryItem.isDeltaData())
        {
          if ((localHistoryItem.batteryLevel != k) || (j == 1)) {
            k = localHistoryItem.batteryLevel;
          }
          i = j;
          l2 = localHistoryItem.time;
          n = i1;
          l6 = l7;
          l4 = l8;
          l3 = l9;
          m = j;
          l1 = l5;
        }
      }
    }
    paramBatteryStats.finishIteratingHistoryLocked();
    l1 = l10 + l9 - l5;
    paramLong /= 1000L;
    k = 0;
    i = 0;
    while (i < paramVarArgs.length)
    {
      paramVarArgs[i].onParsingStarted(l8, l1 + paramLong);
      i += 1;
    }
    if ((l1 > l8) && (paramBatteryStats.startIteratingHistoryLocked()))
    {
      localHistoryItem = new BatteryStats.HistoryItem();
      l2 = l5;
      i = k;
      for (paramLong = l11; (paramBatteryStats.getNextHistoryLocked(localHistoryItem)) && (i < j); paramLong = l1)
      {
        if (localHistoryItem.isDeltaData())
        {
          l3 = paramLong + (localHistoryItem.time - l2);
          l4 = localHistoryItem.time;
          l1 = l3 - l8;
          paramLong = l1;
          if (l1 < 0L) {
            paramLong = 0L;
          }
          k = 0;
          for (;;)
          {
            l1 = l3;
            l2 = l4;
            if (k >= paramVarArgs.length) {
              break;
            }
            paramVarArgs[k].onDataPoint(paramLong, localHistoryItem);
            k += 1;
          }
        }
        if (localHistoryItem.cmd != 5)
        {
          l1 = paramLong;
          l3 = l2;
          if (localHistoryItem.cmd != 7) {}
        }
        else
        {
          if (localHistoryItem.currentTime < l8) {
            break label672;
          }
        }
        label672:
        for (l1 = localHistoryItem.currentTime;; l1 = l8 + (localHistoryItem.time - l7))
        {
          l3 = localHistoryItem.time;
          l4 = l1;
          l1 = l4;
          l2 = l3;
          if (localHistoryItem.cmd == 6) {
            break;
          }
          if (localHistoryItem.cmd == 5)
          {
            l1 = l4;
            l2 = l3;
            if (Math.abs(paramLong - l4) <= 3600000L) {
              break;
            }
          }
          k = 0;
          for (;;)
          {
            l1 = l4;
            l2 = l3;
            if (k >= paramVarArgs.length) {
              break;
            }
            paramVarArgs[k].onDataGap();
            k += 1;
          }
        }
        i += 1;
      }
    }
    paramBatteryStats.finishIteratingHistoryLocked();
    i = 0;
    while (i < paramVarArgs.length)
    {
      paramVarArgs[i].onParsingDone();
      i += 1;
    }
  }
  
  public void bindHistory(final UsageView paramUsageView, BatteryDataParser... paramVarArgs)
  {
    Object localObject1 = new BatteryDataParser()
    {
      SparseIntArray points = new SparseIntArray();
      
      public void onDataGap()
      {
        if (this.points.size() > 1) {
          paramUsageView.addPath(this.points);
        }
        this.points.clear();
      }
      
      public void onDataPoint(long paramAnonymousLong, BatteryStats.HistoryItem paramAnonymousHistoryItem)
      {
        this.points.put((int)paramAnonymousLong, paramAnonymousHistoryItem.batteryLevel);
      }
      
      public void onParsingDone()
      {
        if (this.points.size() > 1) {
          paramUsageView.addPath(this.points);
        }
      }
      
      public void onParsingStarted(long paramAnonymousLong1, long paramAnonymousLong2)
      {
        BatteryInfo.-set0(BatteryInfo.this, paramAnonymousLong2 - paramAnonymousLong1 - BatteryInfo.this.remainingTimeUs / 1000L);
        paramUsageView.clearPaths();
        UsageView localUsageView = paramUsageView;
        int i = (int)(paramAnonymousLong2 - paramAnonymousLong1);
        if (BatteryInfo.this.remainingTimeUs != 0L) {}
        for (boolean bool = true;; bool = false)
        {
          localUsageView.configureGraph(i, 100, bool, BatteryInfo.-get0(BatteryInfo.this));
          return;
        }
      }
    };
    Object localObject2 = new BatteryDataParser[paramVarArgs.length + 1];
    int i = 0;
    while (i < paramVarArgs.length)
    {
      localObject2[i] = paramVarArgs[i];
      i += 1;
    }
    localObject2[paramVarArgs.length] = localObject1;
    parse(this.mStats, this.remainingTimeUs, (BatteryDataParser[])localObject2);
    localObject2 = paramUsageView.getContext();
    localObject1 = ((Context)localObject2).getString(R.string.charge_length_format, new Object[] { Formatter.formatShortElapsedTime((Context)localObject2, Math.abs(this.timePeriod)) });
    paramVarArgs = "";
    if (this.remainingTimeUs != 0L) {
      paramVarArgs = ((Context)localObject2).getString(R.string.remaining_length_format, new Object[] { Formatter.formatShortElapsedTime((Context)localObject2, this.remainingTimeUs / 1000L) });
    }
    paramUsageView.setBottomLabels(new CharSequence[] { localObject1, paramVarArgs });
  }
  
  public static abstract interface BatteryDataParser
  {
    public abstract void onDataGap();
    
    public abstract void onDataPoint(long paramLong, BatteryStats.HistoryItem paramHistoryItem);
    
    public abstract void onParsingDone();
    
    public abstract void onParsingStarted(long paramLong1, long paramLong2);
  }
  
  public static abstract interface Callback
  {
    public abstract void onBatteryInfoLoaded(BatteryInfo paramBatteryInfo);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\BatteryInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */