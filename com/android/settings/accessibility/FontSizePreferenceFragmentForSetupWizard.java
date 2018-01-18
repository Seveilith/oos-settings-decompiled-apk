package com.android.settings.accessibility;

import com.android.internal.logging.MetricsLogger;

public class FontSizePreferenceFragmentForSetupWizard
  extends ToggleFontSizePreferenceFragment
{
  protected int getMetricsCategory()
  {
    return 369;
  }
  
  public void onStop()
  {
    if (this.mCurrentIndex != this.mInitialIndex) {
      MetricsLogger.action(getContext(), 369, this.mCurrentIndex);
    }
    super.onStop();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\FontSizePreferenceFragmentForSetupWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */