package com.android.settings.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.SystemProperties;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import com.android.settingslib.bluetooth.BluetoothDiscoverableTimeoutReceiver;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;

final class BluetoothDiscoverableEnabler
  implements Preference.OnPreferenceClickListener
{
  static final int DEFAULT_DISCOVERABLE_TIMEOUT = 120;
  private static final int DISCOVERABLE_TIMEOUT_FIVE_MINUTES = 300;
  static final int DISCOVERABLE_TIMEOUT_NEVER = 0;
  private static final int DISCOVERABLE_TIMEOUT_ONE_HOUR = 3600;
  private static final int DISCOVERABLE_TIMEOUT_TWO_MINUTES = 120;
  private static final String KEY_DISCOVERABLE_TIMEOUT = "bt_discoverable_timeout";
  private static final String SYSTEM_PROPERTY_DISCOVERABLE_TIMEOUT = "debug.bt.discoverable_time";
  private static final String TAG = "BluetoothDiscoverableEnabler";
  private static final String VALUE_DISCOVERABLE_TIMEOUT_FIVE_MINUTES = "fivemin";
  private static final String VALUE_DISCOVERABLE_TIMEOUT_NEVER = "never";
  private static final String VALUE_DISCOVERABLE_TIMEOUT_ONE_HOUR = "onehour";
  private static final String VALUE_DISCOVERABLE_TIMEOUT_TWO_MINUTES = "twomin";
  private Context mContext;
  private boolean mDiscoverable;
  private final Preference mDiscoveryPreference;
  private final LocalBluetoothAdapter mLocalAdapter;
  private int mNumberOfPairedDevices;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.bluetooth.adapter.action.SCAN_MODE_CHANGED".equals(paramAnonymousIntent.getAction()))
      {
        int i = paramAnonymousIntent.getIntExtra("android.bluetooth.adapter.extra.SCAN_MODE", Integer.MIN_VALUE);
        if (i != Integer.MIN_VALUE) {
          BluetoothDiscoverableEnabler.this.handleModeChanged(i);
        }
      }
    }
  };
  private final SharedPreferences mSharedPreferences;
  private int mTimeoutSecs = -1;
  private final Handler mUiHandler = new Handler();
  private final Runnable mUpdateCountdownSummaryRunnable = new Runnable()
  {
    public void run()
    {
      BluetoothDiscoverableEnabler.-wrap0(BluetoothDiscoverableEnabler.this);
    }
  };
  
  BluetoothDiscoverableEnabler(LocalBluetoothAdapter paramLocalBluetoothAdapter, Preference paramPreference)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDiscoveryPreference = paramPreference;
    this.mSharedPreferences = paramPreference.getSharedPreferences();
    paramPreference.setPersistent(false);
  }
  
  private static String formatTimeRemaining(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder(6);
    int i = paramInt / 60;
    localStringBuilder.append(i).append(':');
    paramInt -= i * 60;
    if (paramInt < 10) {
      localStringBuilder.append('0');
    }
    localStringBuilder.append(paramInt);
    return localStringBuilder.toString();
  }
  
  private int getDiscoverableTimeout()
  {
    if (this.mTimeoutSecs != -1) {
      return this.mTimeoutSecs;
    }
    int j = SystemProperties.getInt("debug.bt.discoverable_time", -1);
    int i = j;
    String str;
    if (j < 0)
    {
      str = this.mSharedPreferences.getString("bt_discoverable_timeout", "twomin");
      if (!str.equals("never")) {
        break label58;
      }
      i = 0;
    }
    for (;;)
    {
      this.mTimeoutSecs = i;
      return i;
      label58:
      if (str.equals("onehour")) {
        i = 3600;
      } else if (str.equals("fivemin")) {
        i = 300;
      } else {
        i = 120;
      }
    }
  }
  
  private void setEnabled(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      int i = getDiscoverableTimeout();
      long l = System.currentTimeMillis() + i * 1000L;
      LocalBluetoothPreferences.persistDiscoverableEndTimestamp(this.mContext, l);
      this.mLocalAdapter.setScanMode(23, i);
      updateCountdownSummary();
      Log.d("BluetoothDiscoverableEnabler", "setEnabled(): enabled = " + paramBoolean + "timeout = " + i);
      if (i > 0)
      {
        BluetoothDiscoverableTimeoutReceiver.setDiscoverableAlarm(this.mContext, l);
        return;
      }
      BluetoothDiscoverableTimeoutReceiver.cancelDiscoverableAlarm(this.mContext);
      return;
    }
    this.mLocalAdapter.setScanMode(21);
    BluetoothDiscoverableTimeoutReceiver.cancelDiscoverableAlarm(this.mContext);
  }
  
  private void setSummaryNotDiscoverable()
  {
    if (this.mNumberOfPairedDevices != 0)
    {
      this.mDiscoveryPreference.setSummary(2131690846);
      return;
    }
    this.mDiscoveryPreference.setSummary(2131690845);
  }
  
  private void updateCountdownSummary()
  {
    if (this.mLocalAdapter.getScanMode() != 23) {
      return;
    }
    long l1 = System.currentTimeMillis();
    long l2 = LocalBluetoothPreferences.getDiscoverableEndTimestamp(this.mContext);
    if (l1 > l2)
    {
      updateTimerDisplay(0);
      return;
    }
    updateTimerDisplay((int)((l2 - l1) / 1000L));
    try
    {
      this.mUiHandler.removeCallbacks(this.mUpdateCountdownSummaryRunnable);
      this.mUiHandler.postDelayed(this.mUpdateCountdownSummaryRunnable, 1000L);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void updateTimerDisplay(int paramInt)
  {
    if (getDiscoverableTimeout() == 0)
    {
      this.mDiscoveryPreference.setSummary(2131690844);
      return;
    }
    String str = formatTimeRemaining(paramInt);
    this.mDiscoveryPreference.setSummary(this.mContext.getString(2131690843, new Object[] { str }));
  }
  
  int getDiscoverableTimeoutIndex()
  {
    switch (getDiscoverableTimeout())
    {
    default: 
      return 0;
    case 300: 
      return 1;
    case 3600: 
      return 2;
    }
    return 3;
  }
  
  void handleModeChanged(int paramInt)
  {
    Log.d("BluetoothDiscoverableEnabler", "handleModeChanged(): mode = " + paramInt);
    if (paramInt == 23)
    {
      this.mDiscoverable = true;
      updateCountdownSummary();
      return;
    }
    this.mDiscoverable = false;
    setSummaryNotDiscoverable();
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (this.mDiscoverable) {}
    for (boolean bool = false;; bool = true)
    {
      this.mDiscoverable = bool;
      setEnabled(this.mDiscoverable);
      return true;
    }
  }
  
  public void pause()
  {
    if (this.mLocalAdapter == null) {
      return;
    }
    this.mUiHandler.removeCallbacks(this.mUpdateCountdownSummaryRunnable);
    this.mContext.unregisterReceiver(this.mReceiver);
    this.mDiscoveryPreference.setOnPreferenceClickListener(null);
  }
  
  public void resume(Context paramContext)
  {
    if (this.mLocalAdapter == null) {
      return;
    }
    if (this.mContext != paramContext) {
      this.mContext = paramContext;
    }
    paramContext = new IntentFilter("android.bluetooth.adapter.action.SCAN_MODE_CHANGED");
    this.mContext.registerReceiver(this.mReceiver, paramContext);
    this.mDiscoveryPreference.setOnPreferenceClickListener(this);
    handleModeChanged(this.mLocalAdapter.getScanMode());
  }
  
  void setDiscoverableTimeout(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      this.mTimeoutSecs = 120;
      str = "twomin";
    }
    for (;;)
    {
      this.mSharedPreferences.edit().putString("bt_discoverable_timeout", str).apply();
      setEnabled(true);
      return;
      this.mTimeoutSecs = 300;
      str = "fivemin";
      continue;
      this.mTimeoutSecs = 3600;
      str = "onehour";
      continue;
      this.mTimeoutSecs = 0;
      str = "never";
    }
  }
  
  void setNumberOfPairedDevices(int paramInt)
  {
    this.mNumberOfPairedDevices = paramInt;
    handleModeChanged(this.mLocalAdapter.getScanMode());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\BluetoothDiscoverableEnabler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */