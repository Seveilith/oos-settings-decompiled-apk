package com.android.settings;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.util.Log;
import android.webkit.IWebViewUpdateService;
import android.webkit.IWebViewUpdateService.Stub;
import android.webkit.WebViewProviderInfo;
import java.util.ArrayList;

public class WebViewImplementation
  extends InstrumentedActivity
  implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener
{
  private static final String TAG = "WebViewImplementation";
  private IWebViewUpdateService mWebViewUpdateService;
  
  protected int getMetricsCategory()
  {
    return 405;
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    int i = 0;
    super.onCreate(paramBundle);
    if (!UserManager.get(this).isAdminUser())
    {
      finish();
      return;
    }
    this.mWebViewUpdateService = IWebViewUpdateService.Stub.asInterface(ServiceManager.getService("webviewupdate"));
    for (;;)
    {
      int k;
      try
      {
        WebViewProviderInfo[] arrayOfWebViewProviderInfo = this.mWebViewUpdateService.getValidWebViewPackages();
        if (arrayOfWebViewProviderInfo == null)
        {
          Log.e("WebViewImplementation", "No WebView providers available");
          finish();
          return;
        }
        Object localObject = this.mWebViewUpdateService.getCurrentWebViewPackageName();
        paramBundle = (Bundle)localObject;
        if (localObject == null) {
          paramBundle = "";
        }
        j = -1;
        localObject = new ArrayList();
        final ArrayList localArrayList = new ArrayList();
        int m = arrayOfWebViewProviderInfo.length;
        if (i < m)
        {
          WebViewProviderInfo localWebViewProviderInfo = arrayOfWebViewProviderInfo[i];
          k = j;
          if (Utils.isPackageEnabled(this, localWebViewProviderInfo.packageName))
          {
            ((ArrayList)localObject).add(localWebViewProviderInfo.description);
            localArrayList.add(localWebViewProviderInfo.packageName);
            k = j;
            if (paramBundle.contentEquals(localWebViewProviderInfo.packageName)) {
              k = localArrayList.size() - 1;
            }
          }
        }
        else
        {
          new AlertDialog.Builder(this).setTitle(2131689757).setSingleChoiceItems((CharSequence[])((ArrayList)localObject).toArray(new String[0]), j, new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              try
              {
                WebViewImplementation.-get0(WebViewImplementation.this).changeProviderAndSetting((String)localArrayList.get(paramAnonymousInt));
                WebViewImplementation.this.finish();
                return;
              }
              catch (RemoteException paramAnonymousDialogInterface)
              {
                for (;;)
                {
                  Log.w("WebViewImplementation", "Problem reaching webviewupdate service", paramAnonymousDialogInterface);
                }
              }
            }
          }).setNegativeButton(17039360, null).setOnCancelListener(this).setOnDismissListener(this).show();
          return;
        }
      }
      catch (RemoteException paramBundle)
      {
        Log.w("WebViewImplementation", "Problem reaching webviewupdate service", paramBundle);
        finish();
        return;
      }
      i += 1;
      int j = k;
    }
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    finish();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\WebViewImplementation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */