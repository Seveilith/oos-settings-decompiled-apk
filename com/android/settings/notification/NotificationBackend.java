package com.android.settings.notification;

import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.Utils;

public class NotificationBackend
{
  private static final String TAG = "NotificationBackend";
  static INotificationManager sINM = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
  
  public boolean getBypassZenMode(String paramString, int paramInt)
  {
    boolean bool = false;
    try
    {
      paramInt = sINM.getPriority(paramString, paramInt);
      if (paramInt == 2) {
        bool = true;
      }
      return bool;
    }
    catch (Exception paramString)
    {
      Log.w("NotificationBackend", "Error calling NoMan", paramString);
    }
    return false;
  }
  
  public int getImportance(String paramString, int paramInt)
  {
    try
    {
      paramInt = sINM.getImportance(paramString, paramInt);
      return paramInt;
    }
    catch (Exception paramString)
    {
      Log.w("NotificationBackend", "Error calling NoMan", paramString);
    }
    return 64536;
  }
  
  public boolean getLedEnabled(String paramString)
  {
    try
    {
      boolean bool = sINM.isNotificationLedEnabled(paramString);
      return bool;
    }
    catch (Exception paramString)
    {
      Log.w("NotificationBackend", "Error calling setLedDisabled", paramString);
    }
    return false;
  }
  
  public boolean getNotificationsBanned(String paramString, int paramInt)
  {
    try
    {
      boolean bool = sINM.areNotificationsEnabledForPackage(paramString, paramInt);
      return !bool;
    }
    catch (Exception paramString)
    {
      Log.w("NotificationBackend", "Error calling NoMan", paramString);
    }
    return false;
  }
  
  public int getVisibilityOverride(String paramString, int paramInt)
  {
    try
    {
      paramInt = sINM.getVisibilityOverride(paramString, paramInt);
      return paramInt;
    }
    catch (Exception paramString)
    {
      Log.w("NotificationBackend", "Error calling NoMan", paramString);
    }
    return 64536;
  }
  
  public AppRow loadAppRow(Context paramContext, PackageManager paramPackageManager, ApplicationInfo paramApplicationInfo)
  {
    AppRow localAppRow = new AppRow();
    localAppRow.pkg = paramApplicationInfo.packageName;
    localAppRow.uid = paramApplicationInfo.uid;
    try
    {
      localAppRow.label = paramApplicationInfo.loadLabel(paramPackageManager);
      localAppRow.icon = paramApplicationInfo.loadIcon(paramPackageManager);
      localAppRow.banned = getNotificationsBanned(localAppRow.pkg, localAppRow.uid);
      localAppRow.appImportance = getImportance(localAppRow.pkg, localAppRow.uid);
      localAppRow.appBypassDnd = getBypassZenMode(localAppRow.pkg, localAppRow.uid);
      localAppRow.appVisOverride = getVisibilityOverride(localAppRow.pkg, localAppRow.uid);
      localAppRow.lockScreenSecure = new LockPatternUtils(paramContext).isSecure(UserHandle.myUserId());
      localAppRow.ledEnabled = getLedEnabled(localAppRow.pkg);
      return localAppRow;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e("NotificationBackend", "Error loading application label for " + localAppRow.pkg, localThrowable);
        localAppRow.label = localAppRow.pkg;
      }
    }
  }
  
  public AppRow loadAppRow(Context paramContext, PackageManager paramPackageManager, PackageInfo paramPackageInfo)
  {
    AppRow localAppRow = loadAppRow(paramContext, paramPackageManager, paramPackageInfo.applicationInfo);
    localAppRow.systemApp = Utils.isSystemPackage(paramContext.getResources(), paramPackageManager, paramPackageInfo);
    return localAppRow;
  }
  
  public boolean setBypassZenMode(String paramString, int paramInt, boolean paramBoolean)
  {
    try
    {
      INotificationManager localINotificationManager = sINM;
      if (paramBoolean) {}
      for (int i = 2;; i = 0)
      {
        localINotificationManager.setPriority(paramString, paramInt, i);
        return true;
      }
      return false;
    }
    catch (Exception paramString)
    {
      Log.w("NotificationBackend", "Error calling NoMan", paramString);
    }
  }
  
  public boolean setImportance(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      sINM.setImportance(paramString, paramInt1, paramInt2);
      return true;
    }
    catch (Exception paramString)
    {
      Log.w("NotificationBackend", "Error calling NoMan", paramString);
    }
    return false;
  }
  
  public boolean setLedEnabled(String paramString, boolean paramBoolean)
  {
    try
    {
      sINM.setNotificationLedStatus(paramString, paramBoolean);
      return true;
    }
    catch (Exception paramString)
    {
      Log.w("NotificationBackend", "Error calling setLedDisabled", paramString);
    }
    return false;
  }
  
  public boolean setVisibilityOverride(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      sINM.setVisibilityOverride(paramString, paramInt1, paramInt2);
      return true;
    }
    catch (Exception paramString)
    {
      Log.w("NotificationBackend", "Error calling NoMan", paramString);
    }
    return false;
  }
  
  public static class AppRow
    extends NotificationBackend.Row
  {
    public boolean appBypassDnd;
    public int appImportance;
    public int appVisOverride;
    public boolean banned;
    public boolean first;
    public Drawable icon;
    public CharSequence label;
    public boolean ledEnabled;
    public boolean lockScreenSecure;
    public String pkg;
    public Intent settingsIntent;
    public boolean systemApp;
    public int uid;
  }
  
  static class Row
  {
    public String section;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\NotificationBackend.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */