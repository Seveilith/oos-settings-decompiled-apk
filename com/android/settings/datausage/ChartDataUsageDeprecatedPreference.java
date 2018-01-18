package com.android.settings.datausage;

import android.content.Context;
import android.net.NetworkPolicy;
import android.net.NetworkStatsHistory;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import com.android.settings.widget.ChartDataUsageView;
import com.android.settings.widget.ChartDataUsageView.DataUsageChartListener;

public class ChartDataUsageDeprecatedPreference
  extends Preference
{
  private static final String TAG = "ChartDataUsageDeprecatedPreference";
  private ChartDataUsageView mChartDataUsageView = null;
  private ChartDataUsageView.DataUsageChartListener mChartListener = null;
  private Context mContext = null;
  private NetworkStatsHistory mDetail;
  private long mInspectEnd;
  private boolean mInspectRangeChanged = false;
  private long mInspectStart;
  private NetworkStatsHistory mNetwork;
  private NetworkPolicy mPolicy;
  private long mSelectLeft;
  private long mSelectRight;
  private boolean mShowWifi = true;
  private int mSubId = 0;
  
  public ChartDataUsageDeprecatedPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    setLayoutResource(2130968672);
  }
  
  private void updateChart()
  {
    this.mChartDataUsageView.setShowWifi(this.mShowWifi);
    this.mChartDataUsageView.setSubId(this.mSubId);
    this.mChartDataUsageView.setListener(this.mChartListener);
    this.mChartDataUsageView.bindNetworkPolicy(this.mPolicy);
    this.mChartDataUsageView.bindNetworkStats(this.mNetwork);
    this.mChartDataUsageView.bindDetailNetworkStats(this.mDetail);
    this.mChartDataUsageView.setVisibleRange(this.mInspectStart, this.mInspectEnd, this.mSelectLeft, this.mSelectRight);
    if (BillingCycleSettings.isShowDataUsage(this.mContext)) {}
    for (int i = 0;; i = 8)
    {
      this.mChartDataUsageView.setDateSelectionSweepVisible(i);
      return;
    }
  }
  
  public void bindDetailNetworkStats(NetworkStatsHistory paramNetworkStatsHistory)
  {
    this.mDetail = paramNetworkStatsHistory;
    notifyChanged();
  }
  
  public void bindNetworkPolicy(NetworkPolicy paramNetworkPolicy)
  {
    this.mPolicy = paramNetworkPolicy;
  }
  
  public void bindNetworkStats(NetworkStatsHistory paramNetworkStatsHistory)
  {
    this.mNetwork = paramNetworkStatsHistory;
    notifyChanged();
  }
  
  public long getInspectEnd()
  {
    if (this.mChartDataUsageView != null) {
      return this.mChartDataUsageView.getInspectEnd();
    }
    return this.mInspectEnd;
  }
  
  public long getInspectLeft()
  {
    if (this.mChartDataUsageView != null) {
      return this.mChartDataUsageView.getInspectLeft();
    }
    return this.mSelectLeft;
  }
  
  public long getInspectRight()
  {
    if (this.mChartDataUsageView != null) {
      return this.mChartDataUsageView.getInspectRight();
    }
    return this.mSelectRight;
  }
  
  public long getInspectStart()
  {
    if (this.mChartDataUsageView != null) {
      return this.mChartDataUsageView.getInspectStart();
    }
    return this.mInspectStart;
  }
  
  public long getLimitBytes()
  {
    return this.mChartDataUsageView.getLimitBytes();
  }
  
  public long getWarningBytes()
  {
    return this.mChartDataUsageView.getWarningBytes();
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mChartDataUsageView = ((ChartDataUsageView)paramPreferenceViewHolder.itemView);
    if (!this.mInspectRangeChanged) {
      updateChart();
    }
    Log.d("ChartDataUsageDeprecatedPreference", "onBindViewHolder mChartDataUsageView = " + this.mChartDataUsageView);
  }
  
  public void setInspectRangeChanged()
  {
    this.mInspectRangeChanged = true;
  }
  
  public void setListener(ChartDataUsageView.DataUsageChartListener paramDataUsageChartListener)
  {
    this.mChartListener = paramDataUsageChartListener;
  }
  
  public void setShowWifi(boolean paramBoolean)
  {
    this.mShowWifi = paramBoolean;
  }
  
  public void setSubId(int paramInt)
  {
    this.mSubId = paramInt;
  }
  
  public void setVisibleRange(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    this.mInspectStart = paramLong1;
    this.mInspectEnd = paramLong2;
    this.mSelectLeft = paramLong3;
    this.mSelectRight = paramLong4;
    notifyChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\ChartDataUsageDeprecatedPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */