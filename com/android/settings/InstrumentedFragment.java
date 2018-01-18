package com.android.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.view.MenuItem;
import com.android.internal.logging.MetricsLogger;

public abstract class InstrumentedFragment
  extends PreferenceFragment
{
  public static final int PREFERENCE_ACTIVITY_FRAGMENT = 100001;
  public static final int UNDECLARED = 100000;
  
  protected abstract int getMetricsCategory();
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setHasOptionsMenu(true);
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString) {}
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 16908332)
    {
      getActivity().finish();
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
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


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\InstrumentedFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */