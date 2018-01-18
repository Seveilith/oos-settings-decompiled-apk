package com.android.settings.applications;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.View;
import com.android.settings.DividerPreference;
import com.android.settings.notification.EmptyTextSettings;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.Callbacks;
import com.android.settingslib.applications.ApplicationsState.Session;
import java.util.ArrayList;

public class PremiumSmsAccess
  extends EmptyTextSettings
  implements AppStateBaseBridge.Callback, ApplicationsState.Callbacks, Preference.OnPreferenceChangeListener
{
  private ApplicationsState mApplicationsState;
  private ApplicationsState.Session mSession;
  private AppStateSmsPremBridge mSmsBackend;
  
  private void update()
  {
    updatePrefs(this.mSession.rebuild(AppStateSmsPremBridge.FILTER_APP_PREMIUM_SMS, ApplicationsState.ALPHA_COMPARATOR));
  }
  
  private void updatePrefs(ArrayList<ApplicationsState.AppEntry> paramArrayList)
  {
    if (paramArrayList == null) {
      return;
    }
    setEmptyText(2131693712);
    setLoading(false, true);
    PreferenceScreen localPreferenceScreen = getPreferenceManager().createPreferenceScreen(getPrefContext());
    localPreferenceScreen.setOrderingAsAdded(true);
    int i = 0;
    while (i < paramArrayList.size())
    {
      PremiumSmsPreference localPremiumSmsPreference = new PremiumSmsPreference((ApplicationsState.AppEntry)paramArrayList.get(i), getPrefContext());
      localPremiumSmsPreference.setOnPreferenceChangeListener(this);
      localPreferenceScreen.addPreference(localPremiumSmsPreference);
      i += 1;
    }
    if (paramArrayList.size() != 0)
    {
      paramArrayList = new DividerPreference(getPrefContext());
      paramArrayList.setSelectable(false);
      paramArrayList.setSummary(2131693713);
      paramArrayList.setDividerAllowedAbove(true);
      localPreferenceScreen.addPreference(paramArrayList);
    }
    setPreferenceScreen(localPreferenceScreen);
  }
  
  protected int getMetricsCategory()
  {
    return 388;
  }
  
  public void onAllSizesComputed() {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mApplicationsState = ApplicationsState.getInstance((Application)getContext().getApplicationContext());
    this.mSession = this.mApplicationsState.newSession(this);
    this.mSmsBackend = new AppStateSmsPremBridge(getContext(), this.mApplicationsState, this);
  }
  
  public void onDestroy()
  {
    if (this.mSession != null) {
      this.mSession.release();
    }
    if (this.mSmsBackend != null) {
      this.mSmsBackend.release();
    }
    super.onDestroy();
  }
  
  public void onExtraInfoUpdated()
  {
    update();
  }
  
  public void onLauncherInfoChanged() {}
  
  public void onLoadEntriesCompleted() {}
  
  public void onPackageIconChanged() {}
  
  public void onPackageListChanged() {}
  
  public void onPackageSizeChanged(String paramString) {}
  
  public void onPause()
  {
    this.mSmsBackend.pause();
    this.mSession.pause();
    super.onPause();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    paramPreference = (PremiumSmsPreference)paramPreference;
    this.mSmsBackend.setSmsState(PremiumSmsPreference.-get0(paramPreference).info.packageName, Integer.parseInt((String)paramObject));
    return true;
  }
  
  public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> paramArrayList)
  {
    updatePrefs(paramArrayList);
  }
  
  public void onResume()
  {
    super.onResume();
    this.mSession.resume();
    this.mSmsBackend.resume();
  }
  
  public void onRunningStateChanged(boolean paramBoolean) {}
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    setLoading(true, false);
  }
  
  private class PremiumSmsPreference
    extends DropDownPreference
  {
    private final ApplicationsState.AppEntry mAppEntry;
    
    public PremiumSmsPreference(ApplicationsState.AppEntry paramAppEntry, Context paramContext)
    {
      super();
      this.mAppEntry = paramAppEntry;
      this.mAppEntry.ensureLabel(paramContext);
      setTitle(this.mAppEntry.label);
      if (this.mAppEntry.icon != null) {
        setIcon(this.mAppEntry.icon);
      }
      setEntries(2131427437);
      setEntryValues(new CharSequence[] { String.valueOf(1), String.valueOf(2), String.valueOf(3) });
      setValue(String.valueOf(getCurrentValue()));
      setSummary("%s");
    }
    
    private int getCurrentValue()
    {
      if ((this.mAppEntry.extraInfo instanceof AppStateSmsPremBridge.SmsState)) {
        return ((AppStateSmsPremBridge.SmsState)this.mAppEntry.extraInfo).smsState;
      }
      return 0;
    }
    
    public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
    {
      if (getIcon() == null) {
        paramPreferenceViewHolder.itemView.post(new Runnable()
        {
          public void run()
          {
            PremiumSmsAccess.-get0(PremiumSmsAccess.this).ensureIcon(PremiumSmsAccess.PremiumSmsPreference.-get0(PremiumSmsAccess.PremiumSmsPreference.this));
            PremiumSmsAccess.PremiumSmsPreference.this.setIcon(PremiumSmsAccess.PremiumSmsPreference.-get0(PremiumSmsAccess.PremiumSmsPreference.this).icon);
          }
        });
      }
      super.onBindViewHolder(paramPreferenceViewHolder);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\PremiumSmsAccess.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */