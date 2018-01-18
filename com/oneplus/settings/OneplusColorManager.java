package com.oneplus.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.display.ColorBalanceManager;
import android.os.SystemProperties;
import android.provider.Settings.System;
import android.util.Log;

public class OneplusColorManager
{
  private static final int OP_DCIP3_MODE_LEVEL = 8;
  private static final int OP_DEFAULT_MODE_LEVEL = 0;
  private static final int OP_SRGB_MODE_LEVEL = 7;
  private static final String OP_SYS_DCIP3_PROPERTY = "sys.dci3p";
  private static final String OP_SYS_SRGB_PROPERTY = "sys.srgb";
  private static final String TAG = "OneplusColorManager";
  private static ColorBalanceManager mCBM;
  private static OneplusColorManager mOneplusColorManager;
  private boolean isSupportReadingMode;
  private Context mContext;
  
  public OneplusColorManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.isSupportReadingMode = this.mContext.getPackageManager().hasSystemFeature("oem.read_mode.support");
    if (mCBM == null) {
      mCBM = new ColorBalanceManager(this.mContext);
    }
  }
  
  public static OneplusColorManager getInstance(Context paramContext)
  {
    if (mOneplusColorManager == null) {
      mOneplusColorManager = new OneplusColorManager(paramContext);
    }
    return mOneplusColorManager;
  }
  
  private void saveColorManagerMode()
  {
    if (mCBM != null) {
      mCBM.sendMsg(4);
    }
  }
  
  public void closeDciP3()
  {
    SystemProperties.set("sys.dci3p", "0");
    mCBM.setActiveMode(0);
    mCBM.setDefaultMode(0);
  }
  
  public void closesRGB()
  {
    SystemProperties.set("sys.srgb", "0");
    mCBM.setActiveMode(0);
    mCBM.setDefaultMode(0);
  }
  
  public void releaseColorManager() {}
  
  public void resetScreenBetterDisplay()
  {
    if (mCBM != null)
    {
      if (!this.isSupportReadingMode) {
        break label33;
      }
      mCBM.setActiveMode(0);
      mCBM.setColorBalance(43);
    }
    for (;;)
    {
      saveColorManagerMode();
      return;
      label33:
      mCBM.setActiveMode(0);
    }
  }
  
  public void restoreScreenBetterDisplay()
  {
    if (mCBM != null)
    {
      Log.d("OneplusColorManager", "orestoreScreenBetterDisplay");
      int i = Settings.System.getInt(this.mContext.getContentResolver(), "oem_screen_better_value", 43);
      mCBM.setColorBalance(100 - i);
    }
    saveColorManagerMode();
  }
  
  public void saveScreenBetter()
  {
    saveColorManagerMode();
  }
  
  public void setActiveMode(int paramInt)
  {
    if (mCBM != null) {
      mCBM.setActiveMode(paramInt);
    }
  }
  
  public void setActivetNightMode()
  {
    SystemProperties.set("sys.srgb", "1");
    if (mCBM != null)
    {
      mCBM.setActiveMode(1);
      mCBM.setDefaultMode(1);
    }
  }
  
  public void setColorBalance(int paramInt)
  {
    if (mCBM != null) {
      mCBM.setColorBalance(paramInt);
    }
  }
  
  public void setDciP3()
  {
    if (mCBM != null)
    {
      mCBM.setActiveMode(0);
      mCBM.setDefaultMode(0);
    }
    SystemProperties.set("sys.dci3p", "1");
  }
  
  public void setNightModeLevel(int paramInt)
  {
    if (mCBM != null)
    {
      mCBM.setActiveMode(paramInt);
      mCBM.setDefaultMode(paramInt);
    }
  }
  
  public void setNotActivetNightMode()
  {
    SystemProperties.set("sys.srgb", "0");
    if (mCBM != null)
    {
      mCBM.setActiveMode(0);
      mCBM.setDefaultMode(0);
    }
  }
  
  public void setsRGB()
  {
    if (mCBM != null)
    {
      mCBM.setActiveMode(0);
      mCBM.setDefaultMode(0);
    }
    SystemProperties.set("sys.srgb", "1");
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OneplusColorManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */