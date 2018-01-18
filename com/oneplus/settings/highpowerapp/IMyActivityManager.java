package com.oneplus.settings.highpowerapp;

import java.util.List;

public abstract interface IMyActivityManager
{
  public abstract boolean getBgMonitorMode();
  
  public abstract List<HighPowerApp> getBgPowerHungryList();
  
  public abstract void setBgMonitorMode(boolean paramBoolean);
  
  public abstract void stopBgPowerHungryApp(String paramString, int paramInt);
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\highpowerapp\IMyActivityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */