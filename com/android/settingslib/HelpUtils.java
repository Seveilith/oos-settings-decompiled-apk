package com.android.settingslib;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import com.android.internal.logging.MetricsLogger;
import java.net.URISyntaxException;
import java.util.Locale;

public class HelpUtils
{
  private static final String EXTRA_BACKUP_URI = "EXTRA_BACKUP_URI";
  private static final String EXTRA_CONTEXT = "EXTRA_CONTEXT";
  private static final String EXTRA_PRIMARY_COLOR = "EXTRA_PRIMARY_COLOR";
  private static final String EXTRA_THEME = "EXTRA_THEME";
  private static final int MENU_HELP = 101;
  private static final String PARAM_LANGUAGE_CODE = "hl";
  private static final String PARAM_VERSION = "version";
  private static final String TAG = HelpUtils.class.getSimpleName();
  private static String sCachedVersionCode = null;
  
  public static void addIntentParameters(Context paramContext, Intent paramIntent, String paramString, boolean paramBoolean)
  {
    if (!paramIntent.hasExtra("EXTRA_CONTEXT")) {
      paramIntent.putExtra("EXTRA_CONTEXT", paramString);
    }
    Object localObject = paramContext.getResources();
    boolean bool = ((Resources)localObject).getBoolean(R.bool.config_sendPackageName);
    if ((paramBoolean) && (bool))
    {
      paramString = ((Resources)localObject).getString(R.string.config_helpPackageNameKey);
      String str1 = ((Resources)localObject).getString(R.string.config_helpPackageNameValue);
      String str2 = ((Resources)localObject).getString(R.string.config_helpIntentExtraKey);
      localObject = ((Resources)localObject).getString(R.string.config_helpIntentNameKey);
      paramIntent.putExtra(str2, new String[] { paramString });
      paramIntent.putExtra((String)localObject, new String[] { str1 });
    }
    paramIntent.putExtra("EXTRA_THEME", 1);
    paramContext = paramContext.obtainStyledAttributes(new int[] { 16843827 });
    paramIntent.putExtra("EXTRA_PRIMARY_COLOR", paramContext.getColor(0, 0));
    paramContext.recycle();
  }
  
  public static Intent getHelpIntent(Context paramContext, String paramString1, String paramString2)
  {
    if (Settings.Global.getInt(paramContext.getContentResolver(), "device_provisioned", 0) == 0) {
      return null;
    }
    try
    {
      Intent localIntent = Intent.parseUri(paramString1, 3);
      addIntentParameters(paramContext, localIntent, paramString2, true);
      if (localIntent.resolveActivity(paramContext.getPackageManager()) != null) {
        return localIntent;
      }
      if (localIntent.hasExtra("EXTRA_BACKUP_URI"))
      {
        paramString2 = getHelpIntent(paramContext, localIntent.getStringExtra("EXTRA_BACKUP_URI"), paramString2);
        return paramString2;
      }
      return null;
    }
    catch (URISyntaxException paramString2)
    {
      paramContext = new Intent("android.intent.action.VIEW", uriWithAddedParameters(paramContext, Uri.parse(paramString1)));
      paramContext.setFlags(276824064);
    }
    return paramContext;
  }
  
  public static boolean prepareHelpMenuItem(Activity paramActivity, Menu paramMenu, int paramInt, String paramString)
  {
    return false;
  }
  
  public static boolean prepareHelpMenuItem(Activity paramActivity, Menu paramMenu, String paramString1, String paramString2)
  {
    return false;
  }
  
  public static boolean prepareHelpMenuItem(Activity paramActivity, MenuItem paramMenuItem, final String paramString1, String paramString2)
  {
    if (Settings.Global.getInt(paramActivity.getContentResolver(), "device_provisioned", 0) == 0) {
      return false;
    }
    if (TextUtils.isEmpty(paramString1))
    {
      paramMenuItem.setVisible(false);
      return false;
    }
    paramString1 = getHelpIntent(paramActivity, paramString1, paramString2);
    if (paramString1 != null)
    {
      paramMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
      {
        public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
        {
          MetricsLogger.action(this.val$activity, 496, paramString1.getStringExtra("EXTRA_CONTEXT"));
          try
          {
            this.val$activity.startActivityForResult(paramString1, 0);
            return true;
          }
          catch (ActivityNotFoundException paramAnonymousMenuItem)
          {
            for (;;)
            {
              Log.e(HelpUtils.-get0(), "No activity found for intent: " + paramString1);
            }
          }
        }
      });
      paramMenuItem.setShowAsAction(0);
      paramMenuItem.setVisible(true);
      return true;
    }
    paramMenuItem.setVisible(false);
    return false;
  }
  
  public static Uri uriWithAddedParameters(Context paramContext, Uri paramUri)
  {
    paramUri = paramUri.buildUpon();
    paramUri.appendQueryParameter("hl", Locale.getDefault().toString());
    if (sCachedVersionCode == null) {}
    for (;;)
    {
      try
      {
        sCachedVersionCode = Integer.toString(paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionCode);
        paramUri.appendQueryParameter("version", sCachedVersionCode);
        return paramUri.build();
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        Log.wtf(TAG, "Invalid package name for context", paramContext);
        continue;
      }
      paramUri.appendQueryParameter("version", sCachedVersionCode);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\HelpUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */