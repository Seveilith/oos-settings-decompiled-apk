package com.android.settings.applications;

import android.content.Intent;
import com.android.settings.SettingsActivity;

public class InstalledAppDetailsTop
  extends SettingsActivity
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":settings:show_fragment", InstalledAppDetails.class.getName());
    return localIntent;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return InstalledAppDetails.class.getName().equals(paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\InstalledAppDetailsTop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */