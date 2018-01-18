package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.Secure;

abstract class SettingsContentObserver
  extends ContentObserver
{
  public SettingsContentObserver(Handler paramHandler)
  {
    super(paramHandler);
  }
  
  public abstract void onChange(boolean paramBoolean, Uri paramUri);
  
  public void register(ContentResolver paramContentResolver)
  {
    paramContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_enabled"), false, this);
    paramContentResolver.registerContentObserver(Settings.Secure.getUriFor("enabled_accessibility_services"), false, this);
  }
  
  public void unregister(ContentResolver paramContentResolver)
  {
    paramContentResolver.unregisterContentObserver(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\SettingsContentObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */