package com.android.settings.accessibility;

import android.os.Bundle;
import android.provider.Settings.Global;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.ToggleSwitch;
import com.android.settings.widget.ToggleSwitch.OnBeforeCheckedChangeListener;

public class ToggleGlobalGesturePreferenceFragment
  extends ToggleFeaturePreferenceFragment
{
  protected int getMetricsCategory()
  {
    return 6;
  }
  
  protected void onInstallSwitchBarToggleSwitch()
  {
    super.onInstallSwitchBarToggleSwitch();
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener()
    {
      public boolean onBeforeCheckedChanged(ToggleSwitch paramAnonymousToggleSwitch, boolean paramAnonymousBoolean)
      {
        ToggleGlobalGesturePreferenceFragment.this.mSwitchBar.setCheckedInternal(paramAnonymousBoolean);
        ToggleGlobalGesturePreferenceFragment.this.getArguments().putBoolean("checked", paramAnonymousBoolean);
        ToggleGlobalGesturePreferenceFragment.this.onPreferenceToggled(ToggleGlobalGesturePreferenceFragment.this.mPreferenceKey, paramAnonymousBoolean);
        return false;
      }
    });
  }
  
  protected void onPreferenceToggled(String paramString, boolean paramBoolean)
  {
    paramString = getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt(paramString, "enable_accessibility_global_gesture_enabled", i);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\ToggleGlobalGesturePreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */