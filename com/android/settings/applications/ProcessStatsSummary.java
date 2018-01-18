package com.android.settings.applications;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.text.format.Formatter;
import android.text.format.Formatter.BytesResult;
import android.util.TypedValue;
import com.android.settings.SummaryPreference;
import com.android.settings.Utils;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import java.util.List;

public class ProcessStatsSummary
  extends ProcessStatsBase
  implements Preference.OnPreferenceClickListener
{
  private static final String KEY_APP_LIST = "apps_list";
  private static final String KEY_AVERAGY_USED = "average_used";
  private static final String KEY_FREE = "free";
  private static final String KEY_PERFORMANCE = "performance";
  private static final String KEY_STATUS_HEADER = "status_header";
  private static final String KEY_TOTAL_MEMORY = "total_memory";
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new ProcessStatsSummary.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
    }
  };
  private Preference mAppListPreference;
  private Preference mAverageUsed;
  private Preference mFree;
  private Preference mPerformance;
  private SummaryPreference mSummaryPref;
  private Preference mTotalMemory;
  
  protected int getMetricsCategory()
  {
    return 202;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230831);
    this.mSummaryPref = ((SummaryPreference)findPreference("status_header"));
    paramBundle = new TypedValue();
    getContext().getTheme().resolveAttribute(16843829, paramBundle, true);
    int i = getResources().getColor(paramBundle.resourceId, null);
    this.mSummaryPref.setColors(i, i, getContext().getColor(2131493678));
    this.mPerformance = findPreference("performance");
    this.mTotalMemory = findPreference("total_memory");
    this.mAverageUsed = findPreference("average_used");
    this.mFree = findPreference("free");
    this.mAppListPreference = findPreference("apps_list");
    this.mAppListPreference.setOnPreferenceClickListener(this);
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mAppListPreference)
    {
      paramPreference = new Bundle();
      paramPreference.putBoolean("transfer_stats", true);
      paramPreference.putInt("duration_index", this.mDurationIndex);
      this.mStatsManager.xferStats();
      startFragment(this, ProcessStatsUi.class.getName(), 2131693504, 0, paramPreference);
      return true;
    }
    return false;
  }
  
  public void refreshUi()
  {
    Object localObject1 = getContext();
    Object localObject2 = this.mStatsManager.getMemInfo();
    double d1 = ((ProcStatsData.MemInfo)localObject2).realUsedRam;
    double d2 = ((ProcStatsData.MemInfo)localObject2).realTotalRam;
    double d3 = ((ProcStatsData.MemInfo)localObject2).realFreeRam;
    localObject2 = Formatter.formatBytes(((Context)localObject1).getResources(), d1, 1);
    String str1 = Formatter.formatShortFileSize((Context)localObject1, d2);
    String str2 = Formatter.formatShortFileSize((Context)localObject1, d3);
    localObject1 = getResources().getTextArray(2131427438);
    int i = this.mStatsManager.getMemState();
    if ((i >= 0) && (i < localObject1.length - 1)) {}
    for (localObject1 = localObject1[i];; localObject1 = localObject1[(localObject1.length - 1)])
    {
      this.mSummaryPref.setAmount(((Formatter.BytesResult)localObject2).value);
      this.mSummaryPref.setUnits(((Formatter.BytesResult)localObject2).units);
      float f = (float)(d1 / (d3 + d1));
      this.mSummaryPref.setRatios(f, 0.0F, 1.0F - f);
      this.mPerformance.setSummary((CharSequence)localObject1);
      this.mTotalMemory.setSummary(str1);
      this.mAverageUsed.setSummary(Utils.formatPercentage(d1, d2));
      this.mFree.setSummary(str2);
      localObject1 = getString(sDurationLabels[this.mDurationIndex]);
      i = this.mStatsManager.getEntries().size();
      this.mAppListPreference.setSummary(getResources().getQuantityString(2131951648, i, new Object[] { Integer.valueOf(i), localObject1 }));
      return;
    }
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mSummaryLoader;
    
    public SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        Object localObject1 = new ProcStatsData(this.mContext, false);
        ((ProcStatsData)localObject1).setDuration(ProcessStatsSummary.sDurations[0]);
        Object localObject2 = ((ProcStatsData)localObject1).getMemInfo();
        localObject1 = Formatter.formatShortFileSize(this.mContext, ((ProcStatsData.MemInfo)localObject2).realUsedRam);
        localObject2 = Formatter.formatShortFileSize(this.mContext, ((ProcStatsData.MemInfo)localObject2).realTotalRam);
        this.mSummaryLoader.setSummary(this, this.mContext.getString(2131693588, new Object[] { localObject1, localObject2 }));
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ProcessStatsSummary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */