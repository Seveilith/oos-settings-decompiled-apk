package com.android.settings.display;

import com.android.internal.logging.MetricsLogger;

public class ScreenZoomPreferenceFragmentForSetupWizard
  extends ScreenZoomSettings
{
  protected int getMetricsCategory()
  {
    return 370;
  }
  
  public void onStop()
  {
    if (this.mCurrentIndex != this.mInitialIndex) {
      MetricsLogger.action(getContext(), 370, this.mCurrentIndex);
    }
    super.onStop();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\display\ScreenZoomPreferenceFragmentForSetupWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */