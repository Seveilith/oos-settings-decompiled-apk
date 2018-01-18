package com.android.settings.fuelgauge;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public abstract class PowerUsageBase
  extends SettingsPreferenceFragment
{
  private static final int MENU_STATS_REFRESH = 2;
  static final int MSG_REFRESH_STATS = 100;
  private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (("android.intent.action.BATTERY_CHANGED".equals(paramAnonymousIntent.getAction())) && (PowerUsageBase.-wrap0(PowerUsageBase.this, paramAnonymousIntent)) && (!PowerUsageBase.-get0(PowerUsageBase.this).hasMessages(100))) {
        PowerUsageBase.-get0(PowerUsageBase.this).sendEmptyMessageDelayed(100, 500L);
      }
    }
  };
  private String mBatteryLevel;
  private String mBatteryStatus;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      default: 
        return;
      }
      synchronized (PowerUsageBase.this.mStatsHelper)
      {
        PowerUsageBase.this.mStatsHelper.clearStats();
        PowerUsageBase.this.refreshStats();
        return;
      }
    }
  };
  protected BatteryStatsHelper mStatsHelper;
  protected UserManager mUm;
  
  private boolean updateBatteryStatus(Intent paramIntent)
  {
    String str;
    if (paramIntent != null)
    {
      str = Utils.getBatteryPercentage(paramIntent);
      paramIntent = Utils.getBatteryStatus(getResources(), paramIntent);
      if ((!str.equals(this.mBatteryLevel)) || (!paramIntent.equals(this.mBatteryStatus))) {}
    }
    else
    {
      return false;
    }
    this.mBatteryLevel = str;
    this.mBatteryStatus = paramIntent;
    return true;
  }
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    this.mUm = ((UserManager)paramActivity.getSystemService("user"));
    this.mStatsHelper = new BatteryStatsHelper(paramActivity, true);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mStatsHelper.create(paramBundle);
    setHasOptionsMenu(true);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (getActivity().isChangingConfigurations()) {
      this.mStatsHelper.storeState();
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem arg1)
  {
    switch (???.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(???);
    }
    synchronized (this.mStatsHelper)
    {
      this.mStatsHelper.clearStats();
      refreshStats();
      this.mHandler.removeMessages(100);
      return true;
    }
  }
  
  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mBatteryInfoReceiver);
  }
  
  public void onResume()
  {
    super.onResume();
    BatteryStatsHelper.dropFile(getActivity(), "tmp_bat_history.bin");
    updateBatteryStatus(getActivity().registerReceiver(this.mBatteryInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED")));
    if (this.mHandler.hasMessages(100)) {
      this.mHandler.removeMessages(100);
    }
    synchronized (this.mStatsHelper)
    {
      this.mStatsHelper.clearStats();
      return;
    }
  }
  
  public void onStart()
  {
    super.onStart();
    synchronized (this.mStatsHelper)
    {
      this.mStatsHelper.clearStats();
      return;
    }
  }
  
  public void onStop()
  {
    super.onStop();
    this.mHandler.removeMessages(100);
  }
  
  protected void refreshStats()
  {
    synchronized (this.mStatsHelper)
    {
      this.mStatsHelper.refreshStats(0, this.mUm.getUserProfiles());
      return;
    }
  }
  
  protected void updatePreference(BatteryHistoryPreference paramBatteryHistoryPreference)
  {
    paramBatteryHistoryPreference.setStats(this.mStatsHelper);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\PowerUsageBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */