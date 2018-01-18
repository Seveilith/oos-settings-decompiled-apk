package com.android.settings;

import android.support.v14.preference.PreferenceFragment;
import com.android.internal.logging.MetricsLogger;

public abstract class InstrumentedPreferenceFragment
  extends PreferenceFragment
{
  protected abstract int getMetricsCategory();
  
  public void onPause()
  {
    super.onPause();
    MetricsLogger.hidden(getActivity(), getMetricsCategory());
  }
  
  public void onResume()
  {
    super.onResume();
    MetricsLogger.visible(getActivity(), getMetricsCategory());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\InstrumentedPreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */