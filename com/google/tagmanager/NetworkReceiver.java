package com.google.tagmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.google.android.gms.common.util.VisibleForTesting;

class NetworkReceiver
  extends BroadcastReceiver
{
  @VisibleForTesting
  static final String SELF_IDENTIFYING_EXTRA = NetworkReceiver.class.getName();
  private final ServiceManager mManager;
  
  NetworkReceiver(ServiceManager paramServiceManager)
  {
    this.mManager = paramServiceManager;
  }
  
  public static void sendRadioPoweredBroadcast(Context paramContext)
  {
    Intent localIntent = new Intent("com.google.analytics.RADIO_POWERED");
    localIntent.addCategory(paramContext.getPackageName());
    localIntent.putExtra(SELF_IDENTIFYING_EXTRA, true);
    paramContext.sendBroadcast(localIntent);
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.getAction();
    if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(paramContext)) {
      if ("com.google.analytics.RADIO_POWERED".equals(paramContext)) {
        break label80;
      }
    }
    label75:
    label80:
    while (paramIntent.hasExtra(SELF_IDENTIFYING_EXTRA))
    {
      return;
      Bundle localBundle = paramIntent.getExtras();
      paramContext = Boolean.FALSE;
      if (localBundle == null)
      {
        paramIntent = this.mManager;
        if (!paramContext.booleanValue()) {
          break label75;
        }
      }
      for (boolean bool = false;; bool = true)
      {
        paramIntent.updateConnectivityStatus(bool);
        return;
        paramContext = Boolean.valueOf(paramIntent.getExtras().getBoolean("noConnectivity"));
        break;
      }
    }
    this.mManager.onRadioPowered();
  }
  
  public void register(Context paramContext)
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    paramContext.registerReceiver(this, localIntentFilter);
    localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("com.google.analytics.RADIO_POWERED");
    localIntentFilter.addCategory(paramContext.getPackageName());
    paramContext.registerReceiver(this, localIntentFilter);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\NetworkReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */