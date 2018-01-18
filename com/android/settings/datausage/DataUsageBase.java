package com.android.settings.datausage;

import android.net.INetworkStatsService.Stub;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.os.Bundle;
import android.os.INetworkManagementService;
import android.os.INetworkManagementService.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.NetworkPolicyEditor;

public abstract class DataUsageBase
  extends SettingsPreferenceFragment
{
  private static final String TAG = "DataUsageBase";
  protected final TemplatePreference.NetworkServices services = new TemplatePreference.NetworkServices();
  
  private boolean isDataEnabled(int paramInt)
  {
    if (paramInt == -1) {
      return true;
    }
    return this.services.mTelephonyManager.getDataEnabled(paramInt);
  }
  
  protected boolean isAdmin()
  {
    return this.services.mUserManager.isAdminUser();
  }
  
  protected boolean isBandwidthControlEnabled()
  {
    try
    {
      boolean bool = this.services.mNetworkService.isBandwidthControlEnabled();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("DataUsageBase", "problem talking with INetworkManagementService: " + localRemoteException);
    }
    return false;
  }
  
  protected boolean isMobileDataAvailable(int paramInt)
  {
    return this.services.mSubscriptionManager.getActiveSubscriptionInfo(paramInt) != null;
  }
  
  protected boolean isNetworkPolicyModifiable(NetworkPolicy paramNetworkPolicy, int paramInt)
  {
    if ((paramNetworkPolicy != null) && (isBandwidthControlEnabled()) && (this.services.mUserManager.isAdminUser())) {
      return isDataEnabled(paramInt);
    }
    return false;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getActivity();
    this.services.mNetworkService = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
    this.services.mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
    this.services.mPolicyManager = NetworkPolicyManager.from(paramBundle);
    this.services.mPolicyEditor = new NetworkPolicyEditor(this.services.mPolicyManager);
    this.services.mTelephonyManager = TelephonyManager.from(paramBundle);
    this.services.mSubscriptionManager = SubscriptionManager.from(paramBundle);
    this.services.mUserManager = UserManager.get(paramBundle);
  }
  
  public void onResume()
  {
    super.onResume();
    this.services.mPolicyEditor.read();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\DataUsageBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */