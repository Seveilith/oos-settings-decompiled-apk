package com.android.settings.vpn2;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.IConnectivityManager.Stub;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.security.Credentials;
import android.security.KeyStore;
import android.util.Log;
import android.widget.Toast;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnProfile;

public class ConfigDialogFragment
  extends DialogFragment
  implements DialogInterface.OnClickListener
{
  private static final String ARG_EDITING = "editing";
  private static final String ARG_EXISTS = "exists";
  private static final String ARG_PROFILE = "profile";
  private static final String TAG = "ConfigDialogFragment";
  private static final String TAG_CONFIG_DIALOG = "vpnconfigdialog";
  private final IConnectivityManager mService = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
  private boolean mUnlocking = false;
  
  private void connect(VpnProfile paramVpnProfile)
    throws RemoteException
  {
    try
    {
      this.mService.startLegacyVpn(paramVpnProfile);
      return;
    }
    catch (IllegalStateException paramVpnProfile)
    {
      Toast.makeText(getActivity(), 2131692881, 1).show();
    }
  }
  
  private void disconnect(VpnProfile paramVpnProfile)
  {
    try
    {
      LegacyVpnInfo localLegacyVpnInfo = this.mService.getLegacyVpnInfo(UserHandle.myUserId());
      if ((localLegacyVpnInfo != null) && (paramVpnProfile.key.equals(localLegacyVpnInfo.key)))
      {
        VpnUtils.clearLockdownVpn(getContext());
        this.mService.prepareVpn("[Legacy VPN]", "[Legacy VPN]", UserHandle.myUserId());
      }
      return;
    }
    catch (RemoteException paramVpnProfile)
    {
      Log.e("ConfigDialogFragment", "Failed to disconnect", paramVpnProfile);
    }
  }
  
  public static void show(VpnSettings paramVpnSettings, VpnProfile paramVpnProfile, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramVpnSettings.isAdded()) {
      return;
    }
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("profile", paramVpnProfile);
    localBundle.putBoolean("editing", paramBoolean1);
    localBundle.putBoolean("exists", paramBoolean2);
    paramVpnProfile = new ConfigDialogFragment();
    paramVpnProfile.setArguments(localBundle);
    paramVpnProfile.setTargetFragment(paramVpnSettings, 0);
    paramVpnProfile.show(paramVpnSettings.getFragmentManager(), "vpnconfigdialog");
  }
  
  private void updateLockdownVpn(boolean paramBoolean, VpnProfile paramVpnProfile)
  {
    if (paramBoolean)
    {
      if (!paramVpnProfile.isValidLockdownProfile())
      {
        Toast.makeText(getContext(), 2131692880, 1).show();
        return;
      }
      ConnectivityManager.from(getActivity()).setAlwaysOnVpnPackageForUser(UserHandle.myUserId(), null, false);
      VpnUtils.setLockdownVpn(getContext(), paramVpnProfile.key);
    }
    while (!VpnUtils.isVpnLockdown(paramVpnProfile.key)) {
      return;
    }
    VpnUtils.clearLockdownVpn(getContext());
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    dismiss();
    super.onCancel(paramDialogInterface);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    paramDialogInterface = (ConfigDialog)getDialog();
    VpnProfile localVpnProfile = paramDialogInterface.getProfile();
    if (paramInt == -1)
    {
      KeyStore.getInstance().put("VPN_" + localVpnProfile.key, localVpnProfile.encode(), -1, 0);
      disconnect(localVpnProfile);
      updateLockdownVpn(paramDialogInterface.isVpnAlwaysOn(), localVpnProfile);
      if ((!paramDialogInterface.isEditing()) && (!VpnUtils.isVpnLockdown(localVpnProfile.key))) {}
    }
    for (;;)
    {
      dismiss();
      return;
      try
      {
        connect(localVpnProfile);
      }
      catch (RemoteException paramDialogInterface)
      {
        Log.e("ConfigDialogFragment", "Failed to connect", paramDialogInterface);
      }
      continue;
      if (paramInt == -3)
      {
        disconnect(localVpnProfile);
        KeyStore.getInstance().delete("VPN_" + localVpnProfile.key, -1);
        updateLockdownVpn(false, localVpnProfile);
      }
    }
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    paramBundle = getArguments();
    VpnProfile localVpnProfile = (VpnProfile)paramBundle.getParcelable("profile");
    boolean bool1 = paramBundle.getBoolean("editing");
    boolean bool2 = paramBundle.getBoolean("exists");
    return new ConfigDialog(getActivity(), this, localVpnProfile, bool1, bool2);
  }
  
  public void onResume()
  {
    boolean bool = false;
    super.onResume();
    if (!KeyStore.getInstance().isUnlocked())
    {
      if (!this.mUnlocking)
      {
        Credentials.getInstance().unlock(getActivity());
        if (!this.mUnlocking) {
          break label52;
        }
      }
      for (;;)
      {
        this.mUnlocking = bool;
        return;
        dismiss();
        break;
        label52:
        bool = true;
      }
    }
    this.mUnlocking = false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\vpn2\ConfigDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */