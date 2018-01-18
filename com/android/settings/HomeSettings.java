package com.android.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Index;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HomeSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final String EXTRA_SUPPORT_MANAGED_PROFILES = "support_managed_profiles";
  public static final String HOME_PREFS = "home_prefs";
  public static final String HOME_PREFS_DO_SHOW = "do_show";
  public static final String HOME_SHOW_NOTICE = "show";
  static final int REQUESTING_UNINSTALL = 10;
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList1 = new ArrayList();
      PackageManager localPackageManager = paramAnonymousContext.getPackageManager();
      ArrayList localArrayList2 = new ArrayList();
      localPackageManager.getHomeActivities(localArrayList2);
      paramAnonymousBoolean = paramAnonymousContext.getSharedPreferences("home_prefs", 0).getBoolean("do_show", false);
      Resources localResources;
      Object localObject;
      if ((localArrayList2.size() > 1) || (paramAnonymousBoolean))
      {
        localResources = paramAnonymousContext.getResources();
        localObject = new SearchIndexableRaw(paramAnonymousContext);
        ((SearchIndexableRaw)localObject).title = localResources.getString(2131691546);
        ((SearchIndexableRaw)localObject).screenTitle = localResources.getString(2131691546);
        ((SearchIndexableRaw)localObject).keywords = localResources.getString(2131693125);
        localArrayList1.add(localObject);
        int i = 0;
        for (;;)
        {
          if (i >= localArrayList2.size()) {
            break label257;
          }
          localObject = ((ResolveInfo)localArrayList2.get(i)).activityInfo;
          try
          {
            CharSequence localCharSequence = ((ActivityInfo)localObject).loadLabel(localPackageManager);
            paramAnonymousBoolean = TextUtils.isEmpty(localCharSequence);
            if (!paramAnonymousBoolean) {
              break;
            }
          }
          catch (Exception localException)
          {
            for (;;)
            {
              Log.v("HomeSettings", "Problem dealing with Home " + ((ActivityInfo)localObject).name, localException);
              continue;
              localObject = new SearchIndexableRaw(paramAnonymousContext);
              ((SearchIndexableRaw)localObject).title = localException.toString();
              ((SearchIndexableRaw)localObject).screenTitle = localResources.getString(2131691546);
              localArrayList1.add(localObject);
            }
          }
          i += 1;
        }
      }
      label257:
      return localArrayList1;
    }
  };
  static final String TAG = "HomeSettings";
  private HomeAppPreference mCurrentHome = null;
  View.OnClickListener mDeleteClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i = ((Integer)paramAnonymousView.getTag()).intValue();
      HomeSettings.this.uninstallApp((HomeSettings.HomeAppPreference)HomeSettings.-get2(HomeSettings.this).get(i));
    }
  };
  View.OnClickListener mHomeClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i = ((Integer)paramAnonymousView.getTag()).intValue();
      paramAnonymousView = (HomeSettings.HomeAppPreference)HomeSettings.-get2(HomeSettings.this).get(i);
      if (!paramAnonymousView.isChecked) {
        HomeSettings.this.makeCurrentHome(paramAnonymousView);
      }
    }
  };
  private ComponentName[] mHomeComponentSet;
  private final IntentFilter mHomeFilter = new IntentFilter("android.intent.action.MAIN");
  private HomePackageReceiver mHomePackageReceiver = new HomePackageReceiver(null);
  private PackageManager mPm;
  private PreferenceGroup mPrefGroup;
  private ArrayList<HomeAppPreference> mPrefs;
  private boolean mShowNotice;
  
  public HomeSettings()
  {
    this.mHomeFilter.addCategory("android.intent.category.HOME");
    this.mHomeFilter.addCategory("android.intent.category.DEFAULT");
  }
  
  private void buildHomeActivitiesList()
  {
    ArrayList localArrayList = new ArrayList();
    ComponentName localComponentName1 = this.mPm.getHomeActivities(localArrayList);
    Context localContext = getPrefContext();
    this.mCurrentHome = null;
    this.mPrefGroup.removeAll();
    this.mPrefs = new ArrayList();
    this.mHomeComponentSet = new ComponentName[localArrayList.size()];
    int j = 0;
    boolean bool = getActivity().getIntent().getBooleanExtra("support_managed_profiles", false);
    if (!hasManagedProfile()) {}
    for (;;)
    {
      int i = 0;
      for (;;)
      {
        if (i < localArrayList.size())
        {
          Object localObject = (ResolveInfo)localArrayList.get(i);
          ActivityInfo localActivityInfo = ((ResolveInfo)localObject).activityInfo;
          ComponentName localComponentName2 = new ComponentName(localActivityInfo.packageName, localActivityInfo.name);
          this.mHomeComponentSet[i] = localComponentName2;
          try
          {
            Drawable localDrawable = localActivityInfo.loadIcon(this.mPm);
            CharSequence localCharSequence = localActivityInfo.loadLabel(this.mPm);
            if ((!bool) || (launcherHasManagedProfilesFeature((ResolveInfo)localObject))) {}
            for (localObject = new HomeAppPreference(localContext, localComponentName2, j, localDrawable, localCharSequence, this, localActivityInfo, true, null);; localObject = new HomeAppPreference(localContext, localComponentName2, j, localDrawable, localCharSequence, this, localActivityInfo, false, getResources().getString(2131691558)))
            {
              this.mPrefs.add(localObject);
              this.mPrefGroup.addPreference((Preference)localObject);
              if (!localComponentName2.equals(localComponentName1)) {
                break;
              }
              this.mCurrentHome = ((HomeAppPreference)localObject);
              break;
            }
            if (this.mCurrentHome == null) {
              break label350;
            }
          }
          catch (Exception localException)
          {
            Log.v("HomeSettings", "Problem dealing with activity " + localComponentName2, localException);
          }
        }
        if (this.mCurrentHome.isEnabled()) {
          getActivity().setResult(-1);
        }
        new Handler().post(new Runnable()
        {
          public void run()
          {
            HomeSettings.-get0(HomeSettings.this).setChecked(true);
          }
        });
        label350:
        return;
        j += 1;
        i += 1;
      }
      bool = true;
    }
  }
  
  private boolean hasManagedProfile()
  {
    Object localObject = getActivity();
    localObject = ((UserManager)getSystemService("user")).getProfiles(((Context)localObject).getUserId()).iterator();
    while (((Iterator)localObject).hasNext()) {
      if (((UserInfo)((Iterator)localObject).next()).isManagedProfile()) {
        return true;
      }
    }
    return false;
  }
  
  private boolean launcherHasManagedProfilesFeature(ResolveInfo paramResolveInfo)
  {
    try
    {
      boolean bool = versionNumberAtLeastL(getPackageManager().getApplicationInfo(paramResolveInfo.activityInfo.packageName, 0).targetSdkVersion);
      return bool;
    }
    catch (PackageManager.NameNotFoundException paramResolveInfo) {}
    return false;
  }
  
  private boolean versionNumberAtLeastL(int paramInt)
  {
    return paramInt >= 21;
  }
  
  protected int getMetricsCategory()
  {
    return 55;
  }
  
  void makeCurrentHome(HomeAppPreference paramHomeAppPreference)
  {
    if (this.mCurrentHome != null) {
      this.mCurrentHome.setChecked(false);
    }
    paramHomeAppPreference.setChecked(true);
    this.mCurrentHome = paramHomeAppPreference;
    this.mPm.replacePreferredActivity(this.mHomeFilter, 1048576, this.mHomeComponentSet, paramHomeAppPreference.activityName);
    getActivity().setResult(-1);
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    buildHomeActivitiesList();
    if ((paramInt1 > 10) && (this.mCurrentHome == null)) {
      paramInt1 = 0;
    }
    for (;;)
    {
      if (paramInt1 < this.mPrefs.size())
      {
        paramIntent = (HomeAppPreference)this.mPrefs.get(paramInt1);
        if (paramIntent.isSystem) {
          makeCurrentHome(paramIntent);
        }
      }
      else
      {
        return;
      }
      paramInt1 += 1;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230769);
    this.mPm = getPackageManager();
    this.mPrefGroup = ((PreferenceGroup)findPreference("home"));
    paramBundle = getArguments();
    if (paramBundle != null) {}
    for (boolean bool = paramBundle.getBoolean("show", false);; bool = false)
    {
      this.mShowNotice = bool;
      return;
    }
  }
  
  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mHomePackageReceiver);
  }
  
  public void onResume()
  {
    super.onResume();
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
    localIntentFilter.addDataScheme("package");
    getActivity().registerReceiver(this.mHomePackageReceiver, localIntentFilter);
    buildHomeActivitiesList();
  }
  
  void uninstallApp(HomeAppPreference paramHomeAppPreference)
  {
    int i = 0;
    Intent localIntent = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + paramHomeAppPreference.uninstallTarget));
    localIntent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
    if (paramHomeAppPreference.isChecked) {
      i = 1;
    }
    startActivityForResult(localIntent, i + 10);
  }
  
  private class HomeAppPreference
    extends Preference
  {
    ComponentName activityName;
    HomeSettings fragment;
    final ColorFilter grayscaleFilter;
    int index;
    boolean isChecked;
    boolean isSystem;
    String uninstallTarget;
    
    public HomeAppPreference(Context paramContext, ComponentName paramComponentName, int paramInt, Drawable paramDrawable, CharSequence paramCharSequence1, HomeSettings paramHomeSettings, ActivityInfo paramActivityInfo, boolean paramBoolean, CharSequence paramCharSequence2)
    {
      super();
      setLayoutResource(2130968907);
      setIcon(paramDrawable);
      setTitle(paramCharSequence1);
      setEnabled(paramBoolean);
      setSummary(paramCharSequence2);
      this.activityName = paramComponentName;
      this.fragment = paramHomeSettings;
      this.index = paramInt;
      this$1 = new ColorMatrix();
      HomeSettings.this.setSaturation(0.0F);
      HomeSettings.this.getArray()[18] = 0.5F;
      this.grayscaleFilter = new ColorMatrixColorFilter(HomeSettings.this);
      determineTargets(paramActivityInfo);
    }
    
    private void determineTargets(ActivityInfo paramActivityInfo)
    {
      boolean bool2 = true;
      Object localObject = paramActivityInfo.metaData;
      if (localObject != null)
      {
        localObject = ((Bundle)localObject).getString("android.app.home.alternate");
        if (localObject != null) {
          try
          {
            if (HomeSettings.-get1(HomeSettings.this).checkSignatures(paramActivityInfo.packageName, (String)localObject) >= 0)
            {
              localObject = HomeSettings.-get1(HomeSettings.this).getPackageInfo((String)localObject, 0);
              if ((((PackageInfo)localObject).applicationInfo.flags & 0x1) != 0) {}
              for (bool1 = true;; bool1 = false)
              {
                this.isSystem = bool1;
                this.uninstallTarget = ((PackageInfo)localObject).packageName;
                return;
              }
            }
            if ((paramActivityInfo.applicationInfo.flags & 0x1) == 0) {}
          }
          catch (Exception localException)
          {
            Log.w("HomeSettings", "Unable to compare/resolve alternate", localException);
          }
        }
      }
      for (boolean bool1 = bool2;; bool1 = false)
      {
        this.isSystem = bool1;
        this.uninstallTarget = paramActivityInfo.packageName;
        return;
      }
    }
    
    public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
    {
      super.onBindViewHolder(paramPreferenceViewHolder);
      ((RadioButton)paramPreferenceViewHolder.findViewById(2131362435)).setChecked(this.isChecked);
      Integer localInteger = new Integer(this.index);
      ImageView localImageView = (ImageView)paramPreferenceViewHolder.findViewById(2131362437);
      if (this.isSystem)
      {
        localImageView.setEnabled(false);
        localImageView.setColorFilter(this.grayscaleFilter);
      }
      for (;;)
      {
        paramPreferenceViewHolder = paramPreferenceViewHolder.findViewById(2131362434);
        paramPreferenceViewHolder.setTag(localInteger);
        paramPreferenceViewHolder.setOnClickListener(HomeSettings.this.mHomeClickListener);
        return;
        localImageView.setEnabled(true);
        localImageView.setOnClickListener(HomeSettings.this.mDeleteClickListener);
        localImageView.setTag(localInteger);
      }
    }
    
    void setChecked(boolean paramBoolean)
    {
      if (paramBoolean != this.isChecked)
      {
        this.isChecked = paramBoolean;
        notifyChanged();
      }
    }
  }
  
  private class HomePackageReceiver
    extends BroadcastReceiver
  {
    private HomePackageReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      HomeSettings.-wrap0(HomeSettings.this);
      Index.getInstance(paramContext).updateFromClassNameResource(HomeSettings.class.getName(), true, true);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\HomeSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */