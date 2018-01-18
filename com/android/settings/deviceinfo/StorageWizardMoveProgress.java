package com.android.settings.deviceinfo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.MoveCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class StorageWizardMoveProgress
  extends StorageWizardBase
{
  private final PackageManager.MoveCallback mCallback = new PackageManager.MoveCallback()
  {
    public void onStatusChanged(int paramAnonymousInt1, int paramAnonymousInt2, long paramAnonymousLong)
    {
      if (StorageWizardMoveProgress.-get0(StorageWizardMoveProgress.this) != paramAnonymousInt1) {
        return;
      }
      if (PackageManager.isMoveStatusFinished(paramAnonymousInt2))
      {
        Log.d("StorageSettings", "Finished with status " + paramAnonymousInt2);
        if (paramAnonymousInt2 != -100) {
          Toast.makeText(StorageWizardMoveProgress.this, StorageWizardMoveProgress.-wrap0(StorageWizardMoveProgress.this, paramAnonymousInt2), 1).show();
        }
        StorageWizardMoveProgress.this.finishAffinity();
        return;
      }
      StorageWizardMoveProgress.this.setCurrentProgress(paramAnonymousInt2);
    }
  };
  private int mMoveId;
  
  private CharSequence moveStatusToMessage(int paramInt)
  {
    switch (paramInt)
    {
    case -7: 
    case -6: 
    default: 
      return getString(2131692158);
    case -1: 
      return getString(2131692158);
    case -8: 
      return getString(2131692163);
    case -2: 
      return getString(2131692159);
    case -4: 
      return getString(2131692160);
    case -5: 
      return getString(2131692161);
    }
    return getString(2131692162);
  }
  
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
    paramBundle = getIntent().getStringExtra("android.intent.extra.TITLE");
    String str = this.mStorage.getBestVolumeDescription(this.mVolume);
    setIllustrationType(1);
    setHeaderText(2131691826, new String[] { paramBundle });
    setBodyText(2131691827, new String[] { str, paramBundle });
    getNextButton().setVisibility(8);
    getPackageManager().registerMoveCallback(this.mCallback, new Handler());
    this.mCallback.onStatusChanged(this.mMoveId, getPackageManager().getMoveStatus(this.mMoveId), -1L);
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    getPackageManager().unregisterMoveCallback(this.mCallback);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageWizardMoveProgress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */