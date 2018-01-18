package com.oneplus.settings.displaysizeadaption;

import android.app.AppOpsManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.applications.AppStateBaseBridge.Callback;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.AppFilter;
import com.android.settingslib.applications.ApplicationsState.Session;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.better.OPAppModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisplaySizeAdaptionBridge
  extends AppStateBaseBridge
{
  public static final ApplicationsState.AppFilter FILTER_APP_All = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      boolean bool2 = false;
      if (paramAnonymousAppEntry == null) {
        return false;
      }
      boolean bool1 = bool2;
      if (UserHandle.getUserId(paramAnonymousAppEntry.info.uid) != 999)
      {
        bool1 = bool2;
        if ((paramAnonymousAppEntry.info.flags & 0x1) == 0) {
          bool1 = DisplaySizeAdaptionBridge.-wrap0(paramAnonymousAppEntry.info.packageName);
        }
      }
      return bool1;
    }
    
    public void init()
    {
      DisplaySizeAdaptionBridge.-set0(DisplaySizeAdaptionBridge.-wrap1());
    }
  };
  public static final ApplicationsState.AppFilter FILTER_APP_FULL_SCREEN = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      if ((paramAnonymousAppEntry == null) || (paramAnonymousAppEntry.extraInfo == null)) {
        return false;
      }
      OPAppModel localOPAppModel = (OPAppModel)paramAnonymousAppEntry.extraInfo;
      return (UserHandle.getUserId(paramAnonymousAppEntry.info.uid) != 999) && ((paramAnonymousAppEntry.info.flags & 0x1) == 0) && (!DisplaySizeAdaptionBridge.-get0().isOriginalSizeApp(localOPAppModel.getPkgName()));
    }
    
    public void init() {}
  };
  public static final ApplicationsState.AppFilter FILTER_APP_ORIGINAL_SIZE = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      boolean bool2 = false;
      if ((paramAnonymousAppEntry == null) || (paramAnonymousAppEntry.extraInfo == null)) {
        return false;
      }
      OPAppModel localOPAppModel = (OPAppModel)paramAnonymousAppEntry.extraInfo;
      boolean bool1 = bool2;
      if (UserHandle.getUserId(paramAnonymousAppEntry.info.uid) != 999)
      {
        bool1 = bool2;
        if ((paramAnonymousAppEntry.info.flags & 0x1) == 0) {
          bool1 = DisplaySizeAdaptionBridge.-get0().isOriginalSizeApp(localOPAppModel.getPkgName());
        }
      }
      return bool1;
    }
    
    public void init() {}
  };
  private static final DisplaySizeAdaptiongeManager mManager = DisplaySizeAdaptiongeManager.getInstance(SettingsBaseApplication.mApplication);
  private static List<ResolveInfo> resolveInfoList;
  private AppOpsManager mAppOpsManager;
  private final Context mContext;
  private CharSequence mLabel;
  private final PackageManager mPm;
  
  public DisplaySizeAdaptionBridge(Context paramContext, ApplicationsState paramApplicationsState, AppStateBaseBridge.Callback paramCallback)
  {
    super(paramApplicationsState, paramCallback);
    this.mContext = paramContext;
    this.mPm = this.mContext.getPackageManager();
    this.mAppOpsManager = ((AppOpsManager)this.mContext.getSystemService("appops"));
  }
  
  private static List<ResolveInfo> getLauncherApp()
  {
    Intent localIntent = new Intent("android.intent.action.MAIN", null);
    localIntent.addCategory("android.intent.category.LAUNCHER");
    return SettingsBaseApplication.mApplication.getPackageManager().queryIntentActivities(localIntent, 0);
  }
  
  private static boolean isLauncherApp(String paramString)
  {
    boolean bool2 = false;
    int i = 0;
    for (;;)
    {
      boolean bool1 = bool2;
      if (i < resolveInfoList.size())
      {
        if (paramString.equals(((ResolveInfo)resolveInfoList.get(i)).activityInfo.packageName)) {
          bool1 = true;
        }
      }
      else {
        return bool1;
      }
      i += 1;
    }
  }
  
  protected void loadAllExtraInfo()
  {
    ArrayList localArrayList = this.mAppSession.getAllApps();
    int j = localArrayList.size();
    Map localMap = mManager.loadAppMap();
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
    try
    {
      paramAppEntry.extraInfo = new OPAppModel(paramString, this.mPm.getApplicationInfo(paramString, 0).loadLabel(this.mPm).toString(), "", this.mPm.getApplicationInfo(paramString, 0).uid, false);
      return;
    }
    catch (Exception paramAppEntry)
    {
      paramAppEntry.printStackTrace();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\displaysizeadaption\DisplaySizeAdaptionBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */