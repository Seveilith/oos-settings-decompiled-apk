package com.android.settings.deviceinfo;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.format.Formatter;
import android.widget.ProgressBar;

public class StorageItemPreference
  extends Preference
{
  private static final int PROGRESS_MAX = 100;
  private int progress = -1;
  private ProgressBar progressBar;
  public int userHandle;
  
  public StorageItemPreference(Context paramContext)
  {
    super(paramContext);
    setLayoutResource(2130969012);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    this.progressBar = ((ProgressBar)paramPreferenceViewHolder.findViewById(16908301));
    updateProgressBar();
    super.onBindViewHolder(paramPreferenceViewHolder);
  }
  
  public void setStorageSize(long paramLong1, long paramLong2)
  {
    String str;
    if (paramLong1 == 0L)
    {
      str = String.valueOf(0);
      setSummary(str);
      if (paramLong2 != 0L) {
        break label47;
      }
    }
    label47:
    for (this.progress = 0;; this.progress = ((int)(100L * paramLong1 / paramLong2)))
    {
      updateProgressBar();
      return;
      str = Formatter.formatFileSize(getContext(), paramLong1);
      break;
    }
  }
  
  protected void updateProgressBar()
  {
    if (this.progressBar == null) {
      return;
    }
    if (this.progress == -1)
    {
      this.progressBar.setVisibility(8);
      return;
    }
    this.progressBar.setVisibility(0);
    this.progressBar.setMax(100);
    this.progressBar.setProgress(this.progress);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageItemPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */