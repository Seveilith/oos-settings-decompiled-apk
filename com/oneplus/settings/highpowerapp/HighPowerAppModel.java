package com.oneplus.settings.highpowerapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Switch;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HighPowerAppModel
  implements View.OnClickListener
{
  private static final String TAG = "HighPowerAppModel";
  private static final boolean USE_FAKE_DATA = false;
  private IMyActivityManager mActivityManager;
  private List<HighPowerApp> mAppList = new ArrayList();
  private SoftReference<Context> mContext;
  private List<PackageInfo> mData = new ArrayList();
  private List<IHighPowerAppObserver> mDataObserverList = new LinkedList();
  private Object mDataObserverListLock = new Byte[0];
  private Map<String, PackageInfo> mInstalledPackages = new ConcurrentHashMap();
  private PackageManager mPackageManager;
  private ExecutorService mThreadPool = Executors.newCachedThreadPool();
  
  public HighPowerAppModel(Context paramContext, IHighPowerAppObserver paramIHighPowerAppObserver)
  {
    this.mContext = new SoftReference(paramContext);
    this.mActivityManager = MyActivityManager.get(paramContext);
    registerObserver(paramIHighPowerAppObserver);
    initialize();
  }
  
  private Context getContext()
  {
    return (Context)this.mContext.get();
  }
  
  private List<HighPowerApp> makeFakeList()
  {
    ArrayList localArrayList = new ArrayList();
    int j = 0;
    if (j < this.mData.size())
    {
      PackageInfo localPackageInfo = (PackageInfo)this.mData.get(j);
      String str = localPackageInfo.packageName;
      int k = localPackageInfo.applicationInfo.uid;
      boolean bool1 = false;
      boolean bool2 = false;
      int i = 0;
      switch (j % 3)
      {
      }
      for (;;)
      {
        localArrayList.add(new HighPowerApp(str, k, i, bool1, bool2, System.currentTimeMillis()));
        j += 1;
        break;
        bool2 = true;
        i = 1;
        continue;
        bool1 = true;
        i = 0;
        continue;
        i = -1;
      }
    }
    return localArrayList;
  }
  
  private void notifyDataChanged()
  {
    if (this.mDataObserverList != null) {
      synchronized (this.mDataObserverListLock)
      {
        Iterator localIterator = this.mDataObserverList.iterator();
        while (localIterator.hasNext())
        {
          IHighPowerAppObserver localIHighPowerAppObserver = (IHighPowerAppObserver)localIterator.next();
          if (localIHighPowerAppObserver != null) {
            localIHighPowerAppObserver.OnDataChanged();
          }
        }
      }
    }
  }
  
  private void process()
  {
    List localList = this.mActivityManager.getBgPowerHungryList();
    Log.d("HighPowerAppModel", "HighPowerAppModel getBgPowerHungryList: " + localList);
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      HighPowerApp localHighPowerApp = (HighPowerApp)localIterator.next();
      if (this.mInstalledPackages.containsKey(localHighPowerApp.pkgName))
      {
        localHighPowerApp.uid = ((PackageInfo)this.mInstalledPackages.get(localHighPowerApp.pkgName)).applicationInfo.uid;
      }
      else
      {
        if (this.mPackageManager == null) {
          this.mPackageManager = getContext().getPackageManager();
        }
        try
        {
          PackageInfo localPackageInfo = this.mPackageManager.getPackageInfo(localHighPowerApp.pkgName, 0);
          this.mInstalledPackages.put(localPackageInfo.packageName, localPackageInfo);
          localHighPowerApp.uid = localPackageInfo.applicationInfo.uid;
        }
        catch (Exception localException) {}
      }
    }
    Collections.sort(localList, new Comparator()
    {
      public int compare(HighPowerApp paramAnonymousHighPowerApp1, HighPowerApp paramAnonymousHighPowerApp2)
      {
        return paramAnonymousHighPowerApp1.pkgName.hashCode() - paramAnonymousHighPowerApp2.pkgName.hashCode();
      }
    });
    this.mAppList = localList;
    notifyDataChanged();
  }
  
  public boolean getBgMonitorMode()
  {
    return this.mActivityManager.getBgMonitorMode();
  }
  
  public List<HighPowerApp> getDataList()
  {
    return this.mAppList;
  }
  
  public void initialize()
  {
    Runnable local1 = new Runnable()
    {
      public void run()
      {
        HighPowerAppModel.-wrap0(HighPowerAppModel.this);
      }
    };
    this.mThreadPool.execute(local1);
  }
  
  public void onClick(View paramView)
  {
    paramView = (Switch)paramView;
    this.mActivityManager.setBgMonitorMode(paramView.isChecked());
    if (paramView.isChecked()) {
      update();
    }
  }
  
  public void registerObserver(IHighPowerAppObserver paramIHighPowerAppObserver)
  {
    synchronized (this.mDataObserverListLock)
    {
      if (!this.mDataObserverList.contains(paramIHighPowerAppObserver)) {
        this.mDataObserverList.add(paramIHighPowerAppObserver);
      }
      return;
    }
  }
  
  public void releaseResource()
  {
    if (this.mThreadPool != null)
    {
      this.mThreadPool.shutdown();
      this.mThreadPool = null;
    }
  }
  
  public void stopApp(String paramString)
  {
    Iterator localIterator = this.mAppList.iterator();
    while (localIterator.hasNext())
    {
      HighPowerApp localHighPowerApp = (HighPowerApp)localIterator.next();
      if (localHighPowerApp.pkgName.equals(paramString)) {
        this.mActivityManager.stopBgPowerHungryApp(paramString, localHighPowerApp.powerLevel);
      }
    }
  }
  
  public void unregisterObserver(IHighPowerAppObserver paramIHighPowerAppObserver)
  {
    synchronized (this.mDataObserverListLock)
    {
      if (this.mDataObserverList.contains(paramIHighPowerAppObserver)) {
        this.mDataObserverList.remove(paramIHighPowerAppObserver);
      }
      return;
    }
  }
  
  public void update()
  {
    Runnable local2 = new Runnable()
    {
      public void run()
      {
        HighPowerAppModel.-wrap0(HighPowerAppModel.this);
      }
    };
    this.mThreadPool.execute(local2);
  }
  
  public void updateInstalledPackages()
  {
    this.mPackageManager = getContext().getPackageManager();
    this.mInstalledPackages.clear();
    try
    {
      Object localObject = this.mPackageManager.getInstalledPackages(2);
      if (((List)localObject).isEmpty()) {
        return;
      }
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        PackageInfo localPackageInfo = (PackageInfo)((Iterator)localObject).next();
        if (!PackageUtils.isSystemApplication(getContext(), localPackageInfo.packageName)) {
          this.mInstalledPackages.put(localPackageInfo.packageName, localPackageInfo);
        }
      }
      return;
    }
    catch (Exception localException)
    {
      Log.e("HighPowerAppModel", "some unknown error happened.");
      localException.printStackTrace();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\highpowerapp\HighPowerAppModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */