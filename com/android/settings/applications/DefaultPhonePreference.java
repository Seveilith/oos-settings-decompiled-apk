package com.android.settings.applications;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import android.telecom.DefaultDialerManager;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.settings.AppListPreference;
import com.android.settings.SelfAvailablePreference;
import java.util.List;
import java.util.Objects;

public class DefaultPhonePreference
  extends AppListPreference
  implements SelfAvailablePreference
{
  private final Context mContext;
  
  public DefaultPhonePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext.getApplicationContext();
    loadDialerApps();
  }
  
  private String getDefaultPackage()
  {
    return DefaultDialerManager.getDefaultDialerApplication(getContext(), this.mUserId);
  }
  
  private String getSystemPackage()
  {
    return TelecomManager.from(getContext()).getSystemDialerPackage();
  }
  
  public static boolean hasPhonePreference(String paramString, Context paramContext)
  {
    return DefaultDialerManager.getInstalledDialerApplications(paramContext, UserHandle.myUserId()).contains(paramString);
  }
  
  public static boolean isPhoneDefault(String paramString, Context paramContext)
  {
    paramContext = DefaultDialerManager.getDefaultDialerApplication(paramContext, UserHandle.myUserId());
    if (paramContext != null) {
      return paramContext.equals(paramString);
    }
    return false;
  }
  
  private void loadDialerApps()
  {
    List localList = DefaultDialerManager.getInstalledDialerApplications(getContext(), this.mUserId);
    String[] arrayOfString = new String[localList.size()];
    int i = 0;
    while (i < localList.size())
    {
      arrayOfString[i] = ((String)localList.get(i));
      i += 1;
    }
    setPackageNames(arrayOfString, getDefaultPackage(), getSystemPackage());
  }
  
  public boolean isAvailable(Context paramContext)
  {
    if (!((TelephonyManager)paramContext.getSystemService("phone")).isVoiceCapable()) {
      return false;
    }
    return !((UserManager)paramContext.getSystemService("user")).hasUserRestriction("no_outgoing_calls");
  }
  
  protected boolean persistString(String paramString)
  {
    if ((TextUtils.isEmpty(paramString)) || (Objects.equals(paramString, getDefaultPackage()))) {}
    for (;;)
    {
      setSummary(getEntry());
      return true;
      DefaultDialerManager.setDefaultDialerApplication(getContext(), paramString, this.mUserId);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\DefaultPhonePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */