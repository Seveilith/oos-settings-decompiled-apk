package com.oneplus.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.oneplus.settings.utils.OPUtils;

public class OPAuthenticationInformationSettings
  extends Activity
{
  private static final String ONEPLUS_A3000 = "oneplus A3000";
  private static final String ONEPLUS_A3000_CMIITID = "2016CP1331";
  private static final String ONEPLUS_A3010 = "oneplus A3010";
  private static final String ONEPLUS_A3010_CMIITID = "2016CP5088";
  private static final String ONEPLUS_A5000 = "ONEPLUS A5000";
  private static final String ONEPLUS_A5000_CMITTID = "2017CP2198";
  private static final String ONEPLUS_A5010 = "ONEPLUS A5010";
  private static final String ONEPLUS_A5010_CMITTID = "2017CP6039";
  private TextView mCmiitIdView;
  private TextView mModelTextView;
  
  private void setCmiitID()
  {
    String str = getResources().getString(2131690747);
    if (Build.MODEL.equalsIgnoreCase("oneplus A3000"))
    {
      this.mCmiitIdView.setText(String.format(str, new Object[] { "2016CP1331" }));
      return;
    }
    if (Build.MODEL.equalsIgnoreCase("oneplus A3010"))
    {
      this.mCmiitIdView.setText(String.format(str, new Object[] { "2016CP5088" }));
      return;
    }
    if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5000"))
    {
      this.mCmiitIdView.setText(String.format(str, new Object[] { "2017CP2198" }));
      return;
    }
    if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5010"))
    {
      this.mCmiitIdView.setText(String.format(str, new Object[] { "2017CP6039" }));
      return;
    }
    this.mCmiitIdView.setText(String.format(str, new Object[] { "2016CP5088" }));
  }
  
  private void setDeviceType()
  {
    String str = getResources().getString(2131690748);
    this.mModelTextView.setText(String.format(str, new Object[] { Build.MODEL }));
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setTitle(2131690328);
    setContentView(2130968790);
    this.mModelTextView = ((TextView)findViewById(2131362276));
    this.mCmiitIdView = ((TextView)findViewById(2131362272));
    if (OPUtils.isWhiteModeOn(getContentResolver())) {
      getWindow().getDecorView().setSystemUiVisibility(8192);
    }
    setDeviceType();
    setCmiitID();
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


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPAuthenticationInformationSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */