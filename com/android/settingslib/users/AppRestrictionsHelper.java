package com.android.settingslib.users;

import android.app.AppGlobals;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class AppRestrictionsHelper
{
  private static final boolean DEBUG = false;
  private static final String TAG = "AppRestrictionsHelper";
  private final Context mContext;
  private final IPackageManager mIPm;
  private boolean mLeanback;
  private final PackageManager mPackageManager;
  private final boolean mRestrictedProfile;
  HashMap<String, Boolean> mSelectedPackages = new HashMap();
  private final UserHandle mUser;
  private final UserManager mUserManager;
  private List<SelectableAppInfo> mVisibleApps;
  
  public AppRestrictionsHelper(Context paramContext, UserHandle paramUserHandle)
  {
    this.mContext = paramContext;
    this.mPackageManager = paramContext.getPackageManager();
    this.mIPm = AppGlobals.getPackageManager();
    this.mUser = paramUserHandle;
    this.mUserManager = ((UserManager)paramContext.getSystemService("user"));
    this.mRestrictedProfile = this.mUserManager.getUserInfo(this.mUser.getIdentifier()).isRestricted();
  }
  
  private void addSystemApps(List<SelectableAppInfo> paramList, Intent paramIntent, Set<String> paramSet)
  {
    PackageManager localPackageManager = this.mPackageManager;
    paramIntent = localPackageManager.queryIntentActivities(paramIntent, 8704).iterator();
    while (paramIntent.hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)paramIntent.next();
      if ((localResolveInfo.activityInfo != null) && (localResolveInfo.activityInfo.applicationInfo != null))
      {
        Object localObject = localResolveInfo.activityInfo.packageName;
        int i = localResolveInfo.activityInfo.applicationInfo.flags;
        if ((((i & 0x1) != 0) || ((i & 0x80) != 0)) && (!paramSet.contains(localObject)))
        {
          i = localPackageManager.getApplicationEnabledSetting((String)localObject);
          if ((i == 4) || (i == 2))
          {
            localObject = getAppInfoForUser((String)localObject, 0, this.mUser);
            if ((localObject == null) || ((((ApplicationInfo)localObject).flags & 0x800000) == 0)) {}
          }
          else
          {
            localObject = new SelectableAppInfo();
            ((SelectableAppInfo)localObject).packageName = localResolveInfo.activityInfo.packageName;
            ((SelectableAppInfo)localObject).appName = localResolveInfo.activityInfo.applicationInfo.loadLabel(localPackageManager);
            ((SelectableAppInfo)localObject).icon = localResolveInfo.activityInfo.loadIcon(localPackageManager);
            ((SelectableAppInfo)localObject).activityName = localResolveInfo.activityInfo.loadLabel(localPackageManager);
            if (((SelectableAppInfo)localObject).activityName == null) {
              ((SelectableAppInfo)localObject).activityName = ((SelectableAppInfo)localObject).appName;
            }
            paramList.add(localObject);
          }
        }
      }
    }
  }
  
  private void addSystemImes(Set<String> paramSet)
  {
    Iterator localIterator = ((InputMethodManager)this.mContext.getSystemService("input_method")).getInputMethodList().iterator();
    while (localIterator.hasNext())
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator.next();
      try
      {
        if ((localInputMethodInfo.isDefault(this.mContext)) && (isSystemPackage(localInputMethodInfo.getPackageName()))) {
          paramSet.add(localInputMethodInfo.getPackageName());
        }
      }
      catch (Resources.NotFoundException localNotFoundException) {}
    }
  }
  
  private ApplicationInfo getAppInfoForUser(String paramString, int paramInt, UserHandle paramUserHandle)
  {
    try
    {
      paramString = this.mIPm.getApplicationInfo(paramString, paramInt, paramUserHandle.getIdentifier());
      return paramString;
    }
    catch (RemoteException paramString) {}
    return null;
  }
  
  private boolean isSystemPackage(String paramString)
  {
    try
    {
      paramString = this.mPackageManager.getPackageInfo(paramString, 0);
      if (paramString.applicationInfo == null) {
        return false;
      }
      int i = paramString.applicationInfo.flags;
      if (((i & 0x1) != 0) || ((i & 0x80) != 0)) {
        return true;
      }
    }
    catch (PackageManager.NameNotFoundException paramString) {}
    return false;
  }
  
  public void applyUserAppState(String paramString, boolean paramBoolean, OnDisableUiForPackageListener paramOnDisableUiForPackageListener)
  {
    int i = this.mUser.getIdentifier();
    if (paramBoolean) {}
    try
    {
      ApplicationInfo localApplicationInfo = this.mIPm.getApplicationInfo(paramString, 8192, i);
      if ((localApplicationInfo == null) || (!localApplicationInfo.enabled) || ((localApplicationInfo.flags & 0x800000) == 0)) {
        this.mIPm.installExistingPackageAsUser(paramString, this.mUser.getIdentifier());
      }
      if ((localApplicationInfo != null) && ((localApplicationInfo.privateFlags & 0x1) != 0) && ((localApplicationInfo.flags & 0x800000) != 0))
      {
        paramOnDisableUiForPackageListener.onDisableUiForPackage(paramString);
        this.mIPm.setApplicationHiddenSettingAsUser(paramString, false, i);
      }
      return;
    }
    catch (RemoteException paramString)
    {
      try
      {
        while (this.mIPm.getApplicationInfo(paramString, 0, i) == null) {}
        if (this.mRestrictedProfile)
        {
          this.mIPm.deletePackageAsUser(paramString, null, this.mUser.getIdentifier(), 4);
          return;
        }
        paramOnDisableUiForPackageListener.onDisableUiForPackage(paramString);
        this.mIPm.setApplicationHiddenSettingAsUser(paramString, true, i);
        return;
      }
      catch (RemoteException paramString) {}
      paramString = paramString;
      return;
    }
  }
  
  public void applyUserAppsStates(OnDisableUiForPackageListener paramOnDisableUiForPackageListener)
  {
    int i = this.mUser.getIdentifier();
    if ((!this.mUserManager.getUserInfo(i).isRestricted()) && (i != UserHandle.myUserId()))
    {
      Log.e("AppRestrictionsHelper", "Cannot apply application restrictions on another user!");
      return;
    }
    Iterator localIterator = this.mSelectedPackages.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      applyUserAppState((String)localEntry.getKey(), ((Boolean)localEntry.getValue()).booleanValue(), paramOnDisableUiForPackageListener);
    }
  }
  
  public void fetchAndMergeApps()
  {
    this.mVisibleApps = new ArrayList();
    Object localObject4 = this.mPackageManager;
    Object localObject1 = this.mIPm;
    localObject3 = new HashSet();
    addSystemImes((Set)localObject3);
    Object localObject5 = new Intent("android.intent.action.MAIN");
    if (this.mLeanback)
    {
      ((Intent)localObject5).addCategory("android.intent.category.LEANBACK_LAUNCHER");
      addSystemApps(this.mVisibleApps, (Intent)localObject5, (Set)localObject3);
      localObject5 = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
      addSystemApps(this.mVisibleApps, (Intent)localObject5, (Set)localObject3);
      localObject3 = ((PackageManager)localObject4).getInstalledApplications(8192).iterator();
    }
    for (;;)
    {
      if (!((Iterator)localObject3).hasNext()) {
        break label305;
      }
      localObject5 = (ApplicationInfo)((Iterator)localObject3).next();
      if ((((ApplicationInfo)localObject5).flags & 0x800000) != 0)
      {
        Object localObject7;
        if (((((ApplicationInfo)localObject5).flags & 0x1) == 0) && ((((ApplicationInfo)localObject5).flags & 0x80) == 0))
        {
          localObject7 = new SelectableAppInfo();
          ((SelectableAppInfo)localObject7).packageName = ((ApplicationInfo)localObject5).packageName;
          ((SelectableAppInfo)localObject7).appName = ((ApplicationInfo)localObject5).loadLabel((PackageManager)localObject4);
          ((SelectableAppInfo)localObject7).activityName = ((SelectableAppInfo)localObject7).appName;
          ((SelectableAppInfo)localObject7).icon = ((ApplicationInfo)localObject5).loadIcon((PackageManager)localObject4);
          this.mVisibleApps.add(localObject7);
          continue;
          ((Intent)localObject5).addCategory("android.intent.category.LAUNCHER");
          break;
        }
        try
        {
          localObject7 = ((PackageManager)localObject4).getPackageInfo(((ApplicationInfo)localObject5).packageName, 0);
          if ((this.mRestrictedProfile) && (((PackageInfo)localObject7).requiredAccountType != null) && (((PackageInfo)localObject7).restrictedAccountType == null)) {
            this.mSelectedPackages.put(((ApplicationInfo)localObject5).packageName, Boolean.valueOf(false));
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
      }
    }
    label305:
    localObject3 = null;
    try
    {
      localObject6 = ((IPackageManager)localObject1).getInstalledApplications(8192, this.mUser.getIdentifier());
      localObject1 = localObject3;
      if (localObject6 != null) {
        localObject1 = ((ParceledListSlice)localObject6).getList();
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Object localObject6;
        int i;
        label568:
        Object localObject2 = localObject3;
      }
    }
    if (localObject1 != null)
    {
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject3 = (ApplicationInfo)((Iterator)localObject1).next();
        if (((((ApplicationInfo)localObject3).flags & 0x800000) != 0) && ((((ApplicationInfo)localObject3).flags & 0x1) == 0) && ((((ApplicationInfo)localObject3).flags & 0x80) == 0))
        {
          localObject6 = new SelectableAppInfo();
          ((SelectableAppInfo)localObject6).packageName = ((ApplicationInfo)localObject3).packageName;
          ((SelectableAppInfo)localObject6).appName = ((ApplicationInfo)localObject3).loadLabel((PackageManager)localObject4);
          ((SelectableAppInfo)localObject6).activityName = ((SelectableAppInfo)localObject6).appName;
          ((SelectableAppInfo)localObject6).icon = ((ApplicationInfo)localObject3).loadIcon((PackageManager)localObject4);
          this.mVisibleApps.add(localObject6);
        }
      }
    }
    Collections.sort(this.mVisibleApps, new AppLabelComparator(null));
    localObject1 = new HashSet();
    i = this.mVisibleApps.size() - 1;
    if (i >= 0)
    {
      localObject3 = (SelectableAppInfo)this.mVisibleApps.get(i);
      localObject4 = ((SelectableAppInfo)localObject3).packageName + "+" + ((SelectableAppInfo)localObject3).activityName;
      if ((TextUtils.isEmpty(((SelectableAppInfo)localObject3).packageName)) || (TextUtils.isEmpty(((SelectableAppInfo)localObject3).activityName))) {
        ((Set)localObject1).add(localObject4);
      }
      for (;;)
      {
        i -= 1;
        break;
        if (!((Set)localObject1).contains(localObject4)) {
          break label568;
        }
        this.mVisibleApps.remove(i);
      }
    }
    localObject1 = new HashMap();
    localObject3 = this.mVisibleApps.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (SelectableAppInfo)((Iterator)localObject3).next();
      if (((HashMap)localObject1).containsKey(((SelectableAppInfo)localObject4).packageName)) {
        ((SelectableAppInfo)localObject4).masterEntry = ((SelectableAppInfo)((HashMap)localObject1).get(((SelectableAppInfo)localObject4).packageName));
      } else {
        ((HashMap)localObject1).put(((SelectableAppInfo)localObject4).packageName, localObject4);
      }
    }
  }
  
  public List<SelectableAppInfo> getVisibleApps()
  {
    return this.mVisibleApps;
  }
  
  public boolean isPackageSelected(String paramString)
  {
    return ((Boolean)this.mSelectedPackages.get(paramString)).booleanValue();
  }
  
  public void setLeanback(boolean paramBoolean)
  {
    this.mLeanback = paramBoolean;
  }
  
  public void setPackageSelected(String paramString, boolean paramBoolean)
  {
    this.mSelectedPackages.put(paramString, Boolean.valueOf(paramBoolean));
  }
  
  private static class AppLabelComparator
    implements Comparator<AppRestrictionsHelper.SelectableAppInfo>
  {
    public int compare(AppRestrictionsHelper.SelectableAppInfo paramSelectableAppInfo1, AppRestrictionsHelper.SelectableAppInfo paramSelectableAppInfo2)
    {
      paramSelectableAppInfo1 = paramSelectableAppInfo1.activityName.toString();
      paramSelectableAppInfo2 = paramSelectableAppInfo2.activityName.toString();
      return paramSelectableAppInfo1.toLowerCase().compareTo(paramSelectableAppInfo2.toLowerCase());
    }
  }
  
  public static abstract interface OnDisableUiForPackageListener
  {
    public abstract void onDisableUiForPackage(String paramString);
  }
  
  public static class SelectableAppInfo
  {
    public CharSequence activityName;
    public CharSequence appName;
    public Drawable icon;
    public SelectableAppInfo masterEntry;
    public String packageName;
    
    public String toString()
    {
      return this.packageName + ": appName=" + this.appName + "; activityName=" + this.activityName + "; icon=" + this.icon + "; masterEntry=" + this.masterEntry;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\users\AppRestrictionsHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */