package com.android.settings.bluetooth;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Global;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager.ServiceListener;
import java.util.Iterator;
import java.util.Set;

public final class DockService
  extends Service
  implements LocalBluetoothProfileManager.ServiceListener
{
  static final boolean DEBUG = false;
  private static final long DISABLE_BT_GRACE_PERIOD = 2000L;
  private static final int INVALID_STARTID = -100;
  private static final String KEY_CONNECT_RETRY_COUNT = "connect_retry_count";
  private static final String KEY_DISABLE_BT = "disable_bt";
  private static final String KEY_DISABLE_BT_WHEN_UNDOCKED = "disable_bt_when_undock";
  private static final int MAX_CONNECT_RETRY = 6;
  private static final int MSG_TYPE_DISABLE_BT = 555;
  private static final int MSG_TYPE_DOCKED = 222;
  private static final int MSG_TYPE_SHOW_UI = 111;
  private static final int MSG_TYPE_UNDOCKED_PERMANENT = 444;
  private static final int MSG_TYPE_UNDOCKED_TEMPORARY = 333;
  private static final String SHARED_PREFERENCES_NAME = "dock_settings";
  private static final String TAG = "DockService";
  private static final long UNDOCKED_GRACE_PERIOD = 1000L;
  private CheckBox mAudioMediaCheckbox;
  private final CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      if (DockService.-get2(DockService.this) != null)
      {
        LocalBluetoothPreferences.saveDockAutoConnectSetting(DockService.this, DockService.-get2(DockService.this).getAddress(), paramAnonymousBoolean);
        return;
      }
      paramAnonymousCompoundButton = DockService.this.getContentResolver();
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        Settings.Global.putInt(paramAnonymousCompoundButton, "dock_audio_media_enabled", i);
        return;
      }
    }
  };
  private boolean[] mCheckedItems;
  private final DialogInterface.OnClickListener mClickListener = new DialogInterface.OnClickListener()
  {
    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
    {
      int i = 1;
      if (paramAnonymousInt == -1)
      {
        if (DockService.-get2(DockService.this) == null) {
          break label77;
        }
        if (!LocalBluetoothPreferences.hasDockAutoConnectSetting(DockService.this, DockService.-get2(DockService.this).getAddress())) {
          LocalBluetoothPreferences.saveDockAutoConnectSetting(DockService.this, DockService.-get2(DockService.this).getAddress(), true);
        }
        DockService.-wrap0(DockService.this, DockService.-get2(DockService.this), DockService.-get4(DockService.this));
      }
      label77:
      while (DockService.-get0(DockService.this) == null) {
        return;
      }
      paramAnonymousDialogInterface = DockService.this.getContentResolver();
      if (DockService.-get0(DockService.this).isChecked()) {}
      for (paramAnonymousInt = i;; paramAnonymousInt = 0)
      {
        Settings.Global.putInt(paramAnonymousDialogInterface, "dock_audio_media_enabled", paramAnonymousInt);
        return;
      }
    }
  };
  private BluetoothDevice mDevice;
  private CachedBluetoothDeviceManager mDeviceManager;
  private AlertDialog mDialog;
  private final DialogInterface.OnDismissListener mDismissListener = new DialogInterface.OnDismissListener()
  {
    public void onDismiss(DialogInterface paramAnonymousDialogInterface)
    {
      if (DockService.-get3(DockService.this) == null) {
        DockEventReceiver.finishStartingService(DockService.this, DockService.-get4(DockService.this));
      }
      DockService.this.stopForeground(true);
    }
  };
  private LocalBluetoothAdapter mLocalAdapter;
  private final DialogInterface.OnMultiChoiceClickListener mMultiClickListener = new DialogInterface.OnMultiChoiceClickListener()
  {
    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      DockService.-get1(DockService.this)[paramAnonymousInt] = paramAnonymousBoolean;
    }
  };
  private BluetoothDevice mPendingDevice;
  private int mPendingStartId;
  private int mPendingTurnOffStartId = -100;
  private int mPendingTurnOnStartId = -100;
  private LocalBluetoothProfileManager mProfileManager;
  private LocalBluetoothProfile[] mProfiles;
  private Runnable mRunnable;
  private volatile ServiceHandler mServiceHandler;
  private volatile Looper mServiceLooper;
  private int mStartIdAssociatedWithDialog;
  
  private void applyBtSettings(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    int i = 0;
    if (paramBluetoothDevice != null) {}
    Object localObject;
    try
    {
      localObject = this.mProfiles;
      if (localObject == null) {}
      while ((this.mCheckedItems == null) || (this.mLocalAdapter == null)) {
        return;
      }
      localObject = this.mCheckedItems;
      int j = localObject.length;
      while (i < j)
      {
        if (localObject[i] != 0)
        {
          int k = this.mLocalAdapter.getBluetoothState();
          this.mLocalAdapter.enable();
          if (k != 12)
          {
            if (this.mPendingDevice != null)
            {
              boolean bool = this.mPendingDevice.equals(this.mDevice);
              if (bool) {
                return;
              }
            }
            this.mPendingDevice = paramBluetoothDevice;
            this.mPendingStartId = paramInt;
            if (k != 11) {
              getPrefs().edit().putBoolean("disable_bt_when_undock", true).apply();
            }
            return;
          }
        }
        i += 1;
      }
      this.mPendingDevice = null;
      i = 0;
      localObject = getCachedBluetoothDevice(paramBluetoothDevice);
      paramInt = 0;
      if (paramInt < this.mProfiles.length)
      {
        LocalBluetoothProfile localLocalBluetoothProfile = this.mProfiles[paramInt];
        if (this.mCheckedItems[paramInt] != 0) {
          j = 1;
        }
        for (;;)
        {
          localLocalBluetoothProfile.setPreferred(paramBluetoothDevice, this.mCheckedItems[paramInt]);
          paramInt += 1;
          i = j;
          break;
          j = i;
          if (this.mCheckedItems[paramInt] == 0)
          {
            j = i;
            if (localLocalBluetoothProfile.getConnectionStatus(((CachedBluetoothDevice)localObject).getDevice()) == 2)
            {
              ((CachedBluetoothDevice)localObject).disconnect(this.mProfiles[paramInt]);
              j = i;
            }
          }
        }
      }
      if (i == 0) {
        break label293;
      }
    }
    finally {}
    ((CachedBluetoothDevice)localObject).connect(false);
    label293:
  }
  
  private void connectIfEnabled(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      CachedBluetoothDevice localCachedBluetoothDevice = getCachedBluetoothDevice(paramBluetoothDevice);
      Iterator localIterator = localCachedBluetoothDevice.getConnectableProfiles().iterator();
      while (localIterator.hasNext()) {
        if (((LocalBluetoothProfile)localIterator.next()).getPreferred(paramBluetoothDevice) == 1000)
        {
          localCachedBluetoothDevice.connect(false);
          return;
        }
      }
      return;
    }
    finally {}
  }
  
  private void createDialog(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
  {
    if (this.mDialog != null)
    {
      this.mDialog.dismiss();
      this.mDialog = null;
    }
    this.mDevice = paramBluetoothDevice;
    switch (paramInt1)
    {
    default: 
      return;
    }
    startForeground(0, new Notification());
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    Object localObject1 = (LayoutInflater)getSystemService("layout_inflater");
    this.mAudioMediaCheckbox = null;
    if (paramBluetoothDevice != null)
    {
      Object localObject2;
      if (LocalBluetoothPreferences.hasDockAutoConnectSetting(this, paramBluetoothDevice.getAddress()))
      {
        bool = false;
        localObject2 = initBtSettings(paramBluetoothDevice, paramInt1, bool);
        localBuilder.setTitle(getString(2131691291));
        localBuilder.setMultiChoiceItems((CharSequence[])localObject2, this.mCheckedItems, this.mMultiClickListener);
        localObject1 = ((LayoutInflater)localObject1).inflate(2130968948, null);
        localObject2 = (CheckBox)((View)localObject1).findViewById(2131362503);
        if (bool) {
          break label315;
        }
      }
      label315:
      for (bool = LocalBluetoothPreferences.getDockAutoConnectSetting(this, paramBluetoothDevice.getAddress());; bool = true)
      {
        ((CheckBox)localObject2).setChecked(bool);
        ((CheckBox)localObject2).setOnCheckedChangeListener(this.mCheckedChangeListener);
        paramBluetoothDevice = (BluetoothDevice)localObject1;
        float f = getResources().getDisplayMetrics().density;
        localBuilder.setView(paramBluetoothDevice, (int)(14.0F * f), 0, (int)(14.0F * f), 0);
        localBuilder.setPositiveButton(getString(17039370), this.mClickListener);
        this.mStartIdAssociatedWithDialog = paramInt2;
        this.mDialog = localBuilder.create();
        this.mDialog.getWindow().setType(2009);
        this.mDialog.setOnDismissListener(this.mDismissListener);
        this.mDialog.show();
        return;
        bool = true;
        break;
      }
    }
    localBuilder.setTitle(getString(2131691291));
    paramBluetoothDevice = ((LayoutInflater)localObject1).inflate(2130968689, null);
    this.mAudioMediaCheckbox = ((CheckBox)paramBluetoothDevice.findViewById(2131362128));
    if (Settings.Global.getInt(getContentResolver(), "dock_audio_media_enabled", 0) == 1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mAudioMediaCheckbox.setChecked(bool);
      this.mAudioMediaCheckbox.setOnCheckedChangeListener(this.mCheckedChangeListener);
      break;
    }
  }
  
  private CachedBluetoothDevice getCachedBluetoothDevice(BluetoothDevice paramBluetoothDevice)
  {
    CachedBluetoothDevice localCachedBluetoothDevice2 = this.mDeviceManager.findDevice(paramBluetoothDevice);
    CachedBluetoothDevice localCachedBluetoothDevice1 = localCachedBluetoothDevice2;
    if (localCachedBluetoothDevice2 == null) {
      localCachedBluetoothDevice1 = this.mDeviceManager.addDevice(this.mLocalAdapter, this.mProfileManager, paramBluetoothDevice);
    }
    return localCachedBluetoothDevice1;
  }
  
  private SharedPreferences getPrefs()
  {
    return getSharedPreferences("dock_settings", 0);
  }
  
  private void handleBluetoothStateOn(int paramInt)
  {
    if (this.mPendingDevice != null)
    {
      if (this.mPendingDevice.equals(this.mDevice)) {
        applyBtSettings(this.mPendingDevice, this.mPendingStartId);
      }
      this.mPendingDevice = null;
      DockEventReceiver.finishStartingService(this, this.mPendingStartId);
    }
    Object localObject;
    do
    {
      for (;;)
      {
        if (this.mPendingTurnOnStartId != -100)
        {
          DockEventReceiver.finishStartingService(this, this.mPendingTurnOnStartId);
          this.mPendingTurnOnStartId = -100;
        }
        DockEventReceiver.finishStartingService(this, paramInt);
        return;
        localObject = getPrefs();
        Intent localIntent = registerReceiver(null, new IntentFilter("android.intent.action.DOCK_EVENT"));
        if (localIntent != null)
        {
          if (localIntent.getIntExtra("android.intent.extra.DOCK_STATE", 0) == 0) {
            break;
          }
          localObject = (BluetoothDevice)localIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
          if (localObject != null) {
            connectIfEnabled((BluetoothDevice)localObject);
          }
        }
      }
    } while ((!((SharedPreferences)localObject).getBoolean("disable_bt", false)) || (!this.mLocalAdapter.disable()));
    this.mPendingTurnOffStartId = paramInt;
    ((SharedPreferences)localObject).edit().remove("disable_bt").apply();
  }
  
  private void handleBtStateChange(Intent paramIntent, int paramInt)
  {
    int i = paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
    if (i == 12) {}
    for (;;)
    {
      try
      {
        handleBluetoothStateOn(paramInt);
        return;
      }
      finally {}
      if (i == 13)
      {
        getPrefs().edit().remove("disable_bt_when_undock").apply();
        DockEventReceiver.finishStartingService(this, paramInt);
      }
      else if (i == 10)
      {
        if (this.mPendingTurnOffStartId != -100)
        {
          DockEventReceiver.finishStartingService(this, this.mPendingTurnOffStartId);
          getPrefs().edit().remove("disable_bt").apply();
          this.mPendingTurnOffStartId = -100;
        }
        if (this.mPendingDevice != null)
        {
          this.mLocalAdapter.enable();
          this.mPendingTurnOnStartId = paramInt;
        }
        else
        {
          DockEventReceiver.finishStartingService(this, paramInt);
        }
      }
    }
  }
  
  /* Error */
  private void handleDocked(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: ifnull +34 -> 37
    //   6: aload_0
    //   7: aload_1
    //   8: invokevirtual 283	android/bluetooth/BluetoothDevice:getAddress	()Ljava/lang/String;
    //   11: invokestatic 323	com/android/settings/bluetooth/LocalBluetoothPreferences:getDockAutoConnectSetting	(Landroid/content/Context;Ljava/lang/String;)Z
    //   14: ifeq +23 -> 37
    //   17: aload_0
    //   18: aload_1
    //   19: iload_2
    //   20: iconst_0
    //   21: invokespecial 293	com/android/settings/bluetooth/DockService:initBtSettings	(Landroid/bluetooth/BluetoothDevice;IZ)[Ljava/lang/CharSequence;
    //   24: pop
    //   25: aload_0
    //   26: aload_0
    //   27: getfield 111	com/android/settings/bluetooth/DockService:mDevice	Landroid/bluetooth/BluetoothDevice;
    //   30: iload_3
    //   31: invokespecial 124	com/android/settings/bluetooth/DockService:applyBtSettings	(Landroid/bluetooth/BluetoothDevice;I)V
    //   34: aload_0
    //   35: monitorexit
    //   36: return
    //   37: aload_0
    //   38: aload_1
    //   39: iload_2
    //   40: iload_3
    //   41: invokespecial 462	com/android/settings/bluetooth/DockService:createDialog	(Landroid/bluetooth/BluetoothDevice;II)V
    //   44: goto -10 -> 34
    //   47: astore_1
    //   48: aload_0
    //   49: monitorexit
    //   50: aload_1
    //   51: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	52	0	this	DockService
    //   0	52	1	paramBluetoothDevice	BluetoothDevice
    //   0	52	2	paramInt1	int
    //   0	52	3	paramInt2	int
    // Exception table:
    //   from	to	target	type
    //   6	34	47	finally
    //   37	44	47	finally
  }
  
  private void handleUndocked(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      this.mRunnable = null;
      this.mProfileManager.removeServiceListener(this);
      if (this.mDialog != null)
      {
        this.mDialog.dismiss();
        this.mDialog = null;
      }
      this.mDevice = null;
      this.mPendingDevice = null;
      if (paramBluetoothDevice != null) {
        getCachedBluetoothDevice(paramBluetoothDevice).disconnect();
      }
      return;
    }
    finally {}
  }
  
  private void handleUnexpectedDisconnect(BluetoothDevice paramBluetoothDevice, LocalBluetoothProfile paramLocalBluetoothProfile, int paramInt)
  {
    if (paramBluetoothDevice != null) {}
    try
    {
      Object localObject = registerReceiver(null, new IntentFilter("android.intent.action.DOCK_EVENT"));
      if ((localObject != null) && (((Intent)localObject).getIntExtra("android.intent.extra.DOCK_STATE", 0) != 0))
      {
        localObject = (BluetoothDevice)((Intent)localObject).getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        if ((localObject != null) && (((BluetoothDevice)localObject).equals(paramBluetoothDevice))) {
          getCachedBluetoothDevice((BluetoothDevice)localObject).connectProfile(paramLocalBluetoothProfile);
        }
      }
      DockEventReceiver.finishStartingService(this, paramInt);
      return;
    }
    finally {}
  }
  
  private CharSequence[] initBtSettings(BluetoothDevice paramBluetoothDevice, int paramInt, boolean paramBoolean)
  {
    switch (paramInt)
    {
    default: 
      return null;
    }
    CharSequence[] arrayOfCharSequence;
    for (int i = 1;; i = 2)
    {
      this.mProfiles = new LocalBluetoothProfile[i];
      this.mCheckedItems = new boolean[i];
      arrayOfCharSequence = new CharSequence[i];
      switch (paramInt)
      {
      default: 
        return arrayOfCharSequence;
      }
    }
    arrayOfCharSequence[0] = getString(2131691292);
    arrayOfCharSequence[1] = getString(2131691293);
    this.mProfiles[0] = this.mProfileManager.getHeadsetProfile();
    this.mProfiles[1] = this.mProfileManager.getA2dpProfile();
    if (paramBoolean)
    {
      this.mCheckedItems[0] = true;
      this.mCheckedItems[1] = true;
      return arrayOfCharSequence;
    }
    this.mCheckedItems[0] = this.mProfiles[0].isPreferred(paramBluetoothDevice);
    this.mCheckedItems[1] = this.mProfiles[1].isPreferred(paramBluetoothDevice);
    return arrayOfCharSequence;
    arrayOfCharSequence[0] = getString(2131691293);
    this.mProfiles[0] = this.mProfileManager.getA2dpProfile();
    if (paramBoolean)
    {
      this.mCheckedItems[0] = false;
      return arrayOfCharSequence;
    }
    this.mCheckedItems[0] = this.mProfiles[0].isPreferred(paramBluetoothDevice);
    return arrayOfCharSequence;
  }
  
  private boolean msgTypeDisableBluetooth(int paramInt)
  {
    SharedPreferences localSharedPreferences = getPrefs();
    if (this.mLocalAdapter.disable())
    {
      localSharedPreferences.edit().remove("disable_bt_when_undock").apply();
      return false;
    }
    localSharedPreferences.edit().putBoolean("disable_bt", true).apply();
    this.mPendingTurnOffStartId = paramInt;
    return true;
  }
  
  private boolean msgTypeDocked(final BluetoothDevice paramBluetoothDevice, final int paramInt1, final int paramInt2)
  {
    this.mServiceHandler.removeMessages(444);
    this.mServiceHandler.removeMessages(555);
    getPrefs().edit().remove("disable_bt").apply();
    if (paramBluetoothDevice != null) {
      if (!paramBluetoothDevice.equals(this.mDevice))
      {
        if (this.mDevice != null) {
          handleUndocked(this.mDevice);
        }
        this.mDevice = paramBluetoothDevice;
        this.mProfileManager.addServiceListener(this);
        if (!this.mProfileManager.isManagerReady()) {
          break label111;
        }
        handleDocked(paramBluetoothDevice, paramInt1, paramInt2);
        this.mProfileManager.removeServiceListener(this);
      }
    }
    label111:
    while ((Settings.Global.getInt(getContentResolver(), "dock_audio_media_enabled", -1) != -1) || (paramInt1 != 3))
    {
      return false;
      this.mRunnable = new Runnable()
      {
        public void run()
        {
          DockService.-wrap1(DockService.this, paramBluetoothDevice, paramInt1, paramInt2);
        }
      };
      return true;
    }
    handleDocked(null, paramInt1, paramInt2);
    return true;
  }
  
  private boolean msgTypeUndockedPermanent(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    handleUndocked(paramBluetoothDevice);
    if (paramBluetoothDevice != null)
    {
      SharedPreferences localSharedPreferences = getPrefs();
      if (localSharedPreferences.getBoolean("disable_bt_when_undock", false))
      {
        if (!hasOtherConnectedDevices(paramBluetoothDevice)) {
          break label54;
        }
        localSharedPreferences.edit().remove("disable_bt_when_undock").apply();
      }
    }
    return false;
    label54:
    paramBluetoothDevice = this.mServiceHandler.obtainMessage(555, 0, paramInt, null);
    this.mServiceHandler.sendMessageDelayed(paramBluetoothDevice, 2000L);
    return true;
  }
  
  private void msgTypeUndockedTemporary(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
  {
    paramBluetoothDevice = this.mServiceHandler.obtainMessage(444, paramInt1, paramInt2, paramBluetoothDevice);
    this.mServiceHandler.sendMessageDelayed(paramBluetoothDevice, 1000L);
  }
  
  private Message parseIntent(Intent paramIntent)
  {
    BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
    int j = paramIntent.getIntExtra("android.intent.extra.DOCK_STATE", 64302);
    int i;
    switch (j)
    {
    default: 
      return null;
    case 0: 
      i = 333;
    }
    for (;;)
    {
      return this.mServiceHandler.obtainMessage(i, j, 0, localBluetoothDevice);
      if (localBluetoothDevice == null)
      {
        Log.w("DockService", "device is null");
        return null;
      }
      if ("com.android.settings.bluetooth.action.DOCK_SHOW_UI".equals(paramIntent.getAction()))
      {
        if (localBluetoothDevice == null)
        {
          Log.w("DockService", "device is null");
          return null;
        }
        i = 111;
      }
      else
      {
        i = 222;
      }
    }
  }
  
  private void processMessage(Message paramMessage)
  {
    for (;;)
    {
      int i;
      int j;
      int k;
      BluetoothDevice localBluetoothDevice;
      try
      {
        i = paramMessage.what;
        j = paramMessage.arg1;
        k = paramMessage.arg2;
        localBluetoothDevice = null;
        if (paramMessage.obj == null) {
          break label164;
        }
        localBluetoothDevice = (BluetoothDevice)paramMessage.obj;
      }
      finally {}
      if (this.mDialog == null)
      {
        paramMessage = this.mPendingDevice;
        if ((paramMessage == null) && (i != 333) && (!bool1)) {}
      }
      else
      {
        return;
        bool1 = bool2;
        if (localBluetoothDevice == null) {
          continue;
        }
        createDialog(localBluetoothDevice, j, k);
        bool1 = bool2;
        continue;
        bool1 = msgTypeDocked(localBluetoothDevice, j, k);
        continue;
        bool1 = msgTypeUndockedPermanent(localBluetoothDevice, k);
        continue;
        msgTypeUndockedTemporary(localBluetoothDevice, j, k);
        bool1 = bool2;
        continue;
        bool1 = msgTypeDisableBluetooth(k);
        continue;
      }
      DockEventReceiver.finishStartingService(this, k);
      continue;
      label164:
      boolean bool2 = false;
      switch (i)
      {
      }
      boolean bool1 = bool2;
    }
  }
  
  boolean hasOtherConnectedDevices(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      Object localObject = this.mDeviceManager.getCachedDevicesCopy();
      Set localSet = this.mLocalAdapter.getBondedDevices();
      if ((localSet == null) || (localObject == null)) {}
      while (localSet.isEmpty()) {
        return false;
      }
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        CachedBluetoothDevice localCachedBluetoothDevice = (CachedBluetoothDevice)((Iterator)localObject).next();
        BluetoothDevice localBluetoothDevice = localCachedBluetoothDevice.getDevice();
        if ((!localBluetoothDevice.equals(paramBluetoothDevice)) && (localSet.contains(localBluetoothDevice)))
        {
          boolean bool = localCachedBluetoothDevice.isConnected();
          if (bool) {
            return true;
          }
        }
      }
      return false;
    }
    finally {}
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  public void onCreate()
  {
    Object localObject = Utils.getLocalBtManager(this);
    if (localObject == null)
    {
      Log.e("DockService", "Can't get LocalBluetoothManager: exiting");
      return;
    }
    this.mLocalAdapter = ((LocalBluetoothManager)localObject).getBluetoothAdapter();
    this.mDeviceManager = ((LocalBluetoothManager)localObject).getCachedDeviceManager();
    this.mProfileManager = ((LocalBluetoothManager)localObject).getProfileManager();
    if (this.mProfileManager == null)
    {
      Log.e("DockService", "Can't get LocalBluetoothProfileManager: exiting");
      return;
    }
    localObject = new HandlerThread("DockService");
    ((HandlerThread)localObject).start();
    this.mServiceLooper = ((HandlerThread)localObject).getLooper();
    this.mServiceHandler = new ServiceHandler(this.mServiceLooper, null);
  }
  
  public void onDestroy()
  {
    this.mRunnable = null;
    if (this.mDialog != null)
    {
      this.mDialog.dismiss();
      this.mDialog = null;
    }
    if (this.mProfileManager != null) {
      this.mProfileManager.removeServiceListener(this);
    }
    if (this.mServiceLooper != null) {
      this.mServiceLooper.quit();
    }
    this.mLocalAdapter = null;
    this.mDeviceManager = null;
    this.mProfileManager = null;
    this.mServiceLooper = null;
    this.mServiceHandler = null;
  }
  
  public void onServiceConnected()
  {
    try
    {
      if (this.mRunnable != null)
      {
        this.mRunnable.run();
        this.mRunnable = null;
        this.mProfileManager.removeServiceListener(this);
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void onServiceDisconnected() {}
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    if (paramIntent == null)
    {
      DockEventReceiver.finishStartingService(this, paramInt2);
      return 2;
    }
    if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(paramIntent.getAction()))
    {
      handleBtStateChange(paramIntent, paramInt2);
      return 2;
    }
    SharedPreferences localSharedPreferences = getPrefs();
    if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(paramIntent.getAction()))
    {
      paramIntent = (BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
      paramInt1 = localSharedPreferences.getInt("connect_retry_count", 0);
      if (paramInt1 < 6)
      {
        localSharedPreferences.edit().putInt("connect_retry_count", paramInt1 + 1).apply();
        handleUnexpectedDisconnect(paramIntent, this.mProfileManager.getHeadsetProfile(), paramInt2);
      }
      return 2;
    }
    if ("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED".equals(paramIntent.getAction()))
    {
      paramIntent = (BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
      paramInt1 = localSharedPreferences.getInt("connect_retry_count", 0);
      if (paramInt1 < 6)
      {
        localSharedPreferences.edit().putInt("connect_retry_count", paramInt1 + 1).apply();
        handleUnexpectedDisconnect(paramIntent, this.mProfileManager.getA2dpProfile(), paramInt2);
      }
      return 2;
    }
    paramIntent = parseIntent(paramIntent);
    if (paramIntent == null)
    {
      DockEventReceiver.finishStartingService(this, paramInt2);
      return 2;
    }
    if (paramIntent.what == 222) {
      localSharedPreferences.edit().remove("connect_retry_count").apply();
    }
    paramIntent.arg2 = paramInt2;
    processMessage(paramIntent);
    return 2;
  }
  
  public static class DockBluetoothCallback
    implements BluetoothCallback
  {
    private final Context mContext;
    
    public DockBluetoothCallback(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    private static String getDockedDeviceAddress(Context paramContext)
    {
      paramContext = paramContext.registerReceiver(null, new IntentFilter("android.intent.action.DOCK_EVENT"));
      if ((paramContext != null) && (paramContext.getIntExtra("android.intent.extra.DOCK_STATE", 0) != 0))
      {
        paramContext = (BluetoothDevice)paramContext.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        if (paramContext != null) {
          return paramContext.getAddress();
        }
      }
      return null;
    }
    
    public void onBluetoothStateChanged(int paramInt) {}
    
    public void onConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt) {}
    
    public void onDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice) {}
    
    public void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt)
    {
      BluetoothDevice localBluetoothDevice = paramCachedBluetoothDevice.getDevice();
      if ((paramInt == 10) && (localBluetoothDevice.isBluetoothDock()))
      {
        LocalBluetoothPreferences.removeDockAutoConnectSetting(this.mContext, localBluetoothDevice.getAddress());
        if (!localBluetoothDevice.getAddress().equals(getDockedDeviceAddress(this.mContext))) {
          paramCachedBluetoothDevice.setVisible(false);
        }
      }
    }
    
    public void onDeviceDeleted(CachedBluetoothDevice paramCachedBluetoothDevice) {}
    
    public void onScanningStateChanged(boolean paramBoolean)
    {
      LocalBluetoothPreferences.persistDiscoveringTimestamp(this.mContext);
    }
  }
  
  private final class ServiceHandler
    extends Handler
  {
    private ServiceHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      DockService.-wrap2(DockService.this, paramMessage);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\DockService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */