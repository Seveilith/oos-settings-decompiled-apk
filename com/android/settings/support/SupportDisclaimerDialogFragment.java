package com.android.settings.support;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.overlay.SupportFeatureProvider;

public final class SupportDisclaimerDialogFragment
  extends DialogFragment
  implements DialogInterface.OnClickListener
{
  private static final String EXTRA_ACCOUNT = "extra_account";
  private static final String EXTRA_TYPE = "extra_type";
  public static final String TAG = "SupportDisclaimerDialog";
  
  public static SupportDisclaimerDialogFragment newInstance(Account paramAccount, int paramInt)
  {
    SupportDisclaimerDialogFragment localSupportDisclaimerDialogFragment = new SupportDisclaimerDialogFragment();
    Bundle localBundle = new Bundle(2);
    localBundle.putParcelable("extra_account", paramAccount);
    localBundle.putInt("extra_type", paramInt);
    localSupportDisclaimerDialogFragment.setArguments(localBundle);
    return localSupportDisclaimerDialogFragment;
  }
  
  private static void stripUnderlines(Spannable paramSpannable)
  {
    int i = 0;
    URLSpan[] arrayOfURLSpan = (URLSpan[])paramSpannable.getSpans(0, paramSpannable.length(), URLSpan.class);
    int j = arrayOfURLSpan.length;
    while (i < j)
    {
      URLSpan localURLSpan = arrayOfURLSpan[i];
      int k = paramSpannable.getSpanStart(localURLSpan);
      int m = paramSpannable.getSpanEnd(localURLSpan);
      paramSpannable.removeSpan(localURLSpan);
      paramSpannable.setSpan(new NoUnderlineUrlSpan(localURLSpan.getURL()), k, m, 33);
      i += 1;
    }
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    super.onCancel(paramDialogInterface);
    MetricsLogger.action(getContext(), 483);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -2)
    {
      MetricsLogger.action(getContext(), 483);
      return;
    }
    paramDialogInterface = getActivity();
    Object localObject = (CheckBox)getDialog().findViewById(2131362582);
    SupportFeatureProvider localSupportFeatureProvider = FeatureFactory.getFactory(paramDialogInterface).getSupportFeatureProvider(paramDialogInterface);
    Context localContext = getContext();
    if (((CheckBox)localObject).isChecked()) {}
    for (boolean bool = false;; bool = true)
    {
      localSupportFeatureProvider.setShouldShowDisclaimerDialog(localContext, bool);
      localObject = getArguments();
      MetricsLogger.action(paramDialogInterface, 484);
      localSupportFeatureProvider.startSupport(getActivity(), (Account)((Bundle)localObject).getParcelable("extra_account"), ((Bundle)localObject).getInt("extra_type"));
      return;
    }
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    paramBundle = new AlertDialog.Builder(getActivity()).setTitle(2131693739).setPositiveButton(17039370, this).setNegativeButton(17039360, this);
    View localView = LayoutInflater.from(paramBundle.getContext()).inflate(2130969023, null);
    TextView localTextView = (TextView)localView.findViewById(2131362581);
    Activity localActivity = getActivity();
    localTextView.setText(FeatureFactory.getFactory(localActivity).getSupportFeatureProvider(localActivity).getDisclaimerStringResId());
    stripUnderlines((Spannable)localTextView.getText());
    return paramBundle.setView(localView).create();
  }
  
  public static class NoUnderlineUrlSpan
    extends URLSpan
  {
    public NoUnderlineUrlSpan(String paramString)
    {
      super();
    }
    
    public void updateDrawState(TextPaint paramTextPaint)
    {
      super.updateDrawState(paramTextPaint);
      paramTextPaint.setUnderlineText(false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\support\SupportDisclaimerDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */