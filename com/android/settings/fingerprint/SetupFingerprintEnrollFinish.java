package com.android.settings.fingerprint;

import android.content.Intent;
import android.content.res.Resources.Theme;
import android.widget.TextView;
import com.android.settings.SetupWizardUtils;

public class SetupFingerprintEnrollFinish
  extends FingerprintEnrollFinish
{
  protected Intent getEnrollingIntent()
  {
    Intent localIntent = new Intent(this, SetupFingerprintEnrollEnrolling.class);
    localIntent.putExtra("hw_auth_token", this.mToken);
    if (this.mUserId != 55536) {
      localIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
    }
    SetupWizardUtils.copySetupExtras(getIntent(), localIntent);
    return localIntent;
  }
  
  protected int getMetricsCategory()
  {
    return 248;
  }
  
  protected void initViews()
  {
    super.initViews();
    ((TextView)findViewById(2131361987)).setText(2131691096);
    ((TextView)findViewById(2131362148)).setVisibility(0);
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getTheme(getIntent()), paramBoolean);
  }
  
  protected boolean supportStatusBarTransparent()
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fingerprint\SetupFingerprintEnrollFinish.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */