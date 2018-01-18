package com.android.settings.fingerprint;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.android.settings.SetupWizardUtils;

public class SetupFingerprintEnrollEnrolling
  extends FingerprintEnrollEnrolling
{
  private static final String TAG_DIALOG = "dialog";
  
  protected Intent getFinishIntent()
  {
    Intent localIntent = new Intent(this, SetupFingerprintEnrollFinish.class);
    SetupWizardUtils.copySetupExtras(getIntent(), localIntent);
    return localIntent;
  }
  
  protected int getMetricsCategory()
  {
    return 246;
  }
  
  protected void initViews()
  {
    super.initViews();
    Button localButton = (Button)findViewById(2131362143);
    localButton.setVisibility(0);
    localButton.setOnClickListener(this);
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getTheme(getIntent()), paramBoolean);
  }
  
  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    default: 
      super.onClick(paramView);
      return;
    }
    new SkipDialog().show(getFragmentManager(), "dialog");
  }
  
  protected boolean supportStatusBarTransparent()
  {
    return true;
  }
  
  public static class SkipDialog
    extends DialogFragment
  {
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      new AlertDialog.Builder(getActivity()).setTitle(2131691098).setMessage(2131691099).setCancelable(false).setPositiveButton(2131691456, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = SetupFingerprintEnrollEnrolling.SkipDialog.this.getActivity();
          if (paramAnonymousDialogInterface != null)
          {
            paramAnonymousDialogInterface.setResult(2);
            paramAnonymousDialogInterface.finish();
          }
        }
      }).setNegativeButton(2131691457, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
      }).create();
    }
    
    public void show(FragmentManager paramFragmentManager, String paramString)
    {
      if (paramFragmentManager.findFragmentByTag(paramString) == null) {
        super.show(paramFragmentManager, paramString);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fingerprint\SetupFingerprintEnrollEnrolling.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */