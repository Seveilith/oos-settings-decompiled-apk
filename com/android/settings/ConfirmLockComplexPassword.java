package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternChecker.OnCheckCallback;
import com.android.internal.widget.LockPatternChecker.OnVerifyCallback;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.TextViewInputDisabler;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import com.oneplus.settings.password.OPPasswordInputCountCallBack;
import com.oneplus.settings.password.OPPasswordTextViewForPin;
import com.oneplus.settings.password.OPPasswordTextViewForPin.OnTextEmptyListerner;
import java.util.ArrayList;

public class ConfirmLockComplexPassword
  extends ConfirmDeviceCredentialBaseActivity
{
  private static final int[] DETAIL_TEXTS = { 2131692013, 2131692014, 2131692016, 2131692017, 2131692019, 2131692020, 2131692022, 2131692023 };
  
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":settings:show_fragment", ConfirmLockPasswordFragment.class.getName());
    return localIntent;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return ConfirmLockPasswordFragment.class.getName().equals(paramString);
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
    Fragment localFragment = getFragmentManager().findFragmentById(2131362550);
    if ((localFragment != null) && ((localFragment instanceof ConfirmLockPasswordFragment))) {
      ((ConfirmLockPasswordFragment)localFragment).onWindowFocusChanged(paramBoolean);
    }
  }
  
  public static class ConfirmLockPasswordFragment
    extends ConfirmDeviceCredentialBaseFragment
    implements View.OnClickListener, TextView.OnEditorActionListener, CredentialCheckResultTracker.Listener
  {
    private static final long ERROR_MESSAGE_TIMEOUT = 3000L;
    private static final int FAILED_ATTEMPTS_BEFORE_TIMEOUT = 5;
    private static final String FRAGMENT_TAG_CHECK_LOCK_RESULT = "check_lock_result";
    private static final String KEY_NUM_WRONG_CONFIRM_ATTEMPTS = "confirm_lock_password_fragment.key_num_wrong_confirm_attempts";
    private static final String KEY_TIME_WRONG_CONFIRM_ATTEMPTS = "confirm_lock_password_fragment.key_time_wrong_confirm_attempts";
    private static final int LOCKOUT_TIME_OUT = 60000;
    private static final int MAX_LOCK_PASSWORD_SIZE = 16;
    private static final int MIN_LOCK_PASSWORD_SIZE = 4;
    private static SharedPreferences mPreferences = null;
    private AppearAnimationUtils mAppearAnimationUtils;
    private boolean mBlockImm;
    private TextView mConfirmButton;
    private CountDownTimer mCountdownTimer;
    private CredentialCheckResultTracker mCredentialCheckResultTracker;
    private TextView mDeleteButton;
    private TextView mDetailsTextView;
    private DisappearAnimationUtils mDisappearAnimationUtils;
    private boolean mDisappearing = false;
    private TextView mHeaderTextView;
    private InputMethodManager mImm;
    private boolean mIsAlpha;
    private int mNumWrongConfirmAttempts;
    private TextView mPasswordEntry;
    private TextViewInputDisabler mPasswordEntryInputDisabler;
    public OPPasswordInputCountCallBack mPasswordInputCountCallBack = new OPPasswordInputCountCallBack()
    {
      public boolean checkPassword()
      {
        return false;
      }
      
      public void setNumbPadKeyForPinEnable(boolean paramAnonymousBoolean) {}
    };
    private OPPasswordTextViewForPin mPasswordTextViewForPin;
    private String mPattenString;
    private AsyncTask<?, ?, ?> mPendingLockCheck;
    private boolean mUsingFingerprint = false;
    
    private View[] getActiveViews()
    {
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(this.mHeaderTextView);
      if (this.mCancelButton.getVisibility() == 0) {
        localArrayList.add(this.mCancelButton);
      }
      if (this.mFingerprintIcon.getVisibility() == 0) {
        localArrayList.add(this.mFingerprintIcon);
      }
      return (View[])localArrayList.toArray(new View[0]);
    }
    
    private int getDefaultDetails()
    {
      int k = 1;
      boolean bool = Utils.isManagedProfile(UserManager.get(getActivity()), this.mEffectiveUserId);
      int i;
      int j;
      if (this.mIsStrongAuthRequired)
      {
        i = 1;
        if (!bool) {
          break label60;
        }
        j = 1;
        label34:
        if (!this.mIsAlpha) {
          break label65;
        }
      }
      for (;;)
      {
        return ConfirmLockComplexPassword.-get0()[((j << 1) + (i << 2) + k)];
        i = 0;
        break;
        label60:
        j = 0;
        break label34;
        label65:
        k = 0;
      }
    }
    
    private int getDefaultHeader()
    {
      if (this.mIsAlpha) {
        return 2131692003;
      }
      return 2131692005;
    }
    
    private int getErrorMessage()
    {
      if (this.mIsAlpha) {
        return 2131692025;
      }
      return 2131692024;
    }
    
    private void handleAttemptLockout(long paramLong)
    {
      long l = SystemClock.elapsedRealtime();
      this.mDeleteButton.setEnabled(false);
      this.mConfirmButton.setEnabled(false);
      this.mHeaderTextView.setText(getString(2131690180, new Object[] { Integer.valueOf(0) }));
      this.mPasswordTextViewForPin.setEnabled(false);
      this.mCountdownTimer = new CountDownTimer(paramLong - l, 1000L)
      {
        public void onFinish()
        {
          ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-wrap3(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this);
          if (ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.isProfileChallenge()) {
            ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.updateErrorMessage(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.mLockPatternUtils.getCurrentFailedPasswordAttempts(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.mEffectiveUserId));
          }
          ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get5(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this).setEnabled(true);
          ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get2(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this).setText(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-wrap1(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this));
          ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-set1(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this, 0);
          ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get6().edit().putInt("confirm_lock_password_fragment.key_num_wrong_confirm_attempts", ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get4(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this)).commit();
        }
        
        public void onTick(long paramAnonymousLong)
        {
          int i = (int)(paramAnonymousLong / 1000L);
          if (ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.getActivity() == null) {
            return;
          }
          ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get2(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this).setText(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.getString(2131692052, new Object[] { Integer.valueOf(i) }));
        }
      }.start();
    }
    
    private void handleNext()
    {
      if ((this.mPendingLockCheck != null) || (this.mDisappearing)) {
        return;
      }
      String str = this.mPasswordTextViewForPin.getText().toString();
      if (str.length() < 4)
      {
        this.mHeaderTextView.setText(getString(2131691210, new Object[] { Integer.valueOf(4) }));
        return;
      }
      this.mPasswordTextViewForPin.reset(true);
      boolean bool = getActivity().getIntent().getBooleanExtra("has_challenge", false);
      Intent localIntent = new Intent();
      if (bool)
      {
        if (isInternalActivity()) {
          startVerifyPassword(str, localIntent);
        }
      }
      else
      {
        startCheckPassword(str, localIntent);
        return;
      }
      this.mCredentialCheckResultTracker.setResult(false, localIntent, 0, this.mEffectiveUserId);
    }
    
    private boolean isInternalActivity()
    {
      return getActivity() instanceof ConfirmLockComplexPassword.InternalActivity;
    }
    
    private void onPasswordChecked(boolean paramBoolean1, Intent paramIntent, int paramInt1, int paramInt2, boolean paramBoolean2)
    {
      if (paramBoolean1)
      {
        if (paramBoolean2) {
          reportSuccessfullAttempt();
        }
        startDisappearAnimation(paramIntent);
        checkForPendingIntent();
        return;
      }
      if (paramBoolean2) {
        reportFailedAttempt();
      }
      if (this.mNumWrongConfirmAttempts == 0) {
        mPreferences.edit().putLong("confirm_lock_password_fragment.key_time_wrong_confirm_attempts", System.currentTimeMillis()).commit();
      }
      paramInt1 = this.mNumWrongConfirmAttempts + 1;
      this.mNumWrongConfirmAttempts = paramInt1;
      if (paramInt1 >= 5)
      {
        handleAttemptLockout(this.mLockPatternUtils.setLockoutAttemptDeadline(paramInt2, 60000));
        return;
      }
      paramIntent = String.format(getResources().getString(2131690752), new Object[] { getResources().getString(2131692025), getResources().getString(2131690181) });
      this.mHeaderTextView.setText(paramIntent);
    }
    
    private void resetState()
    {
      if (this.mBlockImm) {}
    }
    
    private boolean shouldAutoShowSoftKeyboard()
    {
      return false;
    }
    
    private void startCheckPassword(final String paramString, final Intent paramIntent)
    {
      final int i = this.mEffectiveUserId;
      this.mPattenString = paramString;
      this.mPendingLockCheck = LockPatternChecker.checkPassword(this.mLockPatternUtils, paramString, i, new LockPatternChecker.OnCheckCallback()
      {
        public void onChecked(boolean paramAnonymousBoolean, int paramAnonymousInt)
        {
          ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-set2(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this, null);
          Intent localIntent;
          if ((paramAnonymousBoolean) && (ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-wrap0(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this)) && (ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.mReturnCredentials))
          {
            localIntent = paramIntent;
            if (!ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get3(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this)) {
              break label95;
            }
          }
          label95:
          for (int i = 0;; i = 3)
          {
            localIntent.putExtra("type", i);
            paramIntent.putExtra("password", paramString);
            ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get0(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this).setResult(paramAnonymousBoolean, paramIntent, paramAnonymousInt, i);
            return;
          }
        }
      });
    }
    
    private void startDisappearAnimation(final Intent paramIntent)
    {
      if (this.mDisappearing) {
        return;
      }
      this.mDisappearing = true;
      if (getActivity().getThemeResId() == 2131821601)
      {
        this.mDisappearAnimationUtils.startAnimation(getActiveViews(), new Runnable()
        {
          public void run()
          {
            if ((ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.getActivity() == null) || (ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.getActivity().isFinishing())) {
              return;
            }
            ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.getActivity().setResult(-1, paramIntent);
            ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.getActivity().finish();
            ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.getActivity().overridePendingTransition(2131034123, 2131034124);
          }
        });
        return;
      }
      paramIntent.putExtra("power_on_psw", this.mPattenString);
      getActivity().setResult(-1, paramIntent);
      getActivity().finish();
    }
    
    private void startVerifyPassword(String paramString, final Intent paramIntent)
    {
      long l = getActivity().getIntent().getLongExtra("challenge", 0L);
      int i = this.mEffectiveUserId;
      final int j = this.mUserId;
      paramIntent = new LockPatternChecker.OnVerifyCallback()
      {
        public void onVerified(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt)
        {
          ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-set2(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this, null);
          boolean bool1 = false;
          if (paramAnonymousArrayOfByte != null)
          {
            boolean bool2 = true;
            bool1 = bool2;
            if (ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.mReturnCredentials)
            {
              paramIntent.putExtra("hw_auth_token", paramAnonymousArrayOfByte);
              bool1 = bool2;
            }
          }
          ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get0(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this).setResult(bool1, paramIntent, paramAnonymousInt, j);
        }
      };
      if (i == j) {}
      for (paramString = LockPatternChecker.verifyPassword(this.mLockPatternUtils, paramString, l, j, paramIntent);; paramString = LockPatternChecker.verifyTiedProfileChallenge(this.mLockPatternUtils, paramString, false, l, j, paramIntent))
      {
        this.mPendingLockCheck = paramString;
        return;
      }
    }
    
    protected void authenticationSucceeded()
    {
      this.mCredentialCheckResultTracker.setResult(true, new Intent(), 0, this.mEffectiveUserId);
    }
    
    protected int getLastTryErrorMessage()
    {
      if (this.mIsAlpha) {
        return 2131691206;
      }
      return 2131691205;
    }
    
    protected int getMetricsCategory()
    {
      return 30;
    }
    
    public void onClick(View paramView)
    {
      switch (paramView.getId())
      {
      default: 
        return;
      case 2131362013: 
        handleNext();
        return;
      case 2131362012: 
        getActivity().setResult(0);
        getActivity().finish();
        return;
      case 2131362297: 
        if ((this.mPasswordTextViewForPin.getText() == null) || (this.mPasswordTextViewForPin.getText().equals("")))
        {
          getActivity().finish();
          return;
        }
        this.mPasswordTextViewForPin.deleteLastChar();
        return;
      }
      handleNext();
    }
    
    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      mPreferences = getActivity().getPreferences(0);
      if (paramBundle != null) {
        this.mNumWrongConfirmAttempts = paramBundle.getInt("confirm_lock_password_fragment.key_num_wrong_confirm_attempts", 0);
      }
    }
    
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      int i = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mEffectiveUserId);
      paramBundle = paramLayoutInflater.inflate(2130968795, null);
      this.mPasswordTextViewForPin = ((OPPasswordTextViewForPin)paramBundle.findViewById(2131362283));
      this.mPasswordTextViewForPin.setCallBack(this.mPasswordInputCountCallBack);
      this.mDeleteButton = ((TextView)paramBundle.findViewById(2131362297));
      this.mDeleteButton.setOnClickListener(this);
      this.mDeleteButton.setVisibility(0);
      this.mConfirmButton = ((TextView)paramBundle.findViewById(2131362264));
      this.mConfirmButton.setOnClickListener(this);
      this.mConfirmButton.setVisibility(0);
      this.mPasswordTextViewForPin.setTextEmptyListener(new OPPasswordTextViewForPin.OnTextEmptyListerner()
      {
        public void onTextChanged(String paramAnonymousString)
        {
          if (paramAnonymousString.equals(""))
          {
            ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get1(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this).setText(2131690588);
            if (ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get5(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this).getText().length() < 16) {
              break label94;
            }
          }
          label94:
          for (int i = 1;; i = 0)
          {
            if (i != 0)
            {
              ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get5(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this).setEnabled(false);
              ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this.mHandler.postDelayed(new Runnable()
              {
                public void run()
                {
                  ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get5(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this).setEnabled(true);
                  ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-wrap2(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this);
                }
              }, 650L);
            }
            return;
            ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-get1(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this).setText(2131690587);
            break;
          }
        }
      });
      this.mHeaderTextView = ((TextView)paramBundle.findViewById(2131362010));
      boolean bool;
      if ((262144 == i) || (327680 == i)) {
        bool = true;
      }
      for (;;)
      {
        this.mIsAlpha = bool;
        this.mImm = ((InputMethodManager)getActivity().getSystemService("input_method"));
        paramLayoutInflater = getActivity().getIntent();
        if (paramLayoutInflater != null)
        {
          paramViewGroup = paramLayoutInflater.getCharSequenceExtra("com.android.settings.ConfirmCredentials.header");
          CharSequence localCharSequence = paramLayoutInflater.getCharSequenceExtra("com.android.settings.ConfirmCredentials.details");
          paramLayoutInflater = paramViewGroup;
          if (TextUtils.isEmpty(paramViewGroup)) {
            paramLayoutInflater = getString(getDefaultHeader());
          }
          if (TextUtils.isEmpty(localCharSequence)) {
            getString(getDefaultDetails());
          }
          this.mHeaderTextView.setText(paramLayoutInflater);
        }
        this.mAppearAnimationUtils = new AppearAnimationUtils(getContext(), 220L, 2.0F, 1.0F, AnimationUtils.loadInterpolator(getContext(), 17563662));
        this.mDisappearAnimationUtils = new DisappearAnimationUtils(getContext(), 110L, 1.0F, 0.5F, AnimationUtils.loadInterpolator(getContext(), 17563663));
        setAccessibilityTitle(this.mHeaderTextView.getText());
        this.mCredentialCheckResultTracker = ((CredentialCheckResultTracker)getFragmentManager().findFragmentByTag("check_lock_result"));
        if (this.mCredentialCheckResultTracker == null)
        {
          this.mCredentialCheckResultTracker = new CredentialCheckResultTracker();
          getFragmentManager().beginTransaction().add(this.mCredentialCheckResultTracker, "check_lock_result").commit();
        }
        this.mCredentialCheckResultTracker.setListener(this);
        return paramBundle;
        if (393216 == i) {
          break;
        }
        if (524288 == i) {
          bool = true;
        } else {
          bool = false;
        }
      }
    }
    
    public void onCredentialChecked(boolean paramBoolean1, Intent paramIntent, int paramInt1, int paramInt2, boolean paramBoolean2)
    {
      onPasswordChecked(paramBoolean1, paramIntent, paramInt1, paramInt2, paramBoolean2);
    }
    
    public void onDestroy()
    {
      super.onDestroy();
      if (this.mCountdownTimer != null)
      {
        this.mCountdownTimer.cancel();
        this.mCountdownTimer = null;
      }
      this.mCredentialCheckResultTracker.setListener(null);
      mPreferences.edit().putInt("confirm_lock_password_fragment.key_num_wrong_confirm_attempts", 0).commit();
    }
    
    public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
    {
      if ((paramInt == 0) || (paramInt == 6)) {}
      while (paramInt == 5)
      {
        handleNext();
        return true;
      }
      return false;
    }
    
    public void onFingerprintIconVisibilityChanged(boolean paramBoolean)
    {
      this.mUsingFingerprint = paramBoolean;
    }
    
    public void onPause()
    {
      super.onPause();
      mPreferences.edit().putInt("confirm_lock_password_fragment.key_num_wrong_confirm_attempts", this.mNumWrongConfirmAttempts).commit();
    }
    
    public void onResume()
    {
      long l = mPreferences.getLong("confirm_lock_password_fragment.key_time_wrong_confirm_attempts", 0L);
      if (System.currentTimeMillis() - l < 60000L)
      {
        this.mNumWrongConfirmAttempts = mPreferences.getInt("confirm_lock_password_fragment.key_num_wrong_confirm_attempts", 0);
        super.onResume();
        l = this.mLockPatternUtils.getLockoutAttemptDeadline(this.mEffectiveUserId);
        if (l == 0L) {
          break label107;
        }
        this.mCredentialCheckResultTracker.clearResult();
        handleAttemptLockout(l);
      }
      label107:
      do
      {
        return;
        this.mNumWrongConfirmAttempts = 0;
        mPreferences.edit().putInt("confirm_lock_password_fragment.key_num_wrong_confirm_attempts", this.mNumWrongConfirmAttempts).commit();
        break;
        resetState();
      } while (!isProfileChallenge());
      updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId));
    }
    
    protected void onShowError()
    {
      this.mPasswordEntry.setText(null);
    }
    
    public void onWindowFocusChanged(boolean paramBoolean)
    {
      if ((!paramBoolean) || (this.mBlockImm)) {}
    }
    
    public void prepareEnterAnimation()
    {
      super.prepareEnterAnimation();
      this.mHeaderTextView.setAlpha(0.0F);
      this.mCancelButton.setAlpha(0.0F);
      this.mFingerprintIcon.setAlpha(0.0F);
      this.mBlockImm = true;
    }
    
    public void startEnterAnimation()
    {
      super.startEnterAnimation();
      this.mAppearAnimationUtils.startAnimation(getActiveViews(), new Runnable()
      {
        public void run()
        {
          ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-set0(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this, false);
          ConfirmLockComplexPassword.ConfirmLockPasswordFragment.-wrap3(ConfirmLockComplexPassword.ConfirmLockPasswordFragment.this);
        }
      });
    }
  }
  
  public static class InternalActivity
    extends ConfirmLockComplexPassword
  {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ConfirmLockComplexPassword.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */