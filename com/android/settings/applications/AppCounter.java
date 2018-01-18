package com.android.settings.applications;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.Iterator;

public abstract class AppCounter
  extends AsyncTask<Void, Void, Integer>
{
  protected final IPackageManager mIpm;
  protected final PackageManager mPm;
  protected final UserManager mUm;
  
  public AppCounter(Context paramContext)
  {
    this.mPm = paramContext.getPackageManager();
    this.mIpm = AppGlobals.getPackageManager();
    this.mUm = UserManager.get(paramContext);
  }
  
  protected Integer doInBackground(Void... paramVarArgs)
  {
    int i = 0;
    paramVarArgs = this.mUm.getProfiles(UserHandle.myUserId()).iterator();
    for (;;)
    {
      Object localObject;
      int k;
      if (paramVarArgs.hasNext())
      {
        localObject = (UserInfo)paramVarArgs.next();
        k = i;
      }
      try
      {
        IPackageManager localIPackageManager = this.mIpm;
        k = i;
        if (((UserInfo)localObject).isAdmin()) {}
        for (int j = 8192;; j = 0)
        {
          k = i;
          localObject = localIPackageManager.getInstalledApplications(j | 0x8200, ((UserInfo)localObject).id).getList().iterator();
          j = i;
          for (;;)
          {
            i = j;
            k = j;
            if (!((Iterator)localObject).hasNext()) {
              break;
            }
            k = j;
            boolean bool = includeInCount((ApplicationInfo)((Iterator)localObject).next());
            if (bool) {
              j += 1;
            }
          }
        }
        return Integer.valueOf(i);
      }
      catch (RemoteException localRemoteException)
      {
        i = k;
      }
    }
  }
  
  protected abstract boolean includeInCount(ApplicationInfo paramApplicationInfo);
  
  protected abstract void onCountComplete(int paramInt);
  
  protected void onPostExecute(Integer paramInteger)
  {
    onCountComplete(paramInteger.intValue());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppCounter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */