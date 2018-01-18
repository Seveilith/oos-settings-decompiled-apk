package com.android.settings.bluetooth;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.android.settingslib.bluetooth.A2dpProfile;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.HeadsetProfile;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.settingslib.bluetooth.MapProfile;
import com.android.settingslib.bluetooth.PanProfile;
import com.android.settingslib.bluetooth.PbapServerProfile;
import java.util.HashMap;
import java.util.Iterator;

public final class DeviceProfilesSettings
  extends DialogFragment
  implements CachedBluetoothDevice.Callback, DialogInterface.OnClickListener, View.OnClickListener
{
  public static final String ARG_DEVICE_ADDRESS = "device_address";
  private static final String KEY_PBAP_SERVER = "PBAP Server";
  private static final String KEY_PROFILE_CONTAINER = "profile_container";
  private static final String KEY_UNPAIR = "unpair";
  private static final int OK_BUTTON = -1;
  private static final String TAG = "DeviceProfilesSettings";
  private final HashMap<LocalBluetoothProfile, CheckBoxPreference> mAutoConnectPrefs = new HashMap();
  private CachedBluetoothDevice mCachedDevice;
  private EditTextPreference mDeviceNamePref;
  private AlertDialog mDisconnectDialog;
  private LocalBluetoothManager mManager;
  private ViewGroup mProfileContainer;
  private boolean mProfileGroupIsRemoved;
  private TextView mProfileLabel;
  private LocalBluetoothProfileManager mProfileManager;
  private View mRootView;
  
  private void addPreferencesForProfiles()
  {
    this.mProfileContainer.removeAllViews();
    Object localObject1 = this.mCachedDevice.getConnectableProfiles().iterator();
    label81:
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (LocalBluetoothProfile)((Iterator)localObject1).next();
      if (!(localObject2 instanceof PbapServerProfile)) {}
      for (boolean bool = localObject2 instanceof MapProfile;; bool = true)
      {
        if (bool) {
          break label81;
        }
        localObject2 = createProfilePreference((LocalBluetoothProfile)localObject2);
        this.mProfileContainer.addView((View)localObject2);
        break;
      }
    }
    int i = this.mCachedDevice.getPhonebookPermissionChoice();
    Log.d("DeviceProfilesSettings", "addPreferencesForProfiles: pbapPermission = " + i);
    if (i != 0)
    {
      localObject1 = createProfilePreference(this.mManager.getProfileManager().getPbapProfile());
      this.mProfileContainer.addView((View)localObject1);
    }
    localObject1 = this.mManager.getProfileManager().getMapProfile();
    i = this.mCachedDevice.getMessagePermissionChoice();
    Log.d("DeviceProfilesSettings", "addPreferencesForProfiles: mapPermission = " + i);
    if (i != 0)
    {
      localObject1 = createProfilePreference((LocalBluetoothProfile)localObject1);
      this.mProfileContainer.addView((View)localObject1);
    }
    showOrHideProfileGroup();
  }
  
  private void askDisconnect(Context paramContext, final LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    final CachedBluetoothDevice localCachedBluetoothDevice = this.mCachedDevice;
    String str2 = localCachedBluetoothDevice.getName();
    String str1 = str2;
    if (TextUtils.isEmpty(str2)) {
      str1 = paramContext.getString(2131690867);
    }
    String str3 = paramContext.getString(paramLocalBluetoothProfile.getNameResource(localCachedBluetoothDevice.getDevice()));
    str2 = paramContext.getString(2131690864);
    str1 = paramContext.getString(2131690865, new Object[] { str3, str1 });
    paramLocalBluetoothProfile = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1)
        {
          localCachedBluetoothDevice.disconnect(paramLocalBluetoothProfile);
          paramLocalBluetoothProfile.setPreferred(localCachedBluetoothDevice.getDevice(), false);
          if ((paramLocalBluetoothProfile instanceof MapProfile)) {
            localCachedBluetoothDevice.setMessagePermissionChoice(2);
          }
          if ((paramLocalBluetoothProfile instanceof PbapServerProfile)) {
            localCachedBluetoothDevice.setPhonebookPermissionChoice(2);
          }
        }
        DeviceProfilesSettings.-wrap1(DeviceProfilesSettings.this, DeviceProfilesSettings.-wrap0(DeviceProfilesSettings.this, paramLocalBluetoothProfile.toString()), paramLocalBluetoothProfile);
      }
    };
    this.mDisconnectDialog = Utils.showDisconnectDialog(paramContext, this.mDisconnectDialog, paramLocalBluetoothProfile, str2, Html.fromHtml(str1));
  }
  
  private CheckBox createProfilePreference(LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    CheckBox localCheckBox = new CheckBox(getActivity());
    localCheckBox.setTag(paramLocalBluetoothProfile.toString());
    localCheckBox.setText(paramLocalBluetoothProfile.getNameResource(this.mCachedDevice.getDevice()));
    localCheckBox.setOnClickListener(this);
    refreshProfilePreference(localCheckBox, paramLocalBluetoothProfile);
    return localCheckBox;
  }
  
  private CheckBox findProfile(String paramString)
  {
    return (CheckBox)this.mProfileContainer.findViewWithTag(paramString);
  }
  
  private LocalBluetoothProfile getProfileOf(View paramView)
  {
    if (!(paramView instanceof CheckBox)) {
      return null;
    }
    paramView = (String)paramView.getTag();
    if (TextUtils.isEmpty(paramView)) {
      return null;
    }
    try
    {
      paramView = this.mProfileManager.getProfileByName(paramView);
      return paramView;
    }
    catch (IllegalArgumentException paramView) {}
    return null;
  }
  
  private void onProfileClicked(LocalBluetoothProfile paramLocalBluetoothProfile, CheckBox paramCheckBox)
  {
    BluetoothDevice localBluetoothDevice = this.mCachedDevice.getDevice();
    Log.e("DeviceProfilesSettings", "onProfileClicked connect able:" + paramLocalBluetoothProfile.isPreferred(localBluetoothDevice) + " check status:" + paramCheckBox.isChecked());
    if (!paramCheckBox.isChecked())
    {
      paramCheckBox.setChecked(true);
      askDisconnect(this.mManager.getForegroundActivity(), paramLocalBluetoothProfile);
      return;
    }
    if ((paramLocalBluetoothProfile instanceof MapProfile)) {
      this.mCachedDevice.setMessagePermissionChoice(1);
    }
    if ((paramLocalBluetoothProfile instanceof PbapServerProfile))
    {
      this.mCachedDevice.setPhonebookPermissionChoice(1);
      refreshProfilePreference(paramCheckBox, paramLocalBluetoothProfile);
      return;
    }
    if (paramLocalBluetoothProfile.isPreferred(localBluetoothDevice)) {
      if ((paramLocalBluetoothProfile instanceof PanProfile)) {
        this.mCachedDevice.connectProfile(paramLocalBluetoothProfile);
      }
    }
    for (;;)
    {
      refreshProfilePreference(paramCheckBox, paramLocalBluetoothProfile);
      return;
      paramLocalBluetoothProfile.setPreferred(localBluetoothDevice, false);
      continue;
      paramLocalBluetoothProfile.setPreferred(localBluetoothDevice, true);
      this.mCachedDevice.connectProfile(paramLocalBluetoothProfile);
    }
  }
  
  private void refresh()
  {
    EditText localEditText = (EditText)this.mRootView.findViewById(2131362120);
    if (localEditText != null) {
      localEditText.setText(this.mCachedDevice.getName());
    }
    refreshProfiles();
  }
  
  private void refreshProfilePreference(CheckBox paramCheckBox, LocalBluetoothProfile paramLocalBluetoothProfile)
  {
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool2 = true;
    BluetoothDevice localBluetoothDevice = this.mCachedDevice.getDevice();
    if (this.mCachedDevice.isBusy())
    {
      bool1 = false;
      paramCheckBox.setEnabled(bool1);
      if (!(paramLocalBluetoothProfile instanceof MapProfile)) {
        break label72;
      }
      if (this.mCachedDevice.getMessagePermissionChoice() != 1) {
        break label67;
      }
    }
    label67:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      paramCheckBox.setChecked(bool1);
      return;
      bool1 = true;
      break;
    }
    label72:
    if ((paramLocalBluetoothProfile instanceof PbapServerProfile))
    {
      if (this.mCachedDevice.getPhonebookPermissionChoice() == 1) {}
      for (bool1 = bool3;; bool1 = false)
      {
        paramCheckBox.setChecked(bool1);
        return;
      }
    }
    if ((paramLocalBluetoothProfile instanceof PanProfile))
    {
      if (paramLocalBluetoothProfile.getConnectionStatus(localBluetoothDevice) == 2) {}
      for (bool1 = bool4;; bool1 = false)
      {
        paramCheckBox.setChecked(bool1);
        return;
      }
    }
    if ((paramLocalBluetoothProfile instanceof A2dpProfile))
    {
      if (paramLocalBluetoothProfile.getConnectionStatus(localBluetoothDevice) == 2) {}
      for (bool1 = true;; bool1 = false)
      {
        paramCheckBox.setChecked(bool1);
        paramLocalBluetoothProfile.setPreferred(localBluetoothDevice, bool1);
        return;
      }
    }
    if ((paramLocalBluetoothProfile instanceof HeadsetProfile))
    {
      if (paramLocalBluetoothProfile.getConnectionStatus(localBluetoothDevice) == 2) {}
      for (bool1 = true;; bool1 = false)
      {
        paramCheckBox.setChecked(bool1);
        paramLocalBluetoothProfile.setPreferred(localBluetoothDevice, bool1);
        return;
      }
    }
    paramCheckBox.setChecked(paramLocalBluetoothProfile.isPreferred(localBluetoothDevice));
  }
  
  private void refreshProfiles()
  {
    Iterator localIterator = this.mCachedDevice.getConnectableProfiles().iterator();
    Object localObject;
    CheckBox localCheckBox;
    while (localIterator.hasNext())
    {
      localObject = (LocalBluetoothProfile)localIterator.next();
      localCheckBox = findProfile(localObject.toString());
      if (localCheckBox == null)
      {
        localObject = createProfilePreference((LocalBluetoothProfile)localObject);
        this.mProfileContainer.addView((View)localObject);
      }
      else
      {
        refreshProfilePreference(localCheckBox, (LocalBluetoothProfile)localObject);
      }
    }
    localIterator = this.mCachedDevice.getRemovedProfiles().iterator();
    while (localIterator.hasNext())
    {
      localObject = (LocalBluetoothProfile)localIterator.next();
      localCheckBox = findProfile(localObject.toString());
      if (localCheckBox != null)
      {
        int i;
        if ((localObject instanceof PbapServerProfile))
        {
          i = this.mCachedDevice.getPhonebookPermissionChoice();
          Log.d("DeviceProfilesSettings", "refreshProfiles: pbapPermission = " + i);
          if (i != 0) {}
        }
        else if ((localObject instanceof MapProfile))
        {
          i = this.mCachedDevice.getMessagePermissionChoice();
          Log.d("DeviceProfilesSettings", "refreshProfiles: mapPermission = " + i);
          if (i != 0) {}
        }
        else
        {
          Log.d("DeviceProfilesSettings", "Removing " + localObject.toString() + " from profile list");
          this.mProfileContainer.removeView(localCheckBox);
        }
      }
    }
    showOrHideProfileGroup();
  }
  
  private void showOrHideProfileGroup()
  {
    int i = this.mProfileContainer.getChildCount();
    if ((!this.mProfileGroupIsRemoved) && (i == 0))
    {
      this.mProfileContainer.setVisibility(8);
      this.mProfileLabel.setVisibility(8);
      this.mProfileGroupIsRemoved = true;
    }
    while ((!this.mProfileGroupIsRemoved) || (i == 0)) {
      return;
    }
    this.mProfileContainer.setVisibility(0);
    this.mProfileLabel.setVisibility(0);
    this.mProfileGroupIsRemoved = false;
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    case -2: 
    default: 
      return;
    case -1: 
      paramDialogInterface = (EditText)this.mRootView.findViewById(2131362120);
      this.mCachedDevice.setName(paramDialogInterface.getText().toString());
      return;
    }
    this.mCachedDevice.unpair();
    Utils.updateSearchIndex(getContext(), BluetoothSettings.class.getName(), this.mCachedDevice.getName(), getString(2131691235), 2130838037, false);
  }
  
  public void onClick(View paramView)
  {
    if ((paramView instanceof CheckBox))
    {
      LocalBluetoothProfile localLocalBluetoothProfile = getProfileOf(paramView);
      if (localLocalBluetoothProfile != null) {
        onProfileClicked(localLocalBluetoothProfile, (CheckBox)paramView);
      }
    }
    else
    {
      return;
    }
    Log.e("DeviceProfilesSettings", "Error: Can't get the profile for the preference");
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mManager = Utils.getLocalBtManager(getActivity());
    paramBundle = this.mManager.getCachedDeviceManager();
    Object localObject = getArguments().getString("device_address");
    localObject = this.mManager.getBluetoothAdapter().getRemoteDevice((String)localObject);
    this.mCachedDevice = paramBundle.findDevice((BluetoothDevice)localObject);
    if (this.mCachedDevice == null) {
      this.mCachedDevice = paramBundle.addDevice(this.mManager.getBluetoothAdapter(), this.mManager.getProfileManager(), (BluetoothDevice)localObject);
    }
    this.mProfileManager = this.mManager.getProfileManager();
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    this.mRootView = LayoutInflater.from(getContext()).inflate(2130968686, null);
    this.mProfileContainer = ((ViewGroup)this.mRootView.findViewById(2131362123));
    this.mProfileLabel = ((TextView)this.mRootView.findViewById(2131362122));
    ((EditText)this.mRootView.findViewById(2131362120)).setText(this.mCachedDevice.getName(), TextView.BufferType.EDITABLE);
    return new AlertDialog.Builder(getContext()).setView(this.mRootView).setNeutralButton(2131690996, this).setPositiveButton(2131690994, this).setTitle(2131691265).create();
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (this.mDisconnectDialog != null)
    {
      this.mDisconnectDialog.dismiss();
      this.mDisconnectDialog = null;
    }
    if (this.mCachedDevice != null) {
      this.mCachedDevice.unregisterCallback(this);
    }
  }
  
  public void onDeviceAttributesChanged()
  {
    refresh();
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.mCachedDevice != null) {
      this.mCachedDevice.unregisterCallback(this);
    }
  }
  
  public void onResume()
  {
    super.onResume();
    this.mManager.setForegroundActivity(getActivity());
    if (this.mCachedDevice != null)
    {
      this.mCachedDevice.registerCallback(this);
      if (this.mCachedDevice.getBondState() == 10)
      {
        dismiss();
        return;
      }
      addPreferencesForProfiles();
      refresh();
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\DeviceProfilesSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */