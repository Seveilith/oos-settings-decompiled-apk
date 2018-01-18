package com.oneplus.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import com.oneplus.settings.utils.OPUtils;

public class BaseActivity
  extends Activity
{
  protected void onCreate(Bundle paramBundle)
  {
    if (OPUtils.isWhiteModeOn(getContentResolver())) {
      getWindow().getDecorView().setSystemUiVisibility(8192);
    }
    super.onCreate(paramBundle);
    paramBundle = getActionBar();
    if (paramBundle != null)
    {
      paramBundle.setDisplayHomeAsUpEnabled(true);
      paramBundle.setHomeButtonEnabled(true);
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    }
    finish();
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\BaseActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */