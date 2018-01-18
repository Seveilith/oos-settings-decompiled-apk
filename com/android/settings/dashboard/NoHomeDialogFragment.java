package com.android.settings.dashboard;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class NoHomeDialogFragment
  extends DialogFragment
{
  public static void show(Activity paramActivity)
  {
    new NoHomeDialogFragment().show(paramActivity.getFragmentManager(), null);
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    return new AlertDialog.Builder(getActivity()).setMessage(2131692972).setPositiveButton(17039370, null).create();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\NoHomeDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */