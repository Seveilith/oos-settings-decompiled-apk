package com.android.settings;

import android.app.Activity;
import android.view.MenuItem;
import com.android.internal.logging.MetricsLogger;

public abstract class InstrumentedActivity
  extends Activity
{
  protected abstract int getMetricsCategory();
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 16908332)
    {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  public void onPause()
  {
    super.onPause();
    MetricsLogger.hidden(this, getMetricsCategory());
  }
  
  public void onResume()
  {
    super.onResume();
    MetricsLogger.visible(this, getMetricsCategory());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\InstrumentedActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */