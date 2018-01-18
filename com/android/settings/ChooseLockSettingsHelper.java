package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.os.UserManager;
import com.android.internal.widget.LockPatternUtils;

public final class ChooseLockSettingsHelper
{
  public static final String EXTRA_KEY_CHALLENGE = "challenge";
  public static final String EXTRA_KEY_CHALLENGE_TOKEN = "hw_auth_token";
  public static final String EXTRA_KEY_FOR_CHANGE_CRED_REQUIRED_FOR_BOOT = "for_cred_req_boot";
  public static final String EXTRA_KEY_FOR_FINGERPRINT = "for_fingerprint";
  public static final String EXTRA_KEY_HAS_CHALLENGE = "has_challenge";
  static final String EXTRA_KEY_PASSWORD = "password";
  public static final String EXTRA_KEY_RETURN_CREDENTIALS = "return_credentials";
  static final String EXTRA_KEY_TYPE = "type";
  private Activity mActivity;
  private Fragment mFragment;
  LockPatternUtils mLockPatternUtils;
  
  public ChooseLockSettingsHelper(Activity paramActivity)
  {
    this.mActivity = paramActivity;
    this.mLockPatternUtils = new LockPatternUtils(paramActivity);
  }
  
  public ChooseLockSettingsHelper(Activity paramActivity, Fragment paramFragment)
  {
    this(paramActivity);
    this.mFragment = paramFragment;
  }
  
  private void copyOptionalExtras(Intent paramIntent1, Intent paramIntent2)
  {
    IntentSender localIntentSender = (IntentSender)paramIntent1.getParcelableExtra("android.intent.extra.INTENT");
    if (localIntentSender != null) {
      paramIntent2.putExtra("android.intent.extra.INTENT", localIntentSender);
    }
    int i = paramIntent1.getIntExtra("android.intent.extra.TASK_ID", -1);
    if (i != -1) {
      paramIntent2.putExtra("android.intent.extra.TASK_ID", i);
    }
    if ((localIntentSender != null) || (i != -1))
    {
      paramIntent2.addFlags(8388608);
      paramIntent2.addFlags(1073741824);
    }
  }
  
  private boolean launchConfirmationActivity(int paramInt1, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, Class<?> paramClass, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, long paramLong, int paramInt2)
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("com.android.settings.ConfirmCredentials.title", paramCharSequence1);
    localIntent.putExtra("com.android.settings.ConfirmCredentials.header", paramCharSequence2);
    localIntent.putExtra("com.android.settings.ConfirmCredentials.details", paramCharSequence3);
    localIntent.putExtra("com.android.settings.ConfirmCredentials.allowFpAuthentication", paramBoolean2);
    localIntent.putExtra("com.android.settings.ConfirmCredentials.darkTheme", paramBoolean2);
    localIntent.putExtra("com.android.settings.ConfirmCredentials.showCancelButton", paramBoolean2);
    localIntent.putExtra("com.android.settings.ConfirmCredentials.showWhenLocked", paramBoolean2);
    localIntent.putExtra("return_credentials", paramBoolean1);
    localIntent.putExtra("has_challenge", paramBoolean3);
    localIntent.putExtra("challenge", paramLong);
    localIntent.putExtra(":settings:hide_drawer", true);
    localIntent.putExtra("android.intent.extra.USER_ID", paramInt2);
    localIntent.setClassName("com.android.settings", paramClass.getName());
    if (paramBoolean2)
    {
      localIntent.addFlags(33554432);
      if (this.mFragment != null)
      {
        copyOptionalExtras(this.mFragment.getActivity().getIntent(), localIntent);
        this.mFragment.startActivity(localIntent);
        return true;
      }
      copyOptionalExtras(this.mActivity.getIntent(), localIntent);
      this.mActivity.startActivity(localIntent);
      return true;
    }
    if (this.mFragment != null)
    {
      this.mFragment.startActivityForResult(localIntent, paramInt1);
      return true;
    }
    this.mActivity.startActivityForResult(localIntent, paramInt1);
    return true;
  }
  
  private boolean launchConfirmationActivity(int paramInt1, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, long paramLong, int paramInt2)
  {
    int i = UserManager.get(this.mActivity).getCredentialOwnerProfile(paramInt2);
    switch (this.mLockPatternUtils.getKeyguardStoredPasswordQuality(i))
    {
    default: 
      return false;
    case 65536: 
      if ((paramBoolean1) || (paramBoolean3)) {}
      for (localClass = ConfirmLockPattern.InternalActivity.class;; localClass = ConfirmLockPattern.class) {
        return launchConfirmationActivity(paramInt1, paramCharSequence1, paramCharSequence2, paramCharSequence3, localClass, paramBoolean1, paramBoolean2, paramBoolean3, paramLong, paramInt2);
      }
    }
    if ((paramBoolean1) || (paramBoolean3)) {}
    for (Class localClass = ConfirmLockPassword.InternalActivity.class;; localClass = ConfirmLockPassword.class) {
      return launchConfirmationActivity(paramInt1, paramCharSequence1, paramCharSequence2, paramCharSequence3, localClass, paramBoolean1, paramBoolean2, paramBoolean3, paramLong, paramInt2);
    }
  }
  
  public boolean isPasswordLockMode()
  {
    int i = Utils.getEffectiveUserId(this.mActivity);
    switch (this.mLockPatternUtils.getKeyguardStoredPasswordQuality(i))
    {
    default: 
      return false;
    }
    return true;
  }
  
  public boolean launchConfirmationActivity(int paramInt, CharSequence paramCharSequence)
  {
    return launchConfirmationActivity(paramInt, paramCharSequence, null, null, false, false);
  }
  
  public boolean launchConfirmationActivity(int paramInt, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, long paramLong)
  {
    return launchConfirmationActivity(paramInt, paramCharSequence1, paramCharSequence2, paramCharSequence3, true, false, true, paramLong, Utils.getCredentialOwnerUserId(this.mActivity));
  }
  
  public boolean launchConfirmationActivity(int paramInt1, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, long paramLong, int paramInt2)
  {
    return launchConfirmationActivity(paramInt1, paramCharSequence1, paramCharSequence2, paramCharSequence3, true, false, true, paramLong, Utils.enforceSameOwner(this.mActivity, paramInt2));
  }
  
  boolean launchConfirmationActivity(int paramInt, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, boolean paramBoolean1, boolean paramBoolean2)
  {
    return launchConfirmationActivity(paramInt, paramCharSequence1, paramCharSequence2, paramCharSequence3, paramBoolean1, paramBoolean2, false, 0L, Utils.getCredentialOwnerUserId(this.mActivity));
  }
  
  boolean launchConfirmationActivity(int paramInt1, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
  {
    return launchConfirmationActivity(paramInt1, paramCharSequence1, paramCharSequence2, paramCharSequence3, paramBoolean1, paramBoolean2, false, 0L, Utils.enforceSameOwner(this.mActivity, paramInt2));
  }
  
  boolean launchConfirmationActivity(int paramInt, CharSequence paramCharSequence, boolean paramBoolean)
  {
    return launchConfirmationActivity(paramInt, paramCharSequence, null, null, paramBoolean, false);
  }
  
  public boolean launchConfirmationActivity(int paramInt1, CharSequence paramCharSequence, boolean paramBoolean, int paramInt2)
  {
    return launchConfirmationActivity(paramInt1, paramCharSequence, null, null, paramBoolean, false, false, 0L, Utils.enforceSameOwner(this.mActivity, paramInt2));
  }
  
  public boolean launchConfirmationActivityExt(int paramInt, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    return launchConfirmationActivity(paramInt, paramCharSequence1, false);
  }
  
  public boolean launchConfirmationActivityWithExternalAndChallenge(int paramInt1, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, boolean paramBoolean, long paramLong, int paramInt2)
  {
    return launchConfirmationActivity(paramInt1, paramCharSequence1, paramCharSequence2, paramCharSequence3, false, paramBoolean, true, paramLong, Utils.enforceSameOwner(this.mActivity, paramInt2));
  }
  
  public int lockMode()
  {
    int i = Utils.getEffectiveUserId(this.mActivity);
    switch (this.mLockPatternUtils.getKeyguardStoredPasswordQuality(i))
    {
    case 262144: 
    case 327680: 
    case 393216: 
    default: 
      return 0;
    case 65536: 
      return 1;
    }
    return 2;
  }
  
  public LockPatternUtils utils()
  {
    return this.mLockPatternUtils;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ChooseLockSettingsHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */