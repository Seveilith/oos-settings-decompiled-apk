package com.oneplus.settings.highpowerapp;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.util.OpFeatures;
import com.oneplus.settings.backgroundoptimize.Logutil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyActivityManager
  implements IMyActivityManager
{
  private static final String TAG = MyActivityManager.class.getSimpleName();
  private static volatile MyActivityManager instance;
  private Method getBgMonitorMode;
  private Method getBgPowerHungryList;
  Field isLocked = null;
  Field isStopped = null;
  private ActivityManager mActivityManager;
  Field packageName = null;
  Field powerLevel = null;
  private Method setBgMonitorMode;
  private Method stopBgPowerHungryApp;
  Field timeStamp = null;
  Field uId = null;
  
  private MyActivityManager(Context paramContext)
  {
    try
    {
      this.mActivityManager = ((ActivityManager)paramContext.getSystemService("activity"));
      paramContext = this.mActivityManager.getClass();
      this.getBgMonitorMode = paramContext.getDeclaredMethod("getBgMonitorMode", new Class[0]);
      this.setBgMonitorMode = paramContext.getDeclaredMethod("setBgMonitorMode", new Class[] { Boolean.TYPE });
      this.getBgPowerHungryList = paramContext.getDeclaredMethod("getBgPowerHungryList", new Class[0]);
      this.stopBgPowerHungryApp = paramContext.getDeclaredMethod("stopBgPowerHungryApp", new Class[] { String.class, Integer.TYPE });
      return;
    }
    catch (Exception paramContext)
    {
      Log.e(TAG, "MyActivityManager Exception=" + paramContext);
      paramContext.printStackTrace();
    }
  }
  
  private List<HighPowerApp> convert(List<?> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Object localObject = paramList.next();
      if (this.packageName == null) {}
      try
      {
        this.packageName = localObject.getClass().getField("pkgName");
        this.packageName.setAccessible(true);
        this.powerLevel = localObject.getClass().getField("powerLevel");
        this.powerLevel.setAccessible(true);
        this.isLocked = localObject.getClass().getField("isLocked");
        this.isLocked.setAccessible(true);
        this.isStopped = localObject.getClass().getField("isStopped");
        this.isStopped.setAccessible(true);
        this.timeStamp = localObject.getClass().getField("timeStamp");
        this.timeStamp.setAccessible(true);
        if (OpFeatures.isSupport(new int[] { 27 }))
        {
          this.uId = localObject.getClass().getField("uid");
          this.uId.setAccessible(true);
        }
        try
        {
          String str = (String)this.packageName.get(localObject);
          i = ((Integer)this.powerLevel.get(localObject)).intValue();
          bool1 = ((Boolean)this.isLocked.get(localObject)).booleanValue();
          bool2 = ((Boolean)this.isStopped.get(localObject)).booleanValue();
          l = ((Long)this.timeStamp.get(localObject)).longValue();
          if (OpFeatures.isSupport(new int[] { 27 }))
          {
            int j = ((Integer)this.uId.get(localObject)).intValue();
            localArrayList.add(new HighPowerApp(str, j, i, bool1, bool2, l));
            Logutil.loge(TAG, "convert pn: " + str + ", uid =" + j + ", level=" + i + ", lock=" + i + ", stop=" + bool2);
          }
        }
        catch (Exception localException1)
        {
          localException1.printStackTrace();
        }
      }
      catch (Exception localException2)
      {
        int i;
        boolean bool1;
        boolean bool2;
        long l;
        for (;;)
        {
          localException2.printStackTrace();
        }
        localArrayList.add(new HighPowerApp(localException2, i, bool1, bool2, l));
        Logutil.loge(TAG, "convert pn: " + localException2 + ", level=" + i + ", lock=" + i + ", stop=" + bool2);
      }
    }
    return localArrayList;
  }
  
  public static MyActivityManager get(Context paramContext)
  {
    if (instance == null) {}
    try
    {
      if (instance == null) {
        instance = new MyActivityManager(paramContext);
      }
      return instance;
    }
    finally
    {
      paramContext = finally;
      throw paramContext;
    }
  }
  
  public boolean getBgMonitorMode()
  {
    return ((Boolean)invoke(this.getBgMonitorMode, new Object[0])).booleanValue();
  }
  
  public List<HighPowerApp> getBgPowerHungryList()
  {
    return convert((List)invoke(this.getBgPowerHungryList, new Object[0]));
  }
  
  public Object invoke(Method paramMethod, Object... paramVarArgs)
  {
    if ((this.mActivityManager == null) || (paramMethod == null)) {
      return null;
    }
    try
    {
      paramMethod = paramMethod.invoke(this.mActivityManager, paramVarArgs);
      return paramMethod;
    }
    catch (InvocationTargetException paramMethod)
    {
      paramMethod.printStackTrace();
      return null;
    }
    catch (IllegalArgumentException paramMethod)
    {
      paramMethod.printStackTrace();
      return null;
    }
    catch (IllegalAccessException paramMethod)
    {
      paramMethod.printStackTrace();
    }
    return null;
  }
  
  public void setBgMonitorMode(boolean paramBoolean)
  {
    invoke(this.setBgMonitorMode, new Object[] { Boolean.valueOf(paramBoolean) });
  }
  
  public void stopBgPowerHungryApp(String paramString, int paramInt)
  {
    invoke(this.stopBgPowerHungryApp, new Object[] { paramString, Integer.valueOf(paramInt) });
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\highpowerapp\MyActivityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */