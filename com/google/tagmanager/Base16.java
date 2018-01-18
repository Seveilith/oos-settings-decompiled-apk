package com.google.tagmanager;

class Base16
{
  public static byte[] decode(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    byte[] arrayOfByte;
    if (j % 2 == 0) {
      arrayOfByte = new byte[j / 2];
    }
    for (;;)
    {
      if (i >= j)
      {
        return arrayOfByte;
        throw new IllegalArgumentException("purported base16 string has odd number of characters");
      }
      int k = Character.digit(paramString.charAt(i), 16);
      int m = Character.digit(paramString.charAt(i + 1), 16);
      if (k == -1) {}
      while (m == -1) {
        throw new IllegalArgumentException("purported base16 string has illegal char");
      }
      arrayOfByte[(i / 2)] = ((byte)(byte)((k << 4) + m));
      i += 2;
    }
  }
  
  public static String encode(byte[] paramArrayOfByte)
  {
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    int j = paramArrayOfByte.length;
    if (i >= j) {
      return localStringBuilder.toString().toUpperCase();
    }
    int k = paramArrayOfByte[i];
    if ((k & 0xF0) != 0) {}
    for (;;)
    {
      localStringBuilder.append(Integer.toHexString(k & 0xFF));
      i += 1;
      break;
      localStringBuilder.append("0");
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\Base16.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */