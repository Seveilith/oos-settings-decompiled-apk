package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Proxy;
import android.net.ProxyInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProxySelector
  extends InstrumentedFragment
  implements DialogCreatable
{
  private static final int ERROR_DIALOG_ID = 0;
  private static final String TAG = "ProxySelector";
  Button mClearButton;
  View.OnClickListener mClearHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      ProxySelector.this.mHostnameField.setText("");
      ProxySelector.this.mPortField.setText("");
      ProxySelector.this.mExclusionListField.setText("");
    }
  };
  Button mDefaultButton;
  View.OnClickListener mDefaultHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      ProxySelector.this.populateFields();
    }
  };
  private SettingsPreferenceFragment.SettingsDialogFragment mDialogFragment;
  EditText mExclusionListField;
  EditText mHostnameField;
  Button mOKButton;
  View.OnClickListener mOKHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (ProxySelector.this.saveToDb()) {
        ProxySelector.this.getActivity().onBackPressed();
      }
    }
  };
  View.OnFocusChangeListener mOnFocusChangeHandler = new View.OnFocusChangeListener()
  {
    public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean) {
        Selection.selectAll((Spannable)((TextView)paramAnonymousView).getText());
      }
    }
  };
  EditText mPortField;
  private View mView;
  
  private void initView(View paramView)
  {
    this.mHostnameField = ((EditText)paramView.findViewById(2131362463));
    this.mHostnameField.setOnFocusChangeListener(this.mOnFocusChangeHandler);
    this.mPortField = ((EditText)paramView.findViewById(2131362464));
    this.mPortField.setOnClickListener(this.mOKHandler);
    this.mPortField.setOnFocusChangeListener(this.mOnFocusChangeHandler);
    this.mExclusionListField = ((EditText)paramView.findViewById(2131362465));
    this.mExclusionListField.setOnFocusChangeListener(this.mOnFocusChangeHandler);
    this.mOKButton = ((Button)paramView.findViewById(2131362466));
    this.mOKButton.setOnClickListener(this.mOKHandler);
    this.mClearButton = ((Button)paramView.findViewById(2131362467));
    this.mClearButton.setOnClickListener(this.mClearHandler);
    this.mDefaultButton = ((Button)paramView.findViewById(2131362468));
    this.mDefaultButton.setOnClickListener(this.mDefaultHandler);
  }
  
  private void showDialog(int paramInt)
  {
    if (this.mDialogFragment != null) {
      Log.e("ProxySelector", "Old dialog fragment not null!");
    }
    this.mDialogFragment = new SettingsPreferenceFragment.SettingsDialogFragment(this, paramInt);
    this.mDialogFragment.show(getActivity().getFragmentManager(), Integer.toString(paramInt));
  }
  
  public static int validate(String paramString1, String paramString2, String paramString3)
  {
    switch (Proxy.validate(paramString1, paramString2, paramString3))
    {
    default: 
      Log.e("ProxySelector", "Unknown proxy settings error");
      return -1;
    case 0: 
      return 0;
    case 1: 
      return 2131690920;
    case 2: 
      return 2131690917;
    case 3: 
      return 2131690919;
    case 4: 
      return 2131690921;
    }
    return 2131690918;
  }
  
  protected int getMetricsCategory()
  {
    return 82;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (((DevicePolicyManager)getActivity().getSystemService("device_policy")).getGlobalProxyAdmin() == null) {}
    for (boolean bool = true;; bool = false)
    {
      this.mHostnameField.setEnabled(bool);
      this.mPortField.setEnabled(bool);
      this.mExclusionListField.setEnabled(bool);
      this.mOKButton.setEnabled(bool);
      this.mClearButton.setEnabled(bool);
      this.mDefaultButton.setEnabled(bool);
      return;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    if (paramInt == 0)
    {
      String str1 = this.mHostnameField.getText().toString().trim();
      String str2 = this.mPortField.getText().toString().trim();
      String str3 = this.mExclusionListField.getText().toString().trim();
      str1 = getActivity().getString(validate(str1, str2, str3));
      return new AlertDialog.Builder(getActivity()).setTitle(2131690915).setPositiveButton(2131690916, null).setMessage(str1).create();
    }
    return null;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mView = paramLayoutInflater.inflate(2130968942, paramViewGroup, false);
    initView(this.mView);
    populateFields();
    return this.mView;
  }
  
  void populateFields()
  {
    Activity localActivity = getActivity();
    Object localObject1 = "";
    int i = -1;
    String str = "";
    Object localObject2 = ((ConnectivityManager)getActivity().getSystemService("connectivity")).getGlobalProxy();
    if (localObject2 != null)
    {
      localObject1 = ((ProxyInfo)localObject2).getHost();
      i = ((ProxyInfo)localObject2).getPort();
      str = ((ProxyInfo)localObject2).getExclusionListAsString();
    }
    localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = "";
    }
    this.mHostnameField.setText((CharSequence)localObject2);
    if (i == -1) {}
    for (localObject1 = "";; localObject1 = Integer.toString(i))
    {
      this.mPortField.setText((CharSequence)localObject1);
      this.mExclusionListField.setText(str);
      localObject1 = localActivity.getIntent();
      str = ((Intent)localObject1).getStringExtra("button-label");
      if (!TextUtils.isEmpty(str)) {
        this.mOKButton.setText(str);
      }
      localObject1 = ((Intent)localObject1).getStringExtra("title");
      if (!TextUtils.isEmpty((CharSequence)localObject1)) {
        localActivity.setTitle((CharSequence)localObject1);
      }
      return;
    }
  }
  
  boolean saveToDb()
  {
    Object localObject = this.mHostnameField.getText().toString().trim();
    String str1 = this.mPortField.getText().toString().trim();
    String str2 = this.mExclusionListField.getText().toString().trim();
    int i = 0;
    if (validate((String)localObject, str1, str2) != 0)
    {
      showDialog(0);
      return false;
    }
    if (str1.length() > 0) {}
    try
    {
      i = Integer.parseInt(str1);
      localObject = new ProxyInfo((String)localObject, i, str2);
      ((ConnectivityManager)getActivity().getSystemService("connectivity")).setGlobalProxy((ProxyInfo)localObject);
      return true;
    }
    catch (NumberFormatException localNumberFormatException) {}
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ProxySelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */