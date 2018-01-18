package com.android.settings.fuelgauge;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings.Global;
import android.util.Log;
import android.widget.Switch;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.dashboard.conditional.BatterySaverCondition;
import com.android.settings.dashboard.conditional.ConditionManager;
import com.android.settings.notification.SettingPref;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;

public class BatterySaverSettings
  extends SettingsPreferenceFragment
  implements SwitchBar.OnSwitchChangeListener
{
  private static final boolean DEBUG = Log.isLoggable("BatterySaverSettings", 3);
  private static final String KEY_TURN_ON_AUTOMATICALLY = "turn_on_automatically";
  private static final String TAG = "BatterySaverSettings";
  private static final long WAIT_FOR_SWITCH_ANIM = 500L;
  private Context mContext;
  private boolean mCreated;
  private final Handler mHandler = new Handler();
  private PowerManager mPowerManager;
  private final Receiver mReceiver = new Receiver(null);
  private final SettingsObserver mSettingsObserver = new SettingsObserver(this.mHandler);
  private final Runnable mStartMode = new Runnable()
  {
    public void run()
    {
      AsyncTask.execute(new Runnable()
      {
        public void run()
        {
          if (BatterySaverSettings.-get0()) {
            Log.d("BatterySaverSettings", "Starting low power mode from settings");
          }
          BatterySaverSettings.-wrap1(BatterySaverSettings.this, true);
        }
      });
    }
  };
  private Switch mSwitch;
  private SwitchBar mSwitchBar;
  private SettingPref mTriggerPref;
  private final Runnable mUpdateSwitch = new Runnable()
  {
    public void run()
    {
      BatterySaverSettings.-wrap2(BatterySaverSettings.this);
    }
  };
  private boolean mValidListener;
  
  private void trySetPowerSaveMode(boolean paramBoolean)
  {
    if (!this.mPowerManager.setPowerSaveMode(paramBoolean))
    {
      if (DEBUG) {
        Log.d("BatterySaverSettings", "Setting mode failed, fallback to current value");
      }
      this.mHandler.post(this.mUpdateSwitch);
    }
    ((BatterySaverCondition)ConditionManager.get(getContext()).getCondition(BatterySaverCondition.class)).refreshState();
  }
  
  private void updateSwitch()
  {
    boolean bool = this.mPowerManager.isPowerSaveMode();
    if (DEBUG) {
      Log.d("BatterySaverSettings", "updateSwitch: isChecked=" + this.mSwitch.isChecked() + " mode=" + bool);
    }
    if (bool == this.mSwitch.isChecked()) {
      return;
    }
    if (this.mValidListener) {
      this.mSwitchBar.removeOnSwitchChangeListener(this);
    }
    this.mSwitch.setChecked(bool);
    if (this.mValidListener) {
      this.mSwitchBar.addOnSwitchChangeListener(this);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 52;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (this.mCreated)
    {
      this.mSwitchBar.show();
      return;
    }
    this.mCreated = true;
    addPreferencesFromResource(2131230740);
    this.mContext = getActivity();
    this.mSwitchBar = ((SettingsActivity)this.mContext).getSwitchBar();
    this.mSwitch = this.mSwitchBar.getSwitch();
    this.mSwitchBar.show();
    this.mTriggerPref = new SettingPref(1, "turn_on_automatically", "low_power_trigger_level", 0, getResources().getIntArray(2131427442))
    {
      protected String getCaption(Resources paramAnonymousResources, int paramAnonymousInt)
      {
        if ((paramAnonymousInt > 0) && (paramAnonymousInt < 100)) {
          return paramAnonymousResources.getString(2131692521, new Object[] { Utils.formatPercentage(paramAnonymousInt) });
        }
        return paramAnonymousResources.getString(2131692520);
      }
    };
    this.mTriggerPref.init(this);
    this.mPowerManager = ((PowerManager)this.mContext.getSystemService("power"));
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    this.mSwitchBar.hide();
  }
  
  public void onPause()
  {
    super.onPause();
    this.mSettingsObserver.setListening(false);
    this.mReceiver.setListening(false);
    if (this.mValidListener)
    {
      this.mSwitchBar.removeOnSwitchChangeListener(this);
      this.mValidListener = false;
    }
  }
  
  public void onResume()
  {
    super.onResume();
    this.mSettingsObserver.setListening(true);
    this.mReceiver.setListening(true);
    if (!this.mValidListener)
    {
      this.mSwitchBar.addOnSwitchChangeListener(this);
      this.mValidListener = true;
    }
    updateSwitch();
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    this.mHandler.removeCallbacks(this.mStartMode);
    if (paramBoolean)
    {
      this.mHandler.postDelayed(this.mStartMode, 500L);
      return;
    }
    if (DEBUG) {
      Log.d("BatterySaverSettings", "Stopping low power mode from settings");
    }
    trySetPowerSaveMode(false);
  }
  
  private final class Receiver
    extends BroadcastReceiver
  {
    private boolean mRegistered;
    
    private Receiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      boolean bool2 = false;
      if (BatterySaverSettings.-get0()) {
        Log.d("BatterySaverSettings", "Received " + paramIntent.getAction());
      }
      paramContext = paramIntent.getAction();
      if (paramContext.equals("android.os.action.POWER_SAVE_MODE_CHANGING")) {
        BatterySaverSettings.-get2(BatterySaverSettings.this).post(BatterySaverSettings.-get5(BatterySaverSettings.this));
      }
      while (!paramContext.equals("android.intent.action.BATTERY_CHANGED")) {
        return;
      }
      int i = paramIntent.getIntExtra("status", -1);
      paramContext = BatterySaverSettings.-get3(BatterySaverSettings.this);
      boolean bool1 = bool2;
      if (i != 2)
      {
        bool1 = bool2;
        if (i != 5) {
          bool1 = true;
        }
      }
      paramContext.setEnabled(bool1);
    }
    
    public void setListening(boolean paramBoolean)
    {
      if ((!paramBoolean) || (this.mRegistered))
      {
        if ((!paramBoolean) && (this.mRegistered))
        {
          BatterySaverSettings.-get1(BatterySaverSettings.this).unregisterReceiver(this);
          this.mRegistered = false;
        }
        return;
      }
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGING");
      localIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
      BatterySaverSettings.-get1(BatterySaverSettings.this).registerReceiver(this, localIntentFilter);
      this.mRegistered = true;
    }
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    private final Uri LOW_POWER_MODE_TRIGGER_LEVEL_URI = Settings.Global.getUriFor("low_power_trigger_level");
    
    public SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if (this.LOW_POWER_MODE_TRIGGER_LEVEL_URI.equals(paramUri)) {
        BatterySaverSettings.-get4(BatterySaverSettings.this).update(BatterySaverSettings.-get1(BatterySaverSettings.this));
      }
    }
    
    public void setListening(boolean paramBoolean)
    {
      ContentResolver localContentResolver = BatterySaverSettings.-wrap0(BatterySaverSettings.this);
      if (paramBoolean)
      {
        localContentResolver.registerContentObserver(this.LOW_POWER_MODE_TRIGGER_LEVEL_URI, false, this);
        return;
      }
      localContentResolver.unregisterContentObserver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\BatterySaverSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */