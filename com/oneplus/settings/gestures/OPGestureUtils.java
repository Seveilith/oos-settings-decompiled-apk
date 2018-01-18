package com.oneplus.settings.gestures;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.LauncherApps.ShortcutQuery;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.os.Process;
import android.provider.Settings.System;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.List;

public class OPGestureUtils
{
  private static final String TAG = "OPGestureUtils";
  
  public static int get(int paramInt1, int paramInt2)
  {
    return (1 << paramInt2 & paramInt1) >> paramInt2;
  }
  
  public static String getAppNameByPackageName(Context paramContext, String paramString)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    Intent localIntent = new Intent("android.intent.action.MAIN", null);
    localIntent.addCategory("android.intent.category.LAUNCHER");
    localIntent.setPackage(paramString);
    paramString = localPackageManager.queryIntentActivities(localIntent, 0);
    if (paramString.size() > 0) {
      return (String)((ResolveInfo)paramString.get(0)).loadLabel(localPackageManager);
    }
    return paramContext.getString(2131690365);
  }
  
  public static String getGesturePacakgeUid(Context paramContext, String paramString)
  {
    paramContext.getString(2131690365);
    if (paramString.startsWith("oem_acc_blackscreen_gesture")) {}
    for (;;)
    {
      paramContext = Settings.System.getString(paramContext.getContentResolver(), paramString);
      if (!TextUtils.isEmpty(paramContext)) {
        break;
      }
      return "";
      paramString = getGestureTypebyGestureKey(paramString);
    }
    paramString = paramContext.split(";");
    paramContext = paramString[0];
    if (paramContext.startsWith("OpenApp:"))
    {
      paramContext = "";
      if (paramString.length > 1) {
        paramContext = paramString[1];
      }
      return paramContext;
    }
    if (paramContext.startsWith("OpenShortcut:"))
    {
      paramContext.substring("OpenShortcut:".length());
      paramContext = "";
      if (paramString.length > 2) {
        paramContext = paramString[2];
      }
      return paramContext;
    }
    return "";
  }
  
  public static String getGesturePackageName(Context paramContext, String paramString)
  {
    paramContext.getString(2131690365);
    if (paramString.startsWith("oem_acc_blackscreen_gesture")) {}
    for (;;)
    {
      paramString = Settings.System.getString(paramContext.getContentResolver(), paramString);
      if (!TextUtils.isEmpty(paramString)) {
        break;
      }
      return paramContext.getString(2131690365);
      paramString = getGestureTypebyGestureKey(paramString);
    }
    paramString = paramString.split(";");
    String str = paramString[0];
    if (str.startsWith("OpenApp:")) {
      return str.substring("OpenApp:".length());
    }
    if (str.startsWith("OpenShortcut:"))
    {
      str = str.substring("OpenShortcut:".length());
      if (hasShortCutsId(paramContext, str, paramString[1])) {
        return str;
      }
      return paramContext.getString(2131690365);
    }
    return "";
  }
  
  public static String getGestureSummarybyGestureKey(Context paramContext, String paramString)
  {
    paramContext.getString(2131690365);
    if (paramString.startsWith("oem_acc_blackscreen_gesture")) {}
    for (;;)
    {
      paramString = Settings.System.getString(paramContext.getContentResolver(), paramString);
      if (!TextUtils.isEmpty(paramString)) {
        break;
      }
      return paramContext.getString(2131690365);
      paramString = getGestureTypebyGestureKey(paramString);
    }
    if ("OpenCamera".equals(paramString)) {
      return paramContext.getString(2131690366);
    }
    if ("FrontCamera".equals(paramString)) {
      return paramContext.getString(2131690367);
    }
    if ("TakeVideo".equals(paramString)) {
      return paramContext.getString(2131690368);
    }
    if ("OpenTorch".equals(paramString)) {
      return paramContext.getString(2131690369);
    }
    if ("OpenShelf".equals(paramString)) {
      return paramContext.getString(2131690014);
    }
    String[] arrayOfString = paramString.split(";");
    String str = arrayOfString[0];
    if (str.startsWith("OpenApp:")) {
      paramString = str.substring("OpenApp:".length());
    }
    do
    {
      return getAppNameByPackageName(paramContext, paramString);
      paramString = str;
    } while (!str.startsWith("OpenShortcut:"));
    paramString = str.substring("OpenShortcut:".length());
    str = arrayOfString[1];
    if (!hasShortCutsId(paramContext, paramString, str)) {
      return paramContext.getString(2131690365);
    }
    return getAppNameByPackageName(paramContext, paramString) + "/" + getShortCutsNameByID(paramContext, paramString, str);
  }
  
  public static String getGestureTypebyGestureKey(String paramString)
  {
    String str = "";
    if (paramString.equals("oneplus_draw_o_start_app")) {
      str = "oem_acc_blackscreen_gesture_o";
    }
    do
    {
      return str;
      if (paramString.equals("oneplus_draw_v_start_app")) {
        return "oem_acc_blackscreen_gesture_v";
      }
      if (paramString.equals("oneplus_draw_s_start_app")) {
        return "oem_acc_blackscreen_gesture_s";
      }
      if (paramString.equals("oneplus_draw_m_start_app")) {
        return "oem_acc_blackscreen_gesture_m";
      }
    } while (!paramString.equals("oneplus_draw_w_start_app"));
    return "oem_acc_blackscreen_gesture_w";
  }
  
  public static int getIndexByGestureValueKey(String paramString)
  {
    int i = 0;
    if (paramString.equals("oem_acc_blackscreen_gesture_o")) {
      i = 6;
    }
    do
    {
      return i;
      if (paramString.equals("oem_acc_blackscreen_gesture_v")) {
        return 0;
      }
      if (paramString.equals("oem_acc_blackscreen_gesture_s")) {
        return 8;
      }
      if (paramString.equals("oem_acc_blackscreen_gesture_m")) {
        return 9;
      }
    } while (!paramString.equals("oem_acc_blackscreen_gesture_w"));
    return 10;
  }
  
  public static String getShortCutIdByGestureKey(Context paramContext, String paramString)
  {
    paramContext.getString(2131690365);
    if (paramString.startsWith("oem_acc_blackscreen_gesture")) {}
    for (;;)
    {
      paramString = Settings.System.getString(paramContext.getContentResolver(), paramString);
      if (!TextUtils.isEmpty(paramString)) {
        break;
      }
      return paramContext.getString(2131690365);
      paramString = getGestureTypebyGestureKey(paramString);
    }
    paramContext = paramString.split(";");
    if (paramContext[0].startsWith("OpenShortcut:")) {
      return paramContext[1];
    }
    return "";
  }
  
  public static String getShortCutNameByGestureKey(Context paramContext, String paramString)
  {
    paramContext.getString(2131690365);
    if (paramString.startsWith("oem_acc_blackscreen_gesture")) {}
    for (;;)
    {
      paramString = Settings.System.getString(paramContext.getContentResolver(), paramString);
      if (!TextUtils.isEmpty(paramString)) {
        break;
      }
      return paramContext.getString(2131690365);
      paramString = getGestureTypebyGestureKey(paramString);
    }
    paramString = paramString.split(";");
    Object localObject = paramString[0];
    if (((String)localObject).startsWith("OpenApp:")) {
      return getAppNameByPackageName(paramContext, ((String)localObject).substring("OpenApp:".length()));
    }
    if (((String)localObject).startsWith("OpenShortcut:")) {
      return paramString[1];
    }
    return "";
  }
  
  public static String getShortCutsNameByID(Context paramContext, String paramString1, String paramString2)
  {
    Object localObject = loadShortCuts(paramContext, paramString1);
    paramContext = "";
    paramString1 = paramContext;
    if (localObject != null)
    {
      localObject = ((Iterable)localObject).iterator();
      for (;;)
      {
        paramString1 = paramContext;
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
        paramString1 = (ShortcutInfo)((Iterator)localObject).next();
        if (paramString1.getId().equals(paramString2))
        {
          paramString1 = paramString1.getShortLabel();
          paramContext = paramString1;
          if (TextUtils.isEmpty(paramString1)) {
            paramContext = paramString2;
          }
          paramContext = paramContext.toString();
        }
      }
    }
    return paramString1;
  }
  
  public static boolean hasShortCuts(Context paramContext, String paramString)
  {
    boolean bool = false;
    paramContext = loadShortCuts(paramContext, paramString);
    if (paramContext == null) {
      return false;
    }
    if (paramContext.size() > 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean hasShortCutsGesture(Context paramContext, String paramString)
  {
    paramContext.getString(2131690365);
    if (paramString.startsWith("oem_acc_blackscreen_gesture")) {}
    for (;;)
    {
      paramContext = Settings.System.getString(paramContext.getContentResolver(), paramString);
      if ((TextUtils.isEmpty(paramContext)) || (!paramContext.contains("OpenShortcut:"))) {
        break;
      }
      return true;
      paramString = getGestureTypebyGestureKey(paramString);
    }
    return false;
  }
  
  public static boolean hasShortCutsId(Context paramContext, String paramString1, String paramString2)
  {
    paramContext = loadShortCuts(paramContext, paramString1);
    if (paramContext == null) {
      return false;
    }
    boolean bool2 = false;
    paramContext = paramContext.iterator();
    do
    {
      bool1 = bool2;
      if (!paramContext.hasNext()) {
        break;
      }
    } while (!((ShortcutInfo)paramContext.next()).getId().equals(paramString2));
    boolean bool1 = true;
    return bool1;
  }
  
  public static List<ShortcutInfo> loadShortCuts(Context paramContext, String paramString)
  {
    paramContext = (LauncherApps)paramContext.getSystemService("launcherapps");
    LauncherApps.ShortcutQuery localShortcutQuery = new LauncherApps.ShortcutQuery();
    localShortcutQuery.setQueryFlags(8);
    localShortcutQuery.setPackage(paramString);
    return paramContext.getShortcuts(localShortcutQuery, Process.myUserHandle());
  }
  
  public static int set0(Context paramContext, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      paramInt = 65535;
    }
    for (;;)
    {
      int i = Settings.System.getInt(paramContext.getContentResolver(), "oem_acc_blackscreen_gestrue_enable", 0);
      Settings.System.putInt(paramContext.getContentResolver(), "oem_acc_blackscreen_gestrue_enable", i & paramInt);
      return i & paramInt;
      paramInt = 65534;
      continue;
      paramInt = 65533;
      continue;
      paramInt = 65531;
      continue;
      paramInt = 65527;
      continue;
      paramInt = 65519;
      continue;
      paramInt = 65503;
      continue;
      paramInt = 65471;
      continue;
      paramInt = 65407;
      continue;
      paramInt = 65279;
      continue;
      paramInt = 65023;
      continue;
      paramInt = 64511;
    }
  }
  
  public static int set1(Context paramContext, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      paramInt = 0;
    }
    for (;;)
    {
      int i = Settings.System.getInt(paramContext.getContentResolver(), "oem_acc_blackscreen_gestrue_enable", 0);
      Settings.System.putInt(paramContext.getContentResolver(), "oem_acc_blackscreen_gestrue_enable", i | paramInt);
      return i | paramInt;
      paramInt = 1;
      continue;
      paramInt = 2;
      continue;
      paramInt = 4;
      continue;
      paramInt = 8;
      continue;
      paramInt = 16;
      continue;
      paramInt = 32;
      continue;
      paramInt = 64;
      continue;
      paramInt = 128;
      continue;
      paramInt = 256;
      continue;
      paramInt = 512;
      continue;
      paramInt = 1024;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\gestures\OPGestureUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */