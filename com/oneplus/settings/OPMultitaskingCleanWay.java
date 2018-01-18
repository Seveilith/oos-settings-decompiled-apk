package com.oneplus.settings;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings.System;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.ui.RadioButtonPreference;
import com.android.settings.ui.RadioButtonPreference.OnClickListener;

public class OPMultitaskingCleanWay
  extends SettingsPreferenceFragment
  implements RadioButtonPreference.OnClickListener
{
  private static final String KEY_DEEP_CLEAR_WAY = "op_deep_clear_way";
  private static final String KEY_NORMAL_CLEAR_WAY = "op_normal_clear_way";
  private static final int METRICSLOGGER_MULTITASKING_CLEARWAY_VALUE = 1262;
  private Context mContext;
  private RadioButtonPreference mDeepClearWayButton;
  private RadioButtonPreference mNormalClearWayButton;
  
  protected int getMetricsCategory()
  {
    return 1262;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230799);
    this.mContext = SettingsBaseApplication.mApplication;
    this.mNormalClearWayButton = ((RadioButtonPreference)findPreference("op_normal_clear_way"));
    this.mDeepClearWayButton = ((RadioButtonPreference)findPreference("op_deep_clear_way"));
    this.mNormalClearWayButton.setOnClickListener(this);
    this.mDeepClearWayButton.setOnClickListener(this);
  }
  
  public void onRadioButtonClicked(RadioButtonPreference paramRadioButtonPreference)
  {
    if (paramRadioButtonPreference == this.mNormalClearWayButton)
    {
      this.mNormalClearWayButton.setChecked(true);
      this.mDeepClearWayButton.setChecked(false);
      Settings.System.putInt(this.mContext.getContentResolver(), "oem_clear_way", 0);
    }
    while (paramRadioButtonPreference != this.mDeepClearWayButton) {
      return;
    }
    this.mNormalClearWayButton.setChecked(false);
    this.mDeepClearWayButton.setChecked(true);
    Settings.System.putInt(this.mContext.getContentResolver(), "oem_clear_way", 1);
  }
  
  public void onResume()
  {
    boolean bool2 = true;
    RadioButtonPreference localRadioButtonPreference;
    if ((this.mNormalClearWayButton != null) && (this.mDeepClearWayButton != null))
    {
      int i = Settings.System.getInt(this.mContext.getContentResolver(), "oem_clear_way", 0);
      localRadioButtonPreference = this.mNormalClearWayButton;
      if (i != 0) {
        break label72;
      }
      bool1 = true;
      localRadioButtonPreference.setChecked(bool1);
      localRadioButtonPreference = this.mDeepClearWayButton;
      if (i != 1) {
        break label77;
      }
    }
    label72:
    label77:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      localRadioButtonPreference.setChecked(bool1);
      super.onResume();
      return;
      bool1 = false;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPMultitaskingCleanWay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */