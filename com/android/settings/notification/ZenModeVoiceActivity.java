package com.android.settings.notification;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.UserHandle;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.settings.utils.VoiceSettingsActivity;
import java.util.Locale;

public class ZenModeVoiceActivity
  extends VoiceSettingsActivity
{
  private static final int MINUTES_MS = 60000;
  private static final String TAG = "ZenModeVoiceActivity";
  
  private CharSequence getChangeSummary(int paramInt1, int paramInt2)
  {
    int i3 = -1;
    int n = -1;
    int i1 = -1;
    int i2 = -1;
    int j = i1;
    int k = n;
    int m = i2;
    int i = i3;
    switch (paramInt1)
    {
    default: 
      i = i3;
      m = i2;
      k = n;
      j = i1;
    }
    while ((paramInt2 < 0) || (paramInt1 == 0))
    {
      return getString(i);
      i = 2131693320;
      k = 2131951641;
      j = 2131951642;
      m = 2131693321;
      continue;
      i = 2131693322;
      j = i1;
      k = n;
      m = i2;
    }
    long l1 = System.currentTimeMillis();
    long l2 = 60000 * paramInt2;
    if (DateFormat.is24HourFormat(this, UserHandle.myUserId())) {}
    Resources localResources;
    for (Object localObject = "Hm";; localObject = "hma")
    {
      localObject = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), (String)localObject), l1 + l2);
      localResources = getResources();
      if (paramInt2 >= 60) {
        break;
      }
      return localResources.getQuantityString(k, paramInt2, new Object[] { Integer.valueOf(paramInt2), localObject });
    }
    if (paramInt2 % 60 != 0) {
      return localResources.getString(m, new Object[] { localObject });
    }
    paramInt1 = paramInt2 / 60;
    return localResources.getQuantityString(j, paramInt1, new Object[] { Integer.valueOf(paramInt1), localObject });
  }
  
  private void setZenModeConfig(int paramInt, Condition paramCondition)
  {
    if (paramCondition != null)
    {
      NotificationManager.from(this).setZenMode(paramInt, paramCondition.id, "ZenModeVoiceActivity");
      return;
    }
    NotificationManager.from(this).setZenMode(paramInt, null, "ZenModeVoiceActivity");
  }
  
  protected boolean onVoiceSettingInteraction(Intent paramIntent)
  {
    if (paramIntent.hasExtra("android.settings.extra.do_not_disturb_mode_enabled"))
    {
      int j = paramIntent.getIntExtra("android.settings.extra.do_not_disturb_mode_minutes", -1);
      Intent localIntent = null;
      Object localObject = null;
      int i = 0;
      if (paramIntent.getBooleanExtra("android.settings.extra.do_not_disturb_mode_enabled", false))
      {
        paramIntent = (Intent)localObject;
        if (j > 0) {
          paramIntent = ZenModeConfig.toTimeCondition(this, j, UserHandle.myUserId());
        }
        i = 3;
        localIntent = paramIntent;
      }
      setZenModeConfig(i, localIntent);
      paramIntent = (AudioManager)getSystemService("audio");
      if (paramIntent != null) {
        paramIntent.adjustStreamVolume(5, 0, 1);
      }
      notifySuccess(getChangeSummary(i, j));
      return false;
    }
    Log.v("ZenModeVoiceActivity", "Missing extra android.provider.Settings.EXTRA_DO_NOT_DISTURB_MODE_ENABLED");
    finish();
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenModeVoiceActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */