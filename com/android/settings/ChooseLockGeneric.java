package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.RemovalCallback;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
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
import com.android.internal.policy.IOPFaceSettingService;
import com.android.internal.policy.IOPFaceSettingService.Stub;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;
import java.util.List;

public class ChooseLockGeneric
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
    private static final int CHOOSE_LOCK_REQUEST = 102;
    private static final int CONFIRM_EXISTING_REQUEST = 100;
    private static final int ENABLE_ENCRYPTION_REQUEST = 101;
    public static final String ENCRYPT_REQUESTED_DISABLED = "encrypt_requested_disabled";
    public static final String ENCRYPT_REQUESTED_QUALITY = "encrypt_requested_quality";
    public static final String HIDE_DISABLED_PREFS = "hide_disabled_prefs";
    private static final String KEY_UNLOCK_SET_MANAGED = "unlock_set_managed";
    private static final String KEY_UNLOCK_SET_NONE = "unlock_set_none";
    private static final String KEY_UNLOCK_SET_OFF = "unlock_set_off";
    private static final String KEY_UNLOCK_SET_PASSWORD = "unlock_set_password";
    private static final String KEY_UNLOCK_SET_PATTERN = "unlock_set_pattern";
    private static final String KEY_UNLOCK_SET_PIN = "unlock_set_pin";
    public static final String MINIMUM_QUALITY_KEY = "minimum_quality";
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final String PASSWORD_CONFIRMED = "password_confirmed";
    private static final String TAG = "ChooseLockGenericFragment";
    public static final String TAG_FRP_WARNING_DIALOG = "frp_warning_dialog";
    private static final String WAITING_FOR_CONFIRMATION = "waiting_for_confirmation";
    private long mChallenge;
    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    private DevicePolicyManager mDPM;
    private boolean mEncryptionRequestDisabled;
    private int mEncryptionRequestQuality;
    private IOPFaceSettingService mFaceSettingService;
    private ServiceConnection mFaceUnlockConnection = new ServiceConnection()
    {
      public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
      {
        Log.i("ChooseLockGenericFragment", "Oneplus face unlock service connected");
        ChooseLockGeneric.ChooseLockGenericFragment.-set0(ChooseLockGeneric.ChooseLockGenericFragment.this, IOPFaceSettingService.Stub.asInterface(paramAnonymousIBinder));
      }
      
      public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
      {
        Log.i("ChooseLockGenericFragment", "Oneplus face unlock service disconnected");
        ChooseLockGeneric.ChooseLockGenericFragment.-set0(ChooseLockGeneric.ChooseLockGenericFragment.this, null);
      }
    };
    private FingerprintManager mFingerprintManager;
    private boolean mForChangeCredRequiredForBoot = false;
    protected boolean mForFingerprint = false;
    private boolean mHasChallenge = false;
    private boolean mHideDrawer = false;
    private KeyStore mKeyStore;
    private LockPatternUtils mLockPatternUtils;
    private ManagedLockPasswordProvider mManagedPasswordProvider;
    private boolean mPasswordConfirmed = false;
    private boolean mRequirePassword;
    private int mUserId;
    private String mUserPassword;
    private boolean mWaitingForConfirmation = false;
    
    private void bindFaceUnlockService()
    {
      try
      {
        Intent localIntent = new Intent();
        localIntent.setClassName("com.oneplus.faceunlock", "com.oneplus.faceunlock.FaceSettingService");
        getActivity().bindService(localIntent, this.mFaceUnlockConnection, 1);
        Log.i("ChooseLockGenericFragment", "Start bind oneplus face unlockservice");
        return;
      }
      catch (Exception localException)
      {
        Log.i("ChooseLockGenericFragment", "Bind oneplus face unlockservice exception");
      }
    }
    
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
          if (paramBoolean) {}
          for (paramBoolean = bool;; paramBoolean = true)
          {
            localObject = getEncryptionInterstitialIntent(localActivity, paramInt, localLockPatternUtils.isCredentialRequiredToDecrypt(paramBoolean), (Intent)localObject);
            ((Intent)localObject).putExtra("for_fingerprint", this.mForFingerprint);
            ((Intent)localObject).putExtra(":settings:hide_drawer", this.mHideDrawer);
            startActivityForResult((Intent)localObject, 101);
            return;
          }
        }
      }
      this.mRequirePassword = false;
      updateUnlockMethodAndFinish(paramInt, paramBoolean);
    }
    
    private void removeAllFingerprintForUserAndFinish(final int paramInt)
    {
      removeFaceData();
      Object localObject;
      if ((this.mFingerprintManager != null) && (this.mFingerprintManager.isHardwareDetected())) {
        if (this.mFingerprintManager.hasEnrolledFingerprints(paramInt))
        {
          this.mFingerprintManager.setActiveUser(paramInt);
          localObject = new Fingerprint(null, paramInt, 0, 0L);
          this.mFingerprintManager.remove((Fingerprint)localObject, paramInt, new FingerprintManager.RemovalCallback()
          {
            public void onRemovalError(Fingerprint paramAnonymousFingerprint, int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
            {
              Log.v("ChooseLockGenericFragment", "Fingerprint removed: " + paramAnonymousFingerprint.getFingerId());
              if (paramAnonymousFingerprint.getFingerId() == 0) {
                ChooseLockGeneric.ChooseLockGenericFragment.-wrap1(ChooseLockGeneric.ChooseLockGenericFragment.this, paramInt);
              }
            }
            
            public void onRemovalSucceeded(Fingerprint paramAnonymousFingerprint)
            {
              if (paramAnonymousFingerprint.getFingerId() == 0) {
                ChooseLockGeneric.ChooseLockGenericFragment.-wrap1(ChooseLockGeneric.ChooseLockGenericFragment.this, paramInt);
              }
            }
          });
        }
      }
      for (;;)
      {
        localObject = new Intent("com.android.settings.action.DISMISS_APPLOCKER");
        ((Intent)localObject).putExtra("applocker_package_name", "");
        ((Intent)localObject).putExtra("applocker_dismiss_all", true);
        SettingsBaseApplication.mApplication.sendBroadcast((Intent)localObject);
        return;
        removeManagedProfileFingerprintsAndFinishIfNecessary(paramInt);
        continue;
        finish();
      }
    }
    
    private void removeFaceData()
    {
      if (this.mFaceSettingService == null) {
        return;
      }
      try
      {
        this.mFaceSettingService.removeFace(0);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.i("ChooseLockGenericFragment", "Start remove face RemoteException:" + localRemoteException);
      }
    }
    
    private void removeManagedProfileFingerprintsAndFinishIfNecessary(int paramInt)
    {
      this.mFingerprintManager.setActiveUser(UserHandle.myUserId());
      Object localObject = UserManager.get(SettingsBaseApplication.mApplication);
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
            break label115;
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
      label115:
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
    
    private void unbindFaceUnlockService()
    {
      Log.i("ChooseLockGenericFragment", "Start unbind oneplus face unlockservice");
      getActivity().unbindService(this.mFaceUnlockConnection);
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
      Preference localPreference1;
      Preference localPreference2;
      Preference localPreference3;
      if (this.mForFingerprint)
      {
        localPreference1 = findPreference("unlock_set_pattern");
        localPreference2 = findPreference("unlock_set_pin");
        localPreference3 = findPreference("unlock_set_password");
        if (!OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) {
          break label89;
        }
        localPreference1.setTitle(2131691175);
        localPreference2.setTitle(2131691176);
        localPreference3.setTitle(2131691177);
      }
      while (this.mManagedPasswordProvider.isSettingManagedPasswordSupported())
      {
        findPreference("unlock_set_managed").setTitle(this.mManagedPasswordProvider.getPickerOptionTitle(this.mForFingerprint));
        return;
        label89:
        localPreference1.setTitle(2131691167);
        localPreference2.setTitle(2131691168);
        localPreference3.setTitle(2131691169);
      }
      removePreference("unlock_set_managed");
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
      if (this.mForFingerprint) {
        setHeaderView(2130968637);
      }
    }
    
    protected void addPreferences()
    {
      addPreferencesFromResource(2131230849);
      findPreference("unlock_set_none").setViewId(2131361810);
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
      return ChooseLockPattern.createIntent(paramContext, paramBoolean, paramLong, paramInt);
    }
    
    protected Intent getLockPatternIntent(Context paramContext, boolean paramBoolean, String paramString, int paramInt)
    {
      return ChooseLockPattern.createIntent(paramContext, paramBoolean, paramString, paramInt);
    }
    
    protected Intent getLockPatternIntent(Context paramContext, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
    {
      return ChooseLockPattern.createIntent(paramContext, paramBoolean1, paramBoolean2, paramInt);
    }
    
    protected int getMetricsCategory()
    {
      return 27;
    }
    
    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      this.mWaitingForConfirmation = false;
      if ((paramInt1 == 100) && (paramInt2 == -1) && (paramIntent != null))
      {
        this.mPasswordConfirmed = true;
        this.mUserPassword = paramIntent.getStringExtra("password");
        updatePreferencesOrFinish();
        if (this.mForChangeCredRequiredForBoot)
        {
          if (TextUtils.isEmpty(this.mUserPassword)) {
            break label96;
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
        label96:
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
          getActivity().setResult(0);
          finish();
        }
      }
    }
    
    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      bindFaceUnlockService();
      this.mFingerprintManager = ((FingerprintManager)getActivity().getSystemService("fingerprint"));
      this.mDPM = ((DevicePolicyManager)getSystemService("device_policy"));
      this.mKeyStore = KeyStore.getInstance();
      this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
      this.mLockPatternUtils = new LockPatternUtils(getActivity());
      this.mLockPatternUtils.sanitizePassword();
      boolean bool = getActivity().getIntent().getBooleanExtra("confirm_credentials", true);
      label209:
      int i;
      if ((getActivity() instanceof ChooseLockGeneric.InternalActivity))
      {
        if (bool)
        {
          bool = false;
          this.mPasswordConfirmed = bool;
        }
      }
      else
      {
        this.mHideDrawer = getActivity().getIntent().getBooleanExtra(":settings:hide_drawer", false);
        this.mHasChallenge = getActivity().getIntent().getBooleanExtra("has_challenge", false);
        this.mChallenge = getActivity().getIntent().getLongExtra("challenge", 0L);
        this.mForFingerprint = getActivity().getIntent().getBooleanExtra("for_fingerprint", false);
        if (getArguments() == null) {
          break label444;
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
        if (("android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD".equals(getActivity().getIntent().getAction())) || (!this.mLockPatternUtils.isSeparateProfileChallengeAllowed(i))) {
          break label449;
        }
        this.mUserId = i;
        if (("android.app.action.SET_NEW_PASSWORD".equals(getActivity().getIntent().getAction())) && (Utils.isManagedProfile(UserManager.get(getActivity()), this.mUserId)) && (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mUserId))) {
          getActivity().setTitle(2131691145);
        }
        this.mManagedPasswordProvider = ManagedLockPasswordProvider.get(getActivity(), this.mUserId);
        if (!this.mPasswordConfirmed) {
          break label491;
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
        label444:
        bool = false;
        break label209;
        label449:
        paramBundle = getArguments();
        Context localContext = getContext();
        if (paramBundle != null) {}
        for (;;)
        {
          this.mUserId = Utils.getUserIdFromBundle(localContext, paramBundle);
          break;
          paramBundle = getActivity().getIntent().getExtras();
        }
        label491:
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
              break label587;
            }
            this.mWaitingForConfirmation = true;
            break;
            i = 1;
            continue;
            i = 0;
          }
          label587:
          this.mPasswordConfirmed = true;
          updatePreferencesOrFinish();
        }
      }
    }
    
    public void onDestroy()
    {
      super.onDestroy();
      this.mLockPatternUtils.sanitizePassword();
      unbindFaceUnlockService();
    }
    
    public boolean onPreferenceTreeClick(Preference paramPreference)
    {
      paramPreference = paramPreference.getKey();
      if ((!isUnlockMethodSecure(paramPreference)) && (this.mLockPatternUtils.isSecure(this.mUserId)))
      {
        showFactoryResetProtectionWarningDialog(paramPreference);
        return true;
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
        startActivityForResult(localIntent, 102);
        return;
      }
      if (paramInt == 0)
      {
        this.mLockPatternUtils.setSeparateProfileChallengeEnabled(this.mUserId, true, this.mUserPassword);
        this.mChooseLockSettingsHelper.utils().clearLock(this.mUserId);
        this.mChooseLockSettingsHelper.utils().setLockScreenDisabled(paramBoolean, this.mUserId);
        removeAllFingerprintForUserAndFinish(this.mUserId);
        getActivity().setResult(-1);
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
            ChooseLockGeneric.ChooseLockGenericFragment.-wrap0((ChooseLockGeneric.ChooseLockGenericFragment)ChooseLockGeneric.ChooseLockGenericFragment.FactoryResetProtectionWarningDialog.this.getParentFragment(), paramBundle.getString("unlockMethodToSet"));
          }
        }).setNegativeButton(2131690993, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            ChooseLockGeneric.ChooseLockGenericFragment.FactoryResetProtectionWarningDialog.this.dismiss();
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
    extends ChooseLockGeneric
  {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ChooseLockGeneric.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */