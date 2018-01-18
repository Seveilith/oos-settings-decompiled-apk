package com.oneplus.settings.packageuninstaller;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PackageParser.Package;
import android.content.pm.PackageParser.PackageParserException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.List;

public class PackageUtil
{
  public static final String INTENT_ATTR_APPLICATION_INFO = "com.android.packageinstaller.applicationInfo";
  public static final String INTENT_ATTR_INSTALL_STATUS = "com.android.packageinstaller.installStatus";
  public static final String INTENT_ATTR_PACKAGE_NAME = "com.android.packageinstaller.PackageName";
  public static final String INTENT_ATTR_PERMISSIONS_LIST = "com.android.packageinstaller.PermissionsList";
  public static final String PREFIX = "com.android.packageinstaller.";
  
  public static AppSnippet getAppSnippet(Activity paramActivity, ApplicationInfo paramApplicationInfo, File paramFile)
  {
    paramFile = paramFile.getAbsolutePath();
    Object localObject = paramActivity.getResources();
    AssetManager localAssetManager = new AssetManager();
    localAssetManager.addAssetPath(paramFile);
    Resources localResources = new Resources(localAssetManager, ((Resources)localObject).getDisplayMetrics(), ((Resources)localObject).getConfiguration());
    paramFile = null;
    localObject = paramFile;
    if (paramApplicationInfo.labelRes != 0) {}
    try
    {
      localObject = localResources.getText(paramApplicationInfo.labelRes);
      paramFile = (File)localObject;
      if (localObject == null)
      {
        if (paramApplicationInfo.nonLocalizedLabel == null) {
          break label131;
        }
        paramFile = paramApplicationInfo.nonLocalizedLabel;
      }
      for (;;)
      {
        localAssetManager = null;
        localObject = localAssetManager;
        if (paramApplicationInfo.icon != 0) {}
        try
        {
          localObject = localResources.getDrawable(paramApplicationInfo.icon);
          paramApplicationInfo = (ApplicationInfo)localObject;
          if (localObject == null) {
            paramApplicationInfo = paramActivity.getPackageManager().getDefaultActivityIcon();
          }
          return new AppSnippet(paramFile, paramApplicationInfo);
          label131:
          paramFile = paramApplicationInfo.packageName;
        }
        catch (Resources.NotFoundException paramApplicationInfo)
        {
          for (;;)
          {
            localObject = localAssetManager;
          }
        }
      }
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      for (;;)
      {
        File localFile = paramFile;
      }
    }
  }
  
  public static PackageParser.Package getPackageInfo(File paramFile)
  {
    PackageParser localPackageParser = new PackageParser();
    try
    {
      paramFile = localPackageParser.parsePackage(paramFile, 0);
      return paramFile;
    }
    catch (PackageParser.PackageParserException paramFile) {}
    return null;
  }
  
  public static View initSnippet(View paramView, CharSequence paramCharSequence, Drawable paramDrawable)
  {
    ((ImageView)paramView.findViewById(2131361951)).setImageDrawable(paramDrawable);
    ((TextView)paramView.findViewById(2131361952)).setText(paramCharSequence);
    return paramView;
  }
  
  public static View initSnippetForInstalledApp(Activity paramActivity, ApplicationInfo paramApplicationInfo, View paramView)
  {
    return initSnippetForInstalledApp(paramActivity, paramApplicationInfo, paramView, null);
  }
  
  public static View initSnippetForInstalledApp(Activity paramActivity, ApplicationInfo paramApplicationInfo, View paramView, UserHandle paramUserHandle)
  {
    PackageManager localPackageManager = paramActivity.getPackageManager();
    Drawable localDrawable2 = paramApplicationInfo.loadIcon(localPackageManager);
    Drawable localDrawable1 = localDrawable2;
    if (paramUserHandle != null) {
      localDrawable1 = paramActivity.getPackageManager().getUserBadgedIcon(localDrawable2, paramUserHandle);
    }
    return initSnippet(paramView, paramApplicationInfo.loadLabel(localPackageManager), localDrawable1);
  }
  
  public static View initSnippetForNewApp(Activity paramActivity, AppSnippet paramAppSnippet, int paramInt)
  {
    paramActivity = paramActivity.findViewById(paramInt);
    ((ImageView)paramActivity.findViewById(2131361951)).setImageDrawable(paramAppSnippet.icon);
    ((TextView)paramActivity.findViewById(2131361952)).setText(paramAppSnippet.label);
    return paramActivity;
  }
  
  public static boolean isPackageAlreadyInstalled(Activity paramActivity, String paramString)
  {
    paramActivity = paramActivity.getPackageManager().getInstalledPackages(8192);
    int j = paramActivity.size();
    int i = 0;
    while (i < j)
    {
      if (paramString.equalsIgnoreCase(((PackageInfo)paramActivity.get(i)).packageName)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static class AppSnippet
  {
    Drawable icon;
    CharSequence label;
    
    public AppSnippet(CharSequence paramCharSequence, Drawable paramDrawable)
    {
      this.label = paramCharSequence;
      this.icon = paramDrawable;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\packageuninstaller\PackageUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */