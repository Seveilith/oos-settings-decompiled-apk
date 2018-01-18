package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.TetherSettings;

public class UsbBackend
{
  private static final int MODE_DATA_MASK = 14;
  public static final int MODE_DATA_MIDI = 6;
  public static final int MODE_DATA_MTP = 2;
  public static final int MODE_DATA_NONE = 0;
  public static final int MODE_DATA_PTP = 4;
  public static final int MODE_DATA_TETHERING = 8;
  private static final int MODE_POWER_MASK = 1;
  public static final int MODE_POWER_SINK = 0;
  public static final int MODE_POWER_SOURCE = 1;
  private Context mContext;
  private boolean mIsUnlocked;
  private final boolean mMidi;
  private UsbPort mPort;
  private UsbPortStatus mPortStatus;
  private final boolean mRestricted;
  private final boolean mRestrictedBySystem;
  private boolean mTetheringEnabled;
  private UsbManager mUsbManager;
  private UserManager mUserManager;
  
  public UsbBackend(Context paramContext)
  {
    this.mContext = paramContext;
    Object localObject = paramContext.registerReceiver(null, new IntentFilter("android.hardware.usb.action.USB_STATE"));
    int j;
    int i;
    if (localObject == null)
    {
      this.mIsUnlocked = bool;
      this.mUserManager = UserManager.get(paramContext);
      this.mUsbManager = ((UsbManager)paramContext.getSystemService(UsbManager.class));
      this.mTetheringEnabled = paramContext.getResources().getBoolean(2131558462);
      this.mRestricted = this.mUserManager.hasUserRestriction("no_usb_file_transfer");
      this.mRestrictedBySystem = this.mUserManager.hasBaseUserRestriction("no_usb_file_transfer", UserHandle.of(UserHandle.myUserId()));
      this.mMidi = paramContext.getPackageManager().hasSystemFeature("android.software.midi");
      paramContext = this.mUsbManager.getPorts();
      j = paramContext.length;
      i = 0;
    }
    for (;;)
    {
      if (i < j)
      {
        localObject = this.mUsbManager.getPortStatus(paramContext[i]);
        if (((UsbPortStatus)localObject).isConnected())
        {
          this.mPort = paramContext[i];
          this.mPortStatus = ((UsbPortStatus)localObject);
        }
      }
      else
      {
        return;
        bool = ((Intent)localObject).getBooleanExtra("unlocked", false);
        break;
      }
      i += 1;
    }
  }
  
  private int modeToPower(int paramInt)
  {
    if ((paramInt & 0x1) == 1) {
      return 1;
    }
    return 2;
  }
  
  private void setUsbFunction(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
    case 5: 
    case 7: 
    default: 
      this.mUsbManager.setCurrentFunction(null);
      this.mUsbManager.setUsbDataUnlocked(false);
      return;
    case 2: 
      this.mUsbManager.setCurrentFunction("mtp");
      this.mUsbManager.setUsbDataUnlocked(true);
      return;
    case 4: 
      this.mUsbManager.setCurrentFunction("ptp");
      this.mUsbManager.setUsbDataUnlocked(true);
      return;
    case 6: 
      this.mUsbManager.setCurrentFunction("midi");
      this.mUsbManager.setUsbDataUnlocked(true);
      return;
    }
    Intent localIntent = new Intent();
    localIntent.setClass(this.mContext, TetherSettings.class);
    this.mContext.startActivity(localIntent);
  }
  
  public int getCurrentMode()
  {
    if (this.mPort != null)
    {
      if (this.mPortStatus.getCurrentPowerRole() == 1) {}
      for (int i = 1;; i = 0) {
        return getUsbDataMode() | i;
      }
    }
    return getUsbDataMode() | 0x0;
  }
  
  public int getUsbDataMode()
  {
    if ((this.mTetheringEnabled) && (this.mUsbManager.isFunctionEnabled("rndis"))) {
      return 8;
    }
    if (!this.mIsUnlocked) {
      return 0;
    }
    if (this.mUsbManager.isFunctionEnabled("mtp")) {
      return 2;
    }
    if (this.mUsbManager.isFunctionEnabled("ptp")) {
      return 4;
    }
    if (this.mUsbManager.isFunctionEnabled("midi")) {
      return 6;
    }
    return 0;
  }
  
  public boolean isModeDisallowed(int paramInt)
  {
    return (this.mRestricted) && ((paramInt & 0xE) != 0) && ((paramInt & 0xE) != 6);
  }
  
  public boolean isModeDisallowedBySystem(int paramInt)
  {
    return (this.mRestrictedBySystem) && ((paramInt & 0xE) != 0) && ((paramInt & 0xE) != 6);
  }
  
  public boolean isModeSupported(int paramInt)
  {
    boolean bool = true;
    if ((!this.mMidi) && ((paramInt & 0xE) == 6)) {
      return false;
    }
    if (this.mPort != null)
    {
      int i = modeToPower(paramInt);
      if ((paramInt & 0xE) != 0) {
        return this.mPortStatus.isRoleCombinationSupported(i, 2);
      }
      if (!this.mPortStatus.isRoleCombinationSupported(i, 2)) {
        bool = this.mPortStatus.isRoleCombinationSupported(i, 1);
      }
      return bool;
    }
    return (paramInt & 0x1) != 1;
  }
  
  public void setMode(int paramInt)
  {
    int j;
    if (this.mPort != null)
    {
      j = modeToPower(paramInt);
      if (((paramInt & 0xE) != 0) || (!this.mPortStatus.isRoleCombinationSupported(j, 1))) {
        break label56;
      }
    }
    label56:
    for (int i = 1;; i = 2)
    {
      this.mUsbManager.setPortRoles(this.mPort, j, i);
      setUsbFunction(paramInt & 0xE);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\UsbBackend.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */