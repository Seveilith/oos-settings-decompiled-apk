package com.android.settings.applications;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.android.internal.app.procstats.ProcessStats;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;

public abstract class ProcessStatsBase
  extends SettingsPreferenceFragment
  implements AdapterView.OnItemSelectedListener
{
  protected static final String ARG_DURATION_INDEX = "duration_index";
  protected static final String ARG_TRANSFER_STATS = "transfer_stats";
  private static final String DURATION = "duration";
  private static final long DURATION_QUANTUM = ProcessStats.COMMIT_PERIOD;
  protected static final int NUM_DURATIONS = 4;
  protected static int[] sDurationLabels = { 2131692550, 2131692551, 2131692552, 2131692553 };
  protected static long[] sDurations = { 10800000L - DURATION_QUANTUM / 2L, 21600000L - DURATION_QUANTUM / 2L, 43200000L - DURATION_QUANTUM / 2L, 86400000L - DURATION_QUANTUM / 2L };
  protected int mDurationIndex;
  private ArrayAdapter<String> mFilterAdapter;
  private Spinner mFilterSpinner;
  private ViewGroup mSpinnerHeader;
  protected ProcStatsData mStatsManager;
  
  public static void launchMemoryDetail(SettingsActivity paramSettingsActivity, ProcStatsData.MemInfo paramMemInfo, ProcStatsPackageEntry paramProcStatsPackageEntry, boolean paramBoolean)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("package_entry", paramProcStatsPackageEntry);
    localBundle.putDouble("weight_to_ram", paramMemInfo.weightToRam);
    localBundle.putLong("total_time", paramMemInfo.memTotalTime);
    localBundle.putDouble("max_memory_usage", paramMemInfo.usedWeight * paramMemInfo.weightToRam);
    localBundle.putDouble("total_scale", paramMemInfo.totalScale);
    if (paramBoolean) {}
    for (paramBoolean = false;; paramBoolean = true)
    {
      localBundle.putBoolean("hideInfoButton", paramBoolean);
      paramSettingsActivity.startPreferencePanel(ProcessStatsDetail.class.getName(), localBundle, 2131693503, null, null, 0);
      return;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Object localObject = getArguments();
    Activity localActivity = getActivity();
    boolean bool;
    int i;
    if (paramBundle == null)
    {
      if (localObject == null) {
        break label101;
      }
      bool = ((Bundle)localObject).getBoolean("transfer_stats", false);
      this.mStatsManager = new ProcStatsData(localActivity, bool);
      if (paramBundle == null) {
        break label106;
      }
      i = paramBundle.getInt("duration_index");
      label60:
      this.mDurationIndex = i;
      localObject = this.mStatsManager;
      if (paramBundle == null) {
        break label127;
      }
    }
    label101:
    label106:
    label127:
    for (long l = paramBundle.getLong("duration", sDurations[0]);; l = sDurations[0])
    {
      ((ProcStatsData)localObject).setDuration(l);
      return;
      bool = true;
      break;
      bool = false;
      break;
      if (localObject != null)
      {
        i = ((Bundle)localObject).getInt("duration_index");
        break label60;
      }
      i = 0;
      break label60;
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (getActivity().isChangingConfigurations()) {
      this.mStatsManager.xferStats();
    }
  }
  
  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    this.mDurationIndex = paramInt;
    this.mStatsManager.setDuration(sDurations[paramInt]);
    refreshUi();
  }
  
  public void onNothingSelected(AdapterView<?> paramAdapterView)
  {
    this.mFilterSpinner.setSelection(0);
  }
  
  public void onResume()
  {
    super.onResume();
    this.mStatsManager.refreshStats(false);
    refreshUi();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putLong("duration", this.mStatsManager.getDuration());
    paramBundle.putInt("duration_index", this.mDurationIndex);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    this.mSpinnerHeader = ((ViewGroup)setPinnedHeaderView(2130968621));
    this.mSpinnerHeader.findViewById(2131361970).setVisibility(8);
    this.mFilterSpinner = ((Spinner)this.mSpinnerHeader.findViewById(2131361969));
    this.mFilterSpinner.setVisibility(0);
    this.mFilterAdapter = new ArrayAdapter(getActivity(), 2130968700);
    this.mFilterAdapter.setDropDownViewResource(17367049);
    int i = 0;
    while (i < 4)
    {
      this.mFilterAdapter.add(getString(sDurationLabels[i]));
      i += 1;
    }
    this.mFilterSpinner.setAdapter(this.mFilterAdapter);
    this.mFilterSpinner.setSelection(this.mDurationIndex);
    this.mFilterSpinner.setOnItemSelectedListener(this);
  }
  
  public abstract void refreshUi();
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ProcessStatsBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */