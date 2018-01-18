package com.android.settings.hydrogen.fingerprint;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.widget.Toast;
import com.android.settings.ChooseLockSettingsHelper;
import java.io.PrintStream;
import java.util.List;

public class FingerprintEnrollFindSensor
  extends FingerprintEnrollBase
{
  private static final int CONFIRM_REQUEST = 1;
  private static final int ENROLLING = 2;
  public static final String EXTRA_KEY_LAUNCHED_CONFIRM = "launched_confirm_lock";
  private static final int FINGERPRINT_ENROLL_HARDWARE_UNAVAILABLE = 1;
  private static final int FINGERPRINT_ENROLL_TIME_OUT = 3;
  private FingerprintFindSensorAnimation mAnimation;
  private boolean mLaunchedConfirmLock;
  private boolean mNextClicked;
  private FingerprintEnrollSidecar mSidecar;
  
  private void launchConfirmLock()
  {
    long l = ((FingerprintManager)getSystemService(FingerprintManager.class)).preEnroll();
    ChooseLockSettingsHelper localChooseLockSettingsHelper = new ChooseLockSettingsHelper(this);
    if (this.mUserId == 55536) {}
    for (boolean bool = localChooseLockSettingsHelper.launchConfirmationActivity(1, getString(2131691065), null, null, l); !bool; bool = localChooseLockSettingsHelper.launchConfirmationActivity(1, getString(2131691065), null, null, l, this.mUserId))
    {
      finish();
      return;
    }
    this.mLaunchedConfirmLock = true;
  }
  
  private void proceedToEnrolling()
  {
    if (this.mSidecar != null)
    {
      getFragmentManager().beginTransaction().remove(this.mSidecar).commit();
      this.mSidecar = null;
    }
    startActivityForResult(getEnrollingIntent(), 2);
  }
  
  private void startLookingForFingerprint()
  {
    this.mSidecar = ((FingerprintEnrollSidecar)getFragmentManager().findFragmentByTag("sidecar"));
    if (this.mSidecar == null)
    {
      this.mSidecar = new FingerprintEnrollSidecar();
      getFragmentManager().beginTransaction().add(this.mSidecar, "sidecar").commit();
    }
    this.mSidecar.setListener(new FingerprintEnrollSidecar.Listener()
    {
      public void onEnrollmentError(int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
      {
        if ((1 == paramAnonymousInt) || (3 == paramAnonymousInt))
        {
          Toast.makeText(FingerprintEnrollFindSensor.this, 2131690440, 0).show();
          FingerprintEnrollFindSensor.this.finish();
          return;
        }
        if ((FingerprintEnrollFindSensor.-get0(FingerprintEnrollFindSensor.this)) && (paramAnonymousInt == 5))
        {
          FingerprintEnrollFindSensor.-set0(FingerprintEnrollFindSensor.this, false);
          FingerprintEnrollFindSensor.-wrap0(FingerprintEnrollFindSensor.this);
        }
      }
      
      public void onEnrollmentHelp(CharSequence paramAnonymousCharSequence) {}
      
      public void onEnrollmentProgressChange(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        FingerprintEnrollFindSensor.-set0(FingerprintEnrollFindSensor.this, true);
        System.out.println("oneplus--onEnrollmentProgressChange:" + FingerprintEnrollFindSensor.-get1(FingerprintEnrollFindSensor.this));
        if ((FingerprintEnrollFindSensor.-get1(FingerprintEnrollFindSensor.this) == null) || (FingerprintEnrollFindSensor.-get1(FingerprintEnrollFindSensor.this).cancelEnrollment())) {
          return;
        }
        FingerprintEnrollFindSensor.-wrap0(FingerprintEnrollFindSensor.this);
      }
    });
  }
  
  protected int getContentView()
  {
    return 2130969150;
  }
  
  protected int getMetricsCategory()
  {
    return 241;
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 1)
    {
      if (paramInt2 == -1)
      {
        this.mToken = paramIntent.getByteArrayExtra("hw_auth_token");
        overridePendingTransition(2131034162, 2131034163);
        getIntent().putExtra("hw_auth_token", this.mToken);
        startLookingForFingerprint();
        return;
      }
      finish();
      return;
    }
    if (paramInt1 == 2)
    {
      if (paramInt2 == 1)
      {
        setResult(1);
        finish();
        return;
      }
      if (paramInt2 == 2)
      {
        setResult(2);
        finish();
        return;
      }
      if (paramInt2 == 3)
      {
        setResult(3);
        finish();
        return;
      }
      if (((FingerprintManager)getSystemService(FingerprintManager.class)).getEnrolledFingerprints().size() >= getResources().getInteger(17694881))
      {
        finish();
        return;
      }
      startLookingForFingerprint();
      return;
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(getContentView());
    setHeaderText(2131691084);
    setHeaderTextColor(2131493771);
    if (paramBundle != null)
    {
      this.mLaunchedConfirmLock = paramBundle.getBoolean("launched_confirm_lock");
      this.mToken = paramBundle.getByteArray("hw_auth_token");
    }
    this.mAnimation = ((FingerprintFindSensorAnimation)findViewById(2131362147));
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    this.mAnimation.stopAnimation();
  }
  
  protected void onNextButtonClick()
  {
    System.out.println("zhuyang--mNextClicked:" + this.mNextClicked);
    this.mNextClicked = true;
    if ((this.mSidecar != null) && ((this.mSidecar == null) || (this.mSidecar.cancelEnrollment()))) {
      return;
    }
    proceedToEnrolling();
  }
  
  public void onPause()
  {
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    if ((this.mToken != null) || (this.mLaunchedConfirmLock))
    {
      if (this.mToken != null) {
        startLookingForFingerprint();
      }
      return;
    }
    launchConfirmLock();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("launched_confirm_lock", this.mLaunchedConfirmLock);
    paramBundle.putByteArray("hw_auth_token", this.mToken);
  }
  
  protected void onStart()
  {
    super.onStart();
    this.mAnimation.startAnimation();
  }
  
  protected void onStop()
  {
    super.onStop();
    this.mAnimation.pauseAnimation();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\hydrogen\fingerprint\FingerprintEnrollFindSensor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */