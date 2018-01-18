package com.oneplus.settings.backgroundoptimize;

import android.os.RemoteException;
import java.util.List;

public abstract interface IBgOActivityManager
{
  public static final int BgO_PKG_BLACKLIST = 0;
  public static final int BgO_PKG_WHITELIST = 1;
  
  public abstract List<AppControlMode> getAllAppControlModes(int paramInt)
    throws RemoteException;
  
  public abstract int getAppControlMode(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getAppControlState(int paramInt)
    throws RemoteException;
  
  public abstract int setAppControlMode(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int setAppControlState(int paramInt1, int paramInt2)
    throws RemoteException;
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\backgroundoptimize\IBgOActivityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */