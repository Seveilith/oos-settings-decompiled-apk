package com.android.settings.print;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.Theme;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.print.PrintManager;
import android.print.PrintServicesLoader;
import android.print.PrinterDiscoverySession;
import android.print.PrinterDiscoverySession.OnPrintersChangeListener;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintServiceInfo;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;
import com.android.settings.widget.ToggleSwitch;
import com.android.settings.widget.ToggleSwitch.OnBeforeCheckedChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrintServiceSettingsFragment
  extends SettingsPreferenceFragment
  implements SwitchBar.OnSwitchChangeListener, LoaderManager.LoaderCallbacks<List<PrintServiceInfo>>
{
  private static final int LOADER_ID_PRINTERS_LOADER = 1;
  private static final int LOADER_ID_PRINT_SERVICE_LOADER = 2;
  private static final String LOG_TAG = "PrintServiceSettingsFragment";
  private Intent mAddPrintersIntent;
  private ComponentName mComponentName;
  private final DataSetObserver mDataObserver = new DataSetObserver()
  {
    private void invalidateOptionsMenuIfNeeded()
    {
      int i = PrintServiceSettingsFragment.-get3(PrintServiceSettingsFragment.this).getUnfilteredCount();
      if ((PrintServiceSettingsFragment.-get1(PrintServiceSettingsFragment.this) <= 0) && (i > 0)) {}
      for (;;)
      {
        PrintServiceSettingsFragment.this.getActivity().invalidateOptionsMenu();
        do
        {
          PrintServiceSettingsFragment.-set0(PrintServiceSettingsFragment.this, i);
          return;
        } while ((PrintServiceSettingsFragment.-get1(PrintServiceSettingsFragment.this) <= 0) || (i > 0));
      }
    }
    
    public void onChanged()
    {
      invalidateOptionsMenuIfNeeded();
      PrintServiceSettingsFragment.-wrap1(PrintServiceSettingsFragment.this);
    }
    
    public void onInvalidated()
    {
      invalidateOptionsMenuIfNeeded();
    }
  };
  private int mLastUnfilteredItemCount;
  private String mPreferenceKey;
  private PrintersAdapter mPrintersAdapter;
  private SearchView mSearchView;
  private boolean mServiceEnabled;
  private Intent mSettingsIntent;
  private SwitchBar mSwitchBar;
  private ToggleSwitch mToggleSwitch;
  
  private ListView getBackupListView()
  {
    return (ListView)getView().findViewById(2131362446);
  }
  
  private void initComponents()
  {
    this.mPrintersAdapter = new PrintersAdapter(null);
    this.mPrintersAdapter.registerDataSetObserver(this.mDataObserver);
    this.mSwitchBar = ((SettingsActivity)getActivity()).getSwitchBar();
    this.mSwitchBar.addOnSwitchChangeListener(this);
    this.mSwitchBar.show();
    this.mToggleSwitch = this.mSwitchBar.getSwitch();
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener()
    {
      public boolean onBeforeCheckedChanged(ToggleSwitch paramAnonymousToggleSwitch, boolean paramAnonymousBoolean)
      {
        PrintServiceSettingsFragment.-wrap0(PrintServiceSettingsFragment.this, PrintServiceSettingsFragment.-get2(PrintServiceSettingsFragment.this), paramAnonymousBoolean);
        return false;
      }
    });
    getBackupListView().setSelector(new ColorDrawable(0));
    getBackupListView().setAdapter(this.mPrintersAdapter);
    getBackupListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        paramAnonymousAdapterView = (PrinterInfo)PrintServiceSettingsFragment.-get3(PrintServiceSettingsFragment.this).getItem(paramAnonymousInt);
        if (paramAnonymousAdapterView.getInfoIntent() != null) {}
        try
        {
          PrintServiceSettingsFragment.this.getActivity().startIntentSender(paramAnonymousAdapterView.getInfoIntent().getIntentSender(), null, 0, 0, 0);
          return;
        }
        catch (IntentSender.SendIntentException paramAnonymousAdapterView)
        {
          Log.e("PrintServiceSettingsFragment", "Could not execute info intent: %s", paramAnonymousAdapterView);
        }
      }
    });
  }
  
  private void onPreferenceToggled(String paramString, boolean paramBoolean)
  {
    ((PrintManager)getContext().getSystemService("print")).setPrintServiceEnabled(this.mComponentName, paramBoolean);
  }
  
  private void updateEmptyView()
  {
    ViewGroup localViewGroup = (ViewGroup)getListView().getParent();
    View localView1 = getBackupListView().getEmptyView();
    View localView2;
    if (!this.mToggleSwitch.isChecked())
    {
      localView2 = localView1;
      if (localView1 != null)
      {
        localView2 = localView1;
        if (localView1.getId() != 2131362132)
        {
          localViewGroup.removeView(localView1);
          localView2 = null;
        }
      }
      if (localView2 == null)
      {
        localView1 = getActivity().getLayoutInflater().inflate(2130968695, localViewGroup, false);
        ((ImageView)localView1.findViewById(2131361793)).setContentDescription(getString(2131692413));
        ((TextView)localView1.findViewById(2131361987)).setText(2131692413);
        localViewGroup.addView(localView1);
        getBackupListView().setEmptyView(localView1);
      }
    }
    do
    {
      do
      {
        do
        {
          return;
          if (this.mPrintersAdapter.getUnfilteredCount() > 0) {
            break;
          }
          localView2 = localView1;
          if (localView1 != null)
          {
            localView2 = localView1;
            if (localView1.getId() != 2131362134)
            {
              localViewGroup.removeView(localView1);
              localView2 = null;
            }
          }
        } while (localView2 != null);
        localView1 = getActivity().getLayoutInflater().inflate(2130968696, localViewGroup, false);
        localViewGroup.addView(localView1);
        getBackupListView().setEmptyView(localView1);
        return;
      } while (this.mPrintersAdapter.getCount() > 0);
      localView2 = localView1;
      if (localView1 != null)
      {
        localView2 = localView1;
        if (localView1.getId() != 2131362132)
        {
          localViewGroup.removeView(localView1);
          localView2 = null;
        }
      }
    } while (localView2 != null);
    localView1 = getActivity().getLayoutInflater().inflate(2130968695, localViewGroup, false);
    ((ImageView)localView1.findViewById(2131361793)).setContentDescription(getString(2131692404));
    ((TextView)localView1.findViewById(2131361987)).setText(2131692404);
    localViewGroup.addView(localView1);
    getBackupListView().setEmptyView(localView1);
  }
  
  private void updateUiForArguments()
  {
    Bundle localBundle = getArguments();
    this.mComponentName = ComponentName.unflattenFromString(localBundle.getString("EXTRA_SERVICE_COMPONENT_NAME"));
    this.mPreferenceKey = this.mComponentName.flattenToString();
    boolean bool = localBundle.getBoolean("EXTRA_CHECKED");
    this.mSwitchBar.setCheckedInternal(bool);
    getLoaderManager().initLoader(2, null, this);
    setHasOptionsMenu(true);
  }
  
  private void updateUiForServiceState()
  {
    if (this.mServiceEnabled)
    {
      this.mSwitchBar.setCheckedInternal(true);
      this.mPrintersAdapter.enable();
    }
    for (;;)
    {
      getActivity().invalidateOptionsMenu();
      return;
      this.mSwitchBar.setCheckedInternal(false);
      this.mPrintersAdapter.disable();
    }
  }
  
  protected int getMetricsCategory()
  {
    return 79;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getArguments().getString("EXTRA_TITLE");
    if (!TextUtils.isEmpty(paramBundle)) {
      getActivity().setTitle(paramBundle);
    }
  }
  
  public Loader<List<PrintServiceInfo>> onCreateLoader(int paramInt, Bundle paramBundle)
  {
    return new PrintServicesLoader((PrintManager)getContext().getSystemService("print"), getContext(), 3);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    paramMenuInflater.inflate(2132017157, paramMenu);
    paramMenuInflater = paramMenu.findItem(2131362854);
    if ((this.mServiceEnabled) && (this.mAddPrintersIntent != null))
    {
      paramMenuInflater.setIntent(this.mAddPrintersIntent);
      paramMenuInflater = paramMenu.findItem(2131362855);
      if ((!this.mServiceEnabled) || (this.mSettingsIntent == null)) {
        break label167;
      }
      paramMenuInflater.setIntent(this.mSettingsIntent);
    }
    for (;;)
    {
      paramMenuInflater = paramMenu.findItem(2131362853);
      if ((!this.mServiceEnabled) || (this.mPrintersAdapter.getUnfilteredCount() <= 0)) {
        break label179;
      }
      this.mSearchView = ((SearchView)paramMenuInflater.getActionView());
      this.mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
      {
        public boolean onQueryTextChange(String paramAnonymousString)
        {
          PrintServiceSettingsFragment.-get3(PrintServiceSettingsFragment.this).getFilter().filter(paramAnonymousString);
          return true;
        }
        
        public boolean onQueryTextSubmit(String paramAnonymousString)
        {
          return true;
        }
      });
      this.mSearchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
      {
        public void onViewAttachedToWindow(View paramAnonymousView)
        {
          if (AccessibilityManager.getInstance(PrintServiceSettingsFragment.this.getActivity()).isEnabled()) {
            paramAnonymousView.announceForAccessibility(PrintServiceSettingsFragment.this.getString(2131692423));
          }
        }
        
        public void onViewDetachedFromWindow(View paramAnonymousView)
        {
          Activity localActivity = PrintServiceSettingsFragment.this.getActivity();
          if ((localActivity == null) || (localActivity.isFinishing())) {}
          while (!AccessibilityManager.getInstance(localActivity).isEnabled()) {
            return;
          }
          paramAnonymousView.announceForAccessibility(PrintServiceSettingsFragment.this.getString(2131692424));
        }
      });
      return;
      paramMenu.removeItem(2131362854);
      break;
      label167:
      paramMenu.removeItem(2131362855);
    }
    label179:
    paramMenu.removeItem(2131362853);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    this.mServiceEnabled = getArguments().getBoolean("EXTRA_CHECKED");
    return paramLayoutInflater;
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    this.mSwitchBar.removeOnSwitchChangeListener(this);
    this.mSwitchBar.hide();
  }
  
  public void onLoadFinished(Loader<List<PrintServiceInfo>> paramLoader, List<PrintServiceInfo> paramList)
  {
    List localList = null;
    paramLoader = localList;
    int i;
    if (paramList != null)
    {
      int j = paramList.size();
      i = 0;
      paramLoader = localList;
      if (i < j)
      {
        if (!((PrintServiceInfo)paramList.get(i)).getComponentName().equals(this.mComponentName)) {
          break label255;
        }
        paramLoader = (PrintServiceInfo)paramList.get(i);
      }
    }
    if (paramLoader == null) {
      finishFragment();
    }
    this.mServiceEnabled = paramLoader.isEnabled();
    if (paramLoader.getSettingsActivityName() != null)
    {
      paramList = new Intent("android.intent.action.MAIN");
      paramList.setComponent(new ComponentName(paramLoader.getComponentName().getPackageName(), paramLoader.getSettingsActivityName()));
      localList = getPackageManager().queryIntentActivities(paramList, 0);
      if ((!localList.isEmpty()) && (((ResolveInfo)localList.get(0)).activityInfo.exported)) {
        this.mSettingsIntent = paramList;
      }
      label166:
      if (paramLoader.getAddPrintersActivityName() == null) {
        break label270;
      }
      paramList = new Intent("android.intent.action.MAIN");
      paramList.setComponent(new ComponentName(paramLoader.getComponentName().getPackageName(), paramLoader.getAddPrintersActivityName()));
      paramLoader = getPackageManager().queryIntentActivities(paramList, 0);
      if ((paramLoader.isEmpty()) || (!((ResolveInfo)paramLoader.get(0)).activityInfo.exported)) {}
    }
    label255:
    label270:
    for (this.mAddPrintersIntent = paramList;; this.mAddPrintersIntent = null)
    {
      updateUiForServiceState();
      return;
      i += 1;
      break;
      this.mSettingsIntent = null;
      break label166;
    }
  }
  
  public void onLoaderReset(Loader<List<PrintServiceInfo>> paramLoader)
  {
    updateUiForServiceState();
  }
  
  public void onPause()
  {
    if (this.mSearchView != null) {
      this.mSearchView.setOnQueryTextListener(null);
    }
    super.onPause();
  }
  
  public void onStart()
  {
    super.onStart();
    updateEmptyView();
    updateUiForServiceState();
  }
  
  public void onStop()
  {
    super.onStop();
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    updateEmptyView();
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    initComponents();
    updateUiForArguments();
    getListView().setVisibility(8);
    getBackupListView().setVisibility(0);
  }
  
  private final class PrintersAdapter
    extends BaseAdapter
    implements LoaderManager.LoaderCallbacks<List<PrinterInfo>>, Filterable
  {
    private final List<PrinterInfo> mFilteredPrinters = new ArrayList();
    private CharSequence mLastSearchString;
    private final Object mLock = new Object();
    private final List<PrinterInfo> mPrinters = new ArrayList();
    
    private PrintersAdapter() {}
    
    public void disable()
    {
      PrintServiceSettingsFragment.this.getLoaderManager().destroyLoader(1);
      this.mPrinters.clear();
    }
    
    public void enable()
    {
      PrintServiceSettingsFragment.this.getLoaderManager().initLoader(1, null, this);
    }
    
    public int getCount()
    {
      synchronized (this.mLock)
      {
        int i = this.mFilteredPrinters.size();
        return i;
      }
    }
    
    public Filter getFilter()
    {
      new Filter()
      {
        protected Filter.FilterResults performFiltering(CharSequence paramAnonymousCharSequence)
        {
          for (;;)
          {
            int i;
            synchronized (PrintServiceSettingsFragment.PrintersAdapter.-get1(PrintServiceSettingsFragment.PrintersAdapter.this))
            {
              boolean bool = TextUtils.isEmpty(paramAnonymousCharSequence);
              if (bool) {
                return null;
              }
              Filter.FilterResults localFilterResults = new Filter.FilterResults();
              ArrayList localArrayList = new ArrayList();
              paramAnonymousCharSequence = paramAnonymousCharSequence.toString().toLowerCase();
              int j = PrintServiceSettingsFragment.PrintersAdapter.-get2(PrintServiceSettingsFragment.PrintersAdapter.this).size();
              i = 0;
              if (i < j)
              {
                PrinterInfo localPrinterInfo = (PrinterInfo)PrintServiceSettingsFragment.PrintersAdapter.-get2(PrintServiceSettingsFragment.PrintersAdapter.this).get(i);
                String str = localPrinterInfo.getName();
                if ((str != null) && (str.toLowerCase().contains(paramAnonymousCharSequence))) {
                  localArrayList.add(localPrinterInfo);
                }
              }
              else
              {
                localFilterResults.values = localArrayList;
                localFilterResults.count = localArrayList.size();
                return localFilterResults;
              }
            }
            i += 1;
          }
        }
        
        protected void publishResults(CharSequence paramAnonymousCharSequence, Filter.FilterResults paramAnonymousFilterResults)
        {
          synchronized (PrintServiceSettingsFragment.PrintersAdapter.-get1(PrintServiceSettingsFragment.PrintersAdapter.this))
          {
            PrintServiceSettingsFragment.PrintersAdapter.-set0(PrintServiceSettingsFragment.PrintersAdapter.this, paramAnonymousCharSequence);
            PrintServiceSettingsFragment.PrintersAdapter.-get0(PrintServiceSettingsFragment.PrintersAdapter.this).clear();
            if (paramAnonymousFilterResults == null)
            {
              PrintServiceSettingsFragment.PrintersAdapter.-get0(PrintServiceSettingsFragment.PrintersAdapter.this).addAll(PrintServiceSettingsFragment.PrintersAdapter.-get2(PrintServiceSettingsFragment.PrintersAdapter.this));
              PrintServiceSettingsFragment.PrintersAdapter.this.notifyDataSetChanged();
              return;
            }
            paramAnonymousCharSequence = (List)paramAnonymousFilterResults.values;
            PrintServiceSettingsFragment.PrintersAdapter.-get0(PrintServiceSettingsFragment.PrintersAdapter.this).addAll(paramAnonymousCharSequence);
          }
        }
      };
    }
    
    public Object getItem(int paramInt)
    {
      synchronized (this.mLock)
      {
        Object localObject2 = this.mFilteredPrinters.get(paramInt);
        return localObject2;
      }
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getUnfilteredCount()
    {
      return this.mPrinters.size();
    }
    
    public View getView(int paramInt, View paramView, final ViewGroup paramViewGroup)
    {
      View localView = paramView;
      if (paramView == null) {
        localView = PrintServiceSettingsFragment.this.getActivity().getLayoutInflater().inflate(2130968940, paramViewGroup, false);
      }
      localView.setEnabled(isActionable(paramInt));
      paramViewGroup = (PrinterInfo)getItem(paramInt);
      Object localObject2 = paramViewGroup.getName();
      Object localObject1 = paramViewGroup.getDescription();
      paramView = paramViewGroup.loadIcon(PrintServiceSettingsFragment.this.getActivity());
      ((TextView)localView.findViewById(2131361894)).setText((CharSequence)localObject2);
      localObject2 = (TextView)localView.findViewById(2131362171);
      if (!TextUtils.isEmpty((CharSequence)localObject1))
      {
        ((TextView)localObject2).setText((CharSequence)localObject1);
        ((TextView)localObject2).setVisibility(0);
        localObject1 = (LinearLayout)localView.findViewById(2131362460);
        if (paramViewGroup.getInfoIntent() == null) {
          break label254;
        }
        ((LinearLayout)localObject1).setVisibility(0);
        ((LinearLayout)localObject1).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            try
            {
              PrintServiceSettingsFragment.this.getActivity().startIntentSender(paramViewGroup.getInfoIntent().getIntentSender(), null, 0, 0, 0);
              return;
            }
            catch (IntentSender.SendIntentException paramAnonymousView)
            {
              Log.e("PrintServiceSettingsFragment", "Could not execute pending info intent: %s", paramAnonymousView);
            }
          }
        });
      }
      for (;;)
      {
        paramViewGroup = (ImageView)localView.findViewById(2131361793);
        if (paramView == null) {
          break label264;
        }
        paramViewGroup.setVisibility(0);
        if (!isActionable(paramInt))
        {
          paramView.mutate();
          localObject1 = new TypedValue();
          PrintServiceSettingsFragment.this.getActivity().getTheme().resolveAttribute(16842803, (TypedValue)localObject1, true);
          paramView.setAlpha((int)(((TypedValue)localObject1).getFloat() * 255.0F));
        }
        paramViewGroup.setImageDrawable(paramView);
        return localView;
        ((TextView)localObject2).setText(null);
        ((TextView)localObject2).setVisibility(8);
        break;
        label254:
        ((LinearLayout)localObject1).setVisibility(8);
      }
      label264:
      paramViewGroup.setVisibility(8);
      return localView;
    }
    
    public boolean isActionable(int paramInt)
    {
      return ((PrinterInfo)getItem(paramInt)).getStatus() != 3;
    }
    
    public Loader<List<PrinterInfo>> onCreateLoader(int paramInt, Bundle paramBundle)
    {
      if (paramInt == 1) {
        return new PrintServiceSettingsFragment.PrintersLoader(PrintServiceSettingsFragment.this.getContext());
      }
      return null;
    }
    
    public void onLoadFinished(Loader<List<PrinterInfo>> arg1, List<PrinterInfo> paramList)
    {
      for (;;)
      {
        int i;
        synchronized (this.mLock)
        {
          this.mPrinters.clear();
          int j = paramList.size();
          i = 0;
          if (i < j)
          {
            PrinterInfo localPrinterInfo = (PrinterInfo)paramList.get(i);
            if (localPrinterInfo.getId().getServiceName().equals(PrintServiceSettingsFragment.-get0(PrintServiceSettingsFragment.this))) {
              this.mPrinters.add(localPrinterInfo);
            }
          }
          else
          {
            this.mFilteredPrinters.clear();
            this.mFilteredPrinters.addAll(this.mPrinters);
            if (!TextUtils.isEmpty(this.mLastSearchString)) {
              getFilter().filter(this.mLastSearchString);
            }
            notifyDataSetChanged();
            return;
          }
        }
        i += 1;
      }
    }
    
    public void onLoaderReset(Loader<List<PrinterInfo>> arg1)
    {
      synchronized (this.mLock)
      {
        this.mPrinters.clear();
        this.mFilteredPrinters.clear();
        this.mLastSearchString = null;
        notifyDataSetInvalidated();
        return;
      }
    }
  }
  
  private static class PrintersLoader
    extends Loader<List<PrinterInfo>>
  {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "PrintersLoader";
    private PrinterDiscoverySession mDiscoverySession;
    private final Map<PrinterId, PrinterInfo> mPrinters = new LinkedHashMap();
    
    public PrintersLoader(Context paramContext)
    {
      super();
    }
    
    private boolean cancelInternal()
    {
      if ((this.mDiscoverySession != null) && (this.mDiscoverySession.isPrinterDiscoveryStarted()))
      {
        this.mDiscoverySession.stopPrinterDiscovery();
        return true;
      }
      return false;
    }
    
    private void loadInternal()
    {
      if (this.mDiscoverySession == null)
      {
        this.mDiscoverySession = ((PrintManager)getContext().getSystemService("print")).createPrinterDiscoverySession();
        this.mDiscoverySession.setOnPrintersChangeListener(new PrinterDiscoverySession.OnPrintersChangeListener()
        {
          public void onPrintersChanged()
          {
            PrintServiceSettingsFragment.PrintersLoader.this.deliverResult(new ArrayList(PrintServiceSettingsFragment.PrintersLoader.-get0(PrintServiceSettingsFragment.PrintersLoader.this).getPrinters()));
          }
        });
      }
      this.mDiscoverySession.startPrinterDiscovery(null);
    }
    
    public void deliverResult(List<PrinterInfo> paramList)
    {
      if (isStarted()) {
        super.deliverResult(paramList);
      }
    }
    
    protected void onAbandon()
    {
      onStopLoading();
    }
    
    protected boolean onCancelLoad()
    {
      return cancelInternal();
    }
    
    protected void onForceLoad()
    {
      loadInternal();
    }
    
    protected void onReset()
    {
      onStopLoading();
      this.mPrinters.clear();
      if (this.mDiscoverySession != null)
      {
        this.mDiscoverySession.destroy();
        this.mDiscoverySession = null;
      }
    }
    
    protected void onStartLoading()
    {
      if (!this.mPrinters.isEmpty()) {
        deliverResult(new ArrayList(this.mPrinters.values()));
      }
      onForceLoad();
    }
    
    protected void onStopLoading()
    {
      onCancelLoad();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\print\PrintServiceSettingsFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */