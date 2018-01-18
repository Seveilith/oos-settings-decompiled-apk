package com.android.settings.hydrogen.fingerprint;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.android.setupwizardlib.util.SystemBarHelper;
import com.oneplus.settings.utils.OPUtils;

public class SetupSkipDialog
  extends DialogFragment
  implements DialogInterface.OnClickListener
{
  private static final String ARG_FRP_SUPPORTED = "frp_supported";
  public static final String EXTRA_FRP_SUPPORTED = ":settings:frp_supported";
  private static final int RESULT_SKIP = 11;
  private static final String TAG_SKIP_DIALOG = "skip_dialog";
  
  public static SetupSkipDialog newInstance(boolean paramBoolean)
  {
    SetupSkipDialog localSetupSkipDialog = new SetupSkipDialog();
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("frp_supported", paramBoolean);
    localSetupSkipDialog.setArguments(localBundle);
    return localSetupSkipDialog;
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    }
    Activity localActivity = getActivity();
    if (localActivity == null) {
      return;
    }
    if (OPUtils.isO2())
    {
      localActivity.setResult(11);
      localActivity.finish();
      return;
    }
    for (;;)
    {
      try
      {
        Intent localIntent = new Intent();
        if (OPUtils.isGuestMode())
        {
          paramDialogInterface = new ComponentName("com.oneplus.provision", "com.oneplus.provision.UserSettingSuccess");
          localIntent.setComponent(paramDialogInterface);
          localActivity.startActivity(localIntent);
          localActivity.overridePendingTransition(2131034147, 2131034148);
          return;
        }
      }
      catch (ActivityNotFoundException paramDialogInterface)
      {
        paramDialogInterface.printStackTrace();
        localActivity.finish();
        return;
      }
      paramDialogInterface = new ComponentName("com.oneplus.provision", "com.oneplus.provision.GesturesActivity");
    }
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    paramBundle = onCreateDialogBuilder().create();
    SystemBarHelper.hideSystemBars(paramBundle);
    return paramBundle;
  }
  
  @NonNull
  public AlertDialog.Builder onCreateDialogBuilder()
  {
    Bundle localBundle = getArguments();
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getContext()).setPositiveButton(2131691082, this).setNegativeButton(2131691083, this);
    if (localBundle.getBoolean("frp_supported")) {}
    for (int i = 2131691080;; i = 2131691081) {
      return localBuilder.setMessage(i);
    }
  }
  
  public void show(FragmentManager paramFragmentManager)
  {
    show(paramFragmentManager, "skip_dialog");
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\hydrogen\fingerprint\SetupSkipDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */