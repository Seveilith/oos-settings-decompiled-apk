package com.android.settings.deviceinfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.widget.Button;

public class StorageWizardFormatConfirm
  extends StorageWizardBase
{
  public static final String EXTRA_FORGET_UUID = "forget_uuid";
  public static final String EXTRA_FORMAT_PRIVATE = "format_private";
  private boolean mFormatPrivate;
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (this.mDisk == null)
    {
      finish();
      return;
    }
    setContentView(2130969015);
    this.mFormatPrivate = getIntent().getBooleanExtra("format_private", false);
    int i;
    if (this.mFormatPrivate)
    {
      i = 1;
      setIllustrationType(i);
      if (!this.mFormatPrivate) {
        break label116;
      }
      setHeaderText(2131691805, new String[0]);
      setBodyText(2131691806, new String[] { this.mDisk.getDescription() });
    }
    for (;;)
    {
      getNextButton().setText(2131691809);
      getNextButton().setBackgroundTintList(getColorStateList(2131493875));
      return;
      i = 2;
      break;
      label116:
      setHeaderText(2131691807, new String[0]);
      setBodyText(2131691808, new String[] { this.mDisk.getDescription() });
    }
  }
  
  public void onNavigateNext()
  {
    Intent localIntent = new Intent(this, StorageWizardFormatProgress.class);
    localIntent.putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
    localIntent.putExtra("format_private", this.mFormatPrivate);
    localIntent.putExtra("forget_uuid", getIntent().getStringExtra("forget_uuid"));
    startActivity(localIntent);
    finishAffinity();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageWizardFormatConfirm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */