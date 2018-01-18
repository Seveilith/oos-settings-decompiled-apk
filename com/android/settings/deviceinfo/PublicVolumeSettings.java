package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserManager;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.provider.DocumentsContract;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.format.Formatter.BytesResult;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import com.android.internal.util.Preconditions;
import com.android.settings.SettingsPreferenceFragment;
import java.io.File;
import java.util.Objects;

public class PublicVolumeSettings
  extends SettingsPreferenceFragment
{
  private DiskInfo mDisk;
  private Preference mFormatPrivate;
  private Preference mFormatPublic;
  private boolean mIsPermittedToAdopt;
  private Preference mMount;
  private final StorageEventListener mStorageListener = new StorageEventListener()
  {
    public void onVolumeRecordChanged(VolumeRecord paramAnonymousVolumeRecord)
    {
      if (Objects.equals(PublicVolumeSettings.-get1(PublicVolumeSettings.this).getFsUuid(), paramAnonymousVolumeRecord.getFsUuid()))
      {
        PublicVolumeSettings.-set0(PublicVolumeSettings.this, PublicVolumeSettings.-get0(PublicVolumeSettings.this).findVolumeById(PublicVolumeSettings.-get2(PublicVolumeSettings.this)));
        PublicVolumeSettings.this.update();
      }
    }
    
    public void onVolumeStateChanged(VolumeInfo paramAnonymousVolumeInfo, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (Objects.equals(PublicVolumeSettings.-get1(PublicVolumeSettings.this).getId(), paramAnonymousVolumeInfo.getId()))
      {
        PublicVolumeSettings.-set0(PublicVolumeSettings.this, paramAnonymousVolumeInfo);
        PublicVolumeSettings.this.update();
      }
    }
  };
  private StorageManager mStorageManager;
  private StorageSummaryPreference mSummary;
  private Button mUnmount;
  private final View.OnClickListener mUnmountListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      new StorageSettings.UnmountTask(PublicVolumeSettings.this.getActivity(), PublicVolumeSettings.-get1(PublicVolumeSettings.this)).execute(new Void[0]);
    }
  };
  private VolumeInfo mVolume;
  private String mVolumeId;
  
  private void addPreference(Preference paramPreference)
  {
    paramPreference.setOrder(Integer.MAX_VALUE);
    getPreferenceScreen().addPreference(paramPreference);
  }
  
  private Preference buildAction(int paramInt)
  {
    Preference localPreference = new Preference(getPrefContext());
    localPreference.setTitle(paramInt);
    return localPreference;
  }
  
  private boolean isVolumeValid()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mVolume != null)
    {
      bool1 = bool2;
      if (this.mVolume.getType() == 0) {
        bool1 = this.mVolume.isMountedReadable();
      }
    }
    return bool1;
  }
  
  protected int getMetricsCategory()
  {
    return 42;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    int i = getResources().getDimensionPixelSize(2131755604);
    paramBundle = getButtonBar();
    paramBundle.removeAllViews();
    paramBundle.setPadding(i, i, i, i);
    if (this.mUnmount != null) {
      paramBundle.addView(this.mUnmount, new ViewGroup.LayoutParams(-1, -2));
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    boolean bool2 = false;
    super.onCreate(paramBundle);
    paramBundle = getActivity();
    boolean bool1 = bool2;
    if (UserManager.get(paramBundle).isAdminUser())
    {
      if (ActivityManager.isUserAMonkey()) {
        bool1 = bool2;
      }
    }
    else
    {
      this.mIsPermittedToAdopt = bool1;
      this.mStorageManager = ((StorageManager)paramBundle.getSystemService(StorageManager.class));
      if (!"android.provider.action.DOCUMENT_ROOT_SETTINGS".equals(getActivity().getIntent().getAction())) {
        break label114;
      }
      paramBundle = DocumentsContract.getRootId(getActivity().getIntent().getData());
    }
    for (this.mVolume = this.mStorageManager.findVolumeByUuid(paramBundle);; this.mVolume = this.mStorageManager.findVolumeById(paramBundle))
    {
      if (isVolumeValid()) {
        break label139;
      }
      getActivity().finish();
      return;
      bool1 = true;
      break;
      label114:
      paramBundle = getArguments().getString("android.os.storage.extra.VOLUME_ID");
    }
    label139:
    this.mDisk = this.mStorageManager.findDiskById(this.mVolume.getDiskId());
    Preconditions.checkNotNull(this.mDisk);
    this.mVolumeId = this.mVolume.getId();
    addPreferencesFromResource(2131230762);
    getPreferenceScreen().setOrderingAsAdded(true);
    this.mSummary = new StorageSummaryPreference(getPrefContext());
    this.mMount = buildAction(2131691748);
    this.mUnmount = new Button(getActivity());
    this.mUnmount.setText(2131691749);
    this.mUnmount.setOnClickListener(this.mUnmountListener);
    this.mFormatPublic = buildAction(2131691750);
    if (this.mIsPermittedToAdopt) {
      this.mFormatPrivate = buildAction(2131691752);
    }
  }
  
  public void onPause()
  {
    super.onPause();
    this.mStorageManager.unregisterListener(this.mStorageListener);
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    Object localObject = getActivity();
    if (paramPreference == this.mMount) {
      new StorageSettings.MountTask((Context)localObject, this.mVolume).execute(new Void[0]);
    }
    for (;;)
    {
      return super.onPreferenceTreeClick(paramPreference);
      if (paramPreference == this.mFormatPublic)
      {
        localObject = new Intent((Context)localObject, StorageWizardFormatConfirm.class);
        ((Intent)localObject).putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
        ((Intent)localObject).putExtra("format_private", false);
        startActivity((Intent)localObject);
      }
      else if (paramPreference == this.mFormatPrivate)
      {
        localObject = new Intent((Context)localObject, StorageWizardFormatConfirm.class);
        ((Intent)localObject).putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
        ((Intent)localObject).putExtra("format_private", true);
        startActivity((Intent)localObject);
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    this.mVolume = this.mStorageManager.findVolumeById(this.mVolumeId);
    if (!isVolumeValid())
    {
      getActivity().finish();
      return;
    }
    this.mStorageManager.registerListener(this.mStorageListener);
    update();
  }
  
  public void update()
  {
    if (!isVolumeValid())
    {
      getActivity().finish();
      return;
    }
    getActivity().setTitle(this.mStorageManager.getBestVolumeDescription(this.mVolume));
    Activity localActivity = getActivity();
    getPreferenceScreen().removeAll();
    if (this.mVolume.isMountedReadable())
    {
      addPreference(this.mSummary);
      Object localObject = this.mVolume.getPath();
      long l1 = ((File)localObject).getTotalSpace();
      long l2 = l1 - ((File)localObject).getFreeSpace();
      localObject = Formatter.formatBytes(getResources(), l2, 0);
      this.mSummary.setTitle(TextUtils.expandTemplate(getText(2131691771), new CharSequence[] { ((Formatter.BytesResult)localObject).value, ((Formatter.BytesResult)localObject).units }));
      this.mSummary.setSummary(getString(2131691772, new Object[] { Formatter.formatFileSize(localActivity, l1) }));
      this.mSummary.setPercent((int)(100L * l2 / l1));
    }
    if (this.mVolume.getState() == 0) {
      addPreference(this.mMount);
    }
    if (this.mVolume.isMountedReadable()) {
      getButtonBar().setVisibility(0);
    }
    addPreference(this.mFormatPublic);
    if ((this.mDisk.isAdoptable()) && (this.mIsPermittedToAdopt)) {
      addPreference(this.mFormatPrivate);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\PublicVolumeSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */