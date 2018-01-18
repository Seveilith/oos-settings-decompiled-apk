package com.android.settings.location;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Switch;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.applications.InstalledAppDetails;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.location.RecentLocationApps;
import com.android.settingslib.location.RecentLocationApps.Request;
import com.oneplus.settings.ui.OPButtonPreference;
import com.oneplus.settings.ui.OPPreferenceDivider;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class LocationSettings
  extends LocationSettingsBase
  implements SwitchBar.OnSwitchChangeListener
{
  private static final String EXTRA_PREF_KEY = "pref_key";
  private static final String KEY_LOCATION_MODE = "location_mode";
  private static final String KEY_LOCATION_SERVICES = "location_services";
  private static final String KEY_MANAGED_PROFILE_SWITCH = "managed_profile_location_switch";
  private static final String KEY_PREFERENCE_DIVIDER_LINE = "preference_divider_line";
  private static final String KEY_RECENT_LOCATION_REQUESTS = "recent_location_requests";
  private static final int MENU_SCANNING = 1;
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new LocationSettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
    }
  };
  private static final String TAG = "LocationSettings";
  private SettingsInjector injector;
  private PreferenceCategory mCategoryRecentLocationRequests;
  private final BroadcastReceiver mCheckKillProcessesReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (getResultCode() != 0) {}
      for (boolean bool = true;; bool = false) {
        try
        {
          paramAnonymousContext = paramAnonymousIntent.getStringExtra("pref_key");
          paramAnonymousContext = (OPButtonPreference)LocationSettings.this.findPreference(paramAnonymousContext);
          paramAnonymousContext.setButtonEnable(bool);
          paramAnonymousContext.setButtonVisible(bool);
          Log.d("LocationSettings", "mCheckKillProcessesReceiver flag:" + bool);
          if (!bool) {
            paramAnonymousContext.setSummary("");
          }
          return;
        }
        catch (Exception paramAnonymousContext)
        {
          Log.e("LocationSettings", "mCheckKillProcessesReceiver error:" + paramAnonymousContext.getMessage());
          paramAnonymousContext.printStackTrace();
        }
      }
    }
  };
  private DevicePolicyManager mDpm;
  private final BroadcastReceiver mHighPowerChangeReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      Log.i("LocationSettings", "mHighPowerChangeReceiver receive action=" + paramAnonymousContext);
      if ("android.location.HIGH_POWER_REQUEST_CHANGE".equals(paramAnonymousContext)) {
        LocationSettings.-wrap0(LocationSettings.this);
      }
    }
  };
  private IntentFilter mIntentFilter;
  private LocationManager mLocationManager;
  private Preference mLocationMode;
  private UserHandle mManagedProfile;
  private OPRestrictedSwitchPreference mManagedProfileSwitch;
  private Preference.OnPreferenceClickListener mManagedProfileSwitchClickListener = new Preference.OnPreferenceClickListener()
  {
    public boolean onPreferenceClick(Preference paramAnonymousPreference)
    {
      boolean bool2 = LocationSettings.-get2(LocationSettings.this).isChecked();
      paramAnonymousPreference = LocationSettings.-get3(LocationSettings.this);
      boolean bool1;
      if (bool2)
      {
        bool1 = false;
        paramAnonymousPreference.setUserRestriction("no_share_location", bool1, LocationSettings.-get1(LocationSettings.this));
        paramAnonymousPreference = LocationSettings.-get2(LocationSettings.this);
        if (!bool2) {
          break label69;
        }
      }
      label69:
      for (int i = 2131693335;; i = 2131693336)
      {
        paramAnonymousPreference.setSummary(i);
        return true;
        bool1 = true;
        break;
      }
    }
  };
  private OPPreferenceDivider mOPPreferenceDivider;
  private BroadcastReceiver mReceiver;
  private ApplicationsState mState;
  private Switch mSwitch;
  private SwitchBar mSwitchBar;
  private UserManager mUm;
  private boolean mValidListener = false;
  
  private void addLocationServices(Context paramContext, PreferenceScreen paramPreferenceScreen, boolean paramBoolean)
  {
    PreferenceCategory localPreferenceCategory = (PreferenceCategory)paramPreferenceScreen.findPreference("location_services");
    this.injector = new SettingsInjector(paramContext);
    Object localObject = this.injector;
    if (paramBoolean) {}
    for (int i = UserHandle.myUserId();; i = -2)
    {
      localObject = ((SettingsInjector)localObject).getInjectedSettings(i);
      this.mReceiver = new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          if (Log.isLoggable("LocationSettings", 3)) {
            Log.d("LocationSettings", "Received settings change intent: " + paramAnonymousIntent);
          }
          LocationSettings.-get0(LocationSettings.this).reloadStatusMessages();
        }
      };
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.location.InjectedSettingChanged");
      paramContext.registerReceiver(this.mReceiver, localIntentFilter);
      if (((List)localObject).size() <= 0) {
        break;
      }
      addPreferencesSorted((List)localObject, localPreferenceCategory);
      paramPreferenceScreen.addPreference(this.mOPPreferenceDivider);
      return;
    }
    paramPreferenceScreen.removePreference(localPreferenceCategory);
    paramPreferenceScreen.removePreference(this.mOPPreferenceDivider);
  }
  
  private void addPreferencesSorted(List<Preference> paramList, PreferenceGroup paramPreferenceGroup)
  {
    Collections.sort(paramList, new Comparator()
    {
      public int compare(Preference paramAnonymousPreference1, Preference paramAnonymousPreference2)
      {
        return paramAnonymousPreference1.getTitle().toString().compareTo(paramAnonymousPreference2.getTitle().toString());
      }
    });
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      paramPreferenceGroup.addPreference((Preference)paramList.next());
    }
  }
  
  private void changeManagedProfileLocationAccessStatus(boolean paramBoolean)
  {
    boolean bool1 = false;
    if (this.mManagedProfileSwitch == null) {
      return;
    }
    this.mManagedProfileSwitch.setOnPreferenceClickListener(null);
    Object localObject = RestrictedLockUtils.checkIfRestrictionEnforced(getActivity(), "no_share_location", this.mManagedProfile.getIdentifier());
    boolean bool2 = isManagedProfileRestrictedByBase();
    if ((!bool2) && (localObject != null))
    {
      this.mManagedProfileSwitch.setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin)localObject);
      this.mManagedProfileSwitch.setChecked(false);
      return;
    }
    this.mManagedProfileSwitch.setEnabled(paramBoolean);
    int i = 2131693336;
    if (!paramBoolean)
    {
      this.mManagedProfileSwitch.setChecked(false);
      this.mManagedProfileSwitch.setSummary(i);
      return;
    }
    localObject = this.mManagedProfileSwitch;
    if (bool2)
    {
      paramBoolean = bool1;
      label116:
      ((OPRestrictedSwitchPreference)localObject).setChecked(paramBoolean);
      if (!bool2) {
        break label150;
      }
    }
    label150:
    for (i = 2131693336;; i = 2131693335)
    {
      this.mManagedProfileSwitch.setOnPreferenceClickListener(this.mManagedProfileSwitchClickListener);
      break;
      paramBoolean = true;
      break label116;
    }
  }
  
  private PreferenceScreen createPreferenceHierarchy()
  {
    final SettingsActivity localSettingsActivity = (SettingsActivity)getActivity();
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null) {
      localPreferenceScreen.removeAll();
    }
    addPreferencesFromResource(2131230777);
    localPreferenceScreen = getPreferenceScreen();
    setupManagedProfileCategory(localPreferenceScreen);
    this.mLocationMode = localPreferenceScreen.findPreference("location_mode");
    this.mLocationMode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        localSettingsActivity.startPreferencePanel(LocationMode.class.getName(), null, 2131691955, null, LocationSettings.this, 0);
        return true;
      }
    });
    this.mCategoryRecentLocationRequests = ((PreferenceCategory)localPreferenceScreen.findPreference("recent_location_requests"));
    this.mOPPreferenceDivider = ((OPPreferenceDivider)localPreferenceScreen.findPreference("preference_divider_line"));
    Object localObject2 = new RecentLocationApps(localSettingsActivity);
    Object localObject1 = getCurrentUsingGpsListForUid();
    Log.d("LocationSettings", "getCurrentProviderPackageList currentUsingGpsPkgs:" + localObject1);
    localObject2 = ((RecentLocationApps)localObject2).getAppList();
    ArrayList localArrayList = new ArrayList(((List)localObject2).size());
    Iterator localIterator = ((Iterable)localObject2).iterator();
    boolean bool1;
    boolean bool2;
    if (localIterator.hasNext())
    {
      RecentLocationApps.Request localRequest = (RecentLocationApps.Request)localIterator.next();
      Log.d("LocationSettings", "recentLocationRequests request.uid=" + localRequest.uid);
      final OPButtonPreference localOPButtonPreference = new OPButtonPreference(getPrefContext());
      localOPButtonPreference.setKey(UUID.randomUUID().toString().replace("-", ""));
      localOPButtonPreference.setIcon(localRequest.icon);
      localOPButtonPreference.setTitle(localRequest.label);
      final ApplicationsState.AppEntry localAppEntry;
      if ((localObject1 != null) && (((List)localObject1).contains(String.valueOf(localRequest.uid))))
      {
        int i = localRequest.userHandle.getIdentifier();
        localAppEntry = this.mState.getEntry(localRequest.packageName, i);
        bool1 = RestrictedLockUtils.hasBaseUserRestriction(getActivity(), "no_control_apps", UserHandle.myUserId());
        bool2 = this.mDpm.packageHasActiveAdmins(localRequest.packageName);
        if ((bool2) || (bool1))
        {
          Log.d("LocationSettings", "packageHasActiveAdmins:" + bool2 + ", mAppsControlDisallowedBySystem:" + bool1);
          localOPButtonPreference.setButtonEnable(false);
          localOPButtonPreference.setButtonVisible(false);
        }
      }
      for (;;)
      {
        localOPButtonPreference.setOnPreferenceClickListener(new PackageEntryClickedListener(localRequest.packageName, localRequest.userHandle));
        localArrayList.add(localOPButtonPreference);
        break;
        localOPButtonPreference.setSummary(2131690430);
        localOPButtonPreference.setButtonVisible(true);
        localOPButtonPreference.setButtonEnable(true);
        localOPButtonPreference.setButtonString(getString(2131690429));
        localOPButtonPreference.setOnButtonClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            LocationSettings.-wrap2(LocationSettings.this, localAppEntry, localOPButtonPreference);
            localOPButtonPreference.setButtonEnable(false);
          }
        });
        continue;
        localOPButtonPreference.setButtonVisible(false);
      }
    }
    if (((List)localObject2).size() > 0) {
      addPreferencesSorted(localArrayList, this.mCategoryRecentLocationRequests);
    }
    for (;;)
    {
      bool2 = false;
      bool1 = bool2;
      if (this.mManagedProfile != null)
      {
        bool1 = bool2;
        if (this.mUm.hasUserRestriction("no_share_location", this.mManagedProfile)) {
          bool1 = true;
        }
      }
      addLocationServices(localSettingsActivity, localPreferenceScreen, bool1);
      refreshLocationMode();
      return localPreferenceScreen;
      localObject1 = new Preference(getPrefContext());
      ((Preference)localObject1).setLayoutResource(2130968743);
      ((Preference)localObject1).setTitle(2131691951);
      ((Preference)localObject1).setSelectable(false);
      this.mCategoryRecentLocationRequests.addPreference((Preference)localObject1);
    }
  }
  
  private void forceStopPackage(ApplicationsState.AppEntry paramAppEntry, OPButtonPreference paramOPButtonPreference)
  {
    String str = paramAppEntry.info.packageName;
    ActivityManager localActivityManager = (ActivityManager)getActivity().getSystemService("activity");
    int i = UserHandle.getUserId(paramAppEntry.info.uid);
    Log.e("LocationSettings", "forceStopPackage app userId=" + i);
    localActivityManager.forceStopPackageAsUser(str, i);
    this.mState.invalidatePackage(str, i);
    checkForceStop(this.mState.getEntry(str, i), paramOPButtonPreference);
  }
  
  private List<String> getCurrentUsingGpsList()
  {
    try
    {
      List localList = (List)LocationManager.class.getDeclaredMethod("getCurrentProviderPackageList", new Class[] { String.class }).invoke(this.mLocationManager, new Object[] { "gps" });
      return localList;
    }
    catch (Exception localException)
    {
      Log.e("LocationSettings", "getCurrentUsingGpsList Exception:" + localException.getMessage());
      localException.printStackTrace();
    }
    return null;
  }
  
  private List<String> getCurrentUsingGpsListForUid()
  {
    try
    {
      List localList = (List)LocationManager.class.getDeclaredMethod("getCurrentProviderPackageListsForInteger", new Class[] { String.class }).invoke(this.mLocationManager, new Object[] { "gps" });
      return localList;
    }
    catch (Exception localException)
    {
      Log.e("LocationSettings", "getCurrentUsingGpsListForUid Exception:" + localException.getMessage());
      localException.printStackTrace();
    }
    return null;
  }
  
  private static int getLocationString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 0: 
      return 2131691949;
    case 1: 
      return 2131691948;
    case 2: 
      return 2131691947;
    }
    return 2131691946;
  }
  
  private boolean isManagedProfileRestrictedByBase()
  {
    if (this.mManagedProfile == null) {
      return false;
    }
    return this.mUm.hasBaseUserRestriction("no_share_location", this.mManagedProfile);
  }
  
  private void setupManagedProfileCategory(PreferenceScreen paramPreferenceScreen)
  {
    this.mManagedProfile = Utils.getManagedProfile(this.mUm);
    if (this.mManagedProfile == null)
    {
      paramPreferenceScreen.removePreference(paramPreferenceScreen.findPreference("managed_profile_location_switch"));
      this.mManagedProfileSwitch = null;
      return;
    }
    this.mManagedProfileSwitch = ((OPRestrictedSwitchPreference)paramPreferenceScreen.findPreference("managed_profile_location_switch"));
    this.mManagedProfileSwitch.setOnPreferenceClickListener(null);
  }
  
  public void checkForceStop(ApplicationsState.AppEntry paramAppEntry, OPButtonPreference paramOPButtonPreference)
  {
    Intent localIntent = new Intent("android.intent.action.QUERY_PACKAGE_RESTART", Uri.fromParts("package", paramAppEntry.info.packageName, null));
    localIntent.putExtra("android.intent.extra.PACKAGES", new String[] { paramAppEntry.info.packageName });
    localIntent.putExtra("android.intent.extra.UID", paramAppEntry.info.uid);
    localIntent.putExtra("android.intent.extra.user_handle", UserHandle.getUserId(paramAppEntry.info.uid));
    localIntent.putExtra("pref_key", paramOPButtonPreference.getKey());
    getActivity().sendOrderedBroadcastAsUser(localIntent, UserHandle.CURRENT, null, this.mCheckKillProcessesReceiver, null, 0, null, null);
  }
  
  public int getHelpResource()
  {
    return 2131693026;
  }
  
  protected int getMetricsCategory()
  {
    return 63;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    paramBundle = (SettingsActivity)getActivity();
    this.mUm = ((UserManager)paramBundle.getSystemService("user"));
    setHasOptionsMenu(true);
    this.mSwitchBar = paramBundle.getSwitchBar();
    this.mSwitch = this.mSwitchBar.getSwitch();
    this.mSwitchBar.show();
    this.mState = ApplicationsState.getInstance(getActivity().getApplication());
    this.mDpm = ((DevicePolicyManager)getPrefContext().getSystemService("device_policy"));
    this.mLocationManager = ((LocationManager)getPrefContext().getSystemService("location"));
    this.mIntentFilter = new IntentFilter("android.location.HIGH_POWER_REQUEST_CHANGE");
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 1, 0, 2131691959);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    this.mSwitchBar.hide();
  }
  
  public void onModeChanged(int paramInt, boolean paramBoolean)
  {
    boolean bool3 = false;
    int i = getLocationString(paramInt);
    if (i != 0) {
      this.mLocationMode.setSummary(i);
    }
    boolean bool1;
    Object localObject;
    if (paramInt != 0)
    {
      bool1 = true;
      localObject = RestrictedLockUtils.checkIfRestrictionEnforced(getActivity(), "no_share_location", UserHandle.myUserId());
      if ((RestrictedLockUtils.hasBaseUserRestriction(getActivity(), "no_share_location", UserHandle.myUserId())) || (localObject == null)) {
        break label180;
      }
      this.mSwitchBar.setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin)localObject);
      localObject = this.mLocationMode;
      bool2 = bool3;
      if (bool1) {
        if (!paramBoolean) {
          break label209;
        }
      }
    }
    label180:
    label209:
    for (boolean bool2 = bool3;; bool2 = true)
    {
      ((Preference)localObject).setEnabled(bool2);
      this.mCategoryRecentLocationRequests.setEnabled(bool1);
      if (bool1 != this.mSwitch.isChecked())
      {
        if (this.mValidListener) {
          this.mSwitchBar.removeOnSwitchChangeListener(this);
        }
        this.mSwitch.setChecked(bool1);
        if (this.mValidListener) {
          this.mSwitchBar.addOnSwitchChangeListener(this);
        }
      }
      changeManagedProfileLocationAccessStatus(bool1);
      this.injector.reloadStatusMessages();
      return;
      bool1 = false;
      break;
      localObject = this.mSwitchBar;
      if (paramBoolean) {}
      for (bool2 = false;; bool2 = true)
      {
        ((SwitchBar)localObject).setEnabled(bool2);
        break;
      }
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    SettingsActivity localSettingsActivity = (SettingsActivity)getActivity();
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    }
    localSettingsActivity.startPreferencePanel(ScanningSettings.class.getName(), null, 2131691960, null, this, 0);
    return true;
  }
  
  public void onPause()
  {
    try
    {
      getActivity().unregisterReceiver(this.mReceiver);
    }
    catch (RuntimeException localRuntimeException)
    {
      try
      {
        for (;;)
        {
          getActivity().unregisterReceiver(this.mHighPowerChangeReceiver);
          if (this.mValidListener)
          {
            this.mSwitchBar.removeOnSwitchChangeListener(this);
            this.mValidListener = false;
          }
          super.onPause();
          return;
          localRuntimeException = localRuntimeException;
          if (Log.isLoggable("LocationSettings", 2)) {
            Log.v("LocationSettings", "Swallowing " + localRuntimeException);
          }
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.e("LocationSettings", "unregisterReceiver(mHighPowerChangeReceiver): " + localException.getMessage());
        }
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    createPreferenceHierarchy();
    if (!this.mValidListener)
    {
      this.mSwitchBar.addOnSwitchChangeListener(this);
      this.mValidListener = true;
    }
    getActivity().registerReceiver(this.mHighPowerChangeReceiver, this.mIntentFilter);
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      setLocationMode(-1);
      return;
    }
    setLocationMode(0);
  }
  
  private class PackageEntryClickedListener
    implements Preference.OnPreferenceClickListener
  {
    private String mPackage;
    private UserHandle mUserHandle;
    
    public PackageEntryClickedListener(String paramString, UserHandle paramUserHandle)
    {
      this.mPackage = paramString;
      this.mUserHandle = paramUserHandle;
    }
    
    public boolean onPreferenceClick(Preference paramPreference)
    {
      paramPreference = new Bundle();
      paramPreference.putString("package", this.mPackage);
      ((SettingsActivity)LocationSettings.this.getActivity()).startPreferencePanelAsUser(InstalledAppDetails.class.getName(), paramPreference, 2131692076, null, this.mUserHandle);
      return true;
    }
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mSummaryLoader;
    
    public SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "location_mode", 0);
        if (i != 0) {
          this.mSummaryLoader.setSummary(this, this.mContext.getString(2131693591, new Object[] { this.mContext.getString(LocationSettings.-wrap1(i)) }));
        }
      }
      else
      {
        return;
      }
      this.mSummaryLoader.setSummary(this, this.mContext.getString(2131693592));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\location\LocationSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */