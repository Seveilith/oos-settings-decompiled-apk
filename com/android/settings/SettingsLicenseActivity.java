package com.android.settings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.io.File;

public class SettingsLicenseActivity
  extends Activity
{
  private static final String DEFAULT_LICENSE_PATH = "/system/etc/NOTICE.html.gz";
  private static final String PROPERTY_LICENSE_PATH = "ro.config.license_path";
  private static final String TAG = "SettingsLicenseActivity";
  
  private void showErrorAndFinish()
  {
    Toast.makeText(this, 2131691994, 1).show();
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Object localObject = SystemProperties.get("ro.config.license_path", "/system/etc/NOTICE.html.gz");
    if (TextUtils.isEmpty((CharSequence)localObject))
    {
      Log.e("SettingsLicenseActivity", "The system property for the license file is empty");
      showErrorAndFinish();
      return;
    }
    paramBundle = new File((String)localObject);
    if ((!paramBundle.exists()) || (paramBundle.length() == 0L))
    {
      Log.e("SettingsLicenseActivity", "License file " + (String)localObject + " does not exist");
      showErrorAndFinish();
      return;
    }
    localObject = new Intent("android.intent.action.VIEW");
    ((Intent)localObject).setDataAndType(Uri.fromFile(paramBundle), "text/html");
    ((Intent)localObject).putExtra("android.intent.extra.TITLE", getString(2131691993));
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
      Log.e("SettingsLicenseActivity", "Failed to find viewer", paramBundle);
      showErrorAndFinish();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SettingsLicenseActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */