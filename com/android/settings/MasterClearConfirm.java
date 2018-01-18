package com.android.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.service.persistentdata.PersistentDataBlockManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.settingslib.RestrictedLockUtils;
import com.oneplus.settings.OPRebootWipeUserdata;
import com.oneplus.settings.utils.OPUtils;

public class MasterClearConfirm
  extends OptionsMenuFragment
{
  private View mContentView;
  private boolean mEraseSdCard;
  private View.OnClickListener mFinalClickListener = new View.OnClickListener()
  {
    private ProgressDialog getProgressDialog()
    {
      ProgressDialog localProgressDialog = new ProgressDialog(MasterClearConfirm.this.getActivity());
      localProgressDialog.setIndeterminate(true);
      localProgressDialog.setCancelable(false);
      localProgressDialog.setTitle(MasterClearConfirm.this.getActivity().getString(2131691907));
      localProgressDialog.setMessage(MasterClearConfirm.this.getActivity().getString(2131691908));
      return localProgressDialog;
    }
    
    public void onClick(final View paramAnonymousView)
    {
      if (Utils.isMonkeyRunning()) {
        return;
      }
      paramAnonymousView = (PersistentDataBlockManager)MasterClearConfirm.this.getActivity().getSystemService("persistent_data_block");
      if ((paramAnonymousView == null) || (paramAnonymousView.getOemUnlockEnabled())) {}
      while (!Utils.isDeviceProvisioned(MasterClearConfirm.this.getActivity()))
      {
        MasterClearConfirm.-wrap0(MasterClearConfirm.this);
        return;
      }
      new AsyncTask()
      {
        int mOldOrientation;
        ProgressDialog mProgressDialog;
        
        protected Void doInBackground(Void... paramAnonymous2VarArgs)
        {
          paramAnonymousView.wipe();
          return null;
        }
        
        protected void onPostExecute(Void paramAnonymous2Void)
        {
          this.mProgressDialog.hide();
          if (MasterClearConfirm.this.getActivity() != null)
          {
            MasterClearConfirm.this.getActivity().setRequestedOrientation(this.mOldOrientation);
            MasterClearConfirm.-wrap0(MasterClearConfirm.this);
          }
        }
        
        protected void onPreExecute()
        {
          this.mProgressDialog = MasterClearConfirm.1.-wrap0(MasterClearConfirm.1.this);
          this.mProgressDialog.show();
          this.mOldOrientation = MasterClearConfirm.this.getActivity().getRequestedOrientation();
          MasterClearConfirm.this.getActivity().setRequestedOrientation(14);
        }
      }.execute(new Void[0]);
    }
  };
  private String mPowerOnPsw;
  
  private void doMasterClear()
  {
    try
    {
      if (!this.mEraseSdCard) {
        break label87;
      }
      if (OPUtils.isSurportNoNeedPowerOnPassword(getActivity()))
      {
        OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "MasterClearConfirm", "--wipe_data", this.mPowerOnPsw);
        return;
      }
      if (checkIfNeedPasswordToPowerOn())
      {
        OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "MasterClearConfirm", "--wipe_data", this.mPowerOnPsw);
        return;
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      Log.d("MasterClearConfim", "bootCommand Reboot failed (no permissions?)");
      return;
    }
    OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "MasterClearConfirm", "--wipe_data", "");
    return;
    label87:
    if (OPUtils.isSurportNoNeedPowerOnPassword(getActivity()))
    {
      OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "OPMasterClearConfirm", "--delete_data", this.mPowerOnPsw);
      return;
    }
    if (checkIfNeedPasswordToPowerOn())
    {
      OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "OPMasterClearConfirm", "--delete_data", this.mPowerOnPsw);
      return;
    }
    OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "OPMasterClearConfirm", "--delete_data", "");
  }
  
  private void establishFinalConfirmationState()
  {
    this.mContentView.findViewById(2131362210).setOnClickListener(this.mFinalClickListener);
  }
  
  private void setAccessibilityTitle()
  {
    CharSequence localCharSequence = getActivity().getTitle();
    Object localObject = (TextView)this.mContentView.findViewById(2131362209);
    if (localObject != null)
    {
      localObject = localCharSequence + "," + ((TextView)localObject).getText();
      getActivity().setTitle(Utils.createAccessibleSequence(localCharSequence, (String)localObject));
    }
  }
  
  public boolean checkIfNeedPasswordToPowerOn()
  {
    return Settings.Global.getInt(getActivity().getContentResolver(), "require_password_to_decrypt", 0) == 1;
  }
  
  protected int getMetricsCategory()
  {
    return 67;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getArguments();
    if (paramBundle != null) {
      if (paramBundle == null) {
        break label41;
      }
    }
    label41:
    for (boolean bool = paramBundle.getBoolean("erase_sd");; bool = false)
    {
      this.mEraseSdCard = bool;
      this.mPowerOnPsw = paramBundle.getString("power_on_psw");
      return;
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramViewGroup = RestrictedLockUtils.checkIfRestrictionEnforced(getActivity(), "no_factory_reset", UserHandle.myUserId());
    if (RestrictedLockUtils.hasBaseUserRestriction(getActivity(), "no_factory_reset", UserHandle.myUserId())) {
      return paramLayoutInflater.inflate(2130968751, null);
    }
    if (paramViewGroup != null)
    {
      paramLayoutInflater = paramLayoutInflater.inflate(2130968608, null);
      ShowAdminSupportDetailsDialog.setAdminSupportDetails(getActivity(), paramLayoutInflater, paramViewGroup, false);
      paramLayoutInflater.setVisibility(0);
      return paramLayoutInflater;
    }
    this.mContentView = paramLayoutInflater.inflate(2130968750, null);
    establishFinalConfirmationState();
    setAccessibilityTitle();
    return this.mContentView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\MasterClearConfirm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */