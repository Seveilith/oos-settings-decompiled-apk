package com.android.settings.datausage;

import android.net.INetworkStatsService;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.os.INetworkManagementService;
import android.os.UserManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.android.settingslib.NetworkPolicyEditor;

public abstract interface TemplatePreference
{
  public abstract void setTemplate(NetworkTemplate paramNetworkTemplate, int paramInt, NetworkServices paramNetworkServices);
  
  public static class NetworkServices
  {
    INetworkManagementService mNetworkService;
    NetworkPolicyEditor mPolicyEditor;
    NetworkPolicyManager mPolicyManager;
    INetworkStatsService mStatsService;
    SubscriptionManager mSubscriptionManager;
    TelephonyManager mTelephonyManager;
    UserManager mUserManager;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\TemplatePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */