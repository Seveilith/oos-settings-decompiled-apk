package com.android.settings.applications;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.internal.telephony.SmsApplication;
import com.android.internal.telephony.SmsApplication.SmsApplicationData;
import com.android.settings.AppListPreference;
import com.android.settings.SelfAvailablePreference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class DefaultSmsPreference
  extends AppListPreference
  implements SelfAvailablePreference
{
  public DefaultSmsPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    loadSmsApps();
  }
  
  private String getDefaultPackage()
  {
    ComponentName localComponentName = SmsApplication.getDefaultSmsApplication(getContext(), true);
    if (localComponentName != null) {
      return localComponentName.getPackageName();
    }
    return null;
  }
  
  public static boolean hasSmsPreference(String paramString, Context paramContext)
  {
    paramContext = SmsApplication.getApplicationCollection(paramContext).iterator();
    while (paramContext.hasNext()) {
      if (((SmsApplication.SmsApplicationData)paramContext.next()).mPackageName.equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isSmsDefault(String paramString, Context paramContext)
  {
    paramContext = SmsApplication.getDefaultSmsApplication(paramContext, true);
    if (paramContext != null) {
      return paramContext.getPackageName().equals(paramString);
    }
    return false;
  }
  
  private void loadSmsApps()
  {
    Object localObject = SmsApplication.getApplicationCollection(getContext());
    String[] arrayOfString = new String[((Collection)localObject).size()];
    int i = 0;
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      arrayOfString[i] = ((SmsApplication.SmsApplicationData)((Iterator)localObject).next()).mPackageName;
      i += 1;
    }
    setPackageNames(arrayOfString, getDefaultPackage());
  }
  
  public boolean isAvailable(Context paramContext)
  {
    boolean bool = UserManager.get(paramContext).getUserInfo(UserHandle.myUserId()).isRestricted();
    paramContext = (TelephonyManager)paramContext.getSystemService("phone");
    if (!bool) {
      return paramContext.isSmsCapable();
    }
    return false;
  }
  
  protected boolean persistString(String paramString)
  {
    if ((TextUtils.isEmpty(paramString)) || (Objects.equals(paramString, getDefaultPackage()))) {}
    for (;;)
    {
      setSummary(getEntry());
      return true;
      SmsApplication.setDefaultApplication(paramString, getContext());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\DefaultSmsPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */