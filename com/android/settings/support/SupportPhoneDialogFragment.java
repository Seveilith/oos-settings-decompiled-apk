package com.android.settings.support;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import java.util.List;
import java.util.Locale;

public final class SupportPhoneDialogFragment
  extends DialogFragment
  implements View.OnClickListener
{
  private static final String EXTRA_PHONE = "extra_phone";
  public static final String TAG = "SupportPhoneDialog";
  
  public static SupportPhoneDialogFragment newInstance(SupportPhone paramSupportPhone)
  {
    SupportPhoneDialogFragment localSupportPhoneDialogFragment = new SupportPhoneDialogFragment();
    Bundle localBundle = new Bundle(2);
    localBundle.putParcelable("extra_phone", paramSupportPhone);
    localSupportPhoneDialogFragment.setArguments(localBundle);
    return localSupportPhoneDialogFragment;
  }
  
  public void onClick(View paramView)
  {
    int i = 0;
    Object localObject = (SupportPhone)getArguments().getParcelable("extra_phone");
    paramView = getActivity();
    localObject = ((SupportPhone)localObject).getDialIntent();
    if (paramView.getPackageManager().queryIntentActivities((Intent)localObject, 0).isEmpty()) {}
    for (;;)
    {
      if (i != 0)
      {
        MetricsLogger.action(getActivity(), 487);
        getActivity().startActivity((Intent)localObject);
      }
      dismiss();
      return;
      i = 1;
    }
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    paramBundle = (SupportPhone)getArguments().getParcelable("extra_phone");
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity()).setTitle(2131693729);
    View localView1 = LayoutInflater.from(localBuilder.getContext()).inflate(2130969027, null);
    View localView2 = localView1.findViewById(2131362589);
    ((TextView)localView1.findViewById(2131362590)).setText(getContext().getString(2131693728, new Object[] { new Locale(paramBundle.language).getDisplayLanguage(), paramBundle.number }));
    localView2.setOnClickListener(this);
    return localBuilder.setView(localView1).create();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\support\SupportPhoneDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */