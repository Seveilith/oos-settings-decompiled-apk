package com.android.settings.deviceinfo;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.widget.Button;
import com.android.internal.util.Preconditions;
import java.util.List;

public class StorageWizardMoveConfirm
  extends StorageWizardBase
{
  private ApplicationInfo mApp;
  private String mPackageName;
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (this.mVolume == null)
    {
      finish();
      return;
    }
    setContentView(2130969015);
    try
    {
      this.mPackageName = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
      this.mApp = getPackageManager().getApplicationInfo(this.mPackageName, 0);
      Preconditions.checkState(getPackageManager().getPackageCandidateVolumes(this.mApp).contains(this.mVolume));
      paramBundle = getPackageManager().getApplicationLabel(this.mApp).toString();
      String str = this.mStorage.getBestVolumeDescription(this.mVolume);
      setIllustrationType(1);
      setHeaderText(2131691824, new String[] { paramBundle });
      setBodyText(2131691825, new String[] { paramBundle, str });
      getNextButton().setText(2131692153);
      return;
    }
    catch (PackageManager.NameNotFoundException paramBundle)
    {
      finish();
    }
  }
  
  public void onNavigateNext()
  {
    String str = getPackageManager().getApplicationLabel(this.mApp).toString();
    int i = getPackageManager().movePackage(this.mPackageName, this.mVolume);
    Intent localIntent = new Intent(this, StorageWizardMoveProgress.class);
    localIntent.putExtra("android.content.pm.extra.MOVE_ID", i);
    localIntent.putExtra("android.intent.extra.TITLE", str);
    localIntent.putExtra("android.os.storage.extra.VOLUME_ID", this.mVolume.getId());
    startActivity(localIntent);
    finishAffinity();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageWizardMoveConfirm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */