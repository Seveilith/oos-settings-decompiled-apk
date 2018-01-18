package com.oneplus.lib.preference;

public class CharSequences
{
  public static int compareToIgnoreCase(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    int m = paramCharSequence1.length();
    int n = paramCharSequence2.length();
    int k;
    int i;
    int j;
    if (m < n)
    {
      k = m;
      i = 0;
      j = 0;
    }
    while (j < k)
    {
      int i1 = Character.toLowerCase(paramCharSequence1.charAt(j)) - Character.toLowerCase(paramCharSequence2.charAt(i));
      if (i1 != 0)
      {
        return i1;
        k = n;
        i = 0;
        j = 0;
      }
      else
      {
        i += 1;
        j += 1;
      }
    }
    return m - n;
  }
  
  public static boolean equals(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if (paramCharSequence1.length() != paramCharSequence2.length()) {
      return false;
    }
    int j = paramCharSequence1.length();
    int i = 0;
    while (i < j)
    {
      if (paramCharSequence1.charAt(i) != paramCharSequence2.charAt(i)) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public static CharSequence forAsciiBytes(byte[] paramArrayOfByte)
  {
    new CharSequence()
    {
      public char charAt(int paramAnonymousInt)
      {
        return (char)this.val$bytes[paramAnonymousInt];
      }
      
      public int length()
      {
        return this.val$bytes.length;
      }
      
      public CharSequence subSequence(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        return CharSequences.forAsciiBytes(this.val$bytes, paramAnonymousInt1, paramAnonymousInt2);
      }
      
      public String toString()
      {
        return new String(this.val$bytes);
      }
    };
  }
  
  public static CharSequence forAsciiBytes(byte[] paramArrayOfByte, final int paramInt1, final int paramInt2)
  {
    validate(paramInt1, paramInt2, paramArrayOfByte.length);
    new CharSequence()
    {
      public char charAt(int paramAnonymousInt)
      {
        return (char)this.val$bytes[(paramInt1 + paramAnonymousInt)];
      }
      
      public int length()
      {
        return paramInt2 - paramInt1;
      }
      
      public CharSequence subSequence(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramAnonymousInt1 -= paramInt1;
        paramAnonymousInt2 -= paramInt1;
        CharSequences.validate(paramAnonymousInt1, paramAnonymousInt2, length());
        return CharSequences.forAsciiBytes(this.val$bytes, paramAnonymousInt1, paramAnonymousInt2);
      }
      
      public String toString()
      {
        return new String(this.val$bytes, paramInt1, length());
      }
    };
  }
  
  static void validate(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 < 0) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 < 0) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 > paramInt3) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt1 > paramInt2) {
      throw new IndexOutOfBoundsException();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\CharSequences.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */