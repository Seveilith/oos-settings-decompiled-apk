package com.google.tagmanager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.google.analytics.tracking.android.CampaignTrackingService;
import com.google.android.gms.common.util.VisibleForTesting;

public final class InstallReferrerService
  extends IntentService
{
  @VisibleForTesting
  Context contextOverride;
  @VisibleForTesting
  CampaignTrackingService gaService;
  
  public InstallReferrerService()
  {
    super("InstallReferrerService");
  }
  
  public InstallReferrerService(String paramString)
  {
    super(paramString);
  }
  
  private void forwardToGoogleAnalytics(Context paramContext, Intent paramIntent)
  {
    if (this.gaService != null) {}
    for (;;)
    {
      this.gaService.processIntent(paramContext, paramIntent);
      return;
      this.gaService = new CampaignTrackingService();
    }
  }
  
  protected void onHandleIntent(Intent paramIntent)
  {
    String str = paramIntent.getStringExtra("referrer");
    if (this.contextOverride == null) {}
    for (Context localContext = getApplicationContext();; localContext = this.contextOverride)
    {
      InstallReferrerUtil.saveInstallReferrer(localContext, str);
      forwardToGoogleAnalytics(localContext, paramIntent);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\InstallReferrerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */