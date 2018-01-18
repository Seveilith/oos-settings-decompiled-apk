package com.google.analytics.tracking.android;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GoogleAnalytics
  extends TrackerHandler
{
  private static GoogleAnalytics sInstance;
  private volatile Boolean mAppOptOut = Boolean.valueOf(false);
  private Context mContext;
  private Tracker mDefaultTracker;
  private boolean mDryRun;
  private Logger mLogger;
  private AnalyticsThread mThread;
  private final Map<String, Tracker> mTrackers = new HashMap();
  
  @VisibleForTesting
  protected GoogleAnalytics(Context paramContext)
  {
    this(paramContext, GAThread.getInstance(paramContext));
  }
  
  private GoogleAnalytics(Context paramContext, AnalyticsThread paramAnalyticsThread)
  {
    if (paramContext != null)
    {
      this.mContext = paramContext.getApplicationContext();
      this.mThread = paramAnalyticsThread;
      AppFieldsDefaultProvider.initializeProvider(this.mContext);
      ScreenResolutionDefaultProvider.initializeProvider(this.mContext);
      ClientIdDefaultProvider.initializeProvider(this.mContext);
      this.mLogger = new DefaultLoggerImpl();
      return;
    }
    throw new IllegalArgumentException("context cannot be null");
  }
  
  @VisibleForTesting
  static void clearDefaultProviders()
  {
    AppFieldsDefaultProvider.dropInstance();
    ScreenResolutionDefaultProvider.dropInstance();
    ClientIdDefaultProvider.dropInstance();
  }
  
  @VisibleForTesting
  static void clearInstance()
  {
    try
    {
      sInstance = null;
      clearDefaultProviders();
      return;
    }
    finally {}
  }
  
  static GoogleAnalytics getInstance()
  {
    try
    {
      GoogleAnalytics localGoogleAnalytics = sInstance;
      return localGoogleAnalytics;
    }
    finally {}
  }
  
  /* Error */
  public static GoogleAnalytics getInstance(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 93	com/google/analytics/tracking/android/GoogleAnalytics:sInstance	Lcom/google/analytics/tracking/android/GoogleAnalytics;
    //   6: ifnull +12 -> 18
    //   9: getstatic 93	com/google/analytics/tracking/android/GoogleAnalytics:sInstance	Lcom/google/analytics/tracking/android/GoogleAnalytics;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: new 2	com/google/analytics/tracking/android/GoogleAnalytics
    //   21: dup
    //   22: aload_0
    //   23: invokespecial 99	com/google/analytics/tracking/android/GoogleAnalytics:<init>	(Landroid/content/Context;)V
    //   26: putstatic 93	com/google/analytics/tracking/android/GoogleAnalytics:sInstance	Lcom/google/analytics/tracking/android/GoogleAnalytics;
    //   29: goto -20 -> 9
    //   32: astore_0
    //   33: ldc 2
    //   35: monitorexit
    //   36: aload_0
    //   37: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	38	0	paramContext	Context
    // Exception table:
    //   from	to	target	type
    //   3	9	32	finally
    //   9	16	32	finally
    //   18	29	32	finally
    //   33	36	32	finally
  }
  
  /* Error */
  @VisibleForTesting
  static GoogleAnalytics getNewInstance(Context paramContext, AnalyticsThread paramAnalyticsThread)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 93	com/google/analytics/tracking/android/GoogleAnalytics:sInstance	Lcom/google/analytics/tracking/android/GoogleAnalytics;
    //   6: ifnonnull +24 -> 30
    //   9: new 2	com/google/analytics/tracking/android/GoogleAnalytics
    //   12: dup
    //   13: aload_0
    //   14: aload_1
    //   15: invokespecial 33	com/google/analytics/tracking/android/GoogleAnalytics:<init>	(Landroid/content/Context;Lcom/google/analytics/tracking/android/AnalyticsThread;)V
    //   18: putstatic 93	com/google/analytics/tracking/android/GoogleAnalytics:sInstance	Lcom/google/analytics/tracking/android/GoogleAnalytics;
    //   21: getstatic 93	com/google/analytics/tracking/android/GoogleAnalytics:sInstance	Lcom/google/analytics/tracking/android/GoogleAnalytics;
    //   24: astore_0
    //   25: ldc 2
    //   27: monitorexit
    //   28: aload_0
    //   29: areturn
    //   30: getstatic 93	com/google/analytics/tracking/android/GoogleAnalytics:sInstance	Lcom/google/analytics/tracking/android/GoogleAnalytics;
    //   33: invokevirtual 104	com/google/analytics/tracking/android/GoogleAnalytics:close	()V
    //   36: goto -27 -> 9
    //   39: astore_0
    //   40: ldc 2
    //   42: monitorexit
    //   43: aload_0
    //   44: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	45	0	paramContext	Context
    //   0	45	1	paramAnalyticsThread	AnalyticsThread
    // Exception table:
    //   from	to	target	type
    //   3	9	39	finally
    //   9	28	39	finally
    //   30	36	39	finally
    //   40	43	39	finally
  }
  
  @VisibleForTesting
  void close() {}
  
  /* Error */
  public void closeTracker(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: invokestatic 110	com/google/analytics/tracking/android/GAUsage:getInstance	()Lcom/google/analytics/tracking/android/GAUsage;
    //   5: getstatic 116	com/google/analytics/tracking/android/GAUsage$Field:CLOSE_TRACKER	Lcom/google/analytics/tracking/android/GAUsage$Field;
    //   8: invokevirtual 120	com/google/analytics/tracking/android/GAUsage:setUsage	(Lcom/google/analytics/tracking/android/GAUsage$Field;)V
    //   11: aload_0
    //   12: getfield 51	com/google/analytics/tracking/android/GoogleAnalytics:mTrackers	Ljava/util/Map;
    //   15: aload_1
    //   16: invokeinterface 126 2 0
    //   21: checkcast 128	com/google/analytics/tracking/android/Tracker
    //   24: aload_0
    //   25: getfield 130	com/google/analytics/tracking/android/GoogleAnalytics:mDefaultTracker	Lcom/google/analytics/tracking/android/Tracker;
    //   28: if_acmpeq +6 -> 34
    //   31: aload_0
    //   32: monitorexit
    //   33: return
    //   34: aload_0
    //   35: aconst_null
    //   36: putfield 130	com/google/analytics/tracking/android/GoogleAnalytics:mDefaultTracker	Lcom/google/analytics/tracking/android/Tracker;
    //   39: goto -8 -> 31
    //   42: astore_1
    //   43: aload_0
    //   44: monitorexit
    //   45: aload_1
    //   46: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	47	0	this	GoogleAnalytics
    //   0	47	1	paramString	String
    // Exception table:
    //   from	to	target	type
    //   2	31	42	finally
    //   31	33	42	finally
    //   34	39	42	finally
    //   43	45	42	finally
  }
  
  public boolean getAppOptOut()
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.GET_APP_OPT_OUT);
    return this.mAppOptOut.booleanValue();
  }
  
  public Tracker getDefaultTracker()
  {
    try
    {
      GAUsage.getInstance().setUsage(GAUsage.Field.GET_DEFAULT_TRACKER);
      Tracker localTracker = this.mDefaultTracker;
      return localTracker;
    }
    finally {}
  }
  
  public Logger getLogger()
  {
    return this.mLogger;
  }
  
  public Tracker getTracker(String paramString)
  {
    return getTracker(paramString, paramString);
  }
  
  public Tracker getTracker(String paramString1, String paramString2)
  {
    for (;;)
    {
      try
      {
        if (!TextUtils.isEmpty(paramString1))
        {
          localTracker = (Tracker)this.mTrackers.get(paramString1);
          if (localTracker != null)
          {
            paramString1 = localTracker;
            if (!TextUtils.isEmpty(paramString2)) {
              break label106;
            }
            GAUsage.getInstance().setUsage(GAUsage.Field.GET_TRACKER);
            return paramString1;
          }
        }
        else
        {
          throw new IllegalArgumentException("Tracker name cannot be empty");
        }
      }
      finally {}
      Tracker localTracker = new Tracker(paramString1, paramString2, this);
      this.mTrackers.put(paramString1, localTracker);
      paramString1 = localTracker;
      if (this.mDefaultTracker == null)
      {
        this.mDefaultTracker = localTracker;
        paramString1 = localTracker;
        continue;
        label106:
        paramString1.set("&tid", paramString2);
      }
    }
  }
  
  public boolean isDryRunEnabled()
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.GET_DRY_RUN);
    return this.mDryRun;
  }
  
  void sendHit(Map<String, String> paramMap)
  {
    if (paramMap != null) {}
    try
    {
      Utils.putIfAbsent(paramMap, "&ul", Utils.getLanguage(Locale.getDefault()));
      Utils.putIfAbsent(paramMap, "&sr", ScreenResolutionDefaultProvider.getProvider().getValue("&sr"));
      paramMap.put("&_u", GAUsage.getInstance().getAndClearSequence());
      GAUsage.getInstance().getAndClearUsage();
      this.mThread.sendHit(paramMap);
      return;
    }
    finally {}
    throw new IllegalArgumentException("hit cannot be null");
  }
  
  public void setAppOptOut(boolean paramBoolean)
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.SET_APP_OPT_OUT);
    this.mAppOptOut = Boolean.valueOf(paramBoolean);
    if (!this.mAppOptOut.booleanValue()) {
      return;
    }
    this.mThread.clearHits();
  }
  
  public void setDefaultTracker(Tracker paramTracker)
  {
    try
    {
      GAUsage.getInstance().setUsage(GAUsage.Field.SET_DEFAULT_TRACKER);
      this.mDefaultTracker = paramTracker;
      return;
    }
    finally {}
  }
  
  public void setDryRun(boolean paramBoolean)
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.SET_DRY_RUN);
    this.mDryRun = paramBoolean;
  }
  
  public void setLogger(Logger paramLogger)
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.SET_LOGGER);
    this.mLogger = paramLogger;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\GoogleAnalytics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */