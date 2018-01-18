package com.oneplus.lib.widget.recyclerview;

class ContainerHelpers
{
  static final int[] EMPTY_INTS = new int[0];
  static final long[] EMPTY_LONGS = new long[0];
  static final Object[] EMPTY_OBJECTS = new Object[0];
  
  static int binarySearch(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = paramInt1 - 1;
    paramInt1 = i;
    i = j;
    while (paramInt1 <= i)
    {
      j = paramInt1 + i >>> 1;
      int k = paramArrayOfInt[j];
      if (k < paramInt2) {
        paramInt1 = j + 1;
      } else if (k > paramInt2) {
        i = j - 1;
      } else {
        return j;
      }
    }
    return paramInt1;
  }
  
  static int binarySearch(long[] paramArrayOfLong, int paramInt, long paramLong)
  {
    int i = 0;
    int j = paramInt - 1;
    paramInt = i;
    i = j;
    while (paramInt <= i)
    {
      j = paramInt + i >>> 1;
      long l = paramArrayOfLong[j];
      if (l < paramLong) {
        paramInt = j + 1;
      } else if (l > paramLong) {
        i = j - 1;
      } else {
        return j;
      }
    }
    return paramInt;
  }
  
  public static boolean equal(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 != paramObject2)
    {
      if (paramObject1 != null) {
        return paramObject1.equals(paramObject2);
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  public static int idealByteArraySize(int paramInt)
  {
    int i = 4;
    while (i < 32)
    {
      if (paramInt <= (1 << i) - 12) {
        return (1 << i) - 12;
      }
      i += 1;
    }
    return paramInt;
  }
  
  public static int idealIntArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }
  
  public static int idealLongArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 8) / 8;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\recyclerview\ContainerHelpers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */