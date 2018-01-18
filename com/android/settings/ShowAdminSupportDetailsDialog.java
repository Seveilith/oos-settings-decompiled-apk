package com.android.settings;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog.Builder;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;

public class ShowAdminSupportDetailsDialog
  extends Activity
  implements DialogInterface.OnDismissListener
{
  private static final String TAG = "AdminSupportDialog";
  private View mDialogView;
  private DevicePolicyManager mDpm;
  private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
  
  private boolean checkIfCallerHasPermission(String paramString)
  {
    boolean bool = false;
    IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
    try
    {
      int i = localIActivityManager.getLaunchedFromUid(getActivityToken());
      i = AppGlobals.getPackageManager().checkUidPermission(paramString, i);
      if (i == 0) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException paramString)
    {
      Log.e("AdminSupportDialog", "Could not talk to activity manager.", paramString);
    }
    return false;
  }
  
  private RestrictedLockUtils.EnforcedAdmin getAdminDetailsFromIntent(Intent paramIntent)
  {
    RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = new RestrictedLockUtils.EnforcedAdmin(null, UserHandle.myUserId());
    if (paramIntent == null) {
      return localEnforcedAdmin;
    }
    if (checkIfCallerHasPermission("android.permission.MANAGE_DEVICE_ADMINS"))
    {
      localEnforcedAdmin.component = ((ComponentName)paramIntent.getParcelableExtra("android.app.extra.DEVICE_ADMIN"));
      localEnforcedAdmin.userId = paramIntent.getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
    }
    return localEnforcedAdmin;
  }
  
  private void initializeDialogViews(View paramView, ComponentName paramComponentName, int paramInt)
  {
    Object localObject2 = paramComponentName;
    Object localObject1;
    if (paramComponentName != null)
    {
      if ((!RestrictedLockUtils.isAdminInCurrentUserOrProfile(this, paramComponentName)) || (!RestrictedLockUtils.isCurrentUserOrProfile(this, paramInt))) {
        break label115;
      }
      localObject1 = null;
    }
    try
    {
      localObject2 = AppGlobals.getPackageManager().getReceiverInfo(paramComponentName, 0, paramInt);
      localObject1 = localObject2;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        label115:
        Log.w("AdminSupportDialog", "Missing reciever info", localRemoteException);
      }
    }
    localObject2 = paramComponentName;
    if (localObject1 != null)
    {
      localObject1 = ((ActivityInfo)localObject1).loadIcon(getPackageManager());
      localObject1 = getPackageManager().getUserBadgedIcon((Drawable)localObject1, new UserHandle(paramInt));
      ((ImageView)paramView.findViewById(2131361944)).setImageDrawable((Drawable)localObject1);
    }
    for (localObject2 = paramComponentName;; localObject2 = null)
    {
      setAdminSupportDetails(this, paramView, new RestrictedLockUtils.EnforcedAdmin((ComponentName)localObject2, paramInt), true);
      return;
    }
  }
  
  public static void setAdminSupportDetails(final Activity paramActivity, View paramView, RestrictedLockUtils.EnforcedAdmin paramEnforcedAdmin, final boolean paramBoolean)
  {
    if (paramEnforcedAdmin == null) {
      return;
    }
    if (paramEnforcedAdmin.component != null)
    {
      DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramActivity.getSystemService("device_policy");
      if ((!RestrictedLockUtils.isAdminInCurrentUserOrProfile(paramActivity, paramEnforcedAdmin.component)) || (!RestrictedLockUtils.isCurrentUserOrProfile(paramActivity, paramEnforcedAdmin.userId))) {
        break label131;
      }
      if (paramEnforcedAdmin.userId == 55536) {
        paramEnforcedAdmin.userId = UserHandle.myUserId();
      }
      CharSequence localCharSequence = null;
      if (UserHandle.isSameApp(Process.myUid(), 1000)) {
        localCharSequence = localDevicePolicyManager.getShortSupportMessageForUser(paramEnforcedAdmin.component, paramEnforcedAdmin.userId);
      }
      if (localCharSequence != null) {
        ((TextView)paramView.findViewById(2131361942)).setText(localCharSequence);
      }
    }
    for (;;)
    {
      paramView.findViewById(2131361943).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = new Intent();
          if (this.val$enforcedAdmin.component != null)
          {
            paramAnonymousView.setClass(paramActivity, DeviceAdminAdd.class);
            paramAnonymousView.putExtra("android.app.extra.DEVICE_ADMIN", this.val$enforcedAdmin.component);
            paramAnonymousView.putExtra("android.app.extra.CALLED_FROM_SUPPORT_DIALOG", true);
            paramActivity.startActivityAsUser(paramAnonymousView, new UserHandle(this.val$enforcedAdmin.userId));
          }
          for (;;)
          {
            if (paramBoolean) {
              paramActivity.finish();
            }
            return;
            paramAnonymousView.setClass(paramActivity, Settings.DeviceAdminSettingsActivity.class);
            paramAnonymousView.addFlags(268435456);
            paramActivity.startActivity(paramAnonymousView);
          }
        }
      });
      return;
      label131:
      paramEnforcedAdmin.component = null;
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mDpm = ((DevicePolicyManager)getSystemService(DevicePolicyManager.class));
    this.mEnforcedAdmin = getAdminDetailsFromIntent(getIntent());
    paramBundle = new AlertDialog.Builder(this);
    this.mDialogView = LayoutInflater.from(paramBundle.getContext()).inflate(2130968607, null);
    initializeDialogViews(this.mDialogView, this.mEnforcedAdmin.component, this.mEnforcedAdmin.userId);
    paramBundle.setOnDismissListener(this).setPositiveButton(2131690994, null).setView(this.mDialogView).show();
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    finish();
  }
  
  public void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    paramIntent = getAdminDetailsFromIntent(paramIntent);
    if (!this.mEnforcedAdmin.equals(paramIntent))
    {
      this.mEnforcedAdmin = paramIntent;
      initializeDialogViews(this.mDialogView, this.mEnforcedAdmin.component, this.mEnforcedAdmin.userId);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ShowAdminSupportDetailsDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */