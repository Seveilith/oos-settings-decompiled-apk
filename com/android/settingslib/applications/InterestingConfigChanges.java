package com.android.settingslib.applications;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class InterestingConfigChanges
{
  private final Configuration mLastConfiguration = new Configuration();
  private int mLastDensity;
  
  public boolean applyNewConfig(Resources paramResources)
  {
    int j = this.mLastConfiguration.updateFrom(paramResources.getConfiguration());
    if (this.mLastDensity != paramResources.getDisplayMetrics().densityDpi) {}
    for (int i = 1; (i != 0) || ((j & 0x304) != 0); i = 0)
    {
      this.mLastDensity = paramResources.getDisplayMetrics().densityDpi;
      return true;
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\applications\InterestingConfigChanges.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */