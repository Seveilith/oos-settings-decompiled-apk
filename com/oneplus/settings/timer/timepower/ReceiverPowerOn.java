package com.oneplus.settings.timer.timepower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverPowerOn
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext.sendBroadcast(new Intent("com.android.settings.SET_CHANGED"));
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\timer\timepower\ReceiverPowerOn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */