package com.android.settings;

import android.app.Activity;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManager.Stub;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import java.net.URISyntaxException;

public class BackupSettingsActivity
  extends Activity
{
  private static final String TAG = "BackupSettingsActivity";
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getResources().getString(2131689895);
    if (!TextUtils.isEmpty(paramBundle)) {}
    for (;;)
    {
      try
      {
        paramBundle = Intent.parseUri(paramBundle, 0);
        if (paramBundle.resolveActivity(getPackageManager()) == null) {
          continue;
        }
        localIBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
      }
      catch (URISyntaxException paramBundle)
      {
        IBackupManager localIBackupManager;
        boolean bool;
        Log.e("BackupSettingsActivity", "Invalid backup component URI!", paramBundle);
        continue;
      }
      try
      {
        bool = localIBackupManager.isBackupServiceActive(UserHandle.myUserId());
        paramBundle.putExtra("backup_services_available", bool);
        startActivityForResult(paramBundle, -1);
        finish();
        return;
      }
      catch (Exception localException)
      {
        bool = false;
        continue;
      }
      Log.e("BackupSettingsActivity", "Backup component not found!");
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\BackupSettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */