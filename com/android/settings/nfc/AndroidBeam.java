package com.android.settings.nfc;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import com.android.settings.InstrumentedFragment;
import com.android.settings.SettingsActivity;
import com.android.settings.ShowAdminSupportDetailsDialog;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;

public class AndroidBeam
  extends InstrumentedFragment
  implements SwitchBar.OnSwitchChangeListener
{
  private boolean mBeamDisallowedByBase;
  private boolean mBeamDisallowedByOnlyAdmin;
  private IntentFilter mIntentFilter = null;
  private NfcAdapter mNfcAdapter;
  private CharSequence mOldActivityTitle;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.nfc.action.ADAPTER_STATE_CHANGED".equals(paramAnonymousIntent.getAction())) {
        AndroidBeam.-wrap0(AndroidBeam.this, paramAnonymousIntent.getIntExtra("android.nfc.extra.ADAPTER_STATE", 1));
      }
    }
  };
  private SwitchBar mSwitchBar;
  private View mView;
  
  private void handleNfcStateChanged(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 1: 
      this.mSwitchBar.setEnabled(false);
      return;
    case 3: 
      this.mSwitchBar.setEnabled(true);
      return;
    case 2: 
      this.mSwitchBar.setEnabled(true);
      return;
    }
    this.mSwitchBar.setEnabled(false);
  }
  
  protected int getMetricsCategory()
  {
    return 69;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    boolean bool2 = false;
    super.onActivityCreated(paramBundle);
    paramBundle = (SettingsActivity)getActivity();
    this.mOldActivityTitle = paramBundle.getActionBar().getTitle();
    this.mSwitchBar = paramBundle.getSwitchBar();
    if (this.mBeamDisallowedByOnlyAdmin)
    {
      this.mSwitchBar.hide();
      return;
    }
    paramBundle = this.mSwitchBar;
    if (!this.mBeamDisallowedByBase)
    {
      bool1 = this.mNfcAdapter.isNdefPushEnabled();
      paramBundle.setChecked(bool1);
      this.mSwitchBar.addOnSwitchChangeListener(this);
      paramBundle = this.mSwitchBar;
      if (!this.mBeamDisallowedByBase) {
        break label114;
      }
    }
    label114:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      paramBundle.setEnabled(bool1);
      this.mSwitchBar.show();
      return;
      bool1 = false;
      break;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
    setHasOptionsMenu(true);
    this.mIntentFilter = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    HelpUtils.prepareHelpMenuItem(getActivity(), paramMenu, 2131693000, getClass().getName());
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramBundle = RestrictedLockUtils.checkIfRestrictionEnforced(getActivity(), "no_outgoing_beam", UserHandle.myUserId());
    UserManager.get(getActivity());
    this.mBeamDisallowedByBase = RestrictedLockUtils.hasBaseUserRestriction(getActivity(), "no_outgoing_beam", UserHandle.myUserId());
    if ((!this.mBeamDisallowedByBase) && (paramBundle != null))
    {
      paramLayoutInflater = paramLayoutInflater.inflate(2130968608, null);
      ShowAdminSupportDetailsDialog.setAdminSupportDetails(getActivity(), paramLayoutInflater, paramBundle, false);
      paramLayoutInflater.setVisibility(0);
      this.mBeamDisallowedByOnlyAdmin = true;
      return paramLayoutInflater;
    }
    this.mView = paramLayoutInflater.inflate(2130968609, paramViewGroup, false);
    return this.mView;
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    if (this.mOldActivityTitle != null) {
      getActivity().getActionBar().setTitle(this.mOldActivityTitle);
    }
    if (!this.mBeamDisallowedByOnlyAdmin)
    {
      this.mSwitchBar.removeOnSwitchChangeListener(this);
      this.mSwitchBar.hide();
    }
  }
  
  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mReceiver);
  }
  
  public void onResume()
  {
    super.onResume();
    getActivity().registerReceiver(this.mReceiver, this.mIntentFilter);
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    this.mSwitchBar.setEnabled(false);
    if (paramBoolean) {}
    for (boolean bool = this.mNfcAdapter.enableNdefPush();; bool = this.mNfcAdapter.disableNdefPush())
    {
      if (bool) {
        this.mSwitchBar.setChecked(paramBoolean);
      }
      this.mSwitchBar.setEnabled(true);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\nfc\AndroidBeam.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */