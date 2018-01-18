package com.google.tagmanager;

import android.content.Context;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.common.util.VisibleForTesting;

class TrackerProvider
{
  private Context mContext;
  private GoogleAnalytics mGoogleAnalytics;
  
  TrackerProvider(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  @VisibleForTesting
  TrackerProvider(GoogleAnalytics paramGoogleAnalytics)
  {
    this.mGoogleAnalytics = paramGoogleAnalytics;
    this.mGoogleAnalytics.setLogger(new LoggerImpl());
  }
  
  /* Error */
  private void initTrackProviderIfNecessary()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 25	com/google/tagmanager/TrackerProvider:mGoogleAnalytics	Lcom/google/analytics/tracking/android/GoogleAnalytics;
    //   6: astore_1
    //   7: aload_1
    //   8: ifnull +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: aload_0
    //   16: getfield 20	com/google/tagmanager/TrackerProvider:mContext	Landroid/content/Context;
    //   19: invokestatic 38	com/google/analytics/tracking/android/GoogleAnalytics:getInstance	(Landroid/content/Context;)Lcom/google/analytics/tracking/android/GoogleAnalytics;
    //   22: putfield 25	com/google/tagmanager/TrackerProvider:mGoogleAnalytics	Lcom/google/analytics/tracking/android/GoogleAnalytics;
    //   25: aload_0
    //   26: getfield 25	com/google/tagmanager/TrackerProvider:mGoogleAnalytics	Lcom/google/analytics/tracking/android/GoogleAnalytics;
    //   29: new 8	com/google/tagmanager/TrackerProvider$LoggerImpl
    //   32: dup
    //   33: invokespecial 26	com/google/tagmanager/TrackerProvider$LoggerImpl:<init>	()V
    //   36: invokevirtual 32	com/google/analytics/tracking/android/GoogleAnalytics:setLogger	(Lcom/google/analytics/tracking/android/Logger;)V
    //   39: goto -28 -> 11
    //   42: astore_1
    //   43: aload_0
    //   44: monitorexit
    //   45: aload_1
    //   46: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	47	0	this	TrackerProvider
    //   6	2	1	localGoogleAnalytics	GoogleAnalytics
    //   42	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	42	finally
    //   14	39	42	finally
  }
  
  public void close(Tracker paramTracker)
  {
    this.mGoogleAnalytics.closeTracker(paramTracker.getName());
  }
  
  public Tracker getTracker(String paramString)
  {
    initTrackProviderIfNecessary();
    return this.mGoogleAnalytics.getTracker(paramString);
  }
  
  static class LoggerImpl
    implements Logger
  {
    private static com.google.analytics.tracking.android.Logger.LogLevel toAnalyticsLogLevel(Logger.LogLevel paramLogLevel)
    {
      switch (TrackerProvider.1.$SwitchMap$com$google$tagmanager$Logger$LogLevel[paramLogLevel.ordinal()])
      {
      default: 
        return com.google.analytics.tracking.android.Logger.LogLevel.ERROR;
      case 1: 
      case 2: 
        return com.google.analytics.tracking.android.Logger.LogLevel.ERROR;
      case 3: 
        return com.google.analytics.tracking.android.Logger.LogLevel.WARNING;
      case 4: 
      case 5: 
        return com.google.analytics.tracking.android.Logger.LogLevel.INFO;
      }
      return com.google.analytics.tracking.android.Logger.LogLevel.VERBOSE;
    }
    
    public void error(Exception paramException)
    {
      Log.e("", paramException);
    }
    
    public void error(String paramString)
    {
      Log.e(paramString);
    }
    
    public com.google.analytics.tracking.android.Logger.LogLevel getLogLevel()
    {
      Logger.LogLevel localLogLevel = Log.getLogLevel();
      if (localLogLevel != null) {
        return toAnalyticsLogLevel(localLogLevel);
      }
      return com.google.analytics.tracking.android.Logger.LogLevel.ERROR;
    }
    
    public void info(String paramString)
    {
      Log.i(paramString);
    }
    
    public void setLogLevel(com.google.analytics.tracking.android.Logger.LogLevel paramLogLevel)
    {
      Log.w("GA uses GTM logger. Please use TagManager.getLogger().setLogLevel(LogLevel) instead.");
    }
    
    public void verbose(String paramString)
    {
      Log.v(paramString);
    }
    
    public void warn(String paramString)
    {
      Log.w(paramString);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\TrackerProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */