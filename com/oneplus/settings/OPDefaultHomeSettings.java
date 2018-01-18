package com.oneplus.settings;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.Application;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import com.android.settings.SettingsPreferenceFragment;
import com.google.android.collect.Maps;
import com.oneplus.settings.ui.OPPreferenceHeaderMargin;
import com.oneplus.settings.ui.OPRadioButtonPreferenceV7;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class OPDefaultHomeSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener
{
  private static final String NET_ONEPLUS_H2LAUNCHER = "net.oneplus.h2launcher";
  private static final String NET_ONEPLUS_LAUNCHER = "net.oneplus.launcher";
  private final String TAG = "OPDefaultHomeSettings";
  private ActivityManager mActivityManager;
  private final ArrayList<ComponentName> mAllHomeComponents = new ArrayList();
  private ComponentName mCurrentDefaultHome;
  private ArrayList<ResolveInfo> mHomeActivities;
  private final Map<String, ComponentName> mHomeComponentsPreferenceKeyMap = Maps.newHashMap();
  private IntentFilter mHomeFilter;
  private PreferenceScreen mRoot;
  private PackageManager pm;
  
  private void forceStopPackage(String paramString)
  {
    if (this.mActivityManager != null)
    {
      this.mActivityManager.forceStopPackage(paramString);
      Log.d("OPDefaultHomeSettings", "forceStopPackage:" + paramString);
    }
  }
  
  private boolean hasManagedProfile()
  {
    Iterator localIterator = ((UserManager)SettingsBaseApplication.mApplication.getSystemService(UserManager.class)).getProfiles(SettingsBaseApplication.mApplication.getUserId()).iterator();
    while (localIterator.hasNext()) {
      if (((UserInfo)localIterator.next()).isManagedProfile()) {
        return true;
      }
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
  
  private boolean resetDefaultHome(Preference paramPreference)
  {
    Object localObject1 = paramPreference.getKey();
    localObject1 = (ComponentName)this.mHomeComponentsPreferenceKeyMap.get(localObject1);
    if (localObject1 == null)
    {
      Log.d("OPDefaultHomeSettings", "set default home component error ,component is null .");
      return false;
    }
    Object localObject2 = (ComponentName[])this.mAllHomeComponents.toArray(new ComponentName[0]);
    SettingsBaseApplication.mApplication.getPackageManager().replacePreferredActivity(this.mHomeFilter, 1048576, (ComponentName[])localObject2, (ComponentName)localObject1);
    try
    {
      AppGlobals.getPackageManager().setPackageStoppedState(((ComponentName)localObject1).getPackageName(), true, UserHandle.myUserId());
      localObject1 = this.mHomeComponentsPreferenceKeyMap.keySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        ((OPRadioButtonPreferenceV7)this.mRoot.findPreference((CharSequence)localObject2)).setChecked(false);
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;)
      {
        localIllegalArgumentException.printStackTrace();
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        localRemoteException.printStackTrace();
      }
      ((OPRadioButtonPreferenceV7)paramPreference).setChecked(true);
    }
    try
    {
      paramPreference = this.mCurrentDefaultHome.getPackageName();
      refreshHomeOptions();
      if (!paramPreference.equals(this.mCurrentDefaultHome.getPackageName())) {
        forceStopPackage(paramPreference);
      }
      return true;
    }
    catch (Exception paramPreference)
    {
      for (;;)
      {
        Log.e("OPDefaultHomeSettings", "kill old default launcher maybe not success.");
        paramPreference.printStackTrace();
      }
    }
  }
  
  private boolean versionNumberAtLeastL(int paramInt)
  {
    return paramInt >= 21;
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.pm = SettingsBaseApplication.mApplication.getPackageManager();
    this.mActivityManager = ((ActivityManager)SettingsBaseApplication.mApplication.getSystemService("activity"));
    this.mHomeFilter = new IntentFilter("android.intent.action.MAIN");
    this.mHomeFilter.addCategory("android.intent.category.HOME");
    this.mHomeFilter.addCategory("android.intent.category.DEFAULT");
    refreshHomeOptions();
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if ((paramPreference instanceof OPRadioButtonPreferenceV7)) {
      return resetDefaultHome(paramPreference);
    }
    return false;
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    addPreferencesFromResource(2131230790);
    ((OPPreferenceHeaderMargin)findPreference("preference_divider_line")).setOrder(-1);
    this.mRoot = getPreferenceScreen();
    paramView = SettingsBaseApplication.mApplication.getPackageName();
    int i = 0;
    if (i < this.mAllHomeComponents.size())
    {
      paramBundle = (ComponentName)this.mAllHomeComponents.get(i);
      if (paramBundle.getPackageName().equals(paramView)) {
        Log.d("OPDefaultHomeSettings", "homeComponent pkg equal with mypkg" + paramView);
      }
      for (;;)
      {
        i += 1;
        break;
        localObject = ((ResolveInfo)this.mHomeActivities.get(i)).activityInfo;
        if (localObject != null) {
          break label164;
        }
        Log.d("OPDefaultHomeSettings", "appInfo is null for current homeComponent pkg:" + paramBundle.getPackageName());
      }
      label164:
      String str1 = ((ActivityInfo)localObject).packageName;
      CharSequence localCharSequence = ((ActivityInfo)localObject).loadLabel(this.pm);
      Object localObject = ((ActivityInfo)localObject).loadIcon(this.pm);
      OPRadioButtonPreferenceV7 localOPRadioButtonPreferenceV7 = new OPRadioButtonPreferenceV7(getContext());
      String str2 = "key_" + str1;
      this.mHomeComponentsPreferenceKeyMap.put(str2, paramBundle);
      localOPRadioButtonPreferenceV7.setKey(str2);
      localOPRadioButtonPreferenceV7.setIcon((Drawable)localObject);
      localOPRadioButtonPreferenceV7.setTitle(localCharSequence);
      boolean bool;
      if (this.mCurrentDefaultHome != null)
      {
        bool = this.mCurrentDefaultHome.getPackageName().equals(str1);
        label284:
        localOPRadioButtonPreferenceV7.setChecked(bool);
        localOPRadioButtonPreferenceV7.setOnPreferenceClickListener(this);
        if (!"net.oneplus.launcher".equals(str1)) {
          break label373;
        }
        localOPRadioButtonPreferenceV7.setOrder(0);
      }
      for (;;)
      {
        bool = this.mRoot.addPreference(localOPRadioButtonPreferenceV7);
        Log.d("OPDefaultHomeSettings", "mRoot.addPreference:" + bool + ",mRoot=" + this.mRoot);
        break;
        bool = false;
        break label284;
        label373:
        if ("net.oneplus.h2launcher".equals(str1)) {
          localOPRadioButtonPreferenceV7.setOrder(1);
        }
      }
    }
  }
  
  public void refreshHomeOptions()
  {
    this.mHomeActivities = new ArrayList();
    this.mCurrentDefaultHome = this.pm.getHomeActivities(this.mHomeActivities);
    this.mAllHomeComponents.clear();
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < this.mHomeActivities.size())
    {
      ActivityInfo localActivityInfo = ((ResolveInfo)this.mHomeActivities.get(i)).activityInfo;
      ComponentName localComponentName = new ComponentName(localActivityInfo.packageName, localActivityInfo.name);
      this.mAllHomeComponents.add(localComponentName);
      localStringBuilder.append(localActivityInfo.packageName).append(",");
      i += 1;
    }
    Log.d("OPDefaultHomeSettings", "getHomeComponents count:" + this.mHomeActivities.size() + ", pkgs:" + localStringBuilder.toString());
    if ((this.mCurrentDefaultHome == null) && (this.mAllHomeComponents.size() == 1)) {
      this.mCurrentDefaultHome = ((ComponentName)this.mAllHomeComponents.get(0));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPDefaultHomeSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */