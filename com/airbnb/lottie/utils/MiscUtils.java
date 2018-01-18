package com.airbnb.lottie.utils;

import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.FloatRange;
import com.airbnb.lottie.model.CubicCurveData;
import com.airbnb.lottie.model.content.ShapeData;
import java.util.List;

public class MiscUtils
{
  public static PointF addPoints(PointF paramPointF1, PointF paramPointF2)
  {
    return new PointF(paramPointF1.x + paramPointF2.x, paramPointF1.y + paramPointF2.y);
  }
  
  public static float clamp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return Math.max(paramFloat2, Math.min(paramFloat3, paramFloat1));
  }
  
  private static int floorDiv(int paramInt1, int paramInt2)
  {
    int i = paramInt1 / paramInt2;
    if ((paramInt1 ^ paramInt2) >= 0) {}
    while (i * paramInt2 == paramInt1) {
      return i;
    }
    return i - 1;
  }
  
  public static int floorMod(float paramFloat1, float paramFloat2)
  {
    return floorMod((int)paramFloat1, (int)paramFloat2);
  }
  
  public static int floorMod(int paramInt1, int paramInt2)
  {
    return paramInt1 - floorDiv(paramInt1, paramInt2) * paramInt2;
  }
  
  public static void getPathFromData(ShapeData paramShapeData, Path paramPath)
  {
    paramPath.reset();
    PointF localPointF1 = paramShapeData.getInitialPoint();
    paramPath.moveTo(localPointF1.x, localPointF1.y);
    localPointF1 = new PointF(localPointF1.x, localPointF1.y);
    int i = 0;
    if (i >= paramShapeData.getCurves().size())
    {
      if (paramShapeData.isClosed()) {}
    }
    else
    {
      Object localObject = (CubicCurveData)paramShapeData.getCurves().get(i);
      PointF localPointF2 = ((CubicCurveData)localObject).getControlPoint1();
      PointF localPointF3 = ((CubicCurveData)localObject).getControlPoint2();
      localObject = ((CubicCurveData)localObject).getVertex();
      if (!localPointF2.equals(localPointF1)) {
        label105:
        paramPath.cubicTo(localPointF2.x, localPointF2.y, localPointF3.x, localPointF3.y, ((PointF)localObject).x, ((PointF)localObject).y);
      }
      for (;;)
      {
        localPointF1.set(((PointF)localObject).x, ((PointF)localObject).y);
        i += 1;
        break;
        if (!localPointF3.equals(localObject)) {
          break label105;
        }
        paramPath.lineTo(((PointF)localObject).x, ((PointF)localObject).y);
      }
    }
    paramPath.close();
  }
  
  public static double lerp(double paramDouble1, double paramDouble2, @FloatRange(from=0.0D, to=1.0D) double paramDouble3)
  {
    return (paramDouble2 - paramDouble1) * paramDouble3 + paramDouble1;
  }
  
  public static float lerp(float paramFloat1, float paramFloat2, @FloatRange(from=0.0D, to=1.0D) float paramFloat3)
  {
    return (paramFloat2 - paramFloat1) * paramFloat3 + paramFloat1;
  }
  
  public static int lerp(int paramInt1, int paramInt2, @FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    return (int)(paramInt1 + (paramInt2 - paramInt1) * paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\utils\MiscUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */