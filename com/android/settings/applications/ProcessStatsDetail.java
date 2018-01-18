package com.android.settings.applications;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.text.format.Formatter;
import android.text.format.Formatter.BytesResult;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.android.settings.AppHeader;
import com.android.settings.CancellablePreference;
import com.android.settings.CancellablePreference.OnCancelListener;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SummaryPreference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ProcessStatsDetail
  extends SettingsPreferenceFragment
{
  public static final String EXTRA_MAX_MEMORY_USAGE = "max_memory_usage";
  public static final String EXTRA_PACKAGE_ENTRY = "package_entry";
  public static final String EXTRA_TOTAL_SCALE = "total_scale";
  public static final String EXTRA_TOTAL_TIME = "total_time";
  public static final String EXTRA_WEIGHT_TO_RAM = "weight_to_ram";
  private static final String KEY_DETAILS_HEADER = "status_header";
  private static final String KEY_FREQUENCY = "frequency";
  private static final String KEY_MAX_USAGE = "max_usage";
  private static final String KEY_PROCS = "processes";
  public static final int MENU_FORCE_STOP = 1;
  private static final String TAG = "ProcessStatsDetail";
  static final Comparator<ProcStatsEntry> sEntryCompare = new Comparator()
  {
    public int compare(ProcStatsEntry paramAnonymousProcStatsEntry1, ProcStatsEntry paramAnonymousProcStatsEntry2)
    {
      if (paramAnonymousProcStatsEntry1.mRunWeight < paramAnonymousProcStatsEntry2.mRunWeight) {
        return 1;
      }
      if (paramAnonymousProcStatsEntry1.mRunWeight > paramAnonymousProcStatsEntry2.mRunWeight) {
        return -1;
      }
      return 0;
    }
  };
  static final Comparator<ProcStatsEntry.Service> sServiceCompare = new Comparator()
  {
    public int compare(ProcStatsEntry.Service paramAnonymousService1, ProcStatsEntry.Service paramAnonymousService2)
    {
      if (paramAnonymousService1.mDuration < paramAnonymousService2.mDuration) {
        return 1;
      }
      if (paramAnonymousService1.mDuration > paramAnonymousService2.mDuration) {
        return -1;
      }
      return 0;
    }
  };
  static final Comparator<PkgService> sServicePkgCompare = new Comparator()
  {
    public int compare(ProcessStatsDetail.PkgService paramAnonymousPkgService1, ProcessStatsDetail.PkgService paramAnonymousPkgService2)
    {
      if (paramAnonymousPkgService1.mDuration < paramAnonymousPkgService2.mDuration) {
        return 1;
      }
      if (paramAnonymousPkgService1.mDuration > paramAnonymousPkgService2.mDuration) {
        return -1;
      }
      return 0;
    }
  };
  private ProcStatsPackageEntry mApp;
  private DevicePolicyManager mDpm;
  private MenuItem mForceStop;
  private double mMaxMemoryUsage;
  private long mOnePercentTime;
  private PackageManager mPm;
  private PreferenceCategory mProcGroup;
  private final ArrayMap<ComponentName, CancellablePreference> mServiceMap = new ArrayMap();
  private double mTotalScale;
  private long mTotalTime;
  private double mWeightToRam;
  
  private static String capitalize(String paramString)
  {
    char c = paramString.charAt(0);
    if (!Character.isLowerCase(c)) {
      return paramString;
    }
    return Character.toUpperCase(c) + paramString.substring(1);
  }
  
  private void checkForceStop()
  {
    if (this.mForceStop == null) {
      return;
    }
    if (((ProcStatsEntry)this.mApp.mEntries.get(0)).mUid < 10000)
    {
      this.mForceStop.setVisible(false);
      return;
    }
    int k = 0;
    int i = 0;
    while (i < this.mApp.mEntries.size())
    {
      ProcStatsEntry localProcStatsEntry = (ProcStatsEntry)this.mApp.mEntries.get(i);
      int j = 0;
      while (j < localProcStatsEntry.mPackages.size())
      {
        String str = (String)localProcStatsEntry.mPackages.get(j);
        if (this.mDpm.packageHasActiveAdmins(str))
        {
          this.mForceStop.setEnabled(false);
          return;
        }
        try
        {
          int m = this.mPm.getApplicationInfo(str, 0).flags;
          if ((m & 0x200000) == 0) {
            k = 1;
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          for (;;) {}
        }
        j += 1;
      }
      i += 1;
    }
    if (k != 0) {
      this.mForceStop.setVisible(true);
    }
  }
  
  private void createDetails()
  {
    addPreferencesFromResource(2131230732);
    this.mProcGroup = ((PreferenceCategory)findPreference("processes"));
    fillProcessesSection();
    Object localObject1 = (SummaryPreference)findPreference("status_header");
    int i;
    if (this.mApp.mRunWeight > this.mApp.mBgWeight)
    {
      i = 1;
      if (i == 0) {
        break label234;
      }
    }
    label234:
    for (double d1 = this.mApp.mRunWeight;; d1 = this.mApp.mBgWeight)
    {
      d1 *= this.mWeightToRam;
      float f = (float)(d1 / this.mMaxMemoryUsage);
      Object localObject2 = getActivity();
      ((SummaryPreference)localObject1).setRatios(f, 0.0F, 1.0F - f);
      localObject2 = Formatter.formatBytes(((Context)localObject2).getResources(), d1, 1);
      ((SummaryPreference)localObject1).setAmount(((Formatter.BytesResult)localObject2).value);
      ((SummaryPreference)localObject1).setUnits(((Formatter.BytesResult)localObject2).units);
      localObject1 = ProcStatsPackageEntry.getFrequency((float)Math.max(this.mApp.mRunDuration, this.mApp.mBgDuration) / (float)this.mTotalTime, getActivity());
      findPreference("frequency").setSummary((CharSequence)localObject1);
      d1 = Math.max(this.mApp.mMaxBgMem, this.mApp.mMaxRunMem);
      double d2 = this.mTotalScale;
      findPreference("max_usage").setSummary(Formatter.formatShortFileSize(getContext(), (d1 * d2 * 1024.0D)));
      return;
      i = 0;
      break;
    }
  }
  
  private void doStopService(String paramString1, String paramString2)
  {
    getActivity().stopService(new Intent().setClassName(paramString1, paramString2));
    updateRunningServices();
  }
  
  private void fillProcessesSection()
  {
    this.mProcGroup.removeAll();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    ProcStatsEntry localProcStatsEntry;
    if (i < this.mApp.mEntries.size())
    {
      localProcStatsEntry = (ProcStatsEntry)this.mApp.mEntries.get(i);
      if (localProcStatsEntry.mPackage.equals("os")) {}
      for (localProcStatsEntry.mLabel = localProcStatsEntry.mName;; localProcStatsEntry.mLabel = getProcessName(this.mApp.mUiLabel, localProcStatsEntry))
      {
        localArrayList.add(localProcStatsEntry);
        i += 1;
        break;
      }
    }
    Collections.sort(localArrayList, sEntryCompare);
    i = 0;
    while (i < localArrayList.size())
    {
      localProcStatsEntry = (ProcStatsEntry)localArrayList.get(i);
      Preference localPreference = new Preference(getPrefContext());
      localPreference.setTitle(localProcStatsEntry.mLabel);
      localPreference.setSelectable(false);
      long l1 = Math.max(localProcStatsEntry.mRunDuration, localProcStatsEntry.mBgDuration);
      long l2 = Math.max((localProcStatsEntry.mRunWeight * this.mWeightToRam), (localProcStatsEntry.mBgWeight * this.mWeightToRam));
      localPreference.setSummary(getString(2131693457, new Object[] { Formatter.formatShortFileSize(getActivity(), l2), ProcStatsPackageEntry.getFrequency((float)l1 / (float)this.mTotalTime, getActivity()) }));
      this.mProcGroup.addPreference(localPreference);
      i += 1;
    }
    if (this.mProcGroup.getPreferenceCount() < 2) {
      getPreferenceScreen().removePreference(this.mProcGroup);
    }
  }
  
  private void fillServicesSection(ProcStatsEntry paramProcStatsEntry, PreferenceCategory paramPreferenceCategory)
  {
    Object localObject3 = new HashMap();
    ArrayList localArrayList1 = new ArrayList();
    int i = 0;
    Object localObject2;
    int j;
    Object localObject1;
    while (i < paramProcStatsEntry.mServices.size())
    {
      String str = (String)paramProcStatsEntry.mServices.keyAt(i);
      localObject2 = null;
      ArrayList localArrayList2 = (ArrayList)paramProcStatsEntry.mServices.valueAt(i);
      j = localArrayList2.size() - 1;
      while (j >= 0)
      {
        ProcStatsEntry.Service localService = (ProcStatsEntry.Service)localArrayList2.get(j);
        localObject1 = localObject2;
        if (localService.mDuration >= this.mOnePercentTime)
        {
          localObject1 = localObject2;
          if (localObject2 == null)
          {
            localObject2 = (PkgService)((HashMap)localObject3).get(str);
            localObject1 = localObject2;
            if (localObject2 == null)
            {
              localObject1 = new PkgService();
              ((HashMap)localObject3).put(str, localObject1);
              localArrayList1.add(localObject1);
            }
          }
          ((PkgService)localObject1).mServices.add(localService);
          ((PkgService)localObject1).mDuration += localService.mDuration;
        }
        j -= 1;
        localObject2 = localObject1;
      }
      i += 1;
    }
    Collections.sort(localArrayList1, sServicePkgCompare);
    i = 0;
    while (i < localArrayList1.size())
    {
      paramProcStatsEntry = ((PkgService)localArrayList1.get(i)).mServices;
      Collections.sort(paramProcStatsEntry, sServiceCompare);
      j = 0;
      while (j < paramProcStatsEntry.size())
      {
        localObject1 = (ProcStatsEntry.Service)paramProcStatsEntry.get(j);
        localObject2 = getLabel((ProcStatsEntry.Service)localObject1);
        localObject3 = new CancellablePreference(getPrefContext());
        ((CancellablePreference)localObject3).setSelectable(false);
        ((CancellablePreference)localObject3).setTitle((CharSequence)localObject2);
        ((CancellablePreference)localObject3).setSummary(ProcStatsPackageEntry.getFrequency((float)((ProcStatsEntry.Service)localObject1).mDuration / (float)this.mTotalTime, getActivity()));
        paramPreferenceCategory.addPreference((Preference)localObject3);
        this.mServiceMap.put(new ComponentName(((ProcStatsEntry.Service)localObject1).mPackage, ((ProcStatsEntry.Service)localObject1).mName), localObject3);
        j += 1;
      }
      i += 1;
    }
  }
  
  private CharSequence getLabel(ProcStatsEntry.Service paramService)
  {
    try
    {
      Object localObject = getPackageManager().getServiceInfo(new ComponentName(paramService.mPackage, paramService.mName), 0);
      if (((ServiceInfo)localObject).labelRes != 0)
      {
        localObject = ((ServiceInfo)localObject).loadLabel(getPackageManager());
        return (CharSequence)localObject;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      String str = paramService.mName;
      int i = str.lastIndexOf('.');
      paramService = str;
      if (i >= 0)
      {
        paramService = str;
        if (i < str.length() - 1) {
          paramService = str.substring(i + 1);
        }
      }
    }
    return paramService;
  }
  
  private static String getProcessName(String paramString, ProcStatsEntry paramProcStatsEntry)
  {
    String str = paramProcStatsEntry.mName;
    if (str.contains(":")) {
      return capitalize(str.substring(str.lastIndexOf(':') + 1));
    }
    if (str.startsWith(paramProcStatsEntry.mPackage))
    {
      if (str.length() == paramProcStatsEntry.mPackage.length()) {
        return paramString;
      }
      int j = paramProcStatsEntry.mPackage.length();
      int i = j;
      if (str.charAt(j) == '.') {
        i = j + 1;
      }
      return capitalize(str.substring(i));
    }
    return str;
  }
  
  private void killProcesses()
  {
    ActivityManager localActivityManager = (ActivityManager)getActivity().getSystemService("activity");
    int i = 0;
    while (i < this.mApp.mEntries.size())
    {
      ProcStatsEntry localProcStatsEntry = (ProcStatsEntry)this.mApp.mEntries.get(i);
      int j = 0;
      while (j < localProcStatsEntry.mPackages.size())
      {
        localActivityManager.forceStopPackage((String)localProcStatsEntry.mPackages.get(j));
        j += 1;
      }
      i += 1;
    }
  }
  
  private void showStopServiceDialog(final String paramString1, final String paramString2)
  {
    new AlertDialog.Builder(getActivity()).setTitle(2131692221).setMessage(2131692222).setPositiveButton(2131692133, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        ProcessStatsDetail.-wrap0(ProcessStatsDetail.this, paramString1, paramString2);
      }
    }).setNegativeButton(2131692134, null).show();
  }
  
  private void stopService(String paramString1, String paramString2)
  {
    try
    {
      if ((getActivity().getPackageManager().getApplicationInfo(paramString1, 0).flags & 0x1) != 0)
      {
        showStopServiceDialog(paramString1, paramString2);
        return;
      }
    }
    catch (PackageManager.NameNotFoundException paramString2)
    {
      Log.e("ProcessStatsDetail", "Can't find app " + paramString1, paramString2);
      return;
    }
    doStopService(paramString1, paramString2);
  }
  
  private void updateRunningServices()
  {
    List localList = ((ActivityManager)getActivity().getSystemService("activity")).getRunningServices(Integer.MAX_VALUE);
    int j = this.mServiceMap.size();
    int i = 0;
    while (i < j)
    {
      ((CancellablePreference)this.mServiceMap.valueAt(i)).setCancellable(false);
      i += 1;
    }
    j = localList.size();
    i = 0;
    if (i < j)
    {
      final Object localObject = (ActivityManager.RunningServiceInfo)localList.get(i);
      if ((!((ActivityManager.RunningServiceInfo)localObject).started) && (((ActivityManager.RunningServiceInfo)localObject).clientLabel == 0)) {}
      for (;;)
      {
        i += 1;
        break;
        if ((((ActivityManager.RunningServiceInfo)localObject).flags & 0x8) == 0)
        {
          localObject = ((ActivityManager.RunningServiceInfo)localObject).service;
          CancellablePreference localCancellablePreference = (CancellablePreference)this.mServiceMap.get(localObject);
          if (localCancellablePreference != null)
          {
            localCancellablePreference.setOnCancelListener(new CancellablePreference.OnCancelListener()
            {
              public void onCancel(CancellablePreference paramAnonymousCancellablePreference)
              {
                ProcessStatsDetail.-wrap1(ProcessStatsDetail.this, localObject.getPackageName(), localObject.getClassName());
              }
            });
            localCancellablePreference.setCancellable(true);
          }
        }
      }
    }
  }
  
  protected int getMetricsCategory()
  {
    return 21;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPm = getActivity().getPackageManager();
    this.mDpm = ((DevicePolicyManager)getActivity().getSystemService("device_policy"));
    paramBundle = getArguments();
    this.mApp = ((ProcStatsPackageEntry)paramBundle.getParcelable("package_entry"));
    this.mApp.retrieveUiData(getActivity(), this.mPm);
    this.mWeightToRam = paramBundle.getDouble("weight_to_ram");
    this.mTotalTime = paramBundle.getLong("total_time");
    this.mMaxMemoryUsage = paramBundle.getDouble("max_memory_usage");
    this.mTotalScale = paramBundle.getDouble("total_scale");
    this.mOnePercentTime = (this.mTotalTime / 100L);
    this.mServiceMap.clear();
    createDetails();
    setHasOptionsMenu(true);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    this.mForceStop = paramMenu.add(0, 1, 0, 2131692087);
    checkForceStop();
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return false;
    }
    killProcesses();
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    checkForceStop();
    updateRunningServices();
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    if (this.mApp.mUiTargetApp == null)
    {
      finish();
      return;
    }
    if (this.mApp.mUiTargetApp != null) {}
    for (paramView = this.mApp.mUiTargetApp.loadIcon(this.mPm);; paramView = new ColorDrawable(0))
    {
      AppHeader.createAppHeader(this, paramView, this.mApp.mUiLabel, this.mApp.mPackage, this.mApp.mUiTargetApp.uid);
      return;
    }
  }
  
  static class PkgService
  {
    long mDuration;
    final ArrayList<ProcStatsEntry.Service> mServices = new ArrayList();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ProcessStatsDetail.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */