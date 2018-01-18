package com.android.settings.datausage;

import android.net.NetworkTemplate;
import com.android.settingslib.NetworkPolicyEditor;

public abstract interface DataUsageEditController
{
  public abstract NetworkPolicyEditor getNetworkPolicyEditor();
  
  public abstract NetworkTemplate getNetworkTemplate();
  
  public abstract void updateDataUsage();
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\DataUsageEditController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */