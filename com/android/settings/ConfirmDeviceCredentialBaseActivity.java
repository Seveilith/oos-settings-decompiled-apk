package com.android.settings;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.view.MenuItem;
import android.view.Window;

public abstract class ConfirmDeviceCredentialBaseActivity
  extends SettingsActivity
{
  private static final String STATE_IS_KEYGUARD_LOCKED = "STATE_IS_KEYGUARD_LOCKED";
  private boolean mDark;
  private boolean mEnterAnimationPending;
  private boolean mFirstTimeVisible = true;
  private boolean mIsKeyguardLocked = false;
  private boolean mRestoring;
  
  private ConfirmDeviceCredentialBaseFragment getFragment()
  {
    Fragment localFragment = getFragmentManager().findFragmentById(2131362550);
    if ((localFragment != null) && ((localFragment instanceof ConfirmDeviceCredentialBaseFragment))) {
      return (ConfirmDeviceCredentialBaseFragment)localFragment;
    }
    return null;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    int i = Utils.getCredentialOwnerUserId(this, Utils.getUserIdFromBundle(this, getIntent().getExtras()));
    if (Utils.isManagedProfile(UserManager.get(this), i))
    {
      setTheme(2131821602);
      super.onCreate(paramBundle);
      if (paramBundle != null) {
        break label164;
      }
      bool = ((KeyguardManager)getSystemService(KeyguardManager.class)).isKeyguardLocked();
      label55:
      this.mIsKeyguardLocked = bool;
      if ((this.mIsKeyguardLocked) && (getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.showWhenLocked", false))) {
        getWindow().addFlags(524288);
      }
      setTitle(getIntent().getStringExtra("com.android.settings.ConfirmCredentials.title"));
      if (getActionBar() != null)
      {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
      }
      if (paramBundle == null) {
        break label175;
      }
    }
    label164:
    label175:
    for (boolean bool = true;; bool = false)
    {
      this.mRestoring = bool;
      return;
      if (!getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.darkTheme", false)) {
        break;
      }
      setTheme(2131821601);
      this.mDark = true;
      break;
      bool = paramBundle.getBoolean("STATE_IS_KEYGUARD_LOCKED", false);
      break label55;
    }
  }
  
  public void onEnterAnimationComplete()
  {
    super.onEnterAnimationComplete();
    if (this.mEnterAnimationPending)
    {
      startEnterAnimation();
      this.mEnterAnimationPending = false;
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 16908332)
    {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  public void onResume()
  {
    super.onResume();
    if ((isChangingConfigurations()) || (this.mRestoring)) {}
    while ((!this.mDark) || (!this.mFirstTimeVisible)) {
      return;
    }
    this.mFirstTimeVisible = false;
    prepareEnterAnimation();
    this.mEnterAnimationPending = true;
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("STATE_IS_KEYGUARD_LOCKED", this.mIsKeyguardLocked);
  }
  
  public void prepareEnterAnimation()
  {
    getFragment().prepareEnterAnimation();
  }
  
  public void startEnterAnimation()
  {
    getFragment().startEnterAnimation();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ConfirmDeviceCredentialBaseActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */