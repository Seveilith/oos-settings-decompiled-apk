package com.oneplus.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import java.util.ArrayList;
import java.util.List;

public class OPStatusBarCustomizeSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Indexable, Preference.OnPreferenceClickListener
{
  private static final int BATTERY_BAR_STYLE = 0;
  private static final int BATTERY_CIRCLE_STYLE = 1;
  private static final int BATTERY_HIDDEN_STYLE = 2;
  private static final String KEY_BATTERY_PERCENT = "enable_show_statusbar";
  private static final String KEY_BATTERY_STYLE = "battery_style";
  private static final String KEY_STATUSBAR_ICON_MANGER = "status_bar_icon_manager";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new StatusBarCustomizeIndexProvider();
  private static final String SHOW_POWER_PERCENT_IN_STATUSBAR_TITLE = "show_power_percent_in_statusbar_title";
  private static final String TAG = "OPStatusBarCustomizeSettings";
  private ListPreference mBatteryStylePreference;
  private Context mContext;
  private SwitchPreference mShowBatteryPercentPreference;
  private Preference mStatusBarIconMangerPreference;
  
  private void updateBatteryStylePreferenceDescription(int paramInt)
  {
    if (this.mBatteryStylePreference != null)
    {
      int i = paramInt;
      if (paramInt >= this.mBatteryStylePreference.getEntries().length) {
        i = this.mBatteryStylePreference.getEntries().length - 1;
      }
      CharSequence[] arrayOfCharSequence = this.mBatteryStylePreference.getEntries();
      this.mBatteryStylePreference.setSummary(arrayOfCharSequence[i]);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230817);
    this.mContext = getActivity();
    this.mBatteryStylePreference = ((ListPreference)findPreference("battery_style"));
    int i = Settings.System.getInt(this.mContext.getContentResolver(), "status_bar_battery_style", 0);
    this.mBatteryStylePreference.setValue(String.valueOf(i));
    updateBatteryStylePreferenceDescription(i);
    this.mBatteryStylePreference.setOnPreferenceChangeListener(this);
    this.mShowBatteryPercentPreference = ((SwitchPreference)findPreference("enable_show_statusbar"));
    this.mStatusBarIconMangerPreference = findPreference("status_bar_icon_manager");
    this.mStatusBarIconMangerPreference.setOnPreferenceClickListener(this);
    i = Settings.System.getInt(this.mContext.getContentResolver(), "status_bar_show_battery_percent", 0);
    paramBundle = this.mShowBatteryPercentPreference;
    if (i == 1) {}
    for (boolean bool = true;; bool = false)
    {
      paramBundle.setChecked(bool);
      this.mShowBatteryPercentPreference.setOnPreferenceChangeListener(this);
      paramBundle = getActivity().getIntent();
      if ((paramBundle != null) && (paramBundle.hasExtra("show_power_percent_in_statusbar_title")) && (!paramBundle.getBooleanExtra("show_power_percent_in_statusbar_title", true))) {
        getPreferenceScreen().removePreference(this.mShowBatteryPercentPreference);
      }
      return;
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    paramPreference = paramPreference.getKey();
    int i;
    if ("battery_style".equals(paramPreference))
    {
      i = Integer.parseInt((String)paramObject);
      Settings.System.putInt(this.mContext.getContentResolver(), "status_bar_battery_style", i);
      updateBatteryStylePreferenceDescription(i);
      return true;
    }
    if ("enable_show_statusbar".equals(paramPreference))
    {
      boolean bool = ((Boolean)paramObject).booleanValue();
      paramPreference = this.mContext.getContentResolver();
      if (bool) {}
      for (i = 1;; i = 0)
      {
        Settings.System.putInt(paramPreference, "status_bar_show_battery_percent", i);
        return true;
      }
    }
    return true;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if ("status_bar_icon_manager".equals(paramPreference.getKey()))
    {
      new Intent(this.mContext, OPStatusBarCustomizeIconSettings.class);
      startFragment(this, OPStatusBarCustomizeIconSettings.class.getName(), 0, 0, null);
      return true;
    }
    return false;
  }
  
  private static class StatusBarCustomizeIndexProvider
    extends BaseSearchIndexProvider
  {
    boolean mIsPrimary;
    
    public StatusBarCustomizeIndexProvider()
    {
      if (UserHandle.myUserId() == 0) {
        bool = true;
      }
      this.mIsPrimary = bool;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramContext, boolean paramBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      if (!this.mIsPrimary) {
        return localArrayList;
      }
      paramContext = new SearchIndexableResource(paramContext);
      paramContext.xmlResId = 2131230817;
      localArrayList.add(paramContext);
      return localArrayList;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPStatusBarCustomizeSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */