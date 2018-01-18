package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.oneplus.settings.utils.OPUtils;
import java.util.List;

public class SettingsInitialize
  extends BroadcastReceiver
{
  private static final String PRIMARY_PROFILE_SETTING = "com.android.settings.PRIMARY_PROFILE_CONTROLLED";
  private static final String TAG = "Settings";
  
  private void managedProfileSetup(Context paramContext, PackageManager paramPackageManager, Intent paramIntent, UserInfo paramUserInfo)
  {
    int j;
    int i;
    if ((paramUserInfo != null) && (paramUserInfo.isManagedProfile()))
    {
      Log.i("Settings", "Received broadcast: " + paramIntent.getAction() + ". Setting up intent forwarding for managed profile.");
      paramPackageManager.clearCrossProfileIntentFilters(paramUserInfo.id);
      paramIntent = new Intent();
      paramIntent.addCategory("android.intent.category.DEFAULT");
      paramIntent.setPackage(paramContext.getPackageName());
      paramIntent = paramPackageManager.queryIntentActivities(paramIntent, 705);
      j = paramIntent.size();
      i = 0;
    }
    while (i < j)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)paramIntent.get(i);
      if ((localResolveInfo.filter != null) && (localResolveInfo.activityInfo != null) && (localResolveInfo.activityInfo.metaData != null) && (localResolveInfo.activityInfo.metaData.getBoolean("com.android.settings.PRIMARY_PROFILE_CONTROLLED"))) {
        paramPackageManager.addCrossProfileIntentFilter(localResolveInfo.filter, paramUserInfo.id, paramUserInfo.profileGroupId, 2);
      }
      i += 1;
      continue;
      return;
    }
    paramPackageManager.setComponentEnabledSetting(new ComponentName(paramContext, Settings.class), 2, 1);
    try
    {
      paramPackageManager.setComponentEnabledSetting(new ComponentName("com.android.documentsui", "com.android.documentsui.LauncherActivity"), 2, 1);
      if (!OPUtils.isO2()) {
        paramPackageManager.setComponentEnabledSetting(new ComponentName("com.oneplus.provision", "com.oneplus.provision.WelcomePage"), 2, 1);
      }
      if (paramUserInfo.id == 999)
      {
        if (!OPUtils.isO2()) {
          paramPackageManager.setComponentEnabledSetting(new ComponentName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity"), 2, 1);
        }
        for (;;)
        {
          paramPackageManager.setComponentEnabledSetting(new ComponentName(paramContext, FallbackHome.class), 2, 1);
          return;
          paramPackageManager.setComponentEnabledSetting(new ComponentName("com.google.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity"), 2, 1);
        }
      }
      return;
    }
    catch (Exception paramContext) {}
  }
  
  private void webviewSettingSetup(Context paramContext, PackageManager paramPackageManager, UserInfo paramUserInfo)
  {
    if (paramUserInfo == null) {
      return;
    }
    paramContext = new ComponentName(paramContext, WebViewImplementation.class);
    if (paramUserInfo.isAdmin()) {}
    for (int i = 1;; i = 2)
    {
      paramPackageManager.setComponentEnabledSetting(paramContext, i, 1);
      return;
    }
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    UserInfo localUserInfo = ((UserManager)paramContext.getSystemService("user")).getUserInfo(UserHandle.myUserId());
    PackageManager localPackageManager = paramContext.getPackageManager();
    managedProfileSetup(paramContext, localPackageManager, paramIntent, localUserInfo);
    webviewSettingSetup(paramContext, localPackageManager, localUserInfo);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SettingsInitialize.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */