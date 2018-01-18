package com.android.settings.fuelgauge;

import android.os.BatteryStats.HistoryItem;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.settingslib.BatteryInfo.BatteryDataParser;

public class BatteryFlagParser
  implements BatteryInfo.BatteryDataParser, BatteryActiveView.BatteryActiveProvider
{
  private final int mAccentColor;
  private final SparseBooleanArray mData = new SparseBooleanArray();
  private final int mFlag;
  private boolean mLastSet;
  private long mLastTime;
  private long mLength;
  private final boolean mState2;
  
  public BatteryFlagParser(int paramInt1, boolean paramBoolean, int paramInt2)
  {
    this.mAccentColor = paramInt1;
    this.mFlag = paramInt2;
    this.mState2 = paramBoolean;
  }
  
  private int getColor(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.mAccentColor;
    }
    return 0;
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
  
  public boolean hasData()
  {
    return this.mData.size() > 1;
  }
  
  protected boolean isSet(BatteryStats.HistoryItem paramHistoryItem)
  {
    if (this.mState2) {}
    for (int i = paramHistoryItem.states2; (i & this.mFlag) != 0; i = paramHistoryItem.states) {
      return true;
    }
    return false;
  }
  
  public void onDataGap()
  {
    if (this.mLastSet)
    {
      this.mData.put((int)this.mLastTime, false);
      this.mLastSet = false;
    }
  }
  
  public void onDataPoint(long paramLong, BatteryStats.HistoryItem paramHistoryItem)
  {
    boolean bool = isSet(paramHistoryItem);
    if (bool != this.mLastSet)
    {
      this.mData.put((int)paramLong, bool);
      this.mLastSet = bool;
    }
    this.mLastTime = paramLong;
  }
  
  public void onParsingDone()
  {
    if (this.mLastSet)
    {
      this.mData.put((int)this.mLastTime, false);
      this.mLastSet = false;
    }
  }
  
  public void onParsingStarted(long paramLong1, long paramLong2)
  {
    this.mLength = (paramLong2 - paramLong1);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\BatteryFlagParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */