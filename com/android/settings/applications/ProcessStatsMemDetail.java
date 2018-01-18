package com.android.settings.applications;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.settings.InstrumentedFragment;
import com.android.settings.Utils;

public class ProcessStatsMemDetail
  extends InstrumentedFragment
{
  public static final String EXTRA_MEM_CACHED_WEIGHT = "mem_cached_weight";
  public static final String EXTRA_MEM_FREE_WEIGHT = "mem_free_weight";
  public static final String EXTRA_MEM_KERNEL_WEIGHT = "mem_kernel_weight";
  public static final String EXTRA_MEM_NATIVE_WEIGHT = "mem_native_weight";
  public static final String EXTRA_MEM_STATE_WEIGHTS = "mem_state_weights";
  public static final String EXTRA_MEM_TIMES = "mem_times";
  public static final String EXTRA_MEM_TOTAL_WEIGHT = "mem_total_weight";
  public static final String EXTRA_MEM_ZRAM_WEIGHT = "mem_zram_weight";
  public static final String EXTRA_TOTAL_TIME = "total_time";
  public static final String EXTRA_USE_USS = "use_uss";
  double mMemCachedWeight;
  double mMemFreeWeight;
  double mMemKernelWeight;
  double mMemNativeWeight;
  private ViewGroup mMemStateParent;
  double[] mMemStateWeights;
  long[] mMemTimes;
  double mMemTotalWeight;
  private ViewGroup mMemUseParent;
  double mMemZRamWeight;
  private View mRootView;
  long mTotalTime;
  boolean mUseUss;
  
  private void addDetailsItem(ViewGroup paramViewGroup, CharSequence paramCharSequence1, float paramFloat, CharSequence paramCharSequence2)
  {
    Object localObject = getActivity().getLayoutInflater();
    ViewGroup localViewGroup = (ViewGroup)((LayoutInflater)localObject).inflate(2130968614, null);
    ((LayoutInflater)localObject).inflate(2130969108, (ViewGroup)localViewGroup.findViewById(16908312));
    paramViewGroup.addView(localViewGroup);
    localViewGroup.findViewById(16908294).setVisibility(8);
    paramViewGroup = (TextView)localViewGroup.findViewById(16908310);
    localObject = (TextView)localViewGroup.findViewById(16908308);
    paramViewGroup.setText(paramCharSequence1);
    ((TextView)localObject).setText(paramCharSequence2);
    ((ProgressBar)localViewGroup.findViewById(16908301)).setProgress(Math.round(100.0F * paramFloat));
  }
  
  private void addMemUseDetailsItem(ViewGroup paramViewGroup, CharSequence paramCharSequence, double paramDouble)
  {
    if (paramDouble > 0.0D) {
      addDetailsItem(paramViewGroup, paramCharSequence, (float)(paramDouble / this.mMemTotalWeight), Formatter.formatShortFileSize(getActivity(), (1024.0D * paramDouble / this.mTotalTime)));
    }
  }
  
  private void createDetails()
  {
    this.mMemStateParent = ((ViewGroup)this.mRootView.findViewById(2131362461));
    this.mMemUseParent = ((ViewGroup)this.mRootView.findViewById(2131362462));
    fillMemStateSection();
    fillMemUseSection();
  }
  
  private void fillMemStateSection()
  {
    CharSequence[] arrayOfCharSequence = getResources().getTextArray(2131427443);
    int i = 0;
    while (i < 4)
    {
      if (this.mMemTimes[i] > 0L)
      {
        float f = (float)this.mMemTimes[i] / (float)this.mTotalTime;
        addDetailsItem(this.mMemStateParent, arrayOfCharSequence[i], f, Formatter.formatShortElapsedTime(getActivity(), this.mMemTimes[i]));
      }
      i += 1;
    }
  }
  
  private void fillMemUseSection()
  {
    CharSequence[] arrayOfCharSequence = getResources().getTextArray(2131427444);
    addMemUseDetailsItem(this.mMemUseParent, getResources().getText(2131692544), this.mMemKernelWeight);
    addMemUseDetailsItem(this.mMemUseParent, getResources().getText(2131692547), this.mMemZRamWeight);
    addMemUseDetailsItem(this.mMemUseParent, getResources().getText(2131692545), this.mMemNativeWeight);
    int i = 0;
    while (i < 14)
    {
      addMemUseDetailsItem(this.mMemUseParent, arrayOfCharSequence[i], this.mMemStateWeights[i]);
      i += 1;
    }
    addMemUseDetailsItem(this.mMemUseParent, getResources().getText(2131692546), this.mMemCachedWeight);
    addMemUseDetailsItem(this.mMemUseParent, getResources().getText(2131692548), this.mMemFreeWeight);
    addMemUseDetailsItem(this.mMemUseParent, getResources().getText(2131692549), this.mMemTotalWeight);
  }
  
  protected int getMetricsCategory()
  {
    return 22;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getArguments();
    this.mMemTimes = paramBundle.getLongArray("mem_times");
    this.mMemStateWeights = paramBundle.getDoubleArray("mem_state_weights");
    this.mMemCachedWeight = paramBundle.getDouble("mem_cached_weight");
    this.mMemFreeWeight = paramBundle.getDouble("mem_free_weight");
    this.mMemZRamWeight = paramBundle.getDouble("mem_zram_weight");
    this.mMemKernelWeight = paramBundle.getDouble("mem_kernel_weight");
    this.mMemNativeWeight = paramBundle.getDouble("mem_native_weight");
    this.mMemTotalWeight = paramBundle.getDouble("mem_total_weight");
    this.mUseUss = paramBundle.getBoolean("use_uss");
    this.mTotalTime = paramBundle.getLong("total_time");
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(2130968941, paramViewGroup, false);
    Utils.prepareCustomPreferencesList(paramViewGroup, paramLayoutInflater, paramLayoutInflater, false);
    this.mRootView = paramLayoutInflater;
    createDetails();
    return paramLayoutInflater;
  }
  
  public void onPause()
  {
    super.onPause();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ProcessStatsMemDetail.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */