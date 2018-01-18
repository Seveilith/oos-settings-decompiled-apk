package com.oneplus.settings.backgroundoptimize;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.applications.AppStateBaseBridge.Callback;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.AppFilter;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;
import java.util.Map;

public class AppBgOptimizeBridge
  extends AppStateBaseBridge
{
  public static final ApplicationsState.AppFilter FILTER_APP_BG_All = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      boolean bool = false;
      if ((paramAnonymousAppEntry == null) || (UserHandle.getUserId(paramAnonymousAppEntry.info.uid) == 999)) {
        return false;
      }
      if ((paramAnonymousAppEntry.info.flags & 0x1) == 0) {
        bool = true;
      }
      return bool;
    }
    
    public void init() {}
  };
  public static final ApplicationsState.AppFilter FILTER_APP_BG_NOT_OPTIMIZE = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      if ((paramAnonymousAppEntry == null) || (paramAnonymousAppEntry.extraInfo == null)) {}
      while (UserHandle.getUserId(paramAnonymousAppEntry.info.uid) == 999) {
        return false;
      }
      return 1 == ((AppControlMode)paramAnonymousAppEntry.extraInfo).value;
    }
    
    public void init() {}
  };
  private final Context mContext;
  private final BgOActivityManager mManager;
  private final PackageManager mPm;
  
  public AppBgOptimizeBridge(Context paramContext, ApplicationsState paramApplicationsState, AppStateBaseBridge.Callback paramCallback)
  {
    super(paramApplicationsState, paramCallback);
    this.mContext = paramContext;
    this.mPm = this.mContext.getPackageManager();
    this.mManager = BgOActivityManager.getInstance(this.mContext);
  }
  
  protected void loadAllExtraInfo()
  {
    ArrayList localArrayList = this.mAppSession.getAllApps();
    int j = localArrayList.size();
    Map localMap = this.mManager.getAllAppControlModesMap(0);
    int i = 0;
    while (i < j)
    {
      ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)localArrayList.get(i);
      localAppEntry.extraInfo = localMap.get(localAppEntry.info.packageName);
      i += 1;
    }
  }
  
  protected void updateExtraInfo(ApplicationsState.AppEntry paramAppEntry, String paramString, int paramInt)
  {
    paramAppEntry.extraInfo = new AppControlMode(paramString, 0, this.mManager.getAppControlMode(paramString, 0));
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\backgroundoptimize\AppBgOptimizeBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */