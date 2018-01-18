package com.android.settings.wifi;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.text.Editable;
import android.widget.EditText;
import com.android.settings.SettingsPreferenceFragment;

public class WifiAPITest
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener
{
  private static final String KEY_DISABLE_NETWORK = "disable_network";
  private static final String KEY_DISCONNECT = "disconnect";
  private static final String KEY_ENABLE_NETWORK = "enable_network";
  private static final String TAG = "WifiAPITest";
  private Preference mWifiDisableNetwork;
  private Preference mWifiDisconnect;
  private Preference mWifiEnableNetwork;
  private WifiManager mWifiManager;
  private int netid;
  
  protected int getMetricsCategory()
  {
    return 89;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    addPreferencesFromResource(2130969112);
    paramBundle = getPreferenceScreen();
    this.mWifiDisconnect = paramBundle.findPreference("disconnect");
    this.mWifiDisconnect.setOnPreferenceClickListener(this);
    this.mWifiDisableNetwork = paramBundle.findPreference("disable_network");
    this.mWifiDisableNetwork.setOnPreferenceClickListener(this);
    this.mWifiEnableNetwork = paramBundle.findPreference("enable_network");
    this.mWifiEnableNetwork.setOnPreferenceClickListener(this);
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mWifiDisconnect) {
      this.mWifiManager.disconnect();
    }
    for (;;)
    {
      return true;
      final EditText localEditText;
      if (paramPreference == this.mWifiDisableNetwork)
      {
        paramPreference = new AlertDialog.Builder(getContext());
        paramPreference.setTitle("Input");
        paramPreference.setMessage("Enter Network ID");
        localEditText = new EditText(getPrefContext());
        paramPreference.setView(localEditText);
        paramPreference.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            paramAnonymousDialogInterface = localEditText.getText();
            try
            {
              WifiAPITest.-set0(WifiAPITest.this, Integer.parseInt(paramAnonymousDialogInterface.toString()));
              WifiAPITest.-get0(WifiAPITest.this).disableNetwork(WifiAPITest.-get1(WifiAPITest.this));
              return;
            }
            catch (NumberFormatException paramAnonymousDialogInterface)
            {
              paramAnonymousDialogInterface.printStackTrace();
            }
          }
        });
        paramPreference.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
        });
        paramPreference.show();
      }
      else if (paramPreference == this.mWifiEnableNetwork)
      {
        paramPreference = new AlertDialog.Builder(getContext());
        paramPreference.setTitle("Input");
        paramPreference.setMessage("Enter Network ID");
        localEditText = new EditText(getPrefContext());
        paramPreference.setView(localEditText);
        paramPreference.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            paramAnonymousDialogInterface = localEditText.getText();
            WifiAPITest.-set0(WifiAPITest.this, Integer.parseInt(paramAnonymousDialogInterface.toString()));
            WifiAPITest.-get0(WifiAPITest.this).enableNetwork(WifiAPITest.-get1(WifiAPITest.this), false);
          }
        });
        paramPreference.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
        });
        paramPreference.show();
      }
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    super.onPreferenceTreeClick(paramPreference);
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiAPITest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */