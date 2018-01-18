package com.android.settingslib.drawer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Pair;
import com.android.settingslib.R.drawable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TileUtils
{
  private static final Comparator<DashboardCategory> CATEGORY_COMPARATOR = new Comparator()
  {
    public int compare(DashboardCategory paramAnonymousDashboardCategory1, DashboardCategory paramAnonymousDashboardCategory2)
    {
      return paramAnonymousDashboardCategory2.priority - paramAnonymousDashboardCategory1.priority;
    }
  };
  private static final boolean DEBUG = false;
  private static final boolean DEBUG_TIMING = false;
  private static final String EXTRA_CATEGORY_KEY = "com.android.settings.category";
  private static final String EXTRA_SETTINGS_ACTION = "com.android.settings.action.EXTRA_SETTINGS";
  private static final String FRAGMENT_KEY = "com.android.settings.FRAGMENT_CLASS";
  private static final String GOOGLE_PACKAGE_NAME = "com.google.android.gms";
  private static final String LOG_TAG = "TileUtils";
  private static final String MANUFACTURER_DEFAULT_CATEGORY = "com.android.settings.category.device";
  private static final String MANUFACTURER_SETTINGS = "com.android.settings.MANUFACTURER_APPLICATION_SETTING";
  public static final String META_DATA_PREFERENCE_ICON = "com.android.settings.icon";
  public static final String META_DATA_PREFERENCE_SUMMARY = "com.android.settings.summary";
  public static final String META_DATA_PREFERENCE_TITLE = "com.android.settings.title";
  private static final String ONEPLUS_SETUPWIZARD = "com.oneplus.setupwizard";
  private static final String OPERATOR_DEFAULT_CATEGORY = "com.android.settings.category.wireless";
  private static final String OPERATOR_SETTINGS = "com.android.settings.OPERATOR_APPLICATION_SETTING";
  private static final String PRIVACYSETTINGS_VALUE = "com.android.settings.PrivacySettings";
  private static final String SETTINGS_ACTION = "com.android.settings.action.SETTINGS";
  private static final String SETTINGS_CUSTOM_CATAGORY = "com.android.settings.category.custom";
  private static final String SETTING_PKG = "com.android.settings";
  public static final Comparator<Tile> TILE_COMPARATOR = new Comparator()
  {
    public int compare(Tile paramAnonymousTile1, Tile paramAnonymousTile2)
    {
      return paramAnonymousTile2.priority - paramAnonymousTile1.priority;
    }
  };
  
  private static DashboardCategory createCategory(Context paramContext, String paramString)
  {
    DashboardCategory localDashboardCategory = new DashboardCategory();
    localDashboardCategory.key = paramString;
    paramContext = paramContext.getPackageManager();
    paramString = paramContext.queryIntentActivities(new Intent(paramString), 0);
    if (paramString.size() == 0) {
      return null;
    }
    paramString = paramString.iterator();
    while (paramString.hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)paramString.next();
      if (localResolveInfo.system)
      {
        localDashboardCategory.title = localResolveInfo.activityInfo.loadLabel(paramContext);
        if ("com.android.settings".equals(localResolveInfo.activityInfo.applicationInfo.packageName)) {}
        for (int i = localResolveInfo.priority;; i = 0)
        {
          localDashboardCategory.priority = i;
          break;
        }
      }
    }
    return localDashboardCategory;
  }
  
  public static List<DashboardCategory> getCategories(Context paramContext, HashMap<Pair<String, String>, Tile> paramHashMap)
  {
    System.currentTimeMillis();
    if (Settings.Global.getInt(paramContext.getContentResolver(), "device_provisioned", 0) != 0) {}
    Object localObject1;
    for (int i = 1;; i = 0)
    {
      localObject1 = new ArrayList();
      localObject2 = UserManager.get(paramContext).getUserProfiles().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (UserHandle)((Iterator)localObject2).next();
        if (((UserHandle)localObject3).getIdentifier() == ActivityManager.getCurrentUser())
        {
          getTilesForAction(paramContext, (UserHandle)localObject3, "com.android.settings.action.SETTINGS", paramHashMap, null, (ArrayList)localObject1, true);
          getTilesForAction(paramContext, (UserHandle)localObject3, "com.android.settings.category.custom", paramHashMap, null, (ArrayList)localObject1, true);
          getTilesForAction(paramContext, (UserHandle)localObject3, "com.android.settings.OPERATOR_APPLICATION_SETTING", paramHashMap, "com.android.settings.category.wireless", (ArrayList)localObject1, false);
          getTilesForAction(paramContext, (UserHandle)localObject3, "com.android.settings.MANUFACTURER_APPLICATION_SETTING", paramHashMap, "com.android.settings.category.device", (ArrayList)localObject1, false);
        }
        if (i != 0) {
          getTilesForAction(paramContext, (UserHandle)localObject3, "com.android.settings.action.EXTRA_SETTINGS", paramHashMap, null, (ArrayList)localObject1, false);
        }
      }
    }
    Object localObject2 = new HashMap();
    Object localObject3 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject3).hasNext())
    {
      Tile localTile = (Tile)((Iterator)localObject3).next();
      localObject1 = (DashboardCategory)((HashMap)localObject2).get(localTile.category);
      paramHashMap = (HashMap<Pair<String, String>, Tile>)localObject1;
      if (localObject1 == null)
      {
        paramHashMap = createCategory(paramContext, localTile.category);
        if (paramHashMap == null) {
          Log.w("TileUtils", "Couldn't find category " + localTile.category);
        } else {
          ((HashMap)localObject2).put(paramHashMap.key, paramHashMap);
        }
      }
      else
      {
        paramHashMap.addTile(localTile);
      }
    }
    paramContext = new ArrayList(((HashMap)localObject2).values());
    paramHashMap = paramContext.iterator();
    while (paramHashMap.hasNext()) {
      Collections.sort(((DashboardCategory)paramHashMap.next()).tiles, TILE_COMPARATOR);
    }
    Collections.sort(paramContext, CATEGORY_COMPARATOR);
    return paramContext;
  }
  
  private static DashboardCategory getCategory(List<DashboardCategory> paramList, String paramString)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      DashboardCategory localDashboardCategory = (DashboardCategory)paramList.next();
      if (paramString.equals(localDashboardCategory.key)) {
        return localDashboardCategory;
      }
    }
    return null;
  }
  
  private static void getTilesForAction(Context paramContext, UserHandle paramUserHandle, String paramString1, Map<Pair<String, String>, Tile> paramMap, String paramString2, ArrayList<Tile> paramArrayList, boolean paramBoolean)
  {
    paramString1 = new Intent(paramString1);
    if (paramBoolean) {
      paramString1.setPackage("com.android.settings");
    }
    getTilesForIntent(paramContext, paramUserHandle, paramString1, paramMap, paramString2, paramArrayList, paramBoolean, true);
  }
  
  public static void getTilesForIntent(Context paramContext, UserHandle paramUserHandle, Intent paramIntent, Map<Pair<String, String>, Tile> paramMap, String paramString, List<Tile> paramList, boolean paramBoolean1, boolean paramBoolean2)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    Iterator localIterator = localPackageManager.queryIntentActivitiesAsUser(paramIntent, 128, paramUserHandle.getIdentifier()).iterator();
    while (localIterator.hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
      if ((!"com.oneplus.setupwizard".equals(localResolveInfo.activityInfo.packageName)) && (localResolveInfo.system))
      {
        ActivityInfo localActivityInfo = localResolveInfo.activityInfo;
        Object localObject1 = localActivityInfo.metaData;
        label106:
        Pair localPair;
        Object localObject2;
        if ((!paramBoolean2) || ((localObject1 != null) && (((Bundle)localObject1).containsKey("com.android.settings.category"))))
        {
          String str = ((Bundle)localObject1).getString("com.android.settings.category");
          localPair = new Pair(localActivityInfo.packageName, localActivityInfo.name);
          localObject2 = (Tile)paramMap.get(localPair);
          localObject1 = localObject2;
          if (localObject2 == null)
          {
            localObject1 = new Tile();
            ((Tile)localObject1).intent = new Intent().setClassName(localActivityInfo.packageName, localActivityInfo.name);
            ((Tile)localObject1).category = str;
            if (!paramBoolean1) {
              break label383;
            }
          }
        }
        label383:
        for (int i = localResolveInfo.priority;; i = 0)
        {
          ((Tile)localObject1).priority = i;
          ((Tile)localObject1).metaData = localActivityInfo.metaData;
          updateTileData(paramContext, (Tile)localObject1, localActivityInfo, localActivityInfo.applicationInfo, localPackageManager);
          paramMap.put(localPair, localObject1);
          if (!((Tile)localObject1).userHandle.contains(paramUserHandle)) {
            ((Tile)localObject1).userHandle.add(paramUserHandle);
          }
          if (paramList.contains(localObject1)) {
            break;
          }
          paramList.add(localObject1);
          break;
          if (paramString != null) {
            break label106;
          }
          localObject2 = new StringBuilder().append("Found ").append(localResolveInfo.activityInfo.name).append(" for intent ").append(paramIntent).append(" missing metadata ");
          if (localObject1 == null) {}
          for (localObject1 = "";; localObject1 = "com.android.settings.category")
          {
            Log.w("TileUtils", (String)localObject1);
            break;
          }
        }
      }
    }
  }
  
  private static boolean updateTileData(Context paramContext, Tile paramTile, ActivityInfo paramActivityInfo, ApplicationInfo paramApplicationInfo, PackageManager paramPackageManager)
  {
    int n;
    int m;
    Object localObject9;
    Object localObject8;
    Object localObject10;
    Object localObject7;
    String str1;
    if (paramApplicationInfo.isSystemApp())
    {
      n = 0;
      m = 0;
      j = 0;
      localObject9 = null;
      localObject8 = null;
      localObject1 = null;
      localObject10 = null;
      localObject7 = null;
      str1 = null;
      localObject5 = null;
      k = m;
      localObject2 = localObject5;
      localObject3 = localObject7;
      localObject4 = localObject8;
    }
    try
    {
      Resources localResources = paramPackageManager.getResourcesForApplication(paramApplicationInfo.packageName);
      k = m;
      localObject2 = localObject5;
      localObject3 = localObject7;
      localObject4 = localObject8;
      localBundle = paramActivityInfo.metaData;
      k = m;
      localObject2 = localObject5;
      localObject3 = localObject7;
      localObject4 = localObject8;
      str2 = (String)localBundle.get("com.android.settings.FRAGMENT_CLASS");
      i = n;
      localObject6 = str2;
      localObject5 = localObject10;
      paramApplicationInfo = (ApplicationInfo)localObject9;
      if (localResources != null)
      {
        i = n;
        localObject6 = str2;
        localObject5 = localObject10;
        paramApplicationInfo = (ApplicationInfo)localObject9;
        if (localBundle != null)
        {
          k = m;
          localObject2 = str2;
          localObject3 = localObject7;
          localObject4 = localObject8;
          if (localBundle.containsKey("com.android.settings.icon"))
          {
            k = m;
            localObject2 = str2;
            localObject3 = localObject7;
            localObject4 = localObject8;
            j = localBundle.getInt("com.android.settings.icon");
          }
          k = j;
          localObject2 = str2;
          localObject3 = localObject7;
          localObject4 = localObject8;
          if (localBundle.containsKey("com.android.settings.title"))
          {
            k = j;
            localObject2 = str2;
            localObject3 = localObject7;
            localObject4 = localObject8;
            if (!(localBundle.get("com.android.settings.title") instanceof Integer)) {
              break label586;
            }
            k = j;
            localObject2 = str2;
            localObject3 = localObject7;
            localObject4 = localObject8;
            localObject1 = localResources.getString(localBundle.getInt("com.android.settings.title"));
          }
          k = j;
          localObject2 = str2;
          localObject3 = localObject7;
          localObject4 = localObject1;
          if (localBundle.containsKey("com.android.settings.summary"))
          {
            k = j;
            localObject2 = str2;
            localObject3 = localObject7;
            localObject4 = localObject1;
            if (!(localBundle.get("com.android.settings.summary") instanceof Integer)) {
              break label614;
            }
            k = j;
            localObject2 = str2;
            localObject3 = localObject7;
            localObject4 = localObject1;
            str1 = localResources.getString(localBundle.getInt("com.android.settings.summary"));
          }
          label375:
          i = j;
          localObject6 = str2;
          localObject5 = str1;
          paramApplicationInfo = (ApplicationInfo)localObject1;
          k = j;
          localObject2 = str2;
          localObject3 = str1;
          localObject4 = localObject1;
          if ("com.android.settings.PrivacySettings".equals(str2))
          {
            k = j;
            localObject2 = str2;
            localObject3 = str1;
            localObject4 = localObject1;
            boolean bool = OpFeatures.isSupport(new int[] { 1 });
            if (!bool) {
              break label642;
            }
            paramApplicationInfo = (ApplicationInfo)localObject1;
            localObject5 = str1;
            localObject6 = str2;
            i = j;
          }
        }
      }
    }
    catch (PackageManager.NameNotFoundException|Resources.NotFoundException paramApplicationInfo)
    {
      for (;;)
      {
        Bundle localBundle;
        String str2;
        label464:
        int i = k;
        Object localObject6 = localObject2;
        localObject5 = localObject3;
        paramApplicationInfo = (ApplicationInfo)localObject4;
      }
    }
    Object localObject1 = paramApplicationInfo;
    if (TextUtils.isEmpty(paramApplicationInfo)) {
      localObject1 = paramActivityInfo.loadLabel(paramPackageManager).toString();
    }
    int j = i;
    if (i == 0) {
      j = paramActivityInfo.icon;
    }
    if (!"com.google.android.gms".equals(paramActivityInfo.packageName))
    {
      paramTile.icon = Icon.createWithResource(paramActivityInfo.packageName, j);
      label527:
      if ("com.android.settings.PrivacySettings".equals(localObject6)) {
        if (!OpFeatures.isSupport(new int[] { 1 })) {
          break label695;
        }
      }
    }
    for (;;)
    {
      paramTile.title = ((CharSequence)localObject1);
      paramTile.summary = ((CharSequence)localObject5);
      paramTile.intent = new Intent().setClassName(paramActivityInfo.packageName, paramActivityInfo.name);
      return true;
      label586:
      k = j;
      localObject2 = str2;
      localObject3 = localObject7;
      localObject4 = localObject8;
      localObject1 = localBundle.getString("com.android.settings.title");
      break;
      label614:
      k = j;
      localObject2 = str2;
      localObject3 = localObject7;
      localObject4 = localObject1;
      str1 = localBundle.getString("com.android.settings.summary");
      break label375;
      label642:
      k = j;
      localObject2 = str2;
      localObject3 = str1;
      localObject4 = localObject1;
      paramApplicationInfo = paramContext.getText(17039663);
      i = j;
      localObject6 = str2;
      localObject5 = str1;
      break label464;
      paramTile.icon = Icon.createWithResource(paramContext, R.drawable.op_ic_google);
      break label527;
      label695:
      paramTile.icon = Icon.createWithResource(paramContext, R.drawable.op_ic_settings_factory_reset);
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\drawer\TileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */