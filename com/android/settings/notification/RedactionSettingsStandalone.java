package com.android.settings.notification;

import android.content.Intent;
import com.android.settings.SettingsActivity;

public class RedactionSettingsStandalone
  extends SettingsActivity
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":settings:show_fragment", RedactionInterstitial.RedactionInterstitialFragment.class.getName()).putExtra("extra_prefs_show_button_bar", true).putExtra("extra_prefs_set_back_text", (String)null).putExtra("extra_prefs_set_next_text", getString(2131693261));
    return localIntent;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return RedactionInterstitial.RedactionInterstitialFragment.class.getName().equals(paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\RedactionSettingsStandalone.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */