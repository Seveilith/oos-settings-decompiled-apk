package com.android.settings.bluetooth;

import android.app.QueuedWork;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.concurrent.ExecutorService;

final class LocalBluetoothPreferences
{
  private static final boolean DEBUG = true;
  private static final int GRACE_PERIOD_TO_SHOW_DIALOGS_IN_FOREGROUND = 60000;
  private static final String KEY_DISCOVERABLE_END_TIMESTAMP = "discoverable_end_timestamp";
  private static final String KEY_DISCOVERING_TIMESTAMP = "last_discovering_time";
  private static final String KEY_DOCK_AUTO_CONNECT = "auto_connect_to_dock";
  private static final String KEY_LAST_SELECTED_DEVICE = "last_selected_device";
  private static final String KEY_LAST_SELECTED_DEVICE_TIME = "last_selected_device_time";
  private static final String SHARED_PREFERENCES_NAME = "bluetooth_settings";
  private static final String TAG = "LocalBluetoothPreferences";
  
  static long getDiscoverableEndTimestamp(Context paramContext)
  {
    return getSharedPreferences(paramContext).getLong("discoverable_end_timestamp", 0L);
  }
  
  static boolean getDockAutoConnectSetting(Context paramContext, String paramString)
  {
    return getSharedPreferences(paramContext).getBoolean("auto_connect_to_dock" + paramString, false);
  }
  
  private static SharedPreferences getSharedPreferences(Context paramContext)
  {
    return paramContext.getSharedPreferences("bluetooth_settings", 0);
  }
  
  static boolean hasDockAutoConnectSetting(Context paramContext, String paramString)
  {
    return getSharedPreferences(paramContext).contains("auto_connect_to_dock" + paramString);
  }
  
  static void persistDiscoverableEndTimestamp(Context paramContext, long paramLong)
  {
    paramContext = getSharedPreferences(paramContext).edit();
    paramContext.putLong("discoverable_end_timestamp", paramLong);
    paramContext.apply();
  }
  
  static void persistDiscoveringTimestamp(Context paramContext)
  {
    QueuedWork.singleThreadExecutor().submit(new Runnable()
    {
      public void run()
      {
        SharedPreferences.Editor localEditor = LocalBluetoothPreferences.-wrap0(this.val$context).edit();
        localEditor.putLong("last_discovering_time", System.currentTimeMillis());
        localEditor.apply();
      }
    });
  }
  
  static void persistSelectedDeviceInPicker(Context paramContext, String paramString)
  {
    paramContext = getSharedPreferences(paramContext).edit();
    paramContext.putString("last_selected_device", paramString);
    paramContext.putLong("last_selected_device_time", System.currentTimeMillis());
    paramContext.apply();
  }
  
  static void removeDockAutoConnectSetting(Context paramContext, String paramString)
  {
    paramContext = getSharedPreferences(paramContext).edit();
    paramContext.remove("auto_connect_to_dock" + paramString);
    paramContext.apply();
  }
  
  static void saveDockAutoConnectSetting(Context paramContext, String paramString, boolean paramBoolean)
  {
    paramContext = getSharedPreferences(paramContext).edit();
    paramContext.putBoolean("auto_connect_to_dock" + paramString, paramBoolean);
    paramContext.apply();
  }
  
  static boolean shouldShowDialogInForeground(Context paramContext, String paramString1, String paramString2)
  {
    Object localObject = Utils.getLocalBtManager(paramContext);
    if (localObject == null)
    {
      Log.v("LocalBluetoothPreferences", "manager == null - do not show dialog.");
      return false;
    }
    if (((LocalBluetoothManager)localObject).isForegroundActivity()) {
      return true;
    }
    if ((paramContext.getResources().getConfiguration().uiMode & 0x5) == 5)
    {
      Log.v("LocalBluetoothPreferences", "in appliance mode - do not show dialog.");
      return false;
    }
    long l = System.currentTimeMillis();
    SharedPreferences localSharedPreferences = getSharedPreferences(paramContext);
    if (60000L + localSharedPreferences.getLong("discoverable_end_timestamp", 0L) > l) {
      return true;
    }
    localObject = ((LocalBluetoothManager)localObject).getBluetoothAdapter();
    if ((localObject != null) && (((LocalBluetoothAdapter)localObject).isDiscovering())) {
      return true;
    }
    if (localSharedPreferences.getLong("last_discovering_time", 0L) + 60000L > l) {
      return true;
    }
    if ((paramString1 != null) && (paramString1.equals(localSharedPreferences.getString("last_selected_device", null))) && (60000L + localSharedPreferences.getLong("last_selected_device_time", 0L) > l)) {
      return true;
    }
    if ((!TextUtils.isEmpty(paramString2)) && (paramString2.equals(paramContext.getString(17039471))))
    {
      Log.v("LocalBluetoothPreferences", "showing dialog for packaged keyboard");
      return true;
    }
    Log.v("LocalBluetoothPreferences", "Found no reason to show the dialog - do not show dialog.");
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\LocalBluetoothPreferences.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */