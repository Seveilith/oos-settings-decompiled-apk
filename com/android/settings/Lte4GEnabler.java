package com.android.settings;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import com.android.internal.telephony.Phone;

public class Lte4GEnabler
{
  private static final int LTE_FULL = 1;
  private static final int LTE_TDD = 2;
  public static final String SETTING_PREF_NETWORK_BAND = "network_band_preferred";
  public static final String SETTING_PRE_NW_MODE_DEFAULT = "preferred_network_mode_default";
  private static final String TAG = "Lte4GEnabler";
  private static final Uri URI_PHONE_FEATURE = Uri.parse("content://com.qualcomm.qti.phonefeature.FEATURE_PROVIDER");
  private static MyHandler mHandler;
  private static final int sPhoneCount = TelephonyManager.getDefault().getPhoneCount();
  private AlertDialog mAlertDialog;
  private final Context mContext;
  private String[] mCtMccMncs;
  private boolean mDialogClicked = false;
  private CompoundButton.OnCheckedChangeListener mLte4GEnabledListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      Log.e("Lte4GEnabler", "arg1 = " + paramAnonymousBoolean);
      if (paramAnonymousBoolean)
      {
        Lte4GEnabler.-wrap0(Lte4GEnabler.this);
        return;
      }
      Lte4GEnabler.-wrap1(Lte4GEnabler.this);
    }
  };
  private SubscriptionManager mSubscriptionManager;
  private Switch mSwitch;
  
  public Lte4GEnabler(Context paramContext, Switch paramSwitch)
  {
    this.mContext = paramContext;
    this.mSwitch = paramSwitch;
    mHandler = new MyHandler(null);
    this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
    this.mCtMccMncs = paramContext.getResources().getStringArray(2131427448);
  }
  
  private Bundle callBinder(String paramString, Bundle paramBundle)
  {
    if (this.mContext.getContentResolver().acquireProvider(URI_PHONE_FEATURE) == null) {
      return null;
    }
    return this.mContext.getContentResolver().call(URI_PHONE_FEATURE, paramString, null, paramBundle);
  }
  
  private int getPreferredNetworkType(int paramInt)
  {
    int i = Phone.PREFERRED_NT_MODE;
    Object localObject = this.mSubscriptionManager;
    localObject = SubscriptionManager.getSubId(paramInt);
    if ((localObject != null) && (localObject.length > 0)) {
      paramInt = Settings.Global.getInt(this.mContext.getContentResolver(), "preferred_network_mode" + localObject[0], Phone.PREFERRED_NT_MODE);
    }
    for (;;)
    {
      Log.i("Lte4GEnabler", "get preferred network type=" + paramInt);
      return paramInt;
      try
      {
        paramInt = TelephonyManager.getIntAtIndex(this.mContext.getContentResolver(), "preferred_network_mode", paramInt);
      }
      catch (Settings.SettingNotFoundException localSettingNotFoundException)
      {
        Log.e("Lte4GEnabler", "getPreferredNetworkType: Could not find PREFERRED_NETWORK_MODE!!!");
        paramInt = i;
      }
    }
  }
  
  private int getProperNwMode(int paramInt)
  {
    boolean bool = isCtCard(paramInt);
    if (bool)
    {
      paramInt = 10;
      if (!bool) {
        break label38;
      }
    }
    label38:
    for (int i = 7;; i = 18)
    {
      if (!this.mSwitch.isChecked()) {
        return i;
      }
      return paramInt;
      paramInt = 20;
      break;
    }
    return i;
  }
  
  private boolean isCtCard(int paramInt)
  {
    Object localObject = this.mSubscriptionManager;
    localObject = SubscriptionManager.getSubId(paramInt);
    localObject = this.mSubscriptionManager.getActiveSubscriptionInfo(localObject[0]);
    if ((localObject != null) && (this.mCtMccMncs != null))
    {
      localObject = String.format("%03d%02d", new Object[] { Integer.valueOf(((SubscriptionInfo)localObject).getMcc()), Integer.valueOf(((SubscriptionInfo)localObject).getMnc()) });
      Log.i("Lte4GEnabler", "slot: " + paramInt + " mccMnc: " + (String)localObject);
      String[] arrayOfString = this.mCtMccMncs;
      int i = arrayOfString.length;
      paramInt = 0;
      while (paramInt < i)
      {
        if (((String)localObject).equals(arrayOfString[paramInt])) {
          return true;
        }
        paramInt += 1;
      }
    }
    return false;
  }
  
  private boolean isDdsSubInLteMode()
  {
    boolean bool2 = false;
    SubscriptionManager localSubscriptionManager = this.mSubscriptionManager;
    int i = SubscriptionManager.getDefaultDataSubscriptionId();
    localSubscriptionManager = this.mSubscriptionManager;
    i = SubscriptionManager.getSlotId(i);
    int j = getPreferredNetworkType(i);
    if ((j == 22) || (j == 20)) {}
    for (;;)
    {
      boolean bool1 = bool2;
      if (TelephonyManager.getDefault().getSimState(i) == 5) {
        bool1 = true;
      }
      do
      {
        Log.i("Lte4GEnabler", "isDdsSubInLteMode: " + bool1);
        return bool1;
        if ((j == 19) || (j == 17) || (j == 16) || (j == 12) || (j == 11) || (j == 10) || (j == 9)) {
          break;
        }
        bool1 = bool2;
      } while (j != 8);
    }
  }
  
  private boolean isPrefTDDDataOnly(int paramInt)
  {
    boolean bool = false;
    try
    {
      paramInt = TelephonyManager.getIntAtIndex(this.mContext.getContentResolver(), "network_band_preferred", paramInt);
      if (paramInt == 2) {
        bool = true;
      }
      return bool;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException) {}
    return false;
  }
  
  private void promptUser()
  {
    this.mAlertDialog = new AlertDialog.Builder(this.mContext).setMessage(this.mContext.getString(2131693771)).setNeutralButton(2131690772, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
    }).setPositiveButton(2131690771, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        Lte4GEnabler.-wrap1(Lte4GEnabler.this);
        Lte4GEnabler.-set0(Lte4GEnabler.this, true);
      }
    }).create();
    this.mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        if (!Lte4GEnabler.-get0(Lte4GEnabler.this)) {
          Lte4GEnabler.-get1(Lte4GEnabler.this).setChecked(false);
        }
      }
    });
    if (!this.mAlertDialog.isShowing()) {
      this.mAlertDialog.show();
    }
  }
  
  private void setPrefNetwork()
  {
    this.mSwitch.setEnabled(false);
    Log.i("Lte4GEnabler", "isChecked: " + this.mSwitch.isChecked());
    int i;
    int j;
    if (this.mSwitch.isChecked())
    {
      SubscriptionManager localSubscriptionManager = this.mSubscriptionManager;
      localSubscriptionManager = this.mSubscriptionManager;
      i = SubscriptionManager.getSlotId(SubscriptionManager.getDefaultDataSubscriptionId());
      j = getProperNwMode(i);
      storeNwModeIntoDb(i, j);
      if (isPrefTDDDataOnly(i)) {
        setPrefNetwork(i, 11, 2);
      }
    }
    for (;;)
    {
      return;
      setPrefNetwork(i, j);
      return;
      i = 0;
      while (i < sPhoneCount)
      {
        j = getProperNwMode(i);
        storeNwModeIntoDb(i, j);
        setPrefNetwork(i, j);
        i += 1;
      }
    }
  }
  
  private void setPrefNetwork(int paramInt1, int paramInt2)
  {
    Object localObject = new Messenger(mHandler);
    Message localMessage = mHandler.obtainMessage(0);
    localMessage.replyTo = ((Messenger)localObject);
    localObject = new Bundle();
    ((Bundle)localObject).putInt("slot", paramInt1);
    ((Bundle)localObject).putInt("network", paramInt2);
    ((Bundle)localObject).putParcelable("callback", localMessage);
    callBinder("set_pref_network", (Bundle)localObject);
  }
  
  private void setSwitchStatus()
  {
    boolean bool2 = false;
    boolean bool3 = isDdsSubInLteMode();
    boolean bool1 = bool3;
    if (this.mAlertDialog != null)
    {
      bool1 = bool3;
      if (this.mAlertDialog.isShowing()) {
        bool1 = true;
      }
    }
    this.mSwitch.setChecked(bool1);
    Switch localSwitch = this.mSwitch;
    bool1 = bool2;
    if (Settings.System.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 0) {
      bool1 = isThereSimReady();
    }
    localSwitch.setEnabled(bool1);
  }
  
  private void storeNwModeIntoDb(int paramInt1, int paramInt2)
  {
    TelephonyManager.putIntAtIndex(this.mContext.getContentResolver(), "preferred_network_mode_default", paramInt1, paramInt2);
    Object localObject = this.mSubscriptionManager;
    localObject = SubscriptionManager.getSubId(paramInt1);
    if (localObject != null) {
      Settings.Global.putInt(this.mContext.getContentResolver(), "preferred_network_mode" + localObject[0], paramInt2);
    }
  }
  
  public boolean isThereSimReady()
  {
    boolean bool2 = false;
    int i = 0;
    if (i < sPhoneCount)
    {
      if (TelephonyManager.getDefault().getSimState(i) == 5) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        bool2 |= bool1;
        i += 1;
        break;
      }
    }
    Log.i("Lte4GEnabler", "isThereSimReady: " + bool2);
    return bool2;
  }
  
  public void pause()
  {
    this.mSwitch.setOnCheckedChangeListener(null);
  }
  
  public void resume()
  {
    setSwitchStatus();
    this.mSwitch.setOnCheckedChangeListener(this.mLte4GEnabledListener);
  }
  
  public void setPrefNetwork(int paramInt1, int paramInt2, int paramInt3)
  {
    Message localMessage = mHandler.obtainMessage(0);
    if (localMessage != null) {
      localMessage.replyTo = new Messenger(localMessage.getTarget());
    }
    Bundle localBundle = new Bundle();
    localBundle.putInt("slot", paramInt1);
    localBundle.putInt("network", paramInt2);
    localBundle.putInt("band", paramInt3);
    localBundle.putParcelable("callback", localMessage);
    callBinder("set_pref_network", localBundle);
  }
  
  public void setSwitch(Switch paramSwitch)
  {
    if (this.mSwitch == paramSwitch) {
      return;
    }
    this.mSwitch = paramSwitch;
    setSwitchStatus();
    this.mSwitch.setOnCheckedChangeListener(this.mLte4GEnabledListener);
  }
  
  private class MyHandler
    extends Handler
  {
    static final int MESSAGE_SET_PREFERRED_NETWORK_TYPE = 0;
    
    private MyHandler() {}
    
    private void handleSetPreferredNetworkTypeResponse(Message paramMessage)
    {
      Lte4GEnabler.-set0(Lte4GEnabler.this, false);
      Log.i("Lte4GEnabler", "handleSetPreferredNetworkTypeResponse");
      Lte4GEnabler.-wrap2(Lte4GEnabler.this);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      handleSetPreferredNetworkTypeResponse(paramMessage);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\Lte4GEnabler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */