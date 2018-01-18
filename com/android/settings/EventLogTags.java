package com.android.settings;

import android.util.EventLog;

public class EventLogTags
{
  public static final int EXP_DET_DEVICE_ADMIN_ACTIVATED_BY_USER = 90201;
  public static final int EXP_DET_DEVICE_ADMIN_DECLINED_BY_USER = 90202;
  public static final int EXP_DET_DEVICE_ADMIN_UNINSTALLED_BY_USER = 90203;
  public static final int LOCK_SCREEN_TYPE = 90200;
  
  public static void writeExpDetDeviceAdminActivatedByUser(String paramString)
  {
    EventLog.writeEvent(90201, paramString);
  }
  
  public static void writeExpDetDeviceAdminDeclinedByUser(String paramString)
  {
    EventLog.writeEvent(90202, paramString);
  }
  
  public static void writeExpDetDeviceAdminUninstalledByUser(String paramString)
  {
    EventLog.writeEvent(90203, paramString);
  }
  
  public static void writeLockScreenType(String paramString)
  {
    EventLog.writeEvent(90200, paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\EventLogTags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */