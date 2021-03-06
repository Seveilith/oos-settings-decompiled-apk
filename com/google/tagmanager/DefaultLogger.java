package com.google.tagmanager;

import android.util.Log;

class DefaultLogger
  implements Logger
{
  private static final String LOG_TAG = "GoogleTagManager";
  private Logger.LogLevel mLogLevel = Logger.LogLevel.WARNING;
  
  public void d(String paramString)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.DEBUG.ordinal()) {
      return;
    }
    Log.d("GoogleTagManager", paramString);
  }
  
  public void d(String paramString, Throwable paramThrowable)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.DEBUG.ordinal()) {
      return;
    }
    Log.d("GoogleTagManager", paramString, paramThrowable);
  }
  
  public void e(String paramString)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.ERROR.ordinal()) {
      return;
    }
    Log.e("GoogleTagManager", paramString);
  }
  
  public void e(String paramString, Throwable paramThrowable)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.ERROR.ordinal()) {
      return;
    }
    Log.e("GoogleTagManager", paramString, paramThrowable);
  }
  
  public Logger.LogLevel getLogLevel()
  {
    return this.mLogLevel;
  }
  
  public void i(String paramString)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.INFO.ordinal()) {
      return;
    }
    Log.i("GoogleTagManager", paramString);
  }
  
  public void i(String paramString, Throwable paramThrowable)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.INFO.ordinal()) {
      return;
    }
    Log.i("GoogleTagManager", paramString, paramThrowable);
  }
  
  public void setLogLevel(Logger.LogLevel paramLogLevel)
  {
    this.mLogLevel = paramLogLevel;
  }
  
  public void v(String paramString)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.VERBOSE.ordinal()) {
      return;
    }
    Log.v("GoogleTagManager", paramString);
  }
  
  public void v(String paramString, Throwable paramThrowable)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.VERBOSE.ordinal()) {
      return;
    }
    Log.v("GoogleTagManager", paramString, paramThrowable);
  }
  
  public void w(String paramString)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.WARNING.ordinal()) {
      return;
    }
    Log.w("GoogleTagManager", paramString);
  }
  
  public void w(String paramString, Throwable paramThrowable)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.WARNING.ordinal()) {
      return;
    }
    Log.w("GoogleTagManager", paramString, paramThrowable);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\DefaultLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */