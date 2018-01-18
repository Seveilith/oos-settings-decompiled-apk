package com.android.settings.deviceinfo;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.IPackageMoveObserver.Stub;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import java.util.Objects;

public class StorageWizardFormatProgress
  extends StorageWizardBase
{
  private static final String TAG_SLOW_WARNING = "slow_warning";
  private boolean mFormatPrivate;
  private PartitionTask mTask;
  
  private String getDiskDescription()
  {
    return this.mDisk.getDescription();
  }
  
  private String getGenericDiskDescription()
  {
    if (this.mDisk.isSd()) {
      return getString(17040606);
    }
    if (this.mDisk.isUsb()) {
      return getString(17040608);
    }
    return null;
  }
  
  private void onFormatFinished()
  {
    Object localObject = getIntent().getStringExtra("forget_uuid");
    if (!TextUtils.isEmpty((CharSequence)localObject)) {
      this.mStorage.forgetVolume((String)localObject);
    }
    boolean bool;
    if (this.mFormatPrivate)
    {
      localObject = getPackageManager().getPrimaryStorageCurrentVolume();
      if (localObject != null)
      {
        bool = "private".equals(((VolumeInfo)localObject).getId());
        if (!bool) {
          break label103;
        }
        localObject = new Intent(this, StorageWizardMigrate.class);
        ((Intent)localObject).putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
        startActivity((Intent)localObject);
      }
    }
    for (;;)
    {
      finishAffinity();
      return;
      bool = false;
      break;
      bool = false;
      break;
      label103:
      localObject = new Intent(this, StorageWizardReady.class);
      ((Intent)localObject).putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
      startActivity((Intent)localObject);
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (this.mDisk == null)
    {
      finish();
      return;
    }
    setContentView(2130969019);
    setKeepScreenOn(true);
    this.mFormatPrivate = getIntent().getBooleanExtra("format_private", false);
    if (this.mFormatPrivate) {}
    for (int i = 1;; i = 2)
    {
      setIllustrationType(i);
      setHeaderText(2131691810, new String[] { this.mDisk.getDescription() });
      setBodyText(2131691811, new String[] { this.mDisk.getDescription() });
      getNextButton().setVisibility(8);
      this.mTask = ((PartitionTask)getLastNonConfigurationInstance());
      if (this.mTask != null) {
        break;
      }
      this.mTask = new PartitionTask();
      this.mTask.setActivity(this);
      this.mTask.execute(new Void[0]);
      return;
    }
    this.mTask.setActivity(this);
  }
  
  public Object onRetainNonConfigurationInstance()
  {
    return this.mTask;
  }
  
  public static class PartitionTask
    extends AsyncTask<Void, Integer, Exception>
  {
    public StorageWizardFormatProgress mActivity;
    private volatile long mInternalBench;
    private volatile long mPrivateBench;
    private volatile int mProgress = 20;
    
    protected Exception doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = this.mActivity;
      StorageManager localStorageManager = this.mActivity.mStorage;
      try
      {
        if (StorageWizardFormatProgress.-get0(paramVarArgs))
        {
          localStorageManager.partitionPrivate(paramVarArgs.mDisk.getId());
          publishProgress(new Integer[] { Integer.valueOf(40) });
          this.mInternalBench = localStorageManager.benchmark(null);
          publishProgress(new Integer[] { Integer.valueOf(60) });
          VolumeInfo localVolumeInfo = paramVarArgs.findFirstVolume(1);
          this.mPrivateBench = localStorageManager.benchmark(localVolumeInfo.getId());
          if ((paramVarArgs.mDisk.isDefaultPrimary()) && (Objects.equals(localStorageManager.getPrimaryStorageUuid(), "primary_physical")))
          {
            Log.d("StorageSettings", "Just formatted primary physical; silently moving storage to new emulated volume");
            localStorageManager.setPrimaryStorageUuid(localVolumeInfo.getFsUuid(), new StorageWizardFormatProgress.SilentObserver(null));
            return null;
          }
        }
        else
        {
          localStorageManager.partitionPublic(paramVarArgs.mDisk.getId());
          return null;
        }
      }
      catch (Exception paramVarArgs)
      {
        return paramVarArgs;
      }
      return null;
    }
    
    protected void onPostExecute(Exception paramException)
    {
      StorageWizardFormatProgress localStorageWizardFormatProgress = this.mActivity;
      if (localStorageWizardFormatProgress.isDestroyed()) {
        return;
      }
      if (paramException != null)
      {
        Log.e("StorageSettings", "Failed to partition", paramException);
        Toast.makeText(localStorageWizardFormatProgress, paramException.getMessage(), 1).show();
        localStorageWizardFormatProgress.finishAffinity();
        return;
      }
      if (StorageWizardFormatProgress.-get0(localStorageWizardFormatProgress))
      {
        float f = (float)this.mInternalBench / (float)this.mPrivateBench;
        Log.d("StorageSettings", "New volume is " + f + "x the speed of internal");
        if ((Float.isNaN(f)) || (f < 0.25D))
        {
          new StorageWizardFormatProgress.SlowWarningFragment().showAllowingStateLoss(localStorageWizardFormatProgress.getFragmentManager(), "slow_warning");
          return;
        }
        StorageWizardFormatProgress.-wrap2(localStorageWizardFormatProgress);
        return;
      }
      StorageWizardFormatProgress.-wrap2(localStorageWizardFormatProgress);
    }
    
    protected void onProgressUpdate(Integer... paramVarArgs)
    {
      this.mProgress = paramVarArgs[0].intValue();
      this.mActivity.setCurrentProgress(this.mProgress);
    }
    
    public void setActivity(StorageWizardFormatProgress paramStorageWizardFormatProgress)
    {
      this.mActivity = paramStorageWizardFormatProgress;
      this.mActivity.setCurrentProgress(this.mProgress);
    }
  }
  
  private static class SilentObserver
    extends IPackageMoveObserver.Stub
  {
    public void onCreated(int paramInt, Bundle paramBundle) {}
    
    public void onStatusChanged(int paramInt1, int paramInt2, long paramLong) {}
  }
  
  public static class SlowWarningFragment
    extends DialogFragment
  {
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      paramBundle = new AlertDialog.Builder(getActivity());
      Object localObject = (StorageWizardFormatProgress)getActivity();
      String str = StorageWizardFormatProgress.-wrap0((StorageWizardFormatProgress)localObject);
      localObject = StorageWizardFormatProgress.-wrap1((StorageWizardFormatProgress)localObject);
      paramBundle.setMessage(TextUtils.expandTemplate(getText(2131691829), new CharSequence[] { str, localObject }));
      paramBundle.setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          StorageWizardFormatProgress.-wrap2((StorageWizardFormatProgress)StorageWizardFormatProgress.SlowWarningFragment.this.getActivity());
        }
      });
      return paramBundle.create();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageWizardFormatProgress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */