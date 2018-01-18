package com.android.settings.datausage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import com.android.settings.SettingsActivity;
import com.android.settingslib.AppItem;

public class AppDataUsageActivity
  extends SettingsActivity
{
  private static final boolean DEBUG = false;
  private static final String TAG = "AppDataUsageActivity";
  
  protected boolean isValidFragment(String paramString)
  {
    if (!super.isValidFragment(paramString)) {
      return AppDataUsage.class.getName().equals(paramString);
    }
    return true;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    Intent localIntent = getIntent();
    Object localObject1 = localIntent.getData().getSchemeSpecificPart();
    Object localObject2 = getPackageManager();
    try
    {
      int i = ((PackageManager)localObject2).getPackageUid((String)localObject1, 0);
      localObject1 = new Bundle();
      localObject2 = new AppItem(i);
      ((AppItem)localObject2).addUid(i);
      ((Bundle)localObject1).putParcelable("app_item", (Parcelable)localObject2);
      localIntent.putExtra(":settings:show_fragment_args", (Bundle)localObject1);
      localIntent.putExtra(":settings:show_fragment", AppDataUsage.class.getName());
      localIntent.putExtra(":settings:show_fragment_title_resid", 2131693384);
      super.onCreate(paramBundle);
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("AppDataUsageActivity", "invalid package: " + (String)localObject1);
      try
      {
        super.onCreate(paramBundle);
        finish();
        return;
      }
      catch (Exception paramBundle)
      {
        paramBundle = paramBundle;
        finish();
        return;
      }
      finally
      {
        paramBundle = finally;
        finish();
        throw paramBundle;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\AppDataUsageActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */