package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import java.io.File;

public class StorageVolumePreference
  extends Preference
{
  private int mColor;
  private final StorageManager mStorageManager;
  private final View.OnClickListener mUnmountListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      new StorageSettings.UnmountTask(StorageVolumePreference.this.getContext(), StorageVolumePreference.-get0(StorageVolumePreference.this)).execute(new Void[0]);
    }
  };
  private int mUsedPercent = -1;
  private final VolumeInfo mVolume;
  
  public StorageVolumePreference(Context paramContext, VolumeInfo paramVolumeInfo, int paramInt)
  {
    super(paramContext);
    this.mStorageManager = ((StorageManager)paramContext.getSystemService(StorageManager.class));
    this.mVolume = paramVolumeInfo;
    this.mColor = paramInt;
    setLayoutResource(2130969014);
    setKey(paramVolumeInfo.getId());
    setTitle(this.mStorageManager.getBestVolumeDescription(paramVolumeInfo));
    Drawable localDrawable;
    if ("private".equals(paramVolumeInfo.getId()))
    {
      localDrawable = paramContext.getDrawable(2130838069);
      if (!paramVolumeInfo.isMountedReadable()) {
        break label261;
      }
      File localFile = paramVolumeInfo.getPath();
      long l1 = localFile.getFreeSpace();
      long l2 = localFile.getTotalSpace();
      long l3 = l2 - l1;
      setSummary(paramContext.getString(2131691770, new Object[] { Formatter.formatFileSize(paramContext, l3), Formatter.formatFileSize(paramContext, l2) }));
      if (l2 > 0L) {
        this.mUsedPercent = ((int)(100L * l3 / l2));
      }
      if (l1 < this.mStorageManager.getStorageLowBytes(localFile))
      {
        this.mColor = StorageSettings.COLOR_WARNING;
        localDrawable = paramContext.getDrawable(2130838109);
      }
    }
    for (;;)
    {
      localDrawable.mutate();
      localDrawable.setTint(this.mColor);
      setIcon(localDrawable);
      if ((paramVolumeInfo.getType() == 0) && (paramVolumeInfo.isMountedReadable())) {
        setWidgetLayoutResource(2130968923);
      }
      return;
      localDrawable = paramContext.getDrawable(2130838081);
      break;
      label261:
      setSummary(paramVolumeInfo.getStateDescription());
      this.mUsedPercent = -1;
    }
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    Object localObject = (ImageView)paramPreferenceViewHolder.findViewById(2131362450);
    if (localObject != null)
    {
      ((ImageView)localObject).setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(2131493741)));
      ((ImageView)localObject).setOnClickListener(this.mUnmountListener);
    }
    localObject = (ProgressBar)paramPreferenceViewHolder.findViewById(16908301);
    if ((this.mVolume.getType() == 1) && (this.mUsedPercent != -1))
    {
      ((ProgressBar)localObject).setVisibility(0);
      ((ProgressBar)localObject).setProgress(this.mUsedPercent);
      ((ProgressBar)localObject).setProgressTintList(ColorStateList.valueOf(this.mColor));
    }
    for (;;)
    {
      super.onBindViewHolder(paramPreferenceViewHolder);
      return;
      ((ProgressBar)localObject).setVisibility(8);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageVolumePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */