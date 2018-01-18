package com.android.settings.users;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.Utils;

public final class UserDialogs
{
  public static Dialog createEnablePhoneCallsAndSmsDialog(Context paramContext, DialogInterface.OnClickListener paramOnClickListener)
  {
    return new AlertDialog.Builder(paramContext).setTitle(2131692965).setMessage(2131692966).setPositiveButton(2131690994, paramOnClickListener).setNegativeButton(17039360, null).create();
  }
  
  public static Dialog createEnablePhoneCallsDialog(Context paramContext, DialogInterface.OnClickListener paramOnClickListener)
  {
    return new AlertDialog.Builder(paramContext).setTitle(2131692963).setMessage(2131692964).setPositiveButton(2131690994, paramOnClickListener).setNegativeButton(17039360, null).create();
  }
  
  public static Dialog createRemoveDialog(Context paramContext, int paramInt, DialogInterface.OnClickListener paramOnClickListener)
  {
    UserInfo localUserInfo = ((UserManager)paramContext.getSystemService("user")).getUserInfo(paramInt);
    paramOnClickListener = new AlertDialog.Builder(paramContext).setPositiveButton(2131692955, paramOnClickListener).setNegativeButton(17039360, null);
    if (localUserInfo.isManagedProfile())
    {
      paramOnClickListener.setTitle(2131692948);
      paramContext = createRemoveManagedUserDialogView(paramContext, paramInt);
      if (paramContext != null) {
        paramOnClickListener.setView(paramContext);
      }
    }
    for (;;)
    {
      return paramOnClickListener.create();
      paramOnClickListener.setMessage(2131692951);
      continue;
      if (UserHandle.myUserId() == paramInt)
      {
        paramOnClickListener.setTitle(2131692945);
        paramOnClickListener.setMessage(2131692949);
      }
      else if (localUserInfo.isRestricted())
      {
        paramOnClickListener.setTitle(2131692947);
        paramOnClickListener.setMessage(2131692952);
      }
      else
      {
        paramOnClickListener.setTitle(2131692946);
        paramOnClickListener.setMessage(2131692950);
      }
    }
  }
  
  private static View createRemoveManagedUserDialogView(Context paramContext, int paramInt)
  {
    Object localObject1 = paramContext.getPackageManager();
    Object localObject2 = Utils.getAdminApplicationInfo(paramContext, paramInt);
    if (localObject2 == null) {
      return null;
    }
    paramContext = ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(2130968682, null);
    ((ImageView)paramContext.findViewById(2131362106)).setImageDrawable(((PackageManager)localObject1).getUserBadgedIcon(((PackageManager)localObject1).getApplicationIcon((ApplicationInfo)localObject2), new UserHandle(paramInt)));
    localObject2 = ((PackageManager)localObject1).getApplicationLabel((ApplicationInfo)localObject2);
    localObject1 = ((PackageManager)localObject1).getUserBadgedLabel((CharSequence)localObject2, new UserHandle(paramInt));
    TextView localTextView = (TextView)paramContext.findViewById(2131362107);
    localTextView.setText((CharSequence)localObject2);
    if (!((CharSequence)localObject2).toString().contentEquals((CharSequence)localObject1)) {
      localTextView.setContentDescription((CharSequence)localObject1);
    }
    return paramContext;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\users\UserDialogs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */