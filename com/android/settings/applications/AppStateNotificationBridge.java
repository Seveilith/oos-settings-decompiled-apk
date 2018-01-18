package com.android.settings.applications;

import android.content.Context;
import android.content.pm.PackageManager;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.NotificationBackend.AppRow;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.AppFilter;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;

public class AppStateNotificationBridge
  extends AppStateBaseBridge
{
  public static final ApplicationsState.AppFilter FILTER_APP_NOTIFICATION_BLOCKED = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      if ((paramAnonymousAppEntry == null) || (paramAnonymousAppEntry.extraInfo == null)) {
        return false;
      }
      if ((paramAnonymousAppEntry.extraInfo instanceof NotificationBackend.AppRow)) {
        return ((NotificationBackend.AppRow)paramAnonymousAppEntry.extraInfo).banned;
      }
      return false;
    }
    
    public void init() {}
  };
  public static final ApplicationsState.AppFilter FILTER_APP_NOTIFICATION_HIDE_ALL = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      if ((paramAnonymousAppEntry == null) || (paramAnonymousAppEntry.extraInfo == null)) {
        return false;
      }
      if (((NotificationBackend.AppRow)paramAnonymousAppEntry.extraInfo).lockScreenSecure) {
        return ((NotificationBackend.AppRow)paramAnonymousAppEntry.extraInfo).appVisOverride == -1;
      }
      return false;
    }
    
    public void init() {}
  };
  public static final ApplicationsState.AppFilter FILTER_APP_NOTIFICATION_HIDE_SENSITIVE;
  public static final ApplicationsState.AppFilter FILTER_APP_NOTIFICATION_PRIORITY;
  public static final ApplicationsState.AppFilter FILTER_APP_NOTIFICATION_SILENCED = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      boolean bool2 = false;
      if ((paramAnonymousAppEntry == null) || (paramAnonymousAppEntry.extraInfo == null)) {
        return false;
      }
      paramAnonymousAppEntry = (NotificationBackend.AppRow)paramAnonymousAppEntry.extraInfo;
      boolean bool1 = bool2;
      if (paramAnonymousAppEntry.appImportance > 0)
      {
        bool1 = bool2;
        if (paramAnonymousAppEntry.appImportance < 3) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public void init() {}
  };
  private final Context mContext;
  private final NotificationBackend mNotifBackend;
  private final PackageManager mPm;
  
  static
  {
    FILTER_APP_NOTIFICATION_PRIORITY = new ApplicationsState.AppFilter()
    {
      public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
      {
        if ((paramAnonymousAppEntry == null) || (paramAnonymousAppEntry.extraInfo == null)) {
          return false;
        }
        try
        {
          boolean bool = ((NotificationBackend.AppRow)paramAnonymousAppEntry.extraInfo).appBypassDnd;
          return bool;
        }
        catch (Exception paramAnonymousAppEntry)
        {
          paramAnonymousAppEntry.printStackTrace();
        }
        return false;
      }
      
      public void init() {}
    };
    FILTER_APP_NOTIFICATION_HIDE_SENSITIVE = new ApplicationsState.AppFilter()
    {
      public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
      {
        if ((paramAnonymousAppEntry == null) || (paramAnonymousAppEntry.extraInfo == null)) {
          return false;
        }
        if (((NotificationBackend.AppRow)paramAnonymousAppEntry.extraInfo).lockScreenSecure) {
          return ((NotificationBackend.AppRow)paramAnonymousAppEntry.extraInfo).appVisOverride == 0;
        }
        return false;
      }
      
      public void init() {}
    };
  }
  
  public AppStateNotificationBridge(Context paramContext, ApplicationsState paramApplicationsState, AppStateBaseBridge.Callback paramCallback, NotificationBackend paramNotificationBackend)
  {
    super(paramApplicationsState, paramCallback);
    this.mContext = paramContext;
    this.mPm = this.mContext.getPackageManager();
    this.mNotifBackend = paramNotificationBackend;
  }
  
  protected void loadAllExtraInfo()
  {
    ArrayList localArrayList = this.mAppSession.getAllApps();
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)localArrayList.get(i);
      localAppEntry.extraInfo = this.mNotifBackend.loadAppRow(this.mContext, this.mPm, localAppEntry.info);
      i += 1;
    }
  }
  
  protected void updateExtraInfo(ApplicationsState.AppEntry paramAppEntry, String paramString, int paramInt)
  {
    paramAppEntry.extraInfo = this.mNotifBackend.loadAppRow(this.mContext, this.mPm, paramAppEntry.info);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppStateNotificationBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */