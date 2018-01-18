package com.android.settings.datausage;

import android.app.AppGlobals;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings.System;
import android.util.Log;
import com.google.android.collect.Maps;
import com.oneplus.settings.utils.OPSNSUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OPDataUsageUtils
{
  public static final int ERROR_CODE_EXCEPTION = 2;
  public static final int ERROR_CODE_INVILIDSIMCARD = 1;
  public static final int ERROR_CODE_NO = 0;
  public static final String KEY_ACCOUNT_DAY_SIM = "key_account_day_slot_";
  private static final String KEY_DATAUSAGE_ALERT_NUMBER_SIM = "key_datausage_alert_number_sim_";
  private static final String KEY_DATAUSAGE_WARN_STATE = "key_ten_percent_low_remaining_state_sim_";
  public static final String METHOD_QUERY_ONEPLUS_DATAUSAGE = "method_query_oneplus_datausage";
  public static final String METHOD_QUERY_ONEPLUS_DATAUSAGE_REGION = "method_query_oneplus_datausage_region";
  public static final String ONEPLUS_DATAUSAGE_ACCOUNTDAY = "oneplus_datausage_accountday";
  public static final String ONEPLUS_DATAUSAGE_ERROR_CODE = "oneplus_datausage_error_code";
  public static final String ONEPLUS_DATAUSAGE_SLOTID = "oneplus_datausage_slotid";
  public static final String ONEPLUS_DATAUSAGE_TIME_END = "oneplus_datausage_time_end";
  public static final String ONEPLUS_DATAUSAGE_TIME_START = "oneplus_datausage_time_start";
  public static final String ONEPLUS_DATAUSAGE_TOTAL = "oneplus_datausage_total";
  public static final String ONEPLUS_DATAUSAGE_USED = "oneplus_datausage_used";
  public static final String ONEPLUS_DATAUSAGE_WARN_STATE = "oneplus_datausage_warn_state";
  public static final String ONEPLUS_DATAUSAGE_WARN_VALUE = "oneplus_datausage_warn_value";
  public static final String ONEPLUS_SECURITY_URI = "content://com.oneplus.security.database.SafeProvider";
  
  public static final int getAccountDay(Context paramContext, int paramInt)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "key_account_day_slot_" + paramInt, 0);
  }
  
  public static List<ApplicationInfo> getApplicationInfoByUid(Context paramContext, int paramInt)
  {
    localArrayList = new ArrayList();
    paramContext = paramContext.getPackageManager().getPackagesForUid(paramInt);
    int i;
    if (paramContext != null) {
      i = paramContext.length;
    }
    try
    {
      for (;;)
      {
        int j = UserHandle.getUserId(paramInt);
        IPackageManager localIPackageManager = AppGlobals.getPackageManager();
        paramInt = 0;
        while (paramInt < i)
        {
          ApplicationInfo localApplicationInfo = localIPackageManager.getApplicationInfo(paramContext[paramInt], 0, j);
          if (localApplicationInfo != null) {
            localArrayList.add(localApplicationInfo);
          }
          paramInt += 1;
        }
        i = 0;
      }
      return localArrayList;
    }
    catch (Exception paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  public static long[] getDataUsageSectionTimeMillByAccountDay(Context paramContext, int paramInt)
  {
    if (paramInt != -1) {
      return getOneplusDataUsageRegion(paramContext, OPSNSUtils.findSlotIdBySubId(paramInt));
    }
    return getOneplusDataUsageRegion(paramContext, -1);
  }
  
  public static final long getDataWarnBytes(Context paramContext, int paramInt)
  {
    return Settings.System.getLong(paramContext.getContentResolver(), "key_datausage_alert_number_sim_" + paramInt, 0L);
  }
  
  public static final int getDataWarnState(Context paramContext, int paramInt)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "key_ten_percent_low_remaining_state_sim_" + paramInt, 0);
  }
  
  public static Map<String, Object> getOneplusDataUsage(Context paramContext, int paramInt)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("oneplus_datausage_slotid", paramInt);
    try
    {
      paramContext = paramContext.getContentResolver().call(Uri.parse("content://com.oneplus.security.database.SafeProvider"), "method_query_oneplus_datausage", null, localBundle);
      if (paramContext != null)
      {
        paramInt = paramContext.getInt("oneplus_datausage_error_code");
        int i = paramContext.getInt("oneplus_datausage_accountday");
        long l1 = paramContext.getLong("oneplus_datausage_time_start");
        long l2 = paramContext.getLong("oneplus_datausage_time_end");
        long l3 = paramContext.getLong("oneplus_datausage_total");
        long l4 = paramContext.getLong("oneplus_datausage_used");
        boolean bool = paramContext.getBoolean("oneplus_datausage_warn_state");
        long l5 = paramContext.getLong("oneplus_datausage_warn_value");
        paramContext = Maps.newHashMap();
        paramContext.put("oneplus_datausage_error_code", Integer.valueOf(paramInt));
        paramContext.put("oneplus_datausage_accountday", Integer.valueOf(i));
        paramContext.put("oneplus_datausage_total", Long.valueOf(l3));
        paramContext.put("oneplus_datausage_used", Long.valueOf(l4));
        paramContext.put("oneplus_datausage_time_start", Long.valueOf(l1));
        paramContext.put("oneplus_datausage_time_end", Long.valueOf(l2));
        paramContext.put("oneplus_datausage_warn_state", Boolean.valueOf(bool));
        paramContext.put("oneplus_datausage_warn_value", Long.valueOf(l5));
        return paramContext;
      }
    }
    catch (Exception paramContext)
    {
      Log.e("OPDataUsageUtils", "getOneplusDataUsage error");
      paramContext.printStackTrace();
    }
    return null;
  }
  
  public static long[] getOneplusDataUsageRegion(Context paramContext, int paramInt)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("oneplus_datausage_slotid", paramInt);
    try
    {
      paramContext = paramContext.getContentResolver().call(Uri.parse("content://com.oneplus.security.database.SafeProvider"), "method_query_oneplus_datausage_region", null, localBundle);
      if ((paramContext != null) && (paramContext.getInt("oneplus_datausage_error_code") != 2))
      {
        long l1 = paramContext.getLong("oneplus_datausage_time_start");
        long l2 = paramContext.getLong("oneplus_datausage_time_end");
        return new long[] { l1, l2 };
      }
    }
    catch (Exception paramContext)
    {
      Log.e("OPDataUsageUtils", "getOneplusDataUsage error");
      paramContext.printStackTrace();
    }
    return tmp97_93;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\OPDataUsageUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */