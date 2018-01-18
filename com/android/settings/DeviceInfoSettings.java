package com.android.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SELinux;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.CarrierConfigManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.OpFeatures;
import android.widget.Toast;
import com.android.internal.app.PlatLogoActivity;
import com.android.internal.os.IRegionalizationService;
import com.android.internal.os.RegionalizationEnvironment;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Index;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.oneplus.settings.OPAuthenticationInformationSettings;
import com.oneplus.settings.utils.OPPrefUtil;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceInfoSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final String KEY_BASEBAND_VERSION = "baseband_version";
  private static final String KEY_BUILD_NUMBER = "build_number";
  private static final String KEY_DDR_MEMORY_CAPACITY = "ddr_memory_capacity";
  private static final String KEY_DEVICE_FEEDBACK = "device_feedback";
  private static final String KEY_DEVICE_MODEL = "device_model";
  private static final String KEY_EQUIPMENT_ID = "fcc_equipment_id";
  private static final String KEY_FIRMWARE_VERSION = "firmware_version";
  private static final String KEY_KERNEL_VERSION = "kernel_version";
  private static final String KEY_MANUAL = "manual";
  private static final String KEY_MBN_VERSION = "mbn_version";
  private static final String KEY_MEMORY_CAPACITY = "memory_capacity";
  private static final String KEY_MOBILE_DEVICE_NAME = "mobile_device_name";
  private static final String KEY_ONEPLUS_AUTHENTICATION_INFORMATION = "oneplus_authentication_information";
  private static final String KEY_ONEPLUS_OOS_VERSION = "oneplus_oos_version";
  private static final String KEY_ONEPLUS_PRE_APPLICATION = "oneplus_pre_application";
  private static final String KEY_ONEPLUS_PRODUCT_INFO = "oneplus_product_info";
  private static final String KEY_OP_ELECTRONIC_CARD = "op_electronic_card";
  private static final String KEY_QGP_VERSION = "qgp_version";
  private static final String KEY_REGULATORY_INFO = "regulatory_info";
  private static final String KEY_SAFETY_LEGAL = "safetylegal";
  private static final String KEY_SECURITY_PATCH = "security_patch";
  private static final String KEY_SELINUX_STATUS = "selinux_status";
  private static final String KEY_STATUS_INFO = "status_info";
  private static final String KEY_SYSTEM_UPDATE_SETTINGS = "system_update_settings";
  private static final String KEY_UPDATE_SETTING = "additional_system_update_settings";
  private static final String LOG_TAG = "DeviceInfoSettings";
  private static final String MBN_VERSION_PATH = "/persist/speccfg/mbnversion";
  private static final String ONEPLUS_A5000 = "ONEPLUS A5000";
  private static final String ONEPLUS_A5010 = "ONEPLUS A5010";
  private static final String PROPERTY_EQUIPMENT_ID = "ro.ril.fccid";
  private static final String PROPERTY_MBN_VERSION = "persist.mbn.version";
  private static final String PROPERTY_QGP_VERSION = "persist.qgp.version";
  private static final String PROPERTY_SELINUX_STATUS = "ro.build.selinux";
  private static final String PROPERTY_URL_SAFETYLEGAL = "ro.url.safetylegal";
  private static final String QGP_VERSION_PATH = "/persist/speccfg/qgpversion";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    private boolean isPropertyMissing(String paramAnonymousString)
    {
      return SystemProperties.get(paramAnonymousString).equals("");
    }
    
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      ArrayList localArrayList = new ArrayList();
      if (isPropertyMissing("ro.build.selinux")) {
        localArrayList.add("selinux_status");
      }
      if (isPropertyMissing("ro.url.safetylegal")) {
        localArrayList.add("safetylegal");
      }
      if (isPropertyMissing("ro.ril.fccid")) {
        localArrayList.add("fcc_equipment_id");
      }
      if (Utils.isWifiOnly(paramAnonymousContext)) {
        localArrayList.add("baseband_version");
      }
      if (TextUtils.isEmpty(DeviceInfoUtils.getFeedbackReporterPackage(paramAnonymousContext))) {
        localArrayList.add("device_feedback");
      }
      if (!UserManager.get(paramAnonymousContext).isAdminUser()) {
        localArrayList.add("system_update_settings");
      }
      if (!paramAnonymousContext.getResources().getBoolean(2131558415)) {
        localArrayList.add("additional_system_update_settings");
      }
      String str1 = SystemProperties.get("ro.rf_version");
      localArrayList.add("mbn_version");
      localArrayList.add("system_update_settings");
      if (paramAnonymousContext.getPackageManager().hasSystemFeature("oem.authentication_information.support"))
      {
        if ((str1.contains("Eu")) || (str1.contains("In")) || (str1.contains("Am"))) {
          localArrayList.add("oneplus_authentication_information");
        }
        Intent localIntent = new Intent("android.settings.SHOW_REGULATORY_INFO");
        String str2 = SystemProperties.get("ro.product.model");
        if ((!paramAnonymousContext.getPackageManager().queryIntentActivities(localIntent, 0).isEmpty()) && (paramAnonymousContext.getResources().getBoolean(2131558419)) && (!str2.contains("A3003"))) {
          break label308;
        }
        label267:
        localArrayList.add("regulatory_info");
      }
      for (;;)
      {
        localArrayList.add("manual");
        localArrayList.add("op_electronic_card");
        return localArrayList;
        localArrayList.add("oneplus_authentication_information");
        break;
        label308:
        if (!str1.contains("Am")) {
          break label267;
        }
        if ((str1.contains("Eu")) || (str1.contains("In")) || (str1.contains("Ch"))) {
          localArrayList.add("regulatory_info");
        }
      }
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230758;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new DeviceInfoSettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
    }
  };
  static final int TAPS_TO_BE_A_DEVELOPER = 7;
  private RestrictedLockUtils.EnforcedAdmin mDebuggingFeaturesDisallowedAdmin;
  private boolean mDebuggingFeaturesDisallowedBySystem;
  int mDevHitCountdown;
  Toast mDevHitToast;
  private RestrictedLockUtils.EnforcedAdmin mFunDisallowedAdmin;
  private boolean mFunDisallowedBySystem;
  long[] mHits = new long[3];
  private IRegionalizationService mRegionalizationService = null;
  private UserManager mUm;
  
  private void ciActionOnSysUpdate(PersistableBundle paramPersistableBundle)
  {
    String str1 = paramPersistableBundle.getString("ci_action_on_sys_update_intent_string");
    if (!TextUtils.isEmpty(str1))
    {
      String str2 = paramPersistableBundle.getString("ci_action_on_sys_update_extra_string");
      paramPersistableBundle = paramPersistableBundle.getString("ci_action_on_sys_update_extra_val_string");
      Intent localIntent = new Intent(str1);
      if (!TextUtils.isEmpty(str2)) {
        localIntent.putExtra(str2, paramPersistableBundle);
      }
      Log.d("DeviceInfoSettings", "ciActionOnSysUpdate: broadcasting intent " + str1 + " with extra " + str2 + ", " + paramPersistableBundle);
      getActivity().getApplicationContext().sendBroadcast(localIntent);
    }
  }
  
  private String getMBNVersionValue()
  {
    Object localObject1 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    if (RegionalizationEnvironment.isSupported()) {
      this.mRegionalizationService = RegionalizationEnvironment.getRegionalizationService();
    }
    Object localObject2;
    if (this.mRegionalizationService != null) {
      localObject2 = localObject3;
    }
    try
    {
      if (!this.mRegionalizationService.checkFileExists("/persist/speccfg/mbnversion")) {
        return null;
      }
      localObject1 = localObject4;
      localObject2 = localObject3;
      if (this.mRegionalizationService.readFile("/persist/speccfg/mbnversion", "").size() > 0)
      {
        localObject2 = localObject3;
        localObject1 = (String)this.mRegionalizationService.readFile("/persist/speccfg/mbnversion", "").get(0);
      }
      localObject2 = localObject1;
      Log.d("DeviceInfoSettings", "read MBNVersion=" + (String)localObject1);
      return (String)localObject1;
    }
    catch (Exception localException)
    {
      Log.e("DeviceInfoSettings", "IOException:" + localException.getMessage());
    }
    return (String)localObject2;
  }
  
  private String getQGPVersionValue()
  {
    Object localObject1 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    if (RegionalizationEnvironment.isSupported()) {
      this.mRegionalizationService = RegionalizationEnvironment.getRegionalizationService();
    }
    Object localObject2;
    if (this.mRegionalizationService != null) {
      localObject2 = localObject3;
    }
    try
    {
      if (!this.mRegionalizationService.checkFileExists("/persist/speccfg/qgpversion")) {
        return null;
      }
      localObject1 = localObject4;
      localObject2 = localObject3;
      if (this.mRegionalizationService.readFile("/persist/speccfg/qgpversion", "").size() > 0)
      {
        localObject2 = localObject3;
        localObject1 = (String)this.mRegionalizationService.readFile("/persist/speccfg/qgpversion", "").get(0);
      }
      localObject2 = localObject1;
      Log.d("DeviceInfoSettings", "read QGPVersion=" + (String)localObject1);
      return (String)localObject1;
    }
    catch (Exception localException)
    {
      Log.e("DeviceInfoSettings", "IOException:" + localException.getMessage());
    }
    return (String)localObject2;
  }
  
  private void removePreferenceIfActivityMissing(String paramString1, String paramString2)
  {
    paramString2 = new Intent(paramString2);
    if (getPackageManager().queryIntentActivities(paramString2, 0).isEmpty())
    {
      paramString1 = findPreference(paramString1);
      if (paramString1 != null) {
        getPreferenceScreen().removePreference(paramString1);
      }
    }
  }
  
  private void removePreferenceIfBoolFalse(String paramString, int paramInt)
  {
    if (!getResources().getBoolean(paramInt))
    {
      paramString = findPreference(paramString);
      if (paramString != null) {
        getPreferenceScreen().removePreference(paramString);
      }
    }
  }
  
  private void removePreferenceIfPropertyMissing(PreferenceGroup paramPreferenceGroup, String paramString1, String paramString2)
  {
    if (SystemProperties.get(paramString2).equals("")) {}
    try
    {
      paramPreferenceGroup.removePreference(findPreference(paramString1));
      return;
    }
    catch (RuntimeException paramPreferenceGroup)
    {
      Log.d("DeviceInfoSettings", "Property '" + paramString2 + "' missing and no '" + paramString1 + "' preference");
    }
  }
  
  private void sendFeedback()
  {
    String str = DeviceInfoUtils.getFeedbackReporterPackage(getActivity());
    if (TextUtils.isEmpty(str)) {
      return;
    }
    Intent localIntent = new Intent("android.intent.action.BUG_REPORT");
    localIntent.setPackage(str);
    startActivityForResult(localIntent, 0);
  }
  
  private void setStringSummary(String paramString1, String paramString2)
  {
    try
    {
      findPreference(paramString1).setSummary(paramString2);
      return;
    }
    catch (RuntimeException paramString2)
    {
      findPreference(paramString1).setSummary(getResources().getString(2131690786));
    }
  }
  
  private void setValueSummary(String paramString1, String paramString2)
  {
    try
    {
      findPreference(paramString1).setSummary(SystemProperties.get(paramString2, getResources().getString(2131690786)));
      return;
    }
    catch (RuntimeException paramString1) {}
  }
  
  protected int getHelpResource()
  {
    return 2131693010;
  }
  
  protected int getMetricsCategory()
  {
    return 40;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUm = UserManager.get(getActivity());
    addPreferencesFromResource(2131230758);
    setStringSummary("firmware_version", Build.VERSION.RELEASE);
    findPreference("firmware_version").setEnabled(true);
    paramBundle = DeviceInfoUtils.getSecurityPatch();
    Object localObject1;
    label252:
    label361:
    label470:
    Object localObject2;
    if (!TextUtils.isEmpty(paramBundle))
    {
      setStringSummary("security_patch", paramBundle);
      getPreferenceScreen().removePreference(findPreference("op_electronic_card"));
      setValueSummary("baseband_version", "gsm.version.baseband");
      setStringSummary("device_model", Build.MODEL + DeviceInfoUtils.getMsvSuffix());
      setValueSummary("fcc_equipment_id", "ro.ril.fccid");
      setStringSummary("device_model", Build.MODEL);
      setStringSummary("build_number", Build.DISPLAY);
      paramBundle = Settings.System.getString(getActivity().getContentResolver(), "oem_oneplus_modified_devicename");
      localObject1 = Settings.System.getString(getActivity().getContentResolver(), "oem_oneplus_devicename");
      if ((paramBundle != null) || ((localObject1 != null) && (!((String)localObject1).equals("oneplus")) && (!((String)localObject1).equals("ONE E1001")) && (!((String)localObject1).equals("ONE E1003")) && (!((String)localObject1).equals("ONE E1005")))) {
        break label724;
      }
      paramBundle = SystemProperties.get("ro.display.series");
      Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", paramBundle);
      Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_modified_devicename", "1");
      setStringSummary("mobile_device_name", paramBundle);
      setStringSummary("ddr_memory_capacity", OPUtils.showROMStorage());
      setStringSummary("memory_capacity", OPUtils.getTotalMemory());
      findPreference("build_number").setEnabled(true);
      findPreference("kernel_version").setSummary(DeviceInfoUtils.customizeFormatKernelVersion(getResources().getBoolean(2131558481)));
      paramBundle = getMBNVersionValue();
      setStringSummary("mbn_version", paramBundle);
      if (paramBundle == null) {
        getPreferenceScreen().removePreference(findPreference("mbn_version"));
      }
      if (SELinux.isSELinuxEnabled()) {
        break label761;
      }
      setStringSummary("selinux_status", getResources().getString(2131692904));
      removePreferenceIfPropertyMissing(getPreferenceScreen(), "selinux_status", "ro.build.selinux");
      removePreferenceIfPropertyMissing(getPreferenceScreen(), "safetylegal", "ro.url.safetylegal");
      removePreferenceIfPropertyMissing(getPreferenceScreen(), "fcc_equipment_id", "ro.ril.fccid");
      if (Utils.isWifiOnly(getActivity())) {
        getPreferenceScreen().removePreference(findPreference("baseband_version"));
      }
      if (TextUtils.isEmpty(DeviceInfoUtils.getFeedbackReporterPackage(getActivity()))) {
        getPreferenceScreen().removePreference(findPreference("device_feedback"));
      }
      if ((!Build.MODEL.equalsIgnoreCase("ONEPLUS A5000")) && (!Build.MODEL.equalsIgnoreCase("ONEPLUS A5010"))) {
        break label786;
      }
      getActivity();
      removePreference("system_update_settings");
      if (getResources().getBoolean(2131558436)) {
        removePreference("system_update_settings");
      }
      removePreferenceIfBoolFalse("additional_system_update_settings", 2131558415);
      removePreferenceIfBoolFalse("manual", 2131558417);
      localObject1 = new Intent("android.settings.SHOW_REGULATORY_INFO");
      localObject2 = SystemProperties.get("ro.product.model");
      paramBundle = SystemProperties.get("ro.rf_version");
      if ((!Build.MODEL.equalsIgnoreCase("ONEPLUS A5000")) && (!Build.MODEL.equalsIgnoreCase("ONEPLUS A5010"))) {
        break label821;
      }
      label565:
      removePreferenceIfActivityMissing("regulatory_info", "android.settings.SHOW_REGULATORY_INFO");
      localObject1 = findPreference("regulatory_info");
      localObject2 = findPreference("oneplus_authentication_information");
      if ((!Build.MODEL.equalsIgnoreCase("ONEPLUS A5000")) && (!Build.MODEL.equalsIgnoreCase("ONEPLUS A5010"))) {
        break label894;
      }
      label610:
      if (!OpFeatures.isSupport(new int[] { 1 })) {
        break label995;
      }
      findPreference("oneplus_oos_version").setTitle(getActivity().getResources().getString(2131690727));
      findPreference("oneplus_oos_version").setSummary(SystemProperties.get("ro.oxygen.version", getResources().getString(2131690786)));
      label670:
      if ((OPUtils.isO2()) || (!OPUtils.isSurportProductInfo(getPrefContext()))) {
        break label1063;
      }
    }
    for (;;)
    {
      if (OPUtils.isGuestMode()) {
        getPreferenceScreen().removePreference(findPreference("status_info"));
      }
      return;
      getPreferenceScreen().removePreference(findPreference("security_patch"));
      break;
      label724:
      paramBundle = (Bundle)localObject1;
      if (((String)localObject1).length() <= 32) {
        break label252;
      }
      paramBundle = ((String)localObject1).substring(0, 31);
      Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", paramBundle);
      break label252;
      label761:
      if (SELinux.isSELinuxEnforced()) {
        break label361;
      }
      setStringSummary("selinux_status", getResources().getString(2131692905));
      break label361;
      label786:
      if (getActivity().getPackageManager().hasSystemFeature("oem.authentication_information.support")) {
        break label470;
      }
      paramBundle = findPreference("oneplus_authentication_information");
      getPreferenceScreen().removePreference(paramBundle);
      break label470;
      label821:
      if ((!getPackageManager().queryIntentActivities((Intent)localObject1, 0).isEmpty()) && (getResources().getBoolean(2131558419)) && (!((String)localObject2).contains("A3003")) && (paramBundle.contains("Am"))) {
        break label565;
      }
      localObject1 = findPreference("regulatory_info");
      if (localObject1 == null) {
        break label565;
      }
      getPreferenceScreen().removePreference((Preference)localObject1);
      break label565;
      label894:
      if ((paramBundle.contains("Eu")) || (paramBundle.contains("In")))
      {
        if (localObject1 != null) {
          getPreferenceScreen().removePreference((Preference)localObject1);
        }
        if (localObject2 == null) {
          break label610;
        }
        getPreferenceScreen().removePreference((Preference)localObject2);
        break label610;
      }
      if (paramBundle.contains("Ch"))
      {
        if (localObject1 == null) {
          break label610;
        }
        getPreferenceScreen().removePreference((Preference)localObject1);
        break label610;
      }
      if ((!paramBundle.contains("Am")) || (localObject2 == null)) {
        break label610;
      }
      getPreferenceScreen().removePreference((Preference)localObject2);
      break label610;
      label995:
      findPreference("oneplus_oos_version").setTitle(getActivity().getResources().getString(2131690162).replace("H2", "H₂"));
      findPreference("oneplus_oos_version").setSummary(SystemProperties.get("ro.rom.version", getResources().getString(2131690786)).replace("H2", "H₂"));
      break label670;
      label1063:
      getPreferenceScreen().removePreference(findPreference("oneplus_product_info"));
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    Object localObject;
    if (paramPreference.getKey().equals("op_electronic_card"))
    {
      localObject = getActivity();
      long l = OPPrefUtil.getLong("key_warranty_time", -1L);
      if ((OPUtils.checkNetworkAviliable((Context)localObject)) || (l != -1L)) {
        startActivity(new Intent("android.intent.action.ONEPLUS_ELECTRONIC_CARD_ACTION"));
      }
    }
    do
    {
      for (;;)
      {
        return super.onPreferenceTreeClick(paramPreference);
        Toast.makeText((Context)localObject, ((Activity)localObject).getString(2131690469), 0).show();
        continue;
        if (paramPreference.getKey().equals("firmware_version"))
        {
          System.arraycopy(this.mHits, 1, this.mHits, 0, this.mHits.length - 1);
          this.mHits[(this.mHits.length - 1)] = SystemClock.uptimeMillis();
          if (this.mHits[0] >= SystemClock.uptimeMillis() - 500L)
          {
            if (this.mUm.hasUserRestriction("no_fun"))
            {
              if ((this.mFunDisallowedAdmin == null) || (this.mFunDisallowedBySystem)) {}
              for (;;)
              {
                Log.d("DeviceInfoSettings", "Sorry, no fun for you!");
                return false;
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), this.mFunDisallowedAdmin);
              }
            }
            localObject = new Intent("android.intent.action.MAIN");
            ((Intent)localObject).setClassName("android", PlatLogoActivity.class.getName());
            try
            {
              startActivity((Intent)localObject);
            }
            catch (Exception localException)
            {
              Log.e("DeviceInfoSettings", "Unable to start activity " + ((Intent)localObject).toString());
            }
          }
        }
        else
        {
          if (paramPreference.getKey().equals("oneplus_authentication_information"))
          {
            paramPreference = new Intent();
            paramPreference.setClass(getActivity(), OPAuthenticationInformationSettings.class);
            startActivity(paramPreference);
            return true;
          }
          if (paramPreference.getKey().equals("build_number"))
          {
            if (!this.mUm.isAdminUser()) {
              return true;
            }
            if (!Utils.isDeviceProvisioned(getActivity())) {
              return true;
            }
            if (this.mUm.hasUserRestriction("no_debugging_features"))
            {
              if ((this.mDebuggingFeaturesDisallowedAdmin == null) || (this.mDebuggingFeaturesDisallowedBySystem)) {}
              for (;;)
              {
                return true;
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), this.mDebuggingFeaturesDisallowedAdmin);
              }
            }
            if (this.mDevHitCountdown > 0)
            {
              this.mDevHitCountdown -= 1;
              if (this.mDevHitCountdown == 0)
              {
                getActivity().getSharedPreferences("development", 0).edit().putBoolean("show", true).apply();
                if (this.mDevHitToast != null) {
                  this.mDevHitToast.cancel();
                }
                this.mDevHitToast = Toast.makeText(getActivity(), 2131690787, 1);
                this.mDevHitToast.show();
                Index.getInstance(getActivity().getApplicationContext()).updateFromClassNameResource(DevelopmentSettings.class.getName(), true, true);
              }
              else if ((this.mDevHitCountdown > 0) && (this.mDevHitCountdown < 5))
              {
                if (this.mDevHitToast != null) {
                  this.mDevHitToast.cancel();
                }
                this.mDevHitToast = Toast.makeText(getActivity(), getResources().getQuantityString(2131951618, this.mDevHitCountdown, new Object[] { Integer.valueOf(this.mDevHitCountdown) }), 0);
                this.mDevHitToast.show();
              }
            }
            else if (this.mDevHitCountdown < 0)
            {
              if (this.mDevHitToast != null) {
                this.mDevHitToast.cancel();
              }
              this.mDevHitToast = Toast.makeText(getActivity(), 2131690788, 1);
              this.mDevHitToast.show();
            }
          }
          else if (paramPreference.getKey().equals("security_patch"))
          {
            if (getPackageManager().queryIntentActivities(paramPreference.getIntent(), 0).isEmpty())
            {
              Log.w("DeviceInfoSettings", "Stop click action on security_patch: queryIntentActivities() returns empty");
              return true;
            }
          }
          else if (paramPreference.getKey().equals("device_feedback"))
          {
            sendFeedback();
          }
          else
          {
            if (!paramPreference.getKey().equals("system_update_settings")) {
              break;
            }
            localObject = ((CarrierConfigManager)getSystemService("carrier_config")).getConfig();
            if ((localObject != null) && (((PersistableBundle)localObject).getBoolean("ci_action_on_sys_update_bool"))) {
              ciActionOnSysUpdate((PersistableBundle)localObject);
            }
          }
        }
      }
    } while (!paramPreference.getKey().equals("oneplus_pre_application"));
    paramPreference = new Intent();
    paramPreference.setAction("com.oneplus.action.PRE_INSTALLED_APP_LIST");
    startActivity(paramPreference);
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    if (getActivity().getSharedPreferences("development", 0).getBoolean("show", Build.TYPE.equals("eng"))) {}
    for (int i = -1;; i = 7)
    {
      this.mDevHitCountdown = i;
      this.mDevHitToast = null;
      this.mFunDisallowedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(getActivity(), "no_fun", UserHandle.myUserId());
      this.mFunDisallowedBySystem = RestrictedLockUtils.hasBaseUserRestriction(getActivity(), "no_fun", UserHandle.myUserId());
      this.mDebuggingFeaturesDisallowedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(getActivity(), "no_debugging_features", UserHandle.myUserId());
      this.mDebuggingFeaturesDisallowedBySystem = RestrictedLockUtils.hasBaseUserRestriction(getActivity(), "no_debugging_features", UserHandle.myUserId());
      setStringSummary("mobile_device_name", Settings.System.getString(getActivity().getContentResolver(), "oem_oneplus_devicename"));
      return;
    }
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mSummaryLoader;
    
    public SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean) {
        this.mSummaryLoader.setSummary(this, this.mContext.getString(2131693594, new Object[] { Build.VERSION.RELEASE }));
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\DeviceInfoSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */