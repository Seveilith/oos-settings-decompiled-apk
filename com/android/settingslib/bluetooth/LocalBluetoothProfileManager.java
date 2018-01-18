package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.ParcelUuid;
import android.os.SystemProperties;
import android.util.Log;
import com.android.settingslib.R.bool;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class LocalBluetoothProfileManager
{
  private static final boolean DEBUG = true;
  private static final String TAG = "LocalBluetoothProfileManager";
  private static LocalBluetoothProfileManager sInstance;
  private A2dpProfile mA2dpProfile;
  private A2dpSinkProfile mA2dpSinkProfile;
  private final Context mContext;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private DunServerProfile mDunProfile;
  private final BluetoothEventManager mEventManager;
  private HeadsetProfile mHeadsetProfile;
  private HfpClientProfile mHfpClientProfile;
  private final HidProfile mHidProfile;
  private final LocalBluetoothAdapter mLocalAdapter;
  private MapProfile mMapProfile;
  private OppProfile mOppProfile;
  private final PanProfile mPanProfile;
  private PbapClientProfile mPbapClientProfile;
  private final PbapServerProfile mPbapProfile;
  private final Map<String, LocalBluetoothProfile> mProfileNameMap = new HashMap();
  private final Collection<ServiceListener> mServiceListeners = new ArrayList();
  private final boolean mUsePbapPce;
  
  LocalBluetoothProfileManager(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, BluetoothEventManager paramBluetoothEventManager)
  {
    this.mContext = paramContext;
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mEventManager = paramBluetoothEventManager;
    this.mUsePbapPce = this.mContext.getResources().getBoolean(R.bool.enable_pbap_pce_profile);
    this.mLocalAdapter.setProfileManager(this);
    this.mEventManager.setProfileManager(this);
    paramLocalBluetoothAdapter = paramLocalBluetoothAdapter.getUuids();
    if (paramLocalBluetoothAdapter != null) {
      updateLocalProfiles(paramLocalBluetoothAdapter);
    }
    this.mHidProfile = new HidProfile(paramContext, this.mLocalAdapter, this.mDeviceManager, this);
    addProfile(this.mHidProfile, "HID", "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");
    this.mPanProfile = new PanProfile(paramContext);
    addPanProfile(this.mPanProfile, "PAN", "android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED");
    Log.d("LocalBluetoothProfileManager", "Adding local MAP profile");
    this.mMapProfile = new MapProfile(this.mContext, this.mLocalAdapter, this.mDeviceManager, this);
    addProfile(this.mMapProfile, "MAP", "android.bluetooth.map.profile.action.CONNECTION_STATE_CHANGED");
    if (SystemProperties.getBoolean("ro.bluetooth.dun", false))
    {
      this.mDunProfile = new DunServerProfile(paramContext);
      addProfile(this.mDunProfile, "DUN Server", "codeaurora.bluetooth.dun.profile.action.CONNECTION_STATE_CHANGED");
    }
    this.mPbapProfile = new PbapServerProfile(paramContext);
    addProfile(this.mPbapProfile, "PBAP Server", "android.bluetooth.pbap.intent.action.PBAP_STATE_CHANGED");
    Log.d("LocalBluetoothProfileManager", "LocalBluetoothProfileManager construction complete");
  }
  
  private void addPanProfile(LocalBluetoothProfile paramLocalBluetoothProfile, String paramString1, String paramString2)
  {
    this.mEventManager.addProfileHandler(paramString2, new PanStateChangedHandler(paramLocalBluetoothProfile));
    this.mProfileNameMap.put(paramString1, paramLocalBluetoothProfile);
  }
  
  private void addProfile(LocalBluetoothProfile paramLocalBluetoothProfile, String paramString1, String paramString2)
  {
    this.mEventManager.addProfileHandler(paramString2, new StateChangedHandler(paramLocalBluetoothProfile));
    this.mProfileNameMap.put(paramString1, paramLocalBluetoothProfile);
  }
  
  public void addServiceListener(ServiceListener paramServiceListener)
  {
    this.mServiceListeners.add(paramServiceListener);
  }
  
  void callServiceConnectedListeners()
  {
    Iterator localIterator = this.mServiceListeners.iterator();
    while (localIterator.hasNext()) {
      ((ServiceListener)localIterator.next()).onServiceConnected();
    }
  }
  
  void callServiceDisconnectedListeners()
  {
    Iterator localIterator = this.mServiceListeners.iterator();
    while (localIterator.hasNext()) {
      ((ServiceListener)localIterator.next()).onServiceDisconnected();
    }
  }
  
  public A2dpProfile getA2dpProfile()
  {
    return this.mA2dpProfile;
  }
  
  public A2dpSinkProfile getA2dpSinkProfile()
  {
    if ((this.mA2dpSinkProfile != null) && (this.mA2dpSinkProfile.isProfileReady())) {
      return this.mA2dpSinkProfile;
    }
    return null;
  }
  
  public HeadsetProfile getHeadsetProfile()
  {
    return this.mHeadsetProfile;
  }
  
  public HfpClientProfile getHfpClientProfile()
  {
    if ((this.mHfpClientProfile != null) && (this.mHfpClientProfile.isProfileReady())) {
      return this.mHfpClientProfile;
    }
    return null;
  }
  
  public MapProfile getMapProfile()
  {
    return this.mMapProfile;
  }
  
  public PbapClientProfile getPbapClientProfile()
  {
    return this.mPbapClientProfile;
  }
  
  public PbapServerProfile getPbapProfile()
  {
    return this.mPbapProfile;
  }
  
  public LocalBluetoothProfile getProfileByName(String paramString)
  {
    return (LocalBluetoothProfile)this.mProfileNameMap.get(paramString);
  }
  
  public boolean isManagerReady()
  {
    try
    {
      Object localObject1 = this.mHeadsetProfile;
      boolean bool;
      if (localObject1 != null)
      {
        bool = ((LocalBluetoothProfile)localObject1).isProfileReady();
        return bool;
      }
      localObject1 = this.mA2dpProfile;
      if (localObject1 != null)
      {
        bool = ((LocalBluetoothProfile)localObject1).isProfileReady();
        return bool;
      }
      localObject1 = this.mA2dpSinkProfile;
      if (localObject1 != null)
      {
        bool = ((LocalBluetoothProfile)localObject1).isProfileReady();
        return bool;
      }
      return false;
    }
    finally {}
  }
  
  public void removeServiceListener(ServiceListener paramServiceListener)
  {
    this.mServiceListeners.remove(paramServiceListener);
  }
  
  void setBluetoothStateOn()
  {
    ParcelUuid[] arrayOfParcelUuid = this.mLocalAdapter.getUuids();
    if (arrayOfParcelUuid != null) {
      updateLocalProfiles(arrayOfParcelUuid);
    }
    this.mEventManager.readPairedDevices();
  }
  
  void updateLocalProfiles(ParcelUuid[] paramArrayOfParcelUuid)
  {
    if (BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.AudioSource))
    {
      if (this.mA2dpProfile == null)
      {
        Log.d("LocalBluetoothProfileManager", "Adding local A2DP SRC profile");
        this.mA2dpProfile = new A2dpProfile(this.mContext, this.mLocalAdapter, this.mDeviceManager, this);
        addProfile(this.mA2dpProfile, "A2DP", "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
      }
      if (!BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.AudioSink)) {
        break label408;
      }
      if (this.mA2dpSinkProfile == null)
      {
        Log.d("LocalBluetoothProfileManager", "Adding local A2DP Sink profile");
        this.mA2dpSinkProfile = new A2dpSinkProfile(this.mContext, this.mLocalAdapter, this.mDeviceManager, this);
        addProfile(this.mA2dpSinkProfile, "A2DPSink", "android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED");
      }
      label128:
      if ((!BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.Handsfree_AG)) && (!BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.HSP_AG))) {
        break label427;
      }
      if (this.mHeadsetProfile == null)
      {
        Log.d("LocalBluetoothProfileManager", "Adding local HEADSET profile");
        this.mHeadsetProfile = new HeadsetProfile(this.mContext, this.mLocalAdapter, this.mDeviceManager, this);
        addProfile(this.mHeadsetProfile, "HEADSET", "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
      }
      label202:
      if (!BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.Handsfree)) {
        break label446;
      }
      if (this.mHfpClientProfile == null)
      {
        Log.d("LocalBluetoothProfileManager", "Adding local HfpClient profile");
        this.mHfpClientProfile = new HfpClientProfile(this.mContext, this.mLocalAdapter, this.mDeviceManager, this);
        addProfile(this.mHfpClientProfile, "HEADSET_CLIENT", "android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED");
      }
      label266:
      if (!BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.ObexObjectPush)) {
        break label477;
      }
      if (this.mOppProfile == null)
      {
        Log.d("LocalBluetoothProfileManager", "Adding local OPP profile");
        this.mOppProfile = new OppProfile();
        this.mProfileNameMap.put("OPP", this.mOppProfile);
      }
      label320:
      if (!this.mUsePbapPce) {
        break label496;
      }
      if (this.mPbapClientProfile == null)
      {
        Log.d("LocalBluetoothProfileManager", "Adding local PBAP Client profile");
        this.mPbapClientProfile = new PbapClientProfile(this.mContext, this.mLocalAdapter, this.mDeviceManager, this);
        addProfile(this.mPbapClientProfile, "PbapClient", "android.bluetooth.pbap.profile.action.CONNECTION_STATE_CHANGED");
      }
    }
    for (;;)
    {
      this.mEventManager.registerProfileIntentReceiver();
      return;
      if (this.mA2dpProfile == null) {
        break;
      }
      Log.w("LocalBluetoothProfileManager", "Warning: A2DP profile was previously added but the UUID is now missing.");
      break;
      label408:
      if (this.mA2dpSinkProfile == null) {
        break label128;
      }
      Log.w("LocalBluetoothProfileManager", "Warning: A2DP Sink profile was previously added but the UUID is now missing.");
      break label128;
      label427:
      if (this.mHeadsetProfile == null) {
        break label202;
      }
      Log.w("LocalBluetoothProfileManager", "Warning: HEADSET profile was previously added but the UUID is now missing.");
      break label202;
      label446:
      if (this.mHfpClientProfile != null)
      {
        Log.w("LocalBluetoothProfileManager", "Warning: Hfp Client profile was previously added but the UUID is now missing.");
        break label266;
      }
      Log.d("LocalBluetoothProfileManager", "Handsfree Uuid not found.");
      break label266;
      label477:
      if (this.mOppProfile == null) {
        break label320;
      }
      Log.w("LocalBluetoothProfileManager", "Warning: OPP profile was previously added but the UUID is now missing.");
      break label320;
      label496:
      if (this.mPbapClientProfile != null) {
        Log.w("LocalBluetoothProfileManager", "Warning: PBAP Client profile was previously added but the UUID is now missing.");
      }
    }
  }
  
  /* Error */
  void updateProfiles(ParcelUuid[] paramArrayOfParcelUuid1, ParcelUuid[] paramArrayOfParcelUuid2, Collection<LocalBluetoothProfile> paramCollection1, Collection<LocalBluetoothProfile> paramCollection2, boolean paramBoolean, BluetoothDevice paramBluetoothDevice)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload 4
    //   4: invokeinterface 393 1 0
    //   9: aload 4
    //   11: aload_3
    //   12: invokeinterface 397 2 0
    //   17: pop
    //   18: ldc 20
    //   20: new 399	java/lang/StringBuilder
    //   23: dup
    //   24: invokespecial 400	java/lang/StringBuilder:<init>	()V
    //   27: ldc_w 402
    //   30: invokevirtual 406	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   33: aload_3
    //   34: invokevirtual 410	java/lang/Object:toString	()Ljava/lang/String;
    //   37: invokevirtual 406	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: invokevirtual 411	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   43: invokestatic 161	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   46: pop
    //   47: aload_3
    //   48: invokeinterface 393 1 0
    //   53: aload_1
    //   54: ifnonnull +6 -> 60
    //   57: aload_0
    //   58: monitorexit
    //   59: return
    //   60: aload_0
    //   61: getfield 260	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mHeadsetProfile	Lcom/android/settingslib/bluetooth/HeadsetProfile;
    //   64: ifnull +66 -> 130
    //   67: aload_2
    //   68: getstatic 328	android/bluetooth/BluetoothUuid:HSP_AG	Landroid/os/ParcelUuid;
    //   71: invokestatic 303	android/bluetooth/BluetoothUuid:isUuidPresent	([Landroid/os/ParcelUuid;Landroid/os/ParcelUuid;)Z
    //   74: ifeq +13 -> 87
    //   77: aload_1
    //   78: getstatic 414	android/bluetooth/BluetoothUuid:HSP	Landroid/os/ParcelUuid;
    //   81: invokestatic 303	android/bluetooth/BluetoothUuid:isUuidPresent	([Landroid/os/ParcelUuid;Landroid/os/ParcelUuid;)Z
    //   84: ifne +23 -> 107
    //   87: aload_2
    //   88: getstatic 325	android/bluetooth/BluetoothUuid:Handsfree_AG	Landroid/os/ParcelUuid;
    //   91: invokestatic 303	android/bluetooth/BluetoothUuid:isUuidPresent	([Landroid/os/ParcelUuid;Landroid/os/ParcelUuid;)Z
    //   94: ifeq +36 -> 130
    //   97: aload_1
    //   98: getstatic 340	android/bluetooth/BluetoothUuid:Handsfree	Landroid/os/ParcelUuid;
    //   101: invokestatic 303	android/bluetooth/BluetoothUuid:isUuidPresent	([Landroid/os/ParcelUuid;Landroid/os/ParcelUuid;)Z
    //   104: ifeq +26 -> 130
    //   107: aload_3
    //   108: aload_0
    //   109: getfield 260	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mHeadsetProfile	Lcom/android/settingslib/bluetooth/HeadsetProfile;
    //   112: invokeinterface 219 2 0
    //   117: pop
    //   118: aload 4
    //   120: aload_0
    //   121: getfield 260	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mHeadsetProfile	Lcom/android/settingslib/bluetooth/HeadsetProfile;
    //   124: invokeinterface 289 2 0
    //   129: pop
    //   130: aload_0
    //   131: getfield 264	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mHfpClientProfile	Lcom/android/settingslib/bluetooth/HfpClientProfile;
    //   134: ifnull +46 -> 180
    //   137: aload_1
    //   138: getstatic 325	android/bluetooth/BluetoothUuid:Handsfree_AG	Landroid/os/ParcelUuid;
    //   141: invokestatic 303	android/bluetooth/BluetoothUuid:isUuidPresent	([Landroid/os/ParcelUuid;Landroid/os/ParcelUuid;)Z
    //   144: ifeq +36 -> 180
    //   147: aload_2
    //   148: getstatic 340	android/bluetooth/BluetoothUuid:Handsfree	Landroid/os/ParcelUuid;
    //   151: invokestatic 303	android/bluetooth/BluetoothUuid:isUuidPresent	([Landroid/os/ParcelUuid;Landroid/os/ParcelUuid;)Z
    //   154: ifeq +26 -> 180
    //   157: aload_3
    //   158: aload_0
    //   159: getfield 264	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mHfpClientProfile	Lcom/android/settingslib/bluetooth/HfpClientProfile;
    //   162: invokeinterface 219 2 0
    //   167: pop
    //   168: aload 4
    //   170: aload_0
    //   171: getfield 264	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mHfpClientProfile	Lcom/android/settingslib/bluetooth/HfpClientProfile;
    //   174: invokeinterface 289 2 0
    //   179: pop
    //   180: aload_1
    //   181: getstatic 418	com/android/settingslib/bluetooth/A2dpProfile:SINK_UUIDS	[Landroid/os/ParcelUuid;
    //   184: invokestatic 422	android/bluetooth/BluetoothUuid:containsAnyUuid	([Landroid/os/ParcelUuid;[Landroid/os/ParcelUuid;)Z
    //   187: ifeq +33 -> 220
    //   190: aload_0
    //   191: getfield 247	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mA2dpProfile	Lcom/android/settingslib/bluetooth/A2dpProfile;
    //   194: ifnull +26 -> 220
    //   197: aload_3
    //   198: aload_0
    //   199: getfield 247	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mA2dpProfile	Lcom/android/settingslib/bluetooth/A2dpProfile;
    //   202: invokeinterface 219 2 0
    //   207: pop
    //   208: aload 4
    //   210: aload_0
    //   211: getfield 247	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mA2dpProfile	Lcom/android/settingslib/bluetooth/A2dpProfile;
    //   214: invokeinterface 289 2 0
    //   219: pop
    //   220: aload_1
    //   221: getstatic 425	com/android/settingslib/bluetooth/A2dpSinkProfile:SRC_UUIDS	[Landroid/os/ParcelUuid;
    //   224: invokestatic 422	android/bluetooth/BluetoothUuid:containsAnyUuid	([Landroid/os/ParcelUuid;[Landroid/os/ParcelUuid;)Z
    //   227: ifeq +33 -> 260
    //   230: aload_0
    //   231: getfield 251	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mA2dpSinkProfile	Lcom/android/settingslib/bluetooth/A2dpSinkProfile;
    //   234: ifnull +26 -> 260
    //   237: aload_3
    //   238: aload_0
    //   239: getfield 251	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mA2dpSinkProfile	Lcom/android/settingslib/bluetooth/A2dpSinkProfile;
    //   242: invokeinterface 219 2 0
    //   247: pop
    //   248: aload 4
    //   250: aload_0
    //   251: getfield 251	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mA2dpSinkProfile	Lcom/android/settingslib/bluetooth/A2dpSinkProfile;
    //   254: invokeinterface 289 2 0
    //   259: pop
    //   260: aload_1
    //   261: getstatic 350	android/bluetooth/BluetoothUuid:ObexObjectPush	Landroid/os/ParcelUuid;
    //   264: invokestatic 303	android/bluetooth/BluetoothUuid:isUuidPresent	([Landroid/os/ParcelUuid;Landroid/os/ParcelUuid;)Z
    //   267: ifeq +33 -> 300
    //   270: aload_0
    //   271: getfield 352	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mOppProfile	Lcom/android/settingslib/bluetooth/OppProfile;
    //   274: ifnull +26 -> 300
    //   277: aload_3
    //   278: aload_0
    //   279: getfield 352	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mOppProfile	Lcom/android/settingslib/bluetooth/OppProfile;
    //   282: invokeinterface 219 2 0
    //   287: pop
    //   288: aload 4
    //   290: aload_0
    //   291: getfield 352	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mOppProfile	Lcom/android/settingslib/bluetooth/OppProfile;
    //   294: invokeinterface 289 2 0
    //   299: pop
    //   300: aload_1
    //   301: getstatic 428	android/bluetooth/BluetoothUuid:Hid	Landroid/os/ParcelUuid;
    //   304: invokestatic 303	android/bluetooth/BluetoothUuid:isUuidPresent	([Landroid/os/ParcelUuid;Landroid/os/ParcelUuid;)Z
    //   307: ifne +13 -> 320
    //   310: aload_1
    //   311: getstatic 431	android/bluetooth/BluetoothUuid:Hogp	Landroid/os/ParcelUuid;
    //   314: invokestatic 303	android/bluetooth/BluetoothUuid:isUuidPresent	([Landroid/os/ParcelUuid;Landroid/os/ParcelUuid;)Z
    //   317: ifeq +33 -> 350
    //   320: aload_0
    //   321: getfield 131	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mHidProfile	Lcom/android/settingslib/bluetooth/HidProfile;
    //   324: ifnull +26 -> 350
    //   327: aload_3
    //   328: aload_0
    //   329: getfield 131	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mHidProfile	Lcom/android/settingslib/bluetooth/HidProfile;
    //   332: invokeinterface 219 2 0
    //   337: pop
    //   338: aload 4
    //   340: aload_0
    //   341: getfield 131	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mHidProfile	Lcom/android/settingslib/bluetooth/HidProfile;
    //   344: invokeinterface 289 2 0
    //   349: pop
    //   350: iload 5
    //   352: ifeq +12 -> 364
    //   355: ldc 20
    //   357: ldc_w 433
    //   360: invokestatic 161	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   363: pop
    //   364: aload_1
    //   365: getstatic 436	android/bluetooth/BluetoothUuid:NAP	Landroid/os/ParcelUuid;
    //   368: invokestatic 303	android/bluetooth/BluetoothUuid:isUuidPresent	([Landroid/os/ParcelUuid;Landroid/os/ParcelUuid;)Z
    //   371: ifeq +224 -> 595
    //   374: aload_0
    //   375: getfield 146	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPanProfile	Lcom/android/settingslib/bluetooth/PanProfile;
    //   378: ifnull +217 -> 595
    //   381: aload_3
    //   382: aload_0
    //   383: getfield 146	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPanProfile	Lcom/android/settingslib/bluetooth/PanProfile;
    //   386: invokeinterface 219 2 0
    //   391: pop
    //   392: aload 4
    //   394: aload_0
    //   395: getfield 146	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPanProfile	Lcom/android/settingslib/bluetooth/PanProfile;
    //   398: invokeinterface 289 2 0
    //   403: pop
    //   404: aload_0
    //   405: getfield 166	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mMapProfile	Lcom/android/settingslib/bluetooth/MapProfile;
    //   408: ifnull +49 -> 457
    //   411: aload_0
    //   412: getfield 166	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mMapProfile	Lcom/android/settingslib/bluetooth/MapProfile;
    //   415: aload 6
    //   417: invokevirtual 440	com/android/settingslib/bluetooth/MapProfile:getConnectionStatus	(Landroid/bluetooth/BluetoothDevice;)I
    //   420: iconst_2
    //   421: if_icmpne +36 -> 457
    //   424: aload_3
    //   425: aload_0
    //   426: getfield 166	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mMapProfile	Lcom/android/settingslib/bluetooth/MapProfile;
    //   429: invokeinterface 219 2 0
    //   434: pop
    //   435: aload 4
    //   437: aload_0
    //   438: getfield 166	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mMapProfile	Lcom/android/settingslib/bluetooth/MapProfile;
    //   441: invokeinterface 289 2 0
    //   446: pop
    //   447: aload_0
    //   448: getfield 166	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mMapProfile	Lcom/android/settingslib/bluetooth/MapProfile;
    //   451: aload 6
    //   453: iconst_1
    //   454: invokevirtual 444	com/android/settingslib/bluetooth/MapProfile:setPreferred	(Landroid/bluetooth/BluetoothDevice;Z)V
    //   457: aload_0
    //   458: getfield 107	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mUsePbapPce	Z
    //   461: ifeq +49 -> 510
    //   464: aload_3
    //   465: aload_0
    //   466: getfield 273	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPbapClientProfile	Lcom/android/settingslib/bluetooth/PbapClientProfile;
    //   469: invokeinterface 219 2 0
    //   474: pop
    //   475: aload 4
    //   477: aload_0
    //   478: getfield 273	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPbapClientProfile	Lcom/android/settingslib/bluetooth/PbapClientProfile;
    //   481: invokeinterface 289 2 0
    //   486: pop
    //   487: aload_3
    //   488: aload_0
    //   489: getfield 191	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPbapProfile	Lcom/android/settingslib/bluetooth/PbapServerProfile;
    //   492: invokeinterface 289 2 0
    //   497: pop
    //   498: aload 4
    //   500: aload_0
    //   501: getfield 191	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPbapProfile	Lcom/android/settingslib/bluetooth/PbapServerProfile;
    //   504: invokeinterface 219 2 0
    //   509: pop
    //   510: aload_0
    //   511: getfield 191	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPbapProfile	Lcom/android/settingslib/bluetooth/PbapServerProfile;
    //   514: ifnull +49 -> 563
    //   517: aload_0
    //   518: getfield 191	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPbapProfile	Lcom/android/settingslib/bluetooth/PbapServerProfile;
    //   521: aload 6
    //   523: invokevirtual 445	com/android/settingslib/bluetooth/PbapServerProfile:getConnectionStatus	(Landroid/bluetooth/BluetoothDevice;)I
    //   526: iconst_2
    //   527: if_icmpne +36 -> 563
    //   530: aload_3
    //   531: aload_0
    //   532: getfield 191	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPbapProfile	Lcom/android/settingslib/bluetooth/PbapServerProfile;
    //   535: invokeinterface 219 2 0
    //   540: pop
    //   541: aload 4
    //   543: aload_0
    //   544: getfield 191	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPbapProfile	Lcom/android/settingslib/bluetooth/PbapServerProfile;
    //   547: invokeinterface 289 2 0
    //   552: pop
    //   553: aload_0
    //   554: getfield 191	com/android/settingslib/bluetooth/LocalBluetoothProfileManager:mPbapProfile	Lcom/android/settingslib/bluetooth/PbapServerProfile;
    //   557: aload 6
    //   559: iconst_1
    //   560: invokevirtual 446	com/android/settingslib/bluetooth/PbapServerProfile:setPreferred	(Landroid/bluetooth/BluetoothDevice;Z)V
    //   563: ldc 20
    //   565: new 399	java/lang/StringBuilder
    //   568: dup
    //   569: invokespecial 400	java/lang/StringBuilder:<init>	()V
    //   572: ldc_w 448
    //   575: invokevirtual 406	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   578: aload_3
    //   579: invokevirtual 410	java/lang/Object:toString	()Ljava/lang/String;
    //   582: invokevirtual 406	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   585: invokevirtual 411	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   588: invokestatic 161	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   591: pop
    //   592: aload_0
    //   593: monitorexit
    //   594: return
    //   595: iload 5
    //   597: ifeq -193 -> 404
    //   600: goto -219 -> 381
    //   603: astore_1
    //   604: aload_0
    //   605: monitorexit
    //   606: aload_1
    //   607: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	608	0	this	LocalBluetoothProfileManager
    //   0	608	1	paramArrayOfParcelUuid1	ParcelUuid[]
    //   0	608	2	paramArrayOfParcelUuid2	ParcelUuid[]
    //   0	608	3	paramCollection1	Collection<LocalBluetoothProfile>
    //   0	608	4	paramCollection2	Collection<LocalBluetoothProfile>
    //   0	608	5	paramBoolean	boolean
    //   0	608	6	paramBluetoothDevice	BluetoothDevice
    // Exception table:
    //   from	to	target	type
    //   2	53	603	finally
    //   60	87	603	finally
    //   87	107	603	finally
    //   107	130	603	finally
    //   130	180	603	finally
    //   180	220	603	finally
    //   220	260	603	finally
    //   260	300	603	finally
    //   300	320	603	finally
    //   320	350	603	finally
    //   355	364	603	finally
    //   364	381	603	finally
    //   381	404	603	finally
    //   404	457	603	finally
    //   457	510	603	finally
    //   510	563	603	finally
    //   563	592	603	finally
  }
  
  private class PanStateChangedHandler
    extends LocalBluetoothProfileManager.StateChangedHandler
  {
    PanStateChangedHandler(LocalBluetoothProfile paramLocalBluetoothProfile)
    {
      super(paramLocalBluetoothProfile);
    }
    
    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      ((PanProfile)this.mProfile).setLocalRole(paramBluetoothDevice, paramIntent.getIntExtra("android.bluetooth.pan.extra.LOCAL_ROLE", 0));
      super.onReceive(paramContext, paramIntent, paramBluetoothDevice);
    }
  }
  
  public static abstract interface ServiceListener
  {
    public abstract void onServiceConnected();
    
    public abstract void onServiceDisconnected();
  }
  
  private class StateChangedHandler
    implements BluetoothEventManager.Handler
  {
    final LocalBluetoothProfile mProfile;
    
    StateChangedHandler(LocalBluetoothProfile paramLocalBluetoothProfile)
    {
      this.mProfile = paramLocalBluetoothProfile;
    }
    
    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      CachedBluetoothDevice localCachedBluetoothDevice = LocalBluetoothProfileManager.-get0(LocalBluetoothProfileManager.this).findDevice(paramBluetoothDevice);
      paramContext = localCachedBluetoothDevice;
      if (localCachedBluetoothDevice == null)
      {
        Log.w("LocalBluetoothProfileManager", "StateChangedHandler found new device: " + paramBluetoothDevice);
        paramContext = LocalBluetoothProfileManager.-get0(LocalBluetoothProfileManager.this).addDevice(LocalBluetoothProfileManager.-get1(LocalBluetoothProfileManager.this), LocalBluetoothProfileManager.this, paramBluetoothDevice);
      }
      int i = paramIntent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
      int j = paramIntent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 0);
      if ((i == 0) && (j == 1)) {
        Log.i("LocalBluetoothProfileManager", "Failed to connect " + this.mProfile + " device");
      }
      paramContext.onProfileStateChanged(this.mProfile, i);
      paramContext.refresh();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\LocalBluetoothProfileManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */