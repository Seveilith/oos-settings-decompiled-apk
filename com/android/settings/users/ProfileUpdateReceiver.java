package com.android.settings.users;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.Utils;

public class ProfileUpdateReceiver
  extends BroadcastReceiver
{
  private static final String KEY_PROFILE_NAME_COPIED_ONCE = "name_copied_once";
  
  static void copyProfileName(Context paramContext)
  {
    SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("profile", 0);
    if (localSharedPreferences.contains("name_copied_once")) {
      return;
    }
    int i = UserHandle.myUserId();
    UserManager localUserManager = (UserManager)paramContext.getSystemService("user");
    paramContext = Utils.getMeProfileName(paramContext, false);
    if ((paramContext != null) && (paramContext.length() > 0))
    {
      localUserManager.setUserName(i, paramContext);
      localSharedPreferences.edit().putBoolean("name_copied_once", true).commit();
    }
  }
  
  public void onReceive(final Context paramContext, Intent paramIntent)
  {
    new Thread()
    {
      public void run()
      {
        Utils.copyMeProfilePhoto(paramContext, null);
        ProfileUpdateReceiver.copyProfileName(paramContext);
      }
    }.start();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\users\ProfileUpdateReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */