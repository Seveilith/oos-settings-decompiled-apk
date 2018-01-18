package com.android.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.telephony.PhoneStateIntentReceiver;
import com.android.settingslib.WirelessUtils;

public class AirplaneModeEnabler
  implements Preference.OnPreferenceChangeListener
{
  private static final int EVENT_SERVICE_STATE_CHANGED = 3;
  private ContentObserver mAirplaneModeObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      AirplaneModeEnabler.-wrap0(AirplaneModeEnabler.this);
    }
  };
  private final Context mContext;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      AirplaneModeEnabler.-wrap0(AirplaneModeEnabler.this);
    }
  };
  private PhoneStateIntentReceiver mPhoneStateReceiver;
  private final SwitchPreference mSwitchPref;
  
  public AirplaneModeEnabler(Context paramContext, SwitchPreference paramSwitchPreference)
  {
    this.mContext = paramContext;
    this.mSwitchPref = paramSwitchPreference;
    paramSwitchPreference.setPersistent(false);
    this.mPhoneStateReceiver = new PhoneStateIntentReceiver(this.mContext, this.mHandler);
    this.mPhoneStateReceiver.notifyServiceState(3);
  }
  
  private void onAirplaneModeChanged()
  {
    this.mSwitchPref.setChecked(WirelessUtils.isAirplaneModeOn(this.mContext));
  }
  
  private void setAirplaneModeOn(boolean paramBoolean)
  {
    Object localObject = this.mContext.getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt((ContentResolver)localObject, "airplane_mode_on", i);
      this.mSwitchPref.setChecked(paramBoolean);
      localObject = new Intent("android.intent.action.AIRPLANE_MODE");
      ((Intent)localObject).putExtra("state", paramBoolean);
      this.mContext.sendBroadcastAsUser((Intent)localObject, UserHandle.ALL);
      return;
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (Boolean.parseBoolean(SystemProperties.get("ril.cdma.inecmmode"))) {}
    for (;;)
    {
      return true;
      paramPreference = (Boolean)paramObject;
      MetricsLogger.action(this.mContext, 177, paramPreference.booleanValue());
      setAirplaneModeOn(paramPreference.booleanValue());
    }
  }
  
  public void pause()
  {
    this.mPhoneStateReceiver.unregisterIntent();
    this.mSwitchPref.setOnPreferenceChangeListener(null);
    this.mContext.getContentResolver().unregisterContentObserver(this.mAirplaneModeObserver);
  }
  
  public void resume()
  {
    this.mSwitchPref.setChecked(WirelessUtils.isAirplaneModeOn(this.mContext));
    this.mPhoneStateReceiver.registerIntent();
    this.mSwitchPref.setOnPreferenceChangeListener(this);
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("airplane_mode_on"), true, this.mAirplaneModeObserver);
  }
  
  public void setAirplaneModeInECM(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      setAirplaneModeOn(paramBoolean2);
      return;
    }
    onAirplaneModeChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\AirplaneModeEnabler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */