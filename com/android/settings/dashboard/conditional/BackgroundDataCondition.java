package com.android.settings.dashboard.conditional;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.NetworkPolicyManager;

public class BackgroundDataCondition
  extends Condition
{
  public BackgroundDataCondition(ConditionManager paramConditionManager)
  {
    super(paramConditionManager);
  }
  
  public CharSequence[] getActions()
  {
    return new CharSequence[] { this.mManager.getContext().getString(2131693601) };
  }
  
  public Icon getIcon()
  {
    return Icon.createWithResource(this.mManager.getContext(), 2130837969);
  }
  
  public int getMetricsConstant()
  {
    return 378;
  }
  
  public CharSequence getSummary()
  {
    return this.mManager.getContext().getString(2131693613);
  }
  
  public CharSequence getTitle()
  {
    return this.mManager.getContext().getString(2131693612);
  }
  
  public void onActionClick(int paramInt)
  {
    if (paramInt == 0)
    {
      NetworkPolicyManager.from(this.mManager.getContext()).setRestrictBackground(false);
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
    setActive(NetworkPolicyManager.from(this.mManager.getContext()).getRestrictBackground());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\BackgroundDataCondition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */