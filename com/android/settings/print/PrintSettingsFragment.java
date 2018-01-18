package com.android.settings.print;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ActivityNotFoundException;
import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintJob;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.print.PrintManager.PrintJobStateChangeListener;
import android.print.PrintServicesLoader;
import android.printservice.PrintServiceInfo;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.DialogCreatable;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settings.utils.ProfileSettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.List;

public class PrintSettingsFragment
  extends ProfileSettingsPreferenceFragment
  implements DialogCreatable, Indexable, View.OnClickListener
{
  static final String EXTRA_CHECKED = "EXTRA_CHECKED";
  static final String EXTRA_PRINT_JOB_ID = "EXTRA_PRINT_JOB_ID";
  private static final String EXTRA_PRINT_SERVICE_COMPONENT_NAME = "EXTRA_PRINT_SERVICE_COMPONENT_NAME";
  static final String EXTRA_SERVICE_COMPONENT_NAME = "EXTRA_SERVICE_COMPONENT_NAME";
  static final String EXTRA_TITLE = "EXTRA_TITLE";
  private static final int LOADER_ID_PRINT_JOBS_LOADER = 1;
  private static final int LOADER_ID_PRINT_SERVICES = 2;
  private static final int ORDER_LAST = 2147483646;
  private static final String PRINT_JOBS_CATEGORY = "print_jobs_category";
  private static final String PRINT_SERVICES_CATEGORY = "print_services_category";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      PackageManager localPackageManager = paramAnonymousContext.getPackageManager();
      Object localObject1 = (PrintManager)paramAnonymousContext.getSystemService("print");
      String str = paramAnonymousContext.getResources().getString(2131692401);
      Object localObject2 = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject2).title = str;
      ((SearchIndexableRaw)localObject2).screenTitle = str;
      localArrayList.add(localObject2);
      localObject1 = ((PrintManager)localObject1).getPrintServices(3);
      if (localObject1 != null)
      {
        int j = ((List)localObject1).size();
        int i = 0;
        while (i < j)
        {
          localObject2 = (PrintServiceInfo)((List)localObject1).get(i);
          ComponentName localComponentName = new ComponentName(((PrintServiceInfo)localObject2).getResolveInfo().serviceInfo.packageName, ((PrintServiceInfo)localObject2).getResolveInfo().serviceInfo.name);
          SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
          localSearchIndexableRaw.key = localComponentName.flattenToString();
          localSearchIndexableRaw.title = ((PrintServiceInfo)localObject2).getResolveInfo().loadLabel(localPackageManager).toString();
          localSearchIndexableRaw.summaryOn = paramAnonymousContext.getString(2131692407);
          localSearchIndexableRaw.summaryOff = paramAnonymousContext.getString(2131692408);
          localSearchIndexableRaw.screenTitle = str;
          localArrayList.add(localSearchIndexableRaw);
          i += 1;
        }
      }
      return localArrayList;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230829;
      localArrayList.add(paramAnonymousContext);
      return localArrayList;
    }
  };
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new PrintSettingsFragment.PrintSummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
    }
  };
  public static final String TAG = "PrintSettingsFragment";
  private PreferenceCategory mActivePrintJobsCategory;
  private Button mAddNewServiceButton;
  private PrintJobsController mPrintJobsController;
  private PreferenceCategory mPrintServicesCategory;
  private PrintServicesController mPrintServicesController;
  
  private Intent createAddNewServiceIntentOrNull()
  {
    String str = Settings.Secure.getString(getContentResolver(), "print_service_search_uri");
    if (TextUtils.isEmpty(str)) {
      return null;
    }
    return new Intent("android.intent.action.VIEW", Uri.parse(str));
  }
  
  private Preference newAddServicePreferenceOrNull()
  {
    Intent localIntent = createAddNewServiceIntentOrNull();
    if (localIntent == null) {
      return null;
    }
    Preference localPreference = new Preference(getPrefContext());
    localPreference.setTitle(2131692409);
    localPreference.setIcon(2130837998);
    localPreference.setOrder(2147483646);
    localPreference.setIntent(localIntent);
    localPreference.setPersistent(false);
    return localPreference;
  }
  
  private static boolean shouldShowToUser(PrintJobInfo paramPrintJobInfo)
  {
    switch (paramPrintJobInfo.getState())
    {
    case 5: 
    default: 
      return false;
    }
    return true;
  }
  
  private void startSubSettingsIfNeeded()
  {
    if (getArguments() == null) {
      return;
    }
    Object localObject = getArguments().getString("EXTRA_PRINT_SERVICE_COMPONENT_NAME");
    if (localObject != null)
    {
      getArguments().remove("EXTRA_PRINT_SERVICE_COMPONENT_NAME");
      localObject = findPreference((CharSequence)localObject);
      if (localObject != null) {
        ((Preference)localObject).performClick();
      }
    }
  }
  
  protected int getHelpResource()
  {
    return 2131693009;
  }
  
  protected String getIntentActionString()
  {
    return "android.settings.ACTION_PRINT_SETTINGS";
  }
  
  protected int getMetricsCategory()
  {
    return 80;
  }
  
  public void onClick(View paramView)
  {
    if (this.mAddNewServiceButton == paramView)
    {
      paramView = createAddNewServiceIntentOrNull();
      if (paramView == null) {}
    }
    try
    {
      startActivity(paramView);
      return;
    }
    catch (ActivityNotFoundException paramView)
    {
      Log.w("PrintSettingsFragment", "Unable to start activity", paramView);
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    addPreferencesFromResource(2131230829);
    this.mActivePrintJobsCategory = ((PreferenceCategory)findPreference("print_jobs_category"));
    this.mPrintServicesCategory = ((PreferenceCategory)findPreference("print_services_category"));
    getPreferenceScreen().removePreference(this.mActivePrintJobsCategory);
    this.mPrintJobsController = new PrintJobsController(null);
    getLoaderManager().initLoader(1, null, this.mPrintJobsController);
    this.mPrintServicesController = new PrintServicesController(null);
    getLoaderManager().initLoader(2, null, this.mPrintServicesController);
    return paramLayoutInflater;
  }
  
  public void onStart()
  {
    super.onStart();
    setHasOptionsMenu(true);
    startSubSettingsIfNeeded();
  }
  
  public void onStop()
  {
    super.onStop();
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    paramView = (ViewGroup)getListView().getParent();
    paramBundle = getActivity().getLayoutInflater().inflate(2130968695, paramView, false);
    ((TextView)paramBundle.findViewById(2131361987)).setText(2131692403);
    if (createAddNewServiceIntentOrNull() != null)
    {
      this.mAddNewServiceButton = ((Button)paramBundle.findViewById(2131362133));
      this.mAddNewServiceButton.setOnClickListener(this);
      this.mAddNewServiceButton.setVisibility(0);
    }
    paramView.addView(paramBundle);
    setEmptyView(paramBundle);
  }
  
  private final class PrintJobsController
    implements LoaderManager.LoaderCallbacks<List<PrintJobInfo>>
  {
    private PrintJobsController() {}
    
    public Loader<List<PrintJobInfo>> onCreateLoader(int paramInt, Bundle paramBundle)
    {
      if (paramInt == 1) {
        return new PrintSettingsFragment.PrintJobsLoader(PrintSettingsFragment.this.getContext());
      }
      return null;
    }
    
    public void onLoadFinished(Loader<List<PrintJobInfo>> paramLoader, List<PrintJobInfo> paramList)
    {
      if ((paramList == null) || (paramList.isEmpty())) {
        PrintSettingsFragment.this.getPreferenceScreen().removePreference(PrintSettingsFragment.-get0(PrintSettingsFragment.this));
      }
      int j;
      int i;
      do
      {
        return;
        if (PrintSettingsFragment.this.getPreferenceScreen().findPreference("print_jobs_category") == null) {
          PrintSettingsFragment.this.getPreferenceScreen().addPreference(PrintSettingsFragment.-get0(PrintSettingsFragment.this));
        }
        PrintSettingsFragment.-get0(PrintSettingsFragment.this).removeAll();
        j = paramList.size();
        i = 0;
      } while (i >= j);
      paramLoader = (PrintJobInfo)paramList.get(i);
      PreferenceScreen localPreferenceScreen = PrintSettingsFragment.this.getPreferenceManager().createPreferenceScreen(PrintSettingsFragment.this.getActivity());
      localPreferenceScreen.setPersistent(false);
      localPreferenceScreen.setFragment(PrintJobSettingsFragment.class.getName());
      localPreferenceScreen.setKey(paramLoader.getId().flattenToString());
      switch (paramLoader.getState())
      {
      case 5: 
      default: 
        label188:
        localPreferenceScreen.setSummary(PrintSettingsFragment.this.getString(2131692418, new Object[] { paramLoader.getPrinterName(), DateUtils.formatSameDayTime(paramLoader.getCreationTime(), paramLoader.getCreationTime(), 3, 3) }));
        switch (paramLoader.getState())
        {
        }
        break;
      }
      for (;;)
      {
        localPreferenceScreen.getExtras().putString("EXTRA_PRINT_JOB_ID", paramLoader.getId().flattenToString());
        PrintSettingsFragment.-get0(PrintSettingsFragment.this).addPreference(localPreferenceScreen);
        i += 1;
        break;
        if (!paramLoader.isCancelling())
        {
          localPreferenceScreen.setTitle(PrintSettingsFragment.this.getString(2131692419, new Object[] { paramLoader.getLabel() }));
          break label188;
        }
        localPreferenceScreen.setTitle(PrintSettingsFragment.this.getString(2131692420, new Object[] { paramLoader.getLabel() }));
        break label188;
        localPreferenceScreen.setTitle(PrintSettingsFragment.this.getString(2131692421, new Object[] { paramLoader.getLabel() }));
        break label188;
        if (!paramLoader.isCancelling())
        {
          localPreferenceScreen.setTitle(PrintSettingsFragment.this.getString(2131692422, new Object[] { paramLoader.getLabel() }));
          break label188;
        }
        localPreferenceScreen.setTitle(PrintSettingsFragment.this.getString(2131692420, new Object[] { paramLoader.getLabel() }));
        break label188;
        localPreferenceScreen.setIcon(2130838021);
        continue;
        localPreferenceScreen.setIcon(2130838022);
      }
    }
    
    public void onLoaderReset(Loader<List<PrintJobInfo>> paramLoader)
    {
      PrintSettingsFragment.this.getPreferenceScreen().removePreference(PrintSettingsFragment.-get0(PrintSettingsFragment.this));
    }
  }
  
  private static final class PrintJobsLoader
    extends AsyncTaskLoader<List<PrintJobInfo>>
  {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "PrintJobsLoader";
    private PrintManager.PrintJobStateChangeListener mPrintJobStateChangeListener;
    private List<PrintJobInfo> mPrintJobs = new ArrayList();
    private final PrintManager mPrintManager;
    
    public PrintJobsLoader(Context paramContext)
    {
      super();
      this.mPrintManager = ((PrintManager)paramContext.getSystemService("print")).getGlobalPrintManagerForUser(paramContext.getUserId());
    }
    
    public void deliverResult(List<PrintJobInfo> paramList)
    {
      if (isStarted()) {
        super.deliverResult(paramList);
      }
    }
    
    public List<PrintJobInfo> loadInBackground()
    {
      Object localObject1 = null;
      List localList = this.mPrintManager.getPrintJobs();
      int j = localList.size();
      int i = 0;
      while (i < j)
      {
        PrintJobInfo localPrintJobInfo = ((PrintJob)localList.get(i)).getInfo();
        Object localObject2 = localObject1;
        if (PrintSettingsFragment.-wrap1(localPrintJobInfo))
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          ((List)localObject2).add(localPrintJobInfo);
        }
        i += 1;
        localObject1 = localObject2;
      }
      return (List<PrintJobInfo>)localObject1;
    }
    
    protected void onReset()
    {
      onStopLoading();
      this.mPrintJobs.clear();
      if (this.mPrintJobStateChangeListener != null)
      {
        this.mPrintManager.removePrintJobStateChangeListener(this.mPrintJobStateChangeListener);
        this.mPrintJobStateChangeListener = null;
      }
    }
    
    protected void onStartLoading()
    {
      if (!this.mPrintJobs.isEmpty()) {
        deliverResult(new ArrayList(this.mPrintJobs));
      }
      if (this.mPrintJobStateChangeListener == null)
      {
        this.mPrintJobStateChangeListener = new PrintManager.PrintJobStateChangeListener()
        {
          public void onPrintJobStateChanged(PrintJobId paramAnonymousPrintJobId)
          {
            PrintSettingsFragment.PrintJobsLoader.-wrap0(PrintSettingsFragment.PrintJobsLoader.this);
          }
        };
        this.mPrintManager.addPrintJobStateChangeListener(this.mPrintJobStateChangeListener);
      }
      if (this.mPrintJobs.isEmpty()) {
        onForceLoad();
      }
    }
    
    protected void onStopLoading()
    {
      onCancelLoad();
    }
  }
  
  private final class PrintServicesController
    implements LoaderManager.LoaderCallbacks<List<PrintServiceInfo>>
  {
    private PrintServicesController() {}
    
    public Loader<List<PrintServiceInfo>> onCreateLoader(int paramInt, Bundle paramBundle)
    {
      paramBundle = (PrintManager)PrintSettingsFragment.this.getContext().getSystemService("print");
      if (paramBundle != null) {
        return new PrintServicesLoader(paramBundle, PrintSettingsFragment.this.getContext(), 3);
      }
      return null;
    }
    
    public void onLoadFinished(Loader<List<PrintServiceInfo>> paramLoader, List<PrintServiceInfo> paramList)
    {
      if (paramList.isEmpty())
      {
        PrintSettingsFragment.this.getPreferenceScreen().removePreference(PrintSettingsFragment.-get1(PrintSettingsFragment.this));
        return;
      }
      if (PrintSettingsFragment.this.getPreferenceScreen().findPreference("print_services_category") == null) {
        PrintSettingsFragment.this.getPreferenceScreen().addPreference(PrintSettingsFragment.-get1(PrintSettingsFragment.this));
      }
      PrintSettingsFragment.-get1(PrintSettingsFragment.this).removeAll();
      paramLoader = PrintSettingsFragment.this.getActivity().getPackageManager();
      int j = paramList.size();
      int i = 0;
      if (i < j)
      {
        PrintServiceInfo localPrintServiceInfo = (PrintServiceInfo)paramList.get(i);
        PreferenceScreen localPreferenceScreen = PrintSettingsFragment.this.getPreferenceManager().createPreferenceScreen(PrintSettingsFragment.this.getActivity());
        String str = localPrintServiceInfo.getResolveInfo().loadLabel(paramLoader).toString();
        localPreferenceScreen.setTitle(str);
        ComponentName localComponentName = localPrintServiceInfo.getComponentName();
        localPreferenceScreen.setKey(localComponentName.flattenToString());
        localPreferenceScreen.setFragment(PrintServiceSettingsFragment.class.getName());
        localPreferenceScreen.setPersistent(false);
        if (localPrintServiceInfo.isEnabled()) {
          localPreferenceScreen.setSummary(PrintSettingsFragment.this.getString(2131692407));
        }
        for (;;)
        {
          Object localObject = localPrintServiceInfo.getResolveInfo().loadIcon(paramLoader);
          if (localObject != null) {
            localPreferenceScreen.setIcon((Drawable)localObject);
          }
          localObject = localPreferenceScreen.getExtras();
          ((Bundle)localObject).putBoolean("EXTRA_CHECKED", localPrintServiceInfo.isEnabled());
          ((Bundle)localObject).putString("EXTRA_TITLE", str);
          ((Bundle)localObject).putString("EXTRA_SERVICE_COMPONENT_NAME", localComponentName.flattenToString());
          PrintSettingsFragment.-get1(PrintSettingsFragment.this).addPreference(localPreferenceScreen);
          i += 1;
          break;
          localPreferenceScreen.setSummary(PrintSettingsFragment.this.getString(2131692408));
        }
      }
      paramLoader = PrintSettingsFragment.-wrap0(PrintSettingsFragment.this);
      if (paramLoader != null) {
        PrintSettingsFragment.-get1(PrintSettingsFragment.this).addPreference(paramLoader);
      }
    }
    
    public void onLoaderReset(Loader<List<PrintServiceInfo>> paramLoader)
    {
      PrintSettingsFragment.this.getPreferenceScreen().removePreference(PrintSettingsFragment.-get1(PrintSettingsFragment.this));
    }
  }
  
  private static class PrintSummaryProvider
    implements SummaryLoader.SummaryProvider, PrintManager.PrintJobStateChangeListener
  {
    private final Context mContext;
    private final PrintManager mPrintManager;
    private final SummaryLoader mSummaryLoader;
    
    public PrintSummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
      this.mPrintManager = ((PrintManager)paramContext.getSystemService("print")).getGlobalPrintManagerForUser(paramContext.getUserId());
    }
    
    public void onPrintJobStateChanged(PrintJobId paramPrintJobId)
    {
      paramPrintJobId = this.mPrintManager.getPrintJobs();
      int j = 0;
      int m = paramPrintJobId.size();
      int i = 0;
      while (i < m)
      {
        int k = j;
        if (PrintSettingsFragment.-wrap1(((PrintJob)paramPrintJobId.get(i)).getInfo())) {
          k = j + 1;
        }
        i += 1;
        j = k;
      }
      this.mSummaryLoader.setSummary(this, this.mContext.getResources().getQuantityString(2131951634, j, new Object[] { Integer.valueOf(j) }));
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (this.mPrintManager != null)
      {
        if (paramBoolean)
        {
          this.mPrintManager.addPrintJobStateChangeListener(this);
          onPrintJobStateChanged(null);
        }
      }
      else {
        return;
      }
      this.mPrintManager.removePrintJobStateChangeListener(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\print\PrintSettingsFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */