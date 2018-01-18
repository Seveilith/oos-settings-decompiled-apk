package com.android.settingslib.location;

import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecentLocationApps
{
  private static final String ANDROID_SYSTEM_PACKAGE_NAME = "android";
  private static final int[] LOCATION_OPS = { 41, 42 };
  private static final int RECENT_TIME_INTERVAL_MILLIS = 900000;
  private static final String TAG = RecentLocationApps.class.getSimpleName();
  private final Context mContext;
  private final PackageManager mPackageManager;
  
  public RecentLocationApps(Context paramContext)
  {
    this.mContext = paramContext;
    this.mPackageManager = paramContext.getPackageManager();
  }
  
  private Request getRequestFromOps(long paramLong, AppOpsManager.PackageOps paramPackageOps)
  {
    String str = paramPackageOps.getPackageName();
    Object localObject1 = paramPackageOps.getOps();
    boolean bool = false;
    int i = 0;
    localObject1 = ((Iterable)localObject1).iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (AppOpsManager.OpEntry)((Iterator)localObject1).next();
      if ((((AppOpsManager.OpEntry)localObject2).isRunning()) || (((AppOpsManager.OpEntry)localObject2).getTime() >= paramLong - 900000L)) {
        switch (((AppOpsManager.OpEntry)localObject2).getOp())
        {
        default: 
          break;
        case 41: 
          i = 1;
          break;
        case 42: 
          bool = true;
        }
      }
    }
    int j;
    if ((bool) || (i != 0))
    {
      i = paramPackageOps.getUid();
      j = UserHandle.getUserId(i);
    }
    try
    {
      paramPackageOps = AppGlobals.getPackageManager().getApplicationInfo(str, 128, j);
      if (paramPackageOps == null)
      {
        Log.w(TAG, "Null application info retrieved for package " + str + ", userId " + j);
        return null;
        if (Log.isLoggable(TAG, 2)) {
          Log.v(TAG, str + " hadn't used location within the time interval.");
        }
        return null;
      }
      localObject2 = new UserHandle(j);
      localObject1 = this.mPackageManager.getApplicationIcon(paramPackageOps);
      Drawable localDrawable = this.mPackageManager.getUserBadgedIcon((Drawable)localObject1, (UserHandle)localObject2);
      CharSequence localCharSequence = this.mPackageManager.getApplicationLabel(paramPackageOps);
      localObject1 = this.mPackageManager.getUserBadgedLabel(localCharSequence, (UserHandle)localObject2);
      paramPackageOps = (AppOpsManager.PackageOps)localObject1;
      if (localCharSequence.toString().contentEquals((CharSequence)localObject1)) {
        paramPackageOps = null;
      }
      paramPackageOps = new Request(str, i, (UserHandle)localObject2, localDrawable, localCharSequence, bool, paramPackageOps, null);
      return paramPackageOps;
    }
    catch (RemoteException paramPackageOps)
    {
      Log.w(TAG, "Error while retrieving application info for package " + str + ", userId " + j, paramPackageOps);
    }
    return null;
  }
  
  private boolean isSystemApplication(PackageManager paramPackageManager, String paramString)
  {
    boolean bool2 = false;
    if ((paramPackageManager == null) || (paramString == null)) {}
    while (paramString.length() == 0) {
      return false;
    }
    try
    {
      paramPackageManager = paramPackageManager.getApplicationInfo(paramString, 0);
      boolean bool1 = bool2;
      if (paramPackageManager != null)
      {
        int i = paramPackageManager.flags;
        bool1 = bool2;
        if ((i & 0x1) > 0) {
          bool1 = true;
        }
      }
      return bool1;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager)
    {
      paramPackageManager.printStackTrace();
    }
    return false;
  }
  
  public List<Request> getAppList()
  {
    List localList1 = ((AppOpsManager)this.mContext.getSystemService("appops")).getPackagesForOps(LOCATION_OPS);
    int i;
    ArrayList localArrayList;
    long l;
    int j;
    label67:
    Object localObject;
    boolean bool;
    if (localList1 != null)
    {
      i = localList1.size();
      localArrayList = new ArrayList(i);
      l = System.currentTimeMillis();
      List localList2 = ((UserManager)this.mContext.getSystemService("user")).getUserProfiles();
      j = 0;
      if (j >= i) {
        break label209;
      }
      localObject = (AppOpsManager.PackageOps)localList1.get(j);
      String str = ((AppOpsManager.PackageOps)localObject).getPackageName();
      int k = ((AppOpsManager.PackageOps)localObject).getUid();
      int m = UserHandle.getUserId(k);
      if (k != 1000) {
        break label177;
      }
      bool = "android".equals(str);
      label120:
      if ((!bool) && (localList2.contains(new UserHandle(m))) && ((m != 999) || (!isSystemApplication(this.mPackageManager, str)))) {
        break label183;
      }
    }
    for (;;)
    {
      j += 1;
      break label67;
      i = 0;
      break;
      label177:
      bool = false;
      break label120;
      label183:
      localObject = getRequestFromOps(l, (AppOpsManager.PackageOps)localObject);
      if (localObject != null) {
        localArrayList.add(localObject);
      }
    }
    label209:
    return localArrayList;
  }
  
  public static class Request
  {
    public final CharSequence contentDescription;
    public final Drawable icon;
    public final boolean isHighBattery;
    public final CharSequence label;
    public final String packageName;
    public final int uid;
    public final UserHandle userHandle;
    
    private Request(String paramString, int paramInt, UserHandle paramUserHandle, Drawable paramDrawable, CharSequence paramCharSequence1, boolean paramBoolean, CharSequence paramCharSequence2)
    {
      this.packageName = paramString;
      this.userHandle = paramUserHandle;
      this.icon = paramDrawable;
      this.label = paramCharSequence1;
      this.isHighBattery = paramBoolean;
      this.contentDescription = paramCharSequence2;
      this.uid = paramInt;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\location\RecentLocationApps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */