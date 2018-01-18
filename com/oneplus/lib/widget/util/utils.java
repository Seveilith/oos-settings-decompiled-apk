package com.oneplus.lib.widget.util;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.util.TypedValue;

public class utils
{
  public static int resolveDefStyleAttr(Context paramContext, int paramInt)
  {
    TypedValue localTypedValue = new TypedValue();
    paramContext.getTheme().resolveAttribute(paramInt, localTypedValue, true);
    if (localTypedValue.resourceId >>> 24 == 1) {
      return 0;
    }
    return paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\util\utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */