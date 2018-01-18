package com.oneplus.settings.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OPZenModeUtils
{
  private static final Long IGNORE_TIME_VALUE = Long.valueOf(10000L);
  private static final int ZEN_MODE_ALARMS = 3;
  private static final int ZEN_MODE_IMPORTANT_INTERRUPTIONS = 1;
  private static final int ZEN_MODE_OFF = 0;
  private static OPZenModeUtils mOPZenModeUtils;
  Date DateTime = new Date();
  private Context mContext;
  private Handler mHandler = new Handler();
  private Runnable mRun = new Runnable()
  {
    public void run()
    {
      OPZenModeUtils.this.sendAppTracker();
    }
  };
  private SharedPreferences mSharedPreferences = null;
  private int mZenMode = 0;
  
  public OPZenModeUtils(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public static OPZenModeUtils getInstance(Context paramContext)
  {
    if (mOPZenModeUtils == null) {
      mOPZenModeUtils = new OPZenModeUtils(paramContext);
    }
    return mOPZenModeUtils;
  }
  
  public void sendAppTracker()
  {
    this.mSharedPreferences = this.mContext.getSharedPreferences("App_Tracker", 0);
    SharedPreferences.Editor localEditor = this.mSharedPreferences.edit();
    String str = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date());
    this.mZenMode = NotificationManager.from(this.mContext).getZenMode();
    if (this.mZenMode == 3) {
      OPUtils.sendAppTracker("zen_mode_alarms", str);
    }
    for (;;)
    {
      localEditor.putInt("zen_mode", this.mZenMode);
      localEditor.commit();
      return;
      if (this.mZenMode == 1) {
        OPUtils.sendAppTracker("zen_mode_important_interruptions", str);
      } else if (this.mZenMode == 0) {
        OPUtils.sendAppTracker("zen_mode_off", str);
      }
    }
  }
  
  public void sendAppTrackerDelay()
  {
    this.mHandler.removeCallbacks(this.mRun);
    this.mHandler.postDelayed(this.mRun, IGNORE_TIME_VALUE.longValue());
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\utils\OPZenModeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */