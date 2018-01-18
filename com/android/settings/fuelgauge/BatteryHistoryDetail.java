package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.BatteryStats;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.BatteryInfo;
import com.android.settingslib.BatteryInfo.BatteryDataParser;
import com.android.settingslib.graph.UsageView;
import java.util.Locale;

public class BatteryHistoryDetail
  extends SettingsPreferenceFragment
{
  public static final String EXTRA_BROADCAST = "broadcast";
  public static final String EXTRA_STATS = "stats";
  private Intent mBatteryBroadcast;
  private BatteryFlagParser mCameraParser;
  private BatteryFlagParser mChargingParser;
  private BatteryFlagParser mCpuParser;
  private BatteryFlagParser mFlashlightParser;
  private BatteryFlagParser mGpsParser;
  int mLocaleLayoutDirection;
  private BatteryCellParser mPhoneParser;
  private BatteryFlagParser mScreenOn;
  private BatteryStats mStats;
  private BatteryWifiParser mWifiParser;
  
  private void bindData(BatteryActiveView.BatteryActiveProvider paramBatteryActiveProvider, int paramInt1, int paramInt2)
  {
    View localView = getView().findViewById(paramInt2);
    if (paramBatteryActiveProvider.hasData()) {}
    for (paramInt2 = 0;; paramInt2 = 8)
    {
      localView.setVisibility(paramInt2);
      ((TextView)localView.findViewById(16908310)).setText(paramInt1);
      TextView localTextView = (TextView)localView.findViewById(16908310);
      paramInt1 = localTextView.getLayoutDirection();
      if ((this.mLocaleLayoutDirection != paramInt1) && (this.mLocaleLayoutDirection == 1)) {
        localTextView.setTextDirection(4);
      }
      ((BatteryActiveView)localView.findViewById(2131361974)).setProvider(paramBatteryActiveProvider);
      return;
    }
  }
  
  private void updateEverything()
  {
    BatteryInfo localBatteryInfo = BatteryInfo.getBatteryInfo(getContext(), this.mBatteryBroadcast, this.mStats, SystemClock.elapsedRealtime() * 1000L);
    View localView = getView();
    localBatteryInfo.bindHistory((UsageView)localView.findViewById(2131361978), new BatteryInfo.BatteryDataParser[] { this.mChargingParser, this.mScreenOn, this.mGpsParser, this.mFlashlightParser, this.mCameraParser, this.mWifiParser, this.mCpuParser, this.mPhoneParser });
    ((TextView)localView.findViewById(2131361976)).setText(localBatteryInfo.batteryPercentString);
    ((TextView)localView.findViewById(2131361977)).setText(localBatteryInfo.remainingLabel);
    bindData(this.mChargingParser, 2131692437, 2131361986);
    bindData(this.mScreenOn, 2131692438, 2131361985);
    bindData(this.mGpsParser, 2131692439, 2131361982);
    bindData(this.mFlashlightParser, 2131692441, 2131361981);
    bindData(this.mCameraParser, 2131692440, 2131361980);
    bindData(this.mWifiParser, 2131692442, 2131361983);
    bindData(this.mCpuParser, 2131692443, 2131361984);
    bindData(this.mPhoneParser, 2131692444, 2131361979);
  }
  
  protected int getMetricsCategory()
  {
    return 51;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getArguments().getString("stats");
    this.mStats = BatteryStatsHelper.statsFromFile(getActivity(), paramBundle);
    this.mBatteryBroadcast = ((Intent)getArguments().getParcelable("broadcast"));
    paramBundle = new TypedValue();
    getContext().getTheme().resolveAttribute(16843829, paramBundle, true);
    int i = getContext().getColor(paramBundle.resourceId);
    this.mChargingParser = new BatteryFlagParser(i, false, 524288);
    this.mScreenOn = new BatteryFlagParser(i, false, 1048576);
    this.mGpsParser = new BatteryFlagParser(i, false, 536870912);
    this.mFlashlightParser = new BatteryFlagParser(i, true, 134217728);
    this.mCameraParser = new BatteryFlagParser(i, true, 2097152);
    this.mWifiParser = new BatteryWifiParser(i);
    this.mCpuParser = new BatteryFlagParser(i, false, Integer.MIN_VALUE);
    this.mPhoneParser = new BatteryCellParser();
    setHasOptionsMenu(true);
    this.mLocaleLayoutDirection = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault());
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(2130968626, paramViewGroup, false);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    updateEverything();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\BatteryHistoryDetail.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */