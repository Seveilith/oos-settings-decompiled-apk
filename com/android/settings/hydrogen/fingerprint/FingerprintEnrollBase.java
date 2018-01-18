package com.android.settings.hydrogen.fingerprint;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.InstrumentedActivity;
import com.android.setupwizardlib.SetupWizardLayout;
import com.android.setupwizardlib.view.NavigationBar;
import com.oneplus.settings.utils.OPUtils;

public abstract class FingerprintEnrollBase
  extends InstrumentedActivity
  implements View.OnClickListener
{
  static final int RESULT_FINISHED = 1;
  static final int RESULT_SKIP = 2;
  static final int RESULT_TIMEOUT = 3;
  protected byte[] mToken;
  protected int mUserId;
  
  protected Intent getEnrollingIntent()
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", FingerprintEnrollEnrolling.class.getName());
    localIntent.putExtra("hw_auth_token", this.mToken);
    if (this.mUserId != 55536) {
      localIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
    }
    return localIntent;
  }
  
  protected NavigationBar getNavigationBar()
  {
    return (NavigationBar)findViewById(2131362573);
  }
  
  protected Button getNextButton()
  {
    return (Button)findViewById(2131362013);
  }
  
  protected SetupWizardLayout getSetupWizardLayout()
  {
    return (SetupWizardLayout)findViewById(2131362138);
  }
  
  protected void initViews()
  {
    getWindow().addFlags(-2147417856);
    if (OPUtils.isWhiteModeOn(getContentResolver())) {
      getWindow().getDecorView().setSystemUiVisibility(9472);
    }
    for (;;)
    {
      getWindow().setStatusBarColor(0);
      Object localObject = getNavigationBar();
      if (localObject != null) {
        ((NavigationBar)localObject).setVisibility(8);
      }
      localObject = getNextButton();
      if (localObject != null) {
        ((Button)localObject).setOnClickListener(this);
      }
      return;
      getWindow().getDecorView().setSystemUiVisibility(1280);
    }
  }
  
  public void onClick(View paramView)
  {
    if (paramView == getNextButton()) {
      onNextButtonClick();
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setHeaderTextColor(2131493771);
    ActionBar localActionBar = getActionBar();
    if (localActionBar != null)
    {
      localActionBar.setBackgroundDrawable(null);
      localActionBar.setDisplayHomeAsUpEnabled(true);
      localActionBar.setTitle("");
    }
    getWindow().addFlags(-2147417856);
    if (OPUtils.isWhiteModeOn(getContentResolver())) {}
    for (int i = 9472;; i = 1280)
    {
      getWindow().getDecorView().setSystemUiVisibility(i);
      getWindow().setStatusBarColor(0);
      this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
      if ((paramBundle != null) && (this.mToken == null)) {
        this.mToken = paramBundle.getByteArray("hw_auth_token");
      }
      this.mUserId = getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
      return;
    }
  }
  
  protected void onNextButtonClick() {}
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 16908332)
    {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  protected void onPostCreate(Bundle paramBundle)
  {
    super.onPostCreate(paramBundle);
    initViews();
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putByteArray("hw_auth_token", this.mToken);
  }
  
  protected void setBackgroudColor(int paramInt)
  {
    try
    {
      getSetupWizardLayout().setBackgroundDrawable(new ColorDrawable(17170445));
      return;
    }
    catch (Exception localException) {}
  }
  
  protected void setHeaderText(int paramInt)
  {
    setHeaderText(paramInt, false);
  }
  
  protected void setHeaderText(int paramInt, boolean paramBoolean) {}
  
  protected void setHeaderTextColor(int paramInt)
  {
    try
    {
      getSetupWizardLayout().getHeaderTextView().setTextColor(getResources().getColor(paramInt));
      return;
    }
    catch (Exception localException) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\hydrogen\fingerprint\FingerprintEnrollBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */