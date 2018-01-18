package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.RemovalCallback;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.security.KeyStore;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.applications.LayoutPreference;
import com.android.settings.fingerprint.FingerprintEnrollFindSensor;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;
import java.util.List;

public class OxygenChooseLockGeneric
  extends SettingsActivity
{
  public static final String CONFIRM_CREDENTIALS = "confirm_credentials";
  
  Class<? extends Fragment> getFragmentClass()
  {
    return ChooseLockGenericFragment.class;
  }
  
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":settings:show_fragment", getFragmentClass().getName());
    String str = localIntent.getAction();
    if (("android.app.action.SET_NEW_PASSWORD".equals(str)) || ("android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD".equals(str))) {
      localIntent.putExtra(":settings:hide_drawer", true);
    }
    return localIntent;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return ChooseLockGenericFragment.class.getName().equals(paramString);
  }
  
  public static class ChooseLockGenericFragment
    extends SettingsPreferenceFragment
  {
    private static final int CHOOSE_LOCK_BEFORE_FINGERPRINT_REQUEST = 103;
    private static final int CHOOSE_LOCK_REQUEST = 102;
    private static final int CONFIRM_EXISTING_REQUEST = 100;
    private static final int ENABLE_ENCRYPTION_REQUEST = 101;
    public static final String ENCRYPT_REQUESTED_DISABLED = "encrypt_requested_disabled";
    public static final String ENCRYPT_REQUESTED_QUALITY = "encrypt_requested_quality";
    public static final String HIDE_DISABLED_PREFS = "hide_disabled_prefs";
    private static final String KEY_SKIP_FINGERPRINT = "unlock_skip_fingerprint";
    private static final String KEY_UNLOCK_SET_MANAGED = "unlock_set_managed";
    private static final String KEY_UNLOCK_SET_NONE = "unlock_set_none";
    private static final String KEY_UNLOCK_SET_OFF = "unlock_set_off";
    private static final String KEY_UNLOCK_SET_PASSWORD = "unlock_set_password";
    private static final String KEY_UNLOCK_SET_PATTERN = "unlock_set_pattern";
    private static final String KEY_UNLOCK_SET_PIN = "unlock_set_pin";
    public static final String MINIMUM_QUALITY_KEY = "minimum_quality";
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final String PASSWORD_CONFIRMED = "password_confirmed";
    private static final int SKIP_FINGERPRINT_REQUEST = 104;
    private static final String TAG = "ChooseLockGenericFragment";
    public static final String TAG_FRP_WARNING_DIALOG = "frp_warning_dialog";
    private static final String WAITING_FOR_CONFIRMATION = "waiting_for_confirmation";
    private long mChallenge;
    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    private DevicePolicyManager mDPM;
    private boolean mEncryptionRequestDisabled;
    private int mEncryptionRequestQuality;
    private FingerprintManager mFingerprintManager;
    private boolean mForChangeCredRequiredForBoot = false;
    protected boolean mForFingerprint = false;
    private boolean mHasChallenge = false;
    private boolean mHideDrawer = false;
    private boolean mIsSetNewPassword = false;
    private KeyStore mKeyStore;
    private LockPatternUtils mLockPatternUtils;
    private ManagedLockPasswordProvider mManagedPasswordProvider;
    private boolean mPasswordConfirmed = false;
    private boolean mRequirePassword;
    private int mUserId;
    private String mUserPassword;
    private boolean mWaitingForConfirmation = false;
    
    private Intent getIntentForUnlockMethod(int paramInt, boolean paramBoolean)
    {
      Intent localIntent = null;
      Activity localActivity = getActivity();
      if (paramInt >= 524288) {
        localIntent = getLockManagedPasswordIntent(this.mRequirePassword, this.mUserPassword);
      }
      for (;;)
      {
        if (localIntent != null) {
          localIntent.putExtra(":settings:hide_drawer", this.mHideDrawer);
        }
        return localIntent;
        if (paramInt >= 131072)
        {
          int j = this.mDPM.getPasswordMinimumLength(null, this.mUserId);
          int i = j;
          if (j < 4) {
            i = 4;
          }
          j = this.mDPM.getPasswordMaximumLength(paramInt);
          if (this.mHasChallenge) {
            localIntent = getLockPasswordIntent(localActivity, paramInt, i, j, this.mRequirePassword, this.mChallenge, this.mUserId);
          } else {
            localIntent = getLockPasswordIntent(localActivity, paramInt, i, j, this.mRequirePassword, this.mUserPassword, this.mUserId);
          }
        }
        else if (paramInt == 65536)
        {
          if (this.mHasChallenge) {
            localIntent = getLockPatternIntent(localActivity, this.mRequirePassword, this.mChallenge, this.mUserId);
          } else {
            localIntent = getLockPatternIntent(localActivity, this.mRequirePassword, this.mUserPassword, this.mUserId);
          }
        }
      }
    }
    
    private String getKeyForCurrent()
    {
      int i = UserManager.get(getContext()).getCredentialOwnerProfile(this.mUserId);
      if (this.mLockPatternUtils.isLockScreenDisabled(i)) {
        return "unlock_set_off";
      }
      switch (this.mLockPatternUtils.getKeyguardStoredPasswordQuality(i))
      {
      default: 
        return null;
      case 65536: 
        return "unlock_set_pattern";
      case 131072: 
      case 196608: 
        return "unlock_set_pin";
      case 262144: 
      case 327680: 
      case 393216: 
        return "unlock_set_password";
      case 524288: 
        return "unlock_set_managed";
      }
      return "unlock_set_none";
    }
    
    private int getResIdForFactoryResetProtectionWarningMessage()
    {
      boolean bool1 = this.mFingerprintManager.hasEnrolledFingerprints(this.mUserId);
      boolean bool2 = Utils.isManagedProfile(UserManager.get(getActivity()), this.mUserId);
      switch (this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId))
      {
      default: 
        if ((bool1) && (bool2)) {
          return 2131691197;
        }
        break;
      case 65536: 
        if ((bool1) && (bool2)) {
          return 2131691191;
        }
        if ((!bool1) || (bool2))
        {
          if (bool2) {
            return 2131691190;
          }
        }
        else {
          return 2131691183;
        }
        return 2131691182;
      case 131072: 
      case 196608: 
        if ((bool1) && (bool2)) {
          return 2131691193;
        }
        if ((!bool1) || (bool2))
        {
          if (bool2) {
            return 2131691192;
          }
        }
        else {
          return 2131691185;
        }
        return 2131691184;
      case 262144: 
      case 327680: 
      case 393216: 
      case 524288: 
        if ((bool1) && (bool2)) {
          return 2131691195;
        }
        if ((!bool1) || (bool2))
        {
          if (bool2) {
            return 2131691194;
          }
        }
        else {
          return 2131691187;
        }
        return 2131691186;
      }
      if ((!bool1) || (bool2))
      {
        if (bool2) {
          return 2131691196;
        }
      }
      else {
        return 2131691189;
      }
      return 2131691188;
    }
    
    private int getResIdForFactoryResetProtectionWarningTitle()
    {
      if (Utils.isManagedProfile(UserManager.get(getActivity()), this.mUserId)) {
        return 2131691181;
      }
      return 2131691180;
    }
    
    private boolean isUnlockMethodSecure(String paramString)
    {
      return (!"unlock_set_off".equals(paramString)) && (!"unlock_set_none".equals(paramString));
    }
    
    private void maybeEnableEncryption(int paramInt, boolean paramBoolean)
    {
      boolean bool = false;
      Object localObject = (DevicePolicyManager)getSystemService("device_policy");
      if ((!UserManager.get(getActivity()).isAdminUser()) || (this.mUserId != UserHandle.myUserId()) || (!LockPatternUtils.isDeviceEncryptionEnabled()) || (LockPatternUtils.isFileEncryptionEnabled())) {}
      while (this.mForChangeCredRequiredForBoot)
      {
        finish();
        return;
        if (!((DevicePolicyManager)localObject).getDoNotAskCredentialsOnBoot())
        {
          this.mEncryptionRequestQuality = paramInt;
          this.mEncryptionRequestDisabled = paramBoolean;
          localObject = getIntentForUnlockMethod(paramInt, paramBoolean);
          ((Intent)localObject).putExtra("for_cred_req_boot", this.mForChangeCredRequiredForBoot);
          Activity localActivity = getActivity();
          paramBoolean = AccessibilityManager.getInstance(localActivity).isEnabled();
          LockPatternUtils localLockPatternUtils = this.mLockPatternUtils;
          if (paramBoolean)
          {
            paramBoolean = bool;
            localObject = getEncryptionInterstitialIntent(localActivity, paramInt, localLockPatternUtils.isCredentialRequiredToDecrypt(paramBoolean), (Intent)localObject);
            ((Intent)localObject).putExtra("for_fingerprint", this.mForFingerprint);
            ((Intent)localObject).putExtra(":settings:hide_drawer", this.mHideDrawer);
            if ((!this.mIsSetNewPassword) || (!this.mHasChallenge)) {
              break label199;
            }
          }
          label199:
          for (paramInt = 103;; paramInt = 101)
          {
            startActivityForResult((Intent)localObject, paramInt);
            return;
            paramBoolean = true;
            break;
          }
        }
      }
      this.mRequirePassword = false;
      updateUnlockMethodAndFinish(paramInt, paramBoolean);
    }
    
    private void removeAllFingerprintForUserAndFinish(final int paramInt)
    {
      if ((this.mFingerprintManager != null) && (this.mFingerprintManager.isHardwareDetected()))
      {
        if (this.mFingerprintManager.hasEnrolledFingerprints(paramInt))
        {
          this.mFingerprintManager.setActiveUser(paramInt);
          Fingerprint localFingerprint = new Fingerprint(null, paramInt, 0, 0L);
          this.mFingerprintManager.remove(localFingerprint, paramInt, new FingerprintManager.RemovalCallback()
          {
            public void onRemovalError(Fingerprint paramAnonymousFingerprint, int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
            {
              Log.v("ChooseLockGenericFragment", "Fingerprint removed: " + paramAnonymousFingerprint.getFingerId());
              if (paramAnonymousFingerprint.getFingerId() == 0) {
                OxygenChooseLockGeneric.ChooseLockGenericFragment.-wrap1(OxygenChooseLockGeneric.ChooseLockGenericFragment.this, paramInt);
              }
            }
            
            public void onRemovalSucceeded(Fingerprint paramAnonymousFingerprint)
            {
              if (paramAnonymousFingerprint.getFingerId() == 0) {
                OxygenChooseLockGeneric.ChooseLockGenericFragment.-wrap1(OxygenChooseLockGeneric.ChooseLockGenericFragment.this, paramInt);
              }
            }
          });
          return;
        }
        removeManagedProfileFingerprintsAndFinishIfNecessary(paramInt);
        return;
      }
      finish();
    }
    
    private void removeManagedProfileFingerprintsAndFinishIfNecessary(int paramInt)
    {
      this.mFingerprintManager.setActiveUser(UserHandle.myUserId());
      Object localObject = UserManager.get(getActivity());
      int j = 0;
      int i = j;
      if (!((UserManager)localObject).getUserInfo(paramInt).isManagedProfile())
      {
        localObject = ((UserManager)localObject).getProfiles(paramInt);
        int k = ((List)localObject).size();
        paramInt = 0;
        UserInfo localUserInfo;
        for (;;)
        {
          i = j;
          if (paramInt >= k) {
            break label116;
          }
          localUserInfo = (UserInfo)((List)localObject).get(paramInt);
          if ((localUserInfo.isManagedProfile()) && (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(localUserInfo.id))) {
            break;
          }
          paramInt += 1;
        }
        removeAllFingerprintForUserAndFinish(localUserInfo.id);
        i = 1;
      }
      label116:
      if (i == 0) {
        finish();
      }
    }
    
    private boolean setUnlockMethod(String paramString)
    {
      EventLog.writeEvent(90200, paramString);
      if ("unlock_set_off".equals(paramString))
      {
        updateUnlockMethodAndFinish(0, true);
        return true;
      }
      if ("unlock_set_none".equals(paramString))
      {
        updateUnlockMethodAndFinish(0, false);
        return true;
      }
      if ("unlock_set_managed".equals(paramString))
      {
        maybeEnableEncryption(524288, false);
        return true;
      }
      if ("unlock_set_pattern".equals(paramString))
      {
        maybeEnableEncryption(65536, false);
        return true;
      }
      if ("unlock_set_pin".equals(paramString))
      {
        maybeEnableEncryption(131072, false);
        return true;
      }
      if ("unlock_set_password".equals(paramString))
      {
        maybeEnableEncryption(262144, false);
        return true;
      }
      Log.e("ChooseLockGenericFragment", "Encountered unknown unlock method to set: " + paramString);
      return false;
    }
    
    private void showFactoryResetProtectionWarningDialog(String paramString)
    {
      FactoryResetProtectionWarningDialog.newInstance(getResIdForFactoryResetProtectionWarningTitle(), getResIdForFactoryResetProtectionWarningMessage(), paramString).show(getChildFragmentManager(), "frp_warning_dialog");
    }
    
    private void updateCurrentPreference()
    {
      Preference localPreference = findPreference(getKeyForCurrent());
      if (localPreference != null) {
        localPreference.setSummary(2131691166);
      }
    }
    
    private void updatePreferenceSummaryIfNeeded()
    {
      if (!StorageManager.isBlockEncrypted()) {
        return;
      }
      if (StorageManager.isNonDefaultBlockEncrypted()) {
        return;
      }
      if (AccessibilityManager.getInstance(getActivity()).getEnabledAccessibilityServiceList(-1).isEmpty()) {
        return;
      }
      String str1 = getString(2131692390);
      PreferenceScreen localPreferenceScreen = getPreferenceScreen();
      int j = localPreferenceScreen.getPreferenceCount();
      int i = 0;
      if (i < j)
      {
        Preference localPreference = localPreferenceScreen.getPreference(i);
        String str2 = localPreference.getKey();
        if (str2.equals("unlock_set_pattern")) {}
        for (;;)
        {
          label86:
          localPreference.setSummary(str1);
          do
          {
            i += 1;
            break;
            if ((str2.equals("unlock_set_pin")) || (str2.equals("unlock_set_password"))) {
              break label86;
            }
          } while (!str2.equals("unlock_set_managed"));
        }
      }
    }
    
    private void updatePreferenceText()
    {
      boolean bool = false;
      if (this.mForFingerprint)
      {
        String[] arrayOfString = new String[3];
        arrayOfString[0] = "unlock_set_pattern";
        arrayOfString[1] = "unlock_set_pin";
        arrayOfString[2] = "unlock_set_password";
        if (OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) {}
        for (int[] arrayOfInt = { 2131691175, 2131691176, 2131691177 };; arrayOfInt = new int[] { 2131691167, 2131691168, 2131691169 })
        {
          int i = 0;
          while (i < arrayOfString.length)
          {
            Preference localPreference = findPreference(arrayOfString[i]);
            if (localPreference != null) {
              localPreference.setTitle(arrayOfInt[i]);
            }
            i += 1;
          }
        }
      }
      if (this.mManagedPasswordProvider.isSettingManagedPasswordSupported()) {
        findPreference("unlock_set_managed").setTitle(this.mManagedPasswordProvider.getPickerOptionTitle(this.mForFingerprint));
      }
      for (;;)
      {
        if (this.mForFingerprint) {
          bool = this.mIsSetNewPassword;
        }
        if (!bool) {
          removePreference("unlock_skip_fingerprint");
        }
        return;
        removePreference("unlock_set_managed");
      }
    }
    
    private void updatePreferencesOrFinish()
    {
      Object localObject = getActivity().getIntent();
      int i = ((Intent)localObject).getIntExtra("lockscreen.password_type", -1);
      if (i == -1)
      {
        i = upgradeQuality(((Intent)localObject).getIntExtra("minimum_quality", -1));
        boolean bool = ((Intent)localObject).getBooleanExtra("hide_disabled_prefs", false);
        localObject = getPreferenceScreen();
        if (localObject != null) {
          ((PreferenceScreen)localObject).removeAll();
        }
        addPreferences();
        disableUnusablePreferences(i, bool);
        updatePreferenceText();
        updateCurrentPreference();
        updatePreferenceSummaryIfNeeded();
        return;
      }
      updateUnlockMethodAndFinish(i, false);
    }
    
    private int upgradeQuality(int paramInt)
    {
      return upgradeQualityForDPM(paramInt);
    }
    
    private int upgradeQualityForDPM(int paramInt)
    {
      int j = this.mDPM.getPasswordQuality(null, this.mUserId);
      int i = paramInt;
      if (paramInt < j) {
        i = j;
      }
      return i;
    }
    
    protected void addHeaderView()
    {
      if (this.mForFingerprint)
      {
        setHeaderView(2130968876);
        if (this.mIsSetNewPassword) {
          ((TextView)getHeaderView().findViewById(2131362396)).setText(2131691171);
        }
      }
    }
    
    protected void addPreferences()
    {
      addPreferencesFromResource(2131230823);
      findPreference("unlock_set_none").setViewId(2131361810);
      findPreference("unlock_skip_fingerprint").setViewId(2131361810);
      findPreference("unlock_set_pin").setViewId(2131361811);
      findPreference("unlock_set_password").setViewId(2131361812);
    }
    
    protected void disableUnusablePreferences(int paramInt, boolean paramBoolean)
    {
      disableUnusablePreferencesImpl(paramInt, paramBoolean);
    }
    
    protected void disableUnusablePreferencesImpl(int paramInt, boolean paramBoolean)
    {
      PreferenceScreen localPreferenceScreen = getPreferenceScreen();
      int k = this.mDPM.getPasswordQuality(null, this.mUserId);
      RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfPasswordQualityIsSet(getActivity(), this.mUserId);
      int j = localPreferenceScreen.getPreferenceCount() - 1;
      if (j >= 0)
      {
        Preference localPreference = localPreferenceScreen.getPreference(j);
        String str;
        boolean bool;
        int n;
        int i1;
        int m;
        int i;
        if ((localPreference instanceof RestrictedPreference))
        {
          str = localPreference.getKey();
          bool = true;
          n = 1;
          i1 = 1;
          m = 1;
          i = 0;
          if (!"unlock_set_off".equals(str)) {
            break label169;
          }
          if (paramInt > 0) {
            break label158;
          }
          bool = true;
          label102:
          if (getResources().getBoolean(2131558422))
          {
            bool = false;
            m = 0;
          }
          if (k <= 0) {
            break label164;
          }
          i = 1;
          label128:
          if (paramBoolean) {
            m = bool;
          }
          if (m != 0) {
            break label438;
          }
          localPreferenceScreen.removePreference(localPreference);
        }
        for (;;)
        {
          j -= 1;
          break;
          label158:
          bool = false;
          break label102;
          label164:
          i = 0;
          break label128;
          label169:
          if ("unlock_set_none".equals(str))
          {
            m = i1;
            if (this.mUserId != UserHandle.myUserId()) {
              m = 0;
            }
            if (paramInt <= 0) {}
            for (bool = true;; bool = false)
            {
              if (k <= 0) {
                break label219;
              }
              i = 1;
              break;
            }
            label219:
            i = 0;
            break label128;
          }
          if ("unlock_set_pattern".equals(str))
          {
            if (paramInt <= 65536) {}
            for (bool = true;; bool = false)
            {
              if (k <= 65536) {
                break label265;
              }
              i = 1;
              m = n;
              break;
            }
            label265:
            i = 0;
            m = n;
            break label128;
          }
          if ("unlock_set_pin".equals(str))
          {
            if (paramInt <= 196608) {}
            for (bool = true;; bool = false)
            {
              if (k <= 196608) {
                break label317;
              }
              i = 1;
              m = n;
              break;
            }
            label317:
            i = 0;
            m = n;
            break label128;
          }
          if ("unlock_set_password".equals(str))
          {
            if (paramInt <= 393216) {}
            for (bool = true;; bool = false)
            {
              if (k <= 393216) {
                break label369;
              }
              i = 1;
              m = n;
              break;
            }
            label369:
            i = 0;
            m = n;
            break label128;
          }
          m = n;
          if (!"unlock_set_managed".equals(str)) {
            break label128;
          }
          if (paramInt <= 524288) {}
          for (bool = this.mManagedPasswordProvider.isManagedPasswordChoosable();; bool = false)
          {
            if (k <= 524288) {
              break label429;
            }
            i = 1;
            m = n;
            break;
          }
          label429:
          i = 0;
          m = n;
          break label128;
          label438:
          if ((i != 0) && (localEnforcedAdmin != null))
          {
            ((RestrictedPreference)localPreference).setDisabledByAdmin(localEnforcedAdmin);
          }
          else if (!bool)
          {
            ((RestrictedPreference)localPreference).setDisabledByAdmin(null);
            localPreference.setSummary(2131691172);
            localPreference.setEnabled(false);
          }
          else
          {
            ((RestrictedPreference)localPreference).setDisabledByAdmin(null);
          }
        }
      }
    }
    
    protected Intent getEncryptionInterstitialIntent(Context paramContext, int paramInt, boolean paramBoolean, Intent paramIntent)
    {
      return EncryptionInterstitial.createStartIntent(paramContext, paramInt, paramBoolean, paramIntent);
    }
    
    protected int getHelpResource()
    {
      return 2131693021;
    }
    
    protected Intent getLockManagedPasswordIntent(boolean paramBoolean, String paramString)
    {
      return this.mManagedPasswordProvider.createIntent(paramBoolean, paramString);
    }
    
    protected Intent getLockPasswordIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, long paramLong, int paramInt4)
    {
      return ChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean, paramLong, paramInt4);
    }
    
    protected Intent getLockPasswordIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, String paramString, int paramInt4)
    {
      return ChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean, paramString, paramInt4);
    }
    
    protected Intent getLockPasswordIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4)
    {
      return ChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean1, paramBoolean2, paramInt4);
    }
    
    protected Intent getLockPatternIntent(Context paramContext, boolean paramBoolean, long paramLong, int paramInt)
    {
      return OxygenChooseLockPattern.createIntent(paramContext, paramBoolean, paramLong, paramInt);
    }
    
    protected Intent getLockPatternIntent(Context paramContext, boolean paramBoolean, String paramString, int paramInt)
    {
      return OxygenChooseLockPattern.createIntent(paramContext, paramBoolean, paramString, paramInt);
    }
    
    protected Intent getLockPatternIntent(Context paramContext, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
    {
      return OxygenChooseLockPattern.createIntent(paramContext, paramBoolean1, paramBoolean2, paramInt);
    }
    
    protected int getMetricsCategory()
    {
      return 27;
    }
    
    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      this.mWaitingForConfirmation = false;
      if ((paramInt1 == 100) && (paramInt2 == -1))
      {
        this.mPasswordConfirmed = true;
        this.mUserPassword = paramIntent.getStringExtra("password");
        updatePreferencesOrFinish();
        if (this.mForChangeCredRequiredForBoot)
        {
          if (TextUtils.isEmpty(this.mUserPassword)) {
            break label92;
          }
          maybeEnableEncryption(this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId), false);
        }
      }
      for (;;)
      {
        if ((paramInt1 == 0) && (this.mForChangeCredRequiredForBoot)) {
          finish();
        }
        return;
        label92:
        finish();
        continue;
        if ((paramInt1 == 102) || (paramInt1 == 101))
        {
          if ((paramInt2 != 0) || (this.mForChangeCredRequiredForBoot))
          {
            getActivity().setResult(paramInt2, paramIntent);
            finish();
          }
        }
        else
        {
          Object localObject;
          if ((paramInt1 == 103) && (paramInt2 == 1))
          {
            localObject = new Intent(getActivity(), FingerprintEnrollFindSensor.class);
            if (paramIntent != null) {
              ((Intent)localObject).putExtras(paramIntent.getExtras());
            }
            startActivity((Intent)localObject);
            finish();
          }
          else if (paramInt1 == 104)
          {
            if (paramInt2 != 0)
            {
              localObject = getActivity();
              int i = paramInt2;
              if (paramInt2 == 1) {
                i = -1;
              }
              ((Activity)localObject).setResult(i, paramIntent);
              finish();
            }
          }
          else
          {
            getActivity().setResult(0);
            finish();
          }
        }
      }
    }
    
    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      String str = getActivity().getIntent().getAction();
      this.mFingerprintManager = ((FingerprintManager)getActivity().getSystemService("fingerprint"));
      this.mDPM = ((DevicePolicyManager)getSystemService("device_policy"));
      this.mKeyStore = KeyStore.getInstance();
      this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
      this.mLockPatternUtils = new LockPatternUtils(getActivity());
      this.mLockPatternUtils.sanitizePassword();
      boolean bool;
      label148:
      label242:
      int i;
      if (!"android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD".equals(str))
      {
        bool = "android.app.action.SET_NEW_PASSWORD".equals(str);
        this.mIsSetNewPassword = bool;
        bool = getActivity().getIntent().getBooleanExtra("confirm_credentials", true);
        if ((getActivity() instanceof OxygenChooseLockGeneric.InternalActivity))
        {
          if (!bool) {
            break label461;
          }
          bool = false;
          this.mPasswordConfirmed = bool;
        }
        this.mHideDrawer = getActivity().getIntent().getBooleanExtra(":settings:hide_drawer", false);
        this.mHasChallenge = getActivity().getIntent().getBooleanExtra("has_challenge", false);
        this.mChallenge = getActivity().getIntent().getLongExtra("challenge", 0L);
        this.mForFingerprint = getActivity().getIntent().getBooleanExtra("for_fingerprint", false);
        if (getArguments() == null) {
          break label466;
        }
        bool = getArguments().getBoolean("for_cred_req_boot");
        this.mForChangeCredRequiredForBoot = bool;
        if (paramBundle != null)
        {
          this.mPasswordConfirmed = paramBundle.getBoolean("password_confirmed");
          this.mWaitingForConfirmation = paramBundle.getBoolean("waiting_for_confirmation");
          this.mEncryptionRequestQuality = paramBundle.getInt("encrypt_requested_quality");
          this.mEncryptionRequestDisabled = paramBundle.getBoolean("encrypt_requested_disabled");
        }
        i = Utils.getSecureTargetUser(getActivity().getActivityToken(), UserManager.get(getActivity()), null, getActivity().getIntent().getExtras()).getIdentifier();
        if (("android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD".equals(str)) || (!this.mLockPatternUtils.isSeparateProfileChallengeAllowed(i))) {
          break label471;
        }
        this.mUserId = i;
        if (("android.app.action.SET_NEW_PASSWORD".equals(str)) && (Utils.isManagedProfile(UserManager.get(getActivity()), this.mUserId)) && (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mUserId))) {
          getActivity().setTitle(2131691145);
        }
        this.mManagedPasswordProvider = ManagedLockPasswordProvider.get(getActivity(), this.mUserId);
        if (!this.mPasswordConfirmed) {
          break label513;
        }
        updatePreferencesOrFinish();
        if (this.mForChangeCredRequiredForBoot) {
          maybeEnableEncryption(this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId), false);
        }
      }
      for (;;)
      {
        addHeaderView();
        return;
        bool = true;
        break;
        label461:
        bool = true;
        break label148;
        label466:
        bool = false;
        break label242;
        label471:
        paramBundle = getArguments();
        Context localContext = getContext();
        if (paramBundle != null) {}
        for (;;)
        {
          this.mUserId = Utils.getUserIdFromBundle(localContext, paramBundle);
          break;
          paramBundle = getActivity().getIntent().getExtras();
        }
        label513:
        if (!this.mWaitingForConfirmation)
        {
          paramBundle = new ChooseLockSettingsHelper(getActivity(), this);
          if (Utils.isManagedProfile(UserManager.get(getActivity()), this.mUserId)) {
            if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mUserId)) {
              i = 0;
            }
          }
          for (;;)
          {
            if ((i != 0) || (!paramBundle.launchConfirmationActivity(100, getString(2131691150), true, this.mUserId))) {
              break label609;
            }
            this.mWaitingForConfirmation = true;
            break;
            i = 1;
            continue;
            i = 0;
          }
          label609:
          this.mPasswordConfirmed = true;
          updatePreferencesOrFinish();
        }
      }
    }
    
    public void onDestroy()
    {
      super.onDestroy();
      this.mLockPatternUtils.sanitizePassword();
    }
    
    public boolean onPreferenceTreeClick(Preference paramPreference)
    {
      paramPreference = paramPreference.getKey();
      if ((!isUnlockMethodSecure(paramPreference)) && (this.mLockPatternUtils.isSecure(this.mUserId)))
      {
        showFactoryResetProtectionWarningDialog(paramPreference);
        return true;
      }
      if ("unlock_skip_fingerprint".equals(paramPreference))
      {
        paramPreference = new Intent(getActivity(), OxygenChooseLockGeneric.InternalActivity.class);
        paramPreference.setAction(getIntent().getAction());
        if (this.mPasswordConfirmed) {}
        for (boolean bool = false;; bool = true)
        {
          paramPreference.putExtra("confirm_credentials", bool);
          startActivityForResult(paramPreference, 104);
          return true;
        }
      }
      return setUnlockMethod(paramPreference);
    }
    
    public void onSaveInstanceState(Bundle paramBundle)
    {
      super.onSaveInstanceState(paramBundle);
      paramBundle.putBoolean("password_confirmed", this.mPasswordConfirmed);
      paramBundle.putBoolean("waiting_for_confirmation", this.mWaitingForConfirmation);
      paramBundle.putInt("encrypt_requested_quality", this.mEncryptionRequestQuality);
      paramBundle.putBoolean("encrypt_requested_disabled", this.mEncryptionRequestDisabled);
    }
    
    void updateUnlockMethodAndFinish(int paramInt, boolean paramBoolean)
    {
      if (!this.mPasswordConfirmed) {
        throw new IllegalStateException("Tried to update password without confirming it");
      }
      paramInt = upgradeQuality(paramInt);
      Intent localIntent = getIntentForUnlockMethod(paramInt, paramBoolean);
      if (localIntent != null)
      {
        if ((this.mIsSetNewPassword) && (this.mHasChallenge)) {}
        for (paramInt = 103;; paramInt = 102)
        {
          startActivityForResult(localIntent, paramInt);
          return;
        }
      }
      if (paramInt == 0)
      {
        this.mLockPatternUtils.setSeparateProfileChallengeEnabled(this.mUserId, true, this.mUserPassword);
        this.mChooseLockSettingsHelper.utils().clearLock(this.mUserId);
        this.mChooseLockSettingsHelper.utils().setLockScreenDisabled(paramBoolean, this.mUserId);
        getActivity().setResult(-1);
        removeAllFingerprintForUserAndFinish(this.mUserId);
        return;
      }
      removeAllFingerprintForUserAndFinish(this.mUserId);
    }
    
    public static class FactoryResetProtectionWarningDialog
      extends DialogFragment
    {
      private static final String ARG_MESSAGE_RES = "messageRes";
      private static final String ARG_TITLE_RES = "titleRes";
      private static final String ARG_UNLOCK_METHOD_TO_SET = "unlockMethodToSet";
      
      public static FactoryResetProtectionWarningDialog newInstance(int paramInt1, int paramInt2, String paramString)
      {
        FactoryResetProtectionWarningDialog localFactoryResetProtectionWarningDialog = new FactoryResetProtectionWarningDialog();
        Bundle localBundle = new Bundle();
        localBundle.putInt("titleRes", paramInt1);
        localBundle.putInt("messageRes", paramInt2);
        localBundle.putString("unlockMethodToSet", paramString);
        localFactoryResetProtectionWarningDialog.setArguments(localBundle);
        return localFactoryResetProtectionWarningDialog;
      }
      
      public Dialog onCreateDialog(final Bundle paramBundle)
      {
        paramBundle = getArguments();
        new AlertDialog.Builder(getActivity()).setTitle(paramBundle.getInt("titleRes")).setMessage(paramBundle.getInt("messageRes")).setPositiveButton(2131691198, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            OxygenChooseLockGeneric.ChooseLockGenericFragment.-wrap0((OxygenChooseLockGeneric.ChooseLockGenericFragment)OxygenChooseLockGeneric.ChooseLockGenericFragment.FactoryResetProtectionWarningDialog.this.getParentFragment(), paramBundle.getString("unlockMethodToSet"));
          }
        }).setNegativeButton(2131690993, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            OxygenChooseLockGeneric.ChooseLockGenericFragment.FactoryResetProtectionWarningDialog.this.dismiss();
          }
        }).create();
      }
      
      public void show(FragmentManager paramFragmentManager, String paramString)
      {
        if (paramFragmentManager.findFragmentByTag(paramString) == null) {
          super.show(paramFragmentManager, paramString);
        }
      }
    }
  }
  
  public static class InternalActivity
    extends OxygenChooseLockGeneric
  {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\OxygenChooseLockGeneric.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */