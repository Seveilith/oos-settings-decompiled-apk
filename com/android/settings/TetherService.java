package com.android.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settingslib.TetherUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TetherService
  extends Service
{
  private static final boolean DEBUG = Log.isLoggable("TetherService", 3);
  public static final String EXTRA_RESULT = "EntitlementResult";
  private static final String KEY_TETHERS = "currentTethers";
  private static final int MS_PER_HOUR = 3600000;
  private static final String PREFS = "tetherPrefs";
  private static final int RESULT_DEFAULT = 0;
  private static final int RESULT_OK = -1;
  private static final String TAG = "TetherService";
  private static final String TETHER_CHOICE = "TETHER_TYPE";
  private ArrayList<Integer> mCurrentTethers;
  private int mCurrentTypeIndex;
  private boolean mInProvisionCheck;
  private ArrayMap<Integer, List<ResultReceiver>> mPendingCallbacks;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (TetherService.-get0()) {
        Log.d("TetherService", "Got provision result " + paramAnonymousIntent);
      }
      int i;
      int j;
      if (TetherService.this.getResources().getString(17039413).equals(paramAnonymousIntent.getAction()))
      {
        if (!TetherService.-get3(TetherService.this))
        {
          Log.e("TetherService", "Unexpected provision response " + paramAnonymousIntent);
          return;
        }
        i = ((Integer)TetherService.-get1(TetherService.this).get(TetherService.-get2(TetherService.this))).intValue();
        TetherService.-set1(TetherService.this, false);
        j = paramAnonymousIntent.getIntExtra("EntitlementResult", 0);
        if (j != -1) {
          switch (i)
          {
          }
        }
      }
      for (;;)
      {
        TetherService.-wrap3(TetherService.this, i, j);
        paramAnonymousContext = TetherService.this;
        if (TetherService.-set0(paramAnonymousContext, TetherService.-get2(paramAnonymousContext) + 1) < TetherService.-get1(TetherService.this).size()) {
          break;
        }
        TetherService.this.stopSelf();
        return;
        TetherService.-wrap2(TetherService.this);
        continue;
        TetherService.-wrap0(TetherService.this);
        continue;
        TetherService.-wrap1(TetherService.this);
      }
      TetherService.-wrap4(TetherService.this, TetherService.-get2(TetherService.this));
    }
  };
  
  private void cancelAlarmIfNecessary()
  {
    if (this.mCurrentTethers.size() != 0)
    {
      if (DEBUG) {
        Log.d("TetherService", "Tethering still active, not cancelling alarm");
      }
      return;
    }
    PendingIntent localPendingIntent = PendingIntent.getService(this, 0, new Intent(this, TetherService.class), 0);
    ((AlarmManager)getSystemService("alarm")).cancel(localPendingIntent);
    if (DEBUG) {
      Log.d("TetherService", "Tethering no longer active, canceling recheck");
    }
  }
  
  public static void cancelRecheckAlarmIfNecessary(Context paramContext, int paramInt)
  {
    Intent localIntent = new Intent(paramContext, TetherService.class);
    localIntent.putExtra("extraRemTetherType", paramInt);
    paramContext.startService(localIntent);
  }
  
  private void disableBtTethering()
  {
    final BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (localBluetoothAdapter != null) {
      localBluetoothAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener()
      {
        public void onServiceConnected(int paramAnonymousInt, BluetoothProfile paramAnonymousBluetoothProfile)
        {
          ((BluetoothPan)paramAnonymousBluetoothProfile).setBluetoothTethering(false);
          localBluetoothAdapter.closeProfileProxy(5, paramAnonymousBluetoothProfile);
        }
        
        public void onServiceDisconnected(int paramAnonymousInt) {}
      }, 5);
    }
  }
  
  private void disableUsbTethering()
  {
    ((ConnectivityManager)getSystemService("connectivity")).setUsbTethering(false);
  }
  
  private void disableWifiTethering()
  {
    TetherUtil.setWifiTethering(false, this);
  }
  
  private void fireCallbacksForType(int paramInt1, int paramInt2)
  {
    List localList = (List)this.mPendingCallbacks.get(Integer.valueOf(paramInt1));
    if (localList == null) {
      return;
    }
    if (paramInt2 == -1) {}
    for (paramInt1 = 0;; paramInt1 = 11)
    {
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        ResultReceiver localResultReceiver = (ResultReceiver)localIterator.next();
        if (DEBUG) {
          Log.d("TetherService", "Firing result: " + paramInt1 + " to callback");
        }
        localResultReceiver.send(paramInt1, null);
      }
    }
    localList.clear();
  }
  
  private void removeTypeAtIndex(int paramInt)
  {
    this.mCurrentTethers.remove(paramInt);
    if (DEBUG) {
      Log.d("TetherService", "mCurrentTypeIndex: " + this.mCurrentTypeIndex);
    }
    if ((paramInt <= this.mCurrentTypeIndex) && (this.mCurrentTypeIndex > 0)) {
      this.mCurrentTypeIndex -= 1;
    }
  }
  
  private void scheduleAlarm()
  {
    Object localObject = new Intent(this, TetherService.class);
    ((Intent)localObject).putExtra("extraRunProvision", true);
    localObject = PendingIntent.getService(this, 0, (Intent)localObject, 0);
    AlarmManager localAlarmManager = (AlarmManager)getSystemService("alarm");
    long l1 = 3600000 * getResources().getInteger(17694739);
    long l2 = SystemClock.elapsedRealtime();
    if (DEBUG) {
      Log.d("TetherService", "Scheduling alarm at interval " + l1);
    }
    localAlarmManager.setRepeating(3, l2 + l1, l1, (PendingIntent)localObject);
  }
  
  private void startProvisioning(int paramInt)
  {
    if (paramInt < this.mCurrentTethers.size())
    {
      Object localObject = getResources().getString(17039412);
      if (DEBUG) {
        Log.d("TetherService", "Sending provisioning broadcast: " + (String)localObject + " type: " + this.mCurrentTethers.get(paramInt));
      }
      localObject = new Intent((String)localObject);
      ((Intent)localObject).putExtra("TETHER_TYPE", ((Integer)this.mCurrentTethers.get(paramInt)).intValue());
      ((Intent)localObject).setFlags(268435456);
      sendBroadcast((Intent)localObject);
      this.mInProvisionCheck = true;
    }
  }
  
  private ArrayList<Integer> stringToTethers(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    if (TextUtils.isEmpty(paramString)) {
      return localArrayList;
    }
    paramString = paramString.split(",");
    int i = 0;
    while (i < paramString.length)
    {
      localArrayList.add(Integer.valueOf(Integer.parseInt(paramString[i])));
      i += 1;
    }
    return localArrayList;
  }
  
  private String tethersToString(ArrayList<Integer> paramArrayList)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      if (i != 0) {
        localStringBuffer.append(',');
      }
      localStringBuffer.append(paramArrayList.get(i));
      i += 1;
    }
    return localStringBuffer.toString();
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  public void onCreate()
  {
    super.onCreate();
    if (DEBUG) {
      Log.d("TetherService", "Creating TetherService");
    }
    String str = getResources().getString(17039413);
    registerReceiver(this.mReceiver, new IntentFilter(str), "android.permission.CONNECTIVITY_INTERNAL", null);
    this.mCurrentTethers = stringToTethers(getSharedPreferences("tetherPrefs", 0).getString("currentTethers", ""));
    this.mCurrentTypeIndex = 0;
    this.mPendingCallbacks = new ArrayMap(3);
    this.mPendingCallbacks.put(Integer.valueOf(0), new ArrayList());
    this.mPendingCallbacks.put(Integer.valueOf(1), new ArrayList());
    this.mPendingCallbacks.put(Integer.valueOf(2), new ArrayList());
  }
  
  public void onDestroy()
  {
    if (this.mInProvisionCheck) {
      Log.e("TetherService", "TetherService getting destroyed while mid-provisioning" + this.mCurrentTethers.get(this.mCurrentTypeIndex));
    }
    getSharedPreferences("tetherPrefs", 0).edit().putString("currentTethers", tethersToString(this.mCurrentTethers)).commit();
    if (DEBUG) {
      Log.d("TetherService", "Destroying TetherService");
    }
    unregisterReceiver(this.mReceiver);
    super.onDestroy();
  }
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    ResultReceiver localResultReceiver;
    if (paramIntent.hasExtra("extraAddTetherType"))
    {
      paramInt1 = paramIntent.getIntExtra("extraAddTetherType", -1);
      localResultReceiver = (ResultReceiver)paramIntent.getParcelableExtra("extraProvisionCallback");
      if (localResultReceiver != null)
      {
        List localList = (List)this.mPendingCallbacks.get(Integer.valueOf(paramInt1));
        if (localList == null) {
          break label266;
        }
        localList.add(localResultReceiver);
      }
      if (!this.mCurrentTethers.contains(Integer.valueOf(paramInt1)))
      {
        if (DEBUG) {
          Log.d("TetherService", "Adding tether " + paramInt1);
        }
        this.mCurrentTethers.add(Integer.valueOf(paramInt1));
      }
    }
    if (paramIntent.hasExtra("extraRemTetherType"))
    {
      if (!this.mInProvisionCheck)
      {
        paramInt1 = paramIntent.getIntExtra("extraRemTetherType", -1);
        int i = this.mCurrentTethers.indexOf(Integer.valueOf(paramInt1));
        if (DEBUG) {
          Log.d("TetherService", "Removing tether " + paramInt1 + ", index " + i);
        }
        if (i >= 0) {
          removeTypeAtIndex(i);
        }
        cancelAlarmIfNecessary();
      }
    }
    else
    {
      if ((paramIntent.getBooleanExtra("extraSetAlarm", false)) && (this.mCurrentTethers.size() == 1)) {
        scheduleAlarm();
      }
      if (!paramIntent.getBooleanExtra("extraRunProvision", false)) {
        break label297;
      }
      startProvisioning(this.mCurrentTypeIndex);
    }
    label266:
    label297:
    while (this.mInProvisionCheck)
    {
      return 3;
      localResultReceiver.send(1, null);
      stopSelf();
      return 2;
      if (!DEBUG) {
        break;
      }
      Log.d("TetherService", "Don't cancel alarm during provisioning");
      break;
    }
    if (DEBUG) {
      Log.d("TetherService", "Stopping self.  startid: " + paramInt2);
    }
    stopSelf();
    return 2;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\TetherService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */