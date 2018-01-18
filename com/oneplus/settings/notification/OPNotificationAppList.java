package com.oneplus.settings.notification;

import android.app.Activity;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.notification.NotificationBackend;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class OPNotificationAppList
  extends SettingsPreferenceFragment
{
  private static final Intent APP_NOTIFICATION_PREFS_CATEGORY_INTENT = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.NOTIFICATION_PREFERENCES");
  private static final boolean DEBUG = true;
  private static final String EXTRA_HAS_SETTINGS_INTENT = "has_settings_intent";
  private static final String EXTRA_SETTINGS_INTENT = "settings_intent";
  private static final String KEY_ALLOW_LED_APPS = "op_notification_allow_led";
  private static final String KEY_NOT_ALLOW_LED_APPS = "op_notification_not_allow_led";
  private static long PROGRESS_MIN_SHOW_TIME = 500L;
  private static final String TAG = "OPNotificationAppList";
  private static long WILL_SHOW_PROGRESS_TIME = 300L;
  private static final Comparator<AppRow> mRowComparator = new Comparator()
  {
    private final Collator sCollator = Collator.getInstance();
    
    public int compare(OPNotificationAppList.AppRow paramAnonymousAppRow1, OPNotificationAppList.AppRow paramAnonymousAppRow2)
    {
      return this.sCollator.compare(paramAnonymousAppRow1.label, paramAnonymousAppRow2.label);
    }
  };
  private View emptyView;
  private PreferenceCategory mAllowLEDApps;
  private Backend mBackend = new Backend();
  private final Runnable mCollectAppsRunnable = new Runnable()
  {
    public void run()
    {
      long l1;
      synchronized (OPNotificationAppList.-get8(OPNotificationAppList.this))
      {
        l1 = SystemClock.uptimeMillis();
        Log.d("OPNotificationAppList", "Collecting apps...");
        OPNotificationAppList.-get8(OPNotificationAppList.this).clear();
        OPNotificationAppList.-get9(OPNotificationAppList.this).clear();
        localObject3 = new ArrayList();
        Object localObject1 = OPNotificationAppList.-get3(OPNotificationAppList.this).getActivityList(null, UserHandle.OWNER);
        Log.d("OPNotificationAppList", "  launchable activities:");
        localObject1 = ((Iterable)localObject1).iterator();
        if (((Iterator)localObject1).hasNext())
        {
          localObject4 = (LauncherActivityInfo)((Iterator)localObject1).next();
          Log.d("OPNotificationAppList", "oneplus- " + ((LauncherActivityInfo)localObject4).getComponentName().toString());
          ((List)localObject3).add(((LauncherActivityInfo)localObject4).getApplicationInfo());
        }
      }
      List localList = OPNotificationAppList.queryNotificationConfigActivities(OPNotificationAppList.-get5(OPNotificationAppList.this));
      Log.d("OPNotificationAppList", "  config activities:");
      Object localObject4 = localList.iterator();
      Object localObject5;
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (ResolveInfo)((Iterator)localObject4).next();
        Log.d("OPNotificationAppList", "oneplus-" + ((ResolveInfo)localObject5).activityInfo.packageName + "/" + ((ResolveInfo)localObject5).activityInfo.name);
        ((List)localObject3).add(((ResolveInfo)localObject5).activityInfo.applicationInfo);
      }
      Object localObject3 = ((Iterable)localObject3).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject5 = (ApplicationInfo)((Iterator)localObject3).next();
        localObject4 = ((ApplicationInfo)localObject5).packageName;
        if (!OPNotificationAppList.-get8(OPNotificationAppList.this).containsKey(localObject4))
        {
          localObject5 = OPNotificationAppList.loadAppRow(OPNotificationAppList.-get5(OPNotificationAppList.this), (ApplicationInfo)localObject5, OPNotificationAppList.-get1(OPNotificationAppList.this));
          OPNotificationAppList.-get8(OPNotificationAppList.this).put(localObject4, localObject5);
        }
      }
      OPNotificationAppList.applyConfigActivities(OPNotificationAppList.-get5(OPNotificationAppList.this), OPNotificationAppList.-get8(OPNotificationAppList.this), localList);
      OPNotificationAppList.-get9(OPNotificationAppList.this).addAll(OPNotificationAppList.-get8(OPNotificationAppList.this).values());
      Collections.sort(OPNotificationAppList.-get9(OPNotificationAppList.this), OPNotificationAppList.-get7());
      OPNotificationAppList.-get2(OPNotificationAppList.this).post(OPNotificationAppList.-get6(OPNotificationAppList.this));
      long l2 = SystemClock.uptimeMillis();
      Log.d("OPNotificationAppList", "oneplus-Collected " + OPNotificationAppList.-get8(OPNotificationAppList.this).size() + " apps in " + (l2 - l1) + "ms");
    }
  };
  private Context mContext;
  private final Handler mHandler = new Handler(Looper.getMainLooper());
  private Handler mHandler1 = new Handler(Looper.getMainLooper());
  private boolean mHasShowProgress;
  private LauncherApps mLauncherApps;
  private Parcelable mListViewState;
  private PreferenceCategory mNotAllowLEDApps;
  private NotificationBackend mNotificationBackend = new NotificationBackend();
  private PackageManager mPM;
  private final Runnable mRefreshAppsListRunnable = new Runnable()
  {
    public void run()
    {
      OPNotificationAppList.-wrap1(OPNotificationAppList.this, OPNotificationAppList.-get9(OPNotificationAppList.this));
    }
  };
  private PreferenceScreen mRoot;
  private final ArrayMap<String, AppRow> mRows = new ArrayMap();
  private boolean mShowAllowLEDApps = false;
  private boolean mShowNotAllowLEDApps = false;
  private Runnable mShowPromptRunnable;
  private long mShowPromptTime;
  private final ArrayList<AppRow> mSortedRows = new ArrayList();
  private Signature[] mSystemSignature;
  private UserManager mUM;
  
  public static void applyConfigActivities(PackageManager paramPackageManager, ArrayMap<String, AppRow> paramArrayMap, List<ResolveInfo> paramList)
  {
    Object localObject = new StringBuilder().append("Found ").append(paramList.size()).append(" preference activities");
    if (paramList.size() == 0)
    {
      paramPackageManager = " ;_;";
      Log.d("OPNotificationAppList", paramPackageManager);
      paramPackageManager = paramList.iterator();
    }
    for (;;)
    {
      if (!paramPackageManager.hasNext()) {
        return;
      }
      paramList = ((ResolveInfo)paramPackageManager.next()).activityInfo;
      localObject = (AppRow)paramArrayMap.get(paramList.applicationInfo.packageName);
      if (localObject == null)
      {
        Log.v("OPNotificationAppList", "Ignoring notification preference activity (" + paramList.name + ") for unknown package " + paramList.packageName);
        continue;
        paramPackageManager = "";
        break;
      }
      if (((AppRow)localObject).settingsIntent != null) {
        Log.v("OPNotificationAppList", "Ignoring duplicate notification preference activity (" + paramList.name + ") for package " + paramList.packageName);
      } else {
        ((AppRow)localObject).settingsIntent = new Intent(APP_NOTIFICATION_PREFS_CATEGORY_INTENT).setClassName(paramList.packageName, paramList.name);
      }
    }
  }
  
  public static void collectConfigActivities(PackageManager paramPackageManager, ArrayMap<String, AppRow> paramArrayMap)
  {
    applyConfigActivities(paramPackageManager, paramArrayMap, queryNotificationConfigActivities(paramPackageManager));
  }
  
  public static AppRow loadAppRow(PackageManager paramPackageManager, ApplicationInfo paramApplicationInfo, Backend paramBackend)
  {
    AppRow localAppRow = new AppRow();
    localAppRow.pkg = paramApplicationInfo.packageName;
    localAppRow.uid = paramApplicationInfo.uid;
    try
    {
      localAppRow.label = paramApplicationInfo.loadLabel(paramPackageManager);
      localAppRow.icon = paramApplicationInfo.loadIcon(paramPackageManager);
      localAppRow.banned = paramBackend.getNotificationsBanned(localAppRow.pkg, localAppRow.uid);
      localAppRow.priority = paramBackend.getHighPriority(localAppRow.pkg, localAppRow.uid);
      localAppRow.sensitive = paramBackend.getSensitive(localAppRow.pkg, localAppRow.uid);
      localAppRow.ledDisabled = paramBackend.getLedDisabled(localAppRow.pkg);
      return localAppRow;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e("OPNotificationAppList", "Error loading application label for " + localAppRow.pkg, localThrowable);
        localAppRow.label = localAppRow.pkg;
      }
    }
  }
  
  private void loadAppsList()
  {
    AsyncTask.execute(this.mCollectAppsRunnable);
  }
  
  public static List<ResolveInfo> queryNotificationConfigActivities(PackageManager paramPackageManager)
  {
    Log.d("OPNotificationAppList", "APP_NOTIFICATION_PREFS_CATEGORY_INTENT is " + APP_NOTIFICATION_PREFS_CATEGORY_INTENT);
    return paramPackageManager.queryIntentActivities(APP_NOTIFICATION_PREFS_CATEGORY_INTENT, 0);
  }
  
  private void refreshDisplayedItems(ArrayList<AppRow> paramArrayList)
  {
    resetUI();
    final int i = 0;
    while (i < paramArrayList.size())
    {
      final AppRow localAppRow = (AppRow)paramArrayList.get(i);
      SwitchPreference localSwitchPreference = new SwitchPreference(this.mContext);
      if (("com.oneplus.deskclock".equals(localAppRow.pkg)) || ("com.oneplus.deskclock".equals(localAppRow.pkg)) || ("com.google.android.calendar".equals(localAppRow.pkg)) || ("com.oneplus.deskclock".equals(localAppRow.pkg)))
      {
        i += 1;
      }
      else
      {
        localSwitchPreference.setKey(localAppRow.pkg);
        localSwitchPreference.setTitle(localAppRow.label);
        localSwitchPreference.setIcon(localAppRow.icon);
        localSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
          public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
          {
            boolean bool2 = ((Boolean)paramAnonymousObject).booleanValue();
            paramAnonymousPreference = localAppRow;
            if (bool2) {}
            for (boolean bool1 = false;; bool1 = true)
            {
              paramAnonymousPreference.ledDisabled = bool1;
              OPNotificationAppList.-get4(OPNotificationAppList.this).setLedEnabled(localAppRow.pkg, bool2);
              OPNotificationAppList.-get9(OPNotificationAppList.this).set(i, localAppRow);
              OPNotificationAppList.-get2(OPNotificationAppList.this).post(OPNotificationAppList.-get6(OPNotificationAppList.this));
              return true;
            }
          }
        });
        if (localAppRow.ledDisabled)
        {
          this.mShowNotAllowLEDApps = true;
          this.mNotAllowLEDApps.addPreference(localSwitchPreference);
          label169:
          if (!localAppRow.ledDisabled) {
            break label206;
          }
        }
        label206:
        for (boolean bool = false;; bool = true)
        {
          localSwitchPreference.setChecked(bool);
          break;
          this.mShowAllowLEDApps = true;
          this.mAllowLEDApps.addPreference(localSwitchPreference);
          break label169;
        }
      }
    }
    removeCatagoryIfNoneApp();
    Log.d("OPNotificationAppList", "Refreshed " + this.mSortedRows.size() + " displayed items");
  }
  
  private void removeCatagoryIfNoneApp()
  {
    if (this.mShowNotAllowLEDApps) {
      this.mRoot.addPreference(this.mNotAllowLEDApps);
    }
    while (this.mShowAllowLEDApps)
    {
      this.mRoot.addPreference(this.mAllowLEDApps);
      return;
      this.mRoot.removePreference(this.mNotAllowLEDApps);
    }
    this.mRoot.removePreference(this.mAllowLEDApps);
  }
  
  private void resetUI()
  {
    this.mAllowLEDApps.removeAll();
    this.mNotAllowLEDApps.removeAll();
    this.mRoot.removePreference(this.mNotAllowLEDApps);
    this.mRoot.removePreference(this.mAllowLEDApps);
    this.mShowNotAllowLEDApps = false;
    this.mShowAllowLEDApps = false;
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230785);
    this.mContext = getActivity();
    this.mUM = UserManager.get(this.mContext);
    this.mPM = this.mContext.getPackageManager();
    this.mLauncherApps = ((LauncherApps)this.mContext.getSystemService("launcherapps"));
    this.mRoot = getPreferenceScreen();
    this.mAllowLEDApps = ((PreferenceCategory)findPreference("op_notification_allow_led"));
    this.mNotAllowLEDApps = ((PreferenceCategory)findPreference("op_notification_not_allow_led"));
  }
  
  public void onPause()
  {
    super.onPause();
  }
  
  protected final void onPostExecute()
  {
    if (this.mHasShowProgress)
    {
      long l1 = System.currentTimeMillis();
      long l2 = this.mShowPromptTime;
      l1 = PROGRESS_MIN_SHOW_TIME - (l1 - l2);
      if (l1 > 0L)
      {
        this.mHandler1.postDelayed(new Runnable()
        {
          public void run()
          {
            if (OPNotificationAppList.-get0(OPNotificationAppList.this) != null) {
              OPNotificationAppList.-get0(OPNotificationAppList.this).setVisibility(8);
            }
            OPNotificationAppList.-wrap0(OPNotificationAppList.this);
          }
        }, l1);
        return;
      }
      if (this.emptyView != null) {
        this.emptyView.setVisibility(8);
      }
      loadAppsList();
      return;
    }
    if (this.emptyView != null) {
      this.emptyView.setVisibility(8);
    }
    loadAppsList();
  }
  
  protected final void onPreExecute()
  {
    this.mHasShowProgress = false;
    this.mShowPromptRunnable = new Runnable()
    {
      public void run()
      {
        OPNotificationAppList.-set0(OPNotificationAppList.this, true);
        if (OPNotificationAppList.-get0(OPNotificationAppList.this) != null) {
          OPNotificationAppList.this.setEmptyView(OPNotificationAppList.-get0(OPNotificationAppList.this));
        }
        OPNotificationAppList.-set1(OPNotificationAppList.this, System.currentTimeMillis());
      }
    };
    this.mHandler1.postDelayed(this.mShowPromptRunnable, WILL_SHOW_PROGRESS_TIME);
  }
  
  public void onResume()
  {
    super.onResume();
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    paramView = (ViewGroup)getListView().getParent();
    this.emptyView = getActivity().getLayoutInflater().inflate(2130968739, paramView, false);
    this.emptyView.setVisibility(0);
    paramView.addView(this.emptyView);
    onPreExecute();
    resetUI();
    onPostExecute();
  }
  
  public static class AppRow
  {
    public boolean banned;
    public boolean first;
    public Drawable icon;
    public CharSequence label;
    public boolean ledDisabled;
    public String pkg;
    public boolean priority;
    public boolean sensitive;
    public Intent settingsIntent;
    public int uid;
  }
  
  public static class Backend
  {
    static INotificationManager sINM = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    
    public boolean getHighPriority(String paramString, int paramInt)
    {
      return true;
    }
    
    public boolean getLedDisabled(String paramString)
    {
      try
      {
        boolean bool = sINM.isNotificationLedEnabled(paramString);
        return !bool;
      }
      catch (Exception paramString)
      {
        Log.w("OPNotificationAppList", "Error calling NoMan", paramString);
      }
      return false;
    }
    
    public boolean getNotificationsBanned(String paramString, int paramInt)
    {
      try
      {
        boolean bool = sINM.areNotificationsEnabledForPackage(paramString, paramInt);
        return !bool;
      }
      catch (Exception paramString)
      {
        Log.w("OPNotificationAppList", "Error calling NoMan", paramString);
      }
      return false;
    }
    
    public boolean getSensitive(String paramString, int paramInt)
    {
      return true;
    }
    
    public boolean setHighPriority(String paramString, int paramInt, boolean paramBoolean)
    {
      return true;
    }
    
    public boolean setLedDisabled(String paramString, boolean paramBoolean)
    {
      try
      {
        sINM.setNotificationLedStatus(paramString, paramBoolean);
        return true;
      }
      catch (Exception paramString)
      {
        Log.w("OPNotificationAppList", "Error calling NoMan", paramString);
      }
      return false;
    }
    
    public boolean setNotificationsBanned(String paramString, int paramInt, boolean paramBoolean)
    {
      try
      {
        INotificationManager localINotificationManager = sINM;
        if (paramBoolean) {}
        for (paramBoolean = false;; paramBoolean = true)
        {
          localINotificationManager.setNotificationsEnabledForPackage(paramString, paramInt, paramBoolean);
          return true;
        }
        return false;
      }
      catch (Exception paramString)
      {
        Log.w("OPNotificationAppList", "Error calling NoMan", paramString);
      }
    }
    
    public boolean setSensitive(String paramString, int paramInt, boolean paramBoolean)
    {
      return true;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\notification\OPNotificationAppList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */