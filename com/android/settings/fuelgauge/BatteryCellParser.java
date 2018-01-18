package com.android.settings.fuelgauge;

import android.os.BatteryStats.HistoryItem;
import android.util.SparseIntArray;
import com.android.settingslib.BatteryInfo.BatteryDataParser;

public class BatteryCellParser
  implements BatteryInfo.BatteryDataParser, BatteryActiveView.BatteryActiveProvider
{
  private final SparseIntArray mData = new SparseIntArray();
  private long mLastTime;
  private int mLastValue;
  private long mLength;
  
  private int getColor(int paramInt)
  {
    return com.android.settings.Utils.BADNESS_COLORS[paramInt];
  }
  
  public SparseIntArray getColorArray()
  {
    SparseIntArray localSparseIntArray = new SparseIntArray();
    int i = 0;
    while (i < this.mData.size())
    {
      localSparseIntArray.put(this.mData.keyAt(i), getColor(this.mData.valueAt(i)));
      i += 1;
    }
    return localSparseIntArray;
  }
  
  public long getPeriod()
  {
    return this.mLength;
  }
  
  protected int getValue(BatteryStats.HistoryItem paramHistoryItem)
  {
    if ((paramHistoryItem.states & 0x1C0) >> 6 == 3) {
      return 0;
    }
    if ((paramHistoryItem.states & 0x200000) != 0) {
      return 1;
    }
    return ((paramHistoryItem.states & 0x38) >> 3) + 2;
  }
  
  public boolean hasData()
  {
    return this.mData.size() > 1;
  }
  
  public void onDataGap()
  {
    if (this.mLastValue != 0)
    {
      this.mData.put((int)this.mLastTime, 0);
      this.mLastValue = 0;
    }
  }
  
  public void onDataPoint(long paramLong, BatteryStats.HistoryItem paramHistoryItem)
  {
    int i = getValue(paramHistoryItem);
    if (i != this.mLastValue)
    {
      this.mData.put((int)paramLong, i);
      this.mLastValue = i;
    }
    this.mLastTime = paramLong;
  }
  
  public void onParsingDone()
  {
    if (this.mLastValue != 0)
    {
      this.mData.put((int)this.mLastTime, 0);
      this.mLastValue = 0;
    }
  }
  
  public void onParsingStarted(long paramLong1, long paramLong2)
  {
    this.mLength = (paramLong2 - paramLong1);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\BatteryCellParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */