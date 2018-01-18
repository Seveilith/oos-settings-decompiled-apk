package com.android.settings.password;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.android.settings.ChooseLockGeneric;

public class SetNewPasswordActivity
  extends Activity
  implements SetNewPasswordController.Ui
{
  private String mNewPasswordAction;
  private SetNewPasswordController mSetNewPasswordController;
  
  public void launchChooseLock(Bundle paramBundle)
  {
    Intent localIntent = new Intent(this, ChooseLockGeneric.class).setAction(this.mNewPasswordAction);
    if (paramBundle != null) {
      localIntent.putExtras(paramBundle);
    }
    startActivity(localIntent);
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mNewPasswordAction = getIntent().getAction();
    this.mSetNewPasswordController = new SetNewPasswordController(this, this);
    this.mSetNewPasswordController.dispatchSetNewPasswordIntent();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\password\SetNewPasswordActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */