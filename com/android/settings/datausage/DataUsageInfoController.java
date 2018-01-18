package com.android.settings.datausage;

import android.net.NetworkPolicy;
import com.android.settingslib.net.DataUsageController.DataUsageInfo;

public class DataUsageInfoController
{
  public long getSummaryLimit(DataUsageController.DataUsageInfo paramDataUsageInfo)
  {
    long l2 = paramDataUsageInfo.limitLevel;
    long l1 = l2;
    if (l2 <= 0L) {
      l1 = paramDataUsageInfo.warningLevel;
    }
    l2 = l1;
    if (paramDataUsageInfo.usageLevel > l1) {
      l2 = paramDataUsageInfo.usageLevel;
    }
    return l2;
  }
  
  public void updateDataLimit(DataUsageController.DataUsageInfo paramDataUsageInfo, NetworkPolicy paramNetworkPolicy)
  {
    if ((paramDataUsageInfo == null) || (paramNetworkPolicy == null)) {
      return;
    }
    if (paramNetworkPolicy.warningBytes >= 0L) {
      paramDataUsageInfo.warningLevel = paramNetworkPolicy.warningBytes;
    }
    if (paramNetworkPolicy.limitBytes >= 0L) {
      paramDataUsageInfo.limitLevel = paramNetworkPolicy.limitBytes;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\DataUsageInfoController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */