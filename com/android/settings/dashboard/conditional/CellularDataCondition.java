package com.android.settings.dashboard.conditional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CellularDataCondition
  extends Condition
{
  public CellularDataCondition(ConditionManager paramConditionManager)
  {
    super(paramConditionManager);
  }
  
  public CharSequence[] getActions()
  {
    return new CharSequence[] { this.mManager.getContext().getString(2131693602) };
  }
  
  public Icon getIcon()
  {
    return Icon.createWithResource(this.mManager.getContext(), 2130837959);
  }
  
  public int getMetricsConstant()
  {
    return 380;
  }
  
  protected Class<?> getReceiverClass()
  {
    return Receiver.class;
  }
  
  public CharSequence getSummary()
  {
    return this.mManager.getContext().getString(2131693611);
  }
  
  public CharSequence getTitle()
  {
    return this.mManager.getContext().getString(2131693610);
  }
  
  public void onActionClick(int paramInt)
  {
    if (paramInt == 0)
    {
      ((TelephonyManager)this.mManager.getContext().getSystemService(TelephonyManager.class)).setDataEnabled(true);
      setActive(false);
      return;
    }
    throw new IllegalArgumentException("Unexpected index " + paramInt);
  }
  
  public void onPrimaryClick()
  {
    this.mManager.getContext().startActivity(new Intent("com.oneplus.security.action.USAGE_DATA_SUMMARY"));
  }
  
  public void refreshState()
  {
    boolean bool2 = false;
    ConnectivityManager localConnectivityManager = (ConnectivityManager)this.mManager.getContext().getSystemService(ConnectivityManager.class);
    TelephonyManager localTelephonyManager = (TelephonyManager)this.mManager.getContext().getSystemService(TelephonyManager.class);
    Log.d(CellularDataCondition.class.getName(), "connectivity.isNetworkSupported(ConnectivityManager.TYPE_MOBILE) = " + localConnectivityManager.isNetworkSupported(0));
    String str = CellularDataCondition.class.getName();
    StringBuilder localStringBuilder = new StringBuilder().append("telephony.getSimState() != TelephonyManager.SIM_STATE_READY = ");
    if (localTelephonyManager.getSimState() != 5) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      Log.d(str, bool1);
      if ((localConnectivityManager.isNetworkSupported(0)) && (localTelephonyManager.getSimState() == 5)) {
        break;
      }
      setActive(false);
      return;
    }
    Log.d(CellularDataCondition.class.getName(), "telephony.getDataEnabled() = " + localTelephonyManager.getDataEnabled());
    if (localTelephonyManager.getDataEnabled()) {}
    for (bool1 = bool2;; bool1 = true)
    {
      setActive(bool1);
      return;
    }
  }
  
  public static class Receiver
    extends BroadcastReceiver
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if ("android.intent.action.ANY_DATA_STATE".equals(paramIntent.getAction()))
      {
        paramContext = ConditionManager.get(paramContext).getCondition(CellularDataCondition.class);
        if (paramContext != null) {
          paramContext.refreshState();
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\CellularDataCondition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */