package com.android.settings.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;

class WriteWifiConfigToNfcDialog
  extends AlertDialog
  implements TextWatcher, View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
  private static final int HEX_RADIX = 16;
  private static final String NETWORK_ID = "network_id";
  private static final String NFC_TOKEN_MIME_TYPE = "application/vnd.wfa.wsc";
  private static final String PASSWORD_FORMAT = "102700%s%s";
  private static final String SECURITY = "security";
  private static final String TAG = WriteWifiConfigToNfcDialog.class.getName().toString();
  private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
  private Button mCancelButton;
  private Context mContext;
  private TextView mLabelView;
  private int mNetworkId;
  private Handler mOnTextChangedHandler;
  private CheckBox mPasswordCheckBox;
  private TextView mPasswordView;
  private ProgressBar mProgressBar;
  private int mSecurity;
  private Button mSubmitButton;
  private View mView;
  private final PowerManager.WakeLock mWakeLock;
  private WifiManager mWifiManager;
  private String mWpsNfcConfigurationToken;
  
  WriteWifiConfigToNfcDialog(Context paramContext, int paramInt1, int paramInt2, WifiManager paramWifiManager)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "WriteWifiConfigToNfcDialog:wakeLock");
    this.mOnTextChangedHandler = new Handler();
    this.mNetworkId = paramInt1;
    this.mSecurity = paramInt2;
    this.mWifiManager = paramWifiManager;
  }
  
  WriteWifiConfigToNfcDialog(Context paramContext, Bundle paramBundle, WifiManager paramWifiManager)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "WriteWifiConfigToNfcDialog:wakeLock");
    this.mOnTextChangedHandler = new Handler();
    this.mNetworkId = paramBundle.getInt("network_id");
    this.mSecurity = paramBundle.getInt("security");
    this.mWifiManager = paramWifiManager;
  }
  
  private static String byteArrayToHexString(byte[] paramArrayOfByte)
  {
    char[] arrayOfChar = new char[paramArrayOfByte.length * 2];
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      int j = paramArrayOfByte[i] & 0xFF;
      arrayOfChar[(i * 2)] = hexArray[(j >>> 4)];
      arrayOfChar[(i * 2 + 1)] = hexArray[(j & 0xF)];
      i += 1;
    }
    return new String(arrayOfChar);
  }
  
  private void enableSubmitIfAppropriate()
  {
    boolean bool2 = true;
    boolean bool1 = true;
    if (this.mPasswordView != null)
    {
      if (this.mSecurity == 1)
      {
        localButton = this.mSubmitButton;
        if (this.mPasswordView.length() > 0) {
          localButton.setEnabled(bool1);
        }
      }
      while (this.mSecurity != 2) {
        for (;;)
        {
          return;
          bool1 = false;
        }
      }
      Button localButton = this.mSubmitButton;
      if (this.mPasswordView.length() >= 8) {}
      for (bool1 = bool2;; bool1 = false)
      {
        localButton.setEnabled(bool1);
        return;
      }
    }
    this.mSubmitButton.setEnabled(false);
  }
  
  private void handleWriteNfcEvent(Tag paramTag)
  {
    paramTag = Ndef.get(paramTag);
    if (paramTag != null)
    {
      if (paramTag.isWritable())
      {
        NdefRecord localNdefRecord = NdefRecord.createMime("application/vnd.wfa.wsc", hexStringToByteArray(this.mWpsNfcConfigurationToken));
        try
        {
          paramTag.connect();
          paramTag.writeNdefMessage(new NdefMessage(localNdefRecord, new NdefRecord[0]));
          getOwnerActivity().runOnUiThread(new Runnable()
          {
            public void run()
            {
              WriteWifiConfigToNfcDialog.-get0(WriteWifiConfigToNfcDialog.this).setVisibility(8);
            }
          });
          setViewText(this.mLabelView, 2131693167);
          setViewText(this.mCancelButton, 17040820);
          return;
        }
        catch (FormatException paramTag)
        {
          setViewText(this.mLabelView, 2131693168);
          Log.e(TAG, "Unable to write Wi-Fi config to NFC tag.", paramTag);
          return;
        }
        catch (IOException paramTag)
        {
          setViewText(this.mLabelView, 2131693168);
          Log.e(TAG, "Unable to write Wi-Fi config to NFC tag.", paramTag);
          return;
        }
      }
      setViewText(this.mLabelView, 2131693169);
      Log.e(TAG, "Tag is not writable");
      return;
    }
    setViewText(this.mLabelView, 2131693169);
    Log.e(TAG, "Tag does not support NDEF");
  }
  
  private static byte[] hexStringToByteArray(String paramString)
  {
    int j = paramString.length();
    byte[] arrayOfByte = new byte[j / 2];
    int i = 0;
    while (i < j)
    {
      arrayOfByte[(i / 2)] = ((byte)((Character.digit(paramString.charAt(i), 16) << 4) + Character.digit(paramString.charAt(i + 1), 16)));
      i += 2;
    }
    return arrayOfByte;
  }
  
  private void setViewText(final TextView paramTextView, final int paramInt)
  {
    getOwnerActivity().runOnUiThread(new Runnable()
    {
      public void run()
      {
        paramTextView.setText(paramInt);
      }
    });
  }
  
  public void afterTextChanged(Editable paramEditable) {}
  
  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  public void dismiss()
  {
    if (this.mWakeLock.isHeld()) {
      this.mWakeLock.release();
    }
    super.dismiss();
  }
  
  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    paramCompoundButton = this.mPasswordView;
    if (paramBoolean) {}
    for (int i = 144;; i = 128)
    {
      paramCompoundButton.setInputType(i | 0x1);
      return;
    }
  }
  
  public void onClick(View paramView)
  {
    this.mWakeLock.acquire();
    paramView = this.mPasswordView.getText().toString();
    String str1 = this.mWifiManager.getWpsNfcConfigurationToken(this.mNetworkId);
    String str2 = byteArrayToHexString(paramView.getBytes());
    if (paramView.length() >= 16) {}
    for (paramView = Integer.toString(paramView.length(), 16); str1.contains(String.format("102700%s%s", new Object[] { paramView, str2 }).toUpperCase()); paramView = "0" + Character.forDigit(paramView.length(), 16))
    {
      this.mWpsNfcConfigurationToken = str1;
      paramView = getOwnerActivity();
      NfcAdapter.getDefaultAdapter(paramView).enableReaderMode(paramView, new NfcAdapter.ReaderCallback()
      {
        public void onTagDiscovered(Tag paramAnonymousTag)
        {
          WriteWifiConfigToNfcDialog.-wrap1(WriteWifiConfigToNfcDialog.this, paramAnonymousTag);
        }
      }, 31, null);
      this.mPasswordView.setVisibility(8);
      this.mPasswordCheckBox.setVisibility(8);
      this.mSubmitButton.setVisibility(8);
      ((InputMethodManager)getOwnerActivity().getSystemService("input_method")).hideSoftInputFromWindow(this.mPasswordView.getWindowToken(), 0);
      this.mLabelView.setText(2131693165);
      this.mView.findViewById(2131362750).setTextAlignment(4);
      this.mProgressBar.setVisibility(0);
      return;
    }
    this.mLabelView.setText(2131693166);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    this.mView = getLayoutInflater().inflate(2130969127, null);
    setView(this.mView);
    setInverseBackgroundForced(true);
    setTitle(2131693163);
    setCancelable(true);
    setButton(-3, this.mContext.getResources().getString(2131693164), (DialogInterface.OnClickListener)null);
    setButton(-2, this.mContext.getResources().getString(17039360), (DialogInterface.OnClickListener)null);
    this.mPasswordView = ((TextView)this.mView.findViewById(2131362699));
    this.mLabelView = ((TextView)this.mView.findViewById(2131362824));
    this.mPasswordView.addTextChangedListener(this);
    this.mPasswordCheckBox = ((CheckBox)this.mView.findViewById(2131362723));
    this.mPasswordCheckBox.setOnCheckedChangeListener(this);
    this.mProgressBar = ((ProgressBar)this.mView.findViewById(2131362056));
    super.onCreate(paramBundle);
    this.mSubmitButton = getButton(-3);
    this.mSubmitButton.setOnClickListener(this);
    this.mSubmitButton.setEnabled(false);
    this.mCancelButton = getButton(-2);
  }
  
  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mOnTextChangedHandler.post(new Runnable()
    {
      public void run()
      {
        WriteWifiConfigToNfcDialog.-wrap0(WriteWifiConfigToNfcDialog.this);
      }
    });
  }
  
  public void saveState(Bundle paramBundle)
  {
    paramBundle.putInt("network_id", this.mNetworkId);
    paramBundle.putInt("security", this.mSecurity);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WriteWifiConfigToNfcDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */