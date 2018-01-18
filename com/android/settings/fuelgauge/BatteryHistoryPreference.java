package com.android.settings.fuelgauge;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settings.Utils;
import com.android.settingslib.BatteryInfo;
import com.android.settingslib.BatteryInfo.BatteryDataParser;
import com.android.settingslib.graph.UsageView;

public class BatteryHistoryPreference
  extends Preference
{
  protected static final String BATTERY_HISTORY_FILE = "tmp_bat_history.bin";
  private BatteryInfo mBatteryInfo;
  private BatteryStatsHelper mHelper;
  
  public BatteryHistoryPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setLayoutResource(2130968627);
    setSelectable(true);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    if (this.mBatteryInfo == null) {
      return;
    }
    paramPreferenceViewHolder.itemView.setClickable(true);
    paramPreferenceViewHolder.setDividerAllowedAbove(true);
    ((TextView)paramPreferenceViewHolder.findViewById(2131361976)).setText(this.mBatteryInfo.batteryPercentString);
    ((TextView)paramPreferenceViewHolder.findViewById(2131361977)).setText(this.mBatteryInfo.remainingLabel);
    paramPreferenceViewHolder = (UsageView)paramPreferenceViewHolder.findViewById(2131361978);
    paramPreferenceViewHolder.findViewById(2131362652).setAlpha(0.7F);
    this.mBatteryInfo.bindHistory(paramPreferenceViewHolder, new BatteryInfo.BatteryDataParser[0]);
  }
  
  public void performClick()
  {
    if (this.mHelper == null) {
      return;
    }
    this.mHelper.storeStatsHistoryInFile("tmp_bat_history.bin");
    Bundle localBundle = new Bundle();
    localBundle.putString("stats", "tmp_bat_history.bin");
    localBundle.putParcelable("broadcast", this.mHelper.getBatteryBroadcast());
    Utils.startWithFragment(getContext(), BatteryHistoryDetail.class.getName(), localBundle, null, 0, 2131692449, null);
  }
  
  public void setStats(BatteryStatsHelper paramBatteryStatsHelper)
  {
    this.mHelper = paramBatteryStatsHelper;
    long l = SystemClock.elapsedRealtime();
    this.mBatteryInfo = BatteryInfo.getBatteryInfo(getContext(), paramBatteryStatsHelper.getBatteryBroadcast(), paramBatteryStatsHelper.getStats(), l * 1000L);
    notifyChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\BatteryHistoryPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */