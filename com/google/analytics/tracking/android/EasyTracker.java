package com.google.analytics.tracking.android;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class EasyTracker
  extends Tracker
{
  private static final int DEFAULT_SAMPLE_RATE = 100;
  private static final String EASY_TRACKER_NAME = "easy_tracker";
  static final int NUM_MILLISECONDS_TO_WAIT_FOR_OPEN_ACTIVITY = 1000;
  private static EasyTracker sInstance;
  private static String sResourcePackageName;
  private int mActivitiesActive = 0;
  private final Map<String, String> mActivityNameMap = new HashMap();
  private Clock mClock;
  private Context mContext;
  private final GoogleAnalytics mGoogleAnalytics;
  private boolean mIsAutoActivityTracking = false;
  private boolean mIsInForeground = false;
  private boolean mIsReportUncaughtExceptionsEnabled;
  private long mLastOnStopTime;
  private ParameterLoader mParameterFetcher;
  private ServiceManager mServiceManager;
  private long mSessionTimeout;
  private boolean mStartSessionOnNextSend = false;
  private Timer mTimer;
  private TimerTask mTimerTask;
  
  private EasyTracker(Context paramContext)
  {
    this(paramContext, new ParameterLoaderImpl(paramContext), GoogleAnalytics.getInstance(paramContext), GAServiceManager.getInstance(), null);
  }
  
  private EasyTracker(Context paramContext, ParameterLoader paramParameterLoader, GoogleAnalytics paramGoogleAnalytics, ServiceManager paramServiceManager, TrackerHandler paramTrackerHandler)
  {
    super("easy_tracker", null, (TrackerHandler)localObject);
    if (sResourcePackageName == null) {}
    for (;;)
    {
      this.mGoogleAnalytics = paramGoogleAnalytics;
      setContext(paramContext, paramParameterLoader, paramServiceManager);
      this.mClock = new Clock()
      {
        public long currentTimeMillis()
        {
          return System.currentTimeMillis();
        }
      };
      return;
      paramParameterLoader.setResourcePackageName(sResourcePackageName);
    }
  }
  
  /* Error */
  private void clearExistingTimer()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 110	com/google/analytics/tracking/android/EasyTracker:mTimer	Ljava/util/Timer;
    //   6: astore_1
    //   7: aload_1
    //   8: ifnonnull +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: getfield 110	com/google/analytics/tracking/android/EasyTracker:mTimer	Ljava/util/Timer;
    //   18: invokevirtual 115	java/util/Timer:cancel	()V
    //   21: aload_0
    //   22: aconst_null
    //   23: putfield 110	com/google/analytics/tracking/android/EasyTracker:mTimer	Ljava/util/Timer;
    //   26: goto -15 -> 11
    //   29: astore_1
    //   30: aload_0
    //   31: monitorexit
    //   32: aload_1
    //   33: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	34	0	this	EasyTracker
    //   6	2	1	localTimer	Timer
    //   29	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	29	finally
    //   14	26	29	finally
  }
  
  private String getActivityName(Activity paramActivity)
  {
    String str = paramActivity.getClass().getCanonicalName();
    if (!this.mActivityNameMap.containsKey(str))
    {
      paramActivity = this.mParameterFetcher.getString(str);
      if (paramActivity == null) {
        break label64;
      }
    }
    for (;;)
    {
      this.mActivityNameMap.put(str, paramActivity);
      return paramActivity;
      return (String)this.mActivityNameMap.get(str);
      label64:
      paramActivity = str;
    }
  }
  
  public static EasyTracker getInstance(Context paramContext)
  {
    if (sInstance != null) {}
    for (;;)
    {
      return sInstance;
      sInstance = new EasyTracker(paramContext);
    }
  }
  
  private Logger.LogLevel getLogLevelFromString(String paramString)
  {
    try
    {
      paramString = Logger.LogLevel.valueOf(paramString.toUpperCase());
      return paramString;
    }
    catch (IllegalArgumentException paramString) {}
    return null;
  }
  
  @VisibleForTesting
  static EasyTracker getNewInstance(Context paramContext, ParameterLoader paramParameterLoader, GoogleAnalytics paramGoogleAnalytics, ServiceManager paramServiceManager, TrackerHandler paramTrackerHandler)
  {
    sInstance = new EasyTracker(paramContext, paramParameterLoader, paramGoogleAnalytics, paramServiceManager, paramTrackerHandler);
    return sInstance;
  }
  
  private void loadParameters()
  {
    Log.v("Starting EasyTracker.");
    Object localObject = this.mParameterFetcher.getString("ga_trackingId");
    label72:
    label88:
    label104:
    label120:
    label275:
    boolean bool;
    if (!TextUtils.isEmpty((CharSequence)localObject))
    {
      set("&tid", (String)localObject);
      Log.v("[EasyTracker] trackingId loaded: " + (String)localObject);
      localObject = this.mParameterFetcher.getString("ga_appName");
      if (!TextUtils.isEmpty((CharSequence)localObject)) {
        break label386;
      }
      localObject = this.mParameterFetcher.getString("ga_appVersion");
      if (localObject != null) {
        break label420;
      }
      localObject = this.mParameterFetcher.getString("ga_logLevel");
      if (localObject != null) {
        break label454;
      }
      localObject = this.mParameterFetcher.getDoubleFromString("ga_sampleFrequency");
      if (localObject == null) {
        break label503;
      }
      if (((Double)localObject).doubleValue() != 100.0D) {
        set("&sf", Double.toString(((Double)localObject).doubleValue()));
      }
      Log.v("[EasyTracker] sample rate loaded: " + localObject);
      int i = this.mParameterFetcher.getInt("ga_dispatchPeriod", 1800);
      Log.v("[EasyTracker] dispatch period loaded: " + i);
      this.mServiceManager.setLocalDispatchPeriod(i);
      this.mSessionTimeout = (this.mParameterFetcher.getInt("ga_sessionTimeout", 30) * 1000);
      Log.v("[EasyTracker] session timeout loaded: " + this.mSessionTimeout);
      if (!this.mParameterFetcher.getBoolean("ga_autoActivityTracking")) {
        break label529;
      }
      bool = true;
      label277:
      this.mIsAutoActivityTracking = bool;
      Log.v("[EasyTracker] auto activity tracking loaded: " + this.mIsAutoActivityTracking);
      bool = this.mParameterFetcher.getBoolean("ga_anonymizeIp");
      if (bool) {
        break label549;
      }
      label325:
      this.mIsReportUncaughtExceptionsEnabled = this.mParameterFetcher.getBoolean("ga_reportUncaughtExceptions");
      if (this.mIsReportUncaughtExceptionsEnabled) {
        break label585;
      }
    }
    for (;;)
    {
      bool = this.mParameterFetcher.getBoolean("ga_dryRun");
      this.mGoogleAnalytics.setDryRun(bool);
      return;
      localObject = this.mParameterFetcher.getString("ga_api_key");
      break;
      label386:
      Log.v("[EasyTracker] app name loaded: " + (String)localObject);
      set("&an", (String)localObject);
      break label72;
      label420:
      Log.v("[EasyTracker] app version loaded: " + (String)localObject);
      set("&av", (String)localObject);
      break label88;
      label454:
      localObject = getLogLevelFromString((String)localObject);
      if (localObject == null) {
        break label104;
      }
      Log.v("[EasyTracker] log level loaded: " + localObject);
      this.mGoogleAnalytics.getLogger().setLogLevel((Logger.LogLevel)localObject);
      break label104;
      label503:
      localObject = new Double(this.mParameterFetcher.getInt("ga_sampleRate", 100));
      break label120;
      label529:
      if (this.mParameterFetcher.getBoolean("ga_auto_activity_tracking")) {
        break label275;
      }
      bool = false;
      break label277;
      label549:
      set("&aip", "1");
      Log.v("[EasyTracker] anonymize ip loaded: " + bool);
      break label325;
      label585:
      Thread.setDefaultUncaughtExceptionHandler(new ExceptionReporter(this, this.mServiceManager, Thread.getDefaultUncaughtExceptionHandler(), this.mContext));
      Log.v("[EasyTracker] report uncaught exceptions loaded: " + this.mIsReportUncaughtExceptionsEnabled);
    }
  }
  
  private void setContext(Context paramContext, ParameterLoader paramParameterLoader, ServiceManager paramServiceManager)
  {
    if (paramContext != null) {}
    for (;;)
    {
      this.mContext = paramContext.getApplicationContext();
      this.mServiceManager = paramServiceManager;
      this.mParameterFetcher = paramParameterLoader;
      loadParameters();
      return;
      Log.e("Context cannot be null");
    }
  }
  
  public static void setResourcePackageName(String paramString)
  {
    sResourcePackageName = paramString;
  }
  
  public void activityStart(Activity paramActivity)
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.EASY_TRACKER_ACTIVITY_START);
    clearExistingTimer();
    if (this.mIsInForeground) {}
    for (;;)
    {
      this.mIsInForeground = true;
      this.mActivitiesActive += 1;
      if (this.mIsAutoActivityTracking) {
        break;
      }
      return;
      if ((this.mActivitiesActive == 0) && (checkForNewSession())) {
        this.mStartSessionOnNextSend = true;
      }
    }
    HashMap localHashMap = new HashMap();
    localHashMap.put("&t", "appview");
    GAUsage.getInstance().setDisableUsage(true);
    set("&cd", getActivityName(paramActivity));
    send(localHashMap);
    GAUsage.getInstance().setDisableUsage(false);
  }
  
  public void activityStop(Activity paramActivity)
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.EASY_TRACKER_ACTIVITY_STOP);
    this.mActivitiesActive -= 1;
    this.mActivitiesActive = Math.max(0, this.mActivitiesActive);
    this.mLastOnStopTime = this.mClock.currentTimeMillis();
    if (this.mActivitiesActive != 0) {
      return;
    }
    clearExistingTimer();
    this.mTimerTask = new NotInForegroundTimerTask(null);
    this.mTimer = new Timer("waitForActivityStart");
    this.mTimer.schedule(this.mTimerTask, 1000L);
  }
  
  boolean checkForNewSession()
  {
    boolean bool2 = false;
    boolean bool1;
    if (this.mSessionTimeout != 0L)
    {
      if (this.mSessionTimeout > 0L) {
        break label62;
      }
      i = 1;
      bool1 = bool2;
      if (i != 0) {
        break label60;
      }
      if (this.mClock.currentTimeMillis() > this.mLastOnStopTime + this.mSessionTimeout) {
        break label67;
      }
    }
    label60:
    label62:
    label67:
    for (int i = 1;; i = 0)
    {
      bool1 = bool2;
      if (i == 0) {
        bool1 = true;
      }
      return bool1;
      i = 0;
      break;
    }
  }
  
  @Deprecated
  public void dispatchLocalHits()
  {
    this.mServiceManager.dispatchLocalHits();
  }
  
  @VisibleForTesting
  int getActivitiesActive()
  {
    return this.mActivitiesActive;
  }
  
  @VisibleForTesting
  void overrideUncaughtExceptionHandler(Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler)
  {
    if (!this.mIsReportUncaughtExceptionsEnabled) {
      return;
    }
    Thread.setDefaultUncaughtExceptionHandler(paramUncaughtExceptionHandler);
  }
  
  public void send(Map<String, String> paramMap)
  {
    if (!this.mStartSessionOnNextSend) {}
    for (;;)
    {
      super.send(paramMap);
      return;
      paramMap.put("&sc", "start");
      this.mStartSessionOnNextSend = false;
    }
  }
  
  @VisibleForTesting
  void setClock(Clock paramClock)
  {
    this.mClock = paramClock;
  }
  
  private class NotInForegroundTimerTask
    extends TimerTask
  {
    private NotInForegroundTimerTask() {}
    
    public void run()
    {
      EasyTracker.access$102(EasyTracker.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\EasyTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */