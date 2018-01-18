package com.oneplus.settings.ringtone;

import android.util.Log;

public class OPMyLog
{
  private static final boolean DEBUG = false;
  
  public static void d(String paramString1, String paramString2) {}
  
  public static void e(String paramString1, String paramString2)
  {
    Log.e("chenhl", "[" + paramString1 + "] " + paramString2);
  }
  
  public static void e(String paramString1, String paramString2, Throwable paramThrowable)
  {
    Log.e("chenhl", "[" + paramString1 + "] " + paramString2, paramThrowable);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ringtone\OPMyLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */