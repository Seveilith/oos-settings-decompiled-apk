package com.android.settings.applications;

import android.content.Context;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.AppFilter;

public class AppStateUsageBridge
  extends AppStateAppOpsBridge
{
  private static final int APP_OPS_OP_CODE = 43;
  public static final ApplicationsState.AppFilter FILTER_APP_USAGE = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      return paramAnonymousAppEntry.extraInfo != null;
    }
    
    public void init() {}
  };
  private static final String[] PM_PERMISSION = { "android.permission.PACKAGE_USAGE_STATS" };
  private static final String PM_USAGE_STATS = "android.permission.PACKAGE_USAGE_STATS";
  private static final String TAG = "AppStateUsageBridge";
  
  public AppStateUsageBridge(Context paramContext, ApplicationsState paramApplicationsState, AppStateBaseBridge.Callback paramCallback)
  {
    super(paramContext, paramApplicationsState, paramCallback, 43, PM_PERMISSION);
  }
  
  public UsageState getUsageInfo(String paramString, int paramInt)
  {
    return new UsageState(super.getPermissionInfo(paramString, paramInt));
  }
  
  protected void updateExtraInfo(ApplicationsState.AppEntry paramAppEntry, String paramString, int paramInt)
  {
    paramAppEntry.extraInfo = getUsageInfo(paramString, paramInt);
  }
  
  public static class UsageState
    extends AppStateAppOpsBridge.PermissionState
  {
    public UsageState(AppStateAppOpsBridge.PermissionState paramPermissionState)
    {
      super(paramPermissionState.userHandle);
      this.packageInfo = paramPermissionState.packageInfo;
      this.appOpMode = paramPermissionState.appOpMode;
      this.permissionDeclared = paramPermissionState.permissionDeclared;
      this.staticPermissionGranted = paramPermissionState.staticPermissionGranted;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppStateUsageBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */