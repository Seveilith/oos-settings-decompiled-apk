package com.android.settings.dashboard.conditional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.util.Log;
import com.android.settings.Settings.WirelessSettingsActivity;
import com.android.settingslib.WirelessUtils;

public class AirplaneModeCondition
  extends Condition
{
  public AirplaneModeCondition(ConditionManager paramConditionManager)
  {
    super(paramConditionManager);
  }
  
  public CharSequence[] getActions()
  {
    return new CharSequence[] { this.mManager.getContext().getString(2131693601) };
  }
  
  public Icon getIcon()
  {
    return Icon.createWithResource(this.mManager.getContext(), 2130837931);
  }
  
  public int getMetricsConstant()
  {
    return 377;
  }
  
  protected Class<?> getReceiverClass()
  {
    return Receiver.class;
  }
  
  public CharSequence getSummary()
  {
    return this.mManager.getContext().getString(2131693606);
  }
  
  public CharSequence getTitle()
  {
    return this.mManager.getContext().getString(2131693605);
  }
  
  public void onActionClick(int paramInt)
  {
    if (paramInt == 0)
    {
      ConnectivityManager.from(this.mManager.getContext()).setAirplaneMode(false);
      setActive(false);
      return;
    }
    throw new IllegalArgumentException("Unexpected index " + paramInt);
  }
  
  public void onPrimaryClick()
  {
    this.mManager.getContext().startActivity(new Intent(this.mManager.getContext(), Settings.WirelessSettingsActivity.class));
  }
  
  public void refreshState()
  {
    Log.v(AirplaneModeCondition.class.getName(), "isAirplaneModeOn = " + WirelessUtils.isAirplaneModeOn(this.mManager.getContext()));
    setActive(WirelessUtils.isAirplaneModeOn(this.mManager.getContext()));
  }
  
  public static class Receiver
    extends BroadcastReceiver
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if ("android.intent.action.AIRPLANE_MODE".equals(paramIntent.getAction())) {
        ((AirplaneModeCondition)ConditionManager.get(paramContext).getCondition(AirplaneModeCondition.class)).refreshState();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\AirplaneModeCondition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */