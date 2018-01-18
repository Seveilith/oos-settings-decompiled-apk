package com.android.settings.dashboard.conditional;

import android.content.Context;
import android.graphics.drawable.Icon;
import com.android.internal.app.NightDisplayController;
import com.android.internal.app.NightDisplayController.Callback;
import com.android.settings.Utils;
import com.android.settings.display.NightDisplaySettings;

public final class NightDisplayCondition
  extends Condition
  implements NightDisplayController.Callback
{
  private NightDisplayController mController;
  
  NightDisplayCondition(ConditionManager paramConditionManager)
  {
    super(paramConditionManager);
    this.mController = new NightDisplayController(paramConditionManager.getContext());
    this.mController.setListener(this);
  }
  
  public CharSequence[] getActions()
  {
    return new CharSequence[] { this.mManager.getContext().getString(2131693601) };
  }
  
  public Icon getIcon()
  {
    return Icon.createWithResource(this.mManager.getContext(), 2130838060);
  }
  
  public int getMetricsConstant()
  {
    return 492;
  }
  
  public CharSequence getSummary()
  {
    return this.mManager.getContext().getString(2131693617);
  }
  
  public CharSequence getTitle()
  {
    return this.mManager.getContext().getString(2131693616);
  }
  
  public void onActionClick(int paramInt)
  {
    if (paramInt == 0)
    {
      this.mController.setActivated(false);
      return;
    }
    throw new IllegalArgumentException("Unexpected index " + paramInt);
  }
  
  public void onActivated(boolean paramBoolean)
  {
    refreshState();
  }
  
  public void onPrimaryClick()
  {
    Utils.startWithFragment(this.mManager.getContext(), NightDisplaySettings.class.getName(), null, null, 0, 2131691605, null);
  }
  
  public void refreshState()
  {
    setActive(this.mController.isActivated());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\NightDisplayCondition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */