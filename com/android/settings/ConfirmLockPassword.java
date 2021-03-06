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
import android.os.SystemClock;
import android.os.UserManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import java.util.ArrayList;

public class ConfirmLockPassword
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
    implements View.OnClickListener, TextView.OnEditorActionListener, CredentialCheckResultTracker.Listener, TextWatcher
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
    private Button mCancelButton;
    private CountDownTimer mCountdownTimer;
    private CredentialCheckResultTracker mCredentialCheckResultTracker;
    private TextView mDetailsTextView;
    private DisappearAnimationUtils mDisappearAnimationUtils;
    private boolean mDisappearing = false;
    private TextView mHeaderTextView;
    private InputMethodManager mImm;
    private boolean mIsAlpha;
    private Button mNextButton;
    private int mNumWrongConfirmAttempts;
    private TextView mPasswordEntry;
    private TextViewInputDisabler mPasswordEntryInputDisabler;
    private int mPasswordMinLength = 4;
    private String mPattenString;
    private AsyncTask<?, ?, ?> mPendingLockCheck;
    private boolean mUsingFingerprint = false;
    
    private View[] getActiveViews()
    {
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(this.mHeaderTextView);
      localArrayList.add(this.mDetailsTextView);
      if (this.mCancelButton.getVisibility() == 0) {
        localArrayList.add(this.mCancelButton);
      }
      localArrayList.add(this.mPasswordEntry);
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
        return ConfirmLockPassword.-get0()[((j << 1) + (i << 2) + k)];
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
      this.mPasswordEntry.setEnabled(false);
      this.mCountdownTimer = new CountDownTimer(paramLong - l, 1000L)
      {
        public void onFinish()
        {
          ConfirmLockPassword.ConfirmLockPasswordFragment.-wrap2(ConfirmLockPassword.ConfirmLockPasswordFragment.this);
          ConfirmLockPassword.ConfirmLockPasswordFragment.this.mErrorTextView.setText("");
          if (ConfirmLockPassword.ConfirmLockPasswordFragment.this.isProfileChallenge()) {
            ConfirmLockPassword.ConfirmLockPasswordFragment.this.updateErrorMessage(ConfirmLockPassword.ConfirmLockPasswordFragment.this.mLockPatternUtils.getCurrentFailedPasswordAttempts(ConfirmLockPassword.ConfirmLockPasswordFragment.this.mEffectiveUserId));
          }
          ConfirmLockPassword.ConfirmLockPasswordFragment.-set1(ConfirmLockPassword.ConfirmLockPasswordFragment.this, 0);
          ConfirmLockPassword.ConfirmLockPasswordFragment.-get5().edit().putInt("confirm_lock_password_fragment.key_num_wrong_confirm_attempts", ConfirmLockPassword.ConfirmLockPasswordFragment.-get3(ConfirmLockPassword.ConfirmLockPasswordFragment.this)).commit();
        }
        
        public void onTick(long paramAnonymousLong)
        {
          if (!ConfirmLockPassword.ConfirmLockPasswordFragment.this.isAdded()) {
            return;
          }
          int i = (int)(paramAnonymousLong / 1000L);
          ConfirmLockPassword.ConfirmLockPasswordFragment.this.showError(ConfirmLockPassword.ConfirmLockPasswordFragment.this.getString(2131692052, new Object[] { Integer.valueOf(i) }), 0L);
        }
      }.start();
    }
    
    private void handleNext()
    {
      if ((this.mPendingLockCheck != null) || (this.mDisappearing)) {
        return;
      }
      String str = this.mPasswordEntry.getText().toString();
      if (str.length() < 4)
      {
        this.mHeaderTextView.setText(getString(2131691209, new Object[] { Integer.valueOf(4) }));
        return;
      }
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
      return getActivity() instanceof ConfirmLockPassword.InternalActivity;
    }
    
    private void onPasswordChecked(boolean paramBoolean1, Intent paramIntent, int paramInt1, int paramInt2, boolean paramBoolean2)
    {
      this.mPasswordEntryInputDisabler.setInputEnabled(true);
      if (paramBoolean1)
      {
        if (paramBoolean2) {
          reportSuccessfullAttempt();
        }
        startDisappearAnimation(paramIntent);
        checkForPendingIntent();
      }
      for (;;)
      {
        return;
        if (this.mNumWrongConfirmAttempts == 0) {
          mPreferences.edit().putLong("confirm_lock_password_fragment.key_time_wrong_confirm_attempts", System.currentTimeMillis()).commit();
        }
        paramInt1 = this.mNumWrongConfirmAttempts + 1;
        this.mNumWrongConfirmAttempts = paramInt1;
        if (paramInt1 >= 5)
        {
          refreshLockScreen();
          handleAttemptLockout(this.mLockPatternUtils.setLockoutAttemptDeadline(paramInt2, 60000));
        }
        while (paramBoolean2)
        {
          reportFailedAttempt();
          return;
          paramIntent = String.format(getResources().getString(2131690752), new Object[] { getResources().getString(2131692025), getResources().getString(2131690181) });
          this.mHeaderTextView.setText(paramIntent);
          this.mPasswordEntry.setText("");
        }
      }
    }
    
    private void resetState()
    {
      if (this.mBlockImm) {
        return;
      }
      this.mErrorTextView.setText("");
      this.mHeaderTextView.setText(getDefaultHeader());
      this.mPasswordEntry.setEnabled(true);
      this.mPasswordEntryInputDisabler.setInputEnabled(true);
      if (shouldAutoShowSoftKeyboard()) {
        this.mImm.showSoftInput(this.mPasswordEntry, 1);
      }
    }
    
    private boolean shouldAutoShowSoftKeyboard()
    {
      return (this.mPasswordEntry.isEnabled()) && (!this.mUsingFingerprint);
    }
    
    private void startCheckPassword(final String paramString, final Intent paramIntent)
    {
      final int i = this.mEffectiveUserId;
      this.mPattenString = paramString;
      this.mPendingLockCheck = LockPatternChecker.checkPassword(this.mLockPatternUtils, paramString, i, new LockPatternChecker.OnCheckCallback()
      {
        public void onChecked(boolean paramAnonymousBoolean, int paramAnonymousInt)
        {
          ConfirmLockPassword.ConfirmLockPasswordFragment.-set2(ConfirmLockPassword.ConfirmLockPasswordFragment.this, null);
          Intent localIntent;
          if ((paramAnonymousBoolean) && (ConfirmLockPassword.ConfirmLockPasswordFragment.-wrap0(ConfirmLockPassword.ConfirmLockPasswordFragment.this)) && (ConfirmLockPassword.ConfirmLockPasswordFragment.this.mReturnCredentials))
          {
            localIntent = paramIntent;
            if (!ConfirmLockPassword.ConfirmLockPasswordFragment.-get2(ConfirmLockPassword.ConfirmLockPasswordFragment.this)) {
              break label95;
            }
          }
          label95:
          for (int i = 0;; i = 3)
          {
            localIntent.putExtra("type", i);
            paramIntent.putExtra("password", paramString);
            ConfirmLockPassword.ConfirmLockPasswordFragment.-get0(ConfirmLockPassword.ConfirmLockPasswordFragment.this).setResult(paramAnonymousBoolean, paramIntent, paramAnonymousInt, i);
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
            if ((ConfirmLockPassword.ConfirmLockPasswordFragment.this.getActivity() == null) || (ConfirmLockPassword.ConfirmLockPasswordFragment.this.getActivity().isFinishing())) {
              return;
            }
            ConfirmLockPassword.ConfirmLockPasswordFragment.this.getActivity().setResult(-1, paramIntent);
            ConfirmLockPassword.ConfirmLockPasswordFragment.this.getActivity().finish();
            ConfirmLockPassword.ConfirmLockPasswordFragment.this.getActivity().overridePendingTransition(2131034123, 2131034124);
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
          ConfirmLockPassword.ConfirmLockPasswordFragment.-set2(ConfirmLockPassword.ConfirmLockPasswordFragment.this, null);
          boolean bool1 = false;
          if (paramAnonymousArrayOfByte != null)
          {
            boolean bool2 = true;
            bool1 = bool2;
            if (ConfirmLockPassword.ConfirmLockPasswordFragment.this.mReturnCredentials)
            {
              paramIntent.putExtra("hw_auth_token", paramAnonymousArrayOfByte);
              bool1 = bool2;
            }
          }
          ConfirmLockPassword.ConfirmLockPasswordFragment.-get0(ConfirmLockPassword.ConfirmLockPasswordFragment.this).setResult(bool1, paramIntent, paramAnonymousInt, j);
        }
      };
      if (i == j) {}
      for (paramString = LockPatternChecker.verifyPassword(this.mLockPatternUtils, paramString, l, j, paramIntent);; paramString = LockPatternChecker.verifyTiedProfileChallenge(this.mLockPatternUtils, paramString, false, l, j, paramIntent))
      {
        this.mPendingLockCheck = paramString;
        return;
      }
    }
    
    private void updateNextButton()
    {
      if (this.mPasswordEntry.getText().toString().length() >= this.mPasswordMinLength)
      {
        this.mNextButton.setEnabled(true);
        return;
      }
      this.mNextButton.setEnabled(false);
    }
    
    public void afterTextChanged(Editable paramEditable)
    {
      updateNextButton();
    }
    
    protected void authenticationSucceeded()
    {
      this.mCredentialCheckResultTracker.setResult(true, new Intent(), 0, this.mEffectiveUserId);
    }
    
    public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
    
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
      }
      getActivity().setResult(0);
      getActivity().finish();
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
      View localView = paramLayoutInflater.inflate(2130968645, null);
      this.mPasswordEntry = ((TextView)localView.findViewById(2131362011));
      this.mPasswordEntry.requestFocus();
      this.mPasswordEntry.setOnEditorActionListener(this);
      this.mPasswordEntryInputDisabler = new TextViewInputDisabler(this.mPasswordEntry);
      this.mPasswordEntry.addTextChangedListener(this);
      this.mCancelButton = ((Button)localView.findViewById(2131362012));
      this.mCancelButton.setOnClickListener(this);
      this.mNextButton = ((Button)localView.findViewById(2131362013));
      this.mNextButton.setOnClickListener(this);
      updateNextButton();
      this.mHeaderTextView = ((TextView)localView.findViewById(2131362010));
      this.mDetailsTextView = ((TextView)localView.findViewById(2131362035));
      this.mErrorTextView = ((TextView)localView.findViewById(2131362038));
      boolean bool;
      if ((262144 == i) || (327680 == i))
      {
        bool = true;
        label191:
        this.mIsAlpha = bool;
        this.mImm = ((InputMethodManager)getActivity().getSystemService("input_method"));
        paramLayoutInflater = getActivity().getIntent();
        if (paramLayoutInflater != null)
        {
          paramViewGroup = paramLayoutInflater.getCharSequenceExtra("com.android.settings.ConfirmCredentials.header");
          paramBundle = paramLayoutInflater.getCharSequenceExtra("com.android.settings.ConfirmCredentials.details");
          paramLayoutInflater = paramViewGroup;
          if (TextUtils.isEmpty(paramViewGroup)) {
            paramLayoutInflater = getString(getDefaultHeader());
          }
          paramViewGroup = paramBundle;
          if (TextUtils.isEmpty(paramBundle)) {
            paramViewGroup = getString(getDefaultDetails());
          }
          this.mHeaderTextView.setText(paramLayoutInflater);
          this.mDetailsTextView.setText(paramViewGroup);
        }
        i = this.mPasswordEntry.getInputType();
        paramLayoutInflater = this.mPasswordEntry;
        if (!this.mIsAlpha) {
          break label487;
        }
      }
      for (;;)
      {
        paramLayoutInflater.setInputType(i);
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
        return localView;
        if (393216 == i) {
          break;
        }
        if (524288 == i)
        {
          bool = true;
          break label191;
        }
        bool = false;
        break label191;
        label487:
        i = 18;
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
      super.onResume();
      long l = this.mLockPatternUtils.getLockoutAttemptDeadline(this.mEffectiveUserId);
      if (l != 0L)
      {
        this.mCredentialCheckResultTracker.clearResult();
        handleAttemptLockout(l);
      }
      for (;;)
      {
        this.mNumWrongConfirmAttempts = 0;
        if (l == 0L) {
          break;
        }
        this.mNumWrongConfirmAttempts = mPreferences.getInt("confirm_lock_password_fragment.key_num_wrong_confirm_attempts", 0);
        return;
        resetState();
        this.mErrorTextView.setText("");
        if (isProfileChallenge()) {
          updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId));
        }
      }
      this.mNumWrongConfirmAttempts = 0;
      mPreferences.edit().putInt("confirm_lock_password_fragment.key_num_wrong_confirm_attempts", this.mNumWrongConfirmAttempts).commit();
    }
    
    protected void onShowError()
    {
      this.mPasswordEntry.setText(null);
    }
    
    public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onWindowFocusChanged(boolean paramBoolean)
    {
      if ((!paramBoolean) || (this.mBlockImm)) {
        return;
      }
      this.mPasswordEntry.post(new Runnable()
      {
        public void run()
        {
          if (ConfirmLockPassword.ConfirmLockPasswordFragment.-wrap1(ConfirmLockPassword.ConfirmLockPasswordFragment.this))
          {
            ConfirmLockPassword.ConfirmLockPasswordFragment.-wrap2(ConfirmLockPassword.ConfirmLockPasswordFragment.this);
            return;
          }
          ConfirmLockPassword.ConfirmLockPasswordFragment.-get1(ConfirmLockPassword.ConfirmLockPasswordFragment.this).hideSoftInputFromWindow(ConfirmLockPassword.ConfirmLockPasswordFragment.-get4(ConfirmLockPassword.ConfirmLockPasswordFragment.this).getWindowToken(), 1);
        }
      });
    }
    
    public void prepareEnterAnimation()
    {
      super.prepareEnterAnimation();
      this.mHeaderTextView.setAlpha(0.0F);
      this.mDetailsTextView.setAlpha(0.0F);
      this.mCancelButton.setAlpha(0.0F);
      this.mPasswordEntry.setAlpha(0.0F);
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
          ConfirmLockPassword.ConfirmLockPasswordFragment.-set0(ConfirmLockPassword.ConfirmLockPasswordFragment.this, false);
          ConfirmLockPassword.ConfirmLockPasswordFragment.-wrap2(ConfirmLockPassword.ConfirmLockPasswordFragment.this);
        }
      });
    }
  }
  
  public static class InternalActivity
    extends ConfirmLockPassword
  {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ConfirmLockPassword.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */