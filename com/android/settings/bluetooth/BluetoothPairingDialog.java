package com.android.settings.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.internal.app.AlertController.AlertParams;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import java.util.Locale;

public final class BluetoothPairingDialog
  extends AlertActivity
  implements CompoundButton.OnCheckedChangeListener, DialogInterface.OnClickListener, TextWatcher
{
  private static final int BLUETOOTH_PASSKEY_MAX_LENGTH = 6;
  private static final int BLUETOOTH_PIN_MAX_LENGTH = 16;
  private static final int MESSAGE_DELAYED_DISMISS = 1;
  private static final int PAIRING_POPUP_TIMEOUT = 35000;
  private static final String TAG = "BluetoothPairingDialog";
  private LocalBluetoothManager mBluetoothManager;
  private CachedBluetoothDeviceManager mCachedDeviceManager;
  private BluetoothDevice mDevice;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      Log.v("BluetoothPairingDialog", "Delayed pairing pop up handler");
      BluetoothPairingDialog.this.dismiss();
    }
  };
  private Button mOkButton;
  private String mPairingKey;
  private EditText mPairingView;
  private LocalBluetoothProfile mPbapClientProfile;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(paramAnonymousContext))
      {
        int i = paramAnonymousIntent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
        if ((i == 12) || (i == 10)) {
          BluetoothPairingDialog.this.dismiss();
        }
      }
      do
      {
        do
        {
          return;
        } while (!"android.bluetooth.device.action.PAIRING_CANCEL".equals(paramAnonymousContext));
        paramAnonymousContext = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
      } while ((paramAnonymousContext != null) && (!paramAnonymousContext.equals(BluetoothPairingDialog.-get0(BluetoothPairingDialog.this))));
      BluetoothPairingDialog.this.dismiss();
    }
  };
  private int mType;
  
  private void createConfirmationDialog()
  {
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mTitle = getString(2131691238, new Object[] { this.mCachedDeviceManager.getName(this.mDevice) });
    localAlertParams.mView = createView();
    localAlertParams.mPositiveButtonText = getString(2131689567);
    localAlertParams.mPositiveButtonListener = this;
    localAlertParams.mNegativeButtonText = getString(2131689569);
    localAlertParams.mNegativeButtonListener = this;
    setupAlert();
  }
  
  private void createConsentDialog()
  {
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mTitle = getString(2131691238, new Object[] { this.mCachedDeviceManager.getName(this.mDevice) });
    localAlertParams.mView = createView();
    localAlertParams.mPositiveButtonText = getString(2131689567);
    localAlertParams.mPositiveButtonListener = this;
    localAlertParams.mNegativeButtonText = getString(2131689569);
    localAlertParams.mNegativeButtonListener = this;
    setupAlert();
  }
  
  private void createDisplayPasskeyOrPinDialog()
  {
    Object localObject = this.mAlertParams;
    ((AlertController.AlertParams)localObject).mTitle = getString(2131691238, new Object[] { this.mCachedDeviceManager.getName(this.mDevice) });
    ((AlertController.AlertParams)localObject).mView = createView();
    ((AlertController.AlertParams)localObject).mNegativeButtonText = getString(17039360);
    ((AlertController.AlertParams)localObject).mNegativeButtonListener = this;
    setupAlert();
    if (this.mType == 4) {
      this.mDevice.setPairingConfirmation(true);
    }
    while (this.mType != 5) {
      return;
    }
    localObject = BluetoothDevice.convertPinToBytes(this.mPairingKey);
    this.mDevice.setPin((byte[])localObject);
  }
  
  private View createPinEntryView()
  {
    View localView = getLayoutInflater().inflate(2130968631, null);
    TextView localTextView1 = (TextView)localView.findViewById(2131361996);
    TextView localTextView2 = (TextView)localView.findViewById(2131361998);
    CheckBox localCheckBox1 = (CheckBox)localView.findViewById(2131361997);
    CheckBox localCheckBox2 = (CheckBox)localView.findViewById(2131361999);
    localCheckBox2.setText(getString(2131691249, new Object[] { this.mCachedDeviceManager.getName(this.mDevice) }));
    localCheckBox2.setTextColor(getResources().getColor(2131493741));
    if ((this.mPbapClientProfile != null) && (this.mPbapClientProfile.isProfileReady())) {
      localCheckBox2.setVisibility(8);
    }
    if (this.mDevice.getPhonebookAccessPermission() == 1) {
      localCheckBox2.setChecked(true);
    }
    for (;;)
    {
      localCheckBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
        {
          if (paramAnonymousBoolean)
          {
            BluetoothPairingDialog.-get0(BluetoothPairingDialog.this).setPhonebookAccessPermission(1);
            return;
          }
          BluetoothPairingDialog.-get0(BluetoothPairingDialog.this).setPhonebookAccessPermission(2);
        }
      });
      this.mPairingView = ((EditText)localView.findViewById(2131361995));
      this.mPairingView.addTextChangedListener(this);
      localCheckBox1.setOnCheckedChangeListener(this);
      j = 2131691242;
      i = j;
      switch (this.mType)
      {
      default: 
        Log.e("BluetoothPairingDialog", "Incorrect pairing type for createPinEntryView: " + this.mType);
        return null;
        if (this.mDevice.getPhonebookAccessPermission() == 2)
        {
          localCheckBox2.setChecked(false);
        }
        else if ((this.mDevice.getBluetoothClass() != null) && (this.mDevice.getBluetoothClass().getDeviceClass() == 1032))
        {
          localCheckBox2.setChecked(true);
          this.mDevice.setPhonebookAccessPermission(1);
        }
        else
        {
          localCheckBox2.setChecked(false);
          this.mDevice.setPhonebookAccessPermission(2);
        }
        break;
      }
    }
    int i = 2131691243;
    int k = 2131691244;
    int m = 16;
    int j = i;
    i = m;
    for (;;)
    {
      localTextView1.setText(j);
      localTextView2.setText(k);
      this.mPairingView.setInputType(2);
      this.mPairingView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(i) });
      return localView;
      k = 2131691245;
      i = 6;
      localCheckBox1.setVisibility(8);
    }
  }
  
  private void createUserEntryDialog()
  {
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mTitle = getString(2131691238, new Object[] { this.mCachedDeviceManager.getName(this.mDevice) });
    localAlertParams.mView = createPinEntryView();
    localAlertParams.mPositiveButtonText = getString(17039370);
    localAlertParams.mPositiveButtonListener = this;
    localAlertParams.mNegativeButtonText = getString(17039360);
    localAlertParams.mNegativeButtonListener = this;
    setupAlert();
    this.mOkButton = this.mAlert.getButton(-1);
    this.mOkButton.setEnabled(false);
  }
  
  private View createView()
  {
    View localView = getLayoutInflater().inflate(2130968630, null);
    TextView localTextView1 = (TextView)localView.findViewById(2131361989);
    TextView localTextView2 = (TextView)localView.findViewById(2131361990);
    TextView localTextView3 = (TextView)localView.findViewById(2131361991);
    Object localObject = (CheckBox)localView.findViewById(2131361992);
    ((CheckBox)localObject).setText(getString(2131691249, new Object[] { this.mCachedDeviceManager.getName(this.mDevice) }));
    ((CheckBox)localObject).setTextColor(getResources().getColor(2131493741));
    if ((this.mPbapClientProfile != null) && (this.mPbapClientProfile.isProfileReady())) {
      ((CheckBox)localObject).setVisibility(8);
    }
    if (this.mDevice.getPhonebookAccessPermission() == 1) {
      ((CheckBox)localObject).setChecked(true);
    }
    for (;;)
    {
      ((CheckBox)localObject).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
        {
          if (paramAnonymousBoolean)
          {
            BluetoothPairingDialog.-get0(BluetoothPairingDialog.this).setPhonebookAccessPermission(1);
            return;
          }
          BluetoothPairingDialog.-get0(BluetoothPairingDialog.this).setPhonebookAccessPermission(2);
        }
      });
      localObject = null;
      switch (this.mType)
      {
      default: 
        Log.e("BluetoothPairingDialog", "Incorrect pairing type received, not creating view");
        return null;
        if (this.mDevice.getPhonebookAccessPermission() == 2) {
          ((CheckBox)localObject).setChecked(false);
        }
        break;
      }
      try
      {
        if (this.mDevice.getBluetoothClass().getDeviceClass() == 1032)
        {
          ((CheckBox)localObject).setChecked(true);
          this.mDevice.setPhonebookAccessPermission(1);
          continue;
        }
        ((CheckBox)localObject).setChecked(false);
        this.mDevice.setPhonebookAccessPermission(2);
      }
      catch (NullPointerException localNullPointerException) {}
      localTextView3.setVisibility(0);
      localObject = this.mPairingKey;
      for (;;)
      {
        if (localObject != null)
        {
          localTextView1.setVisibility(0);
          localTextView2.setVisibility(0);
          localTextView2.setText((CharSequence)localObject);
        }
        return localView;
        localTextView3.setVisibility(0);
      }
    }
  }
  
  private void onCancel()
  {
    Log.i("BluetoothPairingDialog", "Pairing dialog canceled");
    this.mDevice.cancelPairingUserInput();
  }
  
  private void onPair(String paramString)
  {
    Log.i("BluetoothPairingDialog", "Pairing dialog accepted");
    switch (this.mType)
    {
    default: 
      Log.e("BluetoothPairingDialog", "Incorrect pairing type received");
    case 4: 
    case 5: 
      return;
    case 0: 
    case 7: 
      paramString = BluetoothDevice.convertPinToBytes(paramString);
      if (paramString == null) {
        return;
      }
      this.mDevice.setPin(paramString);
      return;
    case 1: 
      int i = Integer.parseInt(paramString);
      this.mDevice.setPasskey(i);
      return;
    case 2: 
    case 3: 
      this.mDevice.setPairingConfirmation(true);
      return;
    }
    this.mDevice.setRemoteOutOfBandData();
  }
  
  private void popTimedout()
  {
    Message localMessage = this.mHandler.obtainMessage(1);
    this.mHandler.sendMessageDelayed(localMessage, 35000L);
  }
  
  public void afterTextChanged(Editable paramEditable)
  {
    boolean bool2 = true;
    boolean bool1 = true;
    if (this.mOkButton != null)
    {
      if (this.mType != 7) {
        break label49;
      }
      localButton = this.mOkButton;
      if (paramEditable.length() < 16) {
        break label44;
      }
    }
    for (;;)
    {
      localButton.setEnabled(bool1);
      return;
      label44:
      bool1 = false;
    }
    label49:
    Button localButton = this.mOkButton;
    if (paramEditable.length() > 0) {}
    for (bool1 = bool2;; bool1 = false)
    {
      localButton.setEnabled(bool1);
      return;
    }
  }
  
  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mPairingView.setInputType(1);
      return;
    }
    this.mPairingView.setInputType(2);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      onCancel();
      return;
    }
    if (this.mPairingView != null)
    {
      onPair(this.mPairingView.getText().toString());
      return;
    }
    onPair(null);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent();
    if (!paramBundle.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST"))
    {
      Log.e("BluetoothPairingDialog", "Error: this activity may be started only with intent android.bluetooth.device.action.PAIRING_REQUEST");
      finish();
      return;
    }
    this.mBluetoothManager = Utils.getLocalBtManager(this);
    if (this.mBluetoothManager == null)
    {
      Log.e("BluetoothPairingDialog", "Error: BluetoothAdapter not supported by system");
      finish();
      return;
    }
    this.mCachedDeviceManager = this.mBluetoothManager.getCachedDeviceManager();
    this.mPbapClientProfile = this.mBluetoothManager.getProfileManager().getPbapClientProfile();
    this.mDevice = ((BluetoothDevice)paramBundle.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
    this.mType = paramBundle.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", Integer.MIN_VALUE);
    switch (this.mType)
    {
    default: 
      Log.e("BluetoothPairingDialog", "Incorrect pairing type received, not showing any dialog");
    case 0: 
    case 1: 
    case 7: 
    case 2: 
    case 3: 
    case 6: 
      for (;;)
      {
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.PAIRING_CANCEL"));
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED"));
        return;
        createUserEntryDialog();
        popTimedout();
        continue;
        i = paramBundle.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", Integer.MIN_VALUE);
        if (i == Integer.MIN_VALUE)
        {
          Log.e("BluetoothPairingDialog", "Invalid Confirmation Passkey received, not showing any dialog");
          return;
        }
        this.mPairingKey = String.format(Locale.US, "%06d", new Object[] { Integer.valueOf(i) });
        createConfirmationDialog();
        popTimedout();
        continue;
        createConsentDialog();
        popTimedout();
      }
    }
    int i = paramBundle.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", Integer.MIN_VALUE);
    if (i == Integer.MIN_VALUE)
    {
      Log.e("BluetoothPairingDialog", "Invalid Confirmation Passkey or PIN received, not showing any dialog");
      return;
    }
    if (this.mType == 4) {}
    for (this.mPairingKey = String.format("%06d", new Object[] { Integer.valueOf(i) });; this.mPairingKey = String.format("%04d", new Object[] { Integer.valueOf(i) }))
    {
      createDisplayPasskeyOrPinDialog();
      popTimedout();
      break;
    }
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mReceiver != null) {}
    try
    {
      unregisterReceiver(this.mReceiver);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt == 4) {
      onCancel();
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\BluetoothPairingDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */