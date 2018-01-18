package com.airbnb.lottie.utils;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.animation.content.TrimPathContent;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import java.io.Closeable;

public final class Utils
{
  private static final float SQRT_2 = (float)Math.sqrt(2.0D);
  private static DisplayMetrics displayMetrics;
  private static final PathMeasure pathMeasure = new PathMeasure();
  private static final float[] points;
  private static final Path tempPath = new Path();
  private static final Path tempPath2 = new Path();
  
  static
  {
    points = new float[4];
  }
  
  public static void applyTrimPathIfNeeded(Path paramPath, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    int i = 0;
    L.beginSection("applyTrimPathIfNeeded");
    pathMeasure.setPath(paramPath, false);
    float f2 = pathMeasure.getLength();
    if ((paramFloat1 == 1.0F) && (paramFloat2 == 0.0F))
    {
      L.endSection("applyTrimPathIfNeeded");
      return;
    }
    if (f2 < 1.0F) {
      i = 1;
    }
    if ((i != 0) || (Math.abs(paramFloat2 - paramFloat1 - 1.0F) < 0.01D))
    {
      L.endSection("applyTrimPathIfNeeded");
      return;
    }
    float f1 = f2 * paramFloat1;
    paramFloat2 = f2 * paramFloat2;
    paramFloat1 = Math.min(f1, paramFloat2);
    f1 = Math.max(f1, paramFloat2);
    paramFloat3 *= f2;
    paramFloat2 = paramFloat1 + paramFloat3;
    f1 += paramFloat3;
    paramFloat3 = paramFloat2;
    paramFloat1 = f1;
    if (paramFloat2 >= f2)
    {
      paramFloat3 = paramFloat2;
      paramFloat1 = f1;
      if (f1 >= f2)
      {
        paramFloat3 = MiscUtils.floorMod(paramFloat2, f2);
        paramFloat1 = MiscUtils.floorMod(f1, f2);
      }
    }
    paramFloat2 = paramFloat3;
    if (paramFloat3 < 0.0F) {
      paramFloat2 = MiscUtils.floorMod(paramFloat3, f2);
    }
    paramFloat3 = paramFloat1;
    if (paramFloat1 < 0.0F) {
      paramFloat3 = MiscUtils.floorMod(paramFloat1, f2);
    }
    if (paramFloat2 == paramFloat3)
    {
      paramPath.reset();
      L.endSection("applyTrimPathIfNeeded");
      return;
    }
    paramFloat1 = paramFloat2;
    if (paramFloat2 >= paramFloat3) {
      paramFloat1 = paramFloat2 - f2;
    }
    tempPath.reset();
    pathMeasure.getSegment(paramFloat1, paramFloat3, tempPath, true);
    if (paramFloat3 > f2)
    {
      tempPath2.reset();
      pathMeasure.getSegment(0.0F, paramFloat3 % f2, tempPath2, true);
      tempPath.addPath(tempPath2);
    }
    for (;;)
    {
      paramPath.set(tempPath);
      L.endSection("applyTrimPathIfNeeded");
      return;
      if (paramFloat1 < 0.0F)
      {
        tempPath2.reset();
        pathMeasure.getSegment(paramFloat1 + f2, f2, tempPath2, true);
        tempPath.addPath(tempPath2);
      }
    }
  }
  
  public static void applyTrimPathIfNeeded(Path paramPath, @Nullable TrimPathContent paramTrimPathContent)
  {
    if (paramTrimPathContent != null)
    {
      applyTrimPathIfNeeded(paramPath, ((Float)paramTrimPathContent.getStart().getValue()).floatValue() / 100.0F, ((Float)paramTrimPathContent.getEnd().getValue()).floatValue() / 100.0F, ((Float)paramTrimPathContent.getOffset().getValue()).floatValue() / 360.0F);
      return;
    }
  }
  
  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable == null) {
      return;
    }
    try
    {
      paramCloseable.close();
      return;
    }
    catch (RuntimeException paramCloseable)
    {
      throw paramCloseable;
    }
    catch (Exception paramCloseable) {}
  }
  
  public static Path createPath(PointF paramPointF1, PointF paramPointF2, PointF paramPointF3, PointF paramPointF4)
  {
    Path localPath = new Path();
    localPath.moveTo(paramPointF1.x, paramPointF1.y);
    if (paramPointF3 == null) {}
    while ((paramPointF4 == null) || ((paramPointF3.length() == 0.0F) && (paramPointF4.length() == 0.0F)))
    {
      localPath.lineTo(paramPointF2.x, paramPointF2.y);
      return localPath;
    }
    localPath.cubicTo(paramPointF1.x + paramPointF3.x, paramPointF1.y + paramPointF3.y, paramPointF2.x + paramPointF4.x, paramPointF2.y + paramPointF4.y, paramPointF2.x, paramPointF2.y);
    return localPath;
  }
  
  public static float getAnimationScale(Context paramContext)
  {
    if (Build.VERSION.SDK_INT < 17) {
      return Settings.System.getFloat(paramContext.getContentResolver(), "animator_duration_scale", 1.0F);
    }
    return Settings.Global.getFloat(paramContext.getContentResolver(), "animator_duration_scale", 1.0F);
  }
  
  public static float getScale(Matrix paramMatrix)
  {
    points[0] = 0.0F;
    points[1] = 0.0F;
    points[2] = SQRT_2;
    points[3] = SQRT_2;
    paramMatrix.mapPoints(points);
    float f1 = points[2];
    float f2 = points[0];
    float f3 = points[3];
    float f4 = points[1];
    return (float)Math.hypot(f1 - f2, f3 - f4) / 2.0F;
  }
  
  public static int getScreenHeight(Context paramContext)
  {
    if (displayMetrics != null) {}
    for (;;)
    {
      ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
      return displayMetrics.heightPixels;
      displayMetrics = new DisplayMetrics();
    }
  }
  
  public static int getScreenWidth(Context paramContext)
  {
    if (displayMetrics != null) {}
    for (;;)
    {
      ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
      return displayMetrics.widthPixels;
      displayMetrics = new DisplayMetrics();
    }
  }
  
  public static int hashFor(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    int j = 17;
    if (paramFloat1 != 0.0F) {
      j = (int)('ȏ' * paramFloat1);
    }
    int i = j;
    if (paramFloat2 != 0.0F) {
      i = (int)(j * 31 * paramFloat2);
    }
    j = i;
    if (paramFloat3 != 0.0F) {
      j = (int)(i * 31 * paramFloat3);
    }
    i = j;
    if (paramFloat4 != 0.0F) {
      i = (int)(j * 31 * paramFloat4);
    }
    return i;
  }
  
  public static boolean isAtLeastVersion(LottieComposition paramLottieComposition, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramLottieComposition.getMajorVersion() >= paramInt1)
    {
      if (paramLottieComposition.getMajorVersion() <= paramInt1)
      {
        if (paramLottieComposition.getMinorVersion() < paramInt2) {
          break label46;
        }
        if (paramLottieComposition.getMinorVersion() > paramInt2) {
          break label48;
        }
        if (paramLottieComposition.getPatchVersion() >= paramInt3) {
          break label50;
        }
        return false;
      }
    }
    else {
      return false;
    }
    return true;
    label46:
    return false;
    label48:
    return true;
    label50:
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\utils\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */