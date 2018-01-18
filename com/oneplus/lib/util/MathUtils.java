package com.oneplus.lib.util;

import java.util.Random;

public final class MathUtils
{
  private static final float DEG_TO_RAD = 0.017453292F;
  private static final float RAD_TO_DEG = 57.295784F;
  private static final Random sRandom = new Random();
  
  public static float abs(float paramFloat)
  {
    if (paramFloat > 0.0F) {
      return paramFloat;
    }
    return -paramFloat;
  }
  
  public static float acos(float paramFloat)
  {
    return (float)Math.acos(paramFloat);
  }
  
  public static float asin(float paramFloat)
  {
    return (float)Math.asin(paramFloat);
  }
  
  public static float atan(float paramFloat)
  {
    return (float)Math.atan(paramFloat);
  }
  
  public static float atan2(float paramFloat1, float paramFloat2)
  {
    return (float)Math.atan2(paramFloat1, paramFloat2);
  }
  
  public static float constrain(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 < paramFloat2) {
      return paramFloat2;
    }
    if (paramFloat1 > paramFloat3) {
      return paramFloat3;
    }
    return paramFloat1;
  }
  
  public static int constrain(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 < paramInt2) {
      return paramInt2;
    }
    if (paramInt1 > paramInt3) {
      return paramInt3;
    }
    return paramInt1;
  }
  
  public static long constrain(long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramLong1 < paramLong2) {
      return paramLong2;
    }
    if (paramLong1 > paramLong3) {
      return paramLong3;
    }
    return paramLong1;
  }
  
  public static float cross(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    return paramFloat1 * paramFloat4 - paramFloat2 * paramFloat3;
  }
  
  public static float degrees(float paramFloat)
  {
    return 57.295784F * paramFloat;
  }
  
  public static float dist(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    return (float)Math.hypot(paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
  }
  
  public static float dist(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    paramFloat1 = paramFloat4 - paramFloat1;
    paramFloat2 = paramFloat5 - paramFloat2;
    paramFloat3 = paramFloat6 - paramFloat3;
    return (float)Math.sqrt(paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2 + paramFloat3 * paramFloat3);
  }
  
  public static float dot(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    return paramFloat1 * paramFloat3 + paramFloat2 * paramFloat4;
  }
  
  public static float exp(float paramFloat)
  {
    return (float)Math.exp(paramFloat);
  }
  
  public static float lerp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (paramFloat2 - paramFloat1) * paramFloat3 + paramFloat1;
  }
  
  public static float log(float paramFloat)
  {
    return (float)Math.log(paramFloat);
  }
  
  public static float mag(float paramFloat1, float paramFloat2)
  {
    return (float)Math.hypot(paramFloat1, paramFloat2);
  }
  
  public static float mag(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (float)Math.sqrt(paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2 + paramFloat3 * paramFloat3);
  }
  
  public static float map(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    return (paramFloat3 - paramFloat4) * ((paramFloat5 - paramFloat1) / (paramFloat2 - paramFloat1)) + paramFloat3;
  }
  
  public static float max(float paramFloat1, float paramFloat2)
  {
    if (paramFloat1 > paramFloat2) {
      return paramFloat1;
    }
    return paramFloat2;
  }
  
  public static float max(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f;
    if (paramFloat1 > paramFloat2)
    {
      f = paramFloat3;
      if (paramFloat1 > paramFloat3) {
        f = paramFloat1;
      }
    }
    do
    {
      return f;
      f = paramFloat3;
    } while (paramFloat2 <= paramFloat3);
    return paramFloat2;
  }
  
  public static float max(int paramInt1, int paramInt2)
  {
    if (paramInt1 > paramInt2) {}
    for (;;)
    {
      return paramInt1;
      paramInt1 = paramInt2;
    }
  }
  
  public static float max(int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    if (paramInt1 > paramInt2)
    {
      i = paramInt3;
      if (paramInt1 > paramInt3) {
        i = paramInt1;
      }
    }
    for (;;)
    {
      return i;
      i = paramInt3;
      if (paramInt2 > paramInt3) {
        i = paramInt2;
      }
    }
  }
  
  public static float min(float paramFloat1, float paramFloat2)
  {
    if (paramFloat1 < paramFloat2) {
      return paramFloat1;
    }
    return paramFloat2;
  }
  
  public static float min(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f;
    if (paramFloat1 < paramFloat2)
    {
      f = paramFloat3;
      if (paramFloat1 < paramFloat3) {
        f = paramFloat1;
      }
    }
    do
    {
      return f;
      f = paramFloat3;
    } while (paramFloat2 >= paramFloat3);
    return paramFloat2;
  }
  
  public static float min(int paramInt1, int paramInt2)
  {
    if (paramInt1 < paramInt2) {}
    for (;;)
    {
      return paramInt1;
      paramInt1 = paramInt2;
    }
  }
  
  public static float min(int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    if (paramInt1 < paramInt2)
    {
      i = paramInt3;
      if (paramInt1 < paramInt3) {
        i = paramInt1;
      }
    }
    for (;;)
    {
      return i;
      i = paramInt3;
      if (paramInt2 < paramInt3) {
        i = paramInt2;
      }
    }
  }
  
  public static float norm(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (paramFloat3 - paramFloat1) / (paramFloat2 - paramFloat1);
  }
  
  public static float pow(float paramFloat1, float paramFloat2)
  {
    return (float)Math.pow(paramFloat1, paramFloat2);
  }
  
  public static float radians(float paramFloat)
  {
    return 0.017453292F * paramFloat;
  }
  
  public static float random(float paramFloat)
  {
    return sRandom.nextFloat() * paramFloat;
  }
  
  public static float random(float paramFloat1, float paramFloat2)
  {
    if (paramFloat1 >= paramFloat2) {
      return paramFloat1;
    }
    return sRandom.nextFloat() * (paramFloat2 - paramFloat1) + paramFloat1;
  }
  
  public static int random(int paramInt)
  {
    return (int)(sRandom.nextFloat() * paramInt);
  }
  
  public static int random(int paramInt1, int paramInt2)
  {
    if (paramInt1 >= paramInt2) {
      return paramInt1;
    }
    return (int)(sRandom.nextFloat() * (paramInt2 - paramInt1) + paramInt1);
  }
  
  public static void randomSeed(long paramLong)
  {
    sRandom.setSeed(paramLong);
  }
  
  public static float sq(float paramFloat)
  {
    return paramFloat * paramFloat;
  }
  
  public static float tan(float paramFloat)
  {
    return (float)Math.tan(paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\util\MathUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */