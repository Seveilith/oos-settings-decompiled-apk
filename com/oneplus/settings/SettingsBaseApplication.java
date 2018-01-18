package com.oneplus.settings;

import android.app.Application;
import android.content.res.Configuration;
import android.os.SystemProperties;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.google.analytics.tracking.android.Tracker;
import com.oneplus.settings.utils.OPUtils;

public class SettingsBaseApplication
  extends Application
{
  public static final boolean ONEPLUS_DEBUG = SystemProperties.getBoolean("persist.sys.assert.panic", false);
  public static Application mApplication;
  private boolean mIsBeta;
  private Tracker mTracker;
  
  public Tracker getDefaultTracker()
  {
    if (this.mTracker == null) {}
    try
    {
      if (this.mTracker == null)
      {
        GoogleAnalytics localGoogleAnalytics = GoogleAnalytics.getInstance(this);
        localGoogleAnalytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        this.mTracker = localGoogleAnalytics.getTracker("UA-92966593-3");
      }
      return this.mTracker;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isBetaRom()
  {
    return this.mIsBeta;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
  }
  
  public void onCreate()
  {
    super.onCreate();
    mApplication = this;
    this.mIsBeta = OPUtils.isBetaRom();
    OPOnlineConfigManager.getInstence(mApplication).init();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\SettingsBaseApplication.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */