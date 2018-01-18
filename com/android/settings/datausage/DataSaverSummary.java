package com.android.settings.datausage;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.widget.Switch;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.AppStateBaseBridge.Callback;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.AppFilter;
import com.android.settingslib.applications.ApplicationsState.Callbacks;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;

public class DataSaverSummary
  extends SettingsPreferenceFragment
  implements SwitchBar.OnSwitchChangeListener, DataSaverBackend.Listener, AppStateBaseBridge.Callback, ApplicationsState.Callbacks
{
  private static final String KEY_UNRESTRICTED_ACCESS = "unrestricted_access";
  private ApplicationsState mApplicationsState;
  private DataSaverBackend mDataSaverBackend;
  private AppStateDataUsageBridge mDataUsageBridge;
  private ApplicationsState.Session mSession;
  private SwitchBar mSwitchBar;
  private boolean mSwitching;
  private Preference mUnrestrictedAccess;
  
  protected int getHelpResource()
  {
    return 2131693014;
  }
  
  protected int getMetricsCategory()
  {
    return 348;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mSwitchBar = ((SettingsActivity)getActivity()).getSwitchBar();
    this.mSwitchBar.show();
    this.mSwitchBar.addOnSwitchChangeListener(this);
  }
  
  public void onAllSizesComputed() {}
  
  public void onBlacklistStatusChanged(int paramInt, boolean paramBoolean) {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230748);
    this.mUnrestrictedAccess = findPreference("unrestricted_access");
    this.mApplicationsState = ApplicationsState.getInstance((Application)getContext().getApplicationContext());
    this.mDataSaverBackend = new DataSaverBackend(getContext());
    this.mDataUsageBridge = new AppStateDataUsageBridge(this.mApplicationsState, this, this.mDataSaverBackend);
    this.mSession = this.mApplicationsState.newSession(this);
  }
  
  public void onDataSaverChanged(boolean paramBoolean)
  {
    try
    {
      this.mSwitchBar.setChecked(paramBoolean);
      this.mSwitching = false;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void onExtraInfoUpdated()
  {
    if (!isAdded()) {
      return;
    }
    int j = 0;
    ArrayList localArrayList = this.mSession.getAllApps();
    int m = localArrayList.size();
    int i = 0;
    if (i < m)
    {
      ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)localArrayList.get(i);
      int k;
      if (!ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER.filterApp(localAppEntry)) {
        k = j;
      }
      for (;;)
      {
        i += 1;
        j = k;
        break;
        k = j;
        if (localAppEntry.extraInfo != null)
        {
          k = j;
          if (((AppStateDataUsageBridge.DataUsageState)localAppEntry.extraInfo).isDataSaverWhitelisted) {
            k = j + 1;
          }
        }
      }
    }
    this.mUnrestrictedAccess.setSummary(getResources().getQuantityString(2131951650, j, new Object[] { Integer.valueOf(j) }));
  }
  
  public void onLauncherInfoChanged() {}
  
  public void onLoadEntriesCompleted() {}
  
  public void onPackageIconChanged() {}
  
  public void onPackageListChanged() {}
  
  public void onPackageSizeChanged(String paramString) {}
  
  public void onPause()
  {
    super.onPause();
    this.mDataSaverBackend.remListener(this);
    this.mDataUsageBridge.pause();
    this.mSession.pause();
  }
  
  public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> paramArrayList) {}
  
  public void onResume()
  {
    super.onResume();
    this.mDataSaverBackend.refreshWhitelist();
    this.mDataSaverBackend.refreshBlacklist();
    this.mDataSaverBackend.addListener(this);
    this.mSession.resume();
    this.mDataUsageBridge.resume();
  }
  
  public void onRunningStateChanged(boolean paramBoolean) {}
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    try
    {
      boolean bool = this.mSwitching;
      if (bool) {
        return;
      }
      this.mSwitching = true;
      this.mDataSaverBackend.setDataSaverEnabled(paramBoolean);
      return;
    }
    finally {}
  }
  
  public void onWhitelistStatusChanged(int paramInt, boolean paramBoolean) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\DataSaverSummary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */