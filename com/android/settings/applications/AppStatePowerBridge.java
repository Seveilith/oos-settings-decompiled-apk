package com.android.settings.applications;

import android.content.pm.ApplicationInfo;
import com.android.settings.fuelgauge.PowerWhitelistBackend;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.AppFilter;
import com.android.settingslib.applications.ApplicationsState.CompoundFilter;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;

public class AppStatePowerBridge
  extends AppStateBaseBridge
{
  public static final ApplicationsState.AppFilter FILTER_POWER_WHITELISTED = new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_WITHOUT_DISABLED_UNTIL_USED, new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      return paramAnonymousAppEntry.extraInfo == Boolean.TRUE;
    }
    
    public void init() {}
  });
  private final PowerWhitelistBackend mBackend = PowerWhitelistBackend.getInstance();
  
  public AppStatePowerBridge(ApplicationsState paramApplicationsState, AppStateBaseBridge.Callback paramCallback)
  {
    super(paramApplicationsState, paramCallback);
  }
  
  protected void loadAllExtraInfo()
  {
    ArrayList localArrayList = this.mAppSession.getAllApps();
    int j = localArrayList.size();
    int i = 0;
    if (i < j)
    {
      ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)localArrayList.get(i);
      if (this.mBackend.isWhitelisted(localAppEntry.info.packageName)) {}
      for (Boolean localBoolean = Boolean.TRUE;; localBoolean = Boolean.FALSE)
      {
        localAppEntry.extraInfo = localBoolean;
        i += 1;
        break;
      }
    }
  }
  
  protected void updateExtraInfo(ApplicationsState.AppEntry paramAppEntry, String paramString, int paramInt)
  {
    if (this.mBackend.isWhitelisted(paramString)) {}
    for (paramString = Boolean.TRUE;; paramString = Boolean.FALSE)
    {
      paramAppEntry.extraInfo = paramString;
      return;
    }
  }
  
  public static class HighPowerState
  {
    public boolean isHighPower;
    public boolean isSystemHighPower;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppStatePowerBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */