package com.android.settings.fingerprint;

import android.app.Activity;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.EnrollmentCallback;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import com.android.settings.InstrumentedFragment;

public class FingerprintEnrollSidecar
  extends InstrumentedFragment
{
  private boolean mDone;
  private boolean mEnrolling;
  private FingerprintManager.EnrollmentCallback mEnrollmentCallback = new FingerprintManager.EnrollmentCallback()
  {
    public void onEnrollmentError(int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
    {
      if (FingerprintEnrollSidecar.-get1(FingerprintEnrollSidecar.this) != null) {
        FingerprintEnrollSidecar.-get1(FingerprintEnrollSidecar.this).onEnrollmentError(paramAnonymousInt, paramAnonymousCharSequence);
      }
      FingerprintEnrollSidecar.-set1(FingerprintEnrollSidecar.this, false);
    }
    
    public void onEnrollmentHelp(int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
    {
      if (FingerprintEnrollSidecar.-get1(FingerprintEnrollSidecar.this) != null) {
        FingerprintEnrollSidecar.-get1(FingerprintEnrollSidecar.this).onEnrollmentHelp(paramAnonymousCharSequence);
      }
    }
    
    public void onEnrollmentProgress(int paramAnonymousInt)
    {
      boolean bool = false;
      if (FingerprintEnrollSidecar.-get0(FingerprintEnrollSidecar.this) == -1) {
        FingerprintEnrollSidecar.-set3(FingerprintEnrollSidecar.this, paramAnonymousInt);
      }
      FingerprintEnrollSidecar.-set2(FingerprintEnrollSidecar.this, paramAnonymousInt);
      FingerprintEnrollSidecar localFingerprintEnrollSidecar = FingerprintEnrollSidecar.this;
      if (paramAnonymousInt == 0) {
        bool = true;
      }
      FingerprintEnrollSidecar.-set0(localFingerprintEnrollSidecar, bool);
      if (FingerprintEnrollSidecar.-get1(FingerprintEnrollSidecar.this) != null) {
        FingerprintEnrollSidecar.-get1(FingerprintEnrollSidecar.this).onEnrollmentProgressChange(FingerprintEnrollSidecar.-get0(FingerprintEnrollSidecar.this), paramAnonymousInt);
      }
    }
  };
  private CancellationSignal mEnrollmentCancel;
  private int mEnrollmentRemaining = 0;
  private int mEnrollmentSteps = -1;
  private FingerprintManager mFingerprintManager;
  private Handler mHandler = new Handler();
  private Listener mListener;
  private final Runnable mTimeoutRunnable = new Runnable()
  {
    public void run()
    {
      FingerprintEnrollSidecar.this.cancelEnrollment();
    }
  };
  private byte[] mToken;
  private int mUserId;
  
  private void startEnrollment()
  {
    this.mHandler.removeCallbacks(this.mTimeoutRunnable);
    this.mEnrollmentSteps = -1;
    this.mEnrollmentCancel = new CancellationSignal();
    if (this.mUserId != 55536) {
      this.mFingerprintManager.setActiveUser(this.mUserId);
    }
    this.mFingerprintManager.enroll(this.mToken, this.mEnrollmentCancel, 0, this.mUserId, this.mEnrollmentCallback);
    this.mEnrolling = true;
  }
  
  boolean cancelEnrollment()
  {
    this.mHandler.removeCallbacks(this.mTimeoutRunnable);
    if (this.mEnrolling)
    {
      this.mEnrollmentCancel.cancel();
      this.mEnrolling = false;
      this.mEnrollmentSteps = -1;
      return true;
    }
    return false;
  }
  
  public int getEnrollmentRemaining()
  {
    return this.mEnrollmentRemaining;
  }
  
  public int getEnrollmentSteps()
  {
    return this.mEnrollmentSteps;
  }
  
  protected int getMetricsCategory()
  {
    return 245;
  }
  
  public boolean isDone()
  {
    return this.mDone;
  }
  
  public boolean isEnrolling()
  {
    return this.mEnrolling;
  }
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    this.mFingerprintManager = ((FingerprintManager)paramActivity.getSystemService(FingerprintManager.class));
    this.mToken = paramActivity.getIntent().getByteArrayExtra("hw_auth_token");
    this.mUserId = paramActivity.getIntent().getIntExtra("android.intent.extra.USER_ID", 55536);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setRetainInstance(true);
  }
  
  public void onStart()
  {
    super.onStart();
    if (!this.mEnrolling) {
      startEnrollment();
    }
  }
  
  public void onStop()
  {
    super.onStop();
    if (!getActivity().isChangingConfigurations()) {
      cancelEnrollment();
    }
  }
  
  public void setListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }
  
  public static abstract interface Listener
  {
    public abstract void onEnrollmentError(int paramInt, CharSequence paramCharSequence);
    
    public abstract void onEnrollmentHelp(CharSequence paramCharSequence);
    
    public abstract void onEnrollmentProgressChange(int paramInt1, int paramInt2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fingerprint\FingerprintEnrollSidecar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */