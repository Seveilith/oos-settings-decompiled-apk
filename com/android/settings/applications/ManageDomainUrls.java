package com.android.settings.applications;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.ArraySet;
import android.view.View;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.Callbacks;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;

public class ManageDomainUrls
  extends SettingsPreferenceFragment
  implements ApplicationsState.Callbacks, Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private static final int INSTALLED_APP_DETAILS = 1;
  private ApplicationsState mApplicationsState;
  private PreferenceGroup mDomainAppList;
  private ApplicationsState.Session mSession;
  private SwitchPreference mWebAction;
  
  private void rebuild()
  {
    ArrayList localArrayList = this.mSession.rebuild(ApplicationsState.FILTER_WITH_DOMAIN_URLS, ApplicationsState.ALPHA_COMPARATOR);
    if (localArrayList != null) {
      onRebuildComplete(localArrayList);
    }
  }
  
  private void rebuildAppList(PreferenceGroup paramPreferenceGroup, ArrayList<ApplicationsState.AppEntry> paramArrayList)
  {
    cacheRemoveAllPrefs(paramPreferenceGroup);
    int j = paramArrayList.size();
    int i = 0;
    if (i < j)
    {
      ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)paramArrayList.get(i);
      String str = localAppEntry.info.packageName + "|" + localAppEntry.info.uid;
      DomainAppPreference localDomainAppPreference = (DomainAppPreference)getCachedPreference(str);
      if (localDomainAppPreference == null)
      {
        localDomainAppPreference = new DomainAppPreference(getPrefContext(), localAppEntry);
        localDomainAppPreference.setKey(str);
        localDomainAppPreference.setOnPreferenceClickListener(this);
        paramPreferenceGroup.addPreference(localDomainAppPreference);
      }
      for (;;)
      {
        localDomainAppPreference.setOrder(i);
        i += 1;
        break;
        localDomainAppPreference.reuse();
      }
    }
    removeCachedPrefs(paramPreferenceGroup);
  }
  
  protected int getMetricsCategory()
  {
    return 143;
  }
  
  public void onAllSizesComputed() {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setAnimationAllowed(true);
    setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
    this.mApplicationsState = ApplicationsState.getInstance((Application)getContext().getApplicationContext());
    this.mSession = this.mApplicationsState.newSession(this);
    setHasOptionsMenu(true);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mSession.release();
  }
  
  public void onLauncherInfoChanged() {}
  
  public void onLoadEntriesCompleted()
  {
    rebuild();
  }
  
  public void onPackageIconChanged() {}
  
  public void onPackageListChanged() {}
  
  public void onPackageSizeChanged(String paramString) {}
  
  public void onPause()
  {
    super.onPause();
    this.mSession.pause();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (paramPreference == this.mWebAction)
    {
      if (((Boolean)paramObject).booleanValue()) {}
      for (int i = 1;; i = 0)
      {
        Settings.Secure.putInt(getContentResolver(), "web_action_enabled", i);
        return true;
      }
    }
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference.getClass() == DomainAppPreference.class)
    {
      paramPreference = DomainAppPreference.-get0((DomainAppPreference)paramPreference);
      AppInfoBase.startAppInfoFragment(AppLaunchSettings.class, 2131692078, paramPreference.info.packageName, paramPreference.info.uid, this, 1);
      return true;
    }
    return false;
  }
  
  public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> paramArrayList)
  {
    boolean bool = true;
    if (getContext() == null) {
      return;
    }
    int i;
    if (Settings.Global.getInt(getContext().getContentResolver(), "enable_ephemeral_feature", 1) == 0)
    {
      i = 1;
      if (i == 0) {
        break label55;
      }
      this.mDomainAppList = getPreferenceScreen();
    }
    label55:
    PreferenceScreen localPreferenceScreen;
    do
    {
      rebuildAppList(this.mDomainAppList, paramArrayList);
      return;
      i = 0;
      break;
      localPreferenceScreen = getPreferenceScreen();
    } while (localPreferenceScreen.getPreferenceCount() != 0);
    PreferenceCategory localPreferenceCategory = new PreferenceCategory(getPrefContext());
    localPreferenceCategory.setTitle(2131693858);
    localPreferenceScreen.addPreference(localPreferenceCategory);
    this.mWebAction = new SwitchPreference(getPrefContext());
    this.mWebAction.setTitle(2131693856);
    this.mWebAction.setSummary(2131693857);
    SwitchPreference localSwitchPreference = this.mWebAction;
    if (Settings.Secure.getInt(getContentResolver(), "web_action_enabled", 1) != 0) {}
    for (;;)
    {
      localSwitchPreference.setChecked(bool);
      this.mWebAction.setOnPreferenceChangeListener(this);
      localPreferenceCategory.addPreference(this.mWebAction);
      this.mDomainAppList = new PreferenceCategory(getPrefContext());
      this.mDomainAppList.setTitle(2131693859);
      localPreferenceScreen.addPreference(this.mDomainAppList);
      break;
      bool = false;
    }
  }
  
  public void onResume()
  {
    super.onResume();
    this.mSession.resume();
  }
  
  public void onRunningStateChanged(boolean paramBoolean) {}
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
  }
  
  private class DomainAppPreference
    extends Preference
  {
    private final ApplicationsState.AppEntry mEntry;
    private final PackageManager mPm;
    
    public DomainAppPreference(Context paramContext, ApplicationsState.AppEntry paramAppEntry)
    {
      super();
      this.mPm = paramContext.getPackageManager();
      this.mEntry = paramAppEntry;
      this.mEntry.ensureLabel(getContext());
      setState();
      if (this.mEntry.icon != null) {
        setIcon(this.mEntry.icon);
      }
    }
    
    private CharSequence getDomainsSummary(String paramString)
    {
      if (this.mPm.getIntentVerificationStatusAsUser(paramString, UserHandle.myUserId()) == 3) {
        return getContext().getString(2131693419);
      }
      paramString = Utils.getHandledDomains(this.mPm, paramString);
      if (paramString.size() == 0) {
        return getContext().getString(2131693419);
      }
      if (paramString.size() == 1) {
        return getContext().getString(2131693420, new Object[] { paramString.valueAt(0) });
      }
      return getContext().getString(2131693421, new Object[] { paramString.valueAt(0) });
    }
    
    private void setState()
    {
      setTitle(this.mEntry.label);
      setSummary(getDomainsSummary(this.mEntry.info.packageName));
    }
    
    public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
    {
      if (this.mEntry.icon == null) {
        paramPreferenceViewHolder.itemView.post(new Runnable()
        {
          public void run()
          {
            ManageDomainUrls.-get0(ManageDomainUrls.this).ensureIcon(ManageDomainUrls.DomainAppPreference.-get0(ManageDomainUrls.DomainAppPreference.this));
            ManageDomainUrls.DomainAppPreference.this.setIcon(ManageDomainUrls.DomainAppPreference.-get0(ManageDomainUrls.DomainAppPreference.this).icon);
          }
        });
      }
      super.onBindViewHolder(paramPreferenceViewHolder);
    }
    
    public void reuse()
    {
      setState();
      notifyChanged();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ManageDomainUrls.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */