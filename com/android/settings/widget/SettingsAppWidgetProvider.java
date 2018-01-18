package com.android.settings.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.IPowerManager.Stub;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import android.widget.RemoteViews;
import com.android.settings.bluetooth.Utils;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public class SettingsAppWidgetProvider
  extends AppWidgetProvider
{
  private static final int BUTTON_BLUETOOTH = 4;
  private static final int BUTTON_BRIGHTNESS = 1;
  private static final int BUTTON_LOCATION = 3;
  private static final int BUTTON_SYNC = 2;
  private static final int BUTTON_WIFI = 0;
  private static final float FULL_BRIGHTNESS_THRESHOLD = 0.8F;
  private static final float HALF_BRIGHTNESS_THRESHOLD = 0.3F;
  private static final int[] IND_DRAWABLE_MID;
  private static final int[] IND_DRAWABLE_OFF;
  private static final int[] IND_DRAWABLE_ON;
  private static final int POS_CENTER = 1;
  private static final int POS_LEFT = 0;
  private static final int POS_RIGHT = 2;
  private static final int STATE_DISABLED = 0;
  private static final int STATE_ENABLED = 1;
  private static final int STATE_INTERMEDIATE = 5;
  private static final int STATE_TURNING_OFF = 3;
  private static final int STATE_TURNING_ON = 2;
  private static final int STATE_UNKNOWN = 4;
  static final String TAG = "SettingsAppWidgetProvider";
  static final ComponentName THIS_APPWIDGET = new ComponentName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
  private static final StateTracker sBluetoothState = new BluetoothStateTracker(null);
  private static LocalBluetoothAdapter sLocalBluetoothAdapter = null;
  private static final StateTracker sLocationState = new LocationStateTracker(null);
  private static SettingsObserver sSettingsObserver;
  private static final StateTracker sSyncState = new SyncStateTracker(null);
  private static final StateTracker sWifiState;
  
  static
  {
    IND_DRAWABLE_OFF = new int[] { 2130837607, 2130837606, 2130837608 };
    IND_DRAWABLE_MID = new int[] { 2130837604, 2130837603, 2130837605 };
    IND_DRAWABLE_ON = new int[] { 2130837610, 2130837609, 2130837611 };
    sWifiState = new WifiStateTracker(null);
  }
  
  static RemoteViews buildUpdate(Context paramContext)
  {
    RemoteViews localRemoteViews = new RemoteViews(paramContext.getPackageName(), 2130969107);
    localRemoteViews.setOnClickPendingIntent(2131362703, getLaunchPendingIntent(paramContext, 0));
    localRemoteViews.setOnClickPendingIntent(2131362715, getLaunchPendingIntent(paramContext, 1));
    localRemoteViews.setOnClickPendingIntent(2131362712, getLaunchPendingIntent(paramContext, 2));
    localRemoteViews.setOnClickPendingIntent(2131362709, getLaunchPendingIntent(paramContext, 3));
    localRemoteViews.setOnClickPendingIntent(2131362706, getLaunchPendingIntent(paramContext, 4));
    updateButtons(localRemoteViews, paramContext);
    return localRemoteViews;
  }
  
  private static void checkObserver(Context paramContext)
  {
    if (sSettingsObserver == null)
    {
      sSettingsObserver = new SettingsObserver(new Handler(), paramContext.getApplicationContext());
      sSettingsObserver.startObserving();
    }
  }
  
  private static int getBrightness(Context paramContext)
  {
    try
    {
      int i = Settings.System.getInt(paramContext.getContentResolver(), "screen_brightness");
      return i;
    }
    catch (Exception paramContext) {}
    return 0;
  }
  
  private static boolean getBrightnessMode(Context paramContext)
  {
    try
    {
      int i = Settings.System.getInt(paramContext.getContentResolver(), "screen_brightness_mode");
      return i == 1;
    }
    catch (Exception paramContext)
    {
      Log.d("SettingsAppWidgetProvider", "getBrightnessMode: " + paramContext);
    }
    return false;
  }
  
  private static PendingIntent getLaunchPendingIntent(Context paramContext, int paramInt)
  {
    Intent localIntent = new Intent();
    localIntent.setClass(paramContext, SettingsAppWidgetProvider.class);
    localIntent.addCategory("android.intent.category.ALTERNATIVE");
    localIntent.setData(Uri.parse("custom:" + paramInt));
    return PendingIntent.getBroadcast(paramContext, 0, localIntent, 0);
  }
  
  private void toggleBrightness(Context paramContext)
  {
    try
    {
      IPowerManager localIPowerManager = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
      if (localIPowerManager != null)
      {
        PowerManager localPowerManager = (PowerManager)paramContext.getSystemService("power");
        ContentResolver localContentResolver = paramContext.getContentResolver();
        int i = Settings.System.getInt(localContentResolver, "screen_brightness");
        int j = 0;
        if (paramContext.getResources().getBoolean(17956900)) {
          j = Settings.System.getInt(localContentResolver, "screen_brightness_mode");
        }
        if (j == 1)
        {
          i = localPowerManager.getMinimumScreenBrightnessSetting();
          j = 0;
          if (!paramContext.getResources().getBoolean(17956900)) {
            break label171;
          }
          Settings.System.putInt(paramContext.getContentResolver(), "screen_brightness_mode", j);
        }
        for (;;)
        {
          if (j != 0) {
            return;
          }
          localIPowerManager.setTemporaryScreenBrightnessSettingOverride(i);
          Settings.System.putInt(localContentResolver, "screen_brightness", i);
          return;
          if (i < localPowerManager.getDefaultScreenBrightnessSetting())
          {
            i = localPowerManager.getDefaultScreenBrightnessSetting();
            break;
          }
          if (i < localPowerManager.getMaximumScreenBrightnessSetting())
          {
            i = localPowerManager.getMaximumScreenBrightnessSetting();
            break;
          }
          j = 1;
          i = localPowerManager.getMinimumScreenBrightnessSetting();
          break;
          label171:
          j = 0;
        }
      }
      return;
    }
    catch (Settings.SettingNotFoundException paramContext)
    {
      Log.d("SettingsAppWidgetProvider", "toggleBrightness: " + paramContext);
      return;
    }
    catch (RemoteException paramContext)
    {
      Log.d("SettingsAppWidgetProvider", "toggleBrightness: " + paramContext);
    }
  }
  
  private static void updateButtons(RemoteViews paramRemoteViews, Context paramContext)
  {
    sWifiState.setImageViewResources(paramContext, paramRemoteViews);
    sBluetoothState.setImageViewResources(paramContext, paramRemoteViews);
    sLocationState.setImageViewResources(paramContext, paramRemoteViews);
    sSyncState.setImageViewResources(paramContext, paramRemoteViews);
    if (getBrightnessMode(paramContext))
    {
      paramRemoteViews.setContentDescription(2131362715, paramContext.getString(2131692587, new Object[] { paramContext.getString(2131692588) }));
      paramRemoteViews.setImageViewResource(2131362716, 2130837936);
      paramRemoteViews.setImageViewResource(2131362717, 2130837611);
      return;
    }
    int i = getBrightness(paramContext);
    PowerManager localPowerManager = (PowerManager)paramContext.getSystemService("power");
    int j = (int)(localPowerManager.getMaximumScreenBrightnessSetting() * 0.8F);
    int k = (int)(localPowerManager.getMaximumScreenBrightnessSetting() * 0.3F);
    if (i > j)
    {
      paramRemoteViews.setContentDescription(2131362715, paramContext.getString(2131692587, new Object[] { paramContext.getString(2131692589) }));
      paramRemoteViews.setImageViewResource(2131362716, 2130837937);
    }
    while (i > k)
    {
      paramRemoteViews.setImageViewResource(2131362717, 2130837611);
      return;
      if (i > k)
      {
        paramRemoteViews.setContentDescription(2131362715, paramContext.getString(2131692587, new Object[] { paramContext.getString(2131692590) }));
        paramRemoteViews.setImageViewResource(2131362716, 2130837938);
      }
      else
      {
        paramRemoteViews.setContentDescription(2131362715, paramContext.getString(2131692587, new Object[] { paramContext.getString(2131692591) }));
        paramRemoteViews.setImageViewResource(2131362716, 2130837939);
      }
    }
    paramRemoteViews.setImageViewResource(2131362717, 2130837608);
  }
  
  public static void updateWidget(Context paramContext)
  {
    RemoteViews localRemoteViews = buildUpdate(paramContext);
    AppWidgetManager localAppWidgetManager = AppWidgetManager.getInstance(paramContext);
    try
    {
      localAppWidgetManager.updateAppWidget(THIS_APPWIDGET, localRemoteViews);
      checkObserver(paramContext);
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
  
  public void onDisabled(Context paramContext)
  {
    if (sSettingsObserver != null)
    {
      sSettingsObserver.stopObserving();
      sSettingsObserver = null;
    }
  }
  
  public void onEnabled(Context paramContext)
  {
    checkObserver(paramContext);
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    super.onReceive(paramContext, paramIntent);
    String str = paramIntent.getAction();
    if ("android.net.wifi.WIFI_STATE_CHANGED".equals(str)) {
      sWifiState.onActualStateChange(paramContext, paramIntent);
    }
    for (;;)
    {
      updateWidget(paramContext);
      return;
      if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(str))
      {
        sBluetoothState.onActualStateChange(paramContext, paramIntent);
      }
      else if ("android.location.MODE_CHANGED".equals(str))
      {
        sLocationState.onActualStateChange(paramContext, paramIntent);
      }
      else if (ContentResolver.ACTION_SYNC_CONN_STATUS_CHANGED.equals(str))
      {
        sSyncState.onActualStateChange(paramContext, paramIntent);
      }
      else
      {
        if (!paramIntent.hasCategory("android.intent.category.ALTERNATIVE")) {
          break;
        }
        int i = Integer.parseInt(paramIntent.getData().getSchemeSpecificPart());
        if (i == 0) {
          sWifiState.toggleState(paramContext);
        } else if (i == 1) {
          toggleBrightness(paramContext);
        } else if (i == 2) {
          sSyncState.toggleState(paramContext);
        } else if (i == 3) {
          sLocationState.toggleState(paramContext);
        } else if (i == 4) {
          sBluetoothState.toggleState(paramContext);
        }
      }
    }
  }
  
  public void onUpdate(Context paramContext, AppWidgetManager paramAppWidgetManager, int[] paramArrayOfInt)
  {
    paramContext = buildUpdate(paramContext);
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      paramAppWidgetManager.updateAppWidget(paramArrayOfInt[i], paramContext);
      i += 1;
    }
  }
  
  private static final class BluetoothStateTracker
    extends SettingsAppWidgetProvider.StateTracker
  {
    private BluetoothStateTracker()
    {
      super();
    }
    
    private static int bluetoothStateToFiveState(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return 4;
      case 10: 
        return 0;
      case 12: 
        return 1;
      case 11: 
        return 2;
      }
      return 3;
    }
    
    public int getActualState(Context paramContext)
    {
      if (SettingsAppWidgetProvider.-get3() == null)
      {
        paramContext = Utils.getLocalBtManager(paramContext);
        if (paramContext == null) {
          return 4;
        }
        SettingsAppWidgetProvider.-set0(paramContext.getBluetoothAdapter());
        if (SettingsAppWidgetProvider.-get3() == null) {
          return 4;
        }
      }
      return bluetoothStateToFiveState(SettingsAppWidgetProvider.-get3().getBluetoothState());
    }
    
    public int getButtonDescription()
    {
      return 2131692584;
    }
    
    public int getButtonId()
    {
      return 2131362707;
    }
    
    public int getButtonImageId(boolean paramBoolean)
    {
      if (paramBoolean) {
        return 2130837935;
      }
      return 2130837934;
    }
    
    public int getContainerId()
    {
      return 2131362706;
    }
    
    public int getIndicatorId()
    {
      return 2131362708;
    }
    
    public void onActualStateChange(Context paramContext, Intent paramIntent)
    {
      if (!"android.bluetooth.adapter.action.STATE_CHANGED".equals(paramIntent.getAction())) {
        return;
      }
      setCurrentState(paramContext, bluetoothStateToFiveState(paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1)));
    }
    
    protected void requestStateChange(Context paramContext, final boolean paramBoolean)
    {
      if (SettingsAppWidgetProvider.-get3() == null)
      {
        Log.d("SettingsAppWidgetProvider", "No LocalBluetoothManager");
        return;
      }
      new AsyncTask()
      {
        protected Void doInBackground(Void... paramAnonymousVarArgs)
        {
          SettingsAppWidgetProvider.-get3().setBluetoothEnabled(paramBoolean);
          return null;
        }
      }.execute(new Void[0]);
    }
  }
  
  private static final class LocationStateTracker
    extends SettingsAppWidgetProvider.StateTracker
  {
    private int mCurrentLocationMode = 0;
    
    private LocationStateTracker()
    {
      super();
    }
    
    public int getActualState(Context paramContext)
    {
      this.mCurrentLocationMode = Settings.Secure.getInt(paramContext.getContentResolver(), "location_mode", 0);
      if (this.mCurrentLocationMode == 0) {
        return 0;
      }
      return 1;
    }
    
    public int getButtonDescription()
    {
      return 2131692585;
    }
    
    public int getButtonId()
    {
      return 2131362710;
    }
    
    public int getButtonImageId(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        switch (this.mCurrentLocationMode)
        {
        case 2: 
        default: 
          return 2130837942;
        }
        return 2130837941;
      }
      return 2130837940;
    }
    
    public int getContainerId()
    {
      return 2131362709;
    }
    
    public int getIndicatorId()
    {
      return 2131362711;
    }
    
    public void onActualStateChange(Context paramContext, Intent paramIntent)
    {
      setCurrentState(paramContext, getActualState(paramContext));
    }
    
    public void requestStateChange(final Context paramContext, boolean paramBoolean)
    {
      new AsyncTask()
      {
        protected Boolean doInBackground(Void... paramAnonymousVarArgs)
        {
          boolean bool2 = true;
          boolean bool1 = true;
          if (!((UserManager)paramContext.getSystemService("user")).hasUserRestriction("no_share_location"))
          {
            int j = Settings.Secure.getInt(this.val$resolver, "location_mode", 0);
            int i = 3;
            switch (j)
            {
            default: 
              Settings.Secure.putInt(this.val$resolver, "location_mode", i);
              if (i == 0) {
                break;
              }
            }
            for (;;)
            {
              return Boolean.valueOf(bool1);
              i = 2;
              break;
              i = 3;
              break;
              i = 0;
              break;
              i = -1;
              break;
              bool1 = false;
            }
          }
          if (SettingsAppWidgetProvider.LocationStateTracker.this.getActualState(paramContext) == 1) {}
          for (bool1 = bool2;; bool1 = false) {
            return Boolean.valueOf(bool1);
          }
        }
        
        protected void onPostExecute(Boolean paramAnonymousBoolean)
        {
          SettingsAppWidgetProvider.LocationStateTracker localLocationStateTracker = SettingsAppWidgetProvider.LocationStateTracker.this;
          Context localContext = paramContext;
          if (paramAnonymousBoolean.booleanValue()) {}
          for (int i = 1;; i = 0)
          {
            localLocationStateTracker.setCurrentState(localContext, i);
            SettingsAppWidgetProvider.updateWidget(paramContext);
            return;
          }
        }
      }.execute(new Void[0]);
    }
  }
  
  private static class SettingsObserver
    extends ContentObserver
  {
    private Context mContext;
    
    SettingsObserver(Handler paramHandler, Context paramContext)
    {
      super();
      this.mContext = paramContext;
    }
    
    public void onChange(boolean paramBoolean)
    {
      SettingsAppWidgetProvider.updateWidget(this.mContext);
    }
    
    void startObserving()
    {
      ContentResolver localContentResolver = this.mContext.getContentResolver();
      localContentResolver.registerContentObserver(Settings.System.getUriFor("screen_brightness"), false, this);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("screen_brightness_mode"), false, this);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("screen_auto_brightness_adj"), false, this);
    }
    
    void stopObserving()
    {
      this.mContext.getContentResolver().unregisterContentObserver(this);
    }
  }
  
  private static abstract class StateTracker
  {
    private Boolean mActualState = null;
    private boolean mDeferredStateChangeRequestNeeded = false;
    private boolean mInTransition = false;
    private Boolean mIntendedState = null;
    
    private final String getContentDescription(Context paramContext, int paramInt)
    {
      return paramContext.getString(2131692578, new Object[] { paramContext.getString(getButtonDescription()), paramContext.getString(paramInt) });
    }
    
    public abstract int getActualState(Context paramContext);
    
    public abstract int getButtonDescription();
    
    public abstract int getButtonId();
    
    public abstract int getButtonImageId(boolean paramBoolean);
    
    public abstract int getContainerId();
    
    public abstract int getIndicatorId();
    
    public int getPosition()
    {
      return 1;
    }
    
    public final int getTriState(Context paramContext)
    {
      if (this.mInTransition) {
        return 5;
      }
      switch (getActualState(paramContext))
      {
      default: 
        return 5;
      case 0: 
        return 0;
      }
      return 1;
    }
    
    public final boolean isTurningOn()
    {
      if (this.mIntendedState != null) {
        return this.mIntendedState.booleanValue();
      }
      return false;
    }
    
    public abstract void onActualStateChange(Context paramContext, Intent paramIntent);
    
    protected abstract void requestStateChange(Context paramContext, boolean paramBoolean);
    
    protected final void setCurrentState(Context paramContext, int paramInt)
    {
      boolean bool = this.mInTransition;
      switch (paramInt)
      {
      default: 
        if ((bool) && (!this.mInTransition)) {
          break;
        }
      }
      while (!this.mDeferredStateChangeRequestNeeded)
      {
        return;
        this.mInTransition = false;
        this.mActualState = Boolean.valueOf(false);
        break;
        this.mInTransition = false;
        this.mActualState = Boolean.valueOf(true);
        break;
        this.mInTransition = true;
        this.mActualState = Boolean.valueOf(false);
        break;
        this.mInTransition = true;
        this.mActualState = Boolean.valueOf(true);
        break;
      }
      Log.v("SettingsAppWidgetProvider", "processing deferred state change");
      if ((this.mActualState != null) && (this.mIntendedState != null) && (this.mIntendedState.equals(this.mActualState))) {
        Log.v("SettingsAppWidgetProvider", "... but intended state matches, so no changes.");
      }
      for (;;)
      {
        this.mDeferredStateChangeRequestNeeded = false;
        return;
        if (this.mIntendedState != null)
        {
          this.mInTransition = true;
          requestStateChange(paramContext, this.mIntendedState.booleanValue());
        }
      }
    }
    
    public final void setImageViewResources(Context paramContext, RemoteViews paramRemoteViews)
    {
      int i = getContainerId();
      int j = getButtonId();
      int k = getIndicatorId();
      int m = getPosition();
      switch (getTriState(paramContext))
      {
      case 2: 
      case 3: 
      case 4: 
      default: 
        return;
      case 0: 
        paramRemoteViews.setContentDescription(i, getContentDescription(paramContext, 2131692580));
        paramRemoteViews.setImageViewResource(j, getButtonImageId(false));
        paramRemoteViews.setImageViewResource(k, SettingsAppWidgetProvider.-get1()[m]);
        return;
      case 1: 
        paramRemoteViews.setContentDescription(i, getContentDescription(paramContext, 2131692579));
        paramRemoteViews.setImageViewResource(j, getButtonImageId(true));
        paramRemoteViews.setImageViewResource(k, SettingsAppWidgetProvider.-get2()[m]);
        return;
      }
      if (isTurningOn())
      {
        paramRemoteViews.setContentDescription(i, getContentDescription(paramContext, 2131692581));
        paramRemoteViews.setImageViewResource(j, getButtonImageId(true));
        paramRemoteViews.setImageViewResource(k, SettingsAppWidgetProvider.-get0()[m]);
        return;
      }
      paramRemoteViews.setContentDescription(i, getContentDescription(paramContext, 2131692582));
      paramRemoteViews.setImageViewResource(j, getButtonImageId(false));
      paramRemoteViews.setImageViewResource(k, SettingsAppWidgetProvider.-get1()[m]);
    }
    
    public final void toggleState(Context paramContext)
    {
      int i = getTriState(paramContext);
      boolean bool2 = false;
      boolean bool1 = bool2;
      switch (i)
      {
      default: 
        bool1 = bool2;
      }
      for (;;)
      {
        this.mIntendedState = Boolean.valueOf(bool1);
        if (!this.mInTransition) {
          break;
        }
        this.mDeferredStateChangeRequestNeeded = true;
        return;
        bool1 = false;
        continue;
        bool1 = true;
        continue;
        bool1 = bool2;
        if (this.mIntendedState != null) {
          if (this.mIntendedState.booleanValue()) {
            bool1 = false;
          } else {
            bool1 = true;
          }
        }
      }
      this.mInTransition = true;
      requestStateChange(paramContext, bool1);
    }
  }
  
  private static final class SyncStateTracker
    extends SettingsAppWidgetProvider.StateTracker
  {
    private SyncStateTracker()
    {
      super();
    }
    
    public int getActualState(Context paramContext)
    {
      if (ContentResolver.getMasterSyncAutomatically()) {
        return 1;
      }
      return 0;
    }
    
    public int getButtonDescription()
    {
      return 2131692586;
    }
    
    public int getButtonId()
    {
      return 2131362713;
    }
    
    public int getButtonImageId(boolean paramBoolean)
    {
      if (paramBoolean) {
        return 2130837944;
      }
      return 2130837943;
    }
    
    public int getContainerId()
    {
      return 2131362712;
    }
    
    public int getIndicatorId()
    {
      return 2131362714;
    }
    
    public void onActualStateChange(Context paramContext, Intent paramIntent)
    {
      setCurrentState(paramContext, getActualState(paramContext));
    }
    
    public void requestStateChange(final Context paramContext, final boolean paramBoolean)
    {
      ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
      new AsyncTask()
      {
        protected Boolean doInBackground(Void... paramAnonymousVarArgs)
        {
          if (paramBoolean)
          {
            if (!this.val$sync) {
              ContentResolver.setMasterSyncAutomatically(true);
            }
            return Boolean.valueOf(true);
          }
          if (this.val$sync) {
            ContentResolver.setMasterSyncAutomatically(false);
          }
          return Boolean.valueOf(false);
        }
        
        protected void onPostExecute(Boolean paramAnonymousBoolean)
        {
          SettingsAppWidgetProvider.SyncStateTracker localSyncStateTracker = SettingsAppWidgetProvider.SyncStateTracker.this;
          Context localContext = paramContext;
          if (paramAnonymousBoolean.booleanValue()) {}
          for (int i = 1;; i = 0)
          {
            localSyncStateTracker.setCurrentState(localContext, i);
            SettingsAppWidgetProvider.updateWidget(paramContext);
            return;
          }
        }
      }.execute(new Void[0]);
    }
  }
  
  private static final class WifiStateTracker
    extends SettingsAppWidgetProvider.StateTracker
  {
    private WifiStateTracker()
    {
      super();
    }
    
    private static int wifiStateToFiveState(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return 4;
      case 1: 
        return 0;
      case 3: 
        return 1;
      case 0: 
        return 3;
      }
      return 2;
    }
    
    public int getActualState(Context paramContext)
    {
      paramContext = (WifiManager)paramContext.getSystemService("wifi");
      if (paramContext != null) {
        return wifiStateToFiveState(paramContext.getWifiState());
      }
      return 4;
    }
    
    public int getButtonDescription()
    {
      return 2131692583;
    }
    
    public int getButtonId()
    {
      return 2131362704;
    }
    
    public int getButtonImageId(boolean paramBoolean)
    {
      if (paramBoolean) {
        return 2130837946;
      }
      return 2130837945;
    }
    
    public int getContainerId()
    {
      return 2131362703;
    }
    
    public int getIndicatorId()
    {
      return 2131362705;
    }
    
    public int getPosition()
    {
      return 0;
    }
    
    public void onActualStateChange(Context paramContext, Intent paramIntent)
    {
      if (!"android.net.wifi.WIFI_STATE_CHANGED".equals(paramIntent.getAction())) {
        return;
      }
      setCurrentState(paramContext, wifiStateToFiveState(paramIntent.getIntExtra("wifi_state", -1)));
    }
    
    protected void requestStateChange(final Context paramContext, final boolean paramBoolean)
    {
      paramContext = (WifiManager)paramContext.getSystemService("wifi");
      if (paramContext == null)
      {
        Log.d("SettingsAppWidgetProvider", "No wifiManager.");
        return;
      }
      new AsyncTask()
      {
        protected Void doInBackground(Void... paramAnonymousVarArgs)
        {
          int i = paramContext.getWifiApState();
          if ((paramBoolean) && ((i == 12) || (i == 13))) {
            paramContext.setWifiApEnabled(null, false);
          }
          paramContext.setWifiEnabled(paramBoolean);
          return null;
        }
      }.execute(new Void[0]);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\SettingsAppWidgetProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */