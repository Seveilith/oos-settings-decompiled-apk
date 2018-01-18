package com.android.settings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.io.File;

public class ManualDisplayActivity
  extends Activity
{
  private static final String DEFAULT_MANUAL_PATH = "/system/etc/MANUAL.html.gz";
  private static final String PROPERTY_MANUAL_PATH = "ro.config.manual_path";
  private static final String TAG = "SettingsManualActivity";
  
  private void showErrorAndFinish()
  {
    Toast.makeText(this, 2131691992, 1).show();
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (!getResources().getBoolean(2131558417)) {
      finish();
    }
    Object localObject = SystemProperties.get("ro.config.manual_path", "/system/etc/MANUAL.html.gz");
    if (TextUtils.isEmpty((CharSequence)localObject))
    {
      Log.e("SettingsManualActivity", "The system property for the manual is empty");
      showErrorAndFinish();
      return;
    }
    paramBundle = new File((String)localObject);
    if ((!paramBundle.exists()) || (paramBundle.length() == 0L))
    {
      Log.e("SettingsManualActivity", "Manual file " + (String)localObject + " does not exist");
      showErrorAndFinish();
      return;
    }
    localObject = new Intent("android.intent.action.VIEW");
    ((Intent)localObject).setDataAndType(Uri.fromFile(paramBundle), "text/html");
    ((Intent)localObject).putExtra("android.intent.extra.TITLE", getString(2131691991));
    ((Intent)localObject).addCategory("android.intent.category.DEFAULT");
    ((Intent)localObject).setPackage("com.android.htmlviewer");
    try
    {
      startActivity((Intent)localObject);
      finish();
      return;
    }
    catch (ActivityNotFoundException paramBundle)
    {
      Log.e("SettingsManualActivity", "Failed to find viewer", paramBundle);
      showErrorAndFinish();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ManualDisplayActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */