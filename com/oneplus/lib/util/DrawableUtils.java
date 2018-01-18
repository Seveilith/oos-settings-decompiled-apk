package com.oneplus.lib.util;

import android.graphics.PorterDuff.Mode;
import android.os.Build.VERSION;

public class DrawableUtils
{
  public static PorterDuff.Mode parseTintMode(int paramInt, PorterDuff.Mode paramMode)
  {
    switch (paramInt)
    {
    case 4: 
    case 6: 
    case 7: 
    case 8: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    default: 
      return paramMode;
    case 3: 
      return PorterDuff.Mode.SRC_OVER;
    case 5: 
      return PorterDuff.Mode.SRC_IN;
    case 9: 
      return PorterDuff.Mode.SRC_ATOP;
    case 14: 
      return PorterDuff.Mode.MULTIPLY;
    case 15: 
      return PorterDuff.Mode.SCREEN;
    }
    if (Build.VERSION.SDK_INT >= 11) {
      paramMode = PorterDuff.Mode.valueOf("ADD");
    }
    return paramMode;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\util\DrawableUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */