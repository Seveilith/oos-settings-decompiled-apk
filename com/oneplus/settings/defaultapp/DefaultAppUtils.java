package com.oneplus.settings.defaultapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeCamera;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeEmail;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeGallery;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeMusic;
import com.oneplus.settings.utils.OPUtils;
import java.util.List;

public class DefaultAppUtils
{
  public static final String TAG = "DefaultAppUtils";
  
  public static void clearDefaultApp(Context paramContext, String paramString)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    String[] arrayOfString = DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY;
    int j = 0;
    int i = 0;
    int m = arrayOfString.length;
    while (i < m)
    {
      String str1 = arrayOfString[i];
      String str2 = DataHelper.getDefaultAppPackageName(paramContext, str1);
      int k = j;
      if (str2 != null)
      {
        k = j;
        if (str2.equals(paramString))
        {
          k = 1;
          DataHelper.setDefaultAppPackageName(paramContext, str1, "");
        }
      }
      i += 1;
      j = k;
    }
    if (j != 0) {
      localPackageManager.clearPackagePreferredActivities(paramString);
    }
  }
  
  public static DefaultAppTypeInfo create(Context paramContext, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return new DefaultAppTypeCamera();
    case 1: 
      return new DefaultAppTypeGallery();
    case 2: 
      return new DefaultAppTypeMusic();
    }
    return new DefaultAppTypeEmail();
  }
  
  public static DefaultAppTypeInfo create(Context paramContext, String paramString)
  {
    String[] arrayOfString = DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY;
    int k = 0;
    int i = 0;
    for (;;)
    {
      int j = k;
      if (i < arrayOfString.length)
      {
        if (arrayOfString[i].equals(paramString)) {
          j = i;
        }
      }
      else {
        return create(paramContext, j);
      }
      i += 1;
    }
  }
  
  public static String getDefaultAppName(Context paramContext, String paramString)
  {
    String str = DataHelper.getDefaultAppPackageName(paramContext, paramString);
    paramString = getSystemDefaultPackageName(paramContext, paramString);
    boolean bool = isAppExist(paramContext, str);
    if (!TextUtils.isEmpty(str))
    {
      if (bool)
      {
        if (str.equals(paramString)) {
          return paramContext.getString(2131693442);
        }
        return queryAppName(paramContext, str);
      }
      return null;
    }
    return null;
  }
  
  public static String getDefaultAppPackageName(Context paramContext, String paramString)
  {
    paramString = DataHelper.getDefaultAppPackageName(paramContext, paramString);
    boolean bool = isAppExist(paramContext, paramString);
    if (!TextUtils.isEmpty(paramString))
    {
      if (bool) {
        return paramString;
      }
      return null;
    }
    return null;
  }
  
  public static String[] getDefaultAppValueList()
  {
    if (!OPUtils.isO2()) {
      return DefaultAppConstants.DEFAULTAPP_VALUE_LIST_H2OS;
    }
    return DefaultAppConstants.DEFAULTAPP_VALUE_LIST_O2OS;
  }
  
  public static int getKeyTypeInt(String paramString)
  {
    String[] arrayOfString = DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY;
    int k = 0;
    int i = 0;
    for (;;)
    {
      int j = k;
      if (i < arrayOfString.length)
      {
        if (arrayOfString[i].equals(paramString)) {
          j = i;
        }
      }
      else {
        return j;
      }
      i += 1;
    }
  }
  
  public static String getKeyTypeString(int paramInt)
  {
    return DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY[paramInt];
  }
  
  public static String getSystemDefaultPackageName(Context paramContext, String paramString)
  {
    paramContext = DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY;
    String[] arrayOfString = getDefaultAppValueList();
    int i = 0;
    while (i < paramContext.length)
    {
      if (paramContext[i].equals(paramString)) {
        return arrayOfString[i];
      }
      i += 1;
    }
    return null;
  }
  
  public static boolean isAppExist(Context paramContext, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    try
    {
      paramContext.getPackageManager().getApplicationInfo(paramString, 128);
      return true;
    }
    catch (Exception paramContext) {}
    return false;
  }
  
  public static String queryAppName(Context paramContext, String paramString)
  {
    try
    {
      paramContext = paramContext.getPackageManager();
      paramContext = String.valueOf(paramContext.getApplicationInfo(paramString, 128).loadLabel(paramContext));
      return paramContext;
    }
    catch (Exception paramContext)
    {
      paramContext.printStackTrace();
    }
    return "";
  }
  
  public static void resetDefaultApp(Context paramContext, String paramString)
  {
    DefaultAppLogic localDefaultAppLogic = DefaultAppLogic.getInstance(paramContext);
    List localList1 = localDefaultAppLogic.getAppInfoList(paramString);
    List localList2 = localDefaultAppLogic.getAppPackageNameList(paramString, localList1);
    localDefaultAppLogic.setDefaultAppPosition(paramString, localList1, localList2, localDefaultAppLogic.getDefaultAppPosition(paramString, localList2, getSystemDefaultPackageName(paramContext, paramString)));
  }
  
  public static void updateDefaultApp(Context paramContext)
  {
    DefaultAppLogic localDefaultAppLogic = DefaultAppLogic.getInstance(paramContext);
    String[] arrayOfString = DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY;
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      String str1 = arrayOfString[i];
      String str2 = DataHelper.getDefaultAppPackageName(paramContext, str1);
      List localList1 = localDefaultAppLogic.getAppInfoList(str1);
      List localList2 = localDefaultAppLogic.getAppPackageNameList(str1, localList1);
      if ((str2 != null) && (localList2.contains(str2))) {
        localDefaultAppLogic.setDefaultAppPosition(str1, localList1, localList2, localDefaultAppLogic.getDefaultAppPosition(str1, localList2, str2));
      }
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\defaultapp\DefaultAppUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */