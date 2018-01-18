package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import com.android.ims.ImsManager;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;
import com.android.settings.widget.ToggleSwitch;

public class WifiCallingSettings
  extends SettingsPreferenceFragment
  implements SwitchBar.OnSwitchChangeListener, Preference.OnPreferenceChangeListener
{
  private static final String BUTTON_WFC_MODE = "wifi_calling_mode";
  private static final String TAG = "WifiCallingSettings";
  private ListPreference mButtonWfcMode;
  private boolean mEditableWfcMode = true;
  private TextView mEmptyView;
  private IntentFilter mIntentFilter;
  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("com.android.ims.REGISTRATION_ERROR"))
      {
        setResultCode(0);
        WifiCallingSettings.-get0(WifiCallingSettings.this).setChecked(false);
        WifiCallingSettings.-wrap0(WifiCallingSettings.this, paramAnonymousIntent);
      }
    }
  };
  private final PhoneStateListener mPhoneStateListener = new PhoneStateListener()
  {
    public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString)
    {
      boolean bool3 = false;
      paramAnonymousString = (SettingsActivity)WifiCallingSettings.this.getActivity();
      boolean bool2 = ImsManager.isNonTtyOrTtyOnVolteEnabled(paramAnonymousString);
      paramAnonymousString = paramAnonymousString.getSwitchBar();
      boolean bool1;
      if (paramAnonymousString.getSwitch().isChecked())
      {
        bool1 = bool2;
        if (paramAnonymousInt != 0) {
          break label96;
        }
      }
      for (;;)
      {
        paramAnonymousString.setEnabled(bool2);
        paramAnonymousString = WifiCallingSettings.this.getPreferenceScreen().findPreference("wifi_calling_mode");
        if (paramAnonymousString != null)
        {
          bool2 = bool3;
          if (bool1)
          {
            bool2 = bool3;
            if (paramAnonymousInt == 0) {
              bool2 = true;
            }
          }
          paramAnonymousString.setEnabled(bool2);
        }
        return;
        bool1 = false;
        break;
        label96:
        bool2 = false;
      }
    }
  };
  private Switch mSwitch;
  private SwitchBar mSwitchBar;
  private boolean mValidListener = false;
  
  static int getWfcModeSummary(Context paramContext, int paramInt)
  {
    if (ImsManager.isWfcEnabledByUser(paramContext)) {}
    switch (paramInt)
    {
    default: 
      Log.e("WifiCallingSettings", "Unexpected WFC mode value: " + paramInt);
      return 17039610;
    case 0: 
      return 17039613;
    case 1: 
      return 17039612;
    }
    return 17039611;
  }
  
  private void showAlert(Intent paramIntent)
  {
    Object localObject = getActivity();
    CharSequence localCharSequence = paramIntent.getCharSequenceExtra("alertTitle");
    paramIntent = paramIntent.getCharSequenceExtra("alertMessage");
    localObject = new AlertDialog.Builder((Context)localObject);
    ((AlertDialog.Builder)localObject).setMessage(paramIntent).setTitle(localCharSequence).setIcon(17301543).setPositiveButton(17039370, null);
    ((AlertDialog.Builder)localObject).create().show();
  }
  
  private void updateButtonWfcMode(Context paramContext, boolean paramBoolean, int paramInt)
  {
    this.mButtonWfcMode.setSummary(getWfcModeSummary(paramContext, paramInt));
    this.mButtonWfcMode.setEnabled(paramBoolean);
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if ((paramBoolean) && (ImsManager.displayWfcMode(paramContext, false))) {
      localPreferenceScreen.addPreference(this.mButtonWfcMode);
    }
    for (;;)
    {
      localPreferenceScreen.setEnabled(this.mEditableWfcMode);
      return;
      localPreferenceScreen.removePreference(this.mButtonWfcMode);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 105;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mSwitchBar = ((SettingsActivity)getActivity()).getSwitchBar();
    this.mSwitch = this.mSwitchBar.getSwitch();
    this.mSwitchBar.show();
    this.mEmptyView = ((TextView)getView().findViewById(16908292));
    setEmptyView(this.mEmptyView);
    this.mEmptyView.setText(2131691543);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230885);
    this.mButtonWfcMode = ((ListPreference)findPreference("wifi_calling_mode"));
    this.mButtonWfcMode.setOnPreferenceChangeListener(this);
    this.mIntentFilter = new IntentFilter();
    this.mIntentFilter.addAction("com.android.ims.REGISTRATION_ERROR");
    paramBundle = (CarrierConfigManager)getSystemService("carrier_config");
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (paramBundle != null)
    {
      paramBundle = paramBundle.getConfig();
      bool1 = bool2;
      if (paramBundle != null)
      {
        this.mEditableWfcMode = paramBundle.getBoolean("editable_wfc_mode_bool");
        bool1 = paramBundle.getBoolean("carrier_wfc_supports_wifi_only_bool", true);
      }
    }
    if (!bool1)
    {
      this.mButtonWfcMode.setEntries(2131427507);
      this.mButtonWfcMode.setEntryValues(2131427509);
    }
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    this.mSwitchBar.hide();
  }
  
  public void onPause()
  {
    super.onPause();
    Activity localActivity = getActivity();
    if (this.mValidListener)
    {
      this.mValidListener = false;
      ((TelephonyManager)getSystemService("phone")).listen(this.mPhoneStateListener, 0);
      this.mSwitchBar.removeOnSwitchChangeListener(this);
    }
    localActivity.unregisterReceiver(this.mIntentReceiver);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    Activity localActivity = getActivity();
    if (paramPreference == this.mButtonWfcMode)
    {
      this.mButtonWfcMode.setValue((String)paramObject);
      int i = Integer.valueOf((String)paramObject).intValue();
      if (i != ImsManager.getWfcMode(localActivity))
      {
        ImsManager.setWfcMode(localActivity, i);
        this.mButtonWfcMode.setSummary(getWfcModeSummary(localActivity, i));
        MetricsLogger.action(getActivity(), getMetricsCategory(), i);
      }
    }
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    Object localObject = getActivity();
    if (ImsManager.isWfcEnabledByPlatform((Context)localObject))
    {
      ((TelephonyManager)getSystemService("phone")).listen(this.mPhoneStateListener, 32);
      this.mSwitchBar.addOnSwitchChangeListener(this);
      this.mValidListener = true;
    }
    if (ImsManager.isWfcEnabledByUser((Context)localObject)) {}
    for (boolean bool = ImsManager.isNonTtyOrTtyOnVolteEnabled((Context)localObject);; bool = false)
    {
      this.mSwitch.setChecked(bool);
      int i = ImsManager.getWfcMode((Context)localObject);
      this.mButtonWfcMode.setValue(Integer.toString(i));
      updateButtonWfcMode((Context)localObject, bool, i);
      ((Context)localObject).registerReceiver(this.mIntentReceiver, this.mIntentFilter);
      localObject = getActivity().getIntent();
      if (((Intent)localObject).getBooleanExtra("alertShow", false)) {
        showAlert((Intent)localObject);
      }
      return;
    }
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    paramSwitch = getActivity();
    ImsManager.setWfcSetting(paramSwitch, paramBoolean);
    int i = ImsManager.getWfcMode(paramSwitch);
    updateButtonWfcMode(paramSwitch, paramBoolean, i);
    if (paramBoolean)
    {
      MetricsLogger.action(getActivity(), getMetricsCategory(), i);
      return;
    }
    MetricsLogger.action(getActivity(), getMetricsCategory(), -1);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\WifiCallingSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */