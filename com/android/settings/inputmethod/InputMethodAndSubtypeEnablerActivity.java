package com.android.settings.inputmethod;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import com.android.settings.SettingsActivity;

public class InputMethodAndSubtypeEnablerActivity
  extends SettingsActivity
{
  private static final String FRAGMENT_NAME = InputMethodAndSubtypeEnabler.class.getName();
  
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    if (!localIntent.hasExtra(":settings:show_fragment")) {
      localIntent.putExtra(":settings:show_fragment", FRAGMENT_NAME);
    }
    return localIntent;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return FRAGMENT_NAME.equals(paramString);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getActionBar();
    if (paramBundle != null)
    {
      paramBundle.setDisplayHomeAsUpEnabled(true);
      paramBundle.setHomeButtonEnabled(true);
    }
  }
  
  public boolean onNavigateUp()
  {
    finish();
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\InputMethodAndSubtypeEnablerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */