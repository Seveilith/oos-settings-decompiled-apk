package com.android.settings.nfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.UserHandle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.utils.OPUtils;

public class NfcEnabler
  implements Preference.OnPreferenceChangeListener
{
  private static final String KEY_NFC_SWITCH = "NFC_SWITCH";
  private final RestrictedPreference mAndroidBeam;
  private boolean mBeamDisallowedBySystem;
  private final Context mContext;
  private final IntentFilter mIntentFilter;
  private final NfcAdapter mNfcAdapter;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.nfc.action.ADAPTER_STATE_CHANGED".equals(paramAnonymousIntent.getAction())) {
        NfcEnabler.-wrap0(NfcEnabler.this, paramAnonymousIntent.getIntExtra("android.nfc.extra.ADAPTER_STATE", 1));
      }
    }
  };
  private final SwitchPreference mSwitch;
  
  public NfcEnabler(Context paramContext, SwitchPreference paramSwitchPreference, RestrictedPreference paramRestrictedPreference)
  {
    this.mContext = paramContext;
    this.mSwitch = paramSwitchPreference;
    this.mAndroidBeam = paramRestrictedPreference;
    this.mNfcAdapter = NfcAdapter.getDefaultAdapter(paramContext);
    this.mBeamDisallowedBySystem = RestrictedLockUtils.hasBaseUserRestriction(paramContext, "no_outgoing_beam", UserHandle.myUserId());
    if (this.mNfcAdapter == null)
    {
      this.mSwitch.setEnabled(false);
      this.mAndroidBeam.setEnabled(false);
      this.mIntentFilter = null;
      return;
    }
    if (this.mBeamDisallowedBySystem) {
      this.mAndroidBeam.setEnabled(false);
    }
    this.mIntentFilter = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");
  }
  
  private void handleNfcStateChanged(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 1: 
      this.mSwitch.setChecked(false);
      this.mSwitch.setEnabled(true);
      this.mAndroidBeam.setEnabled(false);
      this.mAndroidBeam.setSummary(2131691327);
      return;
    case 3: 
      this.mSwitch.setChecked(true);
      this.mSwitch.setEnabled(true);
      if (this.mBeamDisallowedBySystem)
      {
        this.mAndroidBeam.setDisabledByAdmin(null);
        this.mAndroidBeam.setEnabled(false);
      }
      while ((this.mNfcAdapter.isNdefPushEnabled()) && (this.mAndroidBeam.isEnabled()))
      {
        this.mAndroidBeam.setSummary(2131691325);
        return;
        this.mAndroidBeam.checkRestrictionAndSetDisabled("no_outgoing_beam");
      }
      this.mAndroidBeam.setSummary(2131691326);
      return;
    case 2: 
      this.mSwitch.setChecked(true);
      this.mSwitch.setEnabled(false);
      this.mAndroidBeam.setEnabled(false);
      return;
    }
    this.mSwitch.setChecked(false);
    this.mSwitch.setEnabled(false);
    this.mAndroidBeam.setEnabled(false);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    boolean bool = ((Boolean)paramObject).booleanValue();
    this.mSwitch.setEnabled(false);
    OPUtils.sendAppTracker("NFC_SWITCH", bool);
    if (bool)
    {
      this.mNfcAdapter.enable();
      return false;
    }
    this.mNfcAdapter.disable();
    return false;
  }
  
  public void pause()
  {
    if (this.mNfcAdapter == null) {
      return;
    }
    this.mContext.unregisterReceiver(this.mReceiver);
    this.mSwitch.setOnPreferenceChangeListener(null);
  }
  
  public void resume()
  {
    if (this.mNfcAdapter == null) {
      return;
    }
    handleNfcStateChanged(this.mNfcAdapter.getAdapterState());
    this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    this.mSwitch.setOnPreferenceChangeListener(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\nfc\NfcEnabler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */