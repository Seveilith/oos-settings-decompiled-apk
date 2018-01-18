package com.android.settings.accessibility;

import android.os.Bundle;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.widget.ToggleSwitch;

public class ToggleScreenReaderPreferenceFragmentForSetupWizard
  extends ToggleAccessibilityServicePreferenceFragment
{
  private boolean mToggleSwitchWasInitiallyChecked;
  
  protected int getMetricsCategory()
  {
    return 371;
  }
  
  protected void onProcessArguments(Bundle paramBundle)
  {
    super.onProcessArguments(paramBundle);
    this.mToggleSwitchWasInitiallyChecked = this.mToggleSwitch.isChecked();
  }
  
  public void onStop()
  {
    if (this.mToggleSwitch.isChecked() != this.mToggleSwitchWasInitiallyChecked) {
      MetricsLogger.action(getContext(), 371, this.mToggleSwitch.isChecked());
    }
    super.onStop();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\ToggleScreenReaderPreferenceFragmentForSetupWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */