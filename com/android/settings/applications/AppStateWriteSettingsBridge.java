package com.android.settings.applications;

import android.content.Context;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.AppFilter;

public class AppStateWriteSettingsBridge
  extends AppStateAppOpsBridge
{
  private static final int APP_OPS_OP_CODE = 23;
  public static final ApplicationsState.AppFilter FILTER_WRITE_SETTINGS = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      return paramAnonymousAppEntry.extraInfo != null;
    }
    
    public void init() {}
  };
  private static final String[] PM_PERMISSIONS = { "android.permission.WRITE_SETTINGS" };
  private static final String PM_WRITE_SETTINGS = "android.permission.WRITE_SETTINGS";
  private static final String TAG = "AppStateWriteSettingsBridge";
  
  public AppStateWriteSettingsBridge(Context paramContext, ApplicationsState paramApplicationsState, AppStateBaseBridge.Callback paramCallback)
  {
    super(paramContext, paramApplicationsState, paramCallback, 23, PM_PERMISSIONS);
  }
  
  public int getNumberOfPackagesCanWriteSettings()
  {
    return super.getNumPackagesAllowedByAppOps();
  }
  
  public int getNumberOfPackagesWithPermission()
  {
    return super.getNumPackagesDeclaredPermission();
  }
  
  public WriteSettingsState getWriteSettingsInfo(String paramString, int paramInt)
  {
    return new WriteSettingsState(super.getPermissionInfo(paramString, paramInt));
  }
  
  protected void updateExtraInfo(ApplicationsState.AppEntry paramAppEntry, String paramString, int paramInt)
  {
    paramAppEntry.extraInfo = getWriteSettingsInfo(paramString, paramInt);
  }
  
  public static class WriteSettingsState
    extends AppStateAppOpsBridge.PermissionState
  {
    public WriteSettingsState(AppStateAppOpsBridge.PermissionState paramPermissionState)
    {
      super(paramPermissionState.userHandle);
      this.packageInfo = paramPermissionState.packageInfo;
      this.appOpMode = paramPermissionState.appOpMode;
      this.permissionDeclared = paramPermissionState.permissionDeclared;
      this.staticPermissionGranted = paramPermissionState.staticPermissionGranted;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppStateWriteSettingsBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */