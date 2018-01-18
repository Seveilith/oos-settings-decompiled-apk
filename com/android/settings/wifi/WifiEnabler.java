package com.android.settings.wifi;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.search.Index;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.WirelessUtils;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiEnabler
  implements SwitchBar.OnSwitchChangeListener
{
  private static final int DELAY_TIME = 300;
  private static final String EVENT_DATA_IS_WIFI_ON = "is_wifi_on";
  private static final int EVENT_UPDATE_INDEX = 0;
  public static final String KEY_TURN_OFF_WIFI_SHOW_AGAIN = "TurnOffWifiShowAgain";
  public static final String MY_PREF_FILE = "MY_PERFS";
  private static CheckBox mNotShowAgainCheckbox;
  private AtomicBoolean mConnected = new AtomicBoolean(false);
  private Context mContext;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      boolean bool = paramAnonymousMessage.getData().getBoolean("is_wifi_on");
      Index.getInstance(WifiEnabler.-get1(WifiEnabler.this)).updateFromClassNameResource(WifiSettings.class.getName(), true, bool);
    }
  };
  private final IntentFilter mIntentFilter;
  private boolean mListeningToOnSwitchChange = false;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ("android.net.wifi.WIFI_STATE_CHANGED".equals(paramAnonymousContext)) {
        WifiEnabler.-wrap1(WifiEnabler.this, paramAnonymousIntent.getIntExtra("wifi_state", 4));
      }
      do
      {
        do
        {
          return;
          if (!"android.net.wifi.supplicant.STATE_CHANGE".equals(paramAnonymousContext)) {
            break;
          }
        } while (WifiEnabler.-get0(WifiEnabler.this).get());
        WifiEnabler.-wrap0(WifiEnabler.this, WifiInfo.getDetailedStateOf((SupplicantState)paramAnonymousIntent.getParcelableExtra("newState")));
        return;
      } while (!"android.net.wifi.STATE_CHANGE".equals(paramAnonymousContext));
      paramAnonymousContext = (NetworkInfo)paramAnonymousIntent.getParcelableExtra("networkInfo");
      WifiEnabler.-get0(WifiEnabler.this).set(paramAnonymousContext.isConnected());
      WifiEnabler.-wrap0(WifiEnabler.this, paramAnonymousContext.getDetailedState());
    }
  };
  private boolean mStateMachineEvent;
  private SwitchBar mSwitchBar;
  private WifiDisableRunnable mWifiDisableRunnable = new WifiDisableRunnable();
  private Handler mWifiHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
    }
  };
  private final WifiManager mWifiManager;
  
  public WifiEnabler(Context paramContext, SwitchBar paramSwitchBar)
  {
    this.mContext = paramContext;
    this.mSwitchBar = paramSwitchBar;
    this.mWifiManager = ((WifiManager)paramContext.getSystemService("wifi"));
    this.mIntentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
    this.mIntentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
    this.mIntentFilter.addAction("android.net.wifi.STATE_CHANGE");
    setupSwitchBar();
  }
  
  private void handleStateChanged(NetworkInfo.DetailedState paramDetailedState) {}
  
  private void handleWifiStateChanged(int paramInt)
  {
    boolean bool = true;
    this.mSwitchBar.setDisabledByAdmin(null);
    switch (paramInt)
    {
    default: 
      setSwitchBarChecked(false);
      this.mSwitchBar.setEnabled(true);
      updateSearchIndex(false);
    }
    for (;;)
    {
      if (this.mSwitchBar.isChecked()) {
        bool = false;
      }
      if (mayDisableTethering(bool))
      {
        if (!RestrictedLockUtils.hasBaseUserRestriction(this.mContext, "no_config_tethering", UserHandle.myUserId())) {
          break;
        }
        this.mSwitchBar.setEnabled(false);
      }
      return;
      this.mSwitchBar.setEnabled(false);
      continue;
      setSwitchBarChecked(true);
      this.mSwitchBar.setEnabled(true);
      updateSearchIndex(true);
      continue;
      this.mSwitchBar.setEnabled(false);
      continue;
      setSwitchBarChecked(false);
      this.mSwitchBar.setEnabled(true);
      updateSearchIndex(false);
    }
    RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(this.mContext, "no_config_tethering", UserHandle.myUserId());
    this.mSwitchBar.setDisabledByAdmin(localEnforcedAdmin);
  }
  
  private boolean mayDisableTethering(boolean paramBoolean)
  {
    int i = this.mWifiManager.getWifiApState();
    if (paramBoolean) {
      return (i == 12) || (i == 13);
    }
    return false;
  }
  
  private void setSwitchBarChecked(boolean paramBoolean)
  {
    this.mStateMachineEvent = true;
    this.mSwitchBar.setChecked(paramBoolean);
    this.mStateMachineEvent = false;
  }
  
  private void updateSearchIndex(boolean paramBoolean)
  {
    this.mHandler.removeMessages(0);
    Message localMessage = new Message();
    localMessage.what = 0;
    localMessage.getData().putBoolean("is_wifi_on", paramBoolean);
    this.mHandler.sendMessage(localMessage);
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    if (this.mStateMachineEvent)
    {
      Log.d(WifiEnabler.class.getName(), "mStateMachineEvent = " + this.mStateMachineEvent);
      return;
    }
    final SharedPreferences localSharedPreferences;
    if ((!paramBoolean) || (WirelessUtils.isRadioAllowed(this.mContext, "wifi")))
    {
      if ((this.mWifiManager.getWifiStaSapConcurrency()) || (!mayDisableTethering(paramBoolean))) {
        break label368;
      }
      if (!this.mContext.getResources().getBoolean(2131558464)) {
        break label324;
      }
      paramSwitch = ((LayoutInflater)this.mContext.getSystemService("layout_inflater")).inflate(2130968758, null);
      mNotShowAgainCheckbox = (CheckBox)paramSwitch.findViewById(2131362229);
      localSharedPreferences = this.mContext.getSharedPreferences("MY_PERFS", 0);
      if (localSharedPreferences.getBoolean("TurnOffWifiShowAgain", true)) {
        break label269;
      }
      this.mWifiManager.setWifiApEnabled(null, false);
      paramSwitch = this.mContext;
      if (!paramBoolean) {
        break label262;
      }
    }
    label262:
    for (int i = 139;; i = 138)
    {
      MetricsLogger.action(paramSwitch, i);
      if (!this.mWifiManager.setWifiEnabled(paramBoolean))
      {
        this.mSwitchBar.setEnabled(true);
        Toast.makeText(this.mContext, 2131691340, 0).show();
      }
      return;
      Toast.makeText(this.mContext, 2131691342, 0).show();
      Log.d(WifiEnabler.class.getName(), "isRadioAllowed isChecked= " + paramBoolean);
      this.mSwitchBar.setChecked(false);
      return;
    }
    label269:
    paramSwitch = new AlertDialog.Builder(this.mContext).setTitle(2131693784).setMessage(2131693785).setView(paramSwitch).setPositiveButton(2131690994, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = localSharedPreferences.edit();
        if (WifiEnabler.-get2().isChecked()) {}
        for (boolean bool = false;; bool = true)
        {
          paramAnonymousDialogInterface.putBoolean("TurnOffWifiShowAgain", bool);
          paramAnonymousDialogInterface.commit();
          return;
        }
      }
    });
    paramSwitch.setCancelable(false);
    paramSwitch.show();
    label324:
    this.mWifiManager.setWifiApEnabled(null, false);
    if (!this.mWifiManager.setWifiEnabled(paramBoolean))
    {
      this.mSwitchBar.setEnabled(true);
      Toast.makeText(this.mContext, 2131691340, 0).show();
    }
    return;
    label368:
    if (this.mWifiDisableRunnable == null) {
      this.mWifiDisableRunnable = new WifiDisableRunnable();
    }
    if ((paramBoolean) && (this.mWifiManager != null) && ((this.mWifiManager.getWifiState() != 2) || (this.mWifiManager.getWifiState() != 3)))
    {
      this.mWifiHandler.removeCallbacks(this.mWifiDisableRunnable);
      this.mWifiDisableRunnable.setValue(paramBoolean);
      this.mWifiHandler.postDelayed(this.mWifiDisableRunnable, 300L);
    }
    while ((paramBoolean) || (this.mWifiManager == null) || ((this.mWifiManager.getWifiState() == 0) && (this.mWifiManager.getWifiState() == 1))) {
      return;
    }
    this.mWifiHandler.removeCallbacks(this.mWifiDisableRunnable);
    this.mWifiDisableRunnable.setValue(paramBoolean);
    this.mWifiHandler.postDelayed(this.mWifiDisableRunnable, 300L);
  }
  
  public void pause()
  {
    this.mContext.unregisterReceiver(this.mReceiver);
    if (this.mListeningToOnSwitchChange)
    {
      this.mSwitchBar.removeOnSwitchChangeListener(this);
      this.mListeningToOnSwitchChange = false;
    }
  }
  
  public void resume(Context paramContext)
  {
    this.mContext = paramContext;
    this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    if (!this.mListeningToOnSwitchChange)
    {
      this.mSwitchBar.addOnSwitchChangeListener(this);
      this.mListeningToOnSwitchChange = true;
    }
  }
  
  public void setupSwitchBar()
  {
    handleWifiStateChanged(this.mWifiManager.getWifiState());
    if (!this.mListeningToOnSwitchChange)
    {
      this.mSwitchBar.addOnSwitchChangeListener(this);
      this.mListeningToOnSwitchChange = true;
    }
    this.mSwitchBar.show();
  }
  
  public void teardownSwitchBar()
  {
    if (this.mListeningToOnSwitchChange)
    {
      this.mSwitchBar.removeOnSwitchChangeListener(this);
      this.mListeningToOnSwitchChange = false;
    }
    this.mSwitchBar.hide();
  }
  
  class WifiDisableRunnable
    implements Runnable
  {
    boolean dValue = false;
    
    public WifiDisableRunnable() {}
    
    public WifiDisableRunnable(boolean paramBoolean)
    {
      this.dValue = paramBoolean;
    }
    
    public void run()
    {
      if (WifiEnabler.-get4(WifiEnabler.this) == null) {
        return;
      }
      if ((WifiEnabler.-get4(WifiEnabler.this) == null) || (WifiEnabler.-get4(WifiEnabler.this).setWifiEnabled(this.dValue))) {
        return;
      }
      WifiEnabler.-get3(WifiEnabler.this).setEnabled(true);
      Toast.makeText(WifiEnabler.-get1(WifiEnabler.this), 2131691340, 0).show();
    }
    
    public void setValue(boolean paramBoolean)
    {
      this.dValue = paramBoolean;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiEnabler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */