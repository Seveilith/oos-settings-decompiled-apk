package com.android.settings.password;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import com.android.internal.util.Preconditions;

final class SetNewPasswordController
{
  private final int mCurrentUserId;
  private final DevicePolicyManager mDevicePolicyManager;
  private final FingerprintManager mFingerprintManager;
  private final PackageManager mPackageManager;
  private final Ui mUi;
  
  SetNewPasswordController(int paramInt, PackageManager paramPackageManager, FingerprintManager paramFingerprintManager, DevicePolicyManager paramDevicePolicyManager, Ui paramUi)
  {
    this.mCurrentUserId = paramInt;
    this.mPackageManager = ((PackageManager)Preconditions.checkNotNull(paramPackageManager));
    this.mFingerprintManager = paramFingerprintManager;
    this.mDevicePolicyManager = ((DevicePolicyManager)Preconditions.checkNotNull(paramDevicePolicyManager));
    this.mUi = ((Ui)Preconditions.checkNotNull(paramUi));
  }
  
  public SetNewPasswordController(Context paramContext, Ui paramUi)
  {
    this(paramContext.getUserId(), paramContext.getPackageManager(), (FingerprintManager)paramContext.getSystemService("fingerprint"), (DevicePolicyManager)paramContext.getSystemService("device_policy"), paramUi);
  }
  
  private Bundle getFingerprintChooseLockExtras()
  {
    Bundle localBundle = new Bundle();
    if (this.mFingerprintManager != null)
    {
      long l = this.mFingerprintManager.preEnroll();
      localBundle.putInt("minimum_quality", 65536);
      localBundle.putBoolean("hide_disabled_prefs", true);
      localBundle.putBoolean("has_challenge", true);
      localBundle.putLong("challenge", l);
      localBundle.putBoolean("for_fingerprint", true);
      if (this.mCurrentUserId != 55536) {
        localBundle.putInt("android.intent.extra.USER_ID", this.mCurrentUserId);
      }
    }
    return localBundle;
  }
  
  private boolean isFingerprintDisabledByAdmin()
  {
    boolean bool = false;
    if ((this.mDevicePolicyManager.getKeyguardDisabledFeatures(null, this.mCurrentUserId) & 0x20) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void dispatchSetNewPasswordIntent()
  {
    if ((!this.mPackageManager.hasSystemFeature("android.hardware.fingerprint")) || (this.mFingerprintManager == null) || (!this.mFingerprintManager.isHardwareDetected()) || (this.mFingerprintManager.hasEnrolledFingerprints())) {}
    while (isFingerprintDisabledByAdmin())
    {
      this.mUi.launchChooseLock(null);
      return;
    }
    this.mUi.launchChooseLock(getFingerprintChooseLockExtras());
  }
  
  static abstract interface Ui
  {
    public abstract void launchChooseLock(Bundle paramBundle);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\password\SetNewPasswordController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */