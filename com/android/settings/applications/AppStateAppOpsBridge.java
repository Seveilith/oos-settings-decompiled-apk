package com.android.settings.applications;

import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ParceledListSlice;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class AppStateAppOpsBridge
  extends AppStateBaseBridge
{
  private static final String TAG = "AppStateAppOpsBridge";
  private final AppOpsManager mAppOpsManager;
  private final int[] mAppOpsOpCodes;
  private final Context mContext;
  private final IPackageManager mIPackageManager;
  private final String[] mPermissions;
  private final List<UserHandle> mProfiles;
  private final UserManager mUserManager;
  
  public AppStateAppOpsBridge(Context paramContext, ApplicationsState paramApplicationsState, AppStateBaseBridge.Callback paramCallback, int paramInt, String[] paramArrayOfString)
  {
    super(paramApplicationsState, paramCallback);
    this.mContext = paramContext;
    this.mIPackageManager = AppGlobals.getPackageManager();
    this.mUserManager = UserManager.get(paramContext);
    this.mProfiles = this.mUserManager.getUserProfiles();
    this.mAppOpsManager = ((AppOpsManager)paramContext.getSystemService("appops"));
    this.mAppOpsOpCodes = new int[] { paramInt };
    this.mPermissions = paramArrayOfString;
  }
  
  private boolean doesAnyPermissionMatch(String paramString, String[] paramArrayOfString)
  {
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      if (paramString.equals(paramArrayOfString[i])) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private SparseArray<ArrayMap<String, PermissionState>> getEntries()
  {
    for (;;)
    {
      int i;
      try
      {
        HashSet localHashSet = new HashSet();
        Object localObject1 = this.mPermissions;
        i = 0;
        int j = localObject1.length;
        Object localObject2;
        if (i < j)
        {
          localObject2 = localObject1[i];
          localObject2 = this.mIPackageManager.getAppOpPermissionPackages((String)localObject2);
          if (localObject2 != null) {
            localHashSet.addAll(Arrays.asList((Object[])localObject2));
          }
        }
        else
        {
          if (localHashSet.isEmpty()) {
            return null;
          }
          localObject1 = new SparseArray();
          localObject2 = this.mProfiles.iterator();
          if (((Iterator)localObject2).hasNext())
          {
            UserHandle localUserHandle = (UserHandle)((Iterator)localObject2).next();
            ArrayMap localArrayMap = new ArrayMap();
            i = localUserHandle.getIdentifier();
            ((SparseArray)localObject1).put(i, localArrayMap);
            Iterator localIterator = localHashSet.iterator();
            if (localIterator.hasNext())
            {
              String str = (String)localIterator.next();
              boolean bool = this.mIPackageManager.isPackageAvailable(str, i);
              if ((shouldIgnorePackage(str)) || (!bool)) {
                continue;
              }
              localArrayMap.put(str, new PermissionState(str, localUserHandle));
              continue;
            }
            continue;
          }
          return (SparseArray<ArrayMap<String, PermissionState>>)localObject1;
        }
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("AppStateAppOpsBridge", "PackageManager is dead. Can't get list of packages requesting " + this.mPermissions[0], localRemoteException);
        return null;
      }
      i += 1;
    }
  }
  
  private boolean isThisUserAProfileOfCurrentUser(int paramInt)
  {
    int j = this.mProfiles.size();
    int i = 0;
    while (i < j)
    {
      if (((UserHandle)this.mProfiles.get(i)).getIdentifier() == paramInt) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void loadAppOpsStates(SparseArray<ArrayMap<String, PermissionState>> paramSparseArray)
  {
    List localList = this.mAppOpsManager.getPackagesForOps(this.mAppOpsOpCodes);
    int i;
    int j;
    label28:
    AppOpsManager.PackageOps localPackageOps;
    int k;
    if (localList != null)
    {
      i = localList.size();
      j = 0;
      if (j >= i) {
        return;
      }
      localPackageOps = (AppOpsManager.PackageOps)localList.get(j);
      k = UserHandle.getUserId(localPackageOps.getUid());
      if (isThisUserAProfileOfCurrentUser(k)) {
        break label77;
      }
    }
    for (;;)
    {
      j += 1;
      break label28;
      i = 0;
      break;
      label77:
      Object localObject = (ArrayMap)paramSparseArray.get(k);
      if (localObject != null)
      {
        localObject = (PermissionState)((ArrayMap)localObject).get(localPackageOps.getPackageName());
        if (localObject == null) {
          Log.w("AppStateAppOpsBridge", "AppOp permission exists for package " + localPackageOps.getPackageName() + " of user " + k + " but package doesn't exist or did not request " + this.mPermissions + " access");
        } else if (localPackageOps.getOps().size() < 1) {
          Log.w("AppStateAppOpsBridge", "No AppOps permission exists for package " + localPackageOps.getPackageName());
        } else {
          ((PermissionState)localObject).appOpMode = ((AppOpsManager.OpEntry)localPackageOps.getOps().get(0)).getMode();
        }
      }
    }
  }
  
  private void loadPermissionsStates(SparseArray<ArrayMap<String, PermissionState>> paramSparseArray)
  {
    if (paramSparseArray == null) {
      return;
    }
    for (;;)
    {
      try
      {
        Iterator localIterator = this.mProfiles.iterator();
        if (localIterator.hasNext())
        {
          int i = ((UserHandle)localIterator.next()).getIdentifier();
          ArrayMap localArrayMap = (ArrayMap)paramSparseArray.get(i);
          if (localArrayMap == null) {
            continue;
          }
          List localList = this.mIPackageManager.getPackagesHoldingPermissions(this.mPermissions, 0, i).getList();
          if (localList != null)
          {
            i = localList.size();
            break label187;
            if (j < i)
            {
              PackageInfo localPackageInfo = (PackageInfo)localList.get(j);
              PermissionState localPermissionState = (PermissionState)localArrayMap.get(localPackageInfo.packageName);
              if (localPermissionState != null)
              {
                localPermissionState.packageInfo = localPackageInfo;
                localPermissionState.staticPermissionGranted = true;
              }
              j += 1;
              continue;
            }
          }
          else
          {
            i = 0;
          }
        }
      }
      catch (RemoteException paramSparseArray)
      {
        Log.w("AppStateAppOpsBridge", "PackageManager is dead. Can't get list of packages granted " + this.mPermissions, paramSparseArray);
        return;
      }
      return;
      label187:
      int j = 0;
    }
  }
  
  private boolean shouldIgnorePackage(String paramString)
  {
    if (!paramString.equals("android")) {
      return paramString.equals(this.mContext.getPackageName());
    }
    return true;
  }
  
  public int getNumPackagesAllowedByAppOps()
  {
    Object localObject = getEntries();
    if (localObject == null) {
      return 0;
    }
    loadPermissionsStates((SparseArray)localObject);
    loadAppOpsStates((SparseArray)localObject);
    localObject = (ArrayMap)((SparseArray)localObject).get(this.mUserManager.getUserHandle());
    if (localObject == null) {
      return 0;
    }
    localObject = ((ArrayMap)localObject).values();
    int i = 0;
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      if (((PermissionState)((Iterator)localObject).next()).isPermissible()) {
        i += 1;
      }
    }
    return i;
  }
  
  public int getNumPackagesDeclaredPermission()
  {
    Object localObject = getEntries();
    if (localObject == null) {
      return 0;
    }
    localObject = (ArrayMap)((SparseArray)localObject).get(this.mUserManager.getUserHandle());
    if (localObject == null) {
      return 0;
    }
    return ((ArrayMap)localObject).size();
  }
  
  public PermissionState getPermissionInfo(String paramString, int paramInt)
  {
    localPermissionState = new PermissionState(paramString, new UserHandle(UserHandle.getUserId(paramInt)));
    try
    {
      localPermissionState.packageInfo = this.mIPackageManager.getPackageInfo(paramString, 12288, localPermissionState.userHandle.getIdentifier());
      Object localObject = localPermissionState.packageInfo.requestedPermissions;
      int[] arrayOfInt = localPermissionState.packageInfo.requestedPermissionsFlags;
      int i;
      if (localObject != null) {
        i = 0;
      }
      for (;;)
      {
        if (i < localObject.length)
        {
          if (doesAnyPermissionMatch(localObject[i], this.mPermissions))
          {
            localPermissionState.permissionDeclared = true;
            if ((arrayOfInt[i] & 0x2) != 0) {
              localPermissionState.staticPermissionGranted = true;
            }
          }
        }
        else
        {
          localObject = this.mAppOpsManager.getOpsForPackage(paramInt, paramString, this.mAppOpsOpCodes);
          if ((localObject != null) && (((List)localObject).size() > 0) && (((AppOpsManager.PackageOps)((List)localObject).get(0)).getOps().size() > 0)) {
            localPermissionState.appOpMode = ((AppOpsManager.OpEntry)((AppOpsManager.PackageOps)((List)localObject).get(0)).getOps().get(0)).getMode();
          }
          return localPermissionState;
        }
        i += 1;
      }
      return localPermissionState;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("AppStateAppOpsBridge", "PackageManager is dead. Can't get package info " + paramString, localRemoteException);
    }
  }
  
  protected void loadAllExtraInfo()
  {
    SparseArray localSparseArray = getEntries();
    loadPermissionsStates(localSparseArray);
    loadAppOpsStates(localSparseArray);
    ArrayList localArrayList = this.mAppSession.getAllApps();
    int j = localArrayList.size();
    int i = 0;
    if (i < j)
    {
      ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)localArrayList.get(i);
      Object localObject = (ArrayMap)localSparseArray.get(UserHandle.getUserId(localAppEntry.info.uid));
      if (localObject != null) {}
      for (localObject = ((ArrayMap)localObject).get(localAppEntry.info.packageName);; localObject = null)
      {
        localAppEntry.extraInfo = localObject;
        i += 1;
        break;
      }
    }
  }
  
  protected abstract void updateExtraInfo(ApplicationsState.AppEntry paramAppEntry, String paramString, int paramInt);
  
  public static class PermissionState
  {
    public int appOpMode;
    public PackageInfo packageInfo;
    public final String packageName;
    public boolean permissionDeclared;
    public boolean staticPermissionGranted;
    public final UserHandle userHandle;
    
    public PermissionState(String paramString, UserHandle paramUserHandle)
    {
      this.packageName = paramString;
      this.appOpMode = 3;
      this.userHandle = paramUserHandle;
    }
    
    public boolean isPermissible()
    {
      boolean bool = false;
      if (this.appOpMode == 3) {
        return this.staticPermissionGranted;
      }
      if (this.appOpMode == 0) {
        bool = true;
      }
      return bool;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppStateAppOpsBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */