package com.android.settings.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Switch;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.search.Index;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public final class BluetoothEnabler
  implements SwitchBar.OnSwitchChangeListener
{
  private static final String EVENT_DATA_IS_BT_ON = "is_bluetooth_on";
  private static final int EVENT_UPDATE_INDEX = 0;
  private Context mContext;
  private Handler mDelayedHandler = new Handler();
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      boolean bool = paramAnonymousMessage.getData().getBoolean("is_bluetooth_on");
      Index.getInstance(BluetoothEnabler.-get0(BluetoothEnabler.this)).updateFromClassNameResource(BluetoothSettings.class.getName(), true, bool);
    }
  };
  private final IntentFilter mIntentFilter;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int i = paramAnonymousIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
      BluetoothEnabler.this.handleStateChanged(i);
    }
  };
  private Switch mSwitch;
  private SwitchBar mSwitchBar;
  private Switch mSwitchView;
  private boolean mValidListener;
  Runnable runnable = new Runnable()
  {
    public void run()
    {
      if (BluetoothEnabler.-get1(BluetoothEnabler.this) != null)
      {
        boolean bool = BluetoothEnabler.-get1(BluetoothEnabler.this).setBluetoothEnabled(BluetoothEnabler.-get5(BluetoothEnabler.this));
        if ((BluetoothEnabler.-get5(BluetoothEnabler.this)) && (!bool)) {}
      }
      else
      {
        return;
      }
      BluetoothEnabler.-get4(BluetoothEnabler.this).setChecked(false);
      BluetoothEnabler.-get2(BluetoothEnabler.this).setEnabled(true);
      BluetoothEnabler.-get3(BluetoothEnabler.this).setTextViewLabel(false);
    }
  };
  private boolean switchChecked;
  
  public BluetoothEnabler(Context paramContext, SwitchBar paramSwitchBar)
  {
    this.mContext = paramContext;
    this.mSwitchBar = paramSwitchBar;
    this.mSwitch = paramSwitchBar.getSwitch();
    this.mValidListener = false;
    paramContext = Utils.getLocalBtManager(paramContext);
    if (paramContext == null)
    {
      this.mLocalAdapter = null;
      this.mSwitch.setEnabled(false);
    }
    for (;;)
    {
      this.mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
      return;
      this.mLocalAdapter = paramContext.getBluetoothAdapter();
    }
  }
  
  private void setChecked(boolean paramBoolean)
  {
    if (paramBoolean != this.mSwitch.isChecked())
    {
      if (this.mValidListener) {
        this.mSwitchBar.removeOnSwitchChangeListener(this);
      }
      this.mSwitch.setChecked(paramBoolean);
      if (this.mValidListener) {
        this.mSwitchBar.addOnSwitchChangeListener(this);
      }
    }
  }
  
  private void updateSearchIndex(boolean paramBoolean)
  {
    this.mHandler.removeMessages(0);
    Message localMessage = new Message();
    localMessage.what = 0;
    localMessage.getData().putBoolean("is_bluetooth_on", paramBoolean);
    this.mHandler.sendMessage(localMessage);
  }
  
  void handleStateChanged(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      setChecked(false);
      this.mSwitch.setEnabled(true);
      updateSearchIndex(false);
      return;
    case 11: 
      this.mSwitch.setEnabled(false);
      return;
    case 12: 
      setChecked(true);
      this.mSwitch.setEnabled(true);
      updateSearchIndex(true);
      return;
    case 13: 
      this.mSwitch.setEnabled(false);
      return;
    }
    setChecked(false);
    this.mSwitch.setEnabled(true);
    updateSearchIndex(false);
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    if ((!paramBoolean) || (WirelessUtils.isRadioAllowed(this.mContext, "bluetooth"))) {}
    for (;;)
    {
      MetricsLogger.action(this.mContext, 159, paramBoolean);
      this.switchChecked = paramBoolean;
      paramSwitch = this.mSwitchView;
      this.mDelayedHandler.removeCallbacks(this.runnable);
      this.mDelayedHandler.postDelayed(this.runnable, 500L);
      return;
      Toast.makeText(this.mContext, 2131691342, 0).show();
      paramSwitch.setChecked(false);
    }
  }
  
  public void pause()
  {
    if (this.mLocalAdapter == null) {
      return;
    }
    this.mSwitchBar.removeOnSwitchChangeListener(this);
    this.mContext.unregisterReceiver(this.mReceiver);
    this.mValidListener = false;
  }
  
  public void resume(Context paramContext)
  {
    if (this.mLocalAdapter == null)
    {
      this.mSwitch.setEnabled(false);
      return;
    }
    if (this.mContext != paramContext) {
      this.mContext = paramContext;
    }
    handleStateChanged(this.mLocalAdapter.getBluetoothState());
    this.mSwitchBar.addOnSwitchChangeListener(this);
    this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    this.mValidListener = true;
  }
  
  public void setupSwitchBar()
  {
    this.mSwitchBar.show();
  }
  
  public void teardownSwitchBar()
  {
    this.mSwitchBar.hide();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\BluetoothEnabler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */