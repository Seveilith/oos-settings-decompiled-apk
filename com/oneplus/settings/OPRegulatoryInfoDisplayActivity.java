package com.oneplus.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import com.oneplus.settings.utils.OPUtils;

public class OPRegulatoryInfoDisplayActivity
  extends Activity
{
  private static final String ONEPLUS_A5000 = "ONEPLUS A5000";
  private static final String ONEPLUS_A5010 = "ONEPLUS A5010";
  private ImageView mRegulatoryInfoImage;
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setTitle(2131691983);
    setContentView(2130968851);
    this.mRegulatoryInfoImage = ((ImageView)findViewById(2131362379));
    if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5000")) {
      this.mRegulatoryInfoImage.setImageResource(2130838258);
    }
    for (;;)
    {
      if (OPUtils.isWhiteModeOn(getContentResolver())) {
        getWindow().getDecorView().setSystemUiVisibility(8192);
      }
      return;
      if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5010")) {
        this.mRegulatoryInfoImage.setImageResource(2130838259);
      }
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 16908332)
    {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPRegulatoryInfoDisplayActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */