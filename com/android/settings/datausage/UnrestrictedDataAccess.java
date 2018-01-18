package com.android.settings.datausage;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.AppStateBaseBridge.Callback;
import com.android.settings.applications.InstalledAppDetails;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.AppFilter;
import com.android.settingslib.applications.ApplicationsState.Callbacks;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;

public class UnrestrictedDataAccess
  extends SettingsPreferenceFragment
  implements ApplicationsState.Callbacks, AppStateBaseBridge.Callback, Preference.OnPreferenceChangeListener
{
  private static final String EXTRA_SHOW_SYSTEM = "show_system";
  private static final int MENU_SHOW_SYSTEM = 43;
  private ApplicationsState mApplicationsState;
  private DataSaverBackend mDataSaverBackend;
  private AppStateDataUsageBridge mDataUsageBridge;
  private boolean mExtraLoaded;
  private ApplicationsState.AppFilter mFilter;
  private ApplicationsState.Session mSession;
  private boolean mShowSystem;
  
  private void rebuild()
  {
    ArrayList localArrayList = this.mSession.rebuild(this.mFilter, ApplicationsState.ALPHA_COMPARATOR);
    if (localArrayList != null) {
      onRebuildComplete(localArrayList);
    }
  }
  
  protected int getHelpResource()
  {
    return 2131693015;
  }
  
  protected int getMetricsCategory()
  {
    return 349;
  }
  
  public void onAllSizesComputed() {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setAnimationAllowed(true);
    setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
    this.mApplicationsState = ApplicationsState.getInstance((Application)getContext().getApplicationContext());
    this.mDataSaverBackend = new DataSaverBackend(getContext());
    this.mDataUsageBridge = new AppStateDataUsageBridge(this.mApplicationsState, this, this.mDataSaverBackend);
    this.mSession = this.mApplicationsState.newSession(this);
    boolean bool;
    if (paramBundle != null)
    {
      bool = paramBundle.getBoolean("show_system");
      this.mShowSystem = bool;
      if (!this.mShowSystem) {
        break label132;
      }
    }
    label132:
    for (paramBundle = ApplicationsState.FILTER_ALL_ENABLED;; paramBundle = ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER)
    {
      this.mFilter = paramBundle;
      setHasOptionsMenu(true);
      return;
      bool = false;
      break;
    }
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if (this.mShowSystem) {}
    for (int i = 2131692555;; i = 2131692554)
    {
      paramMenu.add(0, 43, 0, i);
      super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
      return;
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mSession.release();
    this.mDataUsageBridge.release();
  }
  
  public void onExtraInfoUpdated()
  {
    this.mExtraLoaded = true;
    rebuild();
  }
  
  public void onLauncherInfoChanged() {}
  
  public void onLoadEntriesCompleted() {}
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    }
    boolean bool;
    label39:
    int i;
    if (this.mShowSystem)
    {
      bool = false;
      this.mShowSystem = bool;
      if (!this.mShowSystem) {
        break label99;
      }
      i = 2131692555;
      label54:
      paramMenuItem.setTitle(i);
      if (!this.mShowSystem) {
        break label105;
      }
    }
    label99:
    label105:
    for (ApplicationsState.AppFilter localAppFilter = ApplicationsState.FILTER_ALL_ENABLED;; localAppFilter = ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER)
    {
      this.mFilter = localAppFilter;
      if (!this.mExtraLoaded) {
        break;
      }
      rebuild();
      break;
      bool = true;
      break label39;
      i = 2131692554;
      break label54;
    }
  }
  
  public void onPackageIconChanged() {}
  
  public void onPackageListChanged() {}
  
  public void onPackageSizeChanged(String paramString) {}
  
  public void onPause()
  {
    super.onPause();
    this.mDataUsageBridge.pause();
    this.mSession.pause();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if ((paramPreference instanceof AccessPreference))
    {
      paramPreference = (AccessPreference)paramPreference;
      if (paramObject == Boolean.TRUE) {}
      for (boolean bool = true;; bool = false)
      {
        if ((this.mDataSaverBackend != null) && (paramPreference != null) && (AccessPreference.-get1(paramPreference) != null))
        {
          this.mDataSaverBackend.setIsWhitelisted(AccessPreference.-get0(paramPreference).info.uid, AccessPreference.-get0(paramPreference).info.packageName, bool);
          AccessPreference.-get1(paramPreference).isDataSaverWhitelisted = bool;
        }
        return true;
      }
    }
    return false;
  }
  
  public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> paramArrayList)
  {
    if (getContext() == null) {
      return;
    }
    cacheRemoveAllPrefs(getPreferenceScreen());
    int j = paramArrayList.size();
    int i = 0;
    if (i < j)
    {
      ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)paramArrayList.get(i);
      String str = localAppEntry.info.packageName + "|" + localAppEntry.info.uid;
      AccessPreference localAccessPreference = (AccessPreference)getCachedPreference(str);
      if (localAccessPreference == null)
      {
        localAccessPreference = new AccessPreference(getPrefContext(), localAppEntry);
        localAccessPreference.setKey(str);
        localAccessPreference.setOnPreferenceChangeListener(this);
        getPreferenceScreen().addPreference(localAccessPreference);
      }
      for (;;)
      {
        localAccessPreference.setOrder(i);
        i += 1;
        break;
        localAccessPreference.reuse();
      }
    }
    setLoading(false, true);
    removeCachedPrefs(getPreferenceScreen());
  }
  
  public void onResume()
  {
    super.onResume();
    this.mSession.resume();
    this.mDataUsageBridge.resume();
  }
  
  public void onRunningStateChanged(boolean paramBoolean) {}
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("show_system", this.mShowSystem);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    setLoading(true, false);
  }
  
  private class AccessPreference
    extends SwitchPreference
    implements DataSaverBackend.Listener
  {
    private final ApplicationsState.AppEntry mEntry;
    private final AppStateDataUsageBridge.DataUsageState mState;
    
    public AccessPreference(Context paramContext, ApplicationsState.AppEntry paramAppEntry)
    {
      super();
      this.mEntry = paramAppEntry;
      this.mState = ((AppStateDataUsageBridge.DataUsageState)this.mEntry.extraInfo);
      this.mEntry.ensureLabel(getContext());
      setState();
      if (this.mEntry.icon != null) {
        setIcon(this.mEntry.icon);
      }
    }
    
    private void setState()
    {
      setTitle(this.mEntry.label);
      if (this.mState != null)
      {
        setChecked(this.mState.isDataSaverWhitelisted);
        if (this.mState.isDataSaverBlacklisted) {
          setSummary(2131693651);
        }
      }
      else
      {
        return;
      }
      setSummary("");
    }
    
    public void onAttached()
    {
      super.onAttached();
      UnrestrictedDataAccess.-get1(UnrestrictedDataAccess.this).addListener(this);
    }
    
    public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
    {
      if (this.mEntry.icon == null) {
        paramPreferenceViewHolder.itemView.post(new Runnable()
        {
          public void run()
          {
            UnrestrictedDataAccess.-get0(UnrestrictedDataAccess.this).ensureIcon(UnrestrictedDataAccess.AccessPreference.-get0(UnrestrictedDataAccess.AccessPreference.this));
            UnrestrictedDataAccess.AccessPreference.this.setIcon(UnrestrictedDataAccess.AccessPreference.-get0(UnrestrictedDataAccess.AccessPreference.this).icon);
          }
        });
      }
      View localView = paramPreferenceViewHolder.findViewById(16908312);
      if ((this.mState != null) && (this.mState.isDataSaverBlacklisted)) {}
      for (int i = 4;; i = 0)
      {
        localView.setVisibility(i);
        super.onBindViewHolder(paramPreferenceViewHolder);
        return;
      }
    }
    
    public void onBlacklistStatusChanged(int paramInt, boolean paramBoolean)
    {
      if ((this.mState != null) && (this.mEntry.info.uid == paramInt))
      {
        this.mState.isDataSaverBlacklisted = paramBoolean;
        reuse();
      }
    }
    
    protected void onClick()
    {
      if ((this.mState != null) && (this.mState.isDataSaverBlacklisted))
      {
        InstalledAppDetails.startAppInfoFragment(AppDataUsage.class, getContext().getString(2131693384), UnrestrictedDataAccess.this, this.mEntry);
        return;
      }
      super.onClick();
    }
    
    public void onDataSaverChanged(boolean paramBoolean) {}
    
    public void onDetached()
    {
      UnrestrictedDataAccess.-get1(UnrestrictedDataAccess.this).remListener(this);
      super.onDetached();
    }
    
    public void onWhitelistStatusChanged(int paramInt, boolean paramBoolean)
    {
      if ((this.mState != null) && (this.mEntry.info.uid == paramInt))
      {
        this.mState.isDataSaverWhitelisted = paramBoolean;
        reuse();
      }
    }
    
    public void reuse()
    {
      setState();
      notifyChanged();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\UnrestrictedDataAccess.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */