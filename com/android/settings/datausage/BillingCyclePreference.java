package com.android.settings.datausage;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkPolicy;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import com.android.settings.Utils;
import com.android.settingslib.NetworkPolicyEditor;

public class BillingCyclePreference
  extends Preference
  implements TemplatePreference
{
  private final CellDataPreference.DataStateListener mListener = new CellDataPreference.DataStateListener()
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      BillingCyclePreference.-wrap0(BillingCyclePreference.this);
    }
  };
  private NetworkPolicy mPolicy;
  private TemplatePreference.NetworkServices mServices;
  private int mSubId;
  private NetworkTemplate mTemplate;
  
  public BillingCyclePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void updateEnabled()
  {
    try
    {
      if ((this.mPolicy != null) && (this.mServices.mNetworkService.isBandwidthControlEnabled()) && (this.mServices.mTelephonyManager.getDataEnabled(this.mSubId))) {}
      for (boolean bool = this.mServices.mUserManager.isAdminUser();; bool = false)
      {
        setEnabled(bool);
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      setEnabled(false);
    }
  }
  
  public Intent getIntent()
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("network_template", this.mTemplate);
    return Utils.onBuildStartFragmentIntent(getContext(), BillingCycleSettings.class.getName(), localBundle, null, 0, getTitle(), false);
  }
  
  public void onAttached()
  {
    super.onAttached();
    this.mListener.setListener(true, this.mSubId, getContext());
  }
  
  public void onDetached()
  {
    this.mListener.setListener(false, this.mSubId, getContext());
    super.onDetached();
  }
  
  public void setTemplate(NetworkTemplate paramNetworkTemplate, int paramInt, TemplatePreference.NetworkServices paramNetworkServices)
  {
    int i = 1;
    this.mTemplate = paramNetworkTemplate;
    this.mSubId = paramInt;
    this.mServices = paramNetworkServices;
    this.mPolicy = paramNetworkServices.mPolicyEditor.getPolicy(this.mTemplate);
    paramNetworkTemplate = getContext();
    paramInt = i;
    if (this.mPolicy != null) {
      paramInt = this.mPolicy.cycleDay;
    }
    setSummary(paramNetworkTemplate.getString(2131693638, new Object[] { Integer.valueOf(paramInt) }));
    setIntent(getIntent());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\BillingCyclePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */