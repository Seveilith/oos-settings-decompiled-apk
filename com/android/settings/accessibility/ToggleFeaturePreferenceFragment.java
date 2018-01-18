package com.android.settings.accessibility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.ToggleSwitch;

public abstract class ToggleFeaturePreferenceFragment
  extends SettingsPreferenceFragment
{
  protected String mPreferenceKey;
  protected Intent mSettingsIntent;
  protected CharSequence mSettingsTitle;
  protected Preference mSummaryPreference;
  protected SwitchBar mSwitchBar;
  protected ToggleSwitch mToggleSwitch;
  
  private void installActionBarToggleSwitch()
  {
    this.mSwitchBar.show();
    onInstallSwitchBarToggleSwitch();
  }
  
  private void removeActionBarToggleSwitch()
  {
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(null);
    onRemoveSwitchBarToggleSwitch();
    this.mSwitchBar.hide();
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    installActionBarToggleSwitch();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getPreferenceManager().createPreferenceScreen(getActivity());
    setPreferenceScreen(paramBundle);
    this.mSummaryPreference = new Preference(getPrefContext())
    {
      private void sendAccessibilityEvent(View paramAnonymousView)
      {
        AccessibilityManager localAccessibilityManager = AccessibilityManager.getInstance(ToggleFeaturePreferenceFragment.this.getActivity());
        if (localAccessibilityManager.isEnabled())
        {
          AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain();
          localAccessibilityEvent.setEventType(8);
          paramAnonymousView.onInitializeAccessibilityEvent(localAccessibilityEvent);
          paramAnonymousView.dispatchPopulateAccessibilityEvent(localAccessibilityEvent);
          localAccessibilityManager.sendAccessibilityEvent(localAccessibilityEvent);
        }
      }
      
      public void onBindViewHolder(PreferenceViewHolder paramAnonymousPreferenceViewHolder)
      {
        super.onBindViewHolder(paramAnonymousPreferenceViewHolder);
        paramAnonymousPreferenceViewHolder.setDividerAllowedAbove(false);
        paramAnonymousPreferenceViewHolder.setDividerAllowedBelow(false);
        paramAnonymousPreferenceViewHolder = (TextView)paramAnonymousPreferenceViewHolder.findViewById(16908304);
        paramAnonymousPreferenceViewHolder.setText(getSummary());
        sendAccessibilityEvent(paramAnonymousPreferenceViewHolder);
      }
    };
    this.mSummaryPreference.setSelectable(false);
    this.mSummaryPreference.setPersistent(false);
    this.mSummaryPreference.setLayoutResource(2130969078);
    paramBundle.addPreference(this.mSummaryPreference);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    if ((this.mSettingsTitle != null) && (this.mSettingsIntent != null))
    {
      paramMenu = paramMenu.add(this.mSettingsTitle);
      paramMenu.setShowAsAction(1);
      paramMenu.setIntent(this.mSettingsIntent);
    }
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    removeActionBarToggleSwitch();
  }
  
  protected void onInstallSwitchBarToggleSwitch() {}
  
  protected abstract void onPreferenceToggled(String paramString, boolean paramBoolean);
  
  protected void onProcessArguments(Bundle paramBundle)
  {
    if (paramBundle == null)
    {
      getPreferenceScreen().removePreference(this.mSummaryPreference);
      return;
    }
    this.mPreferenceKey = paramBundle.getString("preference_key");
    if (paramBundle.containsKey("checked"))
    {
      boolean bool = paramBundle.getBoolean("checked");
      this.mSwitchBar.setCheckedInternal(bool);
    }
    if (paramBundle.containsKey("title")) {
      setTitle(paramBundle.getString("title"));
    }
    if (paramBundle.containsKey("summary"))
    {
      paramBundle = paramBundle.getCharSequence("summary");
      this.mSummaryPreference.setSummary(paramBundle);
      return;
    }
    getPreferenceScreen().removePreference(this.mSummaryPreference);
  }
  
  protected void onRemoveSwitchBarToggleSwitch() {}
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    this.mSwitchBar = ((SettingsActivity)getActivity()).getSwitchBar();
    this.mToggleSwitch = this.mSwitchBar.getSwitch();
    onProcessArguments(getArguments());
  }
  
  public void setTitle(String paramString)
  {
    getActivity().setTitle(paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\ToggleFeaturePreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */