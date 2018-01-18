package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.PackageManager;
import android.net.NetworkPolicyManager;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.webkit.IWebViewUpdateService;
import android.webkit.IWebViewUpdateService.Stub;
import java.util.List;

public class ResetAppsHelper
  implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener
{
  private static final String EXTRA_RESET_DIALOG = "resetDialog";
  private final AppOpsManager mAom;
  private final Context mContext;
  private final IPackageManager mIPm;
  private final INotificationManager mNm;
  private final NetworkPolicyManager mNpm;
  private final PackageManager mPm;
  private AlertDialog mResetDialog;
  private final IWebViewUpdateService mWvus;
  
  public ResetAppsHelper(Context paramContext)
  {
    this.mContext = paramContext;
    this.mPm = paramContext.getPackageManager();
    this.mIPm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
    this.mNm = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    this.mWvus = IWebViewUpdateService.Stub.asInterface(ServiceManager.getService("webviewupdate"));
    this.mNpm = NetworkPolicyManager.from(paramContext);
    this.mAom = ((AppOpsManager)paramContext.getSystemService("appops"));
  }
  
  private boolean isNonEnableableFallback(String paramString)
  {
    try
    {
      boolean bool = this.mWvus.isFallbackPackage(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException(paramString);
    }
  }
  
  void buildResetDialog()
  {
    if (this.mResetDialog == null) {
      this.mResetDialog = new AlertDialog.Builder(this.mContext).setTitle(2131692113).setMessage(2131692114).setPositiveButton(2131692115, this).setNegativeButton(2131690993, null).setOnDismissListener(this).show();
    }
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (this.mResetDialog != paramDialogInterface) {
      return;
    }
    AsyncTask.execute(new Runnable()
    {
      public void run()
      {
        Object localObject = ResetAppsHelper.-get5(ResetAppsHelper.this).getInstalledApplications(512);
        int i = 0;
        for (;;)
        {
          ApplicationInfo localApplicationInfo;
          if (i < ((List)localObject).size()) {
            localApplicationInfo = (ApplicationInfo)((List)localObject).get(i);
          }
          try
          {
            ResetAppsHelper.-get3(ResetAppsHelper.this).setNotificationsEnabledForPackage(localApplicationInfo.packageName, localApplicationInfo.uid, true);
            if ((localApplicationInfo.enabled) || (ResetAppsHelper.-get5(ResetAppsHelper.this).getApplicationEnabledSetting(localApplicationInfo.packageName) != 3) || (ResetAppsHelper.-wrap0(ResetAppsHelper.this, localApplicationInfo.packageName))) {}
            for (;;)
            {
              i += 1;
              break;
              ResetAppsHelper.-get5(ResetAppsHelper.this).setApplicationEnabledSetting(localApplicationInfo.packageName, 0, 1);
            }
            try
            {
              ResetAppsHelper.-get2(ResetAppsHelper.this).resetApplicationPreferences(UserHandle.myUserId());
              ResetAppsHelper.-get0(ResetAppsHelper.this).resetAllModes();
              localObject = ResetAppsHelper.-get4(ResetAppsHelper.this).getUidsWithPolicy(1);
              int j = ActivityManager.getCurrentUser();
              i = 0;
              int k = localObject.length;
              while (i < k)
              {
                int m = localObject[i];
                if (UserHandle.getUserId(m) == j) {
                  ResetAppsHelper.-get4(ResetAppsHelper.this).setUidPolicy(m, 0);
                }
                i += 1;
              }
              if (Build.VERSION.IS_CTA_BUILD)
              {
                localObject = new Intent("com.oneplus.cta.permission.RESET");
                ((Intent)localObject).setClassName("com.oneplus.permissionutil", "com.oneplus.permissionutil.ResetReceiver");
                ResetAppsHelper.-get1(ResetAppsHelper.this).sendBroadcast((Intent)localObject);
              }
              return;
            }
            catch (RemoteException localRemoteException1)
            {
              for (;;) {}
            }
          }
          catch (RemoteException localRemoteException2)
          {
            for (;;) {}
          }
        }
      }
    });
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    if (this.mResetDialog == paramDialogInterface) {
      this.mResetDialog = null;
    }
  }
  
  public void onRestoreInstanceState(Bundle paramBundle)
  {
    if ((paramBundle != null) && (paramBundle.getBoolean("resetDialog"))) {
      buildResetDialog();
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    if (this.mResetDialog != null) {
      paramBundle.putBoolean("resetDialog", true);
    }
  }
  
  public void stop()
  {
    if (this.mResetDialog != null)
    {
      this.mResetDialog.dismiss();
      this.mResetDialog = null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ResetAppsHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */