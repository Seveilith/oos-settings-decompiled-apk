package com.android.settings.wifi.p2p;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pGroupList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pManager.PersistentGroupInfoListener;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.ui.OPPreferenceDivider;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Iterator;

public class WifiP2pSettings
  extends SettingsPreferenceFragment
  implements WifiP2pManager.PersistentGroupInfoListener, WifiP2pManager.PeerListListener
{
  private static final boolean DBG = false;
  private static final int DIALOG_CANCEL_CONNECT = 2;
  private static final int DIALOG_DELETE_GROUP = 4;
  private static final int DIALOG_DISCONNECT = 1;
  private static final int DIALOG_RENAME = 3;
  private static final int MENU_ID_RENAME = 2;
  private static final int MENU_ID_SEARCH = 1;
  private static final String SAVE_DEVICE_NAME = "DEV_NAME";
  private static final String SAVE_DIALOG_PEER = "PEER_STATE";
  private static final String SAVE_SELECTED_GROUP = "GROUP_NAME";
  private static final String TAG = "WifiP2pSettings";
  private DialogInterface.OnClickListener mCancelConnectListener;
  private WifiP2pManager.Channel mChannel;
  private int mConnectedDevices;
  private DialogInterface.OnClickListener mDeleteGroupListener;
  private EditText mDeviceNameText;
  private DialogInterface.OnClickListener mDisconnectListener;
  private final IntentFilter mIntentFilter = new IntentFilter();
  private boolean mLastGroupFormed = false;
  private WifiP2pDeviceList mPeers = new WifiP2pDeviceList();
  private PreferenceGroup mPeersGroup;
  private PreferenceGroup mPersistentGroup;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      boolean bool = true;
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ("android.net.wifi.p2p.STATE_CHANGED".equals(paramAnonymousContext))
      {
        paramAnonymousContext = WifiP2pSettings.this;
        if (paramAnonymousIntent.getIntExtra("wifi_p2p_state", 1) == 2)
        {
          WifiP2pSettings.-set4(paramAnonymousContext, bool);
          WifiP2pSettings.-wrap0(WifiP2pSettings.this);
        }
      }
      do
      {
        return;
        bool = false;
        break;
        if ("android.net.wifi.p2p.PEERS_CHANGED".equals(paramAnonymousContext))
        {
          WifiP2pSettings.-set1(WifiP2pSettings.this, (WifiP2pDeviceList)paramAnonymousIntent.getParcelableExtra("wifiP2pDeviceList"));
          WifiP2pSettings.-wrap1(WifiP2pSettings.this);
          return;
        }
        if ("android.net.wifi.p2p.CONNECTION_STATE_CHANGE".equals(paramAnonymousContext))
        {
          if (WifiP2pSettings.-get4(WifiP2pSettings.this) == null) {
            return;
          }
          paramAnonymousContext = (NetworkInfo)paramAnonymousIntent.getParcelableExtra("networkInfo");
          paramAnonymousIntent = (WifiP2pInfo)paramAnonymousIntent.getParcelableExtra("wifiP2pInfo");
          if (paramAnonymousContext.isConnected()) {}
          for (;;)
          {
            WifiP2pSettings.-set0(WifiP2pSettings.this, paramAnonymousIntent.groupFormed);
            return;
            if (!WifiP2pSettings.-get2(WifiP2pSettings.this)) {
              WifiP2pSettings.-wrap2(WifiP2pSettings.this);
            }
          }
        }
        if ("android.net.wifi.p2p.THIS_DEVICE_CHANGED".equals(paramAnonymousContext))
        {
          WifiP2pSettings.-set3(WifiP2pSettings.this, (WifiP2pDevice)paramAnonymousIntent.getParcelableExtra("wifiP2pDevice"));
          WifiP2pSettings.-wrap3(WifiP2pSettings.this);
          return;
        }
        if ("android.net.wifi.p2p.DISCOVERY_STATE_CHANGE".equals(paramAnonymousContext))
        {
          if (paramAnonymousIntent.getIntExtra("discoveryState", 1) == 2)
          {
            WifiP2pSettings.-wrap4(WifiP2pSettings.this, true);
            return;
          }
          WifiP2pSettings.-wrap4(WifiP2pSettings.this, false);
          return;
        }
      } while ((!"android.net.wifi.p2p.PERSISTENT_GROUPS_CHANGED".equals(paramAnonymousContext)) || (WifiP2pSettings.-get4(WifiP2pSettings.this) == null));
      WifiP2pSettings.-get4(WifiP2pSettings.this).requestPersistentGroupInfo(WifiP2pSettings.-get0(WifiP2pSettings.this), WifiP2pSettings.this);
    }
  };
  private DialogInterface.OnClickListener mRenameListener;
  private String mSavedDeviceName;
  private WifiP2pPersistentGroup mSelectedGroup;
  private String mSelectedGroupName;
  private WifiP2pPeer mSelectedWifiPeer;
  private WifiP2pDevice mThisDevice;
  private Preference mThisDevicePref;
  private boolean mWifiP2pEnabled;
  private WifiP2pManager mWifiP2pManager;
  private boolean mWifiP2pSearching;
  
  private void handleP2pStateChanged()
  {
    updateSearchMenu(false);
    this.mThisDevicePref.setEnabled(this.mWifiP2pEnabled);
    this.mPeersGroup.setEnabled(this.mWifiP2pEnabled);
    this.mPersistentGroup.setEnabled(this.mWifiP2pEnabled);
  }
  
  private void handlePeersChanged()
  {
    this.mPeersGroup.removeAll();
    this.mConnectedDevices = 0;
    Iterator localIterator = this.mPeers.getDeviceList().iterator();
    while (localIterator.hasNext())
    {
      WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)localIterator.next();
      this.mPeersGroup.addPreference(new WifiP2pPeer(getActivity(), localWifiP2pDevice));
      if (localWifiP2pDevice.status == 0) {
        this.mConnectedDevices += 1;
      }
    }
  }
  
  private void startSearch()
  {
    if ((this.mWifiP2pManager == null) || (this.mWifiP2pSearching)) {
      return;
    }
    this.mWifiP2pManager.discoverPeers(this.mChannel, new WifiP2pManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt) {}
      
      public void onSuccess() {}
    });
  }
  
  private void updateDevicePref()
  {
    Object localObject;
    String str;
    if (this.mThisDevice != null)
    {
      localObject = Settings.System.getString(getActivity().getContentResolver(), "oem_oneplus_modified_devicename");
      str = Settings.System.getString(getActivity().getContentResolver(), "oem_oneplus_devicename");
      if ((localObject != null) || ((str != null) && (!str.equals("oneplus")) && (!str.equals("ONE E1001")) && (!str.equals("ONE E1003")) && (!str.equals("Owifi_p2p_menu_renameNE E1005")))) {
        break label153;
      }
      localObject = SystemProperties.get("ro.display.series");
      Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", (String)localObject);
      Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_modified_devicename", "1");
    }
    for (;;)
    {
      this.mThisDevicePref.setTitle((CharSequence)localObject);
      if (!this.mThisDevice.deviceName.equals(localObject)) {
        this.mWifiP2pManager.setDeviceName(this.mChannel, (String)localObject, null);
      }
      return;
      label153:
      localObject = str;
      if (str.length() > 32)
      {
        localObject = str.substring(0, 31);
        Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", (String)localObject);
      }
    }
  }
  
  private void updateSearchMenu(boolean paramBoolean)
  {
    this.mWifiP2pSearching = paramBoolean;
    Activity localActivity = getActivity();
    if (localActivity != null) {
      localActivity.invalidateOptionsMenu();
    }
  }
  
  private String utfToString(String paramString)
  {
    byte[] arrayOfByte = paramString.getBytes();
    ByteBuffer localByteBuffer = ByteBuffer.allocate(paramString.length());
    int j = 0;
    int i = 0;
    if (i < arrayOfByte.length)
    {
      if ((arrayOfByte[i] == 92) && (arrayOfByte[(i + 1)] == 120))
      {
        localByteBuffer.put((byte)Integer.parseInt(paramString.substring(i + 2, i + 4), 16));
        i += 3;
      }
      for (;;)
      {
        j += 1;
        i += 1;
        break;
        localByteBuffer.put(arrayOfByte[i]);
      }
    }
    try
    {
      paramString = ByteBuffer.allocate(j);
      i = 0;
      while (i < j)
      {
        paramString.put(localByteBuffer.get(i));
        i += 1;
      }
      paramString = new String(paramString.array(), "UTF-8");
      return paramString;
    }
    catch (UnsupportedEncodingException paramString)
    {
      paramString.printStackTrace();
    }
    return null;
  }
  
  protected int getMetricsCategory()
  {
    return 109;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    addPreferencesFromResource(2131230889);
    this.mIntentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
    this.mIntentFilter.addAction("android.net.wifi.p2p.PEERS_CHANGED");
    this.mIntentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
    this.mIntentFilter.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
    this.mIntentFilter.addAction("android.net.wifi.p2p.DISCOVERY_STATE_CHANGE");
    this.mIntentFilter.addAction("android.net.wifi.p2p.PERSISTENT_GROUPS_CHANGED");
    Object localObject = getActivity();
    this.mWifiP2pManager = ((WifiP2pManager)getSystemService("wifip2p"));
    if (this.mWifiP2pManager != null)
    {
      this.mChannel = this.mWifiP2pManager.initialize((Context)localObject, getActivity().getMainLooper(), null);
      if (this.mChannel == null)
      {
        Log.e("WifiP2pSettings", "Failed to set up connection with wifi p2p service");
        this.mWifiP2pManager = null;
      }
    }
    for (;;)
    {
      if ((paramBundle != null) && (paramBundle.containsKey("PEER_STATE")))
      {
        localObject = (WifiP2pDevice)paramBundle.getParcelable("PEER_STATE");
        this.mSelectedWifiPeer = new WifiP2pPeer(getActivity(), (WifiP2pDevice)localObject);
      }
      if ((paramBundle != null) && (paramBundle.containsKey("DEV_NAME"))) {
        this.mSavedDeviceName = paramBundle.getString("DEV_NAME");
      }
      if ((paramBundle != null) && (paramBundle.containsKey("GROUP_NAME"))) {
        this.mSelectedGroupName = paramBundle.getString("GROUP_NAME");
      }
      this.mRenameListener = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == -1) && (WifiP2pSettings.-get4(WifiP2pSettings.this) != null))
          {
            paramAnonymousDialogInterface = WifiP2pSettings.-get1(WifiP2pSettings.this).getText().toString();
            if (paramAnonymousDialogInterface != null)
            {
              paramAnonymousInt = 0;
              if (paramAnonymousInt < paramAnonymousDialogInterface.length())
              {
                char c = paramAnonymousDialogInterface.charAt(paramAnonymousInt);
                if ((Character.isDigit(c)) || (Character.isLetter(c))) {}
                while ((c == '-') || (c == '_') || (c == ' '))
                {
                  paramAnonymousInt += 1;
                  break;
                }
                Toast.makeText(WifiP2pSettings.this.getActivity(), 2131691488, 1).show();
                return;
              }
            }
            WifiP2pSettings.-get4(WifiP2pSettings.this).setDeviceName(WifiP2pSettings.-get0(WifiP2pSettings.this), WifiP2pSettings.-get1(WifiP2pSettings.this).getText().toString(), new WifiP2pManager.ActionListener()
            {
              public void onFailure(int paramAnonymous2Int)
              {
                Toast.makeText(WifiP2pSettings.this.getActivity(), 2131691488, 1).show();
              }
              
              public void onSuccess() {}
            });
          }
        }
      };
      this.mDisconnectListener = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == -1) && (WifiP2pSettings.-get4(WifiP2pSettings.this) != null)) {
            WifiP2pSettings.-get4(WifiP2pSettings.this).removeGroup(WifiP2pSettings.-get0(WifiP2pSettings.this), new WifiP2pManager.ActionListener()
            {
              public void onFailure(int paramAnonymous2Int) {}
              
              public void onSuccess() {}
            });
          }
        }
      };
      this.mCancelConnectListener = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == -1) && (WifiP2pSettings.-get4(WifiP2pSettings.this) != null)) {
            WifiP2pSettings.-get4(WifiP2pSettings.this).cancelConnect(WifiP2pSettings.-get0(WifiP2pSettings.this), new WifiP2pManager.ActionListener()
            {
              public void onFailure(int paramAnonymous2Int) {}
              
              public void onSuccess() {}
            });
          }
        }
      };
      this.mDeleteGroupListener = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            if ((WifiP2pSettings.-get4(WifiP2pSettings.this) != null) && (WifiP2pSettings.-get3(WifiP2pSettings.this) != null))
            {
              WifiP2pSettings.-get4(WifiP2pSettings.this).deletePersistentGroup(WifiP2pSettings.-get0(WifiP2pSettings.this), WifiP2pSettings.-get3(WifiP2pSettings.this).getNetworkId(), new WifiP2pManager.ActionListener()
              {
                public void onFailure(int paramAnonymous2Int) {}
                
                public void onSuccess() {}
              });
              WifiP2pSettings.-set2(WifiP2pSettings.this, null);
            }
          }
          while (paramAnonymousInt != -2) {
            return;
          }
          WifiP2pSettings.-set2(WifiP2pSettings.this, null);
        }
      };
      setHasOptionsMenu(true);
      localObject = getPreferenceScreen();
      ((PreferenceScreen)localObject).removeAll();
      ((PreferenceScreen)localObject).setOrderingAsAdded(true);
      this.mThisDevicePref = new Preference(getPrefContext());
      this.mThisDevicePref.setPersistent(false);
      this.mThisDevicePref.setSelectable(false);
      ((PreferenceScreen)localObject).addPreference(this.mThisDevicePref);
      this.mPeersGroup = new PreferenceCategory(getPrefContext());
      this.mPeersGroup.setTitle(2131691485);
      ((PreferenceScreen)localObject).addPreference(new OPPreferenceDivider(getPrefContext()));
      ((PreferenceScreen)localObject).addPreference(this.mPeersGroup);
      this.mPersistentGroup = new PreferenceCategory(getPrefContext());
      this.mPersistentGroup.setTitle(2131691486);
      ((PreferenceScreen)localObject).addPreference(new OPPreferenceDivider(getPrefContext()));
      ((PreferenceScreen)localObject).addPreference(this.mPersistentGroup);
      super.onActivityCreated(paramBundle);
      return;
      Log.e("WifiP2pSettings", "mWifiP2pManager is null !");
    }
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    String str;
    if (paramInt == 1)
    {
      if (TextUtils.isEmpty(this.mSelectedWifiPeer.device.deviceName))
      {
        str = this.mSelectedWifiPeer.device.deviceAddress;
        if (this.mConnectedDevices <= 1) {
          break label141;
        }
      }
      label141:
      for (str = getActivity().getString(2131691491, new Object[] { str, Integer.valueOf(this.mConnectedDevices - 1) });; str = getActivity().getString(2131691490, new Object[] { str }))
      {
        return new AlertDialog.Builder(getActivity()).setTitle(2131691489).setMessage(str).setPositiveButton(getActivity().getString(2131692133), this.mDisconnectListener).setNegativeButton(getActivity().getString(2131692134), null).create();
        str = this.mSelectedWifiPeer.device.deviceName;
        break;
      }
    }
    if (paramInt == 2)
    {
      if (TextUtils.isEmpty(this.mSelectedWifiPeer.device.deviceName)) {}
      for (str = this.mSelectedWifiPeer.device.deviceAddress;; str = this.mSelectedWifiPeer.device.deviceName) {
        return new AlertDialog.Builder(getActivity()).setTitle(2131691492).setMessage(getActivity().getString(2131691493, new Object[] { str })).setPositiveButton(getActivity().getString(2131692133), this.mCancelConnectListener).setNegativeButton(getActivity().getString(2131692134), null).create();
      }
    }
    if (paramInt == 3)
    {
      this.mDeviceNameText = new EditText(getActivity());
      this.mDeviceNameText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) });
      if (this.mSavedDeviceName != null)
      {
        this.mDeviceNameText.setText(this.mSavedDeviceName);
        this.mDeviceNameText.setSelection(this.mSavedDeviceName.length());
      }
      for (;;)
      {
        this.mSavedDeviceName = null;
        return new AlertDialog.Builder(getActivity()).setTitle(2131691484).setView(this.mDeviceNameText).setPositiveButton(getActivity().getString(2131692133), this.mRenameListener).setNegativeButton(getActivity().getString(2131692134), null).create();
        if ((this.mThisDevice != null) && (!TextUtils.isEmpty(this.mThisDevice.deviceName)))
        {
          this.mDeviceNameText.setText(this.mThisDevice.deviceName);
          this.mDeviceNameText.setSelection(0, this.mThisDevice.deviceName.length());
        }
      }
    }
    if (paramInt == 4) {
      return new AlertDialog.Builder(getActivity()).setMessage(getActivity().getString(2131691494)).setPositiveButton(getActivity().getString(2131692133), this.mDeleteGroupListener).setNegativeButton(getActivity().getString(2131692134), this.mDeleteGroupListener).create();
    }
    return null;
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if (this.mWifiP2pSearching) {}
    for (int i = 2131691483;; i = 2131691482)
    {
      paramMenu.add(0, 1, 0, i).setEnabled(this.mWifiP2pEnabled).setShowAsAction(1);
      paramMenu.add(0, 2, 0, 2131691484).setEnabled(this.mWifiP2pEnabled).setShowAsAction(1);
      super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
      return;
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 1: 
      startSearch();
      return true;
    }
    startFragment(this, "com.oneplus.settings.OPDeviceName", 2131690256, -1, null);
    return true;
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.mWifiP2pManager != null) {
      this.mWifiP2pManager.stopPeerDiscovery(this.mChannel, null);
    }
    getActivity().unregisterReceiver(this.mReceiver);
  }
  
  public void onPeersAvailable(WifiP2pDeviceList paramWifiP2pDeviceList)
  {
    this.mPeers = paramWifiP2pDeviceList;
    handlePeersChanged();
  }
  
  public void onPersistentGroupInfoAvailable(WifiP2pGroupList paramWifiP2pGroupList)
  {
    this.mPersistentGroup.removeAll();
    paramWifiP2pGroupList = paramWifiP2pGroupList.getGroupList().iterator();
    while (paramWifiP2pGroupList.hasNext())
    {
      Object localObject = (WifiP2pGroup)paramWifiP2pGroupList.next();
      String str = ((WifiP2pGroup)localObject).getNetworkName();
      if (str.contains("\\x"))
      {
        str = utfToString(str);
        if (str != null) {
          ((WifiP2pGroup)localObject).setNetworkName(str);
        }
      }
      localObject = new WifiP2pPersistentGroup(getActivity(), (WifiP2pGroup)localObject);
      this.mPersistentGroup.addPreference((Preference)localObject);
      if (((WifiP2pPersistentGroup)localObject).getGroupName().equals(this.mSelectedGroupName))
      {
        this.mSelectedGroup = ((WifiP2pPersistentGroup)localObject);
        this.mSelectedGroupName = null;
      }
    }
    if (this.mSelectedGroupName != null) {
      Log.w("WifiP2pSettings", " Selected group " + this.mSelectedGroupName + " disappered on next query ");
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if ((paramPreference instanceof WifiP2pPeer))
    {
      this.mSelectedWifiPeer = ((WifiP2pPeer)paramPreference);
      if (this.mSelectedWifiPeer.device.status == 0) {
        showDialog(1);
      }
    }
    for (;;)
    {
      return super.onPreferenceTreeClick(paramPreference);
      if (this.mSelectedWifiPeer.device.status == 1)
      {
        showDialog(2);
      }
      else
      {
        WifiP2pConfig localWifiP2pConfig = new WifiP2pConfig();
        localWifiP2pConfig.deviceAddress = this.mSelectedWifiPeer.device.deviceAddress;
        int i = SystemProperties.getInt("wifidirect.wps", -1);
        if (i != -1) {
          localWifiP2pConfig.wps.setup = i;
        }
        for (;;)
        {
          this.mWifiP2pManager.connect(this.mChannel, localWifiP2pConfig, new WifiP2pManager.ActionListener()
          {
            public void onFailure(int paramAnonymousInt)
            {
              Log.e("WifiP2pSettings", " connect fail " + paramAnonymousInt);
              Toast.makeText(WifiP2pSettings.this.getActivity(), 2131691487, 0).show();
            }
            
            public void onSuccess() {}
          });
          break;
          if (this.mSelectedWifiPeer.device.wpsPbcSupported()) {
            localWifiP2pConfig.wps.setup = 0;
          } else if (this.mSelectedWifiPeer.device.wpsKeypadSupported()) {
            localWifiP2pConfig.wps.setup = 2;
          } else {
            localWifiP2pConfig.wps.setup = 1;
          }
        }
        if ((paramPreference instanceof WifiP2pPersistentGroup))
        {
          this.mSelectedGroup = ((WifiP2pPersistentGroup)paramPreference);
          showDialog(4);
        }
      }
    }
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    MenuItem localMenuItem = paramMenu.findItem(1);
    paramMenu = paramMenu.findItem(2);
    if (this.mWifiP2pEnabled)
    {
      localMenuItem.setEnabled(true);
      paramMenu.setEnabled(true);
    }
    while (this.mWifiP2pSearching)
    {
      localMenuItem.setTitle(2131691483);
      return;
      localMenuItem.setEnabled(false);
      paramMenu.setEnabled(false);
    }
    localMenuItem.setTitle(2131691482);
  }
  
  public void onResume()
  {
    super.onResume();
    getActivity().registerReceiver(this.mReceiver, this.mIntentFilter);
    if (this.mWifiP2pManager != null) {
      this.mWifiP2pManager.requestPeers(this.mChannel, this);
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    if (this.mSelectedWifiPeer != null) {
      paramBundle.putParcelable("PEER_STATE", this.mSelectedWifiPeer.device);
    }
    if (this.mDeviceNameText != null) {
      paramBundle.putString("DEV_NAME", this.mDeviceNameText.getText().toString());
    }
    if (this.mSelectedGroup != null) {
      paramBundle.putString("GROUP_NAME", this.mSelectedGroup.getGroupName());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\p2p\WifiP2pSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */