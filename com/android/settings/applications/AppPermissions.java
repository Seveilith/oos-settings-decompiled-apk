package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.util.ArrayMap;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class AppPermissions
{
  private static final String TAG = "AppPermissions";
  private final Context mContext;
  private final ArrayMap<String, PermissionGroup> mGroups = new ArrayMap();
  private final PackageInfo mPackageInfo;
  
  public AppPermissions(Context paramContext, String paramString)
  {
    this.mContext = paramContext;
    this.mPackageInfo = getPackageInfo(paramString);
    refresh();
  }
  
  public static boolean appSupportsRuntime(ApplicationInfo paramApplicationInfo)
  {
    return paramApplicationInfo.targetSdkVersion > 22;
  }
  
  private PackageInfo getPackageInfo(String paramString)
  {
    try
    {
      PackageInfo localPackageInfo = this.mContext.getPackageManager().getPackageInfo(paramString, 4096);
      return localPackageInfo;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.e("AppPermissions", "Unable to find " + paramString, localNameNotFoundException);
    }
    return null;
  }
  
  private void loadPermissionGroups()
  {
    this.mGroups.clear();
    if (this.mPackageInfo.requestedPermissions == null) {
      return;
    }
    boolean bool3 = appSupportsRuntime(this.mPackageInfo.applicationInfo);
    int i = 0;
    Object localObject;
    for (;;)
    {
      if (i >= this.mPackageInfo.requestedPermissions.length) {
        break label245;
      }
      localObject = this.mPackageInfo.requestedPermissions[i];
      try
      {
        localPermissionInfo = this.mContext.getPackageManager().getPermissionInfo((String)localObject, 0);
        str = localPermissionInfo.name;
        if (localPermissionInfo.group == null) {
          break;
        }
        localObject = localPermissionInfo.group;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        for (;;)
        {
          PermissionInfo localPermissionInfo;
          String str;
          PermissionGroup localPermissionGroup2;
          PermissionGroup localPermissionGroup1;
          Log.w("AppPermissions", "Unknown permission: " + (String)localObject);
          continue;
          localObject = str;
          continue;
          boolean bool1 = false;
          continue;
          bool1 = false;
          continue;
          boolean bool2 = false;
        }
      }
      localPermissionGroup2 = (PermissionGroup)this.mGroups.get(localObject);
      localPermissionGroup1 = localPermissionGroup2;
      if (localPermissionGroup2 == null)
      {
        localPermissionGroup1 = new PermissionGroup(null);
        this.mGroups.put(localObject, localPermissionGroup1);
      }
      if (!bool3) {
        break label235;
      }
      if (localPermissionInfo.protectionLevel != 1) {
        break label230;
      }
      bool1 = true;
      if ((this.mPackageInfo.requestedPermissionsFlags[i] & 0x2) == 0) {
        break label240;
      }
      bool2 = true;
      localPermissionGroup1.addPermission(new Permission(bool1, bool2), str);
      i += 1;
    }
    label230:
    label235:
    label240:
    label245:
    i = this.mGroups.size() - 1;
    while (i >= 0)
    {
      if (!PermissionGroup.-get0((PermissionGroup)this.mGroups.valueAt(i))) {
        this.mGroups.removeAt(i);
      }
      i -= 1;
    }
  }
  
  public int getGrantedPermissionsCount()
  {
    int j = 0;
    int i = 0;
    while (i < this.mGroups.size())
    {
      int k = j;
      if (((PermissionGroup)this.mGroups.valueAt(i)).areRuntimePermissionsGranted()) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  public int getPermissionCount()
  {
    return this.mGroups.size();
  }
  
  public void refresh()
  {
    if (this.mPackageInfo != null) {
      loadPermissionGroups();
    }
  }
  
  private static final class Permission
  {
    private boolean granted;
    private final boolean runtime;
    
    public Permission(boolean paramBoolean1, boolean paramBoolean2)
    {
      this.runtime = paramBoolean1;
      this.granted = paramBoolean2;
    }
  }
  
  private static final class PermissionGroup
  {
    private boolean mHasRuntimePermissions;
    private final ArrayMap<String, AppPermissions.Permission> mPermissions = new ArrayMap();
    
    void addPermission(AppPermissions.Permission paramPermission, String paramString)
    {
      this.mPermissions.put(paramString, paramPermission);
      if (AppPermissions.Permission.-get1(paramPermission)) {
        this.mHasRuntimePermissions = true;
      }
    }
    
    public boolean areRuntimePermissionsGranted()
    {
      int j = this.mPermissions.size();
      int i = 0;
      while (i < j)
      {
        AppPermissions.Permission localPermission = (AppPermissions.Permission)this.mPermissions.valueAt(i);
        if ((!AppPermissions.Permission.-get1(localPermission)) || (AppPermissions.Permission.-get0(localPermission))) {
          i += 1;
        } else {
          return false;
        }
      }
      return true;
    }
    
    public List<AppPermissions.Permission> getPermissions()
    {
      return new ArrayList(this.mPermissions.values());
    }
    
    public boolean hasRuntimePermissions()
    {
      return this.mHasRuntimePermissions;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppPermissions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */