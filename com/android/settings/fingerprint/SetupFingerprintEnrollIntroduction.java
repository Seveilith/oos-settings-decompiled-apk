package com.android.settings.fingerprint;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.os.UserHandle;
import android.widget.Button;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.OxygenSetupChooseLockGeneric;
import com.android.settings.SetupWizardUtils;
import com.oneplus.settings.utils.OPUtils;

public class SetupFingerprintEnrollIntroduction
  extends FingerprintEnrollIntroduction
{
  protected Intent getChooseLockIntent()
  {
    Intent localIntent = new Intent(this, OxygenSetupChooseLockGeneric.class);
    SetupWizardUtils.copySetupExtras(getIntent(), localIntent);
    return localIntent;
  }
  
  protected Intent getFindSensorIntent()
  {
    Intent localIntent = new Intent(this, SetupFingerprintEnrollFindSensor.class);
    SetupWizardUtils.copySetupExtras(getIntent(), localIntent);
    return localIntent;
  }
  
  protected int getMetricsCategory()
  {
    return 249;
  }
  
  protected void initViews()
  {
    super.initViews();
    Object localObject = (TextView)findViewById(2131362151);
    Button localButton = getNextButton();
    if (OPUtils.isSurportBackFingerprint(this))
    {
      setHeaderText(2131690535);
      ((TextView)localObject).setText(2131690536);
      localButton.setText(2131690540);
    }
    for (;;)
    {
      localObject = (Button)findViewById(2131362152);
      ((Button)localObject).setOnClickListener(this);
      ((Button)localObject).setText(2131691078);
      return;
      ((TextView)localObject).setText(2131691075);
      localButton.setText(2131691079);
    }
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.mFromSetup = true;
    Intent localIntent1;
    Intent localIntent2;
    if ((paramInt1 == 2) || ((paramInt1 == 4) && (paramInt2 == 1)))
    {
      localIntent1 = paramIntent;
      if (paramIntent == null) {
        localIntent1 = new Intent();
      }
      localIntent1.putExtra(":settings:password_quality", new LockPatternUtils(this).getKeyguardStoredPasswordQuality(UserHandle.myUserId()));
      localIntent2 = localIntent1;
      if (OPUtils.isO2()) {}
    }
    for (;;)
    {
      try
      {
        localIntent2 = new Intent();
        if (!OPUtils.isGuestMode()) {
          continue;
        }
        paramIntent = new ComponentName("com.oneplus.provision", "com.oneplus.provision.UserSettingSuccess");
        localIntent2.setComponent(paramIntent);
        startActivity(localIntent2);
        overridePendingTransition(2131034147, 2131034148);
        finish();
        localIntent2 = localIntent1;
      }
      catch (ActivityNotFoundException paramIntent)
      {
        paramIntent.printStackTrace();
        finish();
        localIntent2 = localIntent1;
        continue;
      }
      super.onActivityResult(paramInt1, paramInt2, localIntent2);
      return;
      paramIntent = new ComponentName("com.oneplus.provision", "com.oneplus.provision.GesturesActivity");
      continue;
      localIntent2 = paramIntent;
      if (paramInt1 == 4) {
        if (paramInt2 == 100)
        {
          launchFindSensor(this.mToken);
          localIntent2 = paramIntent;
        }
        else
        {
          localIntent2 = paramIntent;
          if (paramInt2 == 0)
          {
            finish();
            localIntent2 = paramIntent;
          }
        }
      }
    }
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getTheme(getIntent()), paramBoolean);
  }
  
  protected void onCancelButtonClick()
  {
    SetupSkipDialog.newInstance(getIntent().getBooleanExtra(":settings:frp_supported", false)).show(getFragmentManager());
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if ((!OPUtils.isSurportBackFingerprint(this)) || (OPUtils.isGuestMode())) {
      return;
    }
    setContentView(2130968801);
  }
  
  protected boolean supportStatusBarTransparent()
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fingerprint\SetupFingerprintEnrollIntroduction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */