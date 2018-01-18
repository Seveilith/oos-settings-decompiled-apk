package com.android.setupwizardlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import java.lang.ref.SoftReference;

public class GlifPatternDrawable
  extends Drawable
{
  @SuppressLint({"InlinedApi"})
  private static final int[] ATTRS_PRIMARY_COLOR = { 16843827 };
  private static final float COLOR_ALPHA = 0.8F;
  private static final int COLOR_ALPHA_INT = 204;
  private static final float MAX_CACHED_BITMAP_SCALE = 1.5F;
  private static final int NUM_PATHS = 7;
  private static final float SCALE_FOCUS_X = 0.146F;
  private static final float SCALE_FOCUS_Y = 0.228F;
  private static final float VIEWBOX_HEIGHT = 768.0F;
  private static final float VIEWBOX_WIDTH = 1366.0F;
  private static SoftReference<Bitmap> sBitmapCache;
  private static int[] sPatternLightness;
  private static Path[] sPatternPaths;
  private int mColor;
  private ColorFilter mColorFilter;
  private Paint mTempPaint = new Paint(1);
  
  public GlifPatternDrawable(int paramInt)
  {
    setColor(paramInt);
  }
  
  public static GlifPatternDrawable getDefault(Context paramContext)
  {
    int i = 0;
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramContext = paramContext.obtainStyledAttributes(ATTRS_PRIMARY_COLOR);
      i = paramContext.getColor(0, -16777216);
      paramContext.recycle();
    }
    return new GlifPatternDrawable(i);
  }
  
  public static void invalidatePattern()
  {
    sBitmapCache = null;
  }
  
  private void renderOnCanvas(Canvas paramCanvas, float paramFloat)
  {
    paramCanvas.save();
    paramCanvas.scale(paramFloat, paramFloat);
    this.mTempPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    if (sPatternPaths == null)
    {
      sPatternPaths = new Path[7];
      sPatternLightness = new int[] { 10, 40, 51, 66, 91, 112, 130 };
      Path localPath = new Path();
      sPatternPaths[0] = localPath;
      localPath.moveTo(1029.4F, 357.5F);
      localPath.lineTo(1366.0F, 759.1F);
      localPath.lineTo(1366.0F, 0.0F);
      localPath.lineTo(1137.7F, 0.0F);
      localPath.close();
      localPath = new Path();
      sPatternPaths[1] = localPath;
      localPath.moveTo(1138.1F, 0.0F);
      localPath.rLineTo(-144.8F, 768.0F);
      localPath.rLineTo(372.7F, 0.0F);
      localPath.rLineTo(0.0F, -524.0F);
      localPath.cubicTo(1290.7F, 121.6F, 1219.2F, 41.1F, 1178.7F, 0.0F);
      localPath.close();
      localPath = new Path();
      sPatternPaths[2] = localPath;
      localPath.moveTo(949.8F, 768.0F);
      localPath.rCubicTo(92.6F, -170.6F, 213.0F, -440.3F, 269.4F, -768.0F);
      localPath.lineTo(585.0F, 0.0F);
      localPath.rLineTo(2.1F, 766.0F);
      localPath.close();
      localPath = new Path();
      sPatternPaths[3] = localPath;
      localPath.moveTo(471.1F, 768.0F);
      localPath.rMoveTo(704.5F, 0.0F);
      localPath.cubicTo(1123.6F, 563.3F, 1027.4F, 275.2F, 856.2F, 0.0F);
      localPath.lineTo(476.4F, 0.0F);
      localPath.rLineTo(-5.3F, 768.0F);
      localPath.close();
      localPath = new Path();
      sPatternPaths[4] = localPath;
      localPath.moveTo(323.1F, 768.0F);
      localPath.moveTo(777.5F, 768.0F);
      localPath.cubicTo(661.9F, 348.8F, 427.2F, 21.4F, 401.2F, 25.4F);
      localPath.lineTo(323.1F, 768.0F);
      localPath.close();
      localPath = new Path();
      sPatternPaths[5] = localPath;
      localPath.moveTo(178.44286F, 766.8571F);
      localPath.lineTo(308.7F, 768.0F);
      localPath.cubicTo(381.7F, 604.6F, 481.6F, 344.3F, 562.2F, 0.0F);
      localPath.lineTo(0.0F, 0.0F);
      localPath.close();
      localPath = new Path();
      sPatternPaths[6] = localPath;
      localPath.moveTo(146.0F, 0.0F);
      localPath.lineTo(0.0F, 0.0F);
      localPath.lineTo(0.0F, 768.0F);
      localPath.lineTo(394.2F, 768.0F);
      localPath.cubicTo(327.7F, 475.3F, 228.5F, 201.0F, 146.0F, 0.0F);
      localPath.close();
    }
    int i = 0;
    while (i < 7)
    {
      this.mTempPaint.setColor(sPatternLightness[i] << 24);
      paramCanvas.drawPath(sPatternPaths[i], this.mTempPaint);
      i += 1;
    }
    paramCanvas.restore();
    this.mTempPaint.reset();
  }
  
  public Bitmap createBitmapCache(int paramInt1, int paramInt2)
  {
    float f = Math.min(1.5F, Math.max(paramInt1 / 1366.0F, paramInt2 / 768.0F));
    Bitmap localBitmap = Bitmap.createBitmap((int)(1366.0F * f), (int)(768.0F * f), Bitmap.Config.ALPHA_8);
    renderOnCanvas(new Canvas(localBitmap), f);
    return localBitmap;
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    int i = localRect.width();
    int j = localRect.height();
    Object localObject2 = null;
    if (sBitmapCache != null) {
      localObject2 = (Bitmap)sBitmapCache.get();
    }
    Object localObject1 = localObject2;
    int m;
    if (localObject2 != null)
    {
      int k = ((Bitmap)localObject2).getWidth();
      m = ((Bitmap)localObject2).getHeight();
      if ((i > k) && (k < 2049.0F)) {
        localObject1 = null;
      }
    }
    else
    {
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        this.mTempPaint.reset();
        localObject2 = createBitmapCache(i, j);
        sBitmapCache = new SoftReference(localObject2);
        this.mTempPaint.reset();
      }
      paramCanvas.save();
      paramCanvas.clipRect(localRect);
      scaleCanvasToBounds(paramCanvas, (Bitmap)localObject2, localRect);
      if ((Build.VERSION.SDK_INT < 11) || (!paramCanvas.isHardwareAccelerated())) {
        break label218;
      }
      this.mTempPaint.setColorFilter(this.mColorFilter);
      paramCanvas.drawBitmap((Bitmap)localObject2, 0.0F, 0.0F, this.mTempPaint);
    }
    for (;;)
    {
      paramCanvas.restore();
      return;
      localObject1 = localObject2;
      if (j <= m) {
        break;
      }
      localObject1 = localObject2;
      if (m >= 1152.0F) {
        break;
      }
      localObject1 = null;
      break;
      label218:
      paramCanvas.drawColor(-16777216);
      this.mTempPaint.setColor(-1);
      paramCanvas.drawBitmap((Bitmap)localObject2, 0.0F, 0.0F, this.mTempPaint);
      paramCanvas.drawColor(this.mColor);
    }
  }
  
  public int getColor()
  {
    return Color.argb(255, Color.red(this.mColor), Color.green(this.mColor), Color.blue(this.mColor));
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void scaleCanvasToBounds(Canvas paramCanvas, Bitmap paramBitmap, Rect paramRect)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    float f1 = paramRect.width() / i;
    float f2 = paramRect.height() / j;
    paramCanvas.scale(f1, f2);
    if (f2 > f1) {
      paramCanvas.scale(f2 / f1, 1.0F, i * 0.146F, 0.0F);
    }
    while (f1 <= f2) {
      return;
    }
    paramCanvas.scale(1.0F, f1 / f2, 0.0F, j * 0.228F);
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColor(int paramInt)
  {
    int i = Color.red(paramInt);
    int j = Color.green(paramInt);
    paramInt = Color.blue(paramInt);
    this.mColor = Color.argb(204, i, j, paramInt);
    this.mColorFilter = new ColorMatrixColorFilter(new float[] { 0.0F, 0.0F, 0.0F, 0.19999999F, i * 0.8F, 0.0F, 0.0F, 0.0F, 0.19999999F, j * 0.8F, 0.0F, 0.0F, 0.0F, 0.19999999F, paramInt * 0.8F, 0.0F, 0.0F, 0.0F, 0.0F, 255.0F });
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\GlifPatternDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */