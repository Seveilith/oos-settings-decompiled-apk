package com.google.analytics.tracking.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.google.android.gms.common.util.VisibleForTesting;

class AppFieldsDefaultProvider
  implements DefaultProvider
{
  private static AppFieldsDefaultProvider sInstance;
  private static Object sInstanceLock = new Object();
  protected String mAppId;
  protected String mAppInstallerId;
  protected String mAppName;
  protected String mAppVersion;
  
  @VisibleForTesting
  protected AppFieldsDefaultProvider() {}
  
  private AppFieldsDefaultProvider(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    this.mAppId = paramContext.getPackageName();
    this.mAppInstallerId = localPackageManager.getInstallerPackageName(this.mAppId);
    localObject2 = this.mAppId;
    for (localObject1 = localObject2;; localObject1 = paramContext)
    {
      try
      {
        localPackageInfo = localPackageManager.getPackageInfo(paramContext.getPackageName(), 0);
        if (localPackageInfo != null) {
          break label74;
        }
        localObject1 = localObject2;
        localObject2 = localObject3;
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        for (;;)
        {
          PackageInfo localPackageInfo;
          label74:
          Log.e("Error retrieving package info: appName set to " + (String)localObject1);
          localObject2 = localObject3;
        }
      }
      this.mAppName = ((String)localObject1);
      this.mAppVersion = ((String)localObject2);
      return;
      localObject1 = localObject2;
      paramContext = localPackageManager.getApplicationLabel(localPackageInfo.applicationInfo).toString();
      localObject1 = paramContext;
      localObject2 = localPackageInfo.versionName;
    }
  }
  
  @VisibleForTesting
  static void dropInstance()
  {
    synchronized (sInstanceLock)
    {
      sInstance = null;
      return;
    }
  }
  
  public static AppFieldsDefaultProvider getProvider()
  {
    return sInstance;
  }
  
  public static void initializeProvider(Context paramContext)
  {
    synchronized (sInstanceLock)
    {
      if (sInstance != null) {
        return;
      }
      sInstance = new AppFieldsDefaultProvider(paramContext);
    }
  }
  
  public String getValue(String paramString)
  {
    if (paramString != null)
    {
      if (!paramString.equals("&an"))
      {
        if (paramString.equals("&av")) {
          break label49;
        }
        if (paramString.equals("&aid")) {
          break label54;
        }
        if (paramString.equals("&aiid")) {
          break label59;
        }
        return null;
      }
    }
    else {
      return null;
    }
    return this.mAppName;
    label49:
    return this.mAppVersion;
    label54:
    return this.mAppId;
    label59:
    return this.mAppInstallerId;
  }
  
  public boolean providesField(String paramString)
  {
    if ("&an".equals(paramString)) {}
    while (("&av".equals(paramString)) || ("&aid".equals(paramString)) || ("&aiid".equals(paramString))) {
      return true;
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\AppFieldsDefaultProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */