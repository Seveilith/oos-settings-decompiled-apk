package com.oneplus.settings.timer.timepower;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.System;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class SettingsUtil
{
  public static final String ACTION_PHONE_STATE = "android.intent.action.PHONE_STATE";
  public static final String ACTION_POWER_CANCEL_OP_OFF = "com.android.settings.POWER_CANCEL_OP_OFF";
  public static final String ACTION_POWER_CONFIRM_OP_OFF = "com.android.settings.POWER_CONFIRM_OP_OFF";
  public static final String ACTION_POWER_OFF = "com.android.settings.POWER_OFF";
  public static final String ACTION_POWER_ON = "com.android.settings.POWER_ON";
  public static final String ACTION_POWER_OP_OFF = "com.android.settings.POWER_OP_OFF";
  public static final String ACTION_POWER_OP_ON = "com.android.settings.POWER_OP_ON";
  public static final String ACTION_SET_CHANGED = "com.android.settings.SET_CHANGED";
  public static final long MILLIS_OF_DAY = 86400000L;
  public static final int REQUEST_CODE_OFF = 1;
  public static final int REQUEST_CODE_ON = 0;
  private static final int RTC_POWERUP_MTK = 7;
  private static final int RTC_POWERUP_QCOM = 5;
  public static final String TAG = "SettingsUtil";
  public static final int TIMEOUT_MILLIS = 60000;
  private static final int TIME_POWER_COUNTS = 2;
  public static final String TRIGGER_TIME = "trigger_time";
  private static long mCurrentTime;
  
  public static void cancelAlarm(Context paramContext, Intent paramIntent, int paramInt)
  {
    ((AlarmManager)paramContext.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(paramContext, paramInt, paramIntent, 134217728));
  }
  
  public static boolean checkSwitch(Context paramContext, boolean paramBoolean)
  {
    paramContext = Settings.System.getString(paramContext.getContentResolver(), "def_timepower_config");
    if (paramContext == null) {
      return false;
    }
    int[][] arrayOfInt = (int[][])Array.newInstance(Integer.TYPE, new int[] { 2, 2 });
    boolean[][] arrayOfBoolean = (boolean[][])Array.newInstance(Boolean.TYPE, new int[] { 2, 2 });
    int j = 0;
    int i = 0;
    while (j <= 6)
    {
      String str = paramContext.substring(j, j + 6);
      arrayOfInt[i][0] = Integer.parseInt(str.substring(0, 2));
      arrayOfInt[i][1] = Integer.parseInt(str.substring(2, 4));
      arrayOfBoolean[i][0] = intToBool(Integer.parseInt(str.substring(4, 5)));
      arrayOfBoolean[i][1] = intToBool(Integer.parseInt(str.substring(5, 6)));
      j += 6;
      i += 1;
    }
    if (paramBoolean)
    {
      if (arrayOfBoolean[0][1] != 0) {
        return true;
      }
    }
    else if (arrayOfBoolean[1][1] != 0) {
      return true;
    }
    return false;
  }
  
  public static long[] getNearestTime(String paramString)
  {
    mCurrentTime = System.currentTimeMillis();
    long[] arrayOfLong = new long[2];
    long[] tmp11_10 = arrayOfLong;
    tmp11_10[0] = 0L;
    long[] tmp15_11 = tmp11_10;
    tmp15_11[1] = 0L;
    tmp15_11;
    if (paramString == null) {
      return arrayOfLong;
    }
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    int i = 1;
    if (i <= 2)
    {
      if (1 == i) {
        localArrayList1.add(Long.valueOf(getUTC(Integer.parseInt(paramString.substring(i * 6 - 6, i * 6 - 4)), Integer.parseInt(paramString.substring(i * 6 - 4, i * 6 - 2)))));
      }
      for (;;)
      {
        i += 1;
        break;
        if (2 == i) {
          localArrayList2.add(Long.valueOf(getUTC(Integer.parseInt(paramString.substring(i * 6 - 6, i * 6 - 4)), Integer.parseInt(paramString.substring(i * 6 - 4, i * 6 - 2)))));
        }
      }
    }
    if (localArrayList1.size() != 0)
    {
      localArrayList1.add(Long.valueOf(mCurrentTime));
      Collections.sort(localArrayList1);
      if (((Long)localArrayList1.get(localArrayList1.size() - 1)).longValue() != mCurrentTime) {
        break label305;
      }
      arrayOfLong[0] = (((Long)localArrayList1.get(0)).longValue() + 86400000L);
    }
    for (;;)
    {
      if (localArrayList2.size() != 0)
      {
        localArrayList2.add(Long.valueOf(mCurrentTime));
        Collections.sort(localArrayList2);
        if (((Long)localArrayList2.get(localArrayList2.size() - 1)).longValue() != mCurrentTime) {
          break;
        }
        arrayOfLong[1] = (((Long)localArrayList2.get(0)).longValue() + 86400000L);
      }
      return arrayOfLong;
      label305:
      arrayOfLong[0] = ((Long)localArrayList1.get(localArrayList1.lastIndexOf(Long.valueOf(mCurrentTime)) + 1)).longValue();
    }
    arrayOfLong[1] = ((Long)localArrayList2.get(localArrayList2.lastIndexOf(Long.valueOf(mCurrentTime)) + 1)).longValue();
    return arrayOfLong;
  }
  
  private static long getUTC(int paramInt1, int paramInt2)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(mCurrentTime);
    localCalendar.set(11, paramInt1);
    localCalendar.set(12, paramInt2);
    localCalendar.set(13, 0);
    localCalendar.set(14, 0);
    return localCalendar.getTimeInMillis();
  }
  
  static boolean intToBool(int paramInt)
  {
    boolean bool = false;
    if (paramInt != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isPowerOffEnable(Context paramContext)
  {
    return checkSwitch(paramContext, false);
  }
  
  public static boolean isPowerOnEnable(Context paramContext)
  {
    return checkSwitch(paramContext, true);
  }
  
  public static void setAlarm(Context paramContext, Intent paramIntent, long paramLong, int paramInt)
  {
    AlarmManager localAlarmManager = (AlarmManager)paramContext.getSystemService("alarm");
    paramContext = PendingIntent.getBroadcast(paramContext, paramInt, paramIntent, 134217728);
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      localAlarmManager.setExact(5, paramLong, paramContext);
      return;
    }
    localAlarmManager.setExact(0, paramLong, paramContext);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\timer\timepower\SettingsUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */