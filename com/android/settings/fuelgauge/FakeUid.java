package com.android.settings.fuelgauge;

import android.os.BatteryStats.ControllerActivityCounter;
import android.os.BatteryStats.Timer;
import android.os.BatteryStats.Uid;
import android.os.BatteryStats.Uid.Pid;
import android.os.BatteryStats.Uid.Pkg;
import android.os.BatteryStats.Uid.Proc;
import android.os.BatteryStats.Uid.Sensor;
import android.os.BatteryStats.Uid.Wakelock;
import android.util.ArrayMap;
import android.util.SparseArray;

public class FakeUid
  extends BatteryStats.Uid
{
  private final int mUid;
  
  public FakeUid(int paramInt)
  {
    this.mUid = paramInt;
  }
  
  public BatteryStats.Timer getAudioTurnedOnTimer()
  {
    return null;
  }
  
  public BatteryStats.ControllerActivityCounter getBluetoothControllerActivity()
  {
    return null;
  }
  
  public BatteryStats.Timer getBluetoothScanTimer()
  {
    return null;
  }
  
  public BatteryStats.Timer getCameraTurnedOnTimer()
  {
    return null;
  }
  
  public long getCpuPowerMaUs(int paramInt)
  {
    return 0L;
  }
  
  public BatteryStats.Timer getFlashlightTurnedOnTimer()
  {
    return null;
  }
  
  public BatteryStats.Timer getForegroundActivityTimer()
  {
    return null;
  }
  
  public long getFullWifiLockTime(long paramLong, int paramInt)
  {
    return 0L;
  }
  
  public ArrayMap<String, ? extends BatteryStats.Timer> getJobStats()
  {
    return null;
  }
  
  public int getMobileRadioActiveCount(int paramInt)
  {
    return 0;
  }
  
  public long getMobileRadioActiveTime(int paramInt)
  {
    return 0L;
  }
  
  public long getMobileRadioApWakeupCount(int paramInt)
  {
    return 0L;
  }
  
  public BatteryStats.ControllerActivityCounter getModemControllerActivity()
  {
    return null;
  }
  
  public long getNetworkActivityBytes(int paramInt1, int paramInt2)
  {
    return 0L;
  }
  
  public long getNetworkActivityPackets(int paramInt1, int paramInt2)
  {
    return 0L;
  }
  
  public ArrayMap<String, ? extends BatteryStats.Uid.Pkg> getPackageStats()
  {
    return null;
  }
  
  public SparseArray<? extends BatteryStats.Uid.Pid> getPidStats()
  {
    return null;
  }
  
  public long getProcessStateTime(int paramInt1, long paramLong, int paramInt2)
  {
    return 0L;
  }
  
  public BatteryStats.Timer getProcessStateTimer(int paramInt)
  {
    return null;
  }
  
  public ArrayMap<String, ? extends BatteryStats.Uid.Proc> getProcessStats()
  {
    return null;
  }
  
  public SparseArray<? extends BatteryStats.Uid.Sensor> getSensorStats()
  {
    return null;
  }
  
  public ArrayMap<String, ? extends BatteryStats.Timer> getSyncStats()
  {
    return null;
  }
  
  public long getSystemCpuTimeUs(int paramInt)
  {
    return 0L;
  }
  
  public long getTimeAtCpuSpeed(int paramInt1, int paramInt2, int paramInt3)
  {
    return 0L;
  }
  
  public int getUid()
  {
    return this.mUid;
  }
  
  public int getUserActivityCount(int paramInt1, int paramInt2)
  {
    return 0;
  }
  
  public long getUserCpuTimeUs(int paramInt)
  {
    return 0L;
  }
  
  public BatteryStats.Timer getVibratorOnTimer()
  {
    return null;
  }
  
  public BatteryStats.Timer getVideoTurnedOnTimer()
  {
    return null;
  }
  
  public ArrayMap<String, ? extends BatteryStats.Uid.Wakelock> getWakelockStats()
  {
    return null;
  }
  
  public int getWifiBatchedScanCount(int paramInt1, int paramInt2)
  {
    return 0;
  }
  
  public long getWifiBatchedScanTime(int paramInt1, long paramLong, int paramInt2)
  {
    return 0L;
  }
  
  public BatteryStats.ControllerActivityCounter getWifiControllerActivity()
  {
    return null;
  }
  
  public long getWifiMulticastTime(long paramLong, int paramInt)
  {
    return 0L;
  }
  
  public long getWifiRadioApWakeupCount(int paramInt)
  {
    return 0L;
  }
  
  public long getWifiRunningTime(long paramLong, int paramInt)
  {
    return 0L;
  }
  
  public int getWifiScanCount(int paramInt)
  {
    return 0;
  }
  
  public long getWifiScanTime(long paramLong, int paramInt)
  {
    return 0L;
  }
  
  public boolean hasNetworkActivity()
  {
    return false;
  }
  
  public boolean hasUserActivity()
  {
    return false;
  }
  
  public void noteActivityPausedLocked(long paramLong) {}
  
  public void noteActivityResumedLocked(long paramLong) {}
  
  public void noteFullWifiLockAcquiredLocked(long paramLong) {}
  
  public void noteFullWifiLockReleasedLocked(long paramLong) {}
  
  public void noteUserActivityLocked(int paramInt) {}
  
  public void noteWifiBatchedScanStartedLocked(int paramInt, long paramLong) {}
  
  public void noteWifiBatchedScanStoppedLocked(long paramLong) {}
  
  public void noteWifiMulticastDisabledLocked(long paramLong) {}
  
  public void noteWifiMulticastEnabledLocked(long paramLong) {}
  
  public void noteWifiRunningLocked(long paramLong) {}
  
  public void noteWifiScanStartedLocked(long paramLong) {}
  
  public void noteWifiScanStoppedLocked(long paramLong) {}
  
  public void noteWifiStoppedLocked(long paramLong) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\FakeUid.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */