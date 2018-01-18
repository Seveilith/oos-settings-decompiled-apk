package com.android.settings.applications;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.os.UserManager;
import android.util.AttributeSet;
import com.android.settings.AppListPreference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultHomePreference
  extends AppListPreference
{
  public static final String DEFAULT_LAUNCHER_0 = "com.oneplus.hydrogen.launcher";
  public static final String DEFAULT_LAUNCHER_1 = "net.oneplus.h2launcher";
  public static final String DEFAULT_LAUNCHER_2 = "net.oneplus.launcher";
  private final ArrayList<ComponentName> mAllHomeComponents = new ArrayList();
  private final IntentFilter mHomeFilter = new IntentFilter("android.intent.action.MAIN");
  
  public DefaultHomePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mHomeFilter.addCategory("android.intent.category.HOME");
    this.mHomeFilter.addCategory("android.intent.category.DEFAULT");
    refreshHomeOptions();
  }
  
  public static String getCurrentDefaultHome(Context paramContext)
  {
    ArrayList localArrayList = new ArrayList();
    paramContext = paramContext.getPackageManager().getHomeActivities(localArrayList);
    if (paramContext == null) {
      return null;
    }
    return paramContext.getPackageName();
  }
  
  public static boolean hasHomePreference(String paramString, Context paramContext)
  {
    ArrayList localArrayList = new ArrayList();
    paramContext.getPackageManager().getHomeActivities(localArrayList);
    int i = 0;
    while (i < localArrayList.size())
    {
      if (((ResolveInfo)localArrayList.get(i)).activityInfo.packageName.equals(paramString)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private boolean hasManagedProfile()
  {
    Iterator localIterator = ((UserManager)getContext().getSystemService(UserManager.class)).getProfiles(getContext().getUserId()).iterator();
    while (localIterator.hasNext()) {
      if (((UserInfo)localIterator.next()).isManagedProfile()) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isHomeDefault(String paramString, Context paramContext)
  {
    ArrayList localArrayList = new ArrayList();
    paramContext = paramContext.getPackageManager().getHomeActivities(localArrayList);
    if (paramContext != null) {
      return paramContext.getPackageName().equals(paramString);
    }
    return false;
  }
  
  private boolean launcherHasManagedProfilesFeature(ResolveInfo paramResolveInfo, PackageManager paramPackageManager)
  {
    try
    {
      boolean bool = versionNumberAtLeastL(paramPackageManager.getApplicationInfo(paramResolveInfo.activityInfo.packageName, 0).targetSdkVersion);
      return bool;
    }
    catch (PackageManager.NameNotFoundException paramResolveInfo) {}
    return false;
  }
  
  private boolean versionNumberAtLeastL(int paramInt)
  {
    return paramInt >= 21;
  }
  
  public void performClick()
  {
    refreshHomeOptions();
    super.performClick();
  }
  
  protected boolean persistString(String paramString)
  {
    if (paramString != null)
    {
      ComponentName localComponentName = ComponentName.unflattenFromString(paramString);
      getContext().getPackageManager().replacePreferredActivity(this.mHomeFilter, 1048576, (ComponentName[])this.mAllHomeComponents.toArray(new ComponentName[0]), localComponentName);
      setSummary(getEntry());
    }
    return super.persistString(paramString);
  }
  
  public void refreshHomeOptions()
  {
    Object localObject = getContext().getPackageName();
    ArrayList localArrayList3 = new ArrayList();
    PackageManager localPackageManager = getContext().getPackageManager();
    ComponentName localComponentName1 = localPackageManager.getHomeActivities(localArrayList3);
    ArrayList localArrayList1 = new ArrayList();
    this.mAllHomeComponents.clear();
    ArrayList localArrayList2 = new ArrayList();
    boolean bool = hasManagedProfile();
    int i = 0;
    if (i < localArrayList3.size())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localArrayList3.get(i);
      ActivityInfo localActivityInfo = localResolveInfo.activityInfo;
      ComponentName localComponentName2 = new ComponentName(localActivityInfo.packageName, localActivityInfo.name);
      this.mAllHomeComponents.add(localComponentName2);
      if (localActivityInfo.packageName.equals(localObject)) {}
      for (;;)
      {
        i += 1;
        break;
        localArrayList1.add(localComponentName2);
        if ((!bool) || (launcherHasManagedProfilesFeature(localResolveInfo, localPackageManager))) {
          localArrayList2.add(null);
        } else {
          localArrayList2.add(getContext().getString(2131691558));
        }
      }
    }
    localObject = localComponentName1;
    if (localComponentName1 == null)
    {
      localObject = localComponentName1;
      if (localArrayList1 != null)
      {
        localObject = localComponentName1;
        if (localArrayList1.size() == 1) {
          localObject = (ComponentName)localArrayList1.get(0);
        }
      }
    }
    setComponentNames((ComponentName[])localArrayList1.toArray(new ComponentName[0]), (ComponentName)localObject, (CharSequence[])localArrayList2.toArray(new CharSequence[0]));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\DefaultHomePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */