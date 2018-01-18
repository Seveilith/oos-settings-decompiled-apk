package com.oneplus.settings.backgroundoptimize.funcswitch;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.backgroundoptimize.BgOActivityManager;

public class BgOptimizeSwitch
  extends SettingsPreferenceFragment
{
  private static final String PREF_BG_OPTIMIZE_SWITCH = "bg_optimize_switch";
  
  private void initData()
  {
    boolean bool = true;
    SwitchPreference localSwitchPreference = (SwitchPreference)findPreference("bg_optimize_switch");
    if (localSwitchPreference != null) {
      if (1 != BgOActivityManager.getInstance(getPrefContext()).getAppControlState(0)) {
        break label37;
      }
    }
    for (;;)
    {
      localSwitchPreference.setChecked(bool);
      return;
      label37:
      bool = false;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230741);
    initData();
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if ("bg_optimize_switch".equals(paramPreference.getKey()))
    {
      paramPreference = (SwitchPreference)paramPreference;
      BgOActivityManager localBgOActivityManager = BgOActivityManager.getInstance(getPrefContext());
      if (paramPreference.isChecked()) {}
      for (int i = 1;; i = 0)
      {
        localBgOActivityManager.setAppControlState(0, i);
        return true;
      }
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\backgroundoptimize\funcswitch\BgOptimizeSwitch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */