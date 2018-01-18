package com.android.settings;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserManager;
import android.security.KeyStore;
import android.security.KeyStore.State;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.fingerprint.FingerprintUiHelper;
import com.android.settings.fingerprint.FingerprintUiHelper.Callback;

public abstract class OxygenConfirmDeviceCredentialBaseFragment
  extends OptionsMenuFragment
  implements FingerprintUiHelper.Callback
{
  public static final String ALLOW_FP_AUTHENTICATION = "com.android.settings.ConfirmCredentials.allowFpAuthentication";
  public static final String DARK_THEME = "com.android.settings.ConfirmCredentials.darkTheme";
  public static final String DETAILS_TEXT = "com.android.settings.ConfirmCredentials.details";
  public static final String HEADER_TEXT = "com.android.settings.ConfirmCredentials.header";
  public static final String PACKAGE = "com.android.settings";
  public static final String SHOW_CANCEL_BUTTON = "com.android.settings.ConfirmCredentials.showCancelButton";
  public static final String SHOW_WHEN_LOCKED = "com.android.settings.ConfirmCredentials.showWhenLocked";
  public static final String TITLE_TEXT = "com.android.settings.ConfirmCredentials.title";
  private boolean mAllowFpAuthentication;
  protected Button mCancelButton;
  protected int mEffectiveUserId;
  protected TextView mErrorTextView;
  private FingerprintUiHelper mFingerprintHelper;
  protected ImageView mFingerprintIcon;
  protected final Handler mHandler = new Handler();
  protected boolean mIsStrongAuthRequired;
  protected LockPatternUtils mLockPatternUtils;
  private final Runnable mResetErrorRunnable = new Runnable()
  {
    public void run()
    {
      OxygenConfirmDeviceCredentialBaseFragment.this.mErrorTextView.setText("");
    }
  };
  protected boolean mReturnCredentials = false;
  protected int mUserId;
  
  private boolean isFingerprintDisabledByAdmin()
  {
    boolean bool = false;
    if ((((DevicePolicyManager)getActivity().getSystemService("device_policy")).getKeyguardDisabledFeatures(null, this.mEffectiveUserId) & 0x20) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isFingerprintDisallowedByStrongAuth()
  {
    return (!this.mLockPatternUtils.isFingerprintAllowedForUser(this.mEffectiveUserId)) || (KeyStore.getInstance().state(this.mUserId) != KeyStore.State.UNLOCKED);
  }
  
  private void setWorkChallengeBackground(View paramView, int paramInt)
  {
    Object localObject = getActivity().findViewById(2131362550);
    if (localObject != null) {
      ((View)localObject).setPadding(0, 0, 0, 0);
    }
    paramView.setBackground(new ColorDrawable(((DevicePolicyManager)getActivity().getSystemService("device_policy")).getOrganizationColorForUser(paramInt)));
    paramView = (ImageView)paramView.findViewById(2131362034);
    if (paramView != null)
    {
      localObject = getResources().getDrawable(2130838555);
      ((Drawable)localObject).setColorFilter(getResources().getColor(2131493686), PorterDuff.Mode.DARKEN);
      paramView.setImageDrawable((Drawable)localObject);
      localObject = new Point();
      getActivity().getWindowManager().getDefaultDisplay().getSize((Point)localObject);
      paramView.setLayoutParams(new FrameLayout.LayoutParams(-1, ((Point)localObject).y));
    }
  }
  
  private void showDialog(String paramString1, String paramString2, int paramInt, final boolean paramBoolean)
  {
    new AlertDialog.Builder(getActivity()).setTitle(paramString1).setMessage(paramString2).setPositiveButton(paramInt, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (paramBoolean) {
          OxygenConfirmDeviceCredentialBaseFragment.this.getActivity().finish();
        }
      }
    }).create().show();
  }
  
  protected abstract void authenticationSucceeded();
  
  protected void checkForPendingIntent()
  {
    int i = getActivity().getIntent().getIntExtra("android.intent.extra.TASK_ID", -1);
    if (i != -1) {
      try
      {
        IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
        ActivityOptions localActivityOptions = ActivityOptions.makeBasic();
        localActivityOptions.setLaunchStackId(-1);
        localIActivityManager.startActivityFromRecents(i, localActivityOptions.toBundle());
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    IntentSender localIntentSender = (IntentSender)getActivity().getIntent().getParcelableExtra("android.intent.extra.INTENT");
    if (localIntentSender != null) {}
    try
    {
      getActivity().startIntentSenderForResult(localIntentSender, -1, null, 0, 0, 0);
      return;
    }
    catch (IntentSender.SendIntentException localSendIntentException) {}
  }
  
  protected abstract int getLastTryErrorMessage();
  
  protected boolean isProfileChallenge()
  {
    return Utils.isManagedProfile(UserManager.get(getContext()), this.mEffectiveUserId);
  }
  
  public void onAuthenticated()
  {
    if ((getActivity() != null) && (getActivity().isResumed()))
    {
      ((TrustManager)getActivity().getSystemService("trust")).setDeviceLockedForUser(this.mEffectiveUserId, false);
      authenticationSucceeded();
      checkForPendingIntent();
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    boolean bool2 = false;
    super.onCreate(paramBundle);
    this.mAllowFpAuthentication = getActivity().getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.allowFpAuthentication", false);
    this.mReturnCredentials = getActivity().getIntent().getBooleanExtra("return_credentials", false);
    paramBundle = getActivity().getIntent();
    this.mUserId = Utils.getUserIdFromBundle(getActivity(), paramBundle.getExtras());
    this.mEffectiveUserId = UserManager.get(getActivity()).getCredentialOwnerProfile(this.mUserId);
    this.mLockPatternUtils = new LockPatternUtils(getActivity());
    this.mIsStrongAuthRequired = isFingerprintDisallowedByStrongAuth();
    boolean bool1 = bool2;
    if (this.mAllowFpAuthentication)
    {
      if (!isFingerprintDisabledByAdmin()) {
        break label130;
      }
      bool1 = bool2;
    }
    for (;;)
    {
      this.mAllowFpAuthentication = bool1;
      return;
      label130:
      bool1 = bool2;
      if (!this.mReturnCredentials)
      {
        bool1 = bool2;
        if (!this.mIsStrongAuthRequired) {
          bool1 = true;
        }
      }
    }
  }
  
  public void onFingerprintIconVisibilityChanged(boolean paramBoolean) {}
  
  public void onPause()
  {
    super.onPause();
    if (this.mFingerprintHelper.isListening()) {
      this.mFingerprintHelper.stopListening();
    }
  }
  
  public void onResume()
  {
    super.onResume();
    refreshLockScreen();
  }
  
  protected abstract void onShowError();
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    this.mCancelButton = ((Button)paramView.findViewById(2131362036));
    this.mFingerprintIcon = ((ImageView)paramView.findViewById(2131362037));
    this.mFingerprintHelper = new FingerprintUiHelper(this.mFingerprintIcon, (TextView)paramView.findViewById(2131362038), this, this.mEffectiveUserId);
    boolean bool = getActivity().getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.showCancelButton", false);
    paramBundle = this.mCancelButton;
    if (bool) {}
    for (int i = 0;; i = 8)
    {
      paramBundle.setVisibility(i);
      this.mCancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          OxygenConfirmDeviceCredentialBaseFragment.this.getActivity().finish();
        }
      });
      i = Utils.getCredentialOwnerUserId(getActivity(), Utils.getUserIdFromBundle(getActivity(), getActivity().getIntent().getExtras()));
      if (Utils.isManagedProfile(UserManager.get(getActivity()), i)) {
        setWorkChallengeBackground(paramView, i);
      }
      return;
    }
  }
  
  public void prepareEnterAnimation() {}
  
  protected void refreshLockScreen()
  {
    if (this.mAllowFpAuthentication) {
      this.mFingerprintHelper.startListening();
    }
    for (;;)
    {
      if (isProfileChallenge()) {
        updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId));
      }
      return;
      if (this.mFingerprintHelper.isListening()) {
        this.mFingerprintHelper.stopListening();
      }
    }
  }
  
  protected void reportFailedAttempt()
  {
    if (isProfileChallenge())
    {
      updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId) + 1);
      this.mLockPatternUtils.reportFailedPasswordAttempt(this.mEffectiveUserId);
    }
  }
  
  protected void reportSuccessfullAttempt()
  {
    if (isProfileChallenge())
    {
      this.mLockPatternUtils.reportSuccessfulPasswordAttempt(this.mEffectiveUserId);
      this.mLockPatternUtils.userPresent(this.mEffectiveUserId);
    }
  }
  
  protected void setAccessibilityTitle(CharSequence paramCharSequence)
  {
    Object localObject = getActivity().getIntent();
    if (localObject != null)
    {
      localObject = ((Intent)localObject).getCharSequenceExtra("com.android.settings.ConfirmCredentials.title");
      if ((localObject == null) || (paramCharSequence == null)) {
        return;
      }
      paramCharSequence = (CharSequence)localObject + "," + paramCharSequence;
      getActivity().setTitle(Utils.createAccessibleSequence((CharSequence)localObject, paramCharSequence));
    }
  }
  
  protected void showError(int paramInt, long paramLong)
  {
    showError(getText(paramInt), paramLong);
  }
  
  protected void showError(CharSequence paramCharSequence, long paramLong)
  {
    this.mErrorTextView.setText(paramCharSequence);
    onShowError();
    this.mHandler.removeCallbacks(this.mResetErrorRunnable);
    if (paramLong != 0L) {
      this.mHandler.postDelayed(this.mResetErrorRunnable, paramLong);
    }
  }
  
  public void startEnterAnimation() {}
  
  protected void updateErrorMessage(int paramInt)
  {
    int i = this.mLockPatternUtils.getMaximumFailedPasswordsForWipe(this.mEffectiveUserId);
    int j;
    if ((i > 0) && (paramInt > 0))
    {
      j = i - paramInt;
      if (j != 1) {
        break label99;
      }
      showDialog(getActivity().getString(2131691203), getActivity().getString(getLastTryErrorMessage()), 17039370, false);
    }
    for (;;)
    {
      if (this.mErrorTextView != null) {
        showError(getActivity().getString(2131691202, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i) }), 0L);
      }
      return;
      label99:
      if (j <= 0) {
        showDialog(null, getActivity().getString(2131691207), 2131691208, true);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\OxygenConfirmDeviceCredentialBaseFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */