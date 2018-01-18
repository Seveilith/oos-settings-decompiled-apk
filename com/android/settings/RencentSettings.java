package com.android.settings;

import android.app.ActivityManager.TaskDescription;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import com.oneplus.settings.utils.OPUtils;

public abstract class RencentSettings
  extends SettingsActivity
{
  private int getIconId()
  {
    try
    {
      ActivityInfo localActivityInfo = getPackageManager().getActivityInfo(getComponentName(), 0);
      if (localActivityInfo == null) {
        return 0;
      }
      int i = localActivityInfo.getIconResource();
      return i;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return 0;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    try
    {
      int i = OPUtils.getColor(getTheme(), 16843827);
      paramBundle = BitmapFactory.decodeResource(getResources(), 2130903040);
      setTaskDescription(new ActivityManager.TaskDescription(null, paramBundle, i));
      paramBundle.recycle();
      return;
    }
    catch (Resources.NotFoundException paramBundle) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\RencentSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */