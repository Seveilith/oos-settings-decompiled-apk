package com.android.settings.nfc;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import com.oneplus.settings.utils.OPUtils;

public class HowItWorks
  extends Activity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (OPUtils.isWhiteModeOn(getContentResolver())) {
      getWindow().getDecorView().setSystemUiVisibility(8192);
    }
    setContentView(2130968756);
    getActionBar().setDisplayHomeAsUpEnabled(true);
    ((Button)findViewById(2131362221)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        HowItWorks.this.finish();
      }
    });
  }
  
  public boolean onNavigateUp()
  {
    finish();
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\nfc\HowItWorks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */