package com.android.settings.hydrogen.fingerprint;

import android.content.Intent;
import android.content.res.Resources.Theme;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.SetupWizardUtils;
import com.android.setupwizardlib.view.NavigationBar;
import com.android.setupwizardlib.view.NavigationBar.NavigationBarListener;

public class SetupFingerprintEnrollFinish
  extends FingerprintEnrollFinish
  implements NavigationBar.NavigationBarListener
{
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
    return 248;
  }
  
  protected Button getNextButton()
  {
    return getNavigationBar().getNextButton();
  }
  
  protected void initViews()
  {
    SetupWizardUtils.setImmersiveMode(this);
    Object localObject = findViewById(2131362013);
    if (localObject != null) {
      ((View)localObject).setVisibility(8);
    }
    localObject = getNavigationBar();
    ((NavigationBar)localObject).setNavigationBarListener(this);
    ((NavigationBar)localObject).getBackButton().setVisibility(8);
    ((TextView)findViewById(2131361987)).setText(2131691096);
    ((TextView)findViewById(2131362148)).setVisibility(0);
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


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\hydrogen\fingerprint\SetupFingerprintEnrollFinish.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */