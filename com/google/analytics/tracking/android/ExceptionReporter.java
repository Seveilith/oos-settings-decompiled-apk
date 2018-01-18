package com.google.analytics.tracking.android;

import android.content.Context;
import java.util.ArrayList;

public class ExceptionReporter
  implements Thread.UncaughtExceptionHandler
{
  static final String DEFAULT_DESCRIPTION = "UncaughtException";
  private ExceptionParser mExceptionParser;
  private final Thread.UncaughtExceptionHandler mOriginalHandler;
  private final ServiceManager mServiceManager;
  private final Tracker mTracker;
  
  public ExceptionReporter(Tracker paramTracker, ServiceManager paramServiceManager, Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler, Context paramContext)
  {
    if (paramTracker != null)
    {
      if (paramServiceManager == null) {
        break label94;
      }
      this.mOriginalHandler = paramUncaughtExceptionHandler;
      this.mTracker = paramTracker;
      this.mServiceManager = paramServiceManager;
      this.mExceptionParser = new StandardExceptionParser(paramContext, new ArrayList());
      paramServiceManager = new StringBuilder().append("ExceptionReporter created, original handler is ");
      if (paramUncaughtExceptionHandler == null) {
        break label104;
      }
    }
    label94:
    label104:
    for (paramTracker = paramUncaughtExceptionHandler.getClass().getName();; paramTracker = "null")
    {
      Log.v(paramTracker);
      return;
      throw new NullPointerException("tracker cannot be null");
      throw new NullPointerException("serviceManager cannot be null");
    }
  }
  
  public ExceptionParser getExceptionParser()
  {
    return this.mExceptionParser;
  }
  
  public void setExceptionParser(ExceptionParser paramExceptionParser)
  {
    this.mExceptionParser = paramExceptionParser;
  }
  
  public void uncaughtException(Thread paramThread, Throwable paramThrowable)
  {
    String str = null;
    if (this.mExceptionParser == null)
    {
      str = "UncaughtException";
      Log.v("Tracking Exception: " + str);
      this.mTracker.send(MapBuilder.createException(str, Boolean.valueOf(true)).build());
      this.mServiceManager.dispatchLocalHits();
      if (this.mOriginalHandler != null) {}
    }
    else
    {
      if (paramThread == null) {}
      for (;;)
      {
        str = this.mExceptionParser.getDescription(str, paramThrowable);
        break;
        str = paramThread.getName();
      }
    }
    Log.v("Passing exception to original handler.");
    this.mOriginalHandler.uncaughtException(paramThread, paramThrowable);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\ExceptionReporter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */