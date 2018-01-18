package com.oneplus.settings.packageuninstaller;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageDeleteObserver2.Stub;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import java.io.PrintStream;
import java.util.Iterator;

public class UninstallerActivity
  extends Activity
{
  private static final String TAG = "UninstallerActivity";
  private static AppOpsManager mAppOpsManager;
  private DialogInfo mDialogInfo;
  private String mPackageName;
  
  private void showAppNotFound()
  {
    showDialogFragment(new AppNotFoundDialogFragment());
  }
  
  private void showConfirmationDialog()
  {
    showDialogFragment(new UninstallAlertDialogFragment());
  }
  
  private void showDialogFragment(DialogFragment paramDialogFragment)
  {
    FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
    Fragment localFragment = getFragmentManager().findFragmentByTag("dialog");
    if (localFragment != null) {
      localFragmentTransaction.remove(localFragment);
    }
    paramDialogFragment.show(localFragmentTransaction, "dialog");
  }
  
  void dispatchAborted()
  {
    IPackageDeleteObserver2 localIPackageDeleteObserver2;
    if ((this.mDialogInfo != null) && (this.mDialogInfo.callback != null)) {
      localIPackageDeleteObserver2 = IPackageDeleteObserver2.Stub.asInterface(this.mDialogInfo.callback);
    }
    try
    {
      localIPackageDeleteObserver2.onPackageDeleted(this.mPackageName, -5, "Cancelled by user");
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    mAppOpsManager = (AppOpsManager)getSystemService("appops");
    Intent localIntent = getIntent();
    Object localObject = localIntent.getData();
    if (localObject == null)
    {
      Log.e("UninstallerActivity", "No package URI in intent");
      showAppNotFound();
      return;
    }
    this.mPackageName = ((Uri)localObject).getEncodedSchemeSpecificPart();
    if (this.mPackageName == null)
    {
      Log.e("UninstallerActivity", "Invalid package name in URI: " + localObject);
      showAppNotFound();
      return;
    }
    paramBundle = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
    this.mDialogInfo = new DialogInfo();
    this.mDialogInfo.user = ((UserHandle)localIntent.getParcelableExtra("android.intent.extra.USER"));
    System.out.println("zhuyang--mDialogInfo.user:" + this.mDialogInfo.user);
    if (this.mDialogInfo.user == null) {
      this.mDialogInfo.user = Process.myUserHandle();
    }
    this.mDialogInfo.allUsers = localIntent.getBooleanExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
    this.mDialogInfo.callback = localIntent.getIBinderExtra("android.content.pm.extra.CALLBACK");
    try
    {
      this.mDialogInfo.appInfo = paramBundle.getApplicationInfo(this.mPackageName, 8192, this.mDialogInfo.user.getIdentifier());
      if (this.mDialogInfo.appInfo == null)
      {
        Log.e("UninstallerActivity", "Invalid packageName: " + this.mPackageName);
        showAppNotFound();
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("UninstallerActivity", "Unable to get packageName. Package manager is dead?");
      }
      localObject = ((Uri)localObject).getFragment();
      if (localObject == null) {}
    }
    try
    {
      this.mDialogInfo.activityInfo = paramBundle.getActivityInfo(new ComponentName(this.mPackageName, (String)localObject), 0, this.mDialogInfo.user.getIdentifier());
      showConfirmationDialog();
      return;
    }
    catch (RemoteException paramBundle)
    {
      for (;;)
      {
        Log.e("UninstallerActivity", "Unable to get className. Package manager is dead?");
      }
    }
  }
  
  void startUninstallProgress()
  {
    Intent localIntent = new Intent("android.intent.action.VIEW");
    localIntent.putExtra("android.intent.extra.USER", this.mDialogInfo.user);
    localIntent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", this.mDialogInfo.allUsers);
    localIntent.putExtra("android.content.pm.extra.CALLBACK", this.mDialogInfo.callback);
    localIntent.putExtra("com.android.packageinstaller.applicationInfo", this.mDialogInfo.appInfo);
    if (getIntent().getBooleanExtra("android.intent.extra.RETURN_RESULT", false))
    {
      localIntent.putExtra("android.intent.extra.RETURN_RESULT", true);
      localIntent.addFlags(33554432);
    }
    localIntent.setClass(this, UninstallAppProgress.class);
    startActivity(localIntent);
  }
  
  public static class AppNotFoundDialogFragment
    extends DialogFragment
  {
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      return new AlertDialog.Builder(getActivity()).setTitle(2131692135).setMessage(2131692136).setNeutralButton(17039370, null).create();
    }
    
    public void onDismiss(DialogInterface paramDialogInterface)
    {
      super.onDismiss(paramDialogInterface);
      if (isAdded())
      {
        ((UninstallerActivity)getActivity()).dispatchAborted();
        getActivity().setResult(1);
        getActivity().finish();
      }
    }
  }
  
  static class DialogInfo
  {
    ActivityInfo activityInfo;
    boolean allUsers;
    ApplicationInfo appInfo;
    IBinder callback;
    UserHandle user;
  }
  
  public static class UninstallAlertDialogFragment
    extends DialogFragment
    implements DialogInterface.OnClickListener
  {
    private Drawable getBadgedIcon(PackageManager paramPackageManager, ApplicationInfo paramApplicationInfo)
    {
      return paramPackageManager.getUserBadgedIcon(paramPackageManager.loadUnbadgedItemIcon(paramApplicationInfo, paramApplicationInfo), new UserHandle(UserHandle.getUserId(paramApplicationInfo.uid)));
    }
    
    private boolean isMultiAppEnabled(String paramString)
    {
      System.out.println("zhuyang--isMultiAppEnabled:" + paramString);
      boolean bool2 = false;
      boolean bool1 = false;
      Object localObject = UninstallerActivity.-get0().getPackagesForOps(new int[] { 69 });
      if (localObject != null)
      {
        localObject = ((Iterable)localObject).iterator();
        for (;;)
        {
          bool2 = bool1;
          if (!((Iterator)localObject).hasNext()) {
            return bool2;
          }
          AppOpsManager.PackageOps localPackageOps = (AppOpsManager.PackageOps)((Iterator)localObject).next();
          Iterator localIterator = localPackageOps.getOps().iterator();
          if (localIterator.hasNext())
          {
            AppOpsManager.OpEntry localOpEntry = (AppOpsManager.OpEntry)localIterator.next();
            if ((localOpEntry.getOp() != 69) || (localOpEntry.getMode() != 0) || (!localPackageOps.getPackageName().equals(paramString))) {
              break;
            }
            bool1 = true;
          }
        }
      }
      return bool2;
    }
    
    private boolean isSingleUser(UserManager paramUserManager)
    {
      int i = paramUserManager.getUserCount();
      return (i == 1) || ((UserManager.isSplitSystemUser()) && (i == 2));
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -1)
      {
        ((UninstallerActivity)getActivity()).startUninstallProgress();
        return;
      }
      ((UninstallerActivity)getActivity()).dispatchAborted();
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      PackageManager localPackageManager = getActivity().getPackageManager();
      UninstallerActivity.DialogInfo localDialogInfo = UninstallerActivity.-get1((UninstallerActivity)getActivity());
      paramBundle = localDialogInfo.appInfo.loadLabel(localPackageManager);
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
      StringBuilder localStringBuilder = new StringBuilder();
      Object localObject;
      if (localDialogInfo.activityInfo != null)
      {
        localObject = localDialogInfo.activityInfo.loadLabel(localPackageManager);
        if (!localObject.equals(paramBundle))
        {
          localStringBuilder.append(getString(2131690493, new Object[] { localObject }));
          localStringBuilder.append(" ").append(paramBundle).append(".\n\n");
        }
      }
      int i;
      if ((localDialogInfo.appInfo.flags & 0x80) != 0)
      {
        i = 1;
        localObject = UserManager.get(getActivity());
        if (i == 0) {
          break label247;
        }
        if (!isSingleUser((UserManager)localObject)) {
          break label232;
        }
        localStringBuilder.append(getString(2131690497));
      }
      for (;;)
      {
        localBuilder.setTitle(paramBundle);
        localBuilder.setIcon(getBadgedIcon(localPackageManager, localDialogInfo.appInfo));
        localBuilder.setPositiveButton(17039370, this);
        localBuilder.setNegativeButton(17039360, this);
        localBuilder.setMessage(localStringBuilder.toString());
        return localBuilder.create();
        i = 0;
        break;
        label232:
        localStringBuilder.append(getString(2131690498));
        continue;
        label247:
        if ((!localDialogInfo.allUsers) || (isSingleUser((UserManager)localObject)))
        {
          if (localDialogInfo.user.equals(Process.myUserHandle())) {
            break label397;
          }
          localObject = ((UserManager)localObject).getUserInfo(localDialogInfo.user.getIdentifier());
          UserHandle localUserHandle = localDialogInfo.user;
          if (UserHandle.getUserId(localDialogInfo.appInfo.uid) == 999)
          {
            localStringBuilder.append(getString(2131690479, new Object[] { paramBundle }));
            paramBundle = getString(2131690478, new Object[] { paramBundle });
          }
        }
        else
        {
          localStringBuilder.append(getString(2131690495));
          continue;
        }
        localStringBuilder.append(getString(2131690496, new Object[] { ((UserInfo)localObject).name }));
        continue;
        label397:
        if (isMultiAppEnabled(localDialogInfo.appInfo.packageName)) {
          localStringBuilder.append(getString(2131690480));
        } else {
          localStringBuilder.append(getString(2131690494));
        }
      }
    }
    
    public void onDismiss(DialogInterface paramDialogInterface)
    {
      super.onDismiss(paramDialogInterface);
      if (isAdded()) {
        getActivity().finish();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\packageuninstaller\UninstallerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */