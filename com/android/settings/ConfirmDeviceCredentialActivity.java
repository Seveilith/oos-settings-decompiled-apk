package com.android.settings;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;

public class ConfirmDeviceCredentialActivity
  extends Activity
{
  public static final String TAG = ConfirmDeviceCredentialActivity.class.getSimpleName();
  
  public static Intent createIntent(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", ConfirmDeviceCredentialActivity.class.getName());
    localIntent.putExtra("android.app.extra.TITLE", paramCharSequence1);
    localIntent.putExtra("android.app.extra.DESCRIPTION", paramCharSequence2);
    return localIntent;
  }
  
  public static Intent createIntent(CharSequence paramCharSequence1, CharSequence paramCharSequence2, long paramLong)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", ConfirmDeviceCredentialActivity.class.getName());
    localIntent.putExtra("android.app.extra.TITLE", paramCharSequence1);
    localIntent.putExtra("android.app.extra.DESCRIPTION", paramCharSequence2);
    localIntent.putExtra("challenge", paramLong);
    localIntent.putExtra("has_challenge", true);
    return localIntent;
  }
  
  private String getTitleFromOrganizationName(int paramInt)
  {
    String str = null;
    Object localObject = (DevicePolicyManager)getSystemService("device_policy");
    if (localObject != null) {}
    for (localObject = ((DevicePolicyManager)localObject).getOrganizationNameForUser(paramInt);; localObject = null)
    {
      if (localObject != null) {
        str = ((CharSequence)localObject).toString();
      }
      return str;
    }
  }
  
  private boolean isInternalActivity()
  {
    return this instanceof InternalActivity;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent();
    Object localObject = paramBundle.getStringExtra("android.app.extra.TITLE");
    String str = paramBundle.getStringExtra("android.app.extra.DESCRIPTION");
    int j = Utils.getCredentialOwnerUserId(this);
    int i = j;
    if (isInternalActivity()) {}
    try
    {
      i = Utils.getUserIdFromBundle(this, paramBundle.getExtras());
      bool = Utils.isManagedProfile(UserManager.get(this), i);
      paramBundle = (Bundle)localObject;
      if (localObject == null)
      {
        paramBundle = (Bundle)localObject;
        if (bool) {
          paramBundle = getTitleFromOrganizationName(i);
        }
      }
      localObject = new ChooseLockSettingsHelper(this);
      LockPatternUtils localLockPatternUtils = new LockPatternUtils(this);
      if ((!bool) || (!isInternalActivity()) || (localLockPatternUtils.isSeparateProfileChallengeEnabled(i)))
      {
        bool = ((ChooseLockSettingsHelper)localObject).launchConfirmationActivity(0, null, paramBundle, str, false, true, i);
        if (!bool)
        {
          Log.d(TAG, "No pattern, password or PIN set.");
          setResult(-1);
        }
        finish();
        return;
      }
    }
    catch (SecurityException paramBundle)
    {
      for (;;)
      {
        Log.e(TAG, "Invalid intent extra", paramBundle);
        i = j;
        continue;
        boolean bool = ((ChooseLockSettingsHelper)localObject).launchConfirmationActivityWithExternalAndChallenge(0, null, paramBundle, str, true, 0L, i);
      }
    }
  }
  
  public static class InternalActivity
    extends ConfirmDeviceCredentialActivity
  {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ConfirmDeviceCredentialActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */