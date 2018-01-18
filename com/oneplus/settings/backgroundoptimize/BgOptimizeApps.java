package com.oneplus.settings.backgroundoptimize;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import com.android.settings.applications.AppCounter;
import com.android.settings.applications.ManageApplications;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.notification.NotificationBackend;

public class BgOptimizeApps
  extends ManageApplications
{
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new BgOptimizeApps.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader, null);
    }
  };
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mLoader;
    private final NotificationBackend mNotificationBackend;
    
    private SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mLoader = paramSummaryLoader;
      this.mNotificationBackend = new NotificationBackend();
    }
    
    private void updateSummary(int paramInt)
    {
      if (paramInt == 0)
      {
        this.mLoader.setSummary(this, this.mContext.getString(2131693582));
        return;
      }
      this.mLoader.setSummary(this, this.mContext.getResources().getQuantityString(2131951649, paramInt, new Object[] { Integer.valueOf(paramInt) }));
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean) {
        new AppCounter(this.mContext)
        {
          protected boolean includeInCount(ApplicationInfo paramAnonymousApplicationInfo)
          {
            return BgOptimizeApps.SummaryProvider.-get0(BgOptimizeApps.SummaryProvider.this).getNotificationsBanned(paramAnonymousApplicationInfo.packageName, paramAnonymousApplicationInfo.uid);
          }
          
          protected void onCountComplete(int paramAnonymousInt)
          {
            BgOptimizeApps.SummaryProvider.-wrap0(BgOptimizeApps.SummaryProvider.this, paramAnonymousInt);
          }
        }.execute(new Void[0]);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\backgroundoptimize\BgOptimizeApps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */