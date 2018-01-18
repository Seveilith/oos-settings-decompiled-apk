package com.android.settingslib.net;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.settingslib.R.string;
import com.android.settingslib.Utils;

public class UidDetailProvider
{
  public static final int OTHER_USER_RANGE_START = -2000;
  private static final String TAG = "DataUsage";
  private final Context mContext;
  private final SparseArray<UidDetail> mUidDetailCache;
  
  public UidDetailProvider(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mUidDetailCache = new SparseArray();
  }
  
  public static int buildKeyForUser(int paramInt)
  {
    return 63536 - paramInt;
  }
  
  private UidDetail buildUidDetail(int paramInt)
  {
    Object localObject1 = this.mContext.getResources();
    PackageManager localPackageManager = this.mContext.getPackageManager();
    UidDetail localUidDetail = new UidDetail();
    localUidDetail.label = localPackageManager.getNameForUid(paramInt);
    localUidDetail.icon = localPackageManager.getDefaultActivityIcon();
    Object localObject2;
    switch (paramInt)
    {
    default: 
      localObject1 = (UserManager)this.mContext.getSystemService("user");
      if (isKeyForUser(paramInt))
      {
        localObject2 = ((UserManager)localObject1).getUserInfo(getUserIdForKey(paramInt));
        if (localObject2 != null)
        {
          localUidDetail.label = Utils.getUserLabel(this.mContext, (UserInfo)localObject2);
          localUidDetail.icon = Utils.getUserIcon(this.mContext, (UserManager)localObject1, (UserInfo)localObject2);
          return localUidDetail;
        }
      }
      break;
    case 1000: 
      localUidDetail.label = ((Resources)localObject1).getString(R.string.process_kernel_label);
      localUidDetail.icon = localPackageManager.getDefaultActivityIcon();
      return localUidDetail;
    case -4: 
      if (UserManager.supportsMultipleUsers()) {}
      for (paramInt = R.string.data_usage_uninstalled_apps_users;; paramInt = R.string.data_usage_uninstalled_apps)
      {
        localUidDetail.label = ((Resources)localObject1).getString(paramInt);
        localUidDetail.icon = localPackageManager.getDefaultActivityIcon();
        return localUidDetail;
      }
    case -5: 
      localUidDetail.label = ((Resources)localObject1).getString(Utils.getTetheringLabel((ConnectivityManager)this.mContext.getSystemService("connectivity")));
      localUidDetail.icon = localPackageManager.getDefaultActivityIcon();
      return localUidDetail;
    }
    Object localObject3 = localPackageManager.getPackagesForUid(paramInt);
    int i;
    if (localObject3 != null) {
      i = localObject3.length;
    }
    for (;;)
    {
      try
      {
        k = UserHandle.getUserId(paramInt);
        localObject2 = new UserHandle(k);
        localIPackageManager = AppGlobals.getPackageManager();
        if (i != 1) {
          continue;
        }
        localObject3 = localIPackageManager.getApplicationInfo(localObject3[0], 0, k);
        if (localObject3 != null)
        {
          localUidDetail.label = ((ApplicationInfo)localObject3).loadLabel(localPackageManager).toString();
          localUidDetail.icon = ((UserManager)localObject1).getBadgedIconForUser(((ApplicationInfo)localObject3).loadIcon(localPackageManager), new UserHandle(k));
        }
        localUidDetail.contentDescription = ((UserManager)localObject1).getBadgedLabelForUser(localUidDetail.label, (UserHandle)localObject2);
      }
      catch (RemoteException localRemoteException)
      {
        int k;
        IPackageManager localIPackageManager;
        int j;
        String str;
        PackageInfo localPackageInfo;
        ApplicationInfo localApplicationInfo;
        Log.w("DataUsage", "Error while building UI detail for uid " + paramInt, localRemoteException);
        continue;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.w("DataUsage", "Error while building UI detail for uid " + paramInt, localNameNotFoundException);
        continue;
      }
      if (TextUtils.isEmpty(localUidDetail.label)) {
        localUidDetail.label = Integer.toString(paramInt);
      }
      return localUidDetail;
      i = 0;
      continue;
      if (i > 1)
      {
        localUidDetail.detailLabels = new CharSequence[i];
        localUidDetail.detailContentDescriptions = new CharSequence[i];
        j = 0;
        if (j < i)
        {
          str = localObject3[j];
          localPackageInfo = localPackageManager.getPackageInfo(str, 0);
          localApplicationInfo = localIPackageManager.getApplicationInfo(str, 0, k);
          if (localApplicationInfo != null)
          {
            localUidDetail.detailLabels[j] = localApplicationInfo.loadLabel(localPackageManager).toString();
            localUidDetail.detailContentDescriptions[j] = ((UserManager)localObject1).getBadgedLabelForUser(localUidDetail.detailLabels[j], (UserHandle)localObject2);
            if (localPackageInfo.sharedUserLabel != 0)
            {
              localUidDetail.label = localPackageManager.getText(str, localPackageInfo.sharedUserLabel, localPackageInfo.applicationInfo).toString();
              localUidDetail.icon = ((UserManager)localObject1).getBadgedIconForUser(localApplicationInfo.loadIcon(localPackageManager), (UserHandle)localObject2);
            }
          }
          j += 1;
        }
      }
    }
  }
  
  public static int getUserIdForKey(int paramInt)
  {
    return 63536 - paramInt;
  }
  
  public static boolean isKeyForUser(int paramInt)
  {
    return paramInt <= 63536;
  }
  
  public void clearCache()
  {
    synchronized (this.mUidDetailCache)
    {
      this.mUidDetailCache.clear();
      return;
    }
  }
  
  public UidDetail getUidDetail(int paramInt, boolean paramBoolean)
  {
    synchronized (this.mUidDetailCache)
    {
      UidDetail localUidDetail1 = (UidDetail)this.mUidDetailCache.get(paramInt);
      if (localUidDetail1 != null) {
        return localUidDetail1;
      }
    }
    if (!paramBoolean) {
      return null;
    }
    UidDetail localUidDetail2 = buildUidDetail(paramInt);
    synchronized (this.mUidDetailCache)
    {
      this.mUidDetailCache.put(paramInt, localUidDetail2);
      return localUidDetail2;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\net\UidDetailProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */