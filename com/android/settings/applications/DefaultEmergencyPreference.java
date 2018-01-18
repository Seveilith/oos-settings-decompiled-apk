package com.android.settings.applications;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import com.android.settings.AppListPreference;
import com.android.settings.SelfAvailablePreference;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DefaultEmergencyPreference
  extends AppListPreference
  implements SelfAvailablePreference
{
  private static final boolean DEFAULT_EMERGENCY_APP_IS_CONFIGURABLE = false;
  public static final Intent QUERY_INTENT = new Intent("android.telephony.action.EMERGENCY_ASSISTANCE");
  private final ContentResolver mContentResolver;
  
  public DefaultEmergencyPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContentResolver = paramContext.getContentResolver();
    load();
  }
  
  public static boolean hasEmergencyPreference(String paramString, Context paramContext)
  {
    boolean bool2 = false;
    Intent localIntent = new Intent(QUERY_INTENT);
    localIntent.setPackage(paramString);
    paramString = paramContext.getPackageManager().queryIntentActivities(localIntent, 0);
    boolean bool1 = bool2;
    if (paramString != null)
    {
      bool1 = bool2;
      if (paramString.size() != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static boolean isCapable(Context paramContext)
  {
    return paramContext.getResources().getBoolean(17956957);
  }
  
  public static boolean isEmergencyDefault(String paramString, Context paramContext)
  {
    paramContext = Settings.Secure.getString(paramContext.getContentResolver(), "emergency_assistance_application");
    if (paramContext != null) {
      return paramContext.equals(paramString);
    }
    return false;
  }
  
  private static boolean isSystemApp(PackageInfo paramPackageInfo)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramPackageInfo.applicationInfo != null)
    {
      bool1 = bool2;
      if ((paramPackageInfo.applicationInfo.flags & 0x1) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private void load()
  {
    new AsyncTask()
    {
      protected Set<String> doInBackground(Void[] paramAnonymousArrayOfVoid)
      {
        return DefaultEmergencyPreference.-wrap0(DefaultEmergencyPreference.this);
      }
      
      protected void onPostExecute(Set<String> paramAnonymousSet)
      {
        String str = Settings.Secure.getString(DefaultEmergencyPreference.-get0(DefaultEmergencyPreference.this), "emergency_assistance_application");
        DefaultEmergencyPreference.this.setPackageNames((CharSequence[])paramAnonymousSet.toArray(new String[paramAnonymousSet.size()]), str);
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  private Set<String> resolveAssistPackageAndQueryApps()
  {
    ArraySet localArraySet = new ArraySet();
    PackageManager localPackageManager = getContext().getPackageManager();
    List localList = localPackageManager.queryIntentActivities(QUERY_INTENT, 0);
    Object localObject1 = null;
    int j = localList.size();
    int i = 0;
    if (i < j)
    {
      Object localObject4 = (ResolveInfo)localList.get(i);
      Object localObject2 = localObject1;
      if (localObject4 != null)
      {
        if (((ResolveInfo)localObject4).activityInfo != null) {
          break label88;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        break;
        label88:
        localObject2 = localObject1;
        if (!localArraySet.contains(((ResolveInfo)localObject4).activityInfo.packageName))
        {
          localObject2 = ((ResolveInfo)localObject4).activityInfo.packageName;
          localArraySet.add(localObject2);
          try
          {
            localObject4 = localPackageManager.getPackageInfo((String)localObject2, 0);
            localObject2 = localObject1;
            if (isSystemApp((PackageInfo)localObject4)) {
              if (localObject1 != null)
              {
                localObject2 = localObject1;
                if (((PackageInfo)localObject1).firstInstallTime <= ((PackageInfo)localObject4).firstInstallTime) {}
              }
              else
              {
                localObject2 = localObject4;
              }
            }
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException)
          {
            localObject3 = localObject1;
          }
        }
      }
    }
    Object localObject3 = Settings.Secure.getString(this.mContentResolver, "emergency_assistance_application");
    if (!TextUtils.isEmpty((CharSequence)localObject3))
    {
      if (!localArraySet.contains(localObject3)) {
        break label248;
      }
      i = 0;
    }
    for (;;)
    {
      if ((localObject1 != null) && (i != 0)) {
        Settings.Secure.putString(this.mContentResolver, "emergency_assistance_application", ((PackageInfo)localObject1).packageName);
      }
      return localArraySet;
      i = 1;
      continue;
      label248:
      i = 1;
    }
  }
  
  public boolean isAvailable(Context paramContext)
  {
    return false;
  }
  
  protected boolean persistString(String paramString)
  {
    String str = Settings.Secure.getString(this.mContentResolver, "emergency_assistance_application");
    if ((TextUtils.isEmpty(paramString)) || (Objects.equals(paramString, str))) {}
    for (;;)
    {
      setSummary(getEntry());
      return true;
      Settings.Secure.putString(this.mContentResolver, "emergency_assistance_application", paramString);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\DefaultEmergencyPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */