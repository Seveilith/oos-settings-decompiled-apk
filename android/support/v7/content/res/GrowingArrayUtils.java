package android.support.v7.content.res;

import java.lang.reflect.Array;

final class GrowingArrayUtils
{
  static
  {
    if (GrowingArrayUtils.class.desiredAssertionStatus()) {}
    for (boolean bool = false;; bool = true)
    {
      -assertionsDisabled = bool;
      return;
    }
  }
  
  public static int[] append(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (!-assertionsDisabled)
    {
      if (paramInt1 <= paramArrayOfInt.length) {}
      for (int i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    int[] arrayOfInt = paramArrayOfInt;
    if (paramInt1 + 1 > paramArrayOfInt.length)
    {
      arrayOfInt = new int[growSize(paramInt1)];
      System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramInt1);
    }
    arrayOfInt[paramInt1] = paramInt2;
    return arrayOfInt;
  }
  
  public static long[] append(long[] paramArrayOfLong, int paramInt, long paramLong)
  {
    if (!-assertionsDisabled)
    {
      if (paramInt <= paramArrayOfLong.length) {}
      for (int i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    long[] arrayOfLong = paramArrayOfLong;
    if (paramInt + 1 > paramArrayOfLong.length)
    {
      arrayOfLong = new long[growSize(paramInt)];
      System.arraycopy(paramArrayOfLong, 0, arrayOfLong, 0, paramInt);
    }
    arrayOfLong[paramInt] = paramLong;
    return arrayOfLong;
  }
  
  public static <T> T[] append(T[] paramArrayOfT, int paramInt, T paramT)
  {
    if (!-assertionsDisabled)
    {
      if (paramInt <= paramArrayOfT.length) {}
      for (int i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    Object localObject = paramArrayOfT;
    if (paramInt + 1 > paramArrayOfT.length)
    {
      localObject = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), growSize(paramInt));
      System.arraycopy(paramArrayOfT, 0, localObject, 0, paramInt);
    }
    localObject[paramInt] = paramT;
    return (T[])localObject;
  }
  
  public static boolean[] append(boolean[] paramArrayOfBoolean, int paramInt, boolean paramBoolean)
  {
    if (!-assertionsDisabled)
    {
      if (paramInt <= paramArrayOfBoolean.length) {}
      for (int i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    boolean[] arrayOfBoolean = paramArrayOfBoolean;
    if (paramInt + 1 > paramArrayOfBoolean.length)
    {
      arrayOfBoolean = new boolean[growSize(paramInt)];
      System.arraycopy(paramArrayOfBoolean, 0, arrayOfBoolean, 0, paramInt);
    }
    arrayOfBoolean[paramInt] = paramBoolean;
    return arrayOfBoolean;
  }
  
  public static int growSize(int paramInt)
  {
    if (paramInt <= 4) {
      return 8;
    }
    return paramInt * 2;
  }
  
  public static int[] insert(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    if (!-assertionsDisabled)
    {
      if (paramInt1 <= paramArrayOfInt.length) {}
      for (int i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    if (paramInt1 + 1 <= paramArrayOfInt.length)
    {
      System.arraycopy(paramArrayOfInt, paramInt2, paramArrayOfInt, paramInt2 + 1, paramInt1 - paramInt2);
      paramArrayOfInt[paramInt2] = paramInt3;
      return paramArrayOfInt;
    }
    int[] arrayOfInt = new int[growSize(paramInt1)];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramInt2);
    arrayOfInt[paramInt2] = paramInt3;
    System.arraycopy(paramArrayOfInt, paramInt2, arrayOfInt, paramInt2 + 1, paramArrayOfInt.length - paramInt2);
    return arrayOfInt;
  }
  
  public static long[] insert(long[] paramArrayOfLong, int paramInt1, int paramInt2, long paramLong)
  {
    if (!-assertionsDisabled)
    {
      if (paramInt1 <= paramArrayOfLong.length) {}
      for (int i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    if (paramInt1 + 1 <= paramArrayOfLong.length)
    {
      System.arraycopy(paramArrayOfLong, paramInt2, paramArrayOfLong, paramInt2 + 1, paramInt1 - paramInt2);
      paramArrayOfLong[paramInt2] = paramLong;
      return paramArrayOfLong;
    }
    long[] arrayOfLong = new long[growSize(paramInt1)];
    System.arraycopy(paramArrayOfLong, 0, arrayOfLong, 0, paramInt2);
    arrayOfLong[paramInt2] = paramLong;
    System.arraycopy(paramArrayOfLong, paramInt2, arrayOfLong, paramInt2 + 1, paramArrayOfLong.length - paramInt2);
    return arrayOfLong;
  }
  
  public static <T> T[] insert(T[] paramArrayOfT, int paramInt1, int paramInt2, T paramT)
  {
    if (!-assertionsDisabled)
    {
      if (paramInt1 <= paramArrayOfT.length) {}
      for (int i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    if (paramInt1 + 1 <= paramArrayOfT.length)
    {
      System.arraycopy(paramArrayOfT, paramInt2, paramArrayOfT, paramInt2 + 1, paramInt1 - paramInt2);
      paramArrayOfT[paramInt2] = paramT;
      return paramArrayOfT;
    }
    Object[] arrayOfObject = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), growSize(paramInt1));
    System.arraycopy(paramArrayOfT, 0, arrayOfObject, 0, paramInt2);
    arrayOfObject[paramInt2] = paramT;
    System.arraycopy(paramArrayOfT, paramInt2, arrayOfObject, paramInt2 + 1, paramArrayOfT.length - paramInt2);
    return arrayOfObject;
  }
  
  public static boolean[] insert(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (!-assertionsDisabled)
    {
      if (paramInt1 <= paramArrayOfBoolean.length) {}
      for (int i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    if (paramInt1 + 1 <= paramArrayOfBoolean.length)
    {
      System.arraycopy(paramArrayOfBoolean, paramInt2, paramArrayOfBoolean, paramInt2 + 1, paramInt1 - paramInt2);
      paramArrayOfBoolean[paramInt2] = paramBoolean;
      return paramArrayOfBoolean;
    }
    boolean[] arrayOfBoolean = new boolean[growSize(paramInt1)];
    System.arraycopy(paramArrayOfBoolean, 0, arrayOfBoolean, 0, paramInt2);
    arrayOfBoolean[paramInt2] = paramBoolean;
    System.arraycopy(paramArrayOfBoolean, paramInt2, arrayOfBoolean, paramInt2 + 1, paramArrayOfBoolean.length - paramInt2);
    return arrayOfBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\content\res\GrowingArrayUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */