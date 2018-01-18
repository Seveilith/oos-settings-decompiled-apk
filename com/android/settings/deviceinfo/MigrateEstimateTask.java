package com.android.settings.deviceinfo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.telecom.Log;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import com.android.internal.app.IMediaContainerService;
import com.android.internal.app.IMediaContainerService.Stub;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class MigrateEstimateTask
  extends AsyncTask<Void, Void, Long>
  implements ServiceConnection
{
  private static final ComponentName DEFAULT_CONTAINER_COMPONENT = new ComponentName("com.android.defcontainer", "com.android.defcontainer.DefaultContainerService");
  private static final String EXTRA_SIZE_BYTES = "size_bytes";
  private static final long SPEED_ESTIMATE_BPS = 10485760L;
  private final CountDownLatch mConnected = new CountDownLatch(1);
  private final Context mContext;
  private IMediaContainerService mService;
  private long mSizeBytes = -1L;
  private final StorageManager mStorage;
  
  public MigrateEstimateTask(Context paramContext)
  {
    this.mContext = paramContext;
    this.mStorage = ((StorageManager)paramContext.getSystemService(StorageManager.class));
  }
  
  public void copyFrom(Intent paramIntent)
  {
    this.mSizeBytes = paramIntent.getLongExtra("size_bytes", -1L);
  }
  
  public void copyTo(Intent paramIntent)
  {
    paramIntent.putExtra("size_bytes", this.mSizeBytes);
  }
  
  protected Long doInBackground(Void... paramVarArgs)
  {
    if (this.mSizeBytes != -1L) {
      return Long.valueOf(this.mSizeBytes);
    }
    paramVarArgs = this.mContext.getPackageManager().getPrimaryStorageCurrentVolume();
    paramVarArgs = this.mStorage.findEmulatedForPrivate(paramVarArgs);
    if (paramVarArgs == null)
    {
      Log.w("StorageSettings", "Failed to find current primary storage", new Object[0]);
      return Long.valueOf(-1L);
    }
    paramVarArgs = paramVarArgs.getPath().getAbsolutePath();
    Log.d("StorageSettings", "Estimating for current path " + paramVarArgs, new Object[0]);
    Intent localIntent = new Intent().setComponent(DEFAULT_CONTAINER_COMPONENT);
    this.mContext.bindServiceAsUser(localIntent, this, 1, UserHandle.SYSTEM);
    try
    {
      if (this.mConnected.await(15L, TimeUnit.SECONDS))
      {
        long l = this.mService.calculateDirectorySize(paramVarArgs);
        return Long.valueOf(l);
      }
    }
    catch (InterruptedException|RemoteException localInterruptedException)
    {
      for (;;)
      {
        Log.w("StorageSettings", "Failed to measure " + paramVarArgs, new Object[0]);
        this.mContext.unbindService(this);
      }
    }
    finally
    {
      this.mContext.unbindService(this);
    }
    return Long.valueOf(-1L);
  }
  
  protected void onPostExecute(Long paramLong)
  {
    this.mSizeBytes = paramLong.longValue();
    long l = Math.max(this.mSizeBytes * 1000L / 10485760L, 1000L);
    onPostExecute(Formatter.formatFileSize(this.mContext, this.mSizeBytes), DateUtils.formatDuration(l).toString());
  }
  
  public abstract void onPostExecute(String paramString1, String paramString2);
  
  public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    this.mService = IMediaContainerService.Stub.asInterface(paramIBinder);
    this.mConnected.countDown();
  }
  
  public void onServiceDisconnected(ComponentName paramComponentName) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\MigrateEstimateTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */