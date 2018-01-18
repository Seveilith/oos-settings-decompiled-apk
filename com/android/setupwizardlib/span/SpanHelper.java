package com.android.setupwizardlib.span;

import android.text.Spannable;

public class SpanHelper
{
  public static void replaceSpan(Spannable paramSpannable, Object paramObject1, Object paramObject2)
  {
    int i = paramSpannable.getSpanStart(paramObject1);
    int j = paramSpannable.getSpanEnd(paramObject1);
    paramSpannable.removeSpan(paramObject1);
    paramSpannable.setSpan(paramObject2, i, j, 0);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\span\SpanHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */