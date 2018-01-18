package com.android.settings.vpn2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import com.android.internal.net.VpnConfig;

public class AppPreference
  extends ManageablePreference
{
  public static final int STATE_CONNECTED = 3;
  public static final int STATE_DISCONNECTED = STATE_NONE;
  private final String mName;
  private final String mPackageName;
  
  public AppPreference(Context paramContext, int paramInt, String paramString)
  {
    super(paramContext, null);
    super.setUserId(paramInt);
    this.mPackageName = paramString;
    localObject1 = paramString;
    PackageManager localPackageManager = null;
    Object localObject3 = null;
    paramString = null;
    Object localObject2 = localPackageManager;
    paramContext = (Context)localObject1;
    for (;;)
    {
      try
      {
        localContext = getUserContext();
        localObject2 = localPackageManager;
        paramContext = (Context)localObject1;
        localPackageManager = localContext.getPackageManager();
        paramContext = (Context)localObject3;
      }
      catch (PackageManager.NameNotFoundException paramString)
      {
        Context localContext;
        localObject1 = paramContext;
        continue;
      }
      try
      {
        localObject2 = localPackageManager.getPackageInfo(this.mPackageName, 0);
        paramContext = paramString;
        paramString = (String)localObject1;
        if (localObject2 != null)
        {
          paramContext = (Context)localObject3;
          localObject2 = ((PackageInfo)localObject2).applicationInfo.loadIcon(localPackageManager);
          paramContext = (Context)localObject2;
          paramString = VpnConfig.getVpnLabel(localContext, this.mPackageName).toString();
          paramContext = (Context)localObject2;
        }
      }
      catch (PackageManager.NameNotFoundException paramString)
      {
        paramString = (String)localObject1;
      }
    }
    localObject2 = paramContext;
    localObject1 = paramString;
    if (paramContext == null)
    {
      localObject2 = paramContext;
      paramContext = paramString;
      localObject1 = localPackageManager.getDefaultActivityIcon();
      localObject2 = localObject1;
      localObject1 = paramString;
    }
    this.mName = ((String)localObject1);
    setTitle(this.mName);
    setIcon((Drawable)localObject2);
  }
  
  private Context getUserContext()
    throws PackageManager.NameNotFoundException
  {
    UserHandle localUserHandle = UserHandle.of(this.mUserId);
    return getContext().createPackageContextAsUser(getContext().getPackageName(), 0, localUserHandle);
  }
  
  public int compareTo(Preference paramPreference)
  {
    if ((paramPreference instanceof AppPreference))
    {
      paramPreference = (AppPreference)paramPreference;
      int j = paramPreference.mState - this.mState;
      int i = j;
      if (j == 0)
      {
        j = this.mName.compareToIgnoreCase(paramPreference.mName);
        i = j;
        if (j == 0)
        {
          j = this.mPackageName.compareTo(paramPreference.mPackageName);
          i = j;
          if (j == 0) {
            i = this.mUserId - paramPreference.mUserId;
          }
        }
      }
      return i;
    }
    if ((paramPreference instanceof LegacyVpnPreference)) {
      return -((LegacyVpnPreference)paramPreference).compareTo(this);
    }
    return super.compareTo(paramPreference);
  }
  
  public String getLabel()
  {
    return this.mName;
  }
  
  public PackageInfo getPackageInfo()
  {
    try
    {
      PackageInfo localPackageInfo = getUserContext().getPackageManager().getPackageInfo(this.mPackageName, 0);
      return localPackageInfo;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return null;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\vpn2\AppPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */