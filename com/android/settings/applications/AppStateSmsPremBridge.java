package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.telephony.ISms;
import com.android.internal.telephony.ISms.Stub;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.AppFilter;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;

public class AppStateSmsPremBridge
  extends AppStateBaseBridge
{
  public static final ApplicationsState.AppFilter FILTER_APP_PREMIUM_SMS = new ApplicationsState.AppFilter()
  {
    public boolean filterApp(ApplicationsState.AppEntry paramAnonymousAppEntry)
    {
      return ((paramAnonymousAppEntry.extraInfo instanceof AppStateSmsPremBridge.SmsState)) && (((AppStateSmsPremBridge.SmsState)paramAnonymousAppEntry.extraInfo).smsState != 0);
    }
    
    public void init() {}
  };
  private final Context mContext;
  private final ISms mSmsManager;
  
  public AppStateSmsPremBridge(Context paramContext, ApplicationsState paramApplicationsState, AppStateBaseBridge.Callback paramCallback)
  {
    super(paramApplicationsState, paramCallback);
    this.mContext = paramContext;
    this.mSmsManager = ISms.Stub.asInterface(ServiceManager.getService("isms"));
  }
  
  private int getSmsState(String paramString)
  {
    try
    {
      int i = this.mSmsManager.getPremiumSmsPermission(paramString);
      return i;
    }
    catch (RemoteException paramString) {}
    return 0;
  }
  
  public SmsState getState(String paramString)
  {
    SmsState localSmsState = new SmsState();
    localSmsState.smsState = getSmsState(paramString);
    return localSmsState;
  }
  
  protected void loadAllExtraInfo()
  {
    ArrayList localArrayList = this.mAppSession.getAllApps();
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)localArrayList.get(i);
      updateExtraInfo(localAppEntry, localAppEntry.info.packageName, localAppEntry.info.uid);
      i += 1;
    }
  }
  
  public void setSmsState(String paramString, int paramInt)
  {
    try
    {
      this.mSmsManager.setPremiumSmsPermission(paramString, paramInt);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  protected void updateExtraInfo(ApplicationsState.AppEntry paramAppEntry, String paramString, int paramInt)
  {
    paramAppEntry.extraInfo = getState(paramString);
  }
  
  public static class SmsState
  {
    public int smsState;
    
    public boolean isGranted()
    {
      return this.smsState == 3;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppStateSmsPremBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */