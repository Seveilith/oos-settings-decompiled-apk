package com.android.settings.applications;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.preference.PreferenceFrameLayout.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.InstrumentedFragment;

public class BackgroundCheckSummary
  extends InstrumentedFragment
{
  private LayoutInflater mInflater;
  
  protected int getMetricsCategory()
  {
    return 258;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mInflater = paramLayoutInflater;
    paramLayoutInflater = this.mInflater.inflate(2130968622, paramViewGroup, false);
    if ((paramViewGroup instanceof PreferenceFrameLayout)) {
      ((PreferenceFrameLayout.LayoutParams)paramLayoutInflater.getLayoutParams()).removeBorders = true;
    }
    paramViewGroup = getChildFragmentManager().beginTransaction();
    paramViewGroup.add(2131361972, new AppOpsCategory(AppOpsState.RUN_IN_BACKGROUND_TEMPLATE, true), "appops");
    paramViewGroup.commitAllowingStateLoss();
    return paramLayoutInflater;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\BackgroundCheckSummary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */