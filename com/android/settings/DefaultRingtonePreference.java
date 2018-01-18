package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.AttributeSet;
import com.oneplus.settings.ringtone.OPRingtoneManager;
import com.oneplus.settings.ringtone.OPRingtonePickerActivity;
import java.io.PrintStream;

public class DefaultRingtonePreference
  extends RingtonePreference
{
  private static final String TAG = "DefaultRingtonePreference";
  
  public DefaultRingtonePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected void onClick()
  {
    System.out.println("zhuyang--DefaultRingtonePreference--onClick");
    Intent localIntent = new Intent(getContext(), OPRingtonePickerActivity.class);
    onPrepareRingtonePickerIntent(localIntent);
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
    return OPRingtoneManager.getActualDefaultRingtoneUri(getContext(), getRingtoneType());
  }
  
  protected void onSaveRingtone(Uri paramUri)
  {
    RingtoneManager.setActualDefaultRingtoneUri(getContext(), getRingtoneType(), paramUri);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\DefaultRingtonePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */