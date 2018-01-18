package com.android.settings.datausage;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.net.NetworkPolicy;
import android.net.NetworkStatsHistory;
import android.net.NetworkStatsHistory.Entry;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.format.Formatter.BytesResult;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import com.android.settings.Utils;
import com.android.settingslib.graph.UsageView;

public class ChartDataUsagePreference
  extends Preference
{
  private static final long RESOLUTION = 524288L;
  private long mEnd;
  private final int mLimitColor;
  private NetworkStatsHistory mNetwork;
  private NetworkPolicy mPolicy;
  private int mSecondaryColor;
  private int mSeriesColor;
  private boolean mShowWifi = true;
  private long mStart;
  private int mSubId = 0;
  private final int mWarningColor;
  
  public ChartDataUsagePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setSelectable(false);
    this.mLimitColor = -765666;
    this.mWarningColor = paramContext.getTheme().obtainStyledAttributes(new int[] { 16842808 }).getColor(0, 0);
    setLayoutResource(2130968677);
  }
  
  private void bindNetworkPolicy(UsageView paramUsageView, NetworkPolicy paramNetworkPolicy, int paramInt)
  {
    CharSequence[] arrayOfCharSequence = new CharSequence[3];
    int j = 0;
    int i = 0;
    if (paramNetworkPolicy == null) {
      return;
    }
    if (paramNetworkPolicy.limitBytes != -1L)
    {
      i = this.mLimitColor;
      arrayOfCharSequence[2] = getLabel(paramNetworkPolicy.limitBytes, 2131692822, this.mLimitColor);
    }
    if (paramNetworkPolicy.warningBytes != -1L)
    {
      paramUsageView.setDividerLoc((int)(paramNetworkPolicy.warningBytes / 524288L));
      float f = (float)(paramNetworkPolicy.warningBytes / 524288L) / paramInt;
      paramUsageView.setSideLabelWeights(1.0F - f, f);
      j = this.mWarningColor;
      arrayOfCharSequence[1] = getLabel(paramNetworkPolicy.warningBytes, 2131692821, this.mWarningColor);
    }
    paramUsageView.setSideLabels(arrayOfCharSequence);
    paramUsageView.setDividerColors(j, i);
  }
  
  private void calcPoints(UsageView paramUsageView)
  {
    SparseIntArray localSparseIntArray = new SparseIntArray();
    NetworkStatsHistory.Entry localEntry = null;
    long l1 = 0L;
    int i = this.mNetwork.getIndexAfter(this.mStart);
    int j = this.mNetwork.getIndexAfter(this.mEnd);
    if (i < 0) {
      return;
    }
    localSparseIntArray.put(0, 0);
    while (i <= j)
    {
      localEntry = this.mNetwork.getValues(i, localEntry);
      long l2 = localEntry.bucketStart;
      long l3 = localEntry.bucketDuration;
      l1 += localEntry.rxBytes + localEntry.txBytes;
      localSparseIntArray.put(toInt(l2 - this.mStart + 1L), (int)(l1 / 524288L));
      localSparseIntArray.put(toInt(l2 + l3 - this.mStart), (int)(l1 / 524288L));
      i += 1;
    }
    if (localSparseIntArray.size() > 1) {
      paramUsageView.addPath(localSparseIntArray);
    }
  }
  
  private CharSequence getLabel(long paramLong, int paramInt1, int paramInt2)
  {
    Object localObject = Formatter.formatBytes(getContext().getResources(), paramLong, 1);
    localObject = TextUtils.expandTemplate(getContext().getText(paramInt1), new CharSequence[] { ((Formatter.BytesResult)localObject).value, ((Formatter.BytesResult)localObject).units });
    return new SpannableStringBuilder().append((CharSequence)localObject, new ForegroundColorSpan(paramInt2), 0);
  }
  
  private int toInt(long paramLong)
  {
    return (int)(paramLong / 60000L);
  }
  
  public long getInspectEnd()
  {
    return this.mEnd;
  }
  
  public long getInspectStart()
  {
    return this.mStart;
  }
  
  public int getTop()
  {
    NetworkStatsHistory.Entry localEntry = null;
    long l1 = 0L;
    int i = this.mNetwork.getIndexBefore(this.mStart);
    int j = this.mNetwork.getIndexAfter(this.mEnd);
    while (i <= j)
    {
      localEntry = this.mNetwork.getValues(i, localEntry);
      l1 += localEntry.rxBytes + localEntry.txBytes;
      i += 1;
    }
    if (this.mPolicy != null) {}
    for (long l2 = Math.max(this.mPolicy.limitBytes, this.mPolicy.warningBytes);; l2 = 0L) {
      return (int)(Math.max(l1, l2) / 524288L);
    }
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = (UsageView)paramPreferenceViewHolder.findViewById(2131362090);
    if (this.mNetwork == null) {
      return;
    }
    int i = getTop();
    paramPreferenceViewHolder.clearPaths();
    paramPreferenceViewHolder.configureGraph(toInt(this.mEnd - this.mStart), i, false, false);
    calcPoints(paramPreferenceViewHolder);
    paramPreferenceViewHolder.setBottomLabels(new CharSequence[] { Utils.formatDateRange(getContext(), this.mStart, this.mStart), Utils.formatDateRange(getContext(), this.mEnd, this.mEnd) });
    bindNetworkPolicy(paramPreferenceViewHolder, this.mPolicy, i);
  }
  
  public void setColors(int paramInt1, int paramInt2)
  {
    this.mSeriesColor = paramInt1;
    this.mSecondaryColor = paramInt2;
    notifyChanged();
  }
  
  public void setNetworkPolicy(NetworkPolicy paramNetworkPolicy)
  {
    this.mPolicy = paramNetworkPolicy;
    long l;
    if (this.mPolicy != null)
    {
      int i = OPDataUsageUtils.getDataWarnState(getContext(), this.mSubId);
      l = OPDataUsageUtils.getDataWarnBytes(getContext(), this.mSubId);
      if (i != 1) {
        break label54;
      }
    }
    label54:
    for (this.mPolicy.warningBytes = l;; this.mPolicy.warningBytes = -1L)
    {
      notifyChanged();
      return;
    }
  }
  
  public void setNetworkStats(NetworkStatsHistory paramNetworkStatsHistory)
  {
    this.mNetwork = paramNetworkStatsHistory;
    notifyChanged();
  }
  
  public void setShowWifi(boolean paramBoolean)
  {
    this.mShowWifi = paramBoolean;
  }
  
  public void setSubId(int paramInt)
  {
    this.mSubId = paramInt;
  }
  
  public void setVisibleRange(long paramLong1, long paramLong2)
  {
    this.mStart = paramLong1;
    this.mEnd = paramLong2;
    notifyChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\ChartDataUsagePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */