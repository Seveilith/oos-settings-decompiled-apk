package com.android.settings.dashboard;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.InstrumentedFragment;
import com.android.settings.Lte4GEnabler;
import com.android.settings.Settings.BluetoothSettingsActivity;
import com.android.settings.Settings.ManageApplicationsActivity;
import com.android.settings.Settings.OPSimAndNetworkSettings;
import com.android.settings.Settings.PowerUsageSummaryActivity;
import com.android.settings.Settings.StorageSettingsActivity;
import com.android.settings.Settings.WifiSettingsActivity;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.conditional.Condition;
import com.android.settings.dashboard.conditional.ConditionAdapterUtils;
import com.android.settings.dashboard.conditional.ConditionManager;
import com.android.settings.dashboard.conditional.ConditionManager.ConditionListener;
import com.android.settings.dashboard.conditional.FocusRecyclerView;
import com.android.settings.dashboard.conditional.FocusRecyclerView.FocusListener;
import com.android.settingslib.SuggestionParser;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.SettingsDrawerActivity;
import com.android.settingslib.drawer.SettingsDrawerActivity.CategoryListener;
import com.android.settingslib.drawer.Tile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DashboardSummary
  extends InstrumentedFragment
  implements SettingsDrawerActivity.CategoryListener, ConditionManager.ConditionListener, FocusRecyclerView.FocusListener
{
  private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
  private static final Comparator<DashboardCategory> CATEGORY_COMPARATOR = new Comparator()
  {
    public int compare(DashboardCategory paramAnonymousDashboardCategory1, DashboardCategory paramAnonymousDashboardCategory2)
    {
      return paramAnonymousDashboardCategory2.priority - paramAnonymousDashboardCategory1.priority;
    }
  };
  private static final String CUSTOM_CATAGORY = "com.android.settings.category.custom";
  public static final boolean DEBUG = false;
  private static final boolean DEBUG_TIMING = true;
  private static final String EXTRA_SCROLL_POSITION = "scroll_position";
  public static final String HAS_NEW_VERSION_TO_UPDATE = "has_new_version_to_update";
  public static final String[] INITIAL_ITEMS = { Settings.WifiSettingsActivity.class.getName(), Settings.BluetoothSettingsActivity.class.getName(), Settings.OPSimAndNetworkSettings.class.getName(), Settings.PowerUsageSummaryActivity.class.getName(), Settings.ManageApplicationsActivity.class.getName(), Settings.StorageSettingsActivity.class.getName() };
  private static final String SETTING_PKG = "com.android.settings";
  private static final String SUGGESTIONS = "suggestions";
  private static final String TAG = "DashboardSummary";
  private static final String WIRELESS_CATAGORY = "com.android.settings.category.wireless";
  private static List<DashboardCategory> mCategories;
  private final Uri ALL_DOWNLOAD_FILES_URI = Uri.parse("content://com.oneplus.ap.upgrader.provider/all_download_files");
  private DashboardAdapter mAdapter;
  private ConditionManager mConditionManager;
  private int[] mCustomTileIconResources = { 2130837971, 2130838040, 2130838050, 2130838224 };
  private String[] mCustomTileIntentResources = { "com.android.settings.Settings$OPNotificationAndNotdisturbSettingsActivity", "com.android.settings.Settings$OPButtonsSettingsActivity", "com.android.settings.Settings$OPGestureSettingsActivity", "com.android.settings.Settings$OPStatusBarCustomizeSettingsActivity", "com.android.settings.Settings$OPFontStyleSettingsActivity", "com.android.settings.Settings$OPDefaultHomeSettingsActivity" };
  private int[] mCustomTileLabelResources = { 2131690079, 2131690671, 2131690021, 2131690138 };
  private FocusRecyclerView mDashboard;
  private LinearLayoutManager mLayoutManager;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getAction();
      if (("android.intent.action.SIM_STATE_CHANGED".equals(str)) || ("android.intent.action.AIRPLANE_MODE".equals(str)))
      {
        Log.d("DashboardSummary", "Received ACTION_SIM_STATE_CHANGED or ACTION_AIRPLANE_MODE_CHANGED");
        DashboardSummary.-get1(DashboardSummary.this).updateLte4GEnabler();
      }
      if ("android.intent.action.SIM_STATE_CHANGED".equals(paramAnonymousIntent.getAction())) {}
      switch (((TelephonyManager)paramAnonymousContext.getSystemService("phone")).getSimState())
      {
      default: 
        return;
      }
      DashboardSummary.-get2(DashboardSummary.this).refreshAll();
    }
  };
  private ContentResolver mResolver;
  private SuggestionParser mSuggestionParser;
  private SuggestionsChecks mSuggestionsChecks;
  private SummaryLoader mSummaryLoader;
  private SystemUpdateObserver mSystemUpdateObserver;
  private int[] mWirelessTileIconResources = { 2130838075, 2130838037, 2130838218, 2130838045, 2130838056 };
  private String[] mWirelessTileIntentResources = { "com.android.settings.Settings$WifiSettingsActivity", "com.android.settings.Settings$BluetoothSettingsActivity", "com.android.settings.Settings$OPSimAndNetworkSettings", "com.android.settings.Settings$OPDataUsageSummaryActivity", "com.android.settings.Settings$WirelessSettingsActivity" };
  private int[] mWirelessTileLabelResources = { 2131691332, 2131691236, 2131690148, 2131692752, 2131691003 };
  
  private DashboardCategory createCategory(Context paramContext, String paramString)
  {
    paramContext = new DashboardCategory();
    paramContext.key = paramString;
    if ("com.android.settings.category.wireless".equals(paramString)) {
      paramContext.title = getActivity().getResources().getString(2131690789);
    }
    if ("com.android.settings.category.custom".equals(paramString)) {
      paramContext.title = getActivity().getResources().getString(2131690296);
    }
    return paramContext;
  }
  
  private List<DashboardCategory> initDashboard()
  {
    long l = System.currentTimeMillis();
    Object localObject1 = new ArrayList();
    int i = 0;
    Object localObject2;
    while (i < this.mCustomTileLabelResources.length)
    {
      localObject2 = new Tile();
      ((Tile)localObject2).category = "com.android.settings.category.custom";
      ((Tile)localObject2).icon = Icon.createWithResource("com.android.settings", this.mCustomTileIconResources[i]);
      ((Tile)localObject2).title = getActivity().getResources().getString(this.mCustomTileLabelResources[i]);
      ((Tile)localObject2).intent = new Intent().setClassName("com.android.settings", this.mCustomTileIntentResources[i]);
      ((ArrayList)localObject1).add(localObject2);
      i += 1;
    }
    i = 0;
    while (i < this.mWirelessTileLabelResources.length)
    {
      localObject2 = new Tile();
      ((Tile)localObject2).category = "com.android.settings.category.wireless";
      ((Tile)localObject2).icon = Icon.createWithResource("com.android.settings", this.mWirelessTileIconResources[i]);
      ((Tile)localObject2).title = getActivity().getResources().getString(this.mWirelessTileLabelResources[i]);
      ((Tile)localObject2).intent = new Intent().setClassName("com.android.settings", this.mWirelessTileIntentResources[i]);
      ((ArrayList)localObject1).add(localObject2);
      i += 1;
    }
    HashMap localHashMap = new HashMap();
    Iterator localIterator = ((Iterable)localObject1).iterator();
    while (localIterator.hasNext())
    {
      Tile localTile = (Tile)localIterator.next();
      localObject2 = (DashboardCategory)localHashMap.get(localTile.category);
      Log.d("DashboardSummary", "oneplus-- initDashboard--title:" + localTile.title);
      Log.d("DashboardSummary", "oneplus-- initDashboard--category:" + localTile.category);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = createCategory(getActivity(), localTile.category);
        if (localObject1 == null) {
          Log.w("DashboardSummary", "Couldn't find category " + localTile.category);
        } else {
          localHashMap.put(((DashboardCategory)localObject1).key, localObject1);
        }
      }
      else
      {
        ((DashboardCategory)localObject1).addTile(localTile);
      }
    }
    localObject1 = new ArrayList(localHashMap.values());
    Log.d("DashboardSummary", "oneplus-- initDashboard:" + (System.currentTimeMillis() - l) + " ms" + localObject1);
    return (List<DashboardCategory>)localObject1;
  }
  
  private void rebuildUI()
  {
    if (!isAdded())
    {
      Log.w("DashboardSummary", "Cannot build the DashboardSummary UI yet as the Fragment is not added");
      return;
    }
    System.currentTimeMillis();
    List localList;
    if (((SettingsActivity)getActivity()).getEarlyDashboardCategories() == null) {
      localList = initDashboard();
    }
    for (;;)
    {
      this.mAdapter.setCategories(localList);
      new SuggestionLoader(null).execute(new Void[0]);
      return;
      localList = ((SettingsActivity)getActivity()).getDashboardCategories();
      if (this.mSummaryLoader != null)
      {
        this.mSummaryLoader.setListening(false);
        this.mSummaryLoader.release();
      }
      this.mSummaryLoader = null;
      this.mSummaryLoader = new SummaryLoader(getActivity(), localList);
      this.mSummaryLoader.setAdapter(this.mAdapter);
      this.mSummaryLoader.setListening(true);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 35;
  }
  
  public void onCategoriesChanged()
  {
    rebuildUI();
  }
  
  public void onConditionsChanged()
  {
    Log.d("DashboardSummary", "onConditionsChanged");
    if (Looper.getMainLooper().equals(Looper.myLooper())) {
      this.mAdapter.setConditions(this.mConditionManager.getConditions());
    }
    View localView;
    do
    {
      return;
      localView = getView();
    } while (localView == null);
    localView.post(new Runnable()
    {
      public void run()
      {
        DashboardSummary.-get1(DashboardSummary.this).setConditions(DashboardSummary.-get2(DashboardSummary.this).getConditions());
      }
    });
  }
  
  public void onCreate(Bundle paramBundle)
  {
    long l = System.currentTimeMillis();
    super.onCreate(paramBundle);
    setHasOptionsMenu(true);
    paramBundle = getContext();
    this.mConditionManager = ConditionManager.get(paramBundle, false);
    this.mSuggestionParser = new SuggestionParser(paramBundle, paramBundle.getSharedPreferences("suggestions", 0), 2131230864);
    this.mSuggestionsChecks = new SuggestionsChecks(getContext());
    Log.d("DashboardSummary", "onCreate took " + (System.currentTimeMillis() - l) + " ms");
    this.mResolver = paramBundle.getContentResolver();
    this.mSystemUpdateObserver = new SystemUpdateObserver(new Handler());
    this.mSystemUpdateObserver.startObserving();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(2130968666, paramViewGroup, false);
  }
  
  public void onDestroy()
  {
    if (this.mSummaryLoader != null) {
      this.mSummaryLoader.release();
    }
    if (this.mSystemUpdateObserver != null) {
      this.mSystemUpdateObserver.stopObserving();
    }
    super.onDestroy();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (this.mLayoutManager == null) {
      return;
    }
    paramBundle.putInt("scroll_position", this.mLayoutManager.findFirstVisibleItemPosition());
  }
  
  public void onStart()
  {
    long l = System.currentTimeMillis();
    super.onStart();
    this.mAdapter.getLte4GEnabler().resume();
    ((SettingsDrawerActivity)getActivity()).addCategoryListener(this);
    if (this.mSummaryLoader != null) {
      this.mSummaryLoader.setListening(true);
    }
    Object localObject1 = this.mConditionManager.getConditions().iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Condition)((Iterator)localObject1).next();
      if (((Condition)localObject2).shouldShow()) {
        MetricsLogger.visible(getContext(), ((Condition)localObject2).getMetricsConstant());
      }
    }
    if (this.mAdapter.getSuggestions() != null)
    {
      localObject1 = this.mAdapter.getSuggestions().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Tile)((Iterator)localObject1).next();
        MetricsLogger.action(getContext(), 384, DashboardAdapter.getSuggestionIdentifier(getContext(), (Tile)localObject2));
      }
    }
    localObject1 = new IntentFilter("android.intent.action.AIRPLANE_MODE");
    ((IntentFilter)localObject1).addAction("android.intent.action.SIM_STATE_CHANGED");
    ((IntentFilter)localObject1).addAction("android.intent.action.SIM_STATE_CHANGED");
    getActivity().registerReceiver(this.mReceiver, (IntentFilter)localObject1);
    Log.d("DashboardSummary", "onStart took " + (System.currentTimeMillis() - l) + " ms");
  }
  
  public void onStop()
  {
    super.onStop();
    this.mAdapter.getLte4GEnabler().pause();
    getActivity().unregisterReceiver(this.mReceiver);
    ((SettingsDrawerActivity)getActivity()).remCategoryListener(this);
    if (this.mSummaryLoader != null) {
      this.mSummaryLoader.setListening(false);
    }
    Iterator localIterator = this.mConditionManager.getConditions().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Condition)localIterator.next();
      if (((Condition)localObject).shouldShow()) {
        MetricsLogger.hidden(getContext(), ((Condition)localObject).getMetricsConstant());
      }
    }
    if (this.mAdapter.getSuggestions() == null) {
      return;
    }
    localIterator = this.mAdapter.getSuggestions().iterator();
    while (localIterator.hasNext())
    {
      localObject = (Tile)localIterator.next();
      MetricsLogger.action(getContext(), 385, DashboardAdapter.getSuggestionIdentifier(getContext(), (Tile)localObject));
    }
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    long l = System.currentTimeMillis();
    this.mDashboard = ((FocusRecyclerView)paramView.findViewById(2131362067));
    this.mLayoutManager = new LinearLayoutManager(getContext());
    this.mLayoutManager.setOrientation(1);
    if (paramBundle != null)
    {
      int i = paramBundle.getInt("scroll_position");
      this.mLayoutManager.scrollToPosition(i);
    }
    this.mDashboard.setLayoutManager(this.mLayoutManager);
    this.mDashboard.setHasFixedSize(true);
    this.mDashboard.setListener(this);
    this.mDashboard.setItemAnimator(null);
    this.mDashboard.addItemDecoration(new DashboardDecorator(getContext()));
    this.mAdapter = new DashboardAdapter(getContext(), this.mSuggestionParser);
    this.mAdapter.setConditions(this.mConditionManager.getConditions());
    this.mDashboard.setAdapter(this.mAdapter);
    ConditionAdapterUtils.addDismiss(this.mDashboard);
    Log.d("DashboardSummary", "onViewCreated took " + (System.currentTimeMillis() - l) + " ms");
    rebuildUI();
    Log.d("DashboardSummary", "onViewCreated took--1: " + (System.currentTimeMillis() - l) + " ms");
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    long l = System.currentTimeMillis();
    if (paramBoolean)
    {
      this.mConditionManager.addListener(this);
      this.mConditionManager.refreshAll();
    }
    for (;;)
    {
      Log.d("DashboardSummary", "onWindowFocusChanged took " + (System.currentTimeMillis() - l) + " ms");
      return;
      this.mConditionManager.remListener(this);
    }
  }
  
  private class SuggestionLoader
    extends AsyncTask<Void, Void, List<Tile>>
  {
    private SuggestionLoader() {}
    
    protected List<Tile> doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = DashboardSummary.-get3(DashboardSummary.this).getSuggestions();
      int j;
      for (int i = 0; i < paramVarArgs.size(); i = j + 1)
      {
        j = i;
        if (DashboardSummary.-get4(DashboardSummary.this).isSuggestionComplete((Tile)paramVarArgs.get(i)))
        {
          DashboardSummary.-get1(DashboardSummary.this).disableSuggestion((Tile)paramVarArgs.get(i));
          paramVarArgs.remove(i);
          j = i - 1;
        }
      }
      return paramVarArgs;
    }
    
    protected void onPostExecute(List<Tile> paramList)
    {
      DashboardSummary.-get1(DashboardSummary.this).setSuggestions(paramList);
    }
  }
  
  private class SystemUpdateObserver
    extends ContentObserver
  {
    private final Uri SYSTEM_UPDATE_URI = Settings.System.getUriFor("has_new_version_to_update");
    private final Uri ZEN_MODE = Settings.Global.getUriFor("zen_mode");
    
    public SystemUpdateObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      onChange(paramBoolean, null);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if (paramBoolean) {
        return;
      }
      if ((this.SYSTEM_UPDATE_URI.equals(paramUri)) || (DashboardSummary.-get0(DashboardSummary.this).equals(paramUri))) {
        DashboardSummary.-wrap0(DashboardSummary.this);
      }
      while ((!this.ZEN_MODE.equals(paramUri)) || (DashboardSummary.-get2(DashboardSummary.this) == null)) {
        return;
      }
      DashboardSummary.-get2(DashboardSummary.this).refreshAll();
    }
    
    public void startObserving()
    {
      ContentResolver localContentResolver = DashboardSummary.this.getContext().getContentResolver();
      localContentResolver.unregisterContentObserver(this);
      localContentResolver.registerContentObserver(this.ZEN_MODE, false, this, -1);
      localContentResolver.registerContentObserver(this.SYSTEM_UPDATE_URI, false, this, -1);
      localContentResolver.registerContentObserver(DashboardSummary.-get0(DashboardSummary.this), false, this, -1);
    }
    
    public void stopObserving()
    {
      DashboardSummary.this.getContext().getContentResolver().unregisterContentObserver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\DashboardSummary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */