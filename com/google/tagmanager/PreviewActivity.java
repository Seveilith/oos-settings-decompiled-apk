package com.google.tagmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class PreviewActivity
  extends Activity
{
  private void displayAlert(String paramString1, String paramString2, String paramString3)
  {
    AlertDialog localAlertDialog = new AlertDialog.Builder(this).create();
    localAlertDialog.setTitle(paramString1);
    localAlertDialog.setMessage(paramString2);
    localAlertDialog.setButton(-1, paramString3, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
    });
    localAlertDialog.show();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    try
    {
      super.onCreate(paramBundle);
      Log.i("Preview activity");
      paramBundle = getIntent().getData();
      if (TagManager.getInstance(this).setPreviewData(paramBundle)) {}
      for (;;)
      {
        paramBundle = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (paramBundle != null) {
          break;
        }
        Log.i("No launch activity found for package name: " + getPackageName());
        return;
        paramBundle = "Cannot preview the app with the uri: " + paramBundle + ". Launching current version instead.";
        Log.w(paramBundle);
        displayAlert("Preview failure", paramBundle, "Continue");
      }
      Log.i("Invoke the launch activity for package name: " + getPackageName());
    }
    catch (Exception paramBundle)
    {
      Log.e("Calling preview threw an exception: " + paramBundle.getMessage());
      return;
    }
    startActivity(paramBundle);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\PreviewActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */