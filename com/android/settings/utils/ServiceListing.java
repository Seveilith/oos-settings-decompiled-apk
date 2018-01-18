package com.android.settings.utils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Slog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ServiceListing
{
  private final List<Callback> mCallbacks = new ArrayList();
  private final ManagedServiceSettings.Config mConfig;
  private final ContentResolver mContentResolver;
  private final Context mContext;
  private final HashSet<ComponentName> mEnabledServices = new HashSet();
  private boolean mListening;
  private final BroadcastReceiver mPackageReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      ServiceListing.this.reload();
    }
  };
  private final List<ServiceInfo> mServices = new ArrayList();
  private final ContentObserver mSettingsObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      ServiceListing.this.reload();
    }
  };
  
  public ServiceListing(Context paramContext, ManagedServiceSettings.Config paramConfig)
  {
    this.mContext = paramContext;
    this.mConfig = paramConfig;
    this.mContentResolver = paramContext.getContentResolver();
  }
  
  public static int getEnabledServicesCount(ManagedServiceSettings.Config paramConfig, Context paramContext)
  {
    paramConfig = Settings.Secure.getString(paramContext.getContentResolver(), paramConfig.setting);
    if ((paramConfig == null) || ("".equals(paramConfig))) {
      return 0;
    }
    return paramConfig.split(":").length;
  }
  
  protected static int getServices(ManagedServiceSettings.Config paramConfig, List<ServiceInfo> paramList, PackageManager paramPackageManager)
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
  
  public static int getServicesCount(ManagedServiceSettings.Config paramConfig, PackageManager paramPackageManager)
  {
    return getServices(paramConfig, null, paramPackageManager);
  }
  
  private void loadEnabledServices()
  {
    this.mEnabledServices.clear();
    Object localObject = Settings.Secure.getString(this.mContentResolver, this.mConfig.setting);
    if ((localObject == null) || ("".equals(localObject))) {}
    for (;;)
    {
      return;
      localObject = ((String)localObject).split(":");
      int i = 0;
      while (i < localObject.length)
      {
        ComponentName localComponentName = ComponentName.unflattenFromString(localObject[i]);
        if (localComponentName != null) {
          this.mEnabledServices.add(localComponentName);
        }
        i += 1;
      }
    }
  }
  
  private void saveEnabledServices()
  {
    Object localObject1 = null;
    Object localObject2 = this.mEnabledServices.iterator();
    if (((Iterator)localObject2).hasNext())
    {
      localObject3 = (ComponentName)((Iterator)localObject2).next();
      if (localObject1 == null) {
        localObject1 = new StringBuilder();
      }
      for (;;)
      {
        ((StringBuilder)localObject1).append(((ComponentName)localObject3).flattenToString());
        break;
        ((StringBuilder)localObject1).append(':');
      }
    }
    localObject2 = this.mContentResolver;
    Object localObject3 = this.mConfig.setting;
    if (localObject1 != null) {}
    for (localObject1 = ((StringBuilder)localObject1).toString();; localObject1 = "")
    {
      Settings.Secure.putString((ContentResolver)localObject2, (String)localObject3, (String)localObject1);
      return;
    }
  }
  
  public void addCallback(Callback paramCallback)
  {
    this.mCallbacks.add(paramCallback);
  }
  
  public boolean isEnabled(ComponentName paramComponentName)
  {
    return this.mEnabledServices.contains(paramComponentName);
  }
  
  public List<ServiceInfo> reload()
  {
    loadEnabledServices();
    getServices(this.mConfig, this.mServices, this.mContext.getPackageManager());
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((Callback)localIterator.next()).onServicesReloaded(this.mServices);
    }
    return this.mServices;
  }
  
  public void removeCallback(Callback paramCallback)
  {
    this.mCallbacks.remove(paramCallback);
  }
  
  public void setEnabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mEnabledServices.add(paramComponentName);
    }
    for (;;)
    {
      saveEnabledServices();
      return;
      this.mEnabledServices.remove(paramComponentName);
    }
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (this.mListening == paramBoolean) {
      return;
    }
    this.mListening = paramBoolean;
    if (this.mListening)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
      localIntentFilter.addDataScheme("package");
      this.mContext.registerReceiver(this.mPackageReceiver, localIntentFilter);
      this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor(this.mConfig.setting), false, this.mSettingsObserver);
      return;
    }
    this.mContext.unregisterReceiver(this.mPackageReceiver);
    this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
  }
  
  public static abstract interface Callback
  {
    public abstract void onServicesReloaded(List<ServiceInfo> paramList);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\utils\ServiceListing.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */