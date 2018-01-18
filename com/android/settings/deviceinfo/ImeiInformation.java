package com.android.settings.deviceinfo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TtsSpan.DigitsBuilder;
import android.view.View;
import android.view.Window;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.settings.SettingsPreferenceFragment;

public class ImeiInformation
  extends SettingsPreferenceFragment
{
  private static final int IMEI_14_DIGIT = 14;
  private static final String KEY_ICC_ID = "icc_id";
  private static final String KEY_IMEI = "imei";
  private static final String KEY_IMEI_SV = "imei_sv";
  private static final String KEY_MEID_NUMBER = "meid_number";
  private static final String KEY_MIN_NUMBER = "min_number";
  private static final String KEY_PRL_VERSION = "prl_version";
  private boolean isMultiSIM = false;
  private SubscriptionManager mSubscriptionManager;
  
  private void initPreferenceScreen(int paramInt)
  {
    boolean bool = true;
    if (paramInt > 1) {}
    for (;;)
    {
      this.isMultiSIM = bool;
      int i = 0;
      while (i < paramInt)
      {
        addPreferencesFromResource(2131230757);
        setPreferenceValue(i);
        setNewKey(i);
        i += 1;
      }
      bool = false;
    }
  }
  
  private void removePreferenceFromScreen(String paramString)
  {
    paramString = findPreference(paramString);
    if (paramString != null) {
      getPreferenceScreen().removePreference(paramString);
    }
  }
  
  private void setNewKey(int paramInt)
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    int j = localPreferenceScreen.getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      Preference localPreference = localPreferenceScreen.getPreference(i);
      String str = localPreference.getKey();
      if (!str.startsWith("_"))
      {
        localPreference.setKey("_" + str + String.valueOf(paramInt));
        updateTitle(localPreference, paramInt);
      }
      i += 1;
    }
  }
  
  private void setPreferenceValue(int paramInt)
  {
    Phone localPhone = PhoneFactory.getPhone(paramInt);
    bool2 = false;
    try
    {
      localObject1 = (CarrierConfigManager)getContext().getSystemService("carrier_config");
      localObject2 = SubscriptionManager.getSubId(paramInt);
      bool1 = bool2;
      if (localObject1 != null)
      {
        bool1 = bool2;
        if (((CarrierConfigManager)localObject1).getConfigForSubId(localObject2[0]) != null) {
          bool1 = ((CarrierConfigManager)localObject1).getConfigForSubId(localObject2[0]).getBoolean("config_enable_display_14digit_imei");
        }
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        Object localObject1;
        Object localObject2;
        boolean bool1 = bool2;
      }
    }
    if (localPhone != null)
    {
      localObject2 = localPhone.getImei();
      localObject1 = localObject2;
      if (bool1)
      {
        localObject1 = localObject2;
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          if (((String)localObject2).length() > 14) {
            localObject1 = ((String)localObject2).substring(0, 14);
          }
        }
      }
      if (localPhone.getPhoneType() != 2) {
        break label252;
      }
      setSummaryText("meid_number", localPhone.getMeid());
      setSummaryText("min_number", localPhone.getCdmaMin());
      if (getResources().getBoolean(2131558448)) {
        findPreference("min_number").setTitle(2131691698);
      }
      setSummaryText("prl_version", localPhone.getCdmaPrlVersion());
      if (localPhone.getLteOnCdmaMode() == 1)
      {
        setSummaryText("icc_id", localPhone.getIccSerialNumber());
        setSummaryText("imei", (String)localObject1);
        setSummaryTextAsDigit("imei", localPhone.getImei());
        setSummaryTextAsDigit("imei_sv", localPhone.getDeviceSvn());
      }
    }
    else
    {
      return;
    }
    removePreferenceFromScreen("imei_sv");
    removePreferenceFromScreen("imei");
    removePreferenceFromScreen("icc_id");
    return;
    label252:
    if (getResources().getBoolean(2131558451)) {
      setSummaryText("icc_id", localPhone.getIccSerialNumber());
    }
    for (;;)
    {
      setSummaryText("imei", (String)localObject1);
      setSummaryText("imei_sv", localPhone.getDeviceSvn());
      setSummaryTextAsDigit("imei", localPhone.getImei());
      setSummaryTextAsDigit("imei_sv", localPhone.getDeviceSvn());
      removePreferenceFromScreen("prl_version");
      removePreferenceFromScreen("meid_number");
      removePreferenceFromScreen("min_number");
      return;
      removePreferenceFromScreen("icc_id");
    }
  }
  
  private void setSummaryText(String paramString, CharSequence paramCharSequence, boolean paramBoolean)
  {
    Preference localPreference = findPreference(paramString);
    if (TextUtils.isEmpty(paramCharSequence)) {
      paramString = getResources().getString(2131690786);
    }
    for (;;)
    {
      if (localPreference != null) {
        localPreference.setSummary(paramString);
      }
      return;
      paramString = paramCharSequence;
      if (paramBoolean)
      {
        paramString = paramCharSequence;
        if (TextUtils.isDigitsOnly(paramCharSequence))
        {
          paramString = new SpannableStringBuilder(paramCharSequence);
          paramString.setSpan(new TtsSpan.DigitsBuilder(paramCharSequence.toString()).build(), 0, paramString.length(), 33);
        }
      }
    }
  }
  
  private void setSummaryText(String paramString1, String paramString2)
  {
    setSummaryText(paramString1, paramString2, false);
  }
  
  private void setSummaryTextAsDigit(String paramString1, String paramString2)
  {
    setSummaryText(paramString1, paramString2, true);
  }
  
  private void updateTitle(Preference paramPreference, int paramInt)
  {
    if (paramPreference != null)
    {
      String str2 = paramPreference.getTitle().toString();
      String str1 = str2;
      if (this.isMultiSIM) {
        str1 = str2 + " " + getResources().getString(2131693374, new Object[] { Integer.valueOf(paramInt + 1) });
      }
      paramPreference.setTitle(str1);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 41;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
    super.onCreate(paramBundle);
    this.mSubscriptionManager = SubscriptionManager.from(getContext());
    initPreferenceScreen(((TelephonyManager)getSystemService("phone")).getSimCount());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\ImeiInformation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */