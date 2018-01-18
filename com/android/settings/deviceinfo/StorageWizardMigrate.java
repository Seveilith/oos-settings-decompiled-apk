package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

public class StorageWizardMigrate
  extends StorageWizardBase
{
  private MigrateEstimateTask mEstimate;
  private RadioButton mRadioLater;
  private final CompoundButton.OnCheckedChangeListener mRadioListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean)
      {
        if (paramAnonymousCompoundButton != StorageWizardMigrate.-get1(StorageWizardMigrate.this)) {
          break label38;
        }
        StorageWizardMigrate.-get0(StorageWizardMigrate.this).setChecked(false);
      }
      for (;;)
      {
        StorageWizardMigrate.this.getNextButton().setEnabled(true);
        return;
        label38:
        if (paramAnonymousCompoundButton == StorageWizardMigrate.-get0(StorageWizardMigrate.this)) {
          StorageWizardMigrate.-get1(StorageWizardMigrate.this).setChecked(false);
        }
      }
    }
  };
  private RadioButton mRadioNow;
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (this.mDisk == null)
    {
      finish();
      return;
    }
    setContentView(2130969017);
    setIllustrationType(1);
    setHeaderText(2131691812, new String[] { this.mDisk.getDescription() });
    setBodyText(2131691720, new String[0]);
    this.mRadioNow = ((RadioButton)findViewById(2131362571));
    this.mRadioLater = ((RadioButton)findViewById(2131362572));
    this.mRadioNow.setOnCheckedChangeListener(this.mRadioListener);
    this.mRadioLater.setOnCheckedChangeListener(this.mRadioListener);
    getNextButton().setEnabled(false);
    this.mEstimate = new MigrateEstimateTask(this)
    {
      public void onPostExecute(String paramAnonymousString1, String paramAnonymousString2)
      {
        StorageWizardMigrate.this.setBodyText(2131691813, new String[] { StorageWizardMigrate.this.mDisk.getDescription(), paramAnonymousString2, paramAnonymousString1 });
      }
    };
    this.mEstimate.copyFrom(getIntent());
    this.mEstimate.execute(new Void[0]);
  }
  
  public void onNavigateNext()
  {
    if (this.mRadioNow.isChecked())
    {
      localIntent = new Intent(this, StorageWizardMigrateConfirm.class);
      localIntent.putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
      this.mEstimate.copyTo(localIntent);
      startActivity(localIntent);
    }
    while (!this.mRadioLater.isChecked()) {
      return;
    }
    Intent localIntent = new Intent(this, StorageWizardReady.class);
    localIntent.putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
    startActivity(localIntent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageWizardMigrate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */