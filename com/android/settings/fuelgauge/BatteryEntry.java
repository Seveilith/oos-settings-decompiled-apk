package com.android.settings.fuelgauge;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.BatteryStats.Uid;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.internal.os.BatterySipper;
import com.android.settingslib.Utils;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.HashMap;

public class BatteryEntry
{
  public static final int MSG_REPORT_FULLY_DRAWN = 2;
  public static final int MSG_UPDATE_NAME_ICON = 1;
  static final ArrayList<BatteryEntry> mRequestQueue = new ArrayList();
  private static NameAndIconLoader mRequestThread;
  static Handler sHandler;
  static final HashMap<String, UidToDetail> sUidCache = new HashMap();
  public final Context context;
  public String defaultPackageName;
  public Drawable icon;
  public int iconId;
  public String name;
  public final BatterySipper sipper;
  
  public BatteryEntry(Context paramContext, Handler paramHandler, UserManager paramUserManager, BatterySipper paramBatterySipper)
  {
    sHandler = paramHandler;
    paramHandler = paramContext;
    if (paramContext == null) {
      paramHandler = SettingsBaseApplication.mApplication;
    }
    this.context = paramHandler;
    this.sipper = paramBatterySipper;
    switch (-getcom-android-internal-os-BatterySipper$DrainTypeSwitchesValues()[paramBatterySipper.drainType.ordinal()])
    {
    }
    for (;;)
    {
      if (this.iconId > 0)
      {
        this.icon = paramHandler.getDrawable(this.iconId);
        this.icon.setTint(OPUtils.getAccentColor(paramHandler));
      }
      if (((this.name == null) || (this.iconId == 0)) && (this.sipper.uidObj != null)) {
        getQuickNameIconForUid(this.sipper.uidObj.getUid());
      }
      return;
      this.name = paramHandler.getResources().getString(2131692461);
      this.iconId = 2130838062;
      continue;
      this.name = paramHandler.getResources().getString(2131692459);
      this.iconId = 2130838044;
      continue;
      this.name = paramHandler.getResources().getString(2131692460);
      this.iconId = 2130838074;
      continue;
      this.name = paramHandler.getResources().getString(2131692457);
      this.iconId = 2130838075;
      continue;
      this.name = paramHandler.getResources().getString(2131692458);
      this.iconId = 2130838037;
      continue;
      this.name = paramHandler.getResources().getString(2131692454);
      this.iconId = 2130838049;
      continue;
      this.name = paramHandler.getResources().getString(2131692455);
      this.iconId = 2130838049;
      continue;
      this.name = paramBatterySipper.packageWithHighestDrain;
      continue;
      paramContext = paramUserManager.getUserInfo(paramBatterySipper.userId);
      if (paramContext != null)
      {
        this.icon = Utils.getUserIcon(paramHandler, paramUserManager, paramContext);
        this.name = Utils.getUserLabel(paramHandler, paramContext);
      }
      else
      {
        this.icon = null;
        this.name = paramHandler.getResources().getString(2131692195);
        continue;
        this.name = paramHandler.getResources().getString(2131692462);
        this.iconId = 2130838020;
        continue;
        this.name = paramHandler.getResources().getString(2131692463);
        this.iconId = 2130838020;
        continue;
        this.name = paramHandler.getResources().getString(2131692456);
        this.iconId = 2130838043;
      }
    }
  }
  
  public static void clearUidCache()
  {
    sUidCache.clear();
  }
  
  public static void startRequestQueue()
  {
    if (sHandler != null) {}
    synchronized (mRequestQueue)
    {
      if (!mRequestQueue.isEmpty())
      {
        if (mRequestThread != null) {
          mRequestThread.abort();
        }
        mRequestThread = new NameAndIconLoader();
        mRequestThread.setPriority(1);
        mRequestThread.start();
        mRequestQueue.notify();
      }
      return;
    }
  }
  
  public static void stopRequestQueue()
  {
    synchronized (mRequestQueue)
    {
      if (mRequestThread != null)
      {
        mRequestThread.abort();
        mRequestThread = null;
        sHandler = null;
      }
      return;
    }
  }
  
  public Drawable getIcon()
  {
    return this.icon;
  }
  
  public String getLabel()
  {
    return this.name;
  }
  
  void getQuickNameIconForUid(int paramInt)
  {
    ??? = Integer.toString(paramInt);
    if (sUidCache.containsKey(???))
    {
      ??? = (UidToDetail)sUidCache.get(???);
      this.defaultPackageName = ((UidToDetail)???).packageName;
      this.name = ((UidToDetail)???).name;
      this.icon = ((UidToDetail)???).icon;
      return;
    }
    if (this.context == null) {
      return;
    }
    ??? = this.context.getPackageManager();
    this.icon = ((PackageManager)???).getDefaultActivityIcon();
    if (((PackageManager)???).getPackagesForUid(paramInt) == null)
    {
      if (paramInt != 0) {
        break label162;
      }
      this.name = this.context.getResources().getString(2131689581);
    }
    for (;;)
    {
      this.iconId = 2130838020;
      this.icon = this.context.getDrawable(this.iconId);
      this.icon.setTint(OPUtils.getAccentColor(this.context));
      if (sHandler != null) {}
      synchronized (mRequestQueue)
      {
        mRequestQueue.add(this);
        return;
        label162:
        if ("mediaserver".equals(this.name))
        {
          this.name = this.context.getResources().getString(2131692516);
          continue;
        }
        if (!"dex2oat".equals(this.name)) {
          continue;
        }
        this.name = this.context.getResources().getString(2131692517);
      }
    }
  }
  
  public void loadNameAndIcon()
  {
    if (this.sipper.uidObj == null) {
      return;
    }
    Object localObject1 = this.context.getPackageManager();
    int j = this.sipper.uidObj.getUid();
    this.sipper.mPackages = ((PackageManager)localObject1).getPackagesForUid(j);
    String[] arrayOfString;
    Object localObject2;
    int k;
    int i;
    if (this.sipper.mPackages != null)
    {
      arrayOfString = new String[this.sipper.mPackages.length];
      System.arraycopy(this.sipper.mPackages, 0, arrayOfString, 0, this.sipper.mPackages.length);
      localObject2 = AppGlobals.getPackageManager();
      k = UserHandle.getUserId(j);
      i = 0;
      if (i >= arrayOfString.length) {}
    }
    label621:
    label628:
    label633:
    for (;;)
    {
      Object localObject3;
      try
      {
        ApplicationInfo localApplicationInfo = ((IPackageManager)localObject2).getApplicationInfo(arrayOfString[i], 0, k);
        if (localApplicationInfo == null)
        {
          Log.d("PowerUsageSummary", "Retrieving null app info for package " + arrayOfString[i] + ", user " + k);
          break label621;
        }
        localObject3 = localApplicationInfo.loadLabel((PackageManager)localObject1);
        if (localObject3 != null) {
          arrayOfString[i] = ((CharSequence)localObject3).toString();
        }
        if (localApplicationInfo.icon == 0) {
          break label621;
        }
        this.defaultPackageName = this.sipper.mPackages[i];
        this.icon = localApplicationInfo.loadIcon((PackageManager)localObject1);
        if (arrayOfString.length == 1)
        {
          this.name = arrayOfString[0];
          localObject2 = Integer.toString(j);
          if (this.name == null) {
            this.name = this.context.getResources().getString(2131690303);
          }
          if (this.icon == null) {
            this.icon = ((PackageManager)localObject1).getDefaultActivityIcon();
          }
          localObject1 = new UidToDetail();
          ((UidToDetail)localObject1).name = this.name;
          ((UidToDetail)localObject1).icon = this.icon;
          ((UidToDetail)localObject1).packageName = this.defaultPackageName;
          sUidCache.put(localObject2, localObject1);
          if (sHandler != null) {
            sHandler.sendMessage(sHandler.obtainMessage(1, this));
          }
          return;
        }
      }
      catch (RemoteException localRemoteException1)
      {
        Log.d("PowerUsageSummary", "Error while retrieving app info for package " + arrayOfString[i] + ", user " + k, localRemoteException1);
      }
      arrayOfString = this.sipper.mPackages;
      i = 0;
      int m = arrayOfString.length;
      for (;;)
      {
        if (i >= m) {
          break label633;
        }
        String str = arrayOfString[i];
        try
        {
          localObject3 = ((IPackageManager)localObject2).getPackageInfo(str, 0, k);
          if (localObject3 == null)
          {
            Log.d("PowerUsageSummary", "Retrieving null package info for package " + str + ", user " + k);
            break label628;
          }
          if (((PackageInfo)localObject3).sharedUserLabel == 0) {
            break label628;
          }
          CharSequence localCharSequence = ((PackageManager)localObject1).getText(str, ((PackageInfo)localObject3).sharedUserLabel, ((PackageInfo)localObject3).applicationInfo);
          if (localCharSequence == null) {
            break label628;
          }
          this.name = localCharSequence.toString();
          if (((PackageInfo)localObject3).applicationInfo.icon != 0)
          {
            this.defaultPackageName = str;
            this.icon = ((PackageInfo)localObject3).applicationInfo.loadIcon((PackageManager)localObject1);
          }
          if (j != 1000) {
            break label628;
          }
        }
        catch (RemoteException localRemoteException2)
        {
          Log.d("PowerUsageSummary", "Error while retrieving package info for package " + str + ", user " + k, localRemoteException2);
          break label628;
        }
        i += 1;
        break;
        i += 1;
      }
    }
  }
  
  private static class NameAndIconLoader
    extends Thread
  {
    private boolean mAbort = false;
    
    public NameAndIconLoader()
    {
      super();
    }
    
    public void abort()
    {
      this.mAbort = true;
    }
    
    public void run()
    {
      synchronized (BatteryEntry.mRequestQueue)
      {
        if ((BatteryEntry.mRequestQueue.isEmpty()) || (this.mAbort))
        {
          if (BatteryEntry.sHandler != null) {
            BatteryEntry.sHandler.sendEmptyMessage(2);
          }
          BatteryEntry.mRequestQueue.clear();
          return;
        }
        BatteryEntry localBatteryEntry = (BatteryEntry)BatteryEntry.mRequestQueue.remove(0);
        localBatteryEntry.loadNameAndIcon();
      }
    }
  }
  
  static class UidToDetail
  {
    Drawable icon;
    String name;
    String packageName;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\BatteryEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */