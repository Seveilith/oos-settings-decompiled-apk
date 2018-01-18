package com.android.settings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import com.android.settingslib.HelpUtils;

public class HelpTrampoline
  extends Activity
{
  private static final String TAG = "HelpTrampoline";
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    try
    {
      paramBundle = getIntent().getStringExtra("android.intent.extra.TEXT");
      int i = getResources().getIdentifier(paramBundle, "string", getPackageName());
      paramBundle = HelpUtils.getHelpIntent(this, getResources().getString(i), null);
      if (paramBundle != null) {
        startActivity(paramBundle);
      }
    }
    catch (Resources.NotFoundException|ActivityNotFoundException paramBundle)
    {
      for (;;)
      {
        Log.w("HelpTrampoline", "Failed to resolve help", paramBundle);
      }
    }
    finish();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\HelpTrampoline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */