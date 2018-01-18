package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.security.KeyStore;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.fingerprint.FingerprintEnrollEnrolling;
import com.android.settings.fingerprint.FingerprintEnrollFindSensor;
import com.android.settings.fingerprint.FingerprintEnrollIntroduction;
import com.android.settings.fingerprint.FingerprintSettings;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Index;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.faceunlock.OPFaceUnlockSettings;
import com.oneplus.settings.quickpay.QuickPaySettings;
import com.oneplus.settings.ui.OPPreferenceDivider;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SecuritySettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, DialogInterface.OnClickListener, Indexable, GearPreference.OnGearClickListener
{
  private static final int ADD_FINGERPRINT_REQUEST = 103;
  private static final int CHANGE_TRUST_AGENT_SETTINGS = 126;
  private static final int CHOOSE_LOCK_GENERIC_REQUEST = 102;
  private static final int CONFIRM_REQUEST = 101;
  private static final int GOTO_APPLOCKER_PAGE_REQUEST = 104;
  private static final int GOTO_EDIT_FINGERPRINT_REQUEST = 105;
  private static final int GOTO_FACEUNLOCK_PAGE_REQUEST = 107;
  private static final int GOTO_QUICKPAY_PAGE_REQUEST = 106;
  private static final String KEY_ADVANCED_SECURITY = "advanced_security";
  private static final String KEY_APP_LOCKER = "oneplus_app_locker";
  private static final String KEY_CREDENTIALS_INSTALL = "credentials_install";
  private static final String KEY_CREDENTIALS_MANAGER = "credentials_management";
  private static final String KEY_CREDENTIAL_STORAGE_TYPE = "credential_storage_type";
  private static final String KEY_DEVICE_ADMIN_CATEGORY = "device_admin_category";
  private static final String KEY_FINGERPRINT_ADD = "key_fingerprint_add";
  private static final String KEY_FINGERPRINT_ITEM_PREFIX = "key_fingerprint_item";
  private static final String KEY_MANAGE_TRUST_AGENTS = "manage_trust_agents";
  private static final String KEY_ONEPLUS_CATAGORY_QUICK_PAY = "privacy_catagory_quick_pay";
  private static final String KEY_ONEPLUS_FACE_UNLOCK = "oneplus_face_unlock";
  private static final String KEY_ONEPLUS_QUICK_PAY = "oneplus_quick_pay";
  private static final String KEY_PREFERENCE_DIVIDER_LINE_APPLOCKER = "preference_divider_line_applocker";
  private static final String KEY_PREFERENCE_DIVIDER_LINE_FACE_UNLOCK = "preference_divider_line_face_unlock";
  private static final String KEY_PREFERENCE_DIVIDER_LINE_QUICK_PAY = "preference_divider_line_quick_pay";
  private static final String KEY_PRIVACY_CATAGORY = "privacy_catagory";
  private static final String KEY_PRIVACY_CATAGORY_FACE_UNLOCK = "privacy_catagory_face_unlock";
  private static final String KEY_RESET_CREDENTIALS = "credentials_reset";
  private static final String KEY_SCREEN_PINNING = "screen_pinning_settings";
  private static final String KEY_SECURITY_CATEGORY = "security_category";
  private static final String KEY_SHOW_PASSWORD = "show_password";
  private static final String KEY_SIMLOCK_PREFERENCE_DIVIDER_LINE = "simlock_preference_divider_line";
  private static final String KEY_SIM_LOCK = "sim_lock";
  private static final String KEY_TOGGLE_INSTALL_APPLICATIONS = "toggle_install_applications";
  private static final String KEY_TRUST_AGENT = "trust_agent";
  private static final String KEY_UNIFICATION = "unification";
  private static final String KEY_UNLOCK_SET_OR_CHANGE = "unlock_set_or_change";
  private static final String KEY_UNLOCK_SET_OR_CHANGE_PROFILE = "unlock_set_or_change_profile";
  private static final String KEY_USER_CREDENTIALS = "user_credentials";
  private static final String KEY_VISIBLE_PATTERN_PROFILE = "visiblepattern_profile";
  private static final int MY_USER_ID = UserHandle.myUserId();
  private static final boolean ONLY_ONE_TRUST_AGENT = true;
  private static final String PACKAGE_MIME_TYPE = "application/vnd.android.package-archive";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new SecuritySearchIndexProvider(null);
  private static final int SET_OR_CHANGE_LOCK_METHOD_REQUEST = 123;
  private static final int SET_OR_CHANGE_LOCK_METHOD_REQUEST_PROFILE = 127;
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new SecuritySettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader, null);
    }
  };
  private static final String[] SWITCH_PREFERENCE_KEYS;
  private static final String TAG = "SecuritySettings";
  private static final String TAG_UNIFICATION_DIALOG = "unification_dialog";
  private static final String TRUST_AGENT_CLICK_INTENT = "trust_agent_click_intent";
  private static final Intent TRUST_AGENT_INTENT = new Intent("android.service.trust.TrustAgentService");
  private static final int UNIFY_LOCK_CONFIRM_DEVICE_REQUEST = 128;
  private static final int UNIFY_LOCK_CONFIRM_PROFILE_REQUEST = 129;
  private static final int UNUNIFY_LOCK_CONFIRM_DEVICE_REQUEST = 130;
  private boolean isSupportFinger = false;
  private long mChallenge;
  private ChooseLockSettingsHelper mChooseLockSettingsHelper;
  private String mCurrentDevicePassword;
  private String mCurrentProfilePassword;
  private DevicePolicyManager mDPM;
  private PreferenceCategory mFaceUnlockCategory;
  private Preference mFaceUnlockPreference;
  private CancellationSignal mFingerprintCancel;
  private Preference mFingerprintEditPreference;
  private FingerprintManager mFingerprintManager;
  private PreferenceCategory mFingerprint_list_category;
  public boolean mGotoAppLockerClick = false;
  public boolean mGotoEditFingerprintClick = false;
  public boolean mGotoEnrollClick = false;
  public boolean mGotoFaceUnlockClick = false;
  public boolean mGotoQuickPayClick = false;
  private boolean mHasFingerprint = false;
  private boolean mIsAdmin;
  private KeyStore mKeyStore;
  private boolean mLaunchedConfirm;
  private LockPatternUtils mLockPatternUtils;
  private ManagedLockPasswordProvider mManagedPasswordProvider;
  private OPPreferenceDivider mPreferenceDividerLineFaceUnlock;
  private int mProfileChallengeUserId;
  private PreferenceCategory mQuickPayCategory;
  private Preference mQuickPayPreference;
  private RestrictedPreference mResetCredentials;
  private SwitchPreference mShowPassword;
  private SubscriptionManager mSubscriptionManager;
  private OPRestrictedSwitchPreference mToggleAppInstallation;
  private byte[] mToken;
  private Intent mTrustAgentClickIntent;
  private UserManager mUm;
  private SwitchPreference mUnifyProfile;
  private SwitchPreference mVisiblePatternProfile;
  private DialogInterface mWarnInstallApps;
  private OPPreferenceDivider preferenceDividerLineQuickPay;
  
  static
  {
    SWITCH_PREFERENCE_KEYS = new String[] { "show_password", "toggle_install_applications", "unification", "visiblepattern_profile" };
  }
  
  private void addFingerprintItemPreferences(PreferenceGroup paramPreferenceGroup)
  {
    this.mFingerprint_list_category.removeAll();
    List localList = this.mFingerprintManager.getEnrolledFingerprints(MY_USER_ID);
    int j = localList.size();
    if (j > 0) {}
    for (this.mHasFingerprint = true;; this.mHasFingerprint = false)
    {
      int i = 0;
      while (i < j)
      {
        Fingerprint localFingerprint = (Fingerprint)localList.get(i);
        FingerprintPreference localFingerprintPreference = new FingerprintPreference(paramPreferenceGroup.getContext());
        localFingerprintPreference.setKey(genKey(localFingerprint.getFingerId()));
        localFingerprintPreference.setTitle(localFingerprint.getName());
        localFingerprintPreference.setFingerprint(localFingerprint);
        localFingerprintPreference.setPersistent(false);
        this.mFingerprint_list_category.addPreference(localFingerprintPreference);
        localFingerprintPreference.setOnPreferenceChangeListener(this);
        i += 1;
      }
    }
    updateAddPreference();
  }
  
  private void addTrustAgentSettings(PreferenceGroup paramPreferenceGroup)
  {
    boolean bool = this.mLockPatternUtils.isSecure(MY_USER_ID);
    ArrayList localArrayList = getActiveTrustAgents(getActivity(), this.mLockPatternUtils, this.mDPM);
    int i = 0;
    if (i < localArrayList.size())
    {
      TrustAgentUtils.TrustAgentComponentInfo localTrustAgentComponentInfo = (TrustAgentUtils.TrustAgentComponentInfo)localArrayList.get(i);
      RestrictedPreference localRestrictedPreference = new RestrictedPreference(paramPreferenceGroup.getContext());
      localRestrictedPreference.setKey("trust_agent");
      localRestrictedPreference.setTitle(localTrustAgentComponentInfo.title);
      localRestrictedPreference.setSummary(localTrustAgentComponentInfo.summary);
      Intent localIntent = new Intent();
      localIntent.setComponent(localTrustAgentComponentInfo.componentName);
      localIntent.setAction("android.intent.action.MAIN");
      localRestrictedPreference.setIntent(localIntent);
      paramPreferenceGroup.addPreference(localRestrictedPreference);
      localRestrictedPreference.setDisabledByAdmin(localTrustAgentComponentInfo.admin);
      if ((localRestrictedPreference.isDisabledByAdmin()) || (bool)) {}
      for (;;)
      {
        i += 1;
        break;
        localRestrictedPreference.setEnabled(false);
        localRestrictedPreference.setSummary(2131691231);
      }
    }
  }
  
  private PreferenceScreen createPreferenceHierarchy()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null) {
      localPreferenceScreen.removeAll();
    }
    addPreferencesFromResource(2131230835);
    localPreferenceScreen = getPreferenceScreen();
    addPreferencesFromResource(getResIdForLockUnlockScreen(getActivity(), this.mLockPatternUtils, this.mManagedPasswordProvider, MY_USER_ID));
    disableIfPasswordQualityManaged("unlock_set_or_change", MY_USER_ID);
    this.mProfileChallengeUserId = Utils.getManagedProfileId(this.mUm, MY_USER_ID);
    Object localObject1;
    label379:
    label421:
    label462:
    label531:
    Object localObject2;
    boolean bool;
    if ((this.mProfileChallengeUserId != 55536) && (this.mLockPatternUtils.isSeparateProfileChallengeAllowed(this.mProfileChallengeUserId)))
    {
      addPreferencesFromResource(2131230853);
      addPreferencesFromResource(2131230856);
      addPreferencesFromResource(getResIdForLockUnlockScreen(getActivity(), this.mLockPatternUtils, this.mManagedPasswordProvider, this.mProfileChallengeUserId));
      maybeAddFingerprintPreference(localPreferenceScreen, this.mProfileChallengeUserId);
      if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mProfileChallengeUserId))
      {
        localObject1 = localPreferenceScreen.findPreference("unlock_set_or_change_profile");
        ((Preference)localObject1).setSummary(getContext().getString(2131692063));
        ((Preference)localObject1).setEnabled(false);
        disableIfPasswordQualityManaged("unlock_set_or_change", this.mProfileChallengeUserId);
      }
    }
    else
    {
      localObject1 = findPreference("unlock_set_or_change");
      if ((localObject1 instanceof GearPreference)) {
        ((GearPreference)localObject1).setOnGearClickListener(this);
      }
      this.mIsAdmin = this.mUm.isAdminUser();
      localObject1 = (PreferenceGroup)localPreferenceScreen.findPreference("security_category");
      if (localObject1 != null) {
        addTrustAgentSettings((PreferenceGroup)localObject1);
      }
      this.mVisiblePatternProfile = ((SwitchPreference)localPreferenceScreen.findPreference("visiblepattern_profile"));
      this.mUnifyProfile = ((SwitchPreference)localPreferenceScreen.findPreference("unification"));
      addPreferencesFromResource(2131230793);
      this.mFingerprint_list_category = ((PreferenceCategory)localPreferenceScreen.findPreference("key_fingerprint_list"));
      addFingerprintItemPreferences(localPreferenceScreen);
      addPreferencesFromResource(2131230842);
      this.mFaceUnlockCategory = ((PreferenceCategory)findPreference("privacy_catagory_face_unlock"));
      this.mFaceUnlockPreference = findPreference("oneplus_face_unlock");
      this.mPreferenceDividerLineFaceUnlock = ((OPPreferenceDivider)findPreference("preference_divider_line_face_unlock"));
      if ((OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) && (!OPUtils.isGuestMode())) {
        break label867;
      }
      localPreferenceScreen.removePreference(this.mFaceUnlockCategory);
      localPreferenceScreen.removePreference(this.mPreferenceDividerLineFaceUnlock);
      this.mQuickPayCategory = ((PreferenceCategory)findPreference("privacy_catagory_quick_pay"));
      this.mQuickPayPreference = findPreference("oneplus_quick_pay");
      if (!OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) {
        break label888;
      }
      this.mQuickPayPreference.setSummary(2131690512);
      this.preferenceDividerLineQuickPay = ((OPPreferenceDivider)findPreference("preference_divider_line_quick_pay"));
      if (QuickPaySettings.canShowQuickPay(getContext())) {
        break label901;
      }
      localPreferenceScreen.removePreference(this.mQuickPayCategory);
      localPreferenceScreen.removePreference(this.preferenceDividerLineQuickPay);
      TelephonyManager.getDefault();
      localObject1 = ((CarrierConfigManager)getActivity().getSystemService("carrier_config")).getConfig();
      if ((this.mIsAdmin) && (isSimIccReady()) && (!((PersistableBundle)localObject1).getBoolean("hide_sim_lock_settings_bool"))) {
        break label922;
      }
      localPreferenceScreen.removePreference(localPreferenceScreen.findPreference("sim_lock"));
      localPreferenceScreen.removePreference(localPreferenceScreen.findPreference("simlock_preference_divider_line"));
      if (Settings.System.getInt(getContentResolver(), "lock_to_app_enabled", 0) != 0) {
        localPreferenceScreen.findPreference("screen_pinning_settings").setSummary(getResources().getString(2131693335));
      }
      this.mShowPassword = ((SwitchPreference)localPreferenceScreen.findPreference("show_password"));
      this.mResetCredentials = ((RestrictedPreference)localPreferenceScreen.findPreference("credentials_reset"));
      localObject1 = (UserManager)getActivity().getSystemService("user");
      this.mKeyStore = KeyStore.getInstance();
      localObject2 = (PreferenceGroup)localPreferenceScreen.findPreference("device_admin_category");
      this.mToggleAppInstallation = ((OPRestrictedSwitchPreference)findPreference("toggle_install_applications"));
      this.mToggleAppInstallation.setChecked(isNonMarketAppsAllowed());
      localObject2 = this.mToggleAppInstallation;
      if (!((UserManager)localObject1).getUserInfo(MY_USER_ID).isRestricted()) {
        break label938;
      }
      bool = false;
      label669:
      ((OPRestrictedSwitchPreference)localObject2).setEnabled(bool);
      if ((RestrictedLockUtils.hasBaseUserRestriction(getActivity(), "no_install_unknown_sources", MY_USER_ID)) || (RestrictedLockUtils.hasBaseUserRestriction(getActivity(), "no_install_apps", MY_USER_ID))) {
        this.mToggleAppInstallation.setEnabled(false);
      }
      if (this.mToggleAppInstallation.isEnabled())
      {
        this.mToggleAppInstallation.checkRestrictionAndSetDisabled("no_install_unknown_sources");
        if (!this.mToggleAppInstallation.isDisabledByAdmin()) {
          this.mToggleAppInstallation.checkRestrictionAndSetDisabled("no_install_apps");
        }
      }
      localObject1 = (PreferenceGroup)localPreferenceScreen.findPreference("advanced_security");
      if (localObject1 != null)
      {
        localObject1 = ((PreferenceGroup)localObject1).findPreference("manage_trust_agents");
        if ((localObject1 != null) && (!this.mLockPatternUtils.isSecure(MY_USER_ID))) {
          break label943;
        }
      }
    }
    for (;;)
    {
      Index.getInstance(getActivity()).updateFromClassNameResource(SecuritySettings.class.getName(), true, true);
      int i = 0;
      while (i < SWITCH_PREFERENCE_KEYS.length)
      {
        localObject1 = findPreference(SWITCH_PREFERENCE_KEYS[i]);
        if (localObject1 != null) {
          ((Preference)localObject1).setOnPreferenceChangeListener(this);
        }
        i += 1;
      }
      disableIfPasswordQualityManaged("unlock_set_or_change_profile", this.mProfileChallengeUserId);
      break;
      label867:
      localPreferenceScreen.addPreference(this.mFaceUnlockCategory);
      localPreferenceScreen.addPreference(this.mPreferenceDividerLineFaceUnlock);
      break label379;
      label888:
      this.mQuickPayPreference.setSummary(2131690405);
      break label421;
      label901:
      localPreferenceScreen.addPreference(this.mQuickPayCategory);
      localPreferenceScreen.addPreference(this.preferenceDividerLineQuickPay);
      break label462;
      label922:
      localPreferenceScreen.findPreference("sim_lock").setEnabled(isSimReady());
      break label531;
      label938:
      bool = true;
      break label669;
      label943:
      ((Preference)localObject1).setEnabled(false);
      ((Preference)localObject1).setSummary(2131691231);
    }
    if (this.mIsAdmin)
    {
      if (!LockPatternUtils.isDeviceEncryptionEnabled()) {
        break label1054;
      }
      addPreferencesFromResource(2131230837);
    }
    for (;;)
    {
      localObject1 = (PreferenceCategory)findPreference("privacy_catagory");
      localObject2 = (OPPreferenceDivider)findPreference("preference_divider_line_applocker");
      if (OPUtils.isGuestMode())
      {
        if (localObject1 != null)
        {
          localPreferenceScreen.removePreference((Preference)localObject1);
          localPreferenceScreen.removePreference((Preference)localObject2);
        }
        if (this.mQuickPayCategory != null)
        {
          localPreferenceScreen.removePreference(this.mQuickPayCategory);
          localPreferenceScreen.removePreference(this.preferenceDividerLineQuickPay);
        }
      }
      return localPreferenceScreen;
      label1054:
      addPreferencesFromResource(2131230855);
    }
  }
  
  private void disableIfPasswordQualityManaged(String paramString, int paramInt)
  {
    RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfPasswordQualityIsSet(getActivity(), paramInt);
    if ((localEnforcedAdmin != null) && (this.mDPM.getPasswordQuality(localEnforcedAdmin.component, paramInt) == 524288)) {
      ((RestrictedPreference)getPreferenceScreen().findPreference(paramString)).setDisabledByAdmin(localEnforcedAdmin);
    }
  }
  
  private static String genKey(int paramInt)
  {
    return "key_fingerprint_item_" + paramInt;
  }
  
  private static ArrayList<TrustAgentUtils.TrustAgentComponentInfo> getActiveTrustAgents(Context paramContext, LockPatternUtils paramLockPatternUtils, DevicePolicyManager paramDevicePolicyManager)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    ArrayList localArrayList = new ArrayList();
    List localList = localPackageManager.queryIntentServices(TRUST_AGENT_INTENT, 128);
    paramLockPatternUtils = paramLockPatternUtils.getEnabledTrustAgents(MY_USER_ID);
    paramContext = RestrictedLockUtils.checkIfKeyguardFeaturesDisabled(paramContext, 16, UserHandle.myUserId());
    if ((paramLockPatternUtils == null) || (paramLockPatternUtils.isEmpty())) {
      return localArrayList;
    }
    int i = 0;
    label64:
    ResolveInfo localResolveInfo;
    if (i < localList.size())
    {
      localResolveInfo = (ResolveInfo)localList.get(i);
      if (localResolveInfo.serviceInfo != null) {
        break label103;
      }
    }
    label103:
    TrustAgentUtils.TrustAgentComponentInfo localTrustAgentComponentInfo;
    do
    {
      do
      {
        i += 1;
        break label64;
        break;
      } while (!TrustAgentUtils.checkProvidePermission(localResolveInfo, localPackageManager));
      localTrustAgentComponentInfo = TrustAgentUtils.getSettingsComponent(localPackageManager, localResolveInfo);
    } while ((localTrustAgentComponentInfo.componentName == null) || (!paramLockPatternUtils.contains(TrustAgentUtils.getComponentName(localResolveInfo))) || (TextUtils.isEmpty(localTrustAgentComponentInfo.title)));
    if ((paramContext != null) && (paramDevicePolicyManager.getTrustAgentConfiguration(null, TrustAgentUtils.getComponentName(localResolveInfo)) == null)) {
      localTrustAgentComponentInfo.admin = paramContext;
    }
    localArrayList.add(localTrustAgentComponentInfo);
    return localArrayList;
  }
  
  private static int getResIdForLockUnlockScreen(Context paramContext, LockPatternUtils paramLockPatternUtils, ManagedLockPasswordProvider paramManagedLockPasswordProvider, int paramInt)
  {
    int i;
    if (paramInt == MY_USER_ID) {
      i = 1;
    }
    while (!paramLockPatternUtils.isSecure(paramInt)) {
      if (i == 0)
      {
        return 2131230841;
        i = 0;
      }
      else
      {
        if (paramLockPatternUtils.isLockScreenDisabled(paramInt)) {
          return 2131230840;
        }
        return 2131230836;
      }
    }
    switch (paramLockPatternUtils.getKeyguardStoredPasswordQuality(paramInt))
    {
    default: 
      return 0;
    case 65536: 
      if (i != 0) {
        return 2131230846;
      }
      return 2131230847;
    case 131072: 
    case 196608: 
      if (i != 0) {
        return 2131230850;
      }
      return 2131230851;
    case 262144: 
    case 327680: 
    case 393216: 
      if (i != 0) {
        return 2131230843;
      }
      return 2131230844;
    }
    if (i != 0) {}
    for (boolean bool = false;; bool = true) {
      return paramManagedLockPasswordProvider.getResIdForLockUnlockScreen(bool);
    }
  }
  
  private void gotoFindSensor(byte[] paramArrayOfByte)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", FingerprintEnrollFindSensor.class.getName());
    localIntent.putExtra("hw_auth_token", paramArrayOfByte);
    localIntent.putExtra("android.intent.extra.USER_ID", MY_USER_ID);
    startActivity(localIntent);
  }
  
  private boolean isNonMarketAppsAllowed()
  {
    boolean bool = false;
    if (Settings.Global.getInt(getContentResolver(), "install_non_market_apps", 0) > 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isSimIccReady()
  {
    TelephonyManager localTelephonyManager = TelephonyManager.getDefault();
    Object localObject = this.mSubscriptionManager.getActiveSubscriptionInfoList();
    if (localObject != null)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        if (localTelephonyManager.hasIccCard(((SubscriptionInfo)((Iterator)localObject).next()).getSimSlotIndex())) {
          return true;
        }
      }
    }
    return false;
  }
  
  private boolean isSimReady()
  {
    Object localObject = this.mSubscriptionManager.getActiveSubscriptionInfoList();
    if (localObject != null)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)((Iterator)localObject).next();
        int i = TelephonyManager.getDefault().getSimState(localSubscriptionInfo.getSimSlotIndex());
        if ((i != 1) && (i != 0)) {
          return true;
        }
      }
    }
    return false;
  }
  
  private void launchChooseOrConfirmLock(int paramInt)
  {
    Intent localIntent = new Intent();
    if (!new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(paramInt, getString(2131690547), null, null, this.mChallenge))
    {
      localIntent.setClassName("com.android.settings", ChooseLockGeneric.class.getName());
      localIntent.putExtra("minimum_quality", 65536);
      localIntent.putExtra("hide_disabled_prefs", true);
      localIntent.putExtra("has_challenge", true);
      localIntent.putExtra("challenge", this.mChallenge);
      startActivityForResult(localIntent, 102);
    }
  }
  
  private void launchConfirmDeviceLockForUnification()
  {
    String str = getActivity().getString(2131691150);
    if (!new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(128, str, true, MY_USER_ID)) {
      launchConfirmProfileLockForUnification();
    }
  }
  
  private void launchConfirmProfileLockForUnification()
  {
    String str = getActivity().getString(2131691151);
    if (!new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(129, str, true, this.mProfileChallengeUserId))
    {
      unifyLocks();
      createPreferenceHierarchy();
    }
  }
  
  private void maybeAddFingerprintPreference(PreferenceGroup paramPreferenceGroup, int paramInt)
  {
    Preference localPreference = FingerprintSettings.getFingerprintPreferenceForUser(paramPreferenceGroup.getContext(), paramInt);
    if (localPreference != null) {
      paramPreferenceGroup.addPreference(localPreference);
    }
  }
  
  private void setNonMarketAppsAllowed(boolean paramBoolean)
  {
    if (((UserManager)getActivity().getSystemService("user")).hasUserRestriction("no_install_unknown_sources")) {
      return;
    }
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "install_non_market_apps", i);
      return;
    }
  }
  
  private void unifyLocks()
  {
    int i = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mProfileChallengeUserId);
    if (i == 65536) {
      this.mLockPatternUtils.saveLockPattern(LockPatternUtils.stringToPattern(this.mCurrentProfilePassword), this.mCurrentDevicePassword, MY_USER_ID);
    }
    for (;;)
    {
      this.mLockPatternUtils.setSeparateProfileChallengeEnabled(this.mProfileChallengeUserId, false, this.mCurrentProfilePassword);
      boolean bool = this.mLockPatternUtils.isVisiblePatternEnabled(this.mProfileChallengeUserId);
      this.mLockPatternUtils.setVisiblePatternEnabled(bool, MY_USER_ID);
      this.mCurrentDevicePassword = null;
      this.mCurrentProfilePassword = null;
      return;
      this.mLockPatternUtils.saveLockPassword(this.mCurrentProfilePassword, this.mCurrentDevicePassword, i, MY_USER_ID);
    }
  }
  
  private void unifyUncompliantLocks()
  {
    this.mLockPatternUtils.setSeparateProfileChallengeEnabled(this.mProfileChallengeUserId, false, this.mCurrentProfilePassword);
    startFragment(this, "com.android.settings.ChooseLockGeneric$ChooseLockGenericFragment", 2131691144, 123, null);
  }
  
  private void ununifyLocks()
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("android.intent.extra.USER_ID", this.mProfileChallengeUserId);
    startFragment(this, "com.android.settings.ChooseLockGeneric$ChooseLockGenericFragment", 2131691145, 127, localBundle);
  }
  
  private void updateAddPreference()
  {
    boolean bool = false;
    int j = getContext().getResources().getInteger(17694881);
    int i;
    String str;
    label64:
    Preference localPreference;
    if (this.mFingerprintManager.getEnrolledFingerprints(MY_USER_ID).size() >= j)
    {
      i = 1;
      if (i == 0) {
        break label95;
      }
      str = getContext().getString(2131691112, new Object[] { Integer.valueOf(j) });
      localPreference = findPreference("key_fingerprint_add");
      localPreference.setSummary(str);
      if (i == 0) {
        break label103;
      }
    }
    for (;;)
    {
      localPreference.setEnabled(bool);
      return;
      i = 0;
      break;
      label95:
      str = "";
      break label64;
      label103:
      bool = true;
    }
  }
  
  private void updateUnificationPreference()
  {
    SwitchPreference localSwitchPreference;
    if (this.mUnifyProfile != null)
    {
      localSwitchPreference = this.mUnifyProfile;
      if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mProfileChallengeUserId)) {
        break label34;
      }
    }
    label34:
    for (boolean bool = false;; bool = true)
    {
      localSwitchPreference.setChecked(bool);
      return;
    }
  }
  
  private void warnAppInstallation()
  {
    this.mWarnInstallApps = new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(2131691876)).setIcon(17301543).setMessage(getResources().getString(2131692073)).setPositiveButton(17039379, this).setNegativeButton(17039369, this).show();
  }
  
  public boolean checkIfNeedPassword()
  {
    switch (this.mLockPatternUtils.getKeyguardStoredPasswordQuality(MY_USER_ID))
    {
    default: 
      return false;
    }
    return true;
  }
  
  protected int getHelpResource()
  {
    return 2131693027;
  }
  
  protected int getMetricsCategory()
  {
    return 87;
  }
  
  /* Error */
  public void gotoAppLockerPage()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: new 263	android/content/Intent
    //   5: dup
    //   6: invokespecial 414	android/content/Intent:<init>	()V
    //   9: astore_2
    //   10: aload_2
    //   11: ldc_w 817
    //   14: ldc_w 1041
    //   17: invokevirtual 823	android/content/Intent:setClassName	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   20: pop
    //   21: aload_0
    //   22: invokevirtual 391	com/android/settings/SecuritySettings:getActivity	()Landroid/app/Activity;
    //   25: aload_2
    //   26: invokevirtual 1042	android/app/Activity:startActivity	(Landroid/content/Intent;)V
    //   29: return
    //   30: astore_2
    //   31: ldc -95
    //   33: new 720	java/lang/StringBuilder
    //   36: dup
    //   37: invokespecial 721	java/lang/StringBuilder:<init>	()V
    //   40: ldc_w 1044
    //   43: invokevirtual 727	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   46: aload_1
    //   47: invokevirtual 1047	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   50: invokevirtual 733	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   53: invokestatic 1053	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   56: pop
    //   57: return
    //   58: astore_1
    //   59: aload_2
    //   60: astore_1
    //   61: goto -30 -> 31
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	64	0	this	SecuritySettings
    //   1	46	1	localObject1	Object
    //   58	1	1	localActivityNotFoundException1	android.content.ActivityNotFoundException
    //   60	1	1	localObject2	Object
    //   9	17	2	localIntent	Intent
    //   30	30	2	localActivityNotFoundException2	android.content.ActivityNotFoundException
    // Exception table:
    //   from	to	target	type
    //   2	10	30	android/content/ActivityNotFoundException
    //   10	29	58	android/content/ActivityNotFoundException
  }
  
  public void gotoEnrollFingerprint(byte[] paramArrayOfByte)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", FingerprintEnrollEnrolling.class.getName());
    localIntent.putExtra("hw_auth_token", paramArrayOfByte);
    localIntent.putExtra("android.intent.extra.USER_ID", MY_USER_ID);
    localIntent.putExtra("show_actionbar", true);
    startActivityForResult(localIntent, 103);
  }
  
  public void gotoFingerprintEditPage(Preference paramPreference)
  {
    if (paramPreference == null) {
      return;
    }
    paramPreference = ((FingerprintPreference)paramPreference).getFingerprint();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("fingerprint_parcelable", paramPreference);
    startFragment(this, "com.oneplus.settings.opfinger.OPFingerPrintEditFragments", 2131689957, 123, localBundle);
  }
  
  public void gotoFingerprintEnrollIntroduction()
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", FingerprintEnrollIntroduction.class.getName());
    startActivity(localIntent);
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    Log.d("SecuritySettings", "SecuritySettings onActivityResult requestCode:" + paramInt1 + " resultCode:" + paramInt2);
    if ((this.mGotoEnrollClick) || (this.mGotoQuickPayClick))
    {
      if ((paramInt1 != 102) && (paramInt1 != 101)) {
        break label296;
      }
      if ((paramInt2 == 1) || (paramInt2 == -1))
      {
        if (paramIntent != null) {
          this.mToken = paramIntent.getByteArrayExtra("hw_auth_token");
        }
        if (this.mToken != null)
        {
          if (this.mFingerprintManager.getEnrolledFingerprints(MY_USER_ID).size() <= 0) {
            break label316;
          }
          if (!this.mGotoQuickPayClick) {
            break label305;
          }
          QuickPaySettings.gotoQuickPaySettingsPage(getActivity());
        }
      }
    }
    for (;;)
    {
      this.mGotoQuickPayClick = false;
      this.mGotoEnrollClick = false;
      if (this.mGotoEditFingerprintClick)
      {
        if (((paramInt1 == 102) || (paramInt1 == 105)) && ((paramInt2 == 1) || (paramInt2 == -1))) {
          gotoFingerprintEditPage(this.mFingerprintEditPreference);
        }
        this.mGotoEditFingerprintClick = false;
      }
      if (this.mGotoAppLockerClick)
      {
        if (((paramInt1 == 102) || (paramInt1 == 104)) && ((paramInt2 == 1) || (paramInt2 == -1))) {
          gotoAppLockerPage();
        }
        this.mGotoAppLockerClick = false;
      }
      if (this.mGotoFaceUnlockClick)
      {
        if (((paramInt1 == 102) || (paramInt1 == 107)) && ((paramInt2 == 1) || (paramInt2 == -1))) {
          OPFaceUnlockSettings.gotoFaceUnlockSettings(getActivity());
        }
        this.mGotoFaceUnlockClick = false;
      }
      if ((paramInt1 != 126) || (paramInt2 != -1)) {
        break label327;
      }
      if (this.mTrustAgentClickIntent != null)
      {
        startActivity(this.mTrustAgentClickIntent);
        this.mTrustAgentClickIntent = null;
      }
      return;
      label296:
      if (paramInt1 == 106)
      {
        break;
        label305:
        gotoEnrollFingerprint(this.mToken);
        continue;
        label316:
        gotoFindSensor(this.mToken);
      }
    }
    label327:
    if ((paramInt1 == 128) && (paramInt2 == -1))
    {
      this.mCurrentDevicePassword = paramIntent.getStringExtra("password");
      launchConfirmProfileLockForUnification();
      return;
    }
    if ((paramInt1 == 129) && (paramInt2 == -1))
    {
      this.mCurrentProfilePassword = paramIntent.getStringExtra("password");
      unifyLocks();
      return;
    }
    if ((paramInt1 == 130) && (paramInt2 == -1))
    {
      ununifyLocks();
      return;
    }
    createPreferenceHierarchy();
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    boolean bool;
    if (paramDialogInterface == this.mWarnInstallApps)
    {
      if (paramInt != -1) {
        break label65;
      }
      bool = true;
      setNonMarketAppsAllowed(bool);
      if (this.mToggleAppInstallation != null) {
        this.mToggleAppInstallation.setChecked(bool);
      }
      if ("android.settings.SECURITY_SETTINGS".equals(getIntent().getAction()))
      {
        if (!bool) {
          break label70;
        }
        setResult(-1);
      }
    }
    for (;;)
    {
      finish();
      return;
      label65:
      bool = false;
      break;
      label70:
      setResult(0);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mSubscriptionManager = SubscriptionManager.from(getActivity());
    this.mLockPatternUtils = new LockPatternUtils(getActivity());
    this.mManagedPasswordProvider = ManagedLockPasswordProvider.get(getActivity(), MY_USER_ID);
    this.mDPM = ((DevicePolicyManager)getSystemService("device_policy"));
    this.mUm = UserManager.get(getActivity());
    this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
    this.mFingerprintManager = ((FingerprintManager)getActivity().getSystemService("fingerprint"));
    this.isSupportFinger = this.mFingerprintManager.isHardwareDetected();
    if ((paramBundle != null) && (paramBundle.containsKey("trust_agent_click_intent"))) {
      this.mTrustAgentClickIntent = ((Intent)paramBundle.getParcelable("trust_agent_click_intent"));
    }
    if (paramBundle != null)
    {
      this.mGotoEnrollClick = paramBundle.getBoolean("GotoEnrollClick");
      this.mGotoAppLockerClick = paramBundle.getBoolean("GotoAppLockerClick");
      this.mGotoFaceUnlockClick = paramBundle.getBoolean("GotoFaceunlockClick");
      this.mGotoQuickPayClick = paramBundle.getBoolean("GotoQuickPayClick");
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (this.mWarnInstallApps != null) {
      this.mWarnInstallApps.dismiss();
    }
  }
  
  public void onGearClick(GearPreference paramGearPreference)
  {
    if ("unlock_set_or_change".equals(paramGearPreference.getKey())) {
      startFragment(this, SecuritySubSettings.class.getName(), 0, 0, null);
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    int i = 0;
    boolean bool = false;
    Object localObject = paramPreference.getKey();
    paramPreference = this.mChooseLockSettingsHelper.utils();
    if ("visiblepattern_profile".equals(localObject))
    {
      if (Utils.startQuietModeDialogIfNecessary(getActivity(), this.mUm, this.mProfileChallengeUserId)) {
        return false;
      }
      paramPreference.setVisiblePatternEnabled(((Boolean)paramObject).booleanValue(), this.mProfileChallengeUserId);
    }
    do
    {
      do
      {
        return true;
        if (!"unification".equals(localObject)) {
          break;
        }
        if (Utils.startQuietModeDialogIfNecessary(getActivity(), this.mUm, this.mProfileChallengeUserId)) {
          return false;
        }
        if (((Boolean)paramObject).booleanValue())
        {
          if (this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mProfileChallengeUserId) >= 65536) {
            bool = this.mLockPatternUtils.isSeparateProfileChallengeAllowedToUnify(this.mProfileChallengeUserId);
          }
          UnificationConfirmationDialog.newIntance(bool).show(getChildFragmentManager(), "unification_dialog");
          return true;
        }
        paramPreference = getActivity().getString(2131691150);
      } while (new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(130, paramPreference, true, MY_USER_ID));
      ununifyLocks();
      return true;
      if ("show_password".equals(localObject))
      {
        localObject = getContentResolver();
        if (((Boolean)paramObject).booleanValue()) {
          i = 1;
        }
        Settings.System.putInt((ContentResolver)localObject, "show_password", i);
        paramPreference.setVisiblePasswordEnabled(((Boolean)paramObject).booleanValue(), MY_USER_ID);
        return true;
      }
    } while (!"toggle_install_applications".equals(localObject));
    if (((Boolean)paramObject).booleanValue())
    {
      this.mToggleAppInstallation.setChecked(false);
      warnAppInstallation();
      return false;
    }
    setNonMarketAppsAllowed(false);
    return true;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    Object localObject = paramPreference.getKey();
    if ("oneplus_app_locker".equals(localObject))
    {
      this.mGotoAppLockerClick = true;
      launchChooseOrConfirmLock(104);
    }
    do
    {
      return true;
      if ("oneplus_face_unlock".equals(localObject))
      {
        this.mGotoFaceUnlockClick = true;
        launchChooseOrConfirmLock(107);
        return true;
      }
      if ("oneplus_quick_pay".equals(localObject))
      {
        QuickPaySettings.gotoQuickPaySettingsPage(getActivity());
        return true;
      }
      if ("key_fingerprint_add".equals(localObject))
      {
        this.mGotoEnrollClick = true;
        this.mChallenge = this.mFingerprintManager.preEnroll();
        if (!checkIfNeedPassword())
        {
          gotoFingerprintEnrollIntroduction();
          return true;
        }
        launchChooseOrConfirmLock(101);
        return true;
      }
      if ((paramPreference instanceof FingerprintPreference))
      {
        this.mGotoEditFingerprintClick = true;
        this.mFingerprintEditPreference = paramPreference;
        launchChooseOrConfirmLock(105);
        return true;
      }
      if ("unlock_set_or_change".equals(localObject))
      {
        startFragment(this, "com.android.settings.ChooseLockGeneric$ChooseLockGenericFragment", 2131691144, 123, null);
        return true;
      }
      if ("unlock_set_or_change_profile".equals(localObject))
      {
        if (Utils.startQuietModeDialogIfNecessary(getActivity(), this.mUm, this.mProfileChallengeUserId)) {
          return false;
        }
        paramPreference = new Bundle();
        paramPreference.putInt("android.intent.extra.USER_ID", this.mProfileChallengeUserId);
        startFragment(this, "com.android.settings.ChooseLockGeneric$ChooseLockGenericFragment", 2131691145, 127, paramPreference);
        return true;
      }
      if (!"trust_agent".equals(localObject)) {
        break;
      }
      localObject = new ChooseLockSettingsHelper(getActivity(), this);
      this.mTrustAgentClickIntent = paramPreference.getIntent();
    } while ((((ChooseLockSettingsHelper)localObject).launchConfirmationActivity(126, paramPreference.getTitle())) || (this.mTrustAgentClickIntent == null));
    startActivity(this.mTrustAgentClickIntent);
    this.mTrustAgentClickIntent = null;
    return true;
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onResume()
  {
    boolean bool2 = false;
    super.onResume();
    this.mGotoEditFingerprintClick = false;
    this.mGotoEnrollClick = false;
    this.mGotoAppLockerClick = false;
    this.mGotoFaceUnlockClick = false;
    this.mGotoQuickPayClick = false;
    createPreferenceHierarchy();
    if (this.mVisiblePatternProfile != null) {
      this.mVisiblePatternProfile.setChecked(this.mLockPatternUtils.isVisiblePatternEnabled(this.mProfileChallengeUserId));
    }
    updateUnificationPreference();
    if (this.mShowPassword != null)
    {
      localObject = this.mShowPassword;
      if (Settings.System.getInt(getContentResolver(), "show_password", 1) == 0) {
        break label115;
      }
    }
    label115:
    for (boolean bool1 = true;; bool1 = false)
    {
      ((SwitchPreference)localObject).setChecked(bool1);
      if ((this.mResetCredentials != null) && (!this.mResetCredentials.isDisabledByAdmin())) {
        break;
      }
      return;
    }
    Object localObject = this.mResetCredentials;
    if (this.mKeyStore.isEmpty()) {}
    for (bool1 = bool2;; bool1 = true)
    {
      ((RestrictedPreference)localObject).setEnabled(bool1);
      return;
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (this.mTrustAgentClickIntent != null) {
      paramBundle.putParcelable("trust_agent_click_intent", this.mTrustAgentClickIntent);
    }
    paramBundle.putBoolean("GotoEnrollClick", this.mGotoEnrollClick);
    paramBundle.putBoolean("GotoAppLockerClick", this.mGotoAppLockerClick);
    paramBundle.putBoolean("GotoFaceunlockClick", this.mGotoFaceUnlockClick);
    paramBundle.putBoolean("GotoQuickPayClick", this.mGotoQuickPayClick);
  }
  
  public static class FingerprintPreference
    extends Preference
  {
    private Fingerprint mFingerprint;
    private View mView;
    
    public FingerprintPreference(Context paramContext)
    {
      this(paramContext, null);
    }
    
    public FingerprintPreference(Context paramContext, AttributeSet paramAttributeSet)
    {
      this(paramContext, paramAttributeSet, 16842894);
    }
    
    public FingerprintPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      this(paramContext, paramAttributeSet, paramInt, 0);
    }
    
    public FingerprintPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
      super(paramAttributeSet, paramInt1, paramInt2);
    }
    
    public Fingerprint getFingerprint()
    {
      return this.mFingerprint;
    }
    
    public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
    {
      super.onBindViewHolder(paramPreferenceViewHolder);
    }
    
    public void setFingerprint(Fingerprint paramFingerprint)
    {
      this.mFingerprint = paramFingerprint;
    }
  }
  
  private static class SecuritySearchIndexProvider
    extends BaseSearchIndexProvider
  {
    private SearchIndexableResource getSearchResource(Context paramContext, int paramInt)
    {
      paramContext = new SearchIndexableResource(paramContext);
      paramContext.xmlResId = paramInt;
      return paramContext;
    }
    
    private boolean isPasswordManaged(int paramInt, Context paramContext, DevicePolicyManager paramDevicePolicyManager)
    {
      boolean bool2 = false;
      paramContext = RestrictedLockUtils.checkIfPasswordQualityIsSet(paramContext, paramInt);
      boolean bool1 = bool2;
      if (paramContext != null)
      {
        bool1 = bool2;
        if (paramDevicePolicyManager.getPasswordQuality(paramContext.component, paramInt) == 524288) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public List<String> getNonIndexableKeys(Context paramContext)
    {
      ArrayList localArrayList = new ArrayList();
      LockPatternUtils localLockPatternUtils = new LockPatternUtils(paramContext);
      UserManager localUserManager = UserManager.get(paramContext);
      paramContext = TelephonyManager.from(paramContext);
      if ((localUserManager.isAdminUser()) && (paramContext.hasIccCard()))
      {
        if (!localUserManager.isAdminUser())
        {
          localArrayList.add("oneplus_app_locker");
          localArrayList.add("oneplus_quick_pay");
        }
        if ((!localUserManager.isAdminUser()) || (!OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication))) {
          break label147;
        }
      }
      for (;;)
      {
        if (localUserManager.hasUserRestriction("no_config_credentials")) {
          localArrayList.add("credentials_management");
        }
        if (!localLockPatternUtils.isSecure(SecuritySettings.-get0()))
        {
          localArrayList.add("trust_agent");
          localArrayList.add("manage_trust_agents");
        }
        return localArrayList;
        localArrayList.add("sim_lock");
        break;
        label147:
        localArrayList.add("oneplus_face_unlock");
      }
    }
    
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramContext, boolean paramBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      Object localObject1 = paramContext.getResources();
      String str = ((Resources)localObject1).getString(2131690547);
      Object localObject2 = new SearchIndexableRaw(paramContext);
      ((SearchIndexableRaw)localObject2).title = str;
      ((SearchIndexableRaw)localObject2).screenTitle = str;
      localArrayList.add(localObject2);
      localObject2 = UserManager.get(paramContext);
      if (!((UserManager)localObject2).isAdminUser()) {
        if (!((UserManager)localObject2).isLinkedUser()) {
          break label411;
        }
      }
      label411:
      for (int i = 2131691056;; i = 2131691054)
      {
        Object localObject3 = new SearchIndexableRaw(paramContext);
        ((SearchIndexableRaw)localObject3).title = ((Resources)localObject1).getString(i);
        ((SearchIndexableRaw)localObject3).screenTitle = str;
        localArrayList.add(localObject3);
        localObject3 = (FingerprintManager)paramContext.getSystemService("fingerprint");
        if ((localObject3 != null) && (((FingerprintManager)localObject3).isHardwareDetected()))
        {
          localObject3 = new SearchIndexableRaw(paramContext);
          ((SearchIndexableRaw)localObject3).title = ((Resources)localObject1).getString(2131691065);
          ((SearchIndexableRaw)localObject3).screenTitle = str;
          localArrayList.add(localObject3);
          localObject3 = new SearchIndexableRaw(paramContext);
          ((SearchIndexableRaw)localObject3).title = ((Resources)localObject1).getString(2131691066);
          ((SearchIndexableRaw)localObject3).screenTitle = str;
          localArrayList.add(localObject3);
        }
        localObject3 = new LockPatternUtils(paramContext);
        i = Utils.getManagedProfileId((UserManager)localObject2, SecuritySettings.-get0());
        if ((i != 55536) && (((LockPatternUtils)localObject3).isSeparateProfileChallengeAllowed(i)) && (((LockPatternUtils)localObject3).getKeyguardStoredPasswordQuality(i) >= 65536) && (((LockPatternUtils)localObject3).isSeparateProfileChallengeAllowedToUnify(i)))
        {
          localObject2 = new SearchIndexableRaw(paramContext);
          ((SearchIndexableRaw)localObject2).title = ((Resources)localObject1).getString(2131692056);
          ((SearchIndexableRaw)localObject2).screenTitle = str;
          localArrayList.add(localObject2);
        }
        if (!((LockPatternUtils)localObject3).isSecure(SecuritySettings.-get0())) {
          break;
        }
        localObject1 = SecuritySettings.-wrap1(paramContext, (LockPatternUtils)localObject3, (DevicePolicyManager)paramContext.getSystemService(DevicePolicyManager.class));
        i = 0;
        while (i < ((ArrayList)localObject1).size())
        {
          localObject2 = (TrustAgentUtils.TrustAgentComponentInfo)((ArrayList)localObject1).get(i);
          localObject3 = new SearchIndexableRaw(paramContext);
          ((SearchIndexableRaw)localObject3).title = ((TrustAgentUtils.TrustAgentComponentInfo)localObject2).title;
          ((SearchIndexableRaw)localObject3).screenTitle = str;
          localArrayList.add(localObject3);
          i += 1;
        }
      }
      return localArrayList;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramContext, boolean paramBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      Object localObject = new LockPatternUtils(paramContext);
      ManagedLockPasswordProvider localManagedLockPasswordProvider = ManagedLockPasswordProvider.get(paramContext, SecuritySettings.-get0());
      DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramContext.getSystemService("device_policy");
      UserManager localUserManager = UserManager.get(paramContext);
      int i = Utils.getManagedProfileId(localUserManager, SecuritySettings.-get0());
      if ((isPasswordManaged(SecuritySettings.-get0(), paramContext, localDevicePolicyManager)) || ((i != 55536) && (!((LockPatternUtils)localObject).isSeparateProfileChallengeAllowed(i)) && (isPasswordManaged(i, paramContext, localDevicePolicyManager))))
      {
        if ((i != 55536) && (((LockPatternUtils)localObject).isSeparateProfileChallengeAllowed(i)) && (!isPasswordManaged(i, paramContext, localDevicePolicyManager))) {
          break label240;
        }
        label121:
        if (localUserManager.isAdminUser()) {
          switch (localDevicePolicyManager.getStorageEncryptionStatus())
          {
          }
        }
      }
      for (;;)
      {
        localObject = getSearchResource(paramContext, SecuritySettings.SecuritySubSettings.-wrap0(paramContext, (LockPatternUtils)localObject, localManagedLockPasswordProvider));
        ((SearchIndexableResource)localObject).className = SecuritySettings.SecuritySubSettings.class.getName();
        localArrayList.add(localObject);
        localArrayList.add(getSearchResource(paramContext, 2131230842));
        return localArrayList;
        localArrayList.add(getSearchResource(paramContext, SecuritySettings.-wrap0(paramContext, (LockPatternUtils)localObject, localManagedLockPasswordProvider, SecuritySettings.-get0())));
        break;
        label240:
        localArrayList.add(getSearchResource(paramContext, SecuritySettings.-wrap0(paramContext, (LockPatternUtils)localObject, localManagedLockPasswordProvider, i)));
        break label121;
        localArrayList.add(getSearchResource(paramContext, 2131230837));
        continue;
        localArrayList.add(getSearchResource(paramContext, 2131230855));
      }
    }
  }
  
  public static class SecuritySubSettings
    extends SettingsPreferenceFragment
    implements Preference.OnPreferenceChangeListener
  {
    private static final String KEY_LOCK_AFTER_TIMEOUT = "lock_after_timeout";
    private static final String KEY_OWNER_INFO_SETTINGS = "owner_info_settings";
    private static final String KEY_POWER_INSTANTLY_LOCKS = "power_button_instantly_locks";
    private static final String KEY_VISIBLE_PATTERN = "visiblepattern";
    private static final String[] SWITCH_PREFERENCE_KEYS = { "lock_after_timeout", "visiblepattern", "power_button_instantly_locks" };
    private DevicePolicyManager mDPM;
    private TimeoutListPreference mLockAfter;
    private LockPatternUtils mLockPatternUtils;
    private RestrictedPreference mOwnerInfoPref;
    private SwitchPreference mPowerButtonInstantlyLocks;
    private SwitchPreference mVisiblePattern;
    
    private void createPreferenceHierarchy()
    {
      Object localObject = getPreferenceScreen();
      if (localObject != null) {
        ((PreferenceScreen)localObject).removeAll();
      }
      addPreferencesFromResource(getResIdForLockUnlockSubScreen(getActivity(), new LockPatternUtils(getContext()), ManagedLockPasswordProvider.get(getContext(), SecuritySettings.-get0())));
      this.mLockAfter = ((TimeoutListPreference)findPreference("lock_after_timeout"));
      if (this.mLockAfter != null)
      {
        setupLockAfterPreference();
        updateLockAfterPreferenceSummary();
      }
      this.mVisiblePattern = ((SwitchPreference)findPreference("visiblepattern"));
      this.mPowerButtonInstantlyLocks = ((SwitchPreference)findPreference("power_button_instantly_locks"));
      localObject = findPreference("trust_agent");
      if ((this.mPowerButtonInstantlyLocks != null) && (localObject != null) && (((Preference)localObject).getTitle().length() > 0)) {
        this.mPowerButtonInstantlyLocks.setSummary(getString(2131692048, new Object[] { ((Preference)localObject).getTitle() }));
      }
      this.mOwnerInfoPref = ((RestrictedPreference)findPreference("owner_info_settings"));
      if (this.mOwnerInfoPref != null)
      {
        if (this.mLockPatternUtils.isDeviceOwnerInfoEnabled())
        {
          localObject = RestrictedLockUtils.getDeviceOwner(getActivity());
          this.mOwnerInfoPref.setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin)localObject);
        }
      }
      else
      {
        int i = 0;
        while (i < SWITCH_PREFERENCE_KEYS.length)
        {
          localObject = findPreference(SWITCH_PREFERENCE_KEYS[i]);
          if (localObject != null) {
            ((Preference)localObject).setOnPreferenceChangeListener(this);
          }
          i += 1;
        }
      }
      this.mOwnerInfoPref.setDisabledByAdmin(null);
      localObject = this.mOwnerInfoPref;
      if (this.mLockPatternUtils.isLockScreenDisabled(SecuritySettings.-get0())) {}
      for (boolean bool = false;; bool = true)
      {
        ((RestrictedPreference)localObject).setEnabled(bool);
        if (!this.mOwnerInfoPref.isEnabled()) {
          break;
        }
        this.mOwnerInfoPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
          public boolean onPreferenceClick(Preference paramAnonymousPreference)
          {
            OwnerInfoSettings.show(SecuritySettings.SecuritySubSettings.this);
            return true;
          }
        });
        break;
      }
    }
    
    private static int getResIdForLockUnlockSubScreen(Context paramContext, LockPatternUtils paramLockPatternUtils, ManagedLockPasswordProvider paramManagedLockPasswordProvider)
    {
      if (paramLockPatternUtils.isSecure(SecuritySettings.-get0())) {
        switch (paramLockPatternUtils.getKeyguardStoredPasswordQuality(SecuritySettings.-get0()))
        {
        }
      }
      while (paramLockPatternUtils.isLockScreenDisabled(SecuritySettings.-get0()))
      {
        return 0;
        return 2131230848;
        return 2131230852;
        return 2131230845;
        return paramManagedLockPasswordProvider.getResIdForLockUnlockSubScreen();
      }
      return 2131230854;
    }
    
    private void setupLockAfterPreference()
    {
      long l = Settings.Secure.getLong(getContentResolver(), "lock_screen_lock_after_timeout", 5000L);
      this.mLockAfter.setValue(String.valueOf(l));
      this.mLockAfter.setOnPreferenceChangeListener(this);
      if (this.mDPM != null)
      {
        RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfMaximumTimeToLockIsSet(getActivity());
        l = Math.max(0L, this.mDPM.getMaximumTimeToLockForUserAndProfiles(UserHandle.myUserId()) - Math.max(0, Settings.System.getInt(getContentResolver(), "screen_off_timeout", 0)));
        this.mLockAfter.removeUnusableTimeouts(l, localEnforcedAdmin);
      }
    }
    
    private void updateLockAfterPreferenceSummary()
    {
      Object localObject;
      if (this.mLockAfter.isDisabledByAdmin()) {
        localObject = getString(2131693595);
      }
      for (;;)
      {
        this.mLockAfter.setSummary((CharSequence)localObject);
        return;
        long l = Settings.Secure.getLong(getContentResolver(), "lock_screen_lock_after_timeout", 5000L);
        localObject = this.mLockAfter.getEntries();
        CharSequence[] arrayOfCharSequence = this.mLockAfter.getEntryValues();
        int j = 0;
        int i = 0;
        while (i < arrayOfCharSequence.length)
        {
          if (l >= Long.valueOf(arrayOfCharSequence[i].toString()).longValue()) {
            j = i;
          }
          i += 1;
        }
        Preference localPreference = findPreference("trust_agent");
        if ((localPreference != null) && (localPreference.getTitle().length() > 0))
        {
          if (Long.valueOf(arrayOfCharSequence[j].toString()).longValue() == 0L) {
            localObject = getString(2131691045, new Object[] { localPreference.getTitle() });
          } else {
            localObject = getString(2131691046, new Object[] { localObject[j], localPreference.getTitle() });
          }
        }
        else {
          localObject = getString(2131691044, new Object[] { localObject[j] });
        }
      }
    }
    
    protected int getMetricsCategory()
    {
      return 87;
    }
    
    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      createPreferenceHierarchy();
    }
    
    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      this.mLockPatternUtils = new LockPatternUtils(getContext());
      this.mDPM = ((DevicePolicyManager)getContext().getSystemService(DevicePolicyManager.class));
      createPreferenceHierarchy();
    }
    
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
    {
      paramPreference = paramPreference.getKey();
      if ("power_button_instantly_locks".equals(paramPreference)) {
        this.mLockPatternUtils.setPowerButtonInstantlyLocks(((Boolean)paramObject).booleanValue(), SecuritySettings.-get0());
      }
      do
      {
        return true;
        if ("lock_after_timeout".equals(paramPreference))
        {
          i = Integer.parseInt((String)paramObject);
          try
          {
            Settings.Secure.putInt(getContentResolver(), "lock_screen_lock_after_timeout", i);
            setupLockAfterPreference();
            updateLockAfterPreferenceSummary();
            return true;
          }
          catch (NumberFormatException paramPreference)
          {
            for (;;)
            {
              Log.e("SecuritySettings", "could not persist lockAfter timeout setting", paramPreference);
            }
          }
        }
      } while (!"visiblepattern".equals(paramPreference));
      this.mLockPatternUtils.setVisiblePatternEnabled(((Boolean)paramObject).booleanValue(), SecuritySettings.-get0());
      paramPreference = getContentResolver();
      if (((Boolean)paramObject).booleanValue()) {}
      for (int i = 1;; i = 0)
      {
        Settings.System.putInt(paramPreference, "show_password", i);
        this.mLockPatternUtils.setVisiblePasswordEnabled(((Boolean)paramObject).booleanValue(), SecuritySettings.-get0());
        return true;
      }
    }
    
    public void onResume()
    {
      super.onResume();
      createPreferenceHierarchy();
      if (this.mVisiblePattern != null) {
        this.mVisiblePattern.setChecked(this.mLockPatternUtils.isVisiblePatternEnabled(SecuritySettings.-get0()));
      }
      if (this.mPowerButtonInstantlyLocks != null) {
        this.mPowerButtonInstantlyLocks.setChecked(this.mLockPatternUtils.getPowerButtonInstantlyLocks(SecuritySettings.-get0()));
      }
      updateOwnerInfo();
    }
    
    public void updateOwnerInfo()
    {
      if (this.mOwnerInfoPref != null)
      {
        if (this.mLockPatternUtils.isDeviceOwnerInfoEnabled()) {
          this.mOwnerInfoPref.setSummary(this.mLockPatternUtils.getDeviceOwnerInfo());
        }
      }
      else {
        return;
      }
      RestrictedPreference localRestrictedPreference = this.mOwnerInfoPref;
      if (this.mLockPatternUtils.isOwnerInfoEnabled(SecuritySettings.-get0())) {}
      for (String str = this.mLockPatternUtils.getOwnerInfo(SecuritySettings.-get0());; str = getString(2131691051))
      {
        localRestrictedPreference.setSummary(str);
        return;
      }
    }
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mLoader;
    
    private SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mLoader = paramSummaryLoader;
    }
    
    private void updateSummary()
    {
      String str1;
      String str2;
      String str3;
      if (OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication))
      {
        str1 = this.mContext.getString(2131689950);
        str2 = this.mContext.getString(2131690537);
        str3 = this.mContext.getString(2131690325);
        if (!OPUtils.isZh(this.mContext)) {
          break label125;
        }
        if (!OPUtils.isGuestMode()) {
          break label89;
        }
        str1 = str1 + "、" + str3;
      }
      for (;;)
      {
        this.mLoader.setSummary(this, str1);
        return;
        label89:
        str1 = str1 + "、" + str2 + "、" + str3;
        continue;
        label125:
        String.format(this.mContext.getString(2131690770), new Object[] { str1, str2, str3 });
        if (OPUtils.isGuestMode()) {
          str1 = str1 + ", " + str3;
        } else {
          str1 = str1 + ", " + str2 + ", " + str3;
        }
      }
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean) {
        updateSummary();
      }
    }
  }
  
  public static class UnificationConfirmationDialog
    extends DialogFragment
  {
    private static final String EXTRA_COMPLIANT = "compliant";
    
    public static UnificationConfirmationDialog newIntance(boolean paramBoolean)
    {
      UnificationConfirmationDialog localUnificationConfirmationDialog = new UnificationConfirmationDialog();
      Bundle localBundle = new Bundle();
      localBundle.putBoolean("compliant", paramBoolean);
      localUnificationConfirmationDialog.setArguments(localBundle);
      return localUnificationConfirmationDialog;
    }
    
    public Dialog onCreateDialog(final Bundle paramBundle)
    {
      paramBundle = (SecuritySettings)getParentFragment();
      final boolean bool = getArguments().getBoolean("compliant");
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity()).setTitle(2131692058);
      if (bool)
      {
        i = 2131692059;
        localBuilder = localBuilder.setMessage(i);
        if (!bool) {
          break label90;
        }
      }
      label90:
      for (int i = 2131692061;; i = 2131692062)
      {
        localBuilder.setPositiveButton(i, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            if (bool)
            {
              SecuritySettings.-wrap2(paramBundle);
              return;
            }
            SecuritySettings.-wrap3(paramBundle);
          }
        }).setNegativeButton(2131690993, null).create();
        i = 2131692060;
        break;
      }
    }
    
    public void onDismiss(DialogInterface paramDialogInterface)
    {
      super.onDismiss(paramDialogInterface);
      SecuritySettings.-wrap4((SecuritySettings)getParentFragment());
    }
    
    public void show(FragmentManager paramFragmentManager, String paramString)
    {
      if (paramFragmentManager.findFragmentByTag(paramString) == null) {
        super.show(paramFragmentManager, paramString);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SecuritySettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */