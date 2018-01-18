package android.support.v4.os;

import android.os.Build.VERSION;

public class BuildCompat
{
  public static boolean isAtLeastN()
  {
    return Build.VERSION.SDK_INT >= 24;
  }
  
  public static boolean isAtLeastNMR1()
  {
    return Build.VERSION.SDK_INT >= 25;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\os\BuildCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */