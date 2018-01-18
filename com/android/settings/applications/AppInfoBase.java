package com.android.settings.applications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbManager.Stub;
import android.net.Uri;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.Callbacks;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;

public abstract class AppInfoBase
  extends SettingsPreferenceFragment
  implements ApplicationsState.Callbacks
{
  public static final String ARG_PACKAGE_NAME = "package";
  public static final String ARG_PACKAGE_UID = "uid";
  protected static final int DLG_BASE = 0;
  protected static final String TAG = AppInfoBase.class.getSimpleName();
  protected static final boolean localLOGV = false;
  protected ApplicationsState.AppEntry mAppEntry;
  protected RestrictedLockUtils.EnforcedAdmin mAppsControlDisallowedAdmin;
  protected boolean mAppsControlDisallowedBySystem;
  protected DevicePolicyManager mDpm;
  protected boolean mFinishing;
  protected boolean mListeningToPackageRemove;
  protected PackageInfo mPackageInfo;
  protected String mPackageName;
  protected final BroadcastReceiver mPackageRemovedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getData().getSchemeSpecificPart();
      if ((!AppInfoBase.this.mFinishing) && (AppInfoBase.this.mAppEntry.info.packageName.equals(paramAnonymousContext))) {
        AppInfoBase.this.onPackageRemoved();
      }
    }
  };
  protected PackageManager mPm;
  protected ApplicationsState.Session mSession;
  protected ApplicationsState mState;
  protected IUsbManager mUsbManager;
  protected int mUserId;
  protected UserManager mUserManager;
  
  public static void startAppInfoFragment(Class<?> paramClass, int paramInt1, String paramString, int paramInt2, Activity paramActivity, int paramInt3)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("package", paramString);
    localBundle.putInt("uid", paramInt2);
    paramActivity.startActivityForResultAsUser(Utils.onBuildStartFragmentIntent(paramActivity, paramClass.getName(), localBundle, null, paramInt1, null, false), paramInt3, new UserHandle(UserHandle.getUserId(paramInt2)));
  }
  
  public static void startAppInfoFragment(Class<?> paramClass, int paramInt1, String paramString, int paramInt2, Fragment paramFragment, int paramInt3)
  {
    startAppInfoFragment(paramClass, paramInt1, paramString, paramInt2, paramFragment.getActivity(), paramInt3);
  }
  
  protected abstract AlertDialog createDialog(int paramInt1, int paramInt2);
  
  public void onAllSizesComputed() {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mFinishing = false;
    this.mState = ApplicationsState.getInstance(getActivity().getApplication());
    this.mSession = this.mState.newSession(this);
    paramBundle = getActivity();
    this.mDpm = ((DevicePolicyManager)paramBundle.getSystemService("device_policy"));
    this.mUserManager = ((UserManager)paramBundle.getSystemService("user"));
    this.mPm = paramBundle.getPackageManager();
    this.mUsbManager = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));
    retrieveAppEntry();
    startListeningToPackageRemove();
  }
  
  public void onDestroy()
  {
    stopListeningToPackageRemove();
    this.mSession.release();
    super.onDestroy();
  }
  
  public void onLauncherInfoChanged() {}
  
  public void onLoadEntriesCompleted() {}
  
  public void onPackageIconChanged() {}
  
  public void onPackageListChanged()
  {
    refreshUi();
  }
  
  protected void onPackageRemoved()
  {
    getActivity().finishAndRemoveTask();
  }
  
  public void onPackageSizeChanged(String paramString) {}
  
  public void onPause()
  {
    this.mSession.pause();
    super.onPause();
  }
  
  public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> paramArrayList) {}
  
  public void onResume()
  {
    super.onResume();
    this.mSession.resume();
    this.mAppsControlDisallowedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(getActivity(), "no_control_apps", this.mUserId);
    this.mAppsControlDisallowedBySystem = RestrictedLockUtils.hasBaseUserRestriction(getActivity(), "no_control_apps", this.mUserId);
    if (!refreshUi()) {
      setIntentAndFinish(true, true);
    }
  }
  
  public void onRunningStateChanged(boolean paramBoolean) {}
  
  protected abstract boolean refreshUi();
  
  protected String retrieveAppEntry()
  {
    Bundle localBundle = getArguments();
    Object localObject;
    if (localBundle != null)
    {
      localObject = localBundle.getString("package");
      this.mPackageName = ((String)localObject);
      if (this.mPackageName == null)
      {
        if (localBundle != null) {
          break label129;
        }
        localObject = getActivity().getIntent();
        if ((localObject != null) && (((Intent)localObject).getData() != null)) {
          this.mPackageName = ((Intent)localObject).getData().getSchemeSpecificPart();
        }
      }
      this.mUserId = UserHandle.myUserId();
      this.mAppEntry = this.mState.getEntry(this.mPackageName, this.mUserId);
      if (this.mAppEntry == null) {
        break label184;
      }
    }
    for (;;)
    {
      try
      {
        this.mPackageInfo = this.mPm.getPackageInfo(this.mAppEntry.info.packageName, 12864);
        return this.mPackageName;
        localObject = null;
        break;
        label129:
        localObject = (Intent)localBundle.getParcelable("intent");
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e(TAG, "Exception when retrieving package:" + this.mAppEntry.info.packageName, localNameNotFoundException);
        continue;
      }
      label184:
      Log.w(TAG, "Missing AppEntry; maybe reinstalling?");
      this.mPackageInfo = null;
    }
  }
  
  protected void setIntentAndFinish(boolean paramBoolean1, boolean paramBoolean2)
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("chg", paramBoolean2);
    ((SettingsActivity)getActivity()).finishPreferencePanel(this, -1, localIntent);
    this.mFinishing = true;
  }
  
  protected void showDialogInner(int paramInt1, int paramInt2)
  {
    MyAlertDialogFragment localMyAlertDialogFragment = MyAlertDialogFragment.newInstance(paramInt1, paramInt2);
    localMyAlertDialogFragment.setTargetFragment(this, 0);
    localMyAlertDialogFragment.show(getFragmentManager(), "dialog " + paramInt1);
  }
  
  protected void startListeningToPackageRemove()
  {
    if (this.mListeningToPackageRemove) {
      return;
    }
    this.mListeningToPackageRemove = true;
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addDataScheme("package");
    getContext().registerReceiver(this.mPackageRemovedReceiver, localIntentFilter);
  }
  
  protected void stopListeningToPackageRemove()
  {
    if (!this.mListeningToPackageRemove) {
      return;
    }
    this.mListeningToPackageRemove = false;
    getContext().unregisterReceiver(this.mPackageRemovedReceiver);
  }
  
  public static class MyAlertDialogFragment
    extends DialogFragment
  {
    public static MyAlertDialogFragment newInstance(int paramInt1, int paramInt2)
    {
      MyAlertDialogFragment localMyAlertDialogFragment = new MyAlertDialogFragment();
      Bundle localBundle = new Bundle();
      localBundle.putInt("id", paramInt1);
      localBundle.putInt("moveError", paramInt2);
      localMyAlertDialogFragment.setArguments(localBundle);
      return localMyAlertDialogFragment;
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      int i = getArguments().getInt("id");
      int j = getArguments().getInt("moveError");
      paramBundle = ((AppInfoBase)getTargetFragment()).createDialog(i, j);
      if (paramBundle == null) {
        throw new IllegalArgumentException("unknown id " + i);
      }
      return paramBundle;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppInfoBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */