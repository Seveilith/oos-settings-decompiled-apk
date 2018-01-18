package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.MoveCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class StorageWizardMigrateProgress
  extends StorageWizardBase
{
  private static final String ACTION_FINISH_WIZARD = "com.android.systemui.action.FINISH_WIZARD";
  private final PackageManager.MoveCallback mCallback = new PackageManager.MoveCallback()
  {
    public void onStatusChanged(int paramAnonymousInt1, int paramAnonymousInt2, long paramAnonymousLong)
    {
      if (StorageWizardMigrateProgress.-get0(StorageWizardMigrateProgress.this) != paramAnonymousInt1) {
        return;
      }
      Object localObject = StorageWizardMigrateProgress.this;
      if (PackageManager.isMoveStatusFinished(paramAnonymousInt2))
      {
        Log.d("StorageSettings", "Finished with status " + paramAnonymousInt2);
        if (paramAnonymousInt2 == -100) {
          if (StorageWizardMigrateProgress.this.mDisk != null)
          {
            Intent localIntent = new Intent("com.android.systemui.action.FINISH_WIZARD");
            localIntent.addFlags(1073741824);
            StorageWizardMigrateProgress.this.sendBroadcast(localIntent);
            if (!StorageWizardMigrateProgress.this.isFinishing())
            {
              localObject = new Intent((Context)localObject, StorageWizardReady.class);
              ((Intent)localObject).putExtra("android.os.storage.extra.DISK_ID", StorageWizardMigrateProgress.this.mDisk.getId());
              StorageWizardMigrateProgress.this.startActivity((Intent)localObject);
            }
          }
        }
        for (;;)
        {
          StorageWizardMigrateProgress.this.finishAffinity();
          return;
          Toast.makeText((Context)localObject, StorageWizardMigrateProgress.this.getString(2131692158), 1).show();
        }
      }
      StorageWizardMigrateProgress.this.setCurrentProgress(paramAnonymousInt2);
    }
  };
  private int mMoveId;
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (this.mVolume == null)
    {
      finish();
      return;
    }
    setContentView(2130969019);
    this.mMoveId = getIntent().getIntExtra("android.content.pm.extra.MOVE_ID", -1);
    paramBundle = this.mStorage.getBestVolumeDescription(this.mVolume);
    setIllustrationType(1);
    setHeaderText(2131691819, new String[] { paramBundle });
    setBodyText(2131691820, new String[] { paramBundle });
    getNextButton().setVisibility(8);
    getPackageManager().registerMoveCallback(this.mCallback, new Handler());
    this.mCallback.onStatusChanged(this.mMoveId, getPackageManager().getMoveStatus(this.mMoveId), -1L);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageWizardMigrateProgress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */