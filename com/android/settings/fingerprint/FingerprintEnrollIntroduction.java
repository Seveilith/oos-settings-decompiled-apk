package com.android.settings.fingerprint;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.ChooseLockGeneric;
import com.android.settings.ChooseLockSettingsHelper;
import com.android.settings.SetupWizardUtils;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.setupwizardlib.span.LinkSpan;
import com.android.setupwizardlib.span.LinkSpan.OnClickListener;
import com.oneplus.settings.faceunlock.OPFaceUnlockSettings;
import com.oneplus.settings.utils.OPUtils;
import java.io.PrintStream;

public class FingerprintEnrollIntroduction
  extends FingerprintEnrollBase
  implements View.OnClickListener, LinkSpan.OnClickListener
{
  protected static final int CHOOSE_LOCK_GENERIC_REQUEST = 1;
  protected static final int FACE_UNLOCK_SETUP_REQUEST = 4;
  protected static final int FINGERPRINT_FIND_SENSOR_REQUEST = 2;
  protected static final int LEARN_MORE_REQUEST = 3;
  private static final String TAG = "FingerprintIntro";
  private boolean mFingerprintUnlockDisabledByAdmin;
  protected boolean mFromSetup = false;
  private boolean mHasPassword;
  protected byte[] mToken;
  private UserManager mUserManager;
  
  private void launchChooseLock()
  {
    Intent localIntent = getChooseLockIntent();
    long l = ((FingerprintManager)getSystemService(FingerprintManager.class)).preEnroll();
    localIntent.putExtra("minimum_quality", 65536);
    localIntent.putExtra("hide_disabled_prefs", true);
    localIntent.putExtra("has_challenge", true);
    localIntent.putExtra("challenge", l);
    localIntent.putExtra("for_fingerprint", true);
    if (this.mUserId != 55536) {
      localIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
    }
    startActivityForResult(localIntent, 1);
  }
  
  private void launchFaceUnlockSetup(byte[] paramArrayOfByte)
  {
    try
    {
      Intent localIntent = OPFaceUnlockSettings.getSetupFaceUnlockIntent(this);
      SetupWizardUtils.copySetupExtras(getIntent(), localIntent);
      if (paramArrayOfByte != null) {
        localIntent.putExtra("hw_auth_token", paramArrayOfByte);
      }
      if (this.mUserId != 55536) {
        localIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
      }
      startActivityForResult(localIntent, 4);
      return;
    }
    catch (ActivityNotFoundException paramArrayOfByte) {}
  }
  
  private void updatePasswordQuality()
  {
    boolean bool = false;
    if (new ChooseLockSettingsHelper(this).utils().getActivePasswordQuality(this.mUserManager.getCredentialOwnerProfile(this.mUserId)) != 0) {
      bool = true;
    }
    this.mHasPassword = bool;
  }
  
  protected Intent getChooseLockIntent()
  {
    return new Intent(this, ChooseLockGeneric.class);
  }
  
  protected Intent getFindSensorIntent()
  {
    return new Intent(this, FingerprintEnrollFindSensor.class);
  }
  
  protected int getMetricsCategory()
  {
    return 243;
  }
  
  protected Button getNextButton()
  {
    return (Button)findViewById(2131362153);
  }
  
  protected void initViews()
  {
    super.initViews();
    TextView localTextView = (TextView)findViewById(2131362151);
    if (this.mFingerprintUnlockDisabledByAdmin) {
      localTextView.setText(2131691074);
    }
  }
  
  protected void launchFindSensor(byte[] paramArrayOfByte)
  {
    Intent localIntent = getFindSensorIntent();
    if (paramArrayOfByte != null) {
      localIntent.putExtra("hw_auth_token", paramArrayOfByte);
    }
    if (this.mUserId != 55536) {
      localIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
    }
    startActivityForResult(localIntent, 2);
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    int j = 2;
    if (paramInt2 == 1) {}
    for (int i = 1;; i = 0)
    {
      System.out.println("oneplus--FingerprintEnrollIntroduction--requestCode:" + paramInt1);
      System.out.println("oneplus--FingerprintEnrollIntroduction--resultCode:" + paramInt2);
      System.out.println("oneplus--FingerprintEnrollIntroduction--mFromSetup:" + this.mFromSetup);
      if ((paramInt1 != 2) && ((paramInt1 != 4) || (paramInt2 != 1))) {
        break label153;
      }
      if ((i == 0) && (paramInt2 != 2)) {
        break;
      }
      paramInt1 = j;
      if (i != 0) {
        paramInt1 = -1;
      }
      setResult(paramInt1, paramIntent);
      finish();
      return;
    }
    if (this.mFromSetup)
    {
      finish();
      return;
      label153:
      if (paramInt1 == 1)
      {
        if (i != 0)
        {
          updatePasswordQuality();
          paramIntent = paramIntent.getByteArrayExtra("hw_auth_token");
          this.mToken = paramIntent;
          if ((OPUtils.isAppExist(this, "com.oneplus.faceunlock")) && (OPUtils.isSurportBackFingerprint(this)) && (this.mFromSetup))
          {
            launchFaceUnlockSetup(paramIntent);
            return;
          }
          launchFindSensor(paramIntent);
        }
      }
      else if (paramInt1 == 3) {
        overridePendingTransition(2131034160, 2131034161);
      }
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  protected void onCancelButtonClick()
  {
    finish();
  }
  
  public void onClick(View paramView)
  {
    if (paramView.getId() == 2131362152)
    {
      onCancelButtonClick();
      return;
    }
    super.onClick(paramView);
  }
  
  public void onClick(LinkSpan paramLinkSpan)
  {
    if ("url".equals(paramLinkSpan.getId()))
    {
      paramLinkSpan = HelpUtils.getHelpIntent(this, getString(2131693030), getClass().getName());
      if (paramLinkSpan == null)
      {
        Log.w("FingerprintIntro", "Null help intent.");
        return;
      }
    }
    try
    {
      startActivityForResult(paramLinkSpan, 3);
      return;
    }
    catch (ActivityNotFoundException paramLinkSpan)
    {
      Log.w("FingerprintIntro", "Activity was not found for intent, " + paramLinkSpan);
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    boolean bool = true;
    super.onCreate(paramBundle);
    paramBundle = getActionBar();
    if (paramBundle != null)
    {
      paramBundle.setDisplayHomeAsUpEnabled(true);
      paramBundle.setTitle(2131691071);
    }
    if (RestrictedLockUtils.checkIfKeyguardFeaturesDisabled(this, 32, this.mUserId) != null)
    {
      this.mFingerprintUnlockDisabledByAdmin = bool;
      setContentView(2130968708);
      if (!this.mFingerprintUnlockDisabledByAdmin) {
        break label98;
      }
      setHeaderText(2131691072);
    }
    for (;;)
    {
      ((Button)findViewById(2131362152)).setOnClickListener(this);
      this.mUserManager = UserManager.get(this);
      updatePasswordQuality();
      return;
      bool = false;
      break;
      label98:
      setHeaderText(2131691071);
    }
  }
  
  protected void onNextButtonClick()
  {
    if (!this.mHasPassword)
    {
      launchChooseLock();
      return;
    }
    launchFindSensor(null);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fingerprint\FingerprintEnrollIntroduction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */