package com.android.settings.applications;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.AppHeader;

public abstract class AppInfoWithHeader
  extends AppInfoBase
{
  private boolean mCreated;
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (this.mCreated)
    {
      Log.w(TAG, "onActivityCreated: ignoring duplicate call");
      return;
    }
    this.mCreated = true;
    if (this.mPackageInfo == null) {
      return;
    }
    AppHeader.createAppHeader(this, this.mPackageInfo.applicationInfo.loadIcon(this.mPm), this.mPackageInfo.applicationInfo.loadLabel(this.mPm), this.mPackageName, this.mPackageInfo.applicationInfo.uid, 0);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppInfoWithHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */