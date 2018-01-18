package com.oneplus.settings.displaysizeadaption;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.Log;
import com.oneplus.settings.better.OPAppModel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DisplaySizeAdaptiongeManager
{
  private static final int FULLSCREENAPP_TYPE = 70;
  private static DisplaySizeAdaptiongeManager mDisplaySizeAdaptiongeManager;
  private AppOpsManager mAppOpsManager;
  private Context mContext;
  private Map<String, OPAppModel> mOriginalSizeAppMap = new HashMap();
  private PackageManager mPackageManager;
  ApplicationInfo multiAppInfo = null;
  
  public DisplaySizeAdaptiongeManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAppOpsManager = ((AppOpsManager)this.mContext.getSystemService("appops"));
    this.mPackageManager = this.mContext.getPackageManager();
  }
  
  private Drawable getBadgedIcon(PackageManager paramPackageManager, ResolveInfo paramResolveInfo)
  {
    paramResolveInfo = paramResolveInfo.activityInfo.applicationInfo;
    return paramPackageManager.getUserBadgedIcon(paramPackageManager.loadUnbadgedItemIcon(paramResolveInfo, paramResolveInfo), new UserHandle(UserHandle.getUserId(paramResolveInfo.uid)));
  }
  
  public static DisplaySizeAdaptiongeManager getInstance(Context paramContext)
  {
    if (mDisplaySizeAdaptiongeManager == null) {
      mDisplaySizeAdaptiongeManager = new DisplaySizeAdaptiongeManager(paramContext);
    }
    return mDisplaySizeAdaptiongeManager;
  }
  
  private void loadClassAppList(int paramInt)
  {
    HashMap localHashMap = new HashMap();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    try
    {
      localObject1 = this.mAppOpsManager.getPackagesForOps(new int[] { 70 });
      if (localObject1 != null)
      {
        localObject1 = ((Iterable)localObject1).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (AppOpsManager.PackageOps)((Iterator)localObject1).next();
          int i = ((AppOpsManager.PackageOps)localObject2).getUid();
          localObject3 = ((AppOpsManager.PackageOps)localObject2).getOps().iterator();
          while (((Iterator)localObject3).hasNext())
          {
            localObject4 = (AppOpsManager.OpEntry)((Iterator)localObject3).next();
            if ((((AppOpsManager.OpEntry)localObject4).getOp() == 70) && (((AppOpsManager.OpEntry)localObject4).getMode() == paramInt)) {
              localHashMap.put(((AppOpsManager.PackageOps)localObject2).getPackageName(), Integer.valueOf(i));
            }
          }
          continue;
          return;
        }
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    for (;;)
    {
      localObject1 = new Intent("android.intent.action.MAIN", null);
      ((Intent)localObject1).addCategory("android.intent.category.LAUNCHER");
      localObject1 = this.mPackageManager.queryIntentActivities((Intent)localObject1, 0);
      if (((List)localObject1).isEmpty()) {
        return;
      }
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (ResolveInfo)((Iterator)localObject1).next();
        localObject3 = ((ResolveInfo)localObject2).activityInfo.name;
        localObject3 = ((ResolveInfo)localObject2).activityInfo.packageName;
        localObject4 = (String)((ResolveInfo)localObject2).loadLabel(this.mPackageManager);
        boolean bool = localException.containsKey(localObject3);
        if (bool)
        {
          localObject4 = new OPAppModel((String)localObject3, (String)localObject4, "", 0, bool);
          ((OPAppModel)localObject4).setAppIcon(getBadgedIcon(this.mPackageManager, (ResolveInfo)localObject2));
          if (paramInt == 0) {
            this.mOriginalSizeAppMap.put(localObject3, localObject4);
          }
        }
      }
    }
  }
  
  private void loadFullScreenApp()
  {
    loadClassAppList(1);
  }
  
  private void loadOriginalSizeApp()
  {
    loadClassAppList(0);
  }
  
  public boolean isOriginalSizeApp(String paramString)
  {
    boolean bool = false;
    if ((OPAppModel)this.mOriginalSizeAppMap.get(paramString) != null) {
      bool = true;
    }
    return bool;
  }
  
  public Map<String, OPAppModel> loadAppMap()
  {
    localHashMap = new HashMap();
    try
    {
      Object localObject = new Intent("android.intent.action.MAIN", null);
      ((Intent)localObject).addCategory("android.intent.category.LAUNCHER");
      localObject = this.mPackageManager.queryIntentActivities((Intent)localObject, 0);
      if (((List)localObject).isEmpty()) {
        return null;
      }
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject).next();
        String str = localResolveInfo.activityInfo.name;
        str = localResolveInfo.activityInfo.packageName;
        OPAppModel localOPAppModel = new OPAppModel(str, (String)localResolveInfo.loadLabel(this.mPackageManager), "", localResolveInfo.activityInfo.applicationInfo.uid, false);
        localOPAppModel.setAppIcon(getBadgedIcon(this.mPackageManager, localResolveInfo));
        localHashMap.put(str, localOPAppModel);
      }
      return localHashMap;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      loadOriginalSizeApp();
    }
  }
  
  public void setClassApp(int paramInt, String paramString, boolean paramBoolean)
  {
    int j = 1;
    Object localObject = this.mAppOpsManager;
    int i;
    if (paramBoolean)
    {
      i = 1;
      ((AppOpsManager)localObject).setMode(70, paramInt, paramString, i);
      ((ActivityManager)this.mContext.getSystemService("activity")).forceStopPackage(paramString);
    }
    for (;;)
    {
      try
      {
        this.multiAppInfo = this.mPackageManager.getApplicationInfoByUserId(paramString, 1, 999);
        if (this.multiAppInfo != null)
        {
          localObject = this.mAppOpsManager;
          int k = this.multiAppInfo.uid;
          if (!paramBoolean) {
            break label190;
          }
          i = j;
          ((AppOpsManager)localObject).setMode(70, k, paramString, i);
        }
        try
        {
          localObject = new OPAppModel(paramString, this.mPackageManager.getApplicationInfo(paramString, 0).loadLabel(this.mPackageManager).toString(), "", paramInt, false);
          if (!paramBoolean)
          {
            this.mOriginalSizeAppMap.put(paramString, localObject);
            return;
          }
          this.mOriginalSizeAppMap.remove(paramString);
          return;
        }
        catch (PackageManager.NameNotFoundException paramString)
        {
          Log.e("DisplaySizeAdaptiongeManager", paramString.getMessage());
          return;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        continue;
      }
      i = 0;
      break;
      label190:
      i = 0;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\displaysizeadaption\DisplaySizeAdaptiongeManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */