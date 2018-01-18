package com.google.tagmanager;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import java.io.File;

class FutureApis
{
  @TargetApi(9)
  static boolean setOwnerOnlyReadWrite(String paramString)
  {
    if (version() >= 9)
    {
      paramString = new File(paramString);
      paramString.setReadable(false, false);
      paramString.setWritable(false, false);
      paramString.setReadable(true, true);
      paramString.setWritable(true, true);
      return true;
    }
    return false;
  }
  
  public static int version()
  {
    try
    {
      int i = Integer.parseInt(Build.VERSION.SDK);
      return i;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.e("Invalid version number: " + Build.VERSION.SDK);
    }
    return 0;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\FutureApis.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */