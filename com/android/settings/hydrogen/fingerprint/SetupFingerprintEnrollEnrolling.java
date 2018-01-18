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
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.android.settings.SetupWizardUtils;
import com.android.setupwizardlib.util.SystemBarHelper;
import com.android.setupwizardlib.view.NavigationBar;
import com.android.setupwizardlib.view.NavigationBar.NavigationBarListener;
import com.oneplus.settings.utils.OPUtils;

public class SetupFingerprintEnrollEnrolling
  extends FingerprintEnrollEnrolling
  implements NavigationBar.NavigationBarListener
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
  
  protected Button getNextButton()
  {
    return getNavigationBar().getNextButton();
  }
  
  protected void initViews()
  {
    SetupWizardUtils.setImmersiveMode(this);
    Object localObject = findViewById(2131362444);
    if (localObject != null) {
      ((View)localObject).setVisibility(8);
    }
    localObject = getNavigationBar();
    if (localObject != null)
    {
      ((NavigationBar)localObject).setNavigationBarListener(this);
      ((NavigationBar)localObject).getNextButton().setText(2131690971);
      ((NavigationBar)localObject).getBackButton().setVisibility(8);
    }
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getSettingTheme(), paramBoolean);
  }
  
  public void onNavigateBack()
  {
    onBackPressed();
  }
  
  public void onNavigateNext()
  {
    new SkipDialog().show(getFragmentManager(), "dialog");
  }
  
  public static class SkipDialog
    extends DialogFragment
  {
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      paramBundle = new AlertDialog.Builder(getActivity()).setTitle(2131691098).setMessage(2131691099).setCancelable(false).setPositiveButton(2131691456, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          Activity localActivity = SetupFingerprintEnrollEnrolling.SkipDialog.this.getActivity();
          if (localActivity == null) {
            return;
          }
          if (OPUtils.isO2())
          {
            localActivity.setResult(2);
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
                paramAnonymousDialogInterface = new ComponentName("com.oneplus.provision", "com.oneplus.provision.UserSettingSuccess");
                localIntent.setComponent(paramAnonymousDialogInterface);
                localActivity.startActivity(localIntent);
                localActivity.overridePendingTransition(2131034147, 2131034148);
                return;
              }
            }
            catch (ActivityNotFoundException paramAnonymousDialogInterface)
            {
              paramAnonymousDialogInterface.printStackTrace();
              localActivity.finish();
              return;
            }
            paramAnonymousDialogInterface = new ComponentName("com.oneplus.provision", "com.oneplus.provision.GesturesActivity");
          }
        }
      }).setNegativeButton(2131691457, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
      }).create();
      SystemBarHelper.hideSystemBars(paramBundle);
      return paramBundle;
    }
    
    public void show(FragmentManager paramFragmentManager, String paramString)
    {
      if (paramFragmentManager.findFragmentByTag(paramString) == null) {
        super.show(paramFragmentManager, paramString);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\hydrogen\fingerprint\SetupFingerprintEnrollEnrolling.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */