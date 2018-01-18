package com.android.settings.applications;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.notification.NotificationBackend;

public class NotificationApps
  extends ManageApplications
{
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new NotificationApps.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader, null);
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
            return NotificationApps.SummaryProvider.-get0(NotificationApps.SummaryProvider.this).getNotificationsBanned(paramAnonymousApplicationInfo.packageName, paramAnonymousApplicationInfo.uid);
          }
          
          protected void onCountComplete(int paramAnonymousInt)
          {
            NotificationApps.SummaryProvider.-wrap0(NotificationApps.SummaryProvider.this, paramAnonymousInt);
          }
        }.execute(new Void[0]);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\NotificationApps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */