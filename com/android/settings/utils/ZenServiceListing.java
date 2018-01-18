package com.android.settings.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ZenServiceListing
{
  private final Set<ServiceInfo> mApprovedServices = new ArraySet();
  private final ManagedServiceSettings.Config mConfig;
  private final ContentResolver mContentResolver;
  private final Context mContext;
  private final List<Callback> mZenCallbacks = new ArrayList();
  
  public ZenServiceListing(Context paramContext, ManagedServiceSettings.Config paramConfig)
  {
    this.mContext = paramContext;
    this.mConfig = paramConfig;
    this.mContentResolver = paramContext.getContentResolver();
  }
  
  private static int getServices(ManagedServiceSettings.Config paramConfig, List<ServiceInfo> paramList, PackageManager paramPackageManager)
  {
    int j = 0;
    if (paramList != null) {
      paramList.clear();
    }
    int i = ActivityManager.getCurrentUser();
    paramPackageManager = paramPackageManager.queryIntentServicesAsUser(new Intent(paramConfig.intentAction), 132, i);
    i = 0;
    int k = paramPackageManager.size();
    if (i < k)
    {
      ServiceInfo localServiceInfo = ((ResolveInfo)paramPackageManager.get(i)).serviceInfo;
      if (!paramConfig.permission.equals(localServiceInfo.permission)) {
        Slog.w(paramConfig.tag, "Skipping " + paramConfig.noun + " service " + localServiceInfo.packageName + "/" + localServiceInfo.name + ": it does not require the permission " + paramConfig.permission);
      }
      for (;;)
      {
        i += 1;
        break;
        if (paramList != null) {
          paramList.add(localServiceInfo);
        }
        j += 1;
      }
    }
    return j;
  }
  
  private boolean matchesApprovedPackage(List<String> paramList, ComponentName paramComponentName)
  {
    if ((paramList.contains(paramComponentName.flattenToString())) || (paramList.contains(paramComponentName.getPackageName()))) {
      return true;
    }
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Object localObject = (String)paramList.next();
      if (!TextUtils.isEmpty((CharSequence)localObject))
      {
        localObject = ComponentName.unflattenFromString((String)localObject);
        if ((localObject != null) && (((ComponentName)localObject).getPackageName().equals(paramComponentName.getPackageName()))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public void addZenCallback(Callback paramCallback)
  {
    this.mZenCallbacks.add(paramCallback);
  }
  
  public ServiceInfo findService(ComponentName paramComponentName)
  {
    Iterator localIterator = this.mApprovedServices.iterator();
    while (localIterator.hasNext())
    {
      ServiceInfo localServiceInfo = (ServiceInfo)localIterator.next();
      if (new ComponentName(localServiceInfo.packageName, localServiceInfo.name).equals(paramComponentName)) {
        return localServiceInfo;
      }
    }
    return null;
  }
  
  public void reloadApprovedServices()
  {
    int i = 0;
    this.mApprovedServices.clear();
    Object localObject1 = new String[2];
    localObject1[0] = this.mConfig.setting;
    localObject1[1] = this.mConfig.secondarySetting;
    int j = localObject1.length;
    while (i < j)
    {
      Object localObject2 = localObject1[i];
      if (!TextUtils.isEmpty((CharSequence)localObject2))
      {
        localObject2 = Settings.Secure.getString(this.mContentResolver, (String)localObject2);
        if (!TextUtils.isEmpty((CharSequence)localObject2))
        {
          localObject2 = Arrays.asList(((String)localObject2).split(":"));
          Object localObject3 = new ArrayList();
          getServices(this.mConfig, (List)localObject3, this.mContext.getPackageManager());
          localObject3 = ((Iterable)localObject3).iterator();
          while (((Iterator)localObject3).hasNext())
          {
            ServiceInfo localServiceInfo = (ServiceInfo)((Iterator)localObject3).next();
            if (matchesApprovedPackage((List)localObject2, localServiceInfo.getComponentName())) {
              this.mApprovedServices.add(localServiceInfo);
            }
          }
        }
      }
      i += 1;
    }
    if (!this.mApprovedServices.isEmpty())
    {
      localObject1 = this.mZenCallbacks.iterator();
      while (((Iterator)localObject1).hasNext()) {
        ((Callback)((Iterator)localObject1).next()).onServicesReloaded(this.mApprovedServices);
      }
    }
  }
  
  public void removeZenCallback(Callback paramCallback)
  {
    this.mZenCallbacks.remove(paramCallback);
  }
  
  public static abstract interface Callback
  {
    public abstract void onServicesReloaded(Set<ServiceInfo> paramSet);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\utils\ZenServiceListing.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */