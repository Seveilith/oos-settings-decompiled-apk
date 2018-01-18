package com.android.settings.hydrogen.fingerprint;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.UserHandle;
import android.widget.Button;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.SetupChooseLockGeneric;
import com.android.settings.SetupWizardUtils;
import com.android.setupwizardlib.SetupWizardRecyclerLayout;
import com.android.setupwizardlib.items.Item;
import com.android.setupwizardlib.items.RecyclerItemAdapter;
import com.android.setupwizardlib.view.NavigationBar;
import com.android.setupwizardlib.view.NavigationBar.NavigationBarListener;
import com.oneplus.settings.utils.OPUtils;

public class SetupFingerprintEnrollIntroduction
  extends FingerprintEnrollIntroduction
  implements NavigationBar.NavigationBarListener
{
  public void finish()
  {
    super.finish();
    overridePendingTransition(2131034145, 2131034146);
  }
  
  protected Intent getChooseLockIntent()
  {
    Intent localIntent = new Intent(this, SetupChooseLockGeneric.class);
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
    Object localObject1 = getActionBar();
    if (localObject1 != null) {
      ((ActionBar)localObject1).setDisplayHomeAsUpEnabled(false);
    }
    localObject1 = (SetupWizardRecyclerLayout)findViewById(2131362138);
    Object localObject2 = (RecyclerItemAdapter)((SetupWizardRecyclerLayout)localObject1).getAdapter();
    ((Item)((RecyclerItemAdapter)localObject2).findItemById(2131362013)).setTitle(getText(2131691079));
    ((Item)((RecyclerItemAdapter)localObject2).findItemById(2131362012)).setTitle(getText(2131691078));
    SetupWizardUtils.setImmersiveMode(this);
    getNavigationBar().setNavigationBarListener(this);
    localObject2 = getNavigationBar().getNextButton();
    ((Button)localObject2).setText(null);
    ((Button)localObject2).setEnabled(false);
    ((SetupWizardRecyclerLayout)localObject1).setDividerInset(getResources().getDimensionPixelSize(2131755096));
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    localObject = paramIntent;
    if (paramInt1 == 2)
    {
      localIntent = paramIntent;
      if (paramIntent == null) {
        localIntent = new Intent();
      }
      localIntent.putExtra(":settings:password_quality", new LockPatternUtils(this).getKeyguardStoredPasswordQuality(UserHandle.myUserId()));
      localObject = localIntent;
      if (OPUtils.isO2()) {}
    }
    for (;;)
    {
      try
      {
        localObject = new Intent();
        if (!OPUtils.isGuestMode()) {
          continue;
        }
        paramIntent = new ComponentName("com.oneplus.provision", "com.oneplus.provision.UserSettingSuccess");
        ((Intent)localObject).setComponent(paramIntent);
        startActivity((Intent)localObject);
        overridePendingTransition(2131034147, 2131034148);
        finish();
        localObject = localIntent;
      }
      catch (ActivityNotFoundException paramIntent)
      {
        paramIntent.printStackTrace();
        finish();
        localObject = localIntent;
        continue;
      }
      super.onActivityResult(paramInt1, paramInt2, (Intent)localObject);
      return;
      paramIntent = new ComponentName("com.oneplus.provision", "com.oneplus.provision.GesturesActivity");
    }
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getSettingTheme(), paramBoolean);
  }
  
  protected void onCancelButtonClick()
  {
    SetupSkipDialog.newInstance(getIntent().getBooleanExtra(":settings:frp_supported", false)).show(getFragmentManager());
  }
  
  public void onNavigateBack()
  {
    finish();
  }
  
  public void onNavigateNext() {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\hydrogen\fingerprint\SetupFingerprintEnrollIntroduction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */