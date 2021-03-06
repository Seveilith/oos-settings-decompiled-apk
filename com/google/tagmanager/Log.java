package com.google.tagmanager;

import com.google.android.gms.common.util.VisibleForTesting;

final class Log
{
  @VisibleForTesting
  static Logger sLogger = new DefaultLogger();
  
  public static void d(String paramString)
  {
    sLogger.d(paramString);
  }
  
  public static void d(String paramString, Throwable paramThrowable)
  {
    sLogger.d(paramString, paramThrowable);
  }
  
  public static void e(String paramString)
  {
    sLogger.e(paramString);
  }
  
  public static void e(String paramString, Throwable paramThrowable)
  {
    sLogger.e(paramString, paramThrowable);
  }
  
  public static Logger.LogLevel getLogLevel()
  {
    return sLogger.getLogLevel();
  }
  
  public static Logger getLogger()
  {
    if (sLogger.getClass() != NoOpLogger.class) {
      return sLogger;
    }
    return null;
  }
  
  public static void i(String paramString)
  {
    sLogger.i(paramString);
  }
  
  public static void i(String paramString, Throwable paramThrowable)
  {
    sLogger.i(paramString, paramThrowable);
  }
  
  public static void setLogger(Logger paramLogger)
  {
    if (paramLogger != null)
    {
      sLogger = paramLogger;
      return;
    }
    sLogger = new NoOpLogger();
  }
  
  public static void v(String paramString)
  {
    sLogger.v(paramString);
  }
  
  public static void v(String paramString, Throwable paramThrowable)
  {
    sLogger.v(paramString, paramThrowable);
  }
  
  public static void w(String paramString)
  {
    sLogger.w(paramString);
  }
  
  public static void w(String paramString, Throwable paramThrowable)
  {
    sLogger.w(paramString, paramThrowable);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\Log.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */