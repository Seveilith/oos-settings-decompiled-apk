package com.android.settings.dashboard.conditional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.UserHandle;
import com.android.settings.TetherSettings;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;

public class HotspotCondition
  extends Condition
{
  private final WifiManager mWifiManager = (WifiManager)this.mManager.getContext().getSystemService(WifiManager.class);
  
  public HotspotCondition(ConditionManager paramConditionManager)
  {
    super(paramConditionManager);
  }
  
  private String getSsid()
  {
    WifiConfiguration localWifiConfiguration = this.mWifiManager.getWifiApConfiguration();
    if (localWifiConfiguration == null) {
      return this.mManager.getContext().getString(17040374);
    }
    return localWifiConfiguration.SSID;
  }
  
  public CharSequence[] getActions()
  {
    Context localContext = this.mManager.getContext();
    if (RestrictedLockUtils.hasBaseUserRestriction(localContext, "no_config_tethering", UserHandle.myUserId())) {
      return new CharSequence[0];
    }
    return new CharSequence[] { localContext.getString(2131693601) };
  }
  
  public Icon getIcon()
  {
    return Icon.createWithResource(this.mManager.getContext(), 2130837987);
  }
  
  public int getMetricsConstant()
  {
    return 382;
  }
  
  protected Class<?> getReceiverClass()
  {
    return Receiver.class;
  }
  
  public CharSequence getSummary()
  {
    return this.mManager.getContext().getString(2131690330, new Object[] { getSsid() });
  }
  
  public CharSequence getTitle()
  {
    return this.mManager.getContext().getString(2131690329);
  }
  
  public void onActionClick(int paramInt)
  {
    if (paramInt == 0)
    {
      Context localContext = this.mManager.getContext();
      RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(localContext, "no_config_tethering", UserHandle.myUserId());
      if (localEnforcedAdmin != null)
      {
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(localContext, localEnforcedAdmin);
        return;
      }
      ((ConnectivityManager)localContext.getSystemService("connectivity")).stopTethering(0);
      setActive(false);
      return;
    }
    throw new IllegalArgumentException("Unexpected index " + paramInt);
  }
  
  public void onPrimaryClick()
  {
    Utils.startWithFragment(this.mManager.getContext(), TetherSettings.class.getName(), null, null, 0, 2131689588, null);
  }
  
  public void refreshState()
  {
    setActive(this.mWifiManager.isWifiApEnabled());
  }
  
  public static class Receiver
    extends BroadcastReceiver
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(paramIntent.getAction())) {
        ((HotspotCondition)ConditionManager.get(paramContext).getCondition(HotspotCondition.class)).refreshState();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\HotspotCondition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */