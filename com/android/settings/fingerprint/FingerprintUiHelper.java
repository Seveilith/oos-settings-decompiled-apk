package com.android.settings.fingerprint;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationCallback;
import android.hardware.fingerprint.FingerprintManager.AuthenticationResult;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class FingerprintUiHelper
  extends FingerprintManager.AuthenticationCallback
{
  private static final long ERROR_TIMEOUT = 1300L;
  private Callback mCallback;
  private CancellationSignal mCancellationSignal;
  private TextView mErrorTextView;
  private FingerprintManager mFingerprintManager;
  private ImageView mIcon;
  private Runnable mResetErrorTextRunnable = new Runnable()
  {
    public void run()
    {
      FingerprintUiHelper.-get0(FingerprintUiHelper.this).setText("");
      FingerprintUiHelper.-get1(FingerprintUiHelper.this).setImageResource(2130837976);
    }
  };
  private int mUserId;
  
  public FingerprintUiHelper(ImageView paramImageView, TextView paramTextView, Callback paramCallback, int paramInt)
  {
    this.mFingerprintManager = ((FingerprintManager)paramImageView.getContext().getSystemService(FingerprintManager.class));
    this.mIcon = paramImageView;
    this.mErrorTextView = paramTextView;
    this.mCallback = paramCallback;
    this.mUserId = paramInt;
  }
  
  private void setFingerprintIconVisibility(boolean paramBoolean)
  {
    ImageView localImageView = this.mIcon;
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      localImageView.setVisibility(i);
      this.mCallback.onFingerprintIconVisibilityChanged(paramBoolean);
      return;
    }
  }
  
  private void showError(CharSequence paramCharSequence)
  {
    if (!isListening()) {
      return;
    }
    this.mIcon.setImageResource(2130837978);
    this.mErrorTextView.setText(paramCharSequence);
    this.mErrorTextView.removeCallbacks(this.mResetErrorTextRunnable);
    this.mErrorTextView.postDelayed(this.mResetErrorTextRunnable, 1300L);
  }
  
  public boolean isListening()
  {
    return (this.mCancellationSignal != null) && (!this.mCancellationSignal.isCanceled());
  }
  
  public void onAuthenticationError(int paramInt, CharSequence paramCharSequence)
  {
    showError(paramCharSequence);
    setFingerprintIconVisibility(false);
  }
  
  public void onAuthenticationFailed()
  {
    showError(this.mIcon.getResources().getString(2131693425));
  }
  
  public void onAuthenticationHelp(int paramInt, CharSequence paramCharSequence)
  {
    showError(paramCharSequence);
  }
  
  public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult paramAuthenticationResult)
  {
    this.mIcon.setImageResource(2130837981);
    this.mCallback.onAuthenticated();
  }
  
  public void startListening()
  {
    if ((this.mFingerprintManager != null) && (this.mFingerprintManager.isHardwareDetected()) && (this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() > 0))
    {
      this.mCancellationSignal = new CancellationSignal();
      this.mFingerprintManager.setActiveUser(this.mUserId);
      this.mFingerprintManager.authenticate(null, this.mCancellationSignal, 0, this, null, this.mUserId);
      setFingerprintIconVisibility(true);
      this.mIcon.setImageResource(2130837976);
    }
  }
  
  public void stopListening()
  {
    if (this.mCancellationSignal != null)
    {
      this.mCancellationSignal.cancel();
      this.mCancellationSignal = null;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onAuthenticated();
    
    public abstract void onFingerprintIconVisibilityChanged(boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fingerprint\FingerprintUiHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */