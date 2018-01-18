package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TestingSettingsBroadcastReceiver
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent.getAction().equals("android.provider.Telephony.SECRET_CODE"))
    {
      paramIntent = new Intent("android.intent.action.MAIN");
      paramIntent.setClass(paramContext, Settings.TestingSettingsActivity.class);
      paramIntent.setFlags(268435456);
      paramContext.startActivity(paramIntent);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\TestingSettingsBroadcastReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */