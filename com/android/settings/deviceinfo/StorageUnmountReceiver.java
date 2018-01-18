package com.android.settings.deviceinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;

public class StorageUnmountReceiver
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    Object localObject = (StorageManager)paramContext.getSystemService(StorageManager.class);
    paramIntent = paramIntent.getStringExtra("android.os.storage.extra.VOLUME_ID");
    localObject = ((StorageManager)localObject).findVolumeById(paramIntent);
    if (localObject != null)
    {
      new StorageSettings.UnmountTask(paramContext, (VolumeInfo)localObject).execute(new Void[0]);
      return;
    }
    Log.w("StorageSettings", "Missing volume " + paramIntent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageUnmountReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */