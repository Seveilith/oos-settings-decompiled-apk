package com.android.settings.dashboard.conditional;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.PowerManager;
import com.android.settings.Utils;
import com.android.settings.fuelgauge.BatterySaverSettings;

public class BatterySaverCondition
  extends Condition
{
  public BatterySaverCondition(ConditionManager paramConditionManager)
  {
    super(paramConditionManager);
  }
  
  public CharSequence[] getActions()
  {
    return new CharSequence[] { this.mManager.getContext().getString(2131693601) };
  }
  
  public Icon getIcon()
  {
    return Icon.createWithResource(this.mManager.getContext(), 2130838035);
  }
  
  public int getMetricsConstant()
  {
    return 379;
  }
  
  public CharSequence getSummary()
  {
    return this.mManager.getContext().getString(2131693609);
  }
  
  public CharSequence getTitle()
  {
    return this.mManager.getContext().getString(2131693608);
  }
  
  public void onActionClick(int paramInt)
  {
    if (paramInt == 0)
    {
      ((PowerManager)this.mManager.getContext().getSystemService(PowerManager.class)).setPowerSaveMode(false);
      refreshState();
      return;
    }
    throw new IllegalArgumentException("Unexpected index " + paramInt);
  }
  
  public void onPrimaryClick()
  {
    Utils.startWithFragment(this.mManager.getContext(), BatterySaverSettings.class.getName(), null, null, 0, 2131692518, null);
  }
  
  public void refreshState()
  {
    setActive(((PowerManager)this.mManager.getContext().getSystemService(PowerManager.class)).isPowerSaveMode());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\BatterySaverCondition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */