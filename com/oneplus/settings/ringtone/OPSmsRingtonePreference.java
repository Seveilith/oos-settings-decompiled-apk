package com.oneplus.settings.ringtone;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings.System;
import android.util.AttributeSet;
import com.android.settings.RingtonePreference;

public class OPSmsRingtonePreference
  extends RingtonePreference
{
  public OPSmsRingtonePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private Uri getDefaultSmsNotificationRingtone(Context paramContext)
  {
    Object localObject = null;
    String str = Settings.System.getString(paramContext.getContentResolver(), "mms_notification");
    paramContext = (Context)localObject;
    if (str != null) {
      paramContext = Uri.parse(str);
    }
    return paramContext;
  }
  
  protected void onClick()
  {
    Intent localIntent = new Intent(getContext(), OPRingtonePickerActivity.class);
    localIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", onRestoreRingtone());
    localIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
    localIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", getShowSilent());
    localIntent.putExtra("android.intent.extra.ringtone.TYPE", 100);
    localIntent.putExtra("android.intent.extra.ringtone.TITLE", getTitle());
    getContext().startActivity(localIntent);
  }
  
  public void onPrepareRingtonePickerIntent(Intent paramIntent)
  {
    super.onPrepareRingtonePickerIntent(paramIntent);
    if (paramIntent != null) {
      paramIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
    }
  }
  
  protected Uri onRestoreRingtone()
  {
    return getDefaultSmsNotificationRingtone(getContext());
  }
  
  protected void onSaveRingtone(Uri paramUri)
  {
    String str = null;
    ContentResolver localContentResolver = getContext().getContentResolver();
    if (paramUri != null) {
      str = paramUri.toString();
    }
    Settings.System.putString(localContentResolver, "mms_notification", str);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ringtone\OPSmsRingtonePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */