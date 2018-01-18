package com.android.settings.applications;

import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.SettingsActivity;
import java.util.Comparator;
import java.util.List;

public class AppOpsCategory
  extends ListFragment
  implements LoaderManager.LoaderCallbacks<List<AppOpsState.AppOpEntry>>
{
  private static final int RESULT_APP_DETAILS = 1;
  AppListAdapter mAdapter;
  String mCurrentPkgName;
  AppOpsState mState;
  boolean mUserControlled;
  
  public AppOpsCategory() {}
  
  public AppOpsCategory(AppOpsState.OpsTemplate paramOpsTemplate)
  {
    this(paramOpsTemplate, false);
  }
  
  public AppOpsCategory(AppOpsState.OpsTemplate paramOpsTemplate, boolean paramBoolean)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("template", paramOpsTemplate);
    localBundle.putBoolean("userControlled", paramBoolean);
    setArguments(localBundle);
  }
  
  private void startApplicationDetailsActivity()
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("package", this.mCurrentPkgName);
    ((SettingsActivity)getActivity()).startPreferencePanel(AppOpsDetails.class.getName(), localBundle, 2131692180, null, this, 1);
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    setEmptyText("No applications");
    setHasOptionsMenu(true);
    this.mAdapter = new AppListAdapter(getActivity(), this.mState, this.mUserControlled);
    setListAdapter(this.mAdapter);
    setListShown(false);
    getLoaderManager().initLoader(0, null, this);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mState = new AppOpsState(getActivity());
    this.mUserControlled = getArguments().getBoolean("userControlled");
  }
  
  public Loader<List<AppOpsState.AppOpEntry>> onCreateLoader(int paramInt, Bundle paramBundle)
  {
    Bundle localBundle = getArguments();
    paramBundle = null;
    if (localBundle != null) {
      paramBundle = (AppOpsState.OpsTemplate)localBundle.getParcelable("template");
    }
    return new AppListLoader(getActivity(), this.mState, paramBundle, this.mUserControlled);
  }
  
  public void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    paramListView = this.mAdapter.getItem(paramInt);
    boolean bool;
    if (paramListView != null)
    {
      if (!this.mUserControlled) {
        break label111;
      }
      paramView = (Switch)paramView.findViewById(2131361961);
      if (!paramView.isChecked()) {
        break label100;
      }
      bool = false;
      paramView.setChecked(bool);
      paramView = paramListView.getOpEntry(0);
      if (!bool) {
        break label106;
      }
    }
    label100:
    label106:
    for (paramInt = 0;; paramInt = 1)
    {
      this.mState.getAppOpsManager().setMode(paramView.getOp(), paramListView.getAppEntry().getApplicationInfo().uid, paramListView.getAppEntry().getApplicationInfo().packageName, paramInt);
      paramListView.overridePrimaryOpMode(paramInt);
      return;
      bool = true;
      break;
    }
    label111:
    this.mCurrentPkgName = paramListView.getAppEntry().getApplicationInfo().packageName;
    startApplicationDetailsActivity();
  }
  
  public void onLoadFinished(Loader<List<AppOpsState.AppOpEntry>> paramLoader, List<AppOpsState.AppOpEntry> paramList)
  {
    this.mAdapter.setData(paramList);
    if (isResumed())
    {
      setListShown(true);
      return;
    }
    setListShownNoAnimation(true);
  }
  
  public void onLoaderReset(Loader<List<AppOpsState.AppOpEntry>> paramLoader)
  {
    this.mAdapter.setData(null);
  }
  
  public static class AppListAdapter
    extends BaseAdapter
  {
    private final LayoutInflater mInflater;
    List<AppOpsState.AppOpEntry> mList;
    private final Resources mResources;
    private final AppOpsState mState;
    private final boolean mUserControlled;
    
    public AppListAdapter(Context paramContext, AppOpsState paramAppOpsState, boolean paramBoolean)
    {
      this.mResources = paramContext.getResources();
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
      this.mState = paramAppOpsState;
      this.mUserControlled = paramBoolean;
    }
    
    public int getCount()
    {
      if (this.mList != null) {
        return this.mList.size();
      }
      return 0;
    }
    
    public AppOpsState.AppOpEntry getItem(int paramInt)
    {
      return (AppOpsState.AppOpEntry)this.mList.get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool = false;
      if (paramView == null) {
        paramView = this.mInflater.inflate(2130968617, paramViewGroup, false);
      }
      for (;;)
      {
        paramViewGroup = getItem(paramInt);
        ((ImageView)paramView.findViewById(2131361951)).setImageDrawable(paramViewGroup.getAppEntry().getIcon());
        ((TextView)paramView.findViewById(2131361952)).setText(paramViewGroup.getAppEntry().getLabel());
        if (!this.mUserControlled) {
          break;
        }
        ((TextView)paramView.findViewById(2131361958)).setText(paramViewGroup.getTimeText(this.mResources, false));
        paramView.findViewById(2131361959).setVisibility(8);
        Switch localSwitch = (Switch)paramView.findViewById(2131361961);
        if (paramViewGroup.getPrimaryOpMode() == 0) {
          bool = true;
        }
        localSwitch.setChecked(bool);
        return paramView;
      }
      ((TextView)paramView.findViewById(2131361958)).setText(paramViewGroup.getSummaryText(this.mState));
      ((TextView)paramView.findViewById(2131361959)).setText(paramViewGroup.getTimeText(this.mResources, false));
      paramView.findViewById(2131361961).setVisibility(8);
      return paramView;
    }
    
    public void setData(List<AppOpsState.AppOpEntry> paramList)
    {
      this.mList = paramList;
      notifyDataSetChanged();
    }
  }
  
  public static class AppListLoader
    extends AsyncTaskLoader<List<AppOpsState.AppOpEntry>>
  {
    List<AppOpsState.AppOpEntry> mApps;
    final AppOpsCategory.InterestingConfigChanges mLastConfig = new AppOpsCategory.InterestingConfigChanges();
    AppOpsCategory.PackageIntentReceiver mPackageObserver;
    final AppOpsState mState;
    final AppOpsState.OpsTemplate mTemplate;
    final boolean mUserControlled;
    
    public AppListLoader(Context paramContext, AppOpsState paramAppOpsState, AppOpsState.OpsTemplate paramOpsTemplate, boolean paramBoolean)
    {
      super();
      this.mState = paramAppOpsState;
      this.mTemplate = paramOpsTemplate;
      this.mUserControlled = paramBoolean;
    }
    
    public void deliverResult(List<AppOpsState.AppOpEntry> paramList)
    {
      if ((isReset()) && (paramList != null)) {
        onReleaseResources(paramList);
      }
      this.mApps = paramList;
      if (isStarted()) {
        super.deliverResult(paramList);
      }
      if (paramList != null) {
        onReleaseResources(paramList);
      }
    }
    
    public List<AppOpsState.AppOpEntry> loadInBackground()
    {
      AppOpsState localAppOpsState = this.mState;
      AppOpsState.OpsTemplate localOpsTemplate = this.mTemplate;
      if (this.mUserControlled) {}
      for (Comparator localComparator = AppOpsState.LABEL_COMPARATOR;; localComparator = AppOpsState.RECENCY_COMPARATOR) {
        return localAppOpsState.buildState(localOpsTemplate, 0, null, localComparator);
      }
    }
    
    public void onCanceled(List<AppOpsState.AppOpEntry> paramList)
    {
      super.onCanceled(paramList);
      onReleaseResources(paramList);
    }
    
    protected void onReleaseResources(List<AppOpsState.AppOpEntry> paramList) {}
    
    protected void onReset()
    {
      super.onReset();
      onStopLoading();
      if (this.mApps != null)
      {
        onReleaseResources(this.mApps);
        this.mApps = null;
      }
      if (this.mPackageObserver != null)
      {
        getContext().unregisterReceiver(this.mPackageObserver);
        this.mPackageObserver = null;
      }
    }
    
    protected void onStartLoading()
    {
      onContentChanged();
      if (this.mApps != null) {
        deliverResult(this.mApps);
      }
      if (this.mPackageObserver == null) {
        this.mPackageObserver = new AppOpsCategory.PackageIntentReceiver(this);
      }
      boolean bool = this.mLastConfig.applyNewConfig(getContext().getResources());
      if ((takeContentChanged()) || (this.mApps == null)) {}
      for (;;)
      {
        forceLoad();
        do
        {
          return;
        } while (!bool);
      }
    }
    
    protected void onStopLoading()
    {
      cancelLoad();
    }
  }
  
  public static class InterestingConfigChanges
  {
    final Configuration mLastConfiguration = new Configuration();
    int mLastDensity;
    
    boolean applyNewConfig(Resources paramResources)
    {
      int j = this.mLastConfiguration.updateFrom(paramResources.getConfiguration());
      if (this.mLastDensity != paramResources.getDisplayMetrics().densityDpi) {}
      for (int i = 1; (i != 0) || ((j & 0x304) != 0); i = 0)
      {
        this.mLastDensity = paramResources.getDisplayMetrics().densityDpi;
        return true;
      }
      return false;
    }
  }
  
  public static class PackageIntentReceiver
    extends BroadcastReceiver
  {
    final AppOpsCategory.AppListLoader mLoader;
    
    public PackageIntentReceiver(AppOpsCategory.AppListLoader paramAppListLoader)
    {
      this.mLoader = paramAppListLoader;
      paramAppListLoader = new IntentFilter("android.intent.action.PACKAGE_ADDED");
      paramAppListLoader.addAction("android.intent.action.PACKAGE_REMOVED");
      paramAppListLoader.addAction("android.intent.action.PACKAGE_CHANGED");
      paramAppListLoader.addDataScheme("package");
      this.mLoader.getContext().registerReceiver(this, paramAppListLoader);
      paramAppListLoader = new IntentFilter();
      paramAppListLoader.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
      paramAppListLoader.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
      this.mLoader.getContext().registerReceiver(this, paramAppListLoader);
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      this.mLoader.onContentChanged();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppOpsCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */