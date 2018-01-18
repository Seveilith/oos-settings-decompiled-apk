package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.LocaleList;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.text.BidiFormatter;
import android.text.Spannable;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toolbar;
import android.widget.Toolbar.LayoutParams;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.LinkifyUtils;
import com.android.settings.LinkifyUtils.OnClickListener;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.location.ScanningSettings;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter.Filter;
import com.android.settingslib.bluetooth.BluetoothEventManager;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.oneplus.settings.ui.OPPreferenceDivider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

public final class BluetoothSettings
  extends DeviceListPreferenceFragment
  implements Indexable
{
  private static final String BTOPP_ACTION_OPEN_RECEIVED_FILES = "android.btopp.intent.action.OPEN_RECEIVED_FILES";
  private static final String KEY_PAIRED_DEVICES = "paired_devices";
  private static final int MENU_ID_RENAME_DEVICE = 2;
  private static final int MENU_ID_SCAN = 1;
  private static final int MENU_ID_SHOW_RECEIVED = 3;
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      Resources localResources = paramAnonymousContext.getResources();
      Object localObject = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject).title = localResources.getString(2131691235);
      ((SearchIndexableRaw)localObject).screenTitle = localResources.getString(2131691235);
      localArrayList.add(localObject);
      localObject = Utils.getLocalBtManager(paramAnonymousContext);
      if (localObject != null)
      {
        localObject = ((LocalBluetoothManager)localObject).getBluetoothAdapter().getBondedDevices().iterator();
        while (((Iterator)localObject).hasNext())
        {
          BluetoothDevice localBluetoothDevice = (BluetoothDevice)((Iterator)localObject).next();
          SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
          localSearchIndexableRaw.title = localBluetoothDevice.getName();
          localSearchIndexableRaw.screenTitle = localResources.getString(2131691235);
          localSearchIndexableRaw.enabled = paramAnonymousBoolean;
          localArrayList.add(localSearchIndexableRaw);
        }
      }
      return localArrayList;
    }
  };
  private static final String SETTINGS_SYSTEM_BLUETOOTH_DEFAULT_SCAN_MODE = "bluetooth_default_scan_mode";
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY;
  private static final String TAG = "BluetoothSettings";
  private static View mSettingsDialogView = null;
  private PreferenceGroup mAvailableDevicesCategory;
  private boolean mAvailableDevicesCategoryIsPresent;
  private BluetoothEnabler mBluetoothEnabler;
  private int mBluetoothScanMode = 23;
  private final View.OnClickListener mDeviceProfilesListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!(paramAnonymousView.getTag() instanceof CachedBluetoothDevice))
      {
        Log.w("BluetoothSettings", "onClick() called for other View: " + paramAnonymousView);
        return;
      }
      Object localObject = (CachedBluetoothDevice)paramAnonymousView.getTag();
      paramAnonymousView = new Bundle();
      paramAnonymousView.putString("device_address", ((CachedBluetoothDevice)localObject).getDevice().getAddress());
      localObject = new DeviceProfilesSettings();
      ((DeviceProfilesSettings)localObject).setArguments(paramAnonymousView);
      ((DeviceProfilesSettings)localObject).show(BluetoothSettings.this.getFragmentManager(), DeviceProfilesSettings.class.getSimpleName());
    }
  };
  private SwitchPreference mDiscoverableBluetooth;
  private OPPreferenceDivider mDiscoverableBluetoothDivider;
  private boolean mInitialScanStarted;
  private boolean mInitiateDiscoverable;
  private final IntentFilter mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED");
  Preference mMyDevicePreference;
  private OPPreferenceDivider mPairedBluetoothDivider;
  private PreferenceGroup mPairedDevicesCategory;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    private void updateDeviceName(Context paramAnonymousContext)
    {
      if ((BluetoothSettings.this.mLocalAdapter.isEnabled()) && (BluetoothSettings.this.mMyDevicePreference != null))
      {
        paramAnonymousContext = paramAnonymousContext.getResources();
        BidiFormatter localBidiFormatter = BidiFormatter.getInstance(paramAnonymousContext.getConfiguration().getLocales().get(0));
        BluetoothSettings.this.mMyDevicePreference.setSummary(paramAnonymousContext.getString(2131690861, new Object[] { localBidiFormatter.unicodeWrap(BluetoothSettings.this.mLocalAdapter.getName()) }));
      }
    }
    
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousIntent.getAction();
      if (paramAnonymousIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) == 12) {
        BluetoothSettings.-set0(BluetoothSettings.this, true);
      }
    }
  };
  private SwitchBar mSwitchBar;
  
  static
  {
    SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
    {
      public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
      {
        return new BluetoothSettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
      }
    };
  }
  
  public BluetoothSettings()
  {
    super("no_config_bluetooth");
  }
  
  private void addDeviceCategory(PreferenceGroup paramPreferenceGroup, int paramInt, BluetoothDeviceFilter.Filter paramFilter, boolean paramBoolean)
  {
    cacheRemoveAllPrefs(paramPreferenceGroup);
    paramPreferenceGroup.setTitle(paramInt);
    setFilter(paramFilter);
    setDeviceListGroup(paramPreferenceGroup);
    if (paramBoolean) {
      addCachedDevices();
    }
    paramPreferenceGroup.setEnabled(true);
    removeCachedPrefs(paramPreferenceGroup);
  }
  
  private void resetBarSize(float paramFloat, int paramInt1, int paramInt2)
  {
    Object localObject1 = getActivity();
    DisplayMetrics localDisplayMetrics = Resources.getSystem().getDisplayMetrics();
    int i = Resources.getSystem().getIdentifier("action_bar", "id", "android");
    Toolbar localToolbar = (Toolbar)((Activity)localObject1).getWindow().findViewById(i);
    Object localObject2 = null;
    localObject1 = null;
    if (localToolbar != null)
    {
      localToolbar.getLayoutParams().height = ((int)TypedValue.applyDimension(1, paramInt1, localDisplayMetrics));
      paramInt1 = 0;
      for (;;)
      {
        localObject2 = localObject1;
        if (paramInt1 >= localToolbar.getChildCount()) {
          break;
        }
        if ((localToolbar.getChildAt(paramInt1) instanceof TextView)) {
          localObject1 = (TextView)localToolbar.getChildAt(paramInt1);
        }
        ((Toolbar.LayoutParams)localToolbar.getChildAt(paramInt1).getLayoutParams()).gravity = 16;
        paramInt1 += 1;
      }
    }
    if (localObject2 != null) {
      ((TextView)localObject2).setTextSize(1, paramFloat);
    }
    if (this.mSwitchBar != null) {
      this.mSwitchBar.getLayoutParams().height = ((int)TypedValue.applyDimension(1, paramInt2, localDisplayMetrics));
    }
  }
  
  private void saveScanModeToSettingsProvider(int paramInt)
  {
    Settings.System.putInt(getActivity().getContentResolver(), "bluetooth_default_scan_mode", paramInt);
  }
  
  private void setOffMessage()
  {
    int i = 1;
    TextView localTextView = getEmptyTextView();
    if (localTextView == null) {
      return;
    }
    CharSequence localCharSequence = getText(2131691276);
    if (Settings.Global.getInt(getActivity().getContentResolver(), "ble_scan_always_enabled", 0) == 1)
    {
      if (i != 0) {
        break label96;
      }
      localTextView.setText(localCharSequence, TextView.BufferType.SPANNABLE);
    }
    for (;;)
    {
      getPreferenceScreen().removeAll();
      ((Spannable)localTextView.getText()).setSpan(new TextAppearanceSpan(getActivity(), 16973892), 0, localCharSequence.length(), 33);
      return;
      i = 0;
      break;
      label96:
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(localCharSequence);
      localStringBuilder.append("\n\n");
      localStringBuilder.append(getText(2131691277));
      LinkifyUtils.linkify(localTextView, localStringBuilder, new LinkifyUtils.OnClickListener()
      {
        public void onClick()
        {
          ((SettingsActivity)BluetoothSettings.this.getActivity()).startPreferencePanel(ScanningSettings.class.getName(), null, 2131691960, null, null, 0);
        }
      });
    }
  }
  
  private void startScanning()
  {
    if (isUiRestricted())
    {
      Log.i("BluetoothSettings", "startScanning isUiRestricted() = " + isUiRestricted());
      return;
    }
    if (!this.mAvailableDevicesCategoryIsPresent)
    {
      getPreferenceScreen().addPreference(this.mAvailableDevicesCategory);
      this.mAvailableDevicesCategoryIsPresent = true;
    }
    if (this.mAvailableDevicesCategory != null)
    {
      setDeviceListGroup(this.mAvailableDevicesCategory);
      removeAllDevices();
    }
    this.mLocalManager.getCachedDeviceManager().clearNonBondedDevices();
    if (this.mAvailableDevicesCategory != null) {
      this.mAvailableDevicesCategory.removeAll();
    }
    for (;;)
    {
      this.mInitialScanStarted = true;
      this.mLocalAdapter.startScanning(true);
      update();
      return;
      Log.e("BluetoothSettings", "mAvailableDevicesCategory is null.");
    }
  }
  
  private void update()
  {
    this.mBluetoothScanMode = Settings.System.getInt(getActivity().getContentResolver(), "bluetooth_default_scan_mode", 23);
    if (this.mBluetoothScanMode == 23)
    {
      this.mLocalAdapter.setScanMode(23);
      this.mDiscoverableBluetooth.setSummary(2131690844);
      this.mDiscoverableBluetooth.setChecked(true);
    }
    while (this.mBluetoothScanMode != 21) {
      return;
    }
    this.mLocalAdapter.setScanMode(21);
    this.mDiscoverableBluetooth.setSummary(2131690845);
    this.mDiscoverableBluetooth.setChecked(false);
  }
  
  private void updateContent(int paramInt)
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    int i = 0;
    switch (paramInt)
    {
    default: 
      paramInt = i;
    }
    for (;;)
    {
      setDeviceListGroup(localPreferenceScreen);
      removeAllDevices();
      if (paramInt != 0) {
        getEmptyTextView().setText(paramInt);
      }
      if (!isUiRestricted()) {
        getActivity().invalidateOptionsMenu();
      }
      return;
      String str = Settings.System.getString(getActivity().getContentResolver(), "oem_oneplus_devicename");
      if (str != null) {
        this.mLocalAdapter.setName(str);
      }
      this.mDevicePreferenceMap.clear();
      if (isUiRestricted())
      {
        paramInt = 2131690860;
      }
      else
      {
        getPreferenceScreen().removeAll();
        getPreferenceScreen().addPreference(this.mPairedDevicesCategory);
        localPreferenceScreen.addPreference(this.mPairedBluetoothDivider);
        getPreferenceScreen().addPreference(this.mAvailableDevicesCategory);
        getPreferenceScreen().addPreference(this.mMyDevicePreference);
        addDeviceCategory(this.mPairedDevicesCategory, 2131691265, BluetoothDeviceFilter.BONDED_DEVICE_FILTER, true);
        paramInt = this.mPairedDevicesCategory.getPreferenceCount();
        if ((isUiRestricted()) || (paramInt <= 0)) {
          if (localPreferenceScreen.findPreference("paired_devices") != null)
          {
            localPreferenceScreen.removePreference(this.mPairedDevicesCategory);
            localPreferenceScreen.removePreference(this.mPairedBluetoothDivider);
          }
        }
        for (;;)
        {
          localPreferenceScreen.addPreference(this.mDiscoverableBluetooth);
          localPreferenceScreen.addPreference(this.mDiscoverableBluetoothDivider);
          addDeviceCategory(this.mAvailableDevicesCategory, 2131691266, BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER, this.mInitialScanStarted);
          if (!this.mInitialScanStarted) {
            startScanning();
          }
          BidiFormatter.getInstance(getResources().getConfiguration().getLocales().get(0));
          getActivity().invalidateOptionsMenu();
          if (this.mInitiateDiscoverable) {
            this.mInitiateDiscoverable = false;
          }
          return;
          if (localPreferenceScreen.findPreference("paired_devices") == null)
          {
            localPreferenceScreen.addPreference(this.mPairedDevicesCategory);
            localPreferenceScreen.addPreference(this.mPairedBluetoothDivider);
          }
        }
        paramInt = 2131690885;
        continue;
        this.mDevicePreferenceMap.clear();
        cancelRunnable();
        setOffMessage();
        paramInt = i;
        if (isUiRestricted())
        {
          paramInt = 2131690860;
          continue;
          paramInt = 2131690884;
          this.mInitialScanStarted = false;
        }
      }
    }
  }
  
  void addPreferencesForActivity()
  {
    addPreferencesFromResource(2131230744);
    this.mDiscoverableBluetoothDivider = new OPPreferenceDivider(getPrefContext());
    this.mDiscoverableBluetoothDivider.setOrder(1);
    this.mPairedDevicesCategory = new PreferenceCategory(getPrefContext());
    this.mPairedDevicesCategory.setKey("paired_devices");
    this.mPairedDevicesCategory.setOrder(2);
    getPreferenceScreen().addPreference(this.mPairedDevicesCategory);
    this.mPairedBluetoothDivider = new OPPreferenceDivider(getPrefContext());
    this.mPairedBluetoothDivider.setOrder(3);
    this.mAvailableDevicesCategory = new BluetoothProgressCategory(getActivity());
    this.mAvailableDevicesCategory.setSelectable(false);
    this.mAvailableDevicesCategory.setOrder(4);
    getPreferenceScreen().addPreference(this.mAvailableDevicesCategory);
    this.mMyDevicePreference = new Preference(getPrefContext());
    this.mMyDevicePreference.setSelectable(false);
    this.mMyDevicePreference.setOrder(5);
    getPreferenceScreen().addPreference(this.mMyDevicePreference);
    setHasOptionsMenu(true);
  }
  
  protected int getHelpResource()
  {
    return 2131693012;
  }
  
  protected int getMetricsCategory()
  {
    return 24;
  }
  
  void initDevicePreference(BluetoothDevicePreference paramBluetoothDevicePreference)
  {
    if (paramBluetoothDevicePreference.getCachedDevice().getBondState() == 12) {
      paramBluetoothDevicePreference.setOnSettingsClickListener(this.mDeviceProfilesListener);
    }
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (paramBundle != null) {}
    for (boolean bool = true;; bool = false)
    {
      this.mInitialScanStarted = bool;
      this.mInitiateDiscoverable = true;
      this.mDiscoverableBluetooth = new SwitchPreference(getActivity());
      this.mDiscoverableBluetooth.setTitle(getResources().getString(2131690265));
      this.mDiscoverableBluetooth.setOrder(0);
      this.mDiscoverableBluetooth.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          if (!BluetoothSettings.-get0(BluetoothSettings.this).isChecked())
          {
            BluetoothSettings.this.mLocalAdapter.setScanMode(23);
            BluetoothSettings.this.mLocalAdapter.setDiscoverableTimeout(120);
            BluetoothSettings.-get0(BluetoothSettings.this).setSummary(2131690844);
            BluetoothSettings.-wrap0(BluetoothSettings.this, 23);
          }
          for (;;)
          {
            return true;
            BluetoothSettings.this.mLocalAdapter.setScanMode(21);
            BluetoothSettings.-get0(BluetoothSettings.this).setSummary(2131690845);
            BluetoothSettings.-wrap0(BluetoothSettings.this, 21);
          }
        }
      });
      paramBundle = (SettingsActivity)getActivity();
      this.mSwitchBar = paramBundle.getSwitchBar();
      this.mBluetoothEnabler = new BluetoothEnabler(paramBundle, this.mSwitchBar);
      this.mBluetoothEnabler.setupSwitchBar();
      return;
    }
  }
  
  public void onBluetoothStateChanged(int paramInt)
  {
    super.onBluetoothStateChanged(paramInt);
    if (12 == paramInt) {
      this.mInitiateDiscoverable = true;
    }
    updateContent(paramInt);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    Activity localActivity = getActivity();
    float f;
    int j;
    if (paramConfiguration.orientation == 2)
    {
      f = localActivity.getResources().getDimensionPixelSize(2131755559);
      j = localActivity.getResources().getDimensionPixelSize(2131755563);
    }
    for (int i = localActivity.getResources().getDimensionPixelSize(2131755561);; i = localActivity.getResources().getDimensionPixelSize(2131755564))
    {
      resetBarSize(f, i, j);
      return;
      f = localActivity.getResources().getDimensionPixelSize(2131755560);
      j = localActivity.getResources().getDimensionPixelSize(2131755564);
    }
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    boolean bool2 = true;
    if (this.mLocalAdapter == null) {
      return;
    }
    if (isUiRestricted()) {
      return;
    }
    boolean bool1;
    boolean bool3;
    if (this.mLocalAdapter.getBluetoothState() == 12)
    {
      bool1 = true;
      bool3 = this.mLocalAdapter.isDiscovering();
      if (!bool3) {
        break label148;
      }
    }
    label148:
    for (int i = 2131691254;; i = 2131691253)
    {
      MenuItem localMenuItem = paramMenu.add(0, 1, 0, i);
      if ((!bool1) || (bool3)) {
        bool2 = false;
      }
      localMenuItem.setEnabled(bool2).setShowAsAction(0);
      paramMenu.add(0, 2, 0, 2131690856).setEnabled(bool1).setShowAsAction(0);
      paramMenu.add(0, 3, 0, 2131690873).setShowAsAction(0);
      super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
      return;
      bool1 = false;
      break;
    }
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    this.mBluetoothEnabler.teardownSwitchBar();
  }
  
  public void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt)
  {
    setDeviceListGroup(getPreferenceScreen());
    removeAllDevices();
    updateContent(this.mLocalAdapter.getBluetoothState());
  }
  
  void onDevicePreferenceClick(BluetoothDevicePreference paramBluetoothDevicePreference)
  {
    this.mLocalAdapter.stopScanning();
    super.onDevicePreferenceClick(paramBluetoothDevicePreference);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 1: 
      if (this.mLocalAdapter.getBluetoothState() == 12)
      {
        MetricsLogger.action(getActivity(), 160);
        startScanning();
      }
      return true;
    case 2: 
      startFragment(this, "com.oneplus.settings.OPDeviceName", 2131690256, -1, null);
      return true;
    }
    MetricsLogger.action(getActivity(), 162);
    paramMenuItem = new Intent("android.btopp.intent.action.OPEN_RECEIVED_FILES");
    getActivity().sendBroadcast(paramMenuItem);
    return true;
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.mBluetoothEnabler != null) {
      this.mBluetoothEnabler.pause();
    }
    if (isUiRestricted()) {
      return;
    }
    getActivity().unregisterReceiver(this.mReceiver);
  }
  
  public void onResume()
  {
    if (this.mBluetoothEnabler != null) {
      this.mBluetoothEnabler.resume(getActivity());
    }
    update();
    super.onResume();
    this.mInitiateDiscoverable = true;
    if (isUiRestricted())
    {
      setDeviceListGroup(getPreferenceScreen());
      if (!isUiRestrictedByOnlyAdmin()) {
        getEmptyTextView().setText(2131690860);
      }
      removeAllDevices();
      return;
    }
    getActivity().registerReceiver(this.mReceiver, this.mIntentFilter);
    if (this.mLocalAdapter != null) {
      updateContent(this.mLocalAdapter.getBluetoothState());
    }
  }
  
  public void onScanningStateChanged(boolean paramBoolean)
  {
    super.onScanningStateChanged(paramBoolean);
    if (getActivity() != null) {
      getActivity().invalidateOptionsMenu();
    }
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider, BluetoothCallback
  {
    private final LocalBluetoothManager mBluetoothManager;
    private boolean mConnected;
    private final Context mContext;
    private boolean mEnabled;
    private final SummaryLoader mSummaryLoader;
    
    public SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mBluetoothManager = Utils.getLocalBtManager(paramContext);
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
    }
    
    private CharSequence getSummary()
    {
      Context localContext = this.mContext;
      int i;
      if (!this.mEnabled) {
        i = 2131693715;
      }
      for (;;)
      {
        return localContext.getString(i);
        if (this.mConnected) {
          i = 2131689532;
        } else {
          i = 2131689529;
        }
      }
    }
    
    public void onBluetoothStateChanged(int paramInt)
    {
      if (paramInt == 12) {}
      for (boolean bool = true;; bool = false)
      {
        this.mEnabled = bool;
        this.mSummaryLoader.setSummary(this, getSummary());
        return;
      }
    }
    
    public void onConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt)
    {
      if (paramInt == 2) {}
      for (boolean bool = true;; bool = false)
      {
        this.mConnected = bool;
        this.mSummaryLoader.setSummary(this, getSummary());
        return;
      }
    }
    
    public void onDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice) {}
    
    public void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt) {}
    
    public void onDeviceDeleted(CachedBluetoothDevice paramCachedBluetoothDevice) {}
    
    public void onScanningStateChanged(boolean paramBoolean) {}
    
    public void setListening(boolean paramBoolean)
    {
      BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      if (localBluetoothAdapter == null) {
        return;
      }
      if (paramBoolean)
      {
        this.mEnabled = localBluetoothAdapter.isEnabled();
        if (localBluetoothAdapter.getConnectionState() == 2) {}
        for (paramBoolean = true;; paramBoolean = false)
        {
          this.mConnected = paramBoolean;
          this.mSummaryLoader.setSummary(this, getSummary());
          this.mBluetoothManager.getEventManager().registerCallback(this);
          return;
        }
      }
      this.mBluetoothManager.getEventManager().unregisterCallback(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\BluetoothSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */