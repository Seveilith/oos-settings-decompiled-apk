package com.android.settings.wfd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.Theme;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplaySessionInfo;
import android.hardware.display.WifiDisplayStatus;
import android.media.MediaRouter;
import android.media.MediaRouter.Callback;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Global;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.Editable;
import android.util.Slog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.app.MediaRouteDialogPresenter;
import com.android.settings.SettingsPreferenceFragment;

public final class WifiDisplaySettings
  extends SettingsPreferenceFragment
{
  private static final int CHANGE_ALL = -1;
  private static final int CHANGE_ROUTES = 2;
  private static final int CHANGE_SETTINGS = 1;
  private static final int CHANGE_WIFI_DISPLAY_STATUS = 4;
  private static final boolean DEBUG = false;
  private static final int MENU_ID_ENABLE_WIFI_DISPLAY = 1;
  private static final int ORDER_AVAILABLE = 3;
  private static final int ORDER_CERTIFICATION = 1;
  private static final int ORDER_CONNECTED = 2;
  private static final int ORDER_UNAVAILABLE = 4;
  private static final String TAG = "WifiDisplaySettings";
  private boolean mAutoGO;
  private PreferenceGroup mCertCategory;
  private DisplayManager mDisplayManager;
  private TextView mEmptyView;
  private final Handler mHandler = new Handler();
  private boolean mListen;
  private int mListenChannel;
  private int mOperatingChannel;
  private int mPendingChanges;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED")) {
        WifiDisplaySettings.-wrap1(WifiDisplaySettings.this, 4);
      }
    }
  };
  private MediaRouter mRouter;
  private final MediaRouter.Callback mRouterCallback = new MediaRouter.SimpleCallback()
  {
    public void onRouteAdded(MediaRouter paramAnonymousMediaRouter, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      WifiDisplaySettings.-wrap1(WifiDisplaySettings.this, 2);
    }
    
    public void onRouteChanged(MediaRouter paramAnonymousMediaRouter, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      WifiDisplaySettings.-wrap1(WifiDisplaySettings.this, 2);
    }
    
    public void onRouteRemoved(MediaRouter paramAnonymousMediaRouter, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      WifiDisplaySettings.-wrap1(WifiDisplaySettings.this, 2);
    }
    
    public void onRouteSelected(MediaRouter paramAnonymousMediaRouter, int paramAnonymousInt, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      WifiDisplaySettings.-wrap1(WifiDisplaySettings.this, 2);
    }
    
    public void onRouteUnselected(MediaRouter paramAnonymousMediaRouter, int paramAnonymousInt, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      WifiDisplaySettings.-wrap1(WifiDisplaySettings.this, 2);
    }
  };
  private final ContentObserver mSettingsObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      WifiDisplaySettings.-wrap1(WifiDisplaySettings.this, 1);
    }
  };
  private boolean mStarted;
  private final Runnable mUpdateRunnable = new Runnable()
  {
    public void run()
    {
      int i = WifiDisplaySettings.-get5(WifiDisplaySettings.this);
      WifiDisplaySettings.-set4(WifiDisplaySettings.this, 0);
      WifiDisplaySettings.-wrap8(WifiDisplaySettings.this, i);
    }
  };
  private boolean mWifiDisplayCertificationOn;
  private boolean mWifiDisplayOnSetting;
  private WifiDisplayStatus mWifiDisplayStatus;
  private WifiP2pManager.Channel mWifiP2pChannel;
  private WifiP2pManager mWifiP2pManager;
  private int mWpsConfig = 4;
  
  private void buildCertificationMenu(PreferenceScreen paramPreferenceScreen)
  {
    if (this.mCertCategory == null)
    {
      this.mCertCategory = new PreferenceCategory(getPrefContext());
      this.mCertCategory.setTitle(2131691308);
      this.mCertCategory.setOrder(1);
    }
    for (;;)
    {
      paramPreferenceScreen.addPreference(this.mCertCategory);
      if (!this.mWifiDisplayStatus.getSessionInfo().getGroupId().isEmpty())
      {
        paramPreferenceScreen = new Preference(getPrefContext());
        paramPreferenceScreen.setTitle(2131691309);
        paramPreferenceScreen.setSummary(this.mWifiDisplayStatus.getSessionInfo().toString());
        this.mCertCategory.addPreference(paramPreferenceScreen);
        if (this.mWifiDisplayStatus.getSessionInfo().getSessionId() != 0)
        {
          this.mCertCategory.addPreference(new Preference(getPrefContext())
          {
            public void onBindViewHolder(PreferenceViewHolder paramAnonymousPreferenceViewHolder)
            {
              super.onBindViewHolder(paramAnonymousPreferenceViewHolder);
              Button localButton = (Button)paramAnonymousPreferenceViewHolder.findViewById(2131362644);
              localButton.setText(2131691312);
              localButton.setOnClickListener(new View.OnClickListener()
              {
                public void onClick(View paramAnonymous2View)
                {
                  WifiDisplaySettings.-get1(WifiDisplaySettings.this).pauseWifiDisplay();
                }
              });
              paramAnonymousPreferenceViewHolder = (Button)paramAnonymousPreferenceViewHolder.findViewById(2131362282);
              paramAnonymousPreferenceViewHolder.setText(2131691313);
              paramAnonymousPreferenceViewHolder.setOnClickListener(new View.OnClickListener()
              {
                public void onClick(View paramAnonymous2View)
                {
                  WifiDisplaySettings.-get1(WifiDisplaySettings.this).resumeWifiDisplay();
                }
              });
            }
          });
          this.mCertCategory.setLayoutResource(2130969086);
        }
      }
      paramPreferenceScreen = new SwitchPreference(getPrefContext())
      {
        protected void onClick()
        {
          WifiDisplaySettings localWifiDisplaySettings = WifiDisplaySettings.this;
          if (WifiDisplaySettings.-get2(WifiDisplaySettings.this)) {}
          for (boolean bool = false;; bool = true)
          {
            WifiDisplaySettings.-set1(localWifiDisplaySettings, bool);
            WifiDisplaySettings.-wrap2(WifiDisplaySettings.this, WifiDisplaySettings.-get2(WifiDisplaySettings.this));
            setChecked(WifiDisplaySettings.-get2(WifiDisplaySettings.this));
            return;
          }
        }
      };
      paramPreferenceScreen.setTitle(2131691310);
      paramPreferenceScreen.setChecked(this.mListen);
      this.mCertCategory.addPreference(paramPreferenceScreen);
      paramPreferenceScreen = new SwitchPreference(getPrefContext())
      {
        protected void onClick()
        {
          WifiDisplaySettings localWifiDisplaySettings = WifiDisplaySettings.this;
          boolean bool;
          if (WifiDisplaySettings.-get0(WifiDisplaySettings.this))
          {
            bool = false;
            WifiDisplaySettings.-set0(localWifiDisplaySettings, bool);
            if (!WifiDisplaySettings.-get0(WifiDisplaySettings.this)) {
              break label57;
            }
            WifiDisplaySettings.-wrap5(WifiDisplaySettings.this);
          }
          for (;;)
          {
            setChecked(WifiDisplaySettings.-get0(WifiDisplaySettings.this));
            return;
            bool = true;
            break;
            label57:
            WifiDisplaySettings.-wrap6(WifiDisplaySettings.this);
          }
        }
      };
      paramPreferenceScreen.setTitle(2131691311);
      paramPreferenceScreen.setChecked(this.mAutoGO);
      this.mCertCategory.addPreference(paramPreferenceScreen);
      paramPreferenceScreen = new ListPreference(getPrefContext());
      paramPreferenceScreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          int i = Integer.parseInt((String)paramAnonymousObject);
          if (i != WifiDisplaySettings.-get6(WifiDisplaySettings.this))
          {
            WifiDisplaySettings.-set5(WifiDisplaySettings.this, i);
            WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
            Settings.Global.putInt(WifiDisplaySettings.this.getActivity().getContentResolver(), "wifi_display_wps_config", WifiDisplaySettings.-get6(WifiDisplaySettings.this));
          }
          return true;
        }
      });
      this.mWpsConfig = Settings.Global.getInt(getActivity().getContentResolver(), "wifi_display_wps_config", 4);
      paramPreferenceScreen.setKey("wps");
      paramPreferenceScreen.setTitle(2131691314);
      paramPreferenceScreen.setEntries(new String[] { "Default", "PBC", "KEYPAD", "DISPLAY" });
      paramPreferenceScreen.setEntryValues(new String[] { "4", "0", "2", "1" });
      paramPreferenceScreen.setValue("" + this.mWpsConfig);
      paramPreferenceScreen.setSummary("%1$s");
      this.mCertCategory.addPreference(paramPreferenceScreen);
      paramPreferenceScreen = new ListPreference(getPrefContext());
      paramPreferenceScreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          int i = Integer.parseInt((String)paramAnonymousObject);
          if (i != WifiDisplaySettings.-get3(WifiDisplaySettings.this))
          {
            WifiDisplaySettings.-set2(WifiDisplaySettings.this, i);
            WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
            WifiDisplaySettings.-wrap3(WifiDisplaySettings.this, WifiDisplaySettings.-get3(WifiDisplaySettings.this), WifiDisplaySettings.-get4(WifiDisplaySettings.this));
          }
          return true;
        }
      });
      paramPreferenceScreen.setKey("listening_channel");
      paramPreferenceScreen.setTitle(2131691315);
      paramPreferenceScreen.setEntries(new String[] { "Auto", "1", "6", "11" });
      paramPreferenceScreen.setEntryValues(new String[] { "0", "1", "6", "11" });
      paramPreferenceScreen.setValue("" + this.mListenChannel);
      paramPreferenceScreen.setSummary("%1$s");
      this.mCertCategory.addPreference(paramPreferenceScreen);
      paramPreferenceScreen = new ListPreference(getPrefContext());
      paramPreferenceScreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          int i = Integer.parseInt((String)paramAnonymousObject);
          if (i != WifiDisplaySettings.-get4(WifiDisplaySettings.this))
          {
            WifiDisplaySettings.-set3(WifiDisplaySettings.this, i);
            WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
            WifiDisplaySettings.-wrap3(WifiDisplaySettings.this, WifiDisplaySettings.-get3(WifiDisplaySettings.this), WifiDisplaySettings.-get4(WifiDisplaySettings.this));
          }
          return true;
        }
      });
      paramPreferenceScreen.setKey("operating_channel");
      paramPreferenceScreen.setTitle(2131691316);
      paramPreferenceScreen.setEntries(new String[] { "Auto", "1", "6", "11", "36" });
      paramPreferenceScreen.setEntryValues(new String[] { "0", "1", "6", "11", "36" });
      paramPreferenceScreen.setValue("" + this.mOperatingChannel);
      paramPreferenceScreen.setSummary("%1$s");
      this.mCertCategory.addPreference(paramPreferenceScreen);
      return;
      this.mCertCategory.removeAll();
    }
  }
  
  private RoutePreference createRoutePreference(MediaRouter.RouteInfo paramRouteInfo)
  {
    WifiDisplay localWifiDisplay = findWifiDisplay(paramRouteInfo.getDeviceAddress());
    if (localWifiDisplay != null) {
      return new WifiDisplayRoutePreference(getPrefContext(), paramRouteInfo, localWifiDisplay);
    }
    return new RoutePreference(getPrefContext(), paramRouteInfo);
  }
  
  private WifiDisplay findWifiDisplay(String paramString)
  {
    if ((this.mWifiDisplayStatus != null) && (paramString != null))
    {
      WifiDisplay[] arrayOfWifiDisplay = this.mWifiDisplayStatus.getDisplays();
      int i = 0;
      int j = arrayOfWifiDisplay.length;
      while (i < j)
      {
        WifiDisplay localWifiDisplay = arrayOfWifiDisplay[i];
        if (localWifiDisplay.getDeviceAddress().equals(paramString)) {
          return localWifiDisplay;
        }
        i += 1;
      }
    }
    return null;
  }
  
  private void pairWifiDisplay(WifiDisplay paramWifiDisplay)
  {
    if (paramWifiDisplay.canConnect()) {
      this.mDisplayManager.connectWifiDisplay(paramWifiDisplay.getDeviceAddress());
    }
  }
  
  private void scheduleUpdate(int paramInt)
  {
    if (this.mStarted)
    {
      if (this.mPendingChanges == 0) {
        this.mHandler.post(this.mUpdateRunnable);
      }
      this.mPendingChanges |= paramInt;
    }
  }
  
  private void setListenMode(final boolean paramBoolean)
  {
    this.mWifiP2pManager.listen(this.mWifiP2pChannel, paramBoolean, new WifiP2pManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        StringBuilder localStringBuilder = new StringBuilder().append("Failed to ");
        if (paramBoolean) {}
        for (String str = "entered";; str = "exited")
        {
          Slog.e("WifiDisplaySettings", str + " listen mode with reason " + paramAnonymousInt + ".");
          return;
        }
      }
      
      public void onSuccess() {}
    });
  }
  
  private void setWifiP2pChannels(int paramInt1, int paramInt2)
  {
    this.mWifiP2pManager.setWifiP2pChannels(this.mWifiP2pChannel, paramInt1, paramInt2, new WifiP2pManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Slog.e("WifiDisplaySettings", "Failed to set wifi p2p channels with reason " + paramAnonymousInt + ".");
      }
      
      public void onSuccess() {}
    });
  }
  
  private void showWifiDisplayOptionsDialog(final WifiDisplay paramWifiDisplay)
  {
    View localView = getActivity().getLayoutInflater().inflate(2130969116, null);
    final Object localObject = (EditText)localView.findViewById(2131362120);
    ((EditText)localObject).setText(paramWifiDisplay.getFriendlyDisplayName());
    localObject = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        String str = localObject.getText().toString().trim();
        if (!str.isEmpty())
        {
          paramAnonymousDialogInterface = str;
          if (!str.equals(paramWifiDisplay.getDeviceName())) {}
        }
        else
        {
          paramAnonymousDialogInterface = null;
        }
        WifiDisplaySettings.-get1(WifiDisplaySettings.this).renameWifiDisplay(paramWifiDisplay.getDeviceAddress(), paramAnonymousDialogInterface);
      }
    };
    paramWifiDisplay = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        WifiDisplaySettings.-get1(WifiDisplaySettings.this).forgetWifiDisplay(paramWifiDisplay.getDeviceAddress());
      }
    };
    new AlertDialog.Builder(getActivity()).setCancelable(true).setTitle(2131691304).setView(localView).setPositiveButton(2131691306, (DialogInterface.OnClickListener)localObject).setNegativeButton(2131691305, paramWifiDisplay).create().show();
  }
  
  private void startAutoGO()
  {
    this.mWifiP2pManager.createGroup(this.mWifiP2pChannel, new WifiP2pManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Slog.e("WifiDisplaySettings", "Failed to start AutoGO with reason " + paramAnonymousInt + ".");
      }
      
      public void onSuccess() {}
    });
  }
  
  private void stopAutoGO()
  {
    this.mWifiP2pManager.removeGroup(this.mWifiP2pChannel, new WifiP2pManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Slog.e("WifiDisplaySettings", "Failed to stop AutoGO with reason " + paramAnonymousInt + ".");
      }
      
      public void onSuccess() {}
    });
  }
  
  private void toggleRoute(MediaRouter.RouteInfo paramRouteInfo)
  {
    if (paramRouteInfo.isSelected())
    {
      MediaRouteDialogPresenter.showDialogFragment(getActivity(), 4, null);
      return;
    }
    paramRouteInfo.select();
  }
  
  private void unscheduleUpdate()
  {
    if (this.mPendingChanges != 0)
    {
      this.mPendingChanges = 0;
      this.mHandler.removeCallbacks(this.mUpdateRunnable);
    }
  }
  
  private void update(int paramInt)
  {
    boolean bool2 = true;
    int j = 0;
    int i = 0;
    if ((paramInt & 0x1) != 0)
    {
      if (Settings.Global.getInt(getContentResolver(), "wifi_display_on", 0) == 0) {
        break label162;
      }
      bool1 = true;
      this.mWifiDisplayOnSetting = bool1;
      if (Settings.Global.getInt(getContentResolver(), "wifi_display_certification_on", 0) == 0) {
        break label168;
      }
    }
    PreferenceScreen localPreferenceScreen;
    int k;
    Object localObject;
    label162:
    label168:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mWifiDisplayCertificationOn = bool1;
      this.mWpsConfig = Settings.Global.getInt(getContentResolver(), "wifi_display_wps_config", 4);
      i = 1;
      if ((paramInt & 0x4) != 0)
      {
        this.mWifiDisplayStatus = this.mDisplayManager.getWifiDisplayStatus();
        i = 1;
      }
      localPreferenceScreen = getPreferenceScreen();
      localPreferenceScreen.removeAll();
      k = this.mRouter.getRouteCount();
      paramInt = 0;
      while (paramInt < k)
      {
        localObject = this.mRouter.getRouteAt(paramInt);
        if (((MediaRouter.RouteInfo)localObject).matchesTypes(4)) {
          localPreferenceScreen.addPreference(createRoutePreference((MediaRouter.RouteInfo)localObject));
        }
        paramInt += 1;
      }
      bool1 = false;
      break;
    }
    if ((this.mWifiDisplayStatus != null) && (this.mWifiDisplayStatus.getFeatureState() == 3))
    {
      localObject = this.mWifiDisplayStatus.getDisplays();
      k = localObject.length;
      paramInt = j;
      if (paramInt < k)
      {
        WifiDisplay localWifiDisplay = localObject[paramInt];
        if ((localWifiDisplay.isRemembered()) || (!localWifiDisplay.isAvailable()) || (localWifiDisplay.equals(this.mWifiDisplayStatus.getActiveDisplay()))) {}
        for (;;)
        {
          paramInt += 1;
          break;
          localPreferenceScreen.addPreference(new UnpairedWifiDisplayPreference(getPrefContext(), localWifiDisplay));
        }
      }
      if (this.mWifiDisplayCertificationOn) {
        buildCertificationMenu(localPreferenceScreen);
      }
    }
    if (i != 0) {
      getActivity().invalidateOptionsMenu();
    }
  }
  
  protected int getHelpResource()
  {
    return 2131693029;
  }
  
  protected int getMetricsCategory()
  {
    return 102;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mEmptyView = ((TextView)getView().findViewById(16908292));
    this.mEmptyView.setText(2131691298);
    setEmptyView(this.mEmptyView);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getActivity();
    this.mRouter = ((MediaRouter)paramBundle.getSystemService("media_router"));
    this.mDisplayManager = ((DisplayManager)paramBundle.getSystemService("display"));
    this.mWifiP2pManager = ((WifiP2pManager)paramBundle.getSystemService("wifip2p"));
    this.mWifiP2pChannel = this.mWifiP2pManager.initialize(paramBundle, Looper.getMainLooper(), null);
    addPreferencesFromResource(2131230888);
    setHasOptionsMenu(true);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if ((this.mWifiDisplayStatus != null) && (this.mWifiDisplayStatus.getFeatureState() != 0))
    {
      MenuItem localMenuItem = paramMenu.add(0, 1, 0, 2131691297);
      localMenuItem.setCheckable(true);
      localMenuItem.setChecked(this.mWifiDisplayOnSetting);
    }
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    int i = 0;
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    }
    if (paramMenuItem.isChecked()) {}
    for (boolean bool = false;; bool = true)
    {
      this.mWifiDisplayOnSetting = bool;
      paramMenuItem.setChecked(this.mWifiDisplayOnSetting);
      paramMenuItem = getContentResolver();
      if (this.mWifiDisplayOnSetting) {
        i = 1;
      }
      Settings.Global.putInt(paramMenuItem, "wifi_display_on", i);
      return true;
    }
  }
  
  public void onStart()
  {
    super.onStart();
    this.mStarted = true;
    Activity localActivity = getActivity();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED");
    localActivity.registerReceiver(this.mReceiver, localIntentFilter);
    getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_on"), false, this.mSettingsObserver);
    getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_certification_on"), false, this.mSettingsObserver);
    getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_wps_config"), false, this.mSettingsObserver);
    this.mRouter.addCallback(4, this.mRouterCallback, 1);
    update(-1);
  }
  
  public void onStop()
  {
    super.onStop();
    this.mStarted = false;
    getActivity().unregisterReceiver(this.mReceiver);
    getContentResolver().unregisterContentObserver(this.mSettingsObserver);
    this.mRouter.removeCallback(this.mRouterCallback);
    unscheduleUpdate();
  }
  
  private class RoutePreference
    extends Preference
    implements Preference.OnPreferenceClickListener
  {
    private final MediaRouter.RouteInfo mRoute;
    
    public RoutePreference(Context paramContext, MediaRouter.RouteInfo paramRouteInfo)
    {
      super();
      this.mRoute = paramRouteInfo;
      setTitle(paramRouteInfo.getName());
      setSummary(paramRouteInfo.getDescription());
      setEnabled(paramRouteInfo.isEnabled());
      if (paramRouteInfo.isSelected())
      {
        setOrder(2);
        if (paramRouteInfo.isConnecting()) {
          setSummary(2131691299);
        }
      }
      for (;;)
      {
        setOnPreferenceClickListener(this);
        return;
        setSummary(2131691300);
        continue;
        if (isEnabled())
        {
          setOrder(3);
        }
        else
        {
          setOrder(4);
          if (paramRouteInfo.getStatusCode() == 5) {
            setSummary(2131691301);
          } else {
            setSummary(2131691302);
          }
        }
      }
    }
    
    public boolean onPreferenceClick(Preference paramPreference)
    {
      WifiDisplaySettings.-wrap7(WifiDisplaySettings.this, this.mRoute);
      return true;
    }
  }
  
  private class UnpairedWifiDisplayPreference
    extends Preference
    implements Preference.OnPreferenceClickListener
  {
    private final WifiDisplay mDisplay;
    
    public UnpairedWifiDisplayPreference(Context paramContext, WifiDisplay paramWifiDisplay)
    {
      super();
      this.mDisplay = paramWifiDisplay;
      setTitle(paramWifiDisplay.getFriendlyDisplayName());
      setSummary(17040655);
      setEnabled(paramWifiDisplay.canConnect());
      if (isEnabled()) {
        setOrder(3);
      }
      for (;;)
      {
        setOnPreferenceClickListener(this);
        return;
        setOrder(4);
        setSummary(2131691301);
      }
    }
    
    public boolean onPreferenceClick(Preference paramPreference)
    {
      WifiDisplaySettings.-wrap0(WifiDisplaySettings.this, this.mDisplay);
      return true;
    }
  }
  
  private class WifiDisplayRoutePreference
    extends WifiDisplaySettings.RoutePreference
    implements View.OnClickListener
  {
    private final WifiDisplay mDisplay;
    
    public WifiDisplayRoutePreference(Context paramContext, MediaRouter.RouteInfo paramRouteInfo, WifiDisplay paramWifiDisplay)
    {
      super(paramContext, paramRouteInfo);
      this.mDisplay = paramWifiDisplay;
      setWidgetLayoutResource(2130969117);
    }
    
    public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
    {
      super.onBindViewHolder(paramPreferenceViewHolder);
      paramPreferenceViewHolder = (ImageView)paramPreferenceViewHolder.findViewById(2131362413);
      if (paramPreferenceViewHolder != null)
      {
        paramPreferenceViewHolder.setOnClickListener(this);
        if (!isEnabled())
        {
          TypedValue localTypedValue = new TypedValue();
          getContext().getTheme().resolveAttribute(16842803, localTypedValue, true);
          paramPreferenceViewHolder.setImageAlpha((int)(localTypedValue.getFloat() * 255.0F));
          paramPreferenceViewHolder.setEnabled(true);
        }
      }
    }
    
    public void onClick(View paramView)
    {
      WifiDisplaySettings.-wrap4(WifiDisplaySettings.this, this.mDisplay);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wfd\WifiDisplaySettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */