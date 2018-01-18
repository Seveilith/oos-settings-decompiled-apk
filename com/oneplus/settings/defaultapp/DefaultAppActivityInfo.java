package com.oneplus.settings.defaultapp;

import android.content.pm.ActivityInfo;
import java.util.ArrayList;
import java.util.List;

public class DefaultAppActivityInfo
{
  private final List<ActivityInfo> mActivityInfoList = new ArrayList();
  
  public void addActivityInfo(ActivityInfo paramActivityInfo)
  {
    this.mActivityInfoList.add(paramActivityInfo);
  }
  
  public List<ActivityInfo> getActivityInfo()
  {
    return this.mActivityInfoList;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\defaultapp\DefaultAppActivityInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */