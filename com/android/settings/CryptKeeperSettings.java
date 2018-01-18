package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.internal.widget.LockPatternUtils;

public class CryptKeeperSettings
  extends InstrumentedFragment
{
  private static final int KEYGUARD_REQUEST = 55;
  private static final int MIN_BATTERY_LEVEL = 80;
  private static final String PASSWORD = "password";
  private static final String TAG = "CryptKeeper";
  private static final String TYPE = "type";
  private View mBatteryWarning;
  private View mContentView;
  private Button mInitiateButton;
  private View.OnClickListener mInitiateListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!CryptKeeperSettings.-wrap0(CryptKeeperSettings.this, 55)) {
        new AlertDialog.Builder(CryptKeeperSettings.this.getActivity()).setTitle(2131691125).setMessage(2131691126).setPositiveButton(17039370, null).create().show();
      }
    }
  };
  private IntentFilter mIntentFilter;
  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int k = 8;
      int j;
      boolean bool1;
      label66:
      boolean bool2;
      if (paramAnonymousIntent.getAction().equals("android.intent.action.BATTERY_CHANGED"))
      {
        i = paramAnonymousIntent.getIntExtra("level", 0);
        j = paramAnonymousIntent.getIntExtra("plugged", 0);
        int m = paramAnonymousIntent.getIntExtra("invalid_charger", 0);
        if (i < 80) {
          break label132;
        }
        i = 1;
        if ((j & 0x7) == 0) {
          break label143;
        }
        if (m != 0) {
          break label137;
        }
        bool1 = true;
        paramAnonymousContext = CryptKeeperSettings.-get1(CryptKeeperSettings.this);
        if (i == 0) {
          break label149;
        }
        bool2 = bool1;
        label82:
        paramAnonymousContext.setEnabled(bool2);
        paramAnonymousContext = CryptKeeperSettings.-get2(CryptKeeperSettings.this);
        if (!bool1) {
          break label155;
        }
        j = 8;
        label105:
        paramAnonymousContext.setVisibility(j);
        paramAnonymousContext = CryptKeeperSettings.-get0(CryptKeeperSettings.this);
        if (i == 0) {
          break label161;
        }
      }
      label132:
      label137:
      label143:
      label149:
      label155:
      label161:
      for (int i = k;; i = 0)
      {
        paramAnonymousContext.setVisibility(i);
        return;
        i = 0;
        break;
        bool1 = false;
        break label66;
        bool1 = false;
        break label66;
        bool2 = false;
        break label82;
        j = 0;
        break label105;
      }
    }
  };
  private View mPowerWarning;
  
  private void addEncryptionInfoToPreference(Preference paramPreference, int paramInt, String paramString)
  {
    if (((DevicePolicyManager)getActivity().getSystemService("device_policy")).getDoNotAskCredentialsOnBoot())
    {
      paramPreference.getExtras().putInt("type", 1);
      paramPreference.getExtras().putString("password", "");
      return;
    }
    paramPreference.getExtras().putInt("type", paramInt);
    paramPreference.getExtras().putString("password", paramString);
  }
  
  private boolean runKeyguardConfirmation(int paramInt)
  {
    Resources localResources = getActivity().getResources();
    ChooseLockSettingsHelper localChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity(), this);
    if (localChooseLockSettingsHelper.utils().getKeyguardStoredPasswordQuality(UserHandle.myUserId()) == 0)
    {
      showFinalConfirmation(1, "");
      return true;
    }
    return localChooseLockSettingsHelper.launchConfirmationActivity(paramInt, localResources.getText(2131691119), true);
  }
  
  private void showFinalConfirmation(int paramInt, String paramString)
  {
    Preference localPreference = new Preference(getPreferenceManager().getContext());
    localPreference.setFragment(CryptKeeperConfirm.class.getName());
    localPreference.setTitle(2131691127);
    addEncryptionInfoToPreference(localPreference, paramInt, paramString);
    ((SettingsActivity)getActivity()).onPreferenceStartFragment(null, localPreference);
  }
  
  protected int getMetricsCategory()
  {
    return 32;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    paramBundle = getActivity();
    if ("android.app.action.START_ENCRYPTION".equals(paramBundle.getIntent().getAction()))
    {
      DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramBundle.getSystemService("device_policy");
      if ((localDevicePolicyManager != null) && (localDevicePolicyManager.getStorageEncryptionStatus() != 1)) {
        paramBundle.finish();
      }
    }
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 != 55) {
      return;
    }
    if ((paramInt2 == -1) && (paramIntent != null))
    {
      paramInt1 = paramIntent.getIntExtra("type", -1);
      paramIntent = paramIntent.getStringExtra("password");
      if (!TextUtils.isEmpty(paramIntent)) {
        showFinalConfirmation(paramInt1, paramIntent);
      }
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mContentView = paramLayoutInflater.inflate(2130968663, null);
    this.mIntentFilter = new IntentFilter();
    this.mIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
    this.mInitiateButton = ((Button)this.mContentView.findViewById(2131362064));
    this.mInitiateButton.setOnClickListener(this.mInitiateListener);
    this.mInitiateButton.setEnabled(false);
    this.mPowerWarning = this.mContentView.findViewById(2131362063);
    this.mBatteryWarning = this.mContentView.findViewById(2131362062);
    return this.mContentView;
  }
  
  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mIntentReceiver);
  }
  
  public void onResume()
  {
    super.onResume();
    getActivity().registerReceiver(this.mIntentReceiver, this.mIntentFilter);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\CryptKeeperSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */