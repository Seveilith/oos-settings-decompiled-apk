package com.android.settings.applications;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;

public class LinearColorPreference
  extends Preference
{
  int mColoredRegions = 7;
  int mGreenColor = -13587888;
  float mGreenRatio;
  LinearColorBar.OnRegionTappedListener mOnRegionTappedListener;
  int mRedColor = -5615568;
  float mRedRatio;
  int mYellowColor = -5592528;
  float mYellowRatio;
  
  public LinearColorPreference(Context paramContext)
  {
    super(paramContext);
    setLayoutResource(2130968913);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = (LinearColorBar)paramPreferenceViewHolder.findViewById(2131362439);
    paramPreferenceViewHolder.setShowIndicator(false);
    paramPreferenceViewHolder.setColors(this.mRedColor, this.mYellowColor, this.mGreenColor);
    paramPreferenceViewHolder.setRatios(this.mRedRatio, this.mYellowRatio, this.mGreenRatio);
    paramPreferenceViewHolder.setColoredRegions(this.mColoredRegions);
    paramPreferenceViewHolder.setOnRegionTappedListener(this.mOnRegionTappedListener);
  }
  
  public void setColoredRegions(int paramInt)
  {
    this.mColoredRegions = paramInt;
    notifyChanged();
  }
  
  public void setColors(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mRedColor = paramInt1;
    this.mYellowColor = paramInt2;
    this.mGreenColor = paramInt3;
    notifyChanged();
  }
  
  public void setOnRegionTappedListener(LinearColorBar.OnRegionTappedListener paramOnRegionTappedListener)
  {
    this.mOnRegionTappedListener = paramOnRegionTappedListener;
    notifyChanged();
  }
  
  public void setRatios(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.mRedRatio = paramFloat1;
    this.mYellowRatio = paramFloat2;
    this.mGreenRatio = paramFloat3;
    notifyChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\LinearColorPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */