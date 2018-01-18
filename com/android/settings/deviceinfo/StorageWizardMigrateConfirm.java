package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import java.util.Objects;

public class StorageWizardMigrateConfirm
  extends StorageWizardBase
{
  private MigrateEstimateTask mEstimate;
  
  protected void onCreate(final Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130969015);
    if (this.mVolume == null) {
      this.mVolume = findFirstVolume(1);
    }
    paramBundle = getPackageManager().getPrimaryStorageCurrentVolume();
    if ((paramBundle == null) || (this.mVolume == null))
    {
      Log.d("StorageSettings", "Missing either source or target volume");
      finish();
      return;
    }
    paramBundle = this.mStorage.getBestVolumeDescription(paramBundle);
    String str = this.mStorage.getBestVolumeDescription(this.mVolume);
    setIllustrationType(1);
    setHeaderText(2131691816, new String[] { str });
    setBodyText(2131691720, new String[0]);
    setSecondaryBodyText(2131691820, new String[] { str });
    this.mEstimate = new MigrateEstimateTask(this)
    {
      public void onPostExecute(String paramAnonymousString1, String paramAnonymousString2)
      {
        StorageWizardMigrateConfirm.this.setBodyText(2131691817, new String[] { paramAnonymousString2, paramAnonymousString1, paramBundle });
      }
    };
    this.mEstimate.copyFrom(getIntent());
    this.mEstimate.execute(new Void[0]);
    getNextButton().setText(2131691818);
  }
  
  public void onNavigateNext()
  {
    try
    {
      int i = getPackageManager().movePrimaryStorage(this.mVolume);
      Intent localIntent1 = new Intent(this, StorageWizardMigrateProgress.class);
      localIntent1.putExtra("android.os.storage.extra.VOLUME_ID", this.mVolume.getId());
      localIntent1.putExtra("android.content.pm.extra.MOVE_ID", i);
      startActivity(localIntent1);
      finishAffinity();
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Toast.makeText(this, getString(2131692157), 1).show();
      finishAffinity();
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      StorageManager localStorageManager = (StorageManager)getSystemService("storage");
      Intent localIntent2;
      if (Objects.equals(this.mVolume.getFsUuid(), localStorageManager.getPrimaryStorageVolume().getUuid()))
      {
        localIntent2 = new Intent(this, StorageWizardReady.class);
        localIntent2.putExtra("android.os.storage.extra.DISK_ID", getIntent().getStringExtra("android.os.storage.extra.DISK_ID"));
        startActivity(localIntent2);
        finishAffinity();
        return;
      }
      throw localIntent2;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageWizardMigrateConfirm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */