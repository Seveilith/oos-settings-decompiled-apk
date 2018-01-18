package com.android.settings.bluetooth;

import android.text.InputFilter;
import android.text.Spanned;

class Utf8ByteLengthFilter
  implements InputFilter
{
  private final int mMaxBytes;
  
  Utf8ByteLengthFilter(int paramInt)
  {
    this.mMaxBytes = paramInt;
  }
  
  public CharSequence filter(CharSequence paramCharSequence, int paramInt1, int paramInt2, Spanned paramSpanned, int paramInt3, int paramInt4)
  {
    int j = 0;
    int k = paramInt1;
    if (k < paramInt2)
    {
      i = paramCharSequence.charAt(k);
      if (i < 128) {
        i = 1;
      }
      for (;;)
      {
        j += i;
        k += 1;
        break;
        if (i < 2048) {
          i = 2;
        } else {
          i = 3;
        }
      }
    }
    int n = paramSpanned.length();
    int m = 0;
    k = 0;
    if (k < n)
    {
      if (k >= paramInt3)
      {
        i = m;
        if (k < paramInt4) {}
      }
      else
      {
        i = paramSpanned.charAt(k);
        if (i >= 128) {
          break label151;
        }
        i = 1;
      }
      for (;;)
      {
        i = m + i;
        k += 1;
        m = i;
        break;
        label151:
        if (i < 2048) {
          i = 2;
        } else {
          i = 3;
        }
      }
    }
    int i = this.mMaxBytes - m;
    if (i <= 0) {
      return "";
    }
    if (i >= j) {
      return null;
    }
    paramInt4 = paramInt1;
    while (paramInt4 < paramInt2)
    {
      paramInt3 = paramCharSequence.charAt(paramInt4);
      if (paramInt3 < 128) {
        paramInt3 = 1;
      }
      for (;;)
      {
        i -= paramInt3;
        if (i >= 0) {
          break;
        }
        return paramCharSequence.subSequence(paramInt1, paramInt4);
        if (paramInt3 < 2048) {
          paramInt3 = 2;
        } else {
          paramInt3 = 3;
        }
      }
      paramInt4 += 1;
    }
    return null;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\Utf8ByteLengthFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */