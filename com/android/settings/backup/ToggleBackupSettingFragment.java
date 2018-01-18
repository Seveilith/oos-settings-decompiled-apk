package com.android.settings.backup;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManager.Stub;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.ToggleSwitch;
import com.android.settings.widget.ToggleSwitch.OnBeforeCheckedChangeListener;

public class ToggleBackupSettingFragment
  extends SettingsPreferenceFragment
  implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener
{
  private static final String BACKUP_TOGGLE = "toggle_backup";
  private static final String TAG = "ToggleBackupSettingFragment";
  private static final String USER_FULL_DATA_BACKUP_AWARE = "user_full_data_backup_aware";
  private IBackupManager mBackupManager;
  private Dialog mConfirmDialog;
  private Preference mSummaryPreference;
  protected SwitchBar mSwitchBar;
  protected ToggleSwitch mToggleSwitch;
  private boolean mWaitingForConfirmationDialog = false;
  
  private void setBackupEnabled(boolean paramBoolean)
  {
    if (this.mBackupManager != null) {}
    try
    {
      this.mBackupManager.setBackupEnabled(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("ToggleBackupSettingFragment", "Error communicating with BackupManager", localRemoteException);
    }
  }
  
  private void showEraseBackupDialog()
  {
    if (Settings.Secure.getInt(getContentResolver(), "user_full_data_backup_aware", 0) != 0) {}
    for (CharSequence localCharSequence = getResources().getText(2131692636);; localCharSequence = getResources().getText(2131692635))
    {
      this.mWaitingForConfirmationDialog = true;
      this.mConfirmDialog = new AlertDialog.Builder(getActivity()).setMessage(localCharSequence).setTitle(2131692634).setPositiveButton(17039370, this).setNegativeButton(17039360, this).setOnDismissListener(this).show();
      return;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 81;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener()
    {
      public boolean onBeforeCheckedChanged(ToggleSwitch paramAnonymousToggleSwitch, boolean paramAnonymousBoolean)
      {
        if (!paramAnonymousBoolean)
        {
          ToggleBackupSettingFragment.-wrap1(ToggleBackupSettingFragment.this);
          return true;
        }
        ToggleBackupSettingFragment.-wrap0(ToggleBackupSettingFragment.this, true);
        ToggleBackupSettingFragment.this.mSwitchBar.setCheckedInternal(true);
        return true;
      }
    });
    this.mSwitchBar.show();
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -1)
    {
      this.mWaitingForConfirmationDialog = false;
      setBackupEnabled(false);
      this.mSwitchBar.setCheckedInternal(false);
    }
    while (paramInt != -2) {
      return;
    }
    this.mWaitingForConfirmationDialog = false;
    setBackupEnabled(true);
    this.mSwitchBar.setCheckedInternal(true);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    paramBundle = getPreferenceManager().createPreferenceScreen(getActivity());
    setPreferenceScreen(paramBundle);
    this.mSummaryPreference = new Preference(getPrefContext())
    {
      public void onBindViewHolder(PreferenceViewHolder paramAnonymousPreferenceViewHolder)
      {
        super.onBindViewHolder(paramAnonymousPreferenceViewHolder);
        ((TextView)paramAnonymousPreferenceViewHolder.findViewById(16908304)).setText(getSummary());
      }
    };
    this.mSummaryPreference.setPersistent(false);
    this.mSummaryPreference.setLayoutResource(2130969078);
    paramBundle.addPreference(this.mSummaryPreference);
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(null);
    this.mSwitchBar.hide();
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    if (this.mWaitingForConfirmationDialog)
    {
      setBackupEnabled(true);
      this.mSwitchBar.setCheckedInternal(true);
    }
  }
  
  public void onStop()
  {
    if ((this.mConfirmDialog != null) && (this.mConfirmDialog.isShowing())) {
      this.mConfirmDialog.dismiss();
    }
    this.mConfirmDialog = null;
    super.onStop();
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    this.mSwitchBar = ((SettingsActivity)getActivity()).getSwitchBar();
    this.mToggleSwitch = this.mSwitchBar.getSwitch();
    if (Settings.Secure.getInt(getContentResolver(), "user_full_data_backup_aware", 0) != 0) {
      this.mSummaryPreference.setSummary(2131692637);
    }
    for (;;)
    {
      try
      {
        if (this.mBackupManager != null) {
          continue;
        }
        bool = false;
        this.mSwitchBar.setCheckedInternal(bool);
      }
      catch (RemoteException paramView)
      {
        boolean bool;
        this.mSwitchBar.setEnabled(false);
        continue;
      }
      getActivity().setTitle(2131692626);
      return;
      this.mSummaryPreference.setSummary(2131692627);
      continue;
      bool = this.mBackupManager.isBackupEnabled();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\backup\ToggleBackupSettingFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */