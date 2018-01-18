package com.google.analytics.tracking.android;

import com.google.android.gms.common.util.VisibleForTesting;

public class Log
{
  private static GoogleAnalytics sGaInstance;
  
  @VisibleForTesting
  static void clearGaInstance()
  {
    sGaInstance = null;
  }
  
  public static void e(Exception paramException)
  {
    Logger localLogger = getLogger();
    if (localLogger == null) {
      return;
    }
    localLogger.error(paramException);
  }
  
  public static void e(String paramString)
  {
    Logger localLogger = getLogger();
    if (localLogger == null) {
      return;
    }
    localLogger.error(paramString);
  }
  
  private static Logger getLogger()
  {
    if (sGaInstance != null) {}
    while (sGaInstance == null)
    {
      return null;
      sGaInstance = GoogleAnalytics.getInstance();
    }
    return sGaInstance.getLogger();
  }
  
  public static void i(String paramString)
  {
    Logger localLogger = getLogger();
    if (localLogger == null) {
      return;
    }
    localLogger.info(paramString);
  }
  
  public static boolean isVerbose()
  {
    if (getLogger() == null) {
      return false;
    }
    return Logger.LogLevel.VERBOSE.equals(getLogger().getLogLevel());
  }
  
  public static void v(String paramString)
  {
    Logger localLogger = getLogger();
    if (localLogger == null) {
      return;
    }
    localLogger.verbose(paramString);
  }
  
  public static void w(String paramString)
  {
    Logger localLogger = getLogger();
    if (localLogger == null) {
      return;
    }
    localLogger.warn(paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\Log.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */