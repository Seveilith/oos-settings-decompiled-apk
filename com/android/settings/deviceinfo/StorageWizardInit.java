package com.android.settings.deviceinfo;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

public class StorageWizardInit
  extends StorageWizardBase
{
  private boolean mIsPermittedToAdopt;
  private RadioButton mRadioExternal;
  private RadioButton mRadioInternal;
  private final CompoundButton.OnCheckedChangeListener mRadioListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean)
      {
        if (paramAnonymousCompoundButton != StorageWizardInit.-get0(StorageWizardInit.this)) {
          break label46;
        }
        StorageWizardInit.-get1(StorageWizardInit.this).setChecked(false);
        StorageWizardInit.this.setIllustrationType(2);
      }
      for (;;)
      {
        StorageWizardInit.this.getNextButton().setEnabled(true);
        return;
        label46:
        if (paramAnonymousCompoundButton == StorageWizardInit.-get1(StorageWizardInit.this))
        {
          StorageWizardInit.-get0(StorageWizardInit.this).setChecked(false);
          StorageWizardInit.this.setIllustrationType(1);
        }
      }
    }
  };
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (this.mDisk == null)
    {
      finish();
      return;
    }
    setContentView(2130969016);
    boolean bool;
    if (UserManager.get(this).isAdminUser()) {
      if (ActivityManager.isUserAMonkey()) {
        bool = false;
      }
    }
    for (;;)
    {
      this.mIsPermittedToAdopt = bool;
      setIllustrationType(0);
      setHeaderText(2131691800, new String[] { this.mDisk.getDescription() });
      this.mRadioExternal = ((RadioButton)findViewById(2131362567));
      this.mRadioInternal = ((RadioButton)findViewById(2131362569));
      this.mRadioExternal.setOnCheckedChangeListener(this.mRadioListener);
      this.mRadioInternal.setOnCheckedChangeListener(this.mRadioListener);
      findViewById(2131362568).setPadding(this.mRadioExternal.getCompoundPaddingLeft(), 0, this.mRadioExternal.getCompoundPaddingRight(), 0);
      findViewById(2131362570).setPadding(this.mRadioExternal.getCompoundPaddingLeft(), 0, this.mRadioExternal.getCompoundPaddingRight(), 0);
      getNextButton().setEnabled(false);
      if (!this.mDisk.isAdoptable())
      {
        this.mRadioExternal.setChecked(true);
        onNavigateNext();
        finish();
      }
      if (!this.mIsPermittedToAdopt) {
        this.mRadioInternal.setEnabled(false);
      }
      return;
      bool = true;
      continue;
      bool = false;
    }
  }
  
  public void onNavigateNext()
  {
    if (this.mRadioExternal.isChecked()) {
      if ((this.mVolume != null) && (this.mVolume.getType() == 0) && (this.mVolume.getState() != 6))
      {
        this.mStorage.setVolumeInited(this.mVolume.getFsUuid(), true);
        localIntent = new Intent(this, StorageWizardReady.class);
        localIntent.putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
        startActivity(localIntent);
      }
    }
    while (!this.mRadioInternal.isChecked())
    {
      return;
      localIntent = new Intent(this, StorageWizardFormatConfirm.class);
      localIntent.putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
      localIntent.putExtra("format_private", false);
      startActivity(localIntent);
      return;
    }
    Intent localIntent = new Intent(this, StorageWizardFormatConfirm.class);
    localIntent.putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
    localIntent.putExtra("format_private", true);
    startActivity(localIntent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageWizardInit.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */