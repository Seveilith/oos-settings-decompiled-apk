package com.google.analytics.tracking.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class CampaignTrackingReceiver
  extends BroadcastReceiver
{
  static final String CAMPAIGN_KEY = "referrer";
  static final String INSTALL_ACTION = "com.android.vending.INSTALL_REFERRER";
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    String str = paramIntent.getStringExtra("referrer");
    if (!"com.android.vending.INSTALL_REFERRER".equals(paramIntent.getAction())) {}
    while (str == null) {
      return;
    }
    paramIntent = new Intent(paramContext, CampaignTrackingService.class);
    paramIntent.putExtra("referrer", str);
    paramContext.startService(paramIntent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\CampaignTrackingReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */