package com.oneplus.settings.defaultapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultAppLogic
{
  private static final byte[] initLock = new byte[0];
  private static DefaultAppLogic instance;
  private final String TAG = "DefaultAppLogic";
  private final Context mContext;
  private Map<String, List<String>> mExcludedMap;
  private String[] mKeys;
  private String[] mValues;
  
  private DefaultAppLogic(Context paramContext)
  {
    this.mContext = paramContext;
    this.mKeys = DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY;
    this.mValues = DefaultAppUtils.getDefaultAppValueList();
    initExcludedMap();
  }
  
  public static DefaultAppLogic getInstance(Context paramContext)
  {
    if (instance == null) {}
    synchronized (initLock)
    {
      if (instance == null) {
        instance = new DefaultAppLogic(paramContext);
      }
      return instance;
    }
  }
  
  private void initExcludedMap()
  {
    this.mExcludedMap = new HashMap();
    ArrayList localArrayList = new ArrayList();
    localArrayList.add("com.android.documentsui");
    this.mExcludedMap.put(this.mKeys[1], localArrayList);
  }
  
  private boolean isAppExist(String paramString1, List<String> paramList, String paramString2)
  {
    if (TextUtils.isEmpty(paramString2)) {
      return false;
    }
    paramString1 = (List)this.mExcludedMap.get(paramString1);
    if ((paramString1 != null) && (paramString1.contains(paramString2))) {
      return true;
    }
    int i = 0;
    while (i < paramList.size())
    {
      if (paramString2.equals(paramList.get(i))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void updateRelatedDefaultApp(String paramString1, String paramString2)
  {
    if (TextUtils.isEmpty(paramString2)) {
      return;
    }
    int i = 0;
    while (i < this.mKeys.length)
    {
      if ((!this.mKeys[i].equals(paramString1)) && (paramString2.equals(this.mValues[i]))) {
        resetDefaultApp(this.mKeys[i]);
      }
      i += 1;
    }
  }
  
  public List<DefaultAppActivityInfo> getAppInfoList(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = DefaultAppUtils.create(this.mContext, paramString).getAppIntent();
    paramString = this.mContext.getPackageManager();
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (Intent)((Iterator)localObject1).next();
      DefaultAppActivityInfo localDefaultAppActivityInfo = new DefaultAppActivityInfo();
      localObject2 = paramString.queryIntentActivities((Intent)localObject2, 131072).iterator();
      while (((Iterator)localObject2).hasNext()) {
        localDefaultAppActivityInfo.addActivityInfo(((ResolveInfo)((Iterator)localObject2).next()).activityInfo);
      }
      localArrayList.add(localDefaultAppActivityInfo);
    }
    return localArrayList;
  }
  
  public List<String> getAppPackageNameList(String paramString, List<DefaultAppActivityInfo> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Iterator localIterator = ((DefaultAppActivityInfo)paramList.next()).getActivityInfo().iterator();
      while (localIterator.hasNext())
      {
        ActivityInfo localActivityInfo = (ActivityInfo)localIterator.next();
        if (!isAppExist(paramString, localArrayList, localActivityInfo.packageName)) {
          localArrayList.add(localActivityInfo.packageName);
        }
      }
    }
    return localArrayList;
  }
  
  public String getDefaultAppName(String paramString)
  {
    return DefaultAppUtils.getDefaultAppName(this.mContext, paramString);
  }
  
  public String getDefaultAppPackageName(String paramString)
  {
    return DefaultAppUtils.getDefaultAppPackageName(this.mContext, paramString);
  }
  
  public int getDefaultAppPosition(String paramString, List<String> paramList)
  {
    return getDefaultAppPosition(paramList, DataHelper.getDefaultAppPackageName(this.mContext, paramString));
  }
  
  public int getDefaultAppPosition(String paramString1, List<String> paramList, String paramString2)
  {
    int k = -1;
    int i = 0;
    for (;;)
    {
      int j = k;
      if (i < paramList.size())
      {
        if (((String)paramList.get(i)).equals(paramString2)) {
          j = i;
        }
      }
      else {
        return j;
      }
      i += 1;
    }
  }
  
  public int getDefaultAppPosition(List<String> paramList, String paramString)
  {
    int k = -1;
    int i = 0;
    for (;;)
    {
      int j = k;
      if (i < paramList.size())
      {
        if (((String)paramList.get(i)).equals(paramString)) {
          j = i;
        }
      }
      else {
        return j;
      }
      i += 1;
    }
  }
  
  public String getPmDefaultAppPackageName(String paramString)
  {
    Object localObject3 = DefaultAppUtils.create(this.mContext, paramString).getAppIntent();
    Object localObject2 = this.mContext.getPackageManager();
    Object localObject1 = new LinkedHashSet();
    localObject3 = ((Iterable)localObject3).iterator();
    Object localObject4;
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = ((PackageManager)localObject2).resolveActivity((Intent)((Iterator)localObject3).next(), 65536);
      if (localObject4 != null) {
        ((Set)localObject1).add(((ResolveInfo)localObject4).activityInfo.packageName);
      }
    }
    localObject2 = (List)this.mExcludedMap.get(paramString);
    ((Set)localObject1).remove("android");
    localObject3 = ((Set)localObject1).iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (String)((Iterator)localObject3).next();
      if ((localObject2 != null) && (((List)localObject2).contains(localObject4))) {
        ((Iterator)localObject3).remove();
      }
    }
    if (((Set)localObject1).size() < 1)
    {
      Log.d("DefaultAppLogic", "getDefaultAppPackageName appType:" + paramString + " error defaultApp.size != 1");
      return null;
    }
    localObject1 = (String)localObject1.toArray()[0];
    Log.d("DefaultAppLogic", "getPmDefaultAppPackageName appType:" + paramString + ", defaultApp pkg:" + (String)localObject1);
    return (String)localObject1;
  }
  
  public int getSystemDefaultAppPosition(String paramString, List<String> paramList)
  {
    int k = -1;
    paramString = DefaultAppUtils.getSystemDefaultPackageName(this.mContext, paramString);
    int i = 0;
    for (;;)
    {
      int j = k;
      if (i < paramList.size())
      {
        if (((String)paramList.get(i)).equals(paramString)) {
          j = i;
        }
      }
      else {
        return j;
      }
      i += 1;
    }
  }
  
  public void initDefaultAppSettings()
  {
    if (DataHelper.isDefaultAppInited(this.mContext)) {
      return;
    }
    int i = 0;
    if (i < this.mKeys.length)
    {
      String str1 = this.mKeys[i];
      String str2 = getPmDefaultAppPackageName(str1);
      if ((str2 == null) || ("android".equals(str2))) {
        DefaultAppUtils.resetDefaultApp(this.mContext, this.mKeys[i]);
      }
      for (;;)
      {
        i += 1;
        break;
        List localList1 = getAppInfoList(str1);
        List localList2 = getAppPackageNameList(str1, localList1);
        setDefaultAppPosition(str1, localList1, localList2, getDefaultAppPosition(localList2, str2));
      }
    }
    DataHelper.setDefaultAppInited(this.mContext);
  }
  
  public Object invoke(PackageManager paramPackageManager, Method paramMethod, Object... paramVarArgs)
  {
    if ((paramPackageManager == null) || (paramMethod == null)) {
      return null;
    }
    try
    {
      paramPackageManager = paramMethod.invoke(paramPackageManager, paramVarArgs);
      return paramPackageManager;
    }
    catch (InvocationTargetException paramPackageManager)
    {
      paramPackageManager.printStackTrace();
      return null;
    }
    catch (IllegalArgumentException paramPackageManager)
    {
      paramPackageManager.printStackTrace();
      return null;
    }
    catch (IllegalAccessException paramPackageManager)
    {
      paramPackageManager.printStackTrace();
    }
    return null;
  }
  
  void resetDefaultApp(String paramString)
  {
    List localList1 = getAppInfoList(paramString);
    List localList2 = getAppPackageNameList(paramString, localList1);
    setDefaultAppPosition(paramString, localList1, localList2, getSystemDefaultAppPosition(paramString, localList2));
  }
  
  public void setDefaultApp(String paramString1, List<DefaultAppActivityInfo> paramList, List<String> paramList1, String paramString2)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    int i = getDefaultAppPosition(paramString1, paramList1);
    DataHelper.setDefaultAppPackageName(this.mContext, paramString1, paramString2);
    if (i != -1)
    {
      localPackageManager.clearPackagePreferredActivities((String)paramList1.get(i));
      updateRelatedDefaultApp(paramString1, (String)paramList1.get(i));
    }
    paramList1 = DefaultAppUtils.create(this.mContext, paramString1);
    List localList1 = paramList1.getAppFilter();
    List localList2 = paramList1.getAppMatchParam();
    i = 0;
    for (;;)
    {
      if (i < localList1.size())
      {
        List localList3 = ((DefaultAppActivityInfo)paramList.get(i)).getActivityInfo();
        ComponentName[] arrayOfComponentName = new ComponentName[localList3.size()];
        paramList1 = null;
        int j = localList3.size() - 1;
        Object localObject;
        while (j >= 0)
        {
          localObject = (ActivityInfo)localList3.get(j);
          String str1 = ((ActivityInfo)localObject).packageName;
          String str2 = ((ActivityInfo)localObject).name;
          arrayOfComponentName[j] = new ComponentName(str1, str2);
          localObject = paramList1;
          if (!TextUtils.isEmpty(str1))
          {
            localObject = paramList1;
            if (str1.equals(paramString2)) {
              localObject = new ComponentName(str1, str2);
            }
          }
          j -= 1;
          paramList1 = (List<String>)localObject;
        }
        if (paramList1 != null)
        {
          localPackageManager.addPreferredActivity((IntentFilter)localList1.get(i), ((Integer)localList2.get(i)).intValue(), arrayOfComponentName, paramList1);
          if (!paramString1.equals("op_default_app_browser")) {}
        }
        try
        {
          localObject = Class.forName(UserHandle.class.getName());
          j = ((Integer)((Class)localObject).getMethod("getCallingUserId", new Class[0]).invoke(localObject, new Object[0])).intValue();
          invoke(localPackageManager, localPackageManager.getClass().getDeclaredMethod("setDefaultBrowserPackageName", new Class[] { String.class, Integer.TYPE }), new Object[] { paramList1.getPackageName(), Integer.valueOf(j) });
          i += 1;
        }
        catch (Exception paramList1)
        {
          for (;;)
          {
            Log.e("DefaultAppLogic", "setDefaultAppPosition: " + paramList1);
          }
        }
      }
    }
  }
  
  public void setDefaultAppPosition(String paramString, List<DefaultAppActivityInfo> paramList, List<String> paramList1, int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= paramList1.size())) {
      return;
    }
    setDefaultAppPosition(paramString, paramList, paramList1, (String)paramList1.get(paramInt));
  }
  
  public void setDefaultAppPosition(String paramString1, List<DefaultAppActivityInfo> paramList, List<String> paramList1, String paramString2)
  {
    if (getDefaultAppPosition(paramString1, paramList1) == -1)
    {
      Iterator localIterator = paramList1.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (!paramString2.equals(str)) {
          setDefaultApp(paramString1, paramList, paramList1, str);
        }
      }
    }
    setDefaultApp(paramString1, paramList, paramList1, paramString2);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\defaultapp\DefaultAppLogic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */