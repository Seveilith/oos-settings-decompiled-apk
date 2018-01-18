package com.android.settings.deviceinfo;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;

public class StorageSummaryPreference
  extends Preference
{
  private int mPercent = -1;
  
  public StorageSummaryPreference(Context paramContext)
  {
    super(paramContext);
    setLayoutResource(2130969013);
    setEnabled(false);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    Object localObject = (ProgressBar)paramPreferenceViewHolder.findViewById(16908301);
    if (this.mPercent != -1)
    {
      ((ProgressBar)localObject).setVisibility(0);
      ((ProgressBar)localObject).setProgress(this.mPercent);
      localObject = (TextView)paramPreferenceViewHolder.findViewById(2131362564);
      if (!OPUtils.isZh(SettingsBaseApplication.mApplication)) {
        break label70;
      }
      ((TextView)localObject).setVisibility(0);
    }
    for (;;)
    {
      super.onBindViewHolder(paramPreferenceViewHolder);
      return;
      ((ProgressBar)localObject).setVisibility(8);
      break;
      label70:
      ((TextView)localObject).setVisibility(8);
    }
  }
  
  public void setPercent(int paramInt)
  {
    this.mPercent = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageSummaryPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */