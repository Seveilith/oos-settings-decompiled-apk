package com.android.settings.vpn2;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import com.android.internal.net.VpnConfig;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;
import java.util.List;

public class AppManagementFragment
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private static final String ARG_PACKAGE_NAME = "package";
  private static final String KEY_ALWAYS_ON_VPN = "always_on_vpn";
  private static final String KEY_FORGET_VPN = "forget_vpn";
  private static final String KEY_VERSION = "version";
  private static final String TAG = "AppManagementFragment";
  private AppOpsManager mAppOpsManager;
  private ConnectivityManager mConnectivityManager;
  private final AppDialogFragment.Listener mForgetVpnDialogFragmentListener = new AppDialogFragment.Listener()
  {
    public void onCancel() {}
    
    public void onForget()
    {
      if (AppManagementFragment.-wrap0(AppManagementFragment.this)) {
        AppManagementFragment.-wrap2(AppManagementFragment.this, false);
      }
      AppManagementFragment.this.finish();
    }
  };
  private PackageInfo mPackageInfo;
  private PackageManager mPackageManager;
  private String mPackageName;
  private int mPackageUid;
  private OPRestrictedSwitchPreference mPreferenceAlwaysOn;
  private RestrictedPreference mPreferenceForget;
  private Preference mPreferenceVersion;
  private final int mUserId = UserHandle.myUserId();
  private String mVpnLabel;
  
  private boolean checkTargetVersion()
  {
    if ((this.mPackageInfo == null) || (this.mPackageInfo.applicationInfo == null)) {
      return true;
    }
    int i = this.mPackageInfo.applicationInfo.targetSdkVersion;
    if (i >= 24) {
      return true;
    }
    if (Log.isLoggable("AppManagementFragment", 3)) {
      Log.d("AppManagementFragment", "Package " + this.mPackageName + " targets SDK version " + i + "; must" + " target at least " + 24 + " to use always-on.");
    }
    return false;
  }
  
  private String getAlwaysOnVpnPackage()
  {
    return this.mConnectivityManager.getAlwaysOnVpnPackageForUser(this.mUserId);
  }
  
  private boolean isLegacyVpnLockDownOrAnotherPackageAlwaysOn()
  {
    if ((this.mUserId == 0) && (VpnUtils.getLockdownVpn() != null)) {
      return true;
    }
    return (getAlwaysOnVpnPackage() != null) && (!isVpnAlwaysOn());
  }
  
  private boolean isVpnActivated()
  {
    List localList = this.mAppOpsManager.getOpsForPackage(this.mPackageUid, this.mPackageName, new int[] { 47 });
    return (localList != null) && (localList.size() > 0) && (localList.get(0) != null);
  }
  
  private boolean isVpnAlwaysOn()
  {
    return this.mPackageName.equals(getAlwaysOnVpnPackage());
  }
  
  private boolean loadInfo()
  {
    Bundle localBundle = getArguments();
    if (localBundle == null)
    {
      Log.e("AppManagementFragment", "empty bundle");
      return false;
    }
    this.mPackageName = localBundle.getString("package");
    if (this.mPackageName == null)
    {
      Log.e("AppManagementFragment", "empty package name");
      return false;
    }
    try
    {
      this.mPackageUid = this.mPackageManager.getPackageUid(this.mPackageName, 0);
      this.mPackageInfo = this.mPackageManager.getPackageInfo(this.mPackageName, 0);
      this.mVpnLabel = VpnConfig.getVpnLabel(getPrefContext(), this.mPackageName).toString();
      if (!isVpnActivated())
      {
        Log.e("AppManagementFragment", "package didn't register VPN profile");
        return false;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.e("AppManagementFragment", "package not found", localNameNotFoundException);
      return false;
    }
    return true;
  }
  
  private boolean onAlwaysOnVpnClick(boolean paramBoolean)
  {
    if ((paramBoolean) && (isLegacyVpnLockDownOrAnotherPackageAlwaysOn()))
    {
      ReplaceExistingVpnFragment.show(this);
      return false;
    }
    return setAlwaysOnVpnByUI(paramBoolean);
  }
  
  private boolean onForgetVpnClick()
  {
    updateRestrictedViews();
    if (!this.mPreferenceForget.isEnabled()) {
      return false;
    }
    AppDialogFragment.show(this, this.mForgetVpnDialogFragmentListener, this.mPackageInfo, this.mVpnLabel, true, true);
    return true;
  }
  
  private boolean setAlwaysOnVpn(boolean paramBoolean)
  {
    ConnectivityManager localConnectivityManager = this.mConnectivityManager;
    int i = this.mUserId;
    if (paramBoolean) {}
    for (String str = this.mPackageName;; str = null) {
      return localConnectivityManager.setAlwaysOnVpnPackageForUser(i, str, false);
    }
  }
  
  private boolean setAlwaysOnVpnByUI(boolean paramBoolean)
  {
    updateRestrictedViews();
    if (!this.mPreferenceAlwaysOn.isEnabled()) {
      return false;
    }
    if (this.mUserId == 0) {
      VpnUtils.clearLockdownVpn(getContext());
    }
    boolean bool = setAlwaysOnVpn(paramBoolean);
    if ((!paramBoolean) || ((bool) && (isVpnAlwaysOn()))) {
      return bool;
    }
    CannotConnectFragment.show(this, this.mVpnLabel);
    return bool;
  }
  
  public static void show(Context paramContext, AppPreference paramAppPreference)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("package", paramAppPreference.getPackageName());
    Utils.startWithFragmentAsUser(paramContext, AppManagementFragment.class.getName(), localBundle, -1, paramAppPreference.getLabel(), false, new UserHandle(paramAppPreference.getUserId()));
  }
  
  private void updateRestrictedViews()
  {
    if (isAdded())
    {
      this.mPreferenceAlwaysOn.checkRestrictionAndSetDisabled("no_config_vpn", this.mUserId);
      this.mPreferenceForget.checkRestrictionAndSetDisabled("no_config_vpn", this.mUserId);
      if (checkTargetVersion()) {
        this.mPreferenceAlwaysOn.setSummary(null);
      }
    }
    else
    {
      return;
    }
    this.mPreferenceAlwaysOn.setEnabled(false);
    this.mPreferenceAlwaysOn.setSummary(2131692877);
  }
  
  private void updateUI()
  {
    if (isAdded())
    {
      this.mPreferenceAlwaysOn.setChecked(isVpnAlwaysOn());
      updateRestrictedViews();
    }
  }
  
  protected int getMetricsCategory()
  {
    return 100;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230879);
    this.mPackageManager = getContext().getPackageManager();
    this.mAppOpsManager = ((AppOpsManager)getContext().getSystemService(AppOpsManager.class));
    this.mConnectivityManager = ((ConnectivityManager)getContext().getSystemService(ConnectivityManager.class));
    this.mPreferenceVersion = findPreference("version");
    this.mPreferenceAlwaysOn = ((OPRestrictedSwitchPreference)findPreference("always_on_vpn"));
    this.mPreferenceForget = ((RestrictedPreference)findPreference("forget_vpn"));
    this.mPreferenceAlwaysOn.setOnPreferenceChangeListener(this);
    this.mPreferenceForget.setOnPreferenceClickListener(this);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (paramPreference.getKey().equals("always_on_vpn")) {
      return onAlwaysOnVpnClick(((Boolean)paramObject).booleanValue());
    }
    Log.w("AppManagementFragment", "unknown key is clicked: " + paramPreference.getKey());
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    paramPreference = paramPreference.getKey();
    if (paramPreference.equals("forget_vpn")) {
      return onForgetVpnClick();
    }
    Log.w("AppManagementFragment", "unknown key is clicked: " + paramPreference);
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    if (loadInfo())
    {
      this.mPreferenceVersion.setTitle(getPrefContext().getString(2131692864, new Object[] { this.mPackageInfo.versionName }));
      updateUI();
      return;
    }
    finish();
  }
  
  public static class CannotConnectFragment
    extends DialogFragment
  {
    private static final String ARG_VPN_LABEL = "label";
    private static final String TAG = "CannotConnect";
    
    public static void show(AppManagementFragment paramAppManagementFragment, String paramString)
    {
      if (paramAppManagementFragment.getFragmentManager().findFragmentByTag("CannotConnect") == null)
      {
        Bundle localBundle = new Bundle();
        localBundle.putString("label", paramString);
        paramString = new CannotConnectFragment();
        paramString.setArguments(localBundle);
        paramString.show(paramAppManagementFragment.getFragmentManager(), "CannotConnect");
      }
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      paramBundle = getArguments().getString("label");
      return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getString(2131692868, new Object[] { paramBundle })).setMessage(getActivity().getString(2131692869)).setPositiveButton(2131690994, null).create();
    }
  }
  
  public static class ReplaceExistingVpnFragment
    extends DialogFragment
    implements DialogInterface.OnClickListener
  {
    private static final String TAG = "ReplaceExistingVpn";
    
    public static void show(AppManagementFragment paramAppManagementFragment)
    {
      if (paramAppManagementFragment.getFragmentManager().findFragmentByTag("ReplaceExistingVpn") == null)
      {
        ReplaceExistingVpnFragment localReplaceExistingVpnFragment = new ReplaceExistingVpnFragment();
        localReplaceExistingVpnFragment.setTargetFragment(paramAppManagementFragment, 0);
        localReplaceExistingVpnFragment.show(paramAppManagementFragment.getFragmentManager(), "ReplaceExistingVpn");
      }
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if ((getTargetFragment() instanceof AppManagementFragment))
      {
        paramDialogInterface = (AppManagementFragment)getTargetFragment();
        if (AppManagementFragment.-wrap1(paramDialogInterface, true)) {
          AppManagementFragment.-wrap3(paramDialogInterface);
        }
      }
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      return new AlertDialog.Builder(getActivity()).setTitle(2131692866).setMessage(getActivity().getString(2131692867)).setNegativeButton(getActivity().getString(2131692854), null).setPositiveButton(getActivity().getString(2131692858), this).create();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\vpn2\AppManagementFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */