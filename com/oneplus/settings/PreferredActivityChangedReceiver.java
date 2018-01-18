package com.oneplus.settings;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import com.android.settings.applications.DefaultHomePreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.Arrays;
import java.util.List;

public class PreferredActivityChangedReceiver
  extends BroadcastReceiver
{
  private static final String ACTION_PREFERRED_ACTIVITY_CHANGED = "android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED";
  private static final List<String> SETUPWIZARD_PKGS = Arrays.asList(new String[] { "com.google.android.setupwizard", "com.oneplus.provision" });
  private static final String TAG = "PreferredActivityChangedReceiver";
  
  public static boolean isHomeDefault(String paramString1, String paramString2, Context paramContext)
  {
    if (paramString2 != null) {
      return paramString2.equals(paramString1);
    }
    return false;
  }
  
  private void updateComponentEnable(Context paramContext, String paramString1, String paramString2, String paramString3)
  {
    if (OPUtils.isAppExist(paramContext, paramString2))
    {
      boolean bool = isHomeDefault(paramString2, paramString1, paramContext);
      if (bool) {}
      for (int i = 1;; i = 2)
      {
        paramContext = paramContext.getPackageManager();
        Log.d("PreferredActivityChangedReceiver", "pkgName:" + paramString2 + ", isDefaultHome:" + bool);
        paramString1 = new ComponentName(paramString2, paramString3);
        try
        {
          paramContext.setComponentEnabledSetting(paramString1, i, 1);
          return;
        }
        catch (IllegalArgumentException paramContext)
        {
          Log.d("PreferredActivityChangedReceiver", "updateComponentEnable:ComponentName does not exist.");
          return;
        }
      }
    }
    Log.d("PreferredActivityChangedReceiver", "package not exist:" + paramString2);
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    Log.d("PreferredActivityChangedReceiver", "onReceive:" + paramIntent.getAction());
    if ("android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED".equals(paramIntent.getAction())) {
      new AsyncTask()
      {
        protected Void doInBackground(String... paramAnonymousVarArgs)
        {
          paramAnonymousVarArgs = SettingsBaseApplication.mApplication;
          String str = DefaultHomePreference.getCurrentDefaultHome(paramAnonymousVarArgs);
          Log.d("PreferredActivityChangedReceiver", "defaultHomePkg: " + str);
          if (str == null)
          {
            Log.d("PreferredActivityChangedReceiver", "Nothing to do as current default launcher is null");
            return null;
          }
          if (PreferredActivityChangedReceiver.-get0().contains(str))
          {
            Log.d("PreferredActivityChangedReceiver", "Nothing to do as the current default launcher pkg: " + str);
            return null;
          }
          PreferredActivityChangedReceiver.-wrap0(PreferredActivityChangedReceiver.this, paramAnonymousVarArgs, str, "net.oneplus.h2launcher", "net.oneplus.h2launcher.customizations.wallpaper.SetWallpaperActivity");
          PreferredActivityChangedReceiver.-wrap0(PreferredActivityChangedReceiver.this, paramAnonymousVarArgs, str, "net.oneplus.launcher", "net.oneplus.launcher.wallpaper.SetWallpaperActivity");
          return null;
        }
      }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[0]);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\PreferredActivityChangedReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */