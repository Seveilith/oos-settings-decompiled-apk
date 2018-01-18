package com.android.settings.vpn2;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.IConnectivityManager;
import android.net.IConnectivityManager.Stub;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.internal.net.VpnConfig;

public class AppDialogFragment
  extends DialogFragment
  implements AppDialog.Listener
{
  private static final String ARG_CONNECTED = "connected";
  private static final String ARG_LABEL = "label";
  private static final String ARG_MANAGING = "managing";
  private static final String ARG_PACKAGE = "package";
  private static final String TAG = "AppDialogFragment";
  private static final String TAG_APP_DIALOG = "vpnappdialog";
  private Listener mListener;
  private PackageInfo mPackageInfo;
  private final IConnectivityManager mService = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
  private UserManager mUserManager;
  
  private static String getConnectedPackage(IConnectivityManager paramIConnectivityManager, int paramInt)
    throws RemoteException
  {
    Object localObject = null;
    VpnConfig localVpnConfig = paramIConnectivityManager.getVpnConfig(paramInt);
    paramIConnectivityManager = (IConnectivityManager)localObject;
    if (localVpnConfig != null) {
      paramIConnectivityManager = localVpnConfig.user;
    }
    return paramIConnectivityManager;
  }
  
  private int getUserId()
  {
    return UserHandle.getUserId(this.mPackageInfo.applicationInfo.uid);
  }
  
  private boolean isUiRestricted()
  {
    UserHandle localUserHandle = UserHandle.of(getUserId());
    return this.mUserManager.hasUserRestriction("no_config_vpn", localUserHandle);
  }
  
  private void onDisconnect(DialogInterface paramDialogInterface)
  {
    if (isUiRestricted()) {
      return;
    }
    int i = getUserId();
    try
    {
      if (this.mPackageInfo.packageName.equals(getConnectedPackage(this.mService, i)))
      {
        this.mService.setAlwaysOnVpnPackage(i, null, false);
        this.mService.prepareVpn(this.mPackageInfo.packageName, "[Legacy VPN]", i);
      }
      return;
    }
    catch (RemoteException paramDialogInterface)
    {
      Log.e("AppDialogFragment", "Failed to disconnect package " + this.mPackageInfo.packageName + " for user " + i, paramDialogInterface);
    }
  }
  
  public static void show(Fragment paramFragment, PackageInfo paramPackageInfo, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    show(paramFragment, null, paramPackageInfo, paramString, paramBoolean1, paramBoolean2);
  }
  
  public static void show(Fragment paramFragment, Listener paramListener, PackageInfo paramPackageInfo, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramFragment.isAdded()) {
      return;
    }
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("package", paramPackageInfo);
    localBundle.putString("label", paramString);
    localBundle.putBoolean("managing", paramBoolean1);
    localBundle.putBoolean("connected", paramBoolean2);
    paramPackageInfo = new AppDialogFragment();
    paramPackageInfo.mListener = paramListener;
    paramPackageInfo.setArguments(localBundle);
    paramPackageInfo.setTargetFragment(paramFragment, 0);
    paramPackageInfo.show(paramFragment.getFragmentManager(), "vpnappdialog");
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    dismiss();
    if (this.mListener != null) {
      this.mListener.onCancel();
    }
    super.onCancel(paramDialogInterface);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUserManager = UserManager.get(getContext());
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    paramBundle = getArguments();
    String str = paramBundle.getString("label");
    boolean bool1 = paramBundle.getBoolean("managing");
    boolean bool2 = paramBundle.getBoolean("connected");
    this.mPackageInfo = ((PackageInfo)paramBundle.getParcelable("package"));
    if (bool1) {
      return new AppDialog(getActivity(), this, this.mPackageInfo, str);
    }
    paramBundle = new AlertDialog.Builder(getActivity()).setTitle(str).setMessage(getActivity().getString(2131692862)).setNegativeButton(getActivity().getString(2131692854), null);
    if ((!bool2) || (isUiRestricted())) {}
    for (;;)
    {
      return paramBundle.create();
      paramBundle.setPositiveButton(getActivity().getString(2131692863), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          AppDialogFragment.-wrap0(AppDialogFragment.this, paramAnonymousDialogInterface);
        }
      });
    }
  }
  
  public void onForget(DialogInterface paramDialogInterface)
  {
    if (isUiRestricted()) {
      return;
    }
    int i = getUserId();
    try
    {
      this.mService.setVpnPackageAuthorization(this.mPackageInfo.packageName, i, false);
      onDisconnect(paramDialogInterface);
      if (this.mListener != null) {
        this.mListener.onForget();
      }
      return;
    }
    catch (RemoteException paramDialogInterface)
    {
      for (;;)
      {
        Log.e("AppDialogFragment", "Failed to forget authorization of " + this.mPackageInfo.packageName + " for user " + i, paramDialogInterface);
      }
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onCancel();
    
    public abstract void onForget();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\vpn2\AppDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */