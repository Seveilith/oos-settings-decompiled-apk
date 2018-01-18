package com.android.settings.applications;

import android.content.Context;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.AppFilter;

public class AppStateOverlayBridge
  extends AppStateAppOpsBridge
{
  private static final int APP_OPS_OP_CODE = 24;
  public static final ApplicationsState.AppFilter FILTER_SYSTEM_ALERT_WINDOW = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      return paramAnonymousAppEntry.extraInfo != null;
    }
    
    public void init() {}
  };
  private static final String[] PM_PERMISSION = { "android.permission.SYSTEM_ALERT_WINDOW" };
  private static final String PM_SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";
  private static final String TAG = "AppStateOverlayBridge";
  
  public AppStateOverlayBridge(Context paramContext, ApplicationsState paramApplicationsState, AppStateBaseBridge.Callback paramCallback)
  {
    super(paramContext, paramApplicationsState, paramCallback, 24, PM_PERMISSION);
  }
  
  public int getNumberOfPackagesCanDrawOverlay()
  {
    return super.getNumPackagesAllowedByAppOps();
  }
  
  public int getNumberOfPackagesWithPermission()
  {
    return super.getNumPackagesDeclaredPermission();
  }
  
  public OverlayState getOverlayInfo(String paramString, int paramInt)
  {
    return new OverlayState(super.getPermissionInfo(paramString, paramInt));
  }
  
  protected void updateExtraInfo(ApplicationsState.AppEntry paramAppEntry, String paramString, int paramInt)
  {
    paramAppEntry.extraInfo = getOverlayInfo(paramString, paramInt);
  }
  
  public static class OverlayState
    extends AppStateAppOpsBridge.PermissionState
  {
    public OverlayState(AppStateAppOpsBridge.PermissionState paramPermissionState)
    {
      super(paramPermissionState.userHandle);
      this.packageInfo = paramPermissionState.packageInfo;
      this.appOpMode = paramPermissionState.appOpMode;
      this.permissionDeclared = paramPermissionState.permissionDeclared;
      this.staticPermissionGranted = paramPermissionState.staticPermissionGranted;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppStateOverlayBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */