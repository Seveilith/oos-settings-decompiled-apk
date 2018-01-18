package com.android.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkPolicyManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.ims.ImsManager;
import com.android.settingslib.RestrictedLockUtils;

public class ResetNetworkConfirm
  extends OptionsMenuFragment
{
  private static final boolean DBG = true;
  private static final int RESET_COMPLETED = 1;
  private static final int START_RESET = 0;
  private static final String TAG = "ResetNetworkConfirm";
  private View mContentView;
  private View.OnClickListener mFinalClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (Utils.isMonkeyRunning()) {
        return;
      }
      ResetNetworkConfirm.this.mHandler.sendEmptyMessage(0);
      new Thread(new Runnable()
      {
        public void run()
        {
          Object localObject1 = ResetNetworkConfirm.this.getActivity();
          Object localObject2 = (ConnectivityManager)((Context)localObject1).getSystemService("connectivity");
          if (localObject2 != null) {
            ((ConnectivityManager)localObject2).factoryReset();
          }
          localObject2 = (WifiManager)((Context)localObject1).getSystemService("wifi");
          if (localObject2 != null) {
            ((WifiManager)localObject2).factoryReset();
          }
          localObject2 = (TelephonyManager)((Context)localObject1).getSystemService("phone");
          if (localObject2 != null) {
            ((TelephonyManager)localObject2).factoryReset(ResetNetworkConfirm.-get1(ResetNetworkConfirm.this));
          }
          NetworkPolicyManager localNetworkPolicyManager = (NetworkPolicyManager)((Context)localObject1).getSystemService("netpolicy");
          if (localNetworkPolicyManager != null) {
            localNetworkPolicyManager.factoryReset(((TelephonyManager)localObject2).getSubscriberId(ResetNetworkConfirm.-get1(ResetNetworkConfirm.this)));
          }
          ImsManager.factoryReset((Context)localObject1);
          localObject1 = (BluetoothManager)((Context)localObject1).getSystemService("bluetooth");
          if (localObject1 != null)
          {
            ((BluetoothManager)localObject1).getAdapter().factoryReset();
            ResetNetworkConfirm.this.mHandler.sendEmptyMessage(1);
          }
        }
      }).start();
    }
  };
  public Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      switch (paramAnonymousMessage.what)
      {
      }
      do
      {
        do
        {
          return;
        } while ((ResetNetworkConfirm.-get0(ResetNetworkConfirm.this) == null) || (!ResetNetworkConfirm.-wrap0(ResetNetworkConfirm.this)));
        ResetNetworkConfirm.-get0(ResetNetworkConfirm.this).show();
        return;
        if ((ResetNetworkConfirm.-get0(ResetNetworkConfirm.this) != null) && (ResetNetworkConfirm.-wrap0(ResetNetworkConfirm.this))) {
          ResetNetworkConfirm.-get0(ResetNetworkConfirm.this).dismiss();
        }
        paramAnonymousMessage = ResetNetworkConfirm.this.getActivity();
      } while ((paramAnonymousMessage == null) || (paramAnonymousMessage.isDestroyed()));
      Toast.makeText(paramAnonymousMessage, 2131691891, 0).show();
    }
  };
  private ProgressDialog mProgressDialog;
  private int mSubId = -1;
  
  private void establishFinalConfirmationState()
  {
    this.mContentView.findViewById(2131362506).setOnClickListener(this.mFinalClickListener);
  }
  
  private boolean isActivityValide()
  {
    Activity localActivity = getActivity();
    return (localActivity != null) && (!localActivity.isDestroyed());
  }
  
  protected int getMetricsCategory()
  {
    return 84;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getArguments();
    if (paramBundle != null) {
      this.mSubId = paramBundle.getInt("subscription", -1);
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramViewGroup = RestrictedLockUtils.checkIfRestrictionEnforced(getActivity(), "no_network_reset", UserHandle.myUserId());
    if (RestrictedLockUtils.hasBaseUserRestriction(getActivity(), "no_network_reset", UserHandle.myUserId())) {
      return paramLayoutInflater.inflate(2130968753, null);
    }
    if (paramViewGroup != null)
    {
      paramLayoutInflater = paramLayoutInflater.inflate(2130968608, null);
      ShowAdminSupportDetailsDialog.setAdminSupportDetails(getActivity(), paramLayoutInflater, paramViewGroup, false);
      paramLayoutInflater.setVisibility(0);
      return paramLayoutInflater;
    }
    this.mProgressDialog = new ProgressDialog(getActivity());
    this.mProgressDialog.setCanceledOnTouchOutside(false);
    this.mProgressDialog.setMessage(getResources().getString(2131690216));
    this.mContentView = paramLayoutInflater.inflate(2130968950, null);
    establishFinalConfirmationState();
    return this.mContentView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ResetNetworkConfirm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */