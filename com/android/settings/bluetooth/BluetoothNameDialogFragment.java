package com.android.settings.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public final class BluetoothNameDialogFragment
  extends DialogFragment
  implements TextWatcher
{
  private static final int BLUETOOTH_NAME_MAX_LENGTH_BYTES = 248;
  private static final String KEY_NAME = "device_name";
  private static final String KEY_NAME_EDITED = "device_name_edited";
  static final String TAG = "BluetoothNameDialogFragment";
  private AlertDialog mAlertDialog;
  private boolean mDeviceNameEdited;
  private boolean mDeviceNameUpdated;
  EditText mDeviceNameView;
  final LocalBluetoothAdapter mLocalAdapter = Utils.getLocalBtManager(getActivity()).getBluetoothAdapter();
  private Button mOkButton;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (paramAnonymousContext.equals("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED")) {
        BluetoothNameDialogFragment.this.updateDeviceName();
      }
      while ((!paramAnonymousContext.equals("android.bluetooth.adapter.action.STATE_CHANGED")) || (paramAnonymousIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) != 12)) {
        return;
      }
      BluetoothNameDialogFragment.this.updateDeviceName();
    }
  };
  
  private View createDialogView(String paramString)
  {
    View localView = ((LayoutInflater)getActivity().getSystemService("layout_inflater")).inflate(2130968687, null);
    this.mDeviceNameView = ((EditText)localView.findViewById(2131362124));
    this.mDeviceNameView.setFilters(new InputFilter[] { new Utf8ByteLengthFilter(248) });
    this.mDeviceNameView.setText(paramString);
    this.mDeviceNameView.addTextChangedListener(this);
    this.mDeviceNameView.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if (paramAnonymousInt == 6)
        {
          if ((paramAnonymousTextView.length() == 0) || (paramAnonymousTextView.getText().toString().trim().isEmpty())) {}
          for (;;)
          {
            BluetoothNameDialogFragment.-get0(BluetoothNameDialogFragment.this).dismiss();
            return true;
            BluetoothNameDialogFragment.-wrap0(BluetoothNameDialogFragment.this, paramAnonymousTextView.getText().toString().trim());
          }
        }
        return false;
      }
    });
    return localView;
  }
  
  private void setDeviceName(String paramString)
  {
    Log.d("BluetoothNameDialogFragment", "Setting device name to " + paramString);
    this.mLocalAdapter.setName(paramString);
  }
  
  public void afterTextChanged(Editable paramEditable)
  {
    boolean bool = true;
    if (this.mDeviceNameUpdated)
    {
      this.mDeviceNameUpdated = false;
      this.mOkButton.setEnabled(false);
    }
    do
    {
      return;
      this.mDeviceNameEdited = true;
    } while (this.mOkButton == null);
    Button localButton = this.mOkButton;
    if (paramEditable.toString().trim().length() != 0) {}
    for (;;)
    {
      localButton.setEnabled(bool);
      return;
      bool = false;
    }
  }
  
  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  public void onConfigurationChanged(Configuration paramConfiguration, CharSequence paramCharSequence)
  {
    boolean bool2 = false;
    super.onConfigurationChanged(paramConfiguration);
    if (this.mOkButton != null)
    {
      paramConfiguration = this.mOkButton;
      bool1 = bool2;
      if (paramCharSequence.length() != 0) {
        if (!paramCharSequence.toString().trim().isEmpty()) {
          break label56;
        }
      }
    }
    label56:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      paramConfiguration.setEnabled(bool1);
      return;
    }
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    String str2 = this.mLocalAdapter.getName();
    String str1 = str2;
    if (paramBundle != null)
    {
      str1 = paramBundle.getString("device_name", str2);
      this.mDeviceNameEdited = paramBundle.getBoolean("device_name_edited", false);
    }
    this.mAlertDialog = new AlertDialog.Builder(getActivity()).setTitle(2131690856).setView(createDialogView(str1)).setPositiveButton(2131690857, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = BluetoothNameDialogFragment.this.mDeviceNameView.getText().toString().trim();
        BluetoothNameDialogFragment.-wrap0(BluetoothNameDialogFragment.this, paramAnonymousDialogInterface);
      }
    }).setNegativeButton(17039360, null).create();
    this.mAlertDialog.getWindow().setSoftInputMode(5);
    return this.mAlertDialog;
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mAlertDialog = null;
    this.mDeviceNameView = null;
    this.mOkButton = null;
  }
  
  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mReceiver);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mOkButton == null)
    {
      this.mOkButton = this.mAlertDialog.getButton(-1);
      this.mOkButton.setEnabled(this.mDeviceNameEdited);
    }
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
    localIntentFilter.addAction("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED");
    getActivity().registerReceiver(this.mReceiver, localIntentFilter);
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putString("device_name", this.mDeviceNameView.getText().toString());
    paramBundle.putBoolean("device_name_edited", this.mDeviceNameEdited);
  }
  
  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  void updateDeviceName()
  {
    if ((this.mLocalAdapter != null) && (this.mLocalAdapter.isEnabled()))
    {
      this.mDeviceNameUpdated = true;
      this.mDeviceNameEdited = false;
      this.mDeviceNameView.setText(this.mLocalAdapter.getName());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\BluetoothNameDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */