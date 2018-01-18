package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.R.string;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class CachedBluetoothDevice
  implements Comparable<CachedBluetoothDevice>
{
  public static final int ACCESS_ALLOWED = 1;
  public static final int ACCESS_REJECTED = 2;
  public static final int ACCESS_UNKNOWN = 0;
  private static final boolean DEBUG = false;
  private static final long MAX_HOGP_DELAY_FOR_AUTO_CONNECT = 30000L;
  private static final long MAX_UUID_DELAY_FOR_AUTO_CONNECT = 5000L;
  private static final int MESSAGE_REJECTION_COUNT_LIMIT_TO_PERSIST = 2;
  private static final String MESSAGE_REJECTION_COUNT_PREFS_NAME = "bluetooth_message_reject";
  private static final String TAG = "CachedBluetoothDevice";
  private BluetoothClass mBtClass;
  private final Collection<Callback> mCallbacks = new ArrayList();
  private boolean mConnectAfterPairing;
  private long mConnectAttempted;
  private final Context mContext;
  private final BluetoothDevice mDevice;
  private boolean mIsConnectingErrorPossible;
  private final LocalBluetoothAdapter mLocalAdapter;
  private boolean mLocalNapRoleConnected;
  private int mMessageRejectionCount;
  private String mName;
  private HashMap<LocalBluetoothProfile, Integer> mProfileConnectionState;
  private final LocalBluetoothProfileManager mProfileManager;
  private final List<LocalBluetoothProfile> mProfiles = new ArrayList();
  private final List<LocalBluetoothProfile> mRemovedProfiles = new ArrayList();
  private short mRssi;
  private boolean mVisible;
  
  CachedBluetoothDevice(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, LocalBluetoothProfileManager paramLocalBluetoothProfileManager, BluetoothDevice paramBluetoothDevice)
  {
    this.mContext = paramContext;
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mDevice = paramBluetoothDevice;
    this.mProfileConnectionState = new HashMap();
    fillData();
  }
  
  private void connectAutoConnectableProfiles()
  {
    if (!ensurePaired()) {
      return;
    }
    this.mIsConnectingErrorPossible = true;
    Iterator localIterator = this.mProfiles.iterator();
    while (localIterator.hasNext())
    {
      LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile)localIterator.next();
      if (localLocalBluetoothProfile.isAutoConnectable())
      {
        localLocalBluetoothProfile.setPreferred(this.mDevice, true);
        connectInt(localLocalBluetoothProfile);
      }
    }
  }
  
  private void connectWithoutResettingTimer(boolean paramBoolean)
  {
    if (this.mProfiles.isEmpty())
    {
      Log.d("CachedBluetoothDevice", "No profiles. Maybe we will connect later");
      return;
    }
    this.mIsConnectingErrorPossible = true;
    int i = 0;
    Iterator localIterator = this.mProfiles.iterator();
    label113:
    while (localIterator.hasNext())
    {
      LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile)localIterator.next();
      if (paramBoolean) {}
      for (boolean bool = localLocalBluetoothProfile.isConnectable();; bool = localLocalBluetoothProfile.isAutoConnectable())
      {
        if ((!bool) || (!localLocalBluetoothProfile.isPreferred(this.mDevice))) {
          break label113;
        }
        i += 1;
        connectInt(localLocalBluetoothProfile);
        break;
      }
    }
    if (i == 0) {
      connectAutoConnectableProfiles();
    }
  }
  
  private String describe(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Address:").append(this.mDevice);
    if (paramLocalBluetoothProfile != null) {
      localStringBuilder.append(" Profile:").append(paramLocalBluetoothProfile);
    }
    return localStringBuilder.toString();
  }
  
  private void dispatchAttributesChanged()
  {
    synchronized (this.mCallbacks)
    {
      Iterator localIterator = this.mCallbacks.iterator();
      if (localIterator.hasNext()) {
        ((Callback)localIterator.next()).onDeviceAttributesChanged();
      }
    }
  }
  
  private boolean ensurePaired()
  {
    if (getBondState() == 10)
    {
      startPairing();
      return false;
    }
    return true;
  }
  
  private void fetchBtClass()
  {
    this.mBtClass = this.mDevice.getBluetoothClass();
  }
  
  private void fetchMessageRejectionCount()
  {
    this.mMessageRejectionCount = this.mContext.getSharedPreferences("bluetooth_message_reject", 0).getInt(this.mDevice.getAddress(), 0);
  }
  
  private void fetchName()
  {
    if (this.mDevice != null)
    {
      this.mName = this.mDevice.getAliasName();
      if (TextUtils.isEmpty(this.mName)) {
        this.mName = this.mDevice.getAddress();
      }
    }
  }
  
  private void fillData()
  {
    fetchName();
    fetchBtClass();
    updateProfiles();
    migratePhonebookPermissionChoice();
    migrateMessagePermissionChoice();
    fetchMessageRejectionCount();
    this.mVisible = false;
    dispatchAttributesChanged();
  }
  
  private void migrateMessagePermissionChoice()
  {
    Object localObject = this.mContext.getSharedPreferences("bluetooth_message_permission", 0);
    if (!((SharedPreferences)localObject).contains(this.mDevice.getAddress())) {
      return;
    }
    int i;
    if (this.mDevice.getMessageAccessPermission() == 0)
    {
      i = ((SharedPreferences)localObject).getInt(this.mDevice.getAddress(), 0);
      if (i != 1) {
        break label96;
      }
      this.mDevice.setMessageAccessPermission(1);
    }
    for (;;)
    {
      localObject = ((SharedPreferences)localObject).edit();
      ((SharedPreferences.Editor)localObject).remove(this.mDevice.getAddress());
      ((SharedPreferences.Editor)localObject).commit();
      return;
      label96:
      if (i == 2) {
        this.mDevice.setMessageAccessPermission(2);
      }
    }
  }
  
  private void migratePhonebookPermissionChoice()
  {
    Object localObject = this.mContext.getSharedPreferences("bluetooth_phonebook_permission", 0);
    if (!((SharedPreferences)localObject).contains(this.mDevice.getAddress())) {
      return;
    }
    int i;
    if (this.mDevice.getPhonebookAccessPermission() == 0)
    {
      i = ((SharedPreferences)localObject).getInt(this.mDevice.getAddress(), 0);
      if (i != 1) {
        break label97;
      }
      this.mDevice.setPhonebookAccessPermission(1);
    }
    for (;;)
    {
      localObject = ((SharedPreferences)localObject).edit();
      ((SharedPreferences.Editor)localObject).remove(this.mDevice.getAddress());
      ((SharedPreferences.Editor)localObject).commit();
      return;
      label97:
      if (i == 2) {
        this.mDevice.setPhonebookAccessPermission(2);
      }
    }
  }
  
  private void processPhonebookAccess()
  {
    if (this.mDevice.getBondState() != 12) {
      return;
    }
    if ((BluetoothUuid.containsAnyUuid(this.mDevice.getUuids(), PbapServerProfile.PBAB_CLIENT_UUIDS)) && (getPhonebookPermissionChoice() == 0))
    {
      if ((this.mDevice.getBluetoothClass().getDeviceClass() == 1032) || (this.mDevice.getBluetoothClass().getDeviceClass() == 1028)) {
        setPhonebookPermissionChoice(1);
      }
    }
    else {
      return;
    }
    setPhonebookPermissionChoice(0);
  }
  
  private void saveMessageRejectionCount()
  {
    SharedPreferences.Editor localEditor = this.mContext.getSharedPreferences("bluetooth_message_reject", 0).edit();
    if (this.mMessageRejectionCount == 0) {
      localEditor.remove(this.mDevice.getAddress());
    }
    for (;;)
    {
      localEditor.commit();
      return;
      localEditor.putInt(this.mDevice.getAddress(), this.mMessageRejectionCount);
    }
  }
  
  private boolean updateProfiles()
  {
    ParcelUuid[] arrayOfParcelUuid1 = this.mDevice.getUuids();
    if (arrayOfParcelUuid1 == null) {
      return false;
    }
    ParcelUuid[] arrayOfParcelUuid2 = this.mLocalAdapter.getUuids();
    if (arrayOfParcelUuid2 == null) {
      return false;
    }
    processPhonebookAccess();
    this.mProfileManager.updateProfiles(arrayOfParcelUuid1, arrayOfParcelUuid2, this.mProfiles, this.mRemovedProfiles, this.mLocalNapRoleConnected, this.mDevice);
    return true;
  }
  
  public boolean checkAndIncreaseMessageRejectionCount()
  {
    if (this.mMessageRejectionCount < 2)
    {
      this.mMessageRejectionCount += 1;
      saveMessageRejectionCount();
    }
    return this.mMessageRejectionCount >= 2;
  }
  
  public void clearProfileConnectionState()
  {
    Log.d("CachedBluetoothDevice", " Clearing all connection state for dev:" + this.mDevice.getName());
    Iterator localIterator = getProfiles().iterator();
    while (localIterator.hasNext())
    {
      LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile)localIterator.next();
      this.mProfileConnectionState.put(localLocalBluetoothProfile, Integer.valueOf(0));
    }
  }
  
  public int compareTo(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    int k = 1;
    if (paramCachedBluetoothDevice.isConnected())
    {
      i = 1;
      if (!isConnected()) {
        break label36;
      }
    }
    label36:
    for (int j = 1;; j = 0)
    {
      i -= j;
      if (i == 0) {
        break label41;
      }
      return i;
      i = 0;
      break;
    }
    label41:
    if (paramCachedBluetoothDevice.getBondState() == 12)
    {
      i = 1;
      if (getBondState() != 12) {
        break label78;
      }
    }
    label78:
    for (j = 1;; j = 0)
    {
      i -= j;
      if (i == 0) {
        break label83;
      }
      return i;
      i = 0;
      break;
    }
    label83:
    if (paramCachedBluetoothDevice.mVisible)
    {
      i = 1;
      if (!this.mVisible) {
        break label117;
      }
    }
    label117:
    for (j = k;; j = 0)
    {
      i -= j;
      if (i == 0) {
        break label122;
      }
      return i;
      i = 0;
      break;
    }
    label122:
    int i = paramCachedBluetoothDevice.mRssi - this.mRssi;
    if (i != 0) {
      return i;
    }
    return this.mName.compareTo(paramCachedBluetoothDevice.mName);
  }
  
  public void connect(boolean paramBoolean)
  {
    if (!ensurePaired()) {
      return;
    }
    this.mConnectAttempted = SystemClock.elapsedRealtime();
    connectWithoutResettingTimer(paramBoolean);
  }
  
  void connectInt(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    try
    {
      boolean bool = ensurePaired();
      if (!bool) {
        return;
      }
      if (paramLocalBluetoothProfile.connect(this.mDevice))
      {
        Log.d("CachedBluetoothDevice", "Command sent successfully:CONNECT " + describe(paramLocalBluetoothProfile));
        return;
      }
      Log.i("CachedBluetoothDevice", "Failed to connect " + paramLocalBluetoothProfile.toString() + " to " + this.mName);
      return;
    }
    finally {}
  }
  
  public void connectProfile(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    this.mConnectAttempted = SystemClock.elapsedRealtime();
    this.mIsConnectingErrorPossible = true;
    connectInt(paramLocalBluetoothProfile);
    refresh();
  }
  
  public void disconnect()
  {
    Object localObject = this.mProfiles.iterator();
    while (((Iterator)localObject).hasNext()) {
      disconnect((LocalBluetoothProfile)((Iterator)localObject).next());
    }
    localObject = this.mProfileManager.getPbapProfile();
    if (((PbapServerProfile)localObject).getConnectionStatus(this.mDevice) == 2) {
      ((PbapServerProfile)localObject).disconnect(this.mDevice);
    }
  }
  
  public void disconnect(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    if (paramLocalBluetoothProfile.disconnect(this.mDevice)) {
      Log.d("CachedBluetoothDevice", "Command sent successfully:DISCONNECT " + describe(paramLocalBluetoothProfile));
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof CachedBluetoothDevice))) {
      return this.mDevice.equals(((CachedBluetoothDevice)paramObject).mDevice);
    }
    return false;
  }
  
  public int getBondState()
  {
    return this.mDevice.getBondState();
  }
  
  public BluetoothClass getBtClass()
  {
    return this.mBtClass;
  }
  
  public List<LocalBluetoothProfile> getConnectableProfiles()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mProfiles.iterator();
    while (localIterator.hasNext())
    {
      LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile)localIterator.next();
      if (localLocalBluetoothProfile.isConnectable()) {
        localArrayList.add(localLocalBluetoothProfile);
      }
    }
    return localArrayList;
  }
  
  public int getConnectionSummary()
  {
    boolean bool = false;
    int j = 0;
    int i = 0;
    Iterator localIterator = getProfiles().iterator();
    while (localIterator.hasNext())
    {
      LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile)localIterator.next();
      int k = getProfileConnectionState(localLocalBluetoothProfile);
      switch (k)
      {
      default: 
        break;
      case 0: 
        if (localLocalBluetoothProfile.isProfileReady()) {
          if (((localLocalBluetoothProfile instanceof A2dpProfile)) || ((localLocalBluetoothProfile instanceof A2dpSinkProfile))) {
            j = 1;
          }
        }
        break;
      case 1: 
      case 3: 
        return Utils.getConnectionStateSummary(k);
      case 2: 
        Log.e("CachedBluetoothDevice", "STATE_CONNECTED mDevice = " + this.mDevice);
        bool = true;
        continue;
        if (((localLocalBluetoothProfile instanceof HeadsetProfile)) || ((localLocalBluetoothProfile instanceof HfpClientProfile))) {
          i = 1;
        }
        break;
      }
    }
    Log.e("CachedBluetoothDevice", "bluetooth_connected mDevice = " + this.mDevice + "profileConnected = " + bool);
    if (bool)
    {
      if ((j != 0) && (i != 0)) {
        return R.string.bluetooth_connected_no_headset_no_a2dp;
      }
      if (j != 0) {
        return R.string.bluetooth_connected_no_a2dp;
      }
      if (i != 0) {
        return R.string.bluetooth_connected_no_headset;
      }
      Log.e("CachedBluetoothDevice", "bluetooth_connected mDevice = " + this.mDevice);
      return R.string.bluetooth_connected;
    }
    if (getBondState() == 11) {
      return R.string.bluetooth_pairing;
    }
    return 0;
  }
  
  public BluetoothDevice getDevice()
  {
    return this.mDevice;
  }
  
  public int getMaxConnectionState()
  {
    int i = 0;
    Iterator localIterator = getProfiles().iterator();
    while (localIterator.hasNext())
    {
      int j = getProfileConnectionState((LocalBluetoothProfile)localIterator.next());
      if (j > i) {
        i = j;
      }
    }
    return i;
  }
  
  public int getMessagePermissionChoice()
  {
    int i = this.mDevice.getMessageAccessPermission();
    if (i == 1) {
      return 1;
    }
    if (i == 2) {
      return 2;
    }
    return 0;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int getPhonebookPermissionChoice()
  {
    int i = this.mDevice.getPhonebookAccessPermission();
    if (i == 1) {
      return 1;
    }
    if (i == 2) {
      return 2;
    }
    return 0;
  }
  
  public int getProfileConnectionState(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    if ((this.mProfileConnectionState == null) || (this.mProfileConnectionState.get(paramLocalBluetoothProfile) == null))
    {
      int i = paramLocalBluetoothProfile.getConnectionStatus(this.mDevice);
      this.mProfileConnectionState.put(paramLocalBluetoothProfile, Integer.valueOf(i));
    }
    return ((Integer)this.mProfileConnectionState.get(paramLocalBluetoothProfile)).intValue();
  }
  
  public List<LocalBluetoothProfile> getProfiles()
  {
    return Collections.unmodifiableList(this.mProfiles);
  }
  
  public List<LocalBluetoothProfile> getRemovedProfiles()
  {
    return this.mRemovedProfiles;
  }
  
  public int getSimPermissionChoice()
  {
    int i = this.mDevice.getSimAccessPermission();
    if (i == 1) {
      return 1;
    }
    if (i == 2) {
      return 2;
    }
    return 0;
  }
  
  public int hashCode()
  {
    return this.mDevice.getAddress().hashCode();
  }
  
  public boolean isBusy()
  {
    Iterator localIterator = this.mProfiles.iterator();
    while (localIterator.hasNext())
    {
      int i = getProfileConnectionState((LocalBluetoothProfile)localIterator.next());
      if ((i == 1) || (i == 3)) {
        return true;
      }
    }
    return getBondState() == 11;
  }
  
  public boolean isConnected()
  {
    Iterator localIterator = this.mProfiles.iterator();
    while (localIterator.hasNext()) {
      if (getProfileConnectionState((LocalBluetoothProfile)localIterator.next()) == 2) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isConnectedProfile(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    return getProfileConnectionState(paramLocalBluetoothProfile) == 2;
  }
  
  boolean isUserInitiatedPairing()
  {
    return this.mConnectAfterPairing;
  }
  
  public boolean isVisible()
  {
    return this.mVisible;
  }
  
  void onBondingDockConnect()
  {
    connect(false);
  }
  
  void onBondingStateChanged(int paramInt)
  {
    if (paramInt == 10)
    {
      this.mProfiles.clear();
      this.mConnectAfterPairing = false;
      setPhonebookPermissionChoice(0);
      setMessagePermissionChoice(0);
      setSimPermissionChoice(0);
      this.mMessageRejectionCount = 0;
      saveMessageRejectionCount();
    }
    refresh();
    if (paramInt == 12)
    {
      if (!this.mDevice.isBluetoothDock()) {
        break label74;
      }
      onBondingDockConnect();
    }
    for (;;)
    {
      this.mConnectAfterPairing = false;
      return;
      label74:
      if (this.mConnectAfterPairing) {
        connect(false);
      }
    }
  }
  
  void onProfileStateChanged(LocalBluetoothProfile paramLocalBluetoothProfile, int paramInt)
  {
    Log.d("CachedBluetoothDevice", "onProfileStateChanged: profile " + paramLocalBluetoothProfile + " newProfileState " + paramInt);
    int i = this.mLocalAdapter.getBluetoothState();
    if ((i == 13) || (i == 10))
    {
      Log.d("CachedBluetoothDevice", " BT Turninig Off...Profile conn state change ignored...");
      return;
    }
    this.mProfileConnectionState.put(paramLocalBluetoothProfile, Integer.valueOf(paramInt));
    if (paramInt == 2) {
      if ((paramLocalBluetoothProfile instanceof MapProfile))
      {
        paramLocalBluetoothProfile.setPreferred(this.mDevice, true);
        this.mRemovedProfiles.remove(paramLocalBluetoothProfile);
      }
    }
    do
    {
      do
      {
        this.mProfiles.add(paramLocalBluetoothProfile);
        do
        {
          return;
        } while (this.mProfiles.contains(paramLocalBluetoothProfile));
        this.mRemovedProfiles.remove(paramLocalBluetoothProfile);
        this.mProfiles.add(paramLocalBluetoothProfile);
      } while ((!(paramLocalBluetoothProfile instanceof PanProfile)) || (!((PanProfile)paramLocalBluetoothProfile).isLocalRoleNap(this.mDevice)));
      this.mLocalNapRoleConnected = true;
      return;
      if (((paramLocalBluetoothProfile instanceof MapProfile)) && (paramInt == 0))
      {
        paramLocalBluetoothProfile.setPreferred(this.mDevice, false);
        return;
      }
    } while ((!this.mLocalNapRoleConnected) || (!(paramLocalBluetoothProfile instanceof PanProfile)) || (!((PanProfile)paramLocalBluetoothProfile).isLocalRoleNap(this.mDevice)) || (paramInt != 0));
    Log.d("CachedBluetoothDevice", "Removing PanProfile from device after NAP disconnect");
    this.mProfiles.remove(paramLocalBluetoothProfile);
    this.mRemovedProfiles.add(paramLocalBluetoothProfile);
    this.mLocalNapRoleConnected = false;
  }
  
  void onUuidChanged()
  {
    Log.d("CachedBluetoothDevice", " onUuidChanged, mProfile Size " + this.mProfiles.size());
    Object localObject = new ArrayList();
    ((List)localObject).clear();
    ((List)localObject).addAll(this.mProfiles);
    updateProfiles();
    int i;
    if ((!((List)localObject).containsAll(this.mProfiles)) || (((List)localObject).isEmpty())) {
      i = 0;
    }
    while (i < this.mProfiles.size())
    {
      if (!((List)localObject).contains(this.mProfiles.get(i))) {
        ((List)localObject).add((LocalBluetoothProfile)this.mProfiles.get(i));
      }
      i += 1;
      continue;
      Log.d("CachedBluetoothDevice", "UUID not udpated, returning");
      this.mProfiles.clear();
      this.mProfiles.addAll((Collection)localObject);
      return;
    }
    this.mProfiles.clear();
    this.mProfiles.addAll((Collection)localObject);
    localObject = this.mDevice.getUuids();
    long l = 5000L;
    if (BluetoothUuid.isUuidPresent((ParcelUuid[])localObject, BluetoothUuid.Hogp)) {
      l = 30000L;
    }
    if ((!this.mProfiles.isEmpty()) && (this.mConnectAttempted + l > SystemClock.elapsedRealtime())) {
      connectWithoutResettingTimer(false);
    }
    dispatchAttributesChanged();
  }
  
  void refresh()
  {
    dispatchAttributesChanged();
  }
  
  void refreshBtClass()
  {
    fetchBtClass();
    dispatchAttributesChanged();
  }
  
  void refreshName()
  {
    fetchName();
    dispatchAttributesChanged();
  }
  
  public void registerCallback(Callback paramCallback)
  {
    synchronized (this.mCallbacks)
    {
      this.mCallbacks.add(paramCallback);
      return;
    }
  }
  
  void setBtClass(BluetoothClass paramBluetoothClass)
  {
    if ((paramBluetoothClass != null) && (this.mBtClass != paramBluetoothClass))
    {
      this.mBtClass = paramBluetoothClass;
      dispatchAttributesChanged();
    }
  }
  
  public void setMessagePermissionChoice(int paramInt)
  {
    int i = 0;
    if (paramInt == 1) {
      i = 1;
    }
    for (;;)
    {
      this.mDevice.setMessageAccessPermission(i);
      return;
      if (paramInt == 2) {
        i = 2;
      }
    }
  }
  
  public void setName(String paramString)
  {
    if (!this.mName.equals(paramString))
    {
      this.mName = paramString;
      this.mDevice.setAlias(paramString);
      dispatchAttributesChanged();
    }
  }
  
  void setNewName(String paramString)
  {
    if (this.mName == null)
    {
      this.mName = paramString;
      if ((this.mName == null) || (TextUtils.isEmpty(this.mName))) {
        this.mName = this.mDevice.getAddress();
      }
      dispatchAttributesChanged();
    }
  }
  
  public void setPhonebookPermissionChoice(int paramInt)
  {
    int i = 0;
    if (paramInt == 1) {
      i = 1;
    }
    for (;;)
    {
      this.mDevice.setPhonebookAccessPermission(i);
      return;
      if (paramInt == 2) {
        i = 2;
      }
    }
  }
  
  void setRssi(short paramShort)
  {
    if (this.mRssi != paramShort)
    {
      this.mRssi = paramShort;
      dispatchAttributesChanged();
    }
  }
  
  void setSimPermissionChoice(int paramInt)
  {
    int i = 0;
    if (paramInt == 1) {
      i = 1;
    }
    for (;;)
    {
      this.mDevice.setSimAccessPermission(i);
      return;
      if (paramInt == 2) {
        i = 2;
      }
    }
  }
  
  public void setVisible(boolean paramBoolean)
  {
    if (this.mVisible != paramBoolean)
    {
      this.mVisible = paramBoolean;
      dispatchAttributesChanged();
    }
  }
  
  public boolean startPairing()
  {
    if (this.mLocalAdapter.isDiscovering()) {
      this.mLocalAdapter.cancelDiscovery();
    }
    if (!this.mDevice.createBond()) {
      return false;
    }
    this.mConnectAfterPairing = true;
    return true;
  }
  
  public String toString()
  {
    return this.mDevice.toString();
  }
  
  public void unpair()
  {
    int i = getBondState();
    if (i == 11) {
      this.mDevice.cancelBondProcess();
    }
    if (i != 10)
    {
      BluetoothDevice localBluetoothDevice = this.mDevice;
      if ((localBluetoothDevice != null) && (localBluetoothDevice.removeBond())) {
        Log.d("CachedBluetoothDevice", "Command sent successfully:REMOVE_BOND " + describe(null));
      }
    }
  }
  
  public void unregisterCallback(Callback paramCallback)
  {
    synchronized (this.mCallbacks)
    {
      this.mCallbacks.remove(paramCallback);
      return;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onDeviceAttributesChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\bluetooth\CachedBluetoothDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */