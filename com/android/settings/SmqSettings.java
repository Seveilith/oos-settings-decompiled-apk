package com.android.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SmqSettings
{
  public static final String SMQ_KEY_VALUE = "app_status";
  public static final String SMQ_PREFS_NAME = "smqpreferences";
  private Context mContext;
  private SharedPreferences mSmqPreferences;
  
  public SmqSettings(Context paramContext)
  {
    this.mContext = paramContext;
    new DBReadAsyncTask(this.mContext).execute(new Void[0]);
    this.mSmqPreferences = this.mContext.getSharedPreferences("smqpreferences", 0);
  }
  
  public boolean isShowSmqSettings()
  {
    boolean bool = false;
    if (this.mSmqPreferences.getInt("app_status", 0) > 0) {
      bool = true;
    }
    return bool;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SmqSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */