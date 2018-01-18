package com.android.settings.applications;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.util.TimeUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.settings.SettingsActivity;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProcessStatsUi
  extends ProcessStatsBase
{
  public static final int[] BACKGROUND_AND_SYSTEM_PROC_STATES = { 0, 2, 3, 4, 5, 6, 7, 8, 9 };
  public static final int[] CACHED_PROC_STATES;
  static final boolean DEBUG = false;
  public static final int[] FOREGROUND_PROC_STATES = { 1 };
  private static final String KEY_APP_LIST = "app_list";
  private static final int MENU_SHOW_AVG = 1;
  private static final int MENU_SHOW_MAX = 2;
  static final String TAG = "ProcessStatsUi";
  static final Comparator<ProcStatsPackageEntry> sMaxPackageEntryCompare = new Comparator()
  {
    public int compare(ProcStatsPackageEntry paramAnonymousProcStatsPackageEntry1, ProcStatsPackageEntry paramAnonymousProcStatsPackageEntry2)
    {
      double d1 = Math.max(paramAnonymousProcStatsPackageEntry2.mMaxBgMem, paramAnonymousProcStatsPackageEntry2.mMaxRunMem);
      double d2 = Math.max(paramAnonymousProcStatsPackageEntry1.mMaxBgMem, paramAnonymousProcStatsPackageEntry1.mMaxRunMem);
      if (d2 == d1) {
        return 0;
      }
      if (d2 < d1) {
        return 1;
      }
      return -1;
    }
  };
  static final Comparator<ProcStatsPackageEntry> sPackageEntryCompare;
  private PreferenceGroup mAppListGroup;
  private MenuItem mMenuAvg;
  private MenuItem mMenuMax;
  private PackageManager mPm;
  private boolean mShowMax;
  
  static
  {
    CACHED_PROC_STATES = new int[] { 11, 12, 13 };
    sPackageEntryCompare = new Comparator()
    {
      public int compare(ProcStatsPackageEntry paramAnonymousProcStatsPackageEntry1, ProcStatsPackageEntry paramAnonymousProcStatsPackageEntry2)
      {
        double d1 = Math.max(paramAnonymousProcStatsPackageEntry2.mRunWeight, paramAnonymousProcStatsPackageEntry2.mBgWeight);
        double d2 = Math.max(paramAnonymousProcStatsPackageEntry1.mRunWeight, paramAnonymousProcStatsPackageEntry1.mBgWeight);
        if (d2 == d1) {
          return 0;
        }
        if (d2 < d1) {
          return 1;
        }
        return -1;
      }
    };
  }
  
  public static String makeDuration(long paramLong)
  {
    StringBuilder localStringBuilder = new StringBuilder(32);
    TimeUtils.formatDuration(paramLong, localStringBuilder);
    return localStringBuilder.toString();
  }
  
  private void updateMenu()
  {
    MenuItem localMenuItem = this.mMenuMax;
    if (this.mShowMax) {}
    for (boolean bool = false;; bool = true)
    {
      localMenuItem.setVisible(bool);
      this.mMenuAvg.setVisible(this.mShowMax);
      return;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 23;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPm = getActivity().getPackageManager();
    addPreferencesFromResource(2131230832);
    this.mAppListGroup = ((PreferenceGroup)findPreference("app_list"));
    setHasOptionsMenu(true);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    this.mMenuAvg = paramMenu.add(0, 1, 0, 2131693508);
    this.mMenuMax = paramMenu.add(0, 2, 0, 2131693509);
    updateMenu();
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    }
    if (this.mShowMax) {}
    for (boolean bool = false;; bool = true)
    {
      this.mShowMax = bool;
      refreshUi();
      updateMenu();
      return true;
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if (!(paramPreference instanceof ProcessStatsPreference)) {
      return false;
    }
    ProcessStatsPreference localProcessStatsPreference = (ProcessStatsPreference)paramPreference;
    ProcStatsData.MemInfo localMemInfo = this.mStatsManager.getMemInfo();
    launchMemoryDetail((SettingsActivity)getActivity(), localMemInfo, localProcessStatsPreference.getEntry(), true);
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
  }
  
  public void refreshUi()
  {
    this.mAppListGroup.removeAll();
    this.mAppListGroup.setOrderingAsAdded(false);
    Object localObject = this.mAppListGroup;
    if (this.mShowMax) {}
    Activity localActivity;
    ProcStatsData.MemInfo localMemInfo;
    List localList;
    for (int i = 2131693502;; i = 2131693501)
    {
      ((PreferenceGroup)localObject).setTitle(i);
      localActivity = getActivity();
      localMemInfo = this.mStatsManager.getMemInfo();
      localList = this.mStatsManager.getEntries();
      i = 0;
      int j = localList.size();
      while (i < j)
      {
        ((ProcStatsPackageEntry)localList.get(i)).updateMetrics();
        i += 1;
      }
    }
    double d1;
    label145:
    label148:
    ProcessStatsPreference localProcessStatsPreference;
    PackageManager localPackageManager;
    double d2;
    double d3;
    if (this.mShowMax)
    {
      localObject = sMaxPackageEntryCompare;
      Collections.sort(localList, (Comparator)localObject);
      if (!this.mShowMax) {
        break label284;
      }
      d1 = localMemInfo.realTotalRam;
      i = 0;
      if (i >= localList.size()) {
        return;
      }
      localObject = (ProcStatsPackageEntry)localList.get(i);
      if (((ProcStatsPackageEntry)localObject).mPackage != null)
      {
        localProcessStatsPreference = new ProcessStatsPreference(getPrefContext());
        ((ProcStatsPackageEntry)localObject).retrieveUiData(localActivity, this.mPm);
        localPackageManager = this.mPm;
        d2 = localMemInfo.weightToRam;
        d3 = localMemInfo.totalScale;
        if (!this.mShowMax) {
          break label299;
        }
      }
    }
    label284:
    label299:
    for (boolean bool = false;; bool = true)
    {
      localProcessStatsPreference.init((ProcStatsPackageEntry)localObject, localPackageManager, d1, d2, d3, bool);
      localProcessStatsPreference.setOrder(i);
      this.mAppListGroup.addPreference(localProcessStatsPreference);
      i += 1;
      break label148;
      localObject = sPackageEntryCompare;
      break;
      d1 = localMemInfo.usedWeight * localMemInfo.weightToRam;
      break label145;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ProcessStatsUi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */