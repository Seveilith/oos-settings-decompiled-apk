package com.android.settings.deviceinfo;

import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.VolumeInfo;
import android.widget.Button;

public class StorageWizardReady
  extends StorageWizardBase
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (this.mDisk == null)
    {
      finish();
      return;
    }
    setContentView(2130969015);
    setHeaderText(2131691821, new String[] { this.mDisk.getDescription() });
    paramBundle = findFirstVolume(0);
    VolumeInfo localVolumeInfo = findFirstVolume(1);
    if (paramBundle != null)
    {
      setIllustrationType(2);
      setBodyText(2131691822, new String[] { this.mDisk.getDescription() });
    }
    for (;;)
    {
      getNextButton().setText(2131690998);
      return;
      if (localVolumeInfo != null)
      {
        setIllustrationType(1);
        setBodyText(2131691823, new String[] { this.mDisk.getDescription() });
      }
    }
  }
  
  public void onNavigateNext()
  {
    finishAffinity();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageWizardReady.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */