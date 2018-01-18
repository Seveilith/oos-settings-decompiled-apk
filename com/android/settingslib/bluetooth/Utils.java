package com.android.settingslib.bluetooth;

import android.content.Context;
import com.android.settingslib.R.string;

public class Utils
{
  public static final boolean D = true;
  public static final boolean V = false;
  private static ErrorListener sErrorListener;
  
  public static int getConnectionStateSummary(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 2: 
      return R.string.bluetooth_connected;
    case 1: 
      return R.string.bluetooth_connecting;
    case 0: 
      return R.string.bluetooth_disconnected;
    }
    return R.string.bluetooth_disconnecting;
  }
  
  public static void setErrorListener(ErrorListener paramErrorListener)
  {
    sErrorListener = paramErrorListener;
  }
  
  static void showError(Context paramContext, String paramString, int paramInt)
  {
    if (sErrorListener != null) {
      sErrorListener.onShowError(paramContext, paramString, paramInt);
    }
  }
  
  public static abstract interface ErrorListener
  {
    public abstract void onShowError(Context paramContext, String paramString, int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */