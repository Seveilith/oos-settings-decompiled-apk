package com.android.settings.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.ActionListener;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.HandlerThread;
import android.provider.Settings.Global;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.Spannable;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.LinkifyUtils;
import com.android.settings.LinkifyUtils.OnClickListener;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.Settings.WifiP2pSettingsActivity;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.location.ScanningSettings;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPoint.AccessPointListener;
import com.android.settingslib.wifi.AccessPointPreference.UserBadgeCache;
import com.android.settingslib.wifi.WifiStatusTracker;
import com.android.settingslib.wifi.WifiTracker;
import com.android.settingslib.wifi.WifiTracker.WifiListener;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPPreferenceHeaderMargin;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WifiSettings
  extends RestrictedSettingsFragment
  implements Indexable, WifiTracker.WifiListener, AccessPoint.AccessPointListener, WifiDialog.WifiDialogListener
{
  private static final String EXTRA_ENABLE_NEXT_ON_CONNECT = "wifi_enable_next_on_connect";
  private static final String EXTRA_START_CONNECT_SSID = "wifi_start_connect_ssid";
  private static final int MENU_ID_ADVANCED = 5;
  private static final int MENU_ID_CONFIGURE = 11;
  private static final int MENU_ID_CONNECT = 7;
  private static final int MENU_ID_DISCONNECT = 12;
  private static final int MENU_ID_FORGET = 8;
  private static final int MENU_ID_MODIFY = 9;
  private static final int MENU_ID_P2P = 13;
  private static final int MENU_ID_SCAN = 6;
  static final int MENU_ID_WPS_PBC = 1;
  private static final int MENU_ID_WPS_PIN = 2;
  private static final int MENU_ID_WRITE_NFC = 10;
  private static final String SAVED_WIFI_NFC_DIALOG_STATE = "wifi_nfc_dlg_state";
  private static final String SAVE_DIALOG_ACCESS_POINT_STATE = "wifi_ap_state";
  private static final String SAVE_DIALOG_MODE = "dialog_mode";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      Resources localResources = paramAnonymousContext.getResources();
      Object localObject = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject).title = localResources.getString(2131691332);
      ((SearchIndexableRaw)localObject).screenTitle = localResources.getString(2131691332);
      ((SearchIndexableRaw)localObject).keywords = localResources.getString(2131693121);
      localArrayList.add(localObject);
      localObject = WifiTracker.getCurrentAccessPoints(paramAnonymousContext, true, false, false).iterator();
      while (((Iterator)localObject).hasNext())
      {
        AccessPoint localAccessPoint = (AccessPoint)((Iterator)localObject).next();
        SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
        localSearchIndexableRaw.title = localAccessPoint.getSsidStr();
        localSearchIndexableRaw.screenTitle = localResources.getString(2131691332);
        localSearchIndexableRaw.enabled = paramAnonymousBoolean;
        localArrayList.add(localSearchIndexableRaw);
      }
      return localArrayList;
    }
  };
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new WifiSettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
    }
  };
  private static final String TAG = "WifiSettings";
  public static final int WIFI_DIALOG_ID = 1;
  static final int WPS_PBC_DIALOG_ID = 2;
  private static final int WPS_PIN_DIALOG_ID = 3;
  private static final int WRITE_NFC_DIALOG_ID = 6;
  private Bundle mAccessPointSavedState;
  private Preference mAddPreference;
  private HandlerThread mBgThread;
  private WifiManager.ActionListener mConnectListener;
  private WifiDialog mDialog;
  private int mDialogMode;
  private AccessPoint mDlgAccessPoint;
  private boolean mEnableNextOnConnection;
  private WifiManager.ActionListener mForgetListener;
  private Field mListener = null;
  private Object mListenerObject = null;
  private String mOpenSsid;
  private ProgressBar mProgressHeader;
  private WifiManager.ActionListener mSaveListener;
  private MenuItem mScanMenuItem;
  private AccessPoint mSelectedAccessPoint;
  private AccessPointPreference.UserBadgeCache mUserBadgeCache;
  private WifiEnabler mWifiEnabler;
  protected WifiManager mWifiManager;
  private Bundle mWifiNfcDialogSavedState;
  private MenuItem mWifiP2PMenuItem;
  private WriteWifiConfigToNfcDialog mWifiToNfcDialog;
  private WifiTracker mWifiTracker;
  private AlertDialog mWifiWapiCertNotinstalledDialog;
  
  public WifiSettings()
  {
    super("no_config_wifi");
  }
  
  private void addMessagePreference(int paramInt)
  {
    TextView localTextView = getEmptyTextView();
    if (localTextView != null) {
      localTextView.setText(paramInt);
    }
    getPreferenceScreen().removeAll();
  }
  
  static boolean canModifyNetwork(Context paramContext, WifiConfiguration paramWifiConfiguration)
  {
    if (paramWifiConfiguration == null) {
      return true;
    }
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    PackageManager localPackageManager = paramContext.getPackageManager();
    if ((localPackageManager.hasSystemFeature("android.software.device_admin")) && (localDevicePolicyManager == null)) {
      return false;
    }
    j = 0;
    i = j;
    ComponentName localComponentName;
    if (localDevicePolicyManager != null)
    {
      localComponentName = localDevicePolicyManager.getDeviceOwnerComponentOnAnyUser();
      i = j;
      if (localComponentName == null) {}
    }
    for (i = localDevicePolicyManager.getDeviceOwnerUserId();; i = 0)
    {
      try
      {
        i = localPackageManager.getPackageUidAsUser(localComponentName.getPackageName(), i);
        int k = paramWifiConfiguration.creatorUid;
        if (i != k) {
          continue;
        }
        i = 1;
      }
      catch (PackageManager.NameNotFoundException paramWifiConfiguration)
      {
        for (;;)
        {
          i = j;
        }
      }
      if (i != 0) {
        break;
      }
      return true;
    }
    if (Settings.Global.getInt(paramContext.getContentResolver(), "wifi_device_owner_configs_lockdown", 0) != 0) {}
    for (i = 1; i != 0; i = 0) {
      return false;
    }
    return true;
  }
  
  private void changeNextButtonState(boolean paramBoolean)
  {
    if ((this.mEnableNextOnConnection) && (hasNextButton())) {
      getNextButton().setEnabled(paramBoolean);
    }
  }
  
  private void initPreferenceListener()
  {
    try
    {
      this.mListener = PreferenceScreen.class.getSuperclass().getSuperclass().getDeclaredField("mListener");
      this.mListener.setAccessible(true);
      this.mListenerObject = this.mListener.get(getPreferenceScreen());
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  static boolean isEditabilityLockedDown(Context paramContext, WifiConfiguration paramWifiConfiguration)
  {
    return !canModifyNetwork(paramContext, paramWifiConfiguration);
  }
  
  private boolean isPasspointWifi(AccessPoint paramAccessPoint)
  {
    boolean bool2 = false;
    if ((paramAccessPoint != null) && (paramAccessPoint.getConfig() != null))
    {
      paramAccessPoint = paramAccessPoint.getConfig().enterpriseConfig;
      boolean bool1 = bool2;
      if (paramAccessPoint != null)
      {
        bool1 = bool2;
        if (paramAccessPoint.getEapMethod() != -1) {
          bool1 = true;
        }
      }
      return bool1;
    }
    return false;
  }
  
  private void releaseAccessPointListener()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    int i = localPreferenceScreen.getPreferenceCount() - 1;
    while (i >= 0)
    {
      Object localObject = localPreferenceScreen.getPreference(i);
      if ((localObject instanceof LongPressAccessPointPreference))
      {
        localObject = ((LongPressAccessPointPreference)localObject).getAccessPoint();
        if (localObject != null) {
          ((AccessPoint)localObject).setListener(null);
        }
      }
      i -= 1;
    }
  }
  
  private void resetPreferenceListener()
  {
    if ((this.mListener != null) && (this.mListenerObject != null)) {}
    try
    {
      this.mListener.set(getPreferenceScreen(), this.mListenerObject);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void setOffMessage()
  {
    int i = 1;
    if (isUiRestricted())
    {
      if (!isUiRestrictedByOnlyAdmin()) {
        addMessagePreference(2131691381);
      }
      getPreferenceScreen().removeAll();
      return;
    }
    TextView localTextView = getEmptyTextView();
    if (localTextView == null) {
      return;
    }
    Object localObject = getActivity();
    if ((localObject == null) || (((Activity)localObject).isFinishing())) {
      return;
    }
    localObject = getText(2131691379);
    if (Settings.Global.getInt(getActivity().getContentResolver(), "wifi_scan_always_enabled", 0) == 1)
    {
      if (i != 0) {
        break label144;
      }
      localTextView.setText((CharSequence)localObject, TextView.BufferType.SPANNABLE);
    }
    for (;;)
    {
      ((Spannable)localTextView.getText()).setSpan(new TextAppearanceSpan(getActivity(), 16973892), 0, ((CharSequence)localObject).length(), 33);
      getPreferenceScreen().removeAll();
      return;
      i = 0;
      break;
      label144:
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append((CharSequence)localObject);
      localStringBuilder.append("\n\n");
      localStringBuilder.append(getText(2131691353));
      LinkifyUtils.linkify(localTextView, localStringBuilder, new LinkifyUtils.OnClickListener()
      {
        public void onClick()
        {
          ((SettingsActivity)WifiSettings.this.getActivity()).startPreferencePanel(ScanningSettings.class.getName(), null, 2131691960, null, null, 0);
        }
      });
    }
  }
  
  private void setPreferenceListenerNull()
  {
    if (this.mListener == null) {
      initPreferenceListener();
    }
    if (this.mListener != null) {}
    try
    {
      this.mListener.set(getPreferenceScreen(), null);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void showDialog(AccessPoint paramAccessPoint, int paramInt)
  {
    if (paramAccessPoint != null)
    {
      WifiConfiguration localWifiConfiguration = paramAccessPoint.getConfig();
      if ((isEditabilityLockedDown(getActivity(), localWifiConfiguration)) && (paramAccessPoint.isActive()))
      {
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), RestrictedLockUtils.getDeviceOwner(getActivity()));
        return;
      }
    }
    if (this.mDialog != null)
    {
      removeDialog(1);
      this.mDialog = null;
    }
    this.mDlgAccessPoint = paramAccessPoint;
    this.mDialogMode = paramInt;
    showDialog(1);
  }
  
  void addOptionsMenuItems(Menu paramMenu)
  {
    boolean bool = this.mWifiTracker.isWifiEnabled();
    this.mScanMenuItem = paramMenu.add(0, 6, 0, 2131692515);
    this.mScanMenuItem.setEnabled(bool).setShowAsAction(0);
    this.mWifiP2PMenuItem = paramMenu.add(0, 13, 0, 2131691370);
    this.mWifiP2PMenuItem.setEnabled(bool).setShowAsAction(0);
    paramMenu.add(0, 5, 0, 2131691372).setShowAsAction(0);
    paramMenu.add(0, 11, 0, 2131691373).setIcon(2130838027).setShowAsAction(1);
  }
  
  protected void connect(int paramInt)
  {
    MetricsLogger.action(getActivity(), 135);
    this.mWifiManager.connect(paramInt, this.mConnectListener);
  }
  
  protected void connect(WifiConfiguration paramWifiConfiguration)
  {
    MetricsLogger.action(getActivity(), 135);
    this.mWifiManager.connect(paramWifiConfiguration, this.mConnectListener);
  }
  
  WifiEnabler createWifiEnabler()
  {
    SettingsActivity localSettingsActivity = (SettingsActivity)getActivity();
    return new WifiEnabler(localSettingsActivity, localSettingsActivity.getSwitchBar());
  }
  
  void forget()
  {
    MetricsLogger.action(getActivity(), 137);
    if (!this.mSelectedAccessPoint.isSaved()) {
      if ((this.mSelectedAccessPoint.getNetworkInfo() != null) && (this.mSelectedAccessPoint.getNetworkInfo().getState() != NetworkInfo.State.DISCONNECTED)) {
        this.mWifiManager.disableEphemeralNetwork(AccessPoint.convertToQuotedString(this.mSelectedAccessPoint.getSsidStr()));
      }
    }
    for (;;)
    {
      this.mWifiTracker.resumeScanning();
      changeNextButtonState(false);
      return;
      Log.e("WifiSettings", "Failed to forget invalid network " + this.mSelectedAccessPoint.getConfig());
      return;
      this.mWifiManager.forget(this.mSelectedAccessPoint.getConfig().networkId, this.mForgetListener);
    }
  }
  
  protected int getHelpResource()
  {
    return 2131693011;
  }
  
  protected int getMetricsCategory()
  {
    return 103;
  }
  
  public void onAccessPointChanged(AccessPoint paramAccessPoint)
  {
    ((LongPressAccessPointPreference)paramAccessPoint.getTag()).refresh();
  }
  
  public void onAccessPointsChanged()
  {
    try
    {
      localObject1 = getActivity();
      if (localObject1 == null) {
        return;
      }
      if (isUiRestricted())
      {
        if (!isUiRestrictedByOnlyAdmin()) {
          addMessagePreference(2131691381);
        }
        Log.w("WifiSettings", "onAccessPointsChanged isUiRestricted ()");
        getPreferenceScreen().removeAll();
        return;
      }
      i = this.mWifiManager.getWifiState();
      switch (i)
      {
      default: 
        return;
      }
    }
    finally {}
    Object localObject1 = this.mWifiTracker.getAccessPoints();
    getPreferenceScreen().removeAll();
    Object localObject3 = new OPPreferenceHeaderMargin(SettingsBaseApplication.mApplication);
    ((OPPreferenceHeaderMargin)localObject3).setOrder(-1);
    getPreferenceScreen().addPreference((Preference)localObject3);
    setPreferenceListenerNull();
    int j = 0;
    cacheRemoveAllPrefs(getPreferenceScreen());
    localObject1 = ((Iterable)localObject1).iterator();
    int i = 0;
    label650:
    for (;;)
    {
      if (((Iterator)localObject1).hasNext())
      {
        localObject3 = (AccessPoint)((Iterator)localObject1).next();
        if (((AccessPoint)localObject3).getLevel() != -1)
        {
          if ((!((AccessPoint)localObject3).isSaved()) || (((AccessPoint)localObject3).foundInScanResult)) {}
          String str;
          while (((AccessPoint)localObject3).getDetailedState() != null)
          {
            str = ((AccessPoint)localObject3).getBssid();
            j = 1;
            localLongPressAccessPointPreference = (LongPressAccessPointPreference)getCachedPreference(str);
            if (localLongPressAccessPointPreference == null) {
              break;
            }
            k = i + 1;
            localLongPressAccessPointPreference.setOrder(i);
            i = k;
            break label650;
          }
          break label650;
          LongPressAccessPointPreference localLongPressAccessPointPreference = new LongPressAccessPointPreference((AccessPoint)localObject3, getPrefContext(), this.mUserBadgeCache, false, this);
          localLongPressAccessPointPreference.setKey(str);
          int k = i + 1;
          localLongPressAccessPointPreference.setOrder(i);
          if ((this.mOpenSsid == null) || (!this.mOpenSsid.equals(((AccessPoint)localObject3).getSsidStr())) || (((AccessPoint)localObject3).isSaved())) {}
          for (;;)
          {
            if (localLongPressAccessPointPreference != null) {
              Log.w("WifiSettings", "preference = " + localLongPressAccessPointPreference.toString());
            }
            getPreferenceScreen().addPreference(localLongPressAccessPointPreference);
            ((AccessPoint)localObject3).setListener(this);
            i = k;
            break;
            if (((AccessPoint)localObject3).getSecurity() != 0)
            {
              onPreferenceTreeClick(localLongPressAccessPointPreference);
              this.mOpenSsid = null;
            }
          }
        }
      }
      else
      {
        resetPreferenceListener();
        removeCachedPrefs(getPreferenceScreen());
        if (j == 0)
        {
          setProgressBarVisible(true);
          Preference local6 = new Preference(getContext())
          {
            public void onBindViewHolder(PreferenceViewHolder paramAnonymousPreferenceViewHolder)
            {
              super.onBindViewHolder(paramAnonymousPreferenceViewHolder);
              paramAnonymousPreferenceViewHolder.setDividerAllowedBelow(true);
            }
          };
          local6.setSelectable(false);
          local6.setSummary(2131691380);
          local6.setOrder(0);
          getPreferenceScreen().addPreference(local6);
          this.mAddPreference.setOrder(1);
          getPreferenceScreen().addPreference(this.mAddPreference);
        }
        for (;;)
        {
          if (this.mScanMenuItem != null) {
            this.mScanMenuItem.setEnabled(true);
          }
          if (this.mWifiP2PMenuItem == null) {
            break;
          }
          this.mWifiP2PMenuItem.setEnabled(true);
          break;
          this.mAddPreference.setOrder(i);
          getPreferenceScreen().addPreference(this.mAddPreference);
          setProgressBarVisible(false);
        }
        getPreferenceScreen().removeAll();
        setProgressBarVisible(true);
        break;
        addMessagePreference(2131691339);
        setProgressBarVisible(true);
        break;
        setOffMessage();
        setProgressBarVisible(false);
        if (this.mScanMenuItem != null) {
          this.mScanMenuItem.setEnabled(false);
        }
        if (this.mWifiP2PMenuItem == null) {
          break;
        }
        this.mWifiP2PMenuItem.setEnabled(false);
        break;
      }
    }
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mWifiTracker = new WifiTracker(getActivity(), this, this.mBgThread.getLooper(), true, true, false);
    this.mWifiManager = this.mWifiTracker.getManager();
    this.mConnectListener = new WifiManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Activity localActivity = WifiSettings.this.getActivity();
        if (localActivity != null) {
          Toast.makeText(localActivity, 2131691450, 0).show();
        }
      }
      
      public void onSuccess() {}
    };
    this.mSaveListener = new WifiManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Activity localActivity = WifiSettings.this.getActivity();
        if (localActivity != null) {
          Toast.makeText(localActivity, 2131691454, 0).show();
        }
      }
      
      public void onSuccess() {}
    };
    this.mForgetListener = new WifiManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Activity localActivity = WifiSettings.this.getActivity();
        if (localActivity != null) {
          Toast.makeText(localActivity, 2131691452, 0).show();
        }
      }
      
      public void onSuccess() {}
    };
    if (paramBundle != null)
    {
      this.mDialogMode = paramBundle.getInt("dialog_mode");
      if (paramBundle.containsKey("wifi_ap_state")) {
        this.mAccessPointSavedState = paramBundle.getBundle("wifi_ap_state");
      }
      if (paramBundle.containsKey("wifi_nfc_dlg_state")) {
        this.mWifiNfcDialogSavedState = paramBundle.getBundle("wifi_nfc_dlg_state");
      }
    }
    paramBundle = getActivity().getIntent();
    this.mEnableNextOnConnection = paramBundle.getBooleanExtra("wifi_enable_next_on_connect", false);
    if ((this.mEnableNextOnConnection) && (hasNextButton()))
    {
      ConnectivityManager localConnectivityManager = (ConnectivityManager)getActivity().getSystemService("connectivity");
      if (localConnectivityManager != null) {
        changeNextButtonState(localConnectivityManager.getNetworkInfo(1).isConnected());
      }
    }
    registerForContextMenu(getListView());
    setHasOptionsMenu(true);
    if (paramBundle != null) {
      Log.w("WifiSettings", "intent = " + paramBundle.getAction());
    }
    if (paramBundle.hasExtra("wifi_start_connect_ssid"))
    {
      Log.w("WifiSettings", "Received intent = " + paramBundle.getAction());
      this.mOpenSsid = paramBundle.getStringExtra("wifi_start_connect_ssid");
      onAccessPointsChanged();
    }
  }
  
  void onAddNetworkPressed()
  {
    MetricsLogger.action(getActivity(), 134);
    this.mSelectedAccessPoint = null;
    showDialog(null, 1);
  }
  
  public void onConnectedChanged()
  {
    changeNextButtonState(this.mWifiTracker.isConnected());
  }
  
  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    if (this.mSelectedAccessPoint == null) {
      return super.onContextItemSelected(paramMenuItem);
    }
    switch (paramMenuItem.getItemId())
    {
    case 11: 
    default: 
      return super.onContextItemSelected(paramMenuItem);
    case 7: 
      if (this.mSelectedAccessPoint.isSaved())
      {
        connect(this.mSelectedAccessPoint.getConfig());
        return true;
      }
      if (this.mSelectedAccessPoint.getSecurity() == 0)
      {
        this.mSelectedAccessPoint.generateOpenNetworkConfig();
        connect(this.mSelectedAccessPoint.getConfig());
        return true;
      }
      showDialog(this.mSelectedAccessPoint, 1);
      return true;
    case 8: 
      forget();
      return true;
    case 9: 
      showDialog(this.mSelectedAccessPoint, 2);
      return true;
    case 10: 
      showDialog(6);
      return true;
    }
    this.mWifiManager.disconnect();
    return true;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230890);
    this.mAddPreference = new Preference(getContext());
    this.mAddPreference.setIcon(2130838000);
    this.mAddPreference.setTitle(2131691365);
    this.mUserBadgeCache = new AccessPointPreference.UserBadgeCache(getPackageManager());
    this.mBgThread = new HandlerThread("WifiSettings", 10);
    this.mBgThread.start();
  }
  
  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
  {
    paramView = (Preference)paramView.getTag();
    if ((paramView instanceof LongPressAccessPointPreference))
    {
      this.mSelectedAccessPoint = ((LongPressAccessPointPreference)paramView).getAccessPoint();
      paramContextMenu.setHeaderTitle(this.mSelectedAccessPoint.getSsid());
      if (this.mSelectedAccessPoint.isConnectable()) {
        paramContextMenu.add(0, 7, 0, 2131691374);
      }
      paramView = this.mSelectedAccessPoint.getConfig();
      if (isEditabilityLockedDown(getActivity(), paramView)) {
        return;
      }
      if ((this.mSelectedAccessPoint.isSaved()) || (this.mSelectedAccessPoint.isEphemeral())) {
        paramContextMenu.add(0, 8, 0, 2131691376);
      }
      if ((getResources().getBoolean(2131558425)) && (this.mSelectedAccessPoint.isActive())) {
        paramContextMenu.add(0, 12, 0, 2131693757);
      }
      if (this.mSelectedAccessPoint.isSaved())
      {
        paramContextMenu.add(0, 9, 0, 2131691377);
        paramView = NfcAdapter.getDefaultAdapter(getActivity());
        if ((paramView != null) && (paramView.isEnabled()) && (this.mSelectedAccessPoint.getSecurity() != 0)) {
          paramContextMenu.add(0, 10, 0, 2131691378);
        }
      }
    }
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    boolean bool = true;
    switch (paramInt)
    {
    case 4: 
    case 5: 
    default: 
      return super.onCreateDialog(paramInt);
    case 1: 
      Object localObject2 = this.mDlgAccessPoint;
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = localObject2;
        if (this.mAccessPointSavedState != null)
        {
          localObject1 = new AccessPoint(getActivity(), this.mAccessPointSavedState);
          this.mDlgAccessPoint = ((AccessPoint)localObject1);
          this.mAccessPointSavedState = null;
        }
      }
      this.mSelectedAccessPoint = ((AccessPoint)localObject1);
      if (getResources().getBoolean(17957072))
      {
        if (localObject1 != null) {
          bool = isEditabilityLockedDown(getActivity(), ((AccessPoint)localObject1).getConfig());
        }
        if (!bool)
        {
          bool = isPasspointWifi((AccessPoint)localObject1);
          StringBuilder localStringBuilder = new StringBuilder().append("Passpoint hotspot ? ");
          if (!isPasspointWifi((AccessPoint)localObject1)) {
            break label226;
          }
          localObject2 = "yes";
          Log.d("WifiSettings", (String)localObject2);
          localObject2 = getActivity();
          if (bool) {
            localObject1 = null;
          }
        }
      }
      for (this.mDialog = new WifiDialog((Context)localObject2, this, (AccessPoint)localObject1, this.mDialogMode, false);; this.mDialog = new WifiDialog(getActivity(), this, (AccessPoint)localObject1, this.mDialogMode, false))
      {
        return this.mDialog;
        bool = true;
        break;
        localObject2 = "no";
        break label168;
      }
    case 2: 
      return new WpsDialog(getActivity(), 0);
    case 3: 
      label168:
      label226:
      return new WpsDialog(getActivity(), 1);
    }
    if (this.mSelectedAccessPoint != null) {
      this.mWifiToNfcDialog = new WriteWifiConfigToNfcDialog(getActivity(), this.mSelectedAccessPoint.getConfig().networkId, this.mSelectedAccessPoint.getSecurity(), this.mWifiManager);
    }
    for (;;)
    {
      return this.mWifiToNfcDialog;
      if (this.mWifiNfcDialogSavedState != null) {
        this.mWifiToNfcDialog = new WriteWifiConfigToNfcDialog(getActivity(), this.mWifiNfcDialogSavedState, this.mWifiManager);
      }
    }
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if (isUiRestricted()) {
      return;
    }
    addOptionsMenuItems(paramMenu);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public void onDestroy()
  {
    try
    {
      if ((this.mWifiWapiCertNotinstalledDialog != null) && (this.mWifiWapiCertNotinstalledDialog.isShowing()))
      {
        this.mWifiWapiCertNotinstalledDialog.dismiss();
        this.mWifiWapiCertNotinstalledDialog = null;
      }
      this.mBgThread.quit();
      releaseAccessPointListener();
      super.onDestroy();
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        localException.printStackTrace();
      }
    }
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    if (this.mWifiEnabler != null) {
      this.mWifiEnabler.teardownSwitchBar();
    }
  }
  
  public void onForget(WifiDialog paramWifiDialog)
  {
    forget();
  }
  
  public void onLevelChanged(AccessPoint paramAccessPoint)
  {
    ((LongPressAccessPointPreference)paramAccessPoint.getTag()).onLevelChanged();
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (isUiRestricted()) {
      return false;
    }
    switch (paramMenuItem.getItemId())
    {
    case 3: 
    case 4: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 12: 
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 1: 
      showDialog(2);
      return true;
    case 13: 
      paramMenuItem = new Intent(getActivity(), Settings.WifiP2pSettingsActivity.class);
      getActivity().startActivity(paramMenuItem);
      return true;
    case 2: 
      showDialog(3);
      return true;
    case 6: 
      MetricsLogger.action(getActivity(), 136);
      this.mWifiTracker.forceScan();
      return true;
    case 5: 
      if ((getActivity() instanceof SettingsActivity))
      {
        ((SettingsActivity)getActivity()).startPreferencePanel(AdvancedWifiSettings.class.getCanonicalName(), null, 2131691462, null, this, 0);
        return true;
      }
      startFragment(this, AdvancedWifiSettings.class.getCanonicalName(), 2131691462, -1, null);
      return true;
    }
    if ((getActivity() instanceof SettingsActivity))
    {
      ((SettingsActivity)getActivity()).startPreferencePanel(ConfigureWifiSettings.class.getCanonicalName(), null, 2131691463, null, this, 0);
      return true;
    }
    startFragment(this, ConfigureWifiSettings.class.getCanonicalName(), 2131691463, -1, null);
    return true;
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.mWifiEnabler != null) {
      this.mWifiEnabler.pause();
    }
    this.mWifiTracker.stopTracking();
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if ((paramPreference instanceof LongPressAccessPointPreference))
    {
      this.mSelectedAccessPoint = ((LongPressAccessPointPreference)paramPreference).getAccessPoint();
      if (this.mSelectedAccessPoint == null) {
        return false;
      }
      if ((this.mSelectedAccessPoint.getSecurity() != 0) || (this.mSelectedAccessPoint.isSaved())) {}
      while (this.mSelectedAccessPoint.isSaved())
      {
        showDialog(this.mSelectedAccessPoint, 0);
        return true;
        if (!this.mSelectedAccessPoint.isActive())
        {
          this.mSelectedAccessPoint.generateOpenNetworkConfig();
          connect(this.mSelectedAccessPoint.getConfig());
          return true;
        }
      }
      showDialog(this.mSelectedAccessPoint, 1);
      return true;
    }
    if (paramPreference == this.mAddPreference)
    {
      onAddNetworkPressed();
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onResume()
  {
    Activity localActivity = getActivity();
    super.onResume();
    removePreference("dummy");
    if (this.mWifiEnabler != null) {
      this.mWifiEnabler.resume(localActivity);
    }
    this.mWifiTracker.startTracking();
    localActivity.invalidateOptionsMenu();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if ((this.mDialog != null) && (this.mDialog.isShowing()))
    {
      paramBundle.putInt("dialog_mode", this.mDialogMode);
      if (this.mDlgAccessPoint != null)
      {
        this.mAccessPointSavedState = new Bundle();
        this.mDlgAccessPoint.saveWifiState(this.mAccessPointSavedState);
        paramBundle.putBundle("wifi_ap_state", this.mAccessPointSavedState);
      }
    }
    if ((this.mWifiToNfcDialog != null) && (this.mWifiToNfcDialog.isShowing()))
    {
      Bundle localBundle = new Bundle();
      this.mWifiToNfcDialog.saveState(localBundle);
      paramBundle.putBundle("wifi_nfc_dlg_state", localBundle);
    }
  }
  
  public void onStart()
  {
    super.onStart();
    this.mWifiEnabler = createWifiEnabler();
  }
  
  public void onSubmit(WifiDialog paramWifiDialog)
  {
    if (this.mDialog != null) {
      submit(this.mDialog.getController());
    }
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    if (getActivity() != null) {
      this.mProgressHeader = ((ProgressBar)setPinnedHeaderView(2130969119));
    }
  }
  
  public void onWifiStateChanged(int paramInt)
  {
    switch (paramInt)
    {
    }
    do
    {
      return;
      addMessagePreference(2131691338);
      setProgressBarVisible(true);
      return;
      setOffMessage();
      setProgressBarVisible(false);
      if (this.mScanMenuItem != null) {
        this.mScanMenuItem.setEnabled(false);
      }
    } while (this.mWifiP2PMenuItem == null);
    this.mWifiP2PMenuItem.setEnabled(false);
  }
  
  protected void setProgressBarVisible(boolean paramBoolean)
  {
    ProgressBar localProgressBar;
    if (this.mProgressHeader != null)
    {
      localProgressBar = this.mProgressHeader;
      if (!paramBoolean) {
        break label24;
      }
    }
    label24:
    for (int i = 0;; i = 8)
    {
      localProgressBar.setVisibility(i);
      return;
    }
  }
  
  void submit(WifiConfigController paramWifiConfigController)
  {
    WifiConfiguration localWifiConfiguration = paramWifiConfigController.getConfig();
    if (localWifiConfiguration == null) {
      if ((this.mSelectedAccessPoint != null) && (this.mSelectedAccessPoint.isSaved())) {
        connect(this.mSelectedAccessPoint.getConfig());
      }
    }
    for (;;)
    {
      this.mWifiTracker.resumeScanning();
      return;
      if ((this.mSelectedAccessPoint != null) && (this.mSelectedAccessPoint.getSecurity() == 5))
      {
        Log.e("WifiSettings", "WAPI: WAPI_CERT Selected");
        if (paramWifiConfigController.mCert_Cnt == 0)
        {
          this.mWifiWapiCertNotinstalledDialog = new AlertDialog.Builder(getActivity()).setTitle(2131691876).setIcon(17301543).setMessage(2131690105).setPositiveButton(17039370, null).create();
          this.mWifiWapiCertNotinstalledDialog.show();
          Log.e("WifiSettings", "WAPI: Certificates are not installed");
          Log.e("WifiSettings", "WAPI: configController.mCert_Cnt = " + paramWifiConfigController.mCert_Cnt);
          return;
        }
        if (paramWifiConfigController.getMode() == 2)
        {
          Log.e("WifiSettings", "WAPI CERT issue Save");
          this.mWifiManager.save(localWifiConfiguration, this.mSaveListener);
        }
        else
        {
          Log.e("WifiSettings", "WAPI CERT issue Connect");
          this.mWifiManager.save(localWifiConfiguration, this.mSaveListener);
          connect(localWifiConfiguration);
        }
      }
      else if (paramWifiConfigController.getMode() == 2)
      {
        this.mWifiManager.save(localWifiConfiguration, this.mSaveListener);
      }
      else
      {
        this.mWifiManager.save(localWifiConfiguration, this.mSaveListener);
        if (this.mSelectedAccessPoint != null) {
          connect(localWifiConfiguration);
        }
      }
    }
  }
  
  private static class SummaryProvider
    extends BroadcastReceiver
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mSummaryLoader;
    private final WifiManager mWifiManager;
    private final WifiStatusTracker mWifiTracker;
    
    public SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
      this.mWifiManager = ((WifiManager)paramContext.getSystemService(WifiManager.class));
      this.mWifiTracker = new WifiStatusTracker(this.mWifiManager);
    }
    
    private CharSequence getSummary()
    {
      if (!this.mWifiTracker.enabled) {
        return this.mContext.getString(2131689515);
      }
      if (!this.mWifiTracker.connected) {
        return this.mContext.getString(2131693580);
      }
      return this.mWifiTracker.ssid;
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      this.mWifiTracker.handleBroadcast(paramIntent);
      this.mSummaryLoader.setSummary(this, getSummary());
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        localIntentFilter.addAction("android.net.wifi.STATE_CHANGE");
        localIntentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        this.mSummaryLoader.registerReceiver(this, localIntentFilter);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */