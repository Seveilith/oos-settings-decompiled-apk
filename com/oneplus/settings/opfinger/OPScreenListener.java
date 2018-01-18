package com.oneplus.settings.opfinger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

public class OPScreenListener
{
  private Context mContext;
  private ScreenBroadcastReceiver mScreenReceiver;
  private ScreenStateListener mScreenStateListener;
  
  public OPScreenListener(Context paramContext)
  {
    this.mContext = paramContext;
    this.mScreenReceiver = new ScreenBroadcastReceiver(null);
  }
  
  private void getScreenState()
  {
    if (((PowerManager)this.mContext.getSystemService("power")).isScreenOn()) {
      if (this.mScreenStateListener != null) {
        this.mScreenStateListener.onScreenOn();
      }
    }
    while (this.mScreenStateListener == null) {
      return;
    }
    this.mScreenStateListener.onScreenOff();
  }
  
  private void registerListener()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.SCREEN_ON");
    localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
    localIntentFilter.addAction("android.intent.action.USER_PRESENT");
    this.mContext.registerReceiver(this.mScreenReceiver, localIntentFilter);
  }
  
  public void begin(ScreenStateListener paramScreenStateListener)
  {
    this.mScreenStateListener = paramScreenStateListener;
    registerListener();
    getScreenState();
  }
  
  public void unregisterListener()
  {
    this.mContext.unregisterReceiver(this.mScreenReceiver);
  }
  
  private class ScreenBroadcastReceiver
    extends BroadcastReceiver
  {
    private String action = null;
    
    private ScreenBroadcastReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      this.action = paramIntent.getAction();
      if ("android.intent.action.SCREEN_ON".equals(this.action)) {
        OPScreenListener.-get0(OPScreenListener.this).onScreenOn();
      }
      do
      {
        return;
        if ("android.intent.action.SCREEN_OFF".equals(this.action))
        {
          OPScreenListener.-get0(OPScreenListener.this).onScreenOff();
          return;
        }
      } while (!"android.intent.action.USER_PRESENT".equals(this.action));
      OPScreenListener.-get0(OPScreenListener.this).onUserPresent();
    }
  }
  
  public static abstract interface ScreenStateListener
  {
    public abstract void onScreenOff();
    
    public abstract void onScreenOn();
    
    public abstract void onUserPresent();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\opfinger\OPScreenListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */