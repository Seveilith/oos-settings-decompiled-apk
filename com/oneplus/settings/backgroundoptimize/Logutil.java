package com.oneplus.settings.backgroundoptimize;

import android.util.Log;
import com.oneplus.settings.SettingsBaseApplication;

public class Logutil
{
  public static void loge(String paramString1, String paramString2)
  {
    if (SettingsBaseApplication.ONEPLUS_DEBUG) {
      Log.e(paramString1, paramString2);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\backgroundoptimize\Logutil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */