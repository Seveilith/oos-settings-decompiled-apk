package com.android.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.Toast;
import com.android.internal.telephony.IccCard;
import com.android.internal.telephony.IccCardConstants.State;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

public class IccLockSettings
  extends SettingsPreferenceFragment
  implements EditPinPreference.OnPinEnteredListener
{
  private static final boolean DBG = true;
  private static final String DIALOG_ERROR = "dialogError";
  private static final String DIALOG_PIN = "dialogPin";
  private static final String DIALOG_STATE = "dialogState";
  private static final String ENABLE_TO_STATE = "enableState";
  private static final int ICC_LOCK_MODE = 1;
  private static final int ICC_NEW_MODE = 3;
  private static final int ICC_OLD_MODE = 2;
  private static final int ICC_REENTER_MODE = 4;
  private static final int MAX_PIN_LENGTH = 8;
  private static final int MIN_PIN_LENGTH = 4;
  private static final int MSG_CHANGE_ICC_PIN_COMPLETE = 101;
  private static final int MSG_ENABLE_ICC_PIN_COMPLETE = 100;
  private static final int MSG_SIM_STATE_CHANGED = 102;
  private static final String NEW_PINCODE = "newPinCode";
  private static final int OFF_MODE = 0;
  private static final String OLD_PINCODE = "oldPinCode";
  private static final String PIN_DIALOG = "sim_pin";
  private static final String PIN_TOGGLE = "sim_toggle";
  private static final String TAG = "IccLockSettings";
  private int mDialogState = 0;
  private TabHost.TabContentFactory mEmptyTabContent = new TabHost.TabContentFactory()
  {
    public View createTabContent(String paramAnonymousString)
    {
      return new View(IccLockSettings.-get1(IccLockSettings.this).getContext());
    }
  };
  private String mError;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      boolean bool2 = true;
      boolean bool1 = true;
      AsyncResult localAsyncResult = (AsyncResult)paramAnonymousMessage.obj;
      IccLockSettings localIccLockSettings;
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 100: 
        localIccLockSettings = IccLockSettings.this;
        if (localAsyncResult.exception == null) {}
        for (;;)
        {
          IccLockSettings.-wrap1(localIccLockSettings, bool1, paramAnonymousMessage.arg1);
          return;
          bool1 = false;
        }
      case 101: 
        localIccLockSettings = IccLockSettings.this;
        if (localAsyncResult.exception == null) {}
        for (bool1 = bool2;; bool1 = false)
        {
          IccLockSettings.-wrap2(localIccLockSettings, bool1, paramAnonymousMessage.arg1);
          return;
        }
      }
      IccLockSettings.-wrap3(IccLockSettings.this);
    }
  };
  private ListView mListView;
  private String mNewPin;
  private String mOldPin;
  private Phone mPhone;
  private String mPin;
  private EditPinPreference mPinDialog;
  private SwitchPreference mPinToggle;
  private Resources mRes;
  private final BroadcastReceiver mSimStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ("android.intent.action.SIM_STATE_CHANGED".equals(paramAnonymousContext)) {
        IccLockSettings.-get0(IccLockSettings.this).sendMessage(IccLockSettings.-get0(IccLockSettings.this).obtainMessage(102));
      }
      if ("android.intent.action.ACTION_SUBINFO_CONTENT_CHANGE".equals(paramAnonymousContext))
      {
        int j = ((TelephonyManager)IccLockSettings.this.getContext().getSystemService("phone")).getSimCount();
        if (j > 1)
        {
          int k = IccLockSettings.-get1(IccLockSettings.this).getCurrentTab();
          IccLockSettings.-get1(IccLockSettings.this).clearAllTabs();
          paramAnonymousIntent = SubscriptionManager.from(IccLockSettings.this.getContext());
          int i = 0;
          if (i < j)
          {
            paramAnonymousContext = paramAnonymousIntent.getActiveSubscriptionInfoForSimSlotIndex(i);
            TabHost localTabHost = IccLockSettings.-get1(IccLockSettings.this);
            IccLockSettings localIccLockSettings = IccLockSettings.this;
            if (paramAnonymousContext == null) {}
            for (paramAnonymousContext = IccLockSettings.this.getContext().getString(2131693093, new Object[] { Integer.valueOf(i + 1) });; paramAnonymousContext = paramAnonymousContext.getDisplayName())
            {
              localTabHost.addTab(IccLockSettings.-wrap0(localIccLockSettings, String.valueOf(i), String.valueOf(paramAnonymousContext)));
              i += 1;
              break;
            }
          }
          IccLockSettings.-get1(IccLockSettings.this).setCurrentTab(k);
        }
      }
    }
  };
  private TabHost mTabHost;
  private TabHost.OnTabChangeListener mTabListener = new TabHost.OnTabChangeListener()
  {
    public void onTabChanged(String paramAnonymousString)
    {
      Object localObject = null;
      int i = Integer.parseInt(paramAnonymousString);
      paramAnonymousString = SubscriptionManager.from(IccLockSettings.this.getActivity().getBaseContext()).getActiveSubscriptionInfoForSimSlotIndex(i);
      IccLockSettings localIccLockSettings = IccLockSettings.this;
      if (paramAnonymousString == null) {}
      for (paramAnonymousString = (String)localObject;; paramAnonymousString = PhoneFactory.getPhone(SubscriptionManager.getPhoneId(paramAnonymousString.getSubscriptionId())))
      {
        IccLockSettings.-set0(localIccLockSettings, paramAnonymousString);
        IccLockSettings.-wrap3(IccLockSettings.this);
        return;
      }
    }
  };
  private TabWidget mTabWidget;
  private boolean mToState;
  
  private TabHost.TabSpec buildTabSpec(String paramString1, String paramString2)
  {
    return this.mTabHost.newTabSpec(paramString1).setIndicator(paramString2).setContent(this.mEmptyTabContent);
  }
  
  private String getPinPasswordErrorMessage(int paramInt)
  {
    String str;
    if (paramInt == 0) {
      str = this.mRes.getString(2131691672);
    }
    for (;;)
    {
      Log.d("IccLockSettings", "getPinPasswordErrorMessage: attemptsRemaining=" + paramInt + " displayMessage=" + str);
      return str;
      if (paramInt > 0) {
        str = this.mRes.getQuantityString(2131951627, paramInt, new Object[] { Integer.valueOf(paramInt) });
      } else {
        str = this.mRes.getString(2131691673);
      }
    }
  }
  
  static String getSummary(Context paramContext)
  {
    paramContext = paramContext.getResources();
    if (isIccLockEnabled()) {
      return paramContext.getString(2131691649);
    }
    return paramContext.getString(2131691650);
  }
  
  private void iccLockChanged(boolean paramBoolean, int paramInt)
  {
    if (paramBoolean) {
      this.mPinToggle.setChecked(this.mToState);
    }
    for (;;)
    {
      this.mPinToggle.setEnabled(true);
      resetDialogState();
      return;
      Toast.makeText(getContext(), getPinPasswordErrorMessage(paramInt), 1).show();
    }
  }
  
  private void iccPinChanged(boolean paramBoolean, int paramInt)
  {
    if (!paramBoolean) {
      Toast.makeText(getContext(), getPinPasswordErrorMessage(paramInt), 1).show();
    }
    for (;;)
    {
      resetDialogState();
      return;
      Toast.makeText(getContext(), this.mRes.getString(2131691662), 0).show();
    }
  }
  
  static boolean isIccLockEnabled()
  {
    return PhoneFactory.getDefaultPhone().getIccCard().getIccLockEnabled();
  }
  
  private boolean reasonablePin(String paramString)
  {
    if ((paramString == null) || (paramString.length() < 4)) {}
    while (paramString.length() > 8)
    {
      Toast.makeText(getPrefContext(), 17039547, 1).show();
      return false;
    }
    return true;
  }
  
  private void resetDialogState()
  {
    this.mError = null;
    this.mDialogState = 2;
    this.mPin = "";
    setDialogValues();
    this.mDialogState = 0;
  }
  
  private void setDialogValues()
  {
    this.mPinDialog.setText("");
    Object localObject1 = "";
    switch (this.mDialogState)
    {
    }
    for (;;)
    {
      Object localObject2 = localObject1;
      if (this.mError != null)
      {
        localObject2 = this.mError + "\n" + (String)localObject1;
        this.mError = null;
      }
      this.mPinDialog.setDialogMessage((CharSequence)localObject2);
      return;
      localObject2 = this.mRes.getString(2131691652);
      EditPinPreference localEditPinPreference = this.mPinDialog;
      if (this.mToState) {}
      for (localObject1 = this.mRes.getString(2131691653);; localObject1 = this.mRes.getString(2131691654))
      {
        localEditPinPreference.setDialogTitle((CharSequence)localObject1);
        localObject1 = localObject2;
        break;
      }
      localObject1 = this.mRes.getString(2131691655);
      this.mPinDialog.setDialogTitle(this.mRes.getString(2131691658));
      continue;
      localObject1 = this.mRes.getString(2131691656);
      this.mPinDialog.setDialogTitle(this.mRes.getString(2131691658));
      continue;
      localObject1 = this.mRes.getString(2131691657);
      this.mPinDialog.setDialogTitle(this.mRes.getString(2131691658));
    }
  }
  
  private void showPinDialog()
  {
    if (this.mDialogState == 0) {
      return;
    }
    setDialogValues();
    this.mPinDialog.showPinDialog();
  }
  
  private void tryChangeIccLockState()
  {
    if ((this.mPhone == null) || (this.mPhone.getIccCard() == null)) {
      return;
    }
    Message localMessage = Message.obtain(this.mHandler, 100);
    this.mPhone.getIccCard().setIccLockEnabled(this.mToState, this.mPin, localMessage);
    this.mPinToggle.setEnabled(false);
  }
  
  private void tryChangePin()
  {
    if ((this.mPhone == null) || (this.mPhone.getIccCard() == null)) {
      return;
    }
    Message localMessage = Message.obtain(this.mHandler, 101);
    this.mPhone.getIccCard().changeIccLockPassword(this.mOldPin, this.mNewPin, localMessage);
  }
  
  private void updatePreferences()
  {
    if ((this.mPhone != null) && (this.mPinToggle != null) && (this.mPinDialog != null) && (this.mPhone.getIccCard() != null)) {
      if (this.mPhone.getIccCard().getState() != IccCardConstants.State.READY)
      {
        this.mPinToggle.setEnabled(false);
        this.mPinDialog.setEnabled(false);
        this.mPinToggle.setChecked(this.mPhone.getIccCard().getIccLockEnabled());
      }
    }
    for (;;)
    {
      if ((this.mPhone == null) && (this.mPinDialog != null))
      {
        Dialog localDialog = this.mPinDialog.getDialog();
        if (localDialog == null)
        {
          return;
          this.mPinToggle.setEnabled(true);
          this.mPinDialog.setEnabled(true);
          break;
          if ((this.mPinToggle == null) || (this.mPinDialog == null)) {
            continue;
          }
          this.mPinDialog.setEnabled(false);
          this.mPinToggle.setEnabled(false);
          continue;
        }
        localDialog.dismiss();
      }
    }
  }
  
  protected int getMetricsCategory()
  {
    return 56;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
    super.onCreate(paramBundle);
    if (Utils.isMonkeyRunning())
    {
      finish();
      return;
    }
    addPreferencesFromResource(2131230859);
    this.mPinDialog = ((EditPinPreference)findPreference("sim_pin"));
    this.mPinToggle = ((SwitchPreference)findPreference("sim_toggle"));
    if ((paramBundle != null) && (paramBundle.containsKey("dialogState")))
    {
      this.mDialogState = paramBundle.getInt("dialogState");
      this.mPin = paramBundle.getString("dialogPin");
      this.mError = paramBundle.getString("dialogError");
      this.mToState = paramBundle.getBoolean("enableState");
      switch (this.mDialogState)
      {
      }
    }
    for (;;)
    {
      this.mPinDialog.setOnPinEnteredListener(this);
      getPreferenceScreen().setPersistent(false);
      this.mRes = getResources();
      return;
      this.mOldPin = paramBundle.getString("oldPinCode");
      continue;
      this.mOldPin = paramBundle.getString("oldPinCode");
      this.mNewPin = paramBundle.getString("newPinCode");
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    int j = ((TelephonyManager)getContext().getSystemService("phone")).getSimCount();
    if (j > 1)
    {
      View localView = paramLayoutInflater.inflate(2130968729, paramViewGroup, false);
      ViewGroup localViewGroup = (ViewGroup)localView.findViewById(2131361935);
      Utils.prepareCustomPreferencesList(paramViewGroup, localView, localViewGroup, false);
      localViewGroup.addView(super.onCreateView(paramLayoutInflater, localViewGroup, paramBundle));
      this.mTabHost = ((TabHost)localView.findViewById(16908306));
      this.mTabWidget = ((TabWidget)localView.findViewById(16908307));
      this.mListView = ((ListView)localView.findViewById(16908298));
      this.mTabHost.setup();
      this.mTabHost.setOnTabChangedListener(this.mTabListener);
      this.mTabHost.clearAllTabs();
      paramViewGroup = SubscriptionManager.from(getContext());
      int i = 0;
      if (i < j)
      {
        paramLayoutInflater = paramViewGroup.getActiveSubscriptionInfoForSimSlotIndex(i);
        paramBundle = this.mTabHost;
        if (paramLayoutInflater == null) {}
        for (paramLayoutInflater = getContext().getString(2131693093, new Object[] { Integer.valueOf(i + 1) });; paramLayoutInflater = paramLayoutInflater.getDisplayName())
        {
          paramBundle.addTab(buildTabSpec(String.valueOf(i), String.valueOf(paramLayoutInflater)));
          i += 1;
          break;
        }
      }
      paramLayoutInflater = paramViewGroup.getActiveSubscriptionInfoForSimSlotIndex(0);
      if (paramLayoutInflater == null) {}
      for (paramLayoutInflater = null;; paramLayoutInflater = PhoneFactory.getPhone(SubscriptionManager.getPhoneId(paramLayoutInflater.getSubscriptionId())))
      {
        this.mPhone = paramLayoutInflater;
        return localView;
      }
    }
    this.mPhone = PhoneFactory.getDefaultPhone();
    return super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  public void onPause()
  {
    super.onPause();
    getContext().unregisterReceiver(this.mSimStateReceiver);
  }
  
  public void onPinEntered(EditPinPreference paramEditPinPreference, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      resetDialogState();
      return;
    }
    this.mPin = paramEditPinPreference.getText();
    if (!reasonablePin(this.mPin))
    {
      this.mError = this.mRes.getString(2131691659);
      showPinDialog();
      return;
    }
    switch (this.mDialogState)
    {
    default: 
      return;
    case 1: 
      tryChangeIccLockState();
      return;
    case 2: 
      this.mOldPin = this.mPin;
      this.mDialogState = 3;
      this.mError = null;
      this.mPin = null;
      showPinDialog();
      return;
    case 3: 
      this.mNewPin = this.mPin;
      this.mDialogState = 4;
      this.mPin = null;
      showPinDialog();
      return;
    }
    if (!this.mPin.equals(this.mNewPin))
    {
      this.mError = this.mRes.getString(2131691660);
      this.mDialogState = 3;
      this.mPin = null;
      showPinDialog();
      return;
    }
    this.mError = null;
    tryChangePin();
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    boolean bool = false;
    if (paramPreference == this.mPinToggle)
    {
      this.mToState = this.mPinToggle.isChecked();
      paramPreference = this.mPinToggle;
      if (this.mToState)
      {
        paramPreference.setChecked(bool);
        this.mDialogState = 1;
        showPinDialog();
      }
    }
    while (paramPreference != this.mPinDialog) {
      for (;;)
      {
        return true;
        bool = true;
      }
    }
    this.mDialogState = 2;
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.SIM_STATE_CHANGED");
    localIntentFilter.addAction("android.intent.action.ACTION_SUBINFO_CONTENT_CHANGE");
    getContext().registerReceiver(this.mSimStateReceiver, localIntentFilter);
    if (this.mDialogState != 0)
    {
      showPinDialog();
      return;
    }
    resetDialogState();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    if (this.mPinDialog.isDialogOpen())
    {
      paramBundle.putInt("dialogState", this.mDialogState);
      paramBundle.putString("dialogPin", this.mPinDialog.getEditText().getText().toString());
      paramBundle.putString("dialogError", this.mError);
      paramBundle.putBoolean("enableState", this.mToState);
      switch (this.mDialogState)
      {
      default: 
        return;
      case 3: 
        paramBundle.putString("oldPinCode", this.mOldPin);
        return;
      }
      paramBundle.putString("oldPinCode", this.mOldPin);
      paramBundle.putString("newPinCode", this.mNewPin);
      return;
    }
    super.onSaveInstanceState(paramBundle);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    updatePreferences();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\IccLockSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */