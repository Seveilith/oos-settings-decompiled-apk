package com.android.settings.applications;

import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.Callbacks;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;

public abstract class AppStateBaseBridge
  implements ApplicationsState.Callbacks
{
  protected final ApplicationsState.Session mAppSession;
  protected final ApplicationsState mAppState;
  protected final Callback mCallback;
  protected final BackgroundHandler mHandler;
  protected final MainHandler mMainHandler;
  
  public AppStateBaseBridge(ApplicationsState paramApplicationsState, Callback paramCallback)
  {
    this.mAppState = paramApplicationsState;
    if (this.mAppState != null)
    {
      paramApplicationsState = this.mAppState.newSession(this);
      this.mAppSession = paramApplicationsState;
      this.mCallback = paramCallback;
      if (this.mAppState == null) {
        break label82;
      }
    }
    label82:
    for (paramApplicationsState = this.mAppState.getBackgroundLooper();; paramApplicationsState = Looper.getMainLooper())
    {
      this.mHandler = new BackgroundHandler(paramApplicationsState);
      this.mMainHandler = new MainHandler(null);
      return;
      paramApplicationsState = null;
      break;
    }
  }
  
  public void forceUpdate(String paramString, int paramInt)
  {
    this.mHandler.obtainMessage(2, paramInt, 0, paramString).sendToTarget();
  }
  
  protected abstract void loadAllExtraInfo();
  
  public void onAllSizesComputed() {}
  
  public void onLauncherInfoChanged() {}
  
  public void onLoadEntriesCompleted()
  {
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void onPackageIconChanged() {}
  
  public void onPackageListChanged()
  {
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void onPackageSizeChanged(String paramString) {}
  
  public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> paramArrayList) {}
  
  public void onRunningStateChanged(boolean paramBoolean) {}
  
  public void pause()
  {
    this.mAppSession.pause();
  }
  
  public void release()
  {
    this.mAppSession.release();
  }
  
  public void resume()
  {
    this.mHandler.sendEmptyMessage(1);
    this.mAppSession.resume();
  }
  
  protected abstract void updateExtraInfo(ApplicationsState.AppEntry paramAppEntry, String paramString, int paramInt);
  
  private class BackgroundHandler
    extends Handler
  {
    private static final int MSG_FORCE_LOAD_PKG = 2;
    private static final int MSG_LOAD_ALL = 1;
    
    public BackgroundHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        AppStateBaseBridge.this.loadAllExtraInfo();
        AppStateBaseBridge.this.mMainHandler.sendEmptyMessage(1);
        return;
      }
      ArrayList localArrayList = AppStateBaseBridge.this.mAppSession.getAllApps();
      int j = localArrayList.size();
      String str = (String)paramMessage.obj;
      int k = paramMessage.arg1;
      int i = 0;
      while (i < j)
      {
        paramMessage = (ApplicationsState.AppEntry)localArrayList.get(i);
        if ((paramMessage.info.uid == k) && (str.equals(paramMessage.info.packageName))) {
          AppStateBaseBridge.this.updateExtraInfo(paramMessage, str, k);
        }
        i += 1;
      }
      AppStateBaseBridge.this.mMainHandler.sendEmptyMessage(1);
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onExtraInfoUpdated();
  }
  
  private class MainHandler
    extends Handler
  {
    private static final int MSG_INFO_UPDATED = 1;
    
    private MainHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      AppStateBaseBridge.this.mCallback.onExtraInfoUpdated();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppStateBaseBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */