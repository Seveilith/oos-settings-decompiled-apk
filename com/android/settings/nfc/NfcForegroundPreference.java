package com.android.settings.nfc;

import android.content.Context;
import android.support.v7.preference.DropDownPreference;

public class NfcForegroundPreference
  extends DropDownPreference
  implements PaymentBackend.Callback
{
  private final PaymentBackend mPaymentBackend;
  
  public NfcForegroundPreference(Context paramContext, PaymentBackend paramPaymentBackend)
  {
    super(paramContext);
    this.mPaymentBackend = paramPaymentBackend;
    this.mPaymentBackend.registerCallback(this);
    refresh();
  }
  
  public void onPaymentAppsChanged()
  {
    refresh();
  }
  
  protected boolean persistString(String paramString)
  {
    boolean bool = false;
    PaymentBackend localPaymentBackend = this.mPaymentBackend;
    if (Integer.parseInt(paramString) != 0) {
      bool = true;
    }
    localPaymentBackend.setForegroundMode(bool);
    refresh();
    return true;
  }
  
  void refresh()
  {
    this.mPaymentBackend.getDefaultApp();
    boolean bool = this.mPaymentBackend.isForegroundMode();
    setPersistent(false);
    setTitle(getContext().getString(2131692983));
    setEntries(new CharSequence[] { getContext().getString(2131692985), getContext().getString(2131692984) });
    setEntryValues(new CharSequence[] { "1", "0" });
    if (bool)
    {
      setValue("1");
      setSummary(getContext().getString(2131692985));
      return;
    }
    setValue("0");
    setSummary(getContext().getString(2131692984));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\nfc\NfcForegroundPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */