package com.android.settings;

import android.app.ActivityManagerNative;
import android.app.AlertDialog.Builder;
import android.app.IActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Handler;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;

public class BugreportPreference
  extends CustomDialogPreference
{
  private static final int BUGREPORT_DELAY_SECONDS = 3;
  private static final String TAG = "BugreportPreference";
  private TextView mFullSummary;
  private CheckedTextView mFullTitle;
  private TextView mInteractiveSummary;
  private CheckedTextView mInteractiveTitle;
  
  public BugreportPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void takeBugreport(int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().requestBugReport(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BugreportPreference", "error taking bugreport (bugreportType=" + paramInt + ")", localRemoteException);
    }
  }
  
  protected void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -1)
    {
      paramDialogInterface = getContext();
      if (this.mFullTitle.isChecked())
      {
        Log.v("BugreportPreference", "Taking full bugreport right away");
        MetricsLogger.action(paramDialogInterface, 295);
        takeBugreport(0);
      }
    }
    else
    {
      return;
    }
    Log.v("BugreportPreference", "Taking interactive bugreport in 3s");
    MetricsLogger.action(paramDialogInterface, 294);
    String str = paramDialogInterface.getResources().getQuantityString(18087938, 3, new Object[] { Integer.valueOf(3) });
    Log.v("BugreportPreference", str);
    Toast.makeText(paramDialogInterface, str, 0).show();
    new Handler().postDelayed(new Runnable()
    {
      public void run()
      {
        BugreportPreference.-wrap0(BugreportPreference.this, 1);
      }
    }, 3000L);
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener)
  {
    super.onPrepareDialogBuilder(paramBuilder, paramOnClickListener);
    View localView = View.inflate(getContext(), 2130968634, null);
    this.mInteractiveTitle = ((CheckedTextView)localView.findViewById(2131362001));
    this.mInteractiveSummary = ((TextView)localView.findViewById(2131362002));
    this.mFullTitle = ((CheckedTextView)localView.findViewById(2131362003));
    this.mFullSummary = ((TextView)localView.findViewById(2131362004));
    View.OnClickListener local1 = new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((paramAnonymousView == BugreportPreference.-get1(BugreportPreference.this)) || (paramAnonymousView == BugreportPreference.-get0(BugreportPreference.this)))
        {
          BugreportPreference.-get3(BugreportPreference.this).setChecked(false);
          BugreportPreference.-get1(BugreportPreference.this).setChecked(true);
        }
        if ((paramAnonymousView == BugreportPreference.-get3(BugreportPreference.this)) || (paramAnonymousView == BugreportPreference.-get2(BugreportPreference.this)))
        {
          BugreportPreference.-get3(BugreportPreference.this).setChecked(true);
          BugreportPreference.-get1(BugreportPreference.this).setChecked(false);
        }
      }
    };
    this.mInteractiveTitle.setOnClickListener(local1);
    this.mFullTitle.setOnClickListener(local1);
    this.mInteractiveSummary.setOnClickListener(local1);
    this.mFullSummary.setOnClickListener(local1);
    paramBuilder.setPositiveButton(17040308, paramOnClickListener);
    paramBuilder.setView(localView);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\BugreportPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */