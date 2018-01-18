package com.android.settings.fuelgauge;

import android.os.BatteryStats.HistoryItem;

public class BatteryWifiParser
  extends BatteryFlagParser
{
  public BatteryWifiParser(int paramInt)
  {
    super(paramInt, false, 0);
  }
  
  protected boolean isSet(BatteryStats.HistoryItem paramHistoryItem)
  {
    switch ((paramHistoryItem.states2 & 0xF) >> 0)
    {
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    default: 
      return true;
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\BatteryWifiParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */