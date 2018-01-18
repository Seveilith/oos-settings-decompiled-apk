package com.android.settingslib.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.settingslib.R.string;
import java.util.ArrayList;
import java.util.List;

public class AppUtils
{
  private static final String TAG = "AppUtils";
  
  public static CharSequence getLaunchByDefaultSummary(ApplicationsState.AppEntry paramAppEntry, IUsbManager paramIUsbManager, PackageManager paramPackageManager, Context paramContext)
  {
    paramAppEntry = paramAppEntry.info.packageName;
    boolean bool;
    if (!hasPreferredActivities(paramPackageManager, paramAppEntry))
    {
      bool = hasUsbDefaults(paramIUsbManager, paramAppEntry);
      if (paramPackageManager.getIntentVerificationStatusAsUser(paramAppEntry, UserHandle.myUserId()) == 0) {
        break label65;
      }
      i = 1;
      label37:
      if ((!bool) && (i == 0)) {
        break label71;
      }
    }
    label65:
    label71:
    for (int i = R.string.launch_defaults_some;; i = R.string.launch_defaults_none)
    {
      return paramContext.getString(i);
      bool = true;
      break;
      i = 0;
      break label37;
    }
  }
  
  public static boolean hasPreferredActivities(PackageManager paramPackageManager, String paramString)
  {
    boolean bool = false;
    ArrayList localArrayList = new ArrayList();
    paramPackageManager.getPreferredActivities(new ArrayList(), localArrayList, paramString);
    Log.d("AppUtils", "Have " + localArrayList.size() + " number of activities in preferred list");
    if (localArrayList.size() > 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean hasUsbDefaults(IUsbManager paramIUsbManager, String paramString)
  {
    if (paramIUsbManager != null) {
      try
      {
        boolean bool = paramIUsbManager.hasDefaults(paramString, UserHandle.myUserId());
        return bool;
      }
      catch (RemoteException paramIUsbManager)
      {
        Log.e("AppUtils", "mUsbManager.hasDefaults", paramIUsbManager);
      }
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\applications\AppUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */