package com.android.settingslib.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.text.TextUtils.SimpleStringSplitter;
import android.util.ArraySet;
import android.view.accessibility.AccessibilityManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class AccessibilityUtils
{
  public static final char ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR = ':';
  static final TextUtils.SimpleStringSplitter sStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
  
  public static Set<ComponentName> getEnabledServicesFromSettings(Context paramContext)
  {
    return getEnabledServicesFromSettings(paramContext, UserHandle.myUserId());
  }
  
  public static Set<ComponentName> getEnabledServicesFromSettings(Context paramContext, int paramInt)
  {
    Object localObject = Settings.Secure.getStringForUser(paramContext.getContentResolver(), "enabled_accessibility_services", paramInt);
    if (localObject == null) {
      return Collections.emptySet();
    }
    paramContext = new HashSet();
    TextUtils.SimpleStringSplitter localSimpleStringSplitter = sStringColonSplitter;
    localSimpleStringSplitter.setString((String)localObject);
    while (localSimpleStringSplitter.hasNext())
    {
      localObject = ComponentName.unflattenFromString(localSimpleStringSplitter.next());
      if (localObject != null) {
        paramContext.add(localObject);
      }
    }
    return paramContext;
  }
  
  private static Set<ComponentName> getInstalledServices(Context paramContext)
  {
    HashSet localHashSet = new HashSet();
    localHashSet.clear();
    paramContext = AccessibilityManager.getInstance(paramContext).getInstalledAccessibilityServiceList();
    if (paramContext == null) {
      return localHashSet;
    }
    paramContext = paramContext.iterator();
    while (paramContext.hasNext())
    {
      ResolveInfo localResolveInfo = ((AccessibilityServiceInfo)paramContext.next()).getResolveInfo();
      localHashSet.add(new ComponentName(localResolveInfo.serviceInfo.packageName, localResolveInfo.serviceInfo.name));
    }
    return localHashSet;
  }
  
  public static CharSequence getTextForLocale(Context paramContext, Locale paramLocale, int paramInt)
  {
    Configuration localConfiguration = new Configuration(paramContext.getResources().getConfiguration());
    localConfiguration.setLocale(paramLocale);
    return paramContext.createConfigurationContext(localConfiguration).getText(paramInt);
  }
  
  public static void setAccessibilityServiceState(Context paramContext, ComponentName paramComponentName, boolean paramBoolean)
  {
    setAccessibilityServiceState(paramContext, paramComponentName, paramBoolean, UserHandle.myUserId());
  }
  
  public static void setAccessibilityServiceState(Context paramContext, ComponentName paramComponentName, boolean paramBoolean, int paramInt)
  {
    Object localObject2 = getEnabledServicesFromSettings(paramContext, paramInt);
    Object localObject1 = localObject2;
    if (((Set)localObject2).isEmpty()) {
      localObject1 = new ArraySet(1);
    }
    if (paramBoolean) {
      ((Set)localObject1).add(paramComponentName);
    }
    for (;;)
    {
      paramComponentName = new StringBuilder();
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        paramComponentName.append(((ComponentName)((Iterator)localObject1).next()).flattenToString());
        paramComponentName.append(':');
      }
      ((Set)localObject1).remove(paramComponentName);
      paramComponentName = getInstalledServices(paramContext);
      localObject2 = ((Iterable)localObject1).iterator();
      if (((Iterator)localObject2).hasNext()) {
        if (!paramComponentName.contains((ComponentName)((Iterator)localObject2).next())) {
          break;
        }
      }
    }
    int i = paramComponentName.length();
    if (i > 0) {
      paramComponentName.deleteCharAt(i - 1);
    }
    Settings.Secure.putStringForUser(paramContext.getContentResolver(), "enabled_accessibility_services", paramComponentName.toString(), paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\accessibility\AccessibilityUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */