package com.google.tagmanager;

import android.os.Build.VERSION;
import com.google.android.gms.common.util.VisibleForTesting;

class Base64Encoder
{
  public static final int DEFAULT = 0;
  public static final int NO_PADDING = 1;
  public static final int URL_SAFE = 2;
  
  public static byte[] decode(String paramString, int paramInt)
  {
    int i = 0;
    if (getSdkVersion() < 8) {
      if (((paramInt & 0x1) != 0) && ((paramInt & 0x2) != 0)) {
        break label66;
      }
    }
    label58:
    label66:
    for (paramInt = i; paramInt == 0; paramInt = 1)
    {
      return Base64.decode(paramString);
      i = 2;
      if ((paramInt & 0x1) == 0) {
        if ((paramInt & 0x2) != 0) {
          break label58;
        }
      }
      for (;;)
      {
        return android.util.Base64.decode(paramString, i);
        i = 3;
        break;
        i |= 0x8;
      }
    }
    return Base64.decodeWebSafe(paramString);
  }
  
  public static String encodeToString(byte[] paramArrayOfByte, int paramInt)
  {
    int i = 0;
    boolean bool;
    if (getSdkVersion() < 8)
    {
      if ((paramInt & 0x1) == 0) {
        break label69;
      }
      bool = false;
      if ((paramInt & 0x2) != 0) {
        break label74;
      }
    }
    label61:
    label69:
    label74:
    for (paramInt = i;; paramInt = 1)
    {
      if (paramInt != 0) {
        break label79;
      }
      return Base64.encode(paramArrayOfByte, bool);
      i = 2;
      if ((paramInt & 0x1) == 0) {
        if ((paramInt & 0x2) != 0) {
          break label61;
        }
      }
      for (;;)
      {
        return android.util.Base64.encodeToString(paramArrayOfByte, i);
        i = 3;
        break;
        i |= 0x8;
      }
      bool = true;
      break;
    }
    label79:
    return Base64.encodeWebSafe(paramArrayOfByte, bool);
  }
  
  @VisibleForTesting
  static int getSdkVersion()
  {
    return Build.VERSION.SDK_INT;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\Base64Encoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */