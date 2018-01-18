package com.oneplus.settings;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings.System;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.lib.widget.OPEditText;

public class OPDeviceName
  extends SettingsPreferenceFragment
  implements View.OnClickListener
{
  private static final int BLUETOOTH_NAME_MAX_LENGTH_BYTES = 32;
  private static String devicename;
  private static OPEditText mDeviceName;
  private static TextView mOKView;
  private static View mView;
  private MenuItem mOKMenuItem;
  private String nameTemp = null;
  
  private boolean isNotEmojiCharacter(char paramChar)
  {
    if ((paramChar == 0) || (paramChar == '\t')) {}
    while ((paramChar == '\n') || (paramChar == '\r') || ((paramChar >= ' ') && (paramChar <= 55295)) || ((paramChar >= 57344) && (paramChar <= 65533)) || ((paramChar >= 65536) && (paramChar <= 1114111))) {
      return true;
    }
    return false;
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onClick(View paramView)
  {
    if (paramView.getId() == 2131362302)
    {
      paramView = mDeviceName.getText().toString().trim();
      if (paramView.length() != 0) {
        Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", paramView);
      }
      finish();
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setRetainInstance(true);
    getActivity().getWindow().setSoftInputMode(5);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    this.mOKMenuItem = paramMenu.add(0, 1, 0, 2131690719);
    this.mOKMenuItem.setEnabled(true).setShowAsAction(2);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    mView = paramLayoutInflater.inflate(2130968796, paramViewGroup, false);
    mDeviceName = (OPEditText)mView.findViewById(2131362300);
    paramBundle = Settings.System.getString(getActivity().getContentResolver(), "oem_oneplus_modified_devicename");
    paramViewGroup = Settings.System.getString(getActivity().getContentResolver(), "oem_oneplus_devicename");
    paramLayoutInflater = paramViewGroup;
    if (paramBundle == null) {
      if ((paramViewGroup != null) && (!paramViewGroup.equals("oneplus")) && (!paramViewGroup.equals("ONE E1001")) && (!paramViewGroup.equals("ONE E1003")))
      {
        paramLayoutInflater = paramViewGroup;
        if (!paramViewGroup.equals("ONE E1005")) {}
      }
      else
      {
        paramLayoutInflater = SystemProperties.get("ro.display.series");
        Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", paramLayoutInflater);
        Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_modified_devicename", "1");
      }
    }
    paramViewGroup = paramLayoutInflater;
    if (paramLayoutInflater.length() > 32)
    {
      paramViewGroup = paramLayoutInflater.substring(0, 31);
      Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", paramViewGroup);
    }
    mDeviceName.setText(paramViewGroup);
    if (paramViewGroup != null) {
      mDeviceName.setSelection(paramViewGroup.length());
    }
    mDeviceName.selectAll();
    mDeviceName.addTextChangedListener(new TextWatcher()
    {
      String name;
      int num;
      
      public void afterTextChanged(Editable paramAnonymousEditable)
      {
        if ((OPDeviceName.-get0() != null) && (OPDeviceName.-get0().length() != 0))
        {
          this.name = OPDeviceName.-get0().getText().toString();
          this.num = this.name.getBytes().length;
          if (this.num > 32)
          {
            OPDeviceName.-get0().setText(OPDeviceName.-get3(OPDeviceName.this));
            Editable localEditable = OPDeviceName.-get0().getText();
            if ((localEditable instanceof Spannable)) {
              Selection.setSelection((Spannable)localEditable, localEditable.length());
            }
          }
        }
        if ((paramAnonymousEditable.length() == 0) || (paramAnonymousEditable.toString().trim().isEmpty())) {}
        for (boolean bool = false;; bool = true)
        {
          OPDeviceName.-get2().setEnabled(bool);
          if (OPDeviceName.-get1(OPDeviceName.this) != null) {
            OPDeviceName.-get1(OPDeviceName.this).setEnabled(bool);
          }
          return;
        }
      }
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        if ((OPDeviceName.-get0() != null) && (OPDeviceName.-get0().length() != 0) && ((OPDeviceName.-get0().getText() instanceof Spannable))) {
          OPDeviceName.-set0(OPDeviceName.this, OPDeviceName.-get0().getText().toString());
        }
      }
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    });
    mOKView = (TextView)mView.findViewById(2131362302);
    mOKView.setOnClickListener(this);
    setHasOptionsMenu(true);
    return mView;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 1: 
      paramMenuItem = mDeviceName.getText().toString().trim();
      if (paramMenuItem.length() != 0)
      {
        int i = 0;
        while (i < paramMenuItem.length())
        {
          if (!isNotEmojiCharacter(paramMenuItem.charAt(i)))
          {
            Toast.makeText(getActivity(), 2131691488, 0).show();
            return true;
          }
          i += 1;
        }
        Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", paramMenuItem);
        Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_modified_devicename", "1");
        Object localObject = BluetoothAdapter.getDefaultAdapter();
        if (localObject != null) {
          ((BluetoothAdapter)localObject).setName(paramMenuItem);
        }
        localObject = (WifiP2pManager)getSystemService("wifip2p");
        WifiP2pManager.Channel localChannel = ((WifiP2pManager)localObject).initialize(getActivity(), getActivity().getMainLooper(), null);
        if (localObject != null) {
          ((WifiP2pManager)localObject).setDeviceName(localChannel, paramMenuItem, null);
        }
      }
      finish();
      return true;
    }
    finish();
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
  }
  
  class Utf8ByteLengthFilter
    implements InputFilter
  {
    private final int mMaxBytes;
    
    Utf8ByteLengthFilter(int paramInt)
    {
      this.mMaxBytes = paramInt;
    }
    
    public CharSequence filter(CharSequence paramCharSequence, int paramInt1, int paramInt2, Spanned paramSpanned, int paramInt3, int paramInt4)
    {
      int j = 0;
      int k = paramInt1;
      if (k < paramInt2)
      {
        i = paramCharSequence.charAt(k);
        if (i < 128) {
          i = 1;
        }
        for (;;)
        {
          j += i;
          k += 1;
          break;
          if (i < 2048) {
            i = 2;
          } else {
            i = 3;
          }
        }
      }
      int n = paramSpanned.length();
      int m = 0;
      k = 0;
      if (k < n)
      {
        if (k >= paramInt3)
        {
          i = m;
          if (k < paramInt4) {}
        }
        else
        {
          i = paramSpanned.charAt(k);
          if (i >= 128) {
            break label151;
          }
          i = 1;
        }
        for (;;)
        {
          i = m + i;
          k += 1;
          m = i;
          break;
          label151:
          if (i < 2048) {
            i = 2;
          } else {
            i = 3;
          }
        }
      }
      int i = this.mMaxBytes - m;
      if (i <= 0) {
        return "";
      }
      if (i >= j) {
        return null;
      }
      paramInt4 = paramInt1;
      while (paramInt4 < paramInt2)
      {
        paramInt3 = paramCharSequence.charAt(paramInt4);
        if (paramInt3 < 128) {
          paramInt3 = 1;
        }
        for (;;)
        {
          i -= paramInt3;
          if (i >= 0) {
            break;
          }
          return paramCharSequence.subSequence(paramInt1, paramInt4);
          if (paramInt3 < 2048) {
            paramInt3 = 2;
          } else {
            paramInt3 = 3;
          }
        }
        paramInt4 += 1;
      }
      return null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPDeviceName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */