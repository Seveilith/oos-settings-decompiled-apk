package com.android.settings.hydrogen.fingerprint;

import android.content.Intent;
import android.content.res.Resources.Theme;
import android.view.View;
import android.widget.Button;
import com.android.settings.SetupWizardUtils;
import com.android.setupwizardlib.view.NavigationBar;
import com.android.setupwizardlib.view.NavigationBar.NavigationBarListener;

public class SetupFingerprintEnrollFindSensor
  extends FingerprintEnrollFindSensor
  implements NavigationBar.NavigationBarListener
{
  protected int getContentView()
  {
    return 2130968727;
  }
  
  protected Intent getEnrollingIntent()
  {
    Intent localIntent = new Intent(this, FingerprintEnrollEnrolling.class);
    localIntent.putExtra("hw_auth_token", this.mToken);
    if (this.mUserId != 55536) {
      localIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
    }
    SetupWizardUtils.copySetupExtras(getIntent(), localIntent);
    return localIntent;
  }
  
  protected int getMetricsCategory()
  {
    return 247;
  }
  
  protected Button getNextButton()
  {
    return getNavigationBar().getNextButton();
  }
  
  protected void initViews()
  {
    SetupWizardUtils.setImmersiveMode(this);
    View localView = findViewById(2131362013);
    if (localView != null) {
      localView.setVisibility(8);
    }
    getNavigationBar().setNavigationBarListener(this);
    getNavigationBar().getBackButton().setVisibility(8);
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
    onNextButtonClick();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\hydrogen\fingerprint\SetupFingerprintEnrollFindSensor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */