package com.airbnb.lottie;

import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.ColorInt;

public class SimpleColorFilter
  extends PorterDuffColorFilter
{
  public SimpleColorFilter(@ColorInt int paramInt)
  {
    super(paramInt, PorterDuff.Mode.SRC_ATOP);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\SimpleColorFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */