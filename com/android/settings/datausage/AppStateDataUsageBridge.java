package com.android.settings.datausage;

import android.content.pm.ApplicationInfo;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.applications.AppStateBaseBridge.Callback;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;

public class AppStateDataUsageBridge
  extends AppStateBaseBridge
{
  private static final String TAG = "AppStateDataUsageBridge";
  private final DataSaverBackend mDataSaverBackend;
  
  public AppStateDataUsageBridge(ApplicationsState paramApplicationsState, AppStateBaseBridge.Callback paramCallback, DataSaverBackend paramDataSaverBackend)
  {
    super(paramApplicationsState, paramCallback);
    this.mDataSaverBackend = paramDataSaverBackend;
  }
  
  protected void loadAllExtraInfo()
  {
    ArrayList localArrayList = this.mAppSession.getAllApps();
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)localArrayList.get(i);
      localAppEntry.extraInfo = new DataUsageState(this.mDataSaverBackend.isWhitelisted(localAppEntry.info.uid), this.mDataSaverBackend.isBlacklisted(localAppEntry.info.uid));
      i += 1;
    }
  }
  
  protected void updateExtraInfo(ApplicationsState.AppEntry paramAppEntry, String paramString, int paramInt)
  {
    paramAppEntry.extraInfo = new DataUsageState(this.mDataSaverBackend.isWhitelisted(paramInt), this.mDataSaverBackend.isBlacklisted(paramInt));
  }
  
  public static class DataUsageState
  {
    public boolean isDataSaverBlacklisted;
    public boolean isDataSaverWhitelisted;
    
    public DataUsageState(boolean paramBoolean1, boolean paramBoolean2)
    {
      this.isDataSaverWhitelisted = paramBoolean1;
      this.isDataSaverBlacklisted = paramBoolean2;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\AppStateDataUsageBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */