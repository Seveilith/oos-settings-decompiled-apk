package com.oneplus.settings.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.MediaStore.Audio.Media;
import android.provider.Settings.System;
import android.util.Log;
import com.oneplus.settings.ringtone.OPMyLog;

public class OPNotificationUtils
{
  private static final String DEFAULT_RINGTONE_PROPERTY_PREFIX_RO = "ro.config.";
  static final String[] OriginName = { "beep", "capriccioso", "Cloud", "echo", "In_high_spirit", "Journey", "longing", "Old_telephone", "oneplus_tune", "Rotation", "Innocence", "Talk_about", "Ding", "Distant", "Drops", "Elegant", "Free", "harp", "Linger", "Meet", "Quickly", "surprise", "Tactfully", "Wind_chime", "A_starry_night", "alarm_clock1", "alarm_clock2", "flyer", "Spring", "Walking_in_the_rain" };
  private static final String TAG = "OPNotificationUtils";
  
  private static String getDefaultRingtoneFileName(Context paramContext, String paramString)
  {
    return SystemProperties.get("ro.config." + paramString);
  }
  
  private static boolean hasData(Cursor paramCursor)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramCursor != null)
    {
      bool1 = bool2;
      if (paramCursor.getCount() > 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static String replaceWith(Context paramContext, String paramString1, String paramString2)
  {
    String str2 = paramContext.getResources().getString(2131690223);
    int i = 1;
    if ((paramString2.endsWith("notification_sound")) || (paramString2.endsWith("mms_notification"))) {
      i = 2;
    }
    for (;;)
    {
      OPMyLog.d("", "type:" + i + " settingsName:" + paramString2);
      String str1 = RingtoneManager.getRingtoneAlias(paramContext, i, paramString1);
      paramString1 = str1;
      if (str1.contains(str2))
      {
        restoreRingtoneIfNotExist(paramContext, paramString2);
        if (paramString2.endsWith("ringtone")) {
          restoreRingtoneIfNotExist(paramContext, "ringtone_2");
        }
        if (!paramString2.endsWith("ringtone")) {
          break;
        }
        paramString1 = paramContext.getResources().getString(2131690224);
      }
      return paramString1;
      if (paramString2.endsWith("alarm_alert")) {
        i = 4;
      }
    }
    if (paramString2.endsWith("notification_sound")) {
      return paramContext.getResources().getString(2131690225);
    }
    return paramContext.getResources().getString(2131690226);
  }
  
  private static void restoreRingtoneIfNotExist(Context paramContext, String paramString)
  {
    Object localObject3 = Settings.System.getString(paramContext.getContentResolver(), paramString);
    if (localObject3 == null) {
      return;
    }
    ContentResolver localContentResolver = paramContext.getContentResolver();
    Object localObject2 = null;
    Object localObject1 = null;
    try
    {
      localObject3 = paramContext.getContentResolver().query(Uri.parse((String)localObject3), new String[] { "title" }, null, null, null);
      Object localObject4 = localObject3;
      localObject1 = localObject3;
      localObject2 = localObject3;
      if (!hasData((Cursor)localObject3))
      {
        localObject1 = localObject3;
        localObject2 = localObject3;
        localObject4 = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        localObject1 = localObject3;
        localObject2 = localObject3;
        String str = getDefaultRingtoneFileName(paramContext, paramString);
        localObject1 = localObject3;
        localObject2 = localObject3;
        localObject3 = localContentResolver.query((Uri)localObject4, new String[] { "_id" }, "_display_name=?", new String[] { str }, null, null);
        localObject4 = localObject3;
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (hasData((Cursor)localObject3))
        {
          localObject4 = localObject3;
          localObject1 = localObject3;
          localObject2 = localObject3;
          if (((Cursor)localObject3).moveToFirst())
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            int i = ((Cursor)localObject3).getInt(0);
            localObject1 = localObject3;
            localObject2 = localObject3;
            Settings.System.putString(paramContext.getContentResolver(), paramString, ContentUris.withAppendedId(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, i).toString());
            localObject4 = localObject3;
          }
        }
      }
      return;
    }
    catch (Exception paramContext)
    {
      localObject2 = localObject1;
      Log.e("OPNotificationUtils", "RemoteException in restoreRingtoneIfNotExist()", paramContext);
      return;
    }
    finally
    {
      if (localObject2 != null) {
        ((Cursor)localObject2).close();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\utils\OPNotificationUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */